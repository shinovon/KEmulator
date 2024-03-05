package javax.microedition.m3g;

public class Appearance extends Object3D {

    Appearance(final int n) {
        super(n);
    }

    public Appearance() {
        this(create());
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (getClass() != Appearance.class);
    }

    private static native int create();

    public native int getLayer();

    public Material getMaterial() {
        return (Material) Engine.instantiateJavaPeer(this.getMaterialImpl());
    }

    private native int getMaterialImpl();

    public Fog getFog() {
        return (Fog) Engine.instantiateJavaPeer(this.getFogImpl());
    }

    private native int getFogImpl();

    public CompositingMode getCompositingMode() {
        return (CompositingMode) Engine.instantiateJavaPeer(this.getCompositingModeImpl());
    }

    private native int getCompositingModeImpl();

    public PolygonMode getPolygonMode() {
        return (PolygonMode) Engine.instantiateJavaPeer(this.getPolygonModeImpl());
    }

    private native int getPolygonModeImpl();

    public native void setLayer(final int p0);

    public void setMaterial(final Material materialImpl) {
        this.setMaterialImpl(materialImpl);
        Engine.addXOT(materialImpl);
    }

    private native void setMaterialImpl(final Material p0);

    public void setFog(final Fog fogImpl) {
        this.setFogImpl(fogImpl);
        Engine.addXOT(fogImpl);
    }

    private native void setFogImpl(final Fog p0);

    public void setCompositingMode(final CompositingMode compositingModeImpl) {
        this.setCompositingModeImpl(compositingModeImpl);
        Engine.addXOT(compositingModeImpl);
    }

    private native void setCompositingModeImpl(final CompositingMode p0);

    public void setPolygonMode(final PolygonMode polygonModeImpl) {
        this.setPolygonModeImpl(polygonModeImpl);
        Engine.addXOT(polygonModeImpl);
    }

    private native void setPolygonModeImpl(final PolygonMode p0);

    public Texture2D getTexture(final int n) {
        return (Texture2D) Engine.instantiateJavaPeer(this.getTextureImpl(n));
    }

    private native int getTextureImpl(final int p0);

    public void setTexture(final int n, final Texture2D texture2D) {
        this.setTextureImpl(n, texture2D);
        Engine.addXOT(texture2D);
    }

    private native void setTextureImpl(final int p0, final Texture2D p1);
}
