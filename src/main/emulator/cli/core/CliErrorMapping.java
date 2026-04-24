package emulator.cli.core;

import emulator.automation.shared.AutomationErrorCodes;

public final class CliErrorMapping {
	private CliErrorMapping() {
	}

	public static int exitCodeFor(String code) {
		if (code == null) {
			return CliExitCodes.RUNTIME;
		}

		if (AutomationErrorCodes.PATH_NOT_FOUND.equals(code)) {
			return CliExitCodes.NOT_FOUND;
		}

		if ("USAGE_ERROR".equals(code)
			|| "UNKNOWN_COMMAND".equals(code)
			|| "UNKNOWN_RUNTIME".equals(code)
			|| AutomationErrorCodes.UNSUPPORTED_INPUT.equals(code)
			|| AutomationErrorCodes.MIDLET_SELECTION_REQUIRED.equals(code)
			|| AutomationErrorCodes.UNKNOWN_MIDLET.equals(code)
			|| AutomationErrorCodes.INVALID_REQUEST.equals(code)
			|| AutomationErrorCodes.UNKNOWN_KEY.equals(code)
			|| AutomationErrorCodes.UNKNOWN_COMMAND_ID.equals(code)
			|| AutomationErrorCodes.STALE_SNAPSHOT.equals(code)
			|| AutomationErrorCodes.UNKNOWN_PERMISSION_ID.equals(code)
			|| AutomationErrorCodes.PERMISSION_ORDER_VIOLATION.equals(code)) {
			return CliExitCodes.USAGE;
		}

		return CliExitCodes.RUNTIME;
	}
}
