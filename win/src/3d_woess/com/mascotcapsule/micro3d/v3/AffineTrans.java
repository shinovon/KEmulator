/*
 * Copyright (c) 2020. Yury Kharchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.mascotcapsule.micro3d.v3;

public class AffineTrans {
    public int m00;
    public int m01;
    public int m02;
    public int m03;
    public int m10;
    public int m11;
    public int m12;
    public int m13;
    public int m20;
    public int m21;
    public int m22;
    public int m23;

    public AffineTrans(int m00, int m01, int m02, int m03, int m10, int m11, int m12, int m13, int m20, int m21,
                       int m22, int m23) {
        set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23);
    }

    public AffineTrans(AffineTrans a) {
        set(a);
    }

    public AffineTrans(int[][] a) {
        set(a);
    }

    public AffineTrans(int[] a) {
        set(a);
    }

    public AffineTrans(int[] a, int offset) {
        set(a, offset);
    }

    public final void setRotationX(int r) {
        nSetRotationX(r);
    }

    public final void setRotationY(int r) {
        nSetRotationY(r);
    }

    public final void setRotationZ(int r) {
        nSetRotationZ(r);
    }

    public final void setIdentity() {
        nSetIdentity();
    }

    public final void get(int[] a) {
        get(a, 0);
    }

    public final void get(int[] a, int offset) {
        if (a == null) {
            throw new NullPointerException();
        }
        if ((offset < 0) || (a.length - offset < 12)) {
            throw new IllegalArgumentException();
        }
        a[(offset++)] = this.m00;
        a[(offset++)] = this.m01;
        a[(offset++)] = this.m02;
        a[(offset++)] = this.m03;
        a[(offset++)] = this.m10;
        a[(offset++)] = this.m11;
        a[(offset++)] = this.m12;
        a[(offset++)] = this.m13;
        a[(offset++)] = this.m20;
        a[(offset++)] = this.m21;
        a[(offset++)] = this.m22;
        a[offset] = this.m23;
    }

    public final void set(int[] a, int offset) {
        if (a == null) {
            throw new NullPointerException();
        }
        if ((offset < 0) || (a.length - offset < 12)) {
            throw new IllegalArgumentException();
        }
        this.m00 = a[(offset++)];
        this.m01 = a[(offset++)];
        this.m02 = a[(offset++)];
        this.m03 = a[(offset++)];
        this.m10 = a[(offset++)];
        this.m11 = a[(offset++)];
        this.m12 = a[(offset++)];
        this.m13 = a[(offset++)];
        this.m20 = a[(offset++)];
        this.m21 = a[(offset++)];
        this.m22 = a[(offset++)];
        this.m23 = a[offset];
    }

    public final void set(int m00, int m01, int m02, int m03, int m10, int m11, int m12, int m13, int m20, int m21,
                          int m22, int m23) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
    }

    public final void set(AffineTrans a) {
        if (a == null) {
            throw new NullPointerException();
        }
        this.m00 = a.m00;
        this.m01 = a.m01;
        this.m02 = a.m02;
        this.m03 = a.m03;
        this.m10 = a.m10;
        this.m11 = a.m11;
        this.m12 = a.m12;
        this.m13 = a.m13;
        this.m20 = a.m20;
        this.m21 = a.m21;
        this.m22 = a.m22;
        this.m23 = a.m23;
    }

    public final void set(int[][] a) {
        if (a == null) {
            throw new NullPointerException();
        }
        if (a.length < 3) {
            throw new IllegalArgumentException();
        }
        int[] a0 = a[0];
        int[] a1 = a[1];
        int[] a2 = a[2];
        if ((a0.length < 4) || (a1.length < 4) || (a2.length < 4)) {
            throw new IllegalArgumentException();
        }
        this.m00 = a0[0];
        this.m01 = a0[1];
        this.m02 = a0[2];
        this.m03 = a0[3];
        this.m10 = a1[0];
        this.m11 = a1[1];
        this.m12 = a1[2];
        this.m13 = a1[3];
        this.m20 = a2[0];
        this.m21 = a2[1];
        this.m22 = a2[2];
        this.m23 = a2[3];
    }

    public final void set(int[] a) {
        set(a, 0);
    }

    public final Vector3D transPoint(Vector3D v) {
        return transform(v);
    }

    public final Vector3D transform(Vector3D v) {
        if (v == null) {
            throw new NullPointerException();
        }
        Vector3D d = new Vector3D();
        nTransform(v, d);
        return d;
    }

    public final void rotationX(int r) {
        nSetRotationX(r);
    }

    public final void rotationY(int r) {
        nSetRotationY(r);
    }

    public final void rotationZ(int r) {
        nSetRotationZ(r);
    }

    public final void multiply(AffineTrans a) {
        mul(a);
    }

    public final void mul(AffineTrans a) {
        if (a == null) {
            throw new NullPointerException();
        }
        nMultiply(a);
    }

    public final void multiply(AffineTrans a1, AffineTrans a2) {
        mul(a1, a2);
    }

    public final void mul(AffineTrans a1, AffineTrans a2) {
        if ((a1 == null) || (a2 == null)) {
            throw new NullPointerException();
        }
        nMultiply2(a1, a2);
    }

    public final void rotationV(Vector3D v, int r) {
        if (v == null) {
            throw new NullPointerException();
        }
        nSetRotationV(v, r);
    }

    public final void setRotation(Vector3D v, int r) {
        rotationV(v, r);
    }

    public final void setViewTrans(Vector3D pos, Vector3D look, Vector3D up) {
        lookAt(pos, look, up);
    }

    public final void lookAt(Vector3D pos, Vector3D look, Vector3D up) {
        if ((pos == null) || (look == null) || (up == null)) {
            throw new NullPointerException();
        }
        nSetViewTtans(pos, look, up);
    }

    static {
        System.loadLibrary("java_micro3d_v3_32");
        nInitClass();
    }

    public AffineTrans() {
    }

    private native void nTransform(Vector3D paramVector3D1, Vector3D paramVector3D2);

    private native void nMultiply(AffineTrans paramAffineTrans);

    private native void nMultiply2(AffineTrans paramAffineTrans1, AffineTrans paramAffineTrans2);

    private native void nSetRotationX(int paramInt);

    private native void nSetRotationY(int paramInt);

    private native void nSetRotationZ(int paramInt);

    private native void nSetIdentity();

    private native void nSetRotationV(Vector3D paramVector3D, int paramInt);

    private native void nSetViewTtans(Vector3D paramVector3D1, Vector3D paramVector3D2, Vector3D paramVector3D3);

    private static native void nInitClass();
}
