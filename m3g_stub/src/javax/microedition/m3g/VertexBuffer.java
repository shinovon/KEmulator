package javax.microedition.m3g;

public class VertexBuffer
        extends Object3D {
    public VertexBuffer() {
        super(2);
    }

    public int getVertexCount() {
        return 1;
    }

    public void setPositions(VertexArray paramVertexArray, float paramFloat, float[] paramArrayOfFloat) {
    }

    public void setTexCoords(int paramInt, VertexArray paramVertexArray, float paramFloat, float[] paramArrayOfFloat) {
    }

    public void setNormals(VertexArray paramVertexArray) {
    }

    public void setColors(VertexArray paramVertexArray) {
    }

    public VertexArray getPositions(float[] paramArrayOfFloat) {
        return null;
    }

    public VertexArray getTexCoords(int paramInt, float[] paramArrayOfFloat) {
        return null;
    }

    public VertexArray getNormals() {
        return null;
    }

    public VertexArray getColors() {
        return null;
    }

    public void setDefaultColor(int paramInt) {
    }

    public int getDefaultColor() {
        return 1;
    }

    VertexBuffer(int paramInt) {
        super(paramInt);
    }
}
