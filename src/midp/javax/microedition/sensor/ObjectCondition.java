package javax.microedition.sensor;

public class ObjectCondition implements Condition
{
    private final Object anObject3;
    
    public ObjectCondition(final Object anObject3) {
        super();
        if (anObject3 == null) {
            throw new NullPointerException();
        }
        this.anObject3 = anObject3;
    }
    
    public final Object getLimit() {
        return this.anObject3;
    }
    
    public boolean isMet(final double n) {
        return false;
    }
    
    public boolean isMet(final Object o) {
        return this.anObject3.equals(o);
    }
}
