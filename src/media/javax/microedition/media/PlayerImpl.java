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

import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class PlayerImpl implements javax.microedition.media.Player, Runnable, LineListener {

    private static int count;
    private static boolean midiPlaying;

    public static List<javax.microedition.media.Player> players = new ArrayList<javax.microedition.media.Player>();
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

    PlayerImpl() {
        loopCount = 1;
        state = UNREALIZED;
        listeners = new Vector<PlayerListener>();
        timeBase = Manager.getSystemTimeBase();
        players.add(this);
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
        switch (contentType) {
            case "audio/amr":
                amr(inputStream);
                break;
            case "audio/x-wav":
            case "audio/wav":
                wav(inputStream);
                break;
            case "audio/x-midi":
            case "audio/midi":
                midi(inputStream);
                break;
            case "audio/mpeg":
                try {
                    InputStream i = inputStream;
                    if (i instanceof ByteArrayInputStream) {
                        data = CustomJarResources.getBytes(i);
                        i = new ByteArrayInputStream(data);
                    }
                    sequence = new Player(i);
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                    throw new IOException(e);
                }
                volumeControl = new VolumeControlImpl(this);
                controls = new Control[]{volumeControl};
                break;
            default:
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
                break;
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
            final byte[] method476;
            if ((data = method476 = emulator.media.amr.a.method476(CustomJarResources.getBytes(inputStream))) == null) {
                throw new MediaException("Cannot parse AMR data");
            }
            InputStream i = new ByteArrayInputStream(method476);
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
            data = CustomJarResources.getBytes(inputStream);
            sequence = MidiSystem.getSequence(new ByteArrayInputStream(data));
        } catch (Exception e) {
            sequence = null;
        }
        midiControl = new MIDIControlImpl(this);
        toneControl = new ToneControlImpl();
        volumeControl = new VolumeControlImpl(this);
        controls = new Control[]{toneControl, volumeControl, midiControl};
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
        if (sequence instanceof Player) {
            ((Player) sequence).close();
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
        } else if (sequence instanceof Player) {
            try {
                res = ((double) (dataLen * 8) / ((Player) sequence).getBitrate());
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
                if (EmulatorMIDI.currentPlayer == this && midiPlaying)
                    return EmulatorMIDI.getMicrosecondPosition();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return midiPosition;
        }
        if (sequence instanceof Player) {
            return ((Player) sequence).getPosition() * 1000L;
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
                if (EmulatorMIDI.currentPlayer == this)
                    EmulatorMIDI.setMicrosecondPosition(ms);
            } catch (Exception e) {
                throw new MediaException(e);
            }
        } else if (sequence instanceof Player) {
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
                    EmulatorMIDI.setSequence((Sequence) sequence);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sequence = null;
                throw new MediaException(e.toString());
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

            SourceStream stream = dataSource.getStreams()[0];
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
                    sequence = new Player(new InputStream() {

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
        if(state < PREFETCHED && sequence instanceof Sequence) {
            prefetch();
        }
        if (state != STARTED) {
            if (sequence != null) {
                if (sequence instanceof Sequence && midiPlaying && EmulatorMIDI.currentPlayer != this && EmulatorMIDI.currentPlayer != null) {
                    try {
                        EmulatorMIDI.currentPlayer.stop();
                    } catch (Exception ignored) {
                    }
//					throw new MediaException("MIDI is currently playing");
                }
                if (complete) {
                    setMediaTime(0);
                }
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
        if (sequence instanceof Player) {
            ((Player) sequence).stop();
            return;
        }
        synchronized (playLock) {
            playLock.notify();
        }
        if (playerThread != null) {
            final Thread t = playerThread;
            playerThread = null;
            if(t.isAlive()) {
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
            int loopCount = this.loopCount;
            boolean complete = false;
            boolean b = true;
            while (playerThread != null && loopCount != 0) {
                complete = false;
                if (sequence instanceof Sequence) {
                    EmulatorMIDI.start(this, (Sequence) sequence, midiPosition);
                    EmulatorMIDI.currentPlayer = this;
                    if (b) {
                        notifyListeners(PlayerListener.STARTED, midiPosition = EmulatorMIDI.getMicrosecondPosition());
                        b = false;
                    }
                    midiPlaying = true;
                    synchronized (playLock) {
                        playLock.wait();
                    }
                    complete = this.complete;
                    midiPosition = EmulatorMIDI.getMicrosecondPosition();
                    midiPlaying = false;
                    EmulatorMIDI.stop();
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
                } else if (sequence instanceof Player) {
                    if (b) {
                        notifyListeners(PlayerListener.STARTED, getMediaTime());
                        b = false;
                    }
                    try {
                        ((Player) sequence).play();
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                        notifyListeners(PlayerListener.ERROR, e.toString());
                    }
                    if (sequence != null && ((Player) sequence).isComplete()) {
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
        if (sequence instanceof Player) {
            ((Player) sequence).setLevel(n);
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

    public void notifyCompleted() {
        complete = true;
        synchronized (playLock) {
            playLock.notify();
        }
    }
}
