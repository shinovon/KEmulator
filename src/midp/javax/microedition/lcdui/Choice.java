package javax.microedition.lcdui;

public interface Choice {
    public static final int EXCLUSIVE = 1;
    public static final int MULTIPLE = 2;
    public static final int IMPLICIT = 3;
    public static final int POPUP = 4;
    public static final int TEXT_WRAP_DEFAULT = 0;
    public static final int TEXT_WRAP_ON = 1;
    public static final int TEXT_WRAP_OFF = 2;

    int size();

    String getString(final int p0);

    Image getImage(final int p0);

    int append(final String p0, final Image p1);

    void insert(final int p0, final String p1, final Image p2);

    void delete(final int p0);

    void deleteAll();

    void set(final int p0, final String p1, final Image p2);

    boolean isSelected(final int p0);

    int getSelectedIndex();

    int getSelectedFlags(final boolean[] p0);

    void setSelectedIndex(final int p0, final boolean p1);

    void setSelectedFlags(final boolean[] p0);

    void setFitPolicy(final int p0);

    int getFitPolicy();

    void setFont(final int p0, final Font p1);

    Font getFont(final int p0);
}
