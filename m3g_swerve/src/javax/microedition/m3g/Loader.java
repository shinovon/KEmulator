package javax.microedition.m3g;

import emulator.i;

import javax.microedition.lcdui.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class Loader {
    int swerveHandle;
    static final int ERROR_NONE = -1;
    static final int ERROR_ABORT = -2;
    static final int ERROR_NOTFOUND = -3;
    static final int ERROR_READ = -4;
    static final int ERROR_UNABLETOCREATE = -5;
    static final int ERROR_UNABLETOFREE = -6;
    static final int ERROR_INVALIDHEADER = -7;
    static final int ERROR_INVALIDBODY = -8;
    static final int ERROR_INVALIDCHECKSUM = -9;
    static final int ERROR_UNKNOWN = -10;
    static final int ERROR_DATAPASTEOF = -11;
    static byte[] pngIdentifier;
    static int m3gIdentifierLength;
    static int BLOCK_SIZE;

    protected native void finalize();

    Loader() {
        super();
    }

    Loader(final int swerveHandle) {
        super();
        this.swerveHandle = swerveHandle;
    }

    private static native int createImpl();

    native boolean getbContainsExtensions();

    native int onDataStart(final boolean p0);

    native int onDataEnd();

    native int onData(final byte[] p0, final int p1, final int p2);

    native void onError(final int p0);

    native int getRootCount();

    private native int getRootImpl(final int p0);

    native int getXREFName(final byte[] p0);

    native void resolveXREF(final Object3D p0);

    private static native int getImage2DEncoding(final Image p0);

    native int getAuthoringField(final byte[] p0);

    public static Object3D[] load(final byte[] array, final int n) throws IOException {
        if (n < 0 || n >= array.length) {
            throw new IndexOutOfBoundsException();
        }
        return loadHelper(array, n, true);
    }

    private static Object3D[] loadHelper(final byte[] array, int n, final boolean b) throws IOException {
        try {
            final Loader loader = (Loader) Engine.instantiateJavaPeer(createImpl());
            final int n2 = n;
            final byte[] array2 = new byte[Loader.m3gIdentifierLength];
            int n3;
            try {
                n3 = loader.onDataStart(b);
                int n4 = array.length - n;
                if (n3 == -1 && n4 > Loader.m3gIdentifierLength) {
                    if ((n3 = loader.onData(array, n, Loader.m3gIdentifierLength)) == -1) {
                        System.arraycopy(array, n, array2, 0, Loader.m3gIdentifierLength);
                    }
                    n4 -= Loader.m3gIdentifierLength;
                    n += Loader.m3gIdentifierLength;
                }
                if (n3 == -1) {
                    n3 = loader.onData(array, n, n4);
                }
                if (n3 >= 0) {
                    loader.processXREFs(null);
                    n += n3;
                    n3 = loader.onData(array, n, n4 - n3);
                }
                if (n3 == -11) {
                    n3 = -1;
                }
                if (n3 == -1) {
                    n3 = loader.onDataEnd();
                }
            } catch (SecurityException ex) {
                loader.onError(-10);
                if (ex.getMessage() != null) {
                    throw new SecurityException(ex.getMessage());
                }
                throw new SecurityException();
            } catch (Exception ex2) {
                ex2.printStackTrace();
                loader.onError(-10);
                if (ex2.getMessage() != null) {
                    throw new IOException(ex2.getMessage());
                }
                throw new IOException();
            }
            if (n3 == -1) {
                final Object3D[] roots;
                if ((roots = loader.createRoots()) != null) {
                    adjustUserObject(roots[0], "javax.microedition.m3g.Loader.Header", array2);
                }
                return roots;
            }
            Image2D image2D;
            if ((image2D = createPNGImage2D(array, n2, array.length)) == null) {
                image2D = createImage2D(array, n2, array.length);
            }
            if (image2D != null) {
                return new Object3D[]{image2D};
            }
            return null;
        } catch (SecurityException ex4) {
            final SecurityException ex3 = ex4;
            if (ex4.getMessage() != null) {
                throw new SecurityException(ex3.getMessage());
            }
            throw new SecurityException();
        } catch (Exception ex6) {
            ex6.printStackTrace();
            final Exception ex5 = ex6;
            if (ex6.getMessage() != null) {
                throw new IOException(ex5.getMessage());
            }
            throw new IOException();
        }
    }

    public static Object3D[] load(final String s) throws IOException {
        if (s == null) {
            throw new NullPointerException();
        }
        return loadHelper(s, true);
    }

    private static Object3D[] loadHelper(final String s, final boolean b) throws IOException {
        InputStream inputStream = null;
        try {
            final Loader loader = (Loader) Engine.instantiateJavaPeer(createImpl());
            final byte[] array = new byte[Loader.m3gIdentifierLength];
            int n;
            try {
                if ((inputStream = loader.getClass().getResourceAsStream(s)) == null) {
                    inputStream = openInputStream(s);
                }
                n = loader.onDataStart(b);
                final byte[] array2 = new byte[Loader.BLOCK_SIZE];
                final int read;
                if (n == -1 && (read = inputStream.read(array2, 0, Loader.m3gIdentifierLength)) != -1 && (n = loader.onData(array2, 0, read)) == -1) {
                    System.arraycopy(array2, 0, array, 0, Loader.m3gIdentifierLength);
                }
                int read2;
                while (n == -1 && (read2 = inputStream.read(array2, 0, Loader.BLOCK_SIZE)) != -1) {
                    if ((n = loader.onData(array2, 0, read2)) >= 0) {
                        loader.processXREFs(s);
                        n = loader.onData(array2, n, read2 - n);
                    }
                }
                if (n == -1) {
                    n = loader.onDataEnd();
                }
            } catch (SecurityException ex) {
                loader.onError(-10);
                if (ex.getMessage() != null) {
                    throw new SecurityException(ex.getMessage());
                }
                throw new SecurityException();
            } catch (Exception ex2) {
                ex2.printStackTrace();
                loader.onError(-10);
                ex2.printStackTrace();
                if (ex2.getMessage() != null) {
                    throw new IOException(ex2.getMessage());
                }
                throw new IOException();
            }
            if (n == -1) {
                final Object3D[] roots;
                if ((roots = loader.createRoots()) != null) {
                    adjustUserObject(roots[0], "javax.microedition.m3g.Loader.Header", array);
                }
                return roots;
            }
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            Image2D image2D;
            if ((image2D = createPNGImage2D(s)) == null) {
                image2D = createImage2D(s);
            }
            if (image2D != null) {
                return new Object3D[]{image2D};
            }
            return null;
        } catch (SecurityException ex4) {
            final SecurityException ex3 = ex4;
            if (ex4.getMessage() != null) {
                throw new SecurityException(ex3.getMessage());
            }
            throw new SecurityException();
        } catch (Exception ex6) {
            ex6.printStackTrace();
            final Exception ex5 = ex6;
            if (ex6.getMessage() != null) {
                throw new IOException(ex5.getMessage());
            }
            throw new IOException();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private void processXREFs(final String s) throws IOException {
        byte[] array = null;
        int i;
        do {
            if ((i = this.getXREFName(null)) != -1) {
                if (array == null || array.length != i) {
                    array = new byte[i];
                }
                this.getXREFName(array);
                String resolveURI;
                if (isRelative(resolveURI = new String(array, "UTF-8"))) {
                    if (s == null) {
                        throw new IOException("XREF resource [" + resolveURI + "] has relative URI.");
                    }
                    resolveURI = resolveURI(s, resolveURI);
                }
                final Object3D[] loadHelper;
                if ((loadHelper = loadHelper(resolveURI, this.getbContainsExtensions())) == null) {
                    throw new IOException("XREF resource [" + resolveURI + "] contained no roots.");
                }
                adjustUserObject(loadHelper[0], "javax.microedition.m3g.Loader.ExternalReference.URI", array);
                this.resolveXREF(loadHelper[0]);
            }
        } while (i != -1);
    }

    private static boolean isRelative(final String s) {
        final int length;
        if ((length = s.length()) == 0 || s.startsWith("//")) {
            return true;
        }
        if (s.startsWith("/")) {
            return false;
        }
        int n = 0;
        char char1;
        while ((char1 = s.charAt(n)) != ':' && char1 != '/' && char1 != '?' && char1 != '#' && ++n != length) {
        }
        return char1 != ':';
    }

    private static String resolveURI(final String s, final String s2) {
        int length;
        for (length = s.length(); length > 0 && s.charAt(length - 1) != '/'; --length) {
        }
        if (length > 0) {
            return s.substring(0, length) + s2;
        }
        return '/' + s2;
    }

    private Object3D[] createRoots() {
        int rootCount;
        if ((rootCount = this.getRootCount()) == 0) {
            return null;
        }
        final Object3D[] array = new Object3D[rootCount];
        while (rootCount-- > 0) {
            array[rootCount] = (Object3D) Engine.instantiateJavaPeer(this.getRootImpl(rootCount));
        }
        final byte[] array2 = new byte[this.getAuthoringField(null)];
        this.getAuthoringField(array2);
        adjustUserObject(array[0], "javax.microedition.m3g.Loader.HeaderObject.AuthoringField", array2);
        return array;
    }

    static void adjustUserObject(final Object3D object3D, final String s, final Object o) {
        Object userObject;
        if ((userObject = object3D.getUserObject()) == null) {
            userObject = new Hashtable<String, Object>();
            object3D.setUserObject(userObject);
        }
        ((Hashtable<String, Object>) userObject).put(s, o);
    }

    static InputStream openInputStream(final String s) throws IOException {
        return Helpers.openInputStream(s);
    }

    private static boolean hasPNGIdentifier(final byte[] array, final int n, final int n2) {
        if (Loader.pngIdentifier.length > n2) {
            return false;
        }
        for (int i = 0; i < Loader.pngIdentifier.length; ++i) {
            if (Loader.pngIdentifier[i] != array[n + i]) {
                return false;
            }
        }
        return true;
    }

    static Image2D createPNGImage2D(final String s) throws IOException {
        InputStream inputStream = null;
        try {
            if ((inputStream = (Loader.class.getResourceAsStream(s))) == null) {
                inputStream = openInputStream(s);
            }
            final byte[] array = new byte[Loader.BLOCK_SIZE];
            final int read;
            if ((read = inputStream.read(array, 0, Loader.pngIdentifier.length)) != -1 && !hasPNGIdentifier(array, 0, read)) {
                return null;
            }
            byte[] array2 = new byte[read];
            System.arraycopy(array, 0, array2, 0, read);
            int read2;
            while ((read2 = inputStream.read(array, 0, Loader.BLOCK_SIZE)) != -1) {
                final byte[] array3 = new byte[array2.length + read2];
                System.arraycopy(array2, 0, array3, 0, array2.length);
                System.arraycopy(array, 0, array3, array2.length, read2);
                array2 = array3;
            }
            return new Image2D(array2, 0, array2.length);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    static Image2D createPNGImage2D(final byte[] array, final int n, final int n2) {
        if (!hasPNGIdentifier(array, n, n2)) {
            return null;
        }
        return new Image2D(array, n, n2);
    }

    static Image2D createImage2D(final String s) throws IOException {
        final Image image;
        if ((image = Helpers.createImage(s)) != null) {
            return createImage2D(image);
        }
        return null;
    }

    static Image2D createImage2D(final byte[] array, final int n, final int n2) throws IOException {
        final Image image;
        if ((image = Helpers.createImage(array, n, n2)) != null) {
            return createImage2D(image);
        }
        return null;
    }

    static Image2D createImage2D(final Image image) {
        return new Image2D(getImage2DEncoding(image), image);
    }

    static {
        i.a("jsr184client");
        Engine.cacheFID(Loader.class, 2);
        Loader.pngIdentifier = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
        Loader.m3gIdentifierLength = 12;
        Loader.BLOCK_SIZE = 4096;
    }
}
