package emulator.debug;

import emulator.*;

import javax.microedition.lcdui.*;

import com.nokia.mid.sound.*;

import javax.microedition.m3g.*;

import emulator.graphics2D.*;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import emulator.ui.swt.EmulatorScreen;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import com.samsung.util.AudioClip;

import javax.microedition.media.*;
import javax.microedition.media.control.*;
import emulator.media.vlc.VLCPlayerImpl;

public final class Memory {
    public Hashtable table;
    public Vector aVector1459;
    public Vector aVector1461;
    public Vector aVector1463;
    public Vector players;
    public Vector aVector1467;
    private Vector checkClasses;
    static Class _J;
    static Class _I;
    static Class _S;
    static Class _B;
    static Class _Z;
    static Class _F;
    static Class _D;
    static Class _C;
    static Class lStringCls;
    static Class InputStreamCls;
    static Class ImageCls;
    static Class Image2DCls;

    public Memory() {
        super();
        this.table = new Hashtable();
        this.aVector1459 = new Vector();
        this.aVector1461 = new Vector();
        this.aVector1463 = new Vector();
        this.players = new Vector();
        this.aVector1467 = new Vector();
        checkClasses = new Vector();
        checkClasses.add("javax.microedition.lcdui.ImageItem");
        checkClasses.add("javax.microedition.lcdui.CustomItem");
        checkClasses.add("javax.microedition.lcdui.List");
        checkClasses.add("javax.microedition.lcdui.ChoiceGroup");
        checkClasses.add("javax.microedition.lcdui.Display");
        checkClasses.add("javax.microedition.lcdui.Form");
        checkClasses.add("javax.microedition.lcdui.Graphics");
    }

    public final void method846() {
        if (Settings.recordReleasedImg) {
            for (int i = 0; i < this.aVector1461.size(); ++i) {
                if (!this.aVector1463.contains(this.aVector1461.get(i))) {
                    this.aVector1463.addElement(this.aVector1461.get(i));
                }
            }
        }
        this.table.clear();
        this.aVector1459.clear();
        this.aVector1461.clear();
        this.players.clear();
        this.aVector1467.clear();
        for (Player p : PlayerImpl.players)
            this.players.add(p);
        for (int j = 0; j < Emulator.jarClasses.size(); ++j) {
            final String s = (String) Emulator.jarClasses.get(j);
            Memory a;
            Class clazz = null;
            Object o;
            if (Emulator.getMIDlet().getClass().getName().equals(s)) {
                a = this;
                clazz = cls(s);
                o = Emulator.getMIDlet();
            } else if (Emulator.getCurrentDisplay() != null && Emulator.getCurrentDisplay().getCurrent() != null && Emulator.getCurrentDisplay().getCurrent().getClass().getName().equals(s)) {
                a = this;
                clazz = cls(s);
                o = Emulator.getCurrentDisplay().getCurrent();
            } else {
                a = this;
                try {
                    clazz = cls(s);
                } catch (Exception e) {

                } catch (Error e) {

                }
                o = null;
            }
            if (clazz != null)
                a.method847(clazz, o, s, false);
        }
        for (int j = 0; j < checkClasses.size(); ++j) {
            Memory a;
            Class clazz = null;
            Object o;
            final String s = (String) checkClasses.get(j);
            {
                a = this;
                try {
                    clazz = cls(s);
                } catch (Exception e) {

                } catch (Error e) {

                }
                o = null;
            }
            if (clazz != null)
                a.method847(clazz, o, s, false);
        }
    }

    private static Class strC() {
        return lStringCls != null ? lStringCls : (lStringCls = cls("java.lang.String"));
    }

    private void method847(final Class clazz, final Object o, final String s, boolean vector) {
        String s2 = clazz.getName();
        if (clazz.isArray()) {
            s2 = ClassTypes.method869(clazz);
        }
        ClassInfo classInfo = (ClassInfo) this.table.get(s2);
        if (clazz.isInterface()) {
            return;
        }
        if (classInfo == null) {
            classInfo = new ClassInfo(this, clazz.getName());
            this.table.put(s2, classInfo);
        } else if (o == null) {
            return;
        }
        if (o != null) {
            try {
                if (this.aVector1459.contains(o)) {
                    return;
                }
            } catch (Exception e) {

            }
            final ClassInfo classInfo2 = classInfo;
            ++classInfo2.anInt1485;
            classInfo.objs.add(new ObjInstance(this, s, o));
            this.aVector1459.add(o);
            if (o instanceof Image) {
                this.aVector1461.add(o);
                if (Settings.recordReleasedImg && this.aVector1463.contains(o)) {
                    this.aVector1463.removeElement(o);
                }
            } else if (o instanceof Sound || o instanceof AudioClip || o instanceof Player) {
                if (!PlayerImpl.players.contains(o))
                    this.players.add(o);
            } else if (o instanceof Node) {
                this.aVector1467.add(o);
            } else {
                final IImage method844;
                if (o instanceof Image2D && (method844 = f.method844((Image2D) o)) != null) {
                    this.aVector1461.add(new f(method844));
                }
            }
        }
        if (o != null && clazz.isArray()) {
            Class clazz2 = clazz;
            Class componentType;
            while ((componentType = clazz2.getComponentType()).getComponentType() != null) {
                clazz2 = componentType;
            }
            //if (!ClassTypes.method871(componentType) && componentType != strC()) {
            //    return;
            //}
            for (int i = 0; i < Array.getLength(o); ++i) {
                final Object value;
                if ((value = Array.get(o, i)) != null) {
                    this.method847(value.getClass(), value, s + '[' + i + ']', true);
                }
            }
        } else {
            if (o != null && o instanceof Vector) {
                final Enumeration<Object> elements = (Enumeration<Object>) ((Vector) o).elements();
                while (elements.hasMoreElements()) {
                    final Object nextElement;
                    if ((nextElement = elements.nextElement()) != null) {
                        this.method847(nextElement.getClass(), nextElement, s + "(VectorElement)", true);
                    }
                }
                return;
            }
            if (o instanceof Hashtable) {
                final Enumeration<Object> keys = (Enumeration<Object>) ((Hashtable) o).keys();
                while (keys.hasMoreElements()) {
                    final Object nextElement2 = keys.nextElement();
                    final Object value2;
                    if ((value2 = ((Hashtable) o).get(nextElement2)) != null) {
                        this.method847(value2.getClass(), value2, s + "(HashtableKey=" + nextElement2 + ")", true);
                    }
                }
                return;
            }
            if (o instanceof Object3D) {
                final Field[] method845 = fields(clazz);
                for (int j = 0; j < method845.length; ++j) {
                    final String name = method845[j].getName();
                    method845[j].setAccessible(true);
                    final Object method846 = ClassTypes.getFieldValue(o, method845[j]);
                    final String string = s + '.' + name;
                    if (!method845[j].getType().isPrimitive() && method846 != null) {
                        this.method847(method846.getClass(), method846, string, false);
                    }
                }
                return;
            }
            if (Emulator.jarClasses.contains(clazz.getName()) || vector || checkClasses.contains(clazz.getName()) || ((Memory.InputStreamCls != null) ? Memory.InputStreamCls : (Memory.InputStreamCls = cls("java.io.InputStream"))).isAssignableFrom(clazz)) {
                final Field[] f = fields(clazz);
                for (int k = 0; k < f.length; ++k) {
                    final String name2 = f[k].getName();
                    //if((o instanceof Item || o instanceof Screen) && f[k].getType() == int[].class) continue;
                    if (!Modifier.isFinal(f[k].getModifiers()) || !f[k].getType().isPrimitive()) {
                        f[k].setAccessible(true);
                        final Object method848 = ClassTypes.getFieldValue(o, f[k]);
                        final String string2 = s + '.' + name2;
                        if (!f[k].getType().isPrimitive() && method848 != null) {
                            this.method847(method848.getClass(), method848, string2, false);
                        }
                    }
                }
            }
        }
    }

    private static Field[] fields(final Class clazz) {
        final Vector vector = new Vector<Field>();
        method849(clazz, vector);
        final Field[] array = new Field[vector.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (Field) vector.get(i);
        }
        return array;
    }

    private static void method849(final Class clazz, final Vector vector) {
        try {
            if (clazz.getSuperclass() != null) {
                method849(clazz.getSuperclass(), vector);
            }
            final Field[] declaredFields = clazz.getDeclaredFields();
            for (int i = 0; i < declaredFields.length; ++i) {
                vector.add(declaredFields[i]);
            }
        } catch (Error error) {
        }
    }

    public static int bytecodeSize() {
        int n = 0;
        try {
            if (Emulator.midletJar != null) {
                final ZipFile zipFile = new ZipFile(Emulator.midletJar);
                final Enumeration<String> elements = Emulator.jarClasses.elements();
                while (elements.hasMoreElements()) {
                    final String s;
                    try {
                        if (!cls(s = elements.nextElement()).isInterface()) {
                            n += (int) ((ZipEntry) zipFile.getEntry(s.replace('.', '/') + ".class")).getSize();
                        }
                    } catch (Exception e) {

                    } catch (Error e) {

                    }
                }
            } else {
                final Enumeration<String> elements2 = Emulator.jarClasses.elements();
                while (elements2.hasMoreElements()) {
                    final String s2;
                    if (!cls(s2 = elements2.nextElement()).isInterface()) {
                        n += (int) Emulator.getFileFromClassPath(s2.replace('.', '/') + ".class").length();
                    }
                }
            }
        } catch (Exception ex) {
        }
        return n;
    }

    public final int objectsSize() {
        int n = 0;
        final Enumeration<ClassInfo> elements = this.table.elements();
        while (elements.hasMoreElements()) {
            n += elements.nextElement().size();
        }
        return n;
    }

    public static String playerType(final Object o) {
        if (o instanceof Sound) {
            return ((Sound) o).getType();
        }
        if (o instanceof AudioClip) {
            return "MMF";
        }
        return ((Player) o).getContentType();
    }

    public static String playerStateStr(final Object o) {
        if (o instanceof Sound) {
            switch (((Sound) o).getState()) {
                case 0: {
                    return "SOUND_PLAYING";
                }
                case 1: {
                    return "SOUND_STOPPED";
                }
                case 3: {
                    return "SOUND_UNINITIALIZED";
                }
                default: {
                    return "INVALID STATE";
                }
            }
        } else if (o instanceof AudioClip) {
            switch (((AudioClip) o).getStatus()) {
                case 1: {
                    return "SOUND_PLAY";
                }
                case 2: {
                    return "SOUND_PAUSE";
                }
                case 0: {
                    return "SOUND_STOP";
                }
                default: {
                    return "INVALID STATE";
                }
            }
        } else {
            switch (((Player) o).getState()) {
                case 0: {
                    return "CLOSED";
                }
                case 300: {
                    return "PREFETCHED";
                }
                case 200: {
                    return "REALIZED";
                }
                case 400: {
                    return "STARTED";
                }
                case 100: {
                    return "UNREALIZED";
                }
                default: {
                    return "INVALID STATE";
                }
            }
        }
    }

    public static int loopCount(final Object o) {
        if (o instanceof Sound) {
            return ((Sound) o).m_player.loopCount;
        }
        if (o instanceof AudioClip) {
            return ((AudioClip) o).loopCount;
        }
        if (!(o instanceof PlayerImpl)) {
            return 0;
        }
        return ((PlayerImpl) o).loopCount;
    }

    public static int dataLen(final Object o) {
        if (o instanceof Sound) {
            return ((Sound) o).dataLen;
        }
        if (o instanceof AudioClip) {
            return ((AudioClip) o).dataLen;
        }
        if (o instanceof VLCPlayerImpl) {
            return ((VLCPlayerImpl) o).dataLen;
        }
        if (!(o instanceof PlayerImpl)) {
            return 0;
        }
        return ((PlayerImpl) o).dataLen;
    }

    public static int progress(final Object o) {
        try {
            if (o instanceof Sound) {
                final Sound sound = (Sound) o;
                return (int) (sound.m_player.getMediaTime() * 100L / (sound.m_player.getDuration() / 1000L));
            }
            if (o instanceof AudioClip) {
                return 0;
            }

            if (o instanceof VLCPlayerImpl) {
                final VLCPlayerImpl v = (VLCPlayerImpl) o;
                return (int) (((double) v.getMediaTime() / (double) v.getDuration()) * 100D);
            }
            if (!(o instanceof PlayerImpl)) {
                return 0;
            }
            final PlayerImpl playerImpl = (PlayerImpl) o;
            return (int) (((double) playerImpl.getMediaTime() / (double) playerImpl.getDuration()) * 100D);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static int volume(final Object o) {
        try {
            if (o instanceof Sound) {
                return ((Sound) o).getGain();
            }
            if (o instanceof AudioClip) {
                return ((AudioClip) o).volume * 20;
            }

            if (o instanceof VLCPlayerImpl) {
                return ((VolumeControlImpl) ((VLCPlayerImpl) o).getControl("VolumeControl")).getLevel();
            }
            if (!(o instanceof PlayerImpl)) {
                return 0;
            }
            return ((VolumeControlImpl) ((PlayerImpl) o).getControl("VolumeControl")).getLevel();
        } catch (Exception ex) {
            return 0;
        }
    }

    public static void setVolume(final Object o, final int n) {
        try {
            if (o instanceof Sound) {
                ((Sound) o).setGain(n);
            } else if (!(o instanceof AudioClip)) {

                if (o instanceof VLCPlayerImpl) {
                    ((VolumeControlImpl) ((VLCPlayerImpl) o).getControl("VolumeControl")).setLevel(n);
                    return;
                }
                if (!(o instanceof PlayerImpl)) {
                    return;
                }
                ((VolumeControlImpl) ((PlayerImpl) o).getControl("VolumeControl")).setLevel(n);
            }
        } catch (Exception ex) {
        }
    }

    public static void playerAct(final Object o, final int n) {
        if (o instanceof Sound) {
            final Sound sound = (Sound) o;
            try {
                switch (n) {
                    case 0: {
                        sound.resume();
                        break;
                    }
                    case 1: {
                        final long mediaTime = sound.m_player.getMediaTime();
                        sound.stop();
                        sound.m_player.setMediaTime(mediaTime);
                        break;
                    }
                    case 2: {
                        sound.stop();
                        break;
                    }
                    case 3: {
                        try {
                            byte[] b = sound.getData();
                            String s = sound.getExportName();
                            if (b != null) {
                                exportAudio(b, sound.getExportName());
                                ((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Saved: " + s);
                            } else {
                                ((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export failed: unsupported stream type");
                            }
                        } catch (Exception e) {
                            ((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export failed: " + e.toString());
                        }
                        break;
                    }
                }
            } catch (Exception ex) {
            }
            return;
        }
        if (o instanceof AudioClip) {
            final AudioClip audioClip = (AudioClip) o;
            switch (n) {
                case 0: {
                    audioClip.play(audioClip.loopCount, audioClip.volume);
                    break;
                }
                case 1: {
                    audioClip.pause();
                    break;
                }
                case 2: {
                    audioClip.stop();
                    break;
                }
                case 3: {
                    try {
                        byte[] b = audioClip.getData();
                        String s = audioClip.getExportName();
                        if (b != null) {
                            exportAudio(b, s);
                            ((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Saved: " + s);
                        } else {
                            ((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export failed: unsupported stream type");
                        }
                    } catch (Exception e) {
                        ((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export failed: " + e.toString());
                    }
                    break;
                }
            }
            return;
        }
        if (o instanceof VLCPlayerImpl) {
            final VLCPlayerImpl v = (VLCPlayerImpl) o;
            try {
                switch (n) {
                    case 0: {
                        v.start();
                        break;
                    }
                    case 1: {
                        final long mediaTime2 = v.getMediaTime();
                        v.stop();
                        v.setMediaTime(mediaTime2);
                    }
                    case 2: {
                        v.stop();
                        break;
                    }
                    case 3: {
                        ((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export not supported!");
                        break;
                    }
                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
            return;
        }
        if (!(o instanceof PlayerImpl)) {
            return;
        }
        final PlayerImpl playerImpl = (PlayerImpl) o;
        try {
            switch (n) {
                case 0: {
                    playerImpl.start();
                    break;
                }
                case 1: {
                    final long mediaTime2 = playerImpl.getMediaTime();
                    playerImpl.stop();
                    playerImpl.setMediaTime(mediaTime2);
                    break;
                }
                case 2: {
                    playerImpl.stop();
                    break;
                }
                case 3: {
                    try {
                        byte[] b = playerImpl.getData();
                        String s = "audio" + playerImpl.getExportName();
                        if (b != null) {
                            exportAudio(b, s);
                            ((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Saved: " + s);
                        } else {
                            ((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export failed: unsupported stream type");
                        }
                    } catch (Exception e) {
                        ((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export failed: " + e.toString());
                    }
                }
            }
        } catch (Exception ex2) {
        }
    }

    public static void exportAudio(byte[] b, String name) throws IOException {
        File f = new File(Emulator.getAbsolutePath() + "/" + name);
        if (f.exists()) return;
        f.createNewFile();
        DataOutputStream o = new DataOutputStream(new FileOutputStream(f));
        o.write(b);
        o.close();
    }

    public final int method866(final Object o) {
        try {
            return ((ClassInfo) this.table.get(o)).anInt1485;
        } catch (NullPointerException e) {
        }
        return 0;
    }

    public final int method867(final Object o) {
        try {
            return ((ClassInfo) this.table.get(o)).size();
        } catch (NullPointerException e) {
        }
        return 0;
    }

    public final Vector objs(final Object o) {
        return ((ClassInfo) this.table.get(o)).objs;
    }

    public static String refs(final Object o) {
        return ((ObjInstance) o).ref;
    }

    public static Object val(final Object o) {
        return ((ObjInstance) o).val;
    }

    public static int size(final Object o) {
        return ((ObjInstance) o).size;
    }

    public final int size(Class c, Object o, String s) {
        int i = size(c, o);
        return i;
    }

    public final int size(final Class cls, final Object o) {
        final Field[] fields = fields(cls);
        int res = 0;
        for (int i = 0; i < fields.length; ++i) {
            final Field field;
            final Class<?> type = (field = fields[i]).getType();
            if ((!Modifier.isFinal(field.getModifiers()) || !type.isPrimitive()) && (!Modifier.isStatic(field.getModifiers()) || o == null)) {
                if (Modifier.isStatic(field.getModifiers()) || o != null) {
                    if (type == Long.TYPE || type == Double.TYPE) {
                        res += 24;
                    } else {
                        res += 16;
                    }
                }
            }
        }
        if (o != null) {
            int len;
            int size2;
            if (cls.isArray()) {
                len = res;
                size2 = this.arraySize(cls, o);
            } else {
                res += 12;
                if (cls == ((Memory.lStringCls != null) ? Memory.lStringCls : (Memory.lStringCls = cls("java.lang.String")))) {
                    len = res + 2 + ((String) o).length();
                } else {
                    if (cls == ((Memory.ImageCls != null) ? Memory.ImageCls : (Memory.ImageCls = cls("javax.microedition.lcdui.Image")))) {
                        final Image image = (Image) o;
                        len = res + image.size();
                    } else {
                        if (cls != ((Memory.Image2DCls != null) ? Memory.Image2DCls : (Memory.Image2DCls = cls("javax.microedition.m3g.Image2D")))) {
                            /*if(!(cls == Vector.class || cls == Hashtable.class 
                            		|| cls == StringItem.class || cls == Command.class 
                            		|| cls == cls("javax.microedition.lcdui.a")
                            		))
                            	return res + o.toString().length();
                            else */
                            return res;
                        }
                        final Image2D image2D = (Image2D) o;
                        len = res + image2D.size();
                    }
                }
            }
            res = len;
        }
        return res;
    }

    private int arraySize(final Class clazz, final Object o) {
        int n = 0;
        n += 16;
        if (clazz == ((Memory._J != null) ? Memory._J : (Memory._J = cls("[J")))) {
            n = 16 + 8 * Array.getLength(o);
        } else if (clazz == ((Memory._I != null) ? Memory._I : (Memory._I = cls("[I")))) {
            n = 16 + 4 * Array.getLength(o);
        } else if (clazz == ((Memory._S != null) ? Memory._S : (Memory._S = cls("[S")))) {
            n = 16 + 2 * Array.getLength(o);
        } else if (clazz == ((Memory._B != null) ? Memory._B : (Memory._B = cls("[B")))) {
            n = 16 + 1 * Array.getLength(o);
        } else if (clazz == ((Memory._Z != null) ? Memory._Z : (Memory._Z = cls("[Z")))) {
            n = 16 + 4 * Array.getLength(o);
        } else if (clazz == ((Memory._D != null) ? Memory._D : (Memory._D = cls("[D")))) {
            n = 16 + 8 * Array.getLength(o);
        } else if (clazz == ((Memory._F != null) ? Memory._F : (Memory._F = cls("[F")))) {
            n = 16 + 4 * Array.getLength(o);
        } else if (clazz == ((Memory._C != null) ? Memory._C : (Memory._C = cls("[C")))) {
            n = 16 + 1 * Array.getLength(o);
        } else {
            for (int i = Array.getLength(o) - 1; i >= 0; --i) {
                final Object value;
                if ((value = Array.get(o, i)) != null && !ClassTypes.method871(clazz.getComponentType())) {
                    n += this.size(value.getClass(), value);
                } else if (value != null && value.getClass().isArray()) {
                    n += 16;
                } else {
                    n += 4;
                }
            }
        }
        return n;
    }

    private static Class cls(final String s) {
        Class<?> forName;
        try {
            forName = Class.forName(s, false, Emulator.getCustomClassLoader());
        } catch (ClassNotFoundException ex2) {
            throw new NoClassDefFoundError(ex2.getMessage());
        }
        return forName;
    }

    static Class method848(final String s) {
        return cls(s);
    }

    private final class ObjInstance {
        String ref;
        Object val;
        int size;

        ObjInstance(final Memory a, final String s, final Object o) {
            super();
            this.ref = s;
            this.val = o;
            this.size = a.size(o.getClass(), o, s);
        }
    }

    private final class ClassInfo implements Comparable {
        String s;
        int anInt1485;
        int anInt1487;
        Vector objs;

        public final int size() {
            int anInt1487 = this.anInt1487;
            for (int i = this.objs.size() - 1; i >= 0; --i) {
                anInt1487 += ((ObjInstance) this.objs.get(i)).size;
            }
            return anInt1487;
        }

        public final int compareTo(final Object o) {
            return this.s.compareTo(((ClassInfo) o).s);
        }

        ClassInfo(final Memory a, final String aString1484) {
            super();
            this.objs = new Vector();
            this.anInt1485 = 0;
            this.anInt1487 = a.size(a.method848(aString1484), null);
            this.s = aString1484;
        }
    }
}
