package com.samsung.util;

import emulator.custom.CustomJarResources;
import emulator.media.MMFPlayer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerImpl;
import javax.microedition.media.PlayerListener;

public class AudioClip {
    public static final int TYPE_MMF = 1;
    public static final int TYPE_MP3 = 2;
    public static final int TYPE_MIDI = 3;
    public PlayerImpl m_player;
    public int dataLen;
    public int loopCount;
    public int volume;
    private int type;
    private byte[] data;
    private boolean mmfInit;
    public static final int STATUS_STOP = 0;
    public static final int STATUS_PLAY = 1;
    public static final int STATUS_PAUSE = 2;
    private int status;

    public AudioClip(int n, byte[] b, int n2, int n3) {
        byte[] t = new byte[n3];
        System.arraycopy(b, n2, t, 0, n3);
        this.init(t, n);
    }

    public AudioClip(int type, String s) throws IOException {
        InputStream is = CustomJarResources.getResourceStream(s);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[512];
        while (is.available() > 0) {
            int n2 = is.read(b);
            baos.write(b, 0, n2);
        }
        byte[] t = new byte[baos.size()];
        System.arraycopy(baos.toByteArray(), 0, t, 0, t.length);
        this.init(t, type);
    }

    private void init(byte[] b, int type) {
        if (b == null) {
            throw new NullPointerException();
        }
        this.type = type;
        if (type == 1) {
            this.mmfInit = MMFPlayer.a();
            this.data = b;
            this.dataLen = b.length;
            this.status = 0;
            return;
        }
        try {
            String ct = "";
            if (type == 3) {
                ct = "audio/midi";
            } else if (type == 2) {
                ct = "audio/mpeg";
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            this.m_player = new PlayerImpl(bais, ct);
            this.m_player.addPlayerListener(new MMAPIListener());
            bais.close();
        }
        catch (Exception exception) {}
        this.status = 0;
    }

    public int getStatus() {
        return this.status;
    }

    public static boolean isSupported() {
        return true;
    }

    public void pause() {
        if (this.type == 1) {
            if (this.mmfInit && MMFPlayer.isPlaying()) {
                MMFPlayer.pause();
                this.status = 2;
                return;
            }
        } else if (this.m_player != null) {
            try {
                long l = this.m_player.getMediaTime();
                this.m_player.stop();
                this.m_player.setMediaTime(l);
            }
            catch (Exception exception) {}
            this.status = 2;
        }
    }

    public void play(int l, int v) {
        if(l == -1) {
            l = 255;
        }
        if (l < 0 || l > 255) {
            throw new IllegalArgumentException("loop must be in range 0 to 255: " + l);
        }
        if (v < 0 || v > 5) {
            throw new IllegalArgumentException("volume must be in range from 0 to 5: " + v);
        }
        if (this.type == 1) {
            this.loopCount = l;
            this.volume = v;
            if (this.mmfInit) {
                MMFPlayer.initPlayer(this.data);
                MMFPlayer.play(l, v);
                this.status = 1;
                return;
            }
        } else if (this.m_player != null) {
            this.m_player.setLoopCount(l);
            this.m_player.setLevel(v * 20);
            try {
                this.m_player.start();
            }
            catch (Exception exception) {}
            this.status = 1;
        }
    }

    public void resume() {
        if (this.type == 1) {
            if (this.mmfInit) {
                MMFPlayer.resume();
                this.status = 1;
                return;
            }
        } else if (this.m_player != null) {
            try {
                this.m_player.start();
            }
            catch (Exception exception) {}
            this.status = 1;
        }
    }

    public void stop() {
        if (this.type == 1) {
            if (this.mmfInit && MMFPlayer.isPlaying()) {
                MMFPlayer.stop();
                this.status = 0;
                return;
            }
        } else if (this.m_player != null) {
            try {
                this.m_player.stop();
            }
            catch (Exception exception) {}
            this.status = 0;
        }
    }

    class MMAPIListener implements PlayerListener {
        public void playerUpdate(Player p, String s, Object o) {
            if ("stopped".equals(s)) {
                AudioClip.this.status = 0;
            }
        }
    }
}
