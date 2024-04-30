package javax.microedition.m3g;

import emulator.custom.*;

import javax.microedition.io.*;
import java.io.*;
import java.net.*;
import javax.microedition.lcdui.*;

class Helpers {
    private static int[] pixels;

    static InputStream openInputStream(final String s) throws IOException {
        try {
            if (s.charAt(0) == '/') {
                return CustomJarResources.getResourceAsStream(s);
            }
            final InputConnection inputConnection;
            final String headerField;
            if ((inputConnection = (InputConnection) Connector.open(s)) instanceof HttpConnection && (headerField = ((HttpConnection) inputConnection).getHeaderField("Content-Type")) != null && !headerField.equals("application/m3g") && !headerField.equals("image/png")) {
                throw new IOException("Wrong MIME type: " + headerField);
            }
            return inputConnection.openInputStream();
        } catch (MalformedURLException ex) {
            throw new IOException("Malformed URL: \"" + s + "\"");
        }
    }

    static int getTranslateX(final Graphics graphics) {
        return graphics.getTranslateX();
    }

    static int getTranslateY(final Graphics graphics) {
        return graphics.getTranslateY();
    }

    static int getClipX(final Graphics graphics) {
        return graphics.getClipX();
    }

    static int getClipY(final Graphics graphics) {
        return graphics.getClipY();
    }

    static int getClipWidth(final Graphics graphics) {
        return graphics.getClipWidth();
    }

    static int getClipHeight(final Graphics graphics) {
        return graphics.getClipHeight();
    }

    static void bindTarget(final Graphics3D graphics3D, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        bindTarget(graphics3D, graphics.getImage().getWidth(), graphics.getImage().getHeight(), Helpers.pixels = graphics.getImage().getData(), n, n2, n3, n4);
    }

    static void releaseTarget(final Graphics3D graphics3D, final Graphics graphics) {
        graphics.getImage().setData(Helpers.pixels);
        Helpers.pixels = null;
    }

    private static native void bindTarget(final Graphics3D p0, final int p1, final int p2, final int[] p3, final int p4, final int p5, final int p6, final int p7);

    static Image createImage(final byte[] array, final int n, final int n2) throws IOException {
        throw new IOException("Corrupt PNG or unsupported image type");
    }

    static Image createImage(final String s) throws IOException {
        throw new IOException("Corrupt PNG or unsupported image type");
    }

    static int getImageWidth(final Image image) {
        return image.getWidth();
    }

    static int getImageHeight(final Image image) {
        return image.getHeight();
    }

    static boolean isOpaque(final Image image) {
        return false;
    }

    static void getImageRGB(final Image image, final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        image.getRGB(array, n, n2, n3, n4, n5, n6);
    }

    private Helpers() {
        super();
    }

    static {
        Helpers.pixels = null;
    }
}
