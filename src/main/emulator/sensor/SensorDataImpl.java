package emulator.sensor;

import javax.microedition.sensor.*;

public final class SensorDataImpl implements Data {
    private ChannelInfo aChannelInfo478;
    private boolean aBoolean479;
    private long[] aLongArray480;
    private boolean aBoolean485;
    private float[] aFloatArray481;
    private boolean aBoolean487;
    private boolean[] aBooleanArray482;
    private int anInt483;
    private int anInt486;
    private Object[] anObjectArray484;

    SensorDataImpl(final ChannelInfo aChannelInfo478, final int anInt486, final int anInt487, final boolean aBoolean479, final boolean aBoolean480, final boolean aBoolean481) {
        super();
        this.aChannelInfo478 = aChannelInfo478;
        this.aBoolean479 = aBoolean479;
        if (aBoolean479) {
            this.aLongArray480 = new long[anInt486];
        }
        this.aBoolean485 = aBoolean480;
        if (aBoolean480) {
            this.aFloatArray481 = new float[anInt486];
        }
        this.aBoolean487 = aBoolean481;
        if (aBoolean481) {
            this.aBooleanArray482 = new boolean[anInt486];
        }
        this.anInt483 = anInt487;
        this.anObjectArray484 = new Object[anInt486];
        this.anInt486 = anInt486;
    }

    public final ChannelInfo getChannelInfo() {
        return this.aChannelInfo478;
    }

    public final double[] getDoubleValues() {
        if (this.anInt483 != 1) {
            throw new IllegalStateException("Data type is not double");
        }
        double[] array = new double[0];
        if (this.anObjectArray484 != null && this.anInt486 > 0) {
            array = new double[this.anInt486];
            for (int i = 0; i < this.anInt486; ++i) {
                final Object o;
                if ((o = this.anObjectArray484[i]) instanceof Double) {
                    array[i] = (Double) o;
                } else {
                    array[i] = 0.0;
                    if (this.aBoolean487 && this.aBooleanArray482[i]) {
                        this.aBooleanArray482[i] = false;
                    }
                }
            }
        }
        return array;
    }

    public final int[] getIntValues() {
        if (this.anInt483 != 2) {
            throw new IllegalStateException("Data type is not int");
        }
        int[] array = new int[0];
        if (this.anObjectArray484 != null && this.anInt486 > 0) {
            array = new int[this.anInt486];
            for (int i = 0; i < this.anInt486; ++i) {
                final Object o;
                if ((o = this.anObjectArray484[i]) instanceof Integer) {
                    array[i] = (Integer) o;
                } else {
                    array[i] = 0;
                    if (this.aBoolean487 && this.aBooleanArray482[i]) {
                        this.aBooleanArray482[i] = false;
                    }
                }
            }
        }
        return array;
    }

    public final Object[] getObjectValues() {
        if (this.anInt483 != 4) {
            throw new IllegalStateException("Data type is not Object");
        }
        Object[] anObjectArray484 = new Object[0];
        if (this.anObjectArray484 != null) {
            if (this.anInt486 == this.anObjectArray484.length) {
                anObjectArray484 = this.anObjectArray484;
            } else if (this.anInt486 > 0) {
                anObjectArray484 = new Object[this.anInt486];
                System.arraycopy(this.anObjectArray484, 0, anObjectArray484, 0, this.anInt486);
            }
        }
        return anObjectArray484;
    }

    public final long getTimestamp(final int n) {
        if (!this.aBoolean479) {
            throw new IllegalStateException("Timestamp wasn't requested");
        }
        return this.aLongArray480[n];
    }

    public final float getUncertainty(final int n) {
        if (!this.aBoolean485) {
            throw new IllegalStateException("Uncertainty wasn't requested");
        }
        return this.aFloatArray481[n];
    }

    public final boolean isValid(final int n) {
        if (!this.aBoolean487) {
            throw new IllegalStateException("Validity wasn't requested");
        }
        return this.aBooleanArray482[n];
    }

    final void method269(final int anInt486) {
        if (this.anInt486 < anInt486) {
            long[] aLongArray480 = null;
            float[] aFloatArray481 = null;
            boolean[] aBooleanArray482 = null;
            if (this.aLongArray480 != null) {
                aLongArray480 = new long[anInt486];
                System.arraycopy(this.aLongArray480, 0, aLongArray480, 0, this.aLongArray480.length);
            }
            if (this.aFloatArray481 != null) {
                aFloatArray481 = new float[anInt486];
                System.arraycopy(this.aFloatArray481, 0, aFloatArray481, 0, this.aFloatArray481.length);
            }
            if (this.aBooleanArray482 != null) {
                aBooleanArray482 = new boolean[anInt486];
                System.arraycopy(this.aBooleanArray482, 0, aBooleanArray482, 0, this.aBooleanArray482.length);
            }
            final Object[] anObjectArray484 = new Object[anInt486];
            System.arraycopy(this.anObjectArray484, 0, anObjectArray484, 0, this.anObjectArray484.length);
            this.aLongArray480 = aLongArray480;
            this.aFloatArray481 = aFloatArray481;
            this.aBooleanArray482 = aBooleanArray482;
            this.anObjectArray484 = anObjectArray484;
        }
        this.anInt486 = anInt486;
    }

    final void method270(final int n, final Object o) {
        this.anObjectArray484[n] = o;
    }

    final void method271(final int n, final long n2) {
        if (this.aBoolean479) {
            this.aLongArray480[n] = n2;
        }
    }

    final void method272(final int n, final float n2) {
        if (this.aBoolean485) {
            this.aFloatArray481[n] = n2;
        }
    }

    final void method273(final int n, final boolean b) {
        if (this.aBoolean487) {
            this.aBooleanArray482[n] = b;
        }
    }
}
