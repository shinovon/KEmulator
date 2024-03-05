package emulator.ui.swt;

import java.util.*;

final class SortProperties extends Properties {
    private static final long serialVersionUID = 1L;

    private SortProperties(final Property class38) {
        super();
    }

    public final Enumeration keys() {
        final List list;
        Collections.sort((List<Comparable>) (list = (List) Collections.list((Enumeration) super.keys())));
        return Collections.enumeration((Collection) list);
    }

    SortProperties(final Property class38, final Class117 class39) {
        this(class38);
    }
}
