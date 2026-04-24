package emulator.cli.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CliInvocation {
	private final List<String> tokens;
	private final boolean json;

	public CliInvocation(List<String> tokens, boolean json) {
		this.tokens = Collections.unmodifiableList(new ArrayList<String>(tokens));
		this.json = json;
	}

	public List<String> tokens() {
		return tokens;
	}

	public boolean json() {
		return json;
	}
}
