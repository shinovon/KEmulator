package emulator.sensor;

import java.util.*;

public final class h implements SensorProperties {
    private Hashtable aHashtable446;

    public h() {
        super();
        this.aHashtable446 = new Hashtable(6);
    }

    public final void setProperty(final String s, final Object o) {
        this.aHashtable446.put(s, o);
    }

    public final Object getProperty(final String s) {
        return this.aHashtable446.get(s);
    }

    public final String[] getPropertyNames() {
        final String[] array = new String[this.aHashtable446.size()];
        int n = 0;
        final Enumeration<String> keys = this.aHashtable446.keys();
        while (keys.hasMoreElements()) {
            array[n++] = keys.nextElement();
        }
        return array;
    }
}
