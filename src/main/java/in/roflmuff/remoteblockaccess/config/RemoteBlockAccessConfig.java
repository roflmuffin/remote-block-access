package in.roflmuff.remoteblockaccess.config;

public class RemoteBlockAccessConfig {
    @Config(config="settings", category="multiSelection", key = "multiSelectMaximum", comment = "The maximum amount of blocks to be selected on multi select.")
    public static int multiSelectMaximum = 5;
}
