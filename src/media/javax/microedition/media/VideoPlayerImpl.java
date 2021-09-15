package javax.microedition.media;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.control.FramePositioningControl;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.StringArray;

import emulator.Emulator;
import emulator.i;
import emulator.custom.CustomJarResources;
import emulator.graphics2D.awt.d;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.RuntimeUtil;
import uk.co.caprica.vlcj.binding.internal.libvlc_instance_t;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.media.TrackType;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters;
import uk.co.caprica.vlcj.support.version.LibVlcVersion;

public class VideoPlayerImpl implements Player, MediaPlayerEventListener {

	private Control[] controls;
	private String contentType;
	private int state;
	private FramePositioningControl frameControl;
	private int dataLen;
	private int loopCount;
	private Vector listeners;
	private TimeBase timeBase;
	private Thread playerThread;
	private VideoControl videoControl;
	private VolumeControl volumeControl;
	private String url;
	private String mediaUrl;
	private InputStream inputStream;
	private boolean playing;
	private Object canvas;
	public boolean isItem;
	public int locx;
	public int locy;
	public int w;
	public int h;
	public boolean visible = true;
	public int srcw;
	public int srch;
	public boolean fullscreen;
	private boolean prepared;
	public int bw;
	public int bh;
	private MediaPlayerFactory factory;
	private EmbeddedMediaPlayer mediaPlayer;
	public BufferedImage img;
	public ByteBuffer bb;
	private boolean released;
	private static VideoPlayerImpl inst;

	static {
	}

	public static void draw(Graphics g, Object obj) {
		if (inst != null) {
			if (inst.canvas == null || obj == inst.canvas) {
				inst.paint(g);
			}
		}
	}

	private void paint(Graphics g) {
		if (visible && img != null) {
			if (w == 0 || h == 0) {
				w = Emulator.getEmulator().getScreen().getWidth();
				h = Emulator.getEmulator().getScreen().getHeight();
				fullscreen = true;
			}
			try {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(img, "JPEG", os);
				byte[] bytes = os.toByteArray();
				os.close();
				if (fullscreen) {
					if (w != Emulator.getEmulator().getScreen().getWidth()
							|| h != Emulator.getEmulator().getScreen().getHeight()) {
						w = Emulator.getEmulator().getScreen().getWidth();
						h = Emulator.getEmulator().getScreen().getHeight();
					}
					Image i = resizeProportional(new Image(new d(bytes)), w, h);
					int x = 0;
					int y = 0;
					if (i.getWidth() < w)
						x = (w - i.getWidth()) / 2;
					if (i.getHeight() < h)
						y = (h - i.getHeight()) / 2;
					g.drawImage(i, x, y, 0);
				} else {
					g.drawImage(resize(new Image(new d(bytes)), w, h), locx, locy, 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public VideoPlayerImpl(String url) throws IOException {
		this();
		this.url = url;
		this.mediaUrl = url;
		this.state = 100;
	}

	public VideoPlayerImpl(InputStream inputStream, String type) throws IOException {
		this();
		this.contentType = type;
		this.inputStream = inputStream;
		this.state = 100;
	}

	private VideoPlayerImpl() throws IOException {
		this.loopCount = 1;
		this.listeners = new Vector();
		frameControl = new FramePositioningControlI();
		videoControl = new VideoControlI();
		volumeControl = new VolumeControlI();
		controls = new Control[] { frameControl, videoControl, volumeControl };
		this.timeBase = Manager.getSystemTimeBase();
	}

	public Control getControl(String s) {
		if (s.contains("FramePositioningControl")) {
			return frameControl;
		}
		if (s.contains("GUIControl") || s.contains("VideoControl")) {
			return videoControl;
		}
		if (s.contains("VolumeControl")) {
			return volumeControl;
		}
		return null;
	}

	public Control[] getControls() {
		return controls;
	}

	public void addPlayerListener(PlayerListener p0) throws IllegalStateException {
		if (this.state == 0) {
			throw new IllegalStateException();
		}
		if (p0 != null)
			listeners.add(p0);
	}

	private void load() throws MediaException {
		if (mediaUrl == null && inputStream != null) {
			if (inputStream instanceof FileInputStream) {
				Field field;
				try {
					field = inputStream.getClass().getDeclaredField("path");
					field.setAccessible(true);
					String path = (String) field.get(inputStream);
					mediaUrl = path;
					prepared = true;
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				byte[] b = CustomJarResources.getBytes(inputStream);
				File d = new File(System.getProperty("java.io.tmpdir"));
				File f = new File(d.getPath() + File.separator + "kemtempvideo");
				if (f.exists())
					f.delete();
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(b);
				fos.close();
				this.mediaUrl = f.toString();
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
				throw new MediaException("failed to write temp file");
			}
		}
		prepared = true;
	}

	public void prefetch() throws IllegalStateException, MediaException {
		if (this.state == 0) {
			throw new IllegalStateException("closed");
		}
		if (this.state == 100) {
			this.realize();
		} else if (this.state != 200) {
			return;
		}
		if (!prepared) {
			load();
			if (!mediaPlayer.media().prepare(mediaUrl))
				throw new MediaException("failed to prepare");
			prepared = true;
		}
		this.state = 300;
	}

	public void realize() throws IllegalStateException, MediaException {
		if (this.state == 0) {
			throw new IllegalStateException("closed");
		}
		if (this.state == 200) {
			return;
		}
		inst = this;
		try {
			factory = new MediaPlayerFactory();
			mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();

			mediaPlayer.events().addMediaPlayerEventListener(this);

			mediaPlayer.videoSurface().set(new MyVideoSurface());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MediaException(e);
		}
		if (this.state == 100) {
			this.state = 200;
		}
	}

	public void close() {
		if (this.state == 0) {
			return;
		}
		inst = null;
		if (playing) {
			try {
				this.stop();
			} catch (Exception ex) {
			}
		}
		try {
			if (!released)
				mediaPlayer.release();
			released = true;
		} catch (Error e) {

		}
		this.state = 0;
		this.notifyListeners("closed", null);
	}

	public void deallocate() throws IllegalStateException {
		if (this.state == 0) {
			throw new IllegalStateException("closed");
		}
		if (this.state == 400) {
			try {
				this.stop();
				return;
			} catch (MediaException ex) {
				return;
			}
		}
		if (!released)
			mediaPlayer.release();
		released = true;
		if (this.state == 300) {
			state = 200;
		} else {
			if (this.state != 200) {
				return;
			}
			state = 100;
		}
	}

	public void start() throws IllegalStateException, MediaException {
		if (this.state == 0) {
			throw new IllegalStateException("closed");
		}
		if (released)
			throw new IllegalStateException("mediaPlayer released");
		if (this.state != 400) {
			if (!prepared) {
				load();
				if (!mediaPlayer.media().start(mediaUrl))
					throw new MediaException("failed to start");
				mediaPlayer.audio().setVolume(50);
			} else {
				mediaPlayer.controls().play();
			}
			playing = true;
			this.state = 400;
		}
	}

	private void update() {
		Emulator.getEventQueue().queue(1);
	}

	public void stop() throws IllegalStateException, MediaException {
		if (this.state == 0) {
			throw new IllegalStateException("closed");
		}
		if (playing) {
			mediaPlayer.controls().pause();
			playing = false;
		}
	}

	public String getContentType() {
		return contentType;
	}

	public long getDuration() {
		return mediaPlayer.status().length();
	}

	public long getMediaTime() {
		return mediaPlayer.status().time();
	}

	public int getState() {
		return state;
	}

	public void removePlayerListener(PlayerListener p0) throws IllegalStateException {
		if (this.state == 0) {
			throw new IllegalStateException();
		}
		if (p0 != null)
			listeners.remove(p0);
	}

	public long setMediaTime(long p0) throws MediaException {
		mediaPlayer.controls().setTime(p0);
		return mediaPlayer.status().time();
	}

	public Object getItem() {
		return null;
	}

	protected void notifyListeners(final String s, final Object o) {
		//System.out.println("notifyListeners " + s);
		if (listeners == null)
			return;
		final Enumeration<PlayerListener> elements = this.listeners.elements();
		while (elements.hasMoreElements()) {
			elements.nextElement().playerUpdate(this, s.intern(), o);
		}
	}

	public void setLoopCount(int p0) {
	}

	public void setTimeBase(TimeBase tb) throws MediaException {
		if (this.state == 100 || this.state == 400 || this.state == 0) {
			throw new IllegalStateException();
		}
	}

	public TimeBase getTimeBase() {
		if (this.state == 100 || this.state == 0) {
			throw new IllegalStateException();
		}
		return this.timeBase;
	}

	class FramePositioningControlI implements FramePositioningControl {

		@Override
		public long mapFrameToTime(int frameBumber) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int mapTimeToFrame(long mediaTime) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int seek(int frameNumber) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int skip(int framesToSkip) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	class VolumeControlI implements VolumeControl {

		public int getLevel() {
			if (released || mediaPlayer == null)
				throw new IllegalStateException();
			return mediaPlayer.audio().volume();
		}

		public boolean isMuted() {
			if (released || mediaPlayer == null)
				throw new IllegalStateException();
			return mediaPlayer.audio().isMute();
		}

		public int setLevel(int p0) {
			if (released || mediaPlayer == null)
				throw new IllegalStateException();
			mediaPlayer.audio().setVolume(p0);
			return mediaPlayer.audio().volume();
		}

		public void setMute(boolean p0) {
			if (released || mediaPlayer == null)
				throw new IllegalStateException();
			mediaPlayer.audio().setMute(p0);
		}

	}

	class VideoControlI implements VideoControl {

		@Override
		public int getDisplayHeight() {
			return w;
		}

		@Override
		public int getDisplayWidth() {
			return h;
		}

		@Override
		public int getDisplayX() {
			return locx;
		}

		@Override
		public int getDisplayY() {
			return locy;
		}

		@Override
		public void setDisplayFullScreen(boolean p0) {
			fullscreen = p0;
		}

		@Override
		public void setDisplayLocation(int p0, int p1) {
			locx = p0;
			locy = p1;
		}

		@Override
		public void setDisplaySize(int p0, int p1) {
			w = p0;
			h = p1;
		}

		@Override
		public void setVisible(boolean p0) {
			visible = p0;
		}

		@Override
		public int getSourceHeight() {
			if (released || mediaPlayer == null)
				throw new IllegalStateException();
			return srcw = mediaPlayer.video().videoDimension().height;
		}

		@Override
		public int getSourceWidth() {
			if (released || mediaPlayer == null)
				throw new IllegalStateException();
			return srcw = mediaPlayer.video().videoDimension().width;
		}

		@Override
		public byte[] getSnapshot(String p0) throws MediaException {
			if (released || mediaPlayer == null)
				throw new IllegalStateException();
			try {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(mediaPlayer.snapshots().get(), "JPEG", os);
				byte[] bytes = os.toByteArray();
				os.close();
				return bytes;
			} catch (IOException e) {
				e.printStackTrace();
				throw new MediaException(e);
			}
		}

		@Override
		public Object initDisplayMode(int p0, Object p1) {
			if (p0 == 0) {
				isItem = true;
				return getItem();
			}
			canvas = p1;
			return null;
		}

	}

	private class MyVideoSurface extends CallbackVideoSurface {

		MyVideoSurface() {
			super(new MyBufferFormatCallback(), new MyRenderCallback(), true, VideoSurfaceAdapters
					.getVideoSurfaceAdapter());
		}

	}

	private class MyBufferFormatCallback implements BufferFormatCallback {
		@Override
		public BufferFormat getBufferFormat(int w, int h) {
			bw = w;
			bh = h;
			return new RV32BufferFormat(w, h);
		}

		@Override
		public void allocatedBuffers(ByteBuffer[] buffers) {
			bb = buffers[0];
			img = new BufferedImage(bw, bh, BufferedImage.TYPE_3BYTE_BGR);
		}

	}

	private class MyRenderCallback implements RenderCallback {
		@Override
		public void display(MediaPlayer mediaPlayer, ByteBuffer[] buffers, BufferFormat bufferFormat) {
			bb.rewind();
			byte[] raw = new byte[bb.capacity()];
			bb.get(raw);
			DataBuffer buffer = new DataBufferByte(raw, raw.length);
			SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, bw, bh, 4, bw
					* 4, new int[] { 2, 1, 0 });
			Raster r = Raster.createRaster(sampleModel, buffer, null);
			if (img == null || img.getWidth() != bw || img.getHeight() != bh) {
				img = new BufferedImage(bw, bh, BufferedImage.TYPE_3BYTE_BGR);
			}
			img.setData(r);
			update();
		}
	}

	private static Image resize(Image src_i, int tw, int th) {
		// set source size
		int w = src_i.getWidth();
		int h = src_i.getHeight();
		if (tw == -1)
			tw = (w / h) * th;
		// no change??
		if (tw == w && th == h)
			return src_i;

		int[] dst = new int[tw * th];

		resize_rgb_filtered(src_i, dst, w, h, tw, th);

		// not needed anymore
		src_i = null;

		return Image.createRGBImage(dst, tw, th, true);
	}

	private static Image resizeProportional(Image img, int sw, int sh) {
		int iw = img.getWidth();
		int ih = img.getHeight();
		if (sw == iw && sh == ih)
			return img;
		double widthRatio = (double) sw / (double) iw;
		double heightRatio = (double) sh / (double) ih;
		double ratio = Math.min(widthRatio, heightRatio);
		int tw = (int) (iw * ratio);
		int th = (int) (ih * ratio);
		int[] dst = new int[tw * th];

		resize_rgb_filtered(img, dst, iw, ih, tw, th);

		// not needed anymore
		img = null;

		return Image.createRGBImage(dst, sw, sh, true);
	}

	private static final void resize_rgb_filtered(Image src_i, int[] dst, int w0, int h0, int w1, int h1) {
		int[] buffer1 = new int[w0];
		int[] buffer2 = new int[w0];

		// UNOPTIMIZED bilinear filtering:
		//
		// The pixel position is defined by y_a and y_b,
		// which are 24.8 fixed point numbers
		//
		// for bilinear interpolation, we use y_a1 <= y_a <= y_b1
		// and x_a1 <= x_a <= x_b1, with y_d and x_d defining how long
		// from x/y_b1 we are.
		//
		// since we are resizing one line at a time, we will at most
		// need two lines from the source image (y_a1 and y_b1).
		// this will save us some memory but will make the algorithm
		// noticeably slower

		for (int index1 = 0, y = 0; y < h1; y++) {

			final int y_a = ((y * h0) << 8) / h1;
			final int y_a1 = y_a >> 8;
			int y_d = y_a & 0xFF;

			int y_b1 = y_a1 + 1;
			if (y_b1 >= h0) {
				y_b1 = h0 - 1;
				y_d = 0;
			}

			// get the two affected lines:
			src_i.getRGB(buffer1, 0, w0, 0, y_a1, w0, 1);
			if (y_d != 0)
				src_i.getRGB(buffer2, 0, w0, 0, y_b1, w0, 1);

			for (int x = 0; x < w1; x++) {
				// get this and the next point
				int x_a = ((x * w0) << 8) / w1;
				int x_a1 = x_a >> 8;
				int x_d = x_a & 0xFF;

				int x_b1 = x_a1 + 1;
				if (x_b1 >= w0) {
					x_b1 = w0 - 1;
					x_d = 0;
				}

				// interpolate in x
				int c12, c34;
				int c1 = buffer1[x_a1];
				int c3 = buffer1[x_b1];

				// interpolate in y:
				if (y_d == 0) {
					c12 = c1;
					c34 = c3;
				} else {
					int c2 = buffer2[x_a1];
					int c4 = buffer2[x_b1];

					c12 = blend(c2, c1, y_d);
					c34 = blend(c4, c3, y_d);
				}

				// final result
				dst[index1++] = blend(c34, c12, x_d);
			}
		}

	}

	public static final int blend(final int c1, final int c2, final int value256) {

		final int v1 = value256 & 0xFF;
		final int c1_RB = c1 & 0x00FF00FF;
		final int c2_RB = c2 & 0x00FF00FF;

		final int c1_AG = (c1 >>> 8) & 0x00FF00FF;

		final int c2_AG_org = c2 & 0xFF00FF00;
		final int c2_AG = (c2_AG_org) >>> 8;

		// the world-famous tube42 blend with one mult per two components:
		final int rb = (c2_RB + (((c1_RB - c2_RB) * v1) >> 8)) & 0x00FF00FF;
		final int ag = (c2_AG_org + ((c1_AG - c2_AG) * v1)) & 0xFF00FF00;
		return ag | rb;

	}

	@Override
	public void audioDeviceChanged(MediaPlayer arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void backward(MediaPlayer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void buffering(MediaPlayer arg0, float arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void chapterChanged(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void corked(MediaPlayer arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void elementaryStreamAdded(MediaPlayer arg0, TrackType arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void elementaryStreamDeleted(MediaPlayer arg0, TrackType arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void elementaryStreamSelected(MediaPlayer arg0, TrackType arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void forward(MediaPlayer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lengthChanged(MediaPlayer arg0, long arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mediaChanged(MediaPlayer arg0, MediaRef arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mediaPlayerReady(MediaPlayer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void muted(MediaPlayer arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void opening(MediaPlayer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pausableChanged(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void paused(MediaPlayer arg0) {
		this.state = 300;
		notifyListeners("stopped", null);
	}

	@Override
	public void positionChanged(MediaPlayer arg0, float arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scrambledChanged(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void seekableChanged(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void snapshotTaken(MediaPlayer arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopped(MediaPlayer arg0) {
		this.state = 300;
		notifyListeners("stopped", null);
	}

	@Override
	public void timeChanged(MediaPlayer arg0, long arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void titleChanged(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void videoOutput(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void volumeChanged(MediaPlayer arg0, float arg1) {
		// TODO Auto-generated method stub

	}

	public void finished(MediaPlayer mediaPlayer) {
		this.state = 300;
		notifyListeners("endOfMedia", null);
	}

	public void error(MediaPlayer mediaPlayer) {
		notifyListeners("error", null);
	}

	public void playing(MediaPlayer mediaPlayer) {
		notifyListeners("started", new Long(0L));
	}

}
