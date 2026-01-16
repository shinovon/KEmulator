/*
Copyright (c) 2025 Fyodor Ryzhov
*/
package emulator.ui.swt.devutils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;

public class DevtimeMIDlet {
	public final String className;
	public final String readableName;

	public DevtimeMIDlet(String className, String readableName) {
		this.className = className;
		this.readableName = readableName;
	}

	public static DevtimeMIDlet[] readMidletsList(Path manifestPath) throws IOException {
		Properties manifest = new Properties();
		manifest.load(new InputStreamReader(new FileInputStream(manifestPath.toFile()), StandardCharsets.UTF_8));

		ArrayList<DevtimeMIDlet> names = new ArrayList<>();
		for (int i = 1; true; i++) {
			if (!manifest.containsKey("MIDlet-" + i)) {
				break;
			}
			String[] split = ((String) manifest.get("MIDlet-" + i)).split(",");
			// name,icon,class
			names.add(new DevtimeMIDlet(split[2].trim(), split[0].trim()));
		}
		if (names.isEmpty())
			throw new IllegalArgumentException("Broken manifest file");
		return names.toArray(new DevtimeMIDlet[0]);
	}
}
