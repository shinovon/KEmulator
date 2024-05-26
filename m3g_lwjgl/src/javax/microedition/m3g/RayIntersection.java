package javax.microedition.m3g;

import emulator.graphics3D.Vector4f;
import emulator.graphics3D.lwjgl.Emulator3D;

public class RayIntersection {
	private float[] rayStartEnd;
	private float[] normal;
	private Node intersected;
	private int submeshIndex;
	private float distance;
	private float[] textureS, textureT;

	private Camera camera;
	private float pickX, pickY;
	private float minDistance;

	public RayIntersection() {
		rayStartEnd = new float[]{0, 0, 0, 0, 0, 1};
		this.normal = new float[]{0, 0, 1};
		this.intersected = null;
		this.submeshIndex = 0;
		this.distance = 0.0F;
		this.textureS = new float[Emulator3D.NumTextureUnits];
		this.textureT = new float[Emulator3D.NumTextureUnits];
	}

	public Node getIntersected() {
		return intersected;
	}

	public void getRay(float[] ray) {
		if (ray == null) {
			throw new NullPointerException();
		} else if (ray.length < 6) {
			throw new IllegalArgumentException();
		}

		ray[0] = rayStartEnd[0];
		ray[1] = rayStartEnd[1];
		ray[2] = rayStartEnd[2];
		ray[3] = rayStartEnd[3] - rayStartEnd[0];
		ray[4] = rayStartEnd[4] - rayStartEnd[1];
		ray[5] = rayStartEnd[5] - rayStartEnd[2];
	}

	public float getDistance() {
		return distance;
	}

	public int getSubmeshIndex() {
		return submeshIndex;
	}

	public float getTextureS(int index) {
		if (index < 0 && index >= Emulator3D.NumTextureUnits) {
			throw new IndexOutOfBoundsException();
		}

		return textureS[index];
	}

	public float getTextureT(int index) {
		if (index < 0 && index >= Emulator3D.NumTextureUnits) {
			throw new IndexOutOfBoundsException();
		}

		return textureS[index];
	}

	public float getNormalX() {
		return normal[0];
	}

	public float getNormalY() {
		return normal[1];
	}

	public float getNormalZ() {
		return normal[2];
	}

	protected Camera getCamera() {
		return camera;
	}

	protected float getPickX() {
		return pickX;
	}

	protected float getPickY() {
		return pickY;
	}

	protected void startPick(float[] rayStartEnd, float pickX, float pickY, Camera camera) {
		this.rayStartEnd = rayStartEnd;
		this.pickX = pickX;
		this.pickY = pickY;
		this.camera = camera;
		this.minDistance = Float.MAX_VALUE;
	}

	protected boolean testDistance(float distance) {
		return distance > 0.0F && distance < minDistance;
	}

	protected boolean endPick(float dist, float[] hitTexS, float[] hitTexT, int hitSubmesh, Node hitNode, float[] hitNorm) {
		if (!testDistance(dist)) return false;

		if (hitTexS != null && hitTexT != null) {
			for (int i = 0; i < hitTexS.length; i++) {
				textureS[i] = hitTexS[i];
				textureT[i] = hitTexT[i];
			}
		}

		submeshIndex = hitSubmesh;
		distance = dist;
		intersected = hitNode;

		if (hitNorm != null) {
			normal[0] = hitNorm[0];
			normal[1] = hitNorm[1];
			normal[2] = hitNorm[2];

			float length = Vector4f.length(normal);

			if (length > 1.0E-5F) {
				normal[0] /= length;
				normal[1] /= length;
				normal[2] /= length;
			}
		}

		minDistance = dist;

		return true;
	}
}
