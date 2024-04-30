package emulator.ui.swt;

import emulator.UILocale;
import emulator.graphics3D.view.M3GView3D;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;

import javax.microedition.m3g.*;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Transform;

import emulator.debug.Memory;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import emulator.graphics3D.*;

import org.eclipse.swt.events.*;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.*;

public final class M3GViewUI implements MouseMoveListener, DisposeListener, KeyListener, MouseWheelListener {
    private Shell aShell889;
    private SashForm aSashForm890;
    private Composite aComposite891;
    private Menu aMenu895;
    private Composite aComposite907;
    private Tree aTree896;
    GLCanvas canvas;
    private Memory ana898;
    private M3GView3D m3gview;
    private Camera camera;
    private Transform cameraTransform;
    private Menu aMenu908;
    private Menu aMenu913;
    private Menu aMenu918;
    private Menu aMenu923;
    private boolean aBoolean905;
    boolean aBoolean909;
    private boolean coordinateAxis;
    private boolean showGrid;
    private int anInt362 = 0;
    private int anInt893;
    private int anInt910;
    private float aFloat906;
    private float aFloat911;
    private float fov;
    private float cameraX;
    private float cameraY;
    private float cameraZ;
    private Quaternion quaternion;
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
    private float rotationX, rotationY;
    protected float moveSpeed = 1F;
    private boolean moveForward, moveBackward, moveRight, moveLeft;
    private boolean shift, control;
    private int rotateX, rotateY;
    private long lastUpdate = 1;

    public M3GViewUI() {
        super();
        this.aShell889 = null;
        this.aSashForm890 = null;
        this.aComposite891 = null;
        this.aMenu895 = null;
        this.aComposite907 = null;
        this.aTree896 = null;
        this.canvas = null;
        this.aMenu908 = null;
        this.aMenu913 = null;
        this.aMenu918 = null;
        this.aMenu923 = null;
        this.aBackground900 = null;
        this.aNode361 = null;
        this.camera = new Camera();
        this.cameraTransform = new Transform();
        this.quaternion = new Quaternion();
        this.ana898 = new Memory();
        this.m3gview = M3GView3D.getViewInstance();
    }

    private void method516() {
        this.coordinateAxis = false;
        this.showGrid = false;
        this.aMenuItem894.setSelection(this.coordinateAxis);
        this.aMenuItem912.setSelection(this.showGrid);
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
        this.fov = 70.0f;
        this.method531();
        this.cameraX = 0.0f;
        this.cameraY = 0.0f;
        this.cameraZ = 20.0f;
        this.quaternion.setAngleAxis(-10.0f, 1.0f, 0.0f, 0.0f);
        this.cameraTransform.setIdentity();
        this.cameraTransform.postRotateQuat(this.quaternion.x, this.quaternion.y, this.quaternion.z, this.quaternion.w);
        this.cameraTransform.postTranslate(this.cameraX, this.cameraY, this.cameraZ);
    }


    private void method531() {
        final Rectangle clientArea;
        if ((clientArea = this.canvas.getClientArea()).width == 0 || clientArea.height == 0) {
            return;
        }
        if (this.anInt910 == 0) {
            if (this.fov < 0.0f) {
                this.fov = 0.0f;
            }
            if (this.fov >= 180.0f) {
                this.fov = 179.99f;
            }
            this.camera.setPerspective(this.fov, (float) clientArea.width / (float) clientArea.height, this.aFloat906, this.aFloat911);
            return;
        }
        this.camera.setParallel(this.fov, (float) clientArea.width / (float) clientArea.height, this.aFloat906, this.aFloat911);
    }

    public final void method226() {
        this.method543();
        final Display current = Display.getCurrent();
        this.aShell889.setLocation(current.getClientArea().width - this.aShell889.getSize().x >> 1, current.getClientArea().height - this.aShell889.getSize().y >> 1);
        this.aShell889.open();
        this.aShell889.addDisposeListener(this);
        this.method516();
        this.addM3GObjects();
        new Thread(new Refresher(this)).start();
        new Thread(new Flusher(this)).start();
        while (!this.aShell889.isDisposed()) {
            if (!current.readAndDispatch()) {
                current.sleep();
            }
        }
    }

    public final void method507() {
        if (this.aShell889 != null && !this.aShell889.isDisposed()) {
            this.aShell889.dispose();
        }
    }

    public final boolean method494() {
        return this.aShell889 != null && !this.aShell889.isDisposed();
    }

    private void addM3GObjects() {
        this.ana898.method846();
        this.aTree896.removeAll();

        for (int i = 0; i < this.ana898.m3gObjects.size(); ++i) {
            final Node node = (Node) this.ana898.m3gObjects.get(i);
            final String name = node.getClass().getName();
            final TreeItem widget = new TreeItem(this.aTree896, 0);

            widget.setText(name.substring(name.lastIndexOf(".") + 1) + "_" + node.getUserID());
            if(!node.isRenderingEnabled()) widget.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

            widget.setData(node);
            if (node instanceof Group) {
                new TreeItem((TreeItem) widget, 0);
            }
        }
    }

    private void method276()
    {
        label32: {
            this.aBackground900 = null;
            this.aNode361 = null;
            M3GViewUI var10000;
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
                    this.m3gview.method374(var1);
                }
            } else {
                Light var2;
                (var2 = new Light()).setMode(128);
                M3GView3D.method388();
                M3GView3D.method381(var2, null);
            }
        }
        this.aRectangle903 = this.canvas.getClientArea();
    }

    private void update() {
        float deltaTime = (float) ((System.nanoTime() - lastUpdate) / 10000000.0);
        lastUpdate = System.nanoTime();

        float tmpSpeed = moveSpeed;
        if (shift) tmpSpeed *= 2;
        if (control) tmpSpeed *= 0.25f;

        float forward = ((moveForward?1:0) - (moveBackward?1:0)) * tmpSpeed * deltaTime;
        float strafe = ((moveRight?1:0) - (moveLeft?1:0)) * tmpSpeed * deltaTime;

        if (forward != 0 && strafe != 0) {
            forward /= Math.sqrt(2);
            strafe /= Math.sqrt(2);
        }

        if(forward != 0 || strafe != 0) {
            Transform t = new Transform();
            float[] m = new float[16];
            t.postTranslate(cameraX, cameraY, cameraZ);
            t.postRotateQuat(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
            t.get(m);

            if (forward != 0) {
                cameraX += -m[2] * forward;
                cameraY += -m[6] * forward;
                cameraZ += -m[10] * forward;
            }
            if (strafe != 0) {
                cameraX += m[0] * strafe;
                cameraY += m[4] * strafe;
                cameraZ += m[8] * strafe;
            }
        }
        // TODO camera rotation

        if (!M3GView3D.isCurrent()) {
            this.m3gview.setCurrent();
        }
        this.cameraTransform.setIdentity();
        this.cameraTransform.postTranslate(this.cameraX, this.cameraY, this.cameraZ);
        this.cameraTransform.postRotateQuat(this.quaternion.x, this.quaternion.y, this.quaternion.z, this.quaternion.w);

        M3GView3D.setCamera(this.camera, this.cameraTransform);
        m3gview.clearBackground(this.aBackground900);
        if (this.showGrid) {
            this.m3gview.drawGrid(1.0F);
        }
        if (this.aNode361 != null) {
            try {
                this.m3gview.method368(this.aNode361, null);
            } catch (Exception localException) {
//                localException.printStackTrace();
            }
        }
        if (this.coordinateAxis) {
            Camera localCamera = new Camera();
            localCamera.setPerspective(50.0f, (float)(this.aRectangle903.width >> 1) / (float)(this.aRectangle903.height >> 1), 1.0f, 1000.0f);
            final Transform transform;
            (transform = new Transform()).postRotateQuat(this.quaternion.x, this.quaternion.y, this.quaternion.z, this.quaternion.w);
            transform.postTranslate(0.0f, 0.0f, 6.0f);
            M3GView3D.setCamera(localCamera, transform);
            this.m3gview.drawAxis();
        }
        this.m3gview.setViewport(this.aRectangle903.width, this.aRectangle903.height);
        M3GView3D.swapBuffers();
    }

    public void setXray(boolean b) {
        this.m3gview.setXray(b);
    }

    private void method543() {
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 1;
        layout.marginHeight = 2;
        layout.marginWidth = 2;
        (this.aShell889 = new Shell()).setText(UILocale.get("M3G_VIEW_TITLE", "M3G View"));
        this.aShell889.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
        this.method545();
        this.aShell889.setLayout(layout);
        this.aShell889.setSize(new Point(600, 400));
        this.aMenu895 = new Menu(this.aShell889, 2);
        final MenuItem menuItem = new MenuItem(this.aMenu895, 64);
        final MenuItem menuItem2;
        (menuItem2 = new MenuItem(this.aMenu895, 64)).setText(UILocale.get("M3G_VIEW_CAMERA", "Camera"));
        final MenuItem menuItem3;
        (menuItem3 = new MenuItem(this.aMenu895, 64)).setText(UILocale.get("M3G_VIEW_LIGHT", "Light"));
        this.aMenu918 = new Menu(menuItem3);
        (this.aMenuItem936 = new MenuItem(this.aMenu918, 16)).setText(UILocale.get("M3G_VIEW_LIGHT_SCENE", "Scene Graphics"));
        this.aMenuItem936.addSelectionListener(new Class122(this));
        (this.aMenuItem937 = new MenuItem(this.aMenu918, 16)).setText(UILocale.get("M3G_VIEW_LIGHT_VIEW", "Viewer Light"));
        this.aMenuItem937.addSelectionListener(new Class94(this));
        new MenuItem(this.aMenu918, 2);
        (this.aMenuItem938 = new MenuItem(this.aMenu918, 8)).setText(UILocale.get("M3G_VIEW_LIGHT_SETTING", "Light Setting"));
        menuItem3.setMenu(this.aMenu918);
        this.aMenu913 = new Menu(menuItem2);
        (this.aMenuItem925 = new MenuItem(this.aMenu913, 16)).setText(UILocale.get("M3G_VIEW_CAMERA_ORBIT", "Orbit") + "\t(1)");
        this.aMenuItem925.setAccelerator(49);
        this.aMenuItem925.addSelectionListener(new Class98(this));
        (this.aMenuItem927 = new MenuItem(this.aMenu913, 16)).setText(UILocale.get("M3G_VIEW_CAMERA_PAN", "Pan") + "\t(2)");
        this.aMenuItem927.setAccelerator(50);
        this.aMenuItem927.addSelectionListener(new Class102(this));
        (this.aMenuItem928 = new MenuItem(this.aMenu913, 16)).setText(UILocale.get("M3G_VIEW_CAMERA_DOLLY", "Dolly") + "\t(3)");
        this.aMenuItem928.setAccelerator(51);
        this.aMenuItem928.addSelectionListener(new Class106(this));
        (this.aMenuItem929 = new MenuItem(this.aMenu913, 16)).setText(UILocale.get("M3G_VIEW_CAMERA_ZOOM", "Zoom") + "\t(4)");
        this.aMenuItem929.setAccelerator(52);
        this.aMenuItem929.addSelectionListener(new Class114(this));
        new MenuItem(this.aMenu913, 2);
        final MenuItem menuItem4;
        (menuItem4 = new MenuItem(this.aMenu913, 64)).setText(UILocale.get("M3G_VIEW_CAMERA_PROJECTION", "Projection Mode"));
        this.aMenu923 = new Menu(menuItem4);
        (this.aMenuItem930 = new MenuItem(this.aMenu923, 16)).setText(UILocale.get("M3G_VIEW_CAMERA_PERSPECTIVE", "Perspective Projection"));
        this.aMenuItem930.addSelectionListener(new Class112(this));
        (this.aMenuItem931 = new MenuItem(this.aMenu923, 16)).setText(UILocale.get("M3G_VIEW_CAMERA_PARALLEL", "Parallel Projection"));
        this.aMenuItem931.addSelectionListener(new Class118(this));
        menuItem4.setMenu(this.aMenu923);
        new MenuItem(this.aMenu913, 2);
        (this.aMenuItem932 = new MenuItem(this.aMenu913, 8)).setText(UILocale.get("M3G_VIEW_CAMERA_CLIP_PLANES", "Clipping Planes") + "\tC");
        this.aMenuItem932.setAccelerator(67);
        this.aMenuItem932.addSelectionListener(new Class116(this));
        (this.aMenuItem933 = new MenuItem(this.aMenu913, 8)).setText(UILocale.get("M3G_VIEW_CAMEAR_FIELD_OF_VIEW", "Field of View") + "\tF");
        this.aMenuItem933.setAccelerator(70);
        this.aMenuItem933.addSelectionListener(new Class37(this));
        (this.aMenuItem934 = new MenuItem(this.aMenu913, 8)).setText(UILocale.get("M3G_VIEW_CAMEAR_POSITION", "Camera Position") + "\tP");
        this.aMenuItem934.setAccelerator(80);
        this.aMenuItem934.addSelectionListener(new Class143(this));
        new MenuItem(this.aMenu913, 2);
        (this.aMenuItem935 = new MenuItem(this.aMenu913, 8)).setText(UILocale.get("M3G_VIEW_CAMEAR_RESET", "Reset Camera") + "\tR");
        this.aMenuItem935.setAccelerator(82);
        this.aMenuItem935.addSelectionListener(new Class126(this));
        menuItem2.setMenu(this.aMenu913);
        this.aMenu908 = new Menu(menuItem);
        (this.aMenuItem894 = new MenuItem(this.aMenu908, 32)).setText(UILocale.get("M3G_VIEW_DISPLAY_COORDINATE", "Coordinate Axis"));
        this.aMenuItem894.addSelectionListener(new Class61(this));
        (this.aMenuItem912 = new MenuItem(this.aMenu908, 32)).setText(UILocale.get("M3G_VIEW_DISPLAY_SHOW_GRID", "Show Grid"));
        this.aMenuItem912.addSelectionListener(new Class60(this));
        (this.aMenuItem916 = new MenuItem(this.aMenu908, 32)).setText(UILocale.get("M3G_VIEW_DISPLAY_SHOW_XRAY", "Show Xray") + "\tX");
        this.aMenuItem916.setAccelerator(88);
        this.aMenuItem916.addSelectionListener(new Class63(this));
        new MenuItem(this.aMenu908, 2);
        (this.aMenuItem921 = new MenuItem(this.aMenu908, 8)).setText(UILocale.get("M3G_VIEW_DISPLAY_UPDATE_WORLD", "Update World") + "\tF5");
        this.aMenuItem921.setAccelerator(16777230);
        this.aMenuItem921.addSelectionListener(new Class62(this));
        menuItem.setMenu(this.aMenu908);
        menuItem.setText(UILocale.get("M3G_VIEW_DISPLAY", "Display"));
        this.aShell889.setMenuBar(this.aMenu895);
        this.aShell889.addShellListener(new Class49(this));
    }

    private void method545() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = 4;
        (this.aSashForm890 = new SashForm(this.aShell889, 0)).setLayoutData(layoutData);
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
        (this.aComposite891 = new Composite(this.aSashForm890, 0)).setLayout(layout);
        (this.aTree896 = new Tree(this.aComposite891, 2048)).setHeaderVisible(false);
        this.aTree896.setLayoutData(layoutData);
        this.aTree896.setLinesVisible(false);
        this.aTree896.addListener(17, new M3GViewGroupClick(this));
        this.aTree896.addMouseListener(new Class51(this));
    }

    private void method547() {
        final GridLayout layout;
        (layout = new GridLayout()).marginWidth = 0;
        layout.marginHeight = 0;
        this.aComposite907 = new Composite(this.aSashForm890, 0);
        this.method548();
        this.aComposite907.setLayout(layout);
    }

    private void method548() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = 4;
        GLData gld = new GLData();
        gld.depthSize = 24;
        gld.doubleBuffer = true;
        (this.canvas = new GLCanvas(this.aComposite907, 264192, gld)).setLayoutData(layoutData);
        this.canvas.addMouseMoveListener(this);
        this.canvas.addMouseListener(new Class56(this));
        canvas.addKeyListener(this);
        this.canvas.addControlListener(new Class57(this));
        this.canvas.addListener(12, new Class58(this));
        canvas.addMouseWheelListener(this);
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

    public void keyPressed(KeyEvent keyEvent) {
        int key = keyEvent.keyCode;
        if(keyEvent.keyCode >= SWT.ARROW_UP) {
            float x = 0;
            float y = 0;
            switch (key) {
                case SWT.ARROW_UP:
                    y = 1;
                    break;
                case SWT.ARROW_DOWN:
                    y = -1;
                    break;
                case SWT.ARROW_LEFT:
                    x = 1;
                    break;
                case SWT.ARROW_RIGHT:
                    x = -1;
                    break;
            }

            rotationX += x * 5F;
            rotationY += y * 5F;

            quaternion.setAngleAxis(0, 0, 0, 0);
            Quaternion var5 = new Quaternion();
            var5.setAngleAxis(rotationX, 0, 1, 0);
            var5.mul(quaternion);
            quaternion.set(var5);

            var5 = new Quaternion();
            var5.setAngleAxis(rotationY, 1, 0, 0);
            quaternion.mul(var5);
            return;
        }
        switch (key) {
            case 'w':
                moveForward = true;
                break;
            case 'a':
                moveLeft = true;
                break;
            case 's':
                moveBackward = true;
                break;
            case 'd':
                moveRight = true;
                break;
            case SWT.SHIFT:
                shift = true;
                break;
            case SWT.CONTROL:
                control = true;
                break;
            default:
                break;
        }
    }

    public void keyReleased(KeyEvent keyEvent) {
        keyReleased(keyEvent.keyCode);
    }

    public void keyReleased(int key) {
        switch (key) {
            case SWT.ARROW_UP:
            case SWT.ARROW_DOWN:
                rotateY = 0;
                break;
            case SWT.ARROW_LEFT:
            case SWT.ARROW_RIGHT:
                rotateX = 0;
                break;
            case 'w':
                moveForward = false;
                break;
            case 'a':
                moveLeft = false;
                break;
            case 's':
                moveBackward = false;
                break;
            case 'd':
                moveRight = false;
                break;
            case SWT.SHIFT:
                shift = false;
                break;
            case SWT.CONTROL:
                control = false;
                break;
            default:
                break;
        }
    }

    private void method492(final int x, final int y) {
        final Quaternion a = new Quaternion();
        switch (this.anInt893) {
            case 0: {
                rotationX += x / 2F;
                rotationY += y / 2F;

                quaternion.setAngleAxis(0, 0, 0, 0);
                Quaternion var5 = new Quaternion();
                var5.setAngleAxis(rotationX, 0, 1, 0);
                var5.mul(quaternion);
                quaternion.set(var5);

                var5 = new Quaternion();
                var5.setAngleAxis(rotationY, 1, 0, 0);
                quaternion.mul(var5);
                break;
            }
            case 1: {
                this.cameraX += (float)x / 10.0f;
                this.cameraY -= (float)y / 10.0f;
            }
            case 2: {
                if (this.anInt910 == 0) {
                    this.cameraZ -= (float)x / 10.0f;
                    return;
                }
                break;
            }
            case 3: {
                this.fov -= (float)x / 10.0f;
                Label_0263:
                {
                    if (this.fov > 0.0f) {
                        if (this.fov < 180.0f || this.anInt910 != 0) {
                            break Label_0263;
                        }
                    }
                    this.fov += x / 10.0f;
                }
                this.method531();
                break;
            }
        }
    }

    private Vector4f method495(final Quaternion q) {
        final Vector4f a = new Vector4f(this.cameraX, this.cameraY, this.cameraZ, 1.0f);
        final Vector4f b = new Vector4f();
        final Transform transform;
        (transform = new Transform()).postRotateQuat(q.x, q.y, q.z, q.w);
        transform.postTranslate(this.cameraX, this.cameraY, this.cameraZ);
        ((Transform3D) transform.getImpl()).transform(a);
        b.cross(a, Vector4f.Y_AXIS);
        return b;
    }

    public final void widgetDisposed(final DisposeEvent disposeEvent) {
        this.method507();
    }

    static boolean method243(final M3GViewUI class90, final boolean aBoolean905) {
        return class90.aBoolean905 = aBoolean905;
    }

    static boolean method232(final M3GViewUI class90) {
        return class90.aBoolean905;
    }

    static boolean method242(final M3GViewUI class90) {
        return class90.aBoolean909;
    }

    static int method504(final M3GViewUI class90, final int anInt893) {
        return class90.anInt893 = anInt893;
    }

    static int method510(final M3GViewUI class90, final int anInt910) {
        return class90.anInt910 = anInt910;
    }

    static MenuItem method505(final M3GViewUI class90) {
        return class90.aMenuItem928;
    }

    static void method511(final M3GViewUI class90) {
        class90.method276();
    }

    static void method252(M3GViewUI paramClass57) {
        paramClass57.method531();
    }

    static Shell method499(final M3GViewUI class90) {
        return class90.aShell889;
    }

    static float method503(final M3GViewUI class90) {
        return class90.aFloat906;
    }

    static float method512(final M3GViewUI class90) {
        return class90.aFloat911;
    }

    static int method500(final M3GViewUI class90) {
        return class90.anInt910;
    }

    static float method506(final M3GViewUI class90, final float aFloat906) {
        return class90.aFloat906 = aFloat906;
    }

    static float method513(final M3GViewUI class90, final float aFloat911) {
        return class90.aFloat911 = aFloat911;
    }

    static float method517(final M3GViewUI class90) {
        return class90.fov;
    }

    static float method518(final M3GViewUI class90, final float aFloat915) {
        return class90.fov = aFloat915;
    }

    static float method525(final M3GViewUI class90) {
        return class90.cameraX;
    }

    static float method532(final M3GViewUI class90) {
        return class90.cameraY;
    }

    static float method537(final M3GViewUI class90) {
        return class90.cameraZ;
    }

    static float method526(final M3GViewUI class90, final float aFloat920) {
        return class90.cameraX = aFloat920;
    }

    static float method533(final M3GViewUI class90, final float aFloat924) {
        return class90.cameraY = aFloat924;
    }

    static float method538(final M3GViewUI class90, final float aFloat926) {
        return class90.cameraZ = aFloat926;
    }

    static void method519(final M3GViewUI class90) {
        class90.method524();
    }

    static boolean method520(final M3GViewUI class90, final boolean aBoolean914) {
        return class90.coordinateAxis = aBoolean914;
    }

    static MenuItem method514(final M3GViewUI class90) {
        return class90.aMenuItem894;
    }

    static boolean method527(final M3GViewUI class90, final boolean aBoolean919) {
        return class90.showGrid = aBoolean919;
    }

    static MenuItem method521(final M3GViewUI class90) {
        return class90.aMenuItem912;
    }

    static MenuItem method528(final M3GViewUI class90) {
        return class90.aMenuItem916;
    }

    static void method529(final M3GViewUI class90) {
        class90.addM3GObjects();
    }

    static Tree method501(final M3GViewUI class90) {
        return class90.aTree896;
    }

    static int method515(final M3GViewUI class90) {
        return class90.anInt893++;
    }

    static int method522(final M3GViewUI class90, final int n) {
        return class90.anInt893 %= n;
    }

    static int method523(final M3GViewUI class90) {
        return class90.anInt893;
    }

    static MenuItem method534(final M3GViewUI class90) {
        return class90.aMenuItem929;
    }

    static MenuItem method539(final M3GViewUI class90) {
        return class90.aMenuItem925;
    }

    static MenuItem method541(final M3GViewUI class90) {
        return class90.aMenuItem927;
    }

    static int method530(final M3GViewUI class90, final int anInt917) {
        return class90.anInt917 = anInt917;
    }

    static int method535(final M3GViewUI class90, final int anInt922) {
        return class90.anInt922 = anInt922;
    }

    static float method542(final M3GViewUI class90, final float n) {
        return class90.fov += n;
    }

    static float method544(final M3GViewUI class90, final float n) {
        return class90.fov -= n;
    }

    public void mouseScrolled(MouseEvent mouseEvent) {
        if(mouseEvent.count == 0) return;
        moveSpeed *= Math.pow(1.15F, mouseEvent.count > 0 ? 1 : -1);
        moveSpeed = Math.max(0.01F, Math.min(1000F, moveSpeed)); // limit
        // there was zoom change
//                Class90.method542(this.aClass90_825, event.count);
//                if (Class90.method517(this.aClass90_825) <= 0.0f) {
//                    Class90.method544(this.aClass90_825, event.count);
//                } else if (Class90.method517(this.aClass90_825) >= 180.0f && Class90.method500(this.aClass90_825) == 0) {
//                    Class90.method544(this.aClass90_825, event.count);
//                }
//                Class90.method252(this.aClass90_825);
    }

    private final class Flusher implements Runnable {
        private final M3GViewUI aClass90_1207;

        private Flusher(final M3GViewUI aClass90_1207) {
            super();
            this.aClass90_1207 = aClass90_1207;
        }

        public final void run() {
            M3GViewUI.method243(this.aClass90_1207, aClass90_1207.m3gview.useContext(aClass90_1207.canvas));
            while (aClass90_1207.canvas != null) {
                if (aClass90_1207.canvas.isDisposed()) {
                    return;
                }
                if (M3GViewUI.method232(this.aClass90_1207) && M3GViewUI.method242(this.aClass90_1207)) {
                    try {
                        aClass90_1207.update();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    this.aClass90_1207.aBoolean909 = false;
                }
                try {
                    Thread.sleep(10L);
                } catch (Exception ignored) {}
            }
        }

        Flusher(final M3GViewUI class90, final Class122 class91) {
            this(class90);
        }
    }

    final static class Refresher implements Runnable {
        final M3GViewUI aClass90_830;

        private Refresher(final M3GViewUI aClass90_830) {
            super();
            this.aClass90_830 = aClass90_830;
        }

        public final void run() {
            while (aClass90_830.canvas != null) {
                if (aClass90_830.canvas.isDisposed()) {
                    return;
                }
                EmulatorImpl.syncExec(new Class10(this));
                try {
                    Thread.sleep(10L);
                } catch (Exception ignored) {}
            }
        }

        Refresher(final M3GViewUI class90, final Class122 class91) {
            this(class90);
        }
    }
}
