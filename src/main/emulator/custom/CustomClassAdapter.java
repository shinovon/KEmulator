package emulator.custom;

import emulator.Emulator;
import emulator.Settings;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashSet;
import java.util.Set;

public final class CustomClassAdapter extends ClassVisitor implements Opcodes {
	static boolean hasRenamedMethods;
	static Set<String> renamedClasses = new HashSet<String>();

	private String className;
	private String parentClassName;

	public final MethodVisitor visitMethod(int acc, String name, String desc, final String sign, final String[] array) {
		Label_0037:
		{
			String s4;
			String s5;
			if (desc.contains("java/util/TimerTask")) {
				s4 = "java/util/TimerTask";
				s5 = "emulator/custom/subclass/SubTimerTask";
			} else if (desc.contains("java/util/Timer")) {
				s4 = "java/util/Timer";
				s5 = "emulator/custom/subclass/Timer";
			} else if (desc.contains("com/gcjsp/v10/")) {
				s4 = "com/gcjsp/v10/";
				s5 = "com/vodafone/v10/";
			} else if (desc.contains("tw/com/fareastone/v10/")) {
				s4 = "tw/com/fareastone/v10/";
				s5 = "com/vodafone/v10/";
			} else if (desc.contains("com/bmc/media/")) {
				s4 = "com/bmc/media/";
				s5 = "com/sprintpcs/media/";
			} else {
				break Label_0037;
			}
			desc = desc.replaceAll(s4, s5);
		}
		if (Settings.patchSynchronizedPaint &&
				"paint".equals(name) && "(Ljavax/microedition/lcdui/Graphics;)V".equals(desc)) {
			if ((acc & Opcodes.ACC_SYNCHRONIZED) != 0) {
				acc = acc & (~Opcodes.ACC_SYNCHRONIZED);
				Emulator.getEmulator().getLogStream().println("Patched paint method: " + className);
			}
		} else if (Settings.patchSynchronizedPlayerUpdate &&
				"playerUpdate".equals(name) && "(Ljavax/microedition/media/Player;Ljava/lang/String;Ljava/lang/Object;)V".equals(desc)) {
			if ((acc & Opcodes.ACC_SYNCHRONIZED) != 0) {
				acc = acc & (~Opcodes.ACC_SYNCHRONIZED);
				Emulator.getEmulator().getLogStream().println("Patched playerUpdate method: " + className);
			}
		} else if ("()V".equals(desc) && "java/lang/Thread".equals(parentClassName) &&
				("stop".equals(name) || "resume".equals(name) || "suspend".equals(name))) {
			Emulator.getEmulator().getLogStream().println("Patched illegal Thread method name: " + className);
			hasRenamedMethods = true;
			renamedClasses.add(className.replace('.', '/'));
			name = name + "_";
		} else if (Settings.bypassVserv && "()V".equals(desc) && "startRealApp".equals(name) && acc == Opcodes.ACC_PRIVATE) {
			Emulator.getEmulator().getLogStream().println("Patched ALW1: " + className);
			acc = Opcodes.ACC_PUBLIC;
		}
		final MethodVisitor visitMethod;
		if ((visitMethod = super.visitMethod(acc, name, desc, sign, array)) != null) {
			return new CustomMethodAdapter(visitMethod, this.className, name, desc);
		}
		return null;
	}

	public CustomClassAdapter(final ClassVisitor classVisitor, final String aString1165) {
		super(Opcodes.ASM4, classVisitor);
		this.className = aString1165;
	}

	public final void visit(final int n, final int n2, final String s, final String s2, String s3, final String[] array) {
		parentClassName = s3;
		if (s3.equals("java/util/TimerTask")) {
			super.visit(n, n2, s, s2, "emulator/custom/subclass/SubTimerTask", array);
			return;
		}
		if (s3.equals("java/util/Timer")) {
			super.visit(n, n2, s, s2, "emulator/custom/subclass/Timer", array);
			return;
		}
		if (s3.startsWith("com/gcjsp/v10/")) {
			super.visit(n, n2, s, s2, s3.replace("com/gcjsp/v10/", "com/vodafone/v10/"), array);
			return;
		}
		if (s3.startsWith("tw/com/fareastone/v10/")) {
			super.visit(n, n2, s, s2, s3.replace("tw/com/fareastone/v10/", "com/vodafone/v10/"), array);
			return;
		}
		if (s3.startsWith("com/bmc/media/")) {
			super.visit(n, n2, s, s2, s3.replace("com/bmc/media/", "com/sprintpcs/media/"), array);
			return;
		}
		if (CustomClassLoader.isProtected(s3.replace('/', '.'), false)) {
			s3 = "__".concat(s3);
		}
		super.visit(n, n2, s, s2, s3, array);
	}

	public final FieldVisitor visitField(final int n, final String s, String s2, final String s3, final Object o) {
		if (s2.equals("Ljava/util/TimerTask;")) {
			s2 = "Lemulator/custom/subclass/SubTimerTask;";
		} else if (s2.equals("Ljava/util/Timer;")) {
			s2 = "Lemulator/custom/subclass/Timer;";
		} else if (s2.contains("com/gcjsp/v10/")) {
			s2 = s2.replace("com/gcjsp/v10/", "com/vodafone/v10/");
		} else if (s2.contains("tw/com/fareastone/v10/")) {
			s2 = s2.replace("tw/com/fareastone/v10/", "com/vodafone/v10/");
		} else if (s2.contains("com/bmc/media/")) {
			s2 = s2.replace("com/bmc/media/", "com/sprintpcs/media/");
		}
		return super.visitField(n, s, s2, s3, o);
	}
}
