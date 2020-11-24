package in.roflmuff.remoteblockaccess.screen;

public interface IScreenInfoProvider extends IScreenPlayerInventoryProvider {
    int getVisibleRows();

    int getRows();

    int getCurrentOffset();

    int getTopHeight();

    int getBottomHeight();
}
