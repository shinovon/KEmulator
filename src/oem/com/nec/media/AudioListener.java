package com.nec.media;

public abstract interface AudioListener {
    public static final int AUDIO_STARTED = 0;
    public static final int AUDIO_STOPPED = 1;
    public static final int AUDIO_COMPLETE = 2;

    public abstract void audioAction(AudioClip paramAudioClip, int paramInt1, int paramInt2);
}
