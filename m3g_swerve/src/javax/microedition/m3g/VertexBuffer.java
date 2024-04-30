package javax.microedition.m3g;

public class VertexBuffer extends Object3D {
    VertexBuffer(int n) {
        super(n);
    }

    public VertexBuffer() {
        this(create());
        Engine.addJavaPeer(this.swerveHandle, this);
        this.ii = (getClass() != VertexBuffer.class);
    }

    private static native int create();

    public VertexArray getNormals() {
        return (VertexArray) Engine.instantiateJavaPeer(getNormalsImpl());
    }

    private native int getNormalsImpl();

    public VertexArray getColors() {
        return (VertexArray) Engine.instantiateJavaPeer(getColorsImpl());
    }

    private native int getColorsImpl();

    public native int getDefaultColor();

    public native int getVertexCount();

    public void setNormals(VertexArray normalsImpl) {
        setNormalsImpl(normalsImpl);
        Engine.addXOT(normalsImpl);
    }

    private native void setNormalsImpl(VertexArray paramVertexArray);

    public void setColors(VertexArray colorsImpl) {
        setColorsImpl(colorsImpl);
        Engine.addXOT(colorsImpl);
    }

    private native void setColorsImpl(VertexArray paramVertexArray);

    public native void setDefaultColor(int paramInt);

    public VertexArray getPositions(float[] array) {
        return (VertexArray) Engine.instantiateJavaPeer(getPositionsImpl(array));
    }

    private native int getPositionsImpl(float[] paramArrayOfFloat);

    public void setPositions(VertexArray vertexArray, float n, float[] array) {
        setPositionsImpl(vertexArray, n, array);
        Engine.addXOT(vertexArray);
    }

    private native void setPositionsImpl(VertexArray paramVertexArray, float paramFloat, float[] paramArrayOfFloat);

    public VertexArray getTexCoords(int n, float[] array) {
        return (VertexArray) Engine.instantiateJavaPeer(getTexCoordsImpl(n, array));
    }

    private native int getTexCoordsImpl(int paramInt, float[] paramArrayOfFloat);

    public void setTexCoords(int n, VertexArray vertexArray, float n2, float[] array) {
        setTexCoordsImpl(n, vertexArray, n2, array);
        Engine.addXOT(vertexArray);
    }

    private native void setTexCoordsImpl(int paramInt, VertexArray paramVertexArray, float paramFloat,
                                         float[] paramArrayOfFloat);
}
