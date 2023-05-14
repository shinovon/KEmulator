package javax.microedition.io.file;

import emulator.*;
import java.util.*;
import java.io.*;

public class FileConnectionImpl implements FileConnection
{
    private String origUrl;
    private String systemPath;
    private File file;
    private boolean closed;
    private static String aString441;
    private boolean aBoolean440;
    private String aString442;
    
    public FileConnectionImpl(String url) throws IOException {
        super();
    	if(url.length() > 300) {
    		Thread.dumpStack();
    		throw new IOException("Path too long");
    	}
        this.origUrl = url;
        this.closed = false;
        final File file;
        if (!(file = new File(FileConnectionImpl.aString441)).exists() || file.isFile()) {
            file.mkdirs();
        }
        url = method216(url = method216(url = method216(url = (url = url.replaceFirst("localhost", "")).substring("file://".length()), "c"), "d"), "e");
        if(url.startsWith("/"))
        	url = url.substring(1);
        this.systemPath = Emulator.getAbsolutePath() + "/file/" + url;
        //method134(this.aString439 = Emulator.getAbsolutePath() + "/file/" + aString314);
        this.file = new File(this.systemPath);
    }
    
    private static void method134(final String s) {
        final int length = (Emulator.getAbsolutePath() + "/file/").length();
        String s2 = s;
        String s3 = "/";
        int n = length;
        int index;
        while ((index = s2.indexOf(s3, n)) >= 0 && index >= length) {
            final File file;
            if (!(file = new File(s.substring(0, index))).exists() && new File(s).isFile()) {
                file.mkdirs();
            }
            s2 = s;
            s3 = "/";
            n = index + 1;
        }
    }
    
    private static String method216(final String s, final String s2) {
        String replaceFirst = s;
        if (s.indexOf(s2 + ":") != -1) {
            final File file;
            if (!(file = new File(Emulator.getAbsolutePath() + "/file/" + s2 + "/")).exists() || file.isFile()) {
                file.mkdirs();
            }
            replaceFirst = s.replaceFirst(s2 + ":", s2);
        }
        return replaceFirst;
    }
    
    public long availableSize() {
    	if(file == null) {
    		return 100000000L;
    	}
    	return file.getFreeSpace();
    }
    
    public boolean canRead() {
        return true;
    }
    
    public boolean canWrite() {
        return true;
    }
    
    public void create() throws IOException {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        if (this.file.exists() || this.file.isDirectory()) {
            throw new IOException("File already exists");
        }
        if(!this.file.getParentFile().exists())
        	this.file.getParentFile().mkdirs();
        this.file.createNewFile();
    }
    
    public void delete() throws IOException {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        if (!this.file.exists() || this.file.isDirectory()) {
            throw new IOException();
        }
        this.file.delete();
    }
    
    public long directorySize(final boolean b) throws IOException {
        if (this.file.isFile()) {
            throw new IOException();
        }
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        return this.method217(b);
    }
    
    private int method217(final boolean b) {
        int n = 0;
        if (this.file.isDirectory()) {
            final File[] listFiles = this.file.listFiles();
            for (int i = 0; i < listFiles.length; ++i) {
                int n2;
                if (listFiles[i].isFile()) {
                    n2 = (int)(n + listFiles[i].length());
                }
                else {
                    if (!b) {
                        continue;
                    }
                    n2 = n + this.method217(true);
                }
                n = n2;
            }
        }
        return n;
    }
    
    public boolean exists() {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        return this.file.exists();
    }
    
    public long fileSize() throws IOException {
        if (this.file.isDirectory()) {
            throw new IOException();
        }
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        return this.file.length();
    }
    
    public String getName() {
        return this.file.getName();
    }
    
    public String getPath() {
        return this.origUrl.replaceFirst("localhost", "").substring("file://".length());
    }
    
    public String getURL() {
        return this.origUrl;
    }
    
    public boolean isDirectory() {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        return this.file.isDirectory();
    }
    
    public boolean isHidden() {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        return this.file.isHidden();
    }
    
    public boolean isOpen() {
        return !this.closed;
    }
    
    public long lastModified() {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        return this.file.lastModified();
    }
    
    public Enumeration list() throws IOException {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        if (!this.file.exists() || this.file.isFile()) {
            throw new IOException();
        }
        final File[] listFiles;
        if ((listFiles = this.file.listFiles()) != null) {
            return new FileConnectionListEn2(this, listFiles);
        }
        return null;
    }
    
    public Enumeration list(final String s, final boolean b) throws IOException {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        if (!this.file.exists() || this.file.isFile()) {
            throw new IOException();
        }
        final File[] listFiles = this.file.listFiles(new FileFilterr(s, b));
        if (listFiles != null) {
            return new FileConnectionListEn(this, listFiles);
        }
        return null;
    }
    
    public void mkdir() throws IOException {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        if (this.file.exists() || this.file.isFile()) {
            throw new IOException();
        }
        this.file.mkdir();
    }
    
    public DataInputStream openDataInputStream() throws IOException {
        if (!this.file.exists() || this.file.isDirectory()) {
            throw new IOException();
        }
        return new DataInputStream(new FileInputStream(this.file));
    }
    
    public DataOutputStream openDataOutputStream() throws IOException {
        if (!this.file.exists() || this.file.isDirectory()) {
            throw new IOException(!this.file.exists() ? "File doesn't exist" : "No access to file");
        }
        return new DataOutputStream(new FileOutputStream(this.file));
    }
    
    public InputStream openInputStream() throws IOException {
        if (!this.file.exists() || this.file.isDirectory()) {
            throw new IOException();
        }
        return new FileInputStream(this.file);
    }
    
    public OutputStream openOutputStream() throws IOException {
        if (!this.file.exists() || this.file.isDirectory()) {
            throw new IOException();
        }
        return new FileOutputStream(this.file);
    }
    
    public OutputStream openOutputStream(final long n) throws IOException {
        if (!this.file.exists() || this.file.isDirectory()) {
            throw new IOException();
        }
        final FileInputStream fileInputStream = new FileInputStream(this.file);
        final byte[] array = new byte[(int)Math.min(n, fileInputStream.available())];
        fileInputStream.read(array);
        final FileOutputStream fileOutputStream;
        (fileOutputStream = new FileOutputStream(this.file)).write(array);
        fileOutputStream.flush();
        return fileOutputStream;
    }
    
    public void rename(final String s) throws IOException {
        if (s == null) {
            throw new NullPointerException();
        }
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        this.file.renameTo(new File(this.file.getParent() + "//" + s));
    }
    
    public void setFileConnection(final String s) throws IOException {
        if (s == null) {
            throw new NullPointerException();
        }
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        final File file;
        if (!(file = new File(this.file + "//" + s)).exists()) {
            throw new IllegalArgumentException();
        }
        this.origUrl = "file://" + file.getAbsolutePath().substring(FileConnectionImpl.aString441.length());
        this.closed = false;
        this.systemPath = file.getAbsolutePath();
        this.file = new File(this.systemPath);
    }
    
    public void setHidden(final boolean b) throws IOException {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        if (!this.file.exists()) {
            throw new IOException();
        }
    }
    
    public void setReadable(final boolean b) throws IOException {
    }
    
    public void setWritable(final boolean b) throws IOException {
    }
    
    public long totalSize() {
        return 100000000L;
    }
    
    public void truncate(final long n) throws IOException {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        if (!this.file.exists() || this.file.isDirectory()) {
            throw new IOException();
        }
        final FileInputStream fileInputStream = new FileInputStream(this.file);
        final byte[] array = new byte[(int)Math.min(n, fileInputStream.available())];
        fileInputStream.read(array);
        fileInputStream.close();
        final FileOutputStream fileOutputStream;
        (fileOutputStream = new FileOutputStream(this.file)).write(array);
        fileOutputStream.flush();
        fileOutputStream.close();
    }
    
    public long usedSize() {
        return 1000000L;
    }
    
    public void close() throws IOException {
        this.closed = true;
    }
    
    static {
        FileConnectionImpl.aString441 = Emulator.getAbsolutePath() + "/file/" + "root/";
    }

	public String getRealPath() {
		return file.toString();
	}

	public File getFile() {
		return file;
	}
}
