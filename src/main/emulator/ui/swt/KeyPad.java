package emulator.ui.swt;

import emulator.Emulator;
import emulator.KeyMapping;
import emulator.UILocale;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class KeyPad implements ControlListener, DisposeListener {
	// Indices into KeyMapping.deviceKeycodes; digits use their numeric value.
	private static final int KEY_STAR = 10;
	private static final int KEY_POUND = 11;
	private static final int KEY_UP = 12;
	private static final int KEY_DOWN = 13;
	private static final int KEY_LEFT = 14;
	private static final int KEY_RIGHT = 15;
	private static final int KEY_CENTER = 16;
	private static final int KEY_LEFT_SOFT = 17;
	private static final int KEY_RIGHT_SOFT = 18;

	private static final int FLAT_BUTTON = SWT.FLAT | SWT.PUSH;
	private static final int FLAT_ARROW = SWT.FLAT | SWT.ARROW;

	private Shell shell;
	private Shell ownerShell;
	private boolean open;

	private void createContents() {
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 15;
		layout.horizontalSpacing = 5;
		layout.marginWidth = 5;
		shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.MIN);
		shell.setText(UILocale.get("KEYPAD_FRAME_TITLE", "Keypad"));
		shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/res/icon")));
		shell.setLayout(layout);
		shell.setSize(new Point(259, 280));

		createKey(UILocale.get("KEYPAD_FRAME_LSK", "Left Soft Key"), KEY_LEFT_SOFT, FLAT_BUTTON, null);
		createSpacer();
		createKey(UILocale.get("KEYPAD_FRAME_RSK", "Right Soft Key"), KEY_RIGHT_SOFT, FLAT_BUTTON,
				keyLayout(GridData.END, GridData.CENTER, SWT.DEFAULT, SWT.DEFAULT));

		createSpacer();
		createKey("Up", KEY_UP, FLAT_ARROW, keyLayout(GridData.CENTER, GridData.CENTER, 40, 20));
		createSpacer();
		createKey("Left", KEY_LEFT, FLAT_ARROW | SWT.LEFT, keyLayout(GridData.END, GridData.CENTER, 20, 40));
		createKey("Pad", KEY_CENTER, FLAT_BUTTON, keyLayout(GridData.CENTER, GridData.CENTER, 40, 40)).setFocus();
		GridData rightLayout = new GridData();
		rightLayout.widthHint = 20;
		rightLayout.heightHint = 40;
		createKey("Right", KEY_RIGHT, FLAT_ARROW | SWT.RIGHT, rightLayout);
		createSpacer();
		createKey("Down", KEY_DOWN, FLAT_ARROW | SWT.DOWN, keyLayout(GridData.CENTER, GridData.CENTER, 40, 20));
		createSpacer();

		String[] digitLabels = {"0", "1", "2 abc", "3 def", "4 ghi", "5 jkl", "6 mno", "7 pqrs", "8 tuv", "9 wxyz"};
		for (int digit = 1; digit <= 9; digit++) {
			createNumberKey(digitLabels[digit], digit);
		}
		createNumberKey("* .", KEY_STAR);
		createNumberKey(digitLabels[0], 0);
		createNumberKey("# -+", KEY_POUND);
	}

	private void createNumberKey(String label, int keyIndex) {
		createKey(label, keyIndex, FLAT_BUTTON, keyLayout(GridData.CENTER, GridData.CENTER, 50, SWT.DEFAULT));
	}

	private static GridData keyLayout(int horizontalAlignment, int verticalAlignment, int width, int height) {
		GridData layout = new GridData();
		layout.horizontalAlignment = horizontalAlignment;
		layout.verticalAlignment = verticalAlignment;
		layout.widthHint = width;
		layout.heightHint = height;
		return layout;
	}

	private void createSpacer() {
		Button spacer = new Button(shell, FLAT_BUTTON);
		spacer.setEnabled(false);
		spacer.setVisible(false);
	}

	private Button createKey(String label, int keyIndex, int style, GridData layout) {
		Button button = new Button(shell, style);
		button.setText(label);
		if (layout != null) {
			button.setLayoutData(layout);
		}
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				dispatchKey(keyIndex, true);
			}

			@Override
			public void mouseUp(MouseEvent event) {
				dispatchKey(keyIndex, false);
			}
		});
		return button;
	}

	private static void dispatchKey(int keyIndex, boolean pressed) {
		try {
			EmulatorScreen screen = (EmulatorScreen) Emulator.getEmulator().getScreen();
			int keyCode = Integer.parseInt(KeyMapping.deviceKeycodes[keyIndex]);
			if (pressed) {
				screen.handleKeyPress(keyCode);
			} else {
				screen.handleKeyRelease(keyCode);
			}
		} catch (Exception ignored) {}
	}

	public Shell getShell() {
		return shell;
	}

	public boolean isOpen() {
		return open;
	}

	public void open(Shell ownerShell) {
		createContents();
		shell.pack();
		Display display = Display.getCurrent();
		this.ownerShell = ownerShell;
		shell.setLocation(getDockLocation());
		open = true;
		shell.open();
		shell.addControlListener(this);
		shell.addDisposeListener(this);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		open = false;
	}

	public void dispose() {
		if (shell != null && !shell.isDisposed()) {
			shell.dispose();
		}
		open = false;
	}

	private Point getDockLocation() {
		Point ownerLocation = ownerShell.getLocation();
		return new Point(ownerLocation.x + ownerShell.getSize().x, ownerLocation.y);
	}

	@Override
	public void controlMoved(ControlEvent event) {
		Point dockLocation = getDockLocation();
		Point keypadLocation = shell.getLocation();
		if (Math.abs(dockLocation.x - keypadLocation.x) < 10 && Math.abs(dockLocation.y - keypadLocation.y) < 20) {
			shell.setLocation(dockLocation);
		}
	}

	@Override
	public void controlResized(ControlEvent event) {
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {
		dispose();
	}
}
