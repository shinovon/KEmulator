package com.nttdocomo.fs;

import com.nttdocomo.device.StorageDevice;

import java.io.IOException;

public class Folder {
    protected Folder(StorageDevice paramStorageDevice, AccessToken paramAccessToken, String paramString) {
    }

    public StorageDevice getStorageDevice() {
        return null;
    }

    public AccessToken getAccessToken() {
        return null;
    }

    public String getPath() {
        return null;
    }

    public long getFreeSize()
            throws IOException {
        return 0L;
    }

    public boolean isFileAttributeSupported(Class paramClass) {
        return false;
    }

    public File createFile(String paramString)
            throws IOException {
        return null;
    }

    public File createFile(String paramString, FileAttribute[] paramArrayOfFileAttribute)
            throws IOException {
        return null;
    }

    public File[] getFiles()
            throws IOException {
        return null;
    }

    public File getFile(String paramString)
            throws IOException {
        return null;
    }
}
