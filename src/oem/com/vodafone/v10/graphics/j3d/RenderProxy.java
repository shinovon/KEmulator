/*
 *  Copyright 2022 Yury Kharchenko
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.vodafone.v10.graphics.j3d;

import javax.microedition.lcdui.Graphics;
import java.util.WeakHashMap;

public class RenderProxy {
	private static final WeakHashMap<Graphics, com.mascotcapsule.micro3d.v3.Graphics3D> renders = new WeakHashMap();

	public static void drawFigure(Graphics g, Figure figure, int x, int y,
								  FigureLayout layout, Effect3D effect) {
		if (figure == null || layout == null || effect == null) {
			throw new NullPointerException();
		}
		com.mascotcapsule.micro3d.v3.Graphics3D render = getRender(g);

		render.drawFigure(figure.impl, x, y, getMascotLayout(layout), getMascotEffect(effect));
	}

	private static com.mascotcapsule.micro3d.v3.Effect3D getMascotEffect(Effect3D effect) {
		com.mascotcapsule.micro3d.v3.Effect3D r = new com.mascotcapsule.micro3d.v3.Effect3D();
		r.setShading(effect.shading);
		r.setLight(getMascotLight(effect.light));
		r.setSphereTexture(effect.texture.impl);
		r.setToonParams(effect.toonThreshold, effect.toonHigh, effect.toonLow);
		r.setTransparency(effect.isTransparency);
		return r;
	}

	private static com.mascotcapsule.micro3d.v3.FigureLayout getMascotLayout(FigureLayout layout) {
		com.mascotcapsule.micro3d.v3.FigureLayout r = new com.mascotcapsule.micro3d.v3.FigureLayout(getMascotAffine(layout.getAffineTrans()),
				layout.scaleX, layout.scaleY, layout.centerY, layout.centerY);
		return r;
	}

	private static com.mascotcapsule.micro3d.v3.AffineTrans getMascotAffine(AffineTrans a) {
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
		return new com.mascotcapsule.micro3d.v3.Light(getMascotVector(light.getDirection()), light.getDirIntensity(), light.getAmbIntensity());
	}

	private static com.mascotcapsule.micro3d.v3.Vector3D getMascotVector(Vector3D vector) {
		return new com.mascotcapsule.micro3d.v3.Vector3D(vector.x, vector.y, vector.z);
	}

	private static com.mascotcapsule.micro3d.v3.Texture[] getMascotTextures(Texture[] textures) {
		com.mascotcapsule.micro3d.v3.Texture[] r = new com.mascotcapsule.micro3d.v3.Texture[textures.length];
		for(int i = 0; i < r.length; i++) {
			r[i] = textures[i].impl;
		}
		return r;
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
}