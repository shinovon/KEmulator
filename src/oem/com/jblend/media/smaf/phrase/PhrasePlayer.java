package com.jblend.media.smaf.phrase;

import emulator.media.mmf.MMFPlayer;

public class PhrasePlayer {
    private static PhrasePlayer phrasePlayer;

    protected int trackCount;
    protected int audioTrackCount;

    PhraseTrack[] tracks;
    AudioPhraseTrack[] audioTracks;

    PhrasePlayer() {
        MMFPlayer.initialize();
        MMFPlayer.getMaDll().phraseInitialize();
        tracks = new PhraseTrack[MMFPlayer.getMaDll().getMaxTracks()];
        audioTracks = new AudioPhraseTrack[16];
    }

    public static PhrasePlayer getPlayer() {
        if (phrasePlayer == null) phrasePlayer = new PhrasePlayer();
        return phrasePlayer;
    }

    public void disposePlayer() {
        kill();
        phrasePlayer = null;
    }

    public PhraseTrack getTrack() {
        int id = -1;
        for (int i = 0; i < tracks.length; ++i) {
            if (tracks[i] == null) {
                id = i;
                break;
            }
        }
        if (id == -1) {
            throw new IllegalStateException();
        }
        PhraseTrack t = new PhraseTrack(id);
        tracks[id] = t;
        trackCount++;
        return t;
    }

    public AudioPhraseTrack getAudioTrack() {
        int id = -1;
        for (int i = 0; i < audioTracks.length; ++i) {
            if (audioTracks[i] == null) {
                id = i;
                break;
            }
        }
        if (id == -1) {
            throw new IllegalStateException();
        }
        AudioPhraseTrack t = new AudioPhraseTrack(id);
        audioTracks[id] = t;
        audioTrackCount++;
        return t;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public int getAudioTrackCount() {
        return audioTrackCount;
    }

    public PhraseTrack getTrack(int track) {
        return tracks[track];
    }

    public AudioPhraseTrack getAudioTrack(int track) {
        return audioTracks[track];
    }

    public void disposeTrack(PhraseTrack t) {
        int id = t.getID();
        if (tracks[id] == t) {
            t.removePhrase();
            tracks[id] = null;
        }
    }

    public void disposeAudioTrack(AudioPhraseTrack t) {
        int id = t.getID();
        if (audioTracks[id] == t) {
            t.removePhrase();
            audioTracks[id] = null;
        }
    }

    public void kill() {
        for (int i = 0; i < tracks.length; ++i) {
            if (tracks[i] == null) continue;
            tracks[i].removePhrase();
        }
        for (int i = 0; i < audioTracks.length; ++i) {
            if (audioTracks[i] == null) continue;
            audioTracks[i].removePhrase();
        }
        MMFPlayer.getMaDll().phraseKill();
    }

    public void pause() {
        for (int i = 0; i < tracks.length; ++i) {
            if (tracks[i] == null) continue;
            tracks[i].pause();
        }
        for (int i = 0; i < audioTracks.length; ++i) {
            if (audioTracks[i] == null) continue;
            audioTracks[i].pause();
        }
    }

    public void resume() {
        for (int i = 0; i < tracks.length; ++i) {
            if (tracks[i] == null) continue;
            tracks[i].resume();
        }
        for (int i = 0; i < audioTracks.length; ++i) {
            if (audioTracks[i] == null) continue;
            audioTracks[i].resume();
        }
    }
}