package javax.microedition.m3g;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class TriangleStripArray extends IndexBuffer {
	private int[] stripLengths;
	private IntBuffer buffer;

	public TriangleStripArray(int firstIndex, int[] stripLengths) {
		if (stripLengths == null) {
			throw new NullPointerException();
		} else if (stripLengths.length == 0) {
			throw new IllegalArgumentException();
		}

		int sumStripLengths = 0;

		for (int i = stripLengths.length - 1; i >= 0; --i) {
			if (stripLengths[i] < 3) {
				throw new IllegalArgumentException();
			}

			sumStripLengths += stripLengths[i];
		}

		if (firstIndex + sumStripLengths > 65535) {
			throw new IllegalArgumentException();
		} else {
			super.indices = new int[sumStripLengths];

			for (int i = 0; i < sumStripLengths; ++i) {
				super.indices[i] = firstIndex + i;
			}
			this.stripLengths = new int[stripLengths.length];
			System.arraycopy(stripLengths, 0, this.stripLengths, 0, stripLengths.length);
			copyToNative();
		}
	}

	public TriangleStripArray(int[] indices, int[] stripLengths) {
		if (indices == null || stripLengths == null) {
			throw new NullPointerException();
		} else if (stripLengths.length == 0) {
			throw new IllegalArgumentException();
		}

		int sumStripLengths = 0;

		for (int i = stripLengths.length - 1; i >= 0; --i) {
			if (stripLengths[i] < 3) {
				throw new IllegalArgumentException();
			}

			sumStripLengths += stripLengths[i];
		}

		if (indices.length < sumStripLengths) {
			throw new IllegalArgumentException();
		}

		for (int i = sumStripLengths - 1; i >= 0; --i) {
			if (indices[i] < 0 || indices[i] > 65535) {
				throw new IllegalArgumentException();
			}
		}

		super.indices = new int[sumStripLengths];
		System.arraycopy(indices, 0, super.indices, 0, sumStripLengths);
		this.stripLengths = new int[stripLengths.length];
		System.arraycopy(stripLengths, 0, this.stripLengths, 0, stripLengths.length);
		copyToNative();
	}

	protected Object3D duplicateObject() {
		TriangleStripArray clone = (TriangleStripArray) super.duplicateObject();
		clone.indices = (int[]) super.indices.clone();
		clone.stripLengths = (int[]) stripLengths.clone();

		return clone;
	}

	public int getStripCount() {
		return stripLengths.length;
	}

	public int[] getIndexStrip(int index) {
		if (index >= 0 && index < stripLengths.length) {
			int sumStripLengths = 0;

			for (int i = 0; i < index; ++i) {
				sumStripLengths += stripLengths[i];
			}

			int[] resIndexStrip = new int[stripLengths[index]];
			System.arraycopy(super.indices, sumStripLengths, resIndexStrip, 0, stripLengths[index]);

			return resIndexStrip;
		} else {
			return null;
		}
	}

	public IntBuffer getBuffer() {
		buffer.position(0);
		return buffer;
	}

	protected boolean getIndices(int index, int[] indices) {
		int index2 = 0;

		for (int i = 0; i < stripLengths.length; ++i) {
			if (index < stripLengths[i] - 2) {
				indices[0] = super.indices[index2 + index + 0];
				indices[1] = super.indices[index2 + index + 1];
				indices[2] = super.indices[index2 + index + 2];
				indices[3] = index & 1;

				return true;
			}

			index -= stripLengths[i] - 2;
			index2 += stripLengths[i];
		}

		return false;
	}

	public void getIndices(int[] indices) {
		int stripCount = stripLengths.length;
		int k = 0, m = 0;

		for (int i = 0; i < stripCount; i++) {
			for (int j = 0; j < stripLengths[i] - 2; j++) {
				if ((j & 1) == 0) {
					indices[k++] = super.indices[m + 0];
					indices[k++] = super.indices[m + 1];
				} else {
					indices[k++] = super.indices[m + 1];
					indices[k++] = super.indices[m + 0];
				}
				indices[k++] = super.indices[m + 2];
				m++;
			}
			m += 2;
		}
	}

	public int getIndexCount() {
		int count = 0, stripCount = stripLengths.length;
		for (int i = 0; i < stripCount; i++) {
			count += stripLengths[i] - 2;
		}
		return count * 3;
	}

	private void copyToNative() {
		int joinedIndexCount = 0;

		int stripCount = stripLengths.length;
		int strip;
		for (strip = 0; strip < stripCount; ++strip) {
			if (strip != 0) {
				joinedIndexCount += ((joinedIndexCount & 1) != 0) ? 3 : 2;
			}
			joinedIndexCount += stripLengths[strip];
		}
		allocateBuffer(joinedIndexCount);
		int src = 0;
		for (strip = 0; strip < stripCount; ++strip) {
			if (strip != 0) {
				buffer.put(indices[src - 1]);
				buffer.put(indices[src]);
				if ((stripLengths[strip - 1] & 1) != 0) {
					buffer.put(indices[src]);
				}
			}
			for (int i = 0; i < stripLengths[strip]; ++i) {
				buffer.put(indices[src++]);
			}
		}
	}

	private IntBuffer allocateBuffer(int size) {
		buffer = ByteBuffer.allocateDirect(size << 2)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		buffer.position(0);
		return buffer;
	}

	public int profilerCount() {
		return indices.length - stripLengths.length * 2;
	}
}
