package com.nttdocomo.ui.ogl.math;

public abstract class Tuple3f {
    public float x;
    public float y;
    public float z;

    public Tuple3f() {
    }

    public Tuple3f(float paramFloat1, float paramFloat2, float paramFloat3) {
        this.x = paramFloat1;
        this.y = paramFloat2;
        this.z = paramFloat3;
    }

    public void add(Tuple3f t) {
        x += t.x;
        y += t.y;
        z += t.z;
    }
}
