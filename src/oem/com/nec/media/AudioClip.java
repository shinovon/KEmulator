package com.nec.media;

public abstract interface AudioClip {
    public abstract void addAudioListener(AudioListener paramAudioListener);

    public abstract void play()
            throws IllegalStateException;

    public abstract void stop()
            throws IllegalStateException;

    public abstract int getLapsedTime()
            throws IllegalStateException;

    public abstract int getTime()
            throws IllegalStateException;

    public abstract int getChannel()
            throws IllegalStateException;

    public abstract int getTempo()
            throws IllegalStateException;

    public abstract void setLoopCount(int paramInt)
            throws IllegalArgumentException, IllegalStateException;

    public abstract AudioListener getAudioListener();
}
