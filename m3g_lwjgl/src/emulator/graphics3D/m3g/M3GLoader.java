package emulator.graphics3D.m3g;

import emulator.Emulator;
import emulator.custom.CustomJarResources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.Inflater;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.AnimationController;
import javax.microedition.m3g.AnimationTrack;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.CompositingMode;
import javax.microedition.m3g.Fog;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.IndexBuffer;
import javax.microedition.m3g.KeyframeSequence;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.MorphingMesh;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.SkinnedMesh;
import javax.microedition.m3g.Sprite3D;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.Transformable;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;
import javax.microedition.m3g.World;

public final class M3GLoader {
    static final byte[] aByteArray1080 = new byte[]{-85, 74, 83, 82, 49, 56, 52, -69, 13, 10, 26, 10};
    static final byte[] aByteArray1086 = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
    private static final Boolean aBoolean1081 = new Boolean(false);
    private static final Boolean aBoolean1087 = new Boolean(true);
    private Vector aVector1082 = new Vector();
    private Vector aVector1088 = new Vector();
    private Vector aVector1091 = new Vector();
    private Vector aVector1092 = null;
    private String aString1083;
    private String aString1089;
    private int anInt1084;
    private boolean aBoolean1085;
    private boolean aBoolean1090;

    public static Object3D[] load(String var0) throws IOException {
        if (var0 == null) {
            throw new NullPointerException();
        } else {
            try {
                return (new M3GLoader()).method746(var0);
            } catch (SecurityException var2) {
                throw var2;
            } catch (IOException var3) {
                throw var3;
            } catch (Exception var4) {
                throw new IOException(var4.toString());
            }
        }
    }

    public static Object3D[] load(byte[] var0, int var1) throws IOException {
        if (var0 == null) {
            throw new NullPointerException();
        } else {
            try {
                return (new M3GLoader()).method747(var0, var1);
            } catch (SecurityException var3) {
                throw var3;
            } catch (IOException var4) {
                throw var4;
            } catch (Exception var5) {
                throw new IOException(var5.toString());
            }
        }
    }

    private M3GLoader() {
    }

    private M3GLoader(Vector var1, String var2) {
        this.aString1089 = var2;
        this.aVector1091 = var1;
    }

    private Object3D[] method746(String var1) throws IOException {
        if (var1 == null) {
            throw new NullPointerException();
        } else if (this.method715(var1)) {
            throw new IOException("Reference loop detected.");
        } else {
            this.aString1083 = var1;
            this.aVector1091.addElement(var1);
            PeekInputStream var2;
            int var3 = method756(var2 = new PeekInputStream(this.method752(var1), 12));
            var2.rewind();
            Object3D[] var4 = this.method696(var2, var3);
            this.aVector1091.removeElement(var1);
            return var4;
        }
    }

    private Object3D[] method747(byte[] var1, int var2) throws IOException {
        if (var1 == null) {
            throw new NullPointerException("Resource byte array is null.");
        } else {
            int var3 = method730(var1, var2);
            ByteArrayInputStream var4 = new ByteArrayInputStream(var1, var2, var1.length - var2);
            return this.method696(var4, var3);
        }
    }

    private Object3D[] method696(InputStream var1, int var2) throws IOException {
        if (var2 == 1) {
            return this.method748(var1);
        } else if (var2 == 2) {
            return method707(var1);
        } else {
            throw new IOException("File not recognized.");
        }
    }

    private static Object3D[] method707(InputStream var0) throws IOException {
        return new Object3D[]{new Image2D(100, Image.createImage(var0))};
    }

    private Object3D[] method748(InputStream paramInputStream)
            throws IOException {
        paramInputStream.skip(aByteArray1080.length);
        while (method718(paramInputStream)) {
            this.anInt1084 += 1;
        }
        return method743();
    }

    private boolean method718(InputStream var1) throws IOException {
        if (this.anInt1084 > 1 && this.aBoolean1090 && !this.aBoolean1085) {
            throw new IOException("No external sections (" + this.aString1083 + ").");
        } else {
            AdlerInputStream var2;
            int var3;
            if ((var3 = method705(var2 = new AdlerInputStream(var1))) == -1) {
                return false;
            } else if (this.anInt1084 == 0 && var3 != 0) {
                throw new IOException("Compressed header (" + this.aString1083 + ").");
            } else {
                long var5 = method702(var2);
                long var7 = method702(var2);
                Object var9 = null;
                if (var3 == 0) {
                    var9 = var2;
                    if (var7 != var5 - 13L) {
                        Emulator.getEmulator().getLogStream().println("M3GLoader: Section length mismatch!!!");
                        return false;
                    }
                } else {
                    if (var3 != 1) {
                        Emulator.getEmulator().getLogStream().println("M3GLoader: Unrecognized compression schemeh(" + var3 + ")!!!");
                        return false;
                    }

                    byte[] var10 = new byte[(int) var5 - 13];
                    var2.read(var10);
                    byte[] var11 = new byte[(int) var7];
                    method721(var10, var11);
                    var9 = new CountedInputStream(new ByteArrayInputStream(var11));
                }

                ((CountedInputStream) var9).resetCounter();

                while ((long) ((CountedInputStream) var9).getCounter() < var7) {
                    this.method722(this.method719((CountedInputStream) var9));
                }

                if ((long) ((CountedInputStream) var9).getCounter() != var7) {
                    Emulator.getEmulator().getLogStream().println("M3GLoader: Section length mismatch!!!");
                    return false;
                } else {
                    long var12 = var2.getChecksum();
                    long var14 = method702(var2);
                    if (var12 != var14) {
                        throw new IOException("Checksum is wrong (" + this.aString1083 + ").");
                    } else {
                        return true;
                    }
                }
            }
        }
    }

    private Object3D method719(CountedInputStream var1) throws IOException {
        int var2 = method705(var1);
        long var3 = method702(var1);
        long var5 = (long) var1.getCounter() + var3;
        Object var7 = null;
        switch (var2) {
            case 0:
                if (this.anInt1084 != 0) {
                    throw new IOException("Header in wrong section (" + this.aString1083 + ").");
                }

                this.method744(var1);
                break;
            case 1:
                var7 = this.method717(var1);
                break;
            case 2:
                var7 = this.method740(var1);
                break;
            case 3:
                var7 = this.method700(var1);
                break;
            case 4:
                var7 = this.method742(var1);
                break;
            case 5:
                var7 = this.method720(var1);
                break;
            case 6:
                var7 = this.method741(var1);
                break;
            case 7:
                var7 = this.method731(var1);
                break;
            case 8:
                var7 = this.method708(var1);
                break;
            case 9:
                var7 = this.method699(var1);
                break;
            case 10:
                var7 = this.method739(var1);
                break;
            case 11:
                var7 = this.method703(var1);
                break;
            case 12:
                var7 = this.method729(var1);
                break;
            case 13:
                var7 = this.method711(var1);
                break;
            case 14:
                var7 = this.method712(var1);
                break;
            case 15:
                var7 = this.method706(var1);
                break;
            case 16:
                var7 = this.method738(var1);
                break;
            case 17:
                var7 = this.method734(var1);
                break;
            case 18:
                var7 = this.method701(var1);
                break;
            case 19:
                var7 = this.method727(var1);
                break;
            case 20:
                var7 = this.method713(var1);
                break;
            case 21:
                var7 = this.method723(var1);
                break;
            case 22:
                var7 = this.method714(var1);
                break;
            case 255:
                if (this.anInt1084 != 1) {
                    throw new IOException("External reference in wrong section (" + this.aString1083 + ").");
                }

                if (!this.aBoolean1090) {
                    throw new IOException("External links in self contained file (" + this.aString1083 + ").");
                }

                String var8 = method710(var1);
                this.aBoolean1085 = true;
                var7 = (new M3GLoader(this.aVector1091, this.aString1083)).method746(var8)[0];
                break;
            default:
                throw new IOException("Unrecognized object type " + var2 + " (" + this.aString1083 + ").");
        }

        if (var5 != (long) var1.getCounter()) {
            throw new IOException("Object length mismatch (" + this.aString1083 + ").");
        } else {
            this.method749((Object3D) var7);
            return (Object3D) var7;
        }
    }

    private void method722(Object3D var1) {
        if (var1 != null) {
            this.aVector1082.addElement(var1);
            this.aVector1088.addElement(aBoolean1081);
        }

    }

    private Object3D method726(int var1) {
        if (var1 == 0) {
            return null;
        } else if (var1 >= 2 && var1 - 2 < this.aVector1082.size()) {
            this.aVector1088.setElementAt(aBoolean1087, var1 - 2);
            return (Object3D) this.aVector1082.elementAt(var1 - 2);
        } else {
            throw new IllegalArgumentException("Invalid reference index (" + this.aString1083 + ").");
        }
    }

    private Object3D[] method743() {
        Vector var1 = new Vector();

        for (int var2 = 0; var2 < this.aVector1082.size(); ++var2) {
            if (this.aVector1088.elementAt(var2) == aBoolean1081) {
                var1.addElement(this.aVector1082.elementAt(var2));
            }
        }

        Object3D[] var4 = new Object3D[var1.size()];

        for (int var3 = 0; var3 < var1.size(); ++var3) {
            var4[var3] = (Object3D) var1.elementAt(var3);
        }

        return var4;
    }

    private void method744(InputStream paramInputStream)
            throws IOException {
        byte[] arrayOfByte = new byte[2];
        paramInputStream.read(arrayOfByte);
        this.aBoolean1090 = method750(paramInputStream);
        method702(paramInputStream);
        method702(paramInputStream);
        if ((arrayOfByte[0] != 1) || (arrayOfByte[1] != 0)) {
            throw new IOException("Invalid file version (" + this.aString1083 + ").");
        }
        method710(paramInputStream);
    }

    private void method736(Object3D var1, InputStream var2) throws IOException {
        var1.setUserID((int) method702(var2));
        long var3 = method702(var2);
        this.aVector1092 = new Vector();

        while (var3-- > 0L) {
            AnimationTrack var5;
            if ((var5 = (AnimationTrack) this.method728(var2)) == null) {
                throw new NullPointerException();
            }

            this.aVector1092.addElement(var5);
        }

        Hashtable var10 = new Hashtable();
        long var6 = method702(var2);

        while (var6-- > 0L) {
            int var8 = (int) method702(var2);
            byte[] var9 = new byte[(int) method702(var2)];
            var2.read(var9);
            var10.put(new Integer(var8), var9);
        }

        var1.setUserObject(var10);
    }

    private void method737(Transformable var1, InputStream var2) throws IOException {
        this.method736(var1, var2);
        if (method750(var2)) {
            var1.setTranslation(method733(var2), method733(var2), method733(var2));
            var1.setScale(method733(var2), method733(var2), method733(var2));
            var1.setOrientation(method733(var2), method733(var2), method733(var2), method733(var2));
        }

        if (method750(var2)) {
            var1.setTransform(method732(var2));
        }

    }

    private void method745(Node var1, InputStream var2) throws IOException {
        this.method737(var1, var2);
        var1.setRenderingEnable(method750(var2));
        var1.setPickingEnable(method750(var2));
        var1.setAlphaFactor((float) method705(var2) / 255.0F);
        var1.setScope((int) method702(var2));
        if (method750(var2)) {
            int var3 = method705(var2);
            int var4 = method705(var2);
            int var5 = (int) method702(var2);
            int var6 = (int) method702(var2);
            var1.setAlignment((Node) this.method726(var5), var3, (Node) this.method726(var6), var4);
        }

    }

    private void method716(Group var1, InputStream var2) throws IOException {
        this.method745(var1, var2);
        int var3 = (int) method702(var2);

        while (var3-- > 0) {
            var1.addChild((Node) this.method728(var2));
        }

    }

    private void method749(Object3D var1) {
        if (this.aVector1092 != null && var1 != null) {
            for (int var2 = 0; var2 < this.aVector1092.size(); ++var2) {
                var1.addAnimationTrack((AnimationTrack) this.aVector1092.elementAt(var2));
            }

            this.aVector1092 = null;
        }

    }

    private AnimationController method717(InputStream var1) throws IOException {
        AnimationController var2 = new AnimationController();
        this.method736(var2, var1);
        float var3 = method733(var1);
        float var4 = method733(var1);
        var2.setActiveInterval(method753(var1), method753(var1));
        float var5 = method733(var1);
        int var6 = method753(var1);
        var2.setPosition(var5, var6);
        var2.setSpeed(var3, var6);
        var2.setWeight(var4);
        return var2;
    }

    private AnimationTrack method740(InputStream var1) throws IOException {
        AnimationController var2 = new AnimationController();
        this.method736(var2, var1);
        KeyframeSequence var3 = (KeyframeSequence) this.method728(var1);
        AnimationController var4 = (AnimationController) this.method728(var1);
        int var5 = (int) method702(var1);
        AnimationTrack var6 = new AnimationTrack(var3, var5);
        copyObject3D(var2, var6);
        var6.setController(var4);
        return var6;
    }

    private Appearance method700(InputStream var1) throws IOException {
        Appearance var2 = new Appearance();
        this.method736(var2, var1);
        var2.setLayer(method705(var1));
        var2.setCompositingMode((CompositingMode) this.method728(var1));
        var2.setFog((Fog) this.method728(var1));
        var2.setPolygonMode((PolygonMode) this.method728(var1));
        var2.setMaterial((Material) this.method728(var1));
        int var3 = (int) method702(var1);

        for (int var4 = 0; var4 < var3; ++var4) {
            Texture2D var5;
            if ((var5 = (Texture2D) this.method728(var1)) == null) {
                throw new IOException("Null texture reference");
            }

            var2.setTexture(var4, var5);
        }

        return var2;
    }

    private Background method742(InputStream var1) throws IOException {
        Background var2 = new Background();
        this.method736(var2, var1);
        var2.setColor(method754(var1));
        Image2D var3 = (Image2D) this.method728(var1);
        var2.setImage(var3);
        var2.setImageMode(method705(var1), method705(var1));
        var2.setCrop(method753(var1), method753(var1), method753(var1), method753(var1));
        var2.setDepthClearEnable(method750(var1));
        var2.setColorClearEnable(method750(var1));
        return var2;
    }

    private Camera method720(InputStream var1) throws IOException {
        Camera var2 = new Camera();
        this.method745(var2, var1);
        int var3;
        if ((var3 = method705(var1)) == 48) {
            var2.setGeneric(method732(var1));
        } else if (var3 == 50) {
            var2.setPerspective(method733(var1), method733(var1), method733(var1), method733(var1));
        } else {
            if (var3 != 49) {
                throw new IOException("Projection type not recognized: " + var3 + "(" + this.aString1083 + ").");
            }

            var2.setParallel(method733(var1), method733(var1), method733(var1), method733(var1));
        }

        return var2;
    }

    private CompositingMode method741(InputStream var1) throws IOException {
        CompositingMode var2 = new CompositingMode();
        this.method736(var2, var1);
        var2.setDepthTestEnable(method750(var1));
        var2.setDepthWriteEnable(method750(var1));
        var2.setColorWriteEnable(method750(var1));
        var2.setAlphaWriteEnable(method750(var1));
        var2.setBlending(method705(var1));
        var2.setAlphaThreshold((float) method705(var1) / 255.0F);
        var2.setDepthOffset(method733(var1), method733(var1));
        return var2;
    }

    private Fog method731(InputStream var1) throws IOException {
        Fog var2 = new Fog();
        this.method736(var2, var1);
        var2.setColor(method755(var1));
        var2.setMode(method705(var1));
        if (var2.getMode() == 80) {
            var2.setDensity(method733(var1));
        } else if (var2.getMode() == 81) {
            var2.setLinear(method733(var1), method733(var1));
        }

        return var2;
    }

    private Group method699(InputStream var1) throws IOException {
        Group var2 = new Group();
        this.method716(var2, var1);
        return var2;
    }

    private Image2D method739(InputStream var1) throws IOException {
        AnimationController var2 = new AnimationController();
        this.method736(var2, var1);
        int var3 = method705(var1);
        boolean var4 = method750(var1);
        int var5 = (int) method702(var1);
        int var6 = (int) method702(var1);
        Image2D var7 = null;
        Image2D var10000;
        if (var4) {
            var10000 = new Image2D(var3, var5, var6);
        } else {
            byte[] var8;
            if ((var8 = new byte[(int) method702(var1)]).length > 0) {
                var1.read(var8);
            }

            byte[] var9 = new byte[(int) method702(var1)];
            var1.read(var9);
            var10000 = var8.length != 0 ? new Image2D(var3, var5, var6, var9, var8) : new Image2D(var3, var5, var6, var9);
        }

        var7 = var10000;
        copyObject3D(var2, var7);
        return var7;
    }

    private KeyframeSequence method727(InputStream var1) throws IOException {
        AnimationController var2 = new AnimationController();
        this.method736(var2, var1);
        int var3 = method705(var1);
        int var4 = method705(var1);
        int var5 = method705(var1);
        int var6 = (int) method702(var1);
        int var7 = (int) method702(var1);
        int var8 = (int) method702(var1);
        int var9 = (int) method702(var1);
        int var10 = (int) method702(var1);
        KeyframeSequence var11 = new KeyframeSequence(var10, var9, var3);
        copyObject3D(var2, var11);
        var11.setRepeatMode(var4);
        var11.setDuration(var6);
        var11.setValidRange(var7, var8);
        float[] var12 = new float[var9];
        int var15;
        if (var5 == 0) {
            for (int var13 = 0; var13 < var10; ++var13) {
                int var14 = method753(var1);

                for (var15 = 0; var15 < var9; ++var15) {
                    var12[var15] = method733(var1);
                }

                var11.setKeyframe(var13, var14, var12);
            }
        } else {
            if (var5 != 1 && var5 != 2) {
                throw new IOException("Encoding not recognized: " + var5 + "(" + this.aString1083 + ").");
            }

            float[] var19 = new float[var9];
            float[] var20 = new float[var9];

            for (var15 = 0; var15 < var9; ++var15) {
                var19[var15] = method733(var1);
            }

            for (var15 = 0; var15 < var9; ++var15) {
                var20[var15] = method733(var1);
            }

            for (var15 = 0; var15 < var10; ++var15) {
                int var16 = method753(var1);
                int var17;
                int var18;
                if (var5 == 1) {
                    for (var17 = 0; var17 < var9; ++var17) {
                        var18 = method705(var1);
                        var12[var17] = var19[var17] + var20[var17] * (float) var18 / 255.0F;
                    }
                } else {
                    for (var17 = 0; var17 < var9; ++var17) {
                        var18 = method751(var1);
                        var12[var17] = var19[var17] + var20[var17] * (float) var18 / 65535.0F;
                    }
                }

                var11.setKeyframe(var15, var16, var12);
            }
        }

        return var11;
    }

    private Light method729(InputStream var1) throws IOException {
        Light var2 = new Light();
        this.method745(var2, var1);
        var2.setAttenuation(method733(var1), method733(var1), method733(var1));
        var2.setColor(method755(var1));
        var2.setMode(method705(var1));
        var2.setIntensity(method733(var1));
        var2.setSpotAngle(method733(var1));
        var2.setSpotExponent(method733(var1));
        return var2;
    }

    private Material method711(InputStream var1) throws IOException {
        Material var2 = new Material();
        this.method736(var2, var1);
        var2.setColor(1024, method755(var1));
        var2.setColor(2048, method754(var1));
        var2.setColor(4096, method755(var1));
        var2.setColor(8192, method755(var1));
        var2.setShininess(method733(var1));
        var2.setVertexColorTrackingEnable(method750(var1));
        return var2;
    }

    private Mesh method712(InputStream var1) throws IOException {
        Group var2 = new Group();
        this.method745(var2, var1);
        VertexBuffer var3 = (VertexBuffer) this.method728(var1);
        int var4;
        IndexBuffer[] var5 = new IndexBuffer[var4 = (int) method702(var1)];
        Appearance[] var6 = new Appearance[var4];

        for (int var7 = 0; var7 < var4; ++var7) {
            var5[var7] = (IndexBuffer) this.method728(var1);
            var6[var7] = (Appearance) this.method728(var1);
        }

        Mesh var8 = new Mesh(var3, var5, var6);
        copyNode(var2, var8);
        return var8;
    }

    private MorphingMesh method706(InputStream var1) throws IOException {
        Mesh var2 = this.method712(var1);
        int var3;
        VertexBuffer[] var4 = new VertexBuffer[var3 = (int) method702(var1)];
        float[] var5 = new float[var3];

        int var6;
        for (var6 = 0; var6 < var3; ++var6) {
            var4[var6] = (VertexBuffer) this.method728(var1);
            var5[var6] = method733(var1);
        }

        IndexBuffer[] var7 = new IndexBuffer[var6 = var2.getSubmeshCount()];
        Appearance[] var8 = new Appearance[var6];

        for (int var9 = 0; var9 < var6; ++var9) {
            var7[var9] = var2.getIndexBuffer(var9);
            var8[var9] = var2.getAppearance(var9);
        }

        MorphingMesh var10 = new MorphingMesh(var2.getVertexBuffer(), var4, var7, var8);
        copyMesh(var2, var10);
        var10.setWeights(var5);
        return var10;
    }

    private PolygonMode method708(InputStream var1) throws IOException {
        PolygonMode var2 = new PolygonMode();
        this.method736(var2, var1);
        var2.setCulling(method705(var1));
        var2.setShading(method705(var1));
        var2.setWinding(method705(var1));
        var2.setTwoSidedLightingEnable(method750(var1));
        var2.setLocalCameraLightingEnable(method750(var1));
        var2.setPerspectiveCorrectionEnable(method750(var1));
        return var2;
    }

    private SkinnedMesh method738(InputStream var1) throws IOException {
        Mesh var2 = this.method712(var1);
        Group var3 = (Group) this.method728(var1);
        int var4;
        IndexBuffer[] var5 = new IndexBuffer[var4 = var2.getSubmeshCount()];
        Appearance[] var6 = new Appearance[var4];

        for (int var7 = 0; var7 < var4; ++var7) {
            var5[var7] = var2.getIndexBuffer(var7);
            var6[var7] = var2.getAppearance(var7);
        }

        SkinnedMesh var13 = new SkinnedMesh(var2.getVertexBuffer(), var5, var6, var3);
        copyMesh(var2, var13);
        int var8 = (int) method702(var1);

        while (var8-- > 0) {
            Node var9 = (Node) this.method728(var1);
            int var10 = (int) method702(var1);
            int var11 = (int) method702(var1);
            int var12 = method753(var1);
            var13.addTransform(var9, var12, var10, var11);
        }

        return var13;
    }

    private Sprite3D method701(InputStream var1) throws IOException {
        Group var2 = new Group();
        this.method745(var2, var1);
        Image2D var3 = (Image2D) this.method728(var1);
        Appearance var4 = (Appearance) this.method728(var1);
        boolean var5 = method750(var1);
        Sprite3D var6 = new Sprite3D(var5, var3, var4);
        copyNode(var2, var6);
        var6.setCrop(method753(var1), method753(var1), method753(var1), method753(var1));
        return var6;
    }

    private Texture2D method734(InputStream var1) throws IOException {
        Group var2 = new Group();
        this.method737(var2, var1);
        Texture2D var3 = new Texture2D((Image2D) this.method728(var1));
        copyTransformable(var2, var3);
        var3.setBlendColor(method755(var1));
        var3.setBlending(method705(var1));
        var3.setWrapping(method705(var1), method705(var1));
        var3.setFiltering(method705(var1), method705(var1));
        return var3;
    }

    private TriangleStripArray method703(InputStream var1) throws IOException {
        AnimationController var2;
        int var3;
        int var4;
        int[] var5;
        var2 = new AnimationController();
        this.method736(var2, var1);
        var3 = method705(var1);
        var4 = 0;
        var5 = null;
        int var6;
        label54:
        switch (var3) {
            case 0:
                var4 = (int) method702(var1);
                break;
            case 1:
                var4 = method705(var1);
                break;
            case 2:
                var4 = method751(var1);
                break;
            case 128:
                var5 = new int[(int) method702(var1)];
                var6 = 0;

                while (true) {
                    if (var6 >= var5.length) {
                        break label54;
                    }

                    var5[var6] = (int) method702(var1);
                    ++var6;
                }
            case 129:
                var5 = new int[(int) method702(var1)];
                var6 = 0;

                while (true) {
                    if (var6 >= var5.length) {
                        break label54;
                    }

                    var5[var6] = method705(var1);
                    ++var6;
                }
            case 130:
                var5 = new int[(int) method702(var1)];
                var6 = 0;

                while (true) {
                    if (var6 >= var5.length) {
                        break label54;
                    }

                    var5[var6] = method751(var1);
                    ++var6;
                }
            default:
                throw new IllegalArgumentException("Invalid TriangleStripArray encoding (" + this.aString1083 + ").");
        }

        int[] var9 = new int[(int) method702(var1)];

        for (int var7 = 0; var7 < var9.length; ++var7) {
            var9[var7] = (int) method702(var1);
        }

        TriangleStripArray var8 = null;
        var8 = var3 != 0 && var3 != 1 && var3 != 2 ? new TriangleStripArray(var5, var9) : new TriangleStripArray(var4, var9);
        copyObject3D(var2, var8);
        return var8;
    }

    private VertexArray method713(InputStream var1) throws IOException {
        AnimationController var2 = new AnimationController();
        this.method736(var2, var1);
        int var3 = method705(var1);
        int var4 = method705(var1);
        int var5 = method705(var1);
        int var6 = method751(var1);
        if (var5 != 0 && var5 != 1) {
            throw new IllegalArgumentException("Invalid VertexArray encoding (" + this.aString1083 + ").");
        } else {
            VertexArray var7 = new VertexArray(var6, var4, var3);
            int[] var8 = new int[var4];
            int var10;
            int var11;
            if (var3 == 1) {
                byte[] var9 = new byte[var4];
                if (var5 == 0) {
                    for (var10 = 0; var10 < var6; ++var10) {
                        for (var11 = 0; var11 < var4; ++var11) {
                            var9[var11] = (byte) method705(var1);
                        }

                        var7.set(var10, 1, var9);
                    }
                } else {
                    for (var10 = 0; var10 < var6; ++var10) {
                        for (var11 = 0; var11 < var4; ++var11) {
                            var8[var11] += (byte) method705(var1);
                            var9[var11] = (byte) var8[var11];
                        }

                        var7.set(var10, 1, var9);
                    }
                }
            } else {
                short[] var12 = new short[var4];
                if (var5 == 0) {
                    for (var10 = 0; var10 < var6; ++var10) {
                        for (var11 = 0; var11 < var4; ++var11) {
                            var12[var11] = (short) method751(var1);
                        }

                        var7.set(var10, 1, var12);
                    }
                } else {
                    for (var10 = 0; var10 < var6; ++var10) {
                        for (var11 = 0; var11 < var4; ++var11) {
                            var8[var11] += (short) method751(var1);
                            var12[var11] = (short) var8[var11];
                        }

                        var7.set(var10, 1, var12);
                    }
                }
            }

            copyObject3D(var2, var7);
            return var7;
        }
    }

    private VertexBuffer method723(InputStream var1) throws IOException {
        VertexBuffer var2 = new VertexBuffer();
        this.method736(var2, var1);
        var2.setDefaultColor(method754(var1));
        VertexArray var3 = (VertexArray) this.method728(var1);
        float[] var4 = new float[3];

        for (int var5 = 0; var5 < 3; ++var5) {
            var4[var5] = method733(var1);
        }

        float var13 = method733(var1);
        if (var3 != null) {
            var2.setPositions(var3, var13, var4);
        }

        VertexArray var6;
        if ((var6 = (VertexArray) this.method728(var1)) != null) {
            var2.setNormals(var6);
        }

        VertexArray var7;
        if ((var7 = (VertexArray) this.method728(var1)) != null) {
            var2.setColors(var7);
        }

        int var8 = (int) method702(var1);

        for (int var9 = 0; var9 < var8; ++var9) {
            VertexArray var10;
            if ((var10 = (VertexArray) this.method728(var1)) == null) {
                throw new IOException("Null texture vertex array");
            }

            for (int var11 = 0; var11 < 3; ++var11) {
                var4[var11] = method733(var1);
            }

            float var12 = method733(var1);
            var2.setTexCoords(var9, var10, var12, var4);
        }

        return var2;
    }

    private World method714(InputStream var1) throws IOException {
        World var2 = new World();
        this.method716(var2, var1);
        Camera var3;
        if ((var3 = (Camera) this.method728(var1)) != null) {
            var2.setActiveCamera(var3);
        }

        var2.setBackground((Background) this.method728(var1));
        return var2;
    }

    private Object3D method728(InputStream var1) throws IOException {
        return this.method726((int) method702(var1));
    }

    private static final Transform method732(InputStream var0) throws IOException {
        Transform var1 = new Transform();
        float[] var2 = new float[16];

        for (int var3 = 0; var3 < 16; ++var3) {
            var2[var3] = method733(var0);
        }

        var1.set(var2);
        return var1;
    }

    private static final int method705(InputStream var0) throws IOException {
        return var0.read();
    }

    private static boolean method750(InputStream var0) throws IOException {
        int var1;
        if ((var1 = var0.read()) == 0) {
            return false;
        } else if (var1 != 1) {
            throw new IOException("Malformed boolean.");
        } else {
            return true;
        }
    }

    private static int method751(InputStream var0) throws IOException {
        return var0.read() + (var0.read() << 8);
    }

    private static final int method753(InputStream var0) throws IOException {
        return var0.read() + (var0.read() << 8) + (var0.read() << 16) + (var0.read() << 24);
    }

    private static final long method702(InputStream var0) throws IOException {
        return (long) var0.read() + ((long) var0.read() << 8) + ((long) var0.read() << 16) + ((long) var0.read() << 24);
    }

    private static final float method733(InputStream var0) throws IOException {
        int var1;
        if (((var1 = method753(var0)) & 2139095040) != 2139095040 && var1 != Integer.MIN_VALUE && ((var1 & 8388607) == 0 || (var1 & 2139095040) != 0)) {
            return Float.intBitsToFloat(var1);
        } else {
            throw new IOException("Malformed float.");
        }
    }

    private static int method754(InputStream var0) throws IOException {
        return (var0.read() << 16) + (var0.read() << 8) + var0.read() + (var0.read() << 24);
    }

    private static int method755(InputStream var0) throws IOException {
        return (var0.read() << 16) + (var0.read() << 8) + var0.read();
    }

    private static String method710(InputStream var0) throws IOException {
        StringBuffer var1 = new StringBuffer();

        int var2;
        for (InputStream var10000 = var0; (var2 = var10000.read()) != 0; var10000 = var0) {
            if ((var2 & 128) == 0) {
                var1.append((char) (var2 & 255));
            } else {
                int var3;
                if ((var2 & 224) == 192) {
                    if (((var3 = var0.read()) & 192) != 128) {
                        throw new IOException("Invalid UTF-8 string.");
                    }

                    var1.append((char) ((var2 & 31) << 6 | var3 & 63));
                } else {
                    if ((var2 & 240) != 224) {
                        throw new IOException("Invalid UTF-8 string.");
                    }

                    var3 = var0.read();
                    int var4 = var0.read();
                    if ((var3 & 192) != 128 || (var4 & 192) != 128) {
                        throw new IOException("Invalid UTF-8 string.");
                    }

                    var1.append((char) ((var2 & 15) << 12 | (var3 & 63) << 6 | var4 & 63));
                }
            }
        }

        return var1.toString();
    }

    private static int method730(byte[] var0, int var1) {
        byte[] var2 = aByteArray1086;
        int var3 = 0;

        int var4;
        for (var4 = 0; var3 < var2.length; ++var3) {
            if (var0[var3 + var1] != var2[var3]) {
                ++var4;
            }
        }

        if (var4 == 0) {
            return 2;
        } else {
            var2 = aByteArray1080;
            var3 = 0;

            for (var4 = 0; var3 < var2.length; ++var3) {
                if (var0[var3 + var1] != var2[var3]) {
                    ++var4;
                }
            }

            if (var4 == 0) {
                return 1;
            } else {
                Emulator.getEmulator().getLogStream().println("M3GLoader:Invalid file type, use png instead");
                return 2;
            }
        }
    }

    private static int method756(InputStream var0) throws IOException {
        byte[] var1 = new byte[12];
        var0.read(var1);
        return method730(var1, 0);
    }

    private boolean method715(String var1) {
        for (int var2 = 0; var2 < this.aVector1091.size(); ++var2) {
            if (((String) this.aVector1091.elementAt(var2)).equals(var1)) {
                return true;
            }
        }

        return false;
    }

    private static InputStream method709(String var0) throws IOException {
        InputConnection var1;
        HttpConnection var2;
        String var3;
        if ((var1 = (InputConnection) Connector.open(var0)) instanceof HttpConnection && (var3 = (var2 = (HttpConnection) var1).getHeaderField("Content-Type")) != null && !var3.equals("application/m3g") && !var3.equals("image/png")) {
            throw new IOException("Wrong MIME type: " + var3);
        } else {
            return var1.openInputStream();
        }
    }

    private InputStream method752(String var1) throws IOException {
        if (var1.indexOf(58) != -1) {
            return method709(var1);
        } else if (var1.charAt(0) == 47) {
            return CustomJarResources.getResourceAsStream(var1);
        } else if (this.aString1089 == null) {
            throw new IOException("Relative URI.");
        } else {
            String var2;
            return (var2 = this.aString1089.substring(0, this.aString1089.lastIndexOf(47) + 1) + var1).charAt(0) == 47 ? CustomJarResources.getResourceAsStream(var2) : method709(var2);
        }
    }

    private static void copyObject3D(Object3D var0, Object3D var1) {
        var1.setUserObject(var0.getUserObject());
        var1.setUserID(var0.getUserID());
    }

    private static void copyNode(Node var0, Node var1) {
        copyTransformable(var0, var1);
        var1.setAlphaFactor(var0.getAlphaFactor());
        var1.setScope(var0.getScope());
        var1.setPickingEnable(var0.isPickingEnabled());
        var1.setRenderingEnable(var0.isRenderingEnabled());
    }

    private static void copyTransformable(Transformable var0, Transformable var1) {
        copyObject3D(var0, var1);
        float[] var2 = new float[4];
        Transform var3 = new Transform();
        var0.getTranslation(var2);
        var1.setTranslation(var2[0], var2[1], var2[2]);
        var0.getScale(var2);
        var1.setScale(var2[0], var2[1], var2[2]);
        var0.getOrientation(var2);
        var1.setOrientation(var2[0], var2[1], var2[2], var2[3]);
        var0.getTransform(var3);
        var1.setTransform(var3);
    }

    private static void copyMesh(Mesh var0, Mesh var1) {
        copyNode(var0, var1);
    }

    private static void method721(byte[] var0, byte[] var1) {
        try {
            Inflater var2;
            (var2 = new Inflater(false)).setInput(var0);
            var2.inflate(var1);
            var2.end();
        } catch (Exception e) {
            Emulator.getEmulator().getLogStream().println("m3g upzip error");
        }
    }
}
