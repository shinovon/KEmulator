package emulator.ui.swt.devutils.idea;

import emulator.Emulator;
import emulator.Settings;
import emulator.ui.swt.devutils.JavaTypeValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IdeaUtils implements SelectionListener, ModifyListener {
	private final Shell shell;
	private final Shell parent;

	private final Button chooseEclipse;
	private final Text projectName;
	private final Text midletClassName;
	private final Text midletName;
	private final Text reposPath;
	private final Button chooseProjectsPath;
	private final Button createProject;
	private final Button fixClonedBtn;
	private final Button restartSetup;
	private final Label creationStatus;
	private final Group createNewProject;

	IdeaUtils(Shell parent) {
		if (!Settings.ideaJdkTablePatched)
			throw new RuntimeException("Attempt to open utils window when IDE is not configured.");
		this.parent = parent;
		shell = new Shell(parent, SWT.MAX | SWT.FOREGROUND | SWT.TITLE | SWT.MENU | SWT.MIN);
		shell.setText("Intellij IDEA support");
		shell.setMinimumSize(450, 600);
		shell.setSize(450, 600);

		GridLayout layout = new GridLayout(1, true);
		shell.setLayout(layout);

		Group fixupIdeaClonedProject = new Group(shell, SWT.NONE);
		fixupIdeaClonedProject.setText("Fix a project after clone");
		fixupIdeaClonedProject.setLayout(genGLo());
		fixupIdeaClonedProject.setLayoutData(genGd());

		new Label(fixupIdeaClonedProject, SWT.NONE).setText("Project configuration keeps absolute paths specific to each machine.");
		new Label(fixupIdeaClonedProject, SWT.NONE).setText("Affected files are gitignored by default. This button will create them.");
		fixClonedBtn = new Button(fixupIdeaClonedProject, SWT.FLAT);
		fixClonedBtn.setText("Choose a project");
		fixClonedBtn.addSelectionListener(this);

		createNewProject = new Group(shell, SWT.NONE);
		createNewProject.setText("Create new project");
		createNewProject.setLayout(genGLo());
		createNewProject.setLayoutData(genGd());

		projectName = new Text(createNewProject, 2048);
		projectName.setMessage("Project name");
		projectName.setToolTipText("Name of folder, JAR file, various configurations. Only ASCII letters, numbers, hyphen and underscore allowed.\nExample: \"mahocart_midp\"");
		projectName.setLayoutData(new RowData(400, SWT.DEFAULT));
		projectName.addModifyListener(this);

		midletClassName = new Text(createNewProject, 2048);
		midletClassName.setMessage("MIDlet class name");
		midletClassName.setToolTipText("Full name of your MIDlet class. Must be valid java type name.\n\nExample: \"ru.symansel.mahocart.MahoCartMIDlet\"");
		midletClassName.setLayoutData(new RowData(400, SWT.DEFAULT));
		midletClassName.addModifyListener(this);

		midletName = new Text(createNewProject, 2048);
		midletName.setMessage("Readable MIDlet name");
		midletName.setToolTipText("Name of your MIDlet, shown to user. Can contain any symbols except commas, just make sure your target device has enough fonts to display the name.\n\nExample: \"МАХОКАРТ\uD83D\uDEA8\uD83D\uDEA8\uD83D\uDEA8\"");
		midletName.setLayoutData(new RowData(400, SWT.DEFAULT));
		midletName.addModifyListener(this);

		reposPath = new Text(createNewProject, 2048);
		reposPath.setText(Settings.lastIdeaRepoPath);
		reposPath.setMessage("Project location");
		reposPath.setToolTipText("Location of folder where the project's folder will be created.\n\nExample: \"" + (Emulator.linux ? "/mnt/projects/" : "D:\\projects\\") + "\"");
		reposPath.setLayoutData(new RowData(400, SWT.DEFAULT));
		reposPath.addModifyListener(this);

		chooseProjectsPath = new Button(createNewProject, SWT.FLAT);
		chooseProjectsPath.setText("Choose location via file explorer");
		chooseProjectsPath.addSelectionListener(this);
		createProject = new Button(createNewProject, SWT.FLAT);
		createProject.setText("Create project");
		createProject.addSelectionListener(this);
		createProject.setEnabled(false);

		creationStatus = new Label(createNewProject, 0);
		creationStatus.setText("Hover fields to see tooltips with details.");
		creationStatus.setSize(400, SWT.DEFAULT);

		Group convertEclipseProject = new Group(shell, SWT.NONE);
		convertEclipseProject.setText("Convert Eclipse MTJ project");
		convertEclipseProject.setLayout(genGLo());
		convertEclipseProject.setLayoutData(genGd());

		chooseEclipse = new Button(convertEclipseProject, SWT.FLAT);
		chooseEclipse.setText("Choose existing project");
		chooseEclipse.addSelectionListener(this);

		new Label(convertEclipseProject, SWT.NONE).setText("This works only for projects with default configuration.");
		new Label(convertEclipseProject, SWT.NONE).setText("Run config will be created only for first MIDlet.");

		Group maintenanceGroup = new Group(shell, SWT.NONE);
		maintenanceGroup.setText("Maintenance");
		maintenanceGroup.setLayout(genGLo());
		maintenanceGroup.setLayoutData(genGd());

		restartSetup = new Button(maintenanceGroup, SWT.PUSH);
		restartSetup.setText("Reset all settings and run setup again");
		restartSetup.addSelectionListener(this);

		shell.layout(true, true);
	}

	public static void open(Shell p) {
		if (Settings.ideaJdkTablePatched) {
			// ready for work
			new IdeaUtils(p).shell.open();
		} else {
			if (Emulator.linux)
				new IdeaSetupXdgLinux(p).open();
			else
				new IdeaSetupWindows(p).open();
			// TODO macos? headless linux?
		}
	}

	//#region Utils / empty handlers

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

	private void errorMsg(String header, String text) {
		final MessageBox mb = new MessageBox(this.shell, SWT.ICON_ERROR | SWT.OK);
		mb.setText(header);
		mb.setMessage(text);
		mb.open();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent selectionEvent) {

	}

	private static boolean isValidRepoName(String input) {
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-' || c == '_')) {
				return false;
			}
		}

		return true;
	}

	private static String validateInput(String repoName, String className, String appName, String location) {
		if (repoName.isEmpty()) {
			return "Project name must not be empty.";
		}
		if (!isValidRepoName(repoName)) {
			return "Project name must meet certain restrictions, check tooltip on the field.";
		}
		if (className.isEmpty()) {
			return "Class name must not be empty.";
		}
		if (!JavaTypeValidator.isValidJavaTypeName(className)) {
			return "Class name must be a valid Java class name.";
		}
		if (appName.isEmpty()) {
			return "MIDlet name must not be empty.";
		}
		if (appName.indexOf(',') != -1) {
			return "MIDlet name must not contain commas.";
		}
		if (location.isEmpty() || ".".equals(location)) {
			return "Specify location for the project";
		}
		if (!Files.exists(Paths.get(location))) {
			return "Location for project doesn't exist.";
		}
		if (Files.exists(Paths.get(location, repoName))) {
			return "Folder with the specified name already exists. Do something with it.";
		}
		return null;
	}

	//#endregion

	//#region Handlers

	@Override
	public void modifyText(ModifyEvent modifyEvent) {
		String repoName = projectName.getText().trim();
		String className = midletClassName.getText().trim();
		String appName = midletName.getText().trim();
		String location = reposPath.getText().trim();

		if (location.endsWith("/") || location.endsWith("\\")) location = location.substring(0, location.length() - 1);

		String validation = validateInput(repoName, className, appName, location);
		if (validation != null) creationStatus.setText(validation);
		else creationStatus.setText("Code will be placed at " + Paths.get(location, repoName, "src"));

		createProject.setEnabled(true);
		createNewProject.layout(true, true);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.widget == chooseProjectsPath) {
			DirectoryDialog dd = new DirectoryDialog(shell, SWT.OPEN);
			dd.setText("Choose folder where you store your projects");
			String path = dd.open();
			if (path != null) reposPath.setText(path);
		} else if (e.widget == createProject) {
			createProject();
		} else if (e.widget == fixClonedBtn) {
			restoreProject();
		} else if (e.widget == chooseEclipse) {
			convertProject();
		} else if (e.widget == restartSetup) {
			Settings.ideaJdkTablePatched = false;
			Settings.proguardPath = null;
			Settings.ideaPath = null;
			shell.close();
			shell.dispose();
			open(parent);
		}
	}

	//#endregion

	//#region Implementation

	private void createProject() {
		String repoName = projectName.getText().trim();
		String className = midletClassName.getText().trim();
		String appName = midletName.getText().trim();
		String location = reposPath.getText().trim();

		if (location.endsWith("/") || location.endsWith("\\")) location = location.substring(0, location.length() - 1);

		String validation = validateInput(repoName, className, appName, location);
		if (validation != null) {
			errorMsg("Project creation", validation);
			return;
		}
		try {
			String code = ProjectGenerator.create(location, repoName, className, appName);
			Settings.lastIdeaRepoPath = location;
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
			errorMsg("Project restore", "Failed to generate project: " + ex.getMessage() + "\nNote that this works only with projects created by KEmulator.");
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
			mb.setMessage("Your project was converted and will open in IDEA in few seconds. There are some important notes:\n\n" + "- You can still use Eclipse JDT/MTJ to work on the project.\n" + "- IDEA and Eclipse use different files for MIDlet suite manifest. Synchronize them by hand.\n" + "- IDEA provides bad support for global package classes. Get rid of them.\n" + "- Artifact building will provide you non-pre-verified JAR, it's not ready to be run on real devices.\n" + "- To get a JAR ready for deployment, use \"Package\" run configuration. The file will be \"./deployed/PROJECTNAME.jar\".\n" + "- Pre-verification is done via ProGuard as part of optimization/obfuscation pass. Edit ProGuard config manually to skip it.");
			mb.open();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof IllegalArgumentException)
				errorMsg("Project conversion", "Failed to convert project: " + ex.getMessage());
			else errorMsg("Project conversion", "Failed to convert project: " + ex);
		}
	}

	//#endregion

	//#region CLI implementation

	public static void restoreProjectCLI(String path) {
		if (!Settings.ideaJdkTablePatched) {
			System.out.println("IDE support is not configured, please run setup first.");
			System.exit(2);
		}
		File folder = new File(path);
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".iml")) {
				String imlPath = file.getAbsolutePath();
				try {
					System.out.println("Fixing project at " + imlPath);
					ProjectGenerator.fixCloned(imlPath);
					System.out.println("OK");
					System.exit(0);
				} catch (Exception ex) {
					System.out.println("Failed!");
					System.out.println(ex.getMessage());
					System.exit(1);
				}
			}
		}

		System.out.println("No IML files found. Exiting.");
		System.exit(1);
	}

	public static void createProjectCLI(String path) {
		if (!Settings.ideaJdkTablePatched) {
			System.out.println("IDE support is not configured, please run setup first.");
			System.exit(2);
		}

		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Please enter project name.");
			System.out.println("Name of folder, JAR file, various configurations. Only ASCII letters, numbers, hyphen and underscore allowed.");
			System.out.print("> ");
			String name = br.readLine();

			System.out.println("Please enter MIDlet class name.");
			System.out.println("Full name of your MIDlet class. Must be valid java type name.");
			System.out.print("> ");
			String className = br.readLine();

			System.out.println("Please enter MIDlet name.");
			System.out.println("Name of your MIDlet, shown to user. Can contain any symbols except commas, just make sure your target device has enough fonts to display the name.");
			System.out.print("> ");
			String midletName = br.readLine();

			String validation = validateInput(name, className, midletName, path);
			if (validation != null) {
				System.out.println(validation);
				System.exit(1);
			}

			ProjectGenerator.create(path, name, className, midletName);
			Settings.lastIdeaRepoPath = path;
			System.out.println("OK");
			System.exit(0);
		} catch (Exception ex) {
			System.out.println("Failed!");
			System.out.println(ex.getMessage());
			System.exit(1);
		}
	}

	//#endregion
}
