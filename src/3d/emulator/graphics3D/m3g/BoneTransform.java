package emulator.graphics3D.m3g;

import javax.microedition.m3g.Node;
import javax.microedition.m3g.Transform;

public final class BoneTransform {
	public Node bone;
	public Transform toBoneTrans;
	public Transform posTrans = new Transform();
	public Transform normTrans = new Transform();

	public BoneTransform(Node bone, Transform trans) {
		this.bone = bone;
		this.toBoneTrans = new Transform(trans);
	}
}
