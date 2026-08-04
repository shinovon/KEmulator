/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/
package com.siemens.mp.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class TiledBackground extends GraphicObject {

	private Image[] pixels;
	private byte[] map;
	private int widthInTiles;
	private int heightInTiles;
	private int posx;
	private int posy;

	public TiledBackground(byte[] tilePixels, byte[] tileMask, byte[] map, int widthInTiles, int heightInTiles) {
		this(com.siemens.mp.ui.Image.createImageFromBitmap(tilePixels, 8, tilePixels.length),
				tileMask == null ? null : com.siemens.mp.ui.Image.createImageFromBitmap(tileMask, 8, tilePixels.length),
				map,
				widthInTiles,
				heightInTiles
		);
	}

	public TiledBackground(ExtendedImage tilePixels, ExtendedImage tileMask, byte[] map, int widthInTiles, int heightInTiles) {
		this(tilePixels.getImage(), tileMask == null ? null : tileMask.getImage(), map, widthInTiles, heightInTiles);
	}

	public TiledBackground(Image tilePixels, Image tileMask, byte[] map, int widthInTiles, int heightInTiles) {
		if (tilePixels.getWidth() != 8 || (tileMask != null && tileMask.getWidth() != 8)) {
			throw new IllegalArgumentException("Tile image width must be 8.");
		}
		if (containsTransparentColor(tilePixels) || (tileMask != null && containsTransparentColor(tileMask))) {
			throw new IllegalArgumentException("Images must not contain transparent colors.");
		}
		for (byte tile : map) {
			if ((tile & 0xFF) >= tilePixels.getHeight() / 8) {
				throw new IllegalArgumentException("Invalid tile number in the map.");
			}
		}

		this.map = map;
		this.heightInTiles = heightInTiles;
		this.widthInTiles = widthInTiles;

		pixels = new Image[tilePixels.getHeight() / 8 + 3];
		pixels[0] = Image.createImage(8, 8, 0);
		pixels[1] = Image.createImage(8, 8);
		pixels[2] = Image.createImage(8, 8);
		pixels[2].getGraphics().fillRect(0, 0, 8, 8);

		if (tileMask != null) {
			tilePixels = com.siemens.mp.lcdui.Image.createTransparentImageFromMask(tilePixels, tileMask);
		}

		for (int i = 0; i < this.pixels.length - 3; i++) {
			Image img = Image.createImage(8, 8, 0);

			img.getGraphics().drawImage(tilePixels, 0, -i * 8, 0);
			pixels[i + 3] = img;
		}
	}

	public void setPositionInMap(int x, int y) {
		posx = x;
		posy = y;
	}

	protected void paint(Graphics g, int tx, int ty) {
		g.translate(tx, ty);

		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipW = g.getClipWidth();
		int clipH = g.getClipHeight();

		int startX = (clipX + posx) / 8;
		int startY = (clipY + posy) / 8;
		int endX = (clipX + clipW + posx) / 8;
		int endY = (clipY + clipH + posy) / 8;

		if (clipX + posx < 0) startX--;
		if (clipY + posy < 0) startY--;

		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				int mapX = wrap(x, widthInTiles);
				int mapY = wrap(y, heightInTiles);

				int tile = map[mapY * widthInTiles + mapX] & 0xFF;

				if (tile != 0) {
					g.drawImage(pixels[tile], (x * 8) - posx, (y * 8) - posy, 0);
				}
			}
		}
		g.translate(-tx, -ty);
	}

	private static int wrap(int value, int max) {
		int m = value % max;
		return m < 0 ? m + max : m;
	}
}