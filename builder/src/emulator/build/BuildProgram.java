package emulator.build;

import emulator.Emulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

public class BuildProgram {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("When invoking build system, at least one argument is required: path to project directory.");
			System.out.println("Pass \"--help\" as single argument to see options.");
			System.exit(1);
		}
		if ("--help".equals(args[0]) || "-h".equals(args[0])) {
			printHelp();
			System.exit(0);
		}

		Path path = Paths.get(args[0]).toAbsolutePath();
		if (!Files.exists(path)) {
			System.out.println("Project directory does not exist: " + path);
			System.exit(1);
		}

		HashSet<String> opts = new HashSet<>();
		for (int i = 1; i < args.length; i++) {
			if (args[i].startsWith("--")) {
				opts.add(args[i]);
			} else if (args[i].startsWith("-")) {
				char[] list = args[i].toCharArray();
				for (int j = 1; j < list.length; j++) {
					switch (list[j]) {
						case 'r':
							opts.add("--run");
							break;
						case 'a':
							opts.add("--anyres");
							break;
						case 'o':
							opts.add("--obf");
							break;
						case 's':
							opts.add("--skipmiss");
							break;
						default:
							System.out.println("Unknown option: " + args[i]);
							System.exit(1);
					}
				}
			} else {
				System.out.println("Unknown option: " + args[i]);
				System.exit(1);
			}
		}

		Properties kemProps = null;
		try {
			if (new File(Emulator.getUserPath() + File.separator + "property.txt").exists()) {
				final FileInputStream fileInputStream = new FileInputStream(Emulator.getUserPath() + File.separator + "property.txt");
				final Properties temp = new Properties();
				temp.load(fileInputStream);
				kemProps = temp;
			}
		} catch (IOException ignored) {
		}

		new BuildSystem(path, opts, kemProps).build();
	}

	private static void printHelp() {
		System.out.println("KEmulator build system for simple CLDC/MIDP projects");
		System.out.println("Usage:");
		System.out.println();
		System.out.println("    KEmulator_home$ java -jar builder.jar PATH_TO_PROJECT [OPTIONS ...]");
		System.out.println("    PATH_TO_PROJECT$ starter.sh [OPTIONS ...]");
		System.out.println();
		System.out.println("Options:");
		System.out.println();
		System.out.println("    -h, --help        Print this help message. Must be passed instead of project path.");
		System.out.println("    -r, --run         Run the project after compilation.");
		System.out.println("    -a, --anyres      When passed: include all files except *.java from all source folders as resources in final JAR. " +
				"When not passed: include folders from IML marked as \"resources\" and folders from eclipse named exactly as \"res\" as is, compile everything else.");
		System.out.println("    -o, --obf         When passed: include \"proguard.cfg\" as proguard config. When not passed: disable optimization, shrinking and obfuscation.");
		System.out.println("    -s, --skipmiss    When passed: non-existing source and resource paths will be skipped. Library paths are not affected.");
		System.out.println();
		System.out.println("Principle of operation:");
		System.out.println();
		System.out.println("    1) The program processes command-line arguments:");
		System.out.println("        1.1) The first argument is interpreted as a path. The program verifies that this path exists. If it does not exist, the program outputs an error and exits with code 1.");
		System.out.println("        1.2) The second and subsequent arguments are read as options.");
		System.out.println("    2) The program searches for an application manifest file in the current working directory in the following order:");
		System.out.println("        2.1) \"Application Descriptor\"");
		System.out.println("        2.2) \"META-INF/MANIFEST.MF\"");
		System.out.println("        2.3) \"MANIFEST.MF\"");
		System.out.println("        2.4) \"nbproject/project.properties\"");
		System.out.println("        2.5) If none of the specified files are found, the program outputs an error and exits with code 1.");
		System.out.println("        2.6) If a file is found, the program reads the manifest of the future application package from it.");
		System.out.println("        2.7) The program automatically corrects common errors found within the manifest.");
		System.out.println("    3) The program searches for a project configuration file in the current working directory:");
		System.out.println("        3.1) It looks for a file named \".classpath\" or any file matching the \"*.iml\" pattern.");
		System.out.println("        3.2) If a configuration file is found, the program reads the folders and libraries constituting the application from it. Only simple inclusion options are supported; transient dependencies, exclusions, and other advanced features are ignored.");
		System.out.println("        3.3) If no configuration file is found, the program switches to configless project mode:");
		System.out.println("            3.3.1) The \"src\" directory is included as source root.");
		System.out.println("            3.3.2) The \"res\" directory is included as resource root if it exists.");
		System.out.println("            3.3.3) The \"lib\" directory is recursively scanned:");
		System.out.println("                3.3.3.1) All immediate subdirectories matching the pattern \"lib/*/src\" are included as source roots.");
		System.out.println("                3.3.3.2) All immediate subdirectories matching the pattern \"lib/*/res\" are included as resource roots.");
		System.out.println("                3.3.3.3) All files matching the pattern \"lib/*.jar\" are included as exported libraries.");
		System.out.println("        3.4) To override this behavior, a configuration file must be created and populated with the appropriate directives.");
		System.out.println("    4) The program locates the ProGuard utility:");
		System.out.println("        4.1) The path to ProGuard is first extracted from the emulator's configuration.");
		System.out.println("        4.2) If not found in the emulator configuration, the program searches for ProGuard in the local directory.");
		System.out.println("        4.3) If ProGuard is not found, the program outputs an error and exits with code 1.");
		System.out.println("    5) The program prepares the output directories:");
		System.out.println("        5.1) It deletes the \"bin\" directory.");
		System.out.println("        5.2) It creates the \"deployed\" directory.");
		System.out.println("    6) The program compiles the source code:");
		System.out.println("        6.1) A list of files requiring compilation is formed based on the previously read classpath data.");
		System.out.println("        6.2) A list of libraries is formed from the emulator's \"uei\" directory.");
		System.out.println("        6.3) The javac compiler is executed with target version 1.3 and the formed lists. It must return exit code 0. If a non-zero exit code is returned, the program outputs an error and exits with code 1.");
		System.out.println("        6.4) The jar command is executed to package the compiled content from the \"bin\" directory. It must return exit code 0. If a non-zero exit code is returned, the program outputs an error and exits with code 1.");
		System.out.println("    7) The program processes resources:");
		System.out.println("        7.1) A list of resource files is formed based on the previously read classpath data.");
		System.out.println("        7.2) For each directory containing resources, the jar command is executed to update the application package. It must return exit code 0 for each execution. If a non-zero exit code is returned, the program outputs an error and exits with code 1.");
		System.out.println("    8) The program processes libraries and creates the final package:");
		System.out.println("        8.1) A list of libraries is formed based on the previously read classpath data.");
		System.out.println("        8.2) ProGuard is executed with this list. Depending on the provided options, it is passed either the arguments \"-dontoptimize -dontshrink -dontobfuscate -keep class *\" or a configuration file used by the project.");
		System.out.println("        8.3) The final JAR file is named \"./deployed/%PROJECT_NAME%.jar\".");
		System.out.println("    9) The program deletes temporary files.");
		System.out.println("    10) The program optionally executes the created package with KEmulator.");
	}
}
