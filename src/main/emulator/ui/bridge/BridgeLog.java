package emulator.ui.bridge;

import emulator.Emulator;
import emulator.custom.CustomMethod;
import emulator.ui.ILogStream;

import java.io.*;

public class BridgeLog implements ILogStream, Runnable {
	PrintStream filePrintStream;
	LogStream logStream;
	final StringBuffer printQueue = new StringBuffer();

	public BridgeLog() {
		super();
		try {
			final File file;
			if (!(file = new File(Emulator.getUserPath() + "/log.txt")).exists()) {
				file.createNewFile();
			}
			this.logStream = new LogStream(this);
			this.filePrintStream = new PrintStream(new FileOutputStream(file));
		} catch (Exception ignored) {
		}
		new Thread(this, "KEmulator-Log").start();
	}

	// KEmulator api methods
	public final void print(final String s) {
		logStream.orig.print(s);
		queuePrint(s);
	}

	public final void println(final String s) {
		logStream.orig.println(s);
		queuePrint(s + "\n");
	}

	public final void println() {
		logStream.orig.println();
		queuePrint("\n");
	}

	public final void stdout(final String s) {
		this.logStream.orig.println(s);
		queuePrint(s + "\n");
	}

	public void println(Throwable e) {
		println(CustomMethod.getStackTrace(e));
	}

	public final void printStackTrace(final String s) {
		this.println("==StackTrace==" + s + "==StackTrace==");
	}

	// System.out wrapper methods
	public void wrapPrint(String s) {
		queuePrint(s);
	}

	public void wrapPrintln(String s) {
		queuePrint(s + "\n");
	}

	private void queuePrint(final String s) {
		synchronized (printQueue) {
			printQueue.append(s);
			printQueue.notify();
		}
	}

	public void run() {
		try {
			while (true) {
				if (printQueue.length() > 0) {
					String s;
					synchronized (printQueue) {
						s = printQueue.toString();
						printQueue.setLength(0);
					}
					try {
						filePrintStream.print(s);
					} catch (Exception ignored) {
					}
				} else {
					synchronized (printQueue) {
						printQueue.wait();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final class LogStream extends PrintStream {
		PrintStream orig;
		private final BridgeLog log;

		LogStream(final BridgeLog log) {
			super(System.out, true);
			this.log = log;
			this.orig = System.out;
			System.setOut(this);
			System.setErr(this);
		}

		public final void print(final String s) {
			this.log.wrapPrint(">>" + s);
			this.orig.print(s);
		}

		public final void println(final String s) {
			this.log.wrapPrintln(">>" + s);
			this.orig.println(s);
		}

		public final void print(final Object o) {
			this.log.wrapPrint(">>" + o);
			this.orig.print(o);
		}

		public final void println(final Object o) {
			this.log.wrapPrintln(">>" + o);
			this.orig.println(o);
		}

		public final void println() {
			this.log.wrapPrintln(">>");
			this.orig.println();
		}
	}
}
