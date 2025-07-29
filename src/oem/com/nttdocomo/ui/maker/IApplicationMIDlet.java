package com.nttdocomo.ui.maker;

import javax.microedition.midlet.*;

import com.nttdocomo.ui.*;
import emulator.*;

public class IApplicationMIDlet extends MIDlet implements Runnable {
	public static IApplication pIApplication;
	boolean a;

	public IApplicationMIDlet(final String s) throws Exception {
		System.out.println("DOJA applicationClassName=" + s);
		Emulator.setMIDlet(this);
		IApplicationMIDlet.pIApplication = (IApplication) Class.forName(s, true, Emulator.customClassLoader).newInstance();
	}

	protected void destroyApp(final boolean b) {
	}

	protected void pauseApp() {
	}

	protected void startApp() {
		if (!this.a) {
			this.a = true;
			new Thread(this).start();
		} else {
			IApplicationMIDlet.pIApplication.resume();
		}
	}

	public void run() {
		IApplicationMIDlet.pIApplication.start();
	}
}
