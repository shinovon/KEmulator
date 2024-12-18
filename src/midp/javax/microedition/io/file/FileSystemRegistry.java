package javax.microedition.io.file;

import java.util.Enumeration;
import java.util.Vector;

public class FileSystemRegistry {
	static Vector aVector1364 = new Vector();

	public FileSystemRegistry() {
		super();
	}

	public static boolean addFileSystemListener(final FileSystemListener fileSystemListener) {
		if (fileSystemListener == null) {
			throw new NullPointerException();
		}
		FileSystemRegistry.aVector1364.add(fileSystemListener);
		return true;
	}

	public static boolean removeFileSystemListener(final FileSystemListener fileSystemListener) {
		if (fileSystemListener == null) {
			throw new NullPointerException();
		}
		FileSystemRegistry.aVector1364.remove(fileSystemListener);
		return true;
	}

	public static Enumeration listRoots() {
		return new FileSystemRegistryEn();
	}
}
