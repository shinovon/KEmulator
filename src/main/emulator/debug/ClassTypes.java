package emulator.debug;

import emulator.Emulator;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public final class ClassTypes {
    static Class strCls;

    public static String method869(Class var0) {
        return !var0.isArray() ? var0.getName() : method870(var0.getName());
    }

    public static String method870(String var0) {
        if (var0 == null) {
            return var0;
        } else {
            int var1;
            if ((var1 = var0.lastIndexOf(91)) == -1) {
                return var0;
            } else {
                label35:
                {
                    String var10000;
                    switch ((var0 = var0.substring(var1 + 1)).charAt(0)) {
                        case 'B':
                            var10000 = "byte";
                            break;
                        case 'C':
                            var10000 = "char";
                            break;
                        case 'D':
                            var10000 = "double";
                            break;
                        case 'E':
                        case 'G':
                        case 'H':
                        case 'K':
                        case 'M':
                        case 'N':
                        case 'O':
                        case 'P':
                        case 'Q':
                        case 'R':
                        case 'T':
                        case 'U':
                        case 'W':
                        case 'X':
                        case 'Y':
                        default:
                            break label35;
                        case 'F':
                            var10000 = "float";
                            break;
                        case 'I':
                            var10000 = "int";
                            break;
                        case 'J':
                            var10000 = "long";
                            break;
                        case 'L':
                            var10000 = var0.substring(1, var0.length() - 1);
                            break;
                        case 'S':
                            var10000 = "short";
                            break;
                        case 'V':
                            var10000 = "void";
                            break;
                        case 'Z':
                            var10000 = "boolean";
                    }

                    var0 = var10000;
                }

                for (int var2 = var1; var2 >= 0; --var2) {
                    var0 = var0 + "[]";
                }

                return var0;
            }
        }
    }

    public static boolean method871(Class var0) {
        return var0 == Integer.TYPE ? false : (var0 == Boolean.TYPE ? false : (var0 == Byte.TYPE ? false : (var0 == Short.TYPE ? false : (var0 == Long.TYPE ? false : (var0 == Float.TYPE ? false : (var0 == Double.TYPE ? false : (var0 == Character.TYPE ? false : (var0 == (strCls != null ? strCls : (strCls = cls("java.lang.String"))) ? false : !var0.isArray()))))))));
    }

    public static String method872(Object var0, int var1, boolean var2) {
        Object var3;
        Class var4;
        return var0 != null && var0.getClass().isArray() ? ((var3 = Array.get(var0, var1)) == null ? "null" : ((var4 = var0.getClass().getComponentType()) == Integer.TYPE ? (var2 ? "0x" + Integer.toHexString(Array.getInt(var0, var1)) : String.valueOf(Array.getInt(var0, var1))) : (var4 == Boolean.TYPE ? String.valueOf(Array.getBoolean(var0, var1)) : (var4 == Byte.TYPE ? (var2 ? "0x" + Integer.toHexString(Array.getByte(var0, var1)) : String.valueOf(Array.getByte(var0, var1))) : (var4 == Short.TYPE ? (var2 ? "0x" + Integer.toHexString(Array.getShort(var0, var1)) : String.valueOf(Array.getShort(var0, var1))) : (var4 == Long.TYPE ? (var2 ? "0x" + Long.toHexString(Array.getLong(var0, var1)) : String.valueOf(Array.getLong(var0, var1))) : (var4 == Float.TYPE ? String.valueOf(Array.getFloat(var0, var1)) : (var4 == Double.TYPE ? String.valueOf(Array.getDouble(var0, var1)) : (var4 == Character.TYPE ? String.valueOf(Array.getChar(var0, var1)) : (var4 == (strCls != null ? strCls : (strCls = cls("java.lang.String"))) ? String.valueOf(var3) : (!var4.isArray() ? var3.toString() : "[" + Array.getLength(var3) + "]"))))))))))) : "null";
    }



    public static String asd(Object var0, int var1, boolean var2) {
        Object var3;
        Class var4;
        return var0 != null && var0.getClass().isArray() ? ((var3 = Array.get(var0, var1)) == null ? "null" : ((var4 = var0.getClass().getComponentType()) == Integer.TYPE ? (var2 ? "0x" + Integer.toHexString(Array.getInt(var0, var1)) : String.valueOf(Array.getInt(var0, var1))) : (var4 == Boolean.TYPE ? String.valueOf(Array.getBoolean(var0, var1)) : (var4 == Byte.TYPE ? (var2 ? "0x" + Integer.toHexString(Array.getByte(var0, var1)) : String.valueOf(Array.getByte(var0, var1))) : (var4 == Short.TYPE ? (var2 ? "0x" + Integer.toHexString(Array.getShort(var0, var1)) : String.valueOf(Array.getShort(var0, var1))) : (var4 == Long.TYPE ? (var2 ? "0x" + Long.toHexString(Array.getLong(var0, var1)) : String.valueOf(Array.getLong(var0, var1))) : (var4 == Float.TYPE ? String.valueOf(Array.getFloat(var0, var1)) : (var4 == Double.TYPE ? String.valueOf(Array.getDouble(var0, var1)) : (var4 == Character.TYPE ? String.valueOf(Array.getChar(var0, var1)) : (var4 == (strCls != null ? strCls : (strCls = cls("java.lang.String"))) ? String.valueOf(var3) :
                (!var4.isArray() ? var3.toString() :
                        ClassTypes.method869(var3.getClass()).replaceFirst("\\[\\]", "[" + Array.getLength(var3) + "]")


                ))))))))))) : "null";
    }

    public static void method873(Object var0, int var1, String var2) {
        if (var0 != null && var0.getClass().isArray()) {
            if (Array.get(var0, var1) != null) {
                int var4;
                if ((var4 = var2.startsWith("0x") ? 16 : 10) == 16) {
                    var2 = var2.substring(2);
                }

                Class var5;
                if ((var5 = var0.getClass().getComponentType()) == Long.TYPE) {
                    Array.setLong(var0, var1, Long.parseLong(var2, var4));
                } else if (var5 == Integer.TYPE) {
                    Array.setInt(var0, var1, Integer.parseInt(var2, var4));
                } else if (var5 == Short.TYPE) {
                    Array.setShort(var0, var1, (short) Integer.parseInt(var2, var4));
                } else if (var5 == Byte.TYPE) {
                    Array.setByte(var0, var1, (byte) Integer.parseInt(var2, var4));
                } else if (var5 == Boolean.TYPE) {
                    Array.setBoolean(var0, var1, Boolean.valueOf(var2).booleanValue());
                } else if (var5 == Float.TYPE) {
                    Array.setFloat(var0, var1, Float.parseFloat(var2));
                } else if (var5 == Double.TYPE) {
                    Array.setDouble(var0, var1, Double.parseDouble(var2));
                } else if (var5 == Character.TYPE) {
                    Array.setChar(var0, var1, var2.charAt(0));
                } else {
                    if (var5 == (strCls != null ? strCls : (strCls = cls("java.lang.String")))) {
                        Array.set(var0, var1, var2);
                    }

                }
            }
        }
    }

    public static String method874(Object var0, Field var1, boolean var2) {
        try {
            return var1.get(var0) == null ? "null" : (var1.getType() == Integer.TYPE ? (var2 ? "0x" + Integer.toHexString(var1.getInt(var0)) : String.valueOf(var1.getInt(var0))) : (var1.getType() == Boolean.TYPE ? String.valueOf(var1.getBoolean(var0)) : (var1.getType() == Byte.TYPE ? (var2 ? "0x" + Integer.toHexString(var1.getByte(var0)) : String.valueOf(var1.getByte(var0))) : (var1.getType() == Short.TYPE ? (var2 ? "0x" + Integer.toHexString(var1.getShort(var0)) : String.valueOf(var1.getShort(var0))) : (var1.getType() == Long.TYPE ? (var2 ? "0x" + Long.toHexString(var1.getLong(var0)) : String.valueOf(var1.getLong(var0))) : (var1.getType() == Float.TYPE ? String.valueOf(var1.getFloat(var0)) : (var1.getType() == Double.TYPE ? String.valueOf(var1.getDouble(var0)) : (var1.getType() == Character.TYPE ? String.valueOf(var1.getChar(var0)) : (var1.getType() == (strCls != null ? strCls : (strCls = cls("java.lang.String"))) ? String.valueOf(var1.get(var0)) : (!var1.getType().isArray() ? var1.get(var0).toString() : "[" + Array.getLength(var1.get(var0)) + "]"))))))))));
        } catch (Exception var3) {
            return "!!error!!";
        }
    }

    public static void method875(Object var0, Field var1, String var2) {
        try {
            int var3;
            if ((var3 = var2.startsWith("0x") ? 16 : 10) == 16) {
                var2 = var2.substring(2);
            }

            if (var1.getType() == Long.TYPE) {
                var1.setLong(var0, Long.parseLong(var2, var3));
            } else if (var1.getType() == Integer.TYPE) {
                var1.setInt(var0, Integer.parseInt(var2, var3));
            } else if (var1.getType() == Short.TYPE) {
                var1.setShort(var0, (short) Integer.parseInt(var2, var3));
            } else if (var1.getType() == Byte.TYPE) {
                var1.setByte(var0, (byte) Integer.parseInt(var2, var3));
            } else if (var1.getType() == Boolean.TYPE) {
                var1.setBoolean(var0, Boolean.valueOf(var2).booleanValue());
            } else if (var1.getType() == Float.TYPE) {
                var1.setFloat(var0, Float.parseFloat(var2));
            } else if (var1.getType() == Double.TYPE) {
                var1.setDouble(var0, Double.parseDouble(var2));
            } else {
                if (var1.getType() != Character.TYPE) {
                    if (var1.getType() == (strCls != null ? strCls : (strCls = cls("java.lang.String")))) {
                        var1.set(var0, var2);
                    }

                    return;
                }

                var1.setChar(var0, var2.charAt(0));
            }
        } catch (Exception var4) {
            Emulator.getEmulator().getLogStream().println(var4.toString());
        }

    }

    public static Object getFieldValue(Object var0, Field var1) {
        try {
            return var1.get(var0);
        } catch (Exception var2) {
            return null;
        }
    }

    private static Class cls(String var0) {
        try {
            Class var1 = Class.forName(var0);
            return var1;
        } catch (ClassNotFoundException var3) {
            Emulator.AntiCrack(var3);
            throw new NoClassDefFoundError(var3.getMessage());
        }
    }
}


/*
import java.lang.reflect.*;
import emulator.*;

public final class d
{
    static Class aClass1482;
    
    public d() {
        super();
    }
    
    public static String method869(final Class clazz) {
        if (!clazz.isArray()) {
            return clazz.getName();
        }
        return method870(clazz.getName());
    }
    
    public static String method870(String s) {
        if (s == null) {
            return s;
        }
        String s2 = s;
        final int lastIndex;
        //[
        if ((lastIndex = s.lastIndexOf(91)) == -1) {
            return s;
        }
        while (true) {
            String substring = null;
            switch ((substring = s.substring(lastIndex+1)).charAt(0)) {
                case 'L': {
                    substring = substring.substring(1, substring.length() - 1);
                    break;
                }
                case 'V': {
                    substring = "void";
                    break;
                }
                case 'S': {
                    substring = "short";
                    break;
                }
                case 'D': {
                    substring = "double";
                    break;
                }
                case 'Z': {
                    substring = "boolean";
                    break;
                }
                case 'I': {
                    substring = "int";
                    break;
                }
                case 'C': {
                    substring = "char";
                    break;
                }
                case 'F': {
                    substring = "float";
                    break;
                }
                case 'B': {
                    substring = "byte";
                    break;
                }
                case 'J': {
                    substring = "long";
                    break;
                }
                default: {
                    for (int i = lastIndex; i >= 0; --i) {
                        s += "[]";
                    }
                    return s;
                }
            }
            s = substring;
            continue;
        }
    }
    
    public static boolean method871(final Class clazz) {
        return clazz != Integer.TYPE && clazz != Boolean.TYPE && clazz != Byte.TYPE && clazz != Short.TYPE && clazz != Long.TYPE && clazz != Float.TYPE && clazz != Double.TYPE && clazz != Character.TYPE && clazz != ((d.aClass1482 != null) ? d.aClass1482 : (d.aClass1482 = method877("java.lang.String"))) && !clazz.isArray();
    }
    
    public static String method872(final Object o, final int n, final boolean b) {
        if (o == null || !o.getClass().isArray()) {
            return "null";
        }
        final Object value;
        if ((value = Array.get(o, n)) == null) {
            return "null";
        }
        final Class<?> componentType;
        if ((componentType = o.getClass().getComponentType()) == Integer.TYPE) {
            if (b) {
                return "0x" + Integer.toHexString(Array.getInt(o, n));
            }
            return String.valueOf(Array.getInt(o, n));
        }
        else {
            if (componentType == Boolean.TYPE) {
                return String.valueOf(Array.getBoolean(o, n));
            }
            if (componentType == Byte.TYPE) {
                if (b) {
                    return "0x" + Integer.toHexString(Array.getByte(o, n));
                }
                return String.valueOf(Array.getByte(o, n));
            }
            else if (componentType == Short.TYPE) {
                if (b) {
                    return "0x" + Integer.toHexString(Array.getShort(o, n));
                }
                return String.valueOf(Array.getShort(o, n));
            }
            else if (componentType == Long.TYPE) {
                if (b) {
                    return "0x" + Long.toHexString(Array.getLong(o, n));
                }
                return String.valueOf(Array.getLong(o, n));
            }
            else {
                if (componentType == Float.TYPE) {
                    return String.valueOf(Array.getFloat(o, n));
                }
                if (componentType == Double.TYPE) {
                    return String.valueOf(Array.getDouble(o, n));
                }
                if (componentType == Character.TYPE) {
                    return String.valueOf(Array.getChar(o, n));
                }
                if (componentType == ((d.aClass1482 != null) ? d.aClass1482 : (d.aClass1482 = method877("java.lang.String")))) {
                    return String.valueOf(value);
                }
                if (!componentType.isArray()) {
                    return value.toString();
                }
                return "[" + Array.getLength(value) + "]";
            }
        }
    }
    
    public static void method873(final Object o, final int n, String substring) {
        if (o == null || !o.getClass().isArray()) {
            return;
        }
        if (Array.get(o, n) == null) {
            return;
        }
        final int n2;
        if ((n2 = (substring.startsWith("0x") ? 16 : 10)) == 16) {
            substring = substring.substring(2);
        }
        final Class<?> componentType;
        if ((componentType = o.getClass().getComponentType()) == Long.TYPE) {
            Array.setLong(o, n, Long.parseLong(substring, n2));
            return;
        }
        if (componentType == Integer.TYPE) {
            Array.setInt(o, n, Integer.parseInt(substring, n2));
            return;
        }
        if (componentType == Short.TYPE) {
            Array.setShort(o, n, (short)Integer.parseInt(substring, n2));
            return;
        }
        if (componentType == Byte.TYPE) {
            Array.setByte(o, n, (byte)Integer.parseInt(substring, n2));
            return;
        }
        if (componentType == Boolean.TYPE) {
            Array.setBoolean(o, n, Boolean.valueOf(substring));
            return;
        }
        if (componentType == Float.TYPE) {
            Array.setFloat(o, n, Float.parseFloat(substring));
            return;
        }
        if (componentType == Double.TYPE) {
            Array.setDouble(o, n, Double.parseDouble(substring));
            return;
        }
        if (componentType == Character.TYPE) {
            Array.setChar(o, n, substring.charAt(0));
            return;
        }
        if (componentType == ((d.aClass1482 != null) ? d.aClass1482 : (d.aClass1482 = method877("java.lang.String")))) {
            Array.set(o, n, substring);
        }
    }
    
    public static String method874(final Object o, final Field field, final boolean b) {
        try {
            if (field.get(o) == null) {
                return "null";
            }
            if (field.getType() == Integer.TYPE) {
                if (b) {
                    return "0x" + Integer.toHexString(field.getInt(o));
                }
                return String.valueOf(field.getInt(o));
            }
            else {
                if (field.getType() == Boolean.TYPE) {
                    return String.valueOf(field.getBoolean(o));
                }
                if (field.getType() == Byte.TYPE) {
                    if (b) {
                        return "0x" + Integer.toHexString(field.getByte(o));
                    }
                    return String.valueOf(field.getByte(o));
                }
                else if (field.getType() == Short.TYPE) {
                    if (b) {
                        return "0x" + Integer.toHexString(field.getShort(o));
                    }
                    return String.valueOf(field.getShort(o));
                }
                else if (field.getType() == Long.TYPE) {
                    if (b) {
                        return "0x" + Long.toHexString(field.getLong(o));
                    }
                    return String.valueOf(field.getLong(o));
                }
                else {
                    if (field.getType() == Float.TYPE) {
                        return String.valueOf(field.getFloat(o));
                    }
                    if (field.getType() == Double.TYPE) {
                        return String.valueOf(field.getDouble(o));
                    }
                    if (field.getType() == Character.TYPE) {
                        return String.valueOf(field.getChar(o));
                    }
                    if (field.getType() == ((d.aClass1482 != null) ? d.aClass1482 : (d.aClass1482 = method877("java.lang.String")))) {
                        return String.valueOf(field.get(o));
                    }
                    if (!field.getType().isArray()) {
                        return field.get(o).toString();
                    }
                    return "[" + Array.getLength(field.get(o)) + "]";
                }
            }
        }
        catch (Exception ex) {
            return "!!error!!";
        }
    }
    
    public static void method875(final Object o, final Field field, String substring) {
        try {
            final int n;
            if ((n = (substring.startsWith("0x") ? 16 : 10)) == 16) {
                substring = substring.substring(2);
            }
            if (field.getType() == Long.TYPE) {
                field.setLong(o, Long.parseLong(substring, n));
            }
            else if (field.getType() == Integer.TYPE) {
                field.setInt(o, Integer.parseInt(substring, n));
            }
            else if (field.getType() == Short.TYPE) {
                field.setShort(o, (short)Integer.parseInt(substring, n));
            }
            else if (field.getType() == Byte.TYPE) {
                field.setByte(o, (byte)Integer.parseInt(substring, n));
            }
            else if (field.getType() == Boolean.TYPE) {
                field.setBoolean(o, Boolean.valueOf(substring));
            }
            else if (field.getType() == Float.TYPE) {
                field.setFloat(o, Float.parseFloat(substring));
            }
            else if (field.getType() == Double.TYPE) {
                field.setDouble(o, Double.parseDouble(substring));
            }
            else {
                if (field.getType() != Character.TYPE) {
                    if (field.getType() == ((d.aClass1482 != null) ? d.aClass1482 : (d.aClass1482 = method877("java.lang.String")))) {
                        field.set(o, substring);
                    }
                    return;
                }
                field.setChar(o, substring.charAt(0));
            }
        }
        catch (Exception ex) {
            Emulator.getEmulator().getLogStream().println("2 "+ ex.toString());
        }
    }
    
    public static Object method876(final Object o, final Field field) {
        try {
            return field.get(o);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    private static Class method877(final String s) {
        Class<?> forName;
        try {
            forName = Class.forName(s);
        }
        catch (ClassNotFoundException ex2) {
            final ClassNotFoundException ex = ex2;
            Emulator.AntiCrack(ex2);
            throw new NoClassDefFoundError(ex.getMessage());
        }
        return forName;
    }
}*/
