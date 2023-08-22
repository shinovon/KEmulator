package javax.microedition.m3g;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public abstract class Object3D {

	Vector animationTracks = new Vector();
	int userID;
	Object userObject;
    int id;
	
	abstract void load(M3gInputStream is) throws IOException;
	
	int addRef(Object3D[] refs, int count, Object3D ref){
		if(ref == null) return count;
		if(refs != null) refs[count] = ref;
		return count+1;
	}
	
	
	void _load(M3gInputStream is) throws IOException{
		//	UInt32           userID;
		// ObjectIndex[]    animationTracks;
		// UInt32           userParameterCount;
		// FOR each user parameter...
		//    UInt32        parameterID;
	    //   Byte[]        parameterValue;
		// END
		
		userID = is.readInt32();
		Object[] at = is.readObjectArray();
		for(int i = 0; i < at.length; i++){
			addAnimationTrack((AnimationTrack) at[i]); 
		}
		int userParameterCount = is.readInt32();
		if(userParameterCount != 0){
			Hashtable ht = new Hashtable();
			userObject = ht;
			for(int i = 0; i < userParameterCount; i++){
				int userId = is.readInt32();
				byte[] data = is.readByteArray();
				ht.put(new Integer(userId), data);
			}
		}
		
	}
	
	 public void addAnimationTrack(AnimationTrack animationTrack){
		 // Adds the given AnimationTrack to this Object3D, potentially changing the order and indices of the previously added tracks.
		 if(animationTracks == null){
			 animationTracks = new Vector();
		 }
		 animationTracks.addElement(animationTrack);
	 }
	 
     
     public int animate(int time){
    	 	//System.out.println("Animate called for: "+this);
         int count = getReferences(null);
         Object3D[] refs = new Object3D[count];
         getReferences(refs);
         int dt = _animate(time);
         for(int i = 0; i < count; i++){
             dt = Math.min(dt, refs[i].animate(time));
         }
         
         return dt;
     }
     
     
     /** Animate a single object w/o recursion */
     
	int _animate(int time){
         if(animationTracks.size() == 0) return Integer.MAX_VALUE;
         
         Hashtable newProperties = new Hashtable();
	     for(int i = 0; i < animationTracks.size(); i++){
             AnimationTrack t = getAnimationTrack(i);
             float[] value = t.calcValue(time);
             if(value != null){
                 Integer tp = new Integer(t.getTargetProperty());
                 float[] value0 = (float[]) newProperties.get(tp);
                 if(value0 != null){
                     for(int j = 0; j < value.length; j++){
                         value0[j] += value[j];
                     }
                 }
                 else{
                     newProperties.put(tp, value);
                 }
             }
         }
         
         for(Enumeration e = newProperties.keys(); e.hasMoreElements();){
             Integer tp = (Integer) e.nextElement();
             setProperty(tp.intValue(), (float[]) newProperties.get(tp));
         }
         
		return 0; // legal and does not hurt on J2SE
	 }
	 
	 public abstract Object3D duplicate();

	 Object3D _duplicate(Object3D target){
		 for(int i = 0; i < getAnimationTrackCount(); i++){
			 target.addAnimationTrack(getAnimationTrack(i));
		 }
		 target.userID = userID;
		 target.userObject = userObject;
		 return target;
	 }
	 
	 
	 public Object3D find(int userID){
		 int count = getReferences(null);
		 Object3D[] refs = new Object3D[count];
		 getReferences(refs);
		 for(int i = 0; i < count; i++){
			 if(refs[i].userID == userID) return refs[i];
		 }
		 for(int i = 0; i < count; i++){
			 Object3D result = refs[i].find(userID);
			 if(result != null) return result;
		 }
		 return null;
	 }

	
	 
	 public AnimationTrack getAnimationTrack(int index){
		 return(AnimationTrack) animationTracks.elementAt(index);
	 }
		
	 public int getAnimationTrackCount(){
		 return animationTracks == null ? 0 : animationTracks.size();
	 }
	 
	 public abstract int getReferences(Object3D[] refs);
	 
	 int _getReferences(Object3D[] refs){
		 int count = animationTracks.size();
		 if(refs != null){
			 for(int i = 0; i < count; i++){
				 refs[i] = (Object3D) animationTracks.elementAt(i);
			 }
		 }
		 return count;
	 }
	 
	 public int getUserID(){
		return userID;
	 }
	 
	 public Object getUserObject(){
		return userObject;
	 }
	 
	 public void removeAnimationTrack(AnimationTrack animationTrack){
		 if(animationTracks != null){
			 animationTracks.remove(animationTrack);
		 }
	 }
	 
	 public void setUserID(int userID){
		 this.userID = userID;
	 }
	 
	 public void setUserObject(Object userObject){
     	this.userObject = userObject;
	 }

	 /** used internally to update values from animations */

     void setProperty(int id, float[] value){
         // TODO overwrite in subclasses!
         System.out.println("setProperty "+id+" not performed "+this);
     }
     
     /** Helper method for setProperty */
     static int normalize255(float value){
         return Math.max(0, Math.min(255, Math.round(value * 255)));
     }

     /** Helper method for setProperty */     
     static int setAlpha(float[] value, int argb){
         return (argb & 0x0ffffff) | (normalize255(value[0]) << 24);
     }
     
     static int setColor(float[] value, int argb){
          return (argb & 0x0ff000000) 
              | (normalize255(value[0]) << 16)
              | (normalize255(value[1]) << 8)
              | normalize255(value[2]);
     }
     
     public String toString(){
         String name = getClass().getName();
         return name.substring(name.lastIndexOf('.')+1)+"#"+id+'/'+userID;
     }
     
         
     void dump(){
         String name = getClass().getName();
         System.out.println(name.substring(name.lastIndexOf('.')+1)+" # "+id+ " {");
         Hashtable props = getProperties();
         for(Enumeration e = props.keys(); e.hasMoreElements();){
             String key = (String) e.nextElement();
             System.out.println("  "+key+": "+props.get(key));
         }
         System.out.println("}");
         System.out.println();
     }
     
    Hashtable getProperties() {
        Hashtable props = new Hashtable();
        
        Method [] methods = getClass().getMethods();
        for(int i = 0; i < methods.length; i++){
            Method m = methods[i];
            if(m.getName().startsWith("get") && m.getParameterTypes().length == 0){
                Object p;
                try {
                    p = m.invoke(this, (Object[])null);
                } catch (Exception e) {
                   p = e;
                }
                props.put(m.getName().substring(3), p == null ? "null" : p);
            }
        }
        
        return props;
    }
    
}
