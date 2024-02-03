package javax.microedition.m3g;

public class Light extends Node
{
    public static final int AMBIENT = 128;
    public static final int DIRECTIONAL = 129;
    public static final int OMNI = 130;
    public static final int SPOT = 131;
    
    Light(final int n) {
        super(n);
    }
    
    public Light() {
        this(create());
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != Light.class);
    }
    
    private static native int create();
    
    public native int getMode();
    
    public native int getColor();
    
    public native float getIntensity();
    
    public native float getSpotAngle();
    
    public native float getSpotExponent();
    
    public native float getConstantAttenuation();
    
    public native float getLinearAttenuation();
    
    public native float getQuadraticAttenuation();
    
    public native void setMode(final int p0);
    
    public native void setColor(final int p0);
    
    public native void setIntensity(final float p0);
    
    public native void setSpotAngle(final float p0);
    
    public native void setSpotExponent(final float p0);
    
    public native void setAttenuation(final float p0, final float p1, final float p2);
}
