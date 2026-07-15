package emulator.ui.swt;

import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import emulator.*;
import emulator.custom.ResourceManager;
import emulator.custom.CustomMethod;
import emulator.debug.Profiler;
import emulator.debug.Profiler3D;
import emulator.graphics2D.IImage;
import emulator.graphics2D.awt.ImageAWT;
import emulator.graphics2D.swt.ImageSWT;
import emulator.lcdui.LCDUIUtils;
import emulator.ui.CommandsMenuPosition;
import emulator.ui.ICaret;
import emulator.ui.IScreen;
import emulator.ui.TargetedCommand;
import emulator.ui.VibraPipe;
import emulator.ui.swt.devutils.idea.IdeaUtils;
import emulator.ui.swt.HotkeyManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Screen;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class EmulatorScreen implements
		IScreen, Runnable, PaintListener, DisposeListener,
		ControlListener, KeyListener, MouseListener,
		MouseMoveListener, SelectionListener, MouseWheelListener,
		MouseTrackListener, TouchListener, Listener {
	private static Display display;
	private static int threadCount;
	private int startWidth;
	private int startHeight;

	private Shell shell;
	private Canvas canvas;
	private Rectangle browseFavBounds;
	private Rectangle luckyJarBounds;
	/* Debug button removed
	private Rectangle debugFavBounds;
	/**/
	private float browseBtnR = 120, browseBtnG = 60, browseBtnB = 180; // Performance Scene — random color with fade
	private float browseBtnTargetR = 120, browseBtnTargetG = 60, browseBtnTargetB = 180;
	private int browseBtnCycleTimer;

	private float luckyBtnR = 60, luckyBtnG = 120, luckyBtnB = 180;
	private float luckyBtnTargetR = 60, luckyBtnTargetG = 120, luckyBtnTargetB = 180;
	private int luckyBtnCycleTimer;

	/* Debug button removed
	private float debugBtnR = 180, debugBtnG = 60, debugBtnB = 120;
	private float debugBtnTargetR = 180, debugBtnTargetG = 60, debugBtnTargetB = 120;
	/**/
	private boolean browseAnimScheduled;
	private int browseFavOpenCount; // Performance Scene — pause bg when FavoritesBrowser is open
	private boolean browseFavHeld;
	private boolean browseFavPressed;
	private boolean browseFavHovered;
	private boolean luckyFavHeld;
	private boolean luckyFavPressed;
	private boolean luckyFavHovered;
	/* Debug button removed
	private boolean debugFavHeld;
	private boolean debugFavPressed;
	private boolean debugFavHovered;
	/**/
	private int browseSelectedIdx = -1;
	private Clip browseSoundClip;
	private Object browseSoundPlayer;
	private Thread browseSoundThread;
	private String[] browseSoundFiles;
	private boolean browseSoundPlaying;
	private Random browseSoundRand = new Random();
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
	MenuItem themeMenuItem;
	MenuItem xrayViewMenuItem;
	MenuItem infosMenuItem;
	MenuItem alwaysOnTopMenuItem;
	MenuItem rotateScreenMenuItem;
	MenuItem forcePaintMenuItem;
	MenuItem keypadMenuItem;
	MenuItem openLuckyJarMenuItem;
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
	MenuItem j2meGamepadTerminateMenuItem;
	MenuItem j2meGamepadLaunchMenuItem;
	MenuItem autoSkipAppSettingsMenuItem;
	MenuItem appSettingsMenuItem;
	MenuItem deleteJarMenuItem;
	MenuItem prevJarMenuItem;
	MenuItem nextJarMenuItem;
	MenuItem prevResMenuItem;
	MenuItem nextResMenuItem;
	MenuItem resolutionRestartMenuItem;
	MenuItem disableTouchDblClickMenuItem;
	private Shell toastShell;
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

	private boolean crashed;
	private Image splashImage;
	private Rectangle splashBounds;
	private boolean splashHeld;
	private String splashHeldSentence;
	private float pulsePhase; // Performance Scene — pulsing background hue
	private static final String[] BG_SENTENCES = {
		"",
		"Fun is infinite!",
		"For free?!",
		"Don't litter cities, please",
		"You're going to have a great time!",
		"Tech is fun!",
		"Even the smallest details\ncan greatly enhance\nyour personal experience."
	};
	private String currentBgSentence = BG_SENTENCES[browseSoundRand.nextInt(BG_SENTENCES.length)];
	private boolean pulseAnimScheduled;

	// ── Infinite-scroll background icon animation for splash screen ─
	private static final int BG_ICON_SIZE = 64; // Performance Scene
	private final List<BgIconSlot> bgIconSlots = new ArrayList<>();
	private final java.util.List<File> bgJarFiles = new ArrayList<>();
	private boolean bgIconsLoaded;
	private static final java.util.Set<File> bgLoadingSet = new java.util.HashSet<>(); // Performance Scene — dedup bg icon loads
	private static final java.util.Set<String> bgNoIconJars = new java.util.HashSet<>(); // Performance Scene — jars known to have no icon
	private int bgGridCols = 1;
	private int bgGridRows = 1;
	private int bgMaxActiveIcons = 1; // Performance Scene — limit visible icons to unique jar count for sparse look
	private int bgSteadyCap = 1; // lower cap applied after first wrap transition
	private int bgActiveIconCount;
	private int bgWrapCount;
	private int bgGridPad;
	private float bgScrollY;
	private Runnable bgAnimRunnable;
	private long bgAnimLastNanos;
	private static final int BG_LEADING_PAD = 4; // extra grid rows so leading edge is off-screen before wrap
	private static final int BG_ANIM_INTERVAL_MS = 50; // Performance Scene — 20 FPS ceiling
	private static final float BG_SCROLL_SPEED_SEC = 24.0f; // Performance Scene — px/s scroll
	private static final float PULSE_SPEED_SEC = 0.1f; // Performance Scene — cycles/s
	private static final float ALPHA_FADE_SEC = 0.7f; // Performance Scene — fade duration
	private static final float BTN_FADE_SPEED = 0.08f; // Performance Scene — lerp factor per tick toward target
	private Color pulseBgColor; // Performance Scene — cached pulsing color
	private int pulseBgColorR = -1, pulseBgColorG = -1, pulseBgColorB = -1;
	private Color splashDarkBg;
	private Color splashDarkFg;
	private Color statusBarDarkBg;
	private boolean shellBgStored;
	private Color shellBgLight;


	private boolean bgDirty;
	// Performance Scene — O(1) shuffled jar-index pool (culling to BG_POOL_SIZE entries)
	private int[] bgJarOrder;
	private int bgJarOrderCursor;
	private static final int BG_POOL_SIZE = 3000;
	private String bgCustomDirPath; // Performance Scene — temp override for swapped dir
	private static final int DIR_UP = 0, DIR_DOWN = 1, DIR_RIGHT = 2, DIR_LEFT = 3;
	private static final int DIR_UP_RIGHT = 4, DIR_UP_LEFT = 5, DIR_DOWN_RIGHT = 6, DIR_DOWN_LEFT = 7;
	private int bgScrollDir = browseSoundRand.nextInt(8); // Performance Scene — random direction at startup
	private int bgAnimMode = 8; // 0-7=directions, 8=random; default matches legacy random
	private float bgScrollX;

	private static class BgIconSlot {
		Image icon;
		float x, y;
		float alpha;
		int phase;
		int phaseTimer;
		int drawAlpha;
		int drawX, drawY;
		int row;
		int col;
	}

	public EmulatorScreen() {
		this.pauseStateStrings = new String[]{UILocale.get("MAIN_INFO_BAR_UNLOADED", "UNLOADED"), UILocale.get("MAIN_INFO_BAR_RUNNING", "RUNNING"), UILocale.get("MAIN_INFO_BAR_PAUSED", "PAUSED")};
		display = SWTFrontend.getDisplay();
		display.addFilter(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if (Character.toLowerCase(event.keyCode) == 'w' && (event.stateMask & SWT.CTRL) != 0) {
					if (!fullscreen) return;
					event.doit = false;
					event.type = SWT.None;
					doDeleteJar();
				}
				if (pauseState == 0) {
					Shell[] shells = shell == null ? null : shell.getShells();
					if (shells != null) {
						for (Shell s : shells) {
							if (s.isVisible()) return;
						}
					}
					int kc = event.keyCode;
					if (kc == SWT.ARROW_UP || kc == SWT.KEYPAD_8 || kc == SWT.ARROW_DOWN || kc == SWT.KEYPAD_2 || kc == SWT.F3 || kc == SWT.KEYPAD_5) {
						handleBtnKeyDown(kc);
						event.doit = false;
					}
				}
			}
		});
		display.addFilter(SWT.KeyUp, new Listener() {
			public void handleEvent(Event event) {
				if (pauseState != 0) return;
				Shell[] shells = shell == null ? null : shell.getShells();
				if (shells != null) {
					for (Shell s : shells) {
						if (s.isVisible()) return;
					}
				}
				int kc = event.keyCode;
				if (kc == SWT.F3 || kc == SWT.KEYPAD_5) {
					if (browseSelectedIdx == 0 && browseFavHeld) handleBrowseRelease();
					else if (browseSelectedIdx == 1 && luckyFavHeld) handleLuckyRelease();
					/* Debug button removed
					else if (browseSelectedIdx == 2 && debugFavHeld) handleDebugRelease();
					/**/
					event.doit = false;
				}
			}
		});
		this.initShell();
	}

	public void initScreen(int w, int h) {
		try {
			this.initScreenBuffer(w, h);
		} catch (Throwable e) {
			Emulator.getEmulator().getLogStream().println("Failed to initialize screen buffer with size " + w + "x" + h + ", falling back to 240x320.");
			e.printStackTrace();
			this.initScreenBuffer(w = 240, h = 320);
		}
		startWidth = w; startHeight = h;
		Emulator.syncPresetIndex(w, h);
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
		Shell parentShell = ((Property) Emulator.getEmulator().getProperty()).getShell();
		if (parentShell == null || parentShell.isDisposed() || !parentShell.isVisible()) {
			parentShell = this.shell;
		}
		try {
			setWindowOnTop(ReflectUtil.getHandle(parentShell), true);
		} catch (Throwable ignored) {
		}
		final MessageBox messageBox;
		(messageBox = new MessageBox(this.shell)).setText(UILocale.get("MESSAGE_BOX_TITLE", "KEmulator Alert"));
		messageBox.setMessage(message);
		messageBox.open();
	}

	public void showMessage(String title, String detail) {
		Shell parentShell = ((Property) Emulator.getEmulator().getProperty()).getShell();
		if (parentShell == null || parentShell.isDisposed() || !parentShell.isVisible()) {
			parentShell = this.shell;
		}
		if (parentShell == null || parentShell.isDisposed()) {
			parentShell = null;
		}
		try {
			setWindowOnTop(ReflectUtil.getHandle(parentShell), true);
		} catch (Throwable ignored) {
		}

		Shell dialog = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setSize(450, 300);
		dialog.setText(UILocale.get("MESSAGE_BOX_TITLE", "KEmulator Alert"));
		dialog.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(dialog, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label label = new Label(composite, SWT.NONE);
		label.setText(title);

		Composite composite_1 = new Composite(dialog, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));

		Text text = new Text(composite_1, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		text.setText(detail);

		Composite buttonBar = new Composite(dialog, SWT.NONE);
		buttonBar.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		GridLayout buttonLayout = new GridLayout(2, true);
		buttonLayout.marginWidth = 8;
		buttonLayout.marginHeight = 4;
		buttonBar.setLayout(buttonLayout);

		Label hintLabel = new Label(buttonBar, SWT.NONE);
		hintLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		hintLabel.setText("Press F2 to close");

		Button okBtn = new Button(buttonBar, SWT.PUSH);
		okBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		okBtn.setText("OK");
		if (Settings.favoritesDarkMode) {
			applyThemeToShell(dialog, true);
		}
		okBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.close();
			}
		});

		Rectangle clientArea = dialog.getMonitor().getClientArea();
		Point size = dialog.getSize();
		dialog.setLocation(clientArea.x + (clientArea.width - size.x) / 2, clientArea.y + (clientArea.height - size.y) / 2);

		org.eclipse.swt.widgets.Listener dialogFilter = event -> {
			if (dialog == null || dialog.isDisposed() || !dialog.isVisible()) return;
			org.eclipse.swt.widgets.Control focus = display.getFocusControl();
			if (focus == null || focus.getShell() != dialog) return;
			KeyEvent ke = new KeyEvent(event);
			if (FavoritesBrowser.isConfirmKey(ke)) {
				if (focus instanceof Button) {
					((org.eclipse.swt.widgets.Button) focus).notifyListeners(SWT.Selection, new Event());
				}
				event.doit = false;
			} else if (FavoritesBrowser.matchNavKey(ke, HotkeyManager.UI_CANCEL)) {
				dialog.close();
				event.doit = false;
			}
		};
		display.addFilter(SWT.KeyUp, dialogFilter);

		okBtn.setFocus();

		dialog.open();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.removeFilter(SWT.KeyUp, dialogFilter);
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
		display.asyncExec(new WindowOpen(this, 2));

		if (midletLoaded) {
			String s = Emulator.getEmulator().getAppProperty("Nokia-UI-Enhancement");
			midletSupportsMultitouch = s != null && s.contains("EnableMultiPointTouchEvents");
		}

		if (!shell.isVisible()) {
			shell.open();
		}

		scheduleBrowseBtnAnim();

		if (Utils.win) {
			Rectangle screenArea = display.getClientArea();
			if (!screenArea.contains(shell.getLocation())) {
				EmulatorScreen.locX = clientArea.x + (clientArea.width - shell.getSize().x) >> 1;
				EmulatorScreen.locY = clientArea.y + (clientArea.height - shell.getSize().y) >> 1;
				this.shell.setLocation(EmulatorScreen.locX, EmulatorScreen.locY);
			}
		}

		shell.addDisposeListener(this);
		shell.addControlListener(this); // added only here to avoid firing multiple times while setting shell up

		// when set in try above (i.e. before open), win10 for some reason ignores it. layout() did not help.
		// probably may be fixed by event queue clear call (readAndDispatch?)
		if (maximized)
			shell.setMaximized(true);
		if (fullscreen) {
			Settings.resizeMode = ResizeMethod.Fit;
			if (Utils.win) {
				shell.setMaximized(true);
			} else {
				shell.setBounds(shell.getMonitor().getClientArea());
				shell.setFullScreen(true);
			}
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
		} catch (Throwable e) {
			crashed = true;
			String trace;
			try {
				java.io.StringWriter sw = new java.io.StringWriter();
				java.io.PrintWriter pw = new java.io.PrintWriter(sw);
				e.printStackTrace(pw);
				pw.close();
				trace = sw.toString();
			} catch (Throwable ignored) {
				trace = e.getMessage();
			}
			System.err.println("KEmulator has crashed:");
			System.err.println(trace);
			try {
				java.io.PrintWriter fw = new java.io.PrintWriter(new java.io.FileWriter(System.getProperty("java.io.tmpdir", ".") + "/kemulator_crash.log"));
				fw.println("KEmulator crash log");
				fw.println(trace);
				fw.close();
				System.err.println("Crash log at: %TEMP%\\kemulator_crash.log");
			} catch (Throwable ignored) {
			}
			Runtime.getRuntime().halt(1); // Performance Scene — kill JVM, skip SWT shutdown hooks
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
			initScreenBuffer(x, y);
			updateCanvasRect(true, false, true);
			Emulator.getEventQueue().sizeChanged(x, y);
			if (AppSettings.screenWidth != x || AppSettings.screenHeight != y) {
				AppSettings.screenWidth = x;
				AppSettings.screenHeight = y;
				AppSettings.save();
			}
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
		if (!AppSettings.asyncFlush) return getScreenImg();
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
		if (AppSettings.asyncFlush) {
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
			shell.layout();
		});
	}

	public void setRightSoftLabel(final String label) {
		if (rightSoftLabelText != null && rightSoftLabelText.equals(label))
			return;
		rightSoftLabelText = label;
		display.syncExec(() -> {
			rightSoftLabel.setText(label);
			shell.layout();
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
		markThemeable(shell);
		shell.addListener(SWT.Close, event -> CustomMethod.close());
		shell.setMinimumSize(120, 50); // windows uses 120px as hard limit for width
		shell.setLayout(layout);
		shellBgLight = shell.getBackground();
		shellBgLight = new Color(display, shellBgLight.getRed(), shellBgLight.getGreen(), shellBgLight.getBlue());
		try {
			if (f == null) {
				FontData fd = shell.getFont().getFontData()[0];
				fd.height = (fd.height / -fd.data.lfHeight) * 12;
				f = new Font(shell.getDisplay(), fd);
			}
			shell.setFont(f);
		} catch (Error ignored) {
		}
		try {
			splashImage = new Image(display, getClass().getResourceAsStream("/res/keaddon.png"));
		} catch (Exception ignored) {
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
		if (Settings.favoritesDarkMode) {
			applyStatusBarTheme(true);
			applyThemeToShell(shell, true);
		}
		initMenu();
		setFullscreen(fullscreen);
		this.shell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.shell.addShellListener(new ShellAdapter() {
			public void shellDeactivated(ShellEvent e) {
				try {
					synchronized (pressedKeys) {
						while (!pressedKeys.isEmpty()) {
							handleKeyReleaseMapped(pressedKeys.get(0).toString());
							pressedKeys.remove(0);
						}
					}
				} catch (Exception ignored) {}
			}
		});

		if (Settings.lcduiSystemColors) {
			LCDUIUtils.backgroundColor = getColor(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			LCDUIUtils.foregroundColor = getColor(display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
			LCDUIUtils.highlightedForegroundColor = getColor(display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
			LCDUIUtils.highlightedBackgroundColor = getColor(display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
			LCDUIUtils.borderColor = getColor(display.getSystemColor(SWT.COLOR_WIDGET_BORDER));
			LCDUIUtils.highlightedBorderColor = getColor(display.getSystemColor(SWT.COLOR_LIST_SELECTION));
			LCDUIUtils.gaugeColor = getColor(display.getSystemColor(SWT.COLOR_LINK_FOREGROUND));
		}
	}

	private static int getColor(Color color) {
		return (0xFF << 24) | (color.getRed() << 16) | (color.getGreen() << 8) | (color.getBlue());
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

	public void changeFullscreen() {
//		shell.setMenuBar(null);
		if (infosEnabled) {
			infosEnabled = false;
			this.canvas.setCursor(new Cursor(display, 0));
			((SWTFrontend) Emulator.getEmulator()).getInfos().dispose();
		}
		Shell tempShell = new Shell();
		canvas.setParent(tempShell);
		shell.removeDisposeListener(this);
		shell.removeControlListener(this);
		shell.dispose();
		maximized = false;
		initShell();
		start(pauseState != 0);
		tempShell.dispose();
		if (EmulatorScreen.fullscreen) {
			reloadBgIcons(); // Performance Scene — fresh bg after entering fullscreen
		}
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

		(this.xrayViewMenuItem = new MenuItem(this.menuView, 32)).setText(UILocale.get("MENU_VIEW_XRAY", "X-Ray View") + "\tCtrl+X");
		this.xrayViewMenuItem.addSelectionListener(this);

		if (!Emulator.isX64()) {
			(this.alwaysOnTopMenuItem = new MenuItem(this.menuView, 32)).setText(UILocale.get("MENU_VIEW_TOP", "Always On Top"));
			this.alwaysOnTopMenuItem.addSelectionListener(this);
			this.alwaysOnTopMenuItem.setSelection(Settings.alwaysOnTop);
		}

		(this.rotateScreenMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_ROTATE", "Rotate Screen") + "\tCtrl+Y");
		this.rotateScreenMenuItem.addSelectionListener(this);

		this.rotate90MenuItem = new MenuItem(this.menuView, 8);
		this.rotate90MenuItem.setText(UILocale.get("MENU_VIEW_ROTATE_90", "Rotate 90 Degrees") + "\tCtrl+Shift+Y");
		this.rotate90MenuItem.addSelectionListener(this);

		(this.forcePaintMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_FORCE_PAINT", "Force Paint") + "\tF5");
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

		(this.optionsMenuItem = new MenuItem(this.menuView, 8)).setText("Global Settings...");
		this.optionsMenuItem.addSelectionListener(this);

		(this.themeMenuItem = new MenuItem(this.menuView, 8)).setText(Settings.favoritesDarkMode ? "Light Theme" : "Dark Theme");
		this.themeMenuItem.addSelectionListener(this);

		new MenuItem(this.menuView, 2);

		(this.updateMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_CHECK_UPDATE", "Check for Updates"));
		this.updateMenuItem.addSelectionListener(this);

		(this.helpMenuItem = new MenuItem(this.menuView, 8)).setText(UILocale.get("MENU_VIEW_HELP", "About"));
		this.helpMenuItem.addSelectionListener(this);

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

		new MenuItem(menuResize, 2);
		(this.prevResMenuItem = new MenuItem(menuResize, 8)).setText("Previous resolution preset\tAlt+Left");
		this.prevResMenuItem.setAccelerator(SWT.ALT | SWT.ARROW_LEFT);
		this.prevResMenuItem.addSelectionListener(this);
		(this.nextResMenuItem = new MenuItem(menuResize, 8)).setText("Next resolution preset\tAlt+Right");
		this.nextResMenuItem.setAccelerator(SWT.ALT | SWT.ARROW_RIGHT);
		this.nextResMenuItem.addSelectionListener(this);

		new MenuItem(menuResize, 2);
		(this.resolutionRestartMenuItem = new MenuItem(menuResize, 32)).setText("Restart MIDlet on resolution change");
		this.resolutionRestartMenuItem.setSelection(Settings.resolutionRestartMidlet);
		this.resolutionRestartMenuItem.addSelectionListener(this);

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
		(this.captureToClipboardMenuItem = new MenuItem(this.menuTool, 8)).setText(UILocale.get("MENU_TOOL_CAPTURE_CLIP", "Capture to Clipboard") + "\tAlt+C");
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

		new MenuItem(this.menuTool, 2);
		(this.autoSkipAppSettingsMenuItem = new MenuItem(this.menuTool, 32)).setText("Auto-skip Application Settings");
		this.autoSkipAppSettingsMenuItem.setSelection(!Settings.showAppSettingsOnStart);
		this.autoSkipAppSettingsMenuItem.addSelectionListener(this);

		(this.disableTouchDblClickMenuItem = new MenuItem(this.menuTool, 32)).setText("Disable touchscreen double-click");
		this.disableTouchDblClickMenuItem.setSelection(Settings.disableTouchDoubleClick);
		this.disableTouchDblClickMenuItem.addSelectionListener(this);

		menuItemTool.setMenu(this.menuTool);

		this.menuMidlet = new Menu(menuItemMidlet);
		(this.loadJarMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_LOAD_JAR", "Load jar...") + "\tCtrl+O");
		this.loadJarMenuItem.addSelectionListener(this);
		loadJarMenuItem.setAccelerator(SWT.CONTROL | 'O');
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
		(this.prevJarMenuItem = new MenuItem(this.menuMidlet, 8)).setText("Previous JAR in folder\tCtrl+[");
		this.prevJarMenuItem.setAccelerator(SWT.CONTROL | '[');
		this.prevJarMenuItem.addSelectionListener(this);
		(this.nextJarMenuItem = new MenuItem(this.menuMidlet, 8)).setText("Next JAR in folder\tCtrl+]");
		this.nextJarMenuItem.setAccelerator(SWT.CONTROL | ']');
		this.nextJarMenuItem.addSelectionListener(this);

		new MenuItem(this.menuMidlet, 2);
		(this.deleteJarMenuItem = new MenuItem(this.menuMidlet, 8)).setText("Delete current JAR\tCtrl+W");
		this.deleteJarMenuItem.setAccelerator(SWT.CONTROL | 'W');
		this.deleteJarMenuItem.addSelectionListener(this);

		new MenuItem(this.menuMidlet, 2);
		MenuItem addToFavItem = new MenuItem(this.menuMidlet, 8);
		addToFavItem.setText("Add to Favorites\t" + HotkeyManager.ADD_TO_FAVORITES.formatKey());
		addToFavItem.setAccelerator(HotkeyManager.ADD_TO_FAVORITES.stateMask | HotkeyManager.ADD_TO_FAVORITES.keyCode);
		addToFavItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addCurrentToFavorites();
			}
		});

		MenuItem openFavItem = new MenuItem(this.menuMidlet, 8);
		openFavItem.setText("Open Favorites...\t" + HotkeyManager.OPEN_FAVORITES.formatKey());
		openFavItem.setAccelerator(HotkeyManager.OPEN_FAVORITES.stateMask | HotkeyManager.OPEN_FAVORITES.keyCode);
		openFavItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openFavoritesBrowser();
			}
		});

		new MenuItem(this.menuMidlet, 2);
		MenuItem setupLuckyItem = new MenuItem(this.menuMidlet, 8);
		setupLuckyItem.setText("Setup Lucky Folder...");
		setupLuckyItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new LuckyFolderSetupDlg().open(display);
			}
		});
		openLuckyJarMenuItem = new MenuItem(this.menuMidlet, 8);
		openLuckyJarMenuItem.setText("Open Lucky Jar");
		openLuckyJarMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				loadLuckyJar();
			}
		});

		new MenuItem(this.menuMidlet, 2);
		(this.openJadMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_JAD", "Open JAD with Notepad") + "\tCtrl+D");
		this.openJadMenuItem.addSelectionListener(this);

		this.appSettingsMenuItem = new MenuItem(this.menuMidlet, 8);
		this.appSettingsMenuItem.setText("Application Settings...\tCtrl+A");
		this.appSettingsMenuItem.setAccelerator(SWT.CONTROL | 'A');
		this.appSettingsMenuItem.addSelectionListener(this);

		new MenuItem(this.menuMidlet, 2);
		final MenuItem menuItem7;
		(menuItem7 = new MenuItem(this.menuMidlet, 64)).setText(UILocale.get("MENU_MIDLET_2D_ENGINE", "2D Engine"));
		this.menu2dEngine = new Menu(this.shell, 4194308);
		(this.awt2dMenuItem = new MenuItem(this.menu2dEngine, 16)).setText("AWT");
		this.awt2dMenuItem.setSelection(Settings.g2d == 1);
		this.awt2dMenuItem.addSelectionListener(this);
		(this.swt2dMenuItem = new MenuItem(this.menu2dEngine, 16)).setText("SWT (Deprecated)");
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
		this.j2meGamepadTerminateMenuItem = new MenuItem(this.menuMidlet, 32);
		j2meGamepadTerminateMenuItem.setText("Terminating J2ME Gamepad after exit/crash");
		j2meGamepadTerminateMenuItem.setSelection(Settings.j2meGamepadEnabled);
		j2meGamepadTerminateMenuItem.setEnabled(Emulator.isJ2MEGamepadAvailable());
		j2meGamepadTerminateMenuItem.addSelectionListener(this);
		this.j2meGamepadLaunchMenuItem = new MenuItem(this.menuMidlet, 32);
		j2meGamepadLaunchMenuItem.setText("Launch J2ME Gamepad on startup");
		j2meGamepadLaunchMenuItem.setSelection(Settings.j2meGamepadAutoLaunch);
		j2meGamepadLaunchMenuItem.setEnabled(Emulator.isJ2MEGamepadAvailable());
		j2meGamepadLaunchMenuItem.addSelectionListener(this);
		refreshGamepadMenuState();
		new MenuItem(this.menuMidlet, 2);
		(this.restartMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_RESTART", "Restart") + "\tCtrl+Shift+R");
		this.restartMenuItem.setAccelerator(SWT.CONTROL | SWT.SHIFT | 'R');
		this.restartMenuItem.addSelectionListener(this);
		(this.exitMenuItem = new MenuItem(this.menuMidlet, 8)).setText(UILocale.get("MENU_MIDLET_EXIT", "Exit")/* + "\tESC"*/);
		//this.exitMenuItem.setAccelerator(SWT.CONTROL | 27);
		this.exitMenuItem.addSelectionListener(this);
		menuItemMidlet.setMenu(this.menuMidlet);

		this.infosMenuItem.setAccelerator(SWT.CONTROL | 73);
		this.xrayViewMenuItem.setAccelerator(SWT.CONTROL | 88);
		this.rotateScreenMenuItem.setAccelerator(SWT.CONTROL | 89);
		this.rotate90MenuItem.setAccelerator(SWT.CONTROL | SWT.SHIFT | 89);
		this.forcePaintMenuItem.setAccelerator(SWT.F5);
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
			if (menuItem == this.autoSkipAppSettingsMenuItem) {
				this.autoSkipAppSettingsMenuItem.setSelection(Settings.showAppSettingsOnStart = !Settings.showAppSettingsOnStart);
				return;
			}
			if (menuItem == this.disableTouchDblClickMenuItem) {
				this.disableTouchDblClickMenuItem.setSelection(Settings.disableTouchDoubleClick = !Settings.disableTouchDoubleClick);
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
			if (menuItem == this.j2meGamepadTerminateMenuItem) {
				refreshGamepadMenuState();
				if (!j2meGamepadTerminateMenuItem.isEnabled()) return;
				Settings.j2meGamepadEnabled = !Settings.j2meGamepadEnabled;
				this.j2meGamepadTerminateMenuItem.setSelection(Settings.j2meGamepadEnabled);
				((Property) Emulator.getEmulator().getProperty()).saveProperties();
				return;
			}
			if (menuItem == this.j2meGamepadLaunchMenuItem) {
				refreshGamepadMenuState();
				if (!j2meGamepadLaunchMenuItem.isEnabled()) return;
				Settings.j2meGamepadAutoLaunch = !Settings.j2meGamepadAutoLaunch;
				this.j2meGamepadLaunchMenuItem.setSelection(Settings.j2meGamepadAutoLaunch);
				((Property) Emulator.getEmulator().getProperty()).saveProperties();
				return;
			}
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
			if (menuItem == this.deleteJarMenuItem) {
				doDeleteJar();
				return;
			}
			if (menuItem == this.prevJarMenuItem) {
				browseJarInFolder(-1);
				return;
			}
			if (menuItem == this.nextJarMenuItem) {
				browseJarInFolder(1);
				return;
			}
			if (menuItem == this.appSettingsMenuItem) {
				Emulator.getEmulator().openAppSettings(false);
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
			if (menuItem == this.themeMenuItem) {
				Settings.favoritesDarkMode = !Settings.favoritesDarkMode;
				themeMenuItem.setText(Settings.favoritesDarkMode ? "Light Theme" : "Dark Theme");
				applyStatusBarTheme(Settings.favoritesDarkMode);
				notifyThemeChanged();
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
						String javahome = System.getProperty("java.home");
						Runtime.getRuntime().exec(new String[]{
								javahome == null || javahome.length() < 1 ? "java" : (javahome + "/bin/java"),
								"-jar",
								f.getAbsolutePath()
						});
					} catch (Exception ignored) {}
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
			} else if (menuItem == this.prevResMenuItem) {
				cycleResolutionPreset(-1);
			} else if (menuItem == this.nextResMenuItem) {
				cycleResolutionPreset(1);
			} else if (menuItem == this.resolutionRestartMenuItem) {
				this.resolutionRestartMenuItem.setSelection(Settings.resolutionRestartMidlet = !Settings.resolutionRestartMidlet);
				((Property) Emulator.getEmulator().getProperty()).saveProperties();
				return;
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
			Class cls = Class.forName("org.eclipse.swt.internal.win32.OS");
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
		this.deleteJarMenuItem.setEnabled(pauseState != 0 && Emulator.midletJarPath != null);
		this.prevJarMenuItem.setEnabled(pauseState != 0 && Emulator.midletJarPath != null);
		this.nextJarMenuItem.setEnabled(pauseState != 0 && Emulator.midletJarPath != null);
		this.prevResMenuItem.setEnabled(pauseState != 0);
		this.nextResMenuItem.setEnabled(pauseState != 0);
		appSettingsMenuItem.setEnabled(pauseState != 0);
		this.watchesMenuItem.setEnabled(this.pauseState != 0);
		this.profilerMenuItem.setEnabled(this.pauseState != 0);
		this.memoryViewMenuItem.setEnabled(this.pauseState != 0);
		this.methodsMenuItem.setEnabled(this.pauseState != 0);
		m3gViewMenuItem.setEnabled(Settings.g3d == 1 && pauseState != 0);
//		fullscreenMenuItem.setEnabled(pauseState != 0);
		mediaViewMenuItem.setEnabled(pauseState != 0);
		this.updateStatus();
	}

	private void browseJarInFolder(int direction) {
		if (Emulator.midletJarPath == null) {
			showMessage("No JAR loaded");
			return;
		}
		File currentFile = new File(Emulator.midletJarPath);
		File parentDir = currentFile.getParentFile();
		if (parentDir == null || !parentDir.isDirectory()) {
			showMessage("Cannot determine JAR directory");
			return;
		}
		File[] jars = parentDir.listFiles(new java.io.FileFilter() {
			public boolean accept(File f) {
				return f.isFile() && f.getName().toLowerCase().endsWith(".jar");
			}
		});
		if (jars == null || jars.length < 2) {
			showMessage("No other JARs in this folder");
			return;
		}
		java.util.Arrays.sort(jars, new java.util.Comparator<File>() {
			public int compare(File a, File b) {
				return a.getName().compareToIgnoreCase(b.getName());
			}
		});
		int currentIdx = -1;
		String currentName = currentFile.getName();
		for (int i = 0; i < jars.length; i++) {
			if (jars[i].getName().equals(currentName)) {
				currentIdx = i;
				break;
			}
		}
		if (currentIdx == -1) {
			showMessage("Current JAR not found in file listing");
			return;
		}
		int targetIdx = (currentIdx + direction + jars.length) % jars.length;
		final String targetPath = jars[targetIdx].getAbsolutePath();
		showToast(jars[targetIdx].getName(), 1500);
		new Thread("JAR-Switch") {
			public void run() {
				try { Thread.sleep(800); } catch (InterruptedException ignored) {}
				Settings.recordedKeysFile = null;
				Emulator.loadGame(targetPath, false);
			}
		}.start();
	}

	private void showToast(final String text, final int durationMs) {
		if (toastShell != null && !toastShell.isDisposed()) toastShell.dispose();
		Display display = this.display;
		if (display == null || display.isDisposed()) return;

		toastShell = new Shell(shell, SWT.NO_TRIM | SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
		Color bg = Settings.favoritesDarkMode ? getThemeDarkBg() : display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		Color fg = Settings.favoritesDarkMode ? getThemeDarkFg() : display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
		toastShell.setBackground(bg);
		GridLayout gl = new GridLayout();
		gl.marginWidth = 15;
		gl.marginHeight = 8;
		toastShell.setLayout(gl);
		Label toastLabel = new Label(toastShell, SWT.NONE);
		toastLabel.setText(text);
		toastLabel.setBackground(bg);
		toastLabel.setForeground(fg);
		toastShell.pack();

		Point shellLoc = shell.getLocation();
		Point shellSize = shell.getSize();
		Point toastSize = toastShell.getSize();
		toastShell.setLocation(shellLoc.x + (shellSize.x - toastSize.x) / 2,
							   shellLoc.y + shellSize.y - toastSize.y - 50);
		toastShell.setAlpha(220);
		toastShell.open();
		shell.setFocus();

		display.timerExec(durationMs, new Runnable() {
			public void run() {
				if (toastShell != null && !toastShell.isDisposed()) {
					toastShell.dispose();
					toastShell = null;
				}
			}
		});
	}

	private void cycleResolutionPreset(int direction) {
		int w = getWidth(), h = getHeight();
		int idx = -1;
		for (int i = 0; i < Settings.resolutionPresets.length; i++) {
			if (Settings.resolutionPresets[i][0] == w && Settings.resolutionPresets[i][1] == h) {
				idx = i;
				break;
			}
		}
		if (idx == -1) {
			idx = Settings.currentPresetIdx;
		}
		int newIdx = (idx + direction + Settings.resolutionPresets.length) % Settings.resolutionPresets.length;
		int newW = Settings.resolutionPresets[newIdx][0];
		int newH = Settings.resolutionPresets[newIdx][1];
		Settings.currentPresetIdx = newIdx;
		showResolutionToast(Settings.resolutionPresetNames[newIdx]);
		final String resName = Settings.resolutionPresetNames[newIdx];
		display.timerExec(30, () -> showToast("Res: " + resName, 800));

		if (Settings.resolutionRestartMidlet && Emulator.midletJarPath != null) {
			// Persist resolution so new process picks it up, then restart
			AppSettings.screenWidth = newW;
			AppSettings.screenHeight = newH;
			AppSettings.save();
			Emulator.loadGame(Emulator.midletJarPath, false);
		} else {
			// No restart — resize display buffer and canvas, persist for next launch
			AppSettings.screenWidth = newW;
			AppSettings.screenHeight = newH;
			AppSettings.save();
			initScreenBuffer(newW, newH);
			updateCanvasRect(true, true, false);
		}
	}

	private void showResolutionToast(final String text) {
		final String orig = statusLabel.getText();
		statusLabel.setText(text);
		display.timerExec(1500, new Runnable() {
			public void run() {
				if (statusLabel != null && !statusLabel.isDisposed()) {
					statusLabel.setText(orig);
				}
			}
		});
	}

	private void addCurrentToFavorites() {
		if (Emulator.midletJarPath == null) {
			showMessage("No JAR is currently loaded.");
			return;
		}

		String favoritesDir = Settings.favoritesPath;
		if (favoritesDir == null || favoritesDir.isEmpty()) {
			favoritesDir = Emulator.getUserPath() + File.separator + "favorites";
			Settings.favoritesPath = favoritesDir;
		}

		new File(favoritesDir).mkdirs();

		File srcJar = new File(Emulator.midletJarPath);
		if (!srcJar.exists()) {
			showMessage("JAR file not found: " + Emulator.midletJarPath);
			return;
		}

		File destJar = new File(favoritesDir, srcJar.getName());
		if (destJar.getAbsolutePath().equalsIgnoreCase(srcJar.getAbsolutePath())) {
			showToast("Already in favorites folder", 2000);
			return;
		}

		// Store pending move — executed on next launch when file is not locked
		String entry = srcJar.getAbsolutePath() + "||" + destJar.getAbsolutePath();
		if (Settings.pendingFavoriteMoves == null || Settings.pendingFavoriteMoves.isEmpty()) {
			Settings.pendingFavoriteMoves = entry;
		} else {
			Settings.pendingFavoriteMoves = Settings.pendingFavoriteMoves + ";;" + entry;
		}
		((Property) Emulator.getEmulator().getProperty()).saveProperties();

		// Navigate to next JAR in source folder
		File parentDir = srcJar.getParentFile();
		if (parentDir != null && parentDir.isDirectory()) {
			File[] allJars = parentDir.listFiles(new java.io.FileFilter() {
				public boolean accept(File f) {
					return f.isFile() && f.getName().toLowerCase().endsWith(".jar");
				}
			});
			if (allJars != null && allJars.length > 1) {
				java.util.Arrays.sort(allJars, new java.util.Comparator<File>() {
					public int compare(File a, File b) {
						return a.getName().compareToIgnoreCase(b.getName());
					}
				});
				int currentIdx = 0;
				for (int i = 0; i < allJars.length; i++) {
					if (allJars[i].getAbsolutePath().equalsIgnoreCase(srcJar.getAbsolutePath())) {
						currentIdx = i;
						break;
					}
				}
				int nextIdx = (currentIdx + 1) % allJars.length;
				final String loadPath = allJars[nextIdx].getAbsolutePath();
				showToast("Scheduled: " + srcJar.getName() + " → favorites (on restart)", 2000);
				display.timerExec(100, new Runnable() {
					public void run() {
						Emulator.loadGame(loadPath, false);
					}
				});
				return;
			}
		}

		showToast("Scheduled: " + srcJar.getName() + " → favorites (on restart)", 2000);
	}

	private void doDeleteJar() {
		if (Emulator.midletJarPath == null) return;
		try {
			setWindowOnTop(ReflectUtil.getHandle(shell), true);
		} catch (Throwable ignored) {}
		MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
		mb.setText("Delete JAR");
		mb.setMessage("Permanently delete this JAR file?\n\n" + Emulator.midletJarPath);
		if (mb.open() != SWT.YES) return;
		try {
			synchronized (Emulator.jarFileLock) {
				if (Emulator.midletJar != null) {
					try { Emulator.midletJar.close(); } catch (IOException ignored) {}
					Emulator.midletJar = null;
				}
			}
			File f = new File(Emulator.midletJarPath);
			if (f.exists() && f.delete()) {
				for (int i = 0; i < 5; i++) {
					if (Emulator.midletJarPath.equalsIgnoreCase(Settings.recentJars[i])) {
						Settings.recentJars[i] = "";
						break;
					}
				}
				((Property) Emulator.getEmulator().getProperty()).saveProperties();
				File parentDir = f.getParentFile();
				if (parentDir != null && parentDir.isDirectory()) {
					File[] allJars = parentDir.listFiles(new java.io.FileFilter() {
						public boolean accept(File x) {
							return x.isFile() && x.getName().toLowerCase().endsWith(".jar");
						}
					});
					if (allJars != null && allJars.length > 0) {
						java.util.Arrays.sort(allJars, new java.util.Comparator<File>() {
							public int compare(File a, File b) {
								return a.getName().compareToIgnoreCase(b.getName());
							}
						});
						Emulator.loadGame(allJars[0].getAbsolutePath(), false);
						return;
					}
				}
			}
			Emulator.loadGame(null, false);
		} catch (Exception ex) {
			showMessage("Error: " + ex.getMessage());
		}
	}

	public void openFavoritesBrowser() {
		browseFavOpenCount++; // Performance Scene — pause bg animation
		new FavoritesBrowser(display, () -> {
			stopBrowseSound();
			browseFavOpenCount--;
			if (browseFavOpenCount <= 0) { // Performance Scene — resume on close
				browseFavOpenCount = 0;
				scheduleBrowseBtnAnim();
			}
		}).open(shell);
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
			SelectionListener listener = new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Object d = e.widget.getData();
					if (d instanceof TargetedCommand) {
						((TargetedCommand) d).invoke();
					} else {
						throw new IllegalStateException();
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent selectionEvent) {
				}
			};
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

	private Color splashBg() {
		if (Settings.favoritesDarkMode) {
			if (splashDarkBg == null || splashDarkBg.isDisposed()) {
				splashDarkBg = new Color(display, 45, 45, 45);
			}
			return splashDarkBg;
		}
		return display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	}

	private Color splashFg() {
		if (Settings.favoritesDarkMode) {
			if (splashDarkFg == null || splashDarkFg.isDisposed()) {
				splashDarkFg = new Color(display, 224, 224, 224);
			}
			return splashDarkFg;
		}
		return display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	}

	private static void setShellDarkMode(long handle, boolean dark) {
		try {
			NativeLibrary dwmapi = NativeLibrary.getInstance("dwmapi");
			Function func = dwmapi.getFunction("DwmSetWindowAttribute");
			IntByReference attr = new IntByReference(dark ? 1 : 0);
			try {
				func.invoke(int.class, new Object[]{handle, 20, attr, 4});
			} catch (Throwable t) {
				func.invoke(int.class, new Object[]{handle, 19, attr, 4});
			}
		} catch (Throwable ignored) {}
	}

	private void applyStatusBarTheme(boolean dark) {
		if (dark) {
			if (statusBarDarkBg == null || statusBarDarkBg.isDisposed()) {
				statusBarDarkBg = new Color(display, 18, 18, 18);
			}
			if (splashDarkFg == null || splashDarkFg.isDisposed()) {
				splashDarkFg = new Color(display, 224, 224, 224);
			}
			if (shell != null && !shell.isDisposed()) {
				shell.setBackground(statusBarDarkBg);
				setShellDarkMode(ReflectUtil.getHandle(shell), true);
			}
			if (leftSoftLabel != null && !leftSoftLabel.isDisposed()) {
				((Canvas) leftSoftLabel).setBackground(statusBarDarkBg);
				((Canvas) leftSoftLabel).setForeground(splashDarkFg);
			}
			if (statusLabel != null && !statusLabel.isDisposed()) {
				((Canvas) statusLabel).setBackground(statusBarDarkBg);
				((Canvas) statusLabel).setForeground(splashDarkFg);
			}
			if (rightSoftLabel != null && !rightSoftLabel.isDisposed()) {
				((Canvas) rightSoftLabel).setBackground(statusBarDarkBg);
				((Canvas) rightSoftLabel).setForeground(splashDarkFg);
			}
		} else {
			if (shell != null && !shell.isDisposed()) {
				if (shellBgLight != null && !shellBgLight.isDisposed()) {
					shell.setBackground(shellBgLight);
				} else {
					shell.setBackground(null);
				}
				setShellDarkMode(ReflectUtil.getHandle(shell), false);
			}
			if (leftSoftLabel != null && !leftSoftLabel.isDisposed()) {
				((Canvas) leftSoftLabel).setBackground(null);
				((Canvas) leftSoftLabel).setForeground(null);
			}
			if (statusLabel != null && !statusLabel.isDisposed()) {
				((Canvas) statusLabel).setBackground(null);
				((Canvas) statusLabel).setForeground(null);
			}
			if (rightSoftLabel != null && !rightSoftLabel.isDisposed()) {
				((Canvas) rightSoftLabel).setBackground(null);
				((Canvas) rightSoftLabel).setForeground(null);
			}
			if (statusBarDarkBg != null && !statusBarDarkBg.isDisposed()) {
				statusBarDarkBg.dispose();
				statusBarDarkBg = null;
			}
			if (splashDarkFg != null && !splashDarkFg.isDisposed()) {
				splashDarkFg.dispose();
				splashDarkFg = null;
			}
		}
	}

	public void updateSplashTheme() {
		if (display == null || display.isDisposed()) return;
		display.asyncExec(() -> {
			if (shell == null || shell.isDisposed()) return;
			applyStatusBarTheme(Settings.favoritesDarkMode);
			if (canvas != null && !canvas.isDisposed()) {
				canvas.redraw();
			}
		});
	}
	public static void notifyThemeChanged() {
		if (Emulator.getEmulator() instanceof SWTFrontend) {
			IScreen screen = Emulator.getEmulator().getScreen();
			if (screen instanceof EmulatorScreen) {
				((EmulatorScreen) screen).updateSplashTheme();
			}
		}
		for (Shell s : Display.getCurrent().getShells()) {
			if (s != null && !s.isDisposed() && s.getData("themeable") != null) {
				applyThemeToShell(s, Settings.favoritesDarkMode);
			}
		}
	}

	private static Color themeDarkBg;
	private static Color themeDarkFg;
	private static Color themeDarkBtnBg;
	private static Color themeDarkTextBg;
	private static Color themeLightBg;
	private static Color themeLightFg;

	public static Color getThemeDarkBg() {
		if (themeDarkBg == null || themeDarkBg.isDisposed()) themeDarkBg = new Color(Display.getCurrent(), 45, 45, 45);
		return themeDarkBg;
	}

	public static Color getThemeDarkFg() {
		if (themeDarkFg == null || themeDarkFg.isDisposed()) themeDarkFg = new Color(Display.getCurrent(), 224, 224, 224);
		return themeDarkFg;
	}

	public static Color getThemeDarkBtnBg() {
		if (themeDarkBtnBg == null || themeDarkBtnBg.isDisposed()) themeDarkBtnBg = new Color(Display.getCurrent(), 60, 60, 60);
		return themeDarkBtnBg;
	}

	public static Color getThemeDarkTextBg() {
		if (themeDarkTextBg == null || themeDarkTextBg.isDisposed()) themeDarkTextBg = new Color(Display.getCurrent(), 30, 30, 30);
		return themeDarkTextBg;
	}

	public static Color getThemeLightBg() {
		if (themeLightBg == null || themeLightBg.isDisposed()) themeLightBg = new Color(Display.getCurrent(), 240, 240, 240);
		return themeLightBg;
	}

	public static Color getThemeLightFg() {
		if (themeLightFg == null || themeLightFg.isDisposed()) themeLightFg = new Color(Display.getCurrent(), 0, 0, 0);
		return themeLightFg;
	}

	public static void disposeThemeColors() {
		if (themeDarkBg != null && !themeDarkBg.isDisposed()) themeDarkBg.dispose();
		if (themeDarkFg != null && !themeDarkFg.isDisposed()) themeDarkFg.dispose();
		if (themeDarkBtnBg != null && !themeDarkBtnBg.isDisposed()) themeDarkBtnBg.dispose();
		if (themeDarkTextBg != null && !themeDarkTextBg.isDisposed()) themeDarkTextBg.dispose();
		if (themeLightBg != null && !themeLightBg.isDisposed()) themeLightBg.dispose();
		if (themeLightFg != null && !themeLightFg.isDisposed()) themeLightFg.dispose();
	}

	public static void applyThemeToShell(Shell shell, boolean dark) {
		if (shell == null || shell.isDisposed()) return;
		applyThemeToControl(shell, dark);
	}

	private static void applyThemeToControl(org.eclipse.swt.widgets.Control c, boolean dark) {
		if (c == null || c.isDisposed()) return;
		if (c instanceof Shell) {
			Shell s = (Shell) c;
			if (dark) {
				s.setBackground(getThemeDarkBg());
				try { setShellDarkMode(ReflectUtil.getHandle(s), true); } catch (Throwable ignored) {}
			} else {
				s.setBackground(null);
				try { setShellDarkMode(ReflectUtil.getHandle(s), false); } catch (Throwable ignored) {}
			}
		}
		if (dark) {
			if (c instanceof Table || c instanceof Tree) {
				c.setBackground(getThemeDarkTextBg());
				c.setForeground(getThemeDarkFg());
			} else if (c instanceof Text || c instanceof org.eclipse.swt.custom.StyledText) {
				c.setBackground(getThemeDarkTextBg());
				c.setForeground(getThemeDarkFg());
				setControlWinTheme(c, true);
			} else if (c instanceof Combo || c instanceof Spinner) {
				c.setBackground(getThemeDarkTextBg());
				c.setForeground(getThemeDarkFg());
			} else if (c instanceof Button) {
				Button btn = (Button) c;
				if ((btn.getStyle() & SWT.CHECK) != 0 || (btn.getStyle() & SWT.RADIO) != 0) {
					c.setBackground(getThemeDarkBg());
					c.setForeground(getThemeDarkFg());
				} else {
					c.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
					c.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
				}
				setControlWinTheme(c, true);
			} else if (c instanceof Label || c instanceof CLabel || c instanceof Group) {
				c.setBackground(getThemeDarkBg());
				c.setForeground(getThemeDarkFg());
				setControlWinTheme(c, true);
			} else if (c instanceof org.eclipse.swt.widgets.TabFolder) {
				c.setBackground(getThemeDarkBg());
				setControlWinTheme(c, true);
			} else if (c instanceof org.eclipse.swt.custom.CTabFolder) {
				c.setBackground(getThemeDarkBg());
				c.setForeground(getThemeDarkFg());
			} else if (c instanceof Composite || c instanceof Canvas) {
				c.setBackground(getThemeDarkBg());
			} else if (c instanceof ProgressBar || c instanceof Scale) {
				c.setBackground(getThemeDarkBg());
				c.setForeground(getThemeDarkFg());
			} else if (c instanceof org.eclipse.swt.custom.SashForm) {
				c.setBackground(getThemeDarkBg());
			} else if (c instanceof Link) {
				c.setBackground(getThemeDarkBg());
				c.setForeground(getThemeDarkFg());
			} else {
				try { c.setBackground(getThemeDarkBg()); c.setForeground(getThemeDarkFg()); } catch (Exception ignored) {}
			}
		} else {
			if (c instanceof Table) {
				c.setBackground(null);
				c.setForeground(null);
			} else if (c instanceof Tree) {
				c.setBackground(null);
				c.setForeground(null);
			} else if (c instanceof Text || c instanceof org.eclipse.swt.custom.StyledText) {
				c.setBackground(null);
				c.setForeground(null);
				setControlWinTheme(c, false);
			} else if (c instanceof Combo || c instanceof Spinner) {
				c.setBackground(null);
				c.setForeground(null);
			} else if (c instanceof Button) {
				c.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				c.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
				setControlWinTheme(c, false);
			} else if (c instanceof Label || c instanceof CLabel || c instanceof Group) {
				c.setBackground(null);
				c.setForeground(null);
				setControlWinTheme(c, false);
			} else if (c instanceof org.eclipse.swt.widgets.TabFolder) {
				c.setBackground(null);
				setControlWinTheme(c, false);
			} else if (c instanceof org.eclipse.swt.custom.CTabFolder) {
				c.setBackground(null);
				c.setForeground(null);
			} else if (c instanceof Composite || c instanceof Canvas) {
				c.setBackground(null);
			} else if (c instanceof ProgressBar || c instanceof Scale || c instanceof org.eclipse.swt.custom.SashForm || c instanceof Link) {
				c.setBackground(null);
				c.setForeground(null);
			} else {
				try { c.setBackground(null); c.setForeground(null); } catch (Exception ignored) {}
			}
		}
		if (c instanceof Composite) {
			for (org.eclipse.swt.widgets.Control child : ((Composite) c).getChildren()) {
				applyThemeToControl(child, dark);
			}
		}
	}

	public static void markThemeable(Shell shell) {
		if (shell != null && !shell.isDisposed()) {
			shell.setData("themeable", Boolean.TRUE);
		}
	}

	private static void setControlWinTheme(org.eclipse.swt.widgets.Control c, boolean dark) {
		try {
			NativeLibrary uxtheme = NativeLibrary.getInstance("uxtheme");
			Function func = uxtheme.getFunction("SetWindowTheme");
			func.invoke(int.class, new Object[]{c.handle, dark ? "" : null, dark ? "" : null});
		} catch (Throwable ignored) {}
	}

	private void paintPauseScreen(GC gc, Rectangle size) {
		gc.setAlpha(255);
		boolean fullscreenSplash = EmulatorScreen.fullscreen || (shell != null && !shell.isDisposed() && shell.getFullScreen()); // Performance Scene
		if (fullscreenSplash) {
			if (splashHeld) { splashHeld = false; splashHeldSentence = null; }
			float t = (float)(1d - Math.cos(pulsePhase * Math.PI * 2d)) * 0.5f;
			int rr = lerp(0x1a, 0x84, t);
			int rg = lerp(0x00, 0x12, t);
			int rb = lerp(0x6d, 0x92, t);
			if (pulseBgColor == null || pulseBgColor.isDisposed() || rr != pulseBgColorR || rg != pulseBgColorG || rb != pulseBgColorB) {
				if (pulseBgColor != null && !pulseBgColor.isDisposed()) pulseBgColor.dispose();
				pulseBgColor = new Color(display, rr, rg, rb); // Performance Scene — cached pulse
				pulseBgColorR = rr; pulseBgColorG = rg; pulseBgColorB = rb;
			}
		gc.setBackground(pulseBgColor);
		gc.fillRectangle(0, 0, size.width, size.height);
		if (!bgIconSlots.isEmpty()) { // Performance Scene — paint icons on offscreen buffer
			paintBgIcons(gc, rr, rg, rb);
			gc.setAdvanced(false);
			gc.setTextAntialias(SWT.DEFAULT);
			gc.setAntialias(SWT.DEFAULT);
		}
	} else {
		gc.setBackground(splashBg());
		gc.fillRectangle(0, 0, size.width, size.height);
			if (splashImage != null && !splashImage.isDisposed()) {
				Rectangle ib = splashImage.getBounds();
				double phase = pulsePhase * Math.PI * 2d;
				float bobY = (float)(Math.sin(phase) * 5d);
				float scalePulse = (float)(Math.sin(phase + Math.PI) * 1.5d);
				float baseSw = ib.width * 3f / 8f;
				float baseSh = ib.height * 3f / 8f;
				int sw = Math.round(baseSw + scalePulse);
				int sh = Math.round(baseSh + scalePulse);
				int sx = (size.width >> 1) - (sw >> 1);
				int sy = Math.max(8, (size.height - sh - 100) >> 1) + Math.round(bobY);
				splashBounds = new Rectangle(sx, sy, sw, sh);
				if (splashHeld) {
					gc.setForeground(splashFg());
					gc.setFont(f);
					if (splashHeldSentence == null) {
						splashHeldSentence = BG_SENTENCES[browseSoundRand.nextInt(BG_SENTENCES.length)];
						currentBgSentence = splashHeldSentence;
					}
					String[] lines = splashHeldSentence.split("\n", -1);
					int lh = gc.getFontMetrics().getHeight();
					int totalH = lines.length * lh;
					int textY = sy + ((sh - totalH) >> 1);
					for (int i = 0; i < lines.length; i++) {
						Point lext = gc.stringExtent(lines[i]);
						int lx = sx + ((sw - lext.x) >> 1);
						gc.drawText(lines[i], Math.max(sx, lx), textY + i * lh, true);
					}
				} else {
					gc.drawImage(splashImage, 0, 0, ib.width, ib.height, sx, sy, sw, sh);
				}
			}
		}

		int centerX = size.width >> 1;
		if (fullscreenSplash && splashImage != null && !splashImage.isDisposed()) {
			Rectangle ib = splashImage.getBounds();
			int sx = centerX - (ib.width >> 1);
			int sy = Math.max(8, (size.height - ib.height - 100) >> 1);
			gc.drawImage(splashImage, sx, sy);
		}

		if (fullscreenSplash) {
			gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		} else {
			gc.setForeground(splashFg());
		}
		gc.setFont(f);
		int textY, y2;
		if (fullscreenSplash) {
			String msg = currentBgSentence;
			if (msg != null && !msg.isEmpty()) {
				String[] lines = msg.split("\n", -1);
				int lh = gc.getFontMetrics().getHeight();
				int totalH = lines.length * lh;
				textY = splashImage != null && !splashImage.isDisposed()
					? (size.height + 60) >> 1
					: (size.height - totalH) >> 1;
				for (int i = 0; i < lines.length; i++) {
					Point lext = gc.stringExtent(lines[i]);
					int lx = Math.max(4, centerX - (lext.x >> 1));
					drawTextWithShadow(gc, lines[i], lx, textY + i * lh, true);
				}
				y2 = textY + totalH + 4;
			} else {
				y2 = (size.height + 60) >> 1;
			}
		} else {
			String s = Emulator.getInfoString();
			textY = (size.height - gc.stringExtent(s).y * 2) >> 1;
			drawTextWithShadow(gc, s, Math.max(4, centerX - (gc.stringExtent(s).x >> 1)), textY, false);
			s = "Drop a J2ME application here";
			y2 = textY + gc.getFontMetrics().getHeight() + 4;
			drawTextWithShadow(gc, s, Math.max(4, centerX - (gc.stringExtent(s).x >> 1)), y2, false);
		}
		int btnY = y2 + gc.getFontMetrics().getHeight() + 12;
		paintBrowseButton(gc, size.width, btnY);
		paintLuckyJarButton(gc, size.width, browseFavBounds.y + browseFavBounds.height + 8);
		/* Debug button removed
		paintDebugButton(gc, size.width, luckyJarBounds.y + luckyJarBounds.height + 8);
		/**/
	}

	private void drawTextWithShadow(GC gc, String text, int x, int y, boolean fullscreen) {
		if (fullscreen) {
			gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc.drawText(text, x + 1, y + 1, true);
			gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		}
		gc.drawText(text, x, y, true);
	}

	private void paintBrowseButton(GC gc, int winW, int y) { // Performance Scene — random color with fade
		String s = "Browse favorites";
		gc.setFont(f);
		Point ext = gc.stringExtent(s);
		int btnW = ext.x + 20;
		int btnH = ext.y + 10;
		int bx = (winW - btnW) >> 1;
		float mult = browseFavPressed ? 0.75f : (browseFavHovered || browseSelectedIdx == 0 ? 1.15f : 1.0f);
		Color btnBg = new Color(display,
			Math.min(255, Math.round(browseBtnR * mult)),
			Math.min(255, Math.round(browseBtnG * mult)),
			Math.min(255, Math.round(browseBtnB * mult)));
		gc.setBackground(btnBg);
		int tx = browseFavPressed ? bx + 11 : bx + 10;
		int ty = browseFavPressed ? y + 6 : y + 5;
		gc.fillRoundRectangle(bx, y, btnW, btnH, 6, 6);
		btnBg.dispose();
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawText(s, tx + 1, ty + 1, true);
		gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.drawText(s, tx, ty, true);
		browseFavBounds = new Rectangle(bx, y, btnW, btnH);
	}

	private void paintLuckyJarButton(GC gc, int winW, int y) {
		String s = "Lucky Jar";
		gc.setFont(f);
		Point ext = gc.stringExtent(s);
		int btnW = ext.x + 20;
		int btnH = ext.y + 10;
		int bx = (winW - btnW) >> 1;
		float mult = luckyFavPressed ? 0.75f : (luckyFavHovered || browseSelectedIdx == 1 ? 1.15f : 1.0f);
		Color btnBg = new Color(display,
			Math.min(255, Math.round(luckyBtnR * mult)),
			Math.min(255, Math.round(luckyBtnG * mult)),
			Math.min(255, Math.round(luckyBtnB * mult)));
		gc.setBackground(btnBg);
		int tx = luckyFavPressed ? bx + 11 : bx + 10;
		int ty = luckyFavPressed ? y + 6 : y + 5;
		gc.fillRoundRectangle(bx, y, btnW, btnH, 6, 6);
		btnBg.dispose();
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawText(s, tx + 1, ty + 1, true);
		gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.drawText(s, tx, ty, true);
		luckyJarBounds = new Rectangle(bx, y, btnW, btnH);
	}

	/* Debug button removed
	private void paintDebugButton(GC gc, int winW, int y) {
		String s = "Debug";
		Point ext = gc.stringExtent(s);
		int btnW = ext.x + 20;
		int btnH = ext.y + 10;
		int bx = (winW - btnW) >> 1;
		float mult = debugFavPressed ? 0.75f : (debugFavHovered || browseSelectedIdx == 2 ? 1.15f : 1.0f);
		Color btnBg = new Color(display,
			Math.min(255, Math.round(debugBtnR * mult)),
			Math.min(255, Math.round(debugBtnG * mult)),
			Math.min(255, Math.round(debugBtnB * mult)));
		gc.setBackground(btnBg);
		int tx = debugFavPressed ? bx + 11 : bx + 10;
		int ty = debugFavPressed ? y + 6 : y + 5;
		gc.fillRoundRectangle(bx, y, btnW, btnH, 6, 6);
		btnBg.dispose();
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawText(s, tx + 1, ty + 1, true);
		gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.drawText(s, tx, ty, true);
		debugFavBounds = new Rectangle(bx, y, btnW, btnH);
	}
	/**/

	public void forceCloseCommandsList() {
		display.syncExec(() -> commandsMenu.setVisible(false));
	}

	public void paintControl(final PaintEvent paintEvent) {
		final GC gc = paintEvent.gc;
		Rectangle size = canvas.getClientArea();

		if (this.pauseState == 0) {
			paintPauseScreen(gc, size);
			return;
		}

		browseAnimScheduled = false;

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

	private void handleBtnKeyDown(int kc) {
		if (kc == SWT.ARROW_UP || kc == SWT.KEYPAD_8) {
			if (browseSelectedIdx > 0) {
				resetBrowseBtn();
				resetLuckyBtn();
				browseSelectedIdx--;
			}
			return;
		}
		if (kc == SWT.ARROW_DOWN || kc == SWT.KEYPAD_2) {
			if (browseSelectedIdx < 1) {
				resetBrowseBtn();
				resetLuckyBtn();
				browseSelectedIdx++;
			}
			return;
		}
		if (kc == SWT.F3 || kc == SWT.KEYPAD_5) {
			if (browseSelectedIdx == 0) handleBrowsePress();
			else if (browseSelectedIdx == 1) handleLuckyPress();
			/* Debug button removed
			else if (browseSelectedIdx == 2) handleDebugPress();
			/**/
		}
	}

	private void handleBrowsePress() {
		if (!browseFavHeld) {
			browseFavHeld = true;
			browseFavPressed = true;
			display.timerExec(150, new Runnable() {
				public void run() {
					if (!browseFavHeld) return;
					startBrowseSound();
				}
			});
		}
	}

	/* Debug button removed
	private void handleDebugPress() {
		if (!debugFavHeld) {
			debugFavHeld = true;
			debugFavPressed = true;
		}
	}
	/**/

	private void handleBrowseRelease() {
		browseFavHeld = false;
		browseFavPressed = false;
		stopBrowseSound();
		openFavoritesBrowser();
	}

	/* Debug button removed
	private void handleDebugRelease() {
		debugFavHeld = false;
		debugFavPressed = false;
		((Log) Emulator.getEmulator().getLogStream()).createWindow(this.shell);
	}
	/**/

	private void handleLuckyPress() {
		if (!luckyFavHeld) {
			luckyFavHeld = true;
			luckyFavPressed = true;
		}
	}

	private void handleLuckyRelease() {
		luckyFavHeld = false;
		luckyFavPressed = false;
		loadLuckyJar();
	}

	private void loadLuckyJar() {
		java.io.File jar = LuckyFolderManager.pickRandomJar();
		if (jar != null) {
			String name = jar.getName().replace(".jar", "");
			showToast("Loading: " + name, 800);
			final String path = jar.getAbsolutePath();
			new Thread("LuckyJar") {
				public void run() {
					emulator.Emulator.loadGame(path, false);
				}
			}.start();
		}
	}

	private void resetBrowseBtn() {
		browseFavHeld = false;
		browseFavPressed = false;
		stopBrowseSound();
	}

	/* Debug button removed
	private void resetDebugBtn() {
		debugFavHeld = false;
		debugFavPressed = false;
	}
	/**/

	private void resetLuckyBtn() {
		luckyFavHeld = false;
		luckyFavPressed = false;
	}

	private boolean handleBrowseMouseDown(MouseEvent e) {
		if (browseFavBounds != null && browseFavBounds.contains(e.x, e.y) && e.button == 1) {
			browseFavHeld = true;
			browseFavPressed = true;
			display.timerExec(150, new Runnable() {
				public void run() {
					if (!browseFavHeld) return;
					startBrowseSound();
				}
			});
			return true;
		}
		return false;
	}

	/* Debug button removed
	private boolean handleDebugMouseDown(MouseEvent e) {
		if (debugFavBounds != null && debugFavBounds.contains(e.x, e.y) && e.button == 1) {
			debugFavHeld = true;
			debugFavPressed = true;
			return true;
		}
		return false;
	}
	/**/

	private boolean handleBrowseMouseUp(MouseEvent e) {
		if (browseFavHeld && browseFavBounds != null && browseFavBounds.contains(e.x, e.y)) {
			openFavoritesBrowser();
			browseFavHeld = false;
			browseFavPressed = false;
			return true;
		}
		return false;
	}

	/* Debug button removed
	private boolean handleDebugMouseUp(MouseEvent e) {
		if (debugFavHeld && debugFavBounds != null && debugFavBounds.contains(e.x, e.y)) {
			((Log) Emulator.getEmulator().getLogStream()).createWindow(this.shell);
			debugFavHeld = false;
			debugFavPressed = false;
			resetBrowseBtn();
			resetDebugBtn();
			return true;
		}
		return false;
	}
	/**/

	private void handleBrowseMouseMove(MouseEvent e) {
		boolean hoverInside = browseFavBounds != null && browseFavBounds.contains(e.x, e.y);
		if (hoverInside != browseFavHovered) {
			browseFavHovered = hoverInside;
		}
		if (browseFavHeld) {
			if (hoverInside != browseFavPressed) {
				browseFavPressed = hoverInside;
				if (!hoverInside) {
					browseFavHeld = false;
					stopBrowseSound();
				}
			}
		}
	}

	/* Debug button removed
	private void handleDebugMouseMove(MouseEvent e) {
		boolean hoverInside = debugFavBounds != null && debugFavBounds.contains(e.x, e.y);
		if (hoverInside != debugFavHovered) {
			debugFavHovered = hoverInside;
		}
		if (debugFavHeld && hoverInside != debugFavPressed) {
			debugFavPressed = hoverInside;
		}
	}
	/**/

	private void clearBrowseHover() {
		if (browseFavHeld) {
			browseFavHeld = false;
			browseFavPressed = false;
			stopBrowseSound();
		}
		browseFavHovered = false;
	}

	/* Debug button removed
	private void clearDebugHover() {
		debugFavHovered = false;
	}
	/**/

	private boolean handleLuckyMouseDown(MouseEvent e) {
		if (luckyJarBounds != null && luckyJarBounds.contains(e.x, e.y) && e.button == 1) {
			luckyFavHeld = true;
			luckyFavPressed = true;
			return true;
		}
		return false;
	}

	private boolean handleLuckyMouseUp(MouseEvent e) {
		if (luckyFavHeld && luckyJarBounds != null && luckyJarBounds.contains(e.x, e.y)) {
			loadLuckyJar();
			luckyFavHeld = false;
			luckyFavPressed = false;
			resetBrowseBtn();
			resetLuckyBtn();
			return true;
		}
		return false;
	}

	private void handleLuckyMouseMove(MouseEvent e) {
		boolean hoverInside = luckyJarBounds != null && luckyJarBounds.contains(e.x, e.y);
		if (hoverInside != luckyFavHovered) {
			luckyFavHovered = hoverInside;
		}
		if (luckyFavHeld && hoverInside != luckyFavPressed) {
			luckyFavPressed = hoverInside;
		}
	}

	private void clearLuckyHover() {
		luckyFavHovered = false;
	}

	private void advanceBtnHues(float dt) {
		advanceSingleBtnState(true);
		advanceSingleLuckyBtnState();
		/* Debug button removed
		advanceSingleBtnState(false);
		/**/
	}

	private void advanceSingleBtnState(boolean isBrowse) {
		float speed = BTN_FADE_SPEED;
		boolean pressed = isBrowse && browseFavPressed;
		if (pressed) speed = BTN_FADE_SPEED * 2.5f;

		float r = browseBtnR;
		float g = browseBtnG;
		float b = browseBtnB;
		float tr = browseBtnTargetR;
		float tg = browseBtnTargetG;
		float tb = browseBtnTargetB;

		r += (tr - r) * speed;
		g += (tg - g) * speed;
		b += (tb - b) * speed;

		boolean nearTarget = Math.abs(r - tr) < 0.5f && Math.abs(g - tg) < 0.5f && Math.abs(b - tb) < 0.5f;

		if (pressed) {
			browseBtnCycleTimer++;
			if (browseBtnCycleTimer >= 12 || nearTarget) {
				browseBtnCycleTimer = 0;
				r = tr; g = tg; b = tb;
				tr = 60 + browseSoundRand.nextInt(156);
				tg = 60 + browseSoundRand.nextInt(156);
				tb = 60 + browseSoundRand.nextInt(156);
			}
		} else if (nearTarget) {
			r = tr; g = tg; b = tb;
			do {
				tr = 60 + browseSoundRand.nextInt(156);
				tg = 60 + browseSoundRand.nextInt(156);
				tb = 60 + browseSoundRand.nextInt(156);
			} while (Math.abs(tr - r) < 20 && Math.abs(tg - g) < 20 && Math.abs(tb - b) < 20);
		}

		browseBtnR = r; browseBtnG = g; browseBtnB = b;
		browseBtnTargetR = tr; browseBtnTargetG = tg; browseBtnTargetB = tb;
	}

	private void advanceSingleLuckyBtnState() {
		float speed = BTN_FADE_SPEED;

		float r = luckyBtnR;
		float g = luckyBtnG;
		float b = luckyBtnB;
		float tr = luckyBtnTargetR;
		float tg = luckyBtnTargetG;
		float tb = luckyBtnTargetB;

		r += (tr - r) * speed;
		g += (tg - g) * speed;
		b += (tb - b) * speed;

		boolean nearTarget = Math.abs(r - tr) < 0.5f && Math.abs(g - tg) < 0.5f && Math.abs(b - tb) < 0.5f;

		if (nearTarget) {
			r = tr; g = tg; b = tb;
			do {
				tr = 60 + browseSoundRand.nextInt(156);
				tg = 60 + browseSoundRand.nextInt(156);
				tb = 60 + browseSoundRand.nextInt(156);
			} while (Math.abs(tr - r) < 20 && Math.abs(tg - g) < 20 && Math.abs(tb - b) < 20);
		}

		luckyBtnR = r; luckyBtnG = g; luckyBtnB = b;
		luckyBtnTargetR = tr; luckyBtnTargetG = tg; luckyBtnTargetB = tb;
	}

	private static int lerp(int a, int b, float t) {
		return Math.round(a + (b - a) * t);
	}

	// ── Background icon animation ────────────────────────────────

	private void loadBgIcons() {
		if (bgIconsLoaded) return;
		bgIconsLoaded = true;
		String dirPath = bgCustomDirPath != null ? bgCustomDirPath : Settings.favoritesPath;
		bgCustomDirPath = null;
		if (dirPath == null || dirPath.isEmpty()) {
			dirPath = Emulator.getUserPath() + File.separator + "favorites";
		}
		File dir = new File(dirPath);
		if (!dir.exists() || !dir.isDirectory()) {
			System.err.println("[BgIcons] favorites dir not found: " + dirPath);
			return;
		}

		File[] jars = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".jar"));
		if (jars == null) {
			System.err.println("[BgIcons] failed to list jars in: " + dirPath);
			return;
		}

		for (File jar : jars) {
			bgJarFiles.add(jar);
		}
		bgJarOrder = null;
		bgJarOrderCursor = 0;
		System.err.println("[BgIcons] found " + bgJarFiles.size() + " jar(s) in " + dirPath);
	}

	private void loadBgIconFromJar(final File jar) {
		if (bgIconTempFile(jar).exists() || bgNoIconMarker(jar).exists()) return;
		synchronized (bgLoadingSet) {
			if (bgLoadingSet.contains(jar)) return;
			if (bgLoadingSet.size() >= 8) return; // Performance Scene — cap concurrent extractions
			bgLoadingSet.add(jar);
		}
		final String jarName = jar.getName();
		System.err.println("[BgIcons] loading: " + jarName);
		new Thread("BgIconLoader-" + jarName) {
			public void run() {
				try {
					final byte[] iconBytes = readIconBytesFromJar(jar);
					if (iconBytes == null) {
						System.err.println("[BgIcons] no icon in: " + jarName);
						try { bgNoIconMarker(jar).createNewFile(); } catch (Exception e2) {}
						synchronized (bgLoadingSet) { bgLoadingSet.remove(jar); }
						return;
					}
					try {
						java.nio.file.Files.write(bgIconTempFile(jar).toPath(), iconBytes);
						System.err.println("[BgIcons] extracted: " + jarName);
					} catch (Exception e) {
						System.err.println("[BgIcons] failed to write temp file for: " + jarName + " — " + e);
					} finally {
						synchronized (bgLoadingSet) { bgLoadingSet.remove(jar); }
					}
				} catch (Exception e) {
					System.err.println("[BgIcons] error loading: " + jarName + " — " + e);
					synchronized (bgLoadingSet) { bgLoadingSet.remove(jar); }
				}
			}
		}.start();
	}

	private byte[] readIconBytesFromJar(File jar) { // Performance Scene — background thread
		try (JarFile jf = new JarFile(jar, false)) {
			Manifest mf = jf.getManifest();
			if (mf == null) return null;
			Attributes a = mf.getMainAttributes();
			String iconPath = a.getValue("MIDlet-Icon");
			if (iconPath == null) {
				String midlet1 = a.getValue("MIDlet-1");
				if (midlet1 != null) {
					String[] parts = midlet1.split(",");
					if (parts.length > 1) {
						iconPath = parts[1].trim();
					}
				}
			}
			if (iconPath == null || iconPath.isEmpty()) return null;
			while (iconPath.startsWith("/")) iconPath = iconPath.substring(1);
			JarEntry entry = jf.getJarEntry(iconPath);
			if (entry == null) {
				java.util.Enumeration<JarEntry> e = jf.entries();
				while (e.hasMoreElements()) {
					JarEntry je = e.nextElement();
					if (je.getName().equalsIgnoreCase(iconPath)) {
						entry = je;
						break;
					}
				}
			}
			if (entry == null) return null;
			try (InputStream is = jf.getInputStream(entry)) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buf = new byte[4096];
				int len;
				while ((len = is.read(buf)) > 0) bos.write(buf, 0, len);
				return bos.toByteArray();
			}
		} catch (Exception e) {
			return null;
		}
	}

	private void disposeBgSlotIcons() {
		for (BgIconSlot slot : bgIconSlots) {
			if (slot.icon != null && !slot.icon.isDisposed()) slot.icon.dispose();
			slot.icon = null;
		}
	}

	private void rebuildBgGrid() {
		if (shell == null || shell.isDisposed()) return;
		Rectangle ca = canvas.getClientArea();
		int w = ca.width, h = ca.height;
		if (w <= 0 || h <= 0) return;
		boolean hasY = bgScrollDir == DIR_UP || bgScrollDir == DIR_DOWN || bgScrollDir >= 4;
		boolean hasX = bgScrollDir == DIR_LEFT || bgScrollDir == DIR_RIGHT || bgScrollDir >= 4;
		int baseCols = Math.max(1, w / (BG_ICON_SIZE + 4)) + (hasX ? 3 : 2);
		int baseRows = Math.max(3, h / BG_ICON_SIZE + 5) + (hasY ? 3 : 2);
		int newCols = baseCols + (hasX ? BG_LEADING_PAD : 0);
		int newRows = baseRows + (hasY ? BG_LEADING_PAD : 0);
		if (newCols == bgGridCols && newRows == bgGridRows && !bgIconSlots.isEmpty()) {
			bgGridPad = Math.max(0, (w - baseCols * BG_ICON_SIZE) / (baseCols + 1));
			return;
		}
		disposeBgSlotIcons();
		bgGridCols = newCols;
		bgGridRows = newRows;
		int totalSlots = bgGridCols * bgGridRows;
		int jarCount = bgJarFiles.size();
		int cap = jarCount > 0 ? Math.min(jarCount, Math.max(10, totalSlots * 30 / 100)) : 1;
		bgSteadyCap = cap;
		bgMaxActiveIcons = cap;
		bgActiveIconCount = 0;
		bgGridPad = Math.max(0, (w - baseCols * BG_ICON_SIZE) / (baseCols + 1));
		bgScrollX = 0;
		bgScrollY = 0;
		bgIconSlots.clear();
		int step0 = BG_ICON_SIZE + bgGridPad;
		for (int i = 0; i < totalSlots; i++) {
			BgIconSlot slot = new BgIconSlot();
			int col = i % bgGridCols;
			int r = i / bgGridCols;
			slot.col = col + bgPosDc();
			slot.row = r + bgPosDr();
			slot.phase = 0;
			slot.phaseTimer = 10 + browseSoundRand.nextInt(60);
			slot.x = bgGridPad + slot.col * step0;
			slot.drawX = Math.round(slot.x);
			slot.y = bgGridPad + slot.row * step0;
			slot.drawY = Math.round(slot.y);
			bgIconSlots.add(slot);
		}
	}

	private int bgPosDr() {
		if (bgScrollDir == DIR_DOWN || bgScrollDir == DIR_DOWN_LEFT || bgScrollDir == DIR_DOWN_RIGHT) return -BG_LEADING_PAD;
		return 0;
	}

	private int bgPosDc() {
		if (bgScrollDir == DIR_RIGHT || bgScrollDir == DIR_UP_RIGHT || bgScrollDir == DIR_DOWN_RIGHT) return -BG_LEADING_PAD;
		return 0;
	}

	private static File bgIconTempDir() {
		File dir = new File(System.getProperty("java.io.tmpdir"), "keaddon-bg-icons");
		if (!dir.isDirectory()) dir.mkdirs();
		return dir;
	}
	private static String bgIconHash(File jar) {
		return Integer.toHexString(jar.getAbsolutePath().hashCode());
	}
	private static File bgIconTempFile(File jar) {
		return new File(bgIconTempDir(), bgIconHash(jar) + ".png");
	}
	private static File bgNoIconMarker(File jar) {
		return new File(bgIconTempDir(), bgIconHash(jar) + ".noicon");
	}
	private Image loadBgIconImage(File jar) {
		File f = bgIconTempFile(jar);
		if (!f.exists() || f.length() == 0) return null;
		try {
			ImageData data = new ImageData(f.getAbsolutePath());
			data = data.scaledTo(BG_ICON_SIZE, BG_ICON_SIZE);
			return new Image(display, data);
		} catch (Exception e) {
			return null;
		}
	}
	private void disposeBgIconSlot(BgIconSlot slot) {
		if (slot.icon != null && !slot.icon.isDisposed()) slot.icon.dispose();
		slot.icon = null;
		if (slot.phase >= 1 && slot.phase <= 3) bgActiveIconCount--;
		slot.alpha = 0f; slot.drawAlpha = 0;
		slot.phase = 0;
		slot.phaseTimer = browseSoundRand.nextInt(16);
	}

	// Performance Scene — O(1) random pick from active pool via swap-with-last
	private int nextBgJarIndex() {
		if (bgJarOrder == null || bgJarOrderCursor >= bgJarOrder.length) {
			int total = bgJarFiles.size();
			if (total == 0) return -1;
			int poolSize = Math.min(BG_POOL_SIZE, total);
			bgJarOrder = new int[poolSize];
			if (total <= BG_POOL_SIZE) {
				for (int i = 0; i < poolSize; i++) bgJarOrder[i] = i;
			} else {
				java.util.BitSet used = new java.util.BitSet(total);
				for (int i = 0; i < poolSize; i++) {
					int idx;
					do { idx = browseSoundRand.nextInt(total); } while (used.get(idx));
					used.set(idx);
					bgJarOrder[i] = idx;
				}
			}
			bgJarOrderCursor = 0;
		}
		int pick = bgJarOrderCursor + browseSoundRand.nextInt(bgJarOrder.length - bgJarOrderCursor);
		int jarIdx = bgJarOrder[pick];
		bgJarOrder[pick] = bgJarOrder[bgJarOrderCursor];
		bgJarOrder[bgJarOrderCursor] = jarIdx;
		bgJarOrderCursor++;
		return jarIdx;
	}

	private void advanceBgIconAnim(float dt) { // Performance Scene — tick icons
		if (shell == null || shell.isDisposed()) return;
		rebuildBgGrid();
		if (bgIconSlots.isEmpty()) return;
		Rectangle ca = canvas.getClientArea();
		int w = ca.width, h = ca.height;
		int step = BG_ICON_SIZE + bgGridPad;
		if (step <= 0) return;
		float scrollAmount = BG_SCROLL_SPEED_SEC * dt;
		if (scrollAmount < 0.001f) scrollAmount = 0.001f;
		// Scroll offset per direction (both axes for diagonal)
		switch (bgScrollDir) {
		case DIR_UP:          bgScrollY += scrollAmount; break;
		case DIR_DOWN:        bgScrollY -= scrollAmount; break;
		case DIR_RIGHT:       bgScrollX -= scrollAmount; break;
		case DIR_LEFT:        bgScrollX += scrollAmount; break;
		case DIR_UP_RIGHT:    bgScrollY += scrollAmount; bgScrollX -= scrollAmount; break;
		case DIR_UP_LEFT:     bgScrollY += scrollAmount; bgScrollX += scrollAmount; break;
		case DIR_DOWN_RIGHT:  bgScrollY -= scrollAmount; bgScrollX -= scrollAmount; break;
		case DIR_DOWN_LEFT:   bgScrollY -= scrollAmount; bgScrollX += scrollAmount; break;
		}
		// Wrap check (primary axis depends on direction)
		boolean wrap = false;
		switch (bgScrollDir) {
		case DIR_UP: case DIR_UP_RIGHT: case DIR_UP_LEFT: wrap = bgScrollY >= step * BG_LEADING_PAD; break;
		case DIR_DOWN: case DIR_DOWN_RIGHT: case DIR_DOWN_LEFT: wrap = bgScrollY <= -(step * BG_LEADING_PAD); break;
		case DIR_RIGHT: wrap = bgScrollX <= -(step * BG_LEADING_PAD); break;
		case DIR_LEFT: wrap = bgScrollX >= step * BG_LEADING_PAD; break;
		}
		if (wrap) {
			// Snap back both axes based on direction
			int snapStep = step * BG_LEADING_PAD;
			switch (bgScrollDir) {
			case DIR_UP:          bgScrollY -= snapStep; break;
			case DIR_DOWN:        bgScrollY += snapStep; break;
			case DIR_RIGHT:       bgScrollX += snapStep; break;
			case DIR_LEFT:        bgScrollX -= snapStep; break;
			case DIR_UP_RIGHT:    bgScrollY -= snapStep; bgScrollX += snapStep; break;
			case DIR_UP_LEFT:     bgScrollY -= snapStep; bgScrollX -= snapStep; break;
			case DIR_DOWN_RIGHT:  bgScrollY += snapStep; bgScrollX += snapStep; break;
			case DIR_DOWN_LEFT:   bgScrollY += snapStep; bgScrollX -= snapStep; break;
			}
			if (bgMaxActiveIcons > bgSteadyCap) bgMaxActiveIcons = bgSteadyCap;
			bgWrapCount++;
			int cols = bgGridCols;
			int rows = bgGridRows;
			// Source offset (dr, dc) and iteration order per direction
			int dr, dc, rStart, rEnd, rInc, cStart, cEnd, cInc;
			switch (bgScrollDir) {
			case DIR_UP:          dr=BG_LEADING_PAD;  dc=0;               rStart=0;    rEnd=rows; rInc=1;  cStart=0;    cEnd=cols; cInc=1;  break;
			case DIR_DOWN:        dr=-BG_LEADING_PAD; dc=0;               rStart=rows-1;rEnd=-1;   rInc=-1; cStart=0;    cEnd=cols; cInc=1;  break;
			case DIR_RIGHT:       dr=0;                dc=-BG_LEADING_PAD; rStart=0;    rEnd=rows; rInc=1;  cStart=cols-1;cEnd=-1;   cInc=-1; break;
			case DIR_LEFT:        dr=0;                dc=BG_LEADING_PAD;  rStart=0;    rEnd=rows; rInc=1;  cStart=0;    cEnd=cols; cInc=1;  break;
			case DIR_UP_RIGHT:    dr=BG_LEADING_PAD;  dc=-BG_LEADING_PAD; rStart=0;    rEnd=rows; rInc=1;  cStart=cols-1;cEnd=-1;   cInc=-1; break;
			case DIR_UP_LEFT:     dr=BG_LEADING_PAD;  dc=BG_LEADING_PAD;  rStart=0;    rEnd=rows; rInc=1;  cStart=0;    cEnd=cols; cInc=1;  break;
			case DIR_DOWN_RIGHT:  dr=-BG_LEADING_PAD; dc=-BG_LEADING_PAD; rStart=rows-1;rEnd=-1;   rInc=-1; cStart=cols-1;cEnd=-1;   cInc=-1; break;
			default: /*DIR_DOWN_LEFT*/ dr=-BG_LEADING_PAD; dc=BG_LEADING_PAD;  rStart=rows-1;rEnd=-1;   rInc=-1; cStart=0;    cEnd=cols; cInc=1;  break;
			}
			for (int r = rStart; r != rEnd; r += rInc) {
				for (int c = cStart; c != cEnd; c += cInc) {
					int srcR = r + dr;
					int srcC = c + dc;
					if (srcR >= 0 && srcR < rows && srcC >= 0 && srcC < cols) {
						// Shift data from diagonal/axial neighbor
						BgIconSlot s = bgIconSlots.get(r * cols + c);
						BgIconSlot src = bgIconSlots.get(srcR * cols + srcC);
						// Dispose s's old icon and remove its count if it still has one
						boolean sHadIcon = s.icon != null;
						if (sHadIcon && !s.icon.isDisposed()) s.icon.dispose();
						if (sHadIcon && s.phase >= 1 && s.phase <= 3) bgActiveIconCount--;
						boolean srcHadIcon = src.icon != null && src.phase >= 1 && src.phase <= 3;
						s.icon = src.icon; src.icon = null; s.x = src.x; s.drawX = src.drawX;
						s.y = src.y; s.drawY = src.drawY;
						s.alpha = src.alpha; s.drawAlpha = src.drawAlpha;
						s.phase = src.phase; s.phaseTimer = src.phaseTimer;
						src.phase = 0; src.phaseTimer = browseSoundRand.nextInt(16);
						s.row = r + bgPosDr(); s.col = c + bgPosDc();
						// Fix counts: src loses its contribution, s gains one
						if (srcHadIcon) bgActiveIconCount--;
						if (s.phase >= 1 && s.phase <= 3 && s.icon != null) bgActiveIconCount++;
					} else {
						// Spawn edge — reset with direct cache assign
						BgIconSlot slot = bgIconSlots.get(r * cols + c);
						disposeBgIconSlot(slot);
						slot.row = r + bgPosDr(); slot.col = c + bgPosDc();
						if (!bgJarFiles.isEmpty() && bgActiveIconCount < bgMaxActiveIcons && browseSoundRand.nextFloat() < 0.33f) {
							int jarIdx = nextBgJarIndex();
							if (jarIdx < 0) continue;
							File f = bgJarFiles.get(jarIdx);
							Image img = loadBgIconImage(f);
							if (img != null) {
								slot.icon = img; slot.alpha = 0f; slot.drawAlpha = 0;
								slot.phase = 1; slot.phaseTimer = 30; bgActiveIconCount++;
							} else {
								if (!bgIconTempFile(f).exists() && !bgNoIconMarker(f).exists()) loadBgIconFromJar(f);
							}
						}
					}
				}
			}
		}
		int step2 = step;
		for (int i = 0; i < bgIconSlots.size(); i++) {
			BgIconSlot slot = bgIconSlots.get(i);
			float ny = bgGridPad + slot.row * step2 - bgScrollY;
			if (slot.y != ny) { slot.y = ny; slot.drawY = Math.round(ny); }
			float nx = bgGridPad + slot.col * step2 - bgScrollX;
			if (slot.x != nx) { slot.x = nx; slot.drawX = Math.round(nx); }
			float fadeStep = ALPHA_FADE_SEC * dt;
			switch (slot.phase) {
			case 0:
				if (slot.icon != null) { if (!slot.icon.isDisposed()) slot.icon.dispose(); slot.icon = null; }
				if (--slot.phaseTimer <= 0) {
					if (bgJarFiles.isEmpty()) { slot.phaseTimer = 60; break; }
					if (bgActiveIconCount >= bgMaxActiveIcons) { slot.phaseTimer = 10 + browseSoundRand.nextInt(20); break; }
					if (browseSoundRand.nextFloat() > 0.33f) { slot.phaseTimer = 10 + browseSoundRand.nextInt(10); break; }
					int jarIdx = nextBgJarIndex();
					if (jarIdx < 0) { slot.phaseTimer = 60; break; }
					File f = bgJarFiles.get(jarIdx);
					if (!bgIconTempFile(f).exists() && !bgNoIconMarker(f).exists()) { loadBgIconFromJar(f); slot.phaseTimer = 60; break; }
					Image img = loadBgIconImage(f);
					if (img == null) { slot.phaseTimer = 2 + browseSoundRand.nextInt(4); break; }
					slot.icon = img; slot.alpha = 0f; slot.drawAlpha = 0;
					slot.phase = 1; slot.phaseTimer = 30; bgActiveIconCount++;
				}
				break;
			case 1:
				slot.alpha = Math.min(0.2f, slot.alpha + fadeStep);
				slot.drawAlpha = Math.round(slot.alpha * 255);
				if (--slot.phaseTimer <= 0) { slot.phase = 2; slot.phaseTimer = 20 + browseSoundRand.nextInt(20); }
				break;
			case 2:
				if (--slot.phaseTimer <= 0) {
					slot.phase = 3;
					int fadeTicks = Math.max(1, Math.round(ALPHA_FADE_SEC / (BG_ANIM_INTERVAL_MS / 1000f)));
					slot.phaseTimer = fadeTicks;
				}
				break;
			case 3:
				slot.alpha = Math.max(0f, slot.alpha - fadeStep);
				slot.drawAlpha = Math.round(slot.alpha * 255);
				if (--slot.phaseTimer <= 0 || slot.alpha <= 0f) {
					if (slot.icon != null && !slot.icon.isDisposed()) slot.icon.dispose();
					if (slot.icon != null) bgActiveIconCount--;
					slot.icon = null; slot.phase = 0;
					slot.alpha = 0f; slot.drawAlpha = 0;
					slot.phaseTimer = browseSoundRand.nextInt(16);
				}
				break;
			}
			// Immediate recycle when fully off-screen at trailing edge
			if (slot.phase >= 1 && slot.icon != null) {
				boolean gone = false;
				switch (bgScrollDir) {
				case DIR_UP:          gone = slot.drawY + BG_ICON_SIZE <= 0; break;
				case DIR_DOWN:        gone = slot.drawY >= h; break;
				case DIR_LEFT:        gone = slot.drawX + BG_ICON_SIZE <= 0; break;
				case DIR_RIGHT:       gone = slot.drawX >= w; break;
				case DIR_UP_RIGHT:    gone = slot.drawY + BG_ICON_SIZE <= 0 || slot.drawX >= w; break;
				case DIR_UP_LEFT:     gone = slot.drawY + BG_ICON_SIZE <= 0 || slot.drawX + BG_ICON_SIZE <= 0; break;
				case DIR_DOWN_RIGHT:  gone = slot.drawY >= h || slot.drawX >= w; break;
				case DIR_DOWN_LEFT:   gone = slot.drawY >= h || slot.drawX + BG_ICON_SIZE <= 0; break;
				}
				if (gone) {
					disposeBgIconSlot(slot);
				}
			}
		}
		bgDirty = true;
	}

	private void paintBgIcons(GC gc, int bgR, int bgG, int bgB) {
		if (bgIconSlots.isEmpty()) return;
		Rectangle ca = canvas.getClientArea();
		int w = ca.width, h = ca.height;
		if (w <= 0 || h <= 0) return;
		int lastA = -1;
		for (BgIconSlot slot : bgIconSlots) {
			if (slot.drawAlpha <= 0 || slot.icon == null || slot.icon.isDisposed()) continue;
			if (slot.drawY + BG_ICON_SIZE <= 0 || slot.drawY >= h || slot.drawX + BG_ICON_SIZE <= 0 || slot.drawX >= w) continue;
			if (slot.drawAlpha != lastA) {
				gc.setAlpha(slot.drawAlpha);
				lastA = slot.drawAlpha;
			}
			gc.drawImage(slot.icon, slot.drawX, slot.drawY);
		}
		gc.setAlpha(255);
	}

	private void scheduleBrowseBtnAnim() {
		if (browseAnimScheduled) return;
		browseAnimScheduled = true;
		loadBgIcons();
		canvas.redraw();
		if (bgAnimRunnable == null) {
			bgAnimRunnable = new Runnable() {
				public void run() {
					try {
						if (shell == null || shell.isDisposed()) return;
						// Performance Scene — pause bg animation only in fullscreen when browser open
						if (fullscreen && (pauseState != 0 || browseFavOpenCount > 0)) {
							browseAnimScheduled = false;
							return;
						}
						long now = System.nanoTime();
						if (bgAnimLastNanos == 0) bgAnimLastNanos = now;
						float dt = (now - bgAnimLastNanos) / 1e9f;
						if (dt > 0.05f) dt = 0.05f;
						bgAnimLastNanos = now;
						advanceBtnHues(dt);
						pulsePhase += PULSE_SPEED_SEC * dt;
						if (pulsePhase > 1f) pulsePhase -= 1f;
						if (fullscreen && shell != null && !shell.isDisposed()) {
							advanceBgIconAnim(dt);
						}
						canvas.redraw();
					} catch (Exception e) {
						e.printStackTrace();
					}
					display.timerExec(BG_ANIM_INTERVAL_MS, this);
				}
			};
		} else {
			bgAnimLastNanos = 0;
		}
		display.timerExec(0, bgAnimRunnable);
	}

	private void setBgScrollDirFromMode() {
		if (bgAnimMode >= 0 && bgAnimMode <= 7) {
			bgScrollDir = bgAnimMode;
		} else {
			bgScrollDir = browseSoundRand.nextInt(8);
		}
	}

	private void cycleBgAnimMode() {
		bgAnimMode = (bgAnimMode + 1) % 9;
		if (bgAnimRunnable != null) {
			display.timerExec(-1, bgAnimRunnable);
		}
		browseAnimScheduled = false;
		disposeBgSlotIcons();
		bgIconSlots.clear();
		bgGridCols = 1; bgGridRows = 1;
		bgScrollX = 0;
		bgScrollY = 0;
		bgAnimLastNanos = 0;
		bgDirty = false;
		bgActiveIconCount = 0;
		setBgScrollDirFromMode();
		scheduleBrowseBtnAnim();
	}

	public void reloadBgIcons() {
		reloadBgIcons(null);
	}

	public void reloadBgIcons(String customPath) {
		currentBgSentence = BG_SENTENCES[browseSoundRand.nextInt(BG_SENTENCES.length)];
		bgCustomDirPath = customPath;
		// Performance Scene — full reset of bg icon state
		if (bgAnimRunnable != null) {
			display.timerExec(-1, bgAnimRunnable);
		}
		browseAnimScheduled = false;
		synchronized (bgLoadingSet) {
			bgLoadingSet.clear();
		}
		// Dispose any active slot icons
		for (BgIconSlot slot : bgIconSlots) {
			if (slot.icon != null && !slot.icon.isDisposed()) slot.icon.dispose();
		}
		bgJarFiles.clear();
		bgIconsLoaded = false;
		bgJarOrder = null;
		bgJarOrderCursor = 0;
		bgIconSlots.clear();
		bgGridCols = 1; bgGridRows = 1;
		bgScrollX = 0;
		bgScrollY = 0;
		setBgScrollDirFromMode();
		bgDirty = false;
		bgAnimLastNanos = 0;
		bgNoIconJars.clear();
		loadBgIcons();
		scheduleBrowseBtnAnim();
	}

	private void startBrowseSound() {
		try {
			final File folder = new File(Emulator.getUserPath(), "just_to_feel_something");
			if (!folder.isDirectory()) return;
			browseSoundFiles = folder.list((dir, name) -> new File(folder, name).isFile());
			if (browseSoundFiles == null || browseSoundFiles.length == 0) return;
			Arrays.sort(browseSoundFiles);
			stopBrowseSound();
			File f = new File(folder, browseSoundFiles[browseSoundRand.nextInt(browseSoundFiles.length)]);
			if (!playWithJavaSound(f)) {
				if (!playWithJavaFX(f)) {
					playWithJLayer(folder);
				}
			}
			browseSoundPlaying = browseSoundClip != null || browseSoundPlayer != null || browseSoundThread != null;
		} catch (Exception ignored) {}
	}

	private boolean playWithJavaFX(File f) {
		try {
			Class<?> mediaClass = Class.forName("javafx.scene.media.Media");
			Constructor<?> mediaCtor = mediaClass.getConstructor(String.class);
			Object media = mediaCtor.newInstance(f.toURI().toString());
			Class<?> playerClass = Class.forName("javafx.scene.media.MediaPlayer");
			Constructor<?> playerCtor = playerClass.getConstructor(mediaClass);
			browseSoundPlayer = playerCtor.newInstance(media);
			Method setCycleCount = playerClass.getMethod("setCycleCount", int.class);
			Object val = null;
			for (Class<?> c : playerClass.getDeclaredClasses()) {
				if (c.getSimpleName().equals("Indefinite")) {
					val = c.getDeclaredFields()[0].get(null);
					break;
				}
			}
			setCycleCount.invoke(browseSoundPlayer, val != null ? val : Integer.MAX_VALUE);
			playerClass.getMethod("setVolume", double.class).invoke(browseSoundPlayer, 0.0);
			playerClass.getMethod("play").invoke(browseSoundPlayer);
			return true;
		} catch (Exception e) {
			browseSoundPlayer = null;
			return false;
		}
	}

	private boolean playWithJLayer(final File folder) {
		try {
			final Class<?> playerClass = Class.forName("javazoom.jl.player.Player");
			final Constructor<?> playerCtor = playerClass.getConstructor(InputStream.class);
			final Method playMtd = playerClass.getMethod("play");
			final Method closeMtd = playerClass.getMethod("close");
			browseSoundThread = new Thread("BrowseSound-JLayer") {
				public void run() {
					try {
						while (browseSoundThread == Thread.currentThread() && !isInterrupted()) {
							File pick = new File(folder, browseSoundFiles[browseSoundRand.nextInt(browseSoundFiles.length)]);
							InputStream fis = new BufferedInputStream(new FileInputStream(pick));
							Object player = playerCtor.newInstance(fis);
							browseSoundPlayer = player;
							playMtd.invoke(player);
							closeMtd.invoke(player);
							if (browseSoundThread != Thread.currentThread() || isInterrupted()) break;
						}
					} catch (Exception ignored) {}
					browseSoundPlayer = null;
				}
			};
			browseSoundThread.setDaemon(true);
			browseSoundThread.start();
			return true;
		} catch (Exception e) {
			browseSoundPlayer = null;
			return false;
		}
	}

	private boolean playWithJavaSound(File f) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(f);
			browseSoundClip = AudioSystem.getClip();
			browseSoundClip.open(ais);
			if (browseSoundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				FloatControl gain = (FloatControl) browseSoundClip.getControl(FloatControl.Type.MASTER_GAIN);
				float min = gain.getMinimum();
				float range = gain.getMaximum() - min;
				gain.setValue(min + range * 0.0f);
			}
			browseSoundClip.loop(Clip.LOOP_CONTINUOUSLY);
			browseSoundClip.start();
			return true;
		} catch (Exception e) {
			browseSoundClip = null;
			return false;
		}
	}

	private void stopBrowseSound() {
		browseSoundPlaying = false;
		Thread t = browseSoundThread;
		Object player = browseSoundPlayer;
		Clip clip = browseSoundClip;
		browseSoundThread = null;
		browseSoundPlayer = null;
		browseSoundClip = null;
		if (t != null) {
			try {
				if (player != null) {
					player.getClass().getMethod("close").invoke(player);
				}
				t.interrupt();
			} catch (Exception ignored) {}
		}
		if (player != null) {
			try {
				player.getClass().getMethod("stop").invoke(player);
				player.getClass().getMethod("dispose").invoke(player);
			} catch (Exception ignored) {}
		}
		if (clip != null) {
			try { clip.stop(); } catch (Exception ignored) {}
			try { clip.close(); } catch (Exception ignored) {}
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
			Class<?> os = Class.forName("org.eclipse.swt.internal.win32.OS");
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
		if (crashed || canvas == null || canvas.isDisposed()) return;
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

	private static boolean matchHotkey(KeyEvent e, HotkeyManager.HotkeyAction a) {
		int mods = e.stateMask & (SWT.CTRL | SWT.SHIFT | SWT.ALT);
		if (mods != a.stateMask) return false;
		return e.keyCode == a.keyCode || Character.toLowerCase(e.keyCode) == Character.toLowerCase(a.keyCode);
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
		if (fullscreen && keyEvent.keyCode == SWT.ESC) {
			fullscreen = false;
			changeFullscreen();
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.CYCLE_BG_ANIM)) {
			cycleBgAnimMode();
			return;
		}
		if (this.pauseState == 0) {
			int kc = keyEvent.keyCode;
			if (kc == SWT.ARROW_UP || kc == SWT.KEYPAD_8 || kc == SWT.ARROW_DOWN || kc == SWT.KEYPAD_2 || kc == SWT.F3 || kc == SWT.KEYPAD_5) {
				handleBtnKeyDown(kc);
				return;
			}
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.CYCLE_RES_PREV) && this.pauseState == 1) {
			cycleResolutionPreset(-1);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.CYCLE_RES_NEXT) && this.pauseState == 1) {
			cycleResolutionPreset(1);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.TOGGLE_FULLSCREEN)) {
			fullscreen = !fullscreen;
			changeFullscreen();
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.DELETE_JAR)) {
			doDeleteJar();
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.OPEN_APP_SETTINGS) && this.pauseState == 1) {
			Emulator.getEmulator().openAppSettings(false);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.PREV_JAR) && this.pauseState == 1 && Emulator.midletJarPath != null) {
			browseJarInFolder(-1);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.NEXT_JAR) && this.pauseState == 1 && Emulator.midletJarPath != null) {
			browseJarInFolder(1);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.SPEED_UP)) {
			if (AppSettings.speedModifier == -1) {
				AppSettings.speedModifier = 1;
				this.updateStatus();
				return;
			}
			if (AppSettings.speedModifier < 100) {
				++AppSettings.speedModifier;
				this.updateStatus();
			}
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.SLOW_DOWN)) {
			if (AppSettings.speedModifier == 1) {
				AppSettings.speedModifier = -1;
				this.updateStatus();
				return;
			}
			if (AppSettings.speedModifier > -100) {
				--AppSettings.speedModifier;
				this.updateStatus();
			}
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.RESET_SPEED)) {
			AppSettings.speedModifier = 1;
			this.updateStatus();
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.RESTART_MIDLET) && this.pauseState == 1) {
			Emulator.loadGame(null, false);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.SUSPEND) && this.pauseState == 1) {
			if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
				this.pauseState = 2;
				Emulator.getEventQueue().queue(EventQueue.EVENT_PAUSE);
				this.pauseScreen();
				this.canvas.redraw();
				this.updatePauseState();
			}
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.RESUME) && this.pauseState == 2) {
			if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
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
			}
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.PAUSE_STEP) && this.pauseState == 1) {
			pauseStep();
			this.updatePauseState();
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.PLAY_RESUME) && this.pauseState == 1) {
			this.resumeStep();
			this.updatePauseState();
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.XRAY_VIEW)) {
			AppSettings.xrayView = this.xrayViewMenuItem.getSelection();
			this.xrayViewMenuItem.setSelection(!AppSettings.xrayView);
			AppSettings.xrayView = !AppSettings.xrayView;
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.ROTATE_SCREEN) && this.pauseState == 1) {
			this.setSize(this.getHeight(), this.getWidth());
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.ROTATE_90)) {
			rotate90degrees();
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.FORCE_PAINT) && this.pauseState == 1) {
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
		if (matchHotkey(keyEvent, HotkeyManager.ADD_TO_FAVORITES) && this.pauseState == 1) {
			addCurrentToFavorites();
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.OPEN_FAVORITES)) {
			openFavoritesBrowser();
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.ZOOM_IN)) {
			this.zoomIn();
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.ZOOM_OUT)) {
			this.zoomOut();
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.TOGGLE_RESOLUTION_RESTART)) {
			this.resolutionRestartMenuItem.setSelection(Settings.resolutionRestartMidlet = !Settings.resolutionRestartMidlet);
			((Property) Emulator.getEmulator().getProperty()).saveProperties();
			showToast(Settings.resolutionRestartMidlet ? "Restart on resolution: ON" : "Restart on resolution: OFF", 800);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.RESET_WINDOW_SIZE) && this.pauseState == 1) {
			if (getWidth() != startWidth || getHeight() != startHeight) {
				initScreenBuffer(startWidth, startHeight);
				Emulator.getEventQueue().sizeChanged(startWidth, startHeight);
			}
			Settings.canvasScale = 1f;
			updateCanvasRect(true, true, false);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.CYCLE_SCALE_MODE) && this.pauseState == 1) {
			switch (Settings.resizeMode) {
				case Manual:
					Settings.resizeMode = ResizeMethod.Fit;
					break;
				case Fit:
					Settings.resizeMode = ResizeMethod.FitInteger;
					break;
				case FitInteger:
					Settings.resizeMode = ResizeMethod.FollowWindowSize;
					break;
				case FollowWindowSize:
					Settings.resizeMode = ResizeMethod.Manual;
					break;
			}
			syncScalingModeSelection();
			if (Settings.resizeMode == ResizeMethod.Manual) {
				Settings.canvasScale = (float) (Math.floor(realZoom * 2) / 2d);
			}
			updateCanvasRect(true, false, false);
			String[] names = {"No adaptive", "Fit", "Fit integer", "Sync"};
			showToast("Scale: " + names[Settings.resizeMode.ordinal()], 800);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.CYCLE_INTERP_MODE) && this.pauseState == 1) {
			if (Settings.interpolation == SWT.NONE) {
				Settings.interpolation = SWT.LOW;
			} else if (Settings.interpolation == SWT.LOW) {
				Settings.interpolation = SWT.HIGH;
			} else {
				Settings.interpolation = SWT.NONE;
			}
			interposeNearestMenuItem.setSelection(Settings.interpolation == SWT.NONE);
			interposeLowMenuItem.setSelection(Settings.interpolation == SWT.LOW);
			interposeHighMenuItem.setSelection(Settings.interpolation == SWT.HIGH);
			repaint();
			String[] names = {"Nearest", "Low", "High"};
			showToast("Interp: " + names[Settings.interpolation], 800);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.AUTO_SKIP_APP_SETTINGS)) {
			this.autoSkipAppSettingsMenuItem.setSelection(Settings.showAppSettingsOnStart = !Settings.showAppSettingsOnStart);
			showToast(Settings.showAppSettingsOnStart ? "Auto-skip Application Settings: ON" : "Auto-skip Application Settings: OFF", 800);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.DISABLE_TOUCH_DBLCLICK)) {
			this.disableTouchDblClickMenuItem.setSelection(Settings.disableTouchDoubleClick = !Settings.disableTouchDoubleClick);
			showToast(Settings.disableTouchDoubleClick ? "Disable dbl-click: ON" : "Disable dbl-click: OFF", 800);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.SETUP_LUCKY_FOLDER)) {
			new LuckyFolderSetupDlg().open(display);
			return;
		}
		if (matchHotkey(keyEvent, HotkeyManager.OPEN_LUCKY_JAR)) {
			loadLuckyJar();
			return;
		}
		int n = keyEvent.keyCode & 0xFEFFFFFF;
		caret._keyPressed(keyEvent);
		if (keyEvent.character >= 33 && keyEvent.character <= 90 && Settings.canvasKeyboard && !(n >= 48 && n <= 57))
			n = keyEvent.character;
		handleKeyPress(n);
	}

	public void keyReleased(final KeyEvent keyEvent) {
		if (this.pauseState == 0) {
			int kc = keyEvent.keyCode;
			if (kc == SWT.F3 || kc == SWT.KEYPAD_5) {
				if (browseSelectedIdx == 0 && browseFavHeld) handleBrowseRelease();
				else if (browseSelectedIdx == 1 && luckyFavHeld) handleLuckyRelease();
				/* Debug button removed
				else if (browseSelectedIdx == 2 && debugFavHeld) handleDebugRelease();
				/**/
				return;
			}
		}
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
		if (Settings.disableTouchDoubleClick) {
			return;
		}
		if (Emulator.getCurrentDisplay() != null && Emulator.getCurrentDisplay().getCurrent() != null
				&& Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
			fullscreen = !fullscreen;
			changeFullscreen();
			return;
		}
		if (this.pauseState == 0) {
			fullscreen = !fullscreen;
			changeFullscreen();
			return;
		}
		if (mouseEvent.button != 1) {
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
		if (this.pauseState == 0) {
			if (splashBounds != null && splashBounds.contains(mouseEvent.x, mouseEvent.y)
					&& splashImage != null && !splashImage.isDisposed()
					&& !EmulatorScreen.fullscreen && (shell == null || shell.isDisposed() || !shell.getFullScreen())) {
				splashHeld = true;
				splashHeldSentence = null;
				canvas.redraw();
				return;
			}
			if (handleBrowseMouseDown(mouseEvent)) return;
			if (handleLuckyMouseDown(mouseEvent)) return;
		/* Debug button removed
		if (handleDebugMouseDown(mouseEvent)) return;
		/**/
			splashHeld = false;
			splashHeldSentence = null;
			browseFavHeld = false;
			browseFavPressed = false;
			luckyFavHeld = false;
			luckyFavPressed = false;
			/* Debug button removed
			debugFavHeld = false;
			debugFavPressed = false;
			/**/
			return;
		}
		if (Settings.playingRecordedKeys
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
		if (this.pauseState == 0) {
			if (splashHeld) {
				splashHeld = false;
				splashHeldSentence = null;
				canvas.redraw();
				return;
			}
			if (handleBrowseMouseUp(mouseEvent)) return;
			if (handleLuckyMouseUp(mouseEvent)) return;
		/* Debug button removed
		if (handleDebugMouseUp(mouseEvent)) return;
		/**/
			browseFavHeld = false;
			browseFavPressed = false;
			luckyFavHeld = false;
			luckyFavPressed = false;
			/* Debug button removed
			debugFavHeld = false;
			debugFavPressed = false;
			/**/
			return;
		}
		if (Settings.playingRecordedKeys
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
		handleBrowseMouseMove(mouseEvent);
		handleLuckyMouseMove(mouseEvent);
		/* Debug button removed
		handleDebugMouseMove(mouseEvent);
		/**/
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
		clearBrowseHover();
		clearLuckyHover();
		/* Debug button removed
		clearDebugHover();
		/**/
	}

	public void mouseHover(MouseEvent arg0) {

	}

	public void widgetDisposed(final DisposeEvent disposeEvent) {
		Emulator.getEmulator().disposeSubWindows();
		Emulator.notifyDestroyed();
		if (this.pauseState != 0) {
			Emulator.getEventQueue().queue(EventQueue.EVENT_EXIT);
		}
		if (splashImage != null && !splashImage.isDisposed()) {
			splashImage.dispose();
			splashImage = null;
		}
		// Performance Scene — dispose slot icons and clean up temp files
		for (BgIconSlot slot : bgIconSlots) {
			if (slot.icon != null && !slot.icon.isDisposed()) slot.icon.dispose();
		}
		bgIconSlots.clear();
		bgJarFiles.clear();
		if (pulseBgColor != null && !pulseBgColor.isDisposed()) { pulseBgColor.dispose(); pulseBgColor = null; }
		if (splashDarkBg != null && !splashDarkBg.isDisposed()) { splashDarkBg.dispose(); splashDarkBg = null; }
		if (splashDarkFg != null && !splashDarkFg.isDisposed()) { splashDarkFg.dispose(); splashDarkFg = null; }
		if (statusBarDarkBg != null && !statusBarDarkBg.isDisposed()) { statusBarDarkBg.dispose(); statusBarDarkBg = null; }
		}

	public void controlMoved(final ControlEvent controlEvent) {
		if (controlEvent.widget != shell)
			return;
		this.getWindowPos();
		if (fullscreen) return;
		if (((Log) Emulator.getEmulator().getLogStream()).isLogOpen()) {
			final Shell logWindow = ((Log) Emulator.getEmulator().getLogStream()).getLogShell();
			if (((Log) Emulator.getEmulator().getLogStream()).isAttachedToParent() && !logWindow.isDisposed()) {
				logWindow.setLocation(this.shell.getLocation().x + this.shell.getSize().x, this.shell.getLocation().y);
			}
		}
		if (((SWTFrontend) Emulator.getEmulator()).getClassWatcher().isVisible()) {
			final Shell watchShell = ((SWTFrontend) Emulator.getEmulator()).getClassWatcher().getShell();
			if ((((SWTFrontend) Emulator.getEmulator()).getClassWatcher()).isAttachedToParent() && !watchShell.isDisposed()) {
				watchShell.setLocation(this.shell.getLocation().x - watchShell.getSize().x, this.shell.getLocation().y);
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
		startVibra(100, aLong1013);
	}

	public void startVibra(final int intensity, final long duration) {
		if (!Settings.enableVibration) {
			return;
		}
		sendVibraToGamepad((int) duration, intensity);
		if (maximized) {
			return;
		}
		this.vibra = duration;
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
		sendVibraToGamepad(0, 0);
		if (this.vibraThread != null) {
			this.vibraThread.stop = true;
		}
	}

	private static void sendVibraToGamepad(int durationMs, int intensity) {
		VibraPipe.send(durationMs, intensity);
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
		try {
			setWindowOnTop(ReflectUtil.getHandle(shell), true);
		} catch (Throwable ignored) {}
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
			} catch (Exception ignored) {}
		}

		if (Settings.favoritesDarkMode) {
			applyThemeToShell(shell, true);
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
		} catch (Throwable ignored) {}
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
		} catch (Throwable ignored) {}

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

	private void refreshGamepadMenuState() {
		boolean avail = Emulator.isJ2MEGamepadAvailable();
		j2meGamepadTerminateMenuItem.setEnabled(avail);
		j2meGamepadLaunchMenuItem.setEnabled(avail);
		if (!avail) {
			j2meGamepadTerminateMenuItem.setSelection(false);
			j2meGamepadLaunchMenuItem.setSelection(false);
			Settings.j2meGamepadEnabled = false;
			Settings.j2meGamepadAutoLaunch = false;
		}
	}
}