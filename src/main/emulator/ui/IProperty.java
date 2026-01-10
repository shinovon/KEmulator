package emulator.ui;

public interface IProperty {
	String getRmsFolderPath();

	String getDefaultFontName();

	void setDefaultFontName(final String p0);

	void saveProperties();

	void loadProperties();

	boolean updateController();

	String getOldRmsPath();
}
