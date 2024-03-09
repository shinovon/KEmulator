package emulator;

import javax.microedition.lcdui.*;

import net.rim.device.api.system.Application;
import emulator.graphics2D.*;

import java.util.Vector;

public final class EventQueue implements Runnable {
    private int[] events;
    private int readIndex;
    private int ind;
    private int event;
    private boolean running;
    private Vector serialEvents = new Vector();
    private Thread eventThread;
    private boolean paused;
    private InputThread input = new InputThread();
    private Thread inputThread;
    private int[] repaintRegion = new int[4];
    private Object lock = new Object();
    private Object repaintLock = new Object();
    private boolean repainted;

    public EventQueue() {
        super();
        this.events = new int[128];
        this.ind = 0;
        this.readIndex = 0;
        this.event = 0;
        this.paused = false;
        this.running = true;
        this.eventThread = new Thread(this, "KEmulator-EventQueue");
        this.eventThread.start();
        (this.inputThread = new Thread(input, "KEmulator-InputQueue")).setPriority(3);
        this.inputThread.start();
    }

    public final void stop() {
        this.running = false;
    }

    public final void keyRepeat(int n) {
        input.queue(2, n, 0, true);
        //method717(33554432, n);
    }

    public final void keyPress(int n) {
        input.queue(0, n, 0, true);
        //method717(67108864, n);
    }

    public final void keyRelease(int n) {
        input.queue(1, n, 0, true);
        //method717(134217728, n);
    }

    private static int method718(final int n) {
        if (n < 0) {
            return (-n & 0xFFF) | 0x8000;
        }
        return n & 0xFFF;
    }

    private int method719(int i, final int n, final boolean b) {
        final int n2 = n & 0xFFF;
        if (!b) {
            return n2;
        }
        if ((i & 0x8000) != 0x0) {
            return -n2;
        }
        return n2;
    }

    public final void mouseDown(final int x, final int y) {
        try {
            if (Emulator.getCurrentDisplay().getCurrent() == null) {
                return;
            }
            if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
                Emulator.getCanvas().invokePointerPressed(x, y);
                return;
            }
            Emulator.getScreen().invokePointerPressed(x, y);
        } catch (Throwable e) {
        }
        //input.queue(0, x, y, false);
        //mouse(268435456, x, y);
    }

    public final void mouseUp(final int x, final int y) {
        try {
            if (Emulator.getCurrentDisplay().getCurrent() == null) {
                return;
            }
            if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
                Emulator.getCanvas().invokePointerReleased(x, y);
                return;
            }
            Emulator.getScreen().invokePointerReleased(x, y);
        } catch (Throwable e) {
        }
        //input.queue(1, x, y, false);
        //mouse(536870912, x, y);
    }

    public final void mouseDrag(final int x, final int y) {
        try {
            if (Emulator.getCurrentDisplay().getCurrent() == null) {
                return;
            }
            if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
                Emulator.getCanvas().invokePointerDragged(x, y);
                return;
            }
            Emulator.getScreen().invokePointerDragged(x, y);
        } catch (Throwable e) {
        }
        //input.queue(2, x, y, false);
        //mouse(1073741824, x, y);
    }

    public void queue(int n, int x, int y) {
        final int n4 = n | method718(x) | method718(y) << 12;
        this.queue(n4);
    }

    public synchronized final void mouse(final int n, final int x, final int y) {
        final int n4 = n | method718(x) | method718(y) << 12;
        this.queue(n4);
    }

    /**
     * queue event
     */
    public synchronized void queue(final int n) {
        if(n == 1) {
            repainted = false;
        }
        if (n == 15) {
            this.paused = false;
        }
        this.events[this.ind++] = n;
        if (this.ind >= events.length) {
            this.ind = 0;
        }
    }

    public void queueRepaint() {
        repaintRegion[0] = -1;
        repaintRegion[1] = -1;
        repaintRegion[2] = -1;
        repaintRegion[3] = -1;
        queue(1);
    }

    public void queueRepaint(int x, int y, int w, int h) {
        repaintRegion[0] = x;
        repaintRegion[1] = y;
        repaintRegion[2] = w;
        repaintRegion[3] = h;
        queue(1);
    }

    public void gameGraphicsFlush() {
        synchronized(lock) {
            internalGameFlush();
        }
    }

    public void gameGraphicsFlush(int x, int y, int w, int h) {
        // TODO
        synchronized(lock) {
            internalGameFlush();
        }
    }

    private synchronized int nextEvent() {
        if (this.readIndex == this.ind) {
            return 0;
        }
        int n = this.events[this.readIndex];
        this.events[this.readIndex++] = 0;
        if (this.readIndex >= events.length) {
            this.readIndex = 0;
        }
        return n;
    }

    public void serviceRepaints() {
        if(Thread.currentThread() == eventThread || repainted) return;
        try {
            synchronized (repaintLock) {
                repaintLock.wait();
            }
        } catch (Exception e) {}
//        synchronized(lock) {
//            internalRepaint();
//        }
    }

    public final void run() {
        while (this.running) {
            try {
                if (Emulator.getMIDlet() == null || this.paused) {
                    Thread.sleep(5);
                    continue;
                }
                switch (this.event = this.nextEvent()) {
                    case 1: {
                        Displayable.fpsLimiter();
                        synchronized(lock) {
                            internalRepaint();
                        }
                        break;
                    }
                    case 2: { // serial call
                        synchronized(lock) {
                            if (!serialEvents.isEmpty()) {
                                ((Runnable) serialEvents.remove(0)).run();
                            }
                        }
                        break;
                    }
                    case 4: {
                        if (Emulator.getScreen() == null) {
                            break;
                        }
                        if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getScreen()) {
                            break;
                        }
                        final IImage backBufferImage3 = Emulator.getEmulator().getScreen().getBackBufferImage();
                        final IImage xRayScreenImage3 = Emulator.getEmulator().getScreen().getXRayScreenImage();
                        Emulator.getScreen().invokePaint(new Graphics(backBufferImage3, xRayScreenImage3));
                        (Settings.xrayView ? xRayScreenImage3 : backBufferImage3)
                                .cloneImage(Emulator.getEmulator().getScreen().getScreenImg());
                        Emulator.getEmulator().getScreen().repaint();
                        try {
                            Thread.sleep(100L);
                        } catch (Exception ex2) {
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
                        try {
                            Emulator.getMIDlet().invokePauseApp();
                        } catch (Exception e) {
                            e.printStackTrace();
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
                        try {
                            Emulator.getMIDlet().invokeStartApp();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Emulator.getCanvas().invokeShowNotify();
                        break;
                    }
                    case 0:
                        break;
                    default:
                        if ((this.event & Integer.MIN_VALUE) != 0x0) {
                            if (Emulator.getCanvas() == null) {
                                if (Emulator.getScreen() != null)
                                    Emulator.getScreen().invokeSizeChanged(this.method719(this.event, this.event, false), this
                                            .method719(this.event, this.event >> 12, false));
                                break;
                            }
                            if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                                break;
                            }
                            Emulator.getCanvas().invokeSizeChanged(this.method719(this.event, this.event, false), this
                                    .method719(this.event, this.event >> 12, false));
                            break;
                        }
                        break;
                }
                this.event = 0;
                Controllers.poll();
                try {
                    Thread.sleep(1L);
                } catch (Exception ex4) {
                }
            } catch (Throwable e) {
                System.err.println("Exception in Event Thread!");
                e.printStackTrace();
            }
        }
    }

    static boolean method723(final EventQueue j) {
        return j.running;
    }

    public void callSerially(Runnable run) {
        serialEvents.add(run);
        queue(2);
    }

    private void internalRepaint() {
        try {
            if (Emulator.getCanvas() == null
                    || Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                return;
            }
            Displayable.checkForSteps();
            if (Settings.xrayView) Displayable.resetXRayGraphics();
            final IImage backBufferImage = Emulator.getEmulator().getScreen().getBackBufferImage();
            final IImage xRayScreenImage = Emulator.getEmulator().getScreen().getXRayScreenImage();
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
                    .cloneImage(Emulator.getEmulator().getScreen().getScreenImg());
            Emulator.getEmulator().getScreen().repaint();
        } catch (Exception e) {
            System.err.println("Exception in repaint!");
            e.printStackTrace();
        }
        try {
            synchronized(repaintLock) {
                repaintLock.notify();
            }
        } catch (Exception e) {}
        repainted = true;
    }

    private void internalGameFlush() {
        if (Emulator.getCanvas() == null
                || Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
            return;
        }
        final IImage screenImage = Emulator.getEmulator().getScreen().getScreenImg();
        final IImage backBufferImage2 = Emulator.getEmulator().getScreen().getBackBufferImage();
        final IImage xRayScreenImage2 = Emulator.getEmulator().getScreen().getXRayScreenImage();
        (Settings.xrayView ? xRayScreenImage2 : backBufferImage2).cloneImage(screenImage);
        Emulator.getEmulator().getScreen().repaint();
    }

    private class InputThread implements Runnable {
        private Object[] elements;

        private Object readLock = new Object();
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
                    Object[] tmp = elements;
                    elements = new Object[tmp.length + 16];
                    System.arraycopy(tmp, 0, elements, 0, count);
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
                            if(Settings.synchronizeKeyEvents) {
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
                int n = (int) o[1];
                switch ((int) o[0]) {
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
                int x = (int) o[1];
                int y = (int) o[2];
                switch ((int) o[0]) {
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
