package emulator.ui.swt;

import emulator.ui.effect.a;
import emulator.graphics2D.swt.ImageSWT;

import org.eclipse.swt.layout.*;
import emulator.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.graphics.*;
import java.io.*;
import java.util.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

public final class Class54 implements MouseListener, MouseMoveListener
{
    private Shell aShell806;
    private CLabel aCLabel805;
    private Link aLink807;
    private Link aLink816;
    private Link aLink820;
    private StyledText aStyledText808;
    private Button aButton809;
    private Canvas aCanvas810;
    private a ana811;
    private ImageSWT ad812;
    private ImageSWT ad817;
    private Timer aTimer813;
    GC aGC814;
    int[] anIntArray815;
    int[] anIntArray818;
    private Button aButton819;
    
    public Class54() {
        super();
        this.aShell806 = null;
        this.aCLabel805 = null;
        this.aLink807 = null;
        this.aLink816 = null;
        this.aLink820 = null;
        this.aStyledText808 = null;
        this.aButton809 = null;
        this.aCanvas810 = null;
        this.aButton819 = null;
    }
    
    private void method462(final Shell shell) {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalIndent = 2;
        layoutData.horizontalSpan = 2;
        layoutData.horizontalAlignment = 4;
        layoutData.verticalAlignment = 2;
        layoutData.grabExcessHorizontalSpace = true;
        final GridData layoutData2;
        (layoutData2 = new GridData()).heightHint = 20;
        layoutData2.widthHint = 90;
        final GridData gridData;
        (gridData = new GridData()).horizontalAlignment = 4;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = 2;
        final GridData layoutData3;
        (layoutData3 = new GridData()).horizontalAlignment = 3;
        layoutData3.grabExcessHorizontalSpace = false;
        layoutData3.verticalAlignment = 2;
        final GridData layoutData4;
        (layoutData4 = new GridData()).horizontalAlignment = 4;
        layoutData4.grabExcessHorizontalSpace = true;
        layoutData4.grabExcessVerticalSpace = true;
        layoutData4.horizontalSpan = 2;
        layoutData4.verticalAlignment = 4;
        final GridData layoutData5;
        (layoutData5 = new GridData()).horizontalAlignment = 4;
        layoutData5.grabExcessHorizontalSpace = true;
        layoutData5.grabExcessVerticalSpace = false;
        layoutData5.verticalAlignment = 4;
        layoutData5.verticalSpan = 3;
        final GridData gridData2;
        (gridData2 = new GridData()).horizontalIndent = 5;
        gridData2.horizontalAlignment = 4;
        gridData2.grabExcessHorizontalSpace = false;
        gridData2.grabExcessVerticalSpace = false;
        gridData2.verticalAlignment = 4;
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 2;
        layout.horizontalSpacing = 0;
        ((Decorations)(this.aShell806 = new Shell(shell, 67680))).setText(emulator.UILocale.get("ABOUT_FRAME_TITLE", "About & Help"));
        ((Decorations)this.aShell806).setImage(new Image((Device)Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
        ((Composite)this.aShell806).setLayout((Layout)layout);
        ((Control)this.aShell806).setSize(new Point(360, 400));
        ((Control)(this.aCLabel805 = new CLabel((Composite)this.aShell806, 0))).setLayoutData((Object)layoutData5);
        aCLabel805.setFont(EmulatorScreen.f);
        this.aCLabel805.setText(Emulator.getAboutString());
        this.method463();
        (this.aLink816 = new Link((Composite)this.aShell806, 0)).setText("<a>" + "nnmod web page" + "</a>");
        ((Control)this.aLink816).setLayoutData((Object)gridData2);
        //((Control)this.aLink816).setEnabled(false);
        this.aLink816.addSelectionListener((SelectionListener)new Class158(this));
        (this.aLink820 = new Link((Composite)this.aShell806, 0)).setText("<a>" + "Discussion chat (ru && en)" + "</a>");
        ((Control)this.aLink820).setLayoutData((Object)gridData2);
     //   ((Control)this.aLink820).setEnabled(false);
        this.aLink820.addSelectionListener((SelectionListener)new Class157(this));
        (this.aLink807 = new Link((Composite)this.aShell806, 0)).setText("Mod by shinovon\n" + emulator.UILocale.get("ABOUT_AUTHOR", "Author") + ": <a>Wu.Liang</a>  (c) 2006,2008");
        ((Control)this.aLink807).setLayoutData((Object)layoutData);
        this.aLink807.addSelectionListener((SelectionListener)new Class156(this));
        ((Control)(this.aStyledText808 = new StyledText((Composite)this.aShell806, 2562))).setLayoutData((Object)layoutData4);
        ((Composite)this.aStyledText808).setFocus();
        this.aStyledText808.setEditable(false);
        this.aStyledText808.setIndent(5);
        aStyledText808.setFont(EmulatorScreen.f);
        (this.aButton819 = new Button((Composite)this.aShell806, 8388608)).setText(emulator.UILocale.get("ABOUT_ONLINE_MANUAL", "Online Manual"));
        ((Control)this.aButton819).setLayoutData((Object)layoutData2);
        ((Control)this.aButton819).setEnabled(false);
        this.aButton819.addSelectionListener((SelectionListener)new Class162(this));
        (this.aButton809 = new Button((Composite)this.aShell806, 8388616)).setText(emulator.UILocale.get("DIALOG_OK", "OK"));
        ((Control)this.aButton809).setLayoutData((Object)layoutData3);
        layoutData3.heightHint = 20;
        layoutData3.widthHint = 70;
        this.aButton809.addSelectionListener((SelectionListener)new Class163(this));
        this.method453();
    }
    
    private void method453() {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/res/help")));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final int charCount = this.aStyledText808.getCharCount();
                if (line.startsWith("$")) {
                    this.aStyledText808.append(line.substring(1) + "\n");
                    this.aStyledText808.setStyleRange(new StyleRange(charCount, line.length(), Display.getCurrent().getSystemColor(9), (Color)null));
                }
                else {
                    this.aStyledText808.append(line + "\n");
                }
            }
            bufferedReader.close();
        }
        catch (Exception ex) {}
    }
    
    public final void method454(final Shell shell) {
        this.method462(shell);
        final Display display = ((Widget)shell).getDisplay();
        ((Control)this.aShell806).setLocation(shell.getLocation().x + (shell.getSize().x - this.aShell806.getSize().x >> 1), shell.getLocation().y + (shell.getSize().y - this.aShell806.getSize().y >> 1));
        this.aShell806.open();
        while (!((Widget)this.aShell806).isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
    
    private void method463() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.verticalAlignment = 4;
        layoutData.heightHint = 146;
        layoutData.widthHint = 156;
        ((Control)(this.aCanvas810 = new Canvas((Composite)this.aShell806, 537133056))).setLayoutData((Object)layoutData);
        ((Control)this.aCanvas810).addMouseListener((MouseListener)this);
        ((Control)this.aCanvas810).addMouseMoveListener((MouseMoveListener)this);
        this.aGC814 = new GC((Drawable)this.aCanvas810);
        this.method455("".getClass().getResourceAsStream("/res/sign"));
    }
    
    private void method455(final InputStream inputStream) {
        try {
            this.ad812 = new ImageSWT(inputStream);
            this.ad817 = new ImageSWT(this.ad812.getWidth(), this.ad812.getHeight(), false, 6393563);
            this.anIntArray815 = this.ad812.getData();
            this.anIntArray818 = this.ad817.getData();
            (this.ana811 = new a()).method135(this.ad812.getWidth(), this.ad812.getHeight());
            this.ana811.method137(this.ad812.getWidth() >> 1, this.ad812.getHeight() >> 1, 10, 500, this.ana811.anInt324);
            (this.aTimer813 = new Timer()).schedule(new WaterTask(this), 0L, 30L);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public final void mouseDoubleClick(final MouseEvent mouseEvent) {
    }
    
    public final void mouseDown(final MouseEvent mouseEvent) {
        this.ana811.method137(mouseEvent.x, mouseEvent.y, 5, 500, this.ana811.anInt324);
    }
    
    public final void mouseUp(final MouseEvent mouseEvent) {
    }
    
    public final void mouseMove(final MouseEvent mouseEvent) {
        this.ana811.method137(mouseEvent.x, mouseEvent.y, 5, 50, this.ana811.anInt324);
    }
    
    static Shell method456(final Class54 class54) {
        return class54.aShell806;
    }
    
    static a method457(final Class54 class54) {
        return class54.ana811;
    }
    
    static Canvas method458(final Class54 class54) {
        return class54.aCanvas810;
    }
    
    static Timer method459(final Class54 class54) {
        return class54.aTimer813;
    }
    
    static a method460(final Class54 class54, final a ana811) {
        return class54.ana811 = ana811;
    }
    
    static ImageSWT method461(final Class54 class54) {
        return class54.ad817;
    }
    
    final static class WaterTask extends TimerTask
    {
        private final Class54 aClass54_775;
        
        private WaterTask(final Class54 aClass54_775) {
            super();
            this.aClass54_775 = aClass54_775;
        }
        
        public final void run() {
            Class54.method457(this.aClass54_775).method136(this.aClass54_775.anIntArray815, this.aClass54_775.anIntArray818);
            EmulatorImpl.syncExec(new Water(this, aClass54_775.ana811));
        }
        
        WaterTask(final Class54 class54, final Class158 class55) {
            this(class54);
        }
        
        static Class54 method433(final WaterTask waterTask) {
            return waterTask.aClass54_775;
        }
    }

    public void finalize() {
        EmulatorImpl.asyncExec(new Runnable() {
            public void run() {
                if (!aGC814.isDisposed()) aGC814.dispose();
            }
        });
    }
}
