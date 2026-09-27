package emulator.graphics3D;

public final class Quaternion {
	public float x;
	public float y;
	public float z;
	public float w;

	public Quaternion() {
	}

	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Quaternion(float[] xyzw) {
		this.set(xyzw);
	}

	public Quaternion(Quaternion other) {
		this.set(other);
	}

	public final void set(float[] xyzw) {
		if (xyzw.length != 4) {
			throw new Error("Invalid number of components for quaternion");
		} else {
			this.x = xyzw[0];
			this.y = xyzw[1];
			this.z = xyzw[2];
			this.w = xyzw[3];
		}
	}

	public final void set(Quaternion other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		this.w = other.w;
	}

	private void setIdentity() {
		this.x = this.y = this.z = 0.0F;
		this.w = 1.0F;
	}

	public final void normalize() {
		Quaternion target;
		float lengthSquared;
		float newW;
		if ((lengthSquared = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w) > 1.0E-5F) {
			float invLength = 1.0F / (float) Math.sqrt((double) lengthSquared);
			this.x *= invLength;
			this.y *= invLength;
			this.z *= invLength;
			target = this;
			newW = this.w * invLength;
		} else {
			this.x = this.y = this.z = 0.0F;
			target = this;
			newW = 1.0F;
		}

		target.w = newW;
	}

	public final void setAngleAxis(float angle, float ax, float ay, float az) {
		Vector4f axis;
		if ((axis = new Vector4f(ax, ay, az, 0.0F)).normalize()) {
			float halfAngle;
			float sinHalfAngle = (float) Math.sin((double) (halfAngle = (float) Math.toRadians((double) (0.5F * angle))));
			this.x = sinHalfAngle * axis.x;
			this.y = sinHalfAngle * axis.y;
			this.z = sinHalfAngle * axis.z;
			this.w = (float) Math.cos((double) halfAngle);
		} else {
			this.setIdentity();
		}
	}

	public final void getAngleAxis(float[] angleAxis) {
		this.normalize();
		float sinSquared = 1.0F - this.w * this.w;
		if (sinSquared > 1.0E-5F) {
			float sinHalfAngle = (float) Math.sqrt((double) sinSquared);
			angleAxis[1] = this.x / sinHalfAngle;
			angleAxis[2] = this.y / sinHalfAngle;
			angleAxis[3] = this.z / sinHalfAngle;
		} else {
			angleAxis[1] = angleAxis[2] = 0.0F;
			angleAxis[3] = 1.0F;
		}

		angleAxis[0] = (float) Math.toDegrees(Math.acos((double) this.w) * 2.0);
	}

	public final void mul(Quaternion other) {
		Quaternion left = new Quaternion(this);
		this.w = left.w * other.w - left.x * other.x - left.y * other.y - left.z * other.z;
		this.x = left.w * other.x + left.x * other.w + left.y * other.z - left.z * other.y;
		this.y = left.w * other.y - left.x * other.z + left.y * other.w + left.z * other.x;
		this.z = left.w * other.z + left.x * other.y - left.y * other.x + left.z * other.w;
	}

	public final void mul(float scale) {
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;
		this.w *= scale;
	}

	public final void add(Quaternion other) {
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
		this.w += other.w;
	}

	public final void sub(Quaternion other) {
		this.x -= other.x;
		this.y -= other.y;
		this.z -= other.z;
		this.w -= other.w;
	}

	private void conjugate(Quaternion other) {
		this.x = -other.x;
		this.y = -other.y;
		this.z = -other.z;
		this.w = other.w;
	}

	private float dot(Quaternion other) {
		return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
	}

	private void log(Quaternion q) {
		float vectorLength;
		if ((vectorLength = (float) Math.sqrt((double) (q.x * q.x + q.y * q.y + q.z * q.z))) > 1.0E-5F) {
			float scale = (float) (Math.atan2((double) vectorLength, (double) this.w) / (double) vectorLength);
			this.x = scale * q.x;
			this.y = scale * q.y;
			this.z = scale * q.z;
		} else {
			this.x = this.y = this.z = 0.0F;
		}

		this.w = 0.0F;
	}

	public final void exp(Quaternion q) {
		Quaternion target;
		float newW;
		float angle;
		if ((angle = (float) Math.sqrt((double) (q.x * q.x + q.y * q.y + q.z * q.z))) > 1.0E-5F) {
			float scale = (float) Math.sin((double) angle) / angle;
			this.x = scale * q.x;
			this.y = scale * q.y;
			this.z = scale * q.z;
			target = this;
			newW = (float) Math.cos((double) angle);
		} else {
			this.x = this.y = this.z = 0.0F;
			target = this;
			newW = 1.0F;
		}

		target.w = newW;
	}

	public final void logDiff(Quaternion from, Quaternion to) {
		this.set(from);
		this.conjugate(this);
		this.mul(to);
		this.log(this);
	}

	public final void slerp(float t, Quaternion from, Quaternion to) {
		float cosOmega;
		float fromWeight;
		float toWeight;
		if ((cosOmega = from.dot(to)) + 1.0F > 1.0E-5F) {
			float toWeightValue;
			if (1.0F - cosOmega > 1.0E-5F) {
				float omega;
				float sinOmega = (float) Math.sin((double) (omega = (float) Math.acos((double) cosOmega)));
				fromWeight = (float) Math.sin((double) ((1.0F - t) * omega)) / sinOmega;
				toWeightValue = (float) Math.sin((double) (t * omega)) / sinOmega;
			} else {
				fromWeight = 1.0F - t;
				toWeightValue = t;
			}

			toWeight = toWeightValue;
			this.x = fromWeight * from.x + toWeight * to.x;
			this.y = fromWeight * from.y + toWeight * to.y;
			this.z = fromWeight * from.z + toWeight * to.z;
			this.w = fromWeight * from.w + toWeight * to.w;
		} else {
			this.x = -from.y;
			this.y = from.x;
			this.z = -from.w;
			this.w = from.z;
			fromWeight = (float) Math.sin((double) (1.0F - t) * 3.141592653589793D / 2.0D);
			toWeight = (float) Math.sin((double) t * 3.141592653589793D / 2.0D);
			this.x = fromWeight * from.x + toWeight * this.x;
			this.y = fromWeight * from.y + toWeight * this.y;
			this.z = fromWeight * from.z + toWeight * this.z;
		}
	}

	public final void squad(float t, Quaternion q0, Quaternion controlA, Quaternion controlB, Quaternion q1) {
		Quaternion outer = new Quaternion();
		Quaternion inner = new Quaternion();
		outer.slerp(t, q0, q1);
		inner.slerp(t, controlA, controlB);
		this.slerp(2.0F * t * (1.0F - t), outer, inner);
	}

	public final void setRotation(Vector4f from, Vector4f to, Vector4f upAxis) {
		if (from.w == 0.0F && to.w == 0.0F) {
			Vector4f fromDir = new Vector4f(from);
			Vector4f toDir = new Vector4f(to);
			float scalar;
			if (upAxis != null) {
				Vector4f upDir;
				(upDir = new Vector4f(upAxis)).normalize();
				fromDir.normalize();
				scalar = fromDir.dot(upDir);
				upDir.mul(scalar);
				fromDir.sub(upDir);
				upDir.set(upAxis);
				upDir.normalize();
				toDir.normalize();
				scalar = toDir.dot(upDir);
				upDir.mul(scalar);
				toDir.sub(upDir);
			}

			if (fromDir.normalize() && toDir.normalize()) {
				float cosAngle;
				if ((cosAngle = fromDir.dot(toDir)) > 0.99999F) {
					this.setIdentity();
				} else {
					Quaternion target;
					float angleOrAxisX;
					if (cosAngle < -0.99999F) {
						if (upAxis == null) {
							upAxis = new Vector4f();
							scalar = Math.abs(fromDir.x);
							float absY = Math.abs(fromDir.y);
							float absZ = Math.abs(fromDir.z);
							Vector4f axisTarget;
							float axisY;
							float axisZ;
							if (scalar <= absY && scalar <= absZ) {
								axisTarget = upAxis;
								angleOrAxisX = 1.0F;
								axisY = 0.0F;
								axisZ = 0.0F;
							} else if (absY <= scalar && absY <= absZ) {
								axisTarget = upAxis;
								angleOrAxisX = 0.0F;
								axisY = 1.0F;
								axisZ = 0.0F;
							} else {
								axisTarget = upAxis;
								angleOrAxisX = 0.0F;
								axisY = 0.0F;
								axisZ = 1.0F;
							}

							axisTarget.set(angleOrAxisX, axisY, axisZ, 0.0F);
							float upProjection = upAxis.dot(fromDir);
							fromDir.mul(upProjection);
							upAxis.sub(fromDir);
						}

						target = this;
						angleOrAxisX = 180.0F;
					} else {
						(upAxis = new Vector4f()).cross(fromDir, toDir);
						target = this;
						angleOrAxisX = (float) Math.toDegrees(Math.acos((double) cosAngle));
					}

					target.setAngleAxis(angleOrAxisX, upAxis.x, upAxis.y, upAxis.z);
				}
			} else {
				this.setIdentity();
			}
		} else {
			throw new Error();
		}
	}
}
