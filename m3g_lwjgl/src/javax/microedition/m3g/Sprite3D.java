package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.Vector4f;
import emulator.graphics3D.lwjgl.Emulator3D;

public class Sprite3D extends Node {
	private boolean scaled;
	private Image2D image;
	private Appearance appearance;
	private int cropX;
	private int cropY;
	private int cropWidth;
	private int cropHeight;

	public Sprite3D(boolean scaled, Image2D image, Appearance appearance) {
		this.scaled = scaled;
		this.setImage(image);
		this.setAppearance(appearance);
	}

	public void setImage(Image2D image) {
		if (image == null) {
			throw new NullPointerException();
		} else {
			this.removeReference(this.image);
			this.image = image;
			this.addReference(this.image);
			this.cropX = this.cropY = 0;
			this.cropWidth = Math.min(image.getWidth(), Emulator3D.MaxSpriteCropDimension);
			this.cropHeight = Math.min(image.getHeight(), Emulator3D.MaxSpriteCropDimension);
		}
	}

	public Image2D getImage() {
		return this.image;
	}

	public boolean isScaled() {
		return this.scaled;
	}

	public void setAppearance(Appearance appearance) {
		this.removeReference(this.appearance);
		this.appearance = appearance;
		this.addReference(this.appearance);
	}

	public Appearance getAppearance() {
		return this.appearance;
	}

	public void setCrop(int cropX, int cropY, int width, int height) {
		if (Math.abs(width) <= Emulator3D.MaxSpriteCropDimension && Math.abs(height) <= Emulator3D.MaxSpriteCropDimension) {
			this.cropX = cropX;
			this.cropY = cropY;
			this.cropWidth = width;
			this.cropHeight = height;
		} else {
			throw new IllegalArgumentException("width or height exceeds the MaxSpriteCropDimension:" + Emulator3D.MaxSpriteCropDimension);
		}
	}

	public int getCropX() {
		return this.cropX;
	}

	public int getCropY() {
		return this.cropY;
	}

	public int getCropWidth() {
		return this.cropWidth;
	}

	public int getCropHeight() {
		return this.cropHeight;
	}

	protected void updateProperty(int property, float[] values) {
		switch (property) {
			case 259:
				this.cropX = G3DUtils.round(values[0]);
				this.cropY = G3DUtils.round(values[1]);
				if (values.length > 2) {
					this.cropWidth = G3DUtils.limit(G3DUtils.round(values[2]), -Emulator3D.MaxSpriteCropDimension, Emulator3D.MaxSpriteCropDimension);
					this.cropHeight = G3DUtils.limit(G3DUtils.round(values[3]), -Emulator3D.MaxSpriteCropDimension, Emulator3D.MaxSpriteCropDimension);
					return;
				}
				break;
			default:
				super.updateProperty(property, values);
		}

	}

	protected boolean rayIntersect(int scope, float[] ray, RayIntersection ri, Transform transform) {
		if ((scope & getScope()) == 0) return false;

		if (this.appearance != null && this.image != null && this.scaled && this.cropWidth != 0 && this.cropHeight != 0) {
			Camera camera;
			if ((camera = ri.getCamera()) != null && this.image != null) {
				int[] crop;
				boolean flipX = (crop = new int[]{this.cropX, this.cropY, this.cropWidth, this.cropHeight})[2] < 0;
				boolean flipY = crop[3] < 0;
				crop[2] = Math.abs(crop[2]);
				crop[3] = Math.abs(crop[3]);
				int[] clippedCrop = new int[4];
				if (!G3DUtils.intersectRectangle(crop[0], crop[1], crop[2], crop[3], 0, 0, this.image.getWidth(), this.image.getHeight(), clippedCrop)) {
					return false;
				} else {
					Vector4f origin = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
					Vector4f unitX = new Vector4f(0.5F, 0.0F, 0.0F, 1.0F);
					Vector4f unitY = new Vector4f(0.0F, 0.5F, 0.0F, 1.0F);
					Transform tmpTransform = new Transform();
					this.getTransformTo(camera, tmpTransform);
					tmpTransform.getImpl_().transform(origin);
					tmpTransform.getImpl_().transform(unitX);
					tmpTransform.getImpl_().transform(unitY);
					Vector4f center = new Vector4f(origin);
					origin.mul(1.0F / origin.w);
					unitX.mul(1.0F / unitX.w);
					unitY.mul(1.0F / unitY.w);
					float distance = (origin.z - ray[6]) / (ray[7] - ray[6]);
					unitX.sub(origin);
					unitY.sub(origin);
					Vector4f scaleX = new Vector4f(unitX.length(), 0.0F, 0.0F, 0.0F);
					Vector4f scaleY = new Vector4f(0.0F, unitY.length(), 0.0F, 0.0F);
					scaleX.add(center);
					scaleY.add(center);
					camera.getProjection(tmpTransform);
					tmpTransform.getImpl_().transform(center);
					tmpTransform.getImpl_().transform(scaleX);
					tmpTransform.getImpl_().transform(scaleY);
					if (center.w > 0.0F && -center.w < center.z && center.z <= center.w) {
						center.mul(1.0F / center.w);
						scaleX.mul(1.0F / scaleX.w);
						scaleY.mul(1.0F / scaleY.w);
						scaleX.sub(center);
						scaleY.sub(center);
						scaleX.x = scaleX.length() / (float) crop[2];
						scaleY.y = scaleY.length() / (float) crop[3];
						center.x -= (float) (2 * crop[0] + crop[2] - 2 * clippedCrop[0] - clippedCrop[2]) * scaleX.x;
						center.y += (float) (2 * crop[1] + crop[3] - 2 * clippedCrop[1] - clippedCrop[3]) * scaleY.y;
						scaleX.x *= (float) clippedCrop[2];
						scaleY.y *= (float) clippedCrop[3];
						float[] quad = new float[12];
						int[] texels = new int[8];
						quad[0] = center.x - scaleX.x;
						quad[1] = center.y + scaleY.y;
						quad[2] = center.z;
						quad[3] = quad[0];
						quad[4] = center.y - scaleY.y;
						quad[5] = quad[2];
						quad[6] = center.x + scaleX.x;
						quad[7] = quad[1];
						quad[8] = quad[2];
						quad[9] = quad[6];
						quad[10] = quad[4];
						quad[11] = quad[2];
						int[] targetArray;
						byte targetIndex;
						int targetValue;
						if (!flipX) {
							texels[0] = clippedCrop[0];
							texels[2] = clippedCrop[0];
							texels[4] = clippedCrop[0] + clippedCrop[2];
							targetArray = texels;
							targetIndex = 6;
							targetValue = clippedCrop[0] + clippedCrop[2];
						} else {
							texels[0] = clippedCrop[0] + clippedCrop[2];
							texels[2] = clippedCrop[0] + clippedCrop[2];
							texels[4] = clippedCrop[0];
							targetArray = texels;
							targetIndex = 6;
							targetValue = clippedCrop[0];
						}

						targetArray[targetIndex] = targetValue;
						if (!flipY) {
							texels[1] = clippedCrop[1];
							texels[3] = clippedCrop[1] + clippedCrop[3];
							texels[5] = clippedCrop[1];
							targetArray = texels;
							targetIndex = 7;
							targetValue = clippedCrop[1] + clippedCrop[3];
						} else {
							texels[1] = clippedCrop[1] + clippedCrop[3];
							texels[3] = clippedCrop[1];
							texels[5] = clippedCrop[1] + clippedCrop[3];
							targetArray = texels;
							targetIndex = 7;
							targetValue = clippedCrop[1];
						}

						targetArray[targetIndex] = targetValue;
						float pickX = 2.0F * ri.getPickX() - 1.0F;
						float pickY = 1.0F - 2.0F * ri.getPickY();
						if (pickX >= quad[0] && pickX <= quad[6] && pickY <= quad[1] && pickY >= quad[4]) {
							if (!ri.testDistance(distance)) {
								return false;
							}

							pickX -= quad[0];
							pickY = quad[1] - pickY;
							float[] texS = new float[]{0.0F};
							float[] texT = new float[]{0.0F};
							float[] texCoordTarget;
							float texCoordValue;
							if (!flipX) {
								texCoordTarget = texS;
								targetIndex = 0;
								texCoordValue = (float) texels[0] + (float) (texels[4] - texels[0]) * pickX / (quad[6] - quad[0]);
							} else {
								texCoordTarget = texS;
								targetIndex = 0;
								texCoordValue = (float) texels[0] - (float) (texels[0] - texels[4]) * pickX / (quad[6] - quad[0]);
							}

							texCoordTarget[targetIndex] = texCoordValue;
							if (!flipY) {
								texCoordTarget = texT;
								targetIndex = 0;
								texCoordValue = (float) texels[1] + (float) (texels[3] - texels[1]) * pickY / (quad[1] - quad[4]);
							} else {
								texCoordTarget = texT;
								targetIndex = 0;
								texCoordValue = (float) texels[1] - (float) (texels[1] - texels[3]) * pickY / (quad[1] - quad[4]);
							}

							texCoordTarget[targetIndex] = texCoordValue;
							int texelX = G3DUtils.limit(G3DUtils.round(texS[0]), 0, this.image.getWidth() - 1);
							int texelY = G3DUtils.limit(G3DUtils.round(texT[0]), 0, this.image.getWidth() - 1);
							texS[0] = G3DUtils.limit(texS[0], 0.0F, (float) this.image.getWidth());
							texT[0] = G3DUtils.limit(texT[0], 0.0F, (float) this.image.getHeight());
							int alphaThreshold = 0;
							byte alpha = -1;
							if (this.appearance.getCompositingMode() != null) {
								alphaThreshold = (int) (this.appearance.getCompositingMode().getAlphaThreshold() * 256.0F);
							}

							label71:
							{
								byte[] imageData = this.image.getImageData();
								int pixelOffset;
								byte alphaOffset;
								switch (this.image.getFormat()) {
									case 96:
										pixelOffset = texelY * this.image.getWidth() * 1 + texelX * 1;
										alphaOffset = 0;
										break;
									case 97:
									case 99:
									default:
										break label71;
									case 98:
										pixelOffset = texelY * this.image.getWidth() * 2 + texelX * 2;
										alphaOffset = 1;
										break;
									case 100:
										pixelOffset = texelY * this.image.getWidth() * 4 + texelX * 4;
										alphaOffset = 3;
								}

								alpha = imageData[pixelOffset + alphaOffset];
							}

							texS[0] /= (float) this.image.getWidth();
							texT[0] /= (float) this.image.getHeight();
							if ((alpha & 255) >= alphaThreshold) {
								return ri.endPick(distance, texS, texT, 0, this, new float[]{0, 0, 1});
							}
						}

						return false;
					} else {
						return false;
					}
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
