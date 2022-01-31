package com.samsung.util;

import com.samsung.util.Class47;
import emulator.custom.CustomJarResources;
import emulator.media.MMFPlayer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.PlayerImpl;
import javax.microedition.media.PlayerListener;

public class AudioClip {
    public static final int TYPE_MMF = 1;
    public static final int TYPE_MP3 = 2;
    public static final int TYPE_MIDI = 3;
    public PlayerImpl m_player;
    public int dataLen;
    public int loopCount;
    public int volume5;
    private int anInt955;
    private byte[] aByteArray956;
    private boolean aBoolean957;
    public static final int STATUS_STOP = 0;
    public static final int STATUS_PLAY = 1;
    public static final int STATUS_PAUSE = 2;
    private int anInt958;

    public AudioClip(int n, byte[] arrby, int n2, int n3) {
        byte[] arrby2 = new byte[n3];
        System.arraycopy((Object)arrby, (int)n2, (Object)arrby2, (int)0, (int)n3);
        this.method357(arrby2, n);
    }

    public AudioClip(int n, String string) throws IOException {
        InputStream inputStream = CustomJarResources.getResourceStream((String)string);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrby = new byte[512];
        while (inputStream.available() > 0) {
            int n2 = inputStream.read(arrby);
            byteArrayOutputStream.write(arrby, 0, n2);
        }
        byte[] arrby2 = new byte[byteArrayOutputStream.toByteArray().length];
        System.arraycopy((Object)byteArrayOutputStream.toByteArray(), (int)0, (Object)arrby2, (int)0, (int)arrby2.length);
        this.method357(arrby2, n);
    }

    private void method357(byte[] arrby, int n) {
        block6 : {
            block7 : {
                if (arrby == null) {
                    throw new NullPointerException();
                }
                this.anInt955 = n;
                if (n != 1) break block7;
                this.aBoolean957 = MMFPlayer.a();
                this.aByteArray956 = arrby;
                this.dataLen = arrby.length;
                break block6;
            }
            try {
                String string;
                block10 : {
                    String string2;
                    block9 : {
                        block8 : {
                            string = "";
                            if (n != 3) break block8;
                            string2 = "audio/midi";
                            break block9;
                        }
                        if (n != 2) break block10;
                        string2 = "audio/mpeg";
                    }
                    string = string2;
                }
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
                this.m_player = new PlayerImpl((InputStream)byteArrayInputStream, string);
                this.m_player.addPlayerListener((PlayerListener)new Class47(this));
                byteArrayInputStream.close();
            }
            catch (Exception exception) {}
        }
        this.anInt958 = 0;
    }

    public int getStatus() {
        return this.anInt958;
    }

    public static boolean isSupported() {
        return true;
    }

    public void pause() {
        if (this.anInt955 == 1) {
            if (this.aBoolean957 && MMFPlayer.isPlaying()) {
                MMFPlayer.pause();
                this.anInt958 = 2;
                return;
            }
        } else if (this.m_player != null) {
            try {
                long l = this.m_player.getMediaTime();
                this.m_player.stop();
                this.m_player.setMediaTime(l);
            }
            catch (Exception exception) {}
            this.anInt958 = 2;
        }
    }

    public void play(int n, int n2) {
        if (n < 0 || n > 255) {
            throw new IllegalArgumentException("loop must be in range 0 to 255: " + n);
        }
        if (n2 < 0 || n2 > 5) {
            throw new IllegalArgumentException("volume must be in range from 0 to 5: " + n2);
        }
        if (this.anInt955 == 1) {
            this.loopCount = n;
            this.volume5 = n2;
            if (this.aBoolean957) {
                MMFPlayer.initPlayer((byte[])this.aByteArray956);
                MMFPlayer.play((int)n, (int)n2);
                this.anInt958 = 1;
                return;
            }
        } else if (this.m_player != null) {
            this.m_player.setLoopCount(n);
            this.m_player.setLevel(n2 * 20);
            try {
                this.m_player.start();
            }
            catch (Exception exception) {}
            this.anInt958 = 1;
        }
    }

    public void resume() {
        if (this.anInt955 == 1) {
            if (this.aBoolean957) {
                MMFPlayer.resume();
                this.anInt958 = 1;
                return;
            }
        } else if (this.m_player != null) {
            try {
                this.m_player.start();
            }
            catch (Exception exception) {}
            this.anInt958 = 1;
        }
    }

    public void stop() {
        if (this.anInt955 == 1) {
            if (this.aBoolean957 && MMFPlayer.isPlaying()) {
                MMFPlayer.stop();
                this.anInt958 = 0;
                return;
            }
        } else if (this.m_player != null) {
            try {
                this.m_player.stop();
            }
            catch (Exception exception) {}
            this.anInt958 = 0;
        }
    }

    static int method358(AudioClip audioClip, int n) {
        audioClip.anInt958 = n;
        return audioClip.anInt958;
    }
}
