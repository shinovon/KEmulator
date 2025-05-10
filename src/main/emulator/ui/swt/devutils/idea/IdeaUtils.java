package emulator.ui.swt.devutils.idea;

import emulator.Emulator;
import emulator.Settings;
import emulator.ui.swt.devutils.JavaTypeValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

public abstract class IdeaUtils implements DisposeListener, SelectionListener {

	// state
	private boolean didInstallation = false;
	private String jdkTablePath = null;
	private boolean alreadyPatched = false;

	// view
	private final Shell shell;
	private Button chooseIdeaManuallyBtn;
	private Button refreshInstalledListBtn;
	private Button nnchanProguardBtn;
	private Button githubProguardBtn;
	private Button selectProguardBtn;
	private Link ideaDownloadLink;
	private Button jmeDocsBtn;
	private Button selectJmeDocsBtn;
	private Button autoConfigBtn;
	private Button manualConfigBtn;
	private Button skipPatchBtn;
	private Button doPatchBtn;
	private Button chooseEclipse;
	private Text projectName;
	private Text midletClassName;
	private Text midletName;
	private Text reposPath;
	private Button chooseProjectsPath;
	private Button createProject;
	private Button fixClonedBtn;

	private StyledText log;

	public static final String proguardUrl = "https://nnproject.cc/dl/d/proguard.zip";
	private Button proguardAutoBtn;

	public IdeaUtils(Shell parent) {
		shell = new Shell(parent, SWT.MAX | SWT.FOREGROUND | SWT.TITLE | SWT.MENU | SWT.MIN);
		shell.setText("Developer utils: Intellij IDEA");
		shell.addDisposeListener(this);
		shell.setMinimumSize(450, 500);
		shell.setSize(450, 500);

		GridLayout layout = new GridLayout(1, true);
		shell.setLayout(layout);
	}

	private GridData genGd() {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		return gd;
	}

	private RowLayout genGLo() {
		RowLayout l = new RowLayout(SWT.VERTICAL);
		l.spacing = 5;
		return l;
	}


	public void open() {
		refreshContent();
		shell.open();
	}

	private void refreshContent() {
		for (Control c : shell.getChildren())
			c.dispose();

		if (Settings.ideaPath == null) {
			// IDEA installation
			Group findGroup = new Group(shell, SWT.NONE);
			findGroup.setText("Find installed IDE");
			findGroup.setLayout(genGLo());
			findGroup.setLayoutData(genGd());

			new Label(findGroup, SWT.NONE).setText("Please choose an IDEA installation you are going to work with.");

			Set<String> existing = getIdeaInstallationPath();
			if (existing.isEmpty()) {
				new Label(findGroup, SWT.NONE).setText("Failed to automatically find any installed IDEAs!");
			} else {
				for (String installation : existing) {
					new Label(findGroup, 0).setText(installation);
					Button b = new Button(findGroup, SWT.FLAT);
					b.setText("Use this");
					b.setData(installation);
					b.addSelectionListener(this);
				}
			}
			chooseIdeaManuallyBtn = new Button(findGroup, SWT.PUSH);
			chooseIdeaManuallyBtn.setText("Choose manually");
			chooseIdeaManuallyBtn.addSelectionListener(this);

			Group installGroup = new Group(shell, SWT.NONE);
			installGroup.setText("Install");
			installGroup.setLayout(genGLo());
			installGroup.setLayoutData(genGd());

			if (didInstallation) {
				new Label(installGroup, SWT.NONE).setText("If it didn't show up in list above:");
				new Label(installGroup, SWT.NONE).setText("Make sure you created desktop shortcut or added launcher to $PATH.");
				refreshInstalledListBtn = new Button(installGroup, SWT.PUSH);
				refreshInstalledListBtn.setText("Refresh again");
				refreshInstalledListBtn.addSelectionListener(this);
			} else {
				new Label(installGroup, SWT.NONE).setText("If you don't have one installed:");
				ideaDownloadLink = new Link(installGroup, SWT.NONE);
				ideaDownloadLink.setText("1. Go to <a>download page</a>, grab a release and install it.");
				ideaDownloadLink.addSelectionListener(this);
				new Label(installGroup, SWT.NONE).setText("2. Launch installed IDE, activate a license and create desktop shortcut.");
				new Label(installGroup, SWT.NONE).setText("3. Do not touch any settings related to JDK. Close IDE and return here.");

				refreshInstalledListBtn = new Button(installGroup, SWT.PUSH);
				refreshInstalledListBtn.setText("I did it, refresh list of installed IDEs");
				refreshInstalledListBtn.addSelectionListener(this);
			}

			shell.layout(true, true);
			return;
		}

		if (Settings.proguardPath == null) {
			// proguard installation

			new Label(shell, SWT.NONE).setText("ProGuard is needed for packaging MIDlets into JARs.");
			Group pgGroup = new Group(shell, SWT.NONE);
			pgGroup.setText("Manual setup");
			pgGroup.setLayout(genGLo());
			pgGroup.setLayoutData(genGd());
			nnchanProguardBtn = new Button(pgGroup, SWT.PUSH);
			nnchanProguardBtn.setText("Download v6.2.2 from nnproject archive");
			nnchanProguardBtn.addSelectionListener(this);
			githubProguardBtn = new Button(pgGroup, SWT.PUSH);
			githubProguardBtn.setText("Download from project's github");
			githubProguardBtn.addSelectionListener(this);

			new Label(pgGroup, SWT.NONE).setText("Unpack the archive where you want and select main program file:");
			selectProguardBtn = new Button(pgGroup, SWT.PUSH);
			selectProguardBtn.setText("Choose proguard JAR");
			selectProguardBtn.addSelectionListener(this);

			if (this instanceof IdeaUtilsXdgLinux) {
				Group autoGroup = new Group(shell, SWT.NONE);
				autoGroup.setText("Auto setup");
				autoGroup.setLayout(genGLo());
				autoGroup.setLayoutData(genGd());

				proguardAutoBtn = new Button(autoGroup, SWT.PUSH);
				proguardAutoBtn.setText("Do it");
				proguardAutoBtn.addSelectionListener(this);

				new Label(autoGroup, SWT.NONE).setText("Warning: XDG/GNU/Linux expected.");
				new Label(autoGroup, SWT.NONE).setText("Will be used: wget, bash, unzip, install, pkexec.");
			}

			shell.layout(true, true);
			return;
		}

		if (Settings.j2meDocsPath == null) {
			// docs installation
			Group jmeDocsGroup = new Group(shell, SWT.NONE);
			jmeDocsGroup.setText("Documentation setup");
			jmeDocsGroup.setLayout(genGLo());
			jmeDocsGroup.setLayoutData(genGd());

			new Label(jmeDocsGroup, SWT.NONE).setText("Download nikita36068's documentation archive - will be used as javadocs.");
			jmeDocsBtn = new Button(jmeDocsGroup, SWT.PUSH);
			jmeDocsBtn.setText("Open repository");
			jmeDocsBtn.addSelectionListener(this);
			new Label(jmeDocsGroup, SWT.NONE).setText("Select location where you placed them.");
			selectJmeDocsBtn = new Button(jmeDocsGroup, SWT.PUSH);
			selectJmeDocsBtn.setText("Choose documentation root");
			selectJmeDocsBtn.addSelectionListener(this);

			shell.layout(true, true);
			return;
		}

		if (!Settings.ideaJdkTablePatched) {
			// custom jdks
			Group jvmSetupGroup = new Group(shell, SWT.NONE);
			jvmSetupGroup.setText("JVM setup");
			jvmSetupGroup.setLayout(genGLo());
			jvmSetupGroup.setLayoutData(genGd());
			if (jdkTablePath == null) {
				new Label(jvmSetupGroup, SWT.NONE).setText("CLDC/MIDP projects need specific JDK setup in IDEA.");
				new Label(jvmSetupGroup, SWT.NONE).setText("This will be done automatically.");
				try {
					String path = getDefaultJdkTablePath(); // may fail
					autoConfigBtn = new Button(jvmSetupGroup, SWT.PUSH);
					autoConfigBtn.setText("Edit \"" + path + "\"");
					autoConfigBtn.addSelectionListener(this);
				} catch (IOException e) {
					new Label(jvmSetupGroup, SWT.NONE).setText("Failed to automatically find config folder!");
				}
				new Label(jvmSetupGroup, SWT.NONE).setText("If you use non-standard setup:");
				new Label(jvmSetupGroup, SWT.NONE).setText("1. Find config folder that your IDEA version uses.");
				new Label(jvmSetupGroup, SWT.NONE).setText("2. Navigate to \"options\" subfolder and choose \"jdk.table.xml\" file.");

				manualConfigBtn = new Button(jvmSetupGroup, SWT.PUSH);
				manualConfigBtn.setText("Choose the file");
				manualConfigBtn.addSelectionListener(this);
			} else {
				if (alreadyPatched) {
					new Label(jvmSetupGroup, SWT.NONE).setText("Your JDK table is already set up.");
					new Label(jvmSetupGroup, SWT.NONE).setText("If you want to re-generate the entries,");
					new Label(jvmSetupGroup, SWT.NONE).setText("remove them in IDEA settings GUI and come again.");

					skipPatchBtn = new Button(jvmSetupGroup, SWT.PUSH);
					skipPatchBtn.setText("Continue without doing anything");
					skipPatchBtn.addSelectionListener(this);
				} else {
					new Label(jvmSetupGroup, SWT.NONE).setText("Config file:");
					new Label(jvmSetupGroup, SWT.NONE).setText(jdkTablePath);

					doPatchBtn = new Button(jvmSetupGroup, SWT.PUSH);
					doPatchBtn.setText("Patch the table");
					doPatchBtn.addSelectionListener(this);

					new Label(jvmSetupGroup, SWT.NONE).setText("Note: this will write to config paths to:");
					new Label(jvmSetupGroup, SWT.NONE).setText("1. Running KEmulator");
					new Label(jvmSetupGroup, SWT.NONE).setText("2. JRE, used to run this KEmulator");
					new Label(jvmSetupGroup, SWT.NONE).setText("3. Javadocs that you installed in previous step");
					new Label(jvmSetupGroup, SWT.NONE).setText("You should not relocate them after the setup, or things will break.");
				}
			}

			shell.layout(true, true);
			return;
		}

		// project creation

		Group fixupIdeaClonedProject = new Group(shell, SWT.NONE);
		fixupIdeaClonedProject.setText("Fix a project after git clone");
		fixupIdeaClonedProject.setLayout(genGLo());
		fixupIdeaClonedProject.setLayoutData(genGd());

		new Label(fixupIdeaClonedProject, SWT.NONE).setText("Project configuration keeps absolute paths specific to each machine.");
		new Label(fixupIdeaClonedProject, SWT.NONE).setText("Affected files are ignored by git. This button will recreate them.");
		fixClonedBtn = new Button(fixupIdeaClonedProject, SWT.FLAT);
		fixClonedBtn.setText("Choose a project");
		fixClonedBtn.addSelectionListener(this);
		new Label(fixupIdeaClonedProject, SWT.NONE).setText("Warning: project structure generated by this toolkit is expected only!");

		Group createNewProject = new Group(shell, SWT.NONE);
		createNewProject.setText("Create new project");
		createNewProject.setLayout(genGLo());
		createNewProject.setLayoutData(genGd());

		new Label(createNewProject, SWT.NONE).setText("Hover fields to see tooltips with details.");

		projectName = new Text(createNewProject, 2048);
		projectName.setMessage("Project name");
		projectName.setToolTipText("Name of folder, JAR file, various configurations. Only ASCII letters, numbers, hyphen and underscore allowed.\nExample: \"mahocart_midp\"");
		projectName.setLayoutData(new RowData(400, SWT.DEFAULT));

		midletClassName = new Text(createNewProject, 2048);
		midletClassName.setMessage("MIDlet class name");
		midletClassName.setToolTipText("Full name of your MIDlet class. Must be valid java type name.\nExample: \"ru.symansel.mahocart.MahoCartMIDlet\"");
		midletClassName.setLayoutData(new RowData(400, SWT.DEFAULT));

		midletName = new Text(createNewProject, 2048);
		midletName.setMessage("Readable MIDlet name");
		midletName.setToolTipText("Name of your MIDlet, shown to user. No restrictions applied.\nExample: \"МАХОКАРТ\uD83D\uDEA8\uD83D\uDEA8\uD83D\uDEA8\"");
		midletName.setLayoutData(new RowData(400, SWT.DEFAULT));

		reposPath = new Text(createNewProject, 2048);
		reposPath.setMessage("Project location");
		reposPath.setToolTipText("Location of folder where the project's folder will be created.\nExample: \"" + (Emulator.linux ? "/mnt/projects/" : "D:\\projects\\") + "\"");
		reposPath.setLayoutData(new RowData(400, SWT.DEFAULT));

		chooseProjectsPath = new Button(createNewProject, SWT.FLAT);
		chooseProjectsPath.setText("Choose location via file explorer");
		chooseProjectsPath.addSelectionListener(this);
		createProject = new Button(createNewProject, SWT.FLAT);
		createProject.setText("Create project");
		createProject.addSelectionListener(this);

		Group convertEclipseProject = new Group(shell, SWT.NONE);
		convertEclipseProject.setText("Convert Eclipse MTJ project");
		convertEclipseProject.setLayout(genGLo());
		convertEclipseProject.setLayoutData(genGd());

		chooseEclipse = new Button(convertEclipseProject, SWT.FLAT);
		chooseEclipse.setText("Choose existing project");
		chooseEclipse.addSelectionListener(this);

		new Label(convertEclipseProject, SWT.NONE).setText("Warning: this works only for projects with default configuration.");
		new Label(convertEclipseProject, SWT.NONE).setText("Warning: run config will be created only for first MIDlet.");

		shell.layout(true, true);
	}

	private void makeLogWindow() {
		for (Control c : shell.getChildren())
			c.dispose();

		log = new StyledText(shell, SWT.V_SCROLL | SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		log.setLayoutData(gd);
		shell.layout(true, true);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.widget == ideaDownloadLink) {
			Emulator.openUrlExternallySilent("https://www.jetbrains.com/idea/download/");
		} else if (e.widget == refreshInstalledListBtn) {
			didInstallation = true;
			refreshContent();
		} else if (e.widget == chooseIdeaManuallyBtn) {
			chooseIdeaLauncherManually();
		} else if (e.widget == githubProguardBtn) {
			Emulator.openUrlExternallySilent("https://github.com/Guardsquare/proguard/releases/");
		} else if (e.widget == nnchanProguardBtn) {
			Emulator.openUrlExternallySilent(proguardUrl);
		} else if (e.widget == proguardAutoBtn) {
			makeLogWindow();
			new Thread(() -> {
				try {
					Settings.proguardPath = autoInstallProguard();
					shell.getDisplay().syncExec(this::refreshContent);
				} catch (Exception ex) {
					shell.getDisplay().syncExec(() -> errorMsg("Failed to install proguard", ex.getMessage()));
					shell.getDisplay().syncExec(this::refreshContent);
				}
			}).start();
		} else if (e.widget == selectProguardBtn) {
			FileDialog fd = new FileDialog(shell, SWT.OPEN);
			fd.setText("Choose ProGuard JAR binary (\"path/proguard/lib/proguard.jar\")");
			fd.setFilterExtensions(new String[]{"proguard.jar"});
			String path = fd.open();
			if (path == null) return;
			Settings.proguardPath = path;
			refreshContent();
		} else if (e.widget == jmeDocsBtn) {
			Emulator.openUrlExternallySilent("https://github.com/nikita36078/J2ME_Docs/");
		} else if (e.widget == selectJmeDocsBtn) {
			DirectoryDialog dd = new DirectoryDialog(shell, SWT.OPEN);
			dd.setText("Choose \"docs\" folder");
			dd.setFilterPath("docs");
			String path = dd.open();
			if (path == null) return;
			if (Files.exists(Paths.get(path, "midp-2.0", "javax", "microedition", "midlet", "MIDlet.html"))) {
				Settings.j2meDocsPath = path;
				refreshContent();
				return;
			}
			errorMsg("Documentation location", "Failed to find documentation file for \"MIDlet\" class. You are expected to choose \"docs\" folder, it contains subfolders for each jsr/api.");
		} else if (e.widget == autoConfigBtn) {
			try {
				String path = getDefaultJdkTablePath() + "/options/jdk.table.xml";
				alreadyPatched = JdkTablePatcher.checkJdkTable(path);
				jdkTablePath = path;
				refreshContent();
			} catch (Exception ex) {
				errorMsg("Config location", "Failed to check config table. Logs may help you.\n\nException: " + ex);
			}
		} else if (e.widget == manualConfigBtn) {
			FileDialog fd = new FileDialog(shell, SWT.OPEN);
			fd.setText("Choose IDEA JDK table (\"IntelliJIdeaXXXX.Y/options/jdk.table.xml\")");
			fd.setFilterExtensions(new String[]{"jdk.table.xml"});
			String path = fd.open();
			if (path == null) return;
			try {
				alreadyPatched = JdkTablePatcher.checkJdkTable(path);
				jdkTablePath = path;
				refreshContent();
			} catch (Exception ex) {
				errorMsg("Config location", "Failed to check config table. Logs may help you.\n\nException: " + ex);
			}
		} else if (e.widget == skipPatchBtn) {
			Settings.ideaJdkTablePatched = true;
			refreshContent();
		} else if (e.widget == doPatchBtn) {
			patchJdkTable();
		} else if (e.widget == chooseProjectsPath) {
			DirectoryDialog dd = new DirectoryDialog(shell, SWT.OPEN);
			dd.setText("Choose folder where you store your projects");
			String path = dd.open();
			if (path != null)
				reposPath.setText(path);
		} else if (e.widget == createProject) {
			createProject();
		} else if (e.widget == fixClonedBtn) {
			restoreProject();
		} else if (e.widget == chooseEclipse) {
			convertProject();
		} else if (e.widget instanceof Button && e.widget.getData() instanceof String) {
			// idea path
			Settings.ideaPath = e.widget.getData().toString();
			refreshContent();
		}
	}

	private void patchJdkTable() {
		try {
			JdkTablePatcher.updateJdkTable(jdkTablePath);
			Settings.ideaJdkTablePatched = true;
			refreshContent();
		} catch (Exception ex) {
			errorMsg("Config patch", "Failed to modify config table. Logs may help you.\n\nException: " + ex);
		}
	}

	private void createProject() {
		String repoName = projectName.getText();
		String className = midletClassName.getText();
		String appName = midletName.getText();
		String location = reposPath.getText();

		if (repoName == null || repoName.isEmpty()) {
			errorMsg("Project creation", "Project name must not be empty.");
			return;
		}
		if (!isValidRepoName(repoName)) {
			errorMsg("Project creation", "Project name must meet certain restrictions, check tooltip on the field.");
			return;
		}
		if (className == null || className.isEmpty()) {
			errorMsg("Project creation", "Class name must not be empty.");
			return;
		}
		if (!JavaTypeValidator.isValidJavaTypeName(className)) {
			errorMsg("Project creation", "Class name must be a valid Java class name.");
			return;
		}
		if (appName == null || appName.isEmpty()) {
			errorMsg("Project creation", "MIDlet name must not be empty.");
			return;
		}
		if (location.endsWith("/") || location.endsWith("\\"))
			location = location.substring(0, location.length() - 1);
		if (!Files.exists(Paths.get(location))) {
			errorMsg("Project creation", "Location for project doesn't exist.");
			return;
		}
		if (Files.exists(Paths.get(location, repoName))) {
			errorMsg("Project creation", "Folder with the specified name already exists. Do something with it.");
			return;
		}
		try {
			String code = ProjectGenerator.create(location, repoName, className, appName);
			Runtime.getRuntime().exec(new String[]{Settings.ideaPath, Paths.get(location, repoName).toString(), code});
		} catch (Exception ex) {
			ex.printStackTrace();
			errorMsg("Project creation", "Failed to generate project: " + ex.getMessage());
		}
	}

	private void restoreProject() {
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText("Choose IDEA project file");
		fd.setFilterExtensions(new String[]{"*.iml"});
		String path = fd.open();
		if (path == null) return;
		try {
			String dir = ProjectGenerator.fixCloned(path);
			Runtime.getRuntime().exec(new String[]{Settings.ideaPath, dir});
		} catch (Exception ex) {
			ex.printStackTrace();
			errorMsg("Project restore", "Failed to generate project: " + ex.getMessage());
		}
	}

	private void convertProject() {
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText("Choose Eclipse application descriptor");
		fd.setFilterExtensions(new String[]{"Application Descriptor"});
		String path = fd.open();
		if (path == null) return;
		try {
			String dir = ProjectGenerator.convertEclipse(path);
			Runtime.getRuntime().exec(new String[]{Settings.ideaPath, dir});
			MessageBox mb = new MessageBox(this.shell, SWT.ICON_INFORMATION | SWT.OK);
			mb.setText("Project conversion");
			mb.setMessage("Your project was converted and will open in IDEA in few seconds. There are some important notes:\n\n" +
					"- You can still use Eclipse JDT/MTJ to work on the project.\n" +
					"- IDEA and Eclipse use different files for MIDlet suite manifest. Synchronize them by hand.\n" +
					"- IDEA provides bad support for global package classes. Get rid of them.\n" +
					"- Artifact building will provide you non-pre-verified JAR, it's not ready to be run on real devices.\n" +
					"- To get a JAR ready for deployment, use \"Package\" run configuration. The file will be \"./deployed/PROJNAME_release.jar\".\n" +
					"- Pre-verification is done via ProGuard as part of optimization/obfuscation pass. Edit ProGuard config manually to skip it.");
			mb.open();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof IllegalArgumentException)
				errorMsg("Project conversion", "Failed to convert project: " + ex.getMessage());
			else
				errorMsg("Project conversion", "Failed to convert project: " + ex);
		}
	}

	private void chooseIdeaLauncherManually() {
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText("Choose IDEA binary (\"idea\", \"idea.exe\" or \"idea.sh\")");
		if (Emulator.win)
			fd.setFilterExtensions(new String[]{"idea.exe"});
		else if (Emulator.linux)
			fd.setFilterExtensions(new String[]{"idea", "idea*.sh"});
		String path = fd.open();
		if (path == null) return;
		Settings.ideaPath = path;
		refreshContent();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
	}

	protected abstract Set<String> getIdeaInstallationPath();

	protected abstract String getDefaultJdkTablePath() throws IOException;

	protected abstract String autoInstallProguard() throws IOException, InterruptedException;

	protected void appendLog(char c) {
		shell.getDisplay().asyncExec(() -> log.append(String.valueOf(c)));
	}

	protected void appendLog(String s) {
		shell.getDisplay().asyncExec(() -> log.append(s));
	}

	private static boolean isValidRepoName(String input) {
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (!((c >= 'A' && c <= 'Z')
					|| (c >= 'a' && c <= 'z')
					|| (c >= '0' && c <= '9')
					|| c == '-'
					|| c == '_')) {
				return false;
			}
		}

		return true;
	}

	private void errorMsg(String header, String text) {
		final MessageBox mb = new MessageBox(this.shell, SWT.ICON_ERROR | SWT.OK);
		mb.setText(header);
		mb.setMessage(text);
		mb.open();
	}
}
