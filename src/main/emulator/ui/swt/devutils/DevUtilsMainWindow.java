package emulator.ui.swt.devutils;

import emulator.Emulator;
import emulator.ui.swt.devutils.idea.IdeaUtilsWindows;
import emulator.ui.swt.devutils.idea.IdeaUtilsXdgLinux;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DevUtilsMainWindow implements DisposeListener, SelectionListener {

	private Button eclipse;
	private Button idea;
	private final Shell shell;
	private final Shell parent;

	public DevUtilsMainWindow(Shell parent) {
		shell = new Shell(parent, SWT.MAX | SWT.FOREGROUND | SWT.TITLE | SWT.MENU | SWT.MIN);
		this.parent = parent;
		shell.setText("Developer utils");
		shell.addDisposeListener(this);
		shell.setMinimumSize(450, 200);
		shell.setSize(500, 300);

		if (Emulator.macos) {
			errorState("Mac OS is not supported now.", "You will have to setup everything manually, sorry.");
			return;
		}
		if (Emulator.isJava9()) {
			errorState("KEmulator is launched with java " + System.getProperty("java.version") + ".", "Please install JDK 1.8 and run KEmulator with it.");
			return;
		}

		if (Emulator.getJdkHome() == null) {
			errorState("KEmulator is launched with JRE without JDK.", "Please install JDK 1.8 (not JRE!) and run KEmulator with it.");
			return;
		}

		if (!Files.exists(Paths.get(Emulator.getAbsolutePath(), "uei", "cldc11.jar"))) {
			errorState("UEI directory is missing in your KEmulator setup.", "It contains libraries for your IDE. Find a proper KEmulator package.");
			return;
		}

		shell.setLayout(new GridLayout(2, true));

		Label welcomeLabel = new Label(shell, 0);
		welcomeLabel.setAlignment(SWT.CENTER);
		welcomeLabel.setText("Welcome to developer utils menu!\n\n" +
				"The tools here will help you to start developing your own MIDlets.\n" +
				"Please choose an IDE you are going to work with.");
		GridData gd1 = new GridData();
		gd1.horizontalAlignment = GridData.FILL;
		gd1.grabExcessHorizontalSpace = true;
		gd1.grabExcessVerticalSpace = true;
		gd1.horizontalSpan = 2;
		welcomeLabel.setLayoutData(gd1);

		GridData gd2 = new GridData();
		gd2.horizontalAlignment = GridData.FILL;
		gd2.verticalAlignment = GridData.CENTER;
		Label temp = new Label(shell, 0);
		temp.setText("Eclipse JDT + MTJ (not supported yet)");
		temp.setLayoutData(gd2);
		temp.setAlignment(SWT.CENTER);
//		eclipse = new Button(shell, SWT.FLAT);
//		eclipse.setText("Eclipse JDT + MTJ");
//		eclipse.setLayoutData(gd2);
//		eclipse.addSelectionListener(this);

		GridData gd3 = new GridData();
		gd3.horizontalAlignment = GridData.FILL;
		gd3.verticalAlignment = GridData.FILL;
		idea = new Button(shell, SWT.FLAT);
		idea.setText("Intellij IDEA");
		idea.setLayoutData(gd3);
		idea.addSelectionListener(this);
	}

	private void errorState(String line1, String line2) {
		shell.setLayout(new GridLayout(1, true));
		Label l1 = new Label(shell, 0);
		l1.setAlignment(SWT.CENTER);
		l1.setText(line1);
		GridData gd1 = new GridData();
		gd1.horizontalAlignment = GridData.FILL;
		gd1.grabExcessHorizontalSpace = true;
		l1.setLayoutData(gd1);

		Label l2 = new Label(shell, 0);
		l2.setAlignment(SWT.CENTER);
		l2.setText(line2);
		GridData gd2 = new GridData();
		gd2.horizontalAlignment = GridData.FILL;
		gd2.grabExcessHorizontalSpace = true;
		l2.setLayoutData(gd2);
	}

	public void open() {
		shell.open();
	}

	public void widgetDisposed(DisposeEvent e) {

	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.widget == eclipse) {
			Emulator.getEmulator().getScreen().showMessage("WIP");
			return;
		}
		if (e.widget == idea) {
			if (Emulator.linux)
				new IdeaUtilsXdgLinux(parent).open();
			else
				new IdeaUtilsWindows(parent).open();
			shell.dispose();
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}
}
