package emulator.custom;

import emulator.Emulator;
import emulator.Settings;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class CustomMethodAdapter extends MethodVisitor implements Opcodes {
	private int anInt1185;
	private String className;
	private String methodName;
	private String methodDesc;
	private String aString1190;
	private int sourceLine;

	private void method707(final int n) {
		this.anInt1185 = Math.max(this.anInt1185, n);
	}


	public final void visitMethodInsn(final int acc, final String cls, String name, String sign) {
//		System.out.println("visitMethod " + cls + " " + name + " " + sign);
		Label_0576:
		{
			if (cls.equals("java/lang/System")) {
				if (name.equals("gc")) {
					super.visitMethodInsn(184, "emulator/custom/CustomMethod", "gc", sign);
					return;
				}
				if (name.equals("currentTimeMillis")) {
					super.visitMethodInsn(184, "emulator/custom/CustomMethod", "currentTimeMillis", sign);
					return;
				}
				if (name.equals("getProperty")) {
					super.visitMethodInsn(184, "emulator/custom/CustomMethod", "getProperty", sign);
					return;
				}
				if (name.equals("exit")) {
					if (sign.equals("(I)V")) {
						try {
							// bypass nokia platform check
							if (methodName.equals("<clinit>") && Emulator.getEmulator().getAppProperty("Nokia-Platform") != null) {
								super.visitInsn(Opcodes.POP);
								return;
							}
						} catch (Throwable ignored) {}

						super.visitMethodInsn(184, "emulator/custom/CustomMethod", "exit", sign);
						return;
					}
				}
			} else if (cls.equals("java/util/Random") && (Settings.recordKeys || Settings.playingRecordedKeys)) {
				if (name.equals("<init>")) {
					if (sign.equals("()V")) {
						this.method707(2);
						super.visitLdcInsn(new Long(Settings.recordedRandomSeed));
						super.visitMethodInsn(acc, cls, name, "(J)V");
						return;
					}
					super.visitInsn(88);
					super.visitLdcInsn(new Long(Settings.recordedRandomSeed));
					super.visitMethodInsn(acc, cls, name, sign);
					return;
				}
			} else if (cls.equals("java/lang/Thread")) {
				if (name.equals("yield")) {
					super.visitMethodInsn(184, "emulator/custom/CustomMethod", "yield", sign);
					return;
				}
				if ((Settings.patchSleep || Settings.ignoreSleep) && name.equals("sleep")) {
					super.visitMethodInsn(184, "emulator/custom/CustomMethod", "sleep", sign);
					return;
				}
			} else if (cls.equals("java/lang/Class")) {
				if (name.equals("getResourceAsStream")) {
					super.visitMethodInsn(184, "emulator/custom/CustomMethod", "getResourceAsStream", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/io/InputStream;");
					return;
				}
				if (Settings.hideEmulation && name.equals("forName")) {
					super.visitMethodInsn(184, "emulator/custom/CustomMethod", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");
					return;
				}
			} else if (cls.equals("java/lang/String")) {
				if (name.equals("<init>") && sign.startsWith("([B") && !sign.endsWith("Ljava/lang/String;)V")) {
					this.method707(1);
					super.visitLdcInsn(Settings.fileEncoding);
					super.visitMethodInsn(acc, cls, name, sign.substring(0, sign.length() - 2) + "Ljava/lang/String;)V");
					return;
				}
				if (name.equals("getBytes") && sign.startsWith("()")) {
					this.method707(1);
					super.visitLdcInsn(Settings.fileEncoding);
					super.visitMethodInsn(acc, cls, name, "(Ljava/lang/String;)[B");
					return;
				}
			} else if (cls.equals("java/util/Vector")) {
				if (name.equals("add") || name.equals("get") || name.equals("set") || name.equals("clear") || name.equals("remove")) {
					System.out.println("!!Error in verify methods: \"" + cls + "." + name + sign + "\" not supported!!");
				}
			} else {
				if (cls.equals("java/util/Timer")) {
					if (sign.contains("java/util/TimerTask")) {
						sign = sign.replace("java/util/TimerTask", "emulator/custom/subclass/SubTimerTask");
					}
					super.visitMethodInsn(acc, "emulator/custom/subclass/Timer", name, sign);
					return;
				}
				if (cls.equals("java/util/TimerTask")) {
					super.visitMethodInsn(acc, "emulator/custom/subclass/SubTimerTask", name, sign);
					return;
				}
				if (cls.startsWith("com/gcjsp/v10/") || sign.contains("com/gcjsp/v10/")) {
					super.visitMethodInsn(acc, cls.replace("com/gcjsp/v10/", "com/vodafone/v10/"), name, sign.replace("com/gcjsp/v10/", "com/vodafone/v10/"));
					return;
				}
				if (cls.startsWith("tw/com/fareastone/v10/") || sign.contains("tw/com/fareastone/v10/")) {
					super.visitMethodInsn(acc, cls.replace("tw/com/fareastone/v10/", "com/vodafone/v10/"), name, sign.replace("tw/com/fareastone/v10/", "com/vodafone/v10/"));
					return;
				}
				if (cls.startsWith("com/bmc/media/") || sign.contains("com/bmc/media/")) {
					super.visitMethodInsn(acc, cls.replace("com/bmc/media/", "com/sprintpcs/media/"), name, sign.replace("com/bmc/media/", "com/sprintpcs/media/"));
					return;
				}
				if (cls.startsWith("com/docomostar/") || sign.contains("com/docomostar/")) {
					super.visitMethodInsn(acc, cls.replace("com/docomostar/", "com/nttdocomo/"), name, sign.replace("com/docomostar/", "com/nttdocomo/"));
					return;
				}
				if (cls.equals("com/immersion/VibeTonz") && acc == Opcodes.INVOKESTATIC) {
					super.visitMethodInsn(acc, cls.concat("Static"), name, sign);
					return;
				}
				if (cls.equals("com/siemens/mp/io/Connection") && acc == Opcodes.INVOKESTATIC) {
					super.visitMethodInsn(acc, cls, name.concat("Compat"), sign);
					return;
				}
				if (Settings.g3d == 0) {
					if (cls.equals("javax/microedition/m3g/Transform")) {
						if (!name.equals("finalize") && !name.contains("init>")) {
							Emulator.getEmulator().getLogStream().println("Patched: " + cls + "." + name + sign + " in " + className + "." + methodName + methodDesc);
							sign = "(Ljavax/microedition/m3g/Transform;" + sign.substring(1);
							super.visitMethodInsn(184, "emulator/custom/subclass/CustomTransform", name, sign);
							return;
						}
					}
					if (cls.equals("javax/microedition/m3g/Transformable")) {
						if (!name.equals("finalize") && !name.contains("init>")) {
							Emulator.getEmulator().getLogStream().println("Patched: " + cls + "." + name + sign + " in " + className + "." + methodName + methodDesc);
							sign = "(Ljavax/microedition/m3g/Transformable;" + sign.substring(1);
							super.visitMethodInsn(184, "emulator/custom/subclass/CustomTransformable", name, sign);
							return;
						}
					}
					if (cls.equals("javax/microedition/m3g/Graphics3D")) {
						if (name.endsWith("Light") || name.endsWith("Camera")) {
							Emulator.getEmulator().getLogStream().println("Patched: " + cls + "." + name + sign + " in " + className + "." + methodName + methodDesc);
							sign = "(Ljavax/microedition/m3g/Graphics3D;" + sign.substring(1);
							super.visitMethodInsn(184, "emulator/custom/subclass/CustomGraphics3D", name, sign);
							return;
						}
					}
				}
				if (Settings.hideEmulation) {
					if (cls.equals("java/lang/Runtime")) {
						if (name.equals("totalMemory") || name.equals("freeMemory")) {
							super.visitMethodInsn(184, "emulator/custom/CustomMethod", name, "(Ljava/lang/Object;)J");
							return;
						}
					}
				}
				String s5;
				String s6;
				if (sign.contains("java/util/TimerTask")) {
					s5 = "java/util/TimerTask";
					s6 = "emulator/custom/subclass/SubTimerTask";
				} else if (sign.contains("java/util/Timer")) {
					s5 = "java/util/Timer";
					s6 = "emulator/custom/subclass/Timer";
				} else if (sign.contains("com/gcjsp/v10/")) {
					s5 = "com/gcjsp/v10/";
					s6 = "com/vodafone/v10/";
				} else if (sign.contains("tw/com/fareastone/v10/")) {
					s5 = "tw/com/fareastone/v10/";
					s6 = "com/vodafone/v10/";
				} else if (sign.contains("com/bmc/media/")) {
					s5 = "com/bmc/media/";
					s6 = "com/sprintpcs/media/";
				} else if (sign.contains("com/docomostar/")) {
					s5 = "com/docomostar/";
					s6 = "com/nttdocomo/";
				} else {
					break Label_0576;
				}
				sign = sign.replace(s5, s6);
			}
		}
		if (CustomClassAdapter.hasRenamedMethods && CustomClassAdapter.renamedClasses.contains(cls) && "()V".equals(sign) &&
				(name.equals("stop") || name.equals("resume") || name.equals("suspend") || name.equals("finalize"))) {
			name = name + "_";
		}
		super.visitMethodInsn(acc, cls, name, sign);
	}

	public final void visitFieldInsn(final int n, String s, final String s2, String s3) {
		if (s.contains("com/docomostar/")) {
			s = s.replace("com/docomostar/", "com/nttdocomo/");
		}
		if (s3.contains("java/util/TimerTask")) {
			s3 = s3.replace("java/util/TimerTask", "emulator/custom/subclass/SubTimerTask");
		} else if (s3.contains("java/util/Timer")) {
			s3 = s3.replace("java/util/Timer", "emulator/custom/subclass/Timer");
		} else if (s3.contains("com/gcjsp/v10/")) {
			s3 = s3.replace("com/gcjsp/v10/", "com/vodafone/v10/");
		} else if (s3.contains("tw/com/fareastone/v10/")) {
			s3 = s3.replace("tw/com/fareastone/v10/", "com/vodafone/v10/");
		} else if (s3.contains("com/bmc/media/")) {
			s3 = s3.replace("com/bmc/media/", "com/sprintpcs/media/");
		} else if (s3.contains("com/docomostar/")) {
			s3 = s3.replace("com/docomostar/", "com/nttdocomo/");
		}
		super.visitFieldInsn(n, s, s2, s3);
	}

	public final void visitMaxs(final int n, final int n2) {
		super.visitMaxs(n + this.anInt1185, n2);
	}

	public CustomMethodAdapter(final MethodVisitor methodVisitor, final String aString1186, final String s, final String aString1187) {
		super(Opcodes.ASM4, methodVisitor);
		this.anInt1185 = 0;
		this.className = aString1186;
		this.methodName = s;
		this.methodDesc = aString1187;
		if (Settings.enableNewTrack || Settings.enableMethodTrack) {
			this.methodName = aString1186 + "." + s;
			this.aString1190 = "";
			this.sourceLine = 0;
			final int lastIndex;
			if ((lastIndex = this.className.lastIndexOf(36)) != -1) {
				this.className = this.className.substring(0, lastIndex);
			}
			final int lastIndex2;
			if ((lastIndex2 = this.className.lastIndexOf(46)) != -1) {
				this.className = this.className.substring(lastIndex2 + 1);
			}
		}
	}

	public final void visitLineNumber(final int anInt1188, final Label label) {
		if (Settings.enableNewTrack) {
			this.sourceLine = anInt1188;
		}
		super.visitLineNumber(anInt1188, label);
	}

	private void method708(final String s) {
		try {
			super.visitLdcInsn((this.aString1190 = s + "\t at " + this.methodName + " (" + this.className + ".java:" + this.sourceLine + ")\n"));
			super.visitMethodInsn(184, "emulator/custom/CustomMethod", "showTrackInfo", "(Ljava/lang/String;)V");
			this.method707(1);
		} catch (Exception ignored) {}
	}

	public final void visitMultiANewArrayInsn(final String s, final int n) {
//		System.out.println("visitMulti " + s);
		if (Settings.enableNewTrack) {
			this.method708("new " + emulator.debug.ClassTypes.method870(s));
		}
		if (s.contains("com/docomostar/")) {
			super.visitMultiANewArrayInsn(s.replace("com/docomostar/", "com/nttdocomo/"), n);
			return;
		}
		super.visitMultiANewArrayInsn(s, n);
	}

	public final void visitTypeInsn(final int n, final String s) {
		Label_0078:
		{
			if (Settings.enableNewTrack) {
				CustomMethodAdapter f = null;
				StringBuffer sb = null;
				String s2 = null;
				switch (n) {
					case 187: {
						f = this;
						sb = new StringBuffer().append("new ");
						s2 = s;
						break;
					}
					case 189: {
						f = this;
						sb = new StringBuffer().append("new ").append(s);
						s2 = "[]";
						break;
					}
					default: {
						break Label_0078;
					}
				}
				f.method708(sb.append(s2).toString());
			}
		}
		if (s.contains("java/util/TimerTask")) {
			super.visitTypeInsn(n, s.replace("java/util/TimerTask", "emulator/custom/subclass/SubTimerTask"));
			return;
		}
		if (s.contains("java/util/Timer")) {
			super.visitTypeInsn(n, s.replace("java/util/Timer", "emulator/custom/subclass/Timer"));
			return;
		}
		if (s.contains("com/gcjsp/v10/")) {
			super.visitTypeInsn(n, s.replace("com/gcjsp/v10/", "com/vodafone/v10/"));
			return;
		}
		if (s.contains("tw/com/fareastone/v10/")) {
			super.visitTypeInsn(n, s.replace("tw/com/fareastone/v10/", "com/vodafone/v10/"));
			return;
		}
		if (s.contains("com/bmc/media/")) {
			super.visitTypeInsn(n, s.replace("com/bmc/media/", "com/sprintpcs/media/"));
			return;
		}
		if (s.contains("com/docomostar/")) {
			super.visitTypeInsn(n, s.replace("com/docomostar/", "com/nttdocomo/"));
			return;
		}
		super.visitTypeInsn(n, s);
	}

	public final void visitIntInsn(final int n, final int n2) {
		if (n2 == 6 && CustomClassLoader.isProtected("java.lang.Float", false)) {
			super.visitMethodInsn(184, "emulator/custom/CustomMethod", "checkFloat", "()V");
		}
		if (n2 == 7 && CustomClassLoader.isProtected("java.lang.Double", false)) {
			super.visitMethodInsn(184, "emulator/custom/CustomMethod", "checkDouble", "()V");
		}
		Label_0116:
		{
			if (Settings.enableNewTrack && n == 188) {
				CustomMethodAdapter f = null;
				String s = null;
				switch (n2) {
					case 4: {
						f = this;
						s = "new boolean[]";
						break;
					}
					case 5: {
						f = this;
						s = "new char[]";
						break;
					}
					case 6: {
						f = this;
						s = "new float[]";
						break;
					}
					case 7: {
						f = this;
						s = "new double[]";
						break;
					}
					case 8: {
						f = this;
						s = "new byte[]";
						break;
					}
					case 9: {
						f = this;
						s = "new short[]";
						break;
					}
					case 10: {
						f = this;
						s = "new int[]";
						break;
					}
					case 11: {
						f = this;
						s = "new long[]";
						break;
					}
					default: {
						break Label_0116;
					}
				}
				f.method708(s);
			}
		}
		super.visitIntInsn(n, n2);
	}

	public void visitTryCatchBlock(Label var1, Label var2, Label var3, String var4) {
		if (var4 != null && var4.startsWith("com/docomostar/")) {
			var4 = var4.replace("com/docomostar/", "com/nttdocomo/");
		}
		super.visitTryCatchBlock(var1, var2, var3, var4);
	}

	public final void visitCode() {
		if (Settings.enableMethodTrack) {
			this.method707(1);
			super.visitLdcInsn((this.methodName + this.methodDesc));
			super.visitMethodInsn(184, "emulator/custom/CustomMethod", "beginMethod", "(Ljava/lang/String;)V");
		}
	}

	public final void visitInsn(final int n) {
		if (Settings.enableMethodTrack) {
			switch (n) {
				case 172:
				case 173:
				case 174:
				case 175:
				case 176:
				case 177:
				case 191: {
					this.method707(1);
					super.visitLdcInsn((this.methodName + this.methodDesc));
					super.visitMethodInsn(184, "emulator/custom/CustomMethod", "endMethod", "(Ljava/lang/String;)V");
					break;
				}
			}
		}
		super.visitInsn(n);
	}
}
