package org.objectweb.asm.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public abstract class AbstractVisitor extends MethodVisitor {
	public static final String[] OPCODES;
	public static final String[] TYPES;
	public final List text = new ArrayList();
	protected final StringBuffer buf = new StringBuffer();

	public AbstractVisitor() {
		super(Opcodes.ASM4);
	}

	public List getText() {
		return this.text;
	}

	public static void appendString(StringBuffer var0, String var1) {
		var0.append("\"");

		for (int var2 = 0; var2 < var1.length(); ++var2) {
			char var3;
			if ((var3 = var1.charAt(var2)) == 10) {
				var0.append("\\n");
			} else if (var3 == 13) {
				var0.append("\\r");
			} else if (var3 == 92) {
				var0.append("\\\\");
			} else if (var3 == 34) {
				var0.append("\\\"");
			} else if (var3 >= 32 && var3 <= 127) {
				var0.append(var3);
			} else {
				var0.append("\\u");
				if (var3 < 16) {
					var0.append("000");
				} else if (var3 < 256) {
					var0.append("00");
				} else if (var3 < 4096) {
					var0.append("0");
				}

				var0.append(Integer.toString(var3, 16));
			}
		}

		var0.append("\"");
	}

	void printList(PrintWriter var1, List var2) {
		for (int var3 = 0; var3 < var2.size(); ++var3) {
			Object var4;
			if ((var4 = var2.get(var3)) instanceof List) {
				this.printList(var1, (List) var4);
			} else {
				var1.print(var4.toString());
			}
		}

	}

	public static Attribute[] getDefaultAttributes() {
		return new Attribute[0];
	}

	static {
		int n;
		int n2;
		String string = "NOP,ACONST_NULL,ICONST_M1,ICONST_0,ICONST_1,ICONST_2,ICONST_3,ICONST_4,ICONST_5,LCONST_0,LCONST_1,FCONST_0,FCONST_1,FCONST_2,DCONST_0,DCONST_1,BIPUSH,SIPUSH,LDC,,,ILOAD,LLOAD,FLOAD,DLOAD,ALOAD,,,,,,,,,,,,,,,,,,,,,IALOAD,LALOAD,FALOAD,DALOAD,AALOAD,BALOAD,CALOAD,SALOAD,ISTORE,LSTORE,FSTORE,DSTORE,ASTORE,,,,,,,,,,,,,,,,,,,,,IASTORE,LASTORE,FASTORE,DASTORE,AASTORE,BASTORE,CASTORE,SASTORE,POP,POP2,DUP,DUP_X1,DUP_X2,DUP2,DUP2_X1,DUP2_X2,SWAP,IADD,LADD,FADD,DADD,ISUB,LSUB,FSUB,DSUB,IMUL,LMUL,FMUL,DMUL,IDIV,LDIV,FDIV,DDIV,IREM,LREM,FREM,DREM,INEG,LNEG,FNEG,DNEG,ISHL,LSHL,ISHR,LSHR,IUSHR,LUSHR,IAND,LAND,IOR,LOR,IXOR,LXOR,IINC,I2L,I2F,I2D,L2I,L2F,L2D,F2I,F2L,F2D,D2I,D2L,D2F,I2B,I2C,I2S,LCMP,FCMPL,FCMPG,DCMPL,DCMPG,IFEQ,IFNE,IFLT,IFGE,IFGT,IFLE,IF_ICMPEQ,IF_ICMPNE,IF_ICMPLT,IF_ICMPGE,IF_ICMPGT,IF_ICMPLE,IF_ACMPEQ,IF_ACMPNE,GOTO,JSR,RET,TABLESWITCH,LOOKUPSWITCH,IRETURN,LRETURN,FRETURN,DRETURN,ARETURN,RETURN,GETSTATIC,PUTSTATIC,GETFIELD,PUTFIELD,INVOKEVIRTUAL,INVOKESPECIAL,INVOKESTATIC,INVOKEINTERFACE,,NEW,NEWARRAY,ANEWARRAY,ARRAYLENGTH,ATHROW,CHECKCAST,INSTANCEOF,MONITORENTER,MONITOREXIT,,MULTIANEWARRAY,IFNULL,IFNONNULL,";
		OPCODES = new String[200];
		int n3 = 0;
		int n4 = 0;
		while ((n = string.indexOf(44, n2 = n4)) > 0) {
			AbstractVisitor.OPCODES[n3++] = n2 + 1 == n ? null : string.substring(n2, n);
			n4 = n + 1;
		}
		string = "T_BOOLEAN,T_CHAR,T_FLOAT,T_DOUBLE,T_BYTE,T_SHORT,T_INT,T_LONG,";
		TYPES = new String[12];
		n2 = 0;
		n3 = 4;
		while ((n = string.indexOf(44, n2)) > 0) {
			AbstractVisitor.TYPES[n3++] = string.substring(n2, n);
			n2 = n + 1;
		}
	}
}
