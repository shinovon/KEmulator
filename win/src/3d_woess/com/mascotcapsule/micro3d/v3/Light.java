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

public class Light {
    private int mAmbIntensity;
    private int mDirIntensity;
    private Vector3D mDirVector;

    public Light() {
        this.mDirVector = new Vector3D(0, 0, 4096);
        this.mDirIntensity = 4096;
        this.mAmbIntensity = 0;
    }

    public Light(Vector3D dir, int dirIntensity, int ambIntensity) {
        if (dir == null) {
            throw new NullPointerException();
        }
        this.mDirVector = dir;
        this.mDirIntensity = dirIntensity;
        this.mAmbIntensity = ambIntensity;
    }

    public final int getAmbientIntensity() {
        return this.mAmbIntensity;
    }

    public final int getAmbIntensity() {
        return this.mAmbIntensity;
    }

    public final Vector3D getDirection() {
        return this.mDirVector;
    }

    public final int getDirIntensity() {
        return this.mDirIntensity;
    }

    public final Vector3D getParallelLightDirection() {
        return this.mDirVector;
    }

    public final int getParallelLightIntensity() {
        return this.mDirIntensity;
    }

    public final void setAmbientIntensity(int p) {
        this.mAmbIntensity = p;
    }

    public final void setAmbIntensity(int p) {
        this.mAmbIntensity = p;
    }

    public final void setDirection(Vector3D v) {
        setParallelLightDirection(v);
    }

    public final void setDirIntensity(int p) {
        setParallelLightIntensity(p);
    }

    public final void setParallelLightDirection(Vector3D v) {
        if (v == null) {
            throw new NullPointerException();
        }
        this.mDirVector = v;
    }

    public final void setParallelLightIntensity(int p) {
        this.mDirIntensity = p;
    }

    static {
        System.loadLibrary("java_micro3d_v3_32");
        nInitClass();
    }

    private static native void nInitClass();
}
