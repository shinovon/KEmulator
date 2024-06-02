package javax.microedition.media;

import emulator.Emulator;
import emulator.Settings;
import emulator.custom.*;
import emulator.media.capture.CapturePlayerImpl;
import uk.co.caprica.vlcj.binding.LibC;
import uk.co.caprica.vlcj.binding.RuntimeUtil;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.factory.discovery.strategy.BaseNativeDiscoveryStrategy;
import uk.co.caprica.vlcj.factory.discovery.strategy.LinuxNativeDiscoveryStrategy;
import uk.co.caprica.vlcj.factory.discovery.strategy.NativeDiscoveryStrategy;
import uk.co.caprica.vlcj.factory.discovery.strategy.OsxNativeDiscoveryStrategy;
import uk.co.caprica.vlcj.factory.discovery.strategy.WindowsNativeDiscoveryStrategy;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.microedition.io.*;
import javax.microedition.io.file.FileConnectionImpl;
import javax.microedition.media.protocol.DataSource;

import emulator.media.vlc.VLCPlayerImpl;

import com.nokia.microedition.media.BufferDataSource;

public class Manager {
	public static final String TONE_DEVICE_LOCATOR = "device://tone";
	public static final String MIDI_DEVICE_LOCATOR = "device://midi";
	public static final String CONTENT_TYPE_UNKNOWN = "unknown";
	public static final String CONTENT_TYPE_MIDI = "audio/midi";
	public static final String CONTENT_TYPE_XMIDI = "audio/x-midi";
	public static final String CONTENT_TYPE_XWAVE = "audio/x-wav";
	public static final String CONTENT_TYPE_WAVE = "audio/wav";
	public static final String CONTENT_TYPE_TONE = "audio/x-tone-seq";
	public static final String CONTENT_TYPE_AMR = "audio/amr";
	public static final String CONTENT_TYPE_MPEG = "audio/mpeg";
	public static final String PROTOCOL_FILE = "file";
	public static final String PROTOCOL_HTTP = "http";
	private static final byte[] MP3_HEADER = new byte[]{0x49, 0x44, 0x33};
	private static final byte[] MPEG1_2_VIDEO_HEADER = new byte[]{0x00, 0x00, 0x01, (byte) 0xB3};
	private static final byte[] WAV_HEADER = new byte[]{0x52, 0x49, 0x46, 0x00, 0x00, 0x00, 0x00, 0x57, 0x41, 0x56, 0x45};
	private static final byte[] AVI_HEADER = new byte[]{0x52, 0x49, 0x46, 0x00, 0x00, 0x00, 0x00, 0x41, 0x56, 0x49, 0x20};
	static TimeBase systemTimeBase;
	private static Vector<String> kemAudio;
	private static Vector<String> kemVideo;
	private static Vector<String> vlcAudio;
	private static Vector<String> vlcVideo;
	private static int libVlcState;

	public Manager() {
		super();
	}

	public static Player createPlayer(final InputStream inputStream, final String s) throws IOException, MediaException {
		log("createPlayer " + inputStream + " " + s);
		if (inputStream == null) {
			throw new IllegalArgumentException();
		}
		if (s != null && s.startsWith("video/")) {
			requireLibVlc();
			return new VLCPlayerImpl(inputStream, s);
		}
		if ("audio/amr".equals(s) && isLibVlcSupported() && Emulator.isX64()) {
			return new VLCPlayerImpl(inputStream, s);
		}
		// buffer
		boolean buf = Settings.playerBufferAll;
		if (buf && !(inputStream instanceof ByteArrayInputStream))
			return new PlayerImpl(new ByteArrayInputStream(CustomJarResources.getBytes(inputStream)), s);
		else
			return new PlayerImpl(inputStream, s);
	}

	public static Player createPlayer(String s) throws IOException, MediaException {
		log("createPlayer " + s);
		if (s.startsWith(TONE_DEVICE_LOCATOR)) {
			return new ToneImpl();
		}
		if (s.startsWith(MIDI_DEVICE_LOCATOR)) {
			return new MIDIImpl();
		}
		if (s.startsWith("capture://image") || s.startsWith("capture://video") || s.startsWith("capture://devcam0") || s.startsWith("capture://devcam1")) {
			return new CapturePlayerImpl();
		}
		String contentType = null;
		try {
			contentType = getContentTypeFromLocation(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (contentType != null && contentType.startsWith("video/")) {
			requireLibVlc();
			if (s.startsWith("rtsp://") || s.startsWith("http://") || s.startsWith("https://") || s.startsWith("rtp://")) {
				return new VLCPlayerImpl(s, contentType);
			}
			if (s.startsWith("file:///")) {
				String f = null;
				FileConnectionImpl fc = (FileConnectionImpl) Connector.open(s);
				f = fc.getRealPath();
				fc.close();
				return new VLCPlayerImpl(f, contentType);
			}
			// other protocols
			if (s.indexOf(58) != -1) {
				return new VLCPlayerImpl(s, contentType);
			}
			// jar resources
			return new VLCPlayerImpl(CustomJarResources.getResourceAsStream(s), contentType);
		} else if (contentType != null && contentType.startsWith("audio/")) {
			if (s.startsWith("rtsp://") || s.startsWith("rtp://") || isAudioContentTypeRequiresLibVlc(contentType)) {
				requireLibVlc();
				return new VLCPlayerImpl(s, contentType);
			}
			if (s.startsWith("file:///")) {
				String f = null;
				FileConnectionImpl fc = (FileConnectionImpl) Connector.open(s);
				f = fc.getRealPath();
				fc.close();
				return new PlayerImpl(f, contentType);
			}
			if (s.indexOf(58) != -1) {
				return createPlayer(((InputConnection) Connector.open(s)).openInputStream(), contentType);
			}
			return createPlayer(CustomJarResources.getResourceAsStream(s), contentType);
		}
		throw new MediaException("Unknown content type");
	}

	public static Player createPlayer(final DataSource src) throws IOException, MediaException {
		log("createPlayer DataSource(" + src + ")");
		String contentType = src.getContentType();
		String locator = src.getLocator();
		log("DataSource Content-Type: " + contentType);
		log("DataSource Locator: " + locator);
		Player player = null;
		if (contentType != null) {
			if (contentType.startsWith("audio/") && isAudioContentTypeSupportedForDataSource(contentType)) {
				if (locator != null) {
					// rtsp
					if (locator.startsWith("rtps://") || locator.startsWith("rtp://")) {
						requireLibVlc();
						return new VLCPlayerImpl(locator, contentType);
					}
					// other protocols
					if (locator.indexOf(58) != -1) {
						return createPlayer(((InputConnection) Connector.open(locator)).openInputStream(), contentType);
					}
					// jar resources
					return createPlayer(CustomJarResources.getResourceAsStream(locator), contentType);
				} else {
					// streaming datasource
					player = new PlayerImpl(contentType, src);
				}
				// Videos
			} else if (contentType.startsWith("video/") || Manager.isLibVlcSupported()) {
				requireLibVlc();
				if (locator != null) {
					// located
					src.connect();
					player = new VLCPlayerImpl(locator, contentType, src);
				} else {
					// streaming datasource
					player = new VLCPlayerImpl(contentType, src);
				}
			} else {
				throw new MediaException("Content type not supported: " + contentType);
			}
		}
		if (player == null && locator != null) {
			// check content type
			if (locator.startsWith("rtps://") || locator.startsWith("rtp://")) {
				requireLibVlc();
				return new VLCPlayerImpl(locator, contentType);
			}
			contentType = getContentTypeFromLocation(src.getLocator());

			log("getContentType(): " + contentType);
			if (contentType != null && contentType.startsWith("video/")) {
				requireLibVlc();
				player = new VLCPlayerImpl(locator, contentType, src);
			} else {
				player = new PlayerImpl(locator, contentType, src);
			}
		}
		if (player == null) {
			// TODO: parse header data
			BufferDataSource bds = new BufferDataSource(src);
			byte[] header = bds.getHeader();
			if (checkMp3Header(header)) {

			}
			//player = new PlayerImpl(bds);
		}
		if (player == null) {
			throw new MediaException("Unsupported DataSource");
		}
		return player;
	}

	private static void requireLibVlc() throws MediaException {
		if (!isLibVlcSupported()) {
			throw new MediaException("LibVlc required!");
		}
	}

	private static boolean checkMp3Header(byte[] header) {
		return header[0] == MP3_HEADER[0] && header[2] == MP3_HEADER[2] && header[3] == MP3_HEADER[3];
	}

	private static boolean checkMpeg1_2video(byte[] header) {
		return header[0] == MPEG1_2_VIDEO_HEADER[0] && header[1] == MPEG1_2_VIDEO_HEADER[1]
				&& header[2] == MPEG1_2_VIDEO_HEADER[2] && header[3] == MPEG1_2_VIDEO_HEADER[3];
	}

	private static boolean checkMpegTransportStream(byte[] header) {
		return header[0] == 0x47;
	}

	private static boolean isAudioContentTypeRequiresLibVlc(String c) {
		for (String s : kemAudio) {
			if (c.equals(s)) return false;
		}
		for (String s : vlcAudio) {
			if (c.equals(s)) return true;
		}
		return false;
	}

	private static boolean isAudioContentTypeSupportedForDataSource(String c) {
		return c.equals("audio/mpeg") || c.equals("audio/mp3") || c.equals("audio/amr") || c.equals("audio/aac") || c.equals("audio/wav");
	}

	static {
		// KEmulator supported types
		kemAudio = new Vector<String>();
		kemAudio.add("audio/mid");
		kemAudio.add("audio/midi");
		kemAudio.add("audio/x-midi");
		kemAudio.add("audio/wav");
		kemAudio.add("audio/x-wav");
		kemAudio.add("audio/x-tone-seq");
		kemAudio.add("audio/amr");
		kemAudio.add("audio/x-amr");
		kemAudio.add("audio/mpeg");
		kemAudio.add("audio/mp3");
		kemVideo = new Vector<String>();
		// Supported types with libvlc
		vlcAudio = new Vector<String>();
		vlcAudio.add("audio/amr-wb");
		vlcAudio.add("audio/aac");
		vlcAudio.add("audio/x-ms-wma");
		vlcAudio.add("audio/mp4");
		vlcAudio.add("audio/webm");
		vlcAudio.add("audio/3gpp");
		vlcAudio.add("audio/3gpp2");
		vlcAudio.add("audio/ogg");
		vlcAudio.add("audio/opus");
		vlcAudio.add("audio/adpcm");
		vlcAudio.add("audio/x-aac");
		vlcAudio.add("audio/x-mpegurl");
		vlcAudio.add("audio/mpga");
		vlcVideo = new Vector<String>();
		vlcVideo.add("video/mpeg");
		vlcVideo.add("video/mpeg-4");
		vlcVideo.add("video/mp4");
		vlcVideo.add("video/h.263");
		vlcVideo.add("video/h264");
		vlcVideo.add("video/h.264");
		vlcVideo.add("video/h.265");
		vlcVideo.add("video/quicktime");
		vlcVideo.add("video/x-ms-video");
		vlcVideo.add("video/3gpp");
		vlcVideo.add("video/3gpp2");
		vlcVideo.add("video/flv");
		vlcVideo.add("video/mp2t");
		vlcVideo.add("video/webm");
		vlcVideo.add("video/ogg");
	}

	public static String[] getSupportedContentTypes(final String protocol) {
		// Full supported types list
		Vector<String> fullList = new Vector<String>();
		fullList.addAll(kemAudio);
		fullList.addAll(kemVideo);
		boolean vlc = isLibVlcSupported();
		if (vlc) {
			fullList.addAll(vlcAudio);
			fullList.addAll(vlcVideo);
		}
		String[] arr = fullList.toArray(new String[0]);

		if (protocol == null || protocol.length() <= 1)
			return arr;
		else if (protocol.startsWith("http")) {
			return new String[]{"audio/mpeg", "audio/mp3", "audio/amr"};
		} else if (protocol.startsWith("rtsp") || protocol.startsWith("rtp")) {
			if (vlc) {
				Vector<String> rtsp = new Vector<String>();
				return new String[]{"audio/mpeg", "audio/mp3", "audio/amr"};
			}
			return new String[0];
		} else {
			ArrayList<String> list = new ArrayList<String>();
			for (String t : arr) {
				if (t.startsWith(protocol.toLowerCase()))
					list.add(t);
			}
			return list.toArray(new String[0]);
		}
	}

	private static String getContentTypeFromLocation(String locator) throws IOException {
		String c = null;
		if (locator.startsWith("http://") || locator.startsWith("https://")) {
			try {
				c = getContentTypeHttp(locator);
			} catch (IOException ignored) {
			}
			if (c == null || c.equals("text/plain")) c = getContentTypeFromURL(locator);
		} else {
			c = getContentTypeFromURL(locator);
		}
		return c;
	}

	private static String getContentTypeHttp(String locator) throws IOException {
		// позаимствованно из симбиана
		HttpConnection hc = (HttpConnection) Connector.open(locator);
		hc.setRequestMethod("HEAD");
		if (hc.getResponseCode() == 405) {
			hc.close();
			hc = null;
			hc = (HttpConnection) Connector.open(locator);
			hc.setRequestMethod("GET");
		}
		int rc = hc.getResponseCode();
		if (rc != 200) {
			throw new IOException("HTTP response code: " + rc);
		}
		String cntype = hc.getHeaderField("Content-Type");
		hc.close();
		hc = null;
		return cntype;
	}

	public static String[] getSupportedProtocols(final String type) {
		Vector<String> kem = new Vector<String>();
		kem.add("device://tone");
		kem.add("capture://video");
		kem.add("capture://image");
		kem.add("file");
		kem.add("http");
		kem.add("https");
		Vector<String> vlc = new Vector<String>();
		vlc.add("rtsp");
		vlc.add("rtp");
		Vector<String> fullList = new Vector<String>(kem);
		if (isLibVlcSupported()) {
			fullList.addAll(vlc);
		}
		return fullList.toArray(new String[0]);
	}

	public static String getContentTypeFromURL(String url) throws IOException {
		// remove query
		if (url.contains("?")) {
			url = url.substring(0, url.indexOf("?"));
		}
		if (url.endsWith(".amr")) {
			return "audio/amr";
		}
		if (url.endsWith(".aac")) {
			return "audio/aac";
		}
		if (url.endsWith(".awb")) {
			return "audio/amr-wb";
		}
		if (url.endsWith(".mp3")) {
			return "audio/mpeg";
		}
		if (url.endsWith(".wma")) {
			return "audio/x-ms-wma";
		}
		if (url.endsWith(".ra")) {
			return "audio/x-pn-realaudio";
		}
		if (url.endsWith(".mp4")) {
			return "video/mp4";
		}
		if (url.endsWith(".mpg") || url.endsWith(".mpeg")) {
			return "video/mpeg";
		}
		if (url.endsWith(".3gpp") || url.endsWith(".3gp")) {
			return "video/3gpp";
		}
		if (url.endsWith(".3gpp2") || url.endsWith(".3g2")) {
			return "video/3gpp2";
		}
		if (url.endsWith(".mid")) {
			return "audio/midi";
		}
		if (url.endsWith(".midi")) {
			return "audio/x-midi";
		}
		if (url.endsWith(".avi")) {
			return "video/x-msvideo";
		}
		if (url.endsWith(".mov")) {
			return "video/quicktime";
		}
		if (url.endsWith(".flv")) {
			return "video/flv";
		}
		if (url.endsWith(".ogg") || url.endsWith(".oga")) {
			return "audio/ogg";
		}
		if (url.endsWith(".ogv")) {
			return "video/ogg";
		}
		if (url.endsWith(".opus")) {
			return "audio/opus";
		}
		if (url.endsWith(".webm")) {
			return "video/webm";
		}
		if (url.endsWith(".weba")) {
			return "audio/webm";
		}
		if (url.endsWith(".m3u")) {
			return "audio/x-mpegurl";
		}
		if (url.endsWith(".m4v")) {
			return "video/x-m4v";
		}
		if (url.endsWith(".mpga")) {
			return "audio/mpga";
		}
		if (url.endsWith(".wav")) {
			return "audio/wav";
		}
		return null;
	}

	public static boolean isLibVlcSupported() {
		while (libVlcState == 0) {
			Thread.yield();
		}
		return libVlcState == 1;
	}

	public static void checkLibVlcSupport() {
		/*
		if(Settings.vlcDir == null) {
			try {
				File f = new File("./libvlc.dll");
				if(!f.exists()) {
					log("Vlc path not set");
					libVlcState = -1;
					return;
				}
			} catch (Exception e) {
			}
		}
		*/
		try {
			List<NativeDiscoveryStrategy> list = new ArrayList<NativeDiscoveryStrategy>();
			if (Settings.vlcDir != null && Settings.vlcDir.length() > 2) {
				NativeDiscoveryStrategy win = new BaseNativeDiscoveryStrategy(new String[]{
						"libvlc\\.dll",
						"libvlccore\\.dll"
				}, new String[]{
						"%s\\plugins",
						"%s\\vlc\\plugins"
				}) {

					@Override
					public boolean supported() {
						// kemulator is windows only
						return Emulator.isX64() ? RuntimeUtil.isWindows() : true;
					}

					@Override
					public List<String> discoveryDirectories() {
						List<String> directories = new ArrayList<String>();
						try {
							directories.add(new File(Settings.vlcDir).getCanonicalPath());
						} catch (IOException e) {
							e.printStackTrace();
						}
						return directories;
					}

					@Override
					protected boolean setPluginPath(String pluginPath) {
						return LibC.INSTANCE._putenv(String.format("%s=%s", PLUGIN_ENV_NAME, pluginPath)) == 0;
					}

				};
				list.add(win);
			}
			list.add(new WindowsNativeDiscoveryStrategy());
			if (Emulator.isX64()) {
				list.add(new OsxNativeDiscoveryStrategy());
				list.add(new LinuxNativeDiscoveryStrategy());
			}
			NativeDiscovery nd = new NativeDiscovery(list.toArray(new NativeDiscoveryStrategy[0]));
			boolean b = nd.discover();
			if (b) libVlcState = 1;
			else libVlcState = -1;
			if (b) {
				log("LibVlc loaded");
				return;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		log("LibVlc not loaded");
		libVlcState = -1;
		return;
	}

	public static void playTone(final int n, final int n2, final int n3) throws MediaException {
	}

	public static TimeBase getSystemTimeBase() {
		if (Manager.systemTimeBase == null) {
			Manager.systemTimeBase = new SystemTimeBaseImpl();
		}
		return Manager.systemTimeBase;
	}

	public static void log(String s) {
		Emulator.getEmulator().getLogStream().println("[MEDIA] " + s);
	}
}
