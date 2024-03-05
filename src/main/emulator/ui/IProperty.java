package emulator.ui;

public interface IProperty {
    String getRmsFolderPath();

    String getDefaultFontName();

    int getFontSmallSize();

    int getFontMediumSize();

    int getFontLargeSize();

    void setDefaultFontName(final String p0);

    void setFontSmallSize(final int p0);

    void getFontMediumSize(final int p0);

    void getFontLargeSize(final int p0);

    void resetDeviceName();

    void setCustomProperties();

    void updateCustomProperties();

    void saveProperties();

    void loadProperties();

    boolean updateController();

    String getOldRmsPath();
}
