package in.roflmuff.remoteblockaccess.core;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.screen.RemoteAccessItemScreen;
import in.roflmuff.remoteblockaccess.screen.RemoteAccessItemScreenHandler;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ModScreens {

    public static ScreenHandlerType<? extends ScreenHandler> REMOTE_ACCESS_ITEM_SCREEN;

    private static ScreenHandlerType<ScreenHandler> register(String name, ScreenHandlerRegistry.ExtendedClientHandlerFactory<ScreenHandler> factory, ScreenRegistry.Factory<ScreenHandler, HandledScreen<ScreenHandler>> factory2) {
        Identifier identifier = new Identifier(RemoteBlockAccess.MOD_ID, name);
        ScreenHandlerType<ScreenHandler> screenHandlerType = ScreenHandlerRegistry.registerExtended(identifier, factory);
        RemoteBlockAccess.clientOnly(() -> () -> ScreenRegistry.register(screenHandlerType, factory2));
        return screenHandlerType;
    }

    public static void register() {
        REMOTE_ACCESS_ITEM_SCREEN = register("remote_access_item_screen", RemoteAccessItemScreenHandler::new, RemoteAccessItemScreen::new);
    }
}
