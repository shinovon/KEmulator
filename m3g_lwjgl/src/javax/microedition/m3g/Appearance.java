package javax.microedition.m3g;

import emulator.graphics3D.lwjgl.Emulator3D;

public class Appearance extends Object3D {
	private int layer = 0;
	private PolygonMode polygonMode = null;
	private CompositingMode compositingMode = null;
	private Material material = null;
	private Fog fog = null;
	private Texture2D[] textures;

	public Appearance() {
		this.textures = new Texture2D[Emulator3D.NumTextureUnits];
	}

	protected Object3D duplicateObject() {
		Appearance copy;
		(copy = (Appearance) super.duplicateObject()).textures = (Texture2D[]) this.textures.clone();
		return copy;
	}

	public void setLayer(int layer) {
		if (layer >= -63 && layer <= 63) {
			this.layer = layer;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public int getLayer() {
		return this.layer;
	}

	public void setFog(Fog fog) {
		this.removeReference(this.fog);
		this.fog = fog;
		this.addReference(this.fog);
	}

	public Fog getFog() {
		return this.fog;
	}

	public void setPolygonMode(PolygonMode polygonMode) {
		this.removeReference(this.polygonMode);
		this.polygonMode = polygonMode;
		this.addReference(this.polygonMode);
	}

	public PolygonMode getPolygonMode() {
		return this.polygonMode;
	}

	public void setCompositingMode(CompositingMode compositingMode) {
		this.removeReference(this.compositingMode);
		this.compositingMode = compositingMode;
		this.addReference(this.compositingMode);
	}

	public CompositingMode getCompositingMode() {
		return this.compositingMode;
	}

	public void setTexture(int index, Texture2D texture) {
		if (index >= 0 && index < Emulator3D.NumTextureUnits) {
			this.removeReference(this.textures[index]);
			this.textures[index] = texture;
			this.addReference(this.textures[index]);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public Texture2D getTexture(int index) {
		if (index >= 0 && index < Emulator3D.NumTextureUnits) {
			return this.textures[index];
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public void setMaterial(Material material) {
		this.removeReference(this.material);
		this.material = material;
		this.addReference(this.material);
	}

	public Material getMaterial() {
		return this.material;
	}
}
