package javax.microedition.lcdui.game;

import javax.microedition.lcdui.*;

public class LayerManager
{
    private int anInt430;
    private Layer[] aLayerArray431;
    private int anInt432;
    private int anInt433;
    private int anInt434;
    private int anInt435;
    
    public LayerManager() {
        super();
        this.aLayerArray431 = new Layer[4];
        this.setViewWindow(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public void append(final Layer layer) {
        this.method214(layer);
        this.method213(layer, this.anInt430);
    }
    
    public void insert(final Layer layer, final int n) {
        if (n < 0 || n > this.anInt430) {
            throw new IndexOutOfBoundsException();
        }
        this.method214(layer);
        this.method213(layer, n);
    }
    
    public Layer getLayerAt(final int n) {
        if (n < 0 || n >= this.anInt430) {
            throw new IndexOutOfBoundsException();
        }
        return this.aLayerArray431[n];
    }
    
    public int getSize() {
        return this.anInt430;
    }
    
    public void remove(final Layer layer) {
        this.method214(layer);
    }
    
    public void paint(final Graphics graphics, final int n, final int n2) {
        final int clipX = graphics.getClipX();
        final int clipY = graphics.getClipY();
        final int clipWidth = graphics.getClipWidth();
        final int clipHeight = graphics.getClipHeight();
        graphics.translate(n - this.anInt432, n2 - this.anInt433);
        graphics.clipRect(this.anInt432, this.anInt433, this.anInt434, this.anInt435);
        int anInt430 = this.anInt430;
        while (--anInt430 >= 0) {
            final Layer layer;
            if ((layer = this.aLayerArray431[anInt430]).aBoolean599) {
                layer.paint(graphics);
            }
        }
        graphics.translate(-n + this.anInt432, -n2 + this.anInt433);
        graphics.setClip(clipX, clipY, clipWidth, clipHeight);
    }
    
    public void setViewWindow(final int anInt432, final int anInt433, final int anInt434, final int anInt435) {
        if (anInt434 < 0 || anInt435 < 0) {
            throw new IllegalArgumentException();
        }
        this.anInt432 = anInt432;
        this.anInt433 = anInt433;
        this.anInt434 = anInt434;
        this.anInt435 = anInt435;
    }
    
    private void method213(final Layer layer, final int n) {
        if (this.anInt430 == this.aLayerArray431.length) {
            final Layer[] aLayerArray431 = new Layer[this.anInt430 + 4];
            System.arraycopy(this.aLayerArray431, 0, aLayerArray431, 0, this.anInt430);
            System.arraycopy(this.aLayerArray431, n, aLayerArray431, n + 1, this.anInt430 - n);
            this.aLayerArray431 = aLayerArray431;
        }
        else {
            System.arraycopy(this.aLayerArray431, n, this.aLayerArray431, n + 1, this.anInt430 - n);
        }
        this.aLayerArray431[n] = layer;
        ++this.anInt430;
    }
    
    private void method214(final Layer layer) {
        if (layer == null) {
            throw new NullPointerException();
        }
        int anInt430 = this.anInt430;
        while (--anInt430 >= 0) {
            if (this.aLayerArray431[anInt430] == layer) {
                this.method215(anInt430);
            }
        }
    }
    
    private void method215(final int n) {
        System.arraycopy(this.aLayerArray431, n + 1, this.aLayerArray431, n, this.anInt430 - n - 1);
        this.aLayerArray431[--this.anInt430] = null;
    }
}
