package emulator.ui.swt;

import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import emulator.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

public final class Class161 implements ControlListener, DisposeListener {
    private Shell aShell1404;
    private Button aButton1405;
    private Button aButton1407;
    private Button aButton1409;
    private Button aButton1410;
    private Button aButton1411;
    private Button aButton1412;
    private Button aButton1413;
    private Button aButton1414;
    private Button aButton1415;
    private Button aButton1416;
    private Button aButton1417;
    private Button aButton1418;
    private Button aButton1419;
    private Button aButton1420;
    private Button aButton1421;
    private Button aButton1422;
    private Button aButton1423;
    private Button aButton1424;
    private Button aButton1425;
    private Button aButton1426;
    private Button aButton1427;
    private Button aButton1428;
    private Button aButton1429;
    private Button aButton1430;
    private boolean aBoolean1406;
    private Shell aShell1408;

    public Class161() {
        super();
        this.aShell1404 = null;
        this.aButton1405 = null;
        this.aButton1407 = null;
        this.aButton1409 = null;
        this.aButton1410 = null;
        this.aButton1411 = null;
        this.aButton1412 = null;
        this.aButton1413 = null;
        this.aButton1414 = null;
        this.aButton1415 = null;
        this.aButton1416 = null;
        this.aButton1417 = null;
        this.aButton1418 = null;
        this.aButton1419 = null;
        this.aButton1420 = null;
        this.aButton1421 = null;
        this.aButton1422 = null;
        this.aButton1423 = null;
        this.aButton1424 = null;
        this.aButton1425 = null;
        this.aButton1426 = null;
        this.aButton1427 = null;
        this.aButton1428 = null;
        this.aButton1429 = null;
        this.aButton1430 = null;
    }

    private void method838() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 2;
        layoutData.widthHint = 40;
        layoutData.heightHint = 20;
        layoutData.verticalAlignment = 2;
        final GridData layoutData2;
        (layoutData2 = new GridData()).widthHint = 20;
        layoutData2.heightHint = 40;
        final GridData layoutData3;
        (layoutData3 = new GridData()).horizontalAlignment = 3;
        layoutData3.widthHint = 20;
        layoutData3.heightHint = 40;
        layoutData3.verticalAlignment = 2;
        final GridData layoutData4;
        (layoutData4 = new GridData()).horizontalAlignment = 2;
        layoutData4.widthHint = 40;
        layoutData4.heightHint = 40;
        layoutData4.verticalAlignment = 2;
        final GridData layoutData5;
        (layoutData5 = new GridData()).horizontalAlignment = 2;
        layoutData5.widthHint = 40;
        layoutData5.heightHint = 20;
        layoutData5.verticalAlignment = 2;
        final GridData layoutData6;
        (layoutData6 = new GridData()).horizontalAlignment = 3;
        layoutData6.verticalAlignment = 2;
        final GridData gridData;
        (gridData = new GridData()).horizontalAlignment = 2;
        gridData.widthHint = 50;
        gridData.verticalAlignment = 2;
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 3;
        layout.marginHeight = 15;
        layout.horizontalSpacing = 5;
        layout.marginWidth = 5;
        ((Decorations) (this.aShell1404 = new Shell(224))).setText(UILocale.get("KEYPAD_FRAME_TITLE", "Keypad"));
        ((Decorations) this.aShell1404).setImage(new Image((Device) Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
        ((Composite) this.aShell1404).setLayout((Layout) layout);
        ((Control) this.aShell1404).setSize(new Point(259, 280));
        (this.aButton1405 = new Button((Composite) this.aShell1404, 8388608)).setText(UILocale.get("KEYPAD_FRAME_LSK", "Left Soft Key"));
        ((Control) this.aButton1405).addMouseListener((MouseListener) new Class155(this));
        ((Control) (this.aButton1428 = new Button((Composite) this.aShell1404, 8388608))).setEnabled(false);
        ((Control) this.aButton1428).setVisible(false);
        (this.aButton1407 = new Button((Composite) this.aShell1404, 8388608)).setText(UILocale.get("KEYPAD_FRAME_RSK", "Right Soft Key"));
        ((Control) this.aButton1407).setLayoutData((Object) layoutData6);
        ((Control) this.aButton1407).addMouseListener((MouseListener) new Class154(this));
        ((Control) (this.aButton1429 = new Button((Composite) this.aShell1404, 8388608))).setEnabled(false);
        ((Control) this.aButton1429).setVisible(false);
        (this.aButton1410 = new Button((Composite) this.aShell1404, 8388612)).setText("Up");
        ((Control) this.aButton1410).setLayoutData((Object) layoutData5);
        ((Control) this.aButton1410).addMouseListener((MouseListener) new Class153(this));
        ((Control) (this.aButton1430 = new Button((Composite) this.aShell1404, 8388608))).setEnabled(false);
        ((Control) this.aButton1430).setVisible(false);
        (this.aButton1411 = new Button((Composite) this.method833(), 8404996)).setText("Left");
        ((Control) this.aButton1411).setLayoutData((Object) layoutData3);
        ((Control) this.aButton1411).addMouseListener((MouseListener) new Class152(this));
        (this.aButton1409 = new Button((Composite) this.aShell1404, 8388608)).setText("Pad");
        ((Control) this.aButton1409).setLayoutData((Object) layoutData4);
        ((Control) this.aButton1409).setFocus();
        ((Control) this.aButton1409).addMouseListener((MouseListener) new Class151(this));
        (this.aButton1412 = new Button((Composite) this.method833(), 8519684)).setText("Right");
        ((Control) this.aButton1412).setLayoutData((Object) layoutData2);
        ((Control) this.aButton1412).addMouseListener((MouseListener) new Class150(this));
        ((Control) (this.aButton1427 = new Button((Composite) this.aShell1404, 8388608))).setEnabled(false);
        ((Control) this.aButton1427).setVisible(false);
        (this.aButton1413 = new Button((Composite) this.method833(), 8389636)).setText("Down");
        ((Control) this.aButton1413).setLayoutData((Object) layoutData);
        ((Control) this.aButton1413).addMouseListener((MouseListener) new Class149(this));
        ((Control) (this.aButton1426 = new Button((Composite) this.aShell1404, 8388608))).setEnabled(false);
        ((Control) this.aButton1426).setVisible(false);
        (this.aButton1414 = new Button((Composite) this.aShell1404, 8388608)).setText("1");
        ((Control) this.aButton1414).setLayoutData((Object) gridData);
        ((Control) this.aButton1414).addMouseListener((MouseListener) new Class148(this));
        (this.aButton1415 = new Button((Composite) this.aShell1404, 8388608)).setText("2 abc");
        ((Control) this.aButton1415).setLayoutData((Object) gridData);
        ((Control) this.aButton1415).addMouseListener((MouseListener) new Class145(this));
        (this.aButton1416 = new Button((Composite) this.aShell1404, 8388608)).setText("3 def");
        ((Control) this.aButton1416).setLayoutData((Object) gridData);
        ((Control) this.aButton1416).addMouseListener((MouseListener) new Class100(this));
        (this.aButton1417 = new Button((Composite) this.aShell1404, 8388608)).setText("4 ghi");
        ((Control) this.aButton1417).setLayoutData((Object) gridData);
        ((Control) this.aButton1417).addMouseListener((MouseListener) new Class96(this));
        (this.aButton1418 = new Button((Composite) this.aShell1404, 8388608)).setText("5 jkl");
        ((Control) this.aButton1418).setLayoutData((Object) gridData);
        ((Control) this.aButton1418).addMouseListener((MouseListener) new Class92(this));
        (this.aButton1419 = new Button((Composite) this.aShell1404, 8388608)).setText("6 mno");
        ((Control) this.aButton1419).setLayoutData((Object) gridData);
        ((Control) this.aButton1419).addMouseListener((MouseListener) new Class120(this));
        (this.aButton1420 = new Button((Composite) this.aShell1404, 8388608)).setText("7 pqrs");
        ((Control) this.aButton1420).setLayoutData((Object) gridData);
        ((Control) this.aButton1420).addMouseListener((MouseListener) new Class134(this));
        (this.aButton1421 = new Button((Composite) this.aShell1404, 8388608)).setText("8 tuv");
        ((Control) this.aButton1421).setLayoutData((Object) gridData);
        ((Control) this.aButton1421).addMouseListener((MouseListener) new Class132(this));
        (this.aButton1422 = new Button((Composite) this.aShell1404, 8388608)).setText("9 wxyz");
        ((Control) this.aButton1422).setLayoutData((Object) gridData);
        ((Control) this.aButton1422).addMouseListener((MouseListener) new Class138(this));
        (this.aButton1423 = new Button((Composite) this.aShell1404, 8388608)).setText("* .");
        ((Control) this.aButton1423).setLayoutData((Object) gridData);
        ((Control) this.aButton1423).addMouseListener((MouseListener) new Class136(this));
        (this.aButton1424 = new Button((Composite) this.aShell1404, 8388608)).setText("0");
        ((Control) this.aButton1424).setLayoutData((Object) gridData);
        ((Control) this.aButton1424).addMouseListener((MouseListener) new Class130(this));
        (this.aButton1425 = new Button((Composite) this.aShell1404, 8388608)).setText("# -+");
        ((Control) this.aButton1425).setLayoutData((Object) gridData);
        ((Control) this.aButton1425).addMouseListener((MouseListener) new Class128(this));
    }

    private static void method832(final int n) {
        ((EmulatorImpl) Emulator.getEmulator()).getEmulatorScreen().handleKeyPress(Integer.parseInt(KeyMapping.deviceKeycodes[n]));
    }

    private static void method839(final int n) {
        ((EmulatorImpl) Emulator.getEmulator()).getEmulatorScreen().handleKeyRelease(Integer.parseInt(KeyMapping.deviceKeycodes[n]));
    }

    public final Shell method833() {
        return this.aShell1404;
    }

    public final boolean method834() {
        return this.aBoolean1406;
    }

    public final void method835(final Shell aShell1408) {
        this.method838();
        ((Control) this.aShell1404).pack();
        final Display current = Display.getCurrent();
        this.aShell1408 = aShell1408;
        ((Control) this.aShell1404).setLocation(aShell1408.getLocation().x + aShell1408.getSize().x, aShell1408.getLocation().y);
        this.aBoolean1406 = true;
        this.aShell1404.open();
        ((Control) this.aShell1404).addControlListener((ControlListener) this);
        ((Widget) this.aShell1404).addDisposeListener((DisposeListener) this);
        while (!((Widget) this.aShell1404).isDisposed()) {
            if (!current.readAndDispatch()) {
                current.sleep();
            }
        }
        this.aBoolean1406 = false;
    }

    public final void method836() {
        if (this.aShell1404 != null && !((Widget) this.aShell1404).isDisposed()) {
            this.aShell1404.dispose();
        }
        this.aBoolean1406 = false;
    }

    public final void controlMoved(final ControlEvent controlEvent) {
        if (Math.abs(this.aShell1408.getLocation().x + this.aShell1408.getSize().x - this.aShell1404.getLocation().x) < 10 && Math.abs(this.aShell1408.getLocation().y - this.aShell1404.getLocation().y) < 20) {
            ((Control) this.aShell1404).setLocation(this.aShell1408.getLocation().x + this.aShell1408.getSize().x, this.aShell1408.getLocation().y);
        }
    }

    public final void controlResized(final ControlEvent controlEvent) {
    }

    public final void widgetDisposed(final DisposeEvent disposeEvent) {
        this.method836();
    }

    static void method837(final Class161 class161, final int n) {
        method839(n);
    }

    static void method840(final Class161 class161, final int n) {
        method832(n);
    }
}
