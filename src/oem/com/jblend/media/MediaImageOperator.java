package com.jblend.media;

public interface MediaImageOperator {
	public int getX();

	public int getY();

	public int getWidth();

	public int getHeight();

	public void setBounds(int x, int y, int width, int height);

	public int getOriginX();

	public int getOriginY();

	public void setOrigin(int offset_x, int offset_y);

	public int getMediaWidth();

	public int getMediaHeight();
}