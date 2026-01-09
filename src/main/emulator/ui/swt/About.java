package emulator.ui.swt;

import emulator.Emulator;
import emulator.graphics2D.swt.ImageSWT;
import emulator.ui.effect.WaterEffect;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public final class About implements MouseListener, MouseMoveListener, PaintListener {
	private Shell aboutShell;
	private CLabel aboutText;
	private Link websiteLink;
	private Button okBtn;
	private Canvas waterCanvas;
	private WaterEffect waterEffect;
	private ImageSWT logoImage;
	private ImageSWT waterImage;
	private Timer animationTimer;
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
		aboutShell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				aboutShell.dispose();
				waterImage.finalize();
				logoImage.finalize();
			}
		});

		this.addIcon();

		(this.aboutText = new CLabel(this.aboutShell, 0)).setLayoutData(layoutData5);
		aboutText.setFont(EmulatorScreen.f);
		this.aboutText.setText(Emulator.getAboutString());

		(this.websiteLink = new Link(this.aboutShell, 0)).setText("<a>nnproject.cc/kem</a>");
		this.websiteLink.setLayoutData(gridData2);
		this.websiteLink.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Program.launch("https://nnproject.cc/kem");
			}
		});

		{
			final GridData layoutData3;
			(layoutData3 = new GridData()).horizontalAlignment = 3;
			layoutData3.grabExcessHorizontalSpace = false;
			layoutData3.verticalAlignment = GridData.END;

			(this.okBtn = new Button(this.aboutShell, 8388616)).setText(emulator.UILocale.get("DIALOG_OK", "OK"));
			this.okBtn.setLayoutData(layoutData3);
//			layoutData3.heightHint = 20;
			layoutData3.widthHint = 100;
			this.okBtn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					aboutShell.dispose();
				}
			});
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
		waterCanvas.addPaintListener(this);
		this.method455(Emulator.class.getResourceAsStream("/res/sign"));
	}

	private void method455(final InputStream inputStream) {
		try {
			this.logoImage = new ImageSWT(inputStream);
			this.waterImage = new ImageSWT(this.logoImage.getWidth(), this.logoImage.getHeight(), false, 6393563);
			this.logoImageData = this.logoImage.getData();
			this.waterImageData = this.waterImage.getData();
			(this.waterEffect = new WaterEffect()).initialize(this.logoImage.getWidth(), this.logoImage.getHeight());
			this.waterEffect.addDrop(this.logoImage.getWidth() >> 1, this.logoImage.getHeight() >> 1, 10, 500, this.waterEffect.currentBufferIndex);
			(this.animationTimer = new Timer()).schedule(new TimerTask() {
				public void run() {
					waterEffect.processFrame(logoImageData, waterImageData);
					SWTFrontend.getDisplay().syncExec(new Runnable() {
						public void run() {
							if (waterCanvas.isDisposed()) {
								animationTimer.cancel();
								return;
							}
							waterImage.setData(waterImageData);
							waterCanvas.redraw();
						}
					});
				}
			}, 0L, 30L);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public final void mouseDoubleClick(final MouseEvent mouseEvent) {
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		this.waterEffect.addDrop(mouseEvent.x, mouseEvent.y, 5, 500, this.waterEffect.currentBufferIndex);
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
	}

	public final void mouseMove(final MouseEvent mouseEvent) {
		this.waterEffect.addDrop(mouseEvent.x, mouseEvent.y, 5, 50, this.waterEffect.currentBufferIndex);
	}

	public void paintControl(PaintEvent e) {
		waterImage.method12(e.gc, 0, 0);
	}
}
