package emulator.ui.swt;

import emulator.Emulator;
import emulator.KeyMapping;
import emulator.UILocale;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class KeyPad implements ControlListener, DisposeListener {
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

	public KeyPad() {
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
		(this.aShell1404 = new Shell(224)).setText(UILocale.get("KEYPAD_FRAME_TITLE", "Keypad"));
		this.aShell1404.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.aShell1404.setLayout(layout);
		this.aShell1404.setSize(new Point(259, 280));
		(this.aButton1405 = new Button(this.aShell1404, 8388608)).setText(UILocale.get("KEYPAD_FRAME_LSK", "Left Soft Key"));
		KeyPad aClass161_14001 = this;
		this.aButton1405.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1400 = aClass161_14001;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1400, 17);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1400, 17);
			}
		});
		(this.aButton1428 = new Button(this.aShell1404, 8388608)).setEnabled(false);
		this.aButton1428.setVisible(false);
		(this.aButton1407 = new Button(this.aShell1404, 8388608)).setText(UILocale.get("KEYPAD_FRAME_RSK", "Right Soft Key"));
		this.aButton1407.setLayoutData(layoutData6);
		KeyPad aClass161_13991 = this;
		this.aButton1407.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1399 = aClass161_13991;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1399, 18);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1399, 18);
			}
		});
		(this.aButton1429 = new Button(this.aShell1404, 8388608)).setEnabled(false);
		this.aButton1429.setVisible(false);
		(this.aButton1410 = new Button(this.aShell1404, 8388612)).setText("Up");
		this.aButton1410.setLayoutData(layoutData5);
		KeyPad aClass161_13981 = this;
		this.aButton1410.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1398 = aClass161_13981;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1398, 12);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1398, 12);
			}
		});
		(this.aButton1430 = new Button(this.aShell1404, 8388608)).setEnabled(false);
		this.aButton1430.setVisible(false);
		(this.aButton1411 = new Button(this.method833(), 8404996)).setText("Left");
		this.aButton1411.setLayoutData(layoutData3);
		KeyPad aClass161_13971 = this;
		this.aButton1411.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1397 = aClass161_13971;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1397, 14);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1397, 14);
			}
		});
		(this.aButton1409 = new Button(this.aShell1404, 8388608)).setText("Pad");
		this.aButton1409.setLayoutData(layoutData4);
		((Control) this.aButton1409).setFocus();
		KeyPad aClass161_13961 = this;
		this.aButton1409.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1396 = aClass161_13961;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1396, 16);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1396, 16);
			}
		});
		(this.aButton1412 = new Button(this.method833(), 8519684)).setText("Right");
		this.aButton1412.setLayoutData(layoutData2);
		KeyPad aClass161_13951 = this;
		this.aButton1412.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1395 = aClass161_13951;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1395, 15);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1395, 15);
			}
		});
		(this.aButton1427 = new Button(this.aShell1404, 8388608)).setEnabled(false);
		this.aButton1427.setVisible(false);
		(this.aButton1413 = new Button(this.method833(), 8389636)).setText("Down");
		this.aButton1413.setLayoutData(layoutData);
		KeyPad aClass161_13941 = this;
		this.aButton1413.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1394 = aClass161_13941;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1394, 13);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1394, 13);
			}
		});
		(this.aButton1426 = new Button(this.aShell1404, 8388608)).setEnabled(false);
		this.aButton1426.setVisible(false);
		(this.aButton1414 = new Button(this.aShell1404, 8388608)).setText("1");
		this.aButton1414.setLayoutData(gridData);
		KeyPad aClass161_13931 = this;
		this.aButton1414.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1393 = aClass161_13931;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1393, 1);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1393, 1);
			}
		});
		(this.aButton1415 = new Button(this.aShell1404, 8388608)).setText("2 abc");
		this.aButton1415.setLayoutData(gridData);
		KeyPad aClass161_13761 = this;
		this.aButton1415.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1376 = aClass161_13761;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1376, 2);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1376, 2);
			}
		});
		(this.aButton1416 = new Button(this.aShell1404, 8388608)).setText("3 def");
		this.aButton1416.setLayoutData(gridData);
		KeyPad aClass161_10541 = this;
		this.aButton1416.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1054 = aClass161_10541;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1054, 3);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1054, 3);
			}
		});
		(this.aButton1417 = new Button(this.aShell1404, 8388608)).setText("4 ghi");
		this.aButton1417.setLayoutData(gridData);
		KeyPad aClass161_10471 = this;
		this.aButton1417.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1047 = aClass161_10471;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1047, 4);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1047, 4);
			}
		});
		(this.aButton1418 = new Button(this.aShell1404, 8388608)).setText("5 jkl");
		this.aButton1418.setLayoutData(gridData);
		KeyPad aClass161_9401 = this;
		this.aButton1418.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_940 = aClass161_9401;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_940, 5);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_940, 5);
			}
		});
		(this.aButton1419 = new Button(this.aShell1404, 8388608)).setText("6 mno");
		this.aButton1419.setLayoutData(gridData);
		KeyPad aClass161_11991 = this;
		this.aButton1419.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1199 = aClass161_11991;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1199, 6);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1199, 6);
			}
		});
		(this.aButton1420 = new Button(this.aShell1404, 8388608)).setText("7 pqrs");
		this.aButton1420.setLayoutData(gridData);
		KeyPad aClass161_12861 = this;
		this.aButton1420.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1286 = aClass161_12861;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1286, 7);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1286, 7);
			}
		});
		(this.aButton1421 = new Button(this.aShell1404, 8388608)).setText("8 tuv");
		this.aButton1421.setLayoutData(gridData);
		KeyPad aClass161_12321 = this;
		this.aButton1421.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1232 = aClass161_12321;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1232, 8);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1232, 8);
			}
		});
		(this.aButton1422 = new Button(this.aShell1404, 8388608)).setText("9 wxyz");
		this.aButton1422.setLayoutData(gridData);
		KeyPad aClass161_12971 = this;
		this.aButton1422.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1297 = aClass161_12971;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1297, 9);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1297, 9);
			}
		});
		(this.aButton1423 = new Button(this.aShell1404, 8388608)).setText("* .");
		this.aButton1423.setLayoutData(gridData);
		KeyPad aClass161_12951 = this;
		this.aButton1423.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1295 = aClass161_12951;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1295, 10);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1295, 10);
			}
		});
		(this.aButton1424 = new Button(this.aShell1404, 8388608)).setText("0");
		this.aButton1424.setLayoutData(gridData);
		KeyPad aClass161_12131 = this;
		this.aButton1424.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1213 = aClass161_12131;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1213, 0);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1213, 0);
			}
		});
		(this.aButton1425 = new Button(this.aShell1404, 8388608)).setText("# -+");
		this.aButton1425.setLayoutData(gridData);
		KeyPad aClass161_12111 = this;
		this.aButton1425.addMouseListener(new MouseAdapter() {
			private final KeyPad aClass161_1211 = aClass161_12111;

			public final void mouseUp(final MouseEvent mouseEvent) {
				method837(this.aClass161_1211, 11);
			}

			public final void mouseDown(final MouseEvent mouseEvent) {
				method840(this.aClass161_1211, 11);
			}
		});
	}

	private static void method832(final int n) {
		try {
			((EmulatorScreen) Emulator.getEmulator().getScreen()).handleKeyPress(Integer.parseInt(KeyMapping.deviceKeycodes[n]));
		} catch (Exception ignored) {}
	}

	private static void method839(final int n) {
		try {
			((EmulatorScreen) Emulator.getEmulator().getScreen()).handleKeyRelease(Integer.parseInt(KeyMapping.deviceKeycodes[n]));
		} catch (Exception ignored) {}
	}

	public final Shell method833() {
		return this.aShell1404;
	}

	public final boolean method834() {
		return this.aBoolean1406;
	}

	public final void method835(final Shell aShell1408) {
		this.method838();
		this.aShell1404.pack();
		final Display current = Display.getCurrent();
		this.aShell1408 = aShell1408;
		this.aShell1404.setLocation(aShell1408.getLocation().x + aShell1408.getSize().x, aShell1408.getLocation().y);
		this.aBoolean1406 = true;
		this.aShell1404.open();
		this.aShell1404.addControlListener(this);
		this.aShell1404.addDisposeListener(this);
		while (!this.aShell1404.isDisposed()) {
			if (!current.readAndDispatch()) {
				current.sleep();
			}
		}
		this.aBoolean1406 = false;
	}

	public final void dipose() {
		if (this.aShell1404 != null && !this.aShell1404.isDisposed()) {
			this.aShell1404.dispose();
		}
		this.aBoolean1406 = false;
	}

	public final void controlMoved(final ControlEvent controlEvent) {
		if (Math.abs(this.aShell1408.getLocation().x + this.aShell1408.getSize().x - this.aShell1404.getLocation().x) < 10 && Math.abs(this.aShell1408.getLocation().y - this.aShell1404.getLocation().y) < 20) {
			this.aShell1404.setLocation(this.aShell1408.getLocation().x + this.aShell1408.getSize().x, this.aShell1408.getLocation().y);
		}
	}

	public final void controlResized(final ControlEvent controlEvent) {
	}

	public final void widgetDisposed(final DisposeEvent disposeEvent) {
		this.dipose();
	}

	static void method837(final KeyPad class161, final int n) {
		method839(n);
	}

	static void method840(final KeyPad class161, final int n) {
		method832(n);
	}
}
