package emulator.graphics3D.lwjgl;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public final class LWJGLUtil {
	private ByteBuffer normalByteBuffer;
	private ShortBuffer normalShortBuffer;
	private ByteBuffer colorBuffer;
	private ShortBuffer vertexShortBuffer;
	private ShortBuffer[] texCoordsBuffer = new ShortBuffer[Emulator3D.NumTextureUnits];
	private IntBuffer elementsBuffer;

	private ByteBuffer imageBuffer;
	private FloatBuffer floatBuffer;

	public LWJGLUtil() {
		final int initVerticesCount = 1024 * 4;

		normalByteBuffer = BufferUtils.createByteBuffer(initVerticesCount * 3);
		normalShortBuffer = BufferUtils.createShortBuffer(initVerticesCount * 3);

		colorBuffer = BufferUtils.createByteBuffer(initVerticesCount * 4);

		vertexShortBuffer = BufferUtils.createShortBuffer(initVerticesCount * 3);

		for (int slot = 0; slot < Emulator3D.NumTextureUnits; slot++) {
			texCoordsBuffer[slot] = BufferUtils.createShortBuffer(initVerticesCount * 2);
		}

		final int initPolyCount = 1024;

		elementsBuffer = BufferUtils.createIntBuffer(initPolyCount * 3);

		final int initTextureSize = 512;

		imageBuffer = BufferUtils.createByteBuffer(initTextureSize * initTextureSize * 4);

		floatBuffer = BufferUtils.createFloatBuffer(16); //should be enough for matrices and stuff
	}

	public ByteBuffer getNormalBuffer(byte[] normals) {
		if (normalByteBuffer == null || normalByteBuffer.capacity() < normals.length) {
			normalByteBuffer = BufferUtils.createByteBuffer(normals.length * 4 / 3);
		}

		normalByteBuffer.position(normalByteBuffer.capacity() - normals.length);
		normalByteBuffer.put(normals);
		normalByteBuffer.position(normalByteBuffer.capacity() - normals.length);
		return normalByteBuffer;
	}

	public ShortBuffer getNormalBuffer(short[] normals) {
		if (normalShortBuffer == null || normalShortBuffer.capacity() < normals.length) {
			normalShortBuffer = BufferUtils.createShortBuffer(normals.length * 4 / 3);
		}

		normalShortBuffer.position(normalShortBuffer.capacity() - normals.length);

		for (short i : normals) {
			normalShortBuffer.put(i);
		}

		normalShortBuffer.position(normalShortBuffer.capacity() - normals.length);
		return normalShortBuffer;
	}

	public ByteBuffer getImageBuffer(byte[] pixels) {
		if (imageBuffer == null || imageBuffer.capacity() < pixels.length) {
			imageBuffer = BufferUtils.createByteBuffer(pixels.length * 4 / 3);
		}

		imageBuffer.position(imageBuffer.capacity() - pixels.length);
		imageBuffer.put(pixels);
		imageBuffer.position(imageBuffer.capacity() - pixels.length);
		return imageBuffer;
	}

	public ShortBuffer getVertexBuffer(byte[] positions) {
		if (vertexShortBuffer == null || vertexShortBuffer.capacity() < positions.length) {
			vertexShortBuffer = BufferUtils.createShortBuffer(positions.length * 4 / 3);
		}

		vertexShortBuffer.position(vertexShortBuffer.capacity() - positions.length);
		int i = 0;
		int length = positions.length;

		while (i < length) {
			vertexShortBuffer.put(positions[i++]);
		}

		vertexShortBuffer.position(vertexShortBuffer.capacity() - positions.length);
		return vertexShortBuffer;
	}

	public ShortBuffer getVertexBuffer(short[] positions) {
		if (vertexShortBuffer == null || vertexShortBuffer.capacity() < positions.length) {
			vertexShortBuffer = BufferUtils.createShortBuffer(positions.length * 4 / 3);
		}

		vertexShortBuffer.position(vertexShortBuffer.capacity() - positions.length);
		vertexShortBuffer.put(positions);
		vertexShortBuffer.position(vertexShortBuffer.capacity() - positions.length);
		return vertexShortBuffer;
	}

	public ByteBuffer getColorBuffer(byte[] colors, float alphaFactor, int vertexCount) {
		int size = alphaFactor == 1.0F ? colors.length : 4 * vertexCount;
		if (colorBuffer == null || colorBuffer.capacity() < size) {
			colorBuffer = BufferUtils.createByteBuffer(size * 4 / 3);
		}

		colorBuffer.position(colorBuffer.capacity() - size);
		if (alphaFactor == 1.0F) {
			colorBuffer.put(colors);
		} else {
			int i;
			if (colors.length == size) {
				i = 0;

				while (i < size) {
					colorBuffer.put(colors[i++]);
					colorBuffer.put(colors[i++]);
					colorBuffer.put(colors[i++]);
					colorBuffer.put((byte) ((int) ((float) (colors[i++] & 255) * alphaFactor + 0.5F)));
				}
			} else {
				i = 0;

				while (i < colors.length) {
					colorBuffer.put(colors[i++]);
					colorBuffer.put(colors[i++]);
					colorBuffer.put(colors[i++]);
					colorBuffer.put((byte) ((int) (255.0F * alphaFactor + 0.5F)));
				}
			}
		}

		colorBuffer.position(colorBuffer.capacity() - size);
		return colorBuffer;
	}

	public IntBuffer getElementsBuffer(int[] indices) {
		if (elementsBuffer == null || elementsBuffer.capacity() < indices.length) {
			elementsBuffer = BufferUtils.createIntBuffer(indices.length * 4 / 3);
		}

		elementsBuffer.position(elementsBuffer.capacity() - indices.length);
		elementsBuffer.put(indices);
		elementsBuffer.position(elementsBuffer.capacity() - indices.length);
		return elementsBuffer;
	}

	public ShortBuffer getTexCoordBuffer(short[] texCoords, int idx) {
		if (texCoordsBuffer[idx] == null || texCoordsBuffer[idx].capacity() < texCoords.length) {
			texCoordsBuffer[idx] = BufferUtils.createShortBuffer(texCoords.length * 4 / 3);
		}
		ShortBuffer buf = texCoordsBuffer[idx];

		buf.position(buf.capacity() - texCoords.length);
		int i = 0;
		int length = texCoords.length;

		while (i < length) {
			buf.put(texCoords[i++]);
		}

		buf.position(buf.capacity() - texCoords.length);
		return buf;
	}

	public ShortBuffer getTexCoordBuffer(byte[] texCoords, int idx) {
		if (texCoordsBuffer[idx] == null || texCoordsBuffer[idx].capacity() < texCoords.length) {
			texCoordsBuffer[idx] = BufferUtils.createShortBuffer(texCoords.length * 4 / 3);
		}
		ShortBuffer buf = texCoordsBuffer[idx];

		buf.position(buf.capacity() - texCoords.length);
		int i = 0;
		int length = texCoords.length;

		while (i < length) {
			buf.put(texCoords[i++]);
		}

		buf.position(buf.capacity() - texCoords.length);
		return buf;
	}

	public FloatBuffer getFloatBuffer(float[] values) {
		if (floatBuffer == null || floatBuffer.capacity() < values.length) {
			floatBuffer = BufferUtils.createFloatBuffer(values.length * 4 / 3);
		}

		floatBuffer.position(floatBuffer.capacity() - values.length);
		floatBuffer.put(values);
		floatBuffer.position(floatBuffer.capacity() - values.length);
		return floatBuffer;
	}
}