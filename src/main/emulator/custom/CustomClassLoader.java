package emulator.custom;

import emulator.*;
import org.objectweb.asm.*;
import org.apache.tools.zip.*;

import java.io.*;

public final class CustomClassLoader extends ClassLoader {
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
            } catch (ClassNotFoundException ex) {
                final Class method806;
                if ((method806 = this.loadLibraryClass(s)) == null) {
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

    private Class loadLibraryClass(final String s) {
        Class<?> defineClass = null;
        try {
            for (int i = 0; i < Emulator.jarLibrarys.size(); ++i) {
                final ZipFile zipFile;
                final ZipEntry entry;
                if ((entry = (zipFile = new ZipFile((String) Emulator.jarLibrarys.get(i))).getEntry(s.replace('.', '/') + ".class")) != null) {
                    final ClassReader classReader = new ClassReader(zipFile.getInputStream(entry));
                    final ClassWriter classWriter = new ClassWriter(0);
                    classReader.accept(new ClassVisitor(Opcodes.ASM4, classWriter) {}, Settings.asmSkipDebug ? ClassReader.SKIP_DEBUG : 0);
                    final byte[] byteArray = classWriter.toByteArray();
                    defineClass = this.defineClass(s, byteArray, 0, byteArray.length);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return defineClass;
    }

    protected final Class findClass(final String s) throws ClassNotFoundException {
        Class<?> defineClass;
        try {
            byte[] bytes;
            try {
                bytes = load(s);
            } catch (ArrayIndexOutOfBoundsException e) {
                if(Settings.asmSkipDebug) throw e;
                Settings.asmSkipDebug = true;
                bytes = load(s);
            }
            defineClass = this.defineClass(s, bytes, 0, bytes.length);
        } catch (ClassNotFoundException e) {
            return super.findClass(s);
        } catch (Exception e) {
            e.printStackTrace();
            return super.findClass(s);
        }
        return defineClass;
    }

    private byte[] load(String s) throws Exception {
        InputStream inputStream;
        if (Emulator.midletJar == null) {
            final File fileFromClassPath;
            if ((fileFromClassPath = Emulator.getFileFromClassPath(s.replace('.', '/') + ".class")) == null || !fileFromClassPath.exists()) {
                throw new ClassNotFoundException();
            }
            inputStream = new FileInputStream(fileFromClassPath);
        } else {
            final ZipFile zipFile;
            final ZipEntry entry;
            if ((entry = (zipFile = new ZipFile(Emulator.midletJar)).getEntry(s.replace('.', '/') + ".class")) == null) {
                throw new ClassNotFoundException();
            }
            inputStream = zipFile.getInputStream(entry);
        }

        final ClassReader classReader = new ClassReader(inputStream);
        final ClassWriter classWriter = new ClassWriter(0);
        try {
            classReader.accept(new CustomClassAdapter(classWriter, s), Settings.asmSkipDebug ? ClassReader.SKIP_DEBUG : 0);
        } finally {
            inputStream.close();
        }
        return classWriter.toByteArray();
    }

    public final InputStream getResourceAsStream(final String s) {
        return super.getResourceAsStream(s);
    }
}
