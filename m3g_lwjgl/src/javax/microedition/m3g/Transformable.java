package javax.microedition.m3g;

import emulator.graphics3D.Quaternion;

public abstract class Transformable extends Object3D {
    float[] scale = new float[3];
    float[] translation = new float[3];
    Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
    Transform transform = new Transform();

    Transformable() {
        this.scale[0] = this.scale[1] = this.scale[2] = 1.0F;
    }

    public void setOrientation(float var1, float var2, float var3, float var4) {
        if (var1 != 0.0F && var2 == 0.0F && var3 == 0.0F && var4 == 0.0F) {
            throw new IllegalArgumentException();
        } else {
            this.rotation.setAngleAxis(var1, var2, var3, var4);
        }
    }

    public void getOrientation(float[] var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else if (var1.length < 4) {
            throw new IllegalArgumentException();
        } else {
            this.rotation.getAngleAxis(var1);
        }
    }

    public void preRotate(float var1, float var2, float var3, float var4) {
        if (var1 != 0.0F && var2 == 0.0F && var3 == 0.0F && var4 == 0.0F) {
            throw new IllegalArgumentException();
        } else {
            Quaternion var5;
            (var5 = new Quaternion()).setAngleAxis(var1, var2, var3, var4);
            var5.mul(this.rotation);
            this.rotation.set(var5);
        }
    }

    public void postRotate(float var1, float var2, float var3, float var4) {
        if (var1 != 0.0F && var2 == 0.0F && var3 == 0.0F && var4 == 0.0F) {
            throw new IllegalArgumentException();
        } else {
            Quaternion var5;
            (var5 = new Quaternion()).setAngleAxis(var1, var2, var3, var4);
            this.rotation.mul(var5);
        }
    }

    public void setScale(float var1, float var2, float var3) {
        this.scale[0] = var1;
        this.scale[1] = var2;
        this.scale[2] = var3;
    }

    public void scale(float var1, float var2, float var3) {
        this.scale[0] *= var1;
        this.scale[1] *= var2;
        this.scale[2] *= var3;
    }

    public void getScale(float[] var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else if (var1.length < 3) {
            throw new IllegalArgumentException();
        } else {
            System.arraycopy(this.scale, 0, var1, 0, 3);
        }
    }

    public void setTranslation(float var1, float var2, float var3) {
        this.translation[0] = var1;
        this.translation[1] = var2;
        this.translation[2] = var3;
    }

    public void translate(float var1, float var2, float var3) {
        this.translation[0] += var1;
        this.translation[1] += var2;
        this.translation[2] += var3;
    }

    public void getTranslation(float[] var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else if (var1.length < 3) {
            throw new IllegalArgumentException();
        } else {
            System.arraycopy(this.translation, 0, var1, 0, 3);
        }
    }

    public void setTransform(Transform var1) {
        if (var1 == null) {
            this.transform.setIdentity();
        } else {
            this.transform.set(var1);
        }
    }

    public void getTransform(Transform var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else {
            var1.set(this.transform);
        }
    }

    public void getCompositeTransform(Transform var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else {
            var1.setIdentity();
            var1.postTranslate(translation[0], translation[1], translation[2]);
            var1.postRotateQuat(rotation.x, rotation.y, rotation.z, rotation.w);
            var1.postScale(scale[0], scale[1], scale[2]);
            var1.postMultiply(transform);
        }
    }

    protected void updateProperty(int property, float[] values) {
        switch (property) {
            case AnimationTrack.ORIENTATION:
                rotation.set(values);
                rotation.normalize();
                return;
            case AnimationTrack.SCALE:
                if (values.length == 1) {
                    scale[0] = scale[1] = scale[2] = values[0];
                } else {
                    scale[0] = values[0];
                    scale[1] = values[1];
                    scale[2] = values[2];
                }
                return;
            case AnimationTrack.TRANSLATION:
                translation[0] = values[0];
                translation[1] = values[1];
                translation[2] = values[2];
                return;
            default:
                super.updateProperty(property, values);
        }
    }

    protected Object3D duplicateObject() {
        Transformable var1;
        (var1 = (Transformable) super.duplicateObject()).rotation = new Quaternion(this.rotation);
        var1.transform = new Transform(this.transform);
        var1.translation = (float[]) this.translation.clone();
        var1.scale = (float[]) this.scale.clone();
        return var1;
    }
}
