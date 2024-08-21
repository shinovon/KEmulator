package javax.microedition.media;

import emulator.*;
import emulator.custom.CustomJarResources;
import emulator.media.EmulatorMIDI;

import java.io.*;
import java.net.URL;

import javax.microedition.media.control.*;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

import java.util.*;
import javax.sound.sampled.*;
import javax.sound.midi.*;

import emulator.media.tone.ToneControlImpl;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;

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
	private long midiPosition;
	private final Object playLock = new Object();
	private Sequencer midiSequencer;
	private boolean stopped;

	public PlayerImpl() {
		loopCount = 1;
		state = UNREALIZED;
		listeners = new Vector<PlayerListener>();
		timeBase = Manager.getSystemTimeBase();
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

	private void init(final InputStream inputStream, String contentType) throws IOException {
		if (contentType == null)
			contentType = "";
		this.contentType = (contentType = contentType.toLowerCase());
		if (dataLen == 0) dataLen = inputStream.available();
		if (contentType.equals("audio/amr")) {
			amr(inputStream);
		} else if (contentType.equals("audio/x-wav") || contentType.equals("audio/wav")) {
			wav(inputStream);
		} else if (contentType.equals("audio/x-midi") || contentType.equals("audio/midi")) {
			midi(inputStream);
		} else if (contentType.equals("audio/mpeg") || contentType.equals("audio/mp3")) {
			try {
				InputStream i = inputStream;
				if (i instanceof ByteArrayInputStream && Settings.enableMediaDump) {
					data = CustomJarResources.getBytes(i);
					i = new ByteArrayInputStream(data);
				}
				sequence = new javazoom.jl.player.Player(i, false);
			} catch (JavaLayerException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
			volumeControl = new VolumeControlImpl(this);
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
	}

	public PlayerImpl(String locator, String contentType, DataSource src) {
		this();
		this.contentType = contentType;
		this.dataSource = src;
	}

	public PlayerImpl(String src, String contentType) throws IOException {
		this();
		if ("audio/wav".equals(contentType)) {
			this.contentType = contentType.toLowerCase();
			dataLen = (int) new File(src).length();
			wav(new URL("file:///" + src));
			setLevel(level);
		} else {
			dataLen = (int) new File(src).length();
			init(new FileInputStream(src), contentType);
		}
	}

	private void amr(final InputStream inputStream) throws IOException {
		try {
			final byte[] b;
			if ((b = emulator.media.amr.a.method476(CustomJarResources.getBytes(inputStream))) == null) {
				throw new MediaException("Cannot parse AMR data");
			}
			if (Settings.enableMediaDump) data = b;
			InputStream i = new ByteArrayInputStream(b);
			final AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000.0f, 16, 1, 2, 8000.0f, false);
			final AudioInputStream audioInputStream = new AudioInputStream(i, audioFormat, -1L);
			final Clip clip;
			(clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, audioFormat)))
					.addLineListener(this);
			clip.open(audioInputStream);
			sequence = clip;
		} catch (Exception e) {
			sequence = null;
			throw new IOException("AMR realize error!", e);
		}
		toneControl = new ToneControlImpl();
		volumeControl = new VolumeControlImpl(this);
		controls = new Control[]{toneControl, volumeControl};
	}

	private void wav(final InputStream inputStream) throws IOException {
		try {
			sequence = AudioSystem.getAudioInputStream(inputStream);
		} catch (Exception e) {
			System.out.println("WAV realize error: " + e);
			sequence = null;
			//throw new IOException("WAV realize error!", e);
			return;
		}
		try {
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
					.addLineListener(this);
			clip.open(audioInputStream);
			sequence = clip;
		} catch (Exception e) {
			sequence = null;
			throw new IOException();
		}
		toneControl = new ToneControlImpl();
		volumeControl = new VolumeControlImpl(this);
		controls = new Control[]{toneControl, volumeControl};
	}

	private void wav(final URL url) throws IOException {
		try {
			sequence = AudioSystem.getAudioInputStream(url);
		} catch (Exception e) {
			System.out.println("WAV realize error: " + e);
			sequence = null;
			//throw new IOException("WAV realize error!", e);
			return;
		}
		try {
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
			final Clip anObject298;
			(anObject298 = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, audioInputStream
					.getFormat(), (int) audioInputStream.getFrameLength() * format.getFrameSize())))
					.addLineListener(this);
			anObject298.open(audioInputStream);
			sequence = anObject298;
		} catch (Exception e) {
			sequence = null;
			throw new IOException();
		}
		toneControl = new ToneControlImpl();
		volumeControl = new VolumeControlImpl(this);
		controls = new Control[]{toneControl, volumeControl};
	}

	private void midi(final InputStream inputStream) throws IOException {
		try {
			byte[] data = CustomJarResources.getBytes(inputStream);
			if (Settings.enableMediaDump) this.data = data;
			sequence = MidiSystem.getSequence(new ByteArrayInputStream(data));
		} catch (Exception e) {
			sequence = null;
		}
		midiControl = new MIDIControlImpl(this);
		toneControl = new ToneControlImpl();
		volumeControl = new VolumeControlImpl(this);
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
		if (sequence instanceof javazoom.jl.player.Player) {
			((javazoom.jl.player.Player) sequence).close();
		} else if (sequence instanceof Sequence) {
			if (midiSequencer != null) {
				midiSequencer.close();
				midiSequencer = null;
			}
			if (Settings.oneMidiAtTime) {
				EmulatorMIDI.close();
			}
		} else if (sequence instanceof Clip) {
//			try {
//				((Clip) sequence).close();
//			} catch (Exception ignored) {}
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
			return;
		}
		if (sequence instanceof Clip) {
			try {
				((Clip) sequence).close();
			} catch (Exception ignored) {}
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
			res = ((Sequence) sequence).getMicrosecondLength() / 1000000D;
		} else if (sequence instanceof Clip) {
			final Clip clip;
			res = (clip = (Clip) sequence).getBufferSize()
					/ (clip.getFormat().getFrameSize() * clip.getFormat().getFrameRate());
		} else if (sequence instanceof javazoom.jl.player.Player) {
			try {
				res = ((double) (dataLen * 8) / ((javazoom.jl.player.Player) sequence).getBitrate());
			} catch (ArithmeticException e) {
				return -1;
			}
		} else {
			return 0;
		}
		return (long) (res * 1000000D);
	}

	public long getMediaTime() {
		if (sequence == null) return 0;
		if (sequence instanceof Clip) {
			return ((Clip) sequence).getMicrosecondPosition();
		}
		if (sequence instanceof Sequence) {
			try {
				if (midiSequencer != null)
					midiPosition = midiSequencer.getMicrosecondPosition();
				else if (Settings.oneMidiAtTime && EmulatorMIDI.currentPlayer == this && midiPlaying)
					midiPosition = EmulatorMIDI.getMicrosecondPosition();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return midiPosition;
		}
		if (sequence instanceof javazoom.jl.player.Player) {
			return ((javazoom.jl.player.Player) sequence).getPosition() * 1000L;
		}
		return TIME_UNKNOWN;
	}

	public long setMediaTime(final long t) throws MediaException {
		if (sequence == null) return 0;
		long ms = 0L;
		if (sequence instanceof Clip) {
			ms = ((Clip) sequence).getMicrosecondPosition();
			if (t < ms) {
				ms = t;
			}
			((Clip) sequence).setMicrosecondPosition(t);
		} else if (sequence instanceof Sequence) {
			ms = ((Sequence) sequence).getMicrosecondLength();
			if (t < ms) {
				ms = t;
			}
			midiPosition = ms;
			try {
				if (midiSequencer != null)
					midiSequencer.setMicrosecondPosition(ms);
				else if (Settings.oneMidiAtTime && EmulatorMIDI.currentPlayer == this)
					EmulatorMIDI.setMicrosecondPosition(ms);
			} catch (Exception e) {
				throw new MediaException(e);
			}
		} else if (sequence instanceof javazoom.jl.player.Player) {
			long l = getMediaTime();
//			if (true) return l; // TODO
			if (t == 0 && l == 0 || !((javazoom.jl.player.Player) sequence).isBuffered)
				return 0;
			if (t < l) {
				try {
					stop();
					Header old = ((javazoom.jl.player.Player) sequence).bitstream().header;
					((javazoom.jl.player.Player) sequence).reset();
					((javazoom.jl.player.Player) sequence).skip((int) (t / 1000L), old);
					ms = ((javazoom.jl.player.Player) sequence).getPosition() * 1000L;
					start();
				} catch (JavaLayerException e) {
					throw new MediaException(e);
				}
			} else {
				try {
					stop();
					Header old = ((javazoom.jl.player.Player) sequence).bitstream().header;
					((javazoom.jl.player.Player) sequence).reset();
					((javazoom.jl.player.Player) sequence).skip((int) (t / 1000L), old);
					ms = ((javazoom.jl.player.Player) sequence).getPosition() * 1000L;
					start();
				} catch (JavaLayerException e) {
					throw new MediaException(e);
				}
			}
		}
		return ms;
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
			try {
				if (sequence != null) {
					if (Settings.oneMidiAtTime) {
						EmulatorMIDI.setSequence((Sequence) sequence);
					} else {
						initMidiSequencer();
						midiSequencer.setSequence((Sequence) sequence);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				sequence = null;
				throw new MediaException(e);
			}
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
					sequence = new javazoom.jl.player.Player(new InputStream() {

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
				volumeControl = new VolumeControlImpl(this);
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
						EmulatorMIDI.close();
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
				stopped = false;
				(playerThread = new Thread(this, "PlayerImpl-" + (++count))).start();
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
		if (sequence instanceof javazoom.jl.player.Player) {
			((javazoom.jl.player.Player) sequence).stop();
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
						t.wait();
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
						EmulatorMIDI.start(this, (Sequence) sequence, midiPosition);
						EmulatorMIDI.currentPlayer = this;
					} else {
						initMidiSequencer();
						sequencer = midiSequencer;
						sequencer.setSequence((Sequence) sequence);
						sequencer.setMicrosecondPosition(midiPosition);
						sequencer.start();
					}
					if (b) {
						notifyListeners(PlayerListener.STARTED, midiPosition = globalMidi ? EmulatorMIDI.getMicrosecondPosition() : midiSequencer.getMicrosecondPosition());
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
						midiPosition = EmulatorMIDI.getMicrosecondPosition();
						EmulatorMIDI.stop();
					} else {
						midiPosition = sequencer.getMicrosecondPosition();
						sequencer.stop();
					}
				} else if (sequence instanceof Clip) {
					Clip clip = (Clip) sequence;
					clip.start();
					if (b) {
						notifyListeners(PlayerListener.STARTED, getMediaTime());
						b = false;
					}
					synchronized (playLock) {
						playLock.wait();
					}
					complete = this.complete;
					clip.stop();
				} else if (sequence instanceof javazoom.jl.player.Player) {
					if (b) {
						notifyListeners(PlayerListener.STARTED, getMediaTime());
						b = false;
					}
					try {
						complete = ((javazoom.jl.player.Player) sequence).play(Integer.MAX_VALUE);
					} catch (JavaLayerException e) {
						e.printStackTrace();
						notifyListeners(PlayerListener.ERROR, e.toString());
					}
					if (complete || (sequence != null && ((javazoom.jl.player.Player) sequence).isComplete())) {
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
		if (level != n) notifyListeners(PlayerListener.VOLUME_CHANGED, n);
		level = n;
		if (sequence == null) return;
		final double n2 = n / 100.0;
		if (sequence instanceof Clip) {
			try {
				((FloatControl) ((Clip) sequence).getControl(FloatControl.Type.MASTER_GAIN))
						.setValue((float) (Math.log((n2 == 0.0) ? 1.0E-4 : n2) / Math.log(10.0) * 20.0));
			} catch (Exception ignored) {
			}
			return;
		}
		if (sequence instanceof Sequence) {
			// TODO midi volume
			return;
		}
		if (sequence instanceof javazoom.jl.player.Player) {
			((javazoom.jl.player.Player) sequence).setLevel(n);
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
		} else */if (sequence instanceof javazoom.jl.player.Player) {
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
		midiSequencer = MidiSystem.getSequencer();
		EmulatorMIDI.setupSequencer(midiSequencer);
		midiSequencer.addMetaEventListener(this);
		midiSequencer.open();
	}

	public void meta(MetaMessage meta) {
		if (meta.getType() == 0x2F) {
			notifyCompleted();
		}
	}
}
