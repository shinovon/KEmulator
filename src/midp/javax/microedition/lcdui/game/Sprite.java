package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Sprite extends Layer {
	public static final int TRANS_NONE = 0;
	public static final int TRANS_ROT90 = 5;
	public static final int TRANS_ROT180 = 3;
	public static final int TRANS_ROT270 = 6;
	public static final int TRANS_MIRROR = 2;
	public static final int TRANS_MIRROR_ROT90 = 7;
	public static final int TRANS_MIRROR_ROT180 = 1;
	public static final int TRANS_MIRROR_ROT270 = 4;
	Image image;
	int rawFrameCount;
	int[] anIntArray266;
	int[] anIntArray267;
	int frameWidth;
	int frameHeight;
	int[] frameSequence;
	private int frame;
	private boolean aBoolean407;
	int refX;
	int refY;
	int collisionX;
	int collisionY;
	int collisionW;
	int collisionH;
	int transform;
	int anInt412;
	int anInt413;
	int anInt414;
	int anInt415;

	public Sprite(Image var1) {
		super(var1.getWidth(), var1.getHeight());
		this.method201(var1, var1.getWidth(), var1.getHeight(), false);
		this.method202();
		this.method209(0);
	}

	public Sprite(Image var1, int var2, int var3) {
		super(var2, var3);
		if (var2 >= 1 && var3 >= 1 && var1.getWidth() % var2 == 0 && var1.getHeight() % var3 == 0) {
			this.method201(var1, var2, var3, false);
			this.method202();
			this.method209(0);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public Sprite(Sprite var1) {
		super(var1 != null ? var1.getWidth() : 0, var1 != null ? var1.getHeight() : 0);
		if (var1 == null) {
			throw new NullPointerException();
		} else {
			this.image = Image.createImage(var1.image);
			this.rawFrameCount = var1.rawFrameCount;
			this.anIntArray266 = new int[this.rawFrameCount];
			this.anIntArray267 = new int[this.rawFrameCount];
			System.arraycopy(var1.anIntArray266, 0, this.anIntArray266, 0, var1.getRawFrameCount());
			System.arraycopy(var1.anIntArray267, 0, this.anIntArray267, 0, var1.getRawFrameCount());
			super.x = var1.getX();
			super.y = var1.getY();
			this.refX = var1.refX;
			this.refY = var1.refY;
			this.collisionX = var1.collisionX;
			this.collisionY = var1.collisionY;
			this.collisionW = var1.collisionW;
			this.collisionH = var1.collisionH;
			this.frameWidth = var1.frameWidth;
			this.frameHeight = var1.frameHeight;
			this.method209(var1.transform);
			this.setVisible(var1.isVisible());
			this.frameSequence = new int[var1.getFrameSequenceLength()];
			this.setFrameSequence(var1.frameSequence);
			this.setFrame(var1.getFrame());
			this.setRefPixelPosition(var1.getRefPixelX(), var1.getRefPixelY());
		}
	}

	public void defineReferencePixel(int var1, int var2) {
		this.refX = var1;
		this.refY = var2;
	}

	public void setRefPixelPosition(int var1, int var2) {
		super.x = var1 - this.method206(this.refX, this.refY, this.transform);
		super.y = var2 - this.method208(this.refX, this.refY, this.transform);
	}

	public int getRefPixelX() {
		return super.x + this.method206(this.refX, this.refY, this.transform);
	}

	public int getRefPixelY() {
		return super.y + this.method208(this.refX, this.refY, this.transform);
	}

	public void setFrame(int var1) {
		if (var1 >= 0 && var1 < this.frameSequence.length) {
			this.frame = var1;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public final int getFrame() {
		return this.frame;
	}

	public int getRawFrameCount() {
		return this.rawFrameCount;
	}

	public int getFrameSequenceLength() {
		return this.frameSequence.length;
	}

	public void nextFrame() {
		this.frame = (this.frame + 1) % this.frameSequence.length;
	}

	public void prevFrame() {
		int var10001;
		if (this.frame == 0) {
			var10001 = this.frameSequence.length;
		} else {
			var10001 = this.frame;
		}

		frame = var10001 - 1;
	}

	public final void paint(Graphics var1) {
		if (var1 == null) {
			throw new NullPointerException();
		} else {
			if (super.visible) {
				var1.drawRegion(this.image, this.anIntArray266[this.frameSequence[this.frame]], this.anIntArray267[this.frameSequence[this.frame]], this.frameWidth, this.frameHeight, this.transform, super.x, super.y, 20);
			}

		}
	}

	public void setFrameSequence(int[] var1) {
		int var2;
		if (var1 == null) {
			this.frame = 0;
			this.aBoolean407 = false;
			this.frameSequence = new int[this.rawFrameCount];

			for (var2 = 0; var2 < this.rawFrameCount; this.frameSequence[var2] = var2++) ;

		} else if (var1.length < 1) {
			throw new IllegalArgumentException();
		} else {
			for (var2 = 0; var2 < var1.length; ++var2) {
				if (var1[var2] < 0 || var1[var2] >= this.rawFrameCount) {
					throw new ArrayIndexOutOfBoundsException();
				}
			}

			this.aBoolean407 = true;
			this.frameSequence = new int[var1.length];
			System.arraycopy(var1, 0, this.frameSequence, 0, var1.length);
			this.frame = 0;
		}
	}

	public void setImage(Image var1, int var2, int var3) {
		if (var2 >= 1 && var3 >= 1 && var1.getWidth() % var2 == 0 && var1.getHeight() % var3 == 0) {
			int var4 = var1.getWidth() / var2 * (var1.getHeight() / var3);
			boolean var5 = true;
			if (var4 < this.rawFrameCount) {
				var5 = false;
				this.aBoolean407 = false;
			}

			if (this.frameWidth == var2 && this.frameHeight == var3) {
				this.method201(var1, var2, var3, var5);
			} else {
				int var6 = super.x + this.method206(this.refX, this.refY, this.transform);
				int var7 = super.y + this.method208(this.refX, this.refY, this.transform);
				this._setWidth(var2);
				this._setHeight(var3);
				this.method201(var1, var2, var3, var5);
				this.method202();
				super.x = var6 - this.method206(this.refX, this.refY, this.transform);
				super.y = var7 - this.method208(this.refX, this.refY, this.transform);
				this.method210(this.transform);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void defineCollisionRectangle(int var1, int var2, int var3, int var4) {
		if (var3 >= 0 && var4 >= 0) {
			this.collisionX = var1;
			this.collisionY = var2;
			this.collisionW = var3;
			this.collisionH = var4;
			this.method209(this.transform);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void setTransform(int var1) {
		this.method209(var1);
	}

	public final boolean collidesWith(Sprite var1, boolean var2) {
		if (var1.visible && super.visible) {
			int var3 = var1.x + var1.anInt412;
			int var4 = var1.y + var1.anInt413;
			int var5 = var3 + var1.anInt414;
			int var6 = var4 + var1.anInt415;
			int var7 = super.x + this.anInt412;
			int var8 = super.y + this.anInt413;
			int var9 = var7 + this.anInt414;
			int var10 = var8 + this.anInt415;
			if (method203(var3, var4, var5, var6, var7, var8, var9, var10)) {
				if (var2) {
					if (this.anInt412 < 0) {
						var7 = super.x;
					}

					if (this.anInt413 < 0) {
						var8 = super.y;
					}

					if (this.anInt412 + this.anInt414 > super.width) {
						var9 = super.x + super.width;
					}

					if (this.anInt413 + this.anInt415 > super.height) {
						var10 = super.y + super.height;
					}

					if (var1.anInt412 < 0) {
						var3 = var1.x;
					}

					if (var1.anInt413 < 0) {
						var4 = var1.y;
					}

					if (var1.anInt412 + var1.anInt414 > var1.width) {
						var5 = var1.x + var1.width;
					}

					if (var1.anInt413 + var1.anInt415 > var1.height) {
						var6 = var1.y + var1.height;
					}

					if (!method203(var3, var4, var5, var6, var7, var8, var9, var10)) {
						return false;
					} else {
						int var11 = var7 < var3 ? var3 : var7;
						int var12 = var8 < var4 ? var4 : var8;
						int var13 = var9 < var5 ? var9 : var5;
						int var14 = var10 < var6 ? var10 : var6;
						int var15 = Math.abs(var13 - var11);
						int var16 = Math.abs(var14 - var12);
						int var17 = this.method205(var11, var12, var13, var14);
						int var18 = this.method207(var11, var12, var13, var14);
						int var19 = var1.method205(var11, var12, var13, var14);
						int var20 = var1.method207(var11, var12, var13, var14);
						return method204(var17, var18, var19, var20, this.image, this.transform, var1.image, var1.transform, var15, var16);
					}
				} else {
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public final boolean collidesWith(TiledLayer var1, boolean var2) {
		if (var1.visible && super.visible) {
			int var3 = var1.x;
			int var4 = var1.y;
			int var5 = var3 + var1.width;
			int var6 = var4 + var1.height;
			int var7 = var1.getCellWidth();
			int var8 = var1.getCellHeight();
			int var9 = super.x + this.anInt412;
			int var10 = super.y + this.anInt413;
			int var11 = var9 + this.anInt414;
			int var12 = var10 + this.anInt415;
			int var13 = var1.getColumns();
			int var14 = var1.getRows();
			if (!method203(var3, var4, var5, var6, var9, var10, var11, var12)) {
				return false;
			} else {
				int var15 = var9 <= var3 ? 0 : (var9 - var3) / var7;
				int var16 = var10 <= var4 ? 0 : (var10 - var4) / var8;
				int var17 = var11 < var5 ? (var11 - 1 - var3) / var7 : var13 - 1;
				int var18 = var12 < var6 ? (var12 - 1 - var4) / var8 : var14 - 1;
				int var19;
				int var20;
				if (!var2) {
					for (var19 = var16; var19 <= var18; ++var19) {
						for (var20 = var15; var20 <= var17; ++var20) {
							if (var1.getCell(var20, var19) != 0) {
								return true;
							}
						}
					}

					return false;
				} else {
					if (this.anInt412 < 0) {
						var9 = super.x;
					}

					if (this.anInt413 < 0) {
						var10 = super.y;
					}

					if (this.anInt412 + this.anInt414 > super.width) {
						var11 = super.x + super.width;
					}

					if (this.anInt413 + this.anInt415 > super.height) {
						var12 = super.y + super.height;
					}

					if (!method203(var3, var4, var5, var6, var9, var10, var11, var12)) {
						return false;
					} else {
						var15 = var9 <= var3 ? 0 : (var9 - var3) / var7;
						var16 = var10 <= var4 ? 0 : (var10 - var4) / var8;
						var17 = var11 < var5 ? (var11 - 1 - var3) / var7 : var13 - 1;
						var18 = var12 < var6 ? (var12 - 1 - var4) / var8 : var14 - 1;
						var20 = (var19 = var16 * var8 + var4) + var8;

						for (int var21 = var16; var21 <= var18; var20 += var8) {
							int var22;
							int var23 = (var22 = var15 * var7 + var3) + var7;

							for (int var24 = var15; var24 <= var17; var23 += var7) {
								int var25;
								if ((var25 = var1.getCell(var24, var21)) != 0) {
									int var26 = var9 < var22 ? var22 : var9;
									int var27 = var10 < var19 ? var19 : var10;
									int var28 = var11 < var23 ? var11 : var23;
									int var29 = var12 < var20 ? var12 : var20;
									int var30;
									if (var26 > var28) {
										var30 = var28;
										var28 = var26;
										var26 = var30;
									}

									if (var27 > var29) {
										var30 = var29;
										var29 = var27;
										var27 = var30;
									}

									var30 = var28 - var26;
									int var31 = var29 - var27;
									int var32 = this.method205(var26, var27, var28, var29);
									int var33 = this.method207(var26, var27, var28, var29);
									int var34 = var1.anIntArray266[var25] + (var26 - var22);
									int var35 = var1.anIntArray267[var25] + (var27 - var19);
									if (method204(var32, var33, var34, var35, this.image, this.transform, var1.anImage265, 0, var30, var31)) {
										return true;
									}
								}

								++var24;
								var22 += var7;
							}

							++var21;
							var19 += var8;
						}

						return false;
					}
				}
			}
		} else {
			return false;
		}
	}

	public final boolean collidesWith(Image var1, int var2, int var3, boolean var4) {
		if (!super.visible) {
			return false;
		} else {
			int var7 = var2 + var1.getWidth();
			int var8 = var3 + var1.getHeight();
			int var9 = super.x + this.anInt412;
			int var10 = super.y + this.anInt413;
			int var11 = var9 + this.anInt414;
			int var12 = var10 + this.anInt415;
			if (method203(var2, var3, var7, var8, var9, var10, var11, var12)) {
				if (var4) {
					if (this.anInt412 < 0) {
						var9 = super.x;
					}

					if (this.anInt413 < 0) {
						var10 = super.y;
					}

					if (this.anInt412 + this.anInt414 > super.width) {
						var11 = super.x + super.width;
					}

					if (this.anInt413 + this.anInt415 > super.height) {
						var12 = super.y + super.height;
					}

					if (!method203(var2, var3, var7, var8, var9, var10, var11, var12)) {
						return false;
					} else {
						int var13 = var9 < var2 ? var2 : var9;
						int var14 = var10 < var3 ? var3 : var10;
						int var15 = var11 < var7 ? var11 : var7;
						int var16 = var12 < var8 ? var12 : var8;
						int var17 = Math.abs(var15 - var13);
						int var18 = Math.abs(var16 - var14);
						int var19 = this.method205(var13, var14, var15, var16);
						int var20 = this.method207(var13, var14, var15, var16);
						int var21 = var13 - var2;
						int var22 = var14 - var3;
						return method204(var19, var20, var21, var22, this.image, this.transform, var1, 0, var17, var18);
					}
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
	}

	private void method201(Image var1, int var2, int var3, boolean var4) {
		int var5 = var1.getWidth();
		int var6 = var1.getHeight();
		int var7 = var5 / var2;
		int var8 = var6 / var3;
		this.image = var1;
		this.frameWidth = var2;
		this.frameHeight = var3;
		this.rawFrameCount = var7 * var8;
		this.anIntArray266 = new int[this.rawFrameCount];
		this.anIntArray267 = new int[this.rawFrameCount];
		if (!var4) {
			this.frame = 0;
		}

		if (!this.aBoolean407) {
			this.frameSequence = new int[this.rawFrameCount];
		}

		int var9 = 0;
		int var10000 = 0;

		while (true) {
			int var10 = var10000;
			if (var10000 >= var6) {
				return;
			}

			var10000 = 0;

			while (true) {
				int var11 = var10000;
				if (var10000 >= var5) {
					var10000 = var10 + var3;
					break;
				}

				this.anIntArray266[var9] = var11;
				this.anIntArray267[var9] = var10;
				if (!this.aBoolean407) {
					this.frameSequence[var9] = var9;
				}

				++var9;
				var10000 = var11 + var2;
			}
		}
	}

	private void method202() {
		this.collisionX = 0;
		this.collisionY = 0;
		this.collisionW = super.width;
		this.collisionH = super.height;
	}

	private static boolean method203(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
		return var4 < var2 && var5 < var3 && var6 > var0 && var7 > var1;
	}

	private static boolean method204(int var0, int var1, int var2, int var3, Image var4, int var5, Image var6, int var7, int var8, int var9) {
		int var10;
		int[] var11 = new int[var10 = var9 * var8];
		int[] var12 = new int[var10];
		int var13;
		int var14;
		int var15;
		int var10000;
		int[] var10001;
		byte var10002;
		int var10003;
		int var10004;
		int var10005;
		int var10006;
		int var10007;
		Image var25;
		if (0 != (var5 & 4)) {
			if (0 != (var5 & 1)) {
				var13 = -var9;
				var10000 = var10 - var9;
			} else {
				var13 = var9;
				var10000 = 0;
			}

			var14 = var10000;
			if (0 != (var5 & 2)) {
				var15 = -1;
				var14 += var9 - 1;
			} else {
				var15 = 1;
			}

			var25 = var4;
			var10001 = var11;
			var10002 = 0;
			var10003 = var9;
			var10004 = var0;
			var10005 = var1;
			var10006 = var9;
			var10007 = var8;
		} else {
			if (0 != (var5 & 1)) {
				var14 = var10 - var8;
				var10000 = -var8;
			} else {
				var14 = 0;
				var10000 = var8;
			}

			var15 = var10000;
			if (0 != (var5 & 2)) {
				var13 = -1;
				var14 += var8 - 1;
			} else {
				var13 = 1;
			}

			var25 = var4;
			var10001 = var11;
			var10002 = 0;
			var10003 = var8;
			var10004 = var0;
			var10005 = var1;
			var10006 = var8;
			var10007 = var9;
		}

		var25.getRGB(var10001, var10002, var10003, var10004, var10005, var10006, var10007);
		int var16;
		int var17;
		int var18;
		if (0 != (var7 & 4)) {
			if (0 != (var7 & 1)) {
				var16 = -var9;
				var10000 = var10 - var9;
			} else {
				var16 = var9;
				var10000 = 0;
			}

			var17 = var10000;
			if (0 != (var7 & 2)) {
				var18 = -1;
				var17 += var9 - 1;
			} else {
				var18 = 1;
			}

			var25 = var6;
			var10001 = var12;
			var10002 = 0;
			var10003 = var9;
			var10004 = var2;
			var10005 = var3;
			var10006 = var9;
			var10007 = var8;
		} else {
			if (0 != (var7 & 1)) {
				var17 = var10 - var8;
				var10000 = -var8;
			} else {
				var17 = 0;
				var10000 = var8;
			}

			var18 = var10000;
			if (0 != (var7 & 2)) {
				var16 = -1;
				var17 += var8 - 1;
			} else {
				var16 = 1;
			}

			var25 = var6;
			var10001 = var12;
			var10002 = 0;
			var10003 = var8;
			var10004 = var2;
			var10005 = var3;
			var10006 = var8;
			var10007 = var9;
		}

		var25.getRGB(var10001, var10002, var10003, var10004, var10005, var10006, var10007);
		int var19 = 0;
		int var20 = var14;

		for (int var21 = var17; var19 < var9; ++var19) {
			int var22 = 0;
			int var23 = var20;

			for (int var24 = var21; var22 < var8; ++var22) {
				if ((var11[var23] & -16777216) != 0 && (var12[var24] & -16777216) != 0) {
					return true;
				}

				var23 += var13;
				var24 += var16;
			}

			var20 += var15;
			var21 += var18;
		}

		return false;
	}

	private int method205(int var1, int var2, int var3, int var4) {
		int var5 = 0;
		int var10000;
		int var10001;
		switch (this.transform) {
			case 0:
			case 1:
				var10000 = var1;
				var10001 = super.x;
				break;
			case 2:
			case 3:
				var10000 = super.x + super.width;
				var10001 = var3;
				break;
			case 4:
			case 5:
				var10000 = var2;
				var10001 = super.y;
				break;
			case 6:
			case 7:
				var10000 = super.y + super.height;
				var10001 = var4;
				break;
			default:
				return var5 + this.anIntArray266[this.frameSequence[this.frame]];
		}

		var5 = var10000 - var10001;
		return var5 + this.anIntArray266[this.frameSequence[this.frame]];
	}

	private int method207(int var1, int var2, int var3, int var4) {
		int var5 = 0;
		int var10000;
		int var10001;
		switch (this.transform) {
			case 0:
			case 2:
				var10000 = var2;
				var10001 = super.y;
				break;
			case 1:
			case 3:
				var10000 = super.y + super.height;
				var10001 = var4;
				break;
			case 4:
			case 6:
				var10000 = var1;
				var10001 = super.x;
				break;
			case 5:
			case 7:
				var10000 = super.x + super.width;
				var10001 = var3;
				break;
			default:
				return var5 + this.anIntArray267[this.frameSequence[this.frame]];
		}

		var5 = var10000 - var10001;
		return var5 + this.anIntArray267[this.frameSequence[this.frame]];
	}

	private void method209(int var1) {
		super.x = super.x + this.method206(this.refX, this.refY, this.transform) - this.method206(this.refX, this.refY, var1);
		super.y = super.y + this.method208(this.refX, this.refY, this.transform) - this.method208(this.refX, this.refY, var1);
		this.method210(var1);
		this.transform = var1;
	}

	private void method210(int var1) {
		switch (var1) {
			case 0:
				this.anInt412 = this.collisionX;
				this.anInt413 = this.collisionY;
				this.anInt414 = this.collisionW;
				this.anInt415 = this.collisionH;
				super.width = this.frameWidth;
				super.height = this.frameHeight;
				return;
			case 1:
				this.anInt413 = this.frameHeight - (this.collisionY + this.collisionH);
				this.anInt412 = this.collisionX;
				this.anInt414 = this.collisionW;
				this.anInt415 = this.collisionH;
				super.width = this.frameWidth;
				super.height = this.frameHeight;
				return;
			case 2:
				this.anInt412 = this.frameWidth - (this.collisionX + this.collisionW);
				this.anInt413 = this.collisionY;
				this.anInt414 = this.collisionW;
				this.anInt415 = this.collisionH;
				super.width = this.frameWidth;
				super.height = this.frameHeight;
				return;
			case 3:
				this.anInt412 = this.frameWidth - (this.collisionW + this.collisionX);
				this.anInt413 = this.frameHeight - (this.collisionH + this.collisionY);
				this.anInt414 = this.collisionW;
				this.anInt415 = this.collisionH;
				super.width = this.frameWidth;
				super.height = this.frameHeight;
				return;
			case 4:
				this.anInt413 = this.collisionX;
				this.anInt412 = this.collisionY;
				this.anInt415 = this.collisionW;
				this.anInt414 = this.collisionH;
				super.width = this.frameHeight;
				super.height = this.frameWidth;
				return;
			case 5:
				this.anInt412 = this.frameHeight - (this.collisionH + this.collisionY);
				this.anInt413 = this.collisionX;
				this.anInt415 = this.collisionW;
				this.anInt414 = this.collisionH;
				super.width = this.frameHeight;
				super.height = this.frameWidth;
				return;
			case 6:
				this.anInt412 = this.collisionY;
				this.anInt413 = this.frameWidth - (this.collisionW + this.collisionX);
				this.anInt415 = this.collisionW;
				this.anInt414 = this.collisionH;
				super.width = this.frameHeight;
				super.height = this.frameWidth;
				return;
			case 7:
				this.anInt412 = this.frameHeight - (this.collisionH + this.collisionY);
				this.anInt413 = this.frameWidth - (this.collisionW + this.collisionX);
				this.anInt415 = this.collisionW;
				this.anInt414 = this.collisionH;
				super.width = this.frameHeight;
				super.height = this.frameWidth;
				return;
			default:
				throw new IllegalArgumentException();
		}
	}

	final int method206(int var1, int var2, int var3) {
		boolean var4 = false;
		int var5;
		switch (var3) {
			case 0:
				var5 = var1;
				break;
			case 1:
				var5 = var1;
				break;
			case 2:
				var5 = this.frameWidth - var1 - 1;
				break;
			case 3:
				var5 = this.frameWidth - var1 - 1;
				break;
			case 4:
				var5 = var2;
				break;
			case 5:
				var5 = this.frameHeight - var2 - 1;
				break;
			case 6:
				var5 = var2;
				break;
			case 7:
				var5 = this.frameHeight - var2 - 1;
				break;
			default:
				throw new IllegalArgumentException();
		}

		return var5;
	}

	final int method208(int var1, int var2, int var3) {
		boolean var4 = false;
		int var5;
		switch (var3) {
			case 0:
				var5 = var2;
				break;
			case 1:
				var5 = this.frameHeight - var2 - 1;
				break;
			case 2:
				var5 = var2;
				break;
			case 3:
				var5 = this.frameHeight - var2 - 1;
				break;
			case 4:
				var5 = var1;
				break;
			case 5:
				var5 = var1;
				break;
			case 6:
				var5 = this.frameWidth - var1 - 1;
				break;
			case 7:
				var5 = this.frameWidth - var1 - 1;
				break;
			default:
				throw new IllegalArgumentException();
		}

		return var5;
	}
}
