package emulator.graphics3D;

public final class a {
    public float aFloat1367;
    public float aFloat1368;
    public float aFloat1369;
    public float aFloat1370;

    public a() {
        super();
    }

    private a(final a a) {
        super();
        this.method811(a);
    }

    public final void method811(final a a) {
        this.aFloat1367 = a.aFloat1367;
        this.aFloat1368 = a.aFloat1368;
        this.aFloat1369 = a.aFloat1369;
        this.aFloat1370 = a.aFloat1370;
    }

    private void method812() {
        final float aFloat1367 = 0.0f;
        this.aFloat1369 = aFloat1367;
        this.aFloat1368 = aFloat1367;
        this.aFloat1367 = aFloat1367;
        this.aFloat1370 = 1.0f;
    }

    public final void method813(final float n, final float n2, final float n3, final float n4) {
        final b b;
        if ((b = new b(n2, n3, n4, 0.0f)).method816()) {
            final float n6;
            final float n5 = (float) Math.sin(n6 = (float) Math.toRadians(0.5f * n));
            this.aFloat1367 = n5 * b.aFloat1372;
            this.aFloat1368 = n5 * b.aFloat1373;
            this.aFloat1369 = n5 * b.aFloat1374;
            this.aFloat1370 = (float) Math.cos(n6);
            return;
        }
        this.method812();
    }

    public final void method814(final a a) {
        final a a2 = new a(this);
        this.aFloat1370 = a2.aFloat1370 * a.aFloat1370 - a2.aFloat1367 * a.aFloat1367 - a2.aFloat1368 * a.aFloat1368 - a2.aFloat1369 * a.aFloat1369;
        this.aFloat1367 = a2.aFloat1370 * a.aFloat1367 + a2.aFloat1367 * a.aFloat1370 + a2.aFloat1368 * a.aFloat1369 - a2.aFloat1369 * a.aFloat1368;
        this.aFloat1368 = a2.aFloat1370 * a.aFloat1368 - a2.aFloat1367 * a.aFloat1369 + a2.aFloat1368 * a.aFloat1370 + a2.aFloat1369 * a.aFloat1367;
        this.aFloat1369 = a2.aFloat1370 * a.aFloat1369 + a2.aFloat1367 * a.aFloat1368 - a2.aFloat1368 * a.aFloat1367 + a2.aFloat1369 * a.aFloat1370;
    }
}
