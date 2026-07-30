package javax.microedition.lcdui;

import emulator.automation.worker.AutomationWorkerRuntime;

public class Ticker {
	private String string;

	public Ticker(final String string) {
		super();
		this.setString(string);
	}

	public void setString(final String aString353) {
		if (aString353 == null) {
			throw new NullPointerException();
		}
		this.string = aString353;
		AutomationWorkerRuntime.onDisplayStateChanged("ticker-changed");
	}

	public String getString() {
		return this.string;
	}
}
