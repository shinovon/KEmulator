/*
 * Copyright 2020 Nikita Shakarun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package emulator.media.tone;

import emulator.media.EmulatorMIDI;

import javax.microedition.media.*;
import javax.microedition.media.control.ToneControl;
import javax.microedition.media.control.VolumeControlImpl;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MIDITonePlayer implements Player, ToneControl, Runnable
{
	Control toneControl;
	Control volumeControl;
	Control[] controls;

	private int state = Player.UNREALIZED;
    private byte[] midiSequence;
	private long duration;

	private Sequence sequence;
	private static int count;


	public MIDITonePlayer()
	{
		super();
		this.toneControl = new ToneControlImpl(this);
		this.volumeControl = new VolumeControlImpl(this);
		this.controls = new Control[]{this.toneControl, this.volumeControl};
	}

	public MIDITonePlayer(InputStream stream)
	{
		this();
        
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[16384];
			while ((nRead = stream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();

			midiSequence = buffer.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public long doGetDuration() {
		return duration;
	}

    // ToneControl
	public void setSequence(byte[] sequence) {
        try {
			ToneSequence tone = new ToneSequence(sequence);
			tone.process();
			midiSequence = tone.getByteArray();
			duration = tone.getDuration();
		} catch (Exception e) {
			e.printStackTrace();
		}

        try {
			this.sequence = MidiSystem.getSequence(new ByteArrayInputStream(midiSequence));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addPlayerListener(final PlayerListener playerListener) throws IllegalStateException {
		if (state == Player.CLOSED) throw new IllegalStateException();
	}

	public void close() {
		if (state == Player.CLOSED) return;
		if (state == Player.STARTED) {
			EmulatorMIDI.stop();
		}
		state = Player.CLOSED;
	}

	public void deallocate() throws IllegalStateException {
		if (state == Player.CLOSED) throw new IllegalStateException();
		if (state == Player.STARTED) {
			EmulatorMIDI.stop();
		}
	}

	public String getContentType() {
		return null;
	}

	public long getDuration() {
		return duration;
	}

	public long getMediaTime() {
		return 0L;
	}

	public int getState() {
		return state;
	}

	public void prefetch() throws IllegalStateException, MediaException {
		if (state == Player.CLOSED) throw new IllegalStateException();
		if (state < Player.PREFETCHED) {
			state = Player.PREFETCHED;
		}
	}

	public void realize() throws IllegalStateException, MediaException {
		if (state == Player.CLOSED) throw new IllegalStateException();
		if (state < Player.REALIZED) {
			state = Player.REALIZED;
		}
	}

	public void removePlayerListener(final PlayerListener playerListener) throws IllegalStateException {
		if (state == Player.CLOSED) throw new IllegalStateException();
	}

	public void setLoopCount(final int n) {
	}

	public long setMediaTime(final long n) throws MediaException {
		return 0L;
	}

	public void start() throws IllegalStateException, MediaException {
		if (state == Player.CLOSED || sequence == null) throw new IllegalStateException();
		state = Player.STARTED;
		new Thread(this, "TonePlayer-" + (++count)).start();
	}

	public void stop() throws IllegalStateException, MediaException {
		if (state == Player.CLOSED) throw new IllegalStateException();
		if (state == Player.STARTED) {
			EmulatorMIDI.stop();
		}
		state = Player.PREFETCHED;
	}

	public Control getControl(final String s) {
		if (s.equals("VolumeControl")) {
			return this.volumeControl;
		}
		if (s.equals("ToneControl")) {
			return this.toneControl;
		}
		return null;
	}

	public Control[] getControls() {
		return this.controls;
	}

	public TimeBase getTimeBase() {
		return null;
	}

	public void setTimeBase(final TimeBase timeBase) throws MediaException {
	}

	public void run() {
		try {
			EmulatorMIDI.startTone(sequence, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}