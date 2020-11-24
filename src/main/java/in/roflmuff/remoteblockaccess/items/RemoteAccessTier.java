package in.roflmuff.remoteblockaccess.items;

import net.minecraft.util.Formatting;

public enum RemoteAccessTier {
    LOW(512, 16, 4),
    MEDIUM(1024, 32, 8),
    HIGH(2048, 64, 16),
    EXTREME(4096, 128, 32);

    public final int maxRange;
    public final int costToUse;
    public final int maxCharge;

    RemoteAccessTier(int maxCharge, int maxRange, int costToUse) {
        this.maxRange = maxRange;
        this.costToUse = costToUse;
        this.maxCharge = maxCharge;
    }
}