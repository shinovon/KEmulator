package emulator.debug;

public class Profiler3D {

	// m3g
	public static int M3G_clearCallCount;
	public static int M3G_renderWorldCallCount;
	public static int M3G_renderNodeCallCount;
	public static int M3G_renderVertexCallCount;

	// lwjgl
	public static int bindTargetCallCount;
	public static int releaseTargetCallCount;

	public static int LWJGL_buffersSwapCount;
	public static int LWJGL_drawCallCount;

	// mascot (woesss)
	public static int MC3D_renderFigureCallCount;
	public static int MC3D_renderModelCallCount;
	public static int MC3D_renderMeshCallCount;
	public static int MC3D_renderPrimitiveCallCount;
	public static int MC3D_renderCommandListCallCount;

	public static int MC3D_postFigureCallCount;
	public static int MC3D_postPrimitivesCallCount;
	public static int MC3D_drawFigureCallCount;

	public static int MC3D_bindGraphicsCallCount;
	public static int MC3D_bindTextureCallCount;
	public static int MC3D_flushCallCount;
	public static int MC3D_releaseCallCount;

	public static int MC3D_copy2dCount;

	public static void reset() {

	}

}
