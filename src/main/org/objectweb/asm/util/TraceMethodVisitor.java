package org.objectweb.asm.util;

import java.util.*;

import org.objectweb.asm.*;
import org.objectweb.asm.signature.*;

public class TraceMethodVisitor extends TraceAbstractVisitor {
	protected MethodVisitor mv;
	protected String tab2;
	protected String tab3;
	protected String ltab;
	protected final HashMap labelNames;

	public TraceMethodVisitor() {
		this(null);
	}

	public TraceMethodVisitor(final MethodVisitor mv) {
		this.tab2 = "    ";
		this.tab3 = "      ";
		this.ltab = "   ";
		this.labelNames = new HashMap();
		this.mv = mv;
	}

	public void visitAttribute(final Attribute attribute) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(super.tab).append("ATTRIBUTE ");
		this.appendDescriptor(-1, attribute.type);
//        if (attribute instanceof Traceable) {
//            ((Traceable) attribute).trace(((AbstractVisitor) this).buf, (Map) this.labelNames);
//        } else {
		((AbstractVisitor) this).buf.append(" : unknown\n");
//        }
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitAttribute(attribute);
		}
	}

	public void visitCode() {
		if (this.mv != null) {
			this.mv.visitCode();
		}
	}

	public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.ltab);
		((AbstractVisitor) this).buf.append("FRAME ");
		switch (type) {
			case -1:
			case 0: {
				((AbstractVisitor) this).buf.append("FULL [");
				this.appendFrameTypes(nLocal, local);
				((AbstractVisitor) this).buf.append("] [");
				this.appendFrameTypes(nStack, stack);
				((AbstractVisitor) this).buf.append("]");
				break;
			}
			case 1: {
				((AbstractVisitor) this).buf.append("APPEND [");
				this.appendFrameTypes(nLocal, local);
				((AbstractVisitor) this).buf.append("]");
				break;
			}
			case 2: {
				((AbstractVisitor) this).buf.append("CHOP ").append(nLocal);
				break;
			}
			case 3: {
				((AbstractVisitor) this).buf.append("SAME");
				break;
			}
			case 4: {
				((AbstractVisitor) this).buf.append("SAME1 ");
				this.appendFrameTypes(1, stack);
				break;
			}
		}
		((AbstractVisitor) this).buf.append("\n");
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitFrame(type, nLocal, local, nStack, stack);
		}
	}

	public void visitInsn(final int opcode) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append(AbstractVisitor.OPCODES[opcode]).append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitInsn(opcode);
		}
	}

	public void visitIntInsn(final int opcode, final int operand) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append(AbstractVisitor.OPCODES[opcode]).append(' ').append((opcode == 188) ? AbstractVisitor.TYPES[operand] : Integer.toString(operand)).append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitIntInsn(opcode, operand);
		}
	}

	public void visitVarInsn(final int opcode, final int var) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append(AbstractVisitor.OPCODES[opcode]).append(' ').append(var).append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitVarInsn(opcode, var);
		}
	}

	public void visitTypeInsn(final int opcode, final String type) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append(AbstractVisitor.OPCODES[opcode]).append(' ');
		TraceMethodVisitor traceMethodVisitor;
		int n;
		if (type.startsWith("[")) {
			traceMethodVisitor = this;
			n = 1;
		} else {
			traceMethodVisitor = this;
			n = 0;
		}
		traceMethodVisitor.appendDescriptor(n, type);
		((AbstractVisitor) this).buf.append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitTypeInsn(opcode, type);
		}
	}

	public void visitFieldInsn(final int opcode, final String owner, final String name, final String descriptor) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append(AbstractVisitor.OPCODES[opcode]).append(' ');
		this.appendDescriptor(0, owner);
		((AbstractVisitor) this).buf.append('.').append(name).append(" : ");
		this.appendDescriptor(1, descriptor);
		((AbstractVisitor) this).buf.append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitFieldInsn(opcode, owner, name, descriptor);
		}
	}

	public void visitMethodInsn(final int opcode, final String owner, final String name, final String descriptor) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append(AbstractVisitor.OPCODES[opcode]).append(' ');
		this.appendDescriptor(0, owner);
		((AbstractVisitor) this).buf.append('.').append(name).append(' ');
		this.appendDescriptor(3, descriptor);
		((AbstractVisitor) this).buf.append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitMethodInsn(opcode, owner, name, descriptor);
		}
	}

	public void visitJumpInsn(final int opcode, final Label label) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append(AbstractVisitor.OPCODES[opcode]).append(' ');
		this.appendLabel(label);
		((AbstractVisitor) this).buf.append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitJumpInsn(opcode, label);
		}
	}

	public void visitLabel(final Label label) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.ltab);
		this.appendLabel(label);
		((AbstractVisitor) this).buf.append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitLabel(label);
		}
	}

	public void visitLdcInsn(final Object value) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append("LDC ");
		if (value instanceof String) {
			AbstractVisitor.appendString(((AbstractVisitor) this).buf, (String) value);
		} else if (value instanceof Type) {
			((AbstractVisitor) this).buf.append(((Type) value).getDescriptor() + ".class");
		} else {
			((AbstractVisitor) this).buf.append(value);
		}
		((AbstractVisitor) this).buf.append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitLdcInsn(value);
		}
	}

	public void visitIincInsn(final int var, final int increment) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append("IINC ").append(var).append(' ').append(increment).append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitIincInsn(var, increment);
		}
	}

	public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append("TABLESWITCH\n");
		for (int i = 0; i < labels.length; ++i) {
			((AbstractVisitor) this).buf.append(this.tab3).append(min + i).append(": ");
			this.appendLabel(labels[i]);
			((AbstractVisitor) this).buf.append('\n');
		}
		((AbstractVisitor) this).buf.append(this.tab3).append("default: ");
		this.appendLabel(dflt);
		((AbstractVisitor) this).buf.append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitTableSwitchInsn(min, max, dflt, labels);
		}
	}

	public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append("LOOKUPSWITCH\n");
		for (int i = 0; i < labels.length; ++i) {
			((AbstractVisitor) this).buf.append(this.tab3).append(keys[i]).append(": ");
			this.appendLabel(labels[i]);
			((AbstractVisitor) this).buf.append('\n');
		}
		((AbstractVisitor) this).buf.append(this.tab3).append("default: ");
		this.appendLabel(dflt);
		((AbstractVisitor) this).buf.append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitLookupSwitchInsn(dflt, keys, labels);
		}
	}

	public void visitMultiANewArrayInsn(final String descriptor, final int numDimensions) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append("MULTIANEWARRAY ");
		this.appendDescriptor(1, descriptor);
		((AbstractVisitor) this).buf.append(' ').append(numDimensions).append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitMultiANewArrayInsn(descriptor, numDimensions);
		}
	}

	public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append("TRYCATCHBLOCK ");
		this.appendLabel(start);
		((AbstractVisitor) this).buf.append(' ');
		this.appendLabel(end);
		((AbstractVisitor) this).buf.append(' ');
		this.appendLabel(handler);
		((AbstractVisitor) this).buf.append(' ');
		this.appendDescriptor(0, type);
		((AbstractVisitor) this).buf.append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitTryCatchBlock(start, end, handler, type);
		}
	}

	public void visitLocalVariable(final String name, final String descriptor, final String s, final Label start, final Label end, final int index) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append("LOCALVARIABLE ").append(name).append(' ');
		this.appendDescriptor(1, descriptor);
		((AbstractVisitor) this).buf.append(' ');
		this.appendLabel(start);
		((AbstractVisitor) this).buf.append(' ');
		this.appendLabel(end);
		((AbstractVisitor) this).buf.append(' ').append(index).append('\n');
		if (s != null) {
			((AbstractVisitor) this).buf.append(this.tab2);
			this.appendDescriptor(2, s);
			final TraceSignatureVisitor signatureVisitor = new TraceSignatureVisitor(0);
			new SignatureReader(s).acceptType(signatureVisitor);
			((AbstractVisitor) this).buf.append(this.tab2).append("// declaration: ").append(signatureVisitor.getDeclaration()).append('\n');
		}
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitLocalVariable(name, descriptor, s, start, end, index);
		}
	}

	public void visitLineNumber(final int line, final Label start) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append("LINENUMBER ").append(line).append(' ');
		this.appendLabel(start);
		((AbstractVisitor) this).buf.append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitLineNumber(line, start);
		}
	}

	public void visitMaxs(final int maxStack, final int maxLocals) {
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append("MAXSTACK = ").append(maxStack).append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		((AbstractVisitor) this).buf.setLength(0);
		((AbstractVisitor) this).buf.append(this.tab2).append("MAXLOCALS = ").append(maxLocals).append('\n');
		((AbstractVisitor) this).text.add(((AbstractVisitor) this).buf.toString());
		if (this.mv != null) {
			this.mv.visitMaxs(maxStack, maxLocals);
		}
	}

	public void visitEnd() {
		super.visitEnd();
		if (this.mv != null) {
			this.mv.visitEnd();
		}
	}

	private void appendFrameTypes(final int n, final Object[] array) {
		for (int i = 0; i < n; ++i) {
			if (i > 0) {
				((AbstractVisitor) this).buf.append(' ');
			}
			if (array[i] instanceof String) {
				final String s;
				TraceMethodVisitor traceMethodVisitor;
				int n2;
				if ((s = (String) array[i]).startsWith("[")) {
					traceMethodVisitor = this;
					n2 = 1;
				} else {
					traceMethodVisitor = this;
					n2 = 0;
				}
				traceMethodVisitor.appendDescriptor(n2, s);
			} else if (array[i] instanceof Integer) {
				TraceMethodVisitor traceMethodVisitor2 = null;
				int n3 = 0;
				String s2 = null;
				switch ((Integer) array[i]) {
					case 0: {
						traceMethodVisitor2 = this;
						n3 = 1;
						s2 = "T";
						break;
					}
					case 1: {
						traceMethodVisitor2 = this;
						n3 = 1;
						s2 = "I";
						break;
					}
					case 2: {
						traceMethodVisitor2 = this;
						n3 = 1;
						s2 = "F";
						break;
					}
					case 3: {
						traceMethodVisitor2 = this;
						n3 = 1;
						s2 = "D";
						break;
					}
					case 4: {
						traceMethodVisitor2 = this;
						n3 = 1;
						s2 = "J";
						break;
					}
					case 5: {
						traceMethodVisitor2 = this;
						n3 = 1;
						s2 = "N";
						break;
					}
					case 6: {
						traceMethodVisitor2 = this;
						n3 = 1;
						s2 = "U";
						break;
					}
					default: {
						continue;
					}
				}
				traceMethodVisitor2.appendDescriptor(n3, s2);
			} else {
				this.appendLabel((Label) array[i]);
			}
		}
	}

	protected void appendLabel(final Label label) {
		String string;
		if ((string = (String) this.labelNames.get(label)) == null) {
			string = "L" + this.labelNames.size();
			this.labelNames.put(label, string);
		}
		((AbstractVisitor) this).buf.append(string);
	}
}
