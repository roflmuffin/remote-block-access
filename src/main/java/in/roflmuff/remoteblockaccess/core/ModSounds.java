package in.roflmuff.remoteblockaccess.core;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModSounds {
    public static SoundEvent SUCCESS;
    public static SoundEvent ERROR;

    private static SoundEvent register(String name) {
        Identifier identifier = new Identifier(RemoteBlockAccess.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, identifier, new SoundEvent(identifier));
    }

    public static void register() {
        SUCCESS = register("success");
        ERROR = register("error");
    }
}
