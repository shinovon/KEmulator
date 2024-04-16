package emulator.graphics3D.m3g;

import java.util.Vector;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.World;

public final class a {
    public static final float[] aFloatArray1149 = new float[]{0.0F, 0.0F, 0.0F, 1.0F};
    public static final float[] aFloatArray1151 = new float[]{0.0F, 0.0F, 1.0F, 0.0F};
    public static final float[] aFloatArray1153 = new float[]{0.0F, 0.0F, -1.0F, 0.0F};
    public static Vector aVector1150 = new Vector();
    public static Vector aVector1152 = new Vector();

    public static void method798(World var0) {
        method802();
        method799(var0, var0);
    }

    private static void method799(World var0, Group var1) {
        Transform var2 = new Transform();

        for (int var3 = 0; var3 < var1.getChildCount(); ++var3) {
            Node var4;
            if ((var4 = var1.getChild(var3)) instanceof Light && var4.getTransformTo(var0, var2)) {
                aVector1150.add(var4);
                aVector1152.add(new Transform(var2));
            } else if (var4 instanceof Group) {
                method799(var0, (Group) var4);
            }
        }

    }

    public static int method800(Light var0, Transform var1) {
        if (var0 == null) {
            throw new NullPointerException();
        } else {
            aVector1150.add(var0);
            if (var1 == null) {
                aVector1152.add(new Transform());
            } else {
                aVector1152.add(new Transform(var1));
            }

            return aVector1150.size();
        }
    }

    public static void method801(int var0, Light var1, Transform var2) {
        if (var0 >= 0 && var0 < method803()) {
            aVector1150.set(var0, var1);
            if (var2 == null) {
                aVector1152.set(var0, new Transform());
            } else {
                aVector1152.set(var0, new Transform(var2));
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public static void method802() {
        aVector1150.clear();
        aVector1152.clear();
    }

    public static int method803() {
        return aVector1150.size();
    }

    public static Light method804(int var0, Transform var1) {
        if (var0 >= 0 && var0 < method803()) {
            if (var1 != null) {
                if (aVector1152.get(var0) == null) {
                    var1.setIdentity();
                } else {
                    var1.set((Transform) aVector1152.get(var0));
                }
            }

            return (Light) aVector1150.get(var0);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
}
