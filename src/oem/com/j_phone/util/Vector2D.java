package com.j_phone.util;

public class Vector2D {
    private int x;
    private int y;

    public Vector2D(FixedPoint x, FixedPoint y) {
        this.x = x.n;
        this.y = y.n;
    }

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D() {
    }

    public void add(Vector2D paramVector2D) {
        add(paramVector2D.x, paramVector2D.y);
    }

    public void add(int x, int y) {
        this.x = FixedPoint.add(this.x, x);
        this.y = FixedPoint.add(this.y, y);
    }

    public void subtract(Vector2D paramVector2D) {
        subtract(paramVector2D.x, paramVector2D.y);
    }

    public void subtract(int x, int y) {
        this.x = FixedPoint.subtract(this.x, x);
        this.y = FixedPoint.subtract(this.y, y);
    }

    public void normalize() {
        double x = FixedPoint.toDouble(this.x);
        double y = FixedPoint.toDouble(this.y);
        double len = Math.sqrt(x*x+y*y);
        x /= len;
        y /= len;
        this.x = FixedPoint.fromDouble(x);
        this.y = FixedPoint.fromDouble(y);
    }

    public static FixedPoint innerProduct(Vector2D paramVector2D1, Vector2D paramVector2D2) {
        // TODO
        return null;
    }

    public static FixedPoint outerProduct(Vector2D paramVector2D1, Vector2D paramVector2D2) {
        // TODO
        return null;
    }

    public void setValue(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setValue(FixedPoint x, FixedPoint y) {
        this.x = x.n;
        this.y = y.n;
    }

    public FixedPoint getX() {
        return new FixedPoint(x);
    }

    public FixedPoint getY() {
        return new FixedPoint(y);
    }

    public Vector2D clone() {
        return new Vector2D(x, y);
    }
}
