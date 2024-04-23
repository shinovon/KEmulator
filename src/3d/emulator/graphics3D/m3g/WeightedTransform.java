package emulator.graphics3D.m3g;

import javax.microedition.m3g.Node;
import javax.microedition.m3g.Transform;

public final class WeightedTransform {
    public Node bone;
    public Transform toBoneTrans;
    public int weight;
    public int firstVertex;
    public int lastVertex;

    public Transform posTrans = new Transform();
    public Transform normTrans = new Transform();

    public WeightedTransform(Node bone, Transform trans, int weight, int firstVertex, int lastVertex) {
        this.bone = bone;
        this.weight = weight;
        this.toBoneTrans = new Transform(trans);
        this.firstVertex = firstVertex;
        this.lastVertex = lastVertex;
    }
}
