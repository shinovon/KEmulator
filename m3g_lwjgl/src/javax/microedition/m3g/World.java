package javax.microedition.m3g;

public class World extends Group {
    private Background background = null;
    private Camera activeCamera = null;

    public void setBackground(Background var1) {
        this.removeReference(this.background);
        this.background = var1;
        this.addReference(this.background);
    }

    public Background getBackground() {
        return this.background;
    }

    public void setActiveCamera(Camera var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else {
            this.removeReference(this.activeCamera);
            this.activeCamera = var1;
            this.addReference(this.activeCamera);
        }
    }

    public Camera getActiveCamera() {
        return this.activeCamera;
    }

    protected void updateAlignReferences() {
        super.updateAlignReferences();
        if (this.activeCamera != null && this.activeCamera.m_duplicatedNode != null && this.activeCamera.m_duplicatedNode.isDescendantOf(super.m_duplicatedNode)) {
            ((World) super.m_duplicatedNode).activeCamera = (Camera) this.activeCamera.m_duplicatedNode;
        }

    }
}
