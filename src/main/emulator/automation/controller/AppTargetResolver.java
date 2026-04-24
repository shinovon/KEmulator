package emulator.automation.controller;

import emulator.automation.shared.AppInspection;
import emulator.automation.shared.AppInspector;
import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationException;
import emulator.automation.shared.MidletDescriptor;
import emulator.automation.shared.TextValues;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import mjson.Json;

final class AppTargetResolver {
	private static void rememberSelectedMidlet(AppTarget entry, MidletDescriptor midlet) {
		if (entry == null || midlet == null) {
			return;
		}

		String midletName = TextValues.trimToNull(midlet.name);
		if (midletName != null) {
			entry.midletName = midletName;
		}
	}

	private static String sanitizePathLogId(Path input) {
		String value = input.toAbsolutePath()
			.normalize()
			.toString()
			.replace(File.separatorChar, '/')
			.toLowerCase(Locale.US);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-' || c == '.') {
				builder.append(c);
			} else {
				builder.append('_');
			}
		}

		return builder.toString();
	}

	AppTarget inspect(Path input) throws IOException {
		AppInspection inspection = AppInspector.inspect(input);
		AppTarget entry = new AppTarget();
		entry.appLogId = sanitizePathLogId(input);
		entry.displayName = inspection.displayName;
		entry.jarName = inspection.jarPath == null
			? null
			: inspection.jarPath.getFileName().toString();
		entry.jadPresent = Boolean.valueOf(inspection.jadPath != null);
		entry.vendor = inspection.vendor;
		entry.version = inspection.version;
		entry.midletName = TextValues.firstNonBlank(
			inspection.displayName, inspection.midlets.isEmpty() ? null : inspection.midlets.get(0).name);
		entry.jarPath = inspection.jarPath;
		entry.jadPath = inspection.jadPath;
		entry.launchPath = inspection.launchPath;
		entry.midlets = new ArrayList<MidletDescriptor>(inspection.midlets);

		return entry;
	}

	String resolveMidletClass(AppTarget entry, Integer midletIndex) {
		List<MidletDescriptor> midlets = entry.midlets;
		if (midletIndex == null) {
			if (midlets.size() > 1) {
				throw new AutomationException(
					AutomationErrorCodes.MIDLET_SELECTION_REQUIRED,
					"MIDlet selection required",
					entry == null ? null : entry.toJson());
			}

			if (midlets.isEmpty()) {
				return null;
			}

			rememberSelectedMidlet(entry, midlets.get(0));

			return midlets.get(0).className;
		}

		for (MidletDescriptor midlet : midlets) {
			if (midlet.index == midletIndex.intValue()) {
				rememberSelectedMidlet(entry, midlet);

				return midlet.className;
			}
		}

		Json details = entry == null ? Json.object() : entry.toJson();
		details.set("requestedMidletIndex", midletIndex);
		throw new AutomationException(
			AutomationErrorCodes.UNKNOWN_MIDLET, "Unknown MIDlet index: " + midletIndex, details);
	}
}
