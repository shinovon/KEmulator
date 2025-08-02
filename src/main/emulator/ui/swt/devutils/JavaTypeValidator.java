package emulator.ui.swt.devutils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JavaTypeValidator {
	private static final Set<String> JAVA_KEYWORDS = new HashSet<>(Arrays.asList(
			"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
			"continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
			"for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
			"new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
			"super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
	));

	// written by DeepSeek

	public static boolean isValidJavaTypeName(String name) {
		if (name == null || name.isEmpty()) {
			return false;
		}

		if (name.startsWith("java.") || name.startsWith("javax.")) {
			return false;
		}

		if (name.startsWith(".") || name.endsWith(".")) {
			return false;
		}

		String[] parts = name.split("\\.");
		for (String part : parts) {
			if (part.isEmpty()) {
				return false;
			}

			if (JAVA_KEYWORDS.contains(part)) {
				return false;
			}

			char firstChar = part.charAt(0);
			if (firstChar >= '0' && firstChar <= '9') {
				return false;
			}

			for (int i = 0; i < part.length(); i++) {
				char c = part.charAt(i);
				boolean isValidChar = (c >= 'A' && c <= 'Z')
						|| (c >= 'a' && c <= 'z')
						|| (c >= '0' && c <= '9');
				if (!isValidChar) {
					return false;
				}
			}
		}

		return true;
	}
}