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

package com.vodafone.v10.sound;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerImpl;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Sound {
	private Player player;

	public Sound(byte[] data) throws IOException {
		if (data == null) {
			throw new NullPointerException("sound data is null!");
		}
		try {
			player = Manager.createPlayer(new ByteArrayInputStream(data), "audio/xmf");
		} catch (MediaException e) {
			throw new RuntimeException(e);
		}
		try {
			player.realize();
		} catch (MediaException e) {
			e.printStackTrace();
		}
	}

	public Sound(String name) throws IOException {
		try {
			player = Manager.createPlayer(name);
		} catch (MediaException e) {
			throw new RuntimeException(e);
		}
		try {
			player.realize();
		} catch (MediaException e) {
			e.printStackTrace();
		}
	}

	Player getPlayer() {
		return player;
	}

	public int getSize() {
		try {
			return ((PlayerImpl) player).dataLen;
		} catch (Exception e) {
			return 0;
		}
	}

	public int getUseTracks() {
		return 0;
	}
}