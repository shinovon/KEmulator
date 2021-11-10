package emulator.custom;

import emulator.*;
import org.apache.tools.zip.*;
import javax.microedition.io.*;
import java.io.*;

public final class CustomJarResources
{
    public CustomJarResources() {
        super();
    }
    
    public static InputStream getResourceStream(final String s) {
        try {
            if (Emulator.midletJar != null) {
                String substring = s;
                if (s.length() > 0 && s.startsWith("/")) {
                    substring = s.substring(1);
                }
                final ZipFile zipFile;
                final ZipEntry entry;
                if ((entry = (zipFile = new ZipFile(Emulator.midletJar)).getEntry(substring)) == null) {
                    Emulator.getEmulator().getLogStream().println("Custom.jar.getResourceStream: " + s + " (null)");
                    throw new IOException();
                }
                final byte[] array = new byte[(int)((java.util.zip.ZipEntry)entry).getSize()];
                Emulator.getEmulator().getLogStream().println("Custom.jar.getResourceStream: " + s + " (" + array.length + ")");
                new DataInputStream(zipFile.getInputStream(entry)).readFully(array);
                return new ByteArrayInputStream(array);
            }
            else {
                final File fileFromClassPath;
                if ((fileFromClassPath = Emulator.getFileFromClassPath(s)) == null || !fileFromClassPath.exists()) {
                    Emulator.getEmulator().getLogStream().println("Custom.path.getResourceStream: " + s + " (null)");
                    throw new IOException();
                }
                final byte[] array2 = new byte[(int)fileFromClassPath.length()];
                Emulator.getEmulator().getLogStream().println("Custom.path.getResourceStream: " + s + " (" + fileFromClassPath.length() + ")");
                new DataInputStream(new FileInputStream(fileFromClassPath)).readFully(array2);
                return new ByteArrayInputStream(array2);
            }
        }
        catch (Exception ex) {
            Emulator.AntiCrack(ex);
            return "".getClass().getResourceAsStream(s);
        }
    }
    
    public static InputStream method808(final Object o, String substring) {
        String s;
        if (substring.length() > 0 && substring.charAt(0) == '/') {
            s = substring.substring(1);
        }
        else {
            if (substring.length() > 1 && substring.charAt(0) == '.' && substring.charAt(1) == '/') {
                substring = substring.substring(2);
            }
            final String name;
            final int lastIndex;
            if ((lastIndex = (name = ((Class)o).getName()).lastIndexOf(46)) < 0) {
                return getResourceStream(substring);
            }
            s = "/" + name.substring(0, lastIndex + 1).replace('.', '/') + substring;
        }
        substring = s;
        return getResourceStream(substring);
    }
    
    public static byte[] getBytes(final String s) throws IOException {
        final InputStream inputStream;
        return getBytes(inputStream = ((s.indexOf(58) != -1) ? ((InputConnection)Connector.open(s, Connector.READ)).openInputStream() : getResourceStream(s)));
    }
    
    public static byte[] getBytes(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        int available;
        if ((available = inputStream.available()) <= 0) {
            available = 128;
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(available);
        final byte[] array = new byte[available];
        int read;
        while ((read = inputStream.read(array)) > 0) {
            byteArrayOutputStream.write(array, 0, read);
        }
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        inputStream.close();
        return byteArray;
    }
    
    public static void write(final InputStream i, OutputStream o) throws IOException {
        if (i == null) {
            throw new IllegalArgumentException("input = null");
        }
        int available;
        if ((available = i.available()) <= 0) {
            available = 128;
        }
        final byte[] buf = new byte[available];
        int read;
        while ((read = i.read(buf)) > 0) {
            o.write(buf, 0, read);
        }
        i.close();
    }

	public static InputStream a(String paramString) {
		return getResourceStream(paramString);
	}
}
