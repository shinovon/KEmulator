package emulator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

	public static final Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		for (Method method : clazz.getDeclaredMethods()) {
			Class<?>[] checkParamTypes = method.getParameterTypes();
			if (method.getName().equals(name) && checkParamTypes.length == parameterTypes.length) {
				boolean matches = true;
				for (int i = 0; i < parameterTypes.length; i++) {
					Class<?> paramType = parameterTypes[i];
					Class<?> checkParamType = checkParamTypes[i];

					if (!equals(paramType, checkParamType)) {
						matches = false;
						break;
					}
				}
				if (!matches) {
					continue;
				}
				return method;
			}
		}
		return null;
	}

	public static final boolean equals(Class<?> class1, Class<?> class2) {
		if (class1 == null) {
			return class2 == null;
		}
		if (class2 == null) {
			return false;
		}
		return class1.isAssignableFrom(class2) && class2.isAssignableFrom(class1);
	}

	public static long getHandle(Object c) {
		try {
			Class<?> cl = c.getClass();
			Field f = cl.getField("handle");
			try {
				return f.getLong(c);
			} catch (Exception e) {
				return f.getInt(c);
			}
		} catch (Exception e) {
			throw new Error(e);
		}
	}
}
