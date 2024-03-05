package javax.microedition.lcdui.game;

import javax.microedition.lcdui.*;

public class TiledLayer extends Layer {
    private int cellHeight;
    private int cellWidth;
    private int rows;
    private int columns;
    private int[][] cells;
    Image anImage265;
    private int anInt273;
    int[] anIntArray266;
    int[] anIntArray267;
    private int[] animatedTiles;
    private int anInt274;

    public TiledLayer(final int anInt272, final int anInt273, final Image image, final int n, final int n2) {
        super((anInt272 < 1 || n < 1) ? -1 : (anInt272 * n), (anInt273 < 1 || n2 < 1) ? -1 : (anInt273 * n2));
        if (image.getWidth() % n != 0 || image.getHeight() % n2 != 0) {
            throw new IllegalArgumentException();
        }
        this.columns = anInt272;
        this.rows = anInt273;
        this.cells = new int[anInt273][anInt272];
        this.method111(image, image.getWidth() / n * (image.getHeight() / n2) + 1, n, n2, true);
    }

    public int createAnimatedTile(final int n) {
        if (n < 0 || n >= this.anInt273) {
            throw new IndexOutOfBoundsException();
        }
        if (this.animatedTiles == null) {
            this.animatedTiles = new int[4];
            this.anInt274 = 1;
        } else if (this.anInt274 == this.animatedTiles.length) {
            final int[] anIntArray268 = new int[this.animatedTiles.length * 2];
            System.arraycopy(this.animatedTiles, 0, anIntArray268, 0, this.animatedTiles.length);
            this.animatedTiles = anIntArray268;
        }
        this.animatedTiles[this.anInt274] = n;
        ++this.anInt274;
        return -(this.anInt274 - 1);
    }

    public void setAnimatedTile(int n, final int n2) {
        if (n2 < 0 || n2 >= this.anInt273) {
            throw new IndexOutOfBoundsException();
        }
        n = -n;
        if (this.animatedTiles == null || n <= 0 || n >= this.anInt274) {
            throw new IndexOutOfBoundsException();
        }
        this.animatedTiles[n] = n2;
    }

    public int getAnimatedTile(int n) {
        n = -n;
        if (this.animatedTiles == null || n <= 0 || n >= this.anInt274) {
            throw new IndexOutOfBoundsException();
        }
        return this.animatedTiles[n];
    }

    public void setCell(final int n, final int n2, final int n3) {
        if (n < 0 || n >= this.columns || n2 < 0 || n2 >= this.rows) {
            throw new IndexOutOfBoundsException();
        }
        if (n3 > 0) {
            if (n3 >= this.anInt273) {
                throw new IndexOutOfBoundsException();
            }
        } else if (n3 < 0 && (this.animatedTiles == null || -n3 >= this.anInt274)) {
            throw new IndexOutOfBoundsException();
        }
        this.cells[n2][n] = n3;
    }

    public int getCell(final int n, final int n2) {
        if (n < 0 || n >= this.columns || n2 < 0 || n2 >= this.rows) {
            throw new IndexOutOfBoundsException();
        }
        return this.cells[n2][n];
    }

    public void fillCells(final int n, final int n2, final int n3, final int n4, final int n5) {
        if (n < 0 || n >= this.columns || n2 < 0 || n2 >= this.rows || n3 < 0 || n + n3 > this.columns || n4 < 0 || n2 + n4 > this.rows) {
            throw new IndexOutOfBoundsException();
        }
        if (n5 > 0) {
            if (n5 >= this.anInt273) {
                throw new IndexOutOfBoundsException();
            }
        } else if (n5 < 0 && (this.animatedTiles == null || -n5 >= this.anInt274)) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = n2; i < n2 + n4; ++i) {
            for (int j = n; j < n + n3; ++j) {
                this.cells[i][j] = n5;
            }
        }
    }

    public final int getCellWidth() {
        return this.cellWidth;
    }

    public final int getCellHeight() {
        return this.cellHeight;
    }

    public final int getColumns() {
        return this.columns;
    }

    public final int getRows() {
        return this.rows;
    }

    public void setStaticTileSet(final Image image, final int n, final int n2) {
        if (n < 1 || n2 < 1 || image.getWidth() % n != 0 || image.getHeight() % n2 != 0) {
            throw new IllegalArgumentException();
        }
        this._setWidth(this.columns * n);
        this._setHeight(this.rows * n2);
        final int n3;
        boolean b;
        if ((n3 = image.getWidth() / n * (image.getHeight() / n2)) >= this.anInt273 - 1) {
            b = true;
        } else {
            b = false;
        }
        method111(image, n3 + 1, n, n2, b);
    }

    public final void paint(final Graphics graphics) {
        if (graphics == null) {
            throw new NullPointerException();
        }
        if (super.visible) {
            final int n = graphics.getClipX() - this.cellWidth;
            final int n2 = graphics.getClipY() - this.cellHeight;
            final int n3 = graphics.getClipX() + graphics.getClipWidth() + this.cellWidth;
            final int n4 = graphics.getClipY() + graphics.getClipHeight() + this.cellHeight;
            for (int anInt600 = super.y, i = 0; i < this.cells.length; ++i, anInt600 += this.cellHeight) {
                for (int anInt601 = super.x, length = this.cells[i].length, j = 0; j < length; ++j, anInt601 += this.cellWidth) {
                    int animatedTile;
                    if ((animatedTile = this.cells[i][j]) != 0 && anInt601 >= n && anInt601 <= n3 && anInt600 >= n2) {
                        if (anInt600 <= n4) {
                            if (animatedTile < 0) {
                                animatedTile = this.getAnimatedTile(animatedTile);
                            }
                            graphics.drawRegion(this.anImage265, this.anIntArray266[animatedTile], this.anIntArray267[animatedTile], this.cellWidth, this.cellHeight, 0, anInt601, anInt600, 20);
                        }
                    }
                }
            }
        }
    }

    private void method111(final Image anImage265, final int anInt273, final int anInt274, final int anInt275, final boolean b) {
        this.cellWidth = anInt274;
        this.cellHeight = anInt275;
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
                tiledLayer.rows = anInt276;
                if (this.rows >= this.cells.length) {
                    break;
                }
                final int length = this.cells[this.rows].length;
                TiledLayer tiledLayer2 = this;
                int anInt277 = 0;
                while (true) {
                    tiledLayer2.columns = anInt277;
                    if (this.columns >= length) {
                        break;
                    }
                    this.cells[this.rows][this.columns] = 0;
                    tiledLayer2 = this;
                    anInt277 = this.columns + 1;
                }
                tiledLayer = this;
                anInt276 = this.rows + 1;
            }
            this.animatedTiles = null;
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
