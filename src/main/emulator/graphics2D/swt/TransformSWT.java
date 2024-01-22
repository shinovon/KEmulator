package emulator.graphics2D.swt;

import emulator.graphics2D.ITransform;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Transform;

public final class TransformSWT implements ITransform {
    float[] a;

    public TransformSWT() {
        Transform var1 = new Transform((Device)null);
        this.a = new float[6];
        var1.getElements(this.a);
        var1.dispose();
    }

    public TransformSWT(Transform var1) {
        this.a = new float[6];
        var1.getElements(this.a);
    }

    public final Transform method298() {
        return new Transform((Device)null, this.a);
    }

    public final void transform(float[] var1, int var2, float[] var3, int var4, int var5) {
        System.arraycopy(var1, var2, var3, var4, var5 << 1);
        Transform var6;
        (var6 = new Transform((Device)null, this.a)).transform(var3);
        var6.dispose();
    }

    public final void transform(ITransform var1) {
        Transform var2 = new Transform((Device)null, this.a);
        Transform var3 = ((TransformSWT)var1).method298();
        float[] var4 = new float[6];
        var3.getElements(var4);
        float var5 = (float)Math.asin((double)var4[1]);
        float var6 = (float)((double)var4[0] / Math.cos((double)var5));
        float var7 = (float)((double)var4[3] / Math.cos((double)var5));
        var2.translate(var4[4], var4[5]);
        var2.scale(var6, var7);
        var2.rotate(var5);
        var2.getElements(this.a);
        var2.dispose();
        var3.dispose();
    }

    public final ITransform newTransform(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
        paramInt4 += a(paramInt1, paramInt2, paramInt3, paramInt6);
        paramInt5 += b(paramInt1, paramInt2, paramInt3, paramInt6);
        Transform localTransform;
        (localTransform = new Transform(null)).translate(paramInt4, paramInt5);
        if (paramInt3 == 6)
        {
            localTransform.translate(0.0F, paramInt1);
            localTransform.rotate(270.0F);
        }
        else
        {
            localTransform.rotate(270.0F);
            localTransform.translate(paramInt2, 0.0F);
            localTransform.rotate(90.0F);
            localTransform.translate(paramInt2, paramInt1);
            localTransform.rotate(90.0F);
            localTransform.translate(paramInt1, paramInt2);
            localTransform.rotate(180.0F);
            localTransform.translate(paramInt1, 0.0F);
            if (paramInt3 == 1)
            {
                localTransform.translate(0.0F, paramInt2);
                localTransform.scale(1.0F, paramInt3 == 2 ? 1.0F : paramInt3 == 7 ? 1.0F : paramInt3 == 4 ? 1.0F : -1.0F);
            }
        }
        TransformSWT locala = new TransformSWT(localTransform);
        localTransform.dispose();
        return locala;
    }

    private static int a(int var0, int var1, int var2, int var3) {
        if(var2 <= 3) {
            if((var3 & 1) != 0) {
                return -(var0 >> 1);
            }

            if((var3 & 8) != 0) {
                return -var0;
            }
        } else {
            if((var3 & 1) != 0) {
                return -(var1 >> 1);
            }

            if((var3 & 8) != 0) {
                return -var1;
            }
        }

        return 0;
    }

    private static int b(int var0, int var1, int var2, int var3) {
        if(var2 <= 3) {
            if((var3 & 2) != 0) {
                return -(var1 >> 1);
            }

            if((var3 & 32) != 0) {
                return -var1;
            }
        } else {
            if((var3 & 2) != 0) {
                return -(var0 >> 1);
            }

            if((var3 & 32) != 0) {
                return -var0;
            }
        }

        return 0;
    }
}
