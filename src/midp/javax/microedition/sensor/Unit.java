package javax.microedition.sensor;

import java.util.*;

public class Unit
{
    private static Hashtable aHashtable1317;
    private String aString1318;
    
    private Unit(final String aString1318) {
        super();
        this.aString1318 = aString1318;
    }
    
    public static Unit getUnit(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        if (s.length() == 0) {
            throw new IllegalArgumentException();
        }
        Unit unit = null;
        synchronized (Unit.aHashtable1317) {
            Unit unit2;
            if ((unit2 = (Unit) Unit.aHashtable1317.get(s)) == null) {
                unit2 = new Unit(s);
                Unit.aHashtable1317.put(s, unit2);
            }
            unit = unit2;
        }
        return unit;
    }
    
    public String toString() {
        return this.aString1318;
    }
    
    static {
        Unit.aHashtable1317 = new Hashtable();
    }
}
