package emulator.ui;

public interface ILogStream {
	void print(final String p0);

	void println(final String p0);

	void println();

	void printStackTrace(final String p0);

	void stdout(final String p0);
}
