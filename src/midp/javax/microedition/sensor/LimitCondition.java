package javax.microedition.sensor;

import emulator.sensor.*;

public class LimitCondition implements Condition
{
    private final double aDouble378;
    private final String aString379;
    
    public LimitCondition(final double aDouble378, final String aString379) {
        super();
        if (aString379 == null) {
            throw new NullPointerException();
        }
        if (!i.method241(aString379)) {
            throw new IllegalArgumentException();
        }
        this.aDouble378 = aDouble378;
        this.aString379 = aString379;
    }
    
    public final double getLimit() {
        return this.aDouble378;
    }
    
    public final String getOperator() {
        return this.aString379;
    }
    
    public boolean isMet(final double n) {
        return i.method242(this.aString379, this.aDouble378, n);
    }
    
    public boolean isMet(final Object o) {
        return false;
    }
}
