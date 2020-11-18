package in.roflmuff.remoteblockaccess.items;

import com.google.common.collect.Sets;
import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.config.RemoteBlockAccessConfig;
import in.roflmuff.remoteblockaccess.core.ModNetwork;
import in.roflmuff.remoteblockaccess.core.ModSounds;
import in.roflmuff.remoteblockaccess.util.MiscUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;

import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RemoteAccessItem extends BaseItem {

    private static final String NBT_TARGET = "Target";
    private static final String NBT_MULTI_TARGET = "MultiTarget";

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        if (ctx.getPlayer() != null && ctx.getPlayer().isSneaking()) {
            if (isValidTarget(ctx)) {
                BlockHitResult blockHitResult = new BlockHitResult(ctx.getHitPos(), ctx.getSide(), ctx.getBlockPos(), false);
                handleMultiTarget(ctx.getStack(), ctx.getPlayer(), ctx.getWorld(), ctx.getBlockPos(), ctx.getPlayerFacing(), blockHitResult);
                return ActionResult.SUCCESS;
            } else {
                return super.useOnBlock(ctx);
            }
        }

        return super.useOnBlock(ctx);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        List<RemoteBlockConfiguration> targets = getTargets(user.getStackInHand(hand), false);

        for (RemoteBlockConfiguration target : targets) {
            if (target != null) {
                BlockPos savedPos = target.globalPos.getPos();


                BlockState state = world.getBlockState(savedPos);
                BlockEntity blockEntity = world.getBlockEntity(savedPos);

                if (world.isClient) {
                //if (blockEntity == null && world.isClient) {
                    // The client does not know about this chunk, issue a request to receive it.
                    PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
                    byteBuf.writeBlockPos(savedPos);
                    byteBuf.writeBlockHitResult(target.hitResult);
                    ClientSidePacketRegistry.INSTANCE.sendToServer(ModNetwork.LOAD_CHUNK_REQUEST, byteBuf);
                }
            }
        }

        return super.use(world, user, hand);
    }

    public static void mimicUseAction(RemoteBlockConfiguration target, PlayerEntity user) {
        System.out.println("Attempting to use block");
        World world = user.world;
        BlockPos savedPos = target.globalPos.getPos();
        BlockState state = world.getBlockState(savedPos);
        BlockEntity blockEntity = world.getBlockEntity(savedPos);
        state.onUse(world, user, Hand.MAIN_HAND, target.hitResult);
    }

    protected boolean isValidTarget(ItemUsageContext ctx) {
        // TODO: Ensure block selected has a gui...
        // Find some code that checks block entity below.
        // return ctx.getWorld().getBlockEntity(ctx.getBlockPos()) instanceof Inventory;
        return true;
    }

    private void handleMultiTarget(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction face, BlockHitResult blockHitResult) {
        if (!world.isClient) {
            boolean removing = false;
            String invName = world.getBlockState(pos).getBlock().getTranslationKey();
            GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), pos);
            RemoteBlockConfiguration target = new RemoteBlockConfiguration(globalPos, face, invName, blockHitResult);
            List<RemoteBlockConfiguration> targets = getTargets(itemStack, true);

            if (targets.contains(target)) {
                targets.remove(target);
                removing = true;
                player.sendMessage(new TranslatableText("chatText.misc.targetRemoved", targets.size(), getMaxTargets()).append(
                        target.getTextComponent().formatted(Formatting.YELLOW)), true);
            } else if (targets.size() < getMaxTargets()) {
                targets.add(target);
                player.sendMessage(new TranslatableText("chatText.misc.targetAdded", targets.size(), getMaxTargets()).append(
                        target.getTextComponent().formatted(Formatting.YELLOW)), true);
            } else {
                player.sendMessage(new TranslatableText("chatText.misc.tooManyTargets", getMaxTargets()).formatted(Formatting.RED), true);
                world.playSound(null, pos, ModSounds.ERROR, SoundCategory.BLOCKS, 1.0f, 1.3f);
                return;
            }

            world.playSound(null, pos, ModSounds.SUCCESS, SoundCategory.BLOCKS, 1.0f, removing ? 1.1f : 1.3f);
            setTargets(itemStack, targets);
        }
    }

    public static RemoteBlockConfiguration getTarget(ItemStack stack) {
        return getTargets(stack, false).get(0);
    }

    public static List<RemoteBlockConfiguration> getTargets(ItemStack stack, boolean checkBlockName) {
        List<RemoteBlockConfiguration> result = new ArrayList<RemoteBlockConfiguration>();

        CompoundTag compound = stack.getSubTag(RemoteBlockAccess.MOD_ID);
        if (compound != null && compound.getType(NBT_MULTI_TARGET) == 9 /* List Tag */) {
            ListTag list = compound.getList(NBT_MULTI_TARGET, 10 /* Compound Tag */);
            for (int i = 0; i < list.size(); i++) {
                RemoteBlockConfiguration target = RemoteBlockConfiguration.fromNBT(list.getCompound(i));
                if (checkBlockName) {
                    RemoteBlockConfiguration newTarget = updateTargetBlockName(stack, target);
                    result.add(newTarget != null ? newTarget : target);
                } else {
                    result.add(target);
                }
            }
        }
        return result;
    }

    private static void setTargets(ItemStack stack, List<RemoteBlockConfiguration> targets) {
        CompoundTag compound = stack.getOrCreateSubTag(RemoteBlockAccess.MOD_ID);

        ListTag list = new ListTag();
        for (RemoteBlockConfiguration target : targets) {
            list.add(target.toNBT());
        }
        compound.put(NBT_MULTI_TARGET, list);
        stack.getTag().put(RemoteBlockAccess.MOD_ID, compound);
    }

    private static RemoteBlockConfiguration updateTargetBlockName(ItemStack stack, RemoteBlockConfiguration target) {
        ServerWorld w = MiscUtil.getWorldForGlobalPos(target.globalPos);
        BlockPos pos = target.globalPos.getPos();
        if (w != null && w.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            String invName = w.getBlockState(pos).getBlock().getTranslationKey();
            if (!target.translationKey.equals(invName)) {
                setTarget(stack, w, target.hitResult);
                return new RemoteBlockConfiguration(target.globalPos, target.face, invName, target.hitResult);
            }
        }
        return null;
    }

    private static void setTarget(ItemStack stack, World world, BlockHitResult hitResult) {
        if (world.isClient) {
            return;
        }
        CompoundTag compound = stack.getOrCreateSubTag(RemoteBlockAccess.MOD_ID);
        if (hitResult == null || hitResult.getBlockPos() == null) {
            compound.remove(NBT_TARGET);
        } else {
            GlobalPos gPos = GlobalPos.create(world.getRegistryKey(), hitResult.getBlockPos());
            RemoteBlockConfiguration mt = new RemoteBlockConfiguration(gPos, hitResult.getSide(), world.getBlockState(hitResult.getBlockPos()).getBlock().getTranslationKey(), hitResult);
            compound.put(NBT_TARGET, mt.toNBT());
        }
        stack.getTag().put(RemoteBlockAccess.MOD_ID, compound);
    }

    
    private int getMaxTargets() {
        return RemoteBlockAccessConfig.multiSelectMaximum;
    }

    private double getMaxRange() { return 256; }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        List<RemoteBlockConfiguration> targets = getTargets(stack, false);

        for (RemoteBlockConfiguration target : targets) {
            if (target != null) {
                MutableText msg = MiscUtil.xlate("chatText.misc.target").append(target.getTextComponent()).formatted(Formatting.YELLOW);
                tooltip.add(msg);
            }
        }
    }
}
