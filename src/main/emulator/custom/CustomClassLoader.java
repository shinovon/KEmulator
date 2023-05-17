package emulator.custom;

import emulator.*;
import org.objectweb.asm.*;
import org.apache.tools.zip.*;
import java.io.*;

public final class CustomClassLoader extends ClassLoader
{
    public CustomClassLoader(final ClassLoader classLoader) {
        super(classLoader);
    }
    
    public final synchronized Class loadClass(final String s, final boolean b) throws ClassNotFoundException {
        final Class<?> loadedClass;
        if ((loadedClass = this.findLoadedClass(s)) != null) {
            return loadedClass;
        }
        if (!Emulator.jarClasses.contains(s)) {
            try {
                return super.loadClass(s, b);
            }
            catch (ClassNotFoundException ex) {
                final Class method806;
                if ((method806 = this.method806(s)) == null) {
                    throw ex;
                }
                return method806;
            }
        }
        final Class class1 = this.findClass(s);
        if (b) {
            this.resolveClass(class1);
        }
        return class1;
    }
    
    private Class method806(final String s) {
        Class<?> defineClass = null;
        try {
            for (int i = 0; i < Emulator.jarLibrarys.size(); ++i) {
                final ZipFile zipFile;
                final ZipEntry entry;
                if ((entry = (zipFile = new ZipFile((String)Emulator.jarLibrarys.get(i))).getEntry(s.replace('.', '/') + ".class")) != null) {
                    final ClassReader classReader = new ClassReader(zipFile.getInputStream(entry));
                    final ClassWriter classWriter = new ClassWriter(0);
                    classReader.accept((ClassVisitor)new ClassAdapter((ClassVisitor)classWriter), 0);
                    final byte[] byteArray = classWriter.toByteArray();
                    defineClass = this.defineClass(s, byteArray, 0, byteArray.length);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return defineClass;
    }
    
    protected final Class findClass(final String s) throws ClassNotFoundException {
        Class<?> defineClass;
        try {
            InputStream inputStream;
            if (Emulator.midletJar == null) {
                final File fileFromClassPath;
                if ((fileFromClassPath = Emulator.getFileFromClassPath(s.replace('.', '/') + ".class")) == null || !fileFromClassPath.exists()) {
                    return super.findClass(s);
                }
                inputStream = new FileInputStream(fileFromClassPath);
            }
            else {
                final ZipFile zipFile;
                final ZipEntry entry;
                if ((entry = (zipFile = new ZipFile(Emulator.midletJar)).getEntry(s.replace('.', '/') + ".class")) == null) {
                    return super.findClass(s);
                }
                inputStream = zipFile.getInputStream(entry);
            }
            final ClassReader classReader = new ClassReader(inputStream);
            final ClassWriter classWriter = new ClassWriter(0);
            classReader.accept((ClassVisitor)new CustomClassAdapter((ClassVisitor)classWriter, s), 0);
            final byte[] byteArray = classWriter.toByteArray();
            defineClass = this.defineClass(s, byteArray, 0, byteArray.length);
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
            return super.findClass(s);
        }
        return defineClass;
    }
    
    public final InputStream getResourceAsStream(final String s) {
        return super.getResourceAsStream(s);
    }
}
