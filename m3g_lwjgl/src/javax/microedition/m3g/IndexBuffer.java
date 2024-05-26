package javax.microedition.m3g;

public abstract class IndexBuffer extends Object3D {
	int[] indices;

	public int getIndexCount() {
		return this.indices != null ? this.indices.length : 0;
	}

	public void getIndices(int[] var1) {
		if (var1 == null) {
			throw new NullPointerException();
		} else if (var1.length < this.getIndexCount()) {
			throw new IllegalArgumentException();
		} else {
			if (this.indices != null) {
				System.arraycopy(this.indices, 0, var1, 0, this.indices.length);
			}

		}
	}
}
