package javax.microedition.m3g;

public class Background
        extends Object3D {
    public static final int BORDER = 32;
    public static final int REPEAT = 33;

    public Background() {
        super(2);
    }

    public void setColor(int paramInt) {
    }

    public int getColor() {
        return 1;
    }

    public void setImage(Image2D paramImage2D) {
    }

    public Image2D getImage() {
        return null;
    }

    public void setImageMode(int paramInt1, int paramInt2) {
    }

    public int getImageModeX() {
        return 1;
    }

    public int getImageModeY() {
        return 1;
    }

    public void setColorClearEnable(boolean paramBoolean) {
    }

    public void setDepthClearEnable(boolean paramBoolean) {
    }

    public boolean isColorClearEnabled() {
        return true;
    }

    public boolean isDepthClearEnabled() {
        return true;
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

    Background(int paramInt) {
        super(paramInt);
    }
}
