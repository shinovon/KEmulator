package emulator.cli.controller;

public final class StartOptions {
	public final String mode;
	public final String runtime;
	public final Integer width;
	public final Integer height;

	public StartOptions(String mode, String runtime, Integer width, Integer height) {
		this.mode = mode;
		this.runtime = runtime;
		this.width = width;
		this.height = height;
	}

}
