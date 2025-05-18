package emulator.debug;

import java.util.Objects;
import java.util.Vector;

/**
 * Contains comprehensive how to get to found object.
 */
public class ReferencePath {
	/**
	 * Path's root element. Usually it's static class name.
	 */
	public final String root;
	/**
	 * If this is true, {@link #root} will be accessible via static watcher.
	 */
	public final boolean isRootStatic;

	private final Vector<ReferencePathEntry> path = new Vector<>();

	public ReferencePath(String root, boolean isRootStatic) {
		this.root = root;
		this.isRootStatic = isRootStatic;
	}

	/**
	 * Returns copy of this object with new path element appended.
	 *
	 * @param object    Next object.
	 * @param fieldName Name of field or key, where object was found.
	 * @param isIndex   True if fieldName is index/key in array, vector or map.
	 */
	public ReferencePath append(Object object, String fieldName, boolean isIndex) {
		ReferencePath n = new ReferencePath(root, isRootStatic);
		n.path.addAll(path);
		n.path.add(new ReferencePathEntry(object, fieldName, isIndex));
		return n;
	}

	public ReferencePath append(Object object, int index) {
		ReferencePath n = new ReferencePath(root, isRootStatic);
		n.path.addAll(path);
		n.path.add(new ReferencePathEntry(object, String.valueOf(index), true));
		return n;
	}

	public ReferencePathEntry[] getPath() {
		return path.toArray(new ReferencePathEntry[0]);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ReferencePath)) return false;
		ReferencePath that = (ReferencePath) o;
		return isRootStatic == that.isRootStatic && root.equals(that.root) && path.equals(that.path);
	}

	@Override
	public int hashCode() {
		return Objects.hash(root, isRootStatic);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(root);
		for (ReferencePathEntry entry : path) {
			if (!entry.isIndex)
				sb.append('.');
			sb.append(entry);
		}
		return sb.toString();
	}

	public static final class ReferencePathEntry {
		public final Object object;
		public final String fieldName;
		public final boolean isIndex;

		public ReferencePathEntry(Object object, String fieldName, boolean isIndex) {
			this.object = object;
			this.fieldName = fieldName;
			this.isIndex = isIndex;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof ReferencePathEntry)) return false;
			ReferencePathEntry that = (ReferencePathEntry) o;
			return isIndex == that.isIndex && Objects.equals(fieldName, that.fieldName);
		}

		@Override
		public int hashCode() {
			return Objects.hash(fieldName, isIndex);
		}

		@Override
		public String toString() {
			if (isIndex)
				return '[' + fieldName + ']';
			return fieldName;
		}
	}
}
