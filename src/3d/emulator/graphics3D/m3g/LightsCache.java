package emulator.graphics3D.m3g;

import java.util.Vector;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.World;

public final class LightsCache {
    public static final float[] LOCAL_ORIGIN = new float[]{0.0F, 0.0F, 0.0F, 1.0F};
    public static final float[] POSITIVE_Z_AXIS = new float[]{0.0F, 0.0F, 1.0F, 0.0F};
    public static final float[] NEGATIVE_Z_AXIS = new float[]{0.0F, 0.0F, -1.0F, 0.0F};
    public static Vector m_lights = new Vector();
    public static Vector m_lightsTransform = new Vector();

    public static void getLightsInWorld(World var0) {
        resetLights();
        method799(var0, var0);
    }

    private static void method799(World var0, Group var1) {
        Transform var2 = new Transform();

        for (int var3 = 0; var3 < var1.getChildCount(); ++var3) {
            Node var4;
            if ((var4 = var1.getChild(var3)) instanceof Light && var4.getTransformTo(var0, var2)) {
                m_lights.add(var4);
                m_lightsTransform.add(new Transform(var2));
            } else if (var4 instanceof Group) {
                method799(var0, (Group) var4);
            }
        }

    }

    public static int addLight(Light var0, Transform var1) {
        if (var0 == null) {
            throw new NullPointerException();
        } else {
            m_lights.add(var0);
            if (var1 == null) {
                m_lightsTransform.add(new Transform());
            } else {
                m_lightsTransform.add(new Transform(var1));
            }

            return m_lights.size();
        }
    }

    public static void setLight(int var0, Light var1, Transform var2) {
        if (var0 >= 0 && var0 < getLightCount()) {
            m_lights.set(var0, var1);
            if (var2 == null) {
                m_lightsTransform.set(var0, new Transform());
            } else {
                m_lightsTransform.set(var0, new Transform(var2));
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public static void resetLights() {
        m_lights.clear();
        m_lightsTransform.clear();
    }

    public static int getLightCount() {
        return m_lights.size();
    }

    public static Light getLight(int var0, Transform var1) {
        if (var0 >= 0 && var0 < getLightCount()) {
            if (var1 != null) {
                if (m_lightsTransform.get(var0) == null) {
                    var1.setIdentity();
                } else {
                    var1.set((Transform) m_lightsTransform.get(var0));
                }
            }

            return (Light) m_lights.get(var0);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
}
