package emulator.custom;

import org.objectweb.asm.*;
import emulator.*;
import emulator.debug.*;

public final class CustomMethodAdapter extends MethodAdapter implements Opcodes {
    private int anInt1185;
    private String sourceFile;
    private String aString1187;
    private String aString1189;
    private String aString1190;
    private int sourceLine;

    private void method707(final int n) {
        this.anInt1185 = Math.max(this.anInt1185, n);
    }

    public final void visitMethodInsn(final int acc, final String cls, final String name, String sign) {
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
                        super.visitMethodInsn(184, "emulator/custom/CustomMethod", "exit", sign);
                        return;
                    }
                }
            } else if (cls.equals("java/util/Random") && (Settings.recordKeys || Settings.playingRecordedKeys)) {
                if (name.equals("<init>")) {
                    if (sign.equals("()V")) {
                        this.method707(2);
                        super.visitLdcInsn((Object) new Long(Settings.recordedRandomSeed));
                        super.visitMethodInsn(acc, cls, name, "(J)V");
                        return;
                    }
                    super.visitInsn(88);
                    super.visitLdcInsn((Object) new Long(Settings.recordedRandomSeed));
                    super.visitMethodInsn(acc, cls, name, sign);
                    return;
                }
            } else if (cls.equals("java/lang/Thread")) {
                if (name.equals("yield")) {
                    super.visitMethodInsn(184, "emulator/custom/CustomMethod", "yield", sign);
                    return;
                }
            } else if (cls.equals("java/lang/Class")) {
                if (name.equals("getResourceAsStream")) {
                    super.visitMethodInsn(184, "emulator/custom/CustomMethod", "getResourceAsStream", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/io/InputStream;");
                    return;
                }
            } else if (cls.equals("java/lang/String")) {
                if (name.equals("<init>") && sign.startsWith("([B") && !sign.endsWith("Ljava/lang/String;)V")) {
                    this.method707(1);
                    super.visitLdcInsn((Object) Settings.fileEncoding);
                    super.visitMethodInsn(acc, cls, name, sign.substring(0, sign.length() - 2) + "Ljava/lang/String;)V");
                    return;
                }
                if (name.equals("getBytes") && sign.startsWith("()")) {
                    this.method707(1);
                    super.visitLdcInsn((Object) Settings.fileEncoding);
                    super.visitMethodInsn(acc, cls, name, "(Ljava/lang/String;)[B");
                    return;
                }
            } else if (cls.equals("java/util/Vector")) {
                if (name.equals("add") || name.equals("get") || name.equals("set") || name.equals("clear") || name.equals("remove")) {
                    System.out.println("!!Error in verify methods: \"" + cls + "." + name + sign + "\" not supported!!");
                }
            } else {
                if (cls.equals("java/util/Timer")) {
                    if (sign.indexOf("java/util/TimerTask") != -1) {
                        sign = sign.replaceAll("java/util/TimerTask", "emulator/custom/subclass/SubTimerTask");
                    }
                    super.visitMethodInsn(acc, "emulator/custom/subclass/Timer", name, sign);
                    return;
                }
                if (cls.equals("java/util/TimerTask")) {
                    super.visitMethodInsn(acc, "emulator/custom/subclass/SubTimerTask", name, sign);
                    return;
                }
                if (cls.equals("javax/microedition/m3g/Transform")) {
                    super.visitMethodInsn(acc, "emulator/custom/subclass/M3GDebug", "waitDebug", "()V");
                    super.visitMethodInsn(acc, cls, name, sign);
                    return;
                }
                String s4;
                String s5;
                String s6;
                if (sign.indexOf("java/util/TimerTask") != -1) {
                    s4 = sign;
                    s5 = "java/util/TimerTask";
                    s6 = "emulator/custom/subclass/SubTimerTask";
                } else {
                    if (sign.indexOf("java/util/Timer") == -1) {
                        break Label_0576;
                    }
                    s4 = sign;
                    s5 = "java/util/Timer";
                    s6 = "emulator/custom/subclass/Timer";
                }
                sign = s4.replaceAll(s5, s6);
            }
        }
        super.visitMethodInsn(acc, cls, name, sign);
    }

    public final void visitFieldInsn(final int n, final String s, final String s2, String s3) {
        Label_0029:
        {
            String s4;
            if (s3.equals("Ljava/util/TimerTask;")) {
                s4 = "Lemulator/custom/subclass/SubTimerTask;";
            } else {
                if (!s3.equals("Ljava/util/Timer;")) {
                    break Label_0029;
                }
                s4 = "Lemulator/custom/subclass/Timer;";
            }
            s3 = s4;
        }
        super.visitFieldInsn(n, s, s2, s3);
    }

    public final void visitMaxs(final int n, final int n2) {
        super.visitMaxs(n + this.anInt1185, n2);
    }

    public CustomMethodAdapter(final MethodVisitor methodVisitor, final String aString1186, final String s, final String aString1187) {
        super(methodVisitor);
        this.anInt1185 = 0;
        if (Settings.enableNewTrack || Settings.enableMethodTrack) {
            this.sourceFile = aString1186;
            this.aString1187 = aString1186 + "." + s;
            this.aString1189 = aString1187;
            this.aString1190 = "";
            this.sourceLine = 0;
            final int lastIndex;
            if ((lastIndex = this.sourceFile.lastIndexOf(36)) != -1) {
                this.sourceFile = this.sourceFile.substring(0, lastIndex);
            }
            final int lastIndex2;
            if ((lastIndex2 = this.sourceFile.lastIndexOf(46)) != -1) {
                this.sourceFile = this.sourceFile.substring(lastIndex2 + 1);
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
            super.visitLdcInsn((Object) (this.aString1190 = s + "\t at " + this.aString1187 + " (" + this.sourceFile + ".java:" + this.sourceLine + ")\n"));
            super.visitMethodInsn(184, "emulator/custom/CustomMethod", "showTrackInfo", "(Ljava/lang/String;)V");
            this.method707(1);
        } catch (Exception ex) {
            Emulator.AntiCrack(ex);
        }
    }

    public final void visitMultiANewArrayInsn(final String s, final int n) {
        if (Settings.enableNewTrack) {
            this.method708("new " + emulator.debug.ClassTypes.method870(s));
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
        if (s.equals("java/util/Timer")) {
            super.visitTypeInsn(n, "emulator/custom/subclass/Timer");
            return;
        }
        super.visitTypeInsn(n, s);
    }

    public final void visitIntInsn(final int n, final int n2) {
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

    public final void visitCode() {
        if (Settings.enableMethodTrack) {
            this.method707(1);
            super.visitLdcInsn((Object) (this.aString1187 + this.aString1189));
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
                    super.visitLdcInsn((Object) (this.aString1187 + this.aString1189));
                    super.visitMethodInsn(184, "emulator/custom/CustomMethod", "endMethod", "(Ljava/lang/String;)V");
                    break;
                }
            }
        }
        super.visitInsn(n);
    }
}
