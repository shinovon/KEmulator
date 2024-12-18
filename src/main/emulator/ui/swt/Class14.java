package emulator.ui.swt;

import emulator.Emulator;
import emulator.debug.Instance;

final class Class14 implements Runnable {
	private final Watcher aClass5_587;

	Class14(final Watcher aClass5_579) {
		super();
		this.aClass5_587 = aClass5_579;
	}

	public final void run() {
		try {
			switch (Watcher.staticGetType(this.aClass5_587)) {
				case 0: {
					for (int i = 0; i < Emulator.jarClasses.size(); ++i) {
						final String s = (String) Emulator.jarClasses.get(i);
						try {
							final Instance c;
							if ((c = new Instance(s, s.equals(Emulator.getMIDlet().getClass().getName()) ? Emulator.getMIDlet() : null)).method879(null)) {
								String s2 = c.toString();
								if (c.getCls().getSuperclass() != null) {
									s2 = s2 + "@" + c.getCls().getSuperclass().getName();
								}
								Watcher.staticGetTable(this.aClass5_587).put(s2, c);
							}
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
				case 1: {
					final Instance c2 = new Instance("emulator.debug.Profiler", null);
					c2.method879(null);
					Watcher.staticGetTable(this.aClass5_587).put("SystemProfiler", c2);

					final Instance c3 = new Instance("emulator.debug.Profiler3D", null);
					Watcher.staticGetTable(this.aClass5_587).put("Profiler3D", c3);
					break;
				}
			}
		} catch (Error ignored) {

		}
	}
}
