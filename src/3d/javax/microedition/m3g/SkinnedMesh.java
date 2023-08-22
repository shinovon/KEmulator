package javax.microedition.m3g;

import java.io.IOException;
import java.util.Vector;

public class SkinnedMesh extends Mesh {
    
    class Transformation{
        Node bone;
        int weight;
        int firstVertex;
        int vertexCount;
    }
    
    Group skeleton;
    Vector transformations = new Vector();
    
    public SkinnedMesh(VertexBuffer vertices, IndexBuffer[] submeshes, Appearance[] appearances, Group skeleton) {
   // Constructs a new SkinnedMesh with the given vertices, submeshes and skeleton.
        super(vertices, submeshes, appearances);
        this.skeleton = skeleton;
        buildSkeleton(skeleton);
    }
    
    public SkinnedMesh(VertexBuffer vertices, IndexBuffer submesh, Appearance appearance, Group skeleton) {
    // Constructs a new SkinnedMesh with the given vertices, submesh and skeleton.
        super(vertices, submesh, appearance);
        this.skeleton = skeleton;
        buildSkeleton(skeleton);
    }
  
    
    void buildSkeleton(Node ref)
    {        
   	 
//    	ref.transform = new Transform();
//    	ref.rotation  = new Transform();
//    	ref.translation = new float[3];
//    	ref.scale       = new float[]{1,1,1};
    	 
        Group groupDup = new Group();
        Transform transform = new Transform();
        ref.getTransform(transform);                   
        groupDup.rotation.set(ref.rotation.matrix);
      
        groupDup.setTransform(transform);
        float translation[] = new float[3];
        ref.getTranslation(translation);
        groupDup.setTranslation(translation[0], translation[1], translation[2]);
        float scale[] = new float[3];
        ref.getScale(scale);
        groupDup.setScale(scale[0], scale[1], scale[2]);
        groupDup.rotation = new Transform(ref.rotation);                   
      
        ref.baseSkeleton = groupDup;
//        ref.baseSkeleton = new Group();
        	
        int numReferences = ref.getReferences(null);
        if (numReferences > 0)
        {
           Object3D[] objArray = new Object3D[numReferences];
           ref.getReferences(objArray);
           
           for (int i = 0; i < numReferences; i++)
           {
               Object3D object3D = objArray[i];   
               if (object3D instanceof Node)
               {
            	   Node group = (Node) object3D;                   
                   buildSkeleton(group);                   
               }
           }
        }
    }

    void buildSkeleton(Node ref, Transform bone )
    {        
        Group groupDup = new Group();
        Transform transform = new Transform();
        ref.getTransform(transform);                   
        groupDup.rotation.set(ref.rotation.matrix);
      
        groupDup.setTransform(transform);
        float translation[] = new float[3];
        ref.getTranslation(translation);
        groupDup.setTranslation(translation[0], translation[1], translation[2]);
        float scale[] = new float[3];
        ref.getScale(scale);
        groupDup.setScale(scale[0], scale[1], scale[2]);
        groupDup.rotation = new Transform(ref.rotation);                   
              
        groupDup.transform.postMultiply(bone);
        
        ref.baseSkeleton = groupDup;
        	
        int numReferences = ref.getReferences(null);
        if (numReferences > 0)
        {
           Object3D[] objArray = new Object3D[numReferences];
           ref.getReferences(objArray);
           
           for (int i = 0; i < numReferences; i++)
           {
               Object3D object3D = objArray[i];   
               if (object3D instanceof Node)
               {
            	   Node group = (Node) object3D;                   
                   buildSkeleton(group, bone);                   
               }
           }
        }
    }
    

    
    /** Used by loader */ 
    public SkinnedMesh() {
    }

    public void    addTransform(Node bone, int weight, int firstVertex, int numVertices) {
    //Associates a weighted transformation, or "bone", with a range of vertices in this SkinnedMesh.
        Transformation t = new Transformation();
        t.bone = bone;
        t.weight = weight;
        t.firstVertex = firstVertex;
        t.vertexCount = numVertices;
        transformations.add(t); 
 
//        Transform transform1 = RenderUtils.getTransformSkinnedMesh(this.getSkeleton(), bone, new Transform(), true );
//        Transform transform2 = RenderUtils.getTransformSkinnedMesh(this.getSkeleton(), bone, new Transform(), false );
//        
//        Transform transformR = new Transform();
//        transformR.postMultiply(transform2);
//        transformR.invert();
//        transformR.postMultiply(transform1);
//
//        buildSkeleton(bone,transformR);
    }
     
    public    Group   getSkeleton() {
        return skeleton;
    }
    
    void load(M3gInputStream is) throws IOException{
        //ObjectIndex   skeleton;
        //UInt32        transformReferenceCount;
        //FOR each bone reference...
        //    ObjectIndex   transformNode;
        //    UInt32        firstVertex;
        //    UInt32        vertexCount;
        //    Int32         weight;
        //END
        
        super.load(is);
        skeleton = (Group) is.readObject();
        int trc = is.readInt32();
        for(int i = 0; i < trc; i++){
            Transformation t = new Transformation();
            t.bone = (Node) is.readObject();
            t.firstVertex = is.readInt32();
            t.vertexCount = is.readInt32();
            t.weight = is.readInt32();
            transformations.add(t);
        }
        buildSkeleton(skeleton); 
    }
    
    public Object3D duplicate(){
        SkinnedMesh target = new SkinnedMesh(vertices, submeshes, appearances, skeleton);
        for(int i = 0; i < transformations.size(); i++){
            target.transformations.add(transformations.elementAt(i));
        }
        return _duplicate(target);
    }
    
    public int getReferences(Object3D[] refs){
        int count = super.getReferences(refs);
        count = addRef(refs, count, skeleton);
        return count;
    }
    
}
