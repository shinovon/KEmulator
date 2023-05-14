package javax.microedition.io.file;

import java.io.*;
import java.util.*;

final class FileConnectionListEn2 implements Enumeration
{
    int ind;
    private final File[] files;
    
    FileConnectionListEn2(final FileConnectionImpl fileConnectionImpl, final File[] aFileArray374) {
        super();
        this.files = aFileArray374;
        this.ind = 0;
    }
    
    public final boolean hasMoreElements() {
        return this.ind < this.files.length;
    }
    
    public final Object nextElement() {
        if (this.ind < this.files.length) {
            return this.files[this.ind].getName() + (this.files[this.ind++].isDirectory() ? "/" : "");
        }
        throw new NoSuchElementException("FileConection.list Enumeration");
    }
}
