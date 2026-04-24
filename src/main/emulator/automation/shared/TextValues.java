package emulator.automation.shared;

public final class TextValues {
	private TextValues() {
	}

	public static boolean isBlank(String value) {
		return trimToNull(value) == null;
	}

	public static String trimToNull(String value) {
		if (value == null) {
			return null;
		}

		String trimmed = value.trim();

		return trimmed.length() == 0 ? null : trimmed;
	}

	public static String trimToEmpty(String value) {
		return value == null ? "" : value.trim();
	}

	public static String firstNonBlank(String... values) {
		for (int i = 0; i < values.length; i++) {
			String trimmed = trimToNull(values[i]);
			if (trimmed != null) {
				return trimmed;
			}
		}

		return null;
	}
}
