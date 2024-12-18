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

package com.jblend.graphics.j3d;

import javax.microedition.lcdui.Image;
import java.io.IOException;

public class Texture {
	public final com.mascotcapsule.micro3d.v3.Texture impl;
	final boolean isForModel;

	public Texture(byte[] b, boolean isForModel) {
		this.isForModel = isForModel;
		impl = new com.mascotcapsule.micro3d.v3.Texture(b, isForModel);
	}

	public Texture(Image image, int x, int y, int width, int height, boolean isForModel) {
		this.isForModel = isForModel;
		System.err.println("com.jblend.graphics.j3d.Texture(Image, int, int, int, boolean) constructor not supported");
		throw new RuntimeException();
		// TODO
//		impl = new com.mascotcapsule.micro3d.v3.Texture(image, x, y, width, height);
	}

	public Texture(String name, boolean isForModel) throws IOException {
		this.isForModel = isForModel;
		impl = new com.mascotcapsule.micro3d.v3.Texture(name, isForModel);
	}
}