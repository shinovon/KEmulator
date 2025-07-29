package emulator.debug;

import emulator.Emulator;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public final class ClassTypes {

	public static String getReadableClassName(Class c) {
		return !c.isArray() ? c.getName() : method870(c.getName());
	}

	public static String method870(String s) {
		if (s == null) {
			return null;
		}
		int var1;
		if ((var1 = s.lastIndexOf('[')) != -1) {
			switch ((s = s.substring(var1 + 1)).charAt(0)) {
				case 'B':
					s = "byte";
					break;
				case 'C':
					s = "char";
					break;
				case 'D':
					s = "double";
					break;
				case 'F':
					s = "float";
					break;
				case 'I':
					s = "int";
					break;
				case 'J':
					s = "long";
					break;
				case 'L':
					s = s.substring(1, s.length() - 1);
					break;
				case 'S':
					s = "short";
					break;
				case 'V':
					s = "void";
					break;
				case 'Z':
					s = "boolean";
				default:
					break;
			}

			StringBuilder sBuilder = new StringBuilder(s);
			for (int var2 = var1; var2 >= 0; --var2) {
				sBuilder.append("[]");
			}
			s = sBuilder.toString();

		}
		return s;
	}

	public static boolean isObject(final Class clazz) {
		return clazz != Integer.TYPE &&
				clazz != Boolean.TYPE &&
				clazz != Byte.TYPE &&
				clazz != Short.TYPE &&
				clazz != Long.TYPE &&
				clazz != Float.TYPE &&
				clazz != Double.TYPE &&
				clazz != Character.TYPE &&
				clazz != String.class
				&& !clazz.isArray();
	}

	public static String getArrayValue(Object obj, int index, boolean hex) {
		if (obj == null || !obj.getClass().isArray())
			return "null";
		Class cls = obj.getClass().getComponentType();
		Object e = Array.get(obj, index);
		return (e == null ? "null" : (cls == Integer.TYPE ? (hex ? "0x" + Integer.toHexString(Array.getInt(obj, index)) : String.valueOf(Array.getInt(obj, index))) : (cls == Boolean.TYPE ? String.valueOf(Array.getBoolean(obj, index)) : (cls == Byte.TYPE ? (hex ? "0x" + Integer.toHexString(Array.getByte(obj, index)) : String.valueOf(Array.getByte(obj, index))) : (cls == Short.TYPE ? (hex ? "0x" + Integer.toHexString(Array.getShort(obj, index)) : String.valueOf(Array.getShort(obj, index))) : (cls == Long.TYPE ? (hex ? "0x" + Long.toHexString(Array.getLong(obj, index)) : String.valueOf(Array.getLong(obj, index))) : (cls == Float.TYPE ? String.valueOf(Array.getFloat(obj, index)) : (cls == Double.TYPE ? String.valueOf(Array.getDouble(obj, index)) : (cls == Character.TYPE ? String.valueOf(Array.getChar(obj, index)) : (cls == String.class ? String.valueOf(e) : (!cls.isArray() ? e.toString() : "[" + Array.getLength(e) + "]")))))))))));
	}

	public static void setArrayValue(Object obj, int index, String value) {
		if (obj != null && obj.getClass().isArray()) {
			if (Array.get(obj, index) != null) {
				int var4;
				if ((var4 = value.startsWith("0x") ? 16 : 10) == 16) {
					value = value.substring(2);
				}

				Class cls;
				try {
					if ((cls = obj.getClass().getComponentType()) == Long.TYPE) {
						Array.setLong(obj, index, Long.parseLong(value, var4));
					} else if (cls == Integer.TYPE) {
						if (var4 == 16)
							Array.setInt(obj, index, Integer.parseUnsignedInt(value, var4));
						else if (var4 == 10)
							Array.setInt(obj, index, Integer.parseInt(value, var4));
					} else if (cls == Short.TYPE) {
						Array.setShort(obj, index, (short) Integer.parseInt(value, var4));
					} else if (cls == Byte.TYPE) {
						Array.setByte(obj, index, (byte) Integer.parseInt(value, var4));
					} else if (cls == Boolean.TYPE) {
						String lowerCase = value.toLowerCase();
						boolean b = "true".indexOf(lowerCase) == 0;
						if (!b) {
							try {
								b = Integer.parseInt(value, var4) != 0;
							} catch (java.lang.NumberFormatException e) {
								b = false;
							}
						}
						Array.setBoolean(obj, index, b);
					} else if (cls == Float.TYPE) {
						Array.setFloat(obj, index, Float.parseFloat(value));
					} else if (cls == Double.TYPE) {
						Array.setDouble(obj, index, Double.parseDouble(value));
					} else if (cls == Character.TYPE) {
						Array.setChar(obj, index, value.charAt(0));
					} else if (cls == String.class) {
						Array.set(obj, index, value);
					}
				} catch (java.lang.NumberFormatException e) {
					Emulator.getEmulator().getLogStream().println(e.toString());
				}
			}
		}
	}

	public static String method874(Object var0, Field var1, boolean var2) {
		try {
			return var1.get(var0) == null ? "null" : (var1.getType() == Integer.TYPE ? (var2 ? "0x" + Integer.toHexString(var1.getInt(var0)) : String.valueOf(var1.getInt(var0))) : (var1.getType() == Boolean.TYPE ? String.valueOf(var1.getBoolean(var0)) : (var1.getType() == Byte.TYPE ? (var2 ? "0x" + Integer.toHexString(var1.getByte(var0)) : String.valueOf(var1.getByte(var0))) : (var1.getType() == Short.TYPE ? (var2 ? "0x" + Integer.toHexString(var1.getShort(var0)) : String.valueOf(var1.getShort(var0))) : (var1.getType() == Long.TYPE ? (var2 ? "0x" + Long.toHexString(var1.getLong(var0)) : String.valueOf(var1.getLong(var0))) : (var1.getType() == Float.TYPE ? String.valueOf(var1.getFloat(var0)) : (var1.getType() == Double.TYPE ? String.valueOf(var1.getDouble(var0)) : (var1.getType() == Character.TYPE ? String.valueOf(var1.getChar(var0)) : (var1.getType() == String.class ? String.valueOf(var1.get(var0)) : (!var1.getType().isArray() ? var1.get(var0).toString() : "[" + Array.getLength(var1.get(var0)) + "]"))))))))));
		} catch (Exception var3) {
			return "!!error!!";
		}
	}

	public static void setFieldValue(Object var0, Field var1, String var2) {
		try {
			int var3;
			if ((var3 = var2.startsWith("0x") ? 16 : var2.startsWith("0b") ? 2 : 10) != 10) {
				var2 = var2.substring(2);
			}

			if (var1.getType() == Long.TYPE) {
				var1.setLong(var0, Long.parseLong(var2, var3));
			} else if (var1.getType() == Integer.TYPE) {
				if (var3 == 16 || var3 == 2)
					var1.setInt(var0, Integer.parseUnsignedInt(var2, var3));
				else if (var3 == 10)
					var1.setInt(var0, Integer.parseInt(var2, var3));
			} else if (var1.getType() == Short.TYPE) {
				var1.setShort(var0, (short) Integer.parseInt(var2, var3));
			} else if (var1.getType() == Byte.TYPE) {
				var1.setByte(var0, (byte) Integer.parseInt(var2, var3));
			} else if (var1.getType() == Boolean.TYPE) {
				String lowerCase = var2.toLowerCase();
				boolean b = "true".indexOf(lowerCase) == 0;
				if (!b) {
					try {
						b = Integer.parseInt(var2, var3) != 0;
					} catch (java.lang.NumberFormatException e) {
						b = false;
					}
				}
				var1.setBoolean(var0, b);
			} else if (var1.getType() == Float.TYPE) {
				var1.setFloat(var0, Float.parseFloat(var2));
			} else if (var1.getType() == Double.TYPE) {
				var1.setDouble(var0, Double.parseDouble(var2));
			} else if (var1.getType() == Character.TYPE) {
				var1.setChar(var0, var2.charAt(0));
			} else if (var1.getType() == String.class) {
				var1.set(var0, var2);
			}
		} catch (Exception var4) {
			Emulator.getEmulator().getLogStream().println(var4.toString());
		}
	}

	// must be synced with method above!
	public static boolean canSetFieldValue(Field var1) {
		if (var1.getType() == Long.TYPE) {
			return true;
		} else if (var1.getType() == Integer.TYPE) {
			return true;
		} else if (var1.getType() == Short.TYPE) {
			return true;
		} else if (var1.getType() == Byte.TYPE) {
			return true;
		} else if (var1.getType() == Boolean.TYPE) {
			return true;
		} else if (var1.getType() == Float.TYPE) {
			return true;
		} else if (var1.getType() == Double.TYPE) {
			return true;
		} else if (var1.getType() == Character.TYPE) {
			return true;
		} else if (var1.getType() == String.class) {
			return true;
		}
		return false;
	}

	public static Object getFieldValue(Object var0, Field var1) {
		try {
			return var1.get(var0);
		} catch (Exception e) {
			return null;
		}
	}

	public static String asd(Object obj, int var1, boolean var2) {
		if (obj == null || !obj.getClass().isArray())
			return "null";
		Class cls = obj.getClass().getComponentType();
		Object e = Array.get(obj, var1);
		return (e == null ? "null" : (cls == Integer.TYPE ? (var2 ? "0x" + Integer.toHexString(Array.getInt(obj, var1)) : String.valueOf(Array.getInt(obj, var1))) : (cls == Boolean.TYPE ? String.valueOf(Array.getBoolean(obj, var1)) : (cls == Byte.TYPE ? (var2 ? "0x" + Integer.toHexString(Array.getByte(obj, var1)) : String.valueOf(Array.getByte(obj, var1))) : (cls == Short.TYPE ? (var2 ? "0x" + Integer.toHexString(Array.getShort(obj, var1)) : String.valueOf(Array.getShort(obj, var1))) : (cls == Long.TYPE ? (var2 ? "0x" + Long.toHexString(Array.getLong(obj, var1)) : String.valueOf(Array.getLong(obj, var1))) : (cls == Float.TYPE ? String.valueOf(Array.getFloat(obj, var1)) : (cls == Double.TYPE ? String.valueOf(Array.getDouble(obj, var1)) : (cls == Character.TYPE ? String.valueOf(Array.getChar(obj, var1)) : (cls == String.class ? String.valueOf(e) :
				(!cls.isArray() ? e.toString() :
						ClassTypes.getReadableClassName(e.getClass()).replaceFirst("\\[\\]", "[" + Array.getLength(e) + "]")
				)))))))))));
	}
}