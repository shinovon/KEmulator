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
import java.awt.Rectangle;

public class Sprite extends GraphicObject {
	private Image sprite;
	private int frameHeight;
	private Rectangle dstBounds;
	private Rectangle frameBounds;
	private Rectangle collisionBounds;
	private int frame;
	private int posX;
	private int posY;

	public Sprite(byte[] pixels, int pixel_offset, int width, int height,
				  byte[] mask, int mask_offset, int numFrames) {
		if (pixels == null
				|| pixel_offset != 0
				|| mask_offset != 0
				|| width * height * numFrames / 8 != pixels.length
				|| (mask != null && pixels.length != mask.length)
				|| width % 8 != 0)
			throw new IllegalArgumentException();
		Image image = com.siemens.mp.ui.Image.createImageFromBitmap(pixels, mask, width, height * numFrames);
		init(image, null, numFrames);
	}

	public Sprite(ExtendedImage pixels, ExtendedImage mask, int numFrames) {
		if (pixels == null) throw new NullPointerException();
		init(pixels.getImage(), mask == null ? null : mask.getImage(), numFrames);
	}

	public Sprite(Image pixels, Image mask, int numFrames) {
		init(pixels, mask, numFrames);
	}

	private void init(Image pixels, Image mask, int numFrames) {
		if (pixels == null) {
			throw new NullPointerException();
		}
		int width = pixels.getWidth();
		int height = pixels.getHeight();
		// TODO mask
//		if (mask != null) {
//			sprite = Image.createImage(width, height);
//			Graphics g = sprite.getGraphics();
//			g.drawImage(pixels, 0, 0, 0);
//			g.setColor(0);
//		} else {
			sprite = pixels;
//		}
		frameHeight = height / numFrames;
		frameBounds = new Rectangle(0, 0, width, frameHeight);
		dstBounds = new Rectangle(frameBounds);
		collisionBounds = new Rectangle(dstBounds);
	}

	public int getFrame() {
		return frame;
	}

	public int getXPosition() {
		return posX;
	}

	public int getYPosition() {
		return posY;
	}

	public boolean isCollidingWith(Sprite other) {
		return collisionBounds.intersects(other.collisionBounds);
	}

	public boolean isCollidingWithPos(int xpos, int ypos) {
		return collisionBounds.contains(xpos, ypos);
	}

	public void setCollisionRectangle(int x, int y, int width, int height) {
		int dx = posX;
		int dy = posY;
		collisionBounds.setBounds(dx + x, dy + y, width, height);
	}

	public void setFrame(int frameNumber) {
		frame = frameNumber;
		frameBounds.x = 0;
		frameBounds.y = frameHeight * frameNumber;
	}

	public void setPosition(int x, int y) {
		collisionBounds.x += x - posX;
		collisionBounds.y += y - posY;
		posX = x;
		posY = y;
	}

	protected void paint(Graphics g, int x, int y) {
		dstBounds.x = x + posX;
		dstBounds.y = y + posY;
		g.drawRegion(sprite, frameBounds.x, frameBounds.y, frameBounds.width, frameBounds.height, 0, dstBounds.x, dstBounds.y, 0);
	}
}