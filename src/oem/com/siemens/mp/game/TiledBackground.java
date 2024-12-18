/*
 *  Copyright 2020 Yury Kharchenko
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.siemens.mp.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.awt.*;

public class TiledBackground extends GraphicObject {
	private static final String TAG = TiledBackground.class.getName();
	private Image pixels;
	private byte[][] map;
	private int widthInTiles;
	private int heightInTiles;
	private int posX;
	private int posY;
	private Rectangle frame = new Rectangle(0, 0, 8, 8);
	private Rectangle dst = new Rectangle(0, 0, 8, 8);

	public TiledBackground(byte[] tilePixels, byte[] tileMask, byte[] map,
						   int widthInTiles, int heightInTiles) {
		this(
				com.siemens.mp.ui.Image.createImageFromBitmap(tilePixels, tileMask, 8, tilePixels.length),
				null,
				map,
				widthInTiles,
				heightInTiles
		);
	}

	public TiledBackground(ExtendedImage tilePixels, ExtendedImage tileMask, byte[] map,
						   int widthInTiles, int heightInTiles) {
		this(tilePixels.getImage(), tileMask == null ? null : tileMask.getImage(), map, widthInTiles, heightInTiles);
	}

	public TiledBackground(Image tilePixels, Image tileMask, byte[] map,
						   int widthInTiles, int heightInTiles) {
		this.map = new byte[heightInTiles][widthInTiles];
		this.heightInTiles = heightInTiles;
		this.widthInTiles = widthInTiles;

		int idx = 0;
		for (int i = 0; i < heightInTiles; i++) {
			byte[] row = this.map[i];
			for (int j = 0; j < widthInTiles; j++) {
				row[j] = map[idx++];
			}
		}
		// TODO mask
		pixels = tilePixels;
	}

	public void setPositionInMap(int x, int y) {
		posX = x;
		posY = y;
	}

	protected void paint(Graphics g, int x, int y) {
		int ox, oy, ow, oh;
		final Rectangle clip = new Rectangle(
				ox = g.getClipX(),
				oy = g.getClipY(),
				ow = g.getClipWidth(),
				oh = g.getClipHeight());
		clip.x = Math.max(clip.x, x);
		clip.y = Math.max(clip.y, y);
		g.clipRect(clip.x, clip.y, clip.width, clip.height);
		final int left = x - (posX % 8);
		final int top = y - (posY % 8);
		dst.x = left;
		dst.y = top;
		try {
			for (int ty = posY / 8, tyLen = clip.height / 8 + 1 + ty; ty < tyLen; ty++) {
				byte[] row = this.map[ty % heightInTiles];
				for (int tx = posX / 8, txLen = clip.width / 8 + 1 + tx; tx < txLen; tx++) {
					final int tile = row[tx % widthInTiles] & 0xff;
					switch (tile) {
						case 0:
							// transparent tile
							break;
						case 1:
							g.setColor(-1);
							g.fillRect(dst.x, dst.y, dst.width, dst.height);
							break;
						case 2:
							g.setColor(0);
							g.fillRect(dst.x, dst.y, dst.width, dst.height);
							break;
						default:
							frame.x = 0;
							frame.y = (tile - 3) * 8;
							g.drawRegion(pixels, frame.x, frame.y, dst.width, dst.height, 0, dst.x, dst.y, 0);
					}
					dst.x += 8;
				}
				dst.x = left;
				dst.y += 8;
				dst.width = 8;
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			g.setClip(ox, oy, ow, oh);
		}
	}
}