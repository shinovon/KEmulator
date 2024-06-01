package emulator.ui.swt;

import emulator.*;
import emulator.debug.*;

final class Class14 implements Runnable {
	private final Watcher aClass5_587;

	Class14(final Watcher aClass5_579) {
		super();
		this.aClass5_587 = aClass5_579;
	}

	public final void run() {
		try {
			switch (Watcher.method314(this.aClass5_587)) {
				case 0: {
					for (int i = 0; i < Emulator.jarClasses.size(); ++i) {
						final String s = (String) Emulator.jarClasses.get(i);
						final Instance c;
						if ((c = new Instance(s, s.equals(Emulator.getMIDlet().getClass().getName()) ? Emulator.getMIDlet() : null)).method879(null)) {
							String s2 = c.toString();
							if (c.getCls().getSuperclass() != null) {
								s2 = s2 + "@" + c.getCls().getSuperclass().getName();
							}
							Watcher.method304(this.aClass5_587).put(s2, c);
						}
					}
				}
				case 1: {
					final Instance c2;
					(c2 = new Instance("emulator.debug.Profiler", null)).method879(null);
					Watcher.method304(this.aClass5_587).put("SystemProfiler", c2);
					break;
				}
			}
		} catch (Error ignored) {

		}
	}
}
