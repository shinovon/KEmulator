package javax.microedition.media;

import emulator.*;
import emulator.custom.*;
import emulator.custom.CustomJarResources;
import emulator.media.amr.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;

import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.media.control.*;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

import java.util.*;
import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileReader;
import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class PlayerImpl implements javax.microedition.media.Player, Runnable, LineListener, MetaEventListener {

	public static List<javax.microedition.media.Player> players = new ArrayList<javax.microedition.media.Player>();
	Object sequence;
	Thread playerThread;
	boolean midiCompleted;
	boolean soundCompleted;
	private int state;
	private String contentType;
	private Vector listeners;
	Control midiControl;
	Control toneControl;
	Control volumeControl;
	Control[] controls;
	TimeBase timeBase;
	public int loopCount;
	public int dataLen;
	private boolean mp3Complete;
	private DataSource dataSource;
	private FramePositioningControl frameControl;
	private boolean dataSourceDisconnected;
	private InputStream inputStream;
	static Class clipCls;
	private static int count;


	public PlayerImpl(String contentType, DataSource src) throws IOException, MediaException {
		super();
		this.contentType = contentType;
		this.dataSource = src;
		players.add(this);
	}

	public PlayerImpl(final InputStream inputStream, String contentType) throws IOException {
		super();
		if (contentType == null)
			contentType = "";
		a(inputStream, contentType);
		players.add(this);
	}
	
	private void a(final InputStream inputStream, String contentType) throws IOException {
		contentType = contentType.toLowerCase();
		this.contentType = contentType;
		this.loopCount = 1;
		if(dataLen == 0) this.dataLen = inputStream.available();
		this.inputStream = inputStream;
		if (contentType.equals("audio/amr")) {
			this.amr(inputStream);
		} else if (contentType.equals("audio/x-wav") || contentType.equals("audio/wav")) {
			this.wav(inputStream);
		} else if (contentType.equals("audio/x-midi") || contentType.equals("audio/midi")) {
			this.midi(inputStream);
		} else if (contentType.equals("audio/mpeg")) {
			try {
				this.sequence = new Player(inputStream);
			} catch (JavaLayerException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
			this.volumeControl = new VolumeControlImpl(this);
			this.controls = new Control[] { this.volumeControl };
			frameControl = new FramePositioningControl() {

				public long mapFrameToTime(int p0) {
					if (((Player) sequencer).framesPerSecond() == 0)
						return -1;
					return ((int) (((Player) sequencer).framesPerSecond() * p0)) * 1000L;
				}

				public int mapTimeToFrame(long p0) {
					if (((Player) sequencer).framesPerSecond() == 0)
						return -1;
					return (int)(((float)p0 / 1000.0F) * ((Player) sequencer).framesPerSecond());
				}

				public int seek(int p0) {
					return 0;
				}

				public int skip(int i) {
					if (i < 0) {
						return 0;
					}
					if (i == 0)
						return 0;
					// TODO
					return 0;
				}

			};
		} else {
			try {
				this.midi(inputStream);
			} catch (Exception ex) {
				try {
					this.wav(inputStream);
				} catch (Exception ex2) {
					try {
						this.amr(inputStream);
					} catch (Exception ex3) {
						Emulator.getEmulator().getLogStream().println("*** unsupported sound format ***");
						this.sequence = null;
					}
				}
			}
		}
		this.setLevel(this.state = 100);
		this.listeners = new Vector();
		this.timeBase = Manager.getSystemTimeBase();
	}

	public PlayerImpl(String locator, String contentType, DataSource src) {
		players.add(this);
	}

	public PlayerImpl(String src, String contentType) throws IOException {
		if (contentType == null)
			contentType = "";
		if (contentType.equals("audio/wav")) {
			contentType = contentType.toLowerCase();
			this.contentType = contentType;
			this.loopCount = 1;
			this.dataLen = (int) new File(src).length();
			wav(new URL("file:///" + src));
			this.setLevel(this.state = 100);
			this.listeners = new Vector();
			this.timeBase = Manager.getSystemTimeBase();
		} else {
			this.dataLen = (int) new File(src).length();
			a(new FileInputStream(src), contentType);
		}
		players.add(this);
	}

	private void amr(final InputStream inputStream) throws IOException {
		try {
			final byte[] method476;
			if ((method476 = emulator.media.amr.a.method476(CustomJarResources.getBytes(inputStream))) == null) {
				throw new MediaException("Cannot parse AMR data");
			}
			final AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000.0f, 16, 1, 2, 8000.0f, false);
			final AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(method476), audioFormat, -1L);
			final Clip anObject298;
			(anObject298 = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, audioFormat)))
					.addLineListener(this);
			anObject298.open(audioInputStream);
			this.sequence = anObject298;
		} catch (Exception ex) {
			this.sequence = null;
			throw new IOException("AMR realize error!", ex);
		}
		this.toneControl = new ToneControlImpl();
		this.volumeControl = new VolumeControlImpl(this);
		this.controls = new Control[] { this.toneControl, this.volumeControl };
	}
	
	private void wav(final InputStream inputStream) throws IOException {
		try {
			this.sequence = AudioSystem.getAudioInputStream(inputStream);
		} catch (Exception ex) {
			System.out.println(ex.toString());
			//ex.printStackTrace();
			this.sequence = null;
			//throw new IOException("WAV realize error!", ex);
			return;
		}
		try {
			AudioInputStream audioInputStream;
			AudioFormat format;
			if ((format = (audioInputStream = (AudioInputStream) this.sequence).getFormat())
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
			this.sequence = anObject298;
		} catch (Exception ex2) {
			this.sequence = null;
			throw new IOException();
		}
		this.toneControl = new ToneControlImpl();
		this.volumeControl = new VolumeControlImpl(this);
		this.controls = new Control[] { this.toneControl, this.volumeControl };
	}
	
	private void wav(final URL url) throws IOException {
		try {
			this.sequence = AudioSystem.getAudioInputStream(url);
		} catch (Exception ex) {
			ex.printStackTrace();
			this.sequence = null;
			//throw new IOException("WAV realize error!", ex);
			return;
		}
		try {
			AudioInputStream audioInputStream;
			AudioFormat format;
			if ((format = (audioInputStream = (AudioInputStream) this.sequence).getFormat())
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
			this.sequence = anObject298;
		} catch (Exception ex2) {
			this.sequence = null;
			throw new IOException();
		}
		this.toneControl = new ToneControlImpl();
		this.volumeControl = new VolumeControlImpl(this);
		this.controls = new Control[] { this.toneControl, this.volumeControl };
	}

	private static MidiDevice device;
	Sequencer sequencer;
	static Synthesizer synthesizer;
	
	private void midi(final InputStream inputStream) throws IOException {
		try {
			this.sequence = MidiSystem.getSequence(inputStream);
		} catch (Exception ex) {
			this.sequence = null;
		}
		try {
			/*
			if(device == null || sequencer == null || synthesizer == null) {
				device = MidiSystem.getMidiDevice(Emulator.getMidiDeviceInfo());
				device.open();
				sequencer = MidiSystem.getSequencer();
				for (Transmitter t : sequencer.getTransmitters()) {
					t.setReceiver(device.getReceiver());
				}
				if(sequencer instanceof Synthesizer) {
					synthesizer = (Synthesizer) sequencer;
				} else {
					synthesizer = MidiSystem.getSynthesizer();
					synthesizer.open();
					for (Transmitter t : synthesizer.getTransmitters()) {
						t.setReceiver(device.getReceiver());
					}
				}
			}
			*/
			
			/*
			this.sequencer = MidiSystem.getSequencer();
			
			(this.synthesizer = MidiSystem.getSynthesizer()).open();
			
			
			
			if (this.synthesizer.getDefaultSoundbank() == null) {
			    this.sequencer.getTransmitter().setReceiver(MidiSystem.getReceiver());
			}
			else {
			    this.sequencer.getTransmitter().setReceiver(this.synthesizer.getReceiver());
			}
			
			this.sequencer.getTransmitter().setReceiver(MidiSystem.getReceiver());
			if(MidiSystem.getReceiver() instanceof Sequencer) {
				this.sequencer = (Sequencer) MidiSystem.getReceiver();
			}
			if(this.sequencer instanceof Synthesizer) {
				this.synthesizer = (Synthesizer) this.sequencer;
			}
			else if(MidiSystem.getReceiver() instanceof Synthesizer) {
				this.synthesizer = (Synthesizer) MidiSystem.getReceiver();
			} else {
				this.synthesizer = MidiSystem.getSynthesizer();
				this.synthesizer.open();
				this.sequencer.getTransmitter().setReceiver(this.synthesizer.getReceiver());
			//	this.synthesizer.getTransmitter().setReceiver(MidiSystem.getReceiver());
			}
			*/
			
			if(synthesizer == null) {
				device = MidiSystem.getMidiDevice(Emulator.getMidiDeviceInfo());
				device.open();
				synthesizer = MidiSystem.getSynthesizer();
				for (Transmitter t : synthesizer.getTransmitters()) {
					t.setReceiver(device.getReceiver());
				}
				synthesizer.open();
			}
			sequencer = MidiSystem.getSequencer();
			for (Transmitter t : sequencer.getTransmitters()) {
				t.setReceiver(device.getReceiver());
			}
		} catch (Exception ex2) {
			ex2.printStackTrace();
			this.sequence = null;
			throw new IOException(ex2.toString());
		}
		this.sequencer.addMetaEventListener(this);
		try {
			this.sequencer.open();
			if (this.sequence != null) {
				this.sequencer.setSequence((Sequence) this.sequence);
			}
		} catch (Exception ex3) {
			ex3.printStackTrace();
			this.sequence = null;
			throw new IOException(ex3.toString());
		}
		this.midiControl = new MIDIControlImpl(this);
		this.toneControl = new ToneControlImpl();
		this.volumeControl = new VolumeControlImpl(this);
		this.controls = new Control[] { this.toneControl, this.volumeControl, this.midiControl };
	}

	public void addPlayerListener(final PlayerListener playerListener) throws IllegalStateException {
		if (this.state == 0) {
			throw new IllegalStateException();
		}
		if (playerListener != null) {
			this.listeners.add(playerListener);
		}
	}

	public void removePlayerListener(final PlayerListener playerListener) throws IllegalStateException {
		if (this.state == 0) {
			throw new IllegalStateException();
		}
		if (playerListener != null) {
			this.listeners.remove(playerListener);
		}
	}

	protected void notifyListeners(final String s, final Object o) {
		if (listeners == null)
			return;
		final Enumeration<PlayerListener> elements = this.listeners.elements();
		while (elements.hasMoreElements()) {
			elements.nextElement().playerUpdate(this, s.intern(), o);
		}
	}

	public void close() {
		players.remove(this);
		if (this.playerThread != null) {
			try {
				this.stop();
			} catch (Exception ex) {
			}
		}
		if (this.sequence instanceof Sequence) {
			this.sequencer.close();
		} else if (this.sequence instanceof Player) {
			((Player) this.sequence).close();
		}
		if(dataSource != null && !dataSourceDisconnected) {
			dataSource.disconnect();
			dataSourceDisconnected = true;
		}
		this.sequence = null;
		this.state = 0;
		this.notifyListeners("closed", null);
	}

	public void deallocate() throws IllegalStateException {
		if (this.state == 0) {
			throw new IllegalStateException();
		}
		if (this.state == 400) {
			try {
				this.stop();
				return;
			} catch (MediaException ex) {
				return;
			}
		}
		PlayerImpl playerImpl;
		int anInt303;
		if (this.state == 300) {
			playerImpl = this;
			anInt303 = 200;
		} else {
			if (this.state != 200) {
				return;
			}
			playerImpl = this;
			anInt303 = 100;
		}
		playerImpl.state = anInt303;
		if (dataSource != null && dataSource.getStreams() != null && dataSource.getStreams()[0] != null && !dataSourceDisconnected) {
			if (dataSource.getStreams()[0].getSeekType() == 0) {
				dataSource.disconnect();
				dataSourceDisconnected = true;
			}
		}
	}

	public String getContentType() {
		return this.contentType;
	}

	public long getDuration() {
		double n = 0.0;
		double n2;
		if(sequence == null) return 0;
		if (this.sequence instanceof Sequence) {
			n2 = ((Sequence) this.sequence).getMicrosecondLength() / 1000000.0;
		} else if (this.sequence instanceof Clip) {
			final Clip clip;
			n2 = (clip = (Clip) this.sequence).getBufferSize()
					/ (clip.getFormat().getFrameSize() * clip.getFormat().getFrameRate());
		} else if (this.sequence instanceof Player) {
			try {
				n2 = ((dataLen * 8) / ((Player) sequence).getBitrate());
			} catch (ArithmeticException e) {
				return -1;
			}
		} else {
			return 0;
		}
		n = n2;
		return (long) (n * 1000000.0);
	}

	public long getMediaTime() {
		if(sequence == null) return 0;
		if (this.sequence instanceof Clip) {
			return ((Clip) this.sequence).getMicrosecondPosition();
		}
		if (this.sequence instanceof Sequence) {
			return this.sequencer.getMicrosecondPosition();
		}
		if (this.sequence instanceof Player) {
			return ((Player) this.sequence).getPosition() * 1000;
		}
		return -1L;
	}

	public long setMediaTime(final long t) throws MediaException {
		if(sequence == null) return 0;
		long microsecondPosition2 = 0L;
		if (this.sequence instanceof Clip) {
			microsecondPosition2 = ((Clip) this.sequence).getMicrosecondPosition();
			if (t < microsecondPosition2) {
				microsecondPosition2 = t;
			}
			((Clip) this.sequence).setMicrosecondPosition(t);
		} else if (this.sequence instanceof Sequence) {
			microsecondPosition2 = ((Sequence) this.sequence).getMicrosecondLength();
			if (t < microsecondPosition2) {
				microsecondPosition2 = t;
			}
			this.sequencer.setMicrosecondPosition(microsecondPosition2);
		} else if (this.sequence instanceof Player) {
			long l = getMediaTime();
			if (t == 0 && l == t)
				return 0;
			if (t < l) {
				try {
					Header old = ((Player) sequence).bitstream().header;
					((Player) sequence).reset();
					((Player) sequence).skip((int) (t / 1000L), old);
					microsecondPosition2 = ((Player) sequence).getPosition();
				} catch (JavaLayerException e) {
					throw new MediaException(e);
				}
			} else {
				try {
					Header old = ((Player) sequence).bitstream().header;
					((Player) sequence).reset();
					((Player) sequence).skip((int) (t / 1000L), old);
					microsecondPosition2 = ((Player) sequence).getPosition();
				} catch (JavaLayerException e) {
					throw new MediaException(e);
				}
			}
		}
		return microsecondPosition2;
	}

	public int getState() {
		return this.state;
	}

	public void prefetch() throws IllegalStateException, MediaException {
		if (this.state == 0) {
			throw new IllegalStateException();
		}
		if (this.state == 100) {
			this.realize();
		} else if (this.state != 200) {
			return;
		}
		if(this.dataSource != null) {
			try {
				this.dataSource.connect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new MediaException(e);
			}
		}
		this.state = 300;
	}

	public void realize() throws IllegalStateException, MediaException {
		if (this.state == 0) {
			throw new IllegalStateException();
		}
		if (this.state == 100) {
			this.state = 200;
		}
		if (dataSource != null) {
			try {
				dataSource.connect();
				//dataSource.start();
			} catch (IOException e) {
				throw new MediaException(e.toString());
			}
			boolean mp3 = false;

			SourceStream stream = dataSource.getStreams()[0];
			String streamContentType = stream.getContentDescriptor().getContentType();
			if (streamContentType != null) {
				if (streamContentType.equalsIgnoreCase("audio/mpeg") || this.contentType.equalsIgnoreCase("audio/mp3")) {
					mp3 = true;
					if (this.contentType == null)
						this.contentType = "audio/mpeg";
				}
			}
			if (mp3 || this.contentType.equalsIgnoreCase("audio/mpeg") || this.contentType.equalsIgnoreCase("audio/mp3")) {
				dataLen = (int) stream.getContentLength();
				try {
					this.sequence = new Player(new InputStream() {

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

					});
				} catch (JavaLayerException e) {
					throw new MediaException(e);
				}
				this.volumeControl = new VolumeControlImpl(this);
				this.controls = new Control[] { this.volumeControl };
				this.setLevel(this.state = 100);
				this.listeners = new Vector();
				this.timeBase = Manager.getSystemTimeBase();
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
		if (this.state == 0) {
			throw new IllegalStateException();
		}
		if (this.state != 400) {
			if(sequence != null) {
				if ((this.sequence instanceof Sequence && midiCompleted)
				|| (this.sequence instanceof Player && mp3Complete)
				|| (this.sequence instanceof Clip && soundCompleted)) {
					setMediaTime(0);
				}
				(this.playerThread = new Thread(this, "PlayerImpl-" + (++count))).start();
			}
			this.state = 400;
			this.notifyListeners("started", new Long(0L));
			if(sequence == null) {
				this.state = 300;
				this.notifyListeners("stopped", new Long(0L));
			}
		}
	}

	public void stop() throws IllegalStateException, MediaException {
		if (this.state == 0) {
			throw new IllegalStateException();
		}
		if(sequence == null) return;
		if (this.sequence instanceof Player) {
			((Player) this.sequence).stop();
			return;
		}
		if (this.playerThread != null) {
			final Thread aThread297 = this.playerThread;
			this.playerThread = null;
			while (aThread297.isAlive()) {
			}
		}
	}

	public Control getControl(final String s) {
		if (s.indexOf("VolumeControl") != -1) {
			return this.volumeControl;
		}
		if (s.indexOf("ToneControl") != -1) {
			return this.toneControl;
		}
		if (s.indexOf("MIDIControl") != -1) {
			return this.midiControl;
		}
		if (s.contains("FramePositioningControl") && sequencer instanceof Player) {
			return this.frameControl;
		}
		return null;
	}

	public Control[] getControls() {
		return this.controls;
	}
	/*
	public void run() {
	    int loopCount = this.loopCount;
	    do {
	        final boolean b = false;
	        this.aBoolean309 = b;
	        this.aBoolean302 = b;
	        if (this.sequence instanceof Sequence && this.aThread297 != null) {
	            this.sequencer.start();
	            while (!this.aBoolean302 && this.aThread297 != null) {
	                try {
	                    Thread.sleep(99L);
	                    continue;
	                }
	                catch (Exception ex) {}
	                break;
	            }
	            this.sequencer.stop();
	        }
	        else if (this.sequence instanceof Clip && this.aThread297 != null) {
	            final Clip clip;
	            (clip = (Clip)this.sequence).start();
	            while (!this.aBoolean309 && this.aThread297 != null) {
	                try {
	                    Thread.sleep(99L);
	                    continue;
	                }
	                catch (Exception ex2) {}
	                break;
	            }
	            clip.stop();
	        }
	        else if (this.sequence instanceof c) {
	            ((c)this.sequence).method785();
	        }
	        this.setMediaTime(0L);
	        if (loopCount > 0) {
	            --loopCount;
	        }
	    } while (this.aThread297 != null && loopCount != 0);
	    this.aThread297 = null;
	    this.anInt303 = 300;
	    PlayerImpl playerImpl;
	    String s;
	    Long n;
	    if (this.aBoolean309 || this.aBoolean302) {
	        playerImpl = this;
	        s = "endOfMedia";
	        n = new Long(0L);
	    }
	    else {
	        playerImpl = this;
	        s = "stopped";
	        n = new Long(0L);
	    }
	    playerImpl.notifyListeners(s, n);
	}*/

	public void run() {
		int loopCount = this.loopCount;
		while (playerThread != null && loopCount != 0) {
			boolean b2 = false;
			mp3Complete = midiCompleted = soundCompleted = false;
			if (sequence instanceof Sequence && playerThread != null) {
				sequencer.start();
				while (!midiCompleted && playerThread != null) {
					try {
						Thread.sleep(1);
						Thread.yield();
					} catch (Exception exception) {
						break;
					}
				}
				this.sequencer.stop();
			} else if (sequence instanceof Clip && playerThread != null) {
				Clip clip = (Clip) this.sequence;
				clip.start();
				while (!soundCompleted && playerThread != null) {
					try {
						Thread.sleep(99);
					} catch (Exception exception) {
						break;
					}
				}
				clip.stop();
			} else if (sequence instanceof Player) {
				try {
					((Player) sequence).play();
				} catch (JavaLayerException e) {
					e.printStackTrace();
				}
				if (sequence != null && ((Player) sequence).isComplete()) {
					mp3Complete = true;
					if (dataSource != null) {
						//dataSource.stop();
						dataSource.disconnect();
					}
				}
			}
			if (loopCount != -1) {
				if (loopCount <= 0) continue;
				--loopCount;
			}
			if(loopCount != 0) {
				try {
					setMediaTime(0L);
				} catch (MediaException e) {
					e.printStackTrace();
				}
			}
		}
		this.playerThread = null;
		this.state = 300;
		notifyListeners(this.soundCompleted || this.midiCompleted || this.mp3Complete ? "endOfMedia" : "stopped", getMediaTime());
	}

	public void update(final LineEvent lineEvent) {
		if (lineEvent.getType() == LineEvent.Type.STOP) {
			this.soundCompleted = true;
		}
	}

	public void meta(final MetaMessage metaMessage) {
		if (metaMessage.getType() == 47) {
			this.midiCompleted = true;
		}
	}

	public void setLevel(final int n) {
		notifyListeners("volumeChanged", n);
		final double n2 = n / 100.0;
		if (this.sequence instanceof Clip) {
			try {
				((FloatControl) ((Clip) this.sequence).getControl(FloatControl.Type.MASTER_GAIN))
						.setValue((float) (Math.log((n2 == 0.0) ? 1.0E-4 : n2) / Math.log(10.0) * 20.0));
			} catch (Exception e) {
			}
			return;
		}
		if (this.sequence instanceof Sequence) {
			try {
				if (this.synthesizer.getDefaultSoundbank() == null) {
					final ShortMessage shortMessage = new ShortMessage();
					for (int i = 0; i < 16; ++i) {
						shortMessage.setMessage(176, i, 7, (int) (n2 * 127.0));
						MidiSystem.getReceiver().send(shortMessage, -1L);
					}
					return;
				}
				final MidiChannel[] channels = this.synthesizer.getChannels();
				for (int j = 0; j < channels.length; ++j) {
					channels[j].controlChange(7, (int) (n2 * 127.0));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		if (this.sequence instanceof Player) {
			((Player) this.sequence).setLevel(n);
		}
	}

	public void setMIDIChannelVolume(final int n, final int n2) {
		if (this.sequence instanceof Sequence) {
			try {
				if (this.synthesizer.getDefaultSoundbank() != null) {
					final MidiChannel[] channels = this.synthesizer.getChannels();
					for (int i = 0; i < channels.length; ++i) {
						channels[i].controlChange(n, n2);
					}
					return;
				}
				final ShortMessage shortMessage = new ShortMessage();
				for (int j = 0; j < 16; ++j) {
					shortMessage.setMessage(176, n, 7, n2);
					MidiSystem.getReceiver().send(shortMessage, -1L);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public TimeBase getTimeBase() {
		if (this.state == 100 || this.state == 0) {
			throw new IllegalStateException();
		}
		return this.timeBase;
	}

	public void setTimeBase(final TimeBase timeBase) throws MediaException {
		if (this.state == 100 || this.state == 400 || this.state == 0) {
			throw new IllegalStateException();
		}
		throw new MediaException("TimeBase can't be set on this player.");
	}
}
