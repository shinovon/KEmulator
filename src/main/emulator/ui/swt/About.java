package emulator.ui.swt;

import emulator.Emulator;
import emulator.graphics2D.swt.ImageSWT;
import emulator.ui.effect.WaterEffect;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public final class About implements MouseListener, MouseMoveListener {
	private Shell aboutShell;
	private CLabel aboutText;
	private Link websiteLink;
	private Button okBtn;
	private Canvas waterCanvas;
	private WaterEffect ana811;
	private ImageSWT logoImage;
	private ImageSWT waterImage;
	private Timer animationTimer;
	GC canvasGC;
	int[] logoImageData;
	int[] waterImageData;

	public About() {
		super();
		this.aboutShell = null;
		this.aboutText = null;
		this.websiteLink = null;
		this.okBtn = null;
		this.waterCanvas = null;
	}

	private void createAboutShell(final Shell shell) {
		final GridData gridData;
		(gridData = new GridData()).horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
		final GridData layoutData5;
		(layoutData5 = new GridData()).horizontalAlignment = 4;
		layoutData5.grabExcessHorizontalSpace = true;
		layoutData5.grabExcessVerticalSpace = false;
		layoutData5.verticalAlignment = 4;
		layoutData5.verticalSpan = 1;
		final GridData gridData2;
		(gridData2 = new GridData()).horizontalIndent = 5;
		gridData2.horizontalAlignment = 4;
		gridData2.grabExcessHorizontalSpace = false;
		gridData2.grabExcessVerticalSpace = false;
		gridData2.verticalAlignment = 4;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 2;
		layout.horizontalSpacing = 0;

		(this.aboutShell = new Shell(shell, 67680)).setText("About KEmulator");
		this.aboutShell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.aboutShell.setLayout(layout);

		this.addIcon();

		(this.aboutText = new CLabel(this.aboutShell, 0)).setLayoutData(layoutData5);
		aboutText.setFont(EmulatorScreen.f);
		this.aboutText.setText(Emulator.getAboutString());

		(this.websiteLink = new Link(this.aboutShell, 0)).setText("<a>nnproject.cc/kem</a>");
		this.websiteLink.setLayoutData(gridData2);
		this.websiteLink.addSelectionListener(new Class158(this));

		{
			final GridData layoutData3;
			(layoutData3 = new GridData()).horizontalAlignment = 3;
			layoutData3.grabExcessHorizontalSpace = false;
			layoutData3.verticalAlignment = GridData.END;

			(this.okBtn = new Button(this.aboutShell, 8388616)).setText(emulator.UILocale.get("DIALOG_OK", "OK"));
			this.okBtn.setLayoutData(layoutData3);
//			layoutData3.heightHint = 20;
			layoutData3.widthHint = 100;
			this.okBtn.addSelectionListener(new Class163(this));
		}


		final GridData layoutData2 = new GridData();
		layoutData2.horizontalSpan = 2;
		layoutData2.horizontalAlignment = 4;
		layoutData2.grabExcessHorizontalSpace = true;
		layoutData2.grabExcessVerticalSpace = true;
		layoutData2.verticalAlignment = 4;

		TabFolder tabFolder = new TabFolder(aboutShell, SWT.TOP);
		{
			final GridData layoutData = new GridData();
			layoutData.horizontalIndent = 2;
			layoutData.horizontalSpan = 2;
			layoutData.horizontalAlignment = 4;
			layoutData.verticalAlignment = 4;
			layoutData.grabExcessHorizontalSpace = true;
			layoutData.grabExcessVerticalSpace = true;
			tabFolder.setLayoutData(layoutData);
		}

		Composite helpComposite = new Composite(tabFolder, SWT.NONE);
		StyledText helpText = new StyledText(helpComposite, 2562);
		{
			GridLayout gridLayout = new GridLayout();
			helpComposite.setLayout(gridLayout);

			final GridData layoutData4 = new GridData();
			layoutData4.horizontalAlignment = 4;
			layoutData4.grabExcessHorizontalSpace = true;
			layoutData4.grabExcessVerticalSpace = true;
			layoutData4.horizontalSpan = 2;
			layoutData4.verticalAlignment = 4;

			helpText.setLayoutData(layoutData4);
			helpText.setEditable(false);
			helpText.setIndent(5);
			helpText.setFont(EmulatorScreen.f);
		}

		TabItem logTab = new TabItem(tabFolder, SWT.NONE);
		logTab.setText("Changelog");
		logTab.setControl(helpComposite);

		Composite apisComposite = new Composite(tabFolder, SWT.NONE);
		StyledText apisText = new StyledText(apisComposite, 2562);
		{
			GridLayout gridLayout = new GridLayout();
			apisComposite.setLayout(gridLayout);

			final GridData layoutData4 = new GridData();
			layoutData4.horizontalAlignment = 4;
			layoutData4.grabExcessHorizontalSpace = true;
			layoutData4.grabExcessVerticalSpace = true;
			layoutData4.horizontalSpan = 2;
			layoutData4.verticalAlignment = 4;

			apisText.setLayoutData(layoutData4);
			apisText.setEditable(false);
			apisText.setIndent(5);
			apisText.setFont(EmulatorScreen.f);
		}

		TabItem apisTab = new TabItem(tabFolder, SWT.NONE);
		apisTab.setText("Supported APIs");
		apisTab.setControl(apisComposite);

		Composite creditsComposite = new Composite(tabFolder, SWT.NONE);
		StyledText creditsText = new StyledText(creditsComposite, 2562);
		{
			GridLayout gridLayout = new GridLayout();
			creditsComposite.setLayout(gridLayout);

			final GridData layoutData4 = new GridData();
			layoutData4.horizontalAlignment = 4;
			layoutData4.grabExcessHorizontalSpace = true;
			layoutData4.grabExcessVerticalSpace = true;
			layoutData4.horizontalSpan = 2;
			layoutData4.verticalAlignment = 4;

			creditsText.setLayoutData(layoutData4);
			creditsText.setEditable(false);
			creditsText.setIndent(5);
			creditsText.setFont(EmulatorScreen.f);
		}

		TabItem creditsTab = new TabItem(tabFolder, SWT.NONE);
		creditsTab.setText("Credits");
		creditsTab.setControl(creditsComposite);

		aboutShell.pack();
		aboutShell.setSize(new Point(400, 600));

		this.setText(helpText, "/res/help");
		this.setText(apisText, "/res/apis");
		this.setText(creditsText, "/res/credits");
	}

	private void setText(StyledText styledText, String res) {
		styledText.setText("");
		try {
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(res)));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				final int charCount = styledText.getCharCount();
				if (line.startsWith("$")) {
					styledText.append(line.substring(1) + "\n");
					styledText.setStyleRange(new StyleRange(charCount, line.length(), Display.getCurrent().getSystemColor(9), null));
				} else {
					styledText.append(line + "\n");
				}
			}
			bufferedReader.close();
		} catch (Exception ignored) {
		}
	}

	public final void method454(final Shell shell) {
		this.createAboutShell(shell);
		final Display display = shell.getDisplay();
		this.aboutShell.setLocation(shell.getLocation().x + (shell.getSize().x - this.aboutShell.getSize().x >> 1), shell.getLocation().y + (shell.getSize().y - this.aboutShell.getSize().y >> 1));
		this.aboutShell.open();
		while (!this.aboutShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void addIcon() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.verticalAlignment = 4;
		layoutData.verticalSpan = 3;
		layoutData.heightHint = 146;
		layoutData.widthHint = 156;
		(this.waterCanvas = new Canvas(this.aboutShell, 537133056)).setLayoutData(layoutData);
		this.waterCanvas.addMouseListener(this);
		this.waterCanvas.addMouseMoveListener(this);
		this.canvasGC = new GC(this.waterCanvas);
		this.method455(Emulator.class.getResourceAsStream("/res/sign"));
	}

	private void method455(final InputStream inputStream) {
		try {
			this.logoImage = new ImageSWT(inputStream);
			this.waterImage = new ImageSWT(this.logoImage.getWidth(), this.logoImage.getHeight(), false, 6393563);
			this.logoImageData = this.logoImage.getData();
			this.waterImageData = this.waterImage.getData();
			(this.ana811 = new WaterEffect()).initialize(this.logoImage.getWidth(), this.logoImage.getHeight());
			this.ana811.addDrop(this.logoImage.getWidth() >> 1, this.logoImage.getHeight() >> 1, 10, 500, this.ana811.currentBufferIndex);
			(this.animationTimer = new Timer()).schedule(new WaterTask(this), 0L, 30L);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public final void mouseDoubleClick(final MouseEvent mouseEvent) {
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		this.ana811.addDrop(mouseEvent.x, mouseEvent.y, 5, 500, this.ana811.currentBufferIndex);
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
	}

	public final void mouseMove(final MouseEvent mouseEvent) {
		this.ana811.addDrop(mouseEvent.x, mouseEvent.y, 5, 50, this.ana811.currentBufferIndex);
	}

	static Shell method456(final About class54) {
		return class54.aboutShell;
	}

	static WaterEffect method457(final About class54) {
		return class54.ana811;
	}

	static Canvas method458(final About class54) {
		return class54.waterCanvas;
	}

	static Timer method459(final About class54) {
		return class54.animationTimer;
	}

	static WaterEffect method460(final About class54, final WaterEffect ana811) {
		return class54.ana811 = ana811;
	}

	static ImageSWT method461(final About class54) {
		return class54.waterImage;
	}

	final static class WaterTask extends TimerTask {
		private final About waterTask;

		private WaterTask(final About waterTask) {
			super();
			this.waterTask = waterTask;
		}

		public final void run() {
			About.method457(this.waterTask).processFrame(this.waterTask.logoImageData, this.waterTask.waterImageData);
			//TODO DEOBFUSCATE ALL THIS MESS | 24.10.2025 in progress -klaxons1
			SWTFrontend.getDisplay().syncExec(new Water(this, waterTask.ana811));
		}

		WaterTask(final About class54, final Class158 class55) {
			this(class54);
		}

		static About method433(final WaterTask waterTask) {
			return waterTask.waterTask;
		}
	}

	public void finalize() {
		aboutShell.getDisplay().asyncExec(() -> {
			try {
				if (!canvasGC.isDisposed()) canvasGC.dispose();
			} catch (Exception ignored) {
			}
		});
	}
}
