package javax.microedition.io.file;

import java.io.*;
import java.util.*;

final class FileConnectionListEn implements Enumeration
{
    int index;
    private final File[] files;
    
    FileConnectionListEn(final FileConnectionImpl fileConnectionImpl, final File[] files) {
        super();
        this.files = files;
        this.index = 0;
    }
    
    public final boolean hasMoreElements() {
        return this.index < this.files.length;
    }
    
    public final Object nextElement() {
        if (this.index < this.files.length) {
            Object obj = (String)(this.files[this.index].getName() + (this.files[this.index].isDirectory() ? "/" : ""));
            this.index++;
            return obj;
        }
        throw new NoSuchElementException("FileConection.list Enumeration");
    }
}
