package org.objectweb.asm.util;

import org.objectweb.asm.Attribute;

public abstract class TraceAbstractVisitor
        extends AbstractVisitor {
    public static final int INTERNAL_NAME = 0;
    public static final int FIELD_DESCRIPTOR = 1;
    public static final int FIELD_SIGNATURE = 2;
    public static final int METHOD_DESCRIPTOR = 3;
    public static final int METHOD_SIGNATURE = 4;
    public static final int CLASS_SIGNATURE = 5;
    public static final int TYPE_DECLARATION = 6;
    public static final int CLASS_DECLARATION = 7;
    public static final int PARAMETERS_DECLARATION = 8;
    protected String tab = "  ";

    public TraceAbstractVisitor() {
    }

    public void visitAttribute(Attribute attribute) {
        this.buf.setLength(0);
        this.buf.append(this.tab).append("ATTRIBUTE ");
        this.appendDescriptor(-1, attribute.type);
//        if (attribute instanceof Traceable) {
//            ((Traceable) attribute).trace(this.buf, null);
//        } else {
            this.buf.append(" : unknown\n");
//        }
        this.text.add((Object) this.buf.toString());
    }

    public void visitEnd() {
    }

    protected void appendDescriptor(int n, String string) {
        if (n == 5 || n == 2 || n == 4) {
            if (string != null) {
                this.buf.append("// signature ").append(string).append('\n');
                return;
            }
        } else {
            this.buf.append(string);
        }
    }
}
