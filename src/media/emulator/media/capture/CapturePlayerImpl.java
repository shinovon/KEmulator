package emulator.media.capture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.microedition.amms.control.camera.CameraControl;
import javax.microedition.amms.control.camera.FlashControl;
import javax.microedition.amms.control.camera.FocusControl;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.media.*;
import javax.microedition.media.control.VideoControl;

import emulator.Emulator;
import emulator.media.vlc.VLCPlayerImpl;

import com.github.sarxos.webcam.Webcam;

import emulator.Permission;
import emulator.graphics2D.awt.ImageAWT;

public class CapturePlayerImpl implements Player {

	public static CapturePlayerImpl inst;

	private VideoControl videoControl;
	private Webcam webcam;
	public boolean visible;
	public Object canvas;

	public boolean started;

	private int locy;

	private int locx;

	private int scalew;

	private int scaleh;

	private boolean isItem;

	private CaptureItem item;

	private FocusControl focusControl;

	private FlashControl flashControl;

	private CameraControl cameraControl;

	private int state = UNREALIZED;

	public CapturePlayerImpl() throws MediaException {
		webcam = Webcam.getDefault();
		if (webcam == null) throw new MediaException("No capture devices found!");
		videoControl = new CameraVideoControlImpl(this);
		focusControl = new FocusControlImpl();
		flashControl = new FlashControlImpl();
		cameraControl = new CameraControlImpl();
		scaleh = Math.max(webcam.getViewSize().height, Emulator.getEmulator().getScreen().getHeight());
	}

	public Control getControl(String s) {
		if (s.contains("VideoControl") || s.contains("GUIControl")) {
			return this.videoControl;
		}
		if (s.contains("FocusControl")) {
			return focusControl;
		}
		if (s.contains("FlashControl")) {
			return flashControl;
		}
		if (s.contains("CameraControl")) {
			return cameraControl;
		}
		return null;
	}

	public Control[] getControls() {
		return new Control[]{videoControl, focusControl, flashControl, cameraControl};
	}

	public void addPlayerListener(PlayerListener p0) throws IllegalStateException {
	}

	public void close() {
		if (state == CLOSED) return;
		state = CLOSED;
		started = false;
		webcam.close();
		inst = null;
	}

	public void deallocate() throws IllegalStateException {
		inst = null;
	}

	public String getContentType() {
		return null;
	}

	public long getDuration() {
		return -1;
	}

	public long getMediaTime() {
		return -1;
	}

	public int getState() {
		return state;
	}

	public void prefetch() throws IllegalStateException, MediaException {
		if (state == CLOSED) throw new IllegalStateException();
		state = PREFETCHED;
	}

	public void realize() throws IllegalStateException, MediaException {
		if (state == CLOSED) throw new IllegalStateException();
		state = REALIZED;
		inst = this;
	}

	public void removePlayerListener(PlayerListener p0) throws IllegalStateException {
	}

	public void setLoopCount(int p0) {
	}

	public long setMediaTime(long p0) {
		return 0;
	}

	public void start() throws IllegalStateException, MediaException {
		if (state == CLOSED) throw new IllegalStateException();
		if (inst != this) {
			realize();
		}
		Permission.checkPermission("media.camera");
		webcam.open();
		started = true;
		state = STARTED;
	}

	public void stop() throws IllegalStateException, MediaException {
		if (state == CLOSED) throw new IllegalStateException();
		started = false;
		state = PREFETCHED;
	}

	public void setTimeBase(TimeBase p0) throws MediaException {
	}

	public TimeBase getTimeBase() {
		return null;
	}

	public void setVisible(boolean p0) {
		visible = p0;
	}

	public void setDisplaySize(int p0, int p1) {
		scalew = p0;
		scaleh = p1;
		if(item != null) {
			item.w = p0;
			item.h = p1;
		}
	}

	public void setDisplayLocation(int p0, int p1) {
		locx = p0;
		locy = p1;
	}

	public Object initDisplayMode(int p0, Object p1) {
		if (state == CLOSED) throw new IllegalStateException();
		if (p0 == 0) {
			isItem = true;
			return getItem();
		}
		canvas = p1;
		return null;
	}

	private Item getItem() {
		if (item != null) return item;
		item = new CaptureItem(this);
		return item;
	}

	public byte[] getSnapshot(String p0) throws MediaException {
		if (!started) throw new MediaException("Not visible");
		int w = 0;
		int h = 0;
		try {
			if (p0 != null) {
				String[] ar = p0.split("&");
				for (String s : ar) {
					if (s.startsWith("width=")) {
						w = Integer.parseInt(s.replace("width=", ""));
						continue;
					}
					if (s.startsWith("height=")) {
						h = Integer.parseInt(s.replace("height=", ""));
						continue;
					}
				}
			}
		} catch (Exception ignored) {}
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			BufferedImage img = webcam.getImage();
			if (w > 0 && h > 0) {
				img = VLCPlayerImpl.resize(img, w, h);
			}
			ImageIO.write(img, "JPEG", os);
			byte[] bytes = os.toByteArray();
			os.close();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
			throw new MediaException(e);
		}
	}

	public void setDisplayFullScreen(boolean p0) {
	}

	public int getSourceHeight() {
		return webcam.getViewSize().height;
	}

	public int getSourceWidth() {
		return webcam.getViewSize().width;
	}

	public void paint(Graphics g) {
		if (visible && started) {
			try {
				g.drawImage(new Image(new ImageAWT(VLCPlayerImpl.resize(webcam.getImage(), -1, scaleh))), locx, locy, 0);
			} catch (Exception ignored) {}
		}
	}

	public void paint(Graphics g, int w, int h) {
		if (started) {
			try {
				g.drawImage(new Image(new ImageAWT(VLCPlayerImpl.resize(webcam.getImage(), -1, h))), 0, 0, 0);
			} catch (Exception ignored) {}
		}
	}

	public static void draw(Graphics g, Object obj) {
		if (inst != null) {
			if ((inst.canvas == null || obj == inst.canvas) && !inst.isItem) {
				inst.paint(g);
			}
		} else {
			VLCPlayerImpl.draw(g, obj);
		}
	}

}
