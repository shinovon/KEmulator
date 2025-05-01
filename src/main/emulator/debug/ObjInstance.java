package emulator.debug;

public  final class ObjInstance {
	public final String reference;
	public final Object value;
	public final int size;

	ObjInstance(final Memory a, final String ref, final Object o) {
		super();
		this.reference = ref;
		this.value = o;
		this.size = a.size(o.getClass(), o);
	}
}
