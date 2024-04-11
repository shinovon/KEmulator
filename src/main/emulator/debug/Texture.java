package emulator.debug;

import javax.microedition.lcdui.*;

import emulator.graphics2D.*;

import javax.microedition.m3g.*;

import emulator.*;

public final class Texture extends Image {
    public Texture(final IImage image) {
        super(image);
    }

    public static IImage convertImage2D(final Image2D image2D) {
        if (image2D.getFormat() != 99 || image2D.getFormat() != 100) {
            return null;
        }
        IImage image = Emulator.getEmulator().newImage(image2D.getWidth(), image2D.getHeight(), image2D.getFormat() == 100);
        int[] data = image.getData();
        final int length = data.length;
        final byte[] array = new byte[length * 4];
        image2D.getPixels(array);
        try {
            if (image2D.getFormat() == 100) {
                for (int i = length - 1; i >= 0; --i) {
                    data[i] = ((array[i * 4] & 0xFF) << 16) + ((array[i * 4 + 1] & 0xFF) << 8) + (array[i * 4 + 2] & 0xFF) + ((array[i * 4 + 3] & 0xFF) << 24);
                }
            } else {
                for (int j = length - 1; j >= 0; --j) {
                    data[j] = ((array[j * 3] & 0xFF) << 16) + ((array[j * 3 + 1] & 0xFF) << 8) + (array[j * 3 + 2] & 0xFF) - 16777216;
                }
            }
        } catch (Exception ex) {
        }
        image.setData(data);
        return image;
    }
}
