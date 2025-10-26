package javax.microedition.media;

import emulator.Emulator;
import emulator.Settings;
import emulator.custom.ResourceManager;
import emulator.media.EmulatorMIDI;
import emulator.media.amr.AMRDecoder;
import emulator.media.tone.ToneControlImpl;
import emulator.javazoom.jl.decoder.Header;
import emulator.javazoom.jl.decoder.JavaLayerException;

import javax.microedition.io.Connector;
import javax.microedition.media.control.MIDIControlImpl;
import javax.microedition.media.control.VolumeControlImpl;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;
import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.*;
import java.util.*;
import java.util.zip.CRC32;

public class PlayerImpl implements Player, Runnable, LineListener, MetaEventListener {

	private static int count;
	private static boolean midiPlaying;

	public static Set<Player> players = Collections.newSetFromMap(new WeakHashMap());
	Object sequence;
	Thread playerThread;
	boolean complete;
	private int state;
	private String contentType;
	private Vector<PlayerListener> listeners;
	Control midiControl;
	Control toneControl;
	Control volumeControl;
	Control[] controls;
	TimeBase timeBase;
	public int loopCount;
	public int dataLen;
	private DataSource dataSource;
	private boolean dataSourceDisconnected;
	private int level = 100;
	private byte[] data;
	private long mediaTime;
	private final Object playLock = new Object();
	private Sequencer midiSequencer;
	private Synthesizer midiSynthesizer;
	private boolean stopped;
	private InputStream inputStream;
	private boolean realized;

	private static final Vector<WavCache> wavCache = new Vector<WavCache>();

	private WavCache cacheRef;

	public PlayerImpl() {
		loopCount = 1;
		state = UNREALIZED;
		listeners = new Vector<PlayerListener>();
		timeBase = Manager.getSystemTimeBase();
		volumeControl = new VolumeControlImpl(this);
		toneControl = new ToneControlImpl();
		if (Settings.enableMediaDump) players.add(this);
	}

	public PlayerImpl(String contentType, DataSource src) throws IOException, MediaException {
		this();
		this.contentType = contentType;
		this.dataSource = src;
	}

	public PlayerImpl(final InputStream inputStream, String contentType) throws IOException {
		this();
		init(inputStream, contentType);
	}

	public PlayerImpl(String locator, String contentType, DataSource src) throws IOException {
		this();
		if (locator != null) {
			inputStream = Connector.openInputStream(locator);
		}
		this.contentType = contentType;
		this.dataSource = src;
	}

	public PlayerImpl(String file, String contentType) throws IOException {
		this();
		dataLen = (int) new File(file).length();
		init(new FileInputStream(file), contentType);
	}

	private void init(final InputStream inputStream, String contentType) throws IOException {
		if (contentType == null)
			contentType = "";
		this.contentType = contentType.toLowerCase();
		if (dataLen == 0) dataLen = inputStream.available();
		this.inputStream = inputStream;
	}

	private void amr(final InputStream inputStream) throws IOException {
		controls = new Control[]{toneControl, volumeControl};
		try {
			final byte[] b;
			// Use new AMR decoder
			b = AMRDecoder.decode(data = ResourceManager.getBytes(inputStream));
			if (b == null) {
				throw new MediaException("Cannot parse AMR data");
			}
			if (Settings.enableMediaDump) data = b;
			InputStream i = this.inputStream = new ByteArrayInputStream(b);
			final AudioFormat audioFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					8000.0f,
					16,
					1,
					2,
					8000.0f,
					false
			);
			final AudioInputStream audioInputStream = new AudioInputStream(i, audioFormat, -1L);
			final Clip clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, audioFormat));
			clip.addLineListener(this);
			clip.open(audioInputStream);
			sequence = clip;
		} catch (Throwable e) {
			Emulator.getEmulator().getLogStream().println("AMR realize error: " + e);
			sequence = null;
			//throw new IOException("AMR realize error", e);
		}
	}

	private void wav(InputStream inputStream) throws IOException {
		controls = new Control[]{toneControl, volumeControl};
		byte[] data;
		WavCache key = null;
		try {
			if (inputStream instanceof ByteArrayInputStream || Settings.enableMediaDump) {
				data = ResourceManager.getBytes(inputStream);
				if (Settings.wavCache) {
					key = new WavCache(data);
					synchronized (wavCache) {
						int i = wavCache.indexOf(key);
						if (i != -1) {
							key = wavCache.get(i);
							sequence = key.clip;
							cacheRef = key;
							key.setPlayer(this);
							setMediaTime(0);
							return;
						}
						wavCache.add(key);
					}
				}

				inputStream = this.inputStream = new ByteArrayInputStream(data);
			}

			sequence = AudioSystem.getAudioInputStream(inputStream);

			AudioInputStream audioInputStream;
			AudioFormat format;
			if ((format = (audioInputStream = (AudioInputStream) sequence).getFormat())
					.getEncoding() == AudioFormat.Encoding.ULAW || format.getEncoding() == AudioFormat.Encoding.ALAW) {
				final AudioFormat audioFormat;
				audioInputStream = AudioSystem
						.getAudioInputStream(audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format
								.getSampleRate(), format.getSampleSizeInBits() * 2, format
								.getChannels(), format.getFrameSize()
								* 2, format.getFrameRate(), true), audioInputStream);
				format = audioFormat;
			}
			final Clip clip;
			(clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, audioInputStream
					.getFormat(), (int) audioInputStream.getFrameLength() * format.getFrameSize())))
					.addLineListener(Settings.wavCache ? key : this);
			clip.open(audioInputStream);
			sequence = clip;

			if (Settings.wavCache && key != null) {
				synchronized (wavCache) {
					key.clip = (Clip) sequence;
					key.setPlayer(this);
					cacheRef = key;
				}
			}
		} catch (Exception e) {
			System.out.println("WAV realize error: " + e);
			sequence = null;
		}
	}

	private void midi(final InputStream inputStream) throws IOException {
		try {
			byte[] data = ResourceManager.getBytes(inputStream);
			if (Settings.enableMediaDump) this.data = data;
			sequence = MidiSystem.getSequence(this.inputStream = new ByteArrayInputStream(data));
		} catch (Exception e) {
			sequence = null;
		}
		midiControl = new MIDIControlImpl(this);
		controls = new Control[]{toneControl, volumeControl, midiControl};
	}


	public void setMIDISequence(InputStream in) throws IOException {
		midi(in);
	}

	public void addPlayerListener(final PlayerListener playerListener) throws IllegalStateException {
		if (state == CLOSED) {
			throw new IllegalStateException();
		}
		if (playerListener != null) {
			listeners.add(playerListener);
		}
	}

	public void removePlayerListener(final PlayerListener playerListener) throws IllegalStateException {
		if (state == CLOSED) {
			throw new IllegalStateException();
		}
		if (playerListener != null) {
			listeners.remove(playerListener);
		}
	}

	protected void notifyListeners(final String s, final Object o) {
		if (listeners == null)
			return;
		final Enumeration<PlayerListener> elements = listeners.elements();
		while (elements.hasMoreElements()) {
			elements.nextElement().playerUpdate(this, s.intern(), o);
		}
	}

	public void close() {
		players.remove(this);
		if (playerThread != null) {
			try {
				stop();
			} catch (Exception ignored) {}
		}
		if (sequence instanceof emulator.javazoom.jl.player.Player) {
			((emulator.javazoom.jl.player.Player) sequence).close();
		} else if (sequence instanceof Sequence) {
			if (midiSynthesizer != null) {
				midiSynthesizer.close();
				midiSynthesizer = null;
			}
			if (midiSequencer != null) {
				midiSequencer.close();
				midiSequencer = null;
			}
			if (Settings.oneMidiAtTime) {
				EmulatorMIDI.close(false);
			}
		}
//		else if (sequence instanceof Clip) {
//			try {
//				((Clip) sequence).close();
//			} catch (Exception ignored) {}
//		}
		if (inputStream != null) {
//			try {
//				inputStream.close();
//			} catch (Exception ignored) {}
			inputStream = null;
		}
		if (dataSource != null && !dataSourceDisconnected) {
			dataSource.disconnect();
			dataSourceDisconnected = true;
		}
		sequence = null;
		state = CLOSED;
		notifyListeners(PlayerListener.CLOSED, null);
	}

	public void deallocate() throws IllegalStateException {
		players.remove(this);
		data = null;
		if (state == CLOSED) {
			throw new IllegalStateException();
		}
		if (state == STARTED) {
			try {
				stop();
			} catch (MediaException ignored) {}
		}
//		if (sequence instanceof Clip) {
//			try {
//				((Clip) sequence).close();
//			} catch (Exception ignored) {}
//		}
		if (inputStream != null) {
//			try {
//				inputStream.close();
//			} catch (Exception ignored) {}
			inputStream = null;
		}
		if (state == PREFETCHED) {
			state = REALIZED;
		} else if (state == REALIZED) {
			state = UNREALIZED;
		} else {
			return;
		}
		if (dataSource != null && dataSource.getStreams() != null && dataSource.getStreams()[0] != null && !dataSourceDisconnected) {
			if (dataSource.getStreams()[0].getSeekType() == 0) {
				dataSource.disconnect();
				dataSourceDisconnected = true;
			}
		}
	}

	public String getContentType() {
		return contentType;
	}

	public long getDuration() {
		if (sequence == null) return 0;
		double res;
		if (sequence instanceof Sequence) {
			return ((Sequence) sequence).getMicrosecondLength();
		} else if (sequence instanceof Clip) {
			final Clip clip;
			res = (clip = (Clip) sequence).getBufferSize()
					/ (clip.getFormat().getFrameSize() * clip.getFormat().getFrameRate());
		} else if (sequence instanceof emulator.javazoom.jl.player.Player) {
			try {
				res = ((double) (dataLen * 8) / ((emulator.javazoom.jl.player.Player) sequence).getBitrate());
			} catch (ArithmeticException e) {
				return -1;
			}
		} else return 0;
		return (long) (res * 1000000D);
	}

	public long getMediaTime() {
		if (sequence == null) return mediaTime;
		if (sequence instanceof Clip) {
			return mediaTime = ((Clip) sequence).getMicrosecondPosition();
		}
		if (sequence instanceof Sequence) {
			try {
				if (midiSequencer != null)
					mediaTime = midiSequencer.getMicrosecondPosition();
				else if (Settings.oneMidiAtTime && EmulatorMIDI.currentPlayer == this && midiPlaying)
					mediaTime = EmulatorMIDI.getMicrosecondPosition();
			} catch (Exception ignored) {}
			return mediaTime;
		}
		if (sequence instanceof emulator.javazoom.jl.player.Player) {
			return mediaTime = ((emulator.javazoom.jl.player.Player) sequence).getPosition() * 1000L;
		}
		return TIME_UNKNOWN;
	}

	public long setMediaTime(final long t) throws MediaException {
		if (sequence == null) return 0;
		long ms = 0L;
		if (sequence instanceof Clip) {
			ms = ((Clip) sequence).getMicrosecondLength();
			if (t < ms) ms = t;
			if (ms < 0) ms = 0;
			synchronized (sequence) {
				((Clip) sequence).setMicrosecondPosition(mediaTime = t);
			}
		} else if (sequence instanceof Sequence) {
			ms = ((Sequence) sequence).getMicrosecondLength();
			if (t < ms) ms = t;
			if (ms < 0) ms = 0;
			mediaTime = ms;
			try {
				if (midiSequencer != null)
					midiSequencer.setMicrosecondPosition(ms);
				else if (Settings.oneMidiAtTime && EmulatorMIDI.currentPlayer == this)
					EmulatorMIDI.setMicrosecondPosition(ms);
			} catch (Exception e) {
				throw new MediaException(e);
			}
		} else if (sequence instanceof emulator.javazoom.jl.player.Player) {
			long l = getMediaTime();
//			if (true) return l; // TODO
			if (t == 0 && l == 0 || !((emulator.javazoom.jl.player.Player) sequence).isBuffered)
				return 0;
			if (t < l) {
				try {
					stop();
					Header old = ((emulator.javazoom.jl.player.Player) sequence).bitstream().header;
					((emulator.javazoom.jl.player.Player) sequence).reset();
					((emulator.javazoom.jl.player.Player) sequence).skip((int) (t / 1000L), old);
					ms = ((emulator.javazoom.jl.player.Player) sequence).getPosition() * 1000L;
					start();
				} catch (JavaLayerException e) {
					throw new MediaException(e);
				}
			} else {
				try {
					stop();
					Header old = ((emulator.javazoom.jl.player.Player) sequence).bitstream().header;
					((emulator.javazoom.jl.player.Player) sequence).reset();
					((emulator.javazoom.jl.player.Player) sequence).skip((int) (t / 1000L), old);
					ms = ((emulator.javazoom.jl.player.Player) sequence).getPosition() * 1000L;
					start();
				} catch (JavaLayerException e) {
					throw new MediaException(e);
				}
			}
		}
		return mediaTime = ms;
	}

	public int getState() {
		return state;
	}

	public void prefetch() throws IllegalStateException, MediaException {
		if (state == CLOSED) {
			throw new IllegalStateException();
		}
		if (state == UNREALIZED) {
			realize();
		} else if (state != REALIZED) {
			return;
		}
		if (sequence instanceof Sequence) {
			try {
				EmulatorMIDI.initDevice();
			} catch (Exception e) {
				e.printStackTrace();
				throw new MediaException(e);
			}
//			try {
//				if (sequence != null) {
//					if (Settings.oneMidiAtTime) {
//						EmulatorMIDI.setSequence((Sequence) sequence);
//					} else {
//						initMidiSequencer();
//						midiSequencer.setSequence((Sequence) sequence);
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				sequence = null;
//				throw new MediaException(e);
//			}
		}
		if (dataSource != null) {
			try {
				dataSource.connect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new MediaException(e);
			}
		}
		state = PREFETCHED;
	}

	public void realize() throws IllegalStateException, MediaException {
		if (state == CLOSED) {
			throw new IllegalStateException();
		}
		if (state == UNREALIZED) {
			state = REALIZED;
		}
		if (realized) return;
		realized = true;
		if (inputStream != null) {
			try {
				if ("audio/amr".equals(contentType) || "audio/x-amr".equals(contentType)) {
					amr(inputStream);
				} else if ("audio/x-wav".equals(contentType) || "audio/wav".equals(contentType)) {
					wav(inputStream);
				} else if ("audio/x-midi".equals(contentType) || "audio/midi".equals(contentType)) {
					midi(inputStream);
				} else if ("audio/mpeg".equals(contentType) || "audio/mp3".equals(contentType)) {
					try {
						InputStream i = inputStream;
						if (i instanceof ByteArrayInputStream || Settings.enableMediaDump) {
							data = ResourceManager.getBytes(i);
							i = this.inputStream = new ByteArrayInputStream(data);
						}
						sequence = new emulator.javazoom.jl.player.Player(i, false);
					} catch (JavaLayerException e) {
						e.printStackTrace();
						throw new IOException(e);
					}
					controls = new Control[]{volumeControl};
				} else {
					try {
						midi(inputStream);
					} catch (Exception e) {
						try {
							wav(inputStream);
						} catch (Exception e2) {
							try {
								amr(inputStream);
							} catch (Exception e3) {
								Emulator.getEmulator().getLogStream().println("*** unsupported sound format ***");
								sequence = null;
							}
						}
					}
				}
				setLevel(level);
				return;
			} catch (IOException e) {
				throw new MediaException(e);
			} finally {
				inputStream = null;
			}
		}
		if (dataSource != null) {
			try {
				dataSource.connect();
				//dataSource.start();
			} catch (IOException e) {
				throw new MediaException(e.toString());
			}
			boolean mp3 = false;

			final SourceStream stream = dataSource.getStreams()[0];
			String streamContentType = stream.getContentDescriptor().getContentType();
			if (streamContentType != null) {
				if (streamContentType.equalsIgnoreCase("audio/mpeg") || contentType.equalsIgnoreCase("audio/mp3")) {
					mp3 = true;
					if (contentType == null)
						contentType = "audio/mpeg";
				}
			}
			if (mp3 || contentType.equalsIgnoreCase("audio/mpeg") || contentType.equalsIgnoreCase("audio/mp3")) {
				dataLen = (int) stream.getContentLength();
				try {
					sequence = new emulator.javazoom.jl.player.Player(new InputStream() {

						public int read() throws IOException {
							byte[] b = new byte[1];
							int i = read(b, 0, 1);
							if (i == 1) {
								return b[0] & 0xFF;
							}
							return -1;
						}

						public int read(byte[] b, int i, int j) throws IOException {
							return stream.read(b, i, j);
						}

					}, false);
				} catch (IOException e) {
					throw new MediaException(e);
				} catch (JavaLayerException e) {
					throw new MediaException(e);
				}
				controls = new Control[]{volumeControl};
				setLevel(level);
				listeners = new Vector();
				timeBase = Manager.getSystemTimeBase();
			} else {
				throw new MediaException("Unsupported content type: " + contentType);
			}
		}
	}

	public void setLoopCount(final int loopCount) {
		if (loopCount == 0) {
			throw new IllegalArgumentException();
		}
		this.loopCount = loopCount;
	}

	public void start() throws IllegalStateException, MediaException {
		if (state == CLOSED) {
			throw new IllegalStateException();
		}
		boolean midi = sequence instanceof Sequence;
		if (state == UNREALIZED) {
			realize();
		}
		if (state < PREFETCHED && midi) {
			prefetch();
		}
		if (state != STARTED) {
			if (sequence != null) {
				if (midi) {
					if (Settings.oneMidiAtTime && EmulatorMIDI.currentPlayer != null) {
						if (midiPlaying && EmulatorMIDI.currentPlayer != this) {
							try {
								EmulatorMIDI.currentPlayer.stop();
							} catch (Exception ignored) {}
//                        throw new MediaException("MIDI is currently playing");
						}
						EmulatorMIDI.close(false);
					}
					if (midiSequencer != null) {
						midiSequencer.close();
						midiSequencer = null;
					}
				}
				if (complete) {
					setMediaTime(0);
					complete = false;
				}
				if (cacheRef != null) {
					cacheRef.setPlayer(this);
				}
				stopped = false;
				(playerThread = new Thread(this, "PlayerImpl-" + (++count))).start();
			} else {
				notifyListeners(PlayerListener.STARTED, 0);
				notifyListeners(PlayerListener.END_OF_MEDIA, 0);
				return;
			}
			setLevel(level);
			state = STARTED;
		}
	}

	public void stop() throws IllegalStateException, MediaException {
		if (state == CLOSED) {
			throw new IllegalStateException();
		}
		if (sequence == null) return;
		if (sequence instanceof emulator.javazoom.jl.player.Player) {
			((emulator.javazoom.jl.player.Player) sequence).stop();
			return;
		}
		stopped = true;
		synchronized (playLock) {
			playLock.notifyAll();
		}
		if (playerThread != null) {
			final Thread t = playerThread;
			playerThread = null;
			if (t.isAlive()) {
				try {
					synchronized (t) {
						t.wait(1000);
					}
				} catch (Exception ignored) {}
			}
		}
	}

	public Control getControl(final String s) {
		if (s.contains("VolumeControl")) {
			return volumeControl;
		}
		if (s.contains("ToneControl")) {
			return toneControl;
		}
		if (s.contains("MIDIControl")) {
			return midiControl;
		}
		return null;
	}

	public Control[] getControls() {
		return controls;
	}

	public void run() {
		try {
			if (sequence == null) {
				playerThread = null;
				state = PREFETCHED;
				return;
			}
			players.add(this);
			boolean globalMidi = Settings.oneMidiAtTime;
			int loopCount = this.loopCount;
			boolean complete = false;
			boolean b = true;
			while (playerThread != null && loopCount != 0) {
				complete = false;
				if (sequence instanceof Sequence) {
					Sequencer sequencer = null;
					if (globalMidi) {
						EmulatorMIDI.start(this, (Sequence) sequence, mediaTime);
						EmulatorMIDI.currentPlayer = this;
					} else {
						initMidiSequencer();
						sequencer = midiSequencer;
						sequencer.setSequence((Sequence) sequence);
						if (midiSequencer == null || mediaTime >= getDuration()) {
							sequencer.setMicrosecondPosition(mediaTime);
						}
						sequencer.start();
					}
					if (b) {
						notifyListeners(PlayerListener.STARTED, mediaTime = globalMidi ? EmulatorMIDI.getMicrosecondPosition() : midiSequencer.getMicrosecondPosition());
						b = false;
					}
					midiPlaying = true;
					if (!stopped)
						synchronized (playLock) {
							playLock.wait();
						}
					stopped = false;
					complete = this.complete;
					midiPlaying = false;
					if (globalMidi) {
						mediaTime = EmulatorMIDI.getMicrosecondPosition();
						EmulatorMIDI.stop();
					} else {
						mediaTime = sequencer.getMicrosecondPosition();
						sequencer.stop();
					}
				} else if (sequence instanceof Clip) {
					Clip clip = (Clip) sequence;
					synchronized (clip) {
						clip.start();
					}
					if (b) {
						notifyListeners(PlayerListener.STARTED, getMediaTime());
						b = false;
					}
					synchronized (playLock) {
						playLock.wait();
					}
					complete = this.complete;
					synchronized (clip) {
						clip.stop();
					}
				} else if (sequence instanceof emulator.javazoom.jl.player.Player) {
					if (b) {
						notifyListeners(PlayerListener.STARTED, getMediaTime());
						b = false;
					}
					try {
						complete = ((emulator.javazoom.jl.player.Player) sequence).play(Integer.MAX_VALUE);
					} catch (JavaLayerException e) {
						e.printStackTrace();
						notifyListeners(PlayerListener.ERROR, e.toString());
						complete = true;
						loopCount = 0;
					}
					if (complete || (sequence != null && ((emulator.javazoom.jl.player.Player) sequence).isComplete())) {
						complete = true;
						if (dataSource != null) {
							//dataSource.stop();
							dataSource.disconnect();
						}
					}
				}
				if (!complete) break;
				if (loopCount != -1) {
					if (loopCount <= 0) continue;
					--loopCount;
				}
				if (loopCount != 0) {
					try {
						setMediaTime(0);
					} catch (MediaException ignored) {}
				}
			}
			playerThread = null;
			state = PREFETCHED;
			notifyListeners(complete ? PlayerListener.END_OF_MEDIA : PlayerListener.STOPPED, getMediaTime());
		} catch (Exception e) {
			System.err.println("Exception in player thread!");
			e.printStackTrace();
		}
		if (!Settings.enableMediaDump) players.remove(this);
	}

	public void update(final LineEvent lineEvent) {
		if (lineEvent.getType() == LineEvent.Type.STOP) {
			notifyCompleted();
		}
	}

	public void setLevel(int n) {
		if (n > 100) n = 100;
		if (n < 0) n = 0;
		if (level != n) notifyListeners(PlayerListener.VOLUME_CHANGED, n);
		level = n;
		if (sequence == null) return;
		final double n2 = n / 100.0;
		if (sequence instanceof Clip) {
			synchronized (sequence) {
				try {
					((FloatControl) ((Clip) sequence).getControl(FloatControl.Type.MASTER_GAIN))
							.setValue((float) (Math.log((n2 == 0.0) ? 1.0E-4 : n2) / Math.log(10.0) * 20.0));
				} catch (Exception ignored) {}
			}
			return;
		}
		if (sequence instanceof Sequence) {
			// TODO midi volume
			return;
		}
		if (sequence instanceof emulator.javazoom.jl.player.Player) {
			((emulator.javazoom.jl.player.Player) sequence).setLevel(n);
		}
	}

	public TimeBase getTimeBase() {
		if (state == UNREALIZED || state == CLOSED) {
			throw new IllegalStateException();
		}
		return timeBase;
	}

	public void setTimeBase(final TimeBase timeBase) throws MediaException {
		if (state == UNREALIZED || state == STARTED || state == CLOSED) {
			throw new IllegalStateException();
		}
		throw new MediaException("TimeBase can't be set on this player.");
	}

	public byte[] getData() {
		if (data == null) return null;
		return data.clone();
	}

	public String getExportName() {
		String ext = "";
		if (sequence instanceof Sequence) {
			ext = "mid";
		} else /*if (sequence instanceof Clip) {
			ext = "wav";
		} else */if (sequence instanceof emulator.javazoom.jl.player.Player) {
			ext = "mp3";
		} else if (contentType != null) {
			if (contentType.equalsIgnoreCase("audio/wav") ||
					contentType.equalsIgnoreCase("audio/wave") ||
					contentType.equalsIgnoreCase("audio/x-wav")) {
				ext = "wav";
			} else if (contentType.equalsIgnoreCase("audio/amr")) {
				ext = "amr";
			} else if (contentType.equalsIgnoreCase("audio/x-mid") ||
					contentType.equalsIgnoreCase("audio/mid") ||
					contentType.equalsIgnoreCase("audio/midi")) {
				ext = "mid";
			} else if (contentType.equalsIgnoreCase("audio/mpeg") ||
					contentType.equalsIgnoreCase("audio/mp3")) {
				ext = "mp3";
			}
		}
		if (ext.isEmpty()) return "audio" + hashCode();
		return hashCode() + "." + ext;
	}

	public void notifyCompleted() {
		complete = true;
		synchronized (playLock) {
			playLock.notifyAll();
		}
	}

	private void initMidiSequencer() throws MidiUnavailableException {
		if (midiSequencer != null) return;
		midiSequencer = MidiSystem.getSequencer(false);
		if (EmulatorMIDI.useExternalReceiver()) {
			EmulatorMIDI.setupSequencer(midiSequencer);
		} else {
			midiSynthesizer = MidiSystem.getSynthesizer();
			midiSynthesizer.open();
			midiSequencer.getTransmitter().setReceiver(midiSynthesizer.getReceiver());
		}
		midiSequencer.open();
		midiSequencer.addMetaEventListener(this);
	}

	public void meta(MetaMessage meta) {
		if (meta.getType() == 0x2F) {
			notifyCompleted();
		}
	}

	private static long crcUtil(byte[] b) {
		CRC32 crc = new CRC32();
		crc.update(b);
		return crc.getValue();
	}

	public String getReadableImplementationType() {
		if(sequence == null)
			return "Empty player";
		if(sequence instanceof Sequence)
			return "JVM MIDI";
		if(sequence instanceof Clip)
			return "JVM clip";
		if(sequence instanceof emulator.javazoom.jl.player.Player)
			return "javazoom";
		return "MMAPI/unknown";
	}

	static class WavCache implements LineListener {
		int length;
		long crc;
		Set<Player> references;
		PlayerImpl currentPlayer;
		Clip clip;

		WavCache(byte[] b) {
			length = b.length;
			crc = crcUtil(b);
		}

		void setPlayer(PlayerImpl player) {
			if (references == null)
				references = Collections.newSetFromMap(new WeakHashMap());
			try {
				currentPlayer.stop();
			} catch (Exception ignored) {}
			currentPlayer = player;
			references.add(player);
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof WavCache) {
				return length == ((WavCache) other).length && crc == ((WavCache) other).crc;
			}
			if (other instanceof byte[]) {
				return ((byte[]) other).length == length && crcUtil((byte[]) other) == crc;
			}
			return false;
		}

		public void update(LineEvent event) {
			if (event.getType() == LineEvent.Type.STOP && currentPlayer != null) {
				currentPlayer.notifyCompleted();
			}
		}
	}
}
