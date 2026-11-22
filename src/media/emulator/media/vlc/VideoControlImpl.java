/*
Copyright (c) 2024 Arman Jussupgaliyev
*/
package emulator.media.vlc;

import emulator.graphics2D.awt.AWTImageUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.microedition.media.MediaException;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

class VideoControlImpl implements VideoControl {

	private final VLCPlayerImpl player;

	VideoControlImpl(VLCPlayerImpl player) {
		this.player = player;
	}

	public int getDisplayWidth() {
		return player.width;
	}

	public int getDisplayHeight() {
		return player.height;
	}

	public int getDisplayX() {
		return player.displayX;
	}

	public int getDisplayY() {
		return player.displayY;
	}

	public void setDisplayFullScreen(boolean b) {
		player.fullscreen = b;
	}

	public void setDisplayLocation(int x, int y) {
		player.displayX = x;
		player.displayY = y;
	}

	public void setDisplaySize(int w, int h) {
		if (player.width == w && player.height == h) return;
		player.width = w;
		player.height = h;
		player.notifyListeners(PlayerListener.SIZE_CHANGED, this);
	}

	public void setVisible(boolean b) {
		player.visible = b;
	}

	public int getSourceHeight() {
		if (player.released || player.mediaPlayer == null) {
			throw new IllegalStateException();
		}
		try {
			if (player.mediaPlayer.video().videoDimension() == null) {
				player.mediaPlayer.media().parsing().parse();
				Thread.sleep(100L);
			}
			player.sourceHeight = player.mediaPlayer.video().videoDimension().height;
		} catch (Exception ignored) {}
		if (player.sourceHeight == 0)
			player.sourceHeight = player.bufferHeight;
		if (player.sourceHeight == 0)
			return 480;
		return player.sourceHeight;
	}

	public int getSourceWidth() {
		if (player.released || player.mediaPlayer == null) {
			throw new IllegalStateException();
		}
		try {
			if (player.mediaPlayer.video().videoDimension() == null) {
				player.mediaPlayer.media().parsing().parse();
				Thread.sleep(100L);
			}
			player.sourceWidth = player.mediaPlayer.video().videoDimension().width;
		} catch (Exception ignored) {}
		if (player.sourceWidth == 0)
			player.sourceWidth = player.bufferWidth;
		if (player.sourceWidth == 0)
			return 640;
		return player.sourceWidth;
	}

	public byte[] getSnapshot(String params) throws MediaException {
		if (player.released || player.mediaPlayer == null)
			throw new IllegalStateException();

		if (params == null) {
			params = "encoding=jpeg";
		}

		String[] paramsArr = params.split("&");

		String format = "JPEG";
		int width = 0, height = 0;

		for (String param : paramsArr) {
			String value = null;
			int i = param.indexOf('=') ;
			if (i != -1) {
				value = param.substring(i + 1);
			}
			if (value == null) continue;
			if (param.startsWith("encoding=")) {
				String f = value.toLowerCase();
				if (f.contains("jpeg")) {
					format = "JPEG";
				} else if (f.contains("png")) {
					format = "PNG";
				} else if (f.contains("gif")) {
					format = "GIF";
				}
				continue;
			}
			if (param.startsWith("width=")) {
				try {
					width = Integer.parseInt(value);
				} catch (NumberFormatException ignored) {}
				continue;
			}
			if (param.startsWith("height=")) {
				try {
					height = Integer.parseInt(value);
				} catch (NumberFormatException ignored) {}
			}
		}

		BufferedImage image = player.img;
		if (image == null) {
			image = player.mediaPlayer.snapshots().get();
		}

		if (width != 0 || height != 0) {
			if (width == 0) {
				width = (int) (((float) image.getWidth() * height) / image.getHeight());
			} else if (height == 0) {
				height = (int) (((float) image.getHeight() * width) / image.getWidth());
			}
			image = AWTImageUtils.resizeProportional(image, width, height);
		}

		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, format, os);
			byte[] bytes = os.toByteArray();
			os.close();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
			throw new MediaException(e);
		}
	}

	public Object initDisplayMode(int p0, Object p1) {
		player.isItem = p0 != USE_DIRECT_VIDEO;
		if (p0 == 0) {
			return player.getItem();
		}
		player.canvas = p1;
		return null;
	}
}