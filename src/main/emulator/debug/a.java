package emulator.debug;

import emulator.*;
import javax.microedition.lcdui.*;
import com.nokia.mid.sound.*;
import com.samsung.util.*;
import javax.microedition.m3g.*;
import emulator.graphics2D.*;
import java.util.*;
import java.lang.reflect.*;
import org.apache.tools.zip.*;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import javax.microedition.media.*;
import javax.microedition.media.control.*;
import java.util.zip.*;

public final class a
{
    public Hashtable aHashtable1458;
    public Vector aVector1459;
    public Vector aVector1461;
    public Vector aVector1463;
    public Vector aVector1465;
    public Vector aVector1467;
    static Class aClass1460;
    static Class aClass1462;
    static Class aClass1464;
    static Class aClass1466;
    static Class aClass1468;
    static Class aClass1469;
    static Class aClass1470;
    static Class aClass1471;
    static Class aClass1472;
    static Class aClass1473;
    static Class aClass1474;
    static Class aClass1475;
    
    public a() {
        super();
        this.aHashtable1458 = new Hashtable();
        this.aVector1459 = new Vector();
        this.aVector1461 = new Vector();
        this.aVector1463 = new Vector();
        this.aVector1465 = new Vector();
        this.aVector1467 = new Vector();
    }
    
    public final void method846() {
        if (Settings.recordReleasedImg) {
            for (int i = 0; i < this.aVector1461.size(); ++i) {
                if (!this.aVector1463.contains(this.aVector1461.get(i))) {
                    this.aVector1463.addElement(this.aVector1461.get(i));
                }
            }
        }
        this.aHashtable1458.clear();
        this.aVector1459.clear();
        this.aVector1461.clear();
        this.aVector1465.clear();
        this.aVector1467.clear();
        for (int j = 0; j < Emulator.jarClasses.size(); ++j) {
            final String s = (String) Emulator.jarClasses.get(j);
            a a;
            Class clazz = null;
            Object o;
            if (Emulator.getMIDlet().getClass().getName().equals(s)) {
                a = this;
                clazz = method862(s);
                o = Emulator.getMIDlet();
            }
            else if (Emulator.getCurrentDisplay().getCurrent().getClass().getName().equals(s)) {
                a = this;
                clazz = method862(s);
                o = Emulator.getCurrentDisplay().getCurrent();
            }
            else {
                a = this;
                try {
                	clazz = method862(s);
                } catch (Exception e) {
                	
                } catch (Error e) {
                	
                }
                o = null;
            }
            if(clazz != null)
            	a.method847(clazz, o, s);
        }
    }
    
    private void method847(final Class clazz, final Object o, final String s) {
        String s2 = clazz.getName();
        if (clazz.isArray()) {
            s2 = ClassTypes.method869(clazz);
        }
        ClassInfo classInfo = (ClassInfo) this.aHashtable1458.get(s2);
        if (clazz.isInterface()) {
            return;
        }
        if (classInfo == null) {
            classInfo = new ClassInfo(this, clazz.getName());
            this.aHashtable1458.put(s2, classInfo);
        }
        else if (o == null) {
            return;
        }
        if (o != null) {
            if (this.aVector1459.contains(o)) {
                return;
            }
            final ClassInfo classInfo2 = classInfo;
            ++classInfo2.anInt1485;
            classInfo.aVector1486.add(new ObjInstance(this, s, o));
            this.aVector1459.add(o);
            if (o instanceof Image) {
                this.aVector1461.add(o);
                if (Settings.recordReleasedImg && this.aVector1463.contains(o)) {
                    this.aVector1463.removeElement(o);
                }
            }
            else if (o instanceof Sound || o instanceof AudioClip || o instanceof Player) {
                this.aVector1465.add(o);
            }
            else if (o instanceof Node) {
                this.aVector1467.add(o);
            }
            else {
                final IImage method844;
                if (o instanceof Image2D && (method844 = f.method844((Image2D)o)) != null) {
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
            if (!ClassTypes.method871(componentType)) {
                return;
            }
            for (int i = 0; i < Array.getLength(o); ++i) {
                final Object value;
                if ((value = Array.get(o, i)) != null) {
                    this.method847(value.getClass(), value, s + '[' + i + ']');
                }
            }
        }
        else {
            if (o != null && o instanceof Vector) {
                final Enumeration<Object> elements = (Enumeration<Object>)((Vector)o).elements();
                while (elements.hasMoreElements()) {
                    final Object nextElement;
                    if ((nextElement = elements.nextElement()) != null) {
                        this.method847(nextElement.getClass(), nextElement, s + "(VectorElement)");
                    }
                }
                return;
            }
            if (o instanceof Hashtable) {
                final Enumeration<Object> keys = (Enumeration<Object>)((Hashtable)o).keys();
                while (keys.hasMoreElements()) {
                    final Object nextElement2 = keys.nextElement();
                    final Object value2;
                    if ((value2 = ((Hashtable)o).get(nextElement2)) != null) {
                        this.method847(value2.getClass(), value2, s + "(HashtableKey=" + nextElement2 + ")");
                    }
                }
                return;
            }
            if (o instanceof Object3D) {
                final Field[] method845 = method845(clazz);
                for (int j = 0; j < method845.length; ++j) {
                    final String name = method845[j].getName();
                    method845[j].setAccessible(true);
                    final Object method846 = ClassTypes.method876(o, method845[j]);
                    final String string = s + '.' + name;
                    if (!method845[j].getType().isPrimitive() && method846 != null) {
                        this.method847(method846.getClass(), method846, string);
                    }
                }
                return;
            }
            if (Emulator.jarClasses.contains(clazz.getName()) || ((a.aClass1473 != null) ? a.aClass1473 : (a.aClass1473 = method862("java.io.InputStream"))).isAssignableFrom(clazz)) {
                final Field[] method847 = method845(clazz);
                for (int k = 0; k < method847.length; ++k) {
                    final String name2 = method847[k].getName();
                    if (!Modifier.isFinal(method847[k].getModifiers()) || !method847[k].getType().isPrimitive()) {
                        method847[k].setAccessible(true);
                        final Object method848 = ClassTypes.method876(o, method847[k]);
                        final String string2 = s + '.' + name2;
                        if (!method847[k].getType().isPrimitive() && method848 != null) {
                            this.method847(method848.getClass(), method848, string2);
                        }
                    }
                }
            }
        }
    }
    
    private static Field[] method845(final Class clazz) {
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
        }
        catch (Error error) {
        }
    }
    
    public static int method850() {
        int n = 0;
        try {
            if (Emulator.midletJar != null) {
                final ZipFile zipFile = new ZipFile(Emulator.midletJar);
                final Enumeration<String> elements = Emulator.jarClasses.elements();
                while (elements.hasMoreElements()) {
                    final String s;
                    try {
	                    if (!method862(s = elements.nextElement()).isInterface()) {
	                        n += (int)((ZipEntry)zipFile.getEntry(s.replace('.', '/') + ".class")).getSize();
	                    }
                    } catch (Exception e) {
                    	
                    } catch (Error e) {
                    	
                    }
                }
            }
            else {
                final Enumeration<String> elements2 = Emulator.jarClasses.elements();
                while (elements2.hasMoreElements()) {
                    final String s2;
                    if (!method862(s2 = elements2.nextElement()).isInterface()) {
                        n += (int)Emulator.getFileFromClassPath(s2.replace('.', '/') + ".class").length();
                    }
                }
            }
        }
        catch (Exception ex) {}
        return n;
    }
    
    public final int method857() {
        int n = 0;
        final Enumeration<ClassInfo> elements = this.aHashtable1458.elements();
        while (elements.hasMoreElements()) {
            n += elements.nextElement().method878();
        }
        return n;
    }
    
    public static String method851(final Object o) {
        if (o instanceof Sound) {
            return ((Sound)o).getType();
        }
        if (o instanceof AudioClip) {
            return "MMF";
        }
        return ((Player)o).getContentType();
    }
    
    public static String method858(final Object o) {
        if (o instanceof Sound) {
            switch (((Sound)o).getState()) {
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
        }
        else if (o instanceof AudioClip) {
            switch (((AudioClip)o).getStatus()) {
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
        }
        else {
            switch (((Player)o).getState()) {
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
    
    public static int method852(final Object o) {
        if (o instanceof Sound) {
            return ((Sound)o).m_player.loopCount;
        }
        if (o instanceof AudioClip) {
            return ((AudioClip)o).loopCount;
        }
        return ((PlayerImpl)o).loopCount;
    }
    
    public static int method859(final Object o) {
        if (o instanceof Sound) {
            return ((Sound)o).dataLen;
        }
        if (o instanceof AudioClip) {
            return ((AudioClip)o).dataLen;
        }
        return ((PlayerImpl)o).dataLen;
    }
    
    public static int method863(final Object o) {
        try {
            if (o instanceof Sound) {
                final Sound sound = (Sound)o;
                return (int)(sound.m_player.getMediaTime() * 100L / (sound.m_player.getDuration() / 1000L));
            }
            if (o instanceof AudioClip) {
                return 0;
            }
            final PlayerImpl playerImpl = (PlayerImpl)o;
            return (int)(playerImpl.getMediaTime() * 100L / (playerImpl.getDuration() / 1000L));
        }
        catch (Exception ex) {
            return 0;
        }
    }
    
    public static int method865(final Object o) {
        try {
            if (o instanceof Sound) {
                return ((Sound)o).getGain();
            }
            if (o instanceof AudioClip) {
                return ((AudioClip)o).volume5 * 20;
            }
            return ((VolumeControlImpl)((PlayerImpl)o).getControl("VolumeControl")).getLevel();
        }
        catch (Exception ex) {
            return 0;
        }
    }
    
    public static void method853(final Object o, final int n) {
        try {
            if (o instanceof Sound) {
                ((Sound)o).setGain(n);
            }
            else if (!(o instanceof AudioClip)) {
                ((VolumeControlImpl)((PlayerImpl)o).getControl("VolumeControl")).setLevel(n);
            }
        }
        catch (Exception ex) {}
    }
    
    public static void method860(final Object o, final int n) {
        if (o instanceof Sound) {
            final Sound sound = (Sound)o;
            try {
                switch (n) {
                    case 0: {
                        sound.resume();
                    }
                    case 1: {
                        final long mediaTime = sound.m_player.getMediaTime();
                        sound.stop();
                        sound.m_player.setMediaTime(mediaTime);
                    }
                    case 2: {
                        sound.stop();
                        break;
                    }
                }
            }
            catch (Exception ex) {}
            return;
        }
        if (o instanceof AudioClip) {
            final AudioClip audioClip = (AudioClip)o;
            switch (n) {
                case 0: {
                    audioClip.play(audioClip.loopCount, audioClip.volume5);
                }
                case 1: {
                    audioClip.pause();
                }
                case 2: {
                    audioClip.stop();
                    break;
                }
            }
            return;
        }
        final PlayerImpl playerImpl = (PlayerImpl)o;
        try {
            switch (n) {
                case 0: {
                    playerImpl.start();
                }
                case 1: {
                    final long mediaTime2 = playerImpl.getMediaTime();
                    playerImpl.stop();
                    playerImpl.setMediaTime(mediaTime2);
                }
                case 2: {
                    playerImpl.stop();
                    break;
                }
            }
        }
        catch (Exception ex2) {}
    }
    
    public final int method866(final Object o) {
    	try
    	{
        return ((ClassInfo)this.aHashtable1458.get(o)).anInt1485;
    	}
    	catch(NullPointerException e)
    	{
    	}
    	return 0;
    }
    
    public final int method867(final Object o) {
    	try
    	{
    		return ((ClassInfo)this.aHashtable1458.get(o)).method878();
    	}
    	catch(NullPointerException e)
    	{
    	}
    	return 0;
    }
    
    public final Vector method854(final Object o) {
        return ((ClassInfo)this.aHashtable1458.get(o)).aVector1486;
    }
    
    public static String method864(final Object o) {
        return ((ObjInstance)o).aString1489;
    }
    
    public static Object method855(final Object o) {
        return ((ObjInstance)o).anObject1490;
    }
    
    public static int method868(final Object o) {
        return ((ObjInstance)o).anInt1491;
    }
    
    public final int method856(final Class clazz, final Object o) {
        final Field[] method845 = method845(clazz);
        int n = 0;
        for (int i = 0; i < method845.length; ++i) {
            final Field field;
            final Class<?> type = (field = method845[i]).getType();
            if ((!Modifier.isFinal(field.getModifiers()) || !type.isPrimitive()) && (!Modifier.isStatic(field.getModifiers()) || o == null)) {
                if (Modifier.isStatic(field.getModifiers()) || o != null) {
                    if (type == Long.TYPE || type == Double.TYPE) {
                        n += 8;
                    }
                    else {
                        n += 4;
                    }
                }
            }
        }
        if (o != null) {
            int n2;
            int method846;
            if (clazz.isArray()) {
                n2 = n;
                method846 = this.method861(clazz, o);
            }
            else {
                n += 12;
                int n3;
                int length;
                if (clazz == ((a.aClass1472 != null) ? a.aClass1472 : (a.aClass1472 = method862("java.lang.String")))) {
                    n2 = n;
                    n3 = 2;
                    length = ((String)o).length();
                }
                else {
                    int n4;
                    int n5;
                    if (clazz == ((a.aClass1474 != null) ? a.aClass1474 : (a.aClass1474 = method862("javax.microedition.lcdui.Image")))) {
                        final Image image = (Image)o;
                        n2 = n;
                        n4 = image.getWidth();
                        n5 = image.getHeight();
                    }
                    else {
                        if (clazz != ((a.aClass1475 != null) ? a.aClass1475 : (a.aClass1475 = method862("javax.microedition.m3g.Image2D")))) {
                            return n;
                        }
                        final Image2D image2D = (Image2D)o;
                        n2 = n;
                        n4 = image2D.getWidth();
                        n5 = image2D.getHeight();
                    }
                    n3 = n4 * n5;
                    length = 2;
                }
                method846 = n3 * length;
            }
            n = n2 + method846;
        }
        return n;
    }
    
    private int method861(final Class clazz, final Object o) {
        int n = 0;
        n += 16;
        if (clazz == ((a.aClass1460 != null) ? a.aClass1460 : (a.aClass1460 = method862("[J")))) {
            n = 16 + 8 * Array.getLength(o);
        }
        else if (clazz == ((a.aClass1462 != null) ? a.aClass1462 : (a.aClass1462 = method862("[I")))) {
            n = 16 + 4 * Array.getLength(o);
        }
        else if (clazz == ((a.aClass1464 != null) ? a.aClass1464 : (a.aClass1464 = method862("[S")))) {
            n = 16 + 2 * Array.getLength(o);
        }
        else if (clazz == ((a.aClass1466 != null) ? a.aClass1466 : (a.aClass1466 = method862("[B")))) {
            n = 16 + 1 * Array.getLength(o);
        }
        else if (clazz == ((a.aClass1468 != null) ? a.aClass1468 : (a.aClass1468 = method862("[Z")))) {
            n = 16 + 4 * Array.getLength(o);
        }
        else if (clazz == ((a.aClass1470 != null) ? a.aClass1470 : (a.aClass1470 = method862("[D")))) {
            n = 16 + 8 * Array.getLength(o);
        }
        else if (clazz == ((a.aClass1469 != null) ? a.aClass1469 : (a.aClass1469 = method862("[F")))) {
            n = 16 + 4 * Array.getLength(o);
        }
        else if (clazz == ((a.aClass1471 != null) ? a.aClass1471 : (a.aClass1471 = method862("[C")))) {
            n = 16 + 1 * Array.getLength(o);
        }
        else {
            for (int i = Array.getLength(o) - 1; i >= 0; --i) {
                final Object value;
                if ((value = Array.get(o, i)) != null && !ClassTypes.method871(clazz.getComponentType())) {
                    n += this.method856(value.getClass(), value);
                }
                else if (value != null && value.getClass().isArray()) {
                    n += 16;
                }
                else {
                    n += 4;
                }
            }
        }
        return n;
    }
    
    private static Class method862(final String s) {
        Class<?> forName;
        try {
            forName = Class.forName(s, false, Emulator.getCustomClassLoader());
        }
        catch (ClassNotFoundException ex2) {
            final ClassNotFoundException ex = ex2;
            Emulator.AntiCrack(ex2);
            throw new NoClassDefFoundError(ex.getMessage());
        }
        return forName;
    }
    
    static Class method848(final String s) {
        return method862(s);
    }
    
    private final class ObjInstance
    {
        String aString1489;
        Object anObject1490;
        int anInt1491;
        
        ObjInstance(final a a, final String aString1489, final Object anObject1490) {
            super();
            this.aString1489 = aString1489;
            this.anObject1490 = anObject1490;
            this.anInt1491 = a.method856(anObject1490.getClass(), anObject1490);
        }
    }
    
    private final class ClassInfo implements Comparable
    {
        String aString1484;
        int anInt1485;
        int anInt1487;
        Vector aVector1486;
        
        public final int method878() {
            int anInt1487 = this.anInt1487;
            for (int i = this.aVector1486.size() - 1; i >= 0; --i) {
                anInt1487 += ((ObjInstance)this.aVector1486.get(i)).anInt1491;
            }
            return anInt1487;
        }
        
        public final int compareTo(final Object o) {
            return this.aString1484.compareTo(((ClassInfo)o).aString1484);
        }
        
        ClassInfo(final a a, final String aString1484) {
            super();
            this.aVector1486 = new Vector();
            this.anInt1485 = 0;
            this.anInt1487 = a.method856(a.method848(aString1484), null);
            this.aString1484 = aString1484;
        }
    }
}
