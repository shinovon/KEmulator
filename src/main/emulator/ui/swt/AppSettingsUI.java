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

	public AppSettingsUI() {
	}

	public void open(Display display, Shell parent, boolean start) {
		this.start = start;
		action = 0;
		if (shell == null || shell.isDisposed()) {
			createContents(display);
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
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	protected void createContents(Display display) {
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.RESIZE);
		shell.setLayout(new GridLayout(1, false));
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				if (action == 0 && start) {
					CustomMethod.close();
					System.exit(0);
				}
			}
		});

		shell.setText("Setup");
		shell.setSize(389, 427);
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
		lblNewLabel.setText("Name: " + Emulator.getEmulator().getAppProperty("MIDlet-Name"));

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
		} catch (Exception e) {}

		Canvas canvas = new Canvas(composite_2, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (iconImage != null) {
					iconImage.copyToScreen(e.gc, 0, 0, iconImage.getWidth(), iconImage.getHeight(), 0, 0, e.width, e.height);
				}
			}
		});

		Label lblNewLabel_1 = new Label(composite_1, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNewLabel_1.setText("Vendor: " + Emulator.getEmulator().getAppProperty("MIDlet-Vendor"));

		Label lblNewLabel_2 = new Label(composite_1, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNewLabel_2.setText("Version: " + Emulator.getEmulator().getAppProperty("MIDlet-Version"));

		CTabFolder tabFolder = new CTabFolder(composite, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		CTabItem tbtmGeneral = new CTabItem(tabFolder, SWT.NONE);
		tbtmGeneral.setText("General");

		Composite composite_5 = new Composite(tabFolder, SWT.NONE);
		tbtmGeneral.setControl(composite_5);
		composite_5.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite grpDevice = new Composite(composite_5, SWT.NONE);
		grpDevice.setLayout(new GridLayout(4, false));

		Label lblNewLabel_3 = new Label(grpDevice, SWT.NONE);
		lblNewLabel_3.setText("Device preset:");

		deviceCombo = new Combo(grpDevice, SWT.NONE);
		deviceCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				DevicePlatform p = Devices.getPlatform(deviceCombo.getText().trim());
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
		});
		deviceCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		{
			final Enumeration method620 = Devices.method620();
			while (method620.hasMoreElements()) {
				final String text = (String) method620.nextElement();
				this.deviceCombo.add(text);
				if (AppSettings.devicePreset.equalsIgnoreCase(text)) {
					this.deviceCombo.setText(text);
				}
			}
		}

		Label lblNewLabel_7 = new Label(grpDevice, SWT.NONE);
		lblNewLabel_7.setText("Platform:");

		platformText = new Text(grpDevice, SWT.BORDER);
		platformText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		platformText.setText(AppSettings.microeditionPlatform);

		Label lblNewLabel_8 = new Label(grpDevice, SWT.NONE);
		lblNewLabel_8.setText("Encoding:");

		encodingCombo = new Combo(grpDevice, SWT.NONE);
		encodingCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
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
		lblNewLabel_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_9.setText("Locale:");

		localeText = new Text(grpDevice, SWT.BORDER);
		localeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		localeText.setText(AppSettings.locale);

		Label lblNewLabel_4 = new Label(grpDevice, SWT.NONE);
		lblNewLabel_4.setText("Screen size:");

		Composite composite_4 = new Composite(grpDevice, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		GridLayout gl_composite_4 = new GridLayout(3, false);
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
				String tmp = leftSoftText.getText();
				leftSoftText.setText(rightSoftText.getText());
				rightSoftText.setText(tmp);
			}
		});
		btnNewButton_2.setText("Swap");

		fpsLabel = new Label(grpDevice, SWT.NONE);
		fpsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fpsLabel.setText(UILocale.get("OPTION_CUSTOM_MAX_FPS", "Max FPS:") + " " + ((AppSettings.frameRate > 120) ? "\u221e" : String.valueOf(AppSettings.frameRate)));

		fpsScale = new Scale(grpDevice, SWT.NONE);
		fpsScale.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AppSettings.frameRate = fpsScale.getSelection();
				AppSettings.set("FrameRate", AppSettings.frameRate);
				fpsLabel.setText(UILocale.get("OPTION_CUSTOM_MAX_FPS", "Max FPS:") + " " + ((AppSettings.frameRate > 120) ? "\u221e" : String.valueOf(AppSettings.frameRate)));
			}
		});
		fpsScale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
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
		lblNewLabel_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_10.setText("Left soft key:");

		leftSoftText = new Text(grpKeyMapping, SWT.BORDER);
		leftSoftText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		leftSoftText.setText(Integer.toString(AppSettings.leftSoftKey));

		Label lblNewLabel_11 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_11.setText("Right soft key:");

		rightSoftText = new Text(grpKeyMapping, SWT.BORDER);
		rightSoftText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rightSoftText.setText(Integer.toString(AppSettings.rightSoftKey));

		Label lblNewLabel_12 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_12.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_12.setText("Fire/Middle:");

		fireText = new Text(grpKeyMapping, SWT.BORDER);
		fireText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fireText.setText(Integer.toString(AppSettings.fireKey));

		Label lblNewLabel_13 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_13.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_13.setText("Up:");

		upText = new Text(grpKeyMapping, SWT.BORDER);
		upText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		upText.setText(Integer.toString(AppSettings.upKey));

		Label lblNewLabel_15 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_15.setAlignment(SWT.RIGHT);
		lblNewLabel_15.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblNewLabel_15.setText("Down:");

		downText = new Text(grpKeyMapping, SWT.BORDER);
		downText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		downText.setText(Integer.toString(AppSettings.downKey));

		Label lblNewLabel_14 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_14.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_14.setText("Left:");

		leftText = new Text(grpKeyMapping, SWT.BORDER);
		leftText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		leftText.setText(Integer.toString(AppSettings.leftKey));

		Label lblNewLabel_16 = new Label(grpKeyMapping, SWT.NONE);
		lblNewLabel_16.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_16.setText("Right:");

		rightText = new Text(grpKeyMapping, SWT.BORDER);
		rightText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rightText.setText(Integer.toString(AppSettings.rightKey));
		new Label(grpKeyMapping, SWT.NONE);
		new Label(grpKeyMapping, SWT.NONE);

		CTabItem tbtmFont = new CTabItem(tabFolder, SWT.NONE);
		tbtmFont.setText("Font");

		Composite grpFont = new Composite(tabFolder, SWT.NONE);
		tbtmFont.setControl(grpFont);
		grpFont.setLayout(new GridLayout(2, false));

		Label lblNewLabel_5 = new Label(grpFont, SWT.NONE);
		lblNewLabel_5.setText("Large size:");

		largeSizeSpinner = new Spinner(grpFont, SWT.BORDER);
		largeSizeSpinner.setSelection(AppSettings.fontLargeSize);

		Label lblNewLabel_5_1 = new Label(grpFont, SWT.NONE);
		lblNewLabel_5_1.setText("Medium size:");

		mediumSizeSpinner = new Spinner(grpFont, SWT.BORDER);
		mediumSizeSpinner.setSelection(AppSettings.fontMediumSize);

		Label lblNewLabel_5_1_1 = new Label(grpFont, SWT.NONE);
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

		Button btnCheckButton_4 = new Button(grpCanvasCapabilities, SWT.CHECK);
		btnCheckButton_4.setText("Key repeats support");

		Button btnCheckButton_5 = new Button(grpCanvasCapabilities, SWT.CHECK);
		btnCheckButton_5.setText("Pointer events support");

		Group grpTweaks = new Group(composite_9, SWT.NONE);
		grpTweaks.setText("Tweaks");
		grpTweaks.setLayout(new GridLayout(1, false));
		grpTweaks.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnCheckButton_10 = new Button(grpTweaks, SWT.CHECK);
		btnCheckButton_10.setText("Ignore Canvas.setFullScreenMode");

		Button btnCheckButton_11 = new Button(grpTweaks, SWT.CHECK);
		btnCheckButton_11.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}
		});
		btnCheckButton_11.setText("J2ME Loader style FPS limiter");

		Button btnCheckButton_6 = new Button(grpTweaks, SWT.CHECK);
		btnCheckButton_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}
		});
		btnCheckButton_6.setText("Send key events with commandAction");

		Button btnCheckButton_7 = new Button(grpTweaks, SWT.CHECK);
		btnCheckButton_7.setText("Send keyPressed on repeats");

		Button btnCheckButton_9 = new Button(grpTweaks, SWT.CHECK);
		btnCheckButton_9.setText("Synchronize key events");

		Button btnCheckButton_8 = new Button(grpTweaks, SWT.CHECK);
		btnCheckButton_8.setText("Asynchronous canvas flush");
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

		Button btnCheckButton = new Button(grpMgLwjgl, SWT.CHECK);
		btnCheckButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnCheckButton.setText("Ignore OVERWRITE hint");

		Button btnCheckButton_1 = new Button(grpMgLwjgl, SWT.CHECK);
		btnCheckButton_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnCheckButton_1.setText("Force perspective correction");

		Button btnCheckButton_2 = new Button(grpMgLwjgl, SWT.CHECK);
		btnCheckButton_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnCheckButton_2.setText("Disable light clamping");

		Button btnCheckButton_3 = new Button(grpMgLwjgl, SWT.CHECK);
		btnCheckButton_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnCheckButton_3.setText("Flush contents immediately (slow!)");

		Label lblNewLabel_17 = new Label(grpMgLwjgl, SWT.NONE);
		lblNewLabel_17.setText("Anti-aliasing:");

		Combo combo_2 = new Combo(grpMgLwjgl, SWT.NONE);
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_18 = new Label(grpMgLwjgl, SWT.NONE);
		lblNewLabel_18.setText("Texture filter:");

		Combo combo_3 = new Combo(grpMgLwjgl, SWT.NONE);
		combo_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_19 = new Label(grpMgLwjgl, SWT.NONE);
		lblNewLabel_19.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_19.setText("Mipmapping:");

		Combo combo_4 = new Combo(grpMgLwjgl, SWT.NONE);
		combo_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Group grpMascotcapsuleLwjgl = new Group(composite_8, SWT.NONE);
		grpMascotcapsuleLwjgl.setLayout(new GridLayout(1, false));
		grpMascotcapsuleLwjgl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpMascotcapsuleLwjgl.setText("MascotCapsule LWJGL");

		Button btnCheckButton_12 = new Button(grpMascotcapsuleLwjgl, SWT.CHECK);
		btnCheckButton_12.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}
		});
		btnCheckButton_12.setText("No 2D mixing");

		Button btnIgnoreBackground = new Button(grpMascotcapsuleLwjgl, SWT.CHECK);
		btnIgnoreBackground.setText("Ignore background");

		Button btnCheckButton_13 = new Button(grpMascotcapsuleLwjgl, SWT.CHECK);
		btnCheckButton_13.setText("Texture filter");

		Button btnBackgroundFilter = new Button(grpMascotcapsuleLwjgl, SWT.CHECK);
		btnBackgroundFilter.setText("Background filter");
		scrolledComposite_1.setContent(composite_8);
		scrolledComposite_1.setMinSize(composite_8.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		CTabItem tbtmProperties = new CTabItem(tabFolder, SWT.NONE);
		tbtmProperties.setText("Properties");

		systemPropertiesText = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		systemPropertiesText.setText("TODO");
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
				AppSettings.clear();
				shell.close();
			}
		});
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnNewButton.setText("Default");


		scrolledComposite_1.setMinHeight(composite_8.computeSize(-1, -1).y);
		scrolledComposite_2.setMinHeight(composite_9.computeSize(-1, -1).y);
	}
	
	private void apply() {
		// encoding
		String encoding = encodingCombo.getText().trim();
		if (!encoding.equals(AppSettings.fileEncoding)) {
			AppSettings.fileEncoding = encoding;
			AppSettings.set("FileEncoding", encoding);
		}

		// device
		String device = deviceCombo.getText().trim();
		if (!device.equals(AppSettings.devicePreset)) {
			AppSettings.devicePreset = device;
			AppSettings.set("DevicePreset", device);
			// TODO
		}

		// platform
		String platform = platformText.getText().trim();
		if (!platform.equals(AppSettings.microeditionPlatform)) {
			AppSettings.microeditionPlatform = platform;
			AppSettings.set("MIDPPlatform", platform);
		}

		// locale
		String locale = localeText.getText().trim();
		if (!locale.equals(AppSettings.locale)) {
			AppSettings.locale = locale;
			AppSettings.set("MIDPLocale", locale);
		}

		// screen size
		
		int width = 0;
		try {
			width = Integer.parseInt(screenWidthText.getText().trim());
		} catch (Exception ignored) {}
		if (width > 0 && width != AppSettings.screenWidth) {
			AppSettings.screenWidth = width;
			AppSettings.set("ScreenWidth", width);
		}
		
		int height = 0;
		try {
			height = Integer.parseInt(screenHeightText.getText().trim());
		} catch (Exception ignored) {}
		if (height > 0 && height != AppSettings.screenHeight) {
			AppSettings.screenHeight = height;
			AppSettings.set("ScreenHeight", height);
		}

		// key codes

		int leftSoft = 0;
		try {
			leftSoft = Integer.parseInt(leftSoftText.getText().trim());
		} catch (Exception ignored) {}
		if (leftSoft != 0 && leftSoft != AppSettings.leftSoftKey) {
			AppSettings.leftSoftKey = leftSoft;
			AppSettings.set("KeyLeftSoft", leftSoft);
		}

		int rightSoft = 0;
		try {
			rightSoft = Integer.parseInt(rightSoftText.getText().trim());
		} catch (Exception ignored) {}
		if (rightSoft != 0 && rightSoft != AppSettings.rightSoftKey) {
			AppSettings.rightSoftKey = rightSoft;
			AppSettings.set("KeyRightSoft", rightSoft);
		}

		int up = 0;
		try {
			up = Integer.parseInt(upText.getText().trim());
		} catch (Exception ignored) {}
		if (up != 0 && up != AppSettings.upKey) {
			AppSettings.upKey = up;
			AppSettings.set("KeyUp", up);
		}

		int down = 0;
		try {
			down = Integer.parseInt(downText.getText().trim());
		}  catch (Exception ignored) {}
		if (down != 0 && down != AppSettings.downKey) {
			AppSettings.downKey = down;
			AppSettings.set("KeyDown", down);
		}

		int left = 0;
		try {
			left = Integer.parseInt(leftText.getText().trim());
		} catch (Exception ignored) {}
		if (left != 0 && left != AppSettings.leftKey) {
			AppSettings.leftKey = left;
			AppSettings.set("KeyLeft", left);
		}

		int right = 0;
		try {
			right = Integer.parseInt(rightText.getText().trim());
		} catch (Exception ignored) {}
		if (right != 0 && right != AppSettings.rightKey) {
			AppSettings.rightKey = right;
			AppSettings.set("KeyRight", right);
		}

		// font

		int large = largeSizeSpinner.getSelection();
		if (large > 0 && large != AppSettings.fontLargeSize) {
			AppSettings.fontLargeSize = large;
			AppSettings.set("FontLargeSize", large);
		}

		int medium = mediumSizeSpinner.getSelection();
		if (medium > 0 && medium != AppSettings.fontMediumSize) {
			AppSettings.fontMediumSize = medium;
			AppSettings.set("FontMediumSize", medium);
		}

		int small = smallSizeSpinner.getSelection();
		if (small > 0 && small != AppSettings.fontSmallSize) {
			AppSettings.fontSmallSize = small;
			AppSettings.set("FontSmallSize", small);
		}
	}
}
