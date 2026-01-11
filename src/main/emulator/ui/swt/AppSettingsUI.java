package emulator.ui.swt;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.SortedMap;

import emulator.*;
import emulator.custom.CustomMethod;
import emulator.custom.ResourceManager;
import emulator.graphics2D.swt.ImageSWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Canvas;

public class AppSettingsUI {
	private Text screenWidthText;
	private Text screenHeightText;
	private Text platformText;
	private Shell shell;
	private Text localeText;
	private Text leftSoftText;
	private Text rightSoftText;
	private Text fireText;
	private Text upText;
	private Text leftText;
	private Text rightText;
	private Text downText;
	private Text systemPropertiesText;
	private Combo encodingCombo;
	private Combo deviceCombo;
	private Scale fpsScale;
	private Label fpsLabel;
	private ImageSWT iconImage;

	private int action;
	private boolean start;
	private Spinner largeSizeSpinner;
	private Spinner mediumSizeSpinner;
	private Spinner smallSizeSpinner;
	private Button keyRepeatsCheck;
	private Button pointerEventsCheck;
	private Button ignoreFullScreenCheck;
	private Button j2lStyleFpsLimitCheck;
	private Button motorolaSoftKeyFixCheck;
	private Button synchronizeKeyEventsCheck;
	private Button keyPressOnRepeatCheck;
	private Button asyncFlushCheck;
	private Combo m3gAACombo;
	private Combo m3gTexFilterCombo;
	private Combo m3gMipmapCombo;
	private Button mascotNo2DMixingCheck;
	private Button mascotIgnoreBgCheck;
	private Button mascotTextureFilterCheck;
	private Button mascotBackgroundFilterCheck;
	private Button m3gFlushImmediately;
	private Button m3gIgnoreOverwriteCheck;
	private Button m3gForcePersCorrectCheck;
	private Button m3gDisableLightClampCheck;
	private Button startAppOnResumeCheck;
	private Button m3gThreadCheck;
	private CTabFolder tabFolder;

	public AppSettingsUI() {
	}

	public void open(Display display, Shell parent, boolean start) {
		this.start = start;
		action = 0;
		if (shell == null || shell.isDisposed()) {
			createContents(parent);
		}
		if (parent != null) {
			shell.setLocation(parent.getLocation().x + (parent.getSize().x - this.shell.getSize().x >> 1), parent.getLocation().y + (parent.getSize().y - this.shell.getSize().y >> 1));
		} else {
			Rectangle clientArea = this.shell.getMonitor().getClientArea();
			Point size = shell.getSize();
			shell.setLocation(clientArea.x + (clientArea.width - size.x) / 2, clientArea.y + (clientArea.height - size.y) / 2);
		}
		shell.open();
		shell.layout();
		tabFolder.setSelection(0);
		screenWidthText.setFocus();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	protected void createContents(Shell parent) {
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setLayout(new GridLayout(1, false));
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				if (action == 0 && start) {
					CustomMethod.close();
					System.exit(0);
				}
			}
		});

		shell.setText("Application Settings");
		shell.setSize(460, 420);
		shell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));

		Composite scrolledComposite = new Composite(shell, SWT.NONE);
		scrolledComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		String name = "Name: " + Emulator.getEmulator().getAppProperty("MIDlet-Name");
		if (AppSettings.uei) {
			name = name + " (UEI)";
		}
		lblNewLabel.setText(name);

		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(1, false);
		gl_composite_2.marginHeight = 0;
		gl_composite_2.marginWidth = 0;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 3));

		InputStream inputStream = null;
		try {
			String iconPath = Emulator.getEmulator().getAppProperty("MIDlet-Icon");
			if (iconPath == null) {
				if ((iconPath = Emulator.getEmulator().getAppProperty("AppIcon")) != null) {
					iconPath = iconPath.split(",")[0].trim();
				}
			}
			if (iconPath == null) {
				if ((iconPath = Emulator.getEmulator().getAppProperty("MIDlet-1")) != null) {
					iconPath = iconPath.split(",")[1].trim();
				}
			}
			if (iconPath != null && !iconPath.trim().isEmpty()) {
				inputStream = ResourceManager.getResourceAsStream(iconPath);
			}
			if (inputStream != null) {
				iconImage = new ImageSWT(inputStream);
			}
		} catch (Exception ignored) {}

		Canvas canvas = new Canvas(composite_2, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (iconImage != null) {
					GC gc = e.gc;
					int imageWidth = iconImage.getWidth();
					int imageHeight = iconImage.getHeight();
					int width = canvas.getSize().x;
					int height = canvas.getSize().y;
					if (width < imageWidth * 2 || height < imageHeight * 2) {
						gc.setInterpolation(SWT.HIGH);
					} else {
						gc.setInterpolation(SWT.NONE);
					}
					iconImage.copyToScreen(gc, 0, 0, imageWidth, imageHeight, 0, 0, height, height);
				}
			}
		});

		Label lblNewLabel_1 = new Label(composite_1, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNewLabel_1.setText("Vendor: " + Emulator.getEmulator().getAppProperty("MIDlet-Vendor"));

		Label lblNewLabel_2 = new Label(composite_1, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNewLabel_2.setText("Version: " + Emulator.getEmulator().getAppProperty("MIDlet-Version"));

		tabFolder = new CTabFolder(composite, SWT.BORDER | SWT.FLAT);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(22));
		tabFolder.setSimple(true);
		tabFolder.setUnselectedCloseVisible(false);
		tabFolder.setUnselectedImageVisible(false);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		CTabItem tbtmGeneral = new CTabItem(tabFolder, SWT.NONE);
		tbtmGeneral.setText("General");

		Composite composite_5 = new Composite(tabFolder, SWT.NONE);
		tbtmGeneral.setControl(composite_5);
		composite_5.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite grpDevice = new Composite(composite_5, SWT.NONE);
		grpDevice.setLayout(new GridLayout(4, false));

		Label lblNewLabel_3 = new Label(grpDevice, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setText("Device preset:");

		deviceCombo = new Combo(grpDevice, SWT.NONE);
		deviceCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				applyPreset(deviceCombo.getText().trim());
			}
		});
		deviceCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		{
			final Enumeration method620 = Devices.method620();
			while (method620.hasMoreElements()) {
				final String text = (String) method620.nextElement();
				deviceCombo.add(text);
				if (AppSettings.devicePreset.equalsIgnoreCase(text)) {
					deviceCombo.setText(text);
					deviceCombo.select(deviceCombo.getItemCount() - 1);
				}
			}
		}

		Label lblNewLabel_7 = new Label(grpDevice, SWT.NONE);
		lblNewLabel_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_7.setText("Platform:");

		platformText = new Text(grpDevice, SWT.BORDER);
		platformText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		platformText.setText(AppSettings.microeditionPlatform);

		Label lblNewLabel_8 = new Label(grpDevice, SWT.NONE);
		lblNewLabel_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_8.setText("Encoding:");

		encodingCombo = new Combo(grpDevice, SWT.NONE);
		encodingCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		{
			final SortedMap<String, Charset> availableCharsets = Charset.availableCharsets();
			final ArrayList<Comparable> list = new ArrayList(availableCharsets.keySet());
			Collections.sort(list);
			String s = (String) list.get(0);
			for (int i = 0; i < list.size(); ++i) {
				String e = (String) list.get(i);
				if (!e.equalsIgnoreCase("ISO-8859-1")
						&& !e.equalsIgnoreCase("UTF-8")
						&& !e.equalsIgnoreCase("Shift_JIS")
						&& !e.equalsIgnoreCase("EUC-KR")) {
					continue;
				}
				encodingCombo.add(e);
				if (AppSettings.fileEncoding.equalsIgnoreCase(e)) {
					s = e;
				}
			}
			AppSettings.fileEncoding = s;
			encodingCombo.setText(s);
		}

		Label lblNewLabel_9 = new Label(grpDevice, SWT.NONE);
		lblNewLabel_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_9.setText("Locale:");

		localeText = new Text(grpDevice, SWT.BORDER);
		localeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		localeText.setText(AppSettings.locale);

		Label lblNewLabel_4 = new Label(grpDevice, SWT.NONE);
		lblNewLabel_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_4.setText("Screen size:");

		Composite composite_4 = new Composite(grpDevice, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		GridLayout gl_composite_4 = new GridLayout(3, false);
		gl_composite_4.verticalSpacing = 0;
		gl_composite_4.marginWidth = 0;
		gl_composite_4.marginHeight = 0;
		composite_4.setLayout(gl_composite_4);

		screenWidthText = new Text(composite_4, SWT.BORDER);
		screenWidthText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		screenWidthText.setText(Integer.toString(AppSettings.screenWidth));

		screenHeightText = new Text(composite_4, SWT.BORDER);
		screenHeightText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		screenHeightText.setText(Integer.toString(AppSettings.screenHeight));

		Button btnNewButton_2 = new Button(composite_4, SWT.NONE);
		btnNewButton_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String tmp = screenWidthText.getText();
				screenWidthText.setText(screenHeightText.getText());
				screenHeightText.setText(tmp);
			}
		});
		btnNewButton_2.setText("Swap");

		fpsLabel = new Label(grpDevice, SWT.NONE);
		fpsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		fpsLabel.setText(UILocale.get("OPTION_CUSTOM_MAX_FPS", "Max FPS:") + " " + ((AppSettings.frameRate > 120) ? "\u221e" : String.valueOf(AppSettings.frameRate)));

		fpsScale = new Scale(grpDevice, SWT.NONE);
		fpsScale.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AppSettings.frameRate = fpsScale.getSelection();
				AppSettings.set("FrameRate", AppSettings.frameRate);
				fpsLabel.setText(UILocale.get("OPTION_CUSTOM_MAX_FPS", "Max FPS:") + " " + ((AppSettings.frameRate > 120) ? "\u221e" : String.valueOf(AppSettings.frameRate)));
			}
		});
		fpsScale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		fpsScale.setIncrement(1);
		fpsScale.setPageIncrement(5);
		fpsScale.setMaximum(121);
		fpsScale.setMinimum(1);
		fpsScale.setSelection(AppSettings.frameRate);

		CTabItem tbtmKeyCodes = new CTabItem(tabFolder, SWT.NONE);
		tbtmKeyCodes.setText("Key codes");

		Composite grpKeyMapping = new Composite(tabFolder, SWT.NONE);
		tbtmKeyCodes.setControl(grpKeyMapping);
		grpKeyMapping.setLayout(new GridLayout(4, false));

		Label lblNewLabel_10 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_10.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblNewLabel_10.setText("Left soft key:");

		leftSoftText = new Text(grpKeyMapping, SWT.BORDER);
		leftSoftText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		leftSoftText.setText(Integer.toString(AppSettings.leftSoftKey));

		Label lblNewLabel_11 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_11.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblNewLabel_11.setText("Right soft key:");

		rightSoftText = new Text(grpKeyMapping, SWT.BORDER);
		rightSoftText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rightSoftText.setText(Integer.toString(AppSettings.rightSoftKey));

		Label lblNewLabel_12 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_12.setText("Fire/Middle:");

		fireText = new Text(grpKeyMapping, SWT.BORDER);
		fireText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fireText.setText(Integer.toString(AppSettings.fireKey));
		new Label(grpKeyMapping, SWT.NONE);
		new Label(grpKeyMapping, SWT.NONE);
		
		Label lblNewLabel_13 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_13.setText("Up:");

		upText = new Text(grpKeyMapping, SWT.BORDER);
		upText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		upText.setText(Integer.toString(AppSettings.upKey));

		Label lblNewLabel_15 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_15.setAlignment(SWT.RIGHT);
		lblNewLabel_15.setText("Down:");

		downText = new Text(grpKeyMapping, SWT.BORDER);
		downText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		downText.setText(Integer.toString(AppSettings.downKey));
		
		Label lblNewLabel_14 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_14.setText("Left:");

		leftText = new Text(grpKeyMapping, SWT.BORDER);
		leftText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		leftText.setText(Integer.toString(AppSettings.leftKey));

		Label lblNewLabel_16 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_16.setText("Right:");

		rightText = new Text(grpKeyMapping, SWT.BORDER);
		rightText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rightText.setText(Integer.toString(AppSettings.rightKey));

		CTabItem tbtmFont = new CTabItem(tabFolder, SWT.NONE);
		tbtmFont.setText("Font");

		Composite grpFont = new Composite(tabFolder, SWT.NONE);
		tbtmFont.setControl(grpFont);
		grpFont.setLayout(new GridLayout(2, false));

		Label lblNewLabel_5 = new Label(grpFont, SWT.NONE);
		lblNewLabel_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_5.setText("Large size:");

		largeSizeSpinner = new Spinner(grpFont, SWT.BORDER);
		largeSizeSpinner.setSelection(AppSettings.fontLargeSize);

		Label lblNewLabel_5_1 = new Label(grpFont, SWT.NONE);
		lblNewLabel_5_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_5_1.setText("Medium size:");

		mediumSizeSpinner = new Spinner(grpFont, SWT.BORDER);
		mediumSizeSpinner.setSelection(AppSettings.fontMediumSize);

		Label lblNewLabel_5_1_1 = new Label(grpFont, SWT.NONE);
		lblNewLabel_5_1_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_5_1_1.setText("Small size:");

		smallSizeSpinner = new Spinner(grpFont, SWT.BORDER);
		smallSizeSpinner.setSelection(AppSettings.fontSmallSize);

		CTabItem tbtmTweaks = new CTabItem(tabFolder, SWT.NONE);
		tbtmTweaks.setText("Tweaks");

		Composite composite_6 = new Composite(tabFolder, SWT.NONE);
		tbtmTweaks.setControl(composite_6);
		composite_6.setLayout(new FillLayout(SWT.HORIZONTAL));

		ScrolledComposite scrolledComposite_2 = new ScrolledComposite(composite_6, SWT.V_SCROLL);
		scrolledComposite_2.setExpandHorizontal(true);
		scrolledComposite_2.setExpandVertical(true);

		Composite composite_9 = new Composite(scrolledComposite_2, SWT.NONE);
		composite_9.setLayout(new GridLayout(1, false));

		Group grpCanvasCapabilities = new Group(composite_9, SWT.NONE);
		grpCanvasCapabilities.setText("Canvas capabilities");
		grpCanvasCapabilities.setLayout(new GridLayout(1, false));
		grpCanvasCapabilities.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		keyRepeatsCheck = new Button(grpCanvasCapabilities, SWT.CHECK);
		keyRepeatsCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		keyRepeatsCheck.setText("Key repeats support");
		keyRepeatsCheck.setSelection(AppSettings.enableKeyRepeat);

		pointerEventsCheck = new Button(grpCanvasCapabilities, SWT.CHECK);
		pointerEventsCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		pointerEventsCheck.setText("Pointer events support");
		pointerEventsCheck.setSelection(AppSettings.hasPointerEvents);

		Group grpTweaks = new Group(composite_9, SWT.NONE);
		grpTweaks.setText("Tweaks");
		grpTweaks.setLayout(new GridLayout(1, false));
		grpTweaks.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		ignoreFullScreenCheck = new Button(grpTweaks, SWT.CHECK);
		ignoreFullScreenCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		ignoreFullScreenCheck.setText("Ignore Canvas.setFullScreenMode");
		ignoreFullScreenCheck.setToolTipText("Forces full-screen mode");
		ignoreFullScreenCheck.setSelection(AppSettings.ignoreFullScreen);

		j2lStyleFpsLimitCheck = new Button(grpTweaks, SWT.CHECK);
		j2lStyleFpsLimitCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		j2lStyleFpsLimitCheck.setText("J2ME Loader style FPS limiter");
		j2lStyleFpsLimitCheck.setToolTipText("Compatibility tweak for chinese version of Castlevania");
		j2lStyleFpsLimitCheck.setSelection(AppSettings.j2lStyleFpsLimit);

		motorolaSoftKeyFixCheck = new Button(grpTweaks, SWT.CHECK);
		motorolaSoftKeyFixCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		motorolaSoftKeyFixCheck.setText("Send key events with commandAction");
		motorolaSoftKeyFixCheck.setToolTipText("Compatibility tweak for certain Motorola Triplets games");
		motorolaSoftKeyFixCheck.setSelection(AppSettings.motorolaSoftKeyFix);

		keyPressOnRepeatCheck = new Button(grpTweaks, SWT.CHECK);
		keyPressOnRepeatCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		keyPressOnRepeatCheck.setText("Send keyPressed on repeats");
		keyPressOnRepeatCheck.setToolTipText("Compatibility tweak for The Elder Scrolls: Oblivion");
		keyPressOnRepeatCheck.setSelection(AppSettings.keyPressOnRepeat);

		synchronizeKeyEventsCheck = new Button(grpTweaks, SWT.CHECK);
		synchronizeKeyEventsCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		synchronizeKeyEventsCheck.setText("Synchronize key events");
		synchronizeKeyEventsCheck.setToolTipText("Compatibility option");
		synchronizeKeyEventsCheck.setSelection(AppSettings.synchronizeKeyEvents);

		asyncFlushCheck = new Button(grpTweaks, SWT.CHECK);
		asyncFlushCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		asyncFlushCheck.setText("Asynchronous canvas flush");
		asyncFlushCheck.setToolTipText("If disabled, window refresh delay is passed to app. Restart after changing this property.");
		asyncFlushCheck.setSelection(AppSettings.asyncFlush);
		
		startAppOnResumeCheck = new Button(grpTweaks, SWT.CHECK);
		startAppOnResumeCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		startAppOnResumeCheck.setText("Send startApp on resume");
		startAppOnResumeCheck.setSelection(AppSettings.startAppOnResume);

		scrolledComposite_2.setContent(composite_9);
		scrolledComposite_2.setMinSize(composite_9.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		CTabItem tbtmd = new CTabItem(tabFolder, SWT.NONE);
		tbtmd.setText("3D");

		Composite composite_7 = new Composite(tabFolder, SWT.NONE);
		tbtmd.setControl(composite_7);
		composite_7.setLayout(new FillLayout(SWT.HORIZONTAL));

		ScrolledComposite scrolledComposite_1 = new ScrolledComposite(composite_7, SWT.V_SCROLL);
		scrolledComposite_1.setExpandHorizontal(true);
		scrolledComposite_1.setExpandVertical(true);

		Composite composite_8 = new Composite(scrolledComposite_1, SWT.NONE);
		composite_8.setLayout(new GridLayout(1, false));

		Group grpMgLwjgl = new Group(composite_8, SWT.NONE);
		grpMgLwjgl.setLayout(new GridLayout(2, false));
		grpMgLwjgl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpMgLwjgl.setText("M3G LWJGL");

		m3gIgnoreOverwriteCheck = new Button(grpMgLwjgl, SWT.CHECK);
		m3gIgnoreOverwriteCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		m3gIgnoreOverwriteCheck.setText("Ignore OVERWRITE hint");
		m3gIgnoreOverwriteCheck.setSelection(AppSettings.m3gIgnoreOverwrite);

		m3gForcePersCorrectCheck = new Button(grpMgLwjgl, SWT.CHECK);
		m3gForcePersCorrectCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		m3gForcePersCorrectCheck.setText("Force perspective correction");
		m3gForcePersCorrectCheck.setSelection(AppSettings.m3gForcePerspectiveCorrection);

		m3gDisableLightClampCheck = new Button(grpMgLwjgl, SWT.CHECK);
		m3gDisableLightClampCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		m3gDisableLightClampCheck.setText("Disable light clamping");
		m3gDisableLightClampCheck.setSelection(AppSettings.m3gDisableLightClamp);

		m3gFlushImmediately = new Button(grpMgLwjgl, SWT.CHECK);
		m3gFlushImmediately.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		m3gFlushImmediately.setText("Flush contents immediately (slow!)");
		m3gFlushImmediately.setToolTipText("Fixes background in Angry Birds Seasons");
		m3gFlushImmediately.setSelection(AppSettings.m3gFlushImmediately);

		m3gThreadCheck = new Button(grpMgLwjgl, SWT.CHECK);
		m3gThreadCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		m3gThreadCheck.setText("Single threaded M3G");
		m3gThreadCheck.setToolTipText("Fixes some games, but less performance. Restart after changing this property.");
		m3gThreadCheck.setSelection(AppSettings.m3gThread);

		Label lblNewLabel_17 = new Label(grpMgLwjgl, SWT.NONE);
		lblNewLabel_17.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_17.setText("Anti-aliasing:");

		m3gAACombo = new Combo(grpMgLwjgl, SWT.NONE);
		m3gAACombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		m3gAACombo.add(UILocale.get("OPTION_M3G_APP_CONTROLLED", "Application-controlled"));
		m3gAACombo.add(UILocale.get("OPTION_M3G_FORCE_OFF", "Force off"));
		m3gAACombo.add(UILocale.get("OPTION_M3G_FORCE_ON", "Force on"));
		m3gAACombo.select(AppSettings.m3gAA);

		Label lblNewLabel_18 = new Label(grpMgLwjgl, SWT.NONE);
		lblNewLabel_18.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_18.setText("Texture filter:");

		m3gTexFilterCombo = new Combo(grpMgLwjgl, SWT.NONE);
		m3gTexFilterCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		m3gTexFilterCombo.add(UILocale.get("OPTION_M3G_APP_CONTROLLED", "Application-controlled"));
		m3gTexFilterCombo.add(UILocale.get("OPTION_M3G_FORCE_NEAREST", "Force nearest"));
		m3gTexFilterCombo.add(UILocale.get("OPTION_M3G_FORCE_LINEAR", "Force linear"));
		m3gTexFilterCombo.select(AppSettings.m3gTexFilter);

		Label lblNewLabel_19 = new Label(grpMgLwjgl, SWT.NONE);
		lblNewLabel_19.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_19.setText("Mipmapping:");

		m3gMipmapCombo = new Combo(grpMgLwjgl, SWT.NONE);
		m3gMipmapCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_APP_CONTROLLED", "Application-controlled"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_OFF", "Force off"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_BILINEAR", "Force bilinear"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_TRILINEAR", "Force trilinear"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_ANISO_2X", "Force anisotropic 2x"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_ANISO_4X", "Force anisotropic 4x"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_ANISO_8X", "Force anisotropic 8x"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_ANISO_16X", "Force anisotropic 16x"));
		m3gMipmapCombo.select(AppSettings.m3gMipmapping);

		Group grpMascotcapsuleLwjgl = new Group(composite_8, SWT.NONE);
		grpMascotcapsuleLwjgl.setLayout(new GridLayout(1, false));
		grpMascotcapsuleLwjgl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpMascotcapsuleLwjgl.setText("MascotCapsule LWJGL");

		mascotNo2DMixingCheck = new Button(grpMascotcapsuleLwjgl, SWT.CHECK);
		mascotNo2DMixingCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		mascotNo2DMixingCheck.setText("No 2D mixing");
		mascotNo2DMixingCheck.setSelection(AppSettings.mascotNo2DMixing);

		mascotIgnoreBgCheck = new Button(grpMascotcapsuleLwjgl, SWT.CHECK);
		mascotIgnoreBgCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		mascotIgnoreBgCheck.setText("Ignore background");
		mascotIgnoreBgCheck.setSelection(AppSettings.mascotIgnoreBackground);

		mascotTextureFilterCheck = new Button(grpMascotcapsuleLwjgl, SWT.CHECK);
		mascotTextureFilterCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		mascotTextureFilterCheck.setText("Texture filter");
		mascotTextureFilterCheck.setSelection(AppSettings.mascotTextureFilter);

		mascotBackgroundFilterCheck = new Button(grpMascotcapsuleLwjgl, SWT.CHECK);
		mascotBackgroundFilterCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		mascotBackgroundFilterCheck.setText("Background filter");
		mascotBackgroundFilterCheck.setSelection(AppSettings.mascotBackgroundFilter);

		scrolledComposite_1.setContent(composite_8);
		scrolledComposite_1.setMinSize(composite_8.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		CTabItem tbtmProperties = new CTabItem(tabFolder, SWT.NONE);
		tbtmProperties.setText("Properties");

		systemPropertiesText = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		StringBuilder s = new StringBuilder();
		for (String k: AppSettings.systemProperties.keySet()) {
			s.append(k).append(": ").append(AppSettings.systemProperties.get(k)).append('\n');
		}
		systemPropertiesText.setText(s.toString());
		systemPropertiesText.setToolTipText("foo.bar: Example");
		tbtmProperties.setControl(systemPropertiesText);

		Composite composite_3 = new Composite(shell, SWT.NONE);
		composite_3.setLayout(new GridLayout(2, false));
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));

		Button btnNewButton_1 = new Button(composite_3, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				action = 1;
				apply();
				shell.close();
			}
		});
		btnNewButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnNewButton_1.setText("Done");

		Button btnNewButton = new Button(composite_3, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				action = 2;
				AppSettings.init();
				AppSettings.load(true);
				AppSettings.clear();
				KeyMapping.init();
				shell.close();
			}
		});
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnNewButton.setText(start ? "Default" : "Reset");


		scrolledComposite_1.setMinHeight(composite_8.computeSize(-1, -1).y);
		scrolledComposite_2.setMinHeight(composite_9.computeSize(-1, -1).y);
	}

	private void applyPreset(String s) {
		DevicePlatform p = Devices.getPlatform(s);
		screenWidthText.setText(p.getString("SCREEN_WIDTH"));
		screenHeightText.setText(p.getString("SCREEN_HEIGHT"));
		leftSoftText.setText(p.getString("KEY_S1"));
		rightSoftText.setText(p.getString("KEY_S2"));
		fireText.setText(p.getString("KEY_FIRE"));
		upText.setText(p.getString("KEY_UP"));
		downText.setText(p.getString("KEY_DOWN"));
		leftText.setText(p.getString("KEY_LEFT"));
		rightText.setText(p.getString("KEY_RIGHT"));
	}

	private void apply() {
		// general tab
		{
			// encoding
			String encoding = encodingCombo.getText().trim();
			if (!encoding.equals(AppSettings.fileEncoding)) {
				AppSettings.fileEncoding = encoding;
			}

			// device
			String device = deviceCombo.getText().trim();
			if (!device.equals(AppSettings.devicePreset)) {
				AppSettings.devicePreset = device;
				// TODO
			}

			// platform
			String platform = platformText.getText().trim();
			if (!platform.equals(AppSettings.microeditionPlatform)) {
				AppSettings.microeditionPlatform = platform;
			}

			// locale
			String locale = localeText.getText().trim();
			if (!locale.equals(AppSettings.locale)) {
				AppSettings.locale = locale;
			}
		}

		// screen size
		{
			int width = 0;
			try {
				width = Integer.parseInt(screenWidthText.getText().trim());
			} catch (Exception ignored) {}
			if (width > 0 && width != AppSettings.screenWidth) {
				AppSettings.screenWidth = width;
			}

			int height = 0;
			try {
				height = Integer.parseInt(screenHeightText.getText().trim());
			} catch (Exception ignored) {}
			if (height > 0 && height != AppSettings.screenHeight) {
				AppSettings.screenHeight = height;
			}
		}

		// key codes
		{
			int leftSoft = 0;
			try {
				leftSoft = Integer.parseInt(leftSoftText.getText().trim());
			} catch (Exception ignored) {}
			if (leftSoft != 0) {
				AppSettings.leftSoftKey = leftSoft;
			}

			int rightSoft = 0;
			try {
				rightSoft = Integer.parseInt(rightSoftText.getText().trim());
			} catch (Exception ignored) {}
			if (rightSoft != 0) {
				AppSettings.rightSoftKey = rightSoft;
			}

			int up = 0;
			try {
				up = Integer.parseInt(upText.getText().trim());
			} catch (Exception ignored) {}
			if (up != 0) {
				AppSettings.upKey = up;
			}

			int down = 0;
			try {
				down = Integer.parseInt(downText.getText().trim());
			} catch (Exception ignored) {}
			if (down != 0) {
				AppSettings.downKey = down;
			}

			int left = 0;
			try {
				left = Integer.parseInt(leftText.getText().trim());
			} catch (Exception ignored) {}
			if (left != 0) {
				AppSettings.leftKey = left;
			}

			int right = 0;
			try {
				right = Integer.parseInt(rightText.getText().trim());
			} catch (Exception ignored) {}
			if (right != 0) {
				AppSettings.rightKey = right;
			}
		}

		// font
		{
			AppSettings.fontLargeSize = largeSizeSpinner.getSelection();
			AppSettings.fontMediumSize = mediumSizeSpinner.getSelection();
			AppSettings.fontSmallSize = smallSizeSpinner.getSelection();
		}

		// tweaks tab

		// capabilities
		{
			AppSettings.enableKeyRepeat = keyRepeatsCheck.getSelection();
			AppSettings.hasPointerEvents = pointerEventsCheck.getSelection();
		}

		// tweaks
		{
			AppSettings.ignoreFullScreen = ignoreFullScreenCheck.getSelection();
			AppSettings.j2lStyleFpsLimit = j2lStyleFpsLimitCheck.getSelection();
			AppSettings.motorolaSoftKeyFix = motorolaSoftKeyFixCheck.getSelection();
			AppSettings.keyPressOnRepeat = keyPressOnRepeatCheck.getSelection();
			AppSettings.synchronizeKeyEvents = synchronizeKeyEventsCheck.getSelection();
			AppSettings.asyncFlush = asyncFlushCheck.getSelection();
			AppSettings.startAppOnResume = startAppOnResumeCheck.getSelection();
		}

		// 3d tab

		// m3g
		{
			AppSettings.m3gIgnoreOverwrite = m3gIgnoreOverwriteCheck.getSelection();
			AppSettings.m3gForcePerspectiveCorrection = m3gForcePersCorrectCheck.getSelection();
			AppSettings.m3gDisableLightClamp = m3gDisableLightClampCheck.getSelection();
			AppSettings.m3gFlushImmediately = m3gFlushImmediately.getSelection();
		}
		{
			AppSettings.m3gAA = Math.max(0, m3gAACombo.getSelectionIndex());
			AppSettings.m3gTexFilter = Math.max(0, m3gTexFilterCombo.getSelectionIndex());
			AppSettings.m3gMipmapping = Math.max(0, m3gMipmapCombo.getSelectionIndex());
		}

		// mascot
		{
			AppSettings.mascotNo2DMixing = mascotNo2DMixingCheck.getSelection();
			AppSettings.mascotIgnoreBackground = mascotIgnoreBgCheck.getSelection();
			AppSettings.mascotTextureFilter = mascotTextureFilterCheck.getSelection();
			AppSettings.mascotBackgroundFilter = mascotBackgroundFilterCheck.getSelection();
		}

		// system properties
		{
			String sysProps = systemPropertiesText.getText();
			AppSettings.systemProperties.clear();
			if (!sysProps.isEmpty()) {
				String[] a = sysProps.split("\n");
				for (String s : a) {
					if ((s = s.trim()).isEmpty()) continue;
					int i = s.indexOf(':');
					if (i == -1) continue;
					String k = s.substring(0, i).trim();
					String v = s.substring(i + 1).trim();
					AppSettings.systemProperties.put(k, v);
				}
			}
		}

		AppSettings.save();
		KeyMapping.init();
	}
}
