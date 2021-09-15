package emulator.debug;

import java.util.*;

public final class ClassInfo implements Comparable
{
    String aString1484;
    int anInt1485;
    int anInt1487;
    Vector aVector1486;
    
    public final int method878() {
        int anInt1487 = this.anInt1487;
        for (int i = this.aVector1486.size() - 1; i >= 0; --i) {
            anInt1487 += ((ObjInstance)this.aVector1486.get(i)).anInt1491;
        }
        return anInt1487;
    }
    
    public final int compareTo(final Object o) {
        return this.aString1484.compareTo(((ClassInfo)o).aString1484);
    }
    
    ClassInfo(final a a, final String aString1484) {
        super();
        this.aVector1486 = new Vector();
        this.anInt1485 = 0;
        this.anInt1487 = a.method856(a.method848(aString1484), null);
        this.aString1484 = aString1484;
    }
}
