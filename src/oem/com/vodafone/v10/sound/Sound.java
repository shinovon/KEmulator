package com.vodafone.v10.sound;

import emulator.custom.ResourceManager;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerImpl;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Sound {
	byte[] data;

	public Sound(byte[] data) {
		this.data = data;
	}

	public Sound(String url) throws IOException {
		this(ResourceManager.getBytes(url));
	}

	public int getSize() {
		return data == null ? 0 : data.length;
	}

	public int getUseTracks() {
		return 1;
	}
}