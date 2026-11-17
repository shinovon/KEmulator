package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;

public class Material extends Object3D {
	public static final int AMBIENT = 1024;
	public static final int DIFFUSE = 2048;
	public static final int EMISSIVE = 4096;
	public static final int SPECULAR = 8192;
	private boolean vertexColorTracking = false;
	private int ambientColor = 3355443;
	private int diffuseColor = -3355444;
	private int emissiveColor = 0;
	private int specularColor = 0;
	private float shininess = 0.0F;

	public void setColor(int target, int ARGB) {
		if (target == 0 || (target & ~(AMBIENT | DIFFUSE | EMISSIVE | SPECULAR)) != 0) {
			throw new IllegalArgumentException();
		} else {
			if ((target & AMBIENT) != 0) {
				this.ambientColor = ARGB & 0xffffff;
			}

			if ((target & DIFFUSE) != 0) {
				this.diffuseColor = ARGB;
			}

			if ((target & EMISSIVE) != 0) {
				this.emissiveColor = ARGB & 0xffffff;
			}

			if ((target & SPECULAR) != 0) {
				this.specularColor = ARGB & 0xffffff;
			}

		}
	}

	public int getColor(int target) {
		if (target != AMBIENT && target != DIFFUSE && target != EMISSIVE && target != SPECULAR) {
			throw new IllegalArgumentException();
		} else {
			int color = target == AMBIENT ? this.ambientColor : (target == DIFFUSE ? this.diffuseColor : (target == EMISSIVE ? this.emissiveColor : this.specularColor));
			return target == DIFFUSE ? color : color & 0xffffff;
		}
	}

	public void setShininess(float shininess) {
		if (shininess >= 0.0F && shininess <= 128.0F) {
			this.shininess = shininess;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public float getShininess() {
		return this.shininess;
	}

	public void setVertexColorTrackingEnable(boolean enable) {
		this.vertexColorTracking = enable;
	}

	public boolean isVertexColorTrackingEnabled() {
		return this.vertexColorTracking;
	}

	protected void updateProperty(int property, float[] value) {
		switch (property) {
			case 256:
				this.diffuseColor = this.diffuseColor & 16777215 | G3DUtils.getIntColor(value) & -16777216;
				return;
			case 257:
				this.ambientColor = G3DUtils.getIntColor(value) & 0xffffff;
				return;
			case 258:
			case 259:
			case 260:
			case 263:
			case 264:
			case 265:
			case 266:
			case 267:
			case 268:
			case 269:
			case 270:
			default:
				super.updateProperty(property, value);
				return;
			case 261:
				this.diffuseColor = this.diffuseColor & -16777216 | G3DUtils.getIntColor(value) & 16777215;
				return;
			case 262:
				this.emissiveColor = G3DUtils.getIntColor(value) & 0xffffff;
				return;
			case 271:
				this.shininess = G3DUtils.limit(value[0], 0.0F, 128.0F);
				return;
			case 272:
				this.specularColor = G3DUtils.getIntColor(value) & 0xffffff;
		}
	}
}
