package emulator.debug;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Vector;

public final class ClassInfo implements Comparable<ClassInfo> {
	final String name;
	int instancesCount;
	final int staticsSize;
	public final ArrayList<ObjInstance> objs;
	public final Field[] cachedFields;

	public int size() {
		int total = this.staticsSize;
		for (int i = this.objs.size() - 1; i >= 0; --i) {
			total += this.objs.get(i).size;
		}
		return total;
	}

	public int compareTo(final ClassInfo o) {
		return this.name.compareTo(o.name);
	}

	ClassInfo(final Memory m, final Class cls) {
		super();
		cachedFields = Memory.fields(cls);
		this.objs = new ArrayList<>();
		this.instancesCount = 0;
		staticsSize = m.size(cls, cachedFields, null);
		name = cls.getName();
	}
}
