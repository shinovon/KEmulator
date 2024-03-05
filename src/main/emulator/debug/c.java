package emulator.debug;

import java.util.*;

import emulator.*;

import java.lang.reflect.*;

public final class c {
    Vector aVector1493;
    String aString1494;
    Class aClass1495;
    Object anObject1496;

    public c(final String aString1494, final Object anObject1496) {
        super();
        this.aVector1493 = new Vector();
        this.aString1494 = aString1494;
        this.aClass1495 = method885(aString1494);
        this.anObject1496 = anObject1496;
    }

    public final boolean method879(final String s) {
        this.aVector1493.removeAllElements();
        if (this.aClass1495.isArray()) {
            if (this.anObject1496 != null) {
                for (int i = 0; i < Array.getLength(this.anObject1496); ++i) {
                    this.aVector1493.add(Integer.toString(i));
                }
            }
        } else {
            this.method880(this.aClass1495, s);
        }
        return this.aVector1493.size() > 0;
    }

    private void method880(final Class clazz, final String s) {
        try {
            final Field[] declaredFields = clazz.getDeclaredFields();
            for (int i = 0; i < declaredFields.length; ++i) {
                if (declaredFields[i] != null) {
                    if (s == null || method884(declaredFields[i].getName(), s)) {
                        if (!Modifier.isFinal(declaredFields[i].getModifiers()) || !declaredFields[i].getType().isPrimitive()) {
                            this.aVector1493.add(declaredFields[i]);
                            declaredFields[i].setAccessible(true);
                        }
                    }
                }
            }
            if (clazz.getSuperclass() != null) {
                this.method880(clazz.getSuperclass(), s);
            }
        } catch (Exception e) {

        } catch (Error error) {
        }
    }

    public final Vector method881() {
        return this.aVector1493;
    }

    public final Object method882() {
        return this.anObject1496;
    }

    public final Class method883() {
        return this.aClass1495;
    }

    public final String toString() {
        return this.aString1494;
    }

    private static boolean method884(String lowerCase, final String s) {
        lowerCase = lowerCase.toLowerCase();
        final String lowerCase2;
        return (lowerCase2 = s.toLowerCase()).length() > 0 && lowerCase.indexOf(lowerCase2) >= 0;
    }

    private static Class method885(final String s) {
        Class<?> forName;
        try {
            forName = Class.forName(s, false, Emulator.getCustomClassLoader());
        } catch (ClassNotFoundException ex2) {
            final ClassNotFoundException ex = ex2;
            Emulator.AntiCrack(ex2);
            throw new NoClassDefFoundError(ex.getMessage());
        }
        return forName;
    }
}
