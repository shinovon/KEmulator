package emulator.graphics3D.m3g;

import emulator.graphics3D.Transform3D;
import javax.microedition.m3g.*;

public final class RenderObject {
	public Node node;
	public Transform trans;

	public int submeshIndex;
	public int sortKey;
	public float alphaFactor;
	public float depth;

	private float refX;
	private float refY;
	private float refZ;

	public RenderObject(Node node, Transform trans, int submeshIndex, RenderPipe renderPipe) {
		this.node = node;
		this.trans = new Transform(trans);
		this.submeshIndex = submeshIndex;

		if (node instanceof Mesh) {
			float[] scaleBias = new float[4];
			VertexBuffer vb = ((Mesh) node).getVertexBuffer();
			if (vb != null && vb.getPositions(scaleBias) != null) {
				refX = scaleBias[1];
				refY = scaleBias[2];
				refZ = scaleBias[3];
			}
		}

		Appearance ap;
		if (node instanceof Sprite3D) {
			sortKey = getSortKey(((Sprite3D) node).getAppearance());
		} else {
			sortKey = getSortKey(((Mesh) node).getAppearance(submeshIndex));
		}

		alphaFactor = renderPipe.getEffectiveAlphaFactor(node);
	}

	public void computeDepth(Transform invCamera) {
		Transform3D model = (Transform3D) trans.getImpl();
		float[] m = model.m_matrix;
		float wx = m[0] * refX + m[1] * refY + m[2] * refZ + m[3];
		float wy = m[4] * refX + m[5] * refY + m[6] * refZ + m[7];
		float wz = m[8] * refX + m[9] * refY + m[10] * refZ + m[11];

		Transform3D cam = (Transform3D) invCamera.getImpl();
		float[] c = cam.m_matrix;
		// Eye-space Z is negative in front of the camera; negate so larger is farther.
		float value = -(c[8] * wx + c[9] * wy + c[10] * wz + c[11]);
		depth = value == 0.0F ? 0.0F : value;
	}

	private int getSortKey(Appearance ap) {
		int sortKey = ap.getLayer() * 2;

		if (ap.getCompositingMode() != null && ap.getCompositingMode().getBlending() != CompositingMode.REPLACE) {
			sortKey++;
		}

		return sortKey;
	}
}
