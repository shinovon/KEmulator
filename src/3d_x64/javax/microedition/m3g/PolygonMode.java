package javax.microedition.m3g;

import java.io.IOException;

public class PolygonMode extends Object3D {

	public static final int 	CULL_BACK =	160;
	public static final int 	CULL_FRONT =	161;
	public static final int 	CULL_NONE =	162;
	public static final int 	SHADE_FLAT =	164;
	public static final int 	SHADE_SMOOTH= 	165;
	public static final int 	WINDING_CCW =	168;
	public static final int 	WINDING_CW =	169;
	
	
	int  culling;
	int          shading;
	int          winding;
	boolean       twoSidedLightingEnabled;
	boolean       localCameraLightingEnabled;
	boolean       perspectiveCorrectionEnabled;
	
	public PolygonMode() {
//    Constructs a PolygonMode object with default values.
	}
	

public int 	getCulling(){
//    Retrieves the current polygon culling mode.
	return culling;
}

public int 	getShading(){
 //   Retrieves the current polygon shading mode.
	return shading;
}

public int 	getWinding(){
//    Retrieves the current polygon winding mode.
	return winding;
}

public boolean 	isTwoSidedLightingEnabled(){
//    Queries whether two-sided lighting is enabled.
	return twoSidedLightingEnabled;
}

public void 	setCulling(int mode){
//    Sets the polygon culling mode.
	culling=mode;
}
public void 	setLocalCameraLightingEnable(boolean enable){
//    Enables or disables local camera lighting.
	localCameraLightingEnabled = enable;
}
public	void 	setPerspectiveCorrectionEnable(boolean enable){
//    Enables or disables perspective correction.
	perspectiveCorrectionEnabled =enable;
}

public void 	setShading(int mode){
//    Sets the polygon shading mode.
	shading = mode;
}

public void 	setTwoSidedLightingEnable(boolean enable){
//    Enables or disables two-sided lighting.
	twoSidedLightingEnabled =enable;
}

public void 	setWinding(int mode){
//    Sets the polygon winding mode to clockwise or counter-clockwise.
	winding = mode;
}


	void load(M3gInputStream is) throws IOException {
	//	Byte          culling;
	//	Byte          shading;
	//	Byte          winding;
	//	Boolean       twoSidedLightingEnabled;
	//	Boolean       localCameraLightingEnabled;
	//	Boolean       perspectiveCorrectionEnabled;	}
		_load(is);
		culling = is.read();
		shading = is.read();
		winding = is.read();
		twoSidedLightingEnabled = is.readBoolean();
		localCameraLightingEnabled = is.readBoolean();
		perspectiveCorrectionEnabled = is.readBoolean();
	}
	
	

	public int getReferences(Object3D[] refs) {
		return _getReferences(refs);
	}



	public Object3D duplicate() {
		PolygonMode target = new PolygonMode();
		target.culling = culling;
		target.shading = shading;
		target.winding = winding;
		target.twoSidedLightingEnabled = twoSidedLightingEnabled;
		target.localCameraLightingEnabled = localCameraLightingEnabled;
		target.perspectiveCorrectionEnabled = perspectiveCorrectionEnabled;
		return _duplicate(target);
	}
}
