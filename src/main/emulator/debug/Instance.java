package emulator.debug;

import emulator.Emulator;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Vector;

public final class Instance {
	Vector fields;
	String className;
	Class aClass;
	Object instance;

	public Instance(final String aString1494, final Object anObject1496) {
		super();
		this.fields = new Vector();
		this.className = aString1494;
		this.aClass = getCls(aString1494);
		this.instance = anObject1496;
	}

	public final boolean updateFields(final String s) {
		this.fields.removeAllElements();
		if (this.aClass.isArray()) {
			if (this.instance != null) {
				for (int i = 0; i < Array.getLength(this.instance); ++i) {
					this.fields.add(Integer.toString(i));
				}
			}
		} else {
			this.method880(this.aClass, s);
		}
		return this.fields.size() > 0;
	}

	private void method880(final Class clazz, final String s) {
		try {
			final Field[] declaredFields = clazz.getDeclaredFields();
			for (int i = 0; i < declaredFields.length; ++i) {
				if (declaredFields[i] != null) {
					if (s == null || method884(declaredFields[i].getName(), s)) {
						if (!Modifier.isFinal(declaredFields[i].getModifiers())
								|| !declaredFields[i].getType().isPrimitive()
								|| !Modifier.isStatic(declaredFields[i].getModifiers())) {
							this.fields.add(declaredFields[i]);
							declaredFields[i].setAccessible(true);
						}
					}
				}
			}
			if (clazz.getSuperclass() != null) {
				this.method880(clazz.getSuperclass(), s);
			}
		} catch (Exception ignored) {

		} catch (Error ignored) {
		}
	}

	public final Vector getFields() {
		return this.fields;
	}

	public final Object getInstance() {
		return this.instance;
	}

	public final Class getCls() {
		return this.aClass;
	}

	public final String toString() {
		return this.className;
	}

	private static boolean method884(String lowerCase, final String s) {
		lowerCase = lowerCase.toLowerCase();
		final String lowerCase2;
		return (lowerCase2 = s.toLowerCase()).length() > 0 && lowerCase.contains(lowerCase2);
	}

	private static Class getCls(final String s) {
		Class<?> forName;
		try {
			forName = Class.forName(s, false, Emulator.getCustomClassLoader());
		} catch (ClassNotFoundException e) {
			throw new NoClassDefFoundError(e.getMessage());
		}
		return forName;
	}
}
