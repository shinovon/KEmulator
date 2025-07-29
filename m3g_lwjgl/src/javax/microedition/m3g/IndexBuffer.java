package javax.microedition.m3g;

public abstract class IndexBuffer extends Object3D {
	int[] indices;

	public abstract int getIndexCount();

	public abstract void getIndices(int[] var1);
}
