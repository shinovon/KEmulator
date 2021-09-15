package emulator.ui.swt;

import java.io.*;

final class LogStream extends PrintStream
{
    PrintStream aPrintStream1401;
    private final Class11 aClass11_1402;
    
    LogStream(final Class11 aClass11_1402) {
        super(System.out, true);
        this.aClass11_1402 = aClass11_1402;
        this.aPrintStream1401 = System.out;
        System.setOut(this);
        System.setErr(this);
    }
    
    public final void print(final String s) {
        this.aClass11_1402.aClass11_571.print(">>" + s);
        this.aPrintStream1401.print(s);
    }
    
    public final void println(final String s) {
        this.aClass11_1402.aClass11_571.println(">>" + s);
        this.aPrintStream1401.println(s);
    }
    
    public final void print(final Object o) {
        this.aClass11_1402.aClass11_571.print(">>" + o);
        this.aPrintStream1401.print(o);
    }
    
    public final void println(final Object o) {
        this.aClass11_1402.aClass11_571.println(">>" + o);
        this.aPrintStream1401.println(o);
    }
    
    public final void println() {
        this.aClass11_1402.aClass11_571.println(">>");
        this.aPrintStream1401.println();
    }
    
    public final void method831(final String s) {
        this.aPrintStream1401.println(s);
    }
}
