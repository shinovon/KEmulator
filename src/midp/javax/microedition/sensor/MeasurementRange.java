package javax.microedition.sensor;

public class MeasurementRange
{
    private double aDouble1503;
    private double aDouble1504;
    private double aDouble1505;
    
    public MeasurementRange(final double aDouble1503, final double aDouble1504, final double aDouble1505) {
        super();
        if (aDouble1505 < 0.0 || aDouble1503 > aDouble1504) {
            throw new IllegalArgumentException();
        }
        this.aDouble1503 = aDouble1503;
        this.aDouble1504 = aDouble1504;
        this.aDouble1505 = aDouble1505;
    }
    
    public double getLargestValue() {
        return this.aDouble1504;
    }
    
    public double getResolution() {
        return this.aDouble1505;
    }
    
    public double getSmallestValue() {
        return this.aDouble1503;
    }
}
