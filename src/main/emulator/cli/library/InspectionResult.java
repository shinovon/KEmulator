package emulator.cli.library;

import java.nio.file.Path;
import java.util.List;
import mjson.Json;

public final class InspectionResult {
	public final Path inputPath;
	public final Path launchPath;
	public final Path jarPath;
	public final Path jadPath;
	public final String sourceKind;
	public final String displayName;
	public final String vendor;
	public final String version;
	public final List<MidletEntry> midlets;
	public final String selectedMidletClass;

	public InspectionResult(
		Path inputPath,
		Path launchPath,
		Path jarPath,
		Path jadPath,
		String sourceKind,
		String displayName,
		String vendor,
		String version,
		List<MidletEntry> midlets,
		String selectedMidletClass) {
		this.inputPath = inputPath;
		this.launchPath = launchPath;
		this.jarPath = jarPath;
		this.jadPath = jadPath;
		this.sourceKind = sourceKind;
		this.displayName = displayName;
		this.vendor = vendor;
		this.version = version;
		this.midlets = midlets;
		this.selectedMidletClass = selectedMidletClass;
	}

	public Json toJson() {
		Json midletsJson = Json.array();
		for (MidletEntry entry : midlets) {
			midletsJson.add(entry.toJson());
		}

		return Json.object()
			.set("inputPath", inputPath.toString())
			.set("launchPath", launchPath == null ? null : launchPath.toString())
			.set("jarPath", jarPath == null ? null : jarPath.toString())
			.set("jadPath", jadPath == null ? null : jadPath.toString())
			.set("sourceKind", sourceKind)
			.set("displayName", displayName)
			.set("vendor", vendor)
			.set("version", version)
			.set("midlets", midletsJson)
			.set("selectedMidletClass", selectedMidletClass);
	}
}
