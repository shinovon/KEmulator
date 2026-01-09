package emulator.ui.swt;

import emulator.*;
import emulator.custom.ResourceManager;
import emulator.custom.CustomMethod;
import emulator.debug.Profiler;
import emulator.debug.Profiler3D;
import emulator.graphics2D.IImage;
import emulator.graphics2D.awt.ImageAWT;
import emulator.graphics2D.swt.ImageSWT;
import emulator.ui.CommandsMenuPosition;
import emulator.ui.ICaret;
import emulator.ui.IScreen;
import emulator.ui.TargetedCommand;
import emulator.ui.swt.devutils.idea.IdeaUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Screen;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

public final class EmulatorScreen implements
		IScreen, Runnable, PaintListener, DisposeListener,
		ControlListener, KeyListener, MouseListener,
		MouseMoveListener, SelectionListener, MouseWheelListener,
		MouseTrackListener, TouchListener, Listener {
	private static Display display;
	private static int threadCount;
	private final int startWidth;
	private final int startHeight;

	private Shell shell;
	private Canvas canvas;
	private CLabel leftSoftLabel;
	private CLabel rightSoftLabel;
	private CLabel statusLabel;
	private Menu menu;
	private Menu menuMidlet;
	private Menu menuTool;
	private Menu menuView;
	private Menu menu2dEngine;
	private Menu menuM3GEngine;
	public static int locX = Integer.MIN_VALUE;
	public static int locY = Integer.MIN_VALUE;
	public static int sizeW = -1;
	public static int sizeH = -1;
	public static boolean maximized;
	public static boolean fullscreen;
	private Transform paintTransform;
	private int rotation;
	private int rotatedWidth;
	private int rotatedHeight;
	/**
	 * Real zoom, applied at last canvas invalidation. Usually doesn't match {@link Settings#canvasScale}.
	 */
	private float realZoom;
	private int windowDecorationHeight;
	private Image screenImg;
	private ImageSWT screenCopySwt;
	private ImageSWT screenImageSwt;
	private ImageSWT backBufferImageSwt;
	private ImageSWT xrayScreenImageSwt;
	private ImageAWT screenCopyAwt;
	private ImageAWT screenImageAwt;
	private ImageAWT backBufferImageAwt;
	private ImageAWT xrayScreenImageAwt;
	private static long aLong982;
	MenuItem awt2dMenuItem;
	MenuItem swt2dMenuItem;

	MenuItem swerve3dMenuItem;
	MenuItem lwj3dMenuItem;

	MenuItem loadJarMenuItem;
	MenuItem loadAutoPlayMenuItem;
	MenuItem openJadMenuItem;
	MenuItem suspendMenuItem;
	MenuItem resumeMenuItem;
	MenuItem pausestepMenuItem;
	MenuItem playResumeMenuItem;
	MenuItem restartMenuItem;
	MenuItem exitMenuItem;
	MenuItem zoomInMenuItem;
	MenuItem zoomOutMenuItem;
	MenuItem interposeNearestMenuItem;
	MenuItem interposeLowMenuItem;
	MenuItem interposeHighMenuItem;
	MenuItem speedUpMenuItem;
	MenuItem slowDownMenuItem;
	MenuItem resetSpeedMenuItem;
	MenuItem recordKeysMenuItem;
	MenuItem enableAutoplayMenuItem;
	MenuItem captureToFileMenuItem;
	MenuItem captureToClipboardMenuItem;
	MenuItem showTrackInfoMenuItem;
	MenuItem helpMenuItem;
	MenuItem updateMenuItem;
	MenuItem optionsMenuItem;
	MenuItem xrayViewMenuItem;
	MenuItem infosMenuItem;
	MenuItem alwaysOnTopMenuItem;
	MenuItem rotateScreenMenuItem;
	MenuItem forcePaintMenuItem;
	MenuItem keypadMenuItem;
	MenuItem logMenuItem;
	MenuItem watchesMenuItem;
	MenuItem profilerMenuItem;
	MenuItem methodsMenuItem;
	MenuItem memoryViewMenuItem;
	MenuItem mediaViewMenuItem;
	MenuItem smsConsoleMenuItem;
	MenuItem sensorMenuItem;
	MenuItem devUtilsMenuItem;
	MenuItem networkKillswitchMenuItem;
	private MenuItem canvasKeyboardMenuItem;
	private MenuItem changeResMenuItem;
	private Menu menuResize;
	private MenuItem centerOnScreenMenuItem;
	private MenuItem syncSizeMenuItem;
	private MenuItem fillScreenMenuItem;
	private MenuItem integerScalingMenuItem;
	private MenuItem m3gViewMenuItem;
	private MenuItem resetSizeMenuItem;
	private MenuItem fullscreenMenuItem;
	private static int captureFileCounter;
	private static String aString993;
	private int pauseState;
	private String[] pauseStateStrings;
	private boolean infosEnabled;
	private String aString1008;
	private CaretImpl caret;
	private int mouseXPress;
	private int mouseXRelease;
	private int mouseYPress;
	private int mouseYRelease;
	private boolean mouseDownInfos;
	private Vibrate vibraThread;
	private long vibra;
	private long vibraStart;
	private MenuItem rotate90MenuItem;
	private final Vector<Integer> pressedKeys = new Vector<Integer>();
	static Font f;
	private int screenX;
	private int screenY;
	private int screenWidth;
	private int screenHeight;
	private boolean pointerState;
	private boolean win;
	private MenuItem glM3DMenuItem;
	private MenuItem softM3DMenuItem;
	private Menu menuM3DEngine;
	/**
	 * Sets to true after any change to canvas size.
	 */
	private boolean wasResized;
	/**
	 * Flag to block size set event recursion.
	 */
	private boolean windowResizedByUser = true;
	private boolean windowAutosized = true;
	private StackLayout stackLayout;
	private Composite swtContent;
	private Displayable lastDisplayable;
	protected int dialogSelection;
	private boolean midletSupportsMultitouch;
	private boolean touchEnabled;
	private final Vector<Long> touchIds = new Vector<Long>();
	private int lastPointerX;
	private int lastPointerY;
	private boolean paintPending;

	private Menu commandsMenu;
	private String leftSoftLabelText, rightSoftLabelText;
	private Win32KeyboardPoller poller;

	public EmulatorScreen(int n, int n2) {
		this.pauseStateStrings = new String[]{UILocale.get("MAIN_INFO_BAR_UNLOADED", "UNLOADED"), UILocale.get("MAIN_INFO_BAR_RUNNING", "RUNNING"), UILocale.get("MAIN_INFO_BAR_PAUSED", "PAUSED")};
		display = SWTFrontend.getDisplay();
		this.initShell();
		try {
			this.initScreenBuffer(n, n2);
		} catch (Throwable e) {
			Emulator.getEmulator().getLogStream().println("Failed to initialize screen buffer with size " + n + "x" + n2 + ", falling back to 240x320.");
			e.printStackTrace();
			this.initScreenBuffer(n = 240, n2 = 320);
		}
		startWidth = n; startHeight = n2;
		this.updatePauseState();
	}

	public Shell getShell() {
		return shell;
	}

	private void initScreenBuffer(int w, int h) {
		synchronized (this) {
			if (w < 1) w = 1;
			if (h < 1) h = 1;
			final int bgcolor = 0xffffff; // 9934483
			if (Settings.g2d == 0) {
				this.screenCopySwt = new ImageSWT(w, h, false, bgcolor);
				this.screenImageSwt = new ImageSWT(w, h, false, bgcolor);
				this.backBufferImageSwt = new ImageSWT(w, h, false, bgcolor);
				this.xrayScreenImageSwt = new ImageSWT(w, h, true, bgcolor);
				return;
			}
			if (Settings.g2d == 1) {
				this.screenCopyAwt = new ImageAWT(w, h, false, bgcolor);
				this.screenImageAwt = new ImageAWT(w, h, false, bgcolor);
				this.backBufferImageAwt = new ImageAWT(w, h, false, bgcolor);
				this.xrayScreenImageAwt = new ImageAWT(w, h, true, -16777216);
			}
		}
	}

	public void setWindowIcon(final InputStream inputStream) {
		if (inputStream == null) {
			return;
		}
		try {
			this.shell.setImage(new Image(null, inputStream));
		} catch (Exception ignored) {
		}
	}

	public void showMessage(final String message) {
		try {
			setWindowOnTop(ReflectUtil.getHandle(shell), true);
		} catch (Throwable ignored) {
		}
		final MessageBox messageBox;
		(messageBox = new MessageBox(this.shell)).setText(UILocale.get("MESSAGE_BOX_TITLE", "KEmulator Alert"));
		messageBox.setMessage(message);
		messageBox.open();
	}

	public void showMessage(String title, String detail) {
		try {
			setWindowOnTop(ReflectUtil.getHandle(shell), true);
		} catch (Throwable ignored) {
		}

		Shell shell = new Shell(this.shell, SWT.DIALOG_TRIM);
		shell.setSize(450, 300);
		shell.setText(UILocale.get("MESSAGE_BOX_TITLE", "KEmulator Alert"));
		shell.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label label = new Label(composite, SWT.NONE);
		label.setText(title);

		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));

		Text text = new Text(composite_1, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		text.setText(detail);

//		shell.pack();
		Rectangle clientArea = this.shell.getMonitor().getClientArea();
		Point size = shell.getSize();
		shell.setLocation(clientArea.x + (clientArea.width - size.x) / 2, clientArea.y + (clientArea.height - size.y) / 2);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void showMessageThreadSafe(final String title, final String detail) {
		display.syncExec(() -> showMessage(title, detail));
	}

	private void getWindowPos() {
		maximized = shell.getMaximized();
		if (!maximized && !fullscreen) {
			locX = shell.getLocation().x;
			locY = shell.getLocation().y;

			sizeW = shell.getSize().x;
			sizeH = shell.getSize().y;
		}
	}

	// these two must be COMPLETELY separate paths, but we have what we have.

	public void runWithMidlet() {
		start(true);
	}

	public void runEmpty() {
		start(false);
	}

	void start(final boolean midletLoaded) {
		this.pauseState = (midletLoaded ? 1 : 0);
		this.updatePauseState(); // updated before canvas rect because it checks for pauseState inside
		try {
			if (sizeW > 0 && sizeH > 0)
				shell.setSize(sizeW, sizeH);

			windowResizedByUser = false;
			// window was already resized to minimum so it's safe to capture decor height (potentially with multiline menu)
			windowDecorationHeight = shell.getSize().y - shell.getClientArea().height;
			updateCanvasRect(true, sizeW <= 0 || sizeH <= 0, false);
			windowResizedByUser = true;

		} catch (Exception ex) {
			ex.printStackTrace();
			this.showMessage(UILocale.get("LOAD_GDIPLUS_ERROR", "Can't load \" gdiplus.dll \" !!! Plz download & copy to %system32% path."));
			System.exit(1);
			return;
		}

		Rectangle clientArea = display.getPrimaryMonitor().getClientArea();
		if (EmulatorScreen.locX == Integer.MIN_VALUE) {
			EmulatorScreen.locX = clientArea.x + (clientArea.width - shell.getSize().x) >> 1;
		}
		if (EmulatorScreen.locY == Integer.MIN_VALUE) {
			EmulatorScreen.locY = clientArea.y + (clientArea.height - shell.getSize().y) >> 1;
		}
		this.shell.setLocation(EmulatorScreen.locX, EmulatorScreen.locY);
//		EmulatorImpl.asyncExec(new WindowOpen(this, 0));
		display.asyncExec(new WindowOpen(this, 1));
		display.asyncExec(new WindowOpen(this, 2));

		if (midletLoaded) {
			String s = Emulator.getEmulator().getAppProperty("Nokia-UI-Enhancement");
			midletSupportsMultitouch = s != null && s.contains("EnableMultiPointTouchEvents");
		}

		shell.open();
		shell.addDisposeListener(this);
		shell.addControlListener(this); // added only here to avoid firing multiple times while setting shell up

		// when set in try above (i.e. before open), win10 for some reason ignores it. layout() did not help.
		// probably may be fixed by event queue clear call (readAndDispatch?)
		if (maximized)
			shell.setMaximized(true);
		if (fullscreen) {
			Settings.resizeMode = ResizeMethod.Fit;
			shell.setMaximized(true);
//			shell.setFullScreen(true);
		}

		win = Utils.win;
		if (win) {
			poller = new Win32KeyboardPoller(this);
			new Thread("KEmulator keyboard poll thread") {
				boolean b;

				public void run() {
					try {
						if (b) {
							if (poller != null) poller.pollKeyboard(canvas);
							Controllers.poll();
							return;
						}
						b = true;
						while (shell != null && !shell.isDisposed()) {
							display.asyncExec(this);
							Thread.sleep(10);
						}
					} catch (Exception e) {
						System.err.println("Exception in keyboard poll thread");
						e.printStackTrace();
					}
				}
			}.start();
		}
		try {
			while (this.shell != null && !this.shell.isDisposed()) {
				//pollKeyboard(canvas);
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Error e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			CustomMethod.close();
			System.exit(1);
		}
		this.pauseState = 0;
	}

	// KEYBOARD

	public void setSize(int x, int y) {
		if (this.pauseState == 1) {
			if (Settings.resizeMode == ResizeMethod.FollowWindowSize) {
				Settings.resizeMode = ResizeMethod.Fit;
				syncScalingModeSelection();
			}
			initScreenBuffer(x, y); // it's set automatically only in follow mode
			updateCanvasRect(true, false, true);
			Emulator.getEventQueue().sizeChanged(x, y);
		}
	}

	private void rotate90degrees() {
		this.rotation += 1;
		this.rotation %= 4;
		updateCanvasRect(true, false, true);
	}

	private void onWindowResized() {
		boolean windowWasResizedByUser = windowResizedByUser;
		// next resize won't be ours
		windowResizedByUser = true;

		if (windowWasResizedByUser)
			windowAutosized = false;

		// this will change if:
		// - menu strip wrapped to 2+ lines instead of 1
		// - WM theme change on CSD
		int newDecorH = shell.getSize().y - shell.getClientArea().height;
		boolean decorHChanged = newDecorH != windowDecorationHeight;
		windowDecorationHeight = newDecorH;
		// if triggered by our resize AND decor did not change we do not want recursion
		if (windowWasResizedByUser || decorHChanged)
			// if user is not dragging window and decor height changed - window should be resized automatically.
			updateCanvasRect(decorHChanged && !windowWasResizedByUser, false, false);
		updateStatus();
	}

	/**
	 * Entry point for sizing update.
	 * <p>
	 * Updates {@link #realZoom}, {@link #rotatedWidth}, {@link #rotatedHeight},
	 * {@link #screenX}, {@link #screenY}, {@link #screenWidth}, {@link #screenHeight} as side effect.
	 *
	 * @param allowWindowResize Allow to resize window if in manual mode and user did not touch the window or canvas overflowed.
	 * @param forceWindowReset  If true and in manual mode, resets window size. First argument will be ignored.
	 * @see #onWindowResized()
	 */
	private void updateCanvasRect(boolean allowWindowResize, boolean forceWindowReset, boolean rotate) {
		if (shell.getFullScreen() || fullscreen) {
			allowWindowResize = false;
			forceWindowReset = false;
		}

		// applying rotation
		// nonrotated - size, visible from midlet. Equal to rotated on 0° and 180°.
		int nonRotatedW = getWidth();
		int nonRotatedH = getHeight();
		switch (this.rotation) {
			case 0:
			case 2:
				rotatedWidth = nonRotatedW;
				rotatedHeight = nonRotatedH;
				break;
			case 1:
			case 3:
				rotatedWidth = nonRotatedH;
				rotatedHeight = nonRotatedW;
				break;
		}

		// calculating zoom
		int decorW = shell.getSize().x - shell.getClientArea().width;
		int statusH = fullscreen ? 0 : statusLabel.getSize().y;
		int cbw2 = canvas.getBorderWidth() * 2;
		int availableSpaceX = shell.getClientArea().width - cbw2;
		int availableSpaceY = shell.getClientArea().height - cbw2 - statusH;
		float suggestedFitZoom = Math.min(availableSpaceX / (float) rotatedWidth, availableSpaceY / (float) rotatedHeight);

		// canvas size is managed automatically by layout.

		ResizeMethod mode = Settings.resizeMode;
		if (forceWindowReset) {
			// FIXME: hack for temporarily moving to manual mode to reset window size
			mode = ResizeMethod.Manual;
		}

		float finalZoom = suggestedFitZoom;

		// applying zoom
		switch (mode) {
			case FollowWindowSize: {
				rotatedWidth = (int) ((float) availableSpaceX / Settings.canvasScale);
				rotatedHeight = (int) ((float) availableSpaceY / Settings.canvasScale);
				realZoom = Settings.canvasScale;
				switch (this.rotation) {
					case 0:
					case 2:
						nonRotatedW = rotatedWidth;
						nonRotatedH = rotatedHeight;
						break;
					case 1:
					case 3:
						nonRotatedW = rotatedHeight;
						nonRotatedH = rotatedWidth;
						break;
				}
				if (pauseState != 0 && (getWidth() != nonRotatedW || getHeight() != nonRotatedH)) {
					initScreenBuffer(nonRotatedW, nonRotatedH);
					Emulator.getEventQueue().sizeChanged(nonRotatedW, nonRotatedH);
				}

				break;
			}
			case FitInteger:
				if (suggestedFitZoom >= 1f) {
					finalZoom = (float) Math.floor(suggestedFitZoom);
				} else {
					// rounds to 50%, 33%, 25%, 20%, etc.
					finalZoom = (float) (1d / Math.ceil(1d / suggestedFitZoom));
				}

			case Fit:
				if (!rotate) {
					realZoom = finalZoom;
					break;
				}
				finalZoom = realZoom;

			case Manual: {
				if (mode == ResizeMethod.Manual) {
					finalZoom = Settings.canvasScale;
				}
				// windows' WM can resize our window because it wants to. First flag is tracking, did user ever touched the window. If no (=true), then size is ignored
				boolean windowWasPerfect = windowAutosized || (canvas.getClientArea().width == screenWidth && canvas.getClientArea().height == screenHeight);
				// 120px is windows minimal limit. Accounting for that fixes frame "sticking" to left side.
				int cw = Math.max(120, (int) (rotatedWidth * finalZoom + cbw2));

				int ch = (int) (rotatedHeight * finalZoom + cbw2);
				realZoom = finalZoom;
				boolean overflow = !rotate && (cw > shell.getClientArea().width || ch > shell.getClientArea().height - statusH);
				boolean autoResize = allowWindowResize && (windowWasPerfect || overflow) && !shell.getMaximized();
				if (autoResize || forceWindowReset) {
					windowResizedByUser = false;
					if (forceWindowReset) {
						shell.setMaximized(false);
					}
					availableSpaceX = cw - cbw2;
					availableSpaceY = ch - cbw2;
					shell.setSize(cw + decorW, ch + statusH + windowDecorationHeight);
					if (!rotate) {
						windowAutosized = true;
					}
				}

				if (rotate) {
					// recurse to rescale after rotation
					updateCanvasRect(true, false, false);
					return;
				}
				windowResizedByUser = true;
				break;
			}
			default:
				throw new IllegalStateException("Unknown resize mode: " + Settings.resizeMode);
		}

		screenWidth = (int) (nonRotatedW * realZoom);
		screenHeight = (int) (nonRotatedH * realZoom);
		switch (this.rotation) {
			case 0:
			case 2:
				screenX = (availableSpaceX - screenWidth) / 2;
				screenY = (availableSpaceY - screenHeight) / 2;
				break;
			case 1:
			case 3:
				screenY = (availableSpaceX - screenHeight) / 2;
				screenX = (availableSpaceY - screenWidth) / 2;
				break;
		}


		if (getScreenImg() != null) {
			wasResized = true;
			if (paintTransform == null)
				paintTransform = new Transform(null);

			paintTransform.setElements(1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
			switch (this.rotation) {
				case 0:
					paintTransform.translate(screenX, screenY);
					break;
				case 1:
					this.paintTransform.translate(screenY + rotatedWidth * realZoom, screenX);
					this.paintTransform.rotate(90.0F);
					break;
				case 2:
					this.paintTransform.translate(screenX + rotatedWidth * realZoom, screenY + rotatedHeight * realZoom);
					this.paintTransform.rotate(180.0F);
					break;
				case 3:
					this.paintTransform.translate(screenY, screenX + rotatedHeight * realZoom);
					this.paintTransform.rotate(270.0F);
			}
		}
		canvas.redraw();
		updateStatus();
	}

	private void zoomIn() {
		if (Settings.resizeMode == ResizeMethod.Fit || Settings.resizeMode == ResizeMethod.FitInteger) {
			Settings.resizeMode = ResizeMethod.Manual;
			Settings.canvasScale = (float) (Math.floor(realZoom * 2) / 2d);
			syncScalingModeSelection();
		}
		Settings.canvasScale = Math.min(10f, Settings.canvasScale + 0.5f);
		updateCanvasRect(true, false, false);
	}

	private void zoomOut() {
		if (Settings.resizeMode == ResizeMethod.Fit || Settings.resizeMode == ResizeMethod.FitInteger) {
			Settings.resizeMode = ResizeMethod.Manual;
			Settings.canvasScale = (float) (Math.ceil(realZoom * 2) / 2d);
			syncScalingModeSelection();
		}
		Settings.canvasScale = Math.max(1f, Settings.canvasScale - 0.5f);
		updateCanvasRect(true, false, false);
	}

	private void pauseScreen() {
		if (this.screenImg != null && !this.screenImg.isDisposed()) {
			this.screenImg.dispose();
		}
		this.screenImg = new Image(null, this.getWidth(), this.getHeight());
		final GC gc = new GC(this.screenImg);
		if (Settings.g2d == 0) {
			this.screenImageSwt.copyToScreen(gc, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.getWidth(), this.getHeight());
		} else if (Settings.g2d == 1) {
			this.screenImageAwt.copyToScreen(gc, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.getWidth(), this.getHeight());
		}
		if (this.pauseState == 2) {
			gc.setAlpha(100);
			gc.setBackground(display.getSystemColor(15));
			gc.fillRectangle(0, 0, this.getWidth(), this.getHeight());
		}
		gc.dispose();
	}

	public IImage getScreenImg() {
		synchronized (this) {
			if (Settings.g2d == 0) {
				return this.screenImageSwt;
			}
			if (Settings.g2d == 1) {
				return this.screenImageAwt;
			}
			return null;
		}
	}

	public IImage getBackBufferImage() {
		if (!Settings.asyncFlush) return getScreenImg();
		synchronized (this) {
			if (Settings.g2d == 0) {
				return this.backBufferImageSwt;
			}
			if (Settings.g2d == 1) {
				return this.backBufferImageAwt;
			}
			return null;
		}
	}

	public IImage getXRayScreenImage() {
		synchronized (this) {
			if (Settings.g2d == 0) {
				return this.xrayScreenImageSwt;
			}
			if (Settings.g2d == 1) {
				return this.xrayScreenImageAwt;
			}
			return null;
		}
	}

	public void repaint() {
		if (Emulator.getCurrentDisplay().getCurrent() == null || this.pauseState == 0) {
			return;
		}
		if (Settings.playingRecordedKeys) {
			KeyRecords h = Emulator.getRobot();
			String method698;
			while ((method698 = h.method698(EmulatorScreen.aLong982)) != null && method698.length() > 1) {
				final char char1 = method698.charAt(0);
				final String substring = method698.substring(1);
				if (char1 == '0') {
					method578(Integer.parseInt(substring));
				} else if (char1 == '1') {
					method580(Integer.parseInt(substring));
				}
				h = Emulator.getRobot();
			}
		} else if (Settings.enableKeyCache && !KeyMapping.keyCacheStack.empty()) {
			final String s = (String) KeyMapping.keyCacheStack.pop();
			if (Settings.recordKeys && !Settings.playingRecordedKeys) {
				Emulator.getRobot().print(EmulatorScreen.aLong982 + ":" + s);
			}
			final char char2 = s.charAt(0);
			final String substring2 = s.substring(1);
			if (char2 == '0') {
				method578(Integer.parseInt(substring2));
			} else if (char2 == '1') {
				method580(Integer.parseInt(substring2));
			}
		}
		if (Settings.asyncFlush) {
			if (paintPending) return;
			paintPending = true;
			display.asyncExec(this);
			return;
		}
		display.syncExec(this);
	}

	public int getWidth() {
		return this.getScreenImg().getWidth();
	}

	public int getHeight() {
		return this.getScreenImg().getHeight();
	}

	public void setLeftSoftLabel(final String label) {
		if (leftSoftLabelText != null && leftSoftLabelText.equals(label))
			return;
		leftSoftLabelText = label;
		display.syncExec(() -> {
			leftSoftLabel.setText(label);
		});
	}

	public void setRightSoftLabel(final String label) {
		if (rightSoftLabelText != null && rightSoftLabelText.equals(label))
			return;
		rightSoftLabelText = label;
		display.syncExec(() -> {
			rightSoftLabel.setText(label);
		});
	}

	private void updateStatus() {
		String var8 = this.realZoom == 1.0F ? " " : "  ";
		StringBuffer var9 = new StringBuffer();
		var9.append((int) (this.realZoom * 100.0F));
		var9.append("%");
		var9.append(var8);
		if (pauseState != 1) {
			var9.append(this.pauseStateStrings[this.pauseState]);
			var9.append(var8);
		}
		if (this.pauseState == 1 && Settings.fpsCounter) {
			var9.append(Profiler.FPS);
			var9.append(" FPS");
			var9.append(var8);
		}
		if (AppSettings.speedModifier > 0) {
			var9.append("x");
		}
		var9.append(AppSettings.speedModifier);
		this.statusLabel.setText(var9.toString());
	}


	private void initShell() {
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 3;
		layout.horizontalSpacing = 5;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		layout.makeColumnsEqualWidth = false;
		(this.shell = new Shell(fullscreen ? SWT.NONE : SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.MAX | SWT.MIN))
				.setText(Emulator.getTitle(null));
		shell.addListener(SWT.Close, event -> CustomMethod.close());
		shell.setMinimumSize(120, 50); // windows uses 120px as hard limit for width
		shell.setLayout(layout);
		try {
			if (f == null) {
				FontData fd = shell.getFont().getFontData()[0];
				fd.height = (fd.height / -fd.data.lfHeight) * 12;
				f = new Font(shell.getDisplay(), fd);
			}
			shell.setFont(f);
		} catch (Error ignored) {
		}
		initCanvas();
		(this.leftSoftLabel = new CLabel(this.shell, 0)).setText("\t");
		this.leftSoftLabel.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				if (pauseState == 0 || Emulator.getCurrentDisplay().getCurrent() == null) {
					return;
				}
				new Thread(() -> Emulator.getCurrentDisplay().getCurrent().handleSoftKeyAction(KeyMapping.soft1(), true)).start();
			}
		});
		(this.statusLabel = new CLabel(this.shell, 16777216)).setText("");
		(this.rightSoftLabel = new CLabel(this.shell, 131072)).setText("\t");
		this.rightSoftLabel.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				if (pauseState == 0 || Emulator.getCurrentDisplay().getCurrent() == null) {
					return;
				}
				new Thread(() -> Emulator.getCurrentDisplay().getCurrent().handleSoftKeyAction(KeyMapping.soft2(), true)).start();
			}
		});
		initMenu();
		setFullscreen(fullscreen);
		this.shell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.shell.addShellListener(new ShellAdapter() {
			public void shellDeactivated(ShellEvent e) {
				for (Integer n : pressedKeys) {
					handleKeyReleaseMapped(n.toString());
				}
			}
		});
	}

	private void setFullscreen(boolean fullscreen) {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = 2;
		layoutData.exclude = fullscreen;
		final GridData layoutData2;
		(layoutData2 = new GridData()).horizontalAlignment = 3;
		layoutData2.verticalSpan = 1;
		layoutData2.grabExcessHorizontalSpace = false;
		layoutData2.verticalAlignment = 2;
		layoutData2.exclude = fullscreen;
		final GridData layoutData3;
		(layoutData3 = new GridData()).horizontalAlignment = 1;
		layoutData3.verticalSpan = 1;
		layoutData3.grabExcessHorizontalSpace = false;
		layoutData3.verticalAlignment = 2;
		layoutData3.exclude = fullscreen;
		this.leftSoftLabel.setLayoutData(layoutData3);
		this.statusLabel.setLayoutData(layoutData);
		this.rightSoftLabel.setLayoutData(layoutData2);
		shell.setMenuBar(fullscreen ? null : menu);
	}

	private void changeFullscreen() {
//		shell.setMenuBar(null);
		Shell tempShell = new Shell();
		canvas.setParent(tempShell);
		shell.removeDisposeListener(this);
		shell.removeControlListener(this);
		shell.dispose();
		maximized = false;
		initShell();
		start(pauseState != 0);
		tempShell.dispose();
	}

	private void initMenu() {
		if (menu != null) {
			menu.dispose();
		}
		this.menu = new Menu(this.shell, SWT.BAR);
		final MenuItem menuItemMidlet;
		(menuItemMidlet = new MenuItem(this.menu, 64)).setText(UILocale.get("MENU_MIDLET", "Midlet"));
		final MenuItem menuItemTool;
		(menuItemTool = new MenuItem(this.menu, 64)).setText(UILocale.get("MENU_TOOL", "Tool"));
		final MenuItem menuItemView;
		(menuItemView = new MenuItem(this.menu, 64)).setText(UILocale.get("MENU_VIEW", "View"));

		this.menuView = new Menu(menuItemView);
		(this.infosMenuItem = new MenuItem(this.menuView, 32)).setText(UILocale.get("MENU_VIEW_INFO", "Infos") + "\tCtrl+I");
		this.infosMenuItem.addSelectionListener(this);
		(this.xrayViewMenuItem = new MenuItem(this.menuView, 32)).setText(UILocale.get("MENU_VIEW_XRAY", "X-Ray View") + "\tAlt+X");
		this.xrayViewMenuItem.addSelectionListener(this);
		if (!Emulator.isX64()) {
			(this.alwaysOnTopMenuItem = new MenuItem(this.menuView, 32)).setText(UILocale.get("MENU_VIEW_TOP", "Always On Top") + "\tCtrl+O");
			this.alwaysOnTopMenuItem.addSelectionListener(this);
			this.alwaysOnTopMenuItem.setSelection(Settings.alwaysOnTop);
		}
		(this.rotateScreenMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_ROTATE", "Rotate Screen") + "\tCtrl+Y");
		this.rotateScreenMenuItem.addSelectionListener(this);


		this.rotate90MenuItem = new MenuItem(this.menuView, 8);
		this.rotate90MenuItem.setText(UILocale.get("MENU_VIEW_ROTATE_90", "Rotate 90 Degrees") + "\tAlt+Y");
		this.rotate90MenuItem.addSelectionListener(this);

		(this.forcePaintMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_FORCE_PAINT", "Force Paint") + "\tCtrl+F");
		this.forcePaintMenuItem.addSelectionListener(this);
		try {
			setWindowOnTop(ReflectUtil.getHandle(shell), Settings.alwaysOnTop);
		} catch (Throwable ignored) {
		}
		new MenuItem(this.menuView, 2);
		(this.keypadMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_KEYPAD", "Keypad"));
		this.keypadMenuItem.addSelectionListener(this);
		(this.watchesMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_WATCHES", "Watches"));
		this.watchesMenuItem.addSelectionListener(this);
		(this.profilerMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_PROFILER", "Profiler"));
		this.profilerMenuItem.addSelectionListener(this);
		(this.methodsMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_METHODS", "Methods"));
		this.methodsMenuItem.addSelectionListener(this);
		(this.memoryViewMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_MEMORY", "Memory View"));
		(this.mediaViewMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_MEDIA", "Media View"));
		(this.m3gViewMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_M3GVIEW", "M3G View"));
		m3gViewMenuItem.setEnabled(Settings.g3d == 1);
		this.m3gViewMenuItem.addSelectionListener(this);
		this.memoryViewMenuItem.addSelectionListener(this);
		this.mediaViewMenuItem.addSelectionListener(this);
		(this.smsConsoleMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_SMS", "SMS Console"));
		this.smsConsoleMenuItem.addSelectionListener(this);
		(this.sensorMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_SENSOR", "Sensor Simulator"));
		this.sensorMenuItem.addSelectionListener(this);
		devUtilsMenuItem = new MenuItem(this.menuView, 8);
		devUtilsMenuItem.setText("IntelliJ IDEA support");
		devUtilsMenuItem.addSelectionListener(this);
		(this.logMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_LOG", "Log"));
		this.logMenuItem.addSelectionListener(this);

		new MenuItem(this.menuView, 2);
		(this.optionsMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_OPTIONS", "Options..."));
		this.optionsMenuItem.addSelectionListener(this);
		(this.helpMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_HELP", "About"));
		this.helpMenuItem.addSelectionListener(this);
		(this.updateMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_CHECK_UPDATE", "Check for Updates"));
		this.updateMenuItem.addSelectionListener(this);

		menuItemView.setMenu(this.menuView);
		this.menuTool = new Menu(menuItemTool);
		(this.zoomInMenuItem = new MenuItem(this.menuTool, 8)).setText(UILocale.get("MENU_TOOL_ZOOMIN", "Zoom In") + "\tPad+");
		this.zoomInMenuItem.addSelectionListener(this);
		(this.zoomOutMenuItem = new MenuItem(this.menuTool, 8)).setText(UILocale.get("MENU_TOOL_ZOOMOUT", "Zoom Out") + "\tPad-");
		this.zoomOutMenuItem.addSelectionListener(this);

		SelectionAdapter interpolationListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == interposeNearestMenuItem) {
					Settings.interpolation = SWT.NONE;

					interposeNearestMenuItem.setSelection(true);
					interposeLowMenuItem.setSelection(false);
					interposeHighMenuItem.setSelection(false);
				} else if (e.widget == interposeLowMenuItem) {
					Settings.interpolation = SWT.LOW;

					interposeNearestMenuItem.setSelection(false);
					interposeLowMenuItem.setSelection(true);
					interposeHighMenuItem.setSelection(false);
				} else if (e.widget == interposeHighMenuItem) {
					Settings.interpolation = SWT.HIGH;

					interposeNearestMenuItem.setSelection(false);
					interposeLowMenuItem.setSelection(false);
					interposeHighMenuItem.setSelection(true);
				}

				repaint();
			}
		};

		final MenuItem interpolationMenuItem = new MenuItem(this.menuTool, 64);
		interpolationMenuItem.setText(UILocale.get("MENU_TOOL_INTERPOSE", "Interpolation"));

		Menu menuInterpolation = new Menu(this.shell, 4194308);

		interposeNearestMenuItem = new MenuItem(menuInterpolation, 16);
		interposeNearestMenuItem.setText(UILocale.get("MENU_TOOL_INTER_NEAREST", "NearestNeighbor"));
		interposeNearestMenuItem.setSelection(Settings.interpolation == 0);
		interposeNearestMenuItem.addSelectionListener(interpolationListener);

		interposeLowMenuItem = new MenuItem(menuInterpolation, 16);
		interposeLowMenuItem.setText(UILocale.get("MENU_TOOL_INTER_LOW", "LowQuality"));
		interposeLowMenuItem.setSelection(Settings.interpolation == 1);
		interposeLowMenuItem.addSelectionListener(interpolationListener);

		interposeHighMenuItem = new MenuItem(menuInterpolation, 16);
		interposeHighMenuItem.setText(UILocale.get("MENU_TOOL_INTER_HIGH", "HighQuality"));
		interposeHighMenuItem.setSelection(Settings.interpolation == 2);
		interposeHighMenuItem.addSelectionListener(interpolationListener);

		interpolationMenuItem.setMenu(menuInterpolation);

		MenuItem resizeMenuItem = new MenuItem(menuTool, 64);
		resizeMenuItem.setText(UILocale.get("MENU_TOOL_RESIZE_MODE", "Display mode"));
		menuResize = new Menu(shell, 4194308);

		centerOnScreenMenuItem = new MenuItem(menuResize, SWT.RADIO);
		centerOnScreenMenuItem.setText(UILocale.get("MENU_TOOL_RESIZE_MODE_CENTER", "Center screen"));
		centerOnScreenMenuItem.addSelectionListener(this);

		fillScreenMenuItem = new MenuItem(menuResize, SWT.RADIO);
		fillScreenMenuItem.setText(UILocale.get("MENU_TOOL_RESIZE_MODE_FILL", "Fit window"));
		fillScreenMenuItem.addSelectionListener(this);

		integerScalingMenuItem = new MenuItem(menuResize, SWT.RADIO);
		integerScalingMenuItem.setText(UILocale.get("MENU_TOOL_RESIZE_MODE_FILL_INT", "Fit window (Integer scaling)"));
		integerScalingMenuItem.addSelectionListener(this);

		syncSizeMenuItem = new MenuItem(menuResize, SWT.RADIO);
		syncSizeMenuItem.setText(UILocale.get("MENU_TOOL_RESIZE_MODE_SYNC", "Sync screen size"));
		syncSizeMenuItem.addSelectionListener(this);

		new MenuItem(menuResize, 2);


		changeResMenuItem = new MenuItem(menuResize, 8);
		changeResMenuItem.setText(UILocale.get("MENU_TOOL_RESIZE_SET", "Change screen size"));
		changeResMenuItem.addSelectionListener(this);

		resetSizeMenuItem = new MenuItem(menuResize, SWT.PUSH);
		resetSizeMenuItem.setText(UILocale.get("MENU_TOOL_RESIZE_RESET", "Reset window size"));
		resetSizeMenuItem.addSelectionListener(this);

		fullscreenMenuItem = new MenuItem(menuResize, SWT.CHECK);
		fullscreenMenuItem.setText(UILocale.get("MENU_TOOL_TOGGLE_FULLSCREEN", "Toggle fullscreen") + "\tF11");
		fullscreenMenuItem.addSelectionListener(this);
		fullscreenMenuItem.setSelection(fullscreen);

		resizeMenuItem.setMenu(menuResize);

		syncScalingModeSelection();

		new MenuItem(menuTool, 2);
		(this.speedUpMenuItem = new MenuItem(this.menuTool, 8)).setText(UILocale.get("MENU_TOOL_SPEEDUP", "Speed Up") + (!Settings.altLessSpeedShortcuts ? "\tAlt+>" : "\t>"));
		this.speedUpMenuItem.addSelectionListener(this);
		(this.slowDownMenuItem = new MenuItem(this.menuTool, 8)).setText(UILocale.get("MENU_TOOL_SPEEDDOWN", "Slow Down") + (!Settings.altLessSpeedShortcuts ? "\tAlt+<" : "\t<"));
		this.slowDownMenuItem.addSelectionListener(this);
		this.resetSpeedMenuItem = new MenuItem(this.menuTool, 8);
		this.resetSpeedMenuItem.setText(UILocale.get("MENU_TOOL_SPEEDRESET", "Reset Speed") + (!Settings.altLessSpeedShortcuts ? "\tAlt+?" : "\t?"));
		this.resetSpeedMenuItem.addSelectionListener(this);
		new MenuItem(this.menuTool, 2);
		(this.recordKeysMenuItem = new MenuItem(this.menuTool, 32)).setText(UILocale.get("MENU_TOOL_RECORD_KEY", "Record Keys"));
		this.recordKeysMenuItem.addSelectionListener(this);
		this.recordKeysMenuItem.setSelection(Settings.recordKeys);
		this.recordKeysMenuItem.setEnabled(!Settings.playingRecordedKeys);
		(this.enableAutoplayMenuItem = new MenuItem(this.menuTool, 32)).setText(UILocale.get("MENU_TOOL_AUTO_PLAY", "Enable Autoplay"));
		this.enableAutoplayMenuItem.addSelectionListener(this);
		this.enableAutoplayMenuItem.setSelection(Settings.playingRecordedKeys);
		this.enableAutoplayMenuItem.setEnabled(Settings.playingRecordedKeys);
		new MenuItem(this.menuTool, 2);
		(this.captureToFileMenuItem = new MenuItem(this.menuTool, 8)).setText(UILocale.get("MENU_TOOL_CAPTURE_FILE", "Capture to File") + "\tAlt+S");
		this.captureToFileMenuItem.setAccelerator(SWT.ALT | 'S');
		this.captureToFileMenuItem.addSelectionListener(this);
		(this.captureToClipboardMenuItem = new MenuItem(this.menuTool, 8)).setText(UILocale.get("MENU_TOOL_CAPTURE_CLIP", "Capture to ClipBoard") + "\tAlt+C");
		this.captureToClipboardMenuItem.setAccelerator(SWT.ALT | 'C');
		this.captureToClipboardMenuItem.addSelectionListener(this);
		new MenuItem(this.menuTool, 2);
		(this.showTrackInfoMenuItem = new MenuItem(this.menuTool, 32)).setText(UILocale.get("MENU_TOOL_SHOW_TRACK_INFO", "Show Track Info") + "\tCtrl+F3");
		this.showTrackInfoMenuItem.setSelection(Settings.threadMethodTrack);
		this.showTrackInfoMenuItem.addSelectionListener(this);
		this.showTrackInfoMenuItem.setAccelerator(SWT.CONTROL | SWT.F3);

		this.canvasKeyboardMenuItem = new MenuItem(this.menuTool, 32);
		canvasKeyboardMenuItem.setText(UILocale.get("MENU_TOOL_QWERTY_MODE", "QWERTY Mode"));
		canvasKeyboardMenuItem.setSelection(Settings.canvasKeyboard);
		canvasKeyboardMenuItem.addSelectionListener(this);

		this.networkKillswitchMenuItem = new MenuItem(this.menuTool, 32);
		networkKillswitchMenuItem.setText(UILocale.get("MENU_TOOL_DISABLE_NETWORK_ACCESS", "Disable network access"));
		networkKillswitchMenuItem.setSelection(Settings.networkNotAvailable);
		networkKillswitchMenuItem.addSelectionListener(this);

		menuItemTool.setMenu(this.menuTool);

		this.menuMidlet = new Menu(menuItemMidlet);
		(this.loadJarMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_LOAD_JAR", "Load jar..."));
		this.loadJarMenuItem.addSelectionListener(this);
		(this.loadAutoPlayMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_LOAD_AUTO_PLAY", "Load auto-play record"));
		this.loadAutoPlayMenuItem.addSelectionListener(this);
		final MenuItem menuItem5;
		(menuItem5 = new MenuItem(this.menuMidlet, 64)).setText(UILocale.get("MENU_MIDLET_RECENTLY", "Recent jarfiles"));
		Menu aMenu1018 = new Menu(this.shell, 4194308);
		for (int n = 1; n < 5 && !Settings.recentJars[n].equals(""); ++n) {
			final String s = Settings.recentJars[n];
			final String f = s.substring(s.lastIndexOf(s.lastIndexOf(92) > 0 ? 92 : 47) + 1).trim();
			final MenuItem menuItem6 = new MenuItem(aMenu1018, 8);
			menuItem6.setText("&" + n + " " + f + " " + (s.length() > 10 ? ('[' + s.substring(0, 10) + "...]") : ('[' + s + ']')));
			menuItem6.setAccelerator(SWT.MOD1 + 49 + n - 1);
			int finalN = n;
			menuItem6.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent selectionEvent) {
					Settings.recordedKeysFile = null;
					Emulator.loadGame(Settings.recentJars[finalN], false);
				}
			});
		}
		menuItem5.setMenu(aMenu1018);
		new MenuItem(this.menuMidlet, 2);
		(this.openJadMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_JAD", "Open JAD with Notepad") + "\tCtrl+D");
		this.openJadMenuItem.addSelectionListener(this);
		new MenuItem(this.menuMidlet, 2);
		final MenuItem menuItem7;
		(menuItem7 = new MenuItem(this.menuMidlet, 64)).setText(UILocale.get("MENU_MIDLET_2D_ENGINE", "2D Engine"));
		this.menu2dEngine = new Menu(this.shell, 4194308);
		(this.awt2dMenuItem = new MenuItem(this.menu2dEngine, 16)).setText("AWT-Graphics");
		this.awt2dMenuItem.setSelection(Settings.g2d == 1);
		this.awt2dMenuItem.addSelectionListener(this);
		(this.swt2dMenuItem = new MenuItem(this.menu2dEngine, 16)).setText("SWT-GDI+");
		this.swt2dMenuItem.setSelection(Settings.g2d == 0);
		this.swt2dMenuItem.addSelectionListener(this);
		menuItem7.setMenu(this.menu2dEngine);

		final MenuItem engineM3GGroup;
		(engineM3GGroup = new MenuItem(menuMidlet, 64)).setText(UILocale.get("MENU_MIDLET_M3G_ENGINE", "M3G Engine"));
		engineM3GGroup.setMenu(menuM3GEngine = new Menu(this.shell, 4194308));

		lwj3dMenuItem = new MenuItem(menuM3GEngine, SWT.RADIO);
		lwj3dMenuItem.setText("LWJGL");
		lwj3dMenuItem.setSelection(Settings.g3d == 1);
		lwj3dMenuItem.addSelectionListener(this);
		swerve3dMenuItem = new MenuItem(menuM3GEngine, SWT.RADIO);
		swerve3dMenuItem.setText("Swerve");
		swerve3dMenuItem.setSelection(Settings.g3d == 0);
		swerve3dMenuItem.setEnabled(!Emulator.isX64());
		swerve3dMenuItem.addSelectionListener(this);

		final MenuItem engineM3DGroup;
		(engineM3DGroup = new MenuItem(menuMidlet, 64)).setText(UILocale.get("MENU_MIDLET_M3D_ENGINE", "MascotCapsule Engine"));
		engineM3DGroup.setMenu(menuM3DEngine = new Menu(this.shell, 4194308));

		glM3DMenuItem = new MenuItem(menuM3DEngine, SWT.RADIO);
		glM3DMenuItem.setText("LWJGL");
		glM3DMenuItem.setSelection(Settings.micro3d == 1);
		glM3DMenuItem.addSelectionListener(this);
		softM3DMenuItem = new MenuItem(this.menuM3DEngine, SWT.RADIO);
		softM3DMenuItem.setText("Software");
		softM3DMenuItem.setSelection(Settings.micro3d == 0);
		softM3DMenuItem.setEnabled(!Emulator.isX64());
		softM3DMenuItem.addSelectionListener(this);


		new MenuItem(this.menuMidlet, 2);
		(this.suspendMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_SUSPEND", "Suspend") + "\tCtrl+S");
		this.suspendMenuItem.addSelectionListener(this);
		(this.resumeMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_RESUME", "Resume") + "\tCtrl+E");
		this.resumeMenuItem.addSelectionListener(this);
		new MenuItem(this.menuMidlet, 2);
		(this.pausestepMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_PAUSE_STEP", "Pause/Step") + "\tCtrl+T");
		this.pausestepMenuItem.addSelectionListener(this);
		(this.playResumeMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_PLAY_RESUME", "Play/Resume") + "\tCtrl+R");
		this.playResumeMenuItem.addSelectionListener(this);
		new MenuItem(this.menuMidlet, 2);
		(this.restartMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_RESTART", "Restart") + "\tAlt+R");
		this.restartMenuItem.setAccelerator(65618);
		this.restartMenuItem.addSelectionListener(this);
		(this.exitMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_EXIT", "Exit")/* + "\tESC"*/);
		//this.exitMenuItem.setAccelerator(SWT.CONTROL | 27);
		this.exitMenuItem.addSelectionListener(this);
		menuItemMidlet.setMenu(this.menuMidlet);

		this.infosMenuItem.setAccelerator(SWT.CONTROL | 73);
		this.xrayViewMenuItem.setAccelerator(SWT.ALT | 88);
		if (alwaysOnTopMenuItem != null)
			this.alwaysOnTopMenuItem.setAccelerator(SWT.CONTROL | 79);
		this.rotateScreenMenuItem.setAccelerator(SWT.CONTROL | 89);
		this.rotate90MenuItem.setAccelerator(SWT.ALT | 89);
		this.forcePaintMenuItem.setAccelerator(SWT.CONTROL | 70);
		this.speedUpMenuItem.setAccelerator(!Settings.altLessSpeedShortcuts ? SWT.ALT | 46 : 46);
		this.slowDownMenuItem.setAccelerator(!Settings.altLessSpeedShortcuts ? SWT.ALT | 44 : 44);
		this.resetSpeedMenuItem.setAccelerator(!Settings.altLessSpeedShortcuts ? SWT.ALT | '/' : '/');
		this.suspendMenuItem.setAccelerator(SWT.CONTROL | 83);
		this.resumeMenuItem.setAccelerator(SWT.CONTROL | 69);
		this.openJadMenuItem.setAccelerator(SWT.CONTROL | 68);
		this.pausestepMenuItem.setAccelerator(SWT.CONTROL | 84);
		this.playResumeMenuItem.setAccelerator(SWT.CONTROL | 82);

		toggleMenuAccelerators(!Settings.canvasKeyboard);

		this.shell.setMenuBar(this.menu);
	}


	void toggleMenuAccelerators(final boolean b) {

	}

	public static void pause() {
		AppSettings.steps = 0;
	}

	static void pauseStep() {
		AppSettings.steps = 1;
	}

	public void resumeStep() {
		AppSettings.steps = -1;
		if (this.screenImg != null && !this.screenImg.isDisposed()) {
			this.screenImg.dispose();
		}
	}

	public void widgetSelected(final SelectionEvent selectionEvent) {
		final MenuItem menuItem;
		final Menu parent;
		if ((parent = (menuItem = (MenuItem) selectionEvent.widget).getParent()) == this.menuTool) {
			if (menuItem == this.captureToFileMenuItem) {
				if (this.pauseState != 0) {
					final String string = Emulator.getUserPath() + "/capture/";
					final File file;
					if (!(file = new File(string)).exists() || !file.isDirectory()) {
						file.mkdir();
					}
					if (Settings.g2d == 0) {
						this.screenCopySwt.saveToFile(string + EmulatorScreen.aString993 + EmulatorScreen.captureFileCounter + ".png");
					} else if (Settings.g2d == 1) {
						this.screenCopyAwt.saveToFile(string + EmulatorScreen.aString993 + EmulatorScreen.captureFileCounter + ".png");
					}
					++EmulatorScreen.captureFileCounter;
				}
			} else if (menuItem == this.captureToClipboardMenuItem) {
				if (this.pauseState != 0) {
					if (Settings.g2d == 0) {
						this.screenCopySwt.copyToClipBoard();
						return;
					}
					if (Settings.g2d == 1) {
						this.screenCopyAwt.copyToClipBoard();
					}
				}
			} else {
				if (menuItem == canvasKeyboardMenuItem) {
					Settings.canvasKeyboard = canvasKeyboardMenuItem.getSelection();
					toggleMenuAccelerators(!Settings.canvasKeyboard);
					return;
				}

				if (menuItem == networkKillswitchMenuItem) {
					networkKillswitchMenuItem.setSelection(Settings.networkNotAvailable = !Settings.networkNotAvailable);
					return;
				}
				if (menuItem == this.showTrackInfoMenuItem) {
					this.showTrackInfoMenuItem.setSelection(Settings.threadMethodTrack = !Settings.threadMethodTrack);
					return;
				}
				if (menuItem == this.zoomInMenuItem) {
					this.zoomIn();
					return;
				}
				if (menuItem == this.zoomOutMenuItem) {
					this.zoomOut();
					return;
				}
				if (menuItem == this.resetSpeedMenuItem){
					AppSettings.speedModifier = 1;
					this.updateStatus();
				} else if (menuItem == this.speedUpMenuItem) {
					if (AppSettings.speedModifier == -1) {
						AppSettings.speedModifier = 1;
						this.updateStatus();
						return;
					}
					if (AppSettings.speedModifier < 100) {
						++AppSettings.speedModifier;
						this.updateStatus();
					}
				} else if (menuItem == this.slowDownMenuItem) {
					if (AppSettings.speedModifier == 1) {
						AppSettings.speedModifier = -1;
						this.updateStatus();
						return;
					}
					if (AppSettings.speedModifier > -100) {
						--AppSettings.speedModifier;
						this.updateStatus();
					}
				} else {
					if (menuItem == this.recordKeysMenuItem) {
						Settings.recordKeys = !Settings.recordKeys;
						return;
					}
					if (menuItem == this.enableAutoplayMenuItem) {
						Settings.playingRecordedKeys = !Settings.playingRecordedKeys;
					}
				}
			}
		} else if (parent == this.menuMidlet) {
			boolean equals = false;
			if (menuItem == this.exitMenuItem) {
				shell.close();
				Thread.yield();
				this.shell.dispose();
				return;
			}
			if (menuItem == this.restartMenuItem) {
				Emulator.loadGame(null, false);
				return;
			}
			if (menuItem == this.loadJarMenuItem) {
				pauseStep();
				final FileDialog fileDialog2;
				(fileDialog2 = new FileDialog(this.shell, 4096)).setText(UILocale.get("OPEN_JAR_FILE", "Open a jar file"));
				fileDialog2.setFilterExtensions(new String[]{"*.jar;*.jad;*.jam", "*.*"});
				final String open2;
				if ((open2 = fileDialog2.open()) != null) {
					Settings.recordedKeysFile = null;
					Emulator.loadGame(open2, equals);
				}
				this.resumeStep();
				this.updatePauseState();
				return;
			}
			if (menuItem == this.loadAutoPlayMenuItem) {
				pauseStep();
				final FileDialog fileDialog3;
				(fileDialog3 = new FileDialog(this.shell, 4096)).setText(UILocale.get("OPEN_REC_FILE", "Open a record file"));
				fileDialog3.setFilterPath(Emulator.getUserPath());
				fileDialog3.setFilterExtensions(new String[]{"*.rec", "*.*"});
				Label_1321:
				{
					final String open3;
					if ((open3 = fileDialog3.open()) != null) {
						String s;
						if ((s = KeyRecords.method701(open3)) == null || !new File(s).exists()) {
							fileDialog3.setText(UILocale.get("LINK_JAR_FILE", "Specify a jar file"));
							fileDialog3.setFileName("");
							fileDialog3.setFilterExtensions(new String[]{"*.jar", "*.*"});
							if ((s = fileDialog3.open()) == null) {
								break Label_1321;
							}
						}
						Settings.recordedKeysFile = open3;
						Emulator.loadGame(s, false);
					}
				}
				this.resumeStep();
				this.updatePauseState();
				return;
			}
			if (menuItem == this.suspendMenuItem) {
				if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
					return;
				}
				this.pauseState = 2;
				Emulator.getEventQueue().queue(EventQueue.EVENT_PAUSE);
				this.pauseScreen();
				this.canvas.redraw();
				this.updatePauseState();
				return;
			}
			if (menuItem == this.resumeMenuItem) {
				if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
					return;
				}
				this.pauseState = 1;
				Emulator.getEventQueue().queue(EventQueue.EVENT_RESUME);
				this.screenImg.dispose();
				if (AppSettings.steps == 0) {
					this.pauseScreen();
					this.canvas.redraw();
				} else {
					try {
						Emulator.getCanvas().repaint();
					} catch (Exception ignored) {
					}
				}
				this.updatePauseState();
				return;
			}
			if (menuItem == this.pausestepMenuItem) {
				pauseStep();
				this.updatePauseState();
				return;
			}
			if (menuItem == this.playResumeMenuItem) {
				this.resumeStep();
				this.updatePauseState();
				return;
			}
			if (menuItem == this.openJadMenuItem) {
				try {
					final String jadPath;
					if ((jadPath = Emulator.getJadPath()) != null) {
						Emulator.openFileExternally(jadPath);
					} else {
						showMessage("The application doesn't have JAD with it.");
					}
				} catch (Exception ignored) {
				}
				this.updatePauseState();
				return;
			}
		}
		if (parent == this.menu2dEngine) {
			if (menuItem == this.awt2dMenuItem) {
				if (this.pauseState != 0 && Settings.g2d != 1) {
					Emulator.loadGame(null, 1, Settings.g3d, Settings.micro3d, false);
					return;
				}
				Settings.g2d = 1;
				this.swt2dMenuItem.setSelection(false);
				this.awt2dMenuItem.setSelection(true);
			} else if (menuItem == this.swt2dMenuItem) {
				if (this.pauseState != 0 && Settings.g2d != 0) {
					Emulator.loadGame(null, 0, Settings.g3d, Settings.micro3d, false);
					return;
				}
				Settings.g2d = 0;
				this.awt2dMenuItem.setSelection(false);
				this.swt2dMenuItem.setSelection(true);
			}
			return;
		}
		if (parent == this.menuM3GEngine) {
			if (menuItem == this.swerve3dMenuItem) {
				if (this.pauseState != 0 && Settings.g3d != 0) {
					Emulator.loadGame(null, Settings.g2d, 0, Settings.micro3d, false);
					return;
				}
				Settings.g3d = 0;
				this.lwj3dMenuItem.setSelection(false);
				this.swerve3dMenuItem.setSelection(true);
			} else if (menuItem == this.lwj3dMenuItem) {
				if (this.pauseState != 0 && Settings.g3d != 1) {
					Emulator.loadGame(null, Settings.g2d, 1, Settings.micro3d, false);
					return;
				}
				Settings.g3d = 1;
				this.swerve3dMenuItem.setSelection(false);
				this.lwj3dMenuItem.setSelection(true);
			}
			return;
		}
		if (parent == menuM3DEngine) {
			if (menuItem == softM3DMenuItem) {
				if (pauseState != 0 && Settings.micro3d != 0) {
					Emulator.loadGame(null, Settings.g2d, Settings.g3d, 0, false);
					return;
				}
				Settings.micro3d = 0;
				glM3DMenuItem.setSelection(false);
				softM3DMenuItem.setSelection(true);
			} else if (menuItem == glM3DMenuItem) {
				if (pauseState != 0 && Settings.micro3d != 1) {
					Emulator.loadGame(null, Settings.g2d, Settings.g3d, 1, false);
					return;
				}
				Settings.micro3d = 1;
				softM3DMenuItem.setSelection(false);
				glM3DMenuItem.setSelection(true);
			}
			return;
		}
		if (parent == this.menuView) {
			if (menuItem == this.helpMenuItem) {
				new About().method454(this.shell);
				return;
			}
			if (menuItem == updateMenuItem) {
				if (Updater.state == Updater.STATE_BUSY) return;
				new Thread() {
					public void run() {
						Updater.checkUpdate();
						if (Updater.state == Updater.STATE_UPDATE_AVAILABLE) {
							display.asyncExec(new Runnable() {
								public void run() {
									showUpdateDialog(1);
								}
							});
							return;
						}
						if (Updater.state == Updater.STATE_UP_TO_DATE) {
							display.syncExec(new Runnable() {
								public void run() {
									showMessage(UILocale.get("UPDATE_ALREADY_LATEST_TEXT", "You already have the latest version of KEmulator."));
								}
							});
						}
					}
				}.start();
				return;
			}
			if (menuItem == this.optionsMenuItem) {
				((Property) Emulator.getEmulator().getProperty()).open(this.shell);
				return;
			}
			if (menuItem == this.alwaysOnTopMenuItem) {
				Settings.alwaysOnTop = this.alwaysOnTopMenuItem.getSelection();
				try {
					setWindowOnTop(ReflectUtil.getHandle(shell), Settings.alwaysOnTop);
				} catch (Throwable ignored) {
				}
				return;
			}
			if (menuItem == this.logMenuItem) {
				if (((Log) Emulator.getEmulator().getLogStream()).isLogOpen()) {
					((Log) Emulator.getEmulator().getLogStream()).dispose();
					return;
				}
				((Log) Emulator.getEmulator().getLogStream()).createWindow(this.shell);
				return;
			}
			if (menuItem == this.keypadMenuItem) {
				if (((SWTFrontend) Emulator.getEmulator()).getKeyPad().method834()) {
					((SWTFrontend) Emulator.getEmulator()).getKeyPad().dipose();
					return;
				}
				((SWTFrontend) Emulator.getEmulator()).getKeyPad().method835(this.shell);
				return;
			}
			if (menuItem == this.infosMenuItem) {
				this.infosEnabled = this.infosMenuItem.getSelection();
				if (this.infosEnabled) {
					this.canvas.setCursor(new Cursor(display, 2));
					((SWTFrontend) Emulator.getEmulator()).getInfos().open(this.shell);
					return;
				}
				this.canvas.setCursor(new Cursor(display, 0));
				this.canvas.redraw();
				((SWTFrontend) Emulator.getEmulator()).getInfos().dispose();
				return;
			}
			if (menuItem == this.rotateScreenMenuItem) {
				this.setSize(this.getHeight(), this.getWidth());
				return;
			}

			if (menuItem == this.rotate90MenuItem) {
				rotate90degrees();
				return;
			}
			if (menuItem == this.forcePaintMenuItem) {
				if (Settings.g2d == 0) {
					if (AppSettings.xrayView) {
						this.xrayScreenImageSwt.cloneImage(this.screenCopySwt);
					} else {
						this.backBufferImageSwt.cloneImage(this.screenCopySwt);
					}
				} else if (Settings.g2d == 1) {
					(AppSettings.xrayView ? this.xrayScreenImageAwt : this.backBufferImageAwt).cloneImage(this.screenCopyAwt);
				}
				this.canvas.redraw();
				return;
			}
			if (menuItem == this.sensorMenuItem) {
				final File f;
				if ((f = new File(Emulator.getAbsolutePath() + "/sensorsimulator.jar")).exists()) {
					try {
						if (Emulator.isX64()) {
							String javahome = System.getProperty("java.home");
							Runtime.getRuntime().exec(new String[]{
									javahome == null || javahome.length() < 1 ? "java" : (javahome + "/bin/java"),
									"-jar",
									f.getAbsolutePath()
							});
						} else {
							final String[] array;
							(array = new String[2])[0] = "cmd.exe";
							array[1] = "/c \" java -jar " + f.getAbsolutePath() + " \"";
							Runtime.getRuntime().exec(array);
						}
					} catch (Exception ignored) {
					}
				}
				return;
			}
			if (menuItem == devUtilsMenuItem) {
				IdeaUtils.open(shell);
				return;
			}
			if (menuItem == this.smsConsoleMenuItem) {
				if (((MessageConsole) Emulator.getEmulator().getMessage()).method479()) {
					((MessageConsole) Emulator.getEmulator().getMessage()).dispose();
					return;
				}
				((MessageConsole) Emulator.getEmulator().getMessage()).open(this.shell);
				return;
			}
			if (menuItem == m3gViewMenuItem) {
				try {
					if (((SWTFrontend) Emulator.getEmulator()).getM3GView().method494()) {
						((SWTFrontend) Emulator.getEmulator()).getM3GView().close();
						return;
					}
					((SWTFrontend) Emulator.getEmulator()).getM3GView().method226();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				return;
			}
			if (menuItem == this.xrayViewMenuItem) {
				AppSettings.xrayView = this.xrayViewMenuItem.getSelection();
				return;
			}
			if (menuItem == this.watchesMenuItem) {
				if (((SWTFrontend) Emulator.getEmulator()).getClassWatcher().isVisible()) {
					((SWTFrontend) Emulator.getEmulator()).getClassWatcher().dispose();
					return;
				}
				((SWTFrontend) Emulator.getEmulator()).getClassWatcher().open(this.shell);
				return;
			}
			if (menuItem == this.profilerMenuItem) {
				if (((SWTFrontend) Emulator.getEmulator()).getProfiler().isVisible()) {
					((SWTFrontend) Emulator.getEmulator()).getProfiler().dispose();
					return;
				}
				((SWTFrontend) Emulator.getEmulator()).getProfiler().open(this.shell);
				return;
			}
			if (menuItem == this.methodsMenuItem) {
				if (((SWTFrontend) Emulator.getEmulator()).getMethods().method438()) {
					((SWTFrontend) Emulator.getEmulator()).getMethods().dispose();
					return;
				}
				((SWTFrontend) Emulator.getEmulator()).getMethods().showWindow();
				return;
			}
			if (menuItem == this.memoryViewMenuItem) {
				if (((SWTFrontend) Emulator.getEmulator()).getMemory().isShown()) {
					((SWTFrontend) Emulator.getEmulator()).getMemory().dispose();
					return;
				}
				((SWTFrontend) Emulator.getEmulator()).getMemory().open();
			}
			if (menuItem == this.mediaViewMenuItem) {
				if (((SWTFrontend) Emulator.getEmulator()).getMedia().isShown()) {
					((SWTFrontend) Emulator.getEmulator()).getMedia().dispose();
					return;
				}
				((SWTFrontend) Emulator.getEmulator()).getMedia().open();
			}
		} else if (parent == menuResize) {
			if (menuItem == centerOnScreenMenuItem) {
				Settings.resizeMode = ResizeMethod.Manual;
				syncScalingModeSelection();
				// see zoomIn/zoomOut
				Settings.canvasScale = (float) (Math.floor(realZoom * 2) / 2d);
				updateCanvasRect(true, false, false);
			} else if (menuItem == syncSizeMenuItem) {
				Settings.resizeMode = ResizeMethod.FollowWindowSize;
				syncScalingModeSelection();
				updateCanvasRect(true, false, false);
			} else if (menuItem == fillScreenMenuItem) {
				Settings.resizeMode = ResizeMethod.Fit;
				syncScalingModeSelection();
				updateCanvasRect(true, false, false);
			} else if (menuItem == integerScalingMenuItem) {
				Settings.resizeMode = ResizeMethod.FitInteger;
				syncScalingModeSelection();
				updateCanvasRect(true, false, false);
			} else if (menuItem == resetSizeMenuItem) {
				if (getWidth() != startWidth || getHeight() != startHeight) {
					initScreenBuffer(startWidth, startHeight);
					Emulator.getEventQueue().sizeChanged(startWidth, startHeight);
				}

				Settings.canvasScale = 1f;
				updateCanvasRect(true, true, false);
			} else if (menuItem == changeResMenuItem) {
				ScreenSizeDialog d = new ScreenSizeDialog(shell, getWidth(), getHeight());
				int[] r = d.open();
				if (r != null) {
					setSize(r[0], r[1]);
				}
			} else if (menuItem == fullscreenMenuItem) {
				fullscreen = fullscreenMenuItem.getSelection();
				changeFullscreen();
			}
		}
	}

	private void syncScalingModeSelection() {
		centerOnScreenMenuItem.setSelection(Settings.resizeMode == ResizeMethod.Manual);
		fillScreenMenuItem.setSelection(Settings.resizeMode == ResizeMethod.Fit);
		integerScalingMenuItem.setSelection(Settings.resizeMode == ResizeMethod.FitInteger);
		syncSizeMenuItem.setSelection(Settings.resizeMode == ResizeMethod.FollowWindowSize);
	}

	private static void setWindowOnTop(final long handle, final boolean b) {
		// TODO
		try {
			Class cls = OS.class;
			Method setWindowPos;
			try {
				setWindowPos = cls.getMethod("SetWindowPos", int.class, int.class, int.class, int.class, int.class, int.class, int.class);
				setWindowPos.invoke(null, (int) handle, b ? -1 : -2, 0, 0, 0, 0, 19);
			} catch (Exception e) {
				setWindowPos = cls.getMethod("SetWindowPos", long.class, long.class, int.class, int.class, int.class, int.class, int.class);
				setWindowPos.invoke(null, handle, b ? -1L : -2L, 0, 0, 0, 0, 19);
			}
		} catch (Throwable ignored) {
		}

	}

	private void updatePauseState() {
		this.suspendMenuItem.setEnabled(this.pauseState == 1);
		this.resumeMenuItem.setEnabled(this.pauseState == 2);
		this.restartMenuItem.setEnabled(this.pauseState != 0 && !AppSettings.uei);
		this.xrayViewMenuItem.setSelection(AppSettings.xrayView);
		this.forcePaintMenuItem.setEnabled(this.pauseState != 0);
		this.pausestepMenuItem.setEnabled(this.pauseState != 0);
		this.playResumeMenuItem.setEnabled(AppSettings.steps >= 0 && this.pauseState != 0);
		this.openJadMenuItem.setEnabled(this.pauseState != 0);
		this.watchesMenuItem.setEnabled(this.pauseState != 0);
		this.profilerMenuItem.setEnabled(this.pauseState != 0);
		this.memoryViewMenuItem.setEnabled(this.pauseState != 0);
		this.methodsMenuItem.setEnabled(this.pauseState != 0);
		m3gViewMenuItem.setEnabled(Settings.g3d == 1 && pauseState != 0);
//		fullscreenMenuItem.setEnabled(pauseState != 0);
		mediaViewMenuItem.setEnabled(pauseState != 0);
		this.updateStatus();
	}

	public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
	}

	private void updateInfos(final int n, final int n2) {
		int[] i = transformPointer(n, n2);
		final int n3 = i[0];
		final int n4 = i[1];
		if (n3 < 0 || n4 < 0 || n3 > this.getWidth() - 1 || n4 > this.getHeight() - 1) {
			return;
		}
		final int rgb;
		final int n5 = (rgb = this.getScreenImg().getRGB(n3, n4)) >> 16 & 0xFF;
		final int n6 = rgb >> 8 & 0xFF;
		final int n7 = rgb & 0xFF;
		this.aString1008 = UILocale.get("INFO_FRAME_POS", "Pos") + "(" + n3 + "," + n4 + ")\n" + UILocale.get("INFO_FRAME_COLOR", "Color") + "(";
		EmulatorScreen class93;
		StringBuffer sb2;
		if (Settings.infoColorHex) {
			final StringBuffer sb = new StringBuffer();
			class93 = this;
			sb2 = sb.append(this.aString1008).append(n5).append(",").append(n6).append(",").append(n7);
		} else {
			final StringBuffer sb3 = new StringBuffer();
			class93 = this;
			sb2 = sb3.append(this.aString1008).append("0x").append(Integer.toHexString(rgb).toUpperCase());
		}
		class93.aString1008 = sb2.toString();
		i = transformPointer(mouseXPress, mouseYPress);
		int[] i2 = transformPointer(mouseXRelease, mouseYRelease);
		this.aString1008 = this.aString1008 + ")\n" +
				UILocale.get("INFO_FRAME_RECT", "R")

				+ "(" + (i[0]) + "," +
				(i[1]) + "," + (i2[0] - i[0]) +
				"," + (i2[1] - i[1]) + ")";
        /*
        "(" + (int)(this.mouseXPress / this.zoom) + "," +
        (int)(this.mouseYPress / this.zoom) + "," + (int)((this.mouseXRelease - this.mouseXPress) / this.zoom) +
        "," + (int)((this.mouseYRelease - this.mouseYPress) / this.zoom) + ")";
        */
		((SWTFrontend) Emulator.getEmulator()).getInfos().setText(this.aString1008);
	}

	private void initCanvas() {
		if (canvas != null) {
			canvas.setParent(shell);
			return;
		}
		final GridData layoutData;
		(layoutData = new GridData()).horizontalSpan = 3;
		layoutData.verticalAlignment = 4;
		layoutData.verticalSpan = 1;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = 4;
		// 0x20040800, 537135104
		(this.canvas = new Canvas(this.shell, SWT.DOUBLE_BUFFERED | SWT.ON_TOP)).setLayoutData(layoutData);
		this.canvas.addKeyListener(this);
		this.canvas.addMouseListener(this);
		this.canvas.addMouseWheelListener(this);
		this.canvas.addMouseMoveListener(this);
		this.canvas.getShell().addMouseTrackListener(this);
		this.canvas.addPaintListener(this);
		canvas.addListener(SWT.MouseHorizontalWheel, this);
		canvas.addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent controlEvent) {

			}

			@Override
			public void controlResized(ControlEvent controlEvent) {
				caret.a(paintTransform, rotation);
				if (swtContent != null && lastDisplayable != null && lastDisplayable instanceof Screen
						&& ((Screen) lastDisplayable)._isSWT()) {
					((Screen) lastDisplayable)._swtUpdateSizes();
				}
			}
		});
		canvas.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				if (lastDisplayable != null && lastDisplayable instanceof Form) {
					Point p = canvas.toControl(event.x, event.y);
					int[] t = transformPointer(p.x, p.y);
					if (t[0] >= 0 && t[1] >= 0) {
						Form form = (Form) lastDisplayable;
						if (form._tryShowMenuAt(t[0], t[1]))
							return;
					}
				}
				event.doit = false;
			}
		});
		try {
			canvas.setTouchEnabled(true);
			touchEnabled = canvas.getTouchEnabled();
			canvas.addTouchListener(this);
		} catch (Throwable ignored) {
		}

		stackLayout = new StackLayout();
		canvas.setLayout(stackLayout);
		this.method589();
		this.caret = new CaretImpl(this.canvas);
		shell.layout();

		commandsMenu = new Menu(canvas);
		canvas.setMenu(commandsMenu);
	}

	public void showCommandsList(final Vector<TargetedCommand> cmds, CommandsMenuPosition target, int tx, int ty) {
		display.syncExec(() -> {
			CommandsMenuListener listener = new CommandsMenuListener();
			for (MenuItem mi : commandsMenu.getItems()) {
				mi.dispose();
			}
			if (cmds.isEmpty()) {
				commandsMenu.setVisible(false);
				return;
			}
			for (TargetedCommand cmd : cmds) {
				if (cmd == null) {
					new MenuItem(commandsMenu, SWT.SEPARATOR);
					continue;
				}
				MenuItem mi = new MenuItem(commandsMenu, cmd.isChoice() ? SWT.RADIO : SWT.PUSH);
				mi.setText(cmd.text);
				mi.setData(cmd);
				mi.addSelectionListener(listener);
				if (cmd.wasSelected)
					mi.setSelection(true);

			}
			if (target == CommandsMenuPosition.CommandsButton) {
				commandsMenu.setLocation(getMenuLocation());
				commandsMenu.setVisible(true);
			} else if (target == CommandsMenuPosition.Custom) {
				int[] t = transformCaret(tx, ty, true);
				Point p = getCanvas().toDisplay(new Point(t[0], t[1]));
				commandsMenu.setLocation(p);
				commandsMenu.setVisible(true);
			}

			// menu under cursor is always triggered by right click, so not showing it again because GTK breaks.

		});
	}

	public void forceCloseCommandsList() {
		display.syncExec(() -> commandsMenu.setVisible(false));
	}

	public void paintControl(final PaintEvent paintEvent) {
		final GC gc = paintEvent.gc;
		Rectangle size = canvas.getClientArea();

		if (this.pauseState == 0) {
			// Game not running, show info label
			gc.setBackground(display.getSystemColor(22));
			gc.fillRectangle(0, 0, size.width, size.height);
			gc.setForeground(display.getSystemColor(21));
			gc.setFont(f);
			gc.drawText(Emulator.getInfoString(), size.width >> 3, size.height >> 3, true);
			return;
		}

		gc.setInterpolation(Settings.interpolation);
		int origWidth = getWidth();
		int origHeight = getHeight();

		if (swtContent == null) {
			try {
				// Fill background
				if (screenX > 0 || screenY > 0) {
					gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
					gc.fillRectangle(0, 0, size.width, size.height);
				}
				// Apply transform
				gc.setTransform(this.paintTransform);
				// Draw canvas buffer
				if (this.screenImg == null || this.screenImg.isDisposed()) {
					IImage buf = Settings.g2d == 1 ? screenCopyAwt : screenCopySwt;
					if (screenX == 0 && screenY == 0 && origWidth == screenWidth && origHeight == screenHeight) {
						buf.copyToScreen(gc);
					} else {
						buf.copyToScreen(gc, 0, 0, origWidth, origHeight, 0, 0, screenWidth, screenHeight);
					}
				} else {
					// Hold image (paused)
					gc.drawImage(this.screenImg, 0, 0, origWidth, origHeight, 0, 0, screenWidth, screenHeight);
				}
			} catch (Exception ignored) {
			}
		}
		gc.setAdvanced(false);
		drawMouseInputInfo(gc);
		if (wasResized) {
			caret.setWindowZoom((float) screenHeight / (float) origHeight);
			wasResized = false;
		}
	}

	private void drawMouseInputInfo(final GC gc) {
		if (this.infosEnabled && (this.mouseXPress != this.mouseXRelease || this.mouseYPress != this.mouseYRelease)) {
			try {
				OS_SetROP2(gc, 7);
				gc.setForeground(display.getSystemColor(1));
				gc.drawRectangle(this.mouseXPress, this.mouseYPress, this.mouseXRelease - this.mouseXPress, this.mouseYRelease - this.mouseYPress);
				OS_SetROP2(gc, 13);
			} catch (Throwable ignored) {
			}
		}
	}

	private static void OS_SetROP2(GC gc, int j) {
		try {
			long i = ReflectUtil.getHandle(gc);
			Class<?> os = OS.class;
			Method m = null;
			try {
				m = os.getMethod("SetROP2", int.class, int.class);
				m.invoke(null, (int) i, j);
			} catch (Exception e) {
				m = os.getMethod("SetROP2", long.class, int.class);
				m.invoke(null, i, j);
			}
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	public float getZoom() {
		return realZoom;
	}

	public void run() {
		paintPending = false;
		if (this.pauseState != 1) {
			return;
		}
		if (Settings.pollKeyboardOnRepaint) {
			if (poller != null) poller.pollKeyboard(canvas);
			Controllers.poll();
		}
		if (swtContent == null) {
			if (Settings.g2d == 0) {
				this.screenImageSwt.cloneImage(this.screenCopySwt);
			} else if (Settings.g2d == 1) {
				this.screenImageAwt.cloneImage(this.screenCopyAwt);
			}
		} else {
			swtContent.redraw();
		}
		this.canvas.redraw();
		this.updateStatus();
		++EmulatorScreen.aLong982;
		Emulator.getEmulator().syncValues();
		Profiler.reset();
		Profiler3D.reset();
	}


	public void setCurrent(final javax.microedition.lcdui.Displayable d) {
		display.syncExec(new Runnable() {
			public void run() {
				if (lastDisplayable != null) {
					if (swtContent != null) swtContent.setVisible(false);
					if (lastDisplayable instanceof Screen)
						((Screen) lastDisplayable)._swtHidden();
					lastDisplayable = null;
				}
				if (d == null) return;

				lastDisplayable = d;
				if (d instanceof javax.microedition.lcdui.Canvas) {
					stackLayout.topControl = null;
					swtContent = null;
				} else if (d instanceof Screen) {
					Composite c = (Composite) ((Screen) d)._getSwtContent();
					stackLayout.topControl = c;
					swtContent = c;
					if (c != null) {
						c.setVisible(true);
						c.layout();
					}
				}
				updateTitleInternal();
				canvas.layout();
				if (lastDisplayable instanceof Screen)
					((Screen) lastDisplayable)._swtShown();
			}
		});
	}

	public void updateTitle() {
		display.syncExec(this::updateTitleInternal);
	}

	private void updateTitleInternal() {
		String title = null;
		if (lastDisplayable == null ||
				(title = lastDisplayable.getTitle()) == null ||
				(title = title.trim()).isEmpty()) {
			title = Emulator.getTitle(null);
		} else {
			title = Emulator.getTitle(title);
		}
		shell.setText(title);
	}

	private static void method578(final int n) {
		if (Emulator.getCurrentDisplay().getCurrent() == null) {
			return;
		}
		if (!Emulator.getCurrentDisplay().getCurrent().handleSoftKeyAction(n, true)) {
			if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
				Emulator.getCanvas()._invokeKeyPressed(n);
				return;
			}
			Emulator.getScreen()._invokeKeyPressed(n);
		}
	}

	private static void method580(final int n) {
		if (Emulator.getCurrentDisplay().getCurrent() == null) {
			return;
		}
		if (!Emulator.getCurrentDisplay().getCurrent().handleSoftKeyAction(n, false)) {
			if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
				Emulator.getCanvas()._invokeKeyReleased(n);
				return;
			}
			Emulator.getScreen()._invokeKeyReleased(n);
		}
	}

	public void keyPressed(final KeyEvent keyEvent) {
		if (keyEvent.keyCode == 16777261/*&& (keyEvent.stateMask & SWT.CONTROL) != 0*/) {
			this.zoomOut();
			return;
		}
		if (keyEvent.keyCode == 16777259/*&& (keyEvent.stateMask & SWT.CONTROL) != 0*/) {
			this.zoomIn();
			return;
		}
		if (keyEvent.keyCode == SWT.F11) {
			fullscreenMenuItem.setSelection(fullscreen = !fullscreen);
			changeFullscreen();
			return;
		}
		int n = keyEvent.keyCode & 0xFEFFFFFF;
		caret._keyPressed(keyEvent);
		if (keyEvent.character >= 33 && keyEvent.character <= 90 && Settings.canvasKeyboard && !(n >= 48 && n <= 57))
			n = keyEvent.character;
		handleKeyPress(n);
	}

	public void keyReleased(final KeyEvent keyEvent) {
		if (!Settings.canvasKeyboard && poller != null) {
			return;
		}
		int n = keyEvent.keyCode & 0xFEFFFFFF;
		if (keyEvent.character >= 33 && keyEvent.character <= 90 && Settings.canvasKeyboard && !(n >= 48 && n <= 57))
			n = keyEvent.character;
		handleKeyRelease(n);
	}


	void handleKeyPress(int n) {
		if (this.pauseState == 0 || Settings.playingRecordedKeys || ((n < 0 || n >= 256) && !Settings.canvasKeyboard)) {
			return;
		}
		handleKeyPressMapped(mapKey(n));
	}

	void handleKeyPressMapped(String r) {
		if (r == null) return;
		int n = Integer.parseInt(r);

		if (pressedKeys.contains(n)) {
			if (Emulator.getCurrentDisplay().getCurrent() instanceof Screen) {
				Emulator.getEventQueue().keyRepeat(n);
			} else if (AppSettings.enableKeyRepeat) {
				if (AppSettings.keyPressOnRepeat) {
					Emulator.getEventQueue().keyPress(n);
				} else {
					Emulator.getEventQueue().keyRepeat(n);
				}
			}
			return;
		}
		synchronized (pressedKeys) {
			if (!pressedKeys.contains(n)) {
				pressedKeys.add(n);
			}
		}
		if (Settings.enableKeyCache) {
			KeyMapping.keyCacheStack.push('0' + r);
			return;
		}
		if (Settings.recordKeys && !Settings.playingRecordedKeys) {
			Emulator.getRobot().print(EmulatorScreen.aLong982 + ":" + '0' + r);
		}
		Emulator.getEventQueue().keyPress(n);
	}

	void handleKeyRelease(int n) {
		if (this.pauseState == 0 || Settings.playingRecordedKeys || ((n < 0 || n >= 256) && !Settings.canvasKeyboard)) {
			return;
		}
		handleKeyReleaseMapped(mapKey(n));
	}

	void handleKeyReleaseMapped(String r) {
		if (r == null) return;
		int n = Integer.parseInt(r);

		synchronized (pressedKeys) {
			if (win && !pressedKeys.contains(n)) {
				return;
			}
			pressedKeys.removeElement(n);
		}

		if (Settings.enableKeyCache) {
			KeyMapping.keyCacheStack.push('1' + r);
			return;
		}
		if (Settings.recordKeys && !Settings.playingRecordedKeys) {
			Emulator.getRobot().print(EmulatorScreen.aLong982 + ":" + '1' + r);
		}
		Emulator.getEventQueue().keyRelease(n);
	}

	void onKeyUp(int n, Shell shell) {
		if (shell != this.shell) {
			((SWTFrontend) Emulator.getEmulator()).getM3GView().keyReleased(n);
			return;
		}
		if (n <= 0 || this.pauseState == 0 || Settings.playingRecordedKeys) {
			return;
		}
		final String r;
		if ((r = mapKey(n)) == null) {
			return;
		}
		n = Integer.parseInt(r);
		synchronized (pressedKeys) {
			if (!pressedKeys.contains(n)) {
				return;
			}
			pressedKeys.removeElement(n);
		}
		if (Settings.enableKeyCache) {
			KeyMapping.keyCacheStack.push('1' + r);
			return;
		}
		if (Settings.recordKeys && !Settings.playingRecordedKeys) {
			Emulator.getRobot().print(EmulatorScreen.aLong982 + ":" + '1' + r);
		}
		Emulator.getEventQueue().keyRelease(Integer.parseInt(r));
	}

	private String mapKey(int n) {
		return KeyMapping.replaceKey(n);
	}

	public void mouseDoubleClick(final MouseEvent mouseEvent) {
		if (Settings.playingRecordedKeys) {
			return;
		}
		if (this.pauseState == 0 || Emulator.getCurrentDisplay().getCurrent() == null) {
			return;
		}
		if (mouseEvent.button != 1 || Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
			return;
		}
		try {
			Emulator.getScreen()._invokeKeyPressed(KeyMapping.getArrowKeyFromDevice(javax.microedition.lcdui.Canvas.FIRE));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int[] transformPointer(int x, int y) {
		// Map coordinates on window to canvas
		int w, h;
		if (rotation % 2 == 1) {
			w = getHeight();
			h = getWidth();
			x = (int) ((x - screenY) / ((float) screenHeight / w));
			y = (int) ((y - screenX) / ((float) screenWidth / h));
		} else {
			w = getWidth();
			h = getHeight();
			x = (int) ((x - screenX) / ((float) screenWidth / w));
			y = (int) ((y - screenY) / ((float) screenHeight / h));
		}
		int tmp;
		switch (this.rotation) {
			case 0:
				break;
			case 1:
				tmp = x;
				x = y;
				y = w - tmp;
				break;
			case 2:
				x = w - x;
				y = h - y;
				break;
			case 3:
				tmp = x;
				x = h - y;
				y = tmp;
		}
		return new int[]{x, y};
	}

	public int[] transformCaret(int x, int y, boolean offset) {
		// Map coordinates on window to canvas
		int w, h;
		if (rotation % 2 == 1) {
			w = getHeight();
			h = getWidth();
			x = (int) (x / ((float) w / screenWidth)) + (offset ? screenX : 0);
			y = (int) (y / ((float) h / screenHeight)) + (offset ? screenY : 0);
		} else {
			w = getWidth();
			h = getHeight();
			x = (int) (x / ((float) w / screenWidth)) + (offset ? screenX : 0);
			y = (int) (y / ((float) h / screenHeight)) + (offset ? screenY : 0);
		}
		int tmp;
		switch (this.rotation) {
			case 0:
				break;
			case 1:
				tmp = x;
				x = y;
				y = w - tmp;
				break;
			case 2:
				x = w - x;
				y = h - y;
				break;
			case 3:
				tmp = x;
				x = h - y;
				y = tmp;
		}
		return new int[]{x, y};
	}

	public void mouseDown(final MouseEvent mouseEvent) {
		if (this.infosEnabled && !this.mouseDownInfos) {
			this.mouseXPress = mouseEvent.x;
			this.mouseYPress = mouseEvent.y;
			this.mouseXRelease = mouseEvent.x;
			this.mouseYRelease = mouseEvent.y;
			this.mouseDownInfos = true;
		}
		if (this.pauseState == 0 || Settings.playingRecordedKeys
				|| Emulator.getCurrentDisplay().getCurrent() == null) {
			return;
		}
		int[] i = transformPointer(mouseEvent.x, mouseEvent.y);
		if (i[0] < 0 || i[1] < 0 || i[0] >= getWidth() || i[1] >= getHeight()) {
			return;
		}
		pointerState = true;
		Emulator.getEventQueue().mouseDown(i[0], i[1], 0);
		lastPointerX = i[0];
		lastPointerY = i[1];
		caret.mouseDown(i[0], i[1]);
	}

	public void mouseUp(final MouseEvent mouseEvent) {
		this.mouseDownInfos = false;
		if (this.pauseState == 0 || Settings.playingRecordedKeys
				|| Emulator.getCurrentDisplay().getCurrent() == null) {
			return;
		}
		if (!pointerState) return;
		pointerState = false;
		int[] i = transformPointer(mouseEvent.x, mouseEvent.y);
		lastPointerX = i[0];
		lastPointerY = i[1];
		Emulator.getEventQueue().mouseUp(i[0], i[1], 0);
	}

	public void mouseMove(final MouseEvent mouseEvent) {
		if (this.infosEnabled) {
			if (this.mouseDownInfos) {
				this.mouseXRelease = mouseEvent.x;
				this.mouseYRelease = mouseEvent.y;
				this.canvas.redraw();
			}
			this.updateInfos(mouseEvent.x, mouseEvent.y);
//            return;
		}
		if (this.pauseState == 0 || Settings.playingRecordedKeys
				|| Emulator.getCurrentDisplay().getCurrent() == null) {
			return;
		}
		if (pointerState && (mouseEvent.stateMask & 0x80000) != 0x0) {
			int[] i = transformPointer(mouseEvent.x, mouseEvent.y);
			if (i[0] < 0 || i[1] < 0 || i[0] >= getWidth() || i[1] >= getHeight()
					|| (lastPointerX == i[0] && lastPointerY == i[1])) {
				return;
			}
			lastPointerX = i[0];
			lastPointerY = i[1];
			Emulator.getEventQueue().mouseDrag(i[0], i[1], 0);
		}
	}

	public void touch(TouchEvent touchEvent) {
		if (!midletSupportsMultitouch) {
			return;
		}
		for (Touch touch : touchEvent.touches) {
			Point p = canvas.toControl(touch.x, touch.y);
			int[] pos = transformPointer(p.x, p.y);
			int id;
			if (touch.primary) {
//				id = 0;
				continue;
			}
			id = touchIds.indexOf(touch.id);
			if (id == -1) {
				touchIds.add(touch.id);
				id = touchIds.size();
			} else id++;

			switch (touch.state) {
				case SWT.TOUCHSTATE_DOWN:
					Emulator.getEventQueue().mouseDown(pos[0], pos[1], id);
					break;
				case SWT.TOUCHSTATE_UP:
					Emulator.getEventQueue().mouseUp(pos[0], pos[1], id);
					touchIds.remove(touch.id);
					break;
				case SWT.TOUCHSTATE_MOVE:
					Emulator.getEventQueue().mouseDrag(pos[0], pos[1], id);
					break;

			}
		}
	}

	public void mouseEnter(MouseEvent arg0) {
	}

	public void mouseExit(MouseEvent e) {
	}

	public void mouseHover(MouseEvent arg0) {

	}

	public void widgetDisposed(final DisposeEvent disposeEvent) {
		Emulator.getEmulator().disposeSubWindows();
		Emulator.notifyDestroyed();
		if (this.pauseState != 0) {
			Emulator.getEventQueue().queue(EventQueue.EVENT_EXIT);
		}
	}

	public void controlMoved(final ControlEvent controlEvent) {
		if (controlEvent.widget != shell)
			return;
		this.getWindowPos();
		if (((Log) Emulator.getEmulator().getLogStream()).isLogOpen()) {
			final Shell logWindow = ((Log) Emulator.getEmulator().getLogStream()).getLogShell();
			if (((Log) Emulator.getEmulator().getLogStream()).isAttachedToParent() && !logWindow.isDisposed()) {
				logWindow.setLocation(this.shell.getLocation().x + this.shell.getSize().x, this.shell.getLocation().y);
			}
		}
		if (((MessageConsole) Emulator.getEmulator().getMessage()).method479()) {
			final Shell method329 = ((MessageConsole) Emulator.getEmulator().getMessage()).method480();
			if (((MessageConsole) Emulator.getEmulator().getMessage()).method488() && !method329.isDisposed()) {
				method329.setLocation(this.shell.getLocation().x - method329.getSize().x, this.shell.getLocation().y);
			}
		}
		final Shell method330;
		if (((SWTFrontend) Emulator.getEmulator()).getInfos().isShown() && !(method330 = ((SWTFrontend) Emulator.getEmulator()).getInfos().getShell()).isDisposed()) {
			method330.setLocation(this.shell.getLocation().x + this.shell.getSize().x, this.shell.getLocation().y);
		}
		final Shell method331;
		if (((SWTFrontend) Emulator.getEmulator()).getKeyPad().method834() && !(method331 = ((SWTFrontend) Emulator.getEmulator()).getKeyPad().method833()).isDisposed()) {
			method331.setLocation(this.shell.getLocation().x + this.shell.getSize().x, this.shell.getLocation().y);
		}
	}

	public void controlResized(final ControlEvent controlEvent) {
		onWindowResized();
		this.controlMoved(controlEvent);
	}

	private void mp(int i) {
		if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
			Emulator.getCanvas()._invokeKeyPressed(i);
		} else {
			Emulator.getScreen()._invokeKeyPressed(i);
		}
	}

	private void mr(int i) {
		if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
			Emulator.getCanvas()._invokeKeyReleased(i);
		} else {
			Emulator.getScreen()._invokeKeyReleased(i);
		}
	}

	private void mrp(int i) {
		if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
			Emulator.getCanvas()._invokeKeyRepeated(i);
		}
	}

	public void startVibra(final long aLong1013) {
		if (!Settings.enableVibration) {
			return;
		}
		if (maximized) {
			return;
		}
		this.vibra = aLong1013;
		if (this.vibra == 0L) {
			this.stopVibra();
			return;
		}
		this.vibraStart = System.currentTimeMillis();
		if (this.vibraThread == null) {
			this.vibraThread = new Vibrate(this);
			new Thread(this.vibraThread, "KEmulator vibrate-" + (++threadCount)).start();
			return;
		}
		this.vibraThread.stop = false;
	}

	public void stopVibra() {
		if (this.vibraThread != null) {
			this.vibraThread.stop = true;
		}
	}

	private void method589() {
		final DropTarget dropTarget;
		(dropTarget = new DropTarget(this.canvas, 19)).setTransfer(FileTransfer.getInstance());
		dropTarget.addDropListener(new DropTargetAdapter() {
			public final void dragEnter(final DropTargetEvent dropTargetEvent) {
				if (dropTargetEvent.detail == 16) {
					dropTargetEvent.detail = (((dropTargetEvent.operations & 0x1) != 0x0) ? 1 : 0);
				}
			}

			public final void dragOver(final DropTargetEvent dropTargetEvent) {
				dropTargetEvent.feedback = 9;
			}

			public final void drop(final DropTargetEvent dropTargetEvent) {
				final String[] array;
				if (FileTransfer.getInstance().isSupportedType(dropTargetEvent.currentDataType) && (array = (String[]) dropTargetEvent.data).length > 0 &&
						(array[0].toLowerCase().endsWith(".jar") || array[0].toLowerCase().endsWith(".jad") || array[0].toLowerCase().endsWith(".jam"))) {
					Emulator.loadGame(array[0], false);
				}
			}
		});
	}

	public ICaret getCaret() {
		return this.caret;
	}


	static Shell method561(final EmulatorScreen class93) {
		return class93.shell;
	}

	public static Display method564() {
		return EmulatorScreen.display;
	}

	static Canvas getCanvas(final EmulatorScreen class93) {
		return class93.canvas;
	}

	static int method566(final EmulatorScreen class93) {
		return class93.pauseState;
	}

	static long method559(final EmulatorScreen class93, final long aLong1017) {
		return class93.vibraStart = aLong1017;
	}

	static {
		EmulatorScreen.captureFileCounter = 1;
		EmulatorScreen.aString993 = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss", Locale.ENGLISH).format(Calendar.getInstance().getTime()) + "_";
	}

	public Composite getCanvas() {
		return canvas;
	}

	public Point getMenuLocation() {
		return leftSoftLabel.toDisplay(0, leftSoftLabel.getSize().y);
	}

	public void updateLanguage() {
		initMenu();
		this.pauseStateStrings = new String[]{UILocale.get("MAIN_INFO_BAR_UNLOADED", "UNLOADED"), UILocale.get("MAIN_INFO_BAR_RUNNING", "RUNNING"), UILocale.get("MAIN_INFO_BAR_PAUSED", "PAUSED")};
		updateStatus();
		this.canvas.redraw();
	}

	public void appStarted(boolean first) {
		if (first) // display is already set at this moment
			display.asyncExec(new WindowOpen(this, 0));
	}

	public boolean isShown() {
		boolean[] r = new boolean[1];
		display.syncExec(() -> r[0] = shell.isVisible() && !shell.getMinimized());
		return r[0];
	}

	public int showMidletChoice(Vector<String> midletKeys) {
		dialogSelection = -1;

		Shell shell = new Shell(SWTFrontend.getDisplay(), SWT.DIALOG_TRIM);
		shell.setSize(300, 400);
		shell.setText(UILocale.get("START_MIDLET_CHOICE_TITLE", "Choose MIDlet to run"));
		shell.setLayout(new GridLayout(1, false));

		Table table = new Table(shell, SWT.BORDER | SWT.SINGLE);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialogSelection = table.getSelectionIndex();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				if ((dialogSelection = table.getSelectionIndex()) == -1)
					return;
				shell.close();
			}
		});
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));

		for (String k : midletKeys) {
			String[] p = Emulator.getEmulator().getAppProperty(k).split(",");
			TableItem t = new TableItem(table, SWT.NONE);
			t.setText(0, p[0].trim());
			try {
				t.setImage(0, new Image(SWTFrontend.getDisplay(), ResourceManager.getResourceAsStream(p[1].trim())));
			} catch (Exception ignored) {
			}
		}

		Rectangle clientArea = this.shell.getMonitor().getClientArea();
		Point size = shell.getSize();
		shell.setLocation(clientArea.x + (clientArea.width - size.x) / 2, clientArea.y + (clientArea.height - size.y) / 2);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return dialogSelection;
	}

	public int showUpdateDialog(int type) {
		dialogSelection = 0;

		try {
			setWindowOnTop(ReflectUtil.getHandle(shell), true);
		} catch (Throwable ignored) {
		}
		MessageBox messageBox = new MessageBox(this.shell, SWT.YES | SWT.NO | SWT.CLOSE);
		messageBox.setText(UILocale.get("UPDATE_TITLE", "KEmulator Update"));
		switch (type) {
			case 0:
				messageBox.setMessage(UILocale.get("START_AUTO_UPDATE_TEXT", "Do you want to receive automatic updates?"));
				break;
			case 1:
				messageBox.setMessage(UILocale.get("UPDATE_AVAILABLE_TEXT", "A new version is available.\nDo you want to download it?"));
				break;
		}
		int r = messageBox.open();
		dialogSelection = r == SWT.YES ? 2 : r == SWT.NO ? 1 : 0;

		if (type == 1 && dialogSelection == 2) {
			Updater.startUpdater(true);
		}
		try {
			setWindowOnTop(ReflectUtil.getHandle(shell), Settings.alwaysOnTop);
		} catch (Throwable ignored) {
		}

		return dialogSelection;
	}

	public boolean showSecurityDialog(final String message) {
		final boolean[] ok = new boolean[1];
		shell.getDisplay().syncExec(() -> {
			MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO);
			messageBox.setMessage(message);
			messageBox.setText(UILocale.get("SECURITY_ALERT_TITLE", "Security"));
			ok[0] = messageBox.open() == SWT.YES;
		});
		return ok[0];
	}

	public String showIMEIDialog() {
		InputDialog imeiDialog[] = new InputDialog[1];
		shell.getDisplay().syncExec(() -> {
			imeiDialog[0] = new InputDialog(shell);
			imeiDialog[0].setMessage("Application asks for IMEI");
			imeiDialog[0].setInput("0000000000000000");
			imeiDialog[0].setText(UILocale.get("SECURITY_ALERT_TITLE", "Security"));
			imeiDialog[0].open();
		});
		return imeiDialog[0].getInput();
	}

	public boolean getTouchEnabled() {
		return touchEnabled;
	}

	final class ShellPosition implements Runnable {
		int anInt1478;
		int anInt1481;
		boolean aBoolean1479;
		private final EmulatorScreen aClass93_1480;

		ShellPosition(final EmulatorScreen aClass93_1480, final int anInt1478, final int anInt1479, final boolean aBoolean1479) {
			super();
			this.aClass93_1480 = aClass93_1480;
			this.anInt1478 = anInt1478;
			this.anInt1481 = anInt1479;
			this.aBoolean1479 = aBoolean1479;
		}

		public void run() {
			if (this.aBoolean1479) {
				this.anInt1478 = EmulatorScreen.method561(this.aClass93_1480).getLocation().x;
				this.anInt1481 = EmulatorScreen.method561(this.aClass93_1480).getLocation().y;
				return;
			}
			EmulatorScreen.method561(this.aClass93_1480).setLocation(this.anInt1478, this.anInt1481);
		}
	}

	private final class Vibrate implements Runnable {
		int anInt1193;
		boolean stop;
		Random aRandom1195;
		int anInt1197;
		int anInt1198;
		private final EmulatorScreen aClass93_1196;

		Vibrate(final EmulatorScreen aClass93_1196) {
			super();
			EmulatorScreen.method559(this.aClass93_1196 = aClass93_1196, System.currentTimeMillis());
			this.stop = false;
			this.aRandom1195 = new Random();
		}

		public void run() {
			try {
				while (!shell.isDisposed()) {
					if (stop) {
						Thread.sleep(5);
						continue;
					}
					this.anInt1193 = 10;
					final ShellPosition shellPosition = this.aClass93_1196.new ShellPosition(aClass93_1196, 0, 0, true);
					while (System.currentTimeMillis() - this.aClass93_1196.vibraStart < this.aClass93_1196.vibra && !this.stop) {
						if (EmulatorScreen.method566(this.aClass93_1196) != 2) {
							if (EmulatorScreen.this.shell.isDisposed()) return;
							display.syncExec(shellPosition);
							this.anInt1197 = shellPosition.anInt1478;
							this.anInt1198 = shellPosition.anInt1481;
							if (this.anInt1193++ > 10) {
								display.asyncExec(this.aClass93_1196.new ShellPosition(aClass93_1196, this.anInt1197 + this.aRandom1195.nextInt() % 5, this.anInt1198 + this.aRandom1195.nextInt() % 5, false));
								this.anInt1193 = 0;
							}
							display.asyncExec(this.aClass93_1196.new ShellPosition(aClass93_1196, this.anInt1197, this.anInt1198, false));
						}
						Thread.sleep(1L);
					}
					stop = true;
				}
			} catch (InterruptedException ignored) {
			}
			this.aClass93_1196.vibraThread = null;
		}
	}

	public final class WindowOpen implements Runnable {
		int target;
		private final EmulatorScreen screen;

		WindowOpen(final EmulatorScreen screen, final int target) {
			super();
			this.screen = screen;
			this.target = target;
		}

		public void run() {
			switch (this.target) {
				case 0: {
					MemoryView m = ((SWTFrontend) Emulator.getEmulator()).getMemory();
					if (Settings.showMemViewFrame && !m.isShown()) {
						this.screen.memoryViewMenuItem.setSelection(true);
						m.open();
						return;
					}
					break;
				}
				case 1: {
					if (Settings.showLogFrame) {
						this.screen.logMenuItem.setSelection(true);
						((Log) Emulator.getEmulator().getLogStream()).createWindow(EmulatorScreen.method561(this.screen));
						return;
					}
					break;
				}
				case 2: {
					if (Settings.showInfoFrame) {
						infosEnabled = true;
						this.screen.infosMenuItem.setSelection(true);
						EmulatorScreen.getCanvas(this.screen).setCursor(new Cursor(EmulatorScreen.method564(), 2));
						((SWTFrontend) Emulator.getEmulator()).getInfos().open(EmulatorScreen.method561(this.screen));
						break;
					}
					break;
				}
			}
		}
	}

	public void mouseScrolled(MouseEvent arg0) {
		if (this.pauseState == 0 || Settings.playingRecordedKeys) {
			return;
		}
		try {
			int k = 0;
			if (arg0.count < 0) {
				k = Integer.parseInt(mapKey(2));
			} else if (arg0.count > 0) {
				k = Integer.parseInt(mapKey(1));
			}
			if (k != 0) {
				Emulator.getEventQueue().keyPress(k);
				Emulator.getEventQueue().keyRelease(k);
			}
		} catch (Exception ignored) {
		}
	}

	public void handleEvent(Event event) {
		if (event.type == SWT.MouseHorizontalWheel) {
			if (this.pauseState == 0 || Settings.playingRecordedKeys) {
				return;
			}
			try {
				int k = 0;
				if (event.count < 0) {
					k = Integer.parseInt(mapKey(3));
				} else if (event.count > 0) {
					k = Integer.parseInt(mapKey(4));
				}
				if (k != 0) {
					Emulator.getEventQueue().keyPress(k);
					Emulator.getEventQueue().keyRelease(k);
				}
			} catch (Exception ignored) {
			}
		}
	}
}