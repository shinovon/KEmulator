package emulator.debug;

public class Profiler
{
    public static int FPS;
    public static int drawImageCallCount;
    public static int drawImagePixelCount;
    public static int drawRegionCallCount;
    public static int drawRegionPixelCount;
    public static int drawRGBCallCount;
    public static int drawRGBPixelCount;
    public static int nokiaDrawImageCallCount;
    public static int nokiaDrawImagePixelCount;
    public static int nokiaDrawPixelCallCount;
    public static int nokiaDrawPixelPixelCount;
    public static int totalImageInstances;
    public static int totalImagePixelCount;
    public static int gcCallCount;
    public static int currentTimeMillisCallCount;
	public static int drawCallCount;
    
    public Profiler() {
        super();
    }
    
    public static void reset() {
        Profiler.drawImageCallCount = 0;
        Profiler.drawImagePixelCount = 0;
        Profiler.drawRegionCallCount = 0;
        Profiler.drawRegionPixelCount = 0;
        Profiler.drawRGBCallCount = 0;
        Profiler.drawRGBPixelCount = 0;
        Profiler.nokiaDrawImageCallCount = 0;
        Profiler.nokiaDrawImagePixelCount = 0;
        Profiler.nokiaDrawPixelCallCount = 0;
        Profiler.nokiaDrawPixelPixelCount = 0;
        Profiler.gcCallCount = 0;
        Profiler.currentTimeMillisCallCount = 0;
        Profiler.drawCallCount = 0;
    }
}
