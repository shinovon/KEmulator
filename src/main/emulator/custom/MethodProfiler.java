package emulator.custom;

import emulator.AppSettings;
import emulator.Emulator;
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

public final class MethodProfiler {
	public static Hashtable methodProfiles;

	public MethodProfiler() {
		super();
	}

	public static void loadMethodProfiles() {
		try {
			for (int i = 0; i < Emulator.jarClasses.size(); ++i) {
				final ClassNode classNode = new ClassNode();
                try (InputStream classStream = getClassStream((String) Emulator.jarClasses.get(i))) {
                    final ClassReader classReader = new ClassReader(classStream);
                    classReader.accept((ClassVisitor) classNode, AppSettings.asmSkipDebug ? ClassReader.SKIP_DEBUG : 0);
                }
				for (Object o : classNode.methods) {
					final MethodInfo methodInfo = new MethodInfo(classNode, (MethodNode) o);
					MethodProfiler.methodProfiles.put(methodInfo.getProfileKey(), methodInfo);
				}
			}
			for (int j = 0; j < Emulator.jarClasses.size(); ++j) {
				final InputStream traceStream = getClassStream((String) Emulator.jarClasses.get(j));
				new ClassReader(traceStream).accept((ClassVisitor) new TraceClassAdapter((ClassVisitor) new ClassWriter(0)), AppSettings.asmSkipDebug ? ClassReader.SKIP_DEBUG : 0);
				traceStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static InputStream getClassStream(final String s) throws IOException {
		if (Emulator.midletJarPath == null) {
			final File fileFromClassPath;
			if ((fileFromClassPath = Emulator.getFileFromClassPath(s.replace('.', '/') + ".class")) == null || !fileFromClassPath.exists()) {
				return null;
			}
			return new FileInputStream(fileFromClassPath);
		} else {
            synchronized (Emulator.jarFileLock) {
                final ZipFile zipFile = Emulator.midletJar;
                final ZipEntry entry = zipFile.getEntry(s.replace('.', '/') + ".class");
                if (entry == null) {
                    return null;
                }
                return zipFile.getInputStream(entry);
            }
		}
	}

	static final class TraceClassAdapter extends ClassVisitor implements Opcodes {
		private String internalClassName;

		public TraceClassAdapter(final ClassVisitor classVisitor) {
			super(Opcodes.ASM4, classVisitor);
		}

		public final void visit(final int n, final int n2, final String internalClassName, final String s, final String s2, final String[] array) {
			super.visit(n, n2, this.internalClassName = internalClassName, s, s2, array);
		}

		public final MethodVisitor visitMethod(int acc, final String name, final String desc, final String s3, final String[] array) {
			final MethodVisitor visitMethod;
			if ((visitMethod = super.visitMethod(acc, name, desc, s3, array)) != null) {
				return (MethodVisitor) new TraceMethodAdapter(visitMethod, (MethodInfo) MethodProfiler.methodProfiles.get(this.internalClassName + '.' + name + desc));
			}
			return null;
		}
	}

	static final class TraceMethodAdapter extends TraceMethodVisitor implements Opcodes {
		private MethodInfo methodInfo;

		public TraceMethodAdapter(final MethodVisitor methodVisitor, final MethodInfo methodInfo) {
			super(methodVisitor);
			this.methodInfo = methodInfo;
		}

		public final void visitMethodInsn(final int n, final String s, final String s2, final String s3) {
			final MethodInfo methodInfo;
			if ((methodInfo = (MethodInfo) MethodProfiler.methodProfiles.get(s + '.' + s2 + s3)) != null) {
				final MethodInfo methodInfo2 = methodInfo;
				++methodInfo2.refCount;
			}
			super.visitMethodInsn(n, s, s2, s3);
		}

		public final void visitEnd() {
			if (this.methodInfo != null) {
				this.methodInfo.disassembledCode = ((AbstractVisitor) this).getText();
			}
			super.visitEnd();
		}
	}

	public static final class MethodInfo {
		ClassNode classNode;
		MethodNode methodNode;
		List disassembledCode;
		public String className;
		public String methodName;
		public String methodSignature;
		public int codeSize; //amount of instructions?
		public int refCount;
		public int callCount;
		public long lastEntryTimeMillis;
		public long totalExecutionTime;
		public float averageExecutionTime;
		public float timePercentage;
		static StringBuffer byteCodeBuf;

		public MethodInfo(final ClassNode classNode, final MethodNode methodNode) {
			super();
			this.classNode = classNode;
			this.methodNode = methodNode;
			this.methodSignature = formatSignature(this.methodNode);
			this.codeSize = this.methodNode.instructions.size();
			this.refCount = 0;
			this.className = this.classNode.name.replace('/', '.');
			this.methodName = this.methodNode.name;
		}

		private static String formatSignature(final MethodNode methodNode) {
			final Method method = new Method(methodNode.name, methodNode.desc);
			StringBuilder s = new StringBuilder(method.getReturnType().getClassName() + " " + methodNode.name + "(");
			final Type[] argumentTypes = method.getArgumentTypes();
			for (int i = 0; i < argumentTypes.length; ++i) {
				s.append(argumentTypes[i].getClassName()).append((i >= argumentTypes.length - 1) ? "" : ", ");
			}
			return s.append(")").toString();
		}

		public final String getProfileKey() {
			return this.className + '.' + this.methodName + this.methodNode.desc;
		}

		public final String toString() {
			return this.getProfileKey() + " refCount=" + this.refCount;
		}

		public final String formatDisassembly(final boolean showLineNumbers, final boolean showFrames) {
			String s = "\nname      : " + this.methodNode.name + "\nsignature : " + this.methodNode.signature + "\naccess    : " + getAccess(this.methodNode.access) + "\ndesc      : " + this.methodNode.desc + "\nmaxStack  : " + this.methodNode.maxStack + "\nmaxLocals : " + this.methodNode.maxLocals + "\n";
			if (this.methodNode.exceptions != null && this.methodNode.exceptions.size() > 0) {
				StringBuilder s2 = new StringBuilder(s + "\nExceptions: " + this.methodNode.exceptions.size());
				final Iterator iterator = this.methodNode.exceptions.iterator();
				while (iterator.hasNext()) {
					s2.append("\n\t").append(iterator.next());
				}
				s = s2 + "\n";
			}
			if (this.disassembledCode != null) {
				MethodInfo.byteCodeBuf.setLength(0);
				final Iterator<String> iterator2 = this.disassembledCode.iterator();
				while (iterator2.hasNext()) {
					final String s3;
					if ((!(s3 = iterator2.next()).startsWith("FRAME ") || showFrames) && (!s3.startsWith("    LINENUMBER") || showLineNumbers)) {
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
