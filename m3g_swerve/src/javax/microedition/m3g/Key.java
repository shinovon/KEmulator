package javax.microedition.m3g;

class Key {
    private int key;

    public Key(final int key) {
        super();
        this.key = key;
    }

    public void setKey(final int key) {
        this.key = key;
    }

    public int hashCode() {
        return this.key;
    }

    public boolean equals(final Object o) {
        return o instanceof Key && this.key == ((Key) o).key;
    }

    public String toString() {
        return Integer.toString(this.key);
    }
}
