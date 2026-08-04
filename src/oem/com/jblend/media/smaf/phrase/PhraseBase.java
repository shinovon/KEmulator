/*
 * Copyright 2023 Yury Kharchenko
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

package com.jblend.media.smaf.phrase;

import emulator.custom.ResourceManager;

import java.io.IOException;

abstract class PhraseBase {
	byte[] data;
	public PhraseBase(byte[] data) {
		this.data = data;
	}

	public PhraseBase(String url) throws IOException {
		this(ResourceManager.getBytes(url));
	}

	public int getSize() {
		return data == null ? 0 : data.length;
	}

	public int getUseTracks() {
		return 1;
	}
}