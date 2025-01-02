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

import javax.microedition.lcdui.Graphics;
import java.util.WeakHashMap;

public class RenderProxy {
	private static final WeakHashMap<Graphics, com.mascotcapsule.micro3d.v3.Graphics3D> renders = new WeakHashMap();

	public static void drawCommandList(Graphics g,
									   Texture[] textures,
									   int x, int y,
									   FigureLayout layout,
									   Effect3D effect,
									   int[] commandList) {

		if (layout == null || effect == null || commandList == null) {
			throw new NullPointerException();
		}
		com.mascotcapsule.micro3d.v3.Graphics3D render = getRender(g);

		render.drawCommandList(getMascotTextures(textures), x, y, getMascotLayout(layout), getMascotEffect(effect), commandList);
	}

	public static void drawCommandList(Graphics g,
									   Texture texture,
									   int x, int y,
									   FigureLayout layout,
									   Effect3D effect,
									   int[] commandList) {

		if (layout == null || effect == null || commandList == null) {
			throw new NullPointerException();
		}
		com.mascotcapsule.micro3d.v3.Graphics3D render = getRender(g);

		render.drawCommandList(getMascotTexture(texture), x, y, getMascotLayout(layout), getMascotEffect(effect), commandList);
	}

	public static void drawFigure(Graphics g, Figure figure, int x, int y,
								  FigureLayout layout, Effect3D effect) {
		if (figure == null || layout == null || effect == null) {
			throw new NullPointerException();
		}
		com.mascotcapsule.micro3d.v3.Graphics3D render = getRender(g);

		render.drawFigure(figure.impl, x, y, getMascotLayout(layout), getMascotEffect(effect));
	}

	public static void flush(Graphics g) {
		getRender(g).flush();
	}

	public static void renderFigure(Graphics g, Figure figure, int x, int y,
									FigureLayout layout, Effect3D effect) {
		if (figure == null || layout == null || effect == null) {
			throw new NullPointerException();
		}

		com.mascotcapsule.micro3d.v3.Graphics3D render = getRender(g);

		render.renderFigure(figure.impl, x, y, getMascotLayout(layout), getMascotEffect(effect));
	}

	public static void renderPrimitives(Graphics g, Texture texture, int x, int y,
										FigureLayout layout, Effect3D effect,
										int command, int numPrimitives, int[] vertexCoords,
										int[] normals, int[] textureCoords, int[] colors) {
		if (layout == null || effect == null || vertexCoords == null || normals == null
				|| textureCoords == null || colors == null) {
			throw new NullPointerException();
		}
		if (command < 0 || numPrimitives <= 0 || numPrimitives >= 256) {
			throw new IllegalArgumentException();
		}
		com.mascotcapsule.micro3d.v3.Graphics3D render = getRender(g);

		render.renderPrimitives(getMascotTexture(texture), x, y, getMascotLayout(layout), getMascotEffect(effect), command, numPrimitives, vertexCoords, normals, textureCoords, colors);
	}

	public static com.mascotcapsule.micro3d.v3.Graphics3D getRender(Graphics g) {
		com.mascotcapsule.micro3d.v3.Graphics3D render = renders.get(g);
		if (render == null) {
			render = new com.mascotcapsule.micro3d.v3.Graphics3D();
			render.bind(g);
			renders.put(g, render);
		}
		return render;
	}

	private static com.mascotcapsule.micro3d.v3.Effect3D getMascotEffect(Effect3D effect) {
		if (effect == null) return null;
		com.mascotcapsule.micro3d.v3.Effect3D r = new com.mascotcapsule.micro3d.v3.Effect3D();
		r.setShading(effect.shading);
		r.setLight(getMascotLight(effect.light));
		r.setSphereTexture(effect.texture == null ? null : effect.texture.impl);
		r.setToonParams(effect.toonThreshold, effect.toonHigh, effect.toonLow);
		r.setTransparency(effect.isTransparency);
		return r;
	}

	private static com.mascotcapsule.micro3d.v3.Texture getMascotTexture(Texture texture) {
		if (texture == null) return null;
		return texture.impl;
	}

	private static com.mascotcapsule.micro3d.v3.FigureLayout getMascotLayout(FigureLayout layout) {
		if (layout == null) return null;
		com.mascotcapsule.micro3d.v3.FigureLayout r = new com.mascotcapsule.micro3d.v3.FigureLayout(getMascotAffine(layout.getAffineTrans()),
				layout.scaleX, layout.scaleY, layout.centerY, layout.centerY);
		return r;
	}

	private static com.mascotcapsule.micro3d.v3.AffineTrans getMascotAffine(AffineTrans a) {
		if (a == null) return null;
		int[] out = new int[12];
		int offset = 0;
		out[offset++] = a.m00;
		out[offset++] = a.m01;
		out[offset++] = a.m02;
		out[offset++] = a.m03;
		out[offset++] = a.m10;
		out[offset++] = a.m11;
		out[offset++] = a.m12;
		out[offset++] = a.m13;
		out[offset++] = a.m20;
		out[offset++] = a.m21;
		out[offset++] = a.m22;
		out[offset  ] = a.m23;
		return new com.mascotcapsule.micro3d.v3.AffineTrans(out);
	}

	private static com.mascotcapsule.micro3d.v3.Light getMascotLight(Light light) {
		if (light == null) return null;
		return new com.mascotcapsule.micro3d.v3.Light(getMascotVector(light.getDirection()), light.getDirIntensity(), light.getAmbIntensity());
	}

	private static com.mascotcapsule.micro3d.v3.Vector3D getMascotVector(Vector3D vector) {
		if (vector == null) return null;
		return new com.mascotcapsule.micro3d.v3.Vector3D(vector.x, vector.y, vector.z);
	}

	private static com.mascotcapsule.micro3d.v3.Texture[] getMascotTextures(Texture[] textures) {
		com.mascotcapsule.micro3d.v3.Texture[] r = new com.mascotcapsule.micro3d.v3.Texture[textures.length];
		for(int i = 0; i < r.length; i++) {
			r[i] = textures[i] == null ? null : textures[i].impl;
		}
		return r;
	}
}