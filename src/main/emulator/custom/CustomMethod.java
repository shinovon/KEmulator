package emulator.custom;

import java.util.*;

import javax.microedition.media.Manager;

import com.nokia.mj.impl.utils.DebugUtils;

import emulator.debug.*;
import emulator.*;
import emulator.custom.h.MethodInfo;

import java.io.*;

public class CustomMethod
{
    private static long aLong13;
    private static long aLong17;
    private static Hashtable aHashtable14;
    private static Thread aThread15;
    //static StringBuffer aStringBuffer16;
	static String trackStr;
	private static BufferedWriter trackWriter;
	private static FileWriter fw;
    
    public CustomMethod() {
        super();
    }
    
    public static void gc() {
        ++Profiler.gcCallCount;
    }
    
    public static void yield() throws InterruptedException {
        Thread.sleep(1L);
    }
    
    public static String getProperty(final String prop) {
    	String res = System.getProperty(prop);
    	boolean b = true;
    	if(prop.equalsIgnoreCase("fileconn.dir.private")) {
    		res = "file://root/private_" + Emulator.midletClassName.replace("\\", "_").replace("/", "_").replace(".", "_") + "/";
    	} else if(prop.equalsIgnoreCase("user.name")) {
    		res = "KEmulator";
    	} else if(prop.equalsIgnoreCase("console.encoding")) {
    		res = System.getProperty("file.encoding");
    	} else if(prop.equalsIgnoreCase("user.language")) {
    		//res = "en";
    	} else if(prop.equalsIgnoreCase("com.nokia.mid.networkavailability")) {
    		if(!Settings.networkNotAvailable)
    			res = "available";
    		else
    			res = null;
    	} else if(prop.equalsIgnoreCase("com.nokia.mid.batterylevel")) {
    		res = "50";
    	} else if(prop.equalsIgnoreCase("user.region")) {
    		//res = "US";
    	} else if(prop.equalsIgnoreCase("os.name")) {
    		if(System.getProperty("microedition.platform").indexOf("S60") > -1)
    			res = "Symbian";
    	} else if(prop.startsWith("com.nokia.gpu.memory")) {
    		b = false;
    		/*
    		if(prop.equals("com.nokia.gpu.memory.total")) {
        		res = "" + (32 * 1024);
        	} else if(prop.equals("com.nokia.gpu.memory.used")) {
        		res = "" + 0;
        	}
        	*/
    	} else if(prop.startsWith("com.nokia.memoryram")) {
    		b = false;
    		if(prop.equals("com.nokia.memoryramfree")) {
        		res = "" + Runtime.getRuntime().freeMemory();
        	} else if(prop.equals("com.nokia.memoryramtotal")) {
        		res = "" + Runtime.getRuntime().maxMemory();
        	}
    	} else if(prop.equalsIgnoreCase("kemulator.threadtrace")) {
    		b = false;
    		res = DebugUtils.getStackTrace(new Exception("Trace")).replace("\t", "").replace("\r", "");
    	} else if(prop.equalsIgnoreCase("com.nokia.mid.imei") || prop.equalsIgnoreCase("com.nokia.imei") || prop.equalsIgnoreCase("device.imei")) {
    		b = false;
    		res = Emulator.askIMEI();
    	} else if(prop.equalsIgnoreCase("kemulator.libvlc.supported")) {
    		res = "" + Manager.isLibVlcSupported();
    	}
    	if(b)
            Emulator.getEmulator().getLogStream().println("System.getProperty#" + prop + "=" + res);
        return res;
    }
    
    public static long currentTimeMillis() {
        ++Profiler.currentTimeMillisCallCount;
        final long currentTimeMillis = System.currentTimeMillis();
        final long n2;
        final long n = ((n2 = Settings.f) < 0L) ? ((100L + n2 << 10) / 100L) : (n2 << 10);
        if (Settings.aLong1235 > 0L) {
            CustomMethod.aLong13 += n * (currentTimeMillis - CustomMethod.aLong17 - Settings.aLong1235) >> 10;
            CustomMethod.aLong17 = currentTimeMillis;
            Settings.aLong1235 = 0L;
        }
        else {
            CustomMethod.aLong13 += n * (currentTimeMillis - CustomMethod.aLong17) >> 10;
            CustomMethod.aLong17 = currentTimeMillis;
        }
        return CustomMethod.aLong13;
    }
    
    public static InputStream getResourceAsStream(final Object o, final String s) {
        return CustomJarResources.method808(o, s);
    }
    
    public static void showTrackInfo(final String s) {
        if (Settings.threadMethodTrack) {
            System.out.print(s);
    		if(trackWriter != null) {
    			try {
					trackWriter.append(s);
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
        }
    }
    
    private static int method16() {
        final Thread currentThread = Thread.currentThread();
        int n;
        if (CustomMethod.aThread15 != null && CustomMethod.aThread15 != currentThread) {
            n = 0;
            trackStr = "=====" + currentThread.toString() + "=====\n";
            showTrackInfo(trackStr);
        }
        else {
            final Integer value;
            n = (((value = (Integer) CustomMethod.aHashtable14.get(currentThread)) == null) ? 0 : value);
        }
        CustomMethod.aHashtable14.put(currentThread, new Integer(n + 1));
        CustomMethod.aThread15 = currentThread;
        return n;
    }
    
    private static void method17() {
        final Thread currentThread = Thread.currentThread();
        final Object value;
        if ((value = CustomMethod.aHashtable14.get(currentThread)) != null) {
            CustomMethod.aHashtable14.put(currentThread, new Integer(Math.max((int)value - 1, 0)));
        }
    }
    
    public static void beginMethod(final String s) {
        if (h.aHashtable1061 == null) {
            h.aHashtable1061 = new Hashtable();
            h.method591();
        }
        final h.MethodInfo methodInfo;
        if ((methodInfo = (MethodInfo) h.aHashtable1061.get(s)) != null) {
            final int method16 = method16();
            final h.MethodInfo methodInfo2 = methodInfo;
            ++methodInfo2.anInt1182;
            trackStr = "";
            for (int i = 0; i < method16; ++i) {
            	trackStr += ("  ");
            }
            trackStr += s + "\n";
            showTrackInfo(trackStr);
            methodInfo.aLong1174 = System.currentTimeMillis();
        }
    }
    
    public static void endMethod(final String s) {
        final h.MethodInfo methodInfo;
        if ((methodInfo = (MethodInfo) h.aHashtable1061.get(s)) != null) {
            if (methodInfo.anInt1182 > 0) {
                final h.MethodInfo methodInfo2 = methodInfo;
                methodInfo2.aLong1179 += System.currentTimeMillis() - methodInfo.aLong1174;
                methodInfo.aFloat1175 = methodInfo.aLong1179 / methodInfo.anInt1182;
            }
            method17();
        }
    }
    
    public static void exit(int i) {
    	close();
    	System.exit(i);
    }
    
    public static void close() {
		if(trackWriter != null)
    	try {
			trackWriter.close();
			trackWriter = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    static {
        CustomMethod.aHashtable14 = new Hashtable();
		try {
			fw = new FileWriter(Emulator.getAbsolutePath() + "/track.txt", false);
			trackWriter = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
        trackStr = "";
    }
}
