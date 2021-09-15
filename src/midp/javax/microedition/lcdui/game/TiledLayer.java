package javax.microedition.lcdui.game;

import javax.microedition.lcdui.*;

public class TiledLayer extends Layer
{
    private int anInt269;
    private int anInt270;
    private int anInt271;
    private int anInt272;
    private int[][] anIntArrayArray264;
    Image anImage265;
    private int anInt273;
    int[] anIntArray266;
    int[] anIntArray267;
    private int[] anIntArray268;
    private int anInt274;
    
    public TiledLayer(final int anInt272, final int anInt273, final Image image, final int n, final int n2) {
        super((anInt272 < 1 || n < 1) ? -1 : (anInt272 * n), (anInt273 < 1 || n2 < 1) ? -1 : (anInt273 * n2));
        if (image.getWidth() % n != 0 || image.getHeight() % n2 != 0) {
            throw new IllegalArgumentException();
        }
        this.anInt272 = anInt272;
        this.anInt271 = anInt273;
        this.anIntArrayArray264 = new int[anInt273][anInt272];
        this.method111(image, image.getWidth() / n * (image.getHeight() / n2) + 1, n, n2, true);
    }
    
    public int createAnimatedTile(final int n) {
        if (n < 0 || n >= this.anInt273) {
            throw new IndexOutOfBoundsException();
        }
        if (this.anIntArray268 == null) {
            this.anIntArray268 = new int[4];
            this.anInt274 = 1;
        }
        else if (this.anInt274 == this.anIntArray268.length) {
            final int[] anIntArray268 = new int[this.anIntArray268.length * 2];
            System.arraycopy(this.anIntArray268, 0, anIntArray268, 0, this.anIntArray268.length);
            this.anIntArray268 = anIntArray268;
        }
        this.anIntArray268[this.anInt274] = n;
        ++this.anInt274;
        return -(this.anInt274 - 1);
    }
    
    public void setAnimatedTile(int n, final int n2) {
        if (n2 < 0 || n2 >= this.anInt273) {
            throw new IndexOutOfBoundsException();
        }
        n = -n;
        if (this.anIntArray268 == null || n <= 0 || n >= this.anInt274) {
            throw new IndexOutOfBoundsException();
        }
        this.anIntArray268[n] = n2;
    }
    
    public int getAnimatedTile(int n) {
        n = -n;
        if (this.anIntArray268 == null || n <= 0 || n >= this.anInt274) {
            throw new IndexOutOfBoundsException();
        }
        return this.anIntArray268[n];
    }
    
    public void setCell(final int n, final int n2, final int n3) {
        if (n < 0 || n >= this.anInt272 || n2 < 0 || n2 >= this.anInt271) {
            throw new IndexOutOfBoundsException();
        }
        if (n3 > 0) {
            if (n3 >= this.anInt273) {
                throw new IndexOutOfBoundsException();
            }
        }
        else if (n3 < 0 && (this.anIntArray268 == null || -n3 >= this.anInt274)) {
            throw new IndexOutOfBoundsException();
        }
        this.anIntArrayArray264[n2][n] = n3;
    }
    
    public int getCell(final int n, final int n2) {
        if (n < 0 || n >= this.anInt272 || n2 < 0 || n2 >= this.anInt271) {
            throw new IndexOutOfBoundsException();
        }
        return this.anIntArrayArray264[n2][n];
    }
    
    public void fillCells(final int n, final int n2, final int n3, final int n4, final int n5) {
        if (n < 0 || n >= this.anInt272 || n2 < 0 || n2 >= this.anInt271 || n3 < 0 || n + n3 > this.anInt272 || n4 < 0 || n2 + n4 > this.anInt271) {
            throw new IndexOutOfBoundsException();
        }
        if (n5 > 0) {
            if (n5 >= this.anInt273) {
                throw new IndexOutOfBoundsException();
            }
        }
        else if (n5 < 0 && (this.anIntArray268 == null || -n5 >= this.anInt274)) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = n2; i < n2 + n4; ++i) {
            for (int j = n; j < n + n3; ++j) {
                this.anIntArrayArray264[i][j] = n5;
            }
        }
    }
    
    public final int getCellWidth() {
        return this.anInt270;
    }
    
    public final int getCellHeight() {
        return this.anInt269;
    }
    
    public final int getColumns() {
        return this.anInt272;
    }
    
    public final int getRows() {
        return this.anInt271;
    }
    
    public void setStaticTileSet(final Image image, final int n, final int n2) {
        if (n < 1 || n2 < 1 || image.getWidth() % n != 0 || image.getHeight() % n2 != 0) {
            throw new IllegalArgumentException();
        }
        this.method335(this.anInt272 * n);
        this.method336(this.anInt271 * n2);
        final int n3;
        TiledLayer tiledLayer;
        Image image2;
        int n4;
        int n5;
        int n6;
        boolean b;
        if ((n3 = image.getWidth() / n * (image.getHeight() / n2)) >= this.anInt273 - 1) {
            tiledLayer = this;
            image2 = image;
            n4 = n3 + 1;
            n5 = n;
            n6 = n2;
            b = true;
        }
        else {
            tiledLayer = this;
            image2 = image;
            n4 = n3 + 1;
            n5 = n;
            n6 = n2;
            b = false;
        }
        tiledLayer.method111(image2, n4, n5, n6, b);
    }
    
    public final void paint(final Graphics graphics) {
        if (graphics == null) {
            throw new NullPointerException();
        }
        if (super.aBoolean599) {
            final int n = graphics.getClipX() - this.anInt270;
            final int n2 = graphics.getClipY() - this.anInt269;
            final int n3 = graphics.getClipX() + graphics.getClipWidth() + this.anInt270;
            final int n4 = graphics.getClipY() + graphics.getClipHeight() + this.anInt269;
            for (int anInt600 = super.anInt600, i = 0; i < this.anIntArrayArray264.length; ++i, anInt600 += this.anInt269) {
                for (int anInt601 = super.anInt598, length = this.anIntArrayArray264[i].length, j = 0; j < length; ++j, anInt601 += this.anInt270) {
                    int animatedTile;
                    if ((animatedTile = this.anIntArrayArray264[i][j]) != 0 && anInt601 >= n && anInt601 <= n3 && anInt600 >= n2) {
                        if (anInt600 <= n4) {
                            if (animatedTile < 0) {
                                animatedTile = this.getAnimatedTile(animatedTile);
                            }
                            graphics.drawRegion(this.anImage265, this.anIntArray266[animatedTile], this.anIntArray267[animatedTile], this.anInt270, this.anInt269, 0, anInt601, anInt600, 20);
                        }
                    }
                }
            }
        }
    }
    
    private void method111(final Image anImage265, final int anInt273, final int anInt274, final int anInt275, final boolean b) {
        this.anInt270 = anInt274;
        this.anInt269 = anInt275;
        final int width = anImage265.getWidth();
        final int height = anImage265.getHeight();
        this.anImage265 = anImage265;
        this.anInt273 = anInt273;
        this.anIntArray266 = new int[this.anInt273];
        this.anIntArray267 = new int[this.anInt273];
        if (!b) {
            TiledLayer tiledLayer = this;
            int anInt276 = 0;
            while (true) {
                tiledLayer.anInt271 = anInt276;
                if (this.anInt271 >= this.anIntArrayArray264.length) {
                    break;
                }
                final int length = this.anIntArrayArray264[this.anInt271].length;
                TiledLayer tiledLayer2 = this;
                int anInt277 = 0;
                while (true) {
                    tiledLayer2.anInt272 = anInt277;
                    if (this.anInt272 >= length) {
                        break;
                    }
                    this.anIntArrayArray264[this.anInt271][this.anInt272] = 0;
                    tiledLayer2 = this;
                    anInt277 = this.anInt272 + 1;
                }
                tiledLayer = this;
                anInt276 = this.anInt271 + 1;
            }
            this.anIntArray268 = null;
        }
        int n = 1;
        int n3;
        int n2 = n3 = 0;
        while (true) {
            final int n4 = n3;
            if (n2 >= height) {
                break;
            }
            int n6;
            int n5 = n6 = 0;
            while (true) {
                final int n7 = n6;
                if (n5 >= width) {
                    break;
                }
                this.anIntArray266[n] = n7;
                this.anIntArray267[n] = n4;
                ++n;
                n5 = (n6 = n7 + anInt274);
            }
            n2 = (n3 = n4 + anInt275);
        }
    }
}
