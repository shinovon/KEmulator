package emulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.file.ConnectionClosedException;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileConnectionImpl;

public class DirectFileConnectionImpl implements FileConnection {
	
    private File file;
    private String string;
	private boolean closed;

	public DirectFileConnectionImpl(String string) {
        super();
        this.string = string;
        file = new File(string);
        this.closed = false;
    }
	
    public void close() throws IOException {
        this.closed = true;
    }
	@Override
	public boolean isOpen() {
		return !this.closed;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return new FileInputStream(file);
	}
	
    public DataInputStream openDataInputStream() throws IOException {
        if (!this.file.exists() || this.file.isDirectory()) {
            throw new IOException();
        }
        return new DataInputStream(new FileInputStream(this.file));
    }
    
    public DataOutputStream openDataOutputStream() throws IOException {
        if (!this.file.exists() || this.file.isDirectory()) {
            throw new IOException();
        }
        return new DataOutputStream(new FileOutputStream(this.file));
    }

	@Override
	public OutputStream openOutputStream() throws IOException {
		return new FileOutputStream(file);
	}
    
    public OutputStream openOutputStream(final long n) throws IOException {
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
        if (!(file = new File(this.file.getParent() + "//" + s)).exists()) {
            throw new IllegalArgumentException();
        }
        this.closed = false;
        this.string = file.getAbsolutePath();
        this.file = new File(this.string);
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
    	file.setReadable(b);
    }
    
    public void setWritable(final boolean b) throws IOException {
    	file.setWritable(b);
    }
    
    public long totalSize() {
        return file.getTotalSpace();
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
        return file.getTotalSpace() - file.getFreeSpace();
    }

	@Override
	public long availableSize() {
        return file.getFreeSpace();
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
    
    public Enumeration list() throws IOException {
    	/*
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        if (!this.file.exists() || this.file.isFile()) {
            throw new IOException();
        }
        final File[] listFiles;
        if ((listFiles = this.file.listFiles()) != null) {
            return new d(this, listFiles);
        }
        return null;
        */
    	throw new IOException("Not implemented");
    }
    
    public Enumeration list(final String s, final boolean b) throws IOException {
    	/*
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        if (!this.file.exists() || this.file.isFile()) {
            throw new IOException();
        }
        final File[] listFiles;
        if ((listFiles = this.file.listFiles(new c(this))) != null) {
            return new b(this, listFiles);
        }
        return null;*/
    	throw new IOException("Not implemented");
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

	@Override
	public boolean canRead() {
		return file.canRead();
	}

	@Override
	public boolean canWrite() {
		return file.canWrite();
	}

	@Override
	public void create() throws IOException {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        if (this.file.exists() || this.file.isDirectory()) {
            throw new IOException();
        }
		file.createNewFile();
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

	@Override
	public void delete() throws IOException {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        if (this.file.exists() || this.file.isDirectory()) {
            throw new IOException();
        }
		file.delete();
	}

	@Override
	public String getName() {
        return this.file.getName();
	}

	@Override
	public String getPath() {
		return file.getPath();
	}

	@Override
	public String getURL() {
		return string;
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
    
    public long lastModified() {
        if (this.closed) {
            throw new ConnectionClosedException();
        }
        return this.file.lastModified();
    }

}
