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
	public static int LWJGL_renderSpriteCount;
	public static int LWJGL_glTexImage2DCount;

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
		M3G_clearCallCount = 0;
		M3G_renderWorldCallCount = 0;
		M3G_renderNodeCallCount = 0;
		M3G_renderVertexCallCount = 0;

		bindTargetCallCount = 0;
		releaseTargetCallCount = 0;

		LWJGL_buffersSwapCount = 0;
		LWJGL_drawCallCount = 0;
		LWJGL_renderSpriteCount = 0;
		LWJGL_glTexImage2DCount = 0;

		MC3D_renderFigureCallCount = 0;
		MC3D_renderModelCallCount = 0;
		MC3D_renderMeshCallCount = 0;
		MC3D_renderPrimitiveCallCount = 0;
		MC3D_renderCommandListCallCount = 0;

		MC3D_postFigureCallCount = 0;
		MC3D_postPrimitivesCallCount = 0;
		MC3D_drawFigureCallCount = 0;

		MC3D_bindGraphicsCallCount = 0;
		MC3D_bindTextureCallCount = 0;
		MC3D_flushCallCount = 0;
		MC3D_releaseCallCount = 0;

		MC3D_copy2dCount = 0;
	}

}
