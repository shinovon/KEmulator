package org.eclipse.swt.widgets;

public class TableExtension extends Table {
    public TableExtension(Composite composite, int i) {
        super(composite, i);
    }

    public void setFocusIndex(int i) {
        super.setFocusIndex(i);
    }

    public int getFocusIndex() {
        return super.getFocusIndex();
    }
}
