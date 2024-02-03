package javax.microedition.m3g;

import java.io.IOException;

public class MorphingMesh extends Mesh {

    VertexBuffer[] targets;
    float[] weights;
    
    public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer[] submeshes, Appearance[] appearances){ 
    //Constructs a new MorphingMesh with the given base mesh and morph targets.
        super(base, submeshes, appearances);
        this.targets = new VertexBuffer[targets.length];
        System.arraycopy(targets, 0, this.targets, 0, targets.length);
        weights = new float[targets.length];
    }

    public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer submesh, Appearance appearance) {
    //Constructs a new MorphingMesh with the given base mesh and morph targets.
    super(base, submesh, appearance);
    this.targets = new VertexBuffer[targets.length];
    System.arraycopy(targets, 0, this.targets, 0, targets.length);
    weights = new float[targets.length];
    
    }

    /** Used by loader */
    MorphingMesh() {
    }

public VertexBuffer    getMorphTarget(int index) {
    return targets[index];
}

public    int getMorphTargetCount() {
    //Returns the number of morph targets in this MorphingMesh.
    return targets.length;
}

public void    getWeights(float[] weights) {
    //Gets the current morph target weights for this mesh.
    System.arraycopy(this.weights, 0, weights, 0, this.weights.length);
}

public void    setWeights(float[] weights) {
//    Sets the weights for all morph targets in this mesh.
    System.arraycopy(weights, 0, this.weights, 0, this.weights.length);
}

    void load(M3gInputStream is) throws IOException{
        super.load(is);
        //UInt32        morphTargetCount;
        //FOR each target buffer...
        //    ObjectIndex   morphTarget;
        //    Float32       initialWeight;
        //END
        int tc = is.readInt32();
        targets = new VertexBuffer[tc];
        weights = new float[tc];
        for(int i = 0; i < tc; i++){
            targets[i] = (VertexBuffer) is.readObject();
            weights[i] = is.readFloat32();
        }
    }

    public Object3D duplicate(){
        MorphingMesh mm = new MorphingMesh(vertices, targets, submeshes, appearances);
        System.arraycopy(weights, 0, mm.weights, 0, weights.length);
        return _duplicate(mm);
    }
    
    public int getReferences(Object3D[] refs){
       int count = super.getReferences(refs);
       for(int i = 0; i < targets.length; i++){
           count = addRef(refs, count, targets[i]);
       }
       return count;
    }
    
    
    void setProperty(int id, float [] value){
        switch(id){
        case AnimationTrack.MORPH_WEIGHTS:
            for(int i= 0; i < weights.length; i++){
                weights[i] = i < value.length ? value[i] : 0;
            }
            break;
        default:
            super.setProperty(id, value);
        }
    }
    
}
