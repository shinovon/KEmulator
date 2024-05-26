package javax.microedition.io.file;

import java.util.*;

final class FileSystemRegistryEn implements Enumeration {
	int anInt367;

	FileSystemRegistryEn() {
		super();
		this.anInt367 = 0;
	}

	public final boolean hasMoreElements() {
		return this.anInt367 < 1;
	}

	public final Object nextElement() {
		if (this.anInt367++ < 1) {
			return "root/";
		}
		throw new NoSuchElementException("FileSystemRegistry Enumeration");
	}
}
