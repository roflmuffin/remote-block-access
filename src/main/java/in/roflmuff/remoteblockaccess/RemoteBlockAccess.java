package in.roflmuff.remoteblockaccess;

import in.roflmuff.remoteblockaccess.config.Configuration;
import in.roflmuff.remoteblockaccess.config.RemoteBlockAccessConfig;
import net.fabricmc.api.ModInitializer;

public class RemoteBlockAccess implements ModInitializer {
    @Override
    public void onInitialize() {
        new Configuration(RemoteBlockAccessConfig.class, "remote-block-access");
    }
}