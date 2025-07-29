package emulator.debug;

import java.util.Vector;

public final class ClassInfo implements Comparable<ClassInfo> {
	final String name;
	int instancesCount;
	final int staticsSize;
	public final Vector<ObjInstance> objs;

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
		this.objs = new Vector();
		this.instancesCount = 0;
		this.staticsSize = m.size(cls, null);
		this.name = cls.getName();
	}
}
