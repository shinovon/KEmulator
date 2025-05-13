/*
 * Copyright 2022-2023 Yury Kharchenko
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

package com.nttdocomo.opt.ui.j3d;

import com.nttdocomo.ui.UIException;
import emulator.custom.CustomJarResources;
import ru.woesss.j2me.micro3d.TextureImpl;

import java.io.IOException;
import java.io.InputStream;

public class Texture {
	public final TextureImpl impl;
	final boolean forenv;


	int toonHigh;
	int toonLow;
	int toonThreshold;

	public Texture(byte[] b, boolean isForModel) {
		this.forenv = isForModel;
		impl = new TextureImpl(b);
	}

	public Texture(InputStream inputStream, boolean isForEnv) throws IOException {
		this.forenv = isForEnv;
		impl = new TextureImpl(CustomJarResources.getBytes(inputStream));
	}

	public void setNormalShader() {
		if (forenv) {
			throw new UIException();
		}
	}

	public void setToonShader(int threshold, int high, int low) {
		if (this.forenv) {
			throw new UIException(1, "Can't set shader for env mapping");
		}
		toonThreshold = threshold;
		toonHigh = high;
		toonLow = low;
	}
}