/*
Copyright (c) 2025 Fyodor Ryzhov
*/
package emulator.ui.swt.devutils.idea;

import emulator.Utils;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IdeaSetupDarwin extends IdeaSetup {
	public IdeaSetupDarwin(Shell parent) {
		super(parent);
	}

	@Override
	protected Set<String> getIdeaInstallationPath() {
		Set<String> set = new HashSet<>();
		set.add("/Applications/IntelliJ IDEA.app/Contents/MacOS/idea");
		//checkPath(set);
		//checkStandardLocations(set);
		return set;
	}

	@Override
	protected Path getJetBrainsDataRoot() {
		String cfgFolder = System.getenv("HOME") + "/Library/Application Support";
		return Paths.get(cfgFolder + "/JetBrains");
	}

	@Override
	protected String autoInstallProguard() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected String autoInstallDocs() throws IOException, InterruptedException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	//#region External tools

	//#endregion

	//#region IDEA tools

	private static void checkPath(Set<String> set) {
		String pathEnv = System.getenv("PATH");
		if (pathEnv == null) return;

		for (String root : pathEnv.split(":")) {
			File f = new File(root, "idea");
			if (f.exists()) {
				set.add(f.getAbsolutePath());
			}
		}
	}

	//#endregion
}
