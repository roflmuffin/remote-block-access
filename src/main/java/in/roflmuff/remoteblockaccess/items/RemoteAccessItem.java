package in.roflmuff.remoteblockaccess.items;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.config.RemoteBlockAccessConfig;
import in.roflmuff.remoteblockaccess.core.*;
import in.roflmuff.remoteblockaccess.network.messages.OpenGuiMessage;
import in.roflmuff.remoteblockaccess.screen.RemoteAccessItemScreenHandler;
import in.roflmuff.remoteblockaccess.util.MiscUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RemoteAccessItem extends BaseItem {

    private static final String NBT_TARGET = "Target";
    private static final String NBT_MULTI_TARGET = "MultiTarget";
    private final RemoteAccessTier accessTier;

    public RemoteAccessItem(RemoteAccessTier accessTier) {
        super(new Item.Settings().group(ItemGroup.MISC).maxDamage(accessTier.maxCharge).rarity(Rarity.RARE));
        this.accessTier = accessTier;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        if (getTarget(stack) != null) {
            return true;
        }

        return super.hasGlint(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        if (!(ctx.getPlayer().getMainHandStack().getItem() instanceof RemoteAccessItem)) return super.useOnBlock(ctx);

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
        // Ignore using the item if its in our off-hand.
        if (hand == Hand.OFF_HAND) return super.use(world, user, hand);

        ItemStack stack = user.getMainHandStack();

        if (user.isSneaking()) {
            clearTargets(stack, user);
            return super.use(world, user, hand);
        }

        List<RemoteBlockConfiguration> targets = getTargets(stack, false);

        // Don't proceed if our item does not have enough charge.
        if (!world.isClient && targets.size() > 0 && !hasChargeAvailable(stack)) {
            user.sendMessage(new TranslatableText("chatText.misc.notCharged").formatted(Formatting.RED), true);
            world.playSound(null, user.getBlockPos(), ModSounds.ERROR, SoundCategory.BLOCKS, 1.0f, 0.5f);
            return super.use(world, user, hand);
        }

        if (targets.size() > 1 && !world.isClient) {
            //RemoteAccessItemScreenHandler remoteAccessItemScreenHandler = new RemoteAccessItemScreenHandler(0, targets);
            user.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {

                }

                @Override
                public Text getDisplayName() {
                    return new TranslatableText("item.remoteblockaccess.remote_block_accessor");
                }

                @Override
                public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
                    return new RemoteAccessItemScreenHandler(syncId, inv, byteBuf);
                }
            });
            return super.use(world, user, hand);
        }

        for (RemoteBlockConfiguration target : targets) {
            if (target != null) {
                useForTarget(target, world, user);
                stack.setDamage(stack.getDamage() + this.getCostToUse());
/*                BlockPos savedPos = target.globalPos.getPos();
                Identifier targetDimension = target.globalPos.getDimension().getValue();

                // Handle other vs. same dimensional usage.
                if (targetDimension != world.getRegistryKey().getValue()) {
                    useOtherDimension(user, target);
                } else {
                    if (isWithinRange(user, target)) {
                        useSameDimension(world, user, target);
                    } else if (!world.isClient) {
                        user.sendMessage(new TranslatableText("chatText.misc.tooFarAway", getMaxRange()).formatted(Formatting.RED), true);
                        world.playSound(null, user.getBlockPos(), ModSounds.ERROR, SoundCategory.BLOCKS, 1.0f, 0.7f);
                    }
                }*/
            }

        }

        return super.use(world, user, hand);
    }

    public void useForTarget(RemoteBlockConfiguration target, World world, PlayerEntity user) {
        if (target != null) {
            BlockPos savedPos = target.globalPos.getPos();
            Identifier targetDimension = target.globalPos.getDimension().getValue();

            // Handle other vs. same dimensional usage.
            if (targetDimension != world.getRegistryKey().getValue()) {
                user.sendMessage(new TranslatableText("chatText.misc.wrongDimension").formatted(Formatting.RED), true);
            } else {
                if (isWithinRange(user, target)) {
                    useSameDimension(world, user, target);
                } else if (!world.isClient) {
                    user.sendMessage(new TranslatableText("chatText.misc.tooFarAway", getMaxRange()).formatted(Formatting.RED), true);
                    world.playSound(null, user.getBlockPos(), ModSounds.ERROR, SoundCategory.BLOCKS, 1.0f, 0.7f);
                }
            }
        }
    }

    private void useSameDimension(World world, PlayerEntity user, RemoteBlockConfiguration target) {
        BlockPos savedPos = target.globalPos.getPos();

        if (!world.isClient) {
            // If were server, send the chunk data and then use the block server side.
            // Also send packet for the client to use the block too.
            ServerWorld serverWorld = RemoteBlockAccess.getCurrentServer().getWorld(target.globalPos.getDimension());
            WorldChunk chunk = (WorldChunk) serverWorld.getChunk(savedPos);
            ServerPlayerEntity player =  ((ServerPlayerEntity)user);

            ChunkDataS2CPacket packet = new ChunkDataS2CPacket((WorldChunk) chunk, 65535);
            player.networkHandler.sendPacket(packet);

            RemoteAccessItem.mimicUseAction(world, target, player);

            OpenGuiMessage message = new OpenGuiMessage(savedPos, target.hitResult);
            message.sendToClient(player);
        }
    }

    public static void mimicUseAction(World world, RemoteBlockConfiguration target, PlayerEntity user) {
/*        if (world.isClient) {
            world = new ProxyClientWorld((ClientWorld) world);
        }*/

        BlockPos savedPos = target.globalPos.getPos();
        BlockState state = world.getBlockState(savedPos);
        BlockEntity blockEntity = world.getBlockEntity(savedPos);

        ItemStack offHandItem = user.getStackInHand(Hand.OFF_HAND);
        if (offHandItem != ItemStack.EMPTY) {
            ActionResult actionResult = offHandItem.useOnBlock(new ItemUsageContext(user, Hand.OFF_HAND, target.hitResult));
            if (actionResult.isAccepted()) {
                return;
            }
        }

        try {
            if (blockEntity != null) {
                blockEntity.getCachedState().onUse(world, user, Hand.OFF_HAND, target.hitResult);
            } else {
                state.onUse(world, user, Hand.OFF_HAND, target.hitResult);
            }
        } catch (Exception e) {
            System.out.println("An error occurred trying to open block remotely. " + e.getStackTrace());
        }
    }

    protected boolean isValidTarget(ItemUsageContext ctx) {
        return true;
    }

    private void handleMultiTarget(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction face, BlockHitResult blockHitResult) {
        if (!world.isClient) {
            boolean removing = false;
            String invName = world.getBlockState(pos).getBlock().getTranslationKey();
            GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), pos);
            RemoteBlockConfiguration target = new RemoteBlockConfiguration(globalPos, invName, blockHitResult);
            List<RemoteBlockConfiguration> targets = getTargets(itemStack, true);

            if (targets.contains(target)) {
                targets.remove(target);
                removing = true;
                player.sendMessage(new TranslatableText("chatText.misc.targetRemoved", targets.size(), getMaxTargets()).append(
                        target.getTextComponent(false).formatted(Formatting.YELLOW)), true);
            } else if (targets.size() < getMaxTargets()) {
                targets.add(target);
                player.sendMessage(new TranslatableText("chatText.misc.targetAdded", targets.size(), getMaxTargets()).append(
                        target.getTextComponent(false).formatted(Formatting.YELLOW)), true);
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
        List<RemoteBlockConfiguration> targets = getTargets(stack, false);
        if (targets.size() > 0) return targets.get(0);

        return null;
    }

    public static List<RemoteBlockConfiguration> getTargets(ItemStack stack, boolean checkBlockName) {
        List<RemoteBlockConfiguration> result = new ArrayList<RemoteBlockConfiguration>();

        NbtCompound compound = stack.getSubTag(RemoteBlockAccess.MOD_ID);
        if (compound != null && compound.getType(NBT_MULTI_TARGET) == 9 /* List Tag */) {
            NbtList list = compound.getList(NBT_MULTI_TARGET, 10 /* Compound Tag */);
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
        NbtCompound compound = stack.getOrCreateSubTag(RemoteBlockAccess.MOD_ID);

        NbtList list = new NbtList();
        for (RemoteBlockConfiguration target : targets) {
            list.add(target.toNBT());
        }
        compound.put(NBT_MULTI_TARGET, list);
        stack.getTag().put(RemoteBlockAccess.MOD_ID, compound);
    }

    private static void clearTargets(ItemStack stack, PlayerEntity player) {
        setTargets(stack, new ArrayList<>());

        if (!player.world.isClient) {
            player.sendMessage(new TranslatableText("chatText.misc.targetCleared").formatted(Formatting.YELLOW), true);
            player.world.playSound(null, player.getBlockPos(), ModSounds.SUCCESS, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    private static RemoteBlockConfiguration updateTargetBlockName(ItemStack stack, RemoteBlockConfiguration target) {
        ServerWorld w = MiscUtil.getWorldForGlobalPos(target.globalPos);
        BlockPos pos = target.globalPos.getPos();
        if (w != null && w.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            String invName = w.getBlockState(pos).getBlock().getTranslationKey();
            if (!target.translationKey.equals(invName)) {
                setTarget(stack, w, target.hitResult);
                return new RemoteBlockConfiguration(target.globalPos, invName, target.hitResult);
            }
        }
        return null;
    }

    private static void setTarget(ItemStack stack, World world, BlockHitResult hitResult) {
        if (world.isClient) {
            return;
        }
        NbtCompound compound = stack.getOrCreateSubTag(RemoteBlockAccess.MOD_ID);
        if (hitResult == null || hitResult.getBlockPos() == null) {
            compound.remove(NBT_TARGET);
        } else {
            GlobalPos gPos = GlobalPos.create(world.getRegistryKey(), hitResult.getBlockPos());
            RemoteBlockConfiguration mt = new RemoteBlockConfiguration(gPos, world.getBlockState(hitResult.getBlockPos()).getBlock().getTranslationKey(), hitResult);
            compound.put(NBT_TARGET, mt.toNBT());
        }
        stack.getTag().put(RemoteBlockAccess.MOD_ID, compound);
    }
    
    private int getMaxTargets() {
        return RemoteBlockAccessConfig.multiSelectMaximum;
    }

    private double getMaxRange() { return accessTier.maxRange; }

    private int getCostToUse() { return accessTier.costToUse; }

    private int getMaxCharge() { return accessTier.maxCharge; }

    private int getCharge(ItemStack stack) { return stack.getMaxDamage() - stack.getDamage();}

    private boolean hasChargeAvailable(ItemStack stack) {
        return getCharge(stack) >= this.getCostToUse();
    }

    private boolean isWithinRange(PlayerEntity user, RemoteBlockConfiguration target) {
        return user.getBlockPos().getManhattanDistance(target.globalPos.getPos()) < getMaxRange();
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        List<RemoteBlockConfiguration> targets = getTargets(stack, false);

        tooltip.add(new TranslatableText("guiText.tooltip.blockRange", getMaxRange()));
        tooltip.add(new TranslatableText("guiText.tooltip.storesXCharge", this.getMaxCharge()));
        tooltip.add(new TranslatableText("guiText.tooltip.usesXCharge", this.getCostToUse()));

        for (RemoteBlockConfiguration target : targets) {
            if (target != null) {
                boolean sameDimension = world.getRegistryKey().getValue() == target.globalPos.getDimension().getValue();
                MutableText msg = new TranslatableText("chatText.misc.target").append(target.getTextComponent(!sameDimension)).formatted(Formatting.YELLOW);
                tooltip.add(msg);
            }
        }

        tooltip.add(new TranslatableText("guiText.tooltip.currentCharge", stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()));
        tooltip.add(new TranslatableText("guiText.tooltip.usesLeft", getUsesLeft(stack)));
    }

    private int getUsesLeft(ItemStack stack) {
        return (stack.getMaxDamage() - stack.getDamage()) / getCostToUse();
    }

    @Override
    public Text getName(ItemStack stack) {
        RemoteBlockConfiguration target = getTarget(stack);
        if (target != null) {
             boolean sameDimension = MinecraftClient.getInstance().world.getRegistryKey().getValue() == target.globalPos.getDimension().getValue();
             return super.getName(stack).copy().append(target.getTextComponent(!sameDimension));
        }

        return super.getName(stack);
    }


}
