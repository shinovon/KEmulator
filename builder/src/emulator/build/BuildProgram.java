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
		if ("--help".equals(args[0])) {
			printHelp();
			System.exit(0);
		}

		Path path = Paths.get(args[0]).toAbsolutePath();
		if (!Files.exists(path)) {
			System.out.println("Project directory does not exist: " + path);
			System.exit(1);
		}

		HashSet<String> opts = new HashSet<>(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));

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
		System.out.println("Usage: KEmulator_home$ java -jar builder.jar PATH_TO_PROJECT [OPTIONS ...]");
		System.out.println("Usage: PATH_TO_PROJECT$ starter.sh [OPTIONS ...]");
		System.out.println();
		System.out.println("Options:");
		System.out.println();
		System.out.println("--run       Runs the project after compilation.");
		System.out.println();
		System.out.println("--anyres    When passed: all files from all source folders excluding *.java files will be included as resources in final JAR. " +
				"When not passed: folders from IML marked as \"resources\" and folders from eclipse named exactly as \"res\" will be included as is, everything else will be compiled.");
		System.out.println();
		System.out.println("--obf       When passed: includes \"proguard.cfg\" as proguard config. When not passed: disables optimization, shrinking and obfuscation.");
	}
}
