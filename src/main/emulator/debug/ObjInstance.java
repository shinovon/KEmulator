package emulator.debug;

public final class ObjInstance
{
    String aString1489;
    Object anObject1490;
    int anInt1491;
    
    ObjInstance(final a a, final String aString1489, final Object anObject1490) {
        super();
        this.aString1489 = aString1489;
        this.anObject1490 = anObject1490;
        this.anInt1491 = a.method856(anObject1490.getClass(), anObject1490);
    }
}
