package emulator.cli.library;

import emulator.automation.shared.*;
import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

final class CliAppInspector {
	private CliAppInspector() {
	}

	static InspectionResult inspect(Path input, String commandName, boolean json) throws IOException {
		AppInspection inspection;
		try {
			inspection = AppInspector.inspect(input);
		} catch (AutomationException e) {
			throw new KemuCliException(
				e.code,
				e.getMessage(),
				CliErrorMapping.exitCodeFor(e.code),
				commandName,
				json,
				CliResponses.normalizePublicJson(e.details));
		}

		List<MidletEntry> midlets = new ArrayList<MidletEntry>();
		for (MidletDescriptor midlet : inspection.midlets) {
			midlets.add(new MidletEntry(midlet.index, midlet.key, midlet.name, midlet.icon, midlet.className));
		}

		String selectedMidletClass = null;
		if (midlets.size() == 1) {
			selectedMidletClass = midlets.get(0).className;
		}

		return new InspectionResult(
			inspection.inputPath,
			inspection.launchPath,
			inspection.jarPath,
			inspection.jadPath,
			inspection.sourceKind,
			inspection.displayName,
			inspection.vendor,
			inspection.version,
			midlets,
			selectedMidletClass);
	}

	static void validateOpenTarget(InspectionResult inspection, Integer midletIndex, String commandName, boolean json) {
		if (inspection.midlets.size() > 1 && midletIndex == null) {
			throw new KemuCliException(
				"MIDLET_SELECTION_REQUIRED",
				"Multiple MIDlets found. Use --midlet with one of the reported indexes.",
				CliExitCodes.USAGE,
				commandName,
				json,
				inspection.toJson());
		}

		if (midletIndex == null) {
			return;
		}

		for (MidletEntry entry : inspection.midlets) {
			if (entry.index == midletIndex.intValue()) {
				return;
			}
		}

		throw new KemuCliException(
			"UNKNOWN_MIDLET",
			"Unknown MIDlet index: " + midletIndex,
			CliExitCodes.USAGE,
			commandName,
			json,
			inspection.toJson());
	}
}
