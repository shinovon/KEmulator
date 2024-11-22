package emulator.media.vlc;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.media.protocol.DataSource;

import emulator.Emulator;
import emulator.custom.CustomJarResources;
import emulator.graphics2D.awt.ImageAWT;

public class VLCPlayerImpl implements Player {

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
	public BufferedImage img;
	public ByteBuffer bb;
	boolean released;
	private DataSource dataSource;
	private boolean lengthNotified;
	int volume = -1;
	private File tempFile;

	private boolean started;

	private VideoControl videoControl;
	private VolumeControl volumeControl;
	private RateControl rateControl;
	private StopTimeControl stopTimeControl;
	private MetaDataControl metaDataControl;

	long stopTime = StopTimeControl.RESET;
	private boolean stoppedAtTime;

	private VLCPlayerImpl() {
		this.listeners = new Vector();
		videoControl = new VideoControlImpl(this);
		volumeControl = new VolumeControlImpl(this);
		rateControl = new RateControlImpl(this);
		stopTimeControl = new StopTimeControlImpl(this);
		metaDataControl = new MetaDataControlImpl(this);
		controls = new Control[]{videoControl, volumeControl, rateControl, stopTimeControl};
		this.timeBase = Manager.getSystemTimeBase();
		PlayerImpl.players.add(this);
	}

	public VLCPlayerImpl(String url) throws IOException {
		this();
		if (url.startsWith("file:///root/")) {
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
		inst.paint(g);
	}

	private void paint(Graphics g) {
	}

	private static Image awtImgToLcdui(BufferedImage img) {
		return new Image(new ImageAWT(img));
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
	}

	public void prefetch() throws IllegalStateException, MediaException {
	}

	public void realize() throws IllegalStateException, MediaException {
	}

	public void close() {
	}

	public void deallocate() throws IllegalStateException {
	}

	public void start() throws IllegalStateException, MediaException {
	}

	private void update() {
		Emulator.getEventQueue().queueRepaint();
	}

	public void stop() throws IllegalStateException, MediaException {
	}

	public String getContentType() {
		return contentType;
	}

	public long getDuration() {
		return 0;
	}

	public long getMediaTime() {
		return 0;
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
		return 0;
	}

	public Object getItem() {
		// TODO
		return null;
	}

	protected void notifyListeners(final String s, final Object o) {
		if ("started".equals(s)) {
			if (started) return;
			started = true;
		}
		if ("stopped".equals(s) || "endOfMedia".equals(s)) {
			if (state == STARTED) {
				state = PREFETCHED;
			}
			started = false;
		}
		if (listeners == null)
			return;
		try {
			final Enumeration<PlayerListener> elements = this.listeners.elements();
			while (elements.hasMoreElements()) {
				elements.nextElement().playerUpdate(this, s.intern(), o);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void setLoopCount(int p0) {
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

}
