package emulator.cli.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CommandPath {
	private final List<String> segments;

	private CommandPath(List<String> segments) {
		this.segments = Collections.unmodifiableList(new ArrayList<String>(segments));
	}

	public static CommandPath of(String... segments) {
		List<String> parts = new ArrayList<String>(segments.length);
		for (String segment : segments) {
			parts.add(segment);
		}

		return new CommandPath(parts);
	}

	public int length() {
		return segments.size();
	}

	public String asString() {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < segments.size(); i++) {
			if (i > 0) {
				out.append(' ');
			}

			out.append(segments.get(i));
		}

		return out.toString();
	}

	public boolean matches(List<String> tokens) {
		if (tokens.size() < segments.size()) {
			return false;
		}

		for (int i = 0; i < segments.size(); i++) {
			if (!segments.get(i).equals(tokens.get(i))) {
				return false;
			}
		}

		return true;
	}
}
