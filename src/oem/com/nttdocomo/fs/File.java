package com.nttdocomo.fs;

import com.nttdocomo.io.FileEntity;

import java.io.IOException;

public class File {
    public static final int MODE_READ_ONLY = 0;
    public static final int MODE_WRITE_ONLY = 1;
    public static final int MODE_READ_WRITE = 2;

    private File(Folder paramFolder, String paramString) {
    }

    public Folder getFolder() {
        return null;
    }

    public AccessToken getAccessToken() {
        return null;
    }

    public String getPath() {
        return null;
    }

    public FileEntity open(int paramInt)
            throws IOException {
        return null;
    }

    public void delete()
            throws IOException {
    }

    public long getLength()
            throws IOException {
        return 0L;
    }

    public long getLastModified()
            throws IOException {
        return 0L;
    }
}
