package javax.microedition.m3g;

public class Sprite3D
        extends Node {
    public Sprite3D(boolean paramBoolean, Image2D paramImage2D, Appearance paramAppearance) {
        super(2);
    }

    public boolean isScaled() {
        return true;
    }

    public void setAppearance(Appearance paramAppearance) {
    }

    public Appearance getAppearance() {
        return null;
    }

    public void setImage(Image2D paramImage2D) {
    }

    public Image2D getImage() {
        return null;
    }

    public void setCrop(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    }

    public int getCropX() {
        return 1;
    }

    public int getCropY() {
        return 1;
    }

    public int getCropWidth() {
        return 1;
    }

    public int getCropHeight() {
        return 1;
    }

    Sprite3D(int paramInt) {
        super(paramInt);
    }
}
