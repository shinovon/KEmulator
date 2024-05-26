package javax.microedition.media.control;

import javax.microedition.media.*;

public interface MetaDataControl extends Control {
	public static final String AUTHOR_KEY = "author";
	public static final String COPYRIGHT_KEY = "copyright";
	public static final String DATE_KEY = "date";
	public static final String TITLE_KEY = "title";

	String[] getKeys();

	String getKeyValue(final String p0);
}
