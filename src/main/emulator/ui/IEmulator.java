package emulator.ui;

import emulator.graphics2D.*;
import emulator.graphics3D.*;

public interface IEmulator
{
    IMessage getMessage();
    
    ILogStream getLogStream();
    
    IProperty getProperty();
    
    IScreen getScreen();
    
    int getScreenDepth();
    
    void pushPlugin(final IPlugin p0);
    
    void disposeSubWindows();
    
    IFont newFont(final int p0, final int p1);
    
    IImage newImage(final int p0, final int p1, final boolean p2);
    
    IImage newImage(final int p0, final int p1, final boolean p2, final int p3);
    
    IImage newImage(final byte[] p0);
    
    IGraphics3D getGraphics3D();
    
    void syncValues();
    
    String getAppProperty(final String p0);
}
