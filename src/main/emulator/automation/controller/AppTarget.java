package emulator.automation.controller;

import emulator.automation.shared.MidletDescriptor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import mjson.Json;

final class AppTarget {
	String appLogId;
	String displayName;
	String jarName;
	Boolean jadPresent;
	String vendor;
	String version;
	String midletName;
	Path jarPath;
	Path jadPath;
	Path launchPath;
	List<MidletDescriptor> midlets = new ArrayList<MidletDescriptor>();

	Json toJson() {
		return Json.object()
			.set("displayName", displayName)
			.set("jarName", jarName)
			.set("jadPresent", jadPresent)
			.set("vendor", vendor)
			.set("version", version)
			.set("midletName", midletName);
	}
}
