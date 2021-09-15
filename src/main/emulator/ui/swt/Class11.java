package emulator.ui.swt;

import emulator.ui.*;
import org.eclipse.swt.custom.*;
import java.io.*;
import org.eclipse.swt.layout.*;
import emulator.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

public final class Class11 implements ILogStream, ControlListener, DisposeListener
{
    private Shell aShell568;
    PrintStream aPrintStream569;
    LogStream aClass159_570;
    Class11 aClass11_571;
    static String aString572;
    private boolean aBoolean573;
    private StyledText aStyledText574;
    private Shell aShell575;
    private boolean aBoolean576;
    
    public Class11() {
        super();
        this.aShell568 = null;
        this.aStyledText574 = null;
        try {
            this.aClass159_570 = new LogStream(this);
            final File file;
            if (!(file = new File(Emulator.getAbsolutePath() + "/log.txt")).exists()) {
                file.createNewFile();
            }
            this.aPrintStream569 = new PrintStream(new FileOutputStream(file));
            this.aClass11_571 = this;
        }
        catch (Exception ex) {}
    }
    
    public final void print(final String s) {
        this.aPrintStream569.print(s);
        if (this.aBoolean573) {
            this.method326(s);
        }
    }
    
    public final void println(final String s) {
        this.aPrintStream569.println(s);
        if (this.aBoolean573) {
            this.method326(s + "\n");
        }
    }
    
    public final void println() {
        this.aPrintStream569.println();
        if (this.aBoolean573) {
            this.method326("\n");
        }
    }
    
    public final void stdout(final String s) {
        this.aPrintStream569.println(s);
        this.aClass159_570.method831(s);
        if (this.aBoolean573) {
            this.method326(s + "\n");
        }
    }
    
    public final void printStackTrace(final String s) {
        this.println("==StackTrace==" + s + "==StackTrace==");
    }
    
    private void method326(final String aString572) {
        Class11.aString572 = aString572;
        EmulatorImpl.syncExec(new Textout(this));
    }
    
    private void method332() {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(Emulator.getAbsolutePath() + "/log.txt"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.aStyledText574.append(line + "\n");
            }
            bufferedReader.close();
            this.aStyledText574.setTopIndex(this.aStyledText574.getLineCount());
        }
        catch (Exception ex) {}
    }
    
    public final boolean method327() {
        return this.aBoolean573;
    }
    
    public final Shell method328() {
        return this.aShell568;
    }
    
    public final void method329(final Shell aShell575) {
        this.method334();
        this.method332();
        final Display current = Display.getCurrent();
        this.aShell575 = aShell575;
        ((Control)this.aShell568).setLocation(aShell575.getLocation().x + aShell575.getSize().x, aShell575.getLocation().y);
        ((Control)this.aShell568).setSize(aShell575.getSize());
        this.aBoolean573 = true;
        this.aBoolean576 = true;
        this.aShell568.open();
        ((Control)this.aShell568).addControlListener((ControlListener)this);
        ((Widget)this.aShell568).addDisposeListener((DisposeListener)this);
        ((Composite)aShell575).setFocus();
        while (!((Widget)this.aShell568).isDisposed()) {
            if (!current.readAndDispatch()) {
                current.sleep();
            }
        }
        this.aBoolean573 = false;
    }
    
    public final void method330() {
        if (this.aShell568 != null && !((Widget)this.aShell568).isDisposed()) {
            this.aShell568.dispose();
        }
        this.aBoolean573 = false;
    }
    
    private void method334() {
        final FillLayout layout;
        (layout = new FillLayout()).spacing = 0;
        layout.marginWidth = 1;
        layout.marginHeight = 1;
        ((Decorations)(this.aShell568 = new Shell())).setText(UILocale.uiText("LOG_FRAME_TITLE", "LogFrame"));
        ((Decorations)this.aShell568).setImage(new Image((Device)Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
        ((Composite)this.aShell568).setLayout((Layout)layout);
        ((Control)this.aShell568).setSize(new Point(310, 254));
        this.aStyledText574 = new StyledText((Composite)this.aShell568, 2816);
    }
    
    public final boolean method333() {
        return this.aBoolean576;
    }
    
    public final void controlMoved(final ControlEvent controlEvent) {
        Class11 class11;
        boolean aBoolean576;
        if (Math.abs(this.aShell575.getLocation().x + this.aShell575.getSize().x - this.aShell568.getLocation().x) < 10 && Math.abs(this.aShell575.getLocation().y - this.aShell568.getLocation().y) < 20) {
            ((Control)this.aShell568).setLocation(this.aShell575.getLocation().x + this.aShell575.getSize().x, this.aShell575.getLocation().y);
            class11 = this;
            aBoolean576 = true;
        }
        else {
            class11 = this;
            aBoolean576 = false;
        }
        class11.aBoolean576 = aBoolean576;
    }
    
    public final void controlResized(final ControlEvent controlEvent) {
    }
    
    public final void widgetDisposed(final DisposeEvent disposeEvent) {
        this.method330();
    }
    
    static StyledText method331(final Class11 class11) {
        return class11.aStyledText574;
    }
    
    private final class Textout implements Runnable
    {
        private final Class11 aClass11_1301;
        
        private Textout(final Class11 aClass11_1301) {
            super();
            this.aClass11_1301 = aClass11_1301;
        }
        
        public final void run() {
            if (((Widget)Class11.method331(this.aClass11_1301)).isDisposed()) {
                return;
            }
            Class11.method331(this.aClass11_1301).append(Class11.aString572);
            Class11.method331(this.aClass11_1301).setTopIndex(Class11.method331(this.aClass11_1301).getLineCount());
        }
        
        Textout(final Class11 class11, final Class89 object) {
            this(class11);
        }
    }
    
    private final class LogStream extends PrintStream
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
}
