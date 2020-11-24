package in.roflmuff.remoteblockaccess.util;

public class RenderUtils {
    public static boolean inBounds(int x, int y, int w, int h, double ox, double oy) {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
    }
}
