package emulator.ui.swt;

import emulator.*;
import emulator.debug.*;

final class Class14 implements Runnable {
    private final Class5 aClass5_587;

    Class14(final Class5 aClass5_579) {
        super();
        this.aClass5_587 = aClass5_579;
    }
    /*
    public final void run() {
        switch (Class5.method314(this.aClass5_579)) {
            case 0: {
                for (int i = 0; i < Emulator.jarClasses.size(); ++i) {
                    final String s = (String) Emulator.jarClasses.get(i);
                    final c c;
                    if ((c = new c(s, s.equals(Emulator.getMIDlet().getClass().getName()) ? Emulator.getMIDlet() : null)).method879(null)) {
                        String s2 = c.toString();
                        if (c.method883().getSuperclass() != null) {
                            s2 = s2 + "@" + c.method883().getSuperclass().getName();
                        }
                        Class5.method304(this.aClass5_579).put(s2, c);
                    }
                }
            }
            case 1: {
                final c c2;
                (c2 = new c("emulator.debug.Profiler", null)).method879(null);
                Class5.method304(this.aClass5_579).put("SystemProfiler", c2);
                break;
            }
        }
    }*/

    public final void run() {
        try {
            switch (Class5.method314(this.aClass5_587)) {
                case 0: {
                    for (int i = 0; i < Emulator.jarClasses.size(); ++i) {
                        final String s = (String) Emulator.jarClasses.get(i);
                        final Instance c;
                        if ((c = new Instance(s, (Object) (s.equals(Emulator.getMIDlet().getClass().getName()) ? Emulator.getMIDlet() : null))).method879((String) null)) {
                            String s2 = c.toString();
                            if (c.getCls().getSuperclass() != null) {
                                s2 = s2 + "@" + c.getCls().getSuperclass().getName();
                            }
                            Class5.method304(this.aClass5_587).put(s2, c);
                        }
                    }
                }
                case 1: {
                    final Instance c2;
                    (c2 = new Instance("emulator.debug.Profiler", (Object) null)).method879((String) null);
                    Class5.method304(this.aClass5_587).put("SystemProfiler", c2);
                    break;
                }
            }
        } catch (Error e) {

        }
    }
}
