package javax.microedition.io.file;

import java.io.*;

public final class FileFilterr implements FileFilter {
	private boolean includeHidden;
	private String filter;

	public FileFilterr(String filter, boolean includeHidden) {
		super();
		this.includeHidden = includeHidden;
		this.filter = filter;
	}

	public boolean accept(File file) {
		if (filter.equalsIgnoreCase("*")) {
			return includeHidden ? true : !file.isHidden();
		}
		return (includeHidden ? true : !file.isHidden()) && file.getName().matches(filter);
	}
}
