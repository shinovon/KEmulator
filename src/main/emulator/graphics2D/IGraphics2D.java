package emulator.graphics2D;

public interface IGraphics2D {
	void setAlpha(final int p0);

	void clipRect(final int p0, final int p1, final int p2, final int p3);

	void drawArc(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);

	void drawLine(final int p0, final int p1, final int p2, final int p3);

	void drawRect(final int p0, final int p1, final int p2, final int p3);

	void drawRoundRect(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);

	void fillArc(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);

	void fillRect(final int p0, final int p1, final int p2, final int p3);

	void fillRoundRect(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);

	void fillPolygon(final int[] p0);

	void drawPolygon(final int[] p0);

	void drawString(final String p0, final int p1, final int p2);

	void drawImage(final IImage p0, final int p1, final int p2);

	void drawImage(final IImage p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8);

	void setClip(final int p0, final int p1, final int p2, final int p3);

	void setColor(final int p0, final boolean p1);

	void setColor(final int p0, final int p1, final int p2);

	void setFont(final IFont p0);

	void setStrokeStyle(final int p0);

	int getStrokeStyle();

	int getClipHeight();

	int getClipWidth();

	int getClipX();

	int getClipY();

	int getColor();

	int getColorRed();

	int getColorGreen();

	int getColorBlue();

	float[] RGBtoHSB(final int p0, final int p1, final int p2);

	ITransform getTransform();

	void setTransform(final ITransform p0);

	void transform(final ITransform p0);

	void translate(final int p0, final int p1);

	/**
	 * @return
	 * @deprecated
	 */
	IFont getFont();

	void reset();
}
