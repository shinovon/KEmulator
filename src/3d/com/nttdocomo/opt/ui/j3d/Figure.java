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

import emulator.custom.ResourceManager;
import ru.woesss.j2me.micro3d.FigureImpl;

import java.io.IOException;
import java.io.InputStream;

public class Figure {
	Texture texture;
	FigureImpl impl;


	public Figure(byte[] b) {
		impl = new FigureImpl(b);
	}


	public Figure(InputStream inputStream) throws IOException {
		impl = new FigureImpl(ResourceManager.getBytes(inputStream));
	}

	/** @noinspection unused*/
	public void setPosture(ActionTable actionTable, int action, int frame) {
		if (actionTable == null) {
			throw new NullPointerException();
		}
		impl.setPosture(actionTable.impl, action, frame);
	}

	public void setTexture(Texture tex) {
		if (tex == null)
			throw new NullPointerException();
		if (tex.forenv)
			throw new IllegalArgumentException();

		texture = tex;
	}
}