package javax.microedition.media;

import emulator.*;
import emulator.custom.CustomJarResources;

import java.io.*;
import java.net.URL;

import javax.microedition.media.control.*;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

import java.util.*;
import javax.sound.sampled.*;
import javax.sound.midi.*;

import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class PlayerImpl implements javax.microedition.media.Player, Runnable, LineListener {

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
    private boolean dataSourceDisconnected;
    private InputStream inputStream;
    private static int count;
    private int level = 100;
    private byte[] data;

    PlayerImpl() {
        players.add(this);
    }

    public PlayerImpl(String contentType, DataSource src) throws IOException, MediaException {
        this();
        this.contentType = contentType;
        this.dataSource = src;
    }

    public PlayerImpl(final InputStream inputStream, String contentType) throws IOException {
        this();
        if (contentType == null)
            contentType = "";
        a(inputStream, contentType);
    }

    private void a(final InputStream inputStream, String contentType) throws IOException {
        contentType = contentType.toLowerCase();
        this.contentType = contentType;
        this.loopCount = 1;
        if (dataLen == 0) this.dataLen = inputStream.available();
        this.inputStream = inputStream;
        if (contentType.equals("audio/amr")) {
            this.amr(inputStream);
        } else if (contentType.equals("audio/x-wav") || contentType.equals("audio/wav")) {
            this.wav(inputStream);
        } else if (contentType.equals("audio/x-midi") || contentType.equals("audio/midi")) {
            this.midi(inputStream);
        } else if (contentType.equals("audio/mpeg")) {
            try {
                InputStream i = inputStream;
                if (i instanceof ByteArrayInputStream) {
                    this.data = CustomJarResources.getBytes(i);
                    i = new ByteArrayInputStream(data);
                }
                this.sequence = new Player(i);
            } catch (JavaLayerException e) {
                e.printStackTrace();
                throw new IOException(e);
            }
            this.volumeControl = new VolumeControlImpl(this);
            this.controls = new Control[]{this.volumeControl};
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
        this.state = UNREALIZED;
        this.setLevel(level);
        this.listeners = new Vector();
        this.timeBase = Manager.getSystemTimeBase();
    }

    public PlayerImpl(String locator, String contentType, DataSource src) {
        this();
    }

    public PlayerImpl(String src, String contentType) throws IOException {
        this();
        if (contentType == null)
            contentType = "";
        if (contentType.equals("audio/wav")) {
            contentType = contentType.toLowerCase();
            this.contentType = contentType;
            this.loopCount = 1;
            this.dataLen = (int) new File(src).length();
            wav(new URL("file:///" + src));
            this.state = UNREALIZED;
            this.setLevel(level);
            this.listeners = new Vector();
            this.timeBase = Manager.getSystemTimeBase();
        } else {
            this.dataLen = (int) new File(src).length();
            a(new FileInputStream(src), contentType);
        }
    }

    private void amr(final InputStream inputStream) throws IOException {
        try {
            final byte[] method476;
            if ((data = method476 = emulator.media.amr.a.method476(CustomJarResources.getBytes(inputStream))) == null) {
                throw new MediaException("Cannot parse AMR data");
            }
            InputStream i = new ByteArrayInputStream(method476);
            final AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000.0f, 16, 1, 2, 8000.0f, false);
            final AudioInputStream audioInputStream = new AudioInputStream(i, audioFormat, -1L);
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
        this.controls = new Control[]{this.toneControl, this.volumeControl};
    }

    private void wav(final InputStream inputStream) throws IOException {
        try {
            this.sequence = AudioSystem.getAudioInputStream(inputStream);
        } catch (Exception ex) {
            System.out.println("WAV realize error: " + ex.toString());
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
        this.controls = new Control[]{this.toneControl, this.volumeControl};
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
        this.controls = new Control[]{this.toneControl, this.volumeControl};
    }

    private static MidiDevice midiDevice;
    static Sequencer midiSequencer;
    public static PlayerImpl currentMidiPlayer;
    private static boolean midiPlaying;
    private long midiPosition;

    private void midi(final InputStream inputStream) throws IOException {
        try {
            this.data = CustomJarResources.getBytes(inputStream);
            this.sequence = MidiSystem.getSequence(new ByteArrayInputStream(data));
        } catch (Exception e) {
            this.sequence = null;
        }
        this.midiControl = new MIDIControlImpl(this);
        this.toneControl = new ToneControlImpl();
        this.volumeControl = new VolumeControlImpl(this);
        this.controls = new Control[]{this.toneControl, this.volumeControl, this.midiControl};
    }

    static void initMIDIDevice() throws MidiUnavailableException {
        if (midiDevice == null) {
            midiDevice = MidiSystem.getMidiDevice(Emulator.getMidiDeviceInfo());
            midiDevice.open();
        }
        if (midiSequencer == null) {
            midiSequencer = MidiSystem.getSequencer();
            for (Transmitter t : midiSequencer.getTransmitters()) {
                t.setReceiver(midiDevice.getReceiver());
            }
            midiSequencer.addMetaEventListener(new MetaEventListener() {
                @Override
                public void meta(MetaMessage meta) {
                    if (meta.getType() == 47) {
                        if (currentMidiPlayer != null) {
                            currentMidiPlayer.midiCompleted = true;
                        }
                    }
                }
            });
            midiSequencer.open();
        }
    }

    public void addPlayerListener(final PlayerListener playerListener) throws IllegalStateException {
        if (this.state == CLOSED) {
            throw new IllegalStateException();
        }
        if (playerListener != null) {
            this.listeners.add(playerListener);
        }
    }

    public void removePlayerListener(final PlayerListener playerListener) throws IllegalStateException {
        if (this.state == CLOSED) {
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
            //this.midiSequencer.close();
        } else if (this.sequence instanceof Player) {
            ((Player) this.sequence).close();
        }
        if (dataSource != null && !dataSourceDisconnected) {
            dataSource.disconnect();
            dataSourceDisconnected = true;
        }
        this.sequence = null;
        this.state = CLOSED;
        this.notifyListeners(PlayerListener.CLOSED, null);
    }

    public void deallocate() throws IllegalStateException {
        data = null;
        if (this.state == CLOSED) {
            throw new IllegalStateException();
        }
        if (this.state == STARTED) {
            try {
                this.stop();
                return;
            } catch (MediaException ex) {
                return;
            }
        }
        if (this.state == PREFETCHED) {
            state = REALIZED;
        } else if (this.state == REALIZED) {
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
        return this.contentType;
    }

    public long getDuration() {
        double n = 0.0;
        double n2;
        if (sequence == null) return 0;
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
        if (sequence == null) return 0;
        if (this.sequence instanceof Clip) {
            return ((Clip) this.sequence).getMicrosecondPosition();
        }
        if (this.sequence instanceof Sequence) {
            if (currentMidiPlayer == this && midiPlaying)
                return this.midiSequencer.getMicrosecondPosition();
            return midiPosition;
        }
        if (this.sequence instanceof Player) {
            return ((Player) this.sequence).getPosition() * 1000L;
        }
        return TIME_UNKNOWN;
    }

    public long setMediaTime(final long t) throws MediaException {
        if (sequence == null) return 0;
        long ms = 0L;
        if (this.sequence instanceof Clip) {
            ms = ((Clip) this.sequence).getMicrosecondPosition();
            if (t < ms) {
                ms = t;
            }
            ((Clip) this.sequence).setMicrosecondPosition(t);
        } else if (this.sequence instanceof Sequence) {
            ms = ((Sequence) this.sequence).getMicrosecondLength();
            if (t < ms) {
                ms = t;
            }
            midiPosition = ms;
            if (currentMidiPlayer == this)
                this.midiSequencer.setMicrosecondPosition(ms);
        } else if (this.sequence instanceof Player) {
            long l = getMediaTime();
            if (t == 0 && l == 0)
                return 0;
            if (t < l) {
                try {
                    Header old = ((Player) sequence).bitstream().header;
                    ((Player) sequence).reset();
                    ((Player) sequence).skip((int) (t / 1000L), old);
                    ms = ((Player) sequence).getPosition() * 1000L;
                } catch (JavaLayerException e) {
                    throw new MediaException(e);
                }
            } else {
                try {
                    Header old = ((Player) sequence).bitstream().header;
                    ((Player) sequence).reset();
                    ((Player) sequence).skip((int) (t / 1000L), old);
                    ms = ((Player) sequence).getPosition() * 1000L;
                } catch (JavaLayerException e) {
                    throw new MediaException(e);
                }
            }
        }
        return ms;
    }

    public int getState() {
        return this.state;
    }

    public void prefetch() throws IllegalStateException, MediaException {
        if (this.state == CLOSED) {
            throw new IllegalStateException();
        }
        if (this.state == UNREALIZED) {
            this.realize();
        } else if (this.state != REALIZED) {
            return;
        }
        if (sequence instanceof Sequence) {
            try {
                initMIDIDevice();
            } catch (Exception e) {
                e.printStackTrace();
                throw new MediaException(e);
            }
            try {
                if (this.sequence != null) {
                    midiSequencer.setSequence((Sequence) this.sequence);
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.sequence = null;
                throw new MediaException(e.toString());
            }
        }
        if (this.dataSource != null) {
            try {
                this.dataSource.connect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new MediaException(e);
            }
        }
        this.state = PREFETCHED;
    }

    public void realize() throws IllegalStateException, MediaException {
        if (this.state == CLOSED) {
            throw new IllegalStateException();
        }
        if (this.state == UNREALIZED) {
            this.state = REALIZED;
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
                this.controls = new Control[]{this.volumeControl};
                this.setLevel(level);
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
        if (this.state == CLOSED) {
            throw new IllegalStateException();
        }
        if(state < PREFETCHED && sequence instanceof Sequence) {
            prefetch();
        }
        if (this.state != STARTED) {
            if (sequence != null) {
                if (this.sequence instanceof Sequence && midiPlaying && currentMidiPlayer != this && currentMidiPlayer != null) {
                    try {
                        currentMidiPlayer.stop();
                    } catch (Exception e) {
                    }
//					throw new MediaException("MIDI is currently playing");
                }
                if ((this.sequence instanceof Sequence && midiCompleted)
                        || (this.sequence instanceof Player && mp3Complete)
                        || (this.sequence instanceof Clip && soundCompleted)) {
                    setMediaTime(0);
                }
                (this.playerThread = new Thread(this, "PlayerImpl-" + (++count))).start();
            }
            setLevel(level);
            this.state = STARTED;
            this.notifyListeners(PlayerListener.STARTED, new Long(getMediaTime()));
            if (sequence == null) {
                this.state = PREFETCHED;
                this.notifyListeners(PlayerListener.STOPPED, new Long(0L));
            }
        }
    }

    public void stop() throws IllegalStateException, MediaException {
        if (this.state == CLOSED) {
            throw new IllegalStateException();
        }
        if (sequence == null) return;
        if (this.sequence instanceof Player) {
            ((Player) this.sequence).stop();
            return;
        }
        if (this.playerThread != null) {
            final Thread t = this.playerThread;
            this.playerThread = null;
            if(t.isAlive()) {
                try {
                    synchronized (t) {
                        t.wait();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public Control getControl(final String s) {
        if (s.contains("VolumeControl")) {
            return this.volumeControl;
        }
        if (s.contains("ToneControl")) {
            return this.toneControl;
        }
        if (s.contains("MIDIControl")) {
            return this.midiControl;
        }
        return null;
    }

    public Control[] getControls() {
        return this.controls;
    }

    public void run() {
        int loopCount = this.loopCount;
        while (playerThread != null && loopCount != 0) {
            boolean b2 = false;
            mp3Complete = midiCompleted = soundCompleted = false;
            if (sequence instanceof Sequence && playerThread != null) {
                try {
                    midiSequencer.setSequence((Sequence) sequence);
                    currentMidiPlayer = this;
                    midiSequencer.setMicrosecondPosition(midiPosition);
                    midiSequencer.start();
                    midiPlaying = true;
                    while (!midiCompleted && playerThread != null) {
                        try {
                            Thread.sleep(1);
                        } catch (Exception e) {
                            break;
                        }
                    }
                    midiPosition = midiSequencer.getMicrosecondPosition();
                } catch (InvalidMidiDataException e) {
                }
                midiPlaying = false;
                this.midiSequencer.stop();
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
            if (loopCount != 0) {
                try {
                    setMediaTime(0L);
                } catch (MediaException e) {
                    e.printStackTrace();
                }
            }
        }
        this.playerThread = null;
        this.state = PREFETCHED;
        notifyListeners(this.soundCompleted || this.midiCompleted || this.mp3Complete ? PlayerListener.END_OF_MEDIA : PlayerListener.STOPPED, getMediaTime());
    }

    public void update(final LineEvent lineEvent) {
        if (lineEvent.getType() == LineEvent.Type.STOP) {
            this.soundCompleted = true;
        }
    }

    public void setLevel(int n) {
        if (level != n) notifyListeners(PlayerListener.VOLUME_CHANGED, n);
        level = n;
        if (sequence == null) return;
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
			/*
			try {
				Receiver r = sequencer.getTransmitters().iterator().next().getReceiver();
				final ShortMessage shortMessage = new ShortMessage();
				for (int i = 0; i < 16; ++i) {
					shortMessage.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, (int) (n2 * 127.0));
					r.send(shortMessage, -1L);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			 */
            return;
        }
        if (this.sequence instanceof Player) {
            ((Player) this.sequence).setLevel(n);
        }
    }

    public static void setMIDIChannelVolume(final int n, final int n2) {
        try {
            Receiver r = getMidiReceiver();
            ShortMessage shortMessage = new ShortMessage(ShortMessage.CONTROL_CHANGE, n, 7, (int) (n2 * 127.0));
            r.send(shortMessage, -1L);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void shortMidiEvent(int type, int data1, int data2) {
        try {
            Receiver r = getMidiReceiver();
            ShortMessage shortMessage = new ShortMessage(type, data1, data2);
            r.send(shortMessage, -1L);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static int longMidiEvent(byte[] data, int offset, int length) {
        byte[] b = new byte[length];
        System.arraycopy(data, offset, b, 0, length);
        try {
            Receiver r = getMidiReceiver();
            MidiMessage msg = new MidiMessage(b) {
                public Object clone() {
                    return null;
                }
            };
            r.send(msg, -1L);
            return length;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private static Receiver getMidiReceiver() {
        return midiSequencer.getTransmitters().iterator().next().getReceiver();
    }

    public TimeBase getTimeBase() {
        if (this.state == UNREALIZED || this.state == CLOSED) {
            throw new IllegalStateException();
        }
        return this.timeBase;
    }

    public void setTimeBase(final TimeBase timeBase) throws MediaException {
        if (this.state == UNREALIZED || this.state == STARTED || this.state == CLOSED) {
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
        } else if (sequence instanceof Clip) {
            ext = "amr";
        } else if (sequence instanceof Player) {
            ext = "mp3";
        } else if (contentType != null) {
            switch (contentType.toLowerCase()) {
                case "audio/wav":
                case "audio/wave":
                case "audio/x-wav":
                    ext = "wav";
                    break;
                case "audio/x-mid":
                case "audio/mid":
                case "audio/midi":
                    ext = "mid";
                    break;
                case "audio/mpeg":
                case "audio/mp3":
                    ext = "mp3";
                    break;
            }
        }
        if (ext.isEmpty()) return "audio" + hashCode();
        return hashCode() + "." + ext;
    }
}
