package emulator.ui.swt.devutils.idea;

import emulator.Emulator;
import emulator.Settings;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class IdeaSetup implements DisposeListener, SelectionListener {

	public static final String JAVADOCS_DEFAULT_PATH = "/usr/share/doc/j2me";
	public static final String PROGUARD_URL = "https://nnproject.cc/dl/d/proguard.zip";
	public static final String JAVADOCS_URL = "https://github.com/nikita36078/J2ME_Docs/archive/refs/heads/master.zip";
	public static final String PROGUARD_DEFAULT_PATH_UNIX = "/opt/proguard6.2.2/proguard.jar";
	public static final String PROGUARD_AUR_PATH_UNIX = "/usr/share/proguard/proguard.jar";
	public static final String DEB_DEFAULT_JDK = "/usr/lib/jvm/java-8-openjdk";
	public static final String RPM_DEFAULT_JDK = "/usr/lib/jvm/java-1.8-openjdk";

	// state
	private boolean didInstallation = false;
	private boolean useOnlineDocs = false;
	private String localDocsPath;
	private String jdkHome = null;
	private String invalidJdkHome = null;

	// view
	private final Shell shell;
	private final Shell parent;
	private Button chooseIdeaManuallyBtn;
	private Button refreshInstalledListBtn;
	private Link nnchanProguardBtn;
	private Link githubProguardBtn;
	private Button selectProguardBtn;
	private Link ideaDownloadLink;
	private Link jmeDocsBtn;
	private Button selectJmeDocsBtn;
	private Button manualConfigBtn;
	private StyledText log;
	private Button proguardAutoBtn;
	private Button installDocsToUsr;
	private final Path proguardDefaultLocalPath = Paths.get(Emulator.getAbsolutePath(), "proguard.jar");
	private Button proguardAutoLocalBtn;
	private Button useOnlineDocsBtn;
	private Button selectJdkBtn;
	private Link oracleJdk;
	private Link pkgJdk;

	public IdeaSetup(Shell parent) {
		if (Settings.ideaJdkTablePatched)
			throw new RuntimeException("Attempt to run setup when not needed.");
		this.parent = parent;
		shell = new Shell(parent, SWT.MAX | SWT.FOREGROUND | SWT.TITLE | SWT.MENU | SWT.MIN);
		shell.setText("Intellij IDEA setup");
		shell.addDisposeListener(this);
		shell.setMinimumSize(450, 600);
		shell.setSize(450, 600);

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

		if (Settings.ideaJdkTablePatched) // this is an invalid state! successful patching must close the window.
			throw new RuntimeException("Attempt to run setup when not needed.");

		if (Emulator.macos) {
			new Label(shell, SWT.NONE).setText("Mac OS is not supported yet. Reach us to become a tester!");
			shell.layout(true, true);
			return;
		}

		if (!Files.exists(Paths.get(Emulator.getAbsolutePath()).resolve("uei"))) {
			new Label(shell, SWT.NONE).setText("UEI directory is missing in your KEmulator setup.");
			new Label(shell, SWT.NONE).setText("It contains libraries for your IDE.");
			new Label(shell, SWT.NONE).setText("Find a KEmulator distribution package with them.");
			shell.layout(true, true);
			return;
		}
		if (JdkTablePatcher.getDevTimeJars().isEmpty()) {
			new Label(shell, SWT.NONE).setText("No UEI libraries found.");
			new Label(shell, SWT.NONE).setText("Find a KEmulator distribution package with them.");
			shell.layout(true, true);
			return;
		}

		// proguard installation
		if (Settings.proguardPath == null) {
			// if proguard is already here, skip the setup.
			if (Files.exists(proguardDefaultLocalPath)) {
				Settings.proguardPath = proguardDefaultLocalPath.toString();
				refreshContent();
				return;
			} else if (this instanceof IdeaSetupXdgLinux) {
				if (Files.exists(Paths.get(PROGUARD_DEFAULT_PATH_UNIX))) {
					Settings.proguardPath = PROGUARD_DEFAULT_PATH_UNIX;
					refreshContent();
					return;
				}
				if (Files.exists(Paths.get(PROGUARD_AUR_PATH_UNIX))) {
					Settings.proguardPath = PROGUARD_AUR_PATH_UNIX;
					refreshContent();
					return;
				}
			}

			makeProguardForm();
			shell.layout(true, true);
			return;
		}

		// IDEA installation
		if (Settings.ideaPath == null) {
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
					new Label(findGroup, SWT.NONE).setText(" ");
					new Label(findGroup, 0).setText(installation);
					Button b = new Button(findGroup, SWT.FLAT);
					b.setText("Use this");
					b.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent selectionEvent) {
							Settings.ideaPath = installation;
							refreshContent();
						}
					});
				}
				new Label(findGroup, SWT.NONE).setText(" ");
			}
			chooseIdeaManuallyBtn = new Button(findGroup, SWT.PUSH);
			chooseIdeaManuallyBtn.setText("Choose manually");
			chooseIdeaManuallyBtn.addSelectionListener(this);

			Group installGroup = new Group(shell, SWT.NONE);
			installGroup.setText("Install");
			installGroup.setLayout(genGLo());
			installGroup.setLayoutData(genGd());

			if (didInstallation) {
				new Label(installGroup, SWT.NONE).setText("If it didn't appear in list, make sure you created launch menu entry.");
				refreshInstalledListBtn = new Button(installGroup, SWT.PUSH);
				refreshInstalledListBtn.setText("Refresh again");
				refreshInstalledListBtn.addSelectionListener(this);
			} else {
				new Label(installGroup, SWT.NONE).setText("If you don't have one installed:");
				ideaDownloadLink = new Link(installGroup, SWT.NONE);
				ideaDownloadLink.setText("1. Go to <a>download page</a>, grab a release and install it.");
				ideaDownloadLink.addSelectionListener(this);
				if (this instanceof IdeaSetupXdgLinux)
					new Label(installGroup, SWT.NONE).setText("2. Launch installed IDE.");
				else
					new Label(installGroup, SWT.NONE).setText("2. Launch installed IDE and create desktop shortcut.");
				new Label(installGroup, SWT.NONE).setText("3. Do NOT touch any settings related to JDKs. Close IDE and return here.");
				if (this instanceof IdeaSetupXdgLinux) {
					refreshInstalledListBtn = new Button(installGroup, SWT.PUSH);
					refreshInstalledListBtn.setText("I did it, refresh list of installed IDEs");
					refreshInstalledListBtn.addSelectionListener(this);
				} else {
					new Label(installGroup, SWT.NONE).setText("4. Select Intellij binary via button above.");
				}
			}

			shell.layout(true, true);
			return;
		}

		// user may live with null docs path, when using online ones.
		if (localDocsPath == null && !useOnlineDocs) {
			try {
				Path path = Paths.get(Emulator.getAbsolutePath(), "uei", "javadocs");
				checkDocsPathValid(path);
				localDocsPath = path.toAbsolutePath().toString();
				// skipping the screen entirely if there are builtin javadocs
				refreshContent();
				return;
			} catch (Exception ignored) {
			}
			if (this instanceof IdeaSetupXdgLinux) {
				try {
					checkDocsPathValid(Paths.get(JAVADOCS_DEFAULT_PATH));
					localDocsPath = Paths.get(JAVADOCS_DEFAULT_PATH).toAbsolutePath().toString();
					// skipping the screen entirely if there are installed javadocs
					refreshContent();
					return;
				} catch (Exception ignored) {
				}
			}

			new Label(shell, SWT.NONE).setText("UEI libs contain no javadocs. You need an external source of them.");

			// docs installation
			Group jmeDocsGroup = new Group(shell, SWT.NONE);
			jmeDocsGroup.setText("External javadocs: manual setup");
			jmeDocsGroup.setLayout(genGLo());
			jmeDocsGroup.setLayoutData(genGd());

			new Label(jmeDocsGroup, SWT.NONE).setText("1. Download them from one of the sources, or take from SDK.");
			jmeDocsBtn = new Link(jmeDocsGroup, SWT.NONE);
			jmeDocsBtn.setText("<a>Nikita36068's repository</a>");
			jmeDocsBtn.addSelectionListener(this);
			new Label(jmeDocsGroup, SWT.NONE).setText("2. Select location where you placed them:");
			selectJmeDocsBtn = new Button(jmeDocsGroup, SWT.PUSH);
			selectJmeDocsBtn.setText("Choose documentation root");
			selectJmeDocsBtn.addSelectionListener(this);

			Group autoGroup = new Group(shell, SWT.NONE);
			autoGroup.setText("External javadocs: auto setup");
			autoGroup.setLayout(genGLo());
			autoGroup.setLayoutData(genGd());
			if (this instanceof IdeaSetupXdgLinux) {
				installDocsToUsr = new Button(autoGroup, SWT.PUSH);
				installDocsToUsr.setText("Install to /usr/");
				installDocsToUsr.addSelectionListener(this);
				new Label(autoGroup, SWT.NONE).setText("Required tools: wget, unzip, rm, cp, pkexec.");

			} else {
				new Label(autoGroup, SWT.NONE).setText("Not available.");
			}

			Group onlineGroup = new Group(shell, SWT.NONE);
			onlineGroup.setText("Online javadocs");
			onlineGroup.setLayout(genGLo());
			onlineGroup.setLayoutData(genGd());

			useOnlineDocsBtn = new Button(onlineGroup, SWT.PUSH);
			useOnlineDocsBtn.setText("Use nikita36068's repo as web docs");
			useOnlineDocsBtn.addSelectionListener(this);
			new Label(onlineGroup, SWT.NONE).setText("Not all docs will be connected, only the most important ones.");

			shell.layout(true, true);
			return;
		}

		// JDK 1.8 for use in IDE
		if (jdkHome == null) {
			if (Emulator.isJava9()) {
				new Label(shell, SWT.NONE).setText("KEmulator is launched with java " + System.getProperty("java.version") + ".");
				new Label(shell, SWT.NONE).setText("Please install JDK 1.8 and select it.");
				new Label(shell, SWT.NONE).setText("You can run KEmulator with it for auto setup.");
			} else {
				String _jdkHome = Emulator.getJdkHome();
				if (_jdkHome != null) {
					jdkHome = _jdkHome;
					refreshContent();
					return;
				}
				new Label(shell, SWT.NONE).setText("KEmulator is launched with JRE without JDK.");
				new Label(shell, SWT.NONE).setText("Please install JDK 1.8 and select it.");
				new Label(shell, SWT.NONE).setText("You can run KEmulator with it for auto setup.");
			}

			// in linux it's expected at /usr/lib/jvm, if it's not there we can't do much.
			if (this instanceof IdeaSetupXdgLinux) {
				if (is8(DEB_DEFAULT_JDK)) {
					jdkHome = DEB_DEFAULT_JDK;
					refreshContent();
					return;
				}
				if (is8(RPM_DEFAULT_JDK)) {
					jdkHome = RPM_DEFAULT_JDK;
					refreshContent();
					return;
				}
			}

			if (this instanceof IdeaSetupWindows) {
				Path jdkLocs = Emulator.isX64() ? Paths.get("C:\\Program Files\\Java") : Paths.get("C:\\Program Files (x86)\\Java");
				for (File f : jdkLocs.toFile().listFiles()) {
					String path = f.getAbsolutePath();
					if (!path.toLowerCase().contains("jdk"))
						continue;
					if (is8(path)) {
						jdkHome = path;
						refreshContent();
						return;
					}
				}
			}

			if (this instanceof IdeaSetupWindows) {
				Group jdkSetupGroup = new Group(shell, SWT.NONE);
				jdkSetupGroup.setText("JDKs from PATH");
				jdkSetupGroup.setLayout(genGLo());
				jdkSetupGroup.setLayoutData(genGd());

				String[] paths = System.getenv("PATH").split(";");
				boolean found = false;
				for (String path : paths) {
					Path ppath = Paths.get(path);
					if (Files.exists(ppath.resolve("java.exe")) && Files.exists(ppath.resolve("javac.exe"))) {
						try {
							if (Emulator.getProcessOutput(new String[]{ppath.resolve("java.exe").toAbsolutePath().toString(), "-version"}, true).contains("1.8.")) {
								found = true;
								Button b = new Button(jdkSetupGroup, SWT.FLAT);
								b.setText(path);
								b.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent selectionEvent) {
										jdkHome = ppath.getParent().toAbsolutePath().toString();
										refreshContent();
									}
								});
							}
						} catch (IOException e) {
							// ignore
						}
					}
				}
				if (!found) {
					new Label(jdkSetupGroup, SWT.NONE).setText("Nothing found.");
				}
			}

			Group manualJdkSetupGroup = new Group(shell, SWT.NONE);
			manualJdkSetupGroup.setText("Manual selection");
			manualJdkSetupGroup.setLayout(genGLo());
			manualJdkSetupGroup.setLayoutData(genGd());

			selectJdkBtn = new Button(manualJdkSetupGroup, SWT.PUSH);
			selectJdkBtn.setText("Choose JDK home");
			selectJdkBtn.addSelectionListener(this);

			if (invalidJdkHome != null) {
				Link ignoreVerMismatch = new Link(manualJdkSetupGroup, SWT.NONE);
				ignoreVerMismatch.setText("<a>Ignore inappropriate version of last selected JDK and use it</a>");
				ignoreVerMismatch.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent selectionEvent) {
						jdkHome = invalidJdkHome;
						invalidJdkHome = null;
						refreshContent();
					}
				});
				new Label(manualJdkSetupGroup, SWT.NONE).setText("This will cause issues, make sure to fix your config later.");
			}

			oracleJdk = new Link(shell, SWT.NONE);
			oracleJdk.setText("<a>Download from Oracle</a>");
			oracleJdk.addSelectionListener(this);

			if (this instanceof IdeaSetupXdgLinux) {
				pkgJdk = new Link(shell, SWT.NONE);
				pkgJdk.setText("<a>Search how to install a JDK from your repository</a>");
				pkgJdk.addSelectionListener(this);
			}

			shell.layout(true, true);
			return;
		}

		// JDK table patch

		new Label(shell, SWT.NONE).setText("Select configuration folder that your IDEA uses:");
		new Label(shell, SWT.NONE).setText(" ");

		IdeaDataFolder defaultFolderTemp = null;
		try {
			List<IdeaDataFolder> folders = getIdeaDataFolders();
			for (IdeaDataFolder folder : folders) {
				if (folder.isDefault) {
					defaultFolderTemp = folder;
					break;
				}
			}

			if (folders.isEmpty()) {
				new Label(shell, SWT.NONE).setText("No config folders found!");
			} else {

				final IdeaDataFolder defaultFolder = defaultFolderTemp;

				if (defaultFolder != null) {
					Group defaultGroup = new Group(shell, SWT.NONE);
					defaultGroup.setText("The most relevant");
					defaultGroup.setLayout(genGLo());
					defaultGroup.setLayoutData(genGd());

					new Label(defaultGroup, SWT.NONE).setText("If you didn't touch IDEA's paths, this is yours.");

					Button autoConfigBtn = new Button(defaultGroup, SWT.PUSH);
					autoConfigBtn.setText("Choose \"" + defaultFolder.path.toAbsolutePath() + "\"");
					autoConfigBtn.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent selectionEvent) {
							String table = defaultFolder.path.resolve("options").resolve("jdk.table.xml").toString();
							patchJdkTable(table);
						}
					});
				}


				if (folders.size() != ((defaultFolder == null) ? 0 : 1)) {
					Group othersGroup = new Group(shell, SWT.NONE);
					othersGroup.setText("Other folders");
					othersGroup.setLayout(genGLo());
					othersGroup.setLayoutData(genGd());
					for (IdeaDataFolder folder : folders) {
						if (folder.isDefault)
							continue;
						Link autoConfigBtn = new Link(othersGroup, SWT.PUSH);
						autoConfigBtn.setText("<a>Choose</a> \"" + folder.path.toAbsolutePath() + "\"");
						autoConfigBtn.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent selectionEvent) {
								String table = folder.path.resolve("options").resolve("jdk.table.xml").toString();
								patchJdkTable(table);
							}
						});
					}
				}
			}

		} catch (IOException e) {
			new Label(shell, SWT.NONE).setText("Failed to automatically find config folder!");
			errorMsg("Failed to automatically find config folder!", e.toString());
		}

		Group manualGroup = new Group(shell, SWT.NONE);
		manualGroup.setText("Non-standard setup");
		manualGroup.setLayout(genGLo());
		manualGroup.setLayoutData(genGd());
		new Label(manualGroup, SWT.NONE).setText("1. Find config folder that your IDEA version uses.");
		new Label(manualGroup, SWT.NONE).setText("2. Navigate to \"options\" subfolder and choose \"jdk.table.xml\" file.");

		manualConfigBtn = new Button(manualGroup, SWT.PUSH);
		manualConfigBtn.setText("Choose arbitrary config XML");
		manualConfigBtn.addSelectionListener(this);


		shell.layout(true, true);
		return;

	}

	private void makeProguardForm() {
		new Label(shell, SWT.NONE).setText("ProGuard is needed for packaging MIDlets into JARs.");
		Group pgGroup = new Group(shell, SWT.NONE);
		pgGroup.setText("Manual setup");
		pgGroup.setLayout(genGLo());
		pgGroup.setLayoutData(genGd());
		new Label(pgGroup, SWT.NONE).setText("1. Download it from one of the sources.");
		nnchanProguardBtn = new Link(pgGroup, SWT.NONE);
		nnchanProguardBtn.setText("<a>v6.2.2 from NNP archive</a> (recommended)");
		nnchanProguardBtn.addSelectionListener(this);
		githubProguardBtn = new Link(pgGroup, SWT.NONE);
		githubProguardBtn.setText("<a>Project's github</a>");
		githubProguardBtn.addSelectionListener(this);

		new Label(pgGroup, SWT.NONE).setText("2. Unpack the archive where you want and select main program file:");
		selectProguardBtn = new Button(pgGroup, SWT.PUSH);
		selectProguardBtn.setText("Choose proguard JAR");
		selectProguardBtn.addSelectionListener(this);

		Group autoGroup = new Group(shell, SWT.NONE);
		autoGroup.setText("Auto setup");
		autoGroup.setLayout(genGLo());
		autoGroup.setLayoutData(genGd());

		boolean autoSetupAvailable = false;

		if (this instanceof IdeaSetupXdgLinux) {
			proguardAutoBtn = new Button(autoGroup, SWT.PUSH);
			proguardAutoBtn.setText("Install to /opt/");
			proguardAutoBtn.addSelectionListener(this);
			new Label(autoGroup, SWT.NONE).setText("Required tools: wget, rm, bash, unzip, install, pkexec.");
			autoSetupAvailable = true;
		}

		if (Emulator.isPortable) {
			proguardAutoLocalBtn = new Button(autoGroup, SWT.PUSH);
			proguardAutoLocalBtn.setText("Install to KEmulator folder");
			proguardAutoLocalBtn.addSelectionListener(this);
			autoSetupAvailable = true;
		}

		if (!autoSetupAvailable)
			new Label(autoGroup, SWT.NONE).setText("Not available in your configuration.");
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
			Emulator.openUrlExternallySilent(PROGUARD_URL);
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
		} else if (e.widget == proguardAutoLocalBtn) {
			makeLogWindow();
			new Thread(() -> {
				try {
					autoInstallProguardLocally();
					Settings.proguardPath = proguardDefaultLocalPath.toString();
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
			try {
				checkDocsPathValid(Paths.get(path));
				localDocsPath = path;
				Settings.ideaJdkTablePatched = false;
				refreshContent();
			} catch (IOException ex) {
				errorMsg("Documentation location", "Failed to find documentation file for \"MIDlet\" class. You are expected to choose \"docs\" folder, it contains subfolders for each jsr/api.");
			}
		} else if (e.widget == installDocsToUsr) {
			makeLogWindow();
			new Thread(() -> {
				try {
					localDocsPath = autoInstallDocs();
					Settings.ideaJdkTablePatched = false;
					shell.getDisplay().syncExec(this::refreshContent);
				} catch (Exception ex) {
					shell.getDisplay().syncExec(() -> errorMsg("Failed to install javadocs", ex.getMessage()));
					shell.getDisplay().syncExec(this::refreshContent);
				}
			}).start();
		} else if (e.widget == useOnlineDocsBtn) {
			useOnlineDocs = true;
			Settings.ideaJdkTablePatched = false;
			refreshContent();
		} else if (e.widget == manualConfigBtn) {
			FileDialog fd = new FileDialog(shell, SWT.OPEN);
			fd.setText("Choose IDEA JDK table (\"IntelliJIdeaXXXX.Y/options/jdk.table.xml\")");
			fd.setFilterExtensions(new String[]{"jdk.table.xml"});
			String path = fd.open();
			if (path == null) return;
			patchJdkTable(path);
		} else if (e.widget == selectJdkBtn) {
			DirectoryDialog dd = new DirectoryDialog(shell, SWT.OPEN);
			dd.setText("Choose JDK home folder");
			String path = dd.open();
			if (path == null) return;
			Path ppath = Paths.get(path);
			if (Files.exists(ppath.resolve("bin").resolve(Emulator.win ? "java.exe" : "java")) &&
					Files.exists(ppath.resolve("bin").resolve(Emulator.win ? "javac.exe" : "javac"))) {
				try {
					String javaVer = Emulator.getProcessOutput(new String[]{ppath.resolve("bin").resolve(Emulator.win ? "java.exe" : "java").toAbsolutePath().toString(), "-version"}, true);
					if (javaVer.contains("1.8.")) {
						jdkHome = path;
						refreshContent();
					} else {
						errorMsg("JDK home", "Selected JDK says it's not 1.8.\n\n" + javaVer);
						invalidJdkHome = path;
						refreshContent();
					}
				} catch (IOException ex) {
					errorMsg("JDK home", "Failed to run java binary.");
				}
			} else {
				errorMsg("JDK home", "Selected folder is not JDK home. Select a folder with \"bin\", \"jre\", \"lib\" subfolders.");
			}
		} else if (e.widget == oracleJdk) {
			Emulator.openUrlExternallySilent("https://www.oracle.com/java/technologies/javase/javase8u211-later-archive-downloads.html");
		} else if (e.widget == pkgJdk) {
			try {
				String distroName = Emulator.getProcessOutput(new String[]{"/usr/bin/bash", "-c", "source /etc/os-release && echo $NAME"}, false).trim();
				Emulator.openUrlExternallySilent("https://duckduckgo.com/?q=" + distroName.replace(" ", "+") + "+openjdk+1.8+package");
			} catch (IOException ex) {
				errorMsg("Failed to get distro name", ex.toString());
			}
		}
	}

	private static void checkDocsPathValid(Path path) throws IOException, RuntimeException {
		try (Stream<Path> list = Files.list(path)) {
			long found = list.filter(p -> Files.exists(p.resolve("javax").resolve("microedition").resolve("midlet").resolve("MIDlet.html"))).count();
			if (found == 0)
				throw new RuntimeException();
		}
	}

	private void patchJdkTable(String jdkTablePath) {
		Path lockFile = Paths.get(jdkTablePath).getParent().getParent().resolve(".lock");
		if (Files.exists(lockFile)) {
			final MessageBox mb = new MessageBox(this.shell, SWT.ICON_ERROR | SWT.RETRY | SWT.CANCEL);
			mb.setText("Config location");
			mb.setMessage("Your IntelliJ IDEA is running. Close it to continue, or else things will break. If you are absolutely sure that it is closed, choose \"Retry\" to continue.");
			if (mb.open() == SWT.CANCEL) {
				refreshContent();
				return;
			}
		}
		try {
			JdkTablePatcher.updateJdkTable(jdkTablePath, useOnlineDocs ? null : localDocsPath, jdkHome);
			String repairer = Paths.get(Settings.ideaPath).getParent().resolve(Emulator.win ? "repair.exe" : "repair").toAbsolutePath().toString();
			try {
				Runtime.getRuntime().exec(new String[]{repairer, "caches", "--clear"}).waitFor();
			} catch (IOException ex) {
				// there is no repair util.
				String defaultFolderName = getDefaultIdeaDataFolderName();
				if (defaultFolderName != null) {
					// try to wipe cache manually
					try {
						if (Emulator.win) {
							Path path = Paths.get(System.getenv("LOCALAPPDATA") + "\\JetBrains\\" + defaultFolderName);
							if (Files.exists(path)) {
								deleteRecursive(path.toFile());
							} else {
								throw new RuntimeException();
							}
						}
						if (Emulator.linux) {
							Path path = Paths.get(System.getenv("HOME") + "/.cache/JetBrains/" + defaultFolderName);
							if (Files.exists(path)) {
								Runtime.getRuntime().exec(new String[]{"/usr/bin/rm", "-rf", path.toAbsolutePath().toString()}).waitFor();
							} else {
								throw new RuntimeException();
							}
						}
					} catch (Exception ignored) {
						errorMsg("Failed to clear caches", "If your IDE behaves wrong, try do that via \"File > Invalidate caches...\" menu there. Everything else is done successfully, you may get to work.");
					}
				}
			}
			Settings.ideaJdkTablePatched = true;
		} catch (Exception ex) {
			errorMsg("Config patch", "Failed to modify config table. Logs may help you.\n\nException: " + ex);
			refreshContent();
			return;
		}
		shell.close();
		shell.dispose();
		IdeaUtils.open(parent);

	}

	private void chooseIdeaLauncherManually() {
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText("Choose IDEA binary (\"idea\", \"idea.exe\" or \"idea.sh\")");
		if (Emulator.win)
			fd.setFilterExtensions(new String[]{"idea*.exe", "idea*.bat"});
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

	protected abstract Path getJetBrainsDataRoot();

	protected abstract String autoInstallProguard() throws IOException, InterruptedException;

	protected abstract String autoInstallDocs() throws IOException, InterruptedException;

	protected void autoInstallProguardLocally() throws IOException {
		if (!Emulator.isPortable)
			throw new UnsupportedOperationException();

		Path tempZip;
		if (this instanceof IdeaSetupWindows) {
			Path appData = Paths.get(System.getenv("APPDATA")).toAbsolutePath();
			Path tmp = appData.getParent().resolve("Local").resolve("Temp");
			tempZip = Files.createTempFile(tmp, "proguard", ".zip").toAbsolutePath();
		} else {
			tempZip = Files.createTempFile("proguard", ".zip").toAbsolutePath();
		}
		try {
			appendLog("Downloading manually, wait...\n");
			try (InputStream in = new URL(PROGUARD_URL).openStream()) {
				Files.copy(in, tempZip, StandardCopyOption.REPLACE_EXISTING);
			}
			appendLog("Installing...\n");

			ZipFile zipFile = new ZipFile(tempZip.toString());
			ZipEntry entry = zipFile.getEntry("proguard6.2.2/lib/proguard.jar");

			if (entry == null) throw new IOException("Broken archive");

			try (InputStream entryStream = zipFile.getInputStream(entry)) {
				Files.copy(entryStream, proguardDefaultLocalPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} finally {
			try {
				Files.deleteIfExists(tempZip);
			} catch (IOException ignored) {
				// winapi moment
			}
		}
	}

	protected void appendLog(char c) {
		shell.getDisplay().asyncExec(() -> log.append(String.valueOf(c)));
	}

	protected void appendLog(String s) {
		shell.getDisplay().asyncExec(() -> log.append(s));
	}

	private void errorMsg(String header, String text) {
		final MessageBox mb = new MessageBox(this.shell, SWT.ICON_ERROR | SWT.OK);
		mb.setText(header);
		mb.setMessage(text);
		mb.open();
	}

	protected static String getDefaultIdeaDataFolderName() throws IOException {
		Path infoPath;
		if (Settings.ideaPath.equals("/usr/bin/idea"))
			infoPath = Paths.get("/usr/share/idea/product-info.json");
		else
			infoPath = Paths.get(Settings.ideaPath).getParent().getParent().resolve("product-info.json");
		String content = String.join("", Files.readAllLines(infoPath));

		Pattern pattern = Pattern.compile("\"dataDirectoryName\"\\s*:\\s*\"([^\"]+)\"");
		Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	protected static boolean is8(String jdkHome) {
		Path bin = Paths.get(jdkHome).resolve("bin").resolve(Emulator.win ? "java.exe" : "java").toAbsolutePath();
		if (!Files.exists(bin))
			return false;
		try {
			return Emulator.getProcessOutput(new String[]{bin.toString(), "-version"}, true).contains("1.8.");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void deleteRecursive(File f) {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				deleteRecursive(c);
		}
		f.delete();
	}

	protected List<IdeaDataFolder> getIdeaDataFolders() throws IOException {
		final String defaultName = getDefaultIdeaDataFolderName();

		try (Stream<Path> list = Files.list(getJetBrainsDataRoot())) {
			return list.filter(p -> {
				String name = p.getFileName().toString();
				if (name.toLowerCase().contains("idea"))
					return true;
				if (defaultName == null)
					return false;
				return name.equalsIgnoreCase(defaultName);
			}).map(p -> new IdeaDataFolder(p, p.getFileName().toString().equalsIgnoreCase(defaultName))).collect(Collectors.toList());
		}
	}

	protected static class IdeaDataFolder {
		public final Path path;

		public final boolean isDefault;

		public IdeaDataFolder(Path path, boolean isDefault) {
			this.path = path;
			this.isDefault = isDefault;
		}

	}

}
