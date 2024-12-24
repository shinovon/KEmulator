package emulator.ui.swt;

import emulator.Emulator;
import emulator.UILocale;
import emulator.custom.CustomMethod;
import emulator.ui.ILogStream;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.io.*;

public final class Log implements ILogStream, ControlListener, DisposeListener, Runnable {
	private Shell logShell;
	PrintStream filePrintStream;
	LogStream logStream;
	boolean logOpen;
	StyledText styledText;
	Shell parentShell;
	private boolean aBoolean576;
	final StringBuffer printQueue = new StringBuffer();

	public Log() {
		super();
		this.logShell = null;
		this.styledText = null;
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


	private void method332() {
		try {
			final BufferedReader bufferedReader = new BufferedReader(new FileReader(Emulator.getUserPath() + "/log.txt"));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				this.styledText.append(line + "\n");
			}
			bufferedReader.close();
			this.styledText.setTopIndex(this.styledText.getLineCount());
		} catch (Exception ignored) {
		}
	}

	public final boolean isLogOpen() {
		return this.logOpen;
	}

	public final Shell getLogShell() {
		return this.logShell;
	}

	public final void method329(final Shell aShell575) {
		this.method334();
		this.method332();
		final Display current = Display.getCurrent();
		this.parentShell = aShell575;
		this.logShell.setLocation(aShell575.getLocation().x + aShell575.getSize().x, aShell575.getLocation().y);
		this.logShell.setSize(aShell575.getSize());
		this.logOpen = true;
		this.aBoolean576 = true;
		this.logShell.open();
		this.logShell.addControlListener(this);
		this.logShell.addDisposeListener(this);
		aShell575.setFocus();
		while (!this.logShell.isDisposed()) {
			if (!current.readAndDispatch()) {
				current.sleep();
			}
		}
		this.logOpen = false;
	}

	public final void dispose() {
		if (this.logShell != null && !this.logShell.isDisposed()) {
			this.logShell.dispose();
		}
		this.logOpen = false;
	}

	private void method334() {
		final FillLayout layout;
		(layout = new FillLayout()).spacing = 0;
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		(this.logShell = new Shell()).setText(UILocale.get("LOG_FRAME_TITLE", "Log"));
		this.logShell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.logShell.setLayout(layout);
		this.logShell.setSize(new Point(310, 254));
		this.styledText = new StyledText(this.logShell, 2816);
	}

	public final boolean method333() {
		return this.aBoolean576;
	}

	public final void controlMoved(final ControlEvent controlEvent) {
		if (Math.abs(this.parentShell.getLocation().x + this.parentShell.getSize().x - this.logShell.getLocation().x) < 10 && Math.abs(this.parentShell.getLocation().y - this.logShell.getLocation().y) < 20) {
			this.logShell.setLocation(this.parentShell.getLocation().x + this.parentShell.getSize().x, this.parentShell.getLocation().y);
			aBoolean576 = true;
		} else {
			aBoolean576 = false;
		}
	}

	public final void controlResized(final ControlEvent controlEvent) {
	}

	public final void widgetDisposed(final DisposeEvent disposeEvent) {
		this.dispose();
	}

	public void run() {
		try {
			while (true) {
				if (printQueue.length() > 0) {
					EmulatorImpl.syncExec(new Textout(this));
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

	private final class Textout implements Runnable {
		private final Log class11;

		private Textout(final Log c11) {
			super();
			this.class11 = c11;
		}

		public final void run() {
			String s;
			synchronized (printQueue) {
				s = printQueue.toString();
				printQueue.setLength(0);
			}
			try {
				class11.filePrintStream.print(s);
			} catch (Exception ignored) {}
			if (!class11.logOpen || class11.styledText.isDisposed()) {
				return;
			}
			class11.styledText.append(s);
			class11.styledText.setTopIndex(class11.styledText.getLineCount());
		}
	}

	private final class LogStream extends PrintStream {
		PrintStream orig;
		private final Log class11;

		LogStream(final Log aClass11_1402) {
			super(System.out, true);
			this.class11 = aClass11_1402;
			this.orig = System.out;
			System.setOut(this);
			System.setErr(this);
		}

		public final void print(final String s) {
			this.class11.wrapPrint(">>" + s);
			this.orig.print(s);
		}

		public final void println(final String s) {
			this.class11.wrapPrintln(">>" + s);
			this.orig.println(s);
		}

		public final void print(final Object o) {
			this.class11.wrapPrint(">>" + o);
			this.orig.print(o);
		}

		public final void println(final Object o) {
			this.class11.wrapPrintln(">>" + o);
			this.orig.println(o);
		}

		public final void println() {
			this.class11.wrapPrintln(">>");
			this.orig.println();
		}
	}
}
