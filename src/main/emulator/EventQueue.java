package emulator;

import javax.microedition.lcdui.*;

import net.rim.device.api.system.Application;
import emulator.graphics2D.*;

import java.util.Stack;
import java.util.Vector;

public final class EventQueue implements Runnable {
    private int[] events;
    private int readIndex;
    private int ind;
    private int event;
    private boolean running;
    private Vector serialEvents = new Vector();
    private ThreadCallSerially threadCallSerially = new ThreadCallSerially();
    private Thread eventThread;
    private boolean canvasHidden;
    private boolean repainted;
    private boolean gameRepainted;
    private InputThread input = new InputThread();
    private RepaintThread repaint = new RepaintThread();
    private Thread inputThread;
    private Thread repaintThread;

    public EventQueue() {
        super();
        this.events = new int[128];
        this.ind = 0;
        this.readIndex = 0;
        this.event = 0;
        this.canvasHidden = false;
        this.running = true;
        this.repainted = true;
        this.gameRepainted = true;
        (this.eventThread = new Thread(this, "KEmulator MIDP event queue")).setPriority(2);
        this.eventThread.start();
        (this.repaintThread = new Thread(repaint, "KEmulator repaint queue")).setPriority(2);
        this.repaintThread.start();
        (this.inputThread = new Thread(input, "KEmulator input queue")).setPriority(3);
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
    public synchronized final void queue(final int n) {
        //synchronized (events) {
        if (n == 1) {
            this.repainted = false;
            repaint.queue(n);
            return;
        }
        if (n == 3) {
            this.gameRepainted = false;
            repaint.queue(n);
            return;
        }
        if (n == 4) {
            repaint.queue(n);
            return;
        }
        if (n == 15) {
            this.canvasHidden = false;
        }
        //events.add(n);
        this.events[this.ind++] = n;
        if (this.ind >= events.length) {
            this.ind = 0;
        }
        //}
    }

    public void queueRepaint() {
        this.repainted = false;
        repaint.queue(1);
    }

    public void queueRepaint(int x, int y, int w, int h) {
        this.repainted = false;
        repaint.region.push(new int[]{x, y, w, h});
        repaint.queue(5);
    }

    public void queueGraphicsFlush() {
        this.gameRepainted = false;
        repaint.queue(3);
    }

    public void queueGraphicsFlush(int x, int y, int w, int h) {
        this.gameRepainted = false;
        repaint.region.push(new int[]{x, y, w, h});
        repaint.queue(6);
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

    public final void serviceRepaints() {
        if (Thread.currentThread() == this.repaintThread) {
            return;
        }
        while (!this.repainted && this.running) {
            try {
                Thread.sleep(1L);
            } catch (Exception ex) {
            }
        }
    }

    public final void waitGameRepaint() {
        if (Thread.currentThread() == this.repaintThread) {
            return;
        }
        while (!this.gameRepainted && this.running) {
            try {
                Thread.sleep(1L);
            } catch (Exception ex) {
            }
        }
    }

    public final void run() {
        new Thread(this.threadCallSerially, "KEmulator serial calls thread").start();
        while (this.running) {
            try {
                if (Emulator.getMIDlet() == null || this.canvasHidden) {
                    try {
                        Thread.sleep(5L);
                    } catch (Exception ex4) {
                    }
                    continue;
                }
                switch (this.event = this.nextEvent()) {
                    case 1: {
                        if (Emulator.getCanvas() == null
                                || Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                            this.repainted = true;
                            break;
                        }
                        Displayable.checkForSteps();
                        if (Settings.xrayView) Displayable.resetXRayGraphics();
                        final IImage backBufferImage = Emulator.getEmulator().getScreen().getBackBufferImage();
                        final IImage xRayScreenImage = Emulator.getEmulator().getScreen().getXRayScreenImage();
                        try {
                            Emulator.getCanvas().invokePaint(backBufferImage, xRayScreenImage);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        (Settings.xrayView ? xRayScreenImage : backBufferImage)
                                .cloneImage(Emulator.getEmulator().getScreen().getScreenImg());
                        Emulator.getEmulator().getScreen().repaint();
                        Displayable.fpsLimiter();
                        this.repainted = true;
                        break;
                    }
                    case 3: {
                        if (Emulator.getCanvas() == null
                                || Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                            this.gameRepainted = true;
                            break;
                        }
                        final IImage screenImage = Emulator.getEmulator().getScreen().getScreenImg();
                        final IImage backBufferImage2 = Emulator.getEmulator().getScreen().getBackBufferImage();
                        final IImage xRayScreenImage2 = Emulator.getEmulator().getScreen().getXRayScreenImage();
                        (Settings.xrayView ? xRayScreenImage2 : backBufferImage2).cloneImage(screenImage);
                        Emulator.getEmulator().getScreen().repaint();
                        this.gameRepainted = true;
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
                            this.repainted = true;
                            this.gameRepainted = true;
                            new Thread(new InvokeDestroyAppRunnable(this)).start();
                            break;
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
                        this.canvasHidden = true;
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
        //	queue(2);
    }

    private class RepaintThread implements Runnable {
        private int[] events = new int[length = 16];
        private Object readLock = new Object();
        private int length;
        private int count;
        private boolean added;
        private Object sync = new Object();
        private Stack<int[]> region = new Stack<int[]>();

        public synchronized void queue(int i) {
            if (events[0] == i) return;
            synchronized (sync) {
                if (count + 1 >= length) {
                    int[] tmp = events;
                    events = new int[length += 16];
                    System.arraycopy(tmp, 0, events, 0, count);
                }
            }
            events[count++] = i;
            added = true;
            synchronized (readLock) {
                readLock.notify();
            }
        }

        public void run() {
            try {
                while (running) {
                    if (!added)
                        synchronized (readLock) {
                            readLock.wait();
                        }
                    added = false;
                    while (Emulator.getMIDlet() == null || canvasHidden) {
                        Thread.sleep(5);
                    }
                    while (count > 0) {
                        int i = events[0];
                        synchronized (sync) {
                            System.arraycopy(events, 1, events, 0, length - 1);
                            events[--count] = 0;
                        }
                        switch (i) {
                            case 1: { // Canvas repaint
                                if (Emulator.getCanvas() == null
                                        || Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                                    repainted = true;
                                    break;
                                }
                                Displayable.checkForSteps();
                                if (Settings.xrayView) Displayable.resetXRayGraphics();
                                final IImage backBufferImage = Emulator.getEmulator().getScreen().getBackBufferImage();
                                final IImage xRayScreenImage = Emulator.getEmulator().getScreen().getXRayScreenImage();
                                try {
                                    Emulator.getCanvas().invokePaint(backBufferImage, xRayScreenImage);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                (Settings.xrayView ? xRayScreenImage : backBufferImage)
                                        .cloneImage(Emulator.getEmulator().getScreen().getScreenImg());
                                Emulator.getEmulator().getScreen().repaint();
                                Displayable.fpsLimiter();
                                repainted = true;
                                break;
                            }
                            case 3: { // GameCanvas flush
                                if (Emulator.getCanvas() == null
                                        || Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                                    gameRepainted = true;
                                    break;
                                }
                                final IImage screenImage = Emulator.getEmulator().getScreen().getScreenImg();
                                final IImage backBufferImage2 = Emulator.getEmulator().getScreen().getBackBufferImage();
                                final IImage xRayScreenImage2 = Emulator.getEmulator().getScreen().getXRayScreenImage();
                                (Settings.xrayView ? xRayScreenImage2 : backBufferImage2).cloneImage(screenImage);
                                Emulator.getEmulator().getScreen().repaint();
                                gameRepainted = true;
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
                            case 5: { // Canvas partial repaint
                                int[] r = region.pop();
                                if (Emulator.getCanvas() == null
                                        || Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                                    repainted = true;
                                    break;
                                }
                                Displayable.checkForSteps();
                                if (Settings.xrayView) Displayable.resetXRayGraphics();
                                final IImage backBufferImage = Emulator.getEmulator().getScreen().getBackBufferImage();
                                final IImage xRayScreenImage = Emulator.getEmulator().getScreen().getXRayScreenImage();
                                try {
                                    Emulator.getCanvas().invokePaint(backBufferImage, xRayScreenImage, r);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                (Settings.xrayView ? xRayScreenImage : backBufferImage)
                                        .cloneImage(Emulator.getEmulator().getScreen().getScreenImg());
                                Emulator.getEmulator().getScreen().repaint();
                                Displayable.fpsLimiter();
                                repainted = true;
                                break;
                            }
                            case 6: { // GameCanvas partial flush TODO
                                int[] r = region.pop();
                                if (Emulator.getCanvas() == null
                                        || Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                                    gameRepainted = true;
                                    break;
                                }
                                final IImage screenImage = Emulator.getEmulator().getScreen().getScreenImg();
                                final IImage backBufferImage2 = Emulator.getEmulator().getScreen().getBackBufferImage();
                                final IImage xRayScreenImage2 = Emulator.getEmulator().getScreen().getXRayScreenImage();
                                (Settings.xrayView ? xRayScreenImage2 : backBufferImage2).cloneImage(screenImage);
                                Emulator.getEmulator().getScreen().repaint();
                                gameRepainted = true;
                                break;
                            }
                        }
                    }
                    //Thread.sleep(1);
                }
            } catch (Throwable e) {
                System.out.println("Exception in Repaint Thread!");
                e.printStackTrace();
            }
        }
    }

    private class InputThread implements Runnable {
        private Object[] elements;

        private Object readLock = new Object();
        private int length;
        private int count;
        private boolean added;
        private Object sync = new Object();

        private InputThread() {
            elements = new Object[length = 16];
        }

        public void queue(int state, int arg1, int arg2, boolean key) {
            append(new int[]{state, arg1, arg2, key ? 1 : 0});
        }

        private void append(Object o) {
            synchronized (sync) {
                if (count + 1 >= length) {
                    // увеличивание массива
                    Object[] tmp = elements;
                    elements = new Object[length += 16];
                    System.arraycopy(tmp, 0, elements, 0, count);
                    //System.out.println("Growed input buffer from " + (count + 1) + " to " + length);
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
                    while (Emulator.getMIDlet() == null || canvasHidden) {
                        Thread.sleep(5);
                    }
                    while (count > 0) {
                        try {
                            parseAction((int[]) elements[0]);
                        } catch (Throwable e) {
                            System.out.println("Exception in Input Thread!");
                            e.printStackTrace();
                        }
                        synchronized (sync) {
                            System.arraycopy(elements, 1, elements, 0, length - 1);
                            elements[--count] = null;
                        }
                    }
                    //Thread.sleep(1);
                } catch (Throwable e) {
                    System.out.println("Exception in Input Thread!");
                    e.printStackTrace();
                }
            }
        }

        private void parseAction(int[] o) {
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

    private final class ThreadCallSerially implements Runnable {
        public void run() {
            try {
                while (running) {
                    if (!serialEvents.isEmpty()) {
                        ((Runnable) serialEvents.remove(0)).run();
                    }
                    Thread.sleep(1L);
                }
            } catch (Exception ex) {
            }
        }
    }
}
