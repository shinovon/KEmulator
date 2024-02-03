package javax.microedition.khronos.opengles;

import java.nio.*;

public interface GL11Ext extends GL
{
    public static final int GL_TEXTURE_CROP_RECT_OES = 35741;
    public static final int GL_MAX_VERTEX_UNITS_OES = 34468;
    public static final int GL_MAX_PALETTE_MATRICES_OES = 34882;
    public static final int GL_MATRIX_PALETTE_OES = 34880;
    public static final int GL_MATRIX_INDEX_ARRAY_OES = 34884;
    public static final int GL_WEIGHT_ARRAY_OES = 34477;
    public static final int GL_MATRIX_INDEX_ARRAY_SIZE_OES = 34886;
    public static final int GL_MATRIX_INDEX_ARRAY_TYPE_OES = 34887;
    public static final int GL_MATRIX_INDEX_ARRAY_STRIDE_OES = 34888;
    public static final int GL_MATRIX_INDEX_ARRAY_POINTER_OES = 34889;
    public static final int GL_MATRIX_INDEX_ARRAY_BUFFER_BINDING_OES = 35742;
    public static final int GL_WEIGHT_ARRAY_SIZE_OES = 34475;
    public static final int GL_WEIGHT_ARRAY_TYPE_OES = 34473;
    public static final int GL_WEIGHT_ARRAY_STRIDE_OES = 34474;
    public static final int GL_WEIGHT_ARRAY_POINTER_OES = 34476;
    public static final int GL_WEIGHT_ARRAY_BUFFER_BINDING_OES = 34974;
    
    void glEnable(final int p0);
    
    void glEnableClientState(final int p0);
    
    void glTexParameterfv(final int p0, final int p1, final float[] p2, final int p3);
    
    void glDrawTexfOES(final float p0, final float p1, final float p2, final float p3, final float p4);
    
    void glDrawTexsOES(final short p0, final short p1, final short p2, final short p3, final short p4);
    
    void glDrawTexiOES(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    void glDrawTexxOES(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    void glDrawTexfvOES(final float[] p0, final int p1);
    
    void glDrawTexfvOES(final FloatBuffer p0);
    
    void glDrawTexsvOES(final short[] p0, final int p1);
    
    void glDrawTexsvOES(final ShortBuffer p0);
    
    void glDrawTexivOES(final int[] p0, final int p1);
    
    void glDrawTexivOES(final IntBuffer p0);
    
    void glDrawTexxvOES(final int[] p0, final int p1);
    
    void glDrawTexxvOES(final IntBuffer p0);
    
    void glCurrentPaletteMatrixOES(final int p0);
    
    void glLoadPaletteFromModelViewMatrixOES();
    
    void glMatrixIndexPointerOES(final int p0, final int p1, final int p2, final Buffer p3);
    
    void glMatrixIndexPointerOES(final int p0, final int p1, final int p2, final int p3);
    
    void glWeightPointerOES(final int p0, final int p1, final int p2, final Buffer p3);
    
    void glWeightPointerOES(final int p0, final int p1, final int p2, final int p3);
}
