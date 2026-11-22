/*
Copyright (c) 2021-2026 Arman Jussupgaliyev
*/
package emulator.media.vlc;

import emulator.Emulator;
import emulator.Permission;
import emulator.Settings;
import emulator.Utils;
import emulator.graphics2D.awt.AWTImageUtils;
import emulator.graphics2D.awt.Graphics2DAWT;
import emulator.graphics2D.awt.ImageAWT;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.media.TrackType;
import uk.co.caprica.vlcj.media.callback.CallbackMedia;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.microedition.amms.control.camera.CameraControl;
import javax.microedition.amms.control.camera.FlashControl;
import javax.microedition.amms.control.camera.FocusControl;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.media.protocol.DataSource;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

public class VLCPlayerImpl implements Player, MediaPlayerEventListener {

	private static VLCPlayerImpl inst;

	private Control[] controls;
	private String contentType;
	private int state;
	public int dataLen;
	private Vector listeners;
	private TimeBase timeBase;
	private String url;
	private String mediaUrl;
	private InputStream inputStream;
	private boolean playing;
	Object canvas;
	public boolean isItem;
	public int displayX, displayY;
	public int width, height;
	public boolean visible = true;
	public int sourceWidth, sourceHeight;
	public boolean fullscreen;
	private boolean prepared;
	public int bufferWidth, bufferHeight;
	private static MediaPlayerFactory factory;
	EmbeddedMediaPlayer mediaPlayer;
	public BufferedImage img;
	public ByteBuffer bb;
	boolean released;
	private DataSource dataSource;
	private boolean lengthNotified;
	int volume = -1;
	private CallbackMedia mediaCallback;
	private File tempFile;

	private boolean started;

	private VideoControl videoControl;
	private VolumeControl volumeControl;
	private RateControl rateControl;
	private StopTimeControl stopTimeControl;
	private MetaDataControl metaDataControl;

	private FocusControl focusControl;
	private FlashControl flashControl;
	private CameraControl cameraControl;

	long stopTime = StopTimeControl.RESET;
	private boolean stoppedAtTime;
	private final Object vlcLock = new Object();

	private BufferedImage renderImage;
	private VLCPlayerItem item;
	private boolean capture;

	private VLCPlayerImpl() {
		this.listeners = new Vector();
		videoControl = new VideoControlImpl(this);
		volumeControl = new VolumeControlImpl(this);
		rateControl = new RateControlImpl(this);
		stopTimeControl = new StopTimeControlImpl(this);
		metaDataControl = new MetaDataControlImpl(this);
		controls = new Control[]{videoControl, volumeControl, rateControl, stopTimeControl, metaDataControl};
		this.timeBase = Manager.getSystemTimeBase();
		if (Settings.enableMediaDump)
			PlayerImpl.players.add(this);
	}

	public VLCPlayerImpl(String url) throws IOException {
		this();
		if (url.startsWith("capture://")) {
			capture = true;
			focusControl = new FocusControl() {

				public int setFocus(int paramInt) throws MediaException {
					return 0;
				}

				public int getFocus() {
					return 0;
				}

				public int getMinFocus() {
					return 0;
				}

				public int getFocusSteps() {
					return 0;
				}

				public boolean isManualFocusSupported() {
					return false;
				}

				public boolean isAutoFocusSupported() {
					return false;
				}

				public boolean isMacroSupported() {
					return false;
				}

				public void setMacro(boolean paramBoolean) throws MediaException {

				}

				public boolean getMacro() {
					return false;
				}
			};

			flashControl = new FlashControl() {

				public int[] getSupportedModes() {
					return new int[] { OFF, AUTO };
				}

				public void setMode(int paramInt) {

				}

				public int getMode() {
					return OFF;
				}

				public boolean isFlashReady() {
					return false;
				}
			};

			cameraControl = new CameraControl() {

				public int getCameraRotation() {
					return ROTATE_NONE;
				}

				public void enableShutterFeedback(boolean paramBoolean) throws MediaException {

				}

				public boolean isShutterFeedbackEnabled() {
					return false;
				}

				public String[] getSupportedExposureModes() {
					return new String[0];
				}

				public void setExposureMode(String paramString) {

				}

				public String getExposureMode() {
					return "";
				}

				public int[] getSupportedVideoResolutions() {
					return new int[0];
				}

				public int[] getSupportedStillResolutions() {
					return new int[0];
				}

				public void setVideoResolution(int paramInt) {

				}

				public void setStillResolution(int paramInt) {

				}

				public int getVideoResolution() {
					return 0;
				}

				public int getStillResolution() {
					return 0;
				}
			};

			controls = new Control[] {videoControl, focusControl, cameraControl};

			if (Utils.win) {
				url = "dshow://";
			} else if (Utils.macos) {
				url = "avcapture://";
			} else {
				url = "v4l2:///dev/video0";
			}
		} else if (url.startsWith("file:///root/")) {
			url = "file:///" + (Emulator.getUserPath() + "/file/root/" + url.substring("file:///root/".length())).replace(" ", "%20");
		}
		this.url = url;
		this.mediaUrl = url;
		this.state = UNREALIZED;
	}

	public VLCPlayerImpl(InputStream inputStream, String type) throws IOException {
		this();
		this.contentType = type;
		this.inputStream = inputStream;
		this.state = UNREALIZED;
	}

	public VLCPlayerImpl(String contentType, DataSource src) throws IOException {
		this();
		this.contentType = contentType;
		this.dataSource = src;
		this.state = UNREALIZED;
	}

	public VLCPlayerImpl(String locator, String contentType, DataSource src) throws IOException {
		this();
		if (locator.startsWith("file:///root/")) {
			locator = "file:///" + (Emulator.getUserPath() + "/file/root/" + url.substring("file:///root/".length())).replace(" ", "%20");
		}
		this.url = locator;
		this.mediaUrl = locator;
		this.state = UNREALIZED;
		this.dataSource = src;
	}

	public VLCPlayerImpl(String url, String contentType) throws IOException {
		this();
		if (url.startsWith("file:///root/")) {
			url = "file:///" + (Emulator.getUserPath() + "/file/root/" + url.substring("file:///root/".length())).replace(" ", "%20");
		}
		this.url = url;
		this.mediaUrl = url;
		this.contentType = contentType;
		this.state = UNREALIZED;
	}

	public static void draw(Graphics g, Object obj) {
		if (inst == null || obj != inst.canvas)
			return;

		inst.draw(g);
	}

	private void draw(Graphics g) {
		if (!visible || img == null || !playing) return;

		int x = displayX, y = displayY;

		if (fullscreen || width == 0 || height == 0) {
			width = Emulator.getEmulator().getScreen().getWidth();
			height = Emulator.getEmulator().getScreen().getHeight();
			fullscreen = true;
			x = 0;
			y = 0;
		}

		paint(g, x, y, width, height);
	}

	protected void paint(Graphics g, int x, int y, int w, int h) {
		if (!visible || img == null || !playing) return;
		try {
			g.setColor(0);
			g.fillRect(x, y, width, height);
			int[] size = new int[2];
			renderImage = AWTImageUtils.resizeProportional(renderImage, img, width, height, size);
			if (renderImage == null) return;

			int iw = size[0];
			int ih = size[1];
			if (renderImage.getWidth() < width)
				x = (width - ih) / 2;
			if (renderImage.getHeight() < height)
				y = (height - ih) / 2;
			((Graphics2DAWT) g._getImpl()).g().drawImage(renderImage, x, y, iw, ih, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static byte[] imgToBytes(BufferedImage img) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageOutputStream ios = ImageIO.createImageOutputStream(os);
			Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
			ImageWriter writer = iter.next();
			ImageWriteParam iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(0.5f);
			writer.setOutput(ios);
			writer.write(null, new IIOImage(img, null, null), iwp);
			writer.dispose();
			byte[] bytes = os.toByteArray();
			os.close();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Control getControl(String s) {
		if (s.contains("GUIControl") || s.contains("VideoControl")) {
			return videoControl;
		}
		if (capture) {
			if (s.contains("FocusControl")) {
				return focusControl;
			}
			if (s.contains("FlashControl")) {
				return flashControl;
			}
			if (s.contains("CameraControl")) {
				return cameraControl;
			}
		} else {
			if (s.contains("VolumeControl")) {
				return volumeControl;
			}
			if (s.contains("RateControl")) {
				return rateControl;
			}
			if (s.contains("StopTimeControl")) {
				return stopTimeControl;
			}
			if (s.contains("MetaDataControl")) {
				return metaDataControl;
			}
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
		if (mediaUrl == null) {
			if (this.dataSource != null) {
				try {
					this.dataSource.connect();
				} catch (IOException e) {
					e.printStackTrace();
					throw new MediaException(e);
				}
			}
			if (inputStream != null) {
				if (inputStream instanceof FileInputStream) {
					Field field;
					try {
						field = inputStream.getClass().getDeclaredField("path");
						field.setAccessible(true);
						String path = (String) field.get(inputStream);
						File f = new File(path);
						mediaUrl = path;
						dataLen = (int) f.length();
						prepared = true;
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
//				boolean bufferToFile = inputStream instanceof ByteArrayInputStream && false;
//				if (!bufferToFile) {
				try {
					if (inputStream instanceof ByteArrayInputStream) {
						dataLen = inputStream.available();
					}
				} catch (Exception ignored) {}
				mediaCallback = new VLCCallbackStream(inputStream, dataLen);
//				} else {
//					try {
//						Manager.log("buffering to file");
//						File d = new File(System.getProperty("java.io.tmpdir"));
//						tempFile = new File(d.getPath() + File.separator + "kemtempmedia");
//						tempFile.deleteOnExit();
//						if (tempFile.exists())
//							tempFile.delete();
//						FileOutputStream fos = new FileOutputStream(tempFile);
//						CustomJarResources.write(inputStream, fos);
//						fos.close();
//						dataLen = (int) tempFile.length();
//						this.mediaUrl = tempFile.toString();
//						mediaCallback = new RandomAccessFileMedia(tempFile);
//						Manager.log("buffered " + mediaUrl);
//					} catch (Exception e) {
//						e.printStackTrace();
//						throw new MediaException("failed to write temp file");
//					}
//				}
			} else if (dataSource != null) {
				mediaCallback = new VLCCallbackSourceStream(dataSource);
			}
		}
		prepared = true;
	}

	public void prefetch() throws IllegalStateException, MediaException {
		if (this.state == CLOSED) {
			throw new IllegalStateException("closed");
		}
		if (this.state == UNREALIZED) {
			this.realize();
		} else if (this.state != REALIZED) {
			return;
		}
		if (!prepared) {
			synchronized (vlcLock) {
				load();
				if (capture
						|| mediaUrl.startsWith("v4l2:")
						|| mediaUrl.startsWith("ddraw:")
						|| mediaUrl.startsWith("dshow:")
						|| mediaUrl.startsWith("avcapture:")) {
					Permission.checkPermission("media.camera");
				}
				try {
					if (mediaCallback != null) {
						if (!mediaPlayer.media().prepare(mediaCallback)) {
							Manager.log("Failed to prepare");
							throw new MediaException("failed to prepare");
						}
					} else {
						boolean res;
						if (capture && Utils.win) {
							res = mediaPlayer.media().prepare(mediaUrl,
									":dshow-adev=none",
									":clock-jitter=0",
									":clock-synchro=0",
									":dshow-fps=15");
						} else {
							res = mediaPlayer.media().prepare(mediaUrl);
						}
						if (!res) {
							Manager.log("Failed to prepare");
							throw new MediaException("failed to prepare");
						}
					}
				} catch (MediaException e) {
					throw e;
				} catch (Exception e) {
					throw new MediaException(e);
				}
			}
			prepared = true;
		}
		this.state = PREFETCHED;
	}

	public void realize() throws IllegalStateException, MediaException {
		if (this.state == CLOSED) {
			throw new IllegalStateException("closed");
		}
		if (this.state >= REALIZED) {
			return;
		}
		inst = this;
		try {
			if (factory == null) {
				factory = new MediaPlayerFactory();
			}
			synchronized (vlcLock) {
				mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();

				mediaPlayer.events().addMediaPlayerEventListener(this);

				mediaPlayer.videoSurface().set(new MyVideoSurface());
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new MediaException(e);
		}
		if (this.state == UNREALIZED) {
			this.state = REALIZED;
		}
	}

	public void close() {
		synchronized (vlcLock) {
			if (this.state == CLOSED) {
				return;
			}
			PlayerImpl.players.remove(this);
			state = CLOSED;
			if (inst == this) inst = null;
			if (mediaPlayer != null) {
				mediaPlayer.events().removeMediaPlayerEventListener(this);

				if (mediaPlayer.status().isPlaying()) {
					mediaPlayer.controls().stop();
				}

				mediaPlayer.release();
				mediaPlayer = null;
			}

			if (dataSource != null) {
				dataSource.disconnect();
			}
			released = true;
			this.state = 0;
		}
		this.notifyListeners(PlayerListener.CLOSED, null);
	}

	public void deallocate() throws IllegalStateException {
		if (this.state == 0) {
			throw new IllegalStateException("closed");
		}
		PlayerImpl.players.remove(this);
		if (playing) {
			try {
				this.stop();
				return;
			} catch (MediaException ex) {
				return;
			}
		}
		released = true;
		if (this.state == PREFETCHED) {
			state = REALIZED;
		} else {
			if (this.state != REALIZED) {
				return;
			}
			state = UNREALIZED;
		}
		dataSource = null;
	}

	public void start() throws IllegalStateException, MediaException {
		if (this.state == 0) {
			throw new IllegalStateException("closed");
		}

		if (this.state == UNREALIZED) {
			realize();
		}
		if (released)
			throw new IllegalStateException("mediaPlayer released");
		if (this.state != STARTED) {
			if (!prepared) {
				prefetch();
			}
			PlayerImpl.players.add(this);
			mediaPlayer.submit(new Runnable() {
				@Override
				public void run() {
					synchronized (vlcLock) {
						if (mediaPlayer != null) mediaPlayer.controls().start();
					}
				}
			});
			if (volume == -1) {
				notifyListeners(PlayerListener.VOLUME_CHANGED, volume = 50);
				mediaPlayer.submit(new Runnable() {
					@Override
					public void run() {
						synchronized (vlcLock) {
							if (mediaPlayer != null) mediaPlayer.audio().setVolume(volume);
						}
					}
				});
			}
			playing = true;
			state = STARTED;
		}
	}

	protected void update() {
		if (item != null) {
			item.update();
		}
		Emulator.getEventQueue().queueRepaint();
	}

	public void stop() throws IllegalStateException, MediaException {
		if (this.state == 0) {
			throw new IllegalStateException("closed");
		}
		if (playing) {
			mediaPlayer.submit(new Runnable() {
				@Override
				public void run() {
					synchronized (vlcLock) {
						if (mediaPlayer != null) mediaPlayer.controls().pause();
					}
				}
			});
			if (state == STARTED)
				state = PREFETCHED;
			playing = false;
		}
	}

	public String getContentType() {
		return contentType;
	}

	public long getDuration() {
		if (released || mediaPlayer == null)
			throw new IllegalStateException();
		if (mediaPlayer.status() == null || mediaPlayer.status().length() < 0) {
			return TIME_UNKNOWN;
		}
		return mediaPlayer.status().length() * 1000L;
	}

	public long getMediaTime() {
		if (released || mediaPlayer == null)
			throw new IllegalStateException();
		if (mediaPlayer.status() == null || mediaPlayer.status().time() < 0) {
			return TIME_UNKNOWN;
		}
		return mediaPlayer.status().time() * 1000L;
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
		if (mediaPlayer.status() == null || !mediaPlayer.status().isSeekable()) {
			throw new MediaException();
		}
		synchronized (vlcLock) {
			mediaPlayer.controls().setTime(p0 / 1000L);
		}
		return mediaPlayer.status().time() * 1000L;
	}

	public Object getItem() {
		if (item == null) {
			item = new VLCPlayerItem(this);
		}
		return item;
	}

	protected void notifyListeners(final String s, final Object o) {
		if ("started".equals(s)) {
			if (started) return;
			started = true;
		}
		if ("stopped".equals(s) || "endOfMedia".equals(s)) {
			if (!Settings.enableMediaDump)
				PlayerImpl.players.remove(this);
			if (state == STARTED) {
				state = PREFETCHED;
			}
			started = false;
		}
		if (released || listeners == null)
			return;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final Enumeration<PlayerListener> elements = VLCPlayerImpl.this.listeners.elements();
					while (elements.hasMoreElements()) {
						elements.nextElement().playerUpdate(VLCPlayerImpl.this, s.intern(), o);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void setLoopCount(int p0) {
		if (p0 == -1) {
			synchronized (vlcLock) {
				if (mediaPlayer != null) mediaPlayer.controls().setRepeat(true);
			}
		}
	}

	public void setTimeBase(TimeBase tb) throws MediaException {
		if (this.state == UNREALIZED || this.state == STARTED || this.state == CLOSED) {
			throw new IllegalStateException();
		}
	}

	public TimeBase getTimeBase() {
		if (this.state == UNREALIZED || this.state == CLOSED) {
			throw new IllegalStateException();
		}
		return this.timeBase;
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
			bufferWidth = w;
			bufferHeight = h;
			return new RV32BufferFormat(w, h);
		}

		@Override
		public void allocatedBuffers(ByteBuffer[] buffers) {
			bb = buffers[0];
			img = new BufferedImage(bufferWidth, bufferHeight, BufferedImage.TYPE_3BYTE_BGR);
		}
	}

	private class MyRenderCallback implements RenderCallback {
		@Override
		public void display(MediaPlayer mediaPlayer, ByteBuffer[] buffers, BufferFormat bufferFormat) {
			bb.rewind();
			byte[] raw = new byte[bb.capacity()];
			bb.get(raw);
			DataBuffer buffer = new DataBufferByte(raw, raw.length);
			SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, bufferWidth, bufferHeight, 4, bufferWidth
					* 4, new int[]{2, 1, 0});
			Raster r = Raster.createRaster(sampleModel, buffer, null);
			if (img == null || img.getWidth() != bufferWidth || img.getHeight() != bufferHeight) {
				img = new BufferedImage(bufferWidth, bufferHeight, BufferedImage.TYPE_3BYTE_BGR);
			}
			img.setData(r);
			update();
		}
	}

	@Override
	public void audioDeviceChanged(MediaPlayer arg0, String arg1) {
		notifyListeners("vlc.audioDeviceChanged", arg1);
	}

	@Override
	public void backward(MediaPlayer arg0) {
	}

	@Override
	public void buffering(MediaPlayer arg0, float arg1) {
		notifyListeners("vlc.buffering", new Double(arg1));
	}

	@Override
	public void chapterChanged(MediaPlayer arg0, int arg1) {
		notifyListeners("vlc.chapterChanged", arg1);
	}

	@Override
	public void corked(MediaPlayer arg0, boolean arg1) {
	}

	@Override
	public void elementaryStreamAdded(MediaPlayer arg0, TrackType arg1, int arg2) {
	}

	@Override
	public void elementaryStreamDeleted(MediaPlayer arg0, TrackType arg1, int arg2) {
	}

	@Override
	public void elementaryStreamSelected(MediaPlayer arg0, TrackType arg1, int arg2) {
	}

	@Override
	public void forward(MediaPlayer arg0) {
	}

	@Override
	public void lengthChanged(MediaPlayer arg0, long arg1) {
		if (!lengthNotified) {
			lengthNotified = true;
			return;
		}
		notifyListeners(PlayerListener.DURATION_UPDATED, arg1);
	}

	@Override
	public void mediaChanged(MediaPlayer arg0, MediaRef arg1) {
		notifyListeners("vlc.mediaChanged", arg1.toString());
	}

	@Override
	public void mediaPlayerReady(MediaPlayer arg0) {
		notifyListeners("vlc.mediaPlayerReady", null);
	}

	@Override
	public void muted(MediaPlayer arg0, boolean arg1) {
	}

	@Override
	public void opening(MediaPlayer arg0) {
	}

	@Override
	public void pausableChanged(MediaPlayer arg0, int arg1) {
	}

	@Override
	public void paused(MediaPlayer arg0) {
		if (released) return;
		this.state = PREFETCHED;
		if (stoppedAtTime) {
			notifyListeners(PlayerListener.STOPPED_AT_TIME, getMediaTime());
			stoppedAtTime = false;
			return;
		}
		notifyListeners(PlayerListener.STOPPED, getMediaTime());
	}

	@Override
	public void positionChanged(MediaPlayer arg0, float arg1) {
		//notifyListeners("positionChanged", new Double(arg1));
	}

	@Override
	public void scrambledChanged(MediaPlayer arg0, int arg1) {
	}

	@Override
	public void seekableChanged(MediaPlayer arg0, int arg1) {
	}

	@Override
	public void snapshotTaken(MediaPlayer arg0, String arg1) {
		notifyListeners("vlc.snapshotTaken", arg1);
	}

	@Override
	public void stopped(MediaPlayer arg0) {
		if (released || state == CLOSED) return;
		if (state == STARTED) this.state = PREFETCHED;
		if (stoppedAtTime) {
			notifyListeners(PlayerListener.STOPPED_AT_TIME, getMediaTime());
			stoppedAtTime = false;
			return;
		}
		notifyListeners(PlayerListener.STOPPED, getMediaTime());
	}

	@Override
	public void timeChanged(MediaPlayer arg0, long time) {
		if (stopTime != StopTimeControl.RESET && time >= stopTime / 1000L && time <= stopTime / 1000L + 1000) {
			stoppedAtTime = true;
			if (mediaPlayer != null) {
				mediaPlayer.submit(new Runnable() {
					@Override
					public void run() {
						try {
							stop();
						} catch (MediaException ignored) {}
					}
				});
			}
		}
	}

	@Override
	public void titleChanged(MediaPlayer arg0, int arg1) {
		notifyListeners("vlc.titleChanged", arg1);
	}

	@Override
	public void videoOutput(MediaPlayer arg0, int arg1) {
		Dimension dimensions = arg0.video().videoDimension();
		if (dimensions == null) return;
		int w = sourceWidth;
		int h = sourceHeight;
		sourceHeight = dimensions.height;
		sourceWidth = dimensions.width;
		if (w != sourceWidth || h != sourceHeight)
			notifyListeners(PlayerListener.SIZE_CHANGED, videoControl);
	}

	@Override
	public void volumeChanged(MediaPlayer arg0, float arg1) {
		notifyListeners("com.nokia.external.volume.event", (int) (arg1 * 100F));
	}

	public void finished(MediaPlayer mediaPlayer) {
		this.state = PREFETCHED;
		this.prepared = false;
		notifyListeners(PlayerListener.END_OF_MEDIA, getMediaTime());
	}

	public void error(MediaPlayer mediaPlayer) {
		Manager.log("vlcplayer error");
		notifyListeners(PlayerListener.ERROR, null);
	}

	public void playing(MediaPlayer mediaPlayer) {
		Manager.log("vlcplayer started");
		notifyListeners(PlayerListener.STARTED, getMediaTime());
	}

	protected void finalize() {
		close();
	}

}
