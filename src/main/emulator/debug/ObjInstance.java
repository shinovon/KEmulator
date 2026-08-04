package emulator.debug;

import java.util.HashSet;
import java.util.Vector;

public  final class ObjInstance {
	public final HashSet<ReferencePath> paths;
	public final Object value;
	public final int size;

	ObjInstance(final Memory a, ClassInfo ci, final ReferencePath ref, final Object o) {
		super();
		paths = new HashSet<>();
		paths.add(ref);
		value = o;
		size = a.size(o.getClass(), ci.cachedFields, o);
	}
}
