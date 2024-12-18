package emulator.custom;

import emulator.Emulator;
import emulator.Settings;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.AbstractVisitor;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public final class h {
	public static Hashtable aHashtable1061;

	public h() {
		super();
	}

	public static void method591() {
		try {
			for (int i = 0; i < Emulator.jarClasses.size(); ++i) {
				final InputStream method592 = method592((String) Emulator.jarClasses.get(i));
				final ClassReader classReader = new ClassReader(method592);
				final ClassNode classNode = new ClassNode();
				classReader.accept((ClassVisitor) classNode, Settings.asmSkipDebug ? ClassReader.SKIP_DEBUG : 0);
				method592.close();
				for (Object o : classNode.methods) {
					final MethodInfo methodInfo = new MethodInfo(classNode, (MethodNode) o);
					h.aHashtable1061.put(methodInfo.method704(), methodInfo);
				}
			}
			for (int j = 0; j < Emulator.jarClasses.size(); ++j) {
				final InputStream method593 = method592((String) Emulator.jarClasses.get(j));
				new ClassReader(method593).accept((ClassVisitor) new TraceClassAdapter((ClassVisitor) new ClassWriter(0)), Settings.asmSkipDebug ? ClassReader.SKIP_DEBUG : 0);
				method593.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static InputStream method592(final String s) throws IOException {
		if (Emulator.midletJar == null) {
			final File fileFromClassPath;
			if ((fileFromClassPath = Emulator.getFileFromClassPath(s.replace('.', '/') + ".class")) == null || !fileFromClassPath.exists()) {
				return null;
			}
			return new FileInputStream(fileFromClassPath);
		} else {
			final ZipFile zipFile;
			final ZipEntry entry;
			if ((entry = (zipFile = new ZipFile(Emulator.midletJar)).getEntry(s.replace('.', '/') + ".class")) == null) {
				return null;
			}
			return zipFile.getInputStream(entry);
		}
	}

	static final class TraceClassAdapter extends ClassVisitor implements Opcodes {
		private String aString1366;

		public TraceClassAdapter(final ClassVisitor classVisitor) {
			super(Opcodes.ASM4, classVisitor);
		}

		public final void visit(final int n, final int n2, final String aString1366, final String s, final String s2, final String[] array) {
			super.visit(n, n2, this.aString1366 = aString1366, s, s2, array);
		}

		public final MethodVisitor visitMethod(int acc, final String name, final String desc, final String s3, final String[] array) {
			final MethodVisitor visitMethod;
			if ((visitMethod = super.visitMethod(acc, name, desc, s3, array)) != null) {
				return (MethodVisitor) new TraceMethodAdapter(visitMethod, (MethodInfo) h.aHashtable1061.get(this.aString1366 + '.' + name + desc));
			}
			return null;
		}
	}

	static final class TraceMethodAdapter extends TraceMethodVisitor implements Opcodes {
		private MethodInfo ane1200;

		public TraceMethodAdapter(final MethodVisitor methodVisitor, final MethodInfo ane1200) {
			super(methodVisitor);
			this.ane1200 = ane1200;
		}

		public final void visitMethodInsn(final int n, final String s, final String s2, final String s3) {
			final MethodInfo methodInfo;
			if ((methodInfo = (MethodInfo) h.aHashtable1061.get(s + '.' + s2 + s3)) != null) {
				final MethodInfo methodInfo2 = methodInfo;
				++methodInfo2.refCount;
			}
			super.visitMethodInsn(n, s, s2, s3);
		}

		public final void visitEnd() {
			if (this.ane1200 != null) {
				this.ane1200.aList1171 = ((AbstractVisitor) this).getText();
			}
			super.visitEnd();
		}
	}

	public static final class MethodInfo {
		ClassNode classNode;
		MethodNode methodNode;
		List aList1171;
		public String aString1172;
		public String aString1177;
		public String aString1181;
		public int anInt1173;
		public int refCount;
		public int anInt1182;
		public long aLong1174;
		public long aLong1179;
		public float aFloat1175;
		public float aFloat1180;
		static StringBuffer byteCodeBuf;

		public MethodInfo(final ClassNode aClassNode1169, final MethodNode aMethodNode1170) {
			super();
			this.classNode = aClassNode1169;
			this.methodNode = aMethodNode1170;
			this.aString1181 = method703(this.methodNode);
			this.anInt1173 = this.methodNode.instructions.size();
			this.refCount = 0;
			this.aString1172 = this.classNode.name.replace('/', '.');
			this.aString1177 = this.methodNode.name;
		}

		private static String method703(final MethodNode methodNode) {
			final Method method = new Method(methodNode.name, methodNode.desc);
			StringBuilder s = new StringBuilder(method.getReturnType().getClassName() + " " + methodNode.name + "(");
			final Type[] argumentTypes = method.getArgumentTypes();
			for (int i = 0; i < argumentTypes.length; ++i) {
				s.append(argumentTypes[i].getClassName()).append((i >= argumentTypes.length - 1) ? "" : ", ");
			}
			return s.append(")").toString();
		}

		public final String method704() {
			return this.aString1172 + '.' + this.aString1177 + this.methodNode.desc;
		}

		public final String toString() {
			return this.method704() + " refCount=" + this.refCount;
		}

		public final String method705(final boolean b, final boolean b2) {
			String s = "\nname      : " + this.methodNode.name + "\nsignature : " + this.methodNode.signature + "\naccess    : " + getAccess(this.methodNode.access) + "\ndesc      : " + this.methodNode.desc + "\nmaxStack  : " + this.methodNode.maxStack + "\nmaxLocals : " + this.methodNode.maxLocals + "\n";
			if (this.methodNode.exceptions != null && this.methodNode.exceptions.size() > 0) {
				StringBuilder s2 = new StringBuilder(s + "\nExceptions: " + this.methodNode.exceptions.size());
				final Iterator iterator = this.methodNode.exceptions.iterator();
				while (iterator.hasNext()) {
					s2.append("\n\t").append(iterator.next());
				}
				s = s2 + "\n";
			}
			if (this.aList1171 != null) {
				MethodInfo.byteCodeBuf.setLength(0);
				final Iterator<String> iterator2 = this.aList1171.iterator();
				while (iterator2.hasNext()) {
					final String s3;
					if ((!(s3 = iterator2.next()).startsWith("FRAME ") || b2) && (!s3.startsWith("    LINENUMBER") || b)) {
						MethodInfo.byteCodeBuf.append(s3);
					}
				}
				s = s + "\nByteCode:\n" + MethodInfo.byteCodeBuf.toString() + "\n";
			}
			return s;
		}

		private static String getAccess(final int n) {
			String s = "";
			Label_0073:
			{
				StringBuffer sb;
				String s2;
				if ((n & 0x1) != 0x0) {
					sb = new StringBuffer().append(s);
					s2 = "public ";
				} else if ((n & 0x2) != 0x0) {
					sb = new StringBuffer().append(s);
					s2 = "private ";
				} else {
					if ((n & 0x4) == 0x0) {
						break Label_0073;
					}
					sb = new StringBuffer().append(s);
					s2 = "protected ";
				}
				s = sb.append(s2).toString();
			}
			if ((n & 0x10) != 0x0) {
				s += "final ";
			}
			if ((n & 0x8) != 0x0) {
				s += "static ";
			}
			return s;
		}

		static {
			MethodInfo.byteCodeBuf = new StringBuffer();
		}
	}
}
