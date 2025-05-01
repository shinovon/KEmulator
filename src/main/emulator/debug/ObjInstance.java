package emulator.debug;

import java.util.Vector;

public  final class ObjInstance {
	public final Vector<String> paths;
	public final Object value;
	public final int size;

	ObjInstance(final Memory a, final String ref, final Object o) {
		super();
		paths = new Vector<>();
		paths.add(ref);
		value = o;
		size = a.size(o.getClass(), o);
	}
}
