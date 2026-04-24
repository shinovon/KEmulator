package emulator.cli.controller;

final class ResolvedRuntime {
	final String kind;
	final String classpath;

	ResolvedRuntime(String kind, String classpath) {
		this.kind = kind;
		this.classpath = classpath;
	}
}
