package emulator.graphics3D;

import java.util.*;
import javax.microedition.m3g.*;
import javax.microedition.lcdui.*;

public interface IGraphics3D {
    void setHints(final int p0);

    int getHints();

    void enableDepthBuffer(final boolean p0);

    boolean isDepthBufferEnabled();

    void bindTarget(final Object p0);

    void releaseTarget();

    void setViewport(final int p0, final int p1, final int p2, final int p3);

    void setDepthRange(final float p0, final float p1);

    void clearBackgound(final Object p0);

    Hashtable getProperties();

    void render(final VertexBuffer p0, final IndexBuffer p1, final Appearance p2, final Transform p3, final int p4);

    void render(final Node p0, final Transform p1);

    void render(final World p0);

    void v3bind(final Graphics p0);

    void v3release(final Graphics p0);

    void v3flush();
}
