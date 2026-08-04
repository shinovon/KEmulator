package emulator.graphics2D;

public interface IFont {
	int charWidth(final char p0);

	int stringWidth(final String p0);

	int getHeight();

	int getAscent();

	int getDescent();

	int getMaxAscent();

	int getMaxDescent();

	int getLeading();

	int getPixelSize();

	String getFamily();

	String getName();

	String getFontName();
}
