package emulator.ui.swt;

import emulator.UILocale;
import emulator.graphics3D.view.b;
import org.eclipse.swt.custom.*;

import javax.microedition.m3g.*;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Transform;

import emulator.debug.Memory;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import emulator.graphics3D.*;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

public final class Class90 implements MouseMoveListener, DisposeListener {
    private Shell aShell889;
    private SashForm aSashForm890;
    private Composite aComposite891;
    private Menu aMenu895;
    private Composite aComposite907;
    private Tree aTree896;
    private Canvas aCanvas897;
    private Memory ana898;
    private emulator.graphics3D.view.b ab369;
    private Camera aCamera901;
    private Transform aTransform372;
    private Menu aMenu908;
    private Menu aMenu913;
    private Menu aMenu918;
    private Menu aMenu923;
    private boolean aBoolean905;
    private boolean aBoolean909;
    private boolean aBoolean381;
    private boolean aBoolean386;
    private int anInt362 = 0;
    private int anInt893;
    private int anInt910;
    private float aFloat906;
    private float aFloat911;
    private float aFloat915;
    private float aFloat920;
    private float aFloat924;
    private float aFloat926;
    private Quaternion ana892;
    private Background aBackground900;
    private Node aNode361;
    private Rectangle aRectangle903;
    private MenuItem aMenuItem894;
    private MenuItem aMenuItem912;
    private MenuItem aMenuItem916;
    private MenuItem aMenuItem921;
    private MenuItem aMenuItem925;
    private MenuItem aMenuItem927;
    private MenuItem aMenuItem928;
    private MenuItem aMenuItem929;
    private MenuItem aMenuItem930;
    private MenuItem aMenuItem931;
    private MenuItem aMenuItem932;
    private MenuItem aMenuItem933;
    private MenuItem aMenuItem934;
    private MenuItem aMenuItem935;
    private MenuItem aMenuItem936;
    private MenuItem aMenuItem937;
    private MenuItem aMenuItem938;
    private int anInt917;
    private int anInt922;

    public Class90() {
        super();
        this.aShell889 = null;
        this.aSashForm890 = null;
        this.aComposite891 = null;
        this.aMenu895 = null;
        this.aComposite907 = null;
        this.aTree896 = null;
        this.aCanvas897 = null;
        this.aMenu908 = null;
        this.aMenu913 = null;
        this.aMenu918 = null;
        this.aMenu923 = null;
        this.aBackground900 = null;
        this.aNode361 = null;
        this.aCamera901 = new Camera();
        this.aTransform372 = new Transform();
        this.ana892 = new Quaternion();
        this.ana898 = new Memory();
        this.ab369 = emulator.graphics3D.view.b.method362();
    }

    private void method516() {
        this.aBoolean381 = true;
        this.aBoolean386 = true;
        this.aMenuItem894.setSelection(this.aBoolean381);
        this.aMenuItem912.setSelection(this.aBoolean386);
        this.aMenuItem936.setSelection(true);
        this.aMenuItem938.setEnabled(false);
        this.anInt893 = 0;
        this.anInt910 = 0;
        this.aMenuItem925.setSelection(true);
        this.aMenuItem930.setSelection(true);
        this.method524();
    }

    private void method524() {
        this.aFloat906 = 1.0f;
        this.aFloat911 = 100000.0f;
        this.aFloat915 = 50.0f;
        this.method531();
        this.aFloat920 = 0.0f;
        this.aFloat924 = 0.0f;
        this.aFloat926 = 20.0f;
        this.ana892.setAngleAxis(-10.0f, 1.0f, 0.0f, 0.0f);
        this.aTransform372.setIdentity();
        this.aTransform372.postRotateQuat(this.ana892.x, this.ana892.y, this.ana892.z, this.ana892.w);
        this.aTransform372.postTranslate(this.aFloat920, this.aFloat924, this.aFloat926);
    }


    private void method531() {
        final Rectangle clientArea;
        if ((clientArea = ((Scrollable) this.aCanvas897).getClientArea()).width == 0 || clientArea.height == 0) {
            return;
        }
        if (this.anInt910 == 0) {
            if (this.aFloat915 < 0.0f) {
                this.aFloat915 = 0.0f;
            }
            if (this.aFloat915 >= 180.0f) {
                this.aFloat915 = 179.99f;
            }
            this.aCamera901.setPerspective(this.aFloat915, (float) clientArea.width / (float) clientArea.height, this.aFloat906, this.aFloat911);
            return;
        }
        this.aCamera901.setParallel(this.aFloat915, (float) clientArea.width / (float) clientArea.height, this.aFloat906, this.aFloat911);
    }

    public final void method226() {
        this.method543();
        final Display current = Display.getCurrent();
        ((Control) this.aShell889).setLocation(current.getClientArea().width - this.aShell889.getSize().x >> 1, current.getClientArea().height - this.aShell889.getSize().y >> 1);
        this.aShell889.open();
        ((Widget) this.aShell889).addDisposeListener((DisposeListener) this);
        this.method516();
        this.method536();
        new Thread(new Refresher(this)).start();
        new Thread(new Flusher(this)).start();
        while (!((Widget) this.aShell889).isDisposed()) {
            if (!current.readAndDispatch()) {
                current.sleep();
            }
        }
    }

    public final void method507() {
        if (this.aShell889 != null && !((Widget) this.aShell889).isDisposed()) {
            this.aShell889.dispose();
        }
    }

    public final boolean method494() {
        return this.aShell889 != null && !((Widget) this.aShell889).isDisposed();
    }

    private void method536() {
        this.ana898.method846();
        this.aTree896.removeAll();
        for (int i = 0; i < this.ana898.m3gObjects.size(); ++i) {
            final Node data;
            final String name = (data = (Node) this.ana898.m3gObjects.get(i)).getClass().getName();
            final Widget widget;
            ((TreeItem) (widget = (Widget) new TreeItem(this.aTree896, 0))).setText(name.substring(name.lastIndexOf(".") + 1) + "_" + data.getUserID());
            widget.setData((Object) data);
            if (data instanceof Group) {
                new TreeItem((TreeItem) widget, 0);
            }
        }
    }

    private void method276()
    {
        label32: {
            this.aBackground900 = null;
            this.aNode361 = null;
            Class90 var10000;
            Node var10001;
            if(this.aTree896.getSelectionCount() > 0) {
                this.aNode361 = (Node)this.aTree896.getSelection()[0].getData();
                if(this.aNode361 instanceof Sprite3D || this.aNode361 instanceof Mesh || this.aNode361 instanceof Group) {
                    break label32;
                }

                var10000 = this;
                var10001 = null;
            } else {
                if(this.ana898.m3gObjects.size() <= 0) {
                    break label32;
                }

                var10000 = this;
                var10001 = (Node)this.ana898.m3gObjects.get(0);
            }

            var10000.aNode361 = var10001;
        }

        if(this.aNode361 != null) {
            if(this.aNode361 instanceof World) {
                World var1 = (World)this.aNode361;
                this.aBackground900 = var1.getBackground();
                if(this.anInt362 == 0) {
                    this.ab369.method374(var1);
                }
            } else {
                Light var2;
                (var2 = new Light()).setMode(128);
                b.method388();
                b.method381(var2, (Transform)null);
            }
        }
        this.aRectangle903 = this.aCanvas897.getClientArea();
    }

    private void method540() {
        if (!emulator.graphics3D.view.b.method384()) {
            this.ab369.method380();
        }
        this.aTransform372.setIdentity();
        this.aTransform372.postRotateQuat(this.ana892.x, this.ana892.y, this.ana892.z, this.ana892.w);
        this.aTransform372.postTranslate(this.aFloat920, this.aFloat924, this.aFloat926);
        emulator.graphics3D.view.b.method371(this.aCamera901, this.aTransform372);
        ab369.method367(this.aBackground900);
        if (this.aBoolean386) {
            this.ab369.method372(1.0F);
        }
        if (this.aNode361 != null) {
            try {
                this.ab369.method368(this.aNode361, null);
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
        if (this.aBoolean381) {
            Camera localCamera = new Camera();
            localCamera.setPerspective(50.0f, (float)(this.aRectangle903.width >> 1) / (float)(this.aRectangle903.height >> 1), 1.0f, 1000.0f);
            final Transform transform;
            (transform = new Transform()).postRotateQuat(this.ana892.x, this.ana892.y, this.ana892.z, this.ana892.w);
            transform.postTranslate(0.0f, 0.0f, 6.0f);
            emulator.graphics3D.view.b.method371(localCamera, transform);
            this.ab369.method389();
            this.ab369.method364(this.aRectangle903.width, this.aRectangle903.height);
        }
        emulator.graphics3D.view.b.method386();
    }

    private void method543() {
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 1;
        layout.marginHeight = 2;
        layout.marginWidth = 2;
        ((Decorations) (this.aShell889 = new Shell())).setText(UILocale.get("M3G_VIEW_TITLE", "M3G View"));
        ((Decorations) this.aShell889).setImage(new Image((Device) Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
        this.method545();
        ((Composite) this.aShell889).setLayout((Layout) layout);
        ((Control) this.aShell889).setSize(new Point(600, 400));
        this.aMenu895 = new Menu((Decorations) this.aShell889, 2);
        final MenuItem menuItem = new MenuItem(this.aMenu895, 64);
        final MenuItem menuItem2;
        (menuItem2 = new MenuItem(this.aMenu895, 64)).setText(UILocale.get("M3G_VIEW_CAMERA", "Camera"));
        final MenuItem menuItem3;
        (menuItem3 = new MenuItem(this.aMenu895, 64)).setText(UILocale.get("M3G_VIEW_LIGHT", "Light"));
        this.aMenu918 = new Menu(menuItem3);
        (this.aMenuItem936 = new MenuItem(this.aMenu918, 16)).setText(UILocale.get("M3G_VIEW_LIGHT_SCENE", "Scene Graphics"));
        this.aMenuItem936.addSelectionListener((SelectionListener) new Class122(this));
        (this.aMenuItem937 = new MenuItem(this.aMenu918, 16)).setText(UILocale.get("M3G_VIEW_LIGHT_VIEW", "Viewer Light"));
        this.aMenuItem937.addSelectionListener((SelectionListener) new Class94(this));
        new MenuItem(this.aMenu918, 2);
        (this.aMenuItem938 = new MenuItem(this.aMenu918, 8)).setText(UILocale.get("M3G_VIEW_LIGHT_SETTING", "Light Setting"));
        menuItem3.setMenu(this.aMenu918);
        this.aMenu913 = new Menu(menuItem2);
        (this.aMenuItem925 = new MenuItem(this.aMenu913, 16)).setText(UILocale.get("M3G_VIEW_CAMERA_ORBIT", "Orbit") + "\t(1)");
        this.aMenuItem925.setAccelerator(49);
        this.aMenuItem925.addSelectionListener((SelectionListener) new Class98(this));
        (this.aMenuItem927 = new MenuItem(this.aMenu913, 16)).setText(UILocale.get("M3G_VIEW_CAMERA_PAN", "Pan") + "\t(2)");
        this.aMenuItem927.setAccelerator(50);
        this.aMenuItem927.addSelectionListener((SelectionListener) new Class102(this));
        (this.aMenuItem928 = new MenuItem(this.aMenu913, 16)).setText(UILocale.get("M3G_VIEW_CAMERA_DOLLY", "Dolly") + "\t(3)");
        this.aMenuItem928.setAccelerator(51);
        this.aMenuItem928.addSelectionListener((SelectionListener) new Class106(this));
        (this.aMenuItem929 = new MenuItem(this.aMenu913, 16)).setText(UILocale.get("M3G_VIEW_CAMERA_ZOOM", "Zoom") + "\t(4)");
        this.aMenuItem929.setAccelerator(52);
        this.aMenuItem929.addSelectionListener((SelectionListener) new Class114(this));
        new MenuItem(this.aMenu913, 2);
        final MenuItem menuItem4;
        (menuItem4 = new MenuItem(this.aMenu913, 64)).setText(UILocale.get("M3G_VIEW_CAMERA_PROJECTION", "Projection Mode"));
        this.aMenu923 = new Menu(menuItem4);
        (this.aMenuItem930 = new MenuItem(this.aMenu923, 16)).setText(UILocale.get("M3G_VIEW_CAMERA_PERSPECTIVE", "Perspective Projection"));
        this.aMenuItem930.addSelectionListener((SelectionListener) new Class112(this));
        (this.aMenuItem931 = new MenuItem(this.aMenu923, 16)).setText(UILocale.get("M3G_VIEW_CAMERA_PARALLEL", "Parallel Projection"));
        this.aMenuItem931.addSelectionListener((SelectionListener) new Class118(this));
        menuItem4.setMenu(this.aMenu923);
        new MenuItem(this.aMenu913, 2);
        (this.aMenuItem932 = new MenuItem(this.aMenu913, 8)).setText(UILocale.get("M3G_VIEW_CAMERA_CLIP_PLANES", "Clipping Planes") + "\tC");
        this.aMenuItem932.setAccelerator(67);
        this.aMenuItem932.addSelectionListener((SelectionListener) new Class116(this));
        (this.aMenuItem933 = new MenuItem(this.aMenu913, 8)).setText(UILocale.get("M3G_VIEW_CAMEAR_FIELD_OF_VIEW", "Field of View") + "\tF");
        this.aMenuItem933.setAccelerator(70);
        this.aMenuItem933.addSelectionListener((SelectionListener) new Class37(this));
        (this.aMenuItem934 = new MenuItem(this.aMenu913, 8)).setText(UILocale.get("M3G_VIEW_CAMEAR_POSITION", "Camera Position") + "\tP");
        this.aMenuItem934.setAccelerator(80);
        this.aMenuItem934.addSelectionListener((SelectionListener) new Class143(this));
        new MenuItem(this.aMenu913, 2);
        (this.aMenuItem935 = new MenuItem(this.aMenu913, 8)).setText(UILocale.get("M3G_VIEW_CAMEAR_RESET", "Reset Camera") + "\tR");
        this.aMenuItem935.setAccelerator(82);
        this.aMenuItem935.addSelectionListener((SelectionListener) new Class126(this));
        menuItem2.setMenu(this.aMenu913);
        this.aMenu908 = new Menu(menuItem);
        (this.aMenuItem894 = new MenuItem(this.aMenu908, 32)).setText(UILocale.get("M3G_VIEW_DISPLAY_COORDINATE", "Coordinate Axis"));
        this.aMenuItem894.addSelectionListener((SelectionListener) new Class61(this));
        (this.aMenuItem912 = new MenuItem(this.aMenu908, 32)).setText(UILocale.get("M3G_VIEW_DISPLAY_SHOW_GRID", "Show Grid"));
        this.aMenuItem912.addSelectionListener((SelectionListener) new Class60(this));
        (this.aMenuItem916 = new MenuItem(this.aMenu908, 32)).setText(UILocale.get("M3G_VIEW_DISPLAY_SHOW_XRAY", "Show Xray") + "\tX");
        this.aMenuItem916.setAccelerator(88);
        this.aMenuItem916.addSelectionListener((SelectionListener) new Class63(this));
        new MenuItem(this.aMenu908, 2);
        (this.aMenuItem921 = new MenuItem(this.aMenu908, 8)).setText(UILocale.get("M3G_VIEW_DISPLAY_UPDATE_WORLD", "Update World") + "\tF5");
        this.aMenuItem921.setAccelerator(16777230);
        this.aMenuItem921.addSelectionListener((SelectionListener) new Class62(this));
        menuItem.setMenu(this.aMenu908);
        menuItem.setText(UILocale.get("M3G_VIEW_DISPLAY", "Display"));
        ((Decorations) this.aShell889).setMenuBar(this.aMenu895);
        this.aShell889.addShellListener((ShellListener) new Class49(this));
    }

    private void method545() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = 4;
        ((Control) (this.aSashForm890 = new SashForm((Composite) this.aShell889, 0))).setLayoutData((Object) layoutData);
        this.method546();
        this.method547();
        this.aSashForm890.setWeights(new int[]{3, 7});
    }

    private void method546() {
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 1;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = 4;
        (this.aComposite891 = new Composite((Composite) this.aSashForm890, 0)).setLayout((Layout) layout);
        (this.aTree896 = new Tree(this.aComposite891, 2048)).setHeaderVisible(false);
        ((Control) this.aTree896).setLayoutData((Object) layoutData);
        this.aTree896.setLinesVisible(false);
        ((Widget) this.aTree896).addListener(17, (Listener) new Class48(this));
        ((Control) this.aTree896).addMouseListener((MouseListener) new Class51(this));
    }

    private void method547() {
        final GridLayout layout;
        (layout = new GridLayout()).marginWidth = 0;
        layout.marginHeight = 0;
        this.aComposite907 = new Composite((Composite) this.aSashForm890, 0);
        this.method548();
        this.aComposite907.setLayout((Layout) layout);
    }

    private void method548() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = 4;
        ((Control) (this.aCanvas897 = new Canvas(this.aComposite907, 264192))).setLayoutData((Object) layoutData);
        ((Control) this.aCanvas897).addMouseMoveListener((MouseMoveListener) this);
        ((Control) this.aCanvas897).addMouseListener((MouseListener) new Class56(this));
        ((Control) this.aCanvas897).addControlListener((ControlListener) new Class57(this));
        ((Widget) this.aCanvas897).addListener(12, (Listener) new Class58(this));
        ((Widget) this.aCanvas897).addListener(37, (Listener) new Class59(this));
    }

    public final void mouseMove(final MouseEvent mouseEvent) {
        if (!this.aBoolean905) {
            return;
        }
        if ((mouseEvent.stateMask & 0x80000) != 0x0) {
            this.method492(this.anInt917 - mouseEvent.x, this.anInt922 - mouseEvent.y);
            this.anInt917 = mouseEvent.x;
            this.anInt922 = mouseEvent.y;
        }
    }

    private void method492(final int n, final int n2) {
        final Quaternion a = new Quaternion();
        switch (this.anInt893) {
            case 0: {
                if (Math.abs(n) > Math.abs(n2)) {
                    a.setAngleAxis((float) n / 2.0f, 0.0f, 1.0f, 0.0f);
                    a.mul(this.ana892);
                    this.ana892.set(a);
                    return;
                }
                if (n2 != 0) {
                    final Vector4f method495 = this.method495(this.ana892);
                    a.setAngleAxis(-((float)n2) / 2.0f, method495.x, method495.y, method495.z);
                    a.mul(this.ana892);
                    if (this.method495(a).normalize()) {
                        this.ana892.set(a);
                    }
                    return;
                }
                break;
            }
            case 1: {
                this.aFloat920 += (float)n / 10.0f;
                this.aFloat924 -= (float)n2 / 10.0f;
            }
            case 2: {
                if (this.anInt910 == 0) {
                    this.aFloat926 -= (float)n / 10.0f;
                    return;
                }
                break;
            }
            case 3: {
                this.aFloat915 -= (float)n / 10.0f;
                Label_0263:
                {
                    if (this.aFloat915 > 0.0f) {
                        if (this.aFloat915 < 180.0f || this.anInt910 != 0) {
                            break Label_0263;
                        }
                    }
                    this.aFloat915 += n / 10.0f;
                }
                this.method531();
                break;
            }
        }
    }

    private Vector4f method495(final Quaternion q) {
        final Vector4f a = new Vector4f(this.aFloat920, this.aFloat924, this.aFloat926, 1.0f);
        final Vector4f b = new Vector4f();
        final Transform transform;
        (transform = new Transform()).postRotateQuat(q.x, q.y, q.z, q.w);
        transform.postTranslate(this.aFloat920, this.aFloat924, this.aFloat926);
        ((Transform3D) transform.getImpl()).transform(a);
        b.cross(a, Vector4f.Y_AXIS);
        return b;
    }

    public final void widgetDisposed(final DisposeEvent disposeEvent) {
        this.method507();
    }

    static Canvas method231(final Class90 class90) {
        return class90.aCanvas897;
    }

    static boolean method236(final Class90 class90, final boolean aBoolean909) {
        return class90.aBoolean909 = aBoolean909;
    }

    static boolean method243(final Class90 class90, final boolean aBoolean905) {
        return class90.aBoolean905 = aBoolean905;
    }

    static boolean method232(final Class90 class90) {
        return class90.aBoolean905;
    }

    static boolean method242(final Class90 class90) {
        return class90.aBoolean909;
    }

    static void method244(final Class90 class90) {
        class90.method540();
    }

    static int method504(final Class90 class90, final int anInt893) {
        return class90.anInt893 = anInt893;
    }

    static int method510(final Class90 class90, final int anInt910) {
        return class90.anInt910 = anInt910;
    }

    static MenuItem method505(final Class90 class90) {
        return class90.aMenuItem928;
    }

    static void method511(final Class90 class90) {
        class90.method276();
    }

    static void method252(Class90 paramClass57) {
        paramClass57.method531();
    }

    static Shell method499(final Class90 class90) {
        return class90.aShell889;
    }

    static float method503(final Class90 class90) {
        return class90.aFloat906;
    }

    static float method512(final Class90 class90) {
        return class90.aFloat911;
    }

    static int method500(final Class90 class90) {
        return class90.anInt910;
    }

    static float method506(final Class90 class90, final float aFloat906) {
        return class90.aFloat906 = aFloat906;
    }

    static float method513(final Class90 class90, final float aFloat911) {
        return class90.aFloat911 = aFloat911;
    }

    static float method517(final Class90 class90) {
        return class90.aFloat915;
    }

    static float method518(final Class90 class90, final float aFloat915) {
        return class90.aFloat915 = aFloat915;
    }

    static float method525(final Class90 class90) {
        return class90.aFloat920;
    }

    static float method532(final Class90 class90) {
        return class90.aFloat924;
    }

    static float method537(final Class90 class90) {
        return class90.aFloat926;
    }

    static float method526(final Class90 class90, final float aFloat920) {
        return class90.aFloat920 = aFloat920;
    }

    static float method533(final Class90 class90, final float aFloat924) {
        return class90.aFloat924 = aFloat924;
    }

    static float method538(final Class90 class90, final float aFloat926) {
        return class90.aFloat926 = aFloat926;
    }

    static void method519(final Class90 class90) {
        class90.method524();
    }

    static boolean method520(final Class90 class90, final boolean aBoolean914) {
        return class90.aBoolean381 = aBoolean914;
    }

    static MenuItem method514(final Class90 class90) {
        return class90.aMenuItem894;
    }

    static boolean method527(final Class90 class90, final boolean aBoolean919) {
        return class90.aBoolean386 = aBoolean919;
    }

    static MenuItem method521(final Class90 class90) {
        return class90.aMenuItem912;
    }

    static MenuItem method528(final Class90 class90) {
        return class90.aMenuItem916;
    }

    static void method529(final Class90 class90) {
        class90.method536();
    }

    static Tree method501(final Class90 class90) {
        return class90.aTree896;
    }

    static int method515(final Class90 class90) {
        return class90.anInt893++;
    }

    static int method522(final Class90 class90, final int n) {
        return class90.anInt893 %= n;
    }

    static int method523(final Class90 class90) {
        return class90.anInt893;
    }

    static MenuItem method534(final Class90 class90) {
        return class90.aMenuItem929;
    }

    static MenuItem method539(final Class90 class90) {
        return class90.aMenuItem925;
    }

    static MenuItem method541(final Class90 class90) {
        return class90.aMenuItem927;
    }

    static int method530(final Class90 class90, final int anInt917) {
        return class90.anInt917 = anInt917;
    }

    static int method535(final Class90 class90, final int anInt922) {
        return class90.anInt922 = anInt922;
    }

    static float method542(final Class90 class90, final float n) {
        return class90.aFloat915 += n;
    }

    static float method544(final Class90 class90, final float n) {
        return class90.aFloat915 -= n;
    }

    private final class Flusher implements Runnable {
        private final Class90 aClass90_1207;

        private Flusher(final Class90 aClass90_1207) {
            super();
            this.aClass90_1207 = aClass90_1207;
        }

        public final void run() {
            Class90.method243(this.aClass90_1207, aClass90_1207.ab369.method379(aClass90_1207.aCanvas897));
            while (Class90.method231(this.aClass90_1207) != null) {
                if (((Widget) Class90.method231(this.aClass90_1207)).isDisposed()) {
                    return;
                }
                if (Class90.method232(this.aClass90_1207) && Class90.method242(this.aClass90_1207)) {
                    try {
                        Class90.method244(this.aClass90_1207);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    Class90.method236(this.aClass90_1207, false);
                }
                try {
                    Thread.sleep(10L);
                } catch (Exception ex2) {
                }
            }
        }

        Flusher(final Class90 class90, final Class122 class91) {
            this(class90);
        }
    }

    final static class Refresher implements Runnable {
        private final Class90 aClass90_830;

        private Refresher(final Class90 aClass90_830) {
            super();
            this.aClass90_830 = aClass90_830;
        }

        public final void run() {
            while (Class90.method231(this.aClass90_830) != null) {
                if (((Widget) Class90.method231(this.aClass90_830)).isDisposed()) {
                    return;
                }
                EmulatorImpl.syncExec(new Class10(this));
                try {
                    Thread.sleep(10L);
                } catch (Exception ex) {
                }
            }
        }

        Refresher(final Class90 class90, final Class122 class91) {
            this(class90);
        }

        public static Class90 method464(final Refresher refresher) {
            return refresher.aClass90_830;
        }
    }
}
