package emulator.ui.swt;

import emulator.Emulator;
import emulator.debug.Instance;

import java.util.Map;

final class ClassListFiller implements Runnable {
	private final Watcher watcher;

	ClassListFiller(final Watcher w) {
		super();
		this.watcher = w;
	}

	public final void run() {
		try {
			switch (this.watcher.type) {
				case Static: {
					for (int i = 0; i < Emulator.jarClasses.size(); ++i) {
						final String s = (String) Emulator.jarClasses.get(i);
						try {
							final Instance c;
							if ((c = new Instance(s, s.equals(Emulator.getMIDlet().getClass().getName()) ? Emulator.getMIDlet() : null)).method879(null)) {
								String s2 = c.toString();
								if (c.getCls().getSuperclass() != null) {
									s2 = s2 + "@" + c.getCls().getSuperclass().getName();
								}
								((Map) this.watcher.selectableClasses).put(s2, c);
							}
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
				case Profiler: {
					final Instance c2 = new Instance("emulator.debug.Profiler", null);
					c2.method879(null);
					((Map) this.watcher.selectableClasses).put("System profiler", c2);

					final Instance c3 = new Instance("emulator.debug.Profiler3D", null);
					((Map) this.watcher.selectableClasses).put("3D profiler", c3);
					break;
				}
			}
		} catch (Error ignored) {

		}
	}
}
