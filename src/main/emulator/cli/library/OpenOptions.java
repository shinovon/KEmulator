package emulator.cli.library;

import emulator.cli.controller.StartOptions;
import java.nio.file.Path;

public final class OpenOptions {
	public final Path inputPath;
	public final Integer midletIndex;
	public final StartOptions startOptions;

	public OpenOptions(Path inputPath, Integer midletIndex, StartOptions startOptions) {
		this.inputPath = inputPath;
		this.midletIndex = midletIndex;
		this.startOptions = startOptions;
	}
}
