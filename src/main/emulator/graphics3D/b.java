package emulator.graphics3D;

public final class b
{
    public static final b ab1371;
    public float aFloat1372;
    public float aFloat1373;
    public float aFloat1374;
    public float aFloat1375;
    
    public b() {
        super();
    }
    
    public b(final float[] array) {
        super();
        this.aFloat1372 = array[0];
        this.aFloat1373 = array[1];
        this.aFloat1374 = array[2];
        this.aFloat1375 = array[3];
    }
    
    public b(final float aFloat1372, final float aFloat1373, final float aFloat1374, final float aFloat1375) {
        super();
        this.aFloat1372 = aFloat1372;
        this.aFloat1373 = aFloat1373;
        this.aFloat1374 = aFloat1374;
        this.aFloat1375 = aFloat1375;
    }
    
    public final void method815(final b b, final b b2) {
        this.aFloat1372 = b.aFloat1373 * b2.aFloat1374 - b.aFloat1374 * b2.aFloat1373;
        this.aFloat1373 = b.aFloat1374 * b2.aFloat1372 - b.aFloat1372 * b2.aFloat1374;
        this.aFloat1374 = b.aFloat1372 * b2.aFloat1373 - b.aFloat1373 * b2.aFloat1372;
        this.aFloat1375 = 0.0f;
    }
    
    public final boolean method816() {
        final float n;
        if ((n = this.aFloat1372 * this.aFloat1372 + this.aFloat1373 * this.aFloat1373 + this.aFloat1374 * this.aFloat1374 + this.aFloat1375 * this.aFloat1375) < 1.0E-5f) {
            return false;
        }
        final float n2 = 1.0f / (float)Math.sqrt(n);
        this.aFloat1372 *= n2;
        this.aFloat1373 *= n2;
        this.aFloat1374 *= n2;
        this.aFloat1375 *= n2;
        return true;
    }
    
    static {
        new b(1.0f, 0.0f, 0.0f, 0.0f);
        ab1371 = new b(0.0f, 1.0f, 0.0f, 0.0f);
        new b(0.0f, 0.0f, 1.0f, 0.0f);
        new b(0.0f, 0.0f, 0.0f, 1.0f);
    }
}
