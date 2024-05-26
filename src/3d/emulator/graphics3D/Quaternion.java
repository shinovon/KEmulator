package emulator.graphics3D;

public final class Quaternion {
	public float x;
	public float y;
	public float z;
	public float w;

	public Quaternion() {
	}

	public Quaternion(float var1, float var2, float var3, float var4) {
		this.x = var1;
		this.y = var2;
		this.z = var3;
		this.w = var4;
	}

	public Quaternion(float[] var1) {
		this.set(var1);
	}

	public Quaternion(Quaternion var1) {
		this.set(var1);
	}

	public final void set(float[] var1) {
		if (var1.length != 4) {
			throw new Error("Invalid number of components for quaternion");
		} else {
			this.x = var1[0];
			this.y = var1[1];
			this.z = var1[2];
			this.w = var1[3];
		}
	}

	public final void set(Quaternion var1) {
		this.x = var1.x;
		this.y = var1.y;
		this.z = var1.z;
		this.w = var1.w;
	}

	private void setIdentity() {
		this.x = this.y = this.z = 0.0F;
		this.w = 1.0F;
	}

	public final void normalize() {
		Quaternion var10000;
		float var1;
		float var10001;
		if ((var1 = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w) > 1.0E-5F) {
			float var2 = 1.0F / (float) Math.sqrt((double) var1);
			this.x *= var2;
			this.y *= var2;
			this.z *= var2;
			var10000 = this;
			var10001 = this.w * var2;
		} else {
			this.x = this.y = this.z = 0.0F;
			var10000 = this;
			var10001 = 1.0F;
		}

		var10000.w = var10001;
	}

	public final void setAngleAxis(float var1, float var2, float var3, float var4) {
		Vector4f var5;
		if ((var5 = new Vector4f(var2, var3, var4, 0.0F)).normalize()) {
			float var6;
			float var7 = (float) Math.sin((double) (var6 = (float) Math.toRadians((double) (0.5F * var1))));
			this.x = var7 * var5.x;
			this.y = var7 * var5.y;
			this.z = var7 * var5.z;
			this.w = (float) Math.cos((double) var6);
		} else {
			this.setIdentity();
		}
	}

	public final void getAngleAxis(float[] var1) {
		this.normalize();
		float var2 = 1.0F - this.w * this.w;
		if (var2 > 1.0E-5F) {
			float var3 = (float) Math.sqrt((double) var2);
			var1[1] = this.x / var3;
			var1[2] = this.y / var3;
			var1[3] = this.z / var3;
		} else {
			var1[1] = var1[2] = 0.0F;
			var1[3] = 1.0F;
		}

		var1[0] = (float) Math.toDegrees(Math.acos((double) this.w) * 2.0);
	}

	public final void mul(Quaternion var1) {
		Quaternion var2 = new Quaternion(this);
		this.w = var2.w * var1.w - var2.x * var1.x - var2.y * var1.y - var2.z * var1.z;
		this.x = var2.w * var1.x + var2.x * var1.w + var2.y * var1.z - var2.z * var1.y;
		this.y = var2.w * var1.y - var2.x * var1.z + var2.y * var1.w + var2.z * var1.x;
		this.z = var2.w * var1.z + var2.x * var1.y - var2.y * var1.x + var2.z * var1.w;
	}

	public final void mul(float var1) {
		this.x *= var1;
		this.y *= var1;
		this.z *= var1;
		this.w *= var1;
	}

	public final void add(Quaternion var1) {
		this.x += var1.x;
		this.y += var1.y;
		this.z += var1.z;
		this.w += var1.w;
	}

	public final void sub(Quaternion var1) {
		this.x -= var1.x;
		this.y -= var1.y;
		this.z -= var1.z;
		this.w -= var1.w;
	}

	private void conjugate(Quaternion var1) {
		this.x = -var1.x;
		this.y = -var1.y;
		this.z = -var1.z;
		this.w = var1.w;
	}

	private float dot(Quaternion var1) {
		return this.x * var1.x + this.y * var1.y + this.z * var1.z + this.w * var1.w;
	}

	private void log(Quaternion var1) {
		float var2;
		if ((var2 = (float) Math.sqrt((double) (var1.x * var1.x + var1.y * var1.y + var1.z * var1.z))) > 1.0E-5F) {
			float var3 = (float) (Math.atan2((double) var2, (double) this.w) / (double) var2);
			this.x = var3 * var1.x;
			this.y = var3 * var1.y;
			this.z = var3 * var1.z;
		} else {
			this.x = this.y = this.z = 0.0F;
		}

		this.w = 0.0F;
	}

	public final void exp(Quaternion var1) {
		Quaternion var10000;
		float var10001;
		float var2;
		if ((var2 = (float) Math.sqrt((double) (var1.x * var1.x + var1.y * var1.y + var1.z * var1.z))) > 1.0E-5F) {
			float var3 = (float) Math.sin((double) var2) / var2;
			this.x = var3 * var1.x;
			this.y = var3 * var1.y;
			this.z = var3 * var1.z;
			var10000 = this;
			var10001 = (float) Math.cos((double) var2);
		} else {
			this.x = this.y = this.z = 0.0F;
			var10000 = this;
			var10001 = 1.0F;
		}

		var10000.w = var10001;
	}

	public final void logDiff(Quaternion var1, Quaternion var2) {
		this.set(var1);
		this.conjugate(this);
		this.mul(var2);
		this.log(this);
	}

	public final void slerp(float var1, Quaternion var2, Quaternion var3) {
		float var4;
		float var7;
		float var8;
		if ((var4 = var2.dot(var3)) + 1.0F > 1.0E-5F) {
			float var10000;
			if (1.0F - var4 > 1.0E-5F) {
				float var5;
				float var6 = (float) Math.sin((double) (var5 = (float) Math.acos((double) var4)));
				var7 = (float) Math.sin((double) ((1.0F - var1) * var5)) / var6;
				var10000 = (float) Math.sin((double) (var1 * var5)) / var6;
			} else {
				var7 = 1.0F - var1;
				var10000 = var1;
			}

			var8 = var10000;
			this.x = var7 * var2.x + var8 * var3.x;
			this.y = var7 * var2.y + var8 * var3.y;
			this.z = var7 * var2.z + var8 * var3.z;
			this.w = var7 * var2.w + var8 * var3.w;
		} else {
			this.x = -var2.y;
			this.y = var2.x;
			this.z = -var2.w;
			this.w = var2.z;
			var7 = (float) Math.sin((double) (1.0F - var1) * 3.141592653589793D / 2.0D);
			var8 = (float) Math.sin((double) var1 * 3.141592653589793D / 2.0D);
			this.x = var7 * var2.x + var8 * this.x;
			this.y = var7 * var2.y + var8 * this.y;
			this.z = var7 * var2.z + var8 * this.z;
		}
	}

	public final void squad(float var1, Quaternion var2, Quaternion var3, Quaternion var4, Quaternion var5) {
		Quaternion var6 = new Quaternion();
		Quaternion var7 = new Quaternion();
		var6.slerp(var1, var2, var5);
		var7.slerp(var1, var3, var4);
		this.slerp(2.0F * var1 * (1.0F - var1), var6, var7);
	}

	public final void setRotation(Vector4f var1, Vector4f var2, Vector4f var3) {
		if (var1.w == 0.0F && var2.w == 0.0F) {
			Vector4f var4 = new Vector4f(var1);
			Vector4f var5 = new Vector4f(var2);
			float var7;
			if (var3 != null) {
				Vector4f var6;
				(var6 = new Vector4f(var3)).normalize();
				var4.normalize();
				var7 = var4.dot(var6);
				var6.mul(var7);
				var4.sub(var6);
				var6.set(var3);
				var6.normalize();
				var5.normalize();
				var7 = var5.dot(var6);
				var6.mul(var7);
				var5.sub(var6);
			}

			if (var4.normalize() && var5.normalize()) {
				float var11;
				if ((var11 = var4.dot(var5)) > 0.99999F) {
					this.setIdentity();
				} else {
					Quaternion var12;
					float var10001;
					if (var11 < -0.99999F) {
						if (var3 == null) {
							var3 = new Vector4f();
							var7 = Math.abs(var4.x);
							float var8 = Math.abs(var4.y);
							float var9 = Math.abs(var4.z);
							Vector4f var10000;
							float var10002;
							float var10003;
							if (var7 <= var8 && var7 <= var9) {
								var10000 = var3;
								var10001 = 1.0F;
								var10002 = 0.0F;
								var10003 = 0.0F;
							} else if (var8 <= var7 && var8 <= var9) {
								var10000 = var3;
								var10001 = 0.0F;
								var10002 = 1.0F;
								var10003 = 0.0F;
							} else {
								var10000 = var3;
								var10001 = 0.0F;
								var10002 = 0.0F;
								var10003 = 1.0F;
							}

							var10000.set(var10001, var10002, var10003, 0.0F);
							float var10 = var3.dot(var4);
							var4.mul(var10);
							var3.sub(var4);
						}

						var12 = this;
						var10001 = 180.0F;
					} else {
						(var3 = new Vector4f()).cross(var4, var5);
						var12 = this;
						var10001 = (float) Math.toDegrees(Math.acos((double) var11));
					}

					var12.setAngleAxis(var10001, var3.x, var3.y, var3.z);
				}
			} else {
				this.setIdentity();
			}
		} else {
			throw new Error();
		}
	}
}
