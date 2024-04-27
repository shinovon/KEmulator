package emulator.graphics2D;

import org.eclipse.swt.graphics.*;
import java.awt.image.*;
import java.awt.*;

public final class c
{
    private static final PaletteData aPaletteData354;
    
    public c() {
        super();
    }
    
    public static BufferedImage method167(final ImageData imageData) {
        final PaletteData palette;
        if ((palette = imageData.palette).isDirect) {
            final DirectColorModel directColorModel = new DirectColorModel(imageData.depth, palette.redMask, palette.greenMask, palette.blueMask);
            final BufferedImage bufferedImage;
            final WritableRaster raster = (bufferedImage = new BufferedImage(directColorModel, directColorModel.createCompatibleWritableRaster(imageData.width, imageData.height), 0 != 0, null)).getRaster();
            final int[] array = new int[3];
            for (int i = 0; i < imageData.height; ++i) {
                for (int j = 0; j < imageData.width; ++j) {
                    final RGB rgb = palette.getRGB(imageData.getPixel(j, i));
                    array[0] = rgb.red;
                    array[1] = rgb.green;
                    array[2] = rgb.blue;
                    raster.setPixels(j, i, 1, 1, array);
                }
            }
            return bufferedImage;
        }
        final RGB[] rgBs;
        final byte[] array2 = new byte[(rgBs = palette.getRGBs()).length];
        final byte[] array3 = new byte[rgBs.length];
        final byte[] array4 = new byte[rgBs.length];
        for (int k = 0; k < rgBs.length; ++k) {
            final RGB rgb2 = rgBs[k];
            array2[k] = (byte)rgb2.red;
            array3[k] = (byte)rgb2.green;
            array4[k] = (byte)rgb2.blue;
        }
        final IndexColorModel indexColorModel = (imageData.transparentPixel != -1) ? new IndexColorModel(imageData.depth, rgBs.length, array2, array3, array4, imageData.transparentPixel) : new IndexColorModel(imageData.depth, rgBs.length, array2, array3, array4);
        final BufferedImage bufferedImage2;
        final WritableRaster raster2 = (bufferedImage2 = new BufferedImage(indexColorModel, indexColorModel.createCompatibleWritableRaster(imageData.width, imageData.height), 0 != 0, null)).getRaster();
        final int[] array5 = { 0 };
        for (int l = 0; l < imageData.height; ++l) {
            for (int n = 0; n < imageData.width; ++n) {
                array5[0] = imageData.getPixel(n, l);
                raster2.setPixel(n, l, array5);
            }
        }
        return bufferedImage2;
    }
    
    private static ImageData method170(final BufferedImage bufferedImage) {
        if (bufferedImage.getColorModel() instanceof DirectColorModel) {
            final DirectColorModel directColorModel = (DirectColorModel)bufferedImage.getColorModel();
            final PaletteData paletteData = new PaletteData(directColorModel.getRedMask(), directColorModel.getGreenMask(), directColorModel.getBlueMask());
            final ImageData imageData = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), directColorModel.getPixelSize(), paletteData);
            final WritableRaster raster = bufferedImage.getRaster();
            final int[] array = new int[4];
            for (int i = 0; i < imageData.height; ++i) {
                for (int j = 0; j < imageData.width; ++j) {
                    raster.getPixel(j, i, array);
                    imageData.setPixel(j, i, paletteData.getPixel(new RGB(array[0], array[1], array[2])));
                    if (directColorModel.hasAlpha()) {
                        imageData.setAlpha(j, i, array[3]);
                    }
                }
            }
            return imageData;
        }
        if (bufferedImage.getColorModel() instanceof IndexColorModel) {
            final IndexColorModel indexColorModel;
            final int mapSize;
            final byte[] array2 = new byte[mapSize = (indexColorModel = (IndexColorModel)bufferedImage.getColorModel()).getMapSize()];
            final byte[] array3 = new byte[mapSize];
            final byte[] array4 = new byte[mapSize];
            indexColorModel.getReds(array2);
            indexColorModel.getGreens(array3);
            indexColorModel.getBlues(array4);
            final RGB[] array5 = new RGB[mapSize];
            for (int k = 0; k < array5.length; ++k) {
                array5[k] = new RGB(array2[k] & 0xFF, array3[k] & 0xFF, array4[k] & 0xFF);
            }
            final ImageData imageData2;
            (imageData2 = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), indexColorModel.getPixelSize(), new PaletteData(array5))).transparentPixel = indexColorModel.getTransparentPixel();
            final WritableRaster raster2 = bufferedImage.getRaster();
            final int[] array6 = { 0 };
            for (int l = 0; l < imageData2.height; ++l) {
                for (int n = 0; n < imageData2.width; ++n) {
                    raster2.getPixel(n, l, array6);
                    imageData2.setPixel(n, l, array6[0]);
                }
            }
            return imageData2;
        }
        return null;
    }
    
    public static BufferedImage method171(final ImageData imageData) {
        final BufferedImage bufferedImage;
        int[] data = ((DataBufferInt)(bufferedImage = new BufferedImage(imageData.width, imageData.height, 2)).getRaster().getDataBuffer()).getData();
        final int n = imageData.width * imageData.height;
        imageData.getPixels(0, 0, n, data, 0);
        if (imageData.alphaData != null) {
            for (int i = n - 1; i >= 0; --i) {
                final RGB rgb = imageData.palette.getRGB(data[i]);
                data[i] = (imageData.alphaData[i] << 24) + ((rgb.red & 0xFF) << 16) + ((rgb.green & 0xFF) << 8) + (rgb.blue & 0xFF);
            }
        }
        else if (imageData.transparentPixel != -1 && !imageData.palette.isDirect) {
            final RGB[] colors = imageData.palette.colors;
            for (int j = n - 1; j >= 0; --j) {
                int[] array;
                int n2;
                int n3;
                if (data[j] == imageData.transparentPixel) {
                    array = data;
                    n2 = j;
                    n3 = 0;
                }
                else {
                    final RGB rgb2 = colors[data[j] % colors.length];
                    array = data;
                    n2 = j;
                    n3 = -16777216 + ((rgb2.red & 0xFF) << 16) + ((rgb2.green & 0xFF) << 8) + (rgb2.blue & 0xFF);
                }
                array[n2] = n3;
            }
        }
        else if (!imageData.palette.isDirect) {
            final RGB[] colors2 = imageData.palette.colors;
            for (int k = n - 1; k >= 0; --k) {
                final RGB rgb3 = imageData.palette.getRGB(data[k] % colors2.length);
                data[k] = -16777216 + ((rgb3.red & 0xFF) << 16) + ((rgb3.green & 0xFF) << 8) + (rgb3.blue & 0xFF);
            }
        }
        else {
            for (int l = n - 1; l >= 0; --l) {
                final RGB rgb4 = imageData.palette.getRGB(data[l]);
                data[l] = -16777216 + ((rgb4.red & 0xFF) << 16) + ((rgb4.green & 0xFF) << 8) + (rgb4.blue & 0xFF);
            }
        }
        data = null;
        System.gc();
        return bufferedImage;
    }
    
    public static ImageData method168(final BufferedImage bufferedImage) {
        if (bufferedImage.getType() != 1) {
            return method170(bufferedImage);
        }
        final ImageData imageData = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), 32, c.aPaletteData354);
        final int[] data = ((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData();
        int n = imageData.data.length - 1;
        for (int i = data.length - 1; i >= 0; --i) {
            imageData.data[n--] = (byte)(data[i] >> 24 & 0xFF);
            imageData.data[n--] = (byte)(data[i] >> 16 & 0xFF);
            imageData.data[n--] = (byte)(data[i] >> 8 & 0xFF);
            imageData.data[n--] = (byte)(data[i] & 0xFF);
        }
        return imageData;
    }
    
    public static void method169(final BufferedImage bufferedImage) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new a(bufferedImage), null);
    }
    
    static {
        aPaletteData354 = new PaletteData(65280, 16711680, -16777216);
    }
}
