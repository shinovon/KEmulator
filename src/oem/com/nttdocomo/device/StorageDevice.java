package com.nttdocomo.device;

import com.nttdocomo.fs.AccessToken;
import com.nttdocomo.fs.Folder;
import com.nttdocomo.lang.IllegalStateException;

import java.io.IOException;

public class StorageDevice {
    public static final String CATEGORY_HARDWARE = "hardware";
    public static final String CAPABILITY_SD = "SD";
    public static final String CAPABILITY_MINISD = "miniSD";
    public static final String CATEGORY_FILESYSTEM = "filesystem";
    public static final String CAPABILITY_FAT12 = "FAT12";
    public static final String CAPABILITY_FAT16 = "FAT16";
    public static final String CAPABILITY_FAT32 = "FAT32";
    public static final String CAPABILITY_FAT_LONG_NAME = "FAT_LONG_NAME";
    public static final String CATEGORY_ENCRYPTION = "encryption";
    public static final String CAPABILITY_SD_BINDING = "SD-Binding";

    public static StorageDevice getInstance(String paramString)
            throws IllegalStateException, DeviceException {
        return null;
    }

    private StorageDevice(String paramString, Object paramObject) {
    }

    public String getDeviceName() {
        return null;
    }

    public String getPrintName() {
        return null;
    }

    public boolean isRemovable() {
        return false;
    }

    public boolean isAccessible() {
        return false;
    }

    public boolean isReadable() {
        return false;
    }

    public boolean isWritable() {
        return false;
    }

    public String[] getCapability(String paramString)
            throws IOException {
        return null;
    }

    public String getMediaId()
            throws IOException {
        return null;
    }

    public Folder getFolder(AccessToken paramAccessToken)
            throws IOException {
        return null;
    }
}
