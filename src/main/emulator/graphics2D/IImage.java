package emulator.graphics2D;

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
}
