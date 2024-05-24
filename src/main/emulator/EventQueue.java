package emulator;

import javax.microedition.lcdui.*;

import emulator.ui.IScreen;
import net.rim.device.api.system.Application;
import emulator.graphics2D.*;

import java.util.Vector;

public final class EventQueue implements Runnable {
    private int[] events;
    private int count;
    boolean running;
    private final Vector serialEvents = new Vector();
    private final Thread eventThread;
    private boolean paused;
    private final InputThread input = new InputThread();
    private final Thread inputThread;
    private final int[] repaintRegion = new int[4];
    private final Object lock = new Object();
    private final Object repaintLock = new Object();
    private boolean repainted;
    private boolean alive;

    public EventQueue() {
        events = new int[128];
        paused = false;
        running = true;
        eventThread = new Thread(this, "KEmulator-EventQueue");
        eventThread.start();
        inputThread = new Thread(input, "KEmulator-InputQueue");
        inputThread.setPriority(3);
        inputThread.start();
        Thread crashThread = new Thread() {
            public void run() {
                long time = System.currentTimeMillis();
                try {
                    while (running) {
                        if(alive || paused || Settings.steps >= 0) {
                            time = System.currentTimeMillis();
                            alive = false;
                        } else if((System.currentTimeMillis() - time) > 5000) {
                            time = System.currentTimeMillis();
                            Emulator.getEmulator().getLogStream().println("Event thread is not responding! Is it dead locked?");
                        }
                        sleep(100);
                    }
                } catch (InterruptedException ignored) {}
            }
        };
        crashThread.start();
    }

    public void stop() {
        running = false;
    }

    public void keyRepeat(int n) {
        input.queue(2, n, 0, true);
        //method717(33554432, n);
    }

    public void keyPress(int n) {
        input.queue(0, n, 0, true);
        //method717(67108864, n);
    }

    public void keyRelease(int n) {
        input.queue(1, n, 0, true);
        //method717(134217728, n);
    }

    private static int method718(int n) {
        if (n < 0) {
            return (-n & 0xFFF) | 0x8000;
        }
        return n & 0xFFF;
    }

    private int method719(int i, int n, boolean b) {
        int n2 = n & 0xFFF;
        if (!b) {
            return n2;
        }
        if ((i & 0x8000) != 0x0) {
            return -n2;
        }
        return n2;
    }

    public void mouseDown(int x, int y) {
        try {
            if (Emulator.getCurrentDisplay().getCurrent() == null) {
                return;
            }
            if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
                Emulator.getCanvas().invokePointerPressed(x, y);
                return;
            }
            Emulator.getScreen().invokePointerPressed(x, y);
        } catch (Throwable ignored) {
        }
        //input.queue(0, x, y, false);
        //mouse(268435456, x, y);
    }

    public void mouseUp(int x, int y) {
        try {
            if (Emulator.getCurrentDisplay().getCurrent() == null) {
                return;
            }
            if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
                Emulator.getCanvas().invokePointerReleased(x, y);
                return;
            }
            Emulator.getScreen().invokePointerReleased(x, y);
        } catch (Throwable ignored) {
        }
        //input.queue(1, x, y, false);
        //mouse(536870912, x, y);
    }

    public void mouseDrag(int x, int y) {
        try {
            if (Emulator.getCurrentDisplay().getCurrent() == null) {
                return;
            }
            if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
                Emulator.getCanvas().invokePointerDragged(x, y);
                return;
            }
            Emulator.getScreen().invokePointerDragged(x, y);
        } catch (Throwable ignored) {
        }
        //input.queue(2, x, y, false);
        //mouse(1073741824, x, y);
    }

    public void queue(int n, int x, int y) {
        int n4 = n | method718(x) | method718(y) << 12;
        queue(n4);
    }

    public synchronized void mouse(int n, int x, int y) {
        queue(n | method718(x) | method718(y) << 12);
    }

    public synchronized void queue(int n) {
        if (n == 1) {
            if(events[0] == 1 && !repainted)
                return;
            repainted = false;
        }
        if (n == 15 || n == 17) {
            paused = false;
        }
        synchronized (this) {
            events[count++] = n;
            if (count >= events.length) {
                System.arraycopy(events, 0, events = new int[events.length * 2], 0, count);
            }
        }
    }

    private synchronized int nextEvent() {
        if(count == 0) return 0;
        synchronized (this) {
            int n = events[0];
            System.arraycopy(events, 1, events, 0, events.length - 1);
            count--;
            return n;
        }
    }

    public void queueRepaint() {
        repaintRegion[0] = repaintRegion[1] = repaintRegion[2] = repaintRegion[3] = -1;
        queue(1);
    }

    public void queueRepaint(int x, int y, int w, int h) {
        if(Settings.ignoreRegionRepaint) {
            repaintRegion[0] = repaintRegion[1] = repaintRegion[2] = repaintRegion[3] = -1;
        } else {
            repaintRegion[0] = x;
            repaintRegion[1] = y;
            repaintRegion[2] = w;
            repaintRegion[3] = h;
        }
        queue(1);
    }

    public void gameGraphicsFlush() {
        synchronized(lock) {
            IScreen scr = Emulator.getEmulator().getScreen();
            final IImage screenImage = scr.getScreenImg();
            final IImage backBufferImage2 = scr.getBackBufferImage();
            final IImage xRayScreenImage2 = scr.getXRayScreenImage();
            (Settings.xrayView ? xRayScreenImage2 : backBufferImage2).cloneImage(screenImage);
            scr.repaint();
        }
    }

    public void gameGraphicsFlush(int x, int y, int w, int h) {
        synchronized(lock) {
            IScreen scr = Emulator.getEmulator().getScreen();
            final IImage screenImage = scr.getScreenImg();
            final IImage backBufferImage2 = scr.getBackBufferImage();
            final IImage xRayScreenImage2 = scr.getXRayScreenImage();
            (Settings.xrayView ? xRayScreenImage2 : backBufferImage2).cloneImage(screenImage, x, y, w, h);
            scr.repaint();
        }
    }

    public void serviceRepaints() {
        Thread t = Thread.currentThread();
        if (t == eventThread || t == inputThread) {
            Displayable.checkForSteps();
            synchronized(lock) {
                internalRepaint();
            }
            Displayable.fpsLimiter();
            return;
        }
        if(repainted) return;
        try {
            synchronized (repaintLock) {
                repaintLock.wait();
            }
        } catch (Exception ignored) {}
    }

    public void run() {
        //long time = 0;
        int event;
        while (running) {
            alive = true;
            try {
                if (Emulator.getMIDlet() == null || paused) {
                    Thread.sleep(5);
                    continue;
                }
                switch (event = nextEvent()) {
                    case 1: {
                        synchronized(lock) {
                            internalRepaint();
                        }
                        Displayable.fpsLimiter();
                        break;
                    }
                    case 2: { // serial call
                        if (!serialEvents.isEmpty()) {
                            synchronized(lock) {
                                ((Runnable) serialEvents.remove(0)).run();
                            }
                        }
                        break;
                    }
                    case 4: {
                        if (Emulator.getScreen() == null ||
                                Emulator.getCurrentDisplay().getCurrent() != Emulator.getScreen()) {
                            break;
                        }
                        IScreen scr = Emulator.getEmulator().getScreen();
                        final IImage backBufferImage3 = scr.getBackBufferImage();
                        final IImage xRayScreenImage3 = scr.getXRayScreenImage();
                        Emulator.getScreen().invokePaint(new Graphics(backBufferImage3, xRayScreenImage3));
                        (Settings.xrayView ? xRayScreenImage3 : backBufferImage3)
                                .cloneImage(scr.getScreenImg());
                        scr.repaint();
                        try {
                            Thread.sleep(100L);
                        } catch (Exception ignored) {
                        }
                        this.queue(4);
                        break;
                    }
                    case 10: {
                        if (Emulator.getMIDlet() != null) {
                            new Thread(new InvokeStartAppRunnable(this)).start();
                            break;
                        }
                        break;
                    }
                    case 11: {
                        this.stop();
                        if (Emulator.getMIDlet() != null) {
                            new Thread(new InvokeDestroyAppRunnable(this)).start();
                            break;
                        }
                        break;
                    }
                    case 15: {
                        if (Emulator.getCanvas() == null) {
                            break;
                        }
                        if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                            break;
                        }
                        Emulator.getCanvas().invokeShowNotify();
                        break;
                    }
                    case 16: {
                        if (Emulator.getCanvas() == null) {
                            break;
                        }
                        if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                            break;
                        }
                        Emulator.getCanvas().invokeHideNotify();
                        this.paused = true;
                        if(Settings.startAppOnResume) {
                            try {
                                Emulator.getMIDlet().invokePauseApp();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                    case 17: {
                        if (Emulator.getCanvas() == null) {
                            break;
                        }
                        if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                            break;
                        }
                        if(Settings.startAppOnResume) {
                            try {
                                Emulator.getMIDlet().invokeStartApp();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Emulator.getCanvas().invokeShowNotify();
                        break;
                    }
                    case 0:
                        break;
                    default:
                        if ((event & Integer.MIN_VALUE) != 0x0) {
                            if (Emulator.getCanvas() == null) {
                                if (Emulator.getScreen() != null)
                                    Emulator.getScreen().invokeSizeChanged(this.method719(event, event, false), this
                                            .method719(event, event >> 12, false));
                                break;
                            }
                            if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                                break;
                            }
                            Emulator.getCanvas().invokeSizeChanged(this.method719(event, event, false), this
                                    .method719(event, event >> 12, false));
                            break;
                        }
                        break;
                }
                if (!serialEvents.isEmpty()) {
                    synchronized(lock) {
                        ((Runnable) serialEvents.remove(0)).run();
                    }
                }/*else if(event == 0) {
                    if (time == 0) {
                        time = System.currentTimeMillis();
                    } else if(time != -1 && (System.currentTimeMillis() - time) > 10000) {
                        time = -1;
                        Emulator.getEmulator().getLogStream().println("Event thread remained idle for last 10 seconds!");
                    }
                    Thread.sleep(1);
                } else {
                    time = 0;
                }*/
            } catch (Throwable e) {
                System.err.println("Exception in Event Thread!");
                e.printStackTrace();
            }
        }
    }

    public void callSerially(Runnable run) {
        serialEvents.add(run);
//        queue(2);
    }

    private void internalRepaint() {
        try {
            if (Emulator.getCanvas() == null
                    || Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                return;
            }
            if (Settings.xrayView) Displayable.resetXRayGraphics();
            IScreen scr = Emulator.getEmulator().getScreen();
            final IImage backBufferImage = scr.getBackBufferImage();
            final IImage xRayScreenImage = scr.getXRayScreenImage();
            Displayable.checkForSteps();
            try {
                if (repaintRegion[0] == -1) { // full repaint
                    Emulator.getCanvas().invokePaint(backBufferImage, xRayScreenImage);
                } else {
                    Emulator.getCanvas().invokePaint(backBufferImage, xRayScreenImage, repaintRegion[0], repaintRegion[1], repaintRegion[2], repaintRegion[3]);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            (Settings.xrayView ? xRayScreenImage : backBufferImage)
                    .cloneImage(scr.getScreenImg());
            scr.repaint();
        } catch (Exception e) {
            System.err.println("Exception in repaint!");
            e.printStackTrace();
        }
        repainted = true;
        try {
            synchronized(repaintLock) {
                repaintLock.notifyAll();
            }
        } catch (Exception ignored) {}
    }

    private class InputThread implements Runnable {
        private Object[] elements;
        private final Object readLock = new Object();
        private int count;
        private boolean added;

        private InputThread() {
            elements = new Object[16];
        }

        public void queue(int state, int arg1, int arg2, boolean key) {
            append(new int[]{state, arg1, arg2, key ? 1 : 0});
        }

        private void append(Object o) {
            synchronized (this) {
                if (count + 1 >= elements.length) {
                    System.arraycopy(elements, 0, elements = new Object[elements.length * 2], 0, count);
                }
            }
            elements[count++] = o;
            added = true;
            synchronized (readLock) {
                readLock.notify();
            }
        }

        public void run() {
            while (running) {
                try {
                    if (!added)
                        synchronized (readLock) {
                            readLock.wait();
                        }
                    added = false;
                    while (Emulator.getMIDlet() == null || paused) {
                        Thread.sleep(5);
                    }
                    while (count > 0) {
                        try {
                            int[] o = (int[]) elements[0];
                            if (Settings.synchronizeKeyEvents) {
                                synchronized (lock) {
                                    processAction(o);
                                }
                            } else processAction(o);
                        } catch (Throwable e) {
                            System.out.println("Exception in Input Thread!");
                            e.printStackTrace();
                        }
                        synchronized (this) {
                            System.arraycopy(elements, 1, elements, 0, elements.length - 1);
                            elements[--count] = null;
                        }
                    }
                } catch (Throwable e) {
                    System.out.println("Exception in Input Thread!");
                    e.printStackTrace();
                }
            }
        }

        private void processAction(int[] o) {
            if (o == null) return;
            Displayable d = Emulator.getCurrentDisplay().getCurrent();
            if (d == null) return;
            boolean canv = d instanceof Canvas;
            if (o[3] > 0) {
                // keyboard
                int n = o[1];
                switch (o[0]) {
                    case 0: {
                        Application.internalKeyPress(n);
                        if (d.handleSoftKeyAction(n, true)) return;
                        if (canv) ((Canvas) d).invokeKeyPressed(n);
                        else ((Screen) d).invokeKeyPressed(n);
                        break;
                    }
                    case 1: {
                        Application.internalKeyRelease(n);
                        if (d.handleSoftKeyAction(n, false)) return;
                        if (canv) ((Canvas) d).invokeKeyReleased(n);
                        else ((Screen) d).invokeKeyReleased(n);
                        break;
                    }
                    case 2:
                        if (!canv) return;
                        ((Canvas) d).invokeKeyRepeated(n);
                        break;
                }
            } else {
                // mouse
                int x = o[1];
                int y = o[2];
                switch (o[0]) {
                    case 0:
                        if (canv) ((Canvas) d).invokePointerPressed(x, y);
                        else ((Screen) d).invokePointerPressed(x, y);
                        break;
                    case 1:
                        if (canv) ((Canvas) d).invokePointerReleased(x, y);
                        else ((Screen) d).invokePointerReleased(x, y);
                        break;
                    case 2:
                        if (canv) ((Canvas) d).invokePointerDragged(x, y);
                        else ((Screen) d).invokePointerDragged(x, y);
                        break;
                }
            }
        }
    }
}
