package ru.nnproject.kemulator.filemanagerapi;

import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.file.FileConnection;

public interface AbstractFileManager {

    public boolean openFile();

    public boolean openFile(String path);

    public boolean openFolder();

    public boolean openFolder(String path);

    public boolean saveFile();

    public boolean saveFile(String path);

    public void setFilterExtensions(String[] extensions, String description);

    public void setFilterExtension(String ext, String description);

    public String getPath();

    public OutputStream getOutputStream() throws FileManagerException;

    public InputStream getInputStream() throws FileManagerException;

    public FileConnection getFileConnection() throws FileManagerException;

    public String getFileData() throws FileManagerException;

}
