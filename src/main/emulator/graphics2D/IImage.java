package emulator.graphics2D;

import org.eclipse.swt.graphics.GC;

public interface IImage
{
    IGraphics2D createGraphics();
    
    IGraphics2D getGraphics();
    
    int getWidth();
    
    int getHeight();
    
    int[] getData();
    
    void setData(final int[] p0);
    
    int getRGB(final int p0, final int p1);
    
    void setAlpha(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    void saveToFile(final String p0);
    
    void copyToClipBoard();
    
    void cloneImage(final IImage p0);

	int size();

	Object getNative();

    void copyToScreen(GC gc);

    void copyToScreen(GC gc, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight);
}
