package emulator.ui.swt;

import emulator.ui.*;
import org.eclipse.swt.layout.*;
import java.nio.charset.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import emulator.*;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;
import emulator.graphics2D.swt.*;
import emulator.graphics2D.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

import org.eclipse.swt.widgets.*;

public final class Property implements IProperty
{
    private static Display aDisplay656;
    private Shell aShell655;
    private Combo aCombo657;
    private CLabel aCLabel658;
    private CLabel aCLabel673;
    private Combo aCombo675;
    private CLabel aCLabel683;
    private Group aGroup660;
    private Text aText662;
    private Button aButton666;
    private CLabel aCLabel694;
    private Text aText672;
    private CLabel aCLabel701;
    private Text aText684;
    private CLabel aCLabel707;
    private Text aText695;
    private CLabel aCLabel712;
    private Text aText702;
    private CLabel aCLabel717;
    private Text aText708;
    private CLabel aCLabel722;
    private Text aText713;
    private CLabel aCLabel726;
    private Text aText718;
    private CLabel aCLabel730;
    private Text aText723;
    private CLabel aCLabel734;
    private Text aText727;
    private Composite aComposite667;
    private Button aButton676;
    private Button aButton685;
    private Scale aScale669;
    private CLabel aCLabel738;
    private CTabFolder tabFolder;
    private Composite customComp;
    private Composite keyMapComp;
    private CLabel aCLabel741;
    private Text aText731;
    private CLabel aCLabel744;
    private Text aText735;
    private CLabel aCLabel747;
    private Text aText739;
    private CLabel aCLabel750;
    private Text aText742;
    private CLabel aCLabel753;
    private Text aText745;
    private CLabel aCLabel756;
    private Text aText748;
    private CLabel aCLabel759;
    private Text aText751;
    private CLabel aCLabel762;
    private Text aText754;
    private CLabel aCLabel765;
    private Text aText757;
    private CLabel aCLabel767;
    private Text aText760;
    private CLabel aCLabel769;
    private Text aText763;
    private CLabel aCLabel771;
    private Text aText766;
    private CLabel aCLabel773;
    private Text aText768;
    private CLabel aCLabel626;
    private Text aText770;
    private CLabel aCLabel628;
    private Text aText772;
    private CLabel aCLabel630;
    private Text aText774;
    private CLabel aCLabel632;
    private Text aText627;
    private CLabel aCLabel634;
    private Text aText629;
    private CLabel aCLabel636;
    private Text aText631;
    private Button aButton696;
    private Button aButton703;
    private Button aButton709;
    private Button aButton714;
    private Button aButton719;
    private Composite systemComp;
    private Composite coreApiComp;
    private Group aGroup678;
    private Group sysChecksGroup;
    private Button aButton724;
    private Button aButton728;
    private Button aButton732;
    private Button aButton736;
    private Button aButton740;
    private Button aButton743;
    private Button aButton746;
    private Button aButton749;
    private Button aButton752;
    private Composite sysFontComp;
    private CLabel aCLabel638;
    private Combo aCombo689;
    private CLabel aCLabel640;
    private Spinner aSpinner670;
    private Canvas aCanvas663;
    private CLabel aCLabel642;
    private Spinner aSpinner679;
    private Canvas aCanvas680;
    private CLabel aCLabel644;
    private Spinner aSpinner690;
    private Canvas aCanvas691;
    private CLabel aCLabel645;
    private Text aText633;
    private String device;
    private String defaultFont;
    private String rmsFolder;
    private String screenWidth;
    private String screenHeight;
    private String lsoft;
    private String rsoft;
    private String fire;
    private String up;
    private String down;
    private String left;
    private String right;
    private int fontSmallSize;
    private int fontMediumSIze;
    private int fontLargeSize;
    private CLabel aCLabel646;
    private Combo aCombo699;
    private Button aButton755;
    private static String[] aStringArray661;
    private IImage anIImage671;
    private IImage anIImage674;
    private IImage anIImage693;
    private Composite recordsComp;
    private Table aTable665;
    private CLabel aCLabel647;
    private Button aButton758;
    private Button aButton761;
    private Composite networkComp;
    private Group aGroup700;
    private CLabel aCLabel648;
    private Combo aCombo706;
    private CLabel aCLabel649;
    private Text aText635;
    private CLabel aCLabel650;
    private CLabel aCLabel651;
    private Text aText637;
    private CLabel aCLabel652;
    private Text aText639;
    private CLabel aCLabel653;
    private Text aText641;
    private CLabel aCLabel654;
    private Text aText643;
    private Button aButton764;
	//private Spinner inactiveTimerSpinner;
	private Button rpcBtn;
	private Button antiAliasBtn;
	private Composite mediaComp;
	private Group mediaGroup;
	private Text vlcDirText;
    
    public Property() {
        super();
        this.aShell655 = null;
        this.aCombo657 = null;
        this.aCLabel658 = null;
        this.aCLabel673 = null;
        this.aCombo675 = null;
        this.aCLabel683 = null;
        this.aGroup660 = null;
        this.aText662 = null;
        this.aButton666 = null;
        this.aCLabel694 = null;
        this.aText672 = null;
        this.aCLabel701 = null;
        this.aText684 = null;
        this.aCLabel707 = null;
        this.aText695 = null;
        this.aCLabel712 = null;
        this.aText702 = null;
        this.aCLabel717 = null;
        this.aText708 = null;
        this.aCLabel722 = null;
        this.aText713 = null;
        this.aCLabel726 = null;
        this.aText718 = null;
        this.aCLabel730 = null;
        this.aText723 = null;
        this.aCLabel734 = null;
        this.aText727 = null;
        this.aComposite667 = null;
        this.aButton676 = null;
        this.aButton685 = null;
        this.aScale669 = null;
        this.aCLabel738 = null;
        this.tabFolder = null;
        this.customComp = null;
        this.keyMapComp = null;
        this.aCLabel741 = null;
        this.aText731 = null;
        this.aCLabel744 = null;
        this.aText735 = null;
        this.aCLabel747 = null;
        this.aText739 = null;
        this.aCLabel750 = null;
        this.aText742 = null;
        this.aCLabel753 = null;
        this.aText745 = null;
        this.aCLabel756 = null;
        this.aText748 = null;
        this.aCLabel759 = null;
        this.aText751 = null;
        this.aCLabel762 = null;
        this.aText754 = null;
        this.aCLabel765 = null;
        this.aText757 = null;
        this.aCLabel767 = null;
        this.aText760 = null;
        this.aCLabel769 = null;
        this.aText763 = null;
        this.aCLabel771 = null;
        this.aText766 = null;
        this.aCLabel773 = null;
        this.aText768 = null;
        this.aCLabel626 = null;
        this.aText770 = null;
        this.aCLabel628 = null;
        this.aText772 = null;
        this.aCLabel630 = null;
        this.aText774 = null;
        this.aCLabel632 = null;
        this.aText627 = null;
        this.aCLabel634 = null;
        this.aText629 = null;
        this.aCLabel636 = null;
        this.aText631 = null;
        this.aButton696 = null;
        this.aButton703 = null;
        this.aButton709 = null;
        this.aButton714 = null;
        this.aButton719 = null;
        this.systemComp = null;
        this.coreApiComp = null;
        this.aGroup678 = null;
        this.sysChecksGroup = null;
        this.aButton724 = null;
        this.aButton728 = null;
        this.aButton732 = null;
        this.aButton736 = null;
        this.aButton740 = null;
        this.aButton743 = null;
        this.aButton746 = null;
        this.aButton749 = null;
        this.aButton752 = null;
        this.sysFontComp = null;
        this.aCLabel638 = null;
        this.aCombo689 = null;
        this.aCLabel640 = null;
        this.aSpinner670 = null;
        this.aCanvas663 = null;
        this.aCLabel642 = null;
        this.aSpinner679 = null;
        this.aCanvas680 = null;
        this.aCLabel644 = null;
        this.aSpinner690 = null;
        this.aCanvas691 = null;
        this.aCLabel645 = null;
        this.aText633 = null;
        this.aCLabel646 = null;
        this.aCombo699 = null;
        this.aButton755 = null;
        this.recordsComp = null;
        this.aTable665 = null;
        this.aCLabel647 = null;
        this.aButton758 = null;
        this.aButton761 = null;
        this.networkComp = null;
        this.aGroup700 = null;
        this.aCLabel648 = null;
        this.aCombo706 = null;
        this.aCLabel649 = null;
        this.aText635 = null;
        this.aCLabel650 = null;
        this.aCLabel651 = null;
        this.aText637 = null;
        this.aCLabel652 = null;
        this.aText639 = null;
        this.aCLabel653 = null;
        this.aText641 = null;
        this.aCLabel654 = null;
        this.aText643 = null;
        this.aButton764 = null;
        rpcBtn = null;
        antiAliasBtn = null;
        this.loadProperties();
        this.method353();
    }
    
    public final void method354(final Shell shell) {
        Property.aDisplay656 = ((Widget)shell).getDisplay();
        this.method372(shell);
        ((Control)this.aShell655).pack();
        ((Control)this.aShell655).setSize(480, this.aShell655.getSize().y);
        ((Control)this.aShell655).setLocation(shell.getLocation().x + (shell.getSize().x - this.aShell655.getSize().x >> 1), shell.getLocation().y + (shell.getSize().y - this.aShell655.getSize().y >> 1));
        this.aShell655.open();
        while (!((Widget)this.aShell655).isDisposed()) {
            if (!Property.aDisplay656.readAndDispatch()) {
                Property.aDisplay656.sleep();
            }
        }
        this.anIImage671 = null;
        this.anIImage674 = null;
        this.anIImage693 = null;
    }
    
    public final void resetDeviceName() {
        this.device = Emulator.deviceName;
    }
    
    public final String getDefaultFontName() {
        return this.defaultFont;
    }
    
    public final int getFontSmallSize() {
        return this.fontSmallSize;
    }
    
    public final int getFontMediumSize() {
        return this.fontMediumSIze;
    }
    
    public final int getFontLargeSize() {
        return this.fontLargeSize;
    }
    
    public final void setDefaultFontName(final String aString682) {
        this.defaultFont = aString682;
    }
    
    public final void setFontSmallSize(final int anInt664) {
        this.fontSmallSize = anInt664;
    }
    
    public final void getFontMediumSize(final int anInt681) {
        this.fontMediumSIze = anInt681;
    }
    
    public final void getFontLargeSize(final int anInt687) {
        this.fontLargeSize = anInt687;
    }
    
    private String method355() {
        return this.device + "_" + this.screenWidth + "x" + this.screenHeight;
    }
    
    public final String getRmsFolderPath() {
        String s = null;
        Label_0077: {
            StringBuffer sb;
            String substring;
            if ((s = this.rmsFolder).startsWith(".")) {
                sb = new StringBuffer().append(Emulator.getAbsolutePath());
                substring = s.substring(1);
            }
            else {
                if (!s.startsWith("/") && !s.startsWith("\\")) {
                    break Label_0077;
                }
                sb = new StringBuffer().append(Emulator.getAbsolutePath());
                substring = s;
            }
            s = sb.append(substring).toString();
        }
        final File file;
        if (!(file = new File(s)).exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        final String string = s + "\\" + this.method355();
        final File file2;
        if (!(file2 = new File(string)).exists() || !file2.isDirectory()) {
            file2.mkdirs();
        }
        return string + "\\";
    }
    
    public final void setCustomProperties() {
        if (this.device.equalsIgnoreCase(Emulator.deviceName)) {
            Devices.method618("SCREEN_WIDTH", this.screenWidth);
            Devices.method618("SCREEN_HEIGHT", this.screenHeight);
            Devices.method618("KEY_S1", this.lsoft);
            Devices.method618("KEY_S2", this.rsoft);
            Devices.method618("KEY_FIRE", this.fire);
            Devices.method618("KEY_UP", this.up);
            Devices.method618("KEY_DOWN", this.down);
            Devices.method618("KEY_LEFT", this.left);
            Devices.method618("KEY_RIGHT", this.right);
            Devices.method619();
        }
    }
    
    public final void updateCustomProperties() {
        this.screenWidth = Devices.method616("SCREEN_WIDTH");
        this.screenHeight = Devices.method616("SCREEN_HEIGHT");
        this.lsoft = Devices.method616("KEY_S1");
        this.rsoft = Devices.method616("KEY_S2");
        this.fire = Devices.method616("KEY_FIRE");
        this.up = Devices.method616("KEY_UP");
        this.down = Devices.method616("KEY_DOWN");
        this.left = Devices.method616("KEY_LEFT");
        this.right = Devices.method616("KEY_RIGHT");
    }
    
    public final void loadProperties() {
        try {
            final FileInputStream fileInputStream = new FileInputStream(Emulator.getAbsolutePath() + "/property.txt");
            final Properties properties;
            (properties = new Properties()).load(fileInputStream);
            final String property = properties.getProperty("Device", "SonyEricssonK800");
            this.device = property;
            Emulator.deviceName = property;
            this.defaultFont = properties.getProperty("DefaultFont", "Tahoma");
            this.rmsFolder = properties.getProperty("RMSFolder", "/rms");
            this.fontSmallSize = Integer.parseInt(properties.getProperty("FontSmallSize", String.valueOf(12)));
            this.fontMediumSIze = Integer.parseInt(properties.getProperty("FontMediumSize", String.valueOf(14)));
            this.fontLargeSize = Integer.parseInt(properties.getProperty("FontLargeSize", String.valueOf(16)));
            this.screenWidth = properties.getProperty("SCREEN_WIDTH", "240");
            this.screenHeight = properties.getProperty("SCREEN_HEIGHT", "320");
            this.lsoft = properties.getProperty("KEY_LSOFT", "-6");
            this.rsoft = properties.getProperty("KEY_RSOFT", "-7");
            this.fire = properties.getProperty("KEY_FIRE", "-5");
            this.up = properties.getProperty("KEY_UP", "-1");
            this.down = properties.getProperty("KEY_DOWN", "-2");
            this.left = properties.getProperty("KEY_LEFT", "-3");
            this.right = properties.getProperty("KEY_RIGHT", "-4");
            Settings.g3d = (properties.getProperty("3D_Graphics_Enginge", "LWJ").equalsIgnoreCase("WGL") ? 0 : 1);
            Settings.g2d = (properties.getProperty("2D_Graphics_Enginge", "AWT").equalsIgnoreCase("SWT") ? 0 : 1);
            Settings.alwaysOnTop = Boolean.valueOf(properties.getProperty("AlwaysOnTop", "false"));
            Settings.canvasScale = Integer.parseInt(properties.getProperty("CanvasScale", String.valueOf(100)));
            if (Settings.canvasScale < 100 || Settings.canvasScale % 50 != 0) {
                Settings.canvasScale = 100;
            }
            Settings.frameRate = Integer.parseInt(properties.getProperty("FrameRate", String.valueOf(30)));
            Keyboard.mapDeviceKey(0, Keyboard.method601(properties.getProperty("MAP_KEY_NUM_0")));
            Keyboard.mapDeviceKey(1, Keyboard.method601(properties.getProperty("MAP_KEY_NUM_1")));
            Keyboard.mapDeviceKey(2, Keyboard.method601(properties.getProperty("MAP_KEY_NUM_2")));
            Keyboard.mapDeviceKey(3, Keyboard.method601(properties.getProperty("MAP_KEY_NUM_3")));
            Keyboard.mapDeviceKey(4, Keyboard.method601(properties.getProperty("MAP_KEY_NUM_4")));
            Keyboard.mapDeviceKey(5, Keyboard.method601(properties.getProperty("MAP_KEY_NUM_5")));
            Keyboard.mapDeviceKey(6, Keyboard.method601(properties.getProperty("MAP_KEY_NUM_6")));
            Keyboard.mapDeviceKey(7, Keyboard.method601(properties.getProperty("MAP_KEY_NUM_7")));
            Keyboard.mapDeviceKey(8, Keyboard.method601(properties.getProperty("MAP_KEY_NUM_8")));
            Keyboard.mapDeviceKey(9, Keyboard.method601(properties.getProperty("MAP_KEY_NUM_9")));
            Keyboard.mapDeviceKey(10, Keyboard.method601(properties.getProperty("MAP_KEY_STAR")));
            Keyboard.mapDeviceKey(11, Keyboard.method601(properties.getProperty("MAP_KEY_POUND")));
            Keyboard.mapDeviceKey(12, Keyboard.method601(properties.getProperty("MAP_KEY_UP")));
            Keyboard.mapDeviceKey(13, Keyboard.method601(properties.getProperty("MAP_KEY_DOWN")));
            Keyboard.mapDeviceKey(14, Keyboard.method601(properties.getProperty("MAP_KEY_LEFT")));
            Keyboard.mapDeviceKey(15, Keyboard.method601(properties.getProperty("MAP_KEY_RIGHT")));
            Keyboard.mapDeviceKey(16, Keyboard.method601(properties.getProperty("MAP_KEY_MIDDLE")));
            Keyboard.mapDeviceKey(17, Keyboard.method601(properties.getProperty("MAP_KEY_LSOFT")));
            Keyboard.mapDeviceKey(18, Keyboard.method601(properties.getProperty("MAP_KEY_RSOFT")));
            Settings.enableKeyCache = Boolean.valueOf(properties.getProperty("EnableKeyCache", "false"));
            Settings.enableVibration = Boolean.valueOf(properties.getProperty("EnableVibration", "false"));
            Settings.enableKeyRepeat = Boolean.valueOf(properties.getProperty("EnableKeyRepeat", "false"));
            Settings.ignoreFullScreen = Boolean.valueOf(properties.getProperty("IgnoreFullScreenMode", "false"));
            Settings.networkNotAvailable = Boolean.valueOf(properties.getProperty("NetworkNotAvailable", "false"));
            Settings.associateWithJar = Boolean.valueOf(properties.getProperty("AssociateWithJar", "false"));
            Settings.rightClickMenu = Boolean.valueOf(properties.getProperty("RightClickMenu", "false"));
            Settings.xrayOverlapScreen = Boolean.valueOf(properties.getProperty("XRayOverLapScreen", "false"));
            Settings.xrayShowClipBorder = Boolean.valueOf(properties.getProperty("XRayShowClipBorder", "false"));
            Settings.infoColorHex = Boolean.valueOf(properties.getProperty("InfoColorHex", "false"));
            Settings.recordReleasedImg = Boolean.valueOf(properties.getProperty("RecordReleasedImg", "false"));
            Settings.autoGenJad = Boolean.valueOf(properties.getProperty("AutoGenJad", "false"));
            Settings.enableNewTrack = Boolean.valueOf(properties.getProperty("EnableNewTrack", "false"));
            Settings.enableMethodTrack = Boolean.valueOf(properties.getProperty("EnableMethodTrack", "false"));
            //Emulator.inactivityTimer = Integer.valueOf(properties.getProperty("InactivityTimer", "0"));
            Settings.threadMethodTrack = Boolean.valueOf(properties.getProperty("ShowMethodTrack", "false"));
            EmulatorScreen.locX = Integer.parseInt(properties.getProperty("LocationX", "-1"));
            EmulatorScreen.locY = Integer.parseInt(properties.getProperty("LocationY", "-1"));
            Settings.fileEncoding = properties.getProperty("FileEncoding", "ISO-8859-1");
            Settings.recordKeys = Boolean.valueOf(properties.getProperty("RecordKeys", "false"));
            for (int i = 0; i < 5; ++i) {
                Settings.aArray[i] = properties.getProperty("MRUList" + i, "");
            }
            Settings.proxyType = Integer.parseInt(properties.getProperty("ProxyType", "0"));
            if (Settings.proxyType < 0 || Settings.proxyType > 2) {
                Settings.proxyType = 0;
            }
            Settings.proxyHost = properties.getProperty("ProxyHost", "");
            Settings.proxyPort = properties.getProperty("ProxyPort", "");
            Settings.proxyUser = properties.getProperty("ProxyUsername", "");
            Settings.proxyPass = properties.getProperty("ProxyPassword", "");
            Settings.proxyDomain = properties.getProperty("ProxyDomain", "");
            Settings.showLogFrame = Boolean.valueOf(properties.getProperty("ShowLogFrame", "false"));
            Settings.showInfoFrame = Boolean.valueOf(properties.getProperty("ShowInfoFrame", "false"));
            Settings.showMemViewFrame = Boolean.valueOf(properties.getProperty("ShowMemViewFrame", "false"));
            Emulator.rpcEnabled = Boolean.valueOf(properties.getProperty("DiscordRichPresence", "true"));
            Settings.awtAntiAliasing = Boolean.valueOf(properties.getProperty("AWTAntiAliasing", "false"));
            Settings.canvasKeyboard = Boolean.valueOf(properties.getProperty("CanvasKeyboardMode", "true"));
            if(Emulator.getEmulator() != null && Emulator.getEmulator().getScreen() != null) {
	            ((EmulatorScreen)Emulator.getEmulator().getScreen()).toggleMenuAccelerators(!Settings.canvasKeyboard);
	            ((EmulatorScreen)Emulator.getEmulator().getScreen()).setFpsMode(Settings.fpsMode);
            }
            Settings.vlcDir = properties.getProperty("VlcDir", "");
            fileInputStream.close();
        }
        catch (Exception ex) {
        	System.out.println("properties load failed");
        	ex.printStackTrace();
            this.device = "SonyEricssonK800";
            this.defaultFont = "Tahoma";
            this.rmsFolder = "/rms";
            this.screenWidth = "240";
            this.screenHeight = "320";
            this.lsoft = "-6";
            this.rsoft = "-7";
            this.fire = "-5";
            this.up = "-1";
            this.down = "-2";
            this.left = "-3";
            this.right = "-4";
            EmulatorScreen.locX = -1;
            EmulatorScreen.locY = -1;
            Settings.fileEncoding = "ISO-8859-1";
            Settings.autoGenJad = false;
            Settings.enableNewTrack = false;
            Settings.enableMethodTrack = false;
            this.fontSmallSize = 12;
            this.fontMediumSIze = 14;
            this.fontLargeSize = 16;
            for (int j = 0; j < 5; ++j) {
                Settings.aArray[j] = "";
            }
            Settings.proxyType = 0;
            Settings.proxyHost = "";
            Settings.proxyPort = "";
            Settings.proxyUser = "";
            Settings.proxyPass = "";
            Settings.proxyDomain = "";
            Settings.showLogFrame = false;
            Settings.showInfoFrame = false;
            Settings.showMemViewFrame = false;
        }
    }
    
    public final void saveProperties() {
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(Emulator.getAbsolutePath() + "/property.txt");
            final SortProperties sortProperties;
            (sortProperties = new SortProperties(this)).setProperty("Device", this.device);
            sortProperties.setProperty("DefaultFont", this.defaultFont);
            sortProperties.setProperty("RMSFolder", this.rmsFolder);
            sortProperties.setProperty("FontSmallSize", String.valueOf(this.fontSmallSize));
            sortProperties.setProperty("FontMediumSize", String.valueOf(this.fontMediumSIze));
            sortProperties.setProperty("FontLargeSize", String.valueOf(this.fontLargeSize));
            sortProperties.setProperty("SCREEN_WIDTH", this.screenWidth);
            sortProperties.setProperty("SCREEN_HEIGHT", this.screenHeight);
            sortProperties.setProperty("KEY_LSOFT", this.lsoft);
            sortProperties.setProperty("KEY_RSOFT", this.rsoft);
            sortProperties.setProperty("KEY_FIRE", this.fire);
            sortProperties.setProperty("KEY_UP", this.up);
            sortProperties.setProperty("KEY_DOWN", this.down);
            sortProperties.setProperty("KEY_LEFT", this.left);
            sortProperties.setProperty("KEY_RIGHT", this.right);
            sortProperties.setProperty("2D_Graphics_Enginge", (Settings.g2d == 0) ? "SWT" : "AWT");
            sortProperties.setProperty("AlwaysOnTop", String.valueOf(Settings.alwaysOnTop));
            sortProperties.setProperty("CanvasScale", String.valueOf(Settings.canvasScale));
            sortProperties.setProperty("FrameRate", String.valueOf(Settings.frameRate));
            sortProperties.setProperty("3D_Graphics_Enginge", (Settings.g3d == 0) ? "WGL" : "LWJ");
            sortProperties.setProperty("MAP_KEY_NUM_0", Keyboard.get(0));
            sortProperties.setProperty("MAP_KEY_NUM_1", Keyboard.get(1));
            sortProperties.setProperty("MAP_KEY_NUM_2", Keyboard.get(2));
            sortProperties.setProperty("MAP_KEY_NUM_3", Keyboard.get(3));
            sortProperties.setProperty("MAP_KEY_NUM_4", Keyboard.get(4));
            sortProperties.setProperty("MAP_KEY_NUM_5", Keyboard.get(5));
            sortProperties.setProperty("MAP_KEY_NUM_6", Keyboard.get(6));
            sortProperties.setProperty("MAP_KEY_NUM_7", Keyboard.get(7));
            sortProperties.setProperty("MAP_KEY_NUM_8", Keyboard.get(8));
            sortProperties.setProperty("MAP_KEY_NUM_9", Keyboard.get(9));
            sortProperties.setProperty("MAP_KEY_STAR", Keyboard.get(10));
            sortProperties.setProperty("MAP_KEY_POUND", Keyboard.get(11));
            sortProperties.setProperty("MAP_KEY_UP", Keyboard.get(12));
            sortProperties.setProperty("MAP_KEY_DOWN", Keyboard.get(13));
            sortProperties.setProperty("MAP_KEY_LEFT", Keyboard.get(14));
            sortProperties.setProperty("MAP_KEY_RIGHT", Keyboard.get(15));
            sortProperties.setProperty("MAP_KEY_MIDDLE", Keyboard.get(16));
            sortProperties.setProperty("MAP_KEY_LSOFT", Keyboard.get(17));
            sortProperties.setProperty("MAP_KEY_RSOFT", Keyboard.get(18));
            sortProperties.setProperty("EnableKeyCache", String.valueOf(Settings.enableKeyCache));
            sortProperties.setProperty("EnableVibration", String.valueOf(Settings.enableVibration));
            //sortProperties.setProperty("InactivityTimer", String.valueOf(Emulator.inactivityTimer));
            sortProperties.setProperty("EnableKeyRepeat", String.valueOf(Settings.enableKeyRepeat));
            sortProperties.setProperty("IgnoreFullScreenMode", String.valueOf(Settings.ignoreFullScreen));
            sortProperties.setProperty("NetworkNotAvailable", String.valueOf(Settings.networkNotAvailable));
            sortProperties.setProperty("AssociateWithJar", String.valueOf(Settings.associateWithJar));
            sortProperties.setProperty("RightClickMenu", String.valueOf(Settings.rightClickMenu));
            sortProperties.setProperty("XRayOverLapScreen", String.valueOf(Settings.xrayOverlapScreen));
            sortProperties.setProperty("XRayShowClipBorder", String.valueOf(Settings.xrayShowClipBorder));
            sortProperties.setProperty("InfoColorHex", String.valueOf(Settings.infoColorHex));
            sortProperties.setProperty("RecordReleasedImg", String.valueOf(Settings.recordReleasedImg));
            sortProperties.setProperty("AutoGenJad", String.valueOf(Settings.autoGenJad));
            sortProperties.setProperty("EnableNewTrack", String.valueOf(Settings.enableNewTrack));
            sortProperties.setProperty("EnableMethodTrack", String.valueOf(Settings.enableMethodTrack));
            sortProperties.setProperty("ShowMethodTrack", String.valueOf(Settings.threadMethodTrack));
            sortProperties.setProperty("LocationX", String.valueOf(EmulatorScreen.locX));
            sortProperties.setProperty("LocationY", String.valueOf(EmulatorScreen.locY));
            sortProperties.setProperty("FileEncoding", Settings.fileEncoding);
            sortProperties.setProperty("RecordKeys", String.valueOf(Settings.recordKeys));
            for (int i = 0; i < 5; ++i) {
                sortProperties.setProperty("MRUList" + i, Settings.aArray[i]);
            }
            sortProperties.setProperty("ProxyType", String.valueOf(Settings.proxyType));
            sortProperties.setProperty("ProxyHost", Settings.proxyHost);
            sortProperties.setProperty("ProxyPort", Settings.proxyPort);
            sortProperties.setProperty("ProxyUsername", Settings.proxyUser);
            sortProperties.setProperty("ProxyPassword", Settings.proxyPass);
            sortProperties.setProperty("ProxyDomain", Settings.proxyDomain);
            sortProperties.setProperty("ShowLogFrame", String.valueOf(Settings.showLogFrame));
            sortProperties.setProperty("ShowInfoFrame", String.valueOf(Settings.showInfoFrame));
            sortProperties.setProperty("ShowMemViewFrame", String.valueOf(Settings.showMemViewFrame));
            sortProperties.setProperty("DiscordRichPresence", String.valueOf(Emulator.rpcEnabled));
            sortProperties.setProperty("AWTAntiAliasing", String.valueOf(Settings.awtAntiAliasing));
            sortProperties.setProperty("CanvasKeyboardMode", String.valueOf(Settings.canvasKeyboard));
            sortProperties.setProperty("VlcDir", Settings.vlcDir);
            sortProperties.store(fileOutputStream, "KEmulator properties");
            fileOutputStream.close();
        }
        catch (Exception ex) {}
    }
    
    private void method358() {
        final String trim = this.aCombo657.getText().trim();
        this.device = trim;
        Emulator.deviceName = trim;
        Devices.curPlatform = Devices.getPlatform(Emulator.deviceName);
        this.defaultFont = this.aCombo689.getText().trim();
        this.rmsFolder = this.aText662.getText().trim();
        Settings.fileEncoding = this.aCombo675.getText().trim();
        this.fontSmallSize = this.aSpinner690.getSelection();
        this.fontMediumSIze = this.aSpinner679.getSelection();
        this.fontLargeSize = this.aSpinner670.getSelection();
        this.screenWidth = this.aText672.getText().trim();
        this.screenHeight = this.aText684.getText().trim();
        this.lsoft = this.aText695.getText().trim();
        this.rsoft = this.aText702.getText().trim();
        this.fire = this.aText708.getText().trim();
        this.up = this.aText713.getText().trim();
        this.down = this.aText718.getText().trim();
        this.left = this.aText723.getText().trim();
        this.right = this.aText727.getText().trim();
        Keyboard.mapDeviceKey(17, Keyboard.method601(Property.aStringArray661[17]));
        Keyboard.mapDeviceKey(18, Keyboard.method601(Property.aStringArray661[18]));
        Keyboard.mapDeviceKey(14, Keyboard.method601(Property.aStringArray661[14]));
        Keyboard.mapDeviceKey(15, Keyboard.method601(Property.aStringArray661[15]));
        Keyboard.mapDeviceKey(12, Keyboard.method601(Property.aStringArray661[12]));
        Keyboard.mapDeviceKey(13, Keyboard.method601(Property.aStringArray661[13]));
        Keyboard.mapDeviceKey(16, Keyboard.method601(Property.aStringArray661[16]));
        Keyboard.mapDeviceKey(10, Keyboard.method601(Property.aStringArray661[10]));
        Keyboard.mapDeviceKey(11, Keyboard.method601(Property.aStringArray661[11]));
        Keyboard.mapDeviceKey(1, Keyboard.method601(Property.aStringArray661[1]));
        Keyboard.mapDeviceKey(2, Keyboard.method601(Property.aStringArray661[2]));
        Keyboard.mapDeviceKey(3, Keyboard.method601(Property.aStringArray661[3]));
        Keyboard.mapDeviceKey(4, Keyboard.method601(Property.aStringArray661[4]));
        Keyboard.mapDeviceKey(5, Keyboard.method601(Property.aStringArray661[5]));
        Keyboard.mapDeviceKey(6, Keyboard.method601(Property.aStringArray661[6]));
        Keyboard.mapDeviceKey(7, Keyboard.method601(Property.aStringArray661[7]));
        Keyboard.mapDeviceKey(8, Keyboard.method601(Property.aStringArray661[8]));
        Keyboard.mapDeviceKey(9, Keyboard.method601(Property.aStringArray661[9]));
        Keyboard.mapDeviceKey(0, Keyboard.method601(Property.aStringArray661[0]));
        Devices.method618("KEY_S1", this.lsoft);
        Devices.method618("KEY_S2", this.rsoft);
        Devices.method618("KEY_FIRE", this.fire);
        Devices.method618("KEY_UP", this.up);
        Devices.method618("KEY_DOWN", this.down);
        Devices.method618("KEY_LEFT", this.left);
        Devices.method618("KEY_RIGHT", this.right);
        Devices.method619();
        Keyboard.init();
        if (Settings.enableKeyCache != this.aButton696.getSelection()) {
            Keyboard.keyCacheStack.clear();
            Settings.enableKeyCache = this.aButton696.getSelection();
        }
        if (Settings.enableVibration != this.aButton724.getSelection() && !(Settings.enableVibration = this.aButton724.getSelection())) {
            Emulator.getEmulator().getScreen().stopVibra();
        }
        if (Settings.associateWithJar != this.aButton740.getSelection()) {
            method371(Settings.associateWithJar = this.aButton740.getSelection());
        }
        if (Settings.rightClickMenu != this.aButton743.getSelection()) {
            method359(Settings.rightClickMenu = this.aButton743.getSelection());
        }
        Settings.enableKeyRepeat = this.aButton728.getSelection();
        Settings.ignoreFullScreen = this.aButton732.getSelection();
        Settings.networkNotAvailable = this.aButton736.getSelection();
        Settings.xrayOverlapScreen = this.aButton746.getSelection();
        Settings.xrayShowClipBorder = this.aButton749.getSelection();
        Settings.infoColorHex = this.aButton752.getSelection();
        Settings.recordReleasedImg = this.aButton703.getSelection();
        Settings.autoGenJad = this.aButton709.getSelection();
        Settings.enableNewTrack = this.aButton714.getSelection();
        Settings.enableMethodTrack = this.aButton719.getSelection();
        Settings.proxyType = this.aCombo706.getSelectionIndex();
        Settings.proxyHost = this.aText635.getText().trim();
        Settings.proxyPort = this.aText637.getText().trim();
        Settings.proxyUser = this.aText639.getText().trim();
        Settings.proxyPass = this.aText641.getText();
        Settings.proxyDomain = this.aText643.getText().trim();
        //Emulator.inactivityTimer = this.inactiveTimerSpinner.getSelection();
        Emulator.rpcEnabled = this.rpcBtn.getSelection();
        Settings.awtAntiAliasing = this.antiAliasBtn.getSelection();
        Settings.vlcDir = vlcDirText.getText().trim();
        this.method353();
    }
    
    private static void method359(final boolean b) {
        try {
            if (!b) {
                Emulator.unregRightMenu();
                return;
            }
            final String absoluteFile = Emulator.getAbsoluteFile();
            Emulator.regRightMenu("\"" + (absoluteFile.substring(0, absoluteFile.length() - 3) + "exe").replace('/', '\\') + "\" \"%1\"");
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            Emulator.getEmulator().getLogStream().println("+++ emulator.dll not loaded +++");
        }
        catch (Exception ex) {}
    }
    
    private static void method371(final boolean b) {
        try {
            if (!b) {
                Emulator.unregAssociateJar("KEmulator");
                return;
            }
            final String absoluteFile = Emulator.getAbsoluteFile();
            final String replace = (absoluteFile.substring(0, absoluteFile.length() - 3) + "exe").replace('/', '\\');
            Emulator.regAssociateJar("KEmulator", replace + ",0", "\"" + replace + "\" \"%1\"");
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            Emulator.getEmulator().getLogStream().println("+++ emulator.dll not loaded +++");
        }
        catch (Exception ex) {}
    }
    
    private void method372(final Shell shell) {
        ((Decorations)(this.aShell655 = new Shell(shell, 67680))).setText(UILocale.uiText("OPTION_FRAME_TITLE", "Options & Properties"));
        ((Decorations)this.aShell655).setImage(new Image((Device)Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 2;
        ((Composite)this.aShell655).setLayout((Layout)layout);
        this.method393();
        this.method390();
    }
    
    private void method373() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = false;
        layoutData.verticalSpan = 1;
        layoutData.verticalAlignment = 2;
        ((Control)(this.aCombo657 = new Combo(this.customComp, 12))).setLayoutData((Object)layoutData);
        final Enumeration method620 = Devices.method620();
        while (method620.hasMoreElements()) {
            final String text = (String) method620.nextElement();
            this.aCombo657.add(text);
            if (this.device.equalsIgnoreCase(text)) {
                this.aCombo657.setText(text);
            }
        }
        this.aCombo657.addModifyListener((ModifyListener)new Class117(this));
    }
    
    private void method379() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = 2;
        ((Control)(this.aCombo675 = new Combo(this.customComp, 8))).setLayoutData((Object)layoutData);
        final SortedMap<String, Charset> availableCharsets = Charset.availableCharsets();
        final ArrayList<Comparable> list = new ArrayList<Comparable>();
        final Iterator iterator = availableCharsets.keySet().iterator();
        while (iterator.hasNext()) {
            list.add((Comparable) iterator.next());
        }
        Collections.sort((List<Comparable>)list);
        String s = (String)list.get(0);
        for (int i = 0; i < list.size(); ++i) {
            this.aCombo675.add((String)list.get(i));
            if (Settings.fileEncoding.equalsIgnoreCase((String)list.get(i))) {
                s = (String)list.get(i);
            }
        }
        Settings.fileEncoding = s;
        this.aCombo675.setText(s);
    }
    
    private void method384() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = 2;
        final GridData layoutData2;
        (layoutData2 = new GridData()).horizontalAlignment = 4;
        layoutData2.grabExcessHorizontalSpace = true;
        layoutData2.verticalAlignment = 2;
        final GridData layoutData3;
        (layoutData3 = new GridData()).horizontalAlignment = 4;
        layoutData3.grabExcessHorizontalSpace = true;
        layoutData3.verticalAlignment = 2;
        final GridData layoutData4;
        (layoutData4 = new GridData()).horizontalAlignment = 4;
        layoutData4.grabExcessHorizontalSpace = true;
        layoutData4.verticalAlignment = 2;
        final GridData layoutData5;
        (layoutData5 = new GridData()).horizontalAlignment = 4;
        layoutData5.grabExcessHorizontalSpace = true;
        layoutData5.verticalAlignment = 2;
        final GridData layoutData6;
        (layoutData6 = new GridData()).horizontalAlignment = 4;
        layoutData6.grabExcessHorizontalSpace = true;
        layoutData6.verticalAlignment = 2;
        final GridData layoutData7;
        (layoutData7 = new GridData()).horizontalAlignment = 4;
        layoutData7.grabExcessHorizontalSpace = true;
        layoutData7.verticalAlignment = 2;
        final GridData layoutData8;
        (layoutData8 = new GridData()).horizontalAlignment = 1;
        layoutData8.verticalAlignment = 2;
        final GridData layoutData9;
        (layoutData9 = new GridData()).horizontalAlignment = 1;
        layoutData9.grabExcessHorizontalSpace = false;
        layoutData9.verticalAlignment = 2;
        final GridData layoutData10;
        (layoutData10 = new GridData()).horizontalAlignment = 4;
        layoutData10.grabExcessHorizontalSpace = true;
        layoutData10.verticalAlignment = 2;
        final GridData layoutData11;
        (layoutData11 = new GridData()).horizontalAlignment = 4;
        layoutData11.grabExcessHorizontalSpace = true;
        layoutData11.verticalAlignment = 2;
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 4;
        layout.makeColumnsEqualWidth = false;
        layout.marginHeight = 10;
        layout.horizontalSpacing = 5;
        final GridData layoutData12;
        (layoutData12 = new GridData()).horizontalAlignment = 4;
        layoutData12.horizontalSpan = 3;
        layoutData12.grabExcessHorizontalSpace = true;
        layoutData12.grabExcessVerticalSpace = false;
        layoutData12.heightHint = 140;
        layoutData12.verticalAlignment = 4;
        (this.aGroup660 = new Group(this.customComp, 0)).setText(UILocale.uiText("OPTION_CUSTOM_PROPERTIES", "Custom Properties"));
        ((Composite)this.aGroup660).setLayout((Layout)layout);
        ((Control)this.aGroup660).setLayoutData((Object)layoutData12);
        (this.aCLabel694 = new CLabel((Composite)this.aGroup660, 0)).setText(UILocale.uiText("OPTION_CUSTOM_SCREEN_WIDTH", "Screen Width:"));
        ((Control)this.aCLabel694).setLayoutData((Object)layoutData9);
        ((Control)(this.aText672 = new Text((Composite)this.aGroup660, 2048))).setLayoutData((Object)layoutData11);
        (this.aCLabel701 = new CLabel((Composite)this.aGroup660, 0)).setText(UILocale.uiText("OPTION_CUSTOM_SCREEN_HEIGHT", "Screen Height:"));
        ((Control)this.aCLabel701).setLayoutData((Object)layoutData8);
        ((Control)(this.aText684 = new Text((Composite)this.aGroup660, 2048))).setLayoutData((Object)layoutData10);
        (this.aCLabel707 = new CLabel((Composite)this.aGroup660, 0)).setText(UILocale.uiText("OPTION_CUSTOM_KEY_LSK", "Left Soft Key:"));
        ((Control)(this.aText695 = new Text((Composite)this.aGroup660, 2048))).setLayoutData((Object)layoutData7);
        (this.aCLabel712 = new CLabel((Composite)this.aGroup660, 0)).setText(UILocale.uiText("OPTION_CUSTOM_KEY_RSK", "Right Soft Key:"));
        ((Control)(this.aText702 = new Text((Composite)this.aGroup660, 2048))).setLayoutData((Object)layoutData6);
        (this.aCLabel717 = new CLabel((Composite)this.aGroup660, 0)).setText(UILocale.uiText("OPTION_CUSTOM_KEY_MIDDLE", "Fire/Middle:"));
        ((Control)(this.aText708 = new Text((Composite)this.aGroup660, 2048))).setLayoutData((Object)layoutData5);
        this.aCLabel722 = new CLabel((Composite)this.aGroup660, 0);
        this.aCLabel722 = new CLabel((Composite)this.aGroup660, 0);
        (this.aCLabel722 = new CLabel((Composite)this.aGroup660, 0)).setText(UILocale.uiText("OPTION_CUSTOM_KEY_UP", "Up:"));
        ((Control)(this.aText713 = new Text((Composite)this.aGroup660, 2048))).setLayoutData((Object)layoutData4);
        (this.aCLabel726 = new CLabel((Composite)this.aGroup660, 0)).setText(UILocale.uiText("OPTION_CUSTOM_KEY_DOWN", "Down:"));
        ((Control)(this.aText718 = new Text((Composite)this.aGroup660, 2048))).setLayoutData((Object)layoutData3);
        (this.aCLabel730 = new CLabel((Composite)this.aGroup660, 0)).setText(UILocale.uiText("OPTION_CUSTOM_KEY_LEFT", "Left:"));
        ((Control)(this.aText723 = new Text((Composite)this.aGroup660, 2048))).setLayoutData((Object)layoutData2);
        (this.aCLabel734 = new CLabel((Composite)this.aGroup660, 0)).setText(UILocale.uiText("OPTION_CUSTOM_KEY_RIGHT", "Right:"));
        ((Control)(this.aText727 = new Text((Composite)this.aGroup660, 2048))).setLayoutData((Object)layoutData);
        this.method387();
    }
    
    private void method387() {
        this.aText672.setText(this.screenWidth);
        this.aText684.setText(this.screenHeight);
        this.aText695.setText(this.lsoft);
        this.aText702.setText(this.rsoft);
        this.aText708.setText(this.fire);
        this.aText713.setText(this.up);
        this.aText718.setText(this.down);
        this.aText723.setText(this.left);
        this.aText727.setText(this.right);
    }
    
    private void method390() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = 2;
        int x = (480-320)/2;
        ((Control)(this.aComposite667 = new Composite((Composite)this.aShell655, 0))).setLayoutData((Object)layoutData);
        ((Control)(this.aButton676 = new Button(this.aComposite667, 8388616))).setBounds(new Rectangle(62+x, 1, 68, 19));
        this.aButton676.setSelection(false);
        this.aButton676.setText(UILocale.uiText("DIALOG_OK", "OK"));
        this.aButton676.addSelectionListener((SelectionListener)new Class113(this));
        ((Control)(this.aButton685 = new Button(this.aComposite667, 8388616))).setBounds(new Rectangle(197+x, 0, 66, 21));
        this.aButton685.setText(UILocale.uiText("DIALOG_CANCEL", "Cancel"));
        this.aButton685.addSelectionListener((SelectionListener)new Class111(this));
    }
    
    private void method393() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = 2;
        (this.tabFolder = new CTabFolder((Composite)this.aShell655, 8390656)).setSelectionBackground(Display.getCurrent().getSystemColor(22));
        this.tabFolder.setSimple(true);
        this.tabFolder.setMRUVisible(false);
        this.tabFolder.setUnselectedCloseVisible(false);
        this.tabFolder.setUnselectedImageVisible(false);
        ((Control)this.tabFolder).setLayoutData((Object)layoutData);
        this.setupCustomComp();
        this.setupKeyMapComp();
        this.setupSystemComp();
        this.setupCoreApiComp();
        this.setupSysFontComp();
        this.setupRecordsComp();
        this.setupNetworkComp();
        this.setupMediaComp();
        final CTabItem cTabItem;
        (cTabItem = new CTabItem(this.tabFolder, 0)).setText(UILocale.uiText("OPTION_TAB_CUSTOM", "Custom"));
        cTabItem.setControl((Control)this.customComp);
        final CTabItem cTabItem2;
        (cTabItem2 = new CTabItem(this.tabFolder, 0)).setText(UILocale.uiText("OPTION_TAB_KEYMAP", "KeyMap"));
        cTabItem2.setControl((Control)this.keyMapComp);
        final CTabItem cTabItem3;
        (cTabItem3 = new CTabItem(this.tabFolder, 0)).setText(UILocale.uiText("OPTION_TAB_SYSFONT", "SysFont"));
        cTabItem3.setControl((Control)this.sysFontComp);
        final CTabItem cTabItem4;
        (cTabItem4 = new CTabItem(this.tabFolder, 0)).setText(UILocale.uiText("OPTION_TAB_SYSTEM", "System"));
        cTabItem4.setControl((Control)this.systemComp);
        final CTabItem coreApiTab;
        (coreApiTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.uiText("OPTION_TAB_COREAPI", "CoreAPI"));
        coreApiTab.setControl((Control)this.coreApiComp);
        final CTabItem cTabItem6;
        (cTabItem6 = new CTabItem(this.tabFolder, 0)).setText(UILocale.uiText("OPTION_TAB_RECORDS", "Records"));
        cTabItem6.setControl((Control)this.recordsComp);
        final CTabItem cTabItem7;
        (cTabItem7 = new CTabItem(this.tabFolder, 0)).setText(UILocale.uiText("OPTION_TAB_NETWORK", "Network"));
        cTabItem7.setControl((Control)this.networkComp);
        final CTabItem cTabItem8;
        (cTabItem8 = new CTabItem(this.tabFolder, 0)).setText(UILocale.uiText("OPTION_TAB_MEDIA", "Media"));
        cTabItem8.setControl((Control)this.mediaComp);
    }
    
    private void setupCustomComp() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.verticalAlignment = 4;
        this.customComp = new Composite((Composite)this.tabFolder, 0);
        final GridData layoutData2;
        (layoutData2 = new GridData()).horizontalAlignment = 4;
        layoutData2.horizontalSpan = 2;
        layoutData2.grabExcessHorizontalSpace = true;
        layoutData2.verticalAlignment = 2;
        final GridData layoutData3;
        (layoutData3 = new GridData()).horizontalAlignment = 4;
        layoutData3.verticalAlignment = 2;
        final GridData layoutData4;
        (layoutData4 = new GridData()).horizontalAlignment = 4;
        layoutData4.verticalAlignment = 2;
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 3;
        layout.marginWidth = 5;
        this.customComp.setLayout((Layout)layout);
        (this.aCLabel658 = new CLabel(this.customComp, 0)).setText(UILocale.uiText("OPTION_CUSTOM_DEVICE", "Device Select:"));
        ((Control)this.aCLabel658).setLayoutData((Object)layoutData3);
        this.method373();
        (this.aCLabel673 = new CLabel(this.customComp, 0)).setText(UILocale.uiText("OPTION_CUSTOM_ENCODING", "Default Encoding:"));
        ((Control)this.aCLabel673).setLayoutData((Object)layoutData4);
        this.method379();
        this.method384();
        (this.aCLabel738 = new CLabel(this.customComp, 0)).setText(UILocale.uiText("OPTION_CUSTOM_MAX_FPS", "Max FPS:") + " " + ((Settings.frameRate > 50) ? "\u221e" : String.valueOf(Settings.frameRate)));
        ((Control)this.aCLabel738).setLayoutData((Object)layoutData);
        (this.aScale669 = new Scale(this.customComp, 256)).setIncrement(1);
        this.aScale669.setMaximum(51);
        this.aScale669.setPageIncrement(5);
        this.aScale669.setSelection(Settings.frameRate);
        this.aScale669.setMinimum(1);
        ((Control)this.aScale669).setLayoutData((Object)layoutData2);
        this.aScale669.addSelectionListener((SelectionListener)new Class109(this));
    }
    
    private void setupKeyMapComp() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.verticalAlignment = 2;
        final GridData layoutData2;
        (layoutData2 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData2.verticalAlignment = 2;
        layoutData2.horizontalAlignment = 4;
        final GridData layoutData3;
        (layoutData3 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData3.verticalAlignment = 2;
        layoutData3.horizontalAlignment = 4;
        final GridData layoutData4;
        (layoutData4 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData4.verticalAlignment = 2;
        layoutData4.horizontalAlignment = 4;
        final GridData layoutData5;
        (layoutData5 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData5.verticalAlignment = 2;
        layoutData5.horizontalAlignment = 4;
        final GridData layoutData6;
        (layoutData6 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData6.verticalAlignment = 2;
        layoutData6.horizontalAlignment = 4;
        final GridData layoutData7;
        (layoutData7 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData7.verticalAlignment = 2;
        layoutData7.horizontalAlignment = 4;
        final GridData layoutData8;
        (layoutData8 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData8.verticalAlignment = 2;
        layoutData8.horizontalAlignment = 4;
        final GridData layoutData9;
        (layoutData9 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData9.verticalAlignment = 2;
        layoutData9.horizontalAlignment = 4;
        final GridData layoutData10;
        (layoutData10 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData10.verticalAlignment = 2;
        layoutData10.horizontalAlignment = 4;
        final GridData layoutData11;
        (layoutData11 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData11.verticalAlignment = 2;
        layoutData11.horizontalAlignment = 4;
        final GridData layoutData12;
        (layoutData12 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData12.verticalAlignment = 2;
        layoutData12.horizontalAlignment = 4;
        final GridData layoutData13;
        (layoutData13 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData13.verticalAlignment = 2;
        layoutData13.horizontalAlignment = 4;
        final GridData layoutData14;
        (layoutData14 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData14.verticalAlignment = 2;
        layoutData14.horizontalAlignment = 4;
        final GridData layoutData15;
        (layoutData15 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData15.verticalAlignment = 2;
        layoutData15.horizontalAlignment = 4;
        final GridData layoutData16;
        (layoutData16 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData16.verticalAlignment = 2;
        layoutData16.horizontalAlignment = 4;
        final GridData layoutData17;
        (layoutData17 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData17.verticalAlignment = 2;
        layoutData17.horizontalAlignment = 4;
        final GridData layoutData18;
        (layoutData18 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData18.verticalAlignment = 2;
        layoutData18.horizontalAlignment = 4;
        final GridData layoutData19;
        (layoutData19 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData19.verticalAlignment = 2;
        layoutData19.horizontalAlignment = 4;
        final GridData layoutData20;
        (layoutData20 = new GridData()).grabExcessHorizontalSpace = true;
        layoutData20.verticalAlignment = 2;
        layoutData20.horizontalAlignment = 4;
        final GridData layoutData21;
        (layoutData21 = new GridData()).horizontalAlignment = 4;
        layoutData21.verticalAlignment = 2;
        final GridData layoutData22;
        (layoutData22 = new GridData()).horizontalAlignment = 4;
        layoutData22.verticalAlignment = 2;
        final GridData layoutData23;
        (layoutData23 = new GridData()).horizontalAlignment = 4;
        layoutData23.verticalAlignment = 2;
        final GridData layoutData24;
        (layoutData24 = new GridData()).horizontalAlignment = 4;
        layoutData24.verticalAlignment = 2;
        final GridData layoutData25;
        (layoutData25 = new GridData()).horizontalAlignment = 4;
        layoutData25.verticalAlignment = 2;
        final GridData layoutData26;
        (layoutData26 = new GridData()).horizontalAlignment = 4;
        layoutData26.verticalAlignment = 2;
        final GridData layoutData27;
        (layoutData27 = new GridData()).horizontalAlignment = 4;
        layoutData27.verticalAlignment = 2;
        final GridData layoutData28;
        (layoutData28 = new GridData()).horizontalSpan = 2;
        layoutData28.verticalAlignment = 2;
        layoutData28.horizontalAlignment = 2;
        final GridData layoutData29;
        (layoutData29 = new GridData()).horizontalAlignment = 4;
        layoutData29.verticalAlignment = 2;
        final GridData layoutData30;
        (layoutData30 = new GridData()).horizontalAlignment = 4;
        layoutData30.verticalAlignment = 2;
        final GridData layoutData31;
        (layoutData31 = new GridData()).horizontalAlignment = 4;
        layoutData31.verticalAlignment = 2;
        final GridData layoutData32;
        (layoutData32 = new GridData()).horizontalAlignment = 4;
        layoutData32.verticalAlignment = 2;
        final GridData layoutData33;
        (layoutData33 = new GridData()).horizontalAlignment = 4;
        layoutData33.verticalAlignment = 2;
        final GridData layoutData34;
        (layoutData34 = new GridData()).horizontalAlignment = 4;
        layoutData34.verticalAlignment = 2;
        final GridData layoutData35;
        (layoutData35 = new GridData()).horizontalAlignment = 4;
        layoutData35.verticalAlignment = 2;
        final GridData layoutData36;
        (layoutData36 = new GridData()).horizontalAlignment = 4;
        layoutData36.verticalAlignment = 2;
        final GridData layoutData37;
        (layoutData37 = new GridData()).horizontalAlignment = 4;
        layoutData37.verticalAlignment = 2;
        final GridData layoutData38;
        (layoutData38 = new GridData()).horizontalAlignment = 4;
        layoutData38.verticalAlignment = 2;
        final GridData layoutData39;
        (layoutData39 = new GridData()).horizontalAlignment = 4;
        layoutData39.verticalAlignment = 2;
        final GridData layoutData40;
        (layoutData40 = new GridData()).horizontalAlignment = 4;
        layoutData40.grabExcessHorizontalSpace = false;
        layoutData40.verticalAlignment = 2;
        this.keyMapComp = new Composite((Composite)this.tabFolder, 0);
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 4;
        this.keyMapComp.setLayout((Layout)layout);
        (this.aCLabel646 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_CONTROLLER", "Controller:"));
        ((Control)this.aCLabel646).setLayoutData((Object)layoutData);
        this.method400();
        (this.aCLabel741 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_LSK", "LeftSoftKey:"));
        ((Control)this.aCLabel741).setLayoutData((Object)layoutData38);
        (this.aText731 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText731).setLayoutData((Object)layoutData20);
        this.aText731.setText(Keyboard.get(17));
        ((Control)this.aText731).addKeyListener((KeyListener)new Class107(this));
        (this.aCLabel744 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_RSK", "RightSoftKey:"));
        ((Control)this.aCLabel744).setLayoutData((Object)layoutData37);
        (this.aText735 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText735).setLayoutData((Object)layoutData2);
        this.aText735.setText(Keyboard.get(18));
        ((Control)this.aText735).addKeyListener((KeyListener)new Class137(this));
        (this.aCLabel762 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_NUM_1", "Num_1:"));
        ((Control)this.aCLabel762).setLayoutData((Object)layoutData40);
        (this.aText754 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText754).setLayoutData((Object)layoutData16);
        this.aText754.setText(Keyboard.get(1));
        ((Control)this.aText754).addKeyListener((KeyListener)new Class135(this));
        (this.aCLabel765 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_NUM_2", "Num_2:"));
        ((Control)this.aCLabel765).setLayoutData((Object)layoutData32);
        (this.aText757 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText757).setLayoutData((Object)layoutData5);
        this.aText757.setText(Keyboard.get(2));
        ((Control)this.aText757).addKeyListener((KeyListener)new Class133(this));
        (this.aCLabel767 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_NUM_3", "Num_3:"));
        ((Control)this.aCLabel767).setLayoutData((Object)layoutData31);
        (this.aText760 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText760).setLayoutData((Object)layoutData15);
        this.aText760.setText(Keyboard.get(3));
        ((Control)this.aText760).addKeyListener((KeyListener)new Class131(this));
        (this.aCLabel769 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_NUM_4", "Num_4:"));
        ((Control)this.aCLabel769).setLayoutData((Object)layoutData27);
        (this.aText763 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText763).setLayoutData((Object)layoutData6);
        this.aText763.setText(Keyboard.get(4));
        ((Control)this.aText763).addKeyListener((KeyListener)new Class177(this));
        (this.aCLabel771 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_NUM_5", "Num_5:"));
        ((Control)this.aCLabel771).setLayoutData((Object)layoutData25);
        (this.aText766 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText766).setLayoutData((Object)layoutData14);
        this.aText766.setText(Keyboard.get(5));
        ((Control)this.aText766).addKeyListener((KeyListener)new Class168(this));
        (this.aCLabel773 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_NUM_6", "Num_6:"));
        ((Control)this.aCLabel773).setLayoutData((Object)layoutData26);
        (this.aText768 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText768).setLayoutData((Object)layoutData7);
        this.aText768.setText(Keyboard.get(6));
        ((Control)this.aText768).addKeyListener((KeyListener)new Class173(this));
        (this.aCLabel626 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_NUM_7", "Num_7:"));
        ((Control)this.aCLabel626).setLayoutData((Object)layoutData24);
        (this.aText770 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText770).setLayoutData((Object)layoutData13);
        this.aText770.setText(Keyboard.get(7));
        ((Control)this.aText770).addKeyListener((KeyListener)new Class172(this));
        (this.aCLabel628 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_NUM_8", "Num_8:"));
        ((Control)this.aCLabel628).setLayoutData((Object)layoutData23);
        (this.aText772 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText772).setLayoutData((Object)layoutData8);
        this.aText772.setText(Keyboard.get(8));
        ((Control)this.aText772).addKeyListener((KeyListener)new Class171(this));
        (this.aCLabel630 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_NUM_9", "Num_9:"));
        ((Control)this.aCLabel630).setLayoutData((Object)layoutData21);
        (this.aText774 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText774).setLayoutData((Object)layoutData12);
        this.aText774.setText(Keyboard.get(9));
        ((Control)this.aText774).addKeyListener((KeyListener)new Class174(this));
        (this.aCLabel632 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_NUM_0", "Num_0:"));
        ((Control)this.aCLabel632).setLayoutData((Object)layoutData22);
        (this.aText627 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText627).setLayoutData((Object)layoutData9);
        this.aText627.setText(Keyboard.get(0));
        ((Control)this.aText627).addKeyListener((KeyListener)new Class175(this));
        (this.aCLabel634 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_KEY_*", "Key *:"));
        ((Control)this.aCLabel634).setLayoutData((Object)layoutData30);
        (this.aText629 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText629).setLayoutData((Object)layoutData11);
        this.aText629.setText(Keyboard.get(10));
        ((Control)this.aText629).addKeyListener((KeyListener)new Class180(this));
        (this.aCLabel636 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_KEY_#", "Key #:"));
        ((Control)this.aCLabel636).setLayoutData((Object)layoutData29);
        (this.aText631 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText631).setLayoutData((Object)layoutData10);
        this.aText631.setText(Keyboard.get(11));
        ((Control)this.aText631).addKeyListener((KeyListener)new Class181(this));
        (this.aCLabel747 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_PAD_LEFT", "Pad_Left:"));
        ((Control)this.aCLabel747).setLayoutData((Object)layoutData35);
        (this.aText739 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText739).setLayoutData((Object)layoutData19);
        this.aText739.setText(Keyboard.get(14));
        ((Control)this.aText739).addKeyListener((KeyListener)new Class176(this));
        (this.aCLabel750 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_PAD_RIGHT", "Pad_Right:"));
        ((Control)this.aCLabel750).setLayoutData((Object)layoutData36);
        (this.aText742 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText742).setLayoutData((Object)layoutData3);
        this.aText742.setText(Keyboard.get(15));
        ((Control)this.aText742).addKeyListener((KeyListener)new Class179(this));
        (this.aCLabel753 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_PAD_UP", "Pad_Up:"));
        ((Control)this.aCLabel753).setLayoutData((Object)layoutData34);
        (this.aText745 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText745).setLayoutData((Object)layoutData18);
        this.aText745.setText(Keyboard.get(12));
        ((Control)this.aText745).addKeyListener((KeyListener)new Class178(this));
        (this.aCLabel756 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_PAD_DOWN", "Pad_Down:"));
        ((Control)this.aCLabel756).setLayoutData((Object)layoutData33);
        (this.aText748 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText748).setLayoutData((Object)layoutData4);
        this.aText748.setText(Keyboard.get(13));
        ((Control)this.aText748).addKeyListener((KeyListener)new Class183(this));
        (this.aCLabel759 = new CLabel(this.keyMapComp, 0)).setText(UILocale.uiText("OPTION_KEYMAP_PAD_MIDDLE", "Pad_Middle:"));
        ((Control)this.aCLabel759).setLayoutData((Object)layoutData39);
        (this.aText751 = new Text(this.keyMapComp, 2048)).setEditable(false);
        ((Control)this.aText751).setLayoutData((Object)layoutData17);
        this.aText751.setText(Keyboard.get(16));
        ((Control)this.aText751).addKeyListener((KeyListener)new Class182(this));
        (this.aButton696 = new Button(this.keyMapComp, 32)).setText(UILocale.uiText("OPTION_KEYMAP_KEY_CACHE", "Enable Key Cache"));
        ((Control)this.aButton696).setLayoutData((Object)layoutData28);
        this.aButton696.setSelection(Settings.enableKeyCache);
        this.method404();
    }
    
    private void method400() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalSpan = 2;
        layoutData.verticalAlignment = 2;
        layoutData.grabExcessHorizontalSpace = false;
        layoutData.horizontalAlignment = 4;
        ((Control)(this.aCombo699 = new Combo(this.keyMapComp, 8))).setLayoutData((Object)layoutData);
        this.aCombo699.addModifyListener((ModifyListener)new Class185(this));
        final GridData layoutData2;
        (layoutData2 = new GridData()).horizontalAlignment = 2;
        layoutData2.verticalAlignment = 2;
        (this.aButton755 = new Button(this.keyMapComp, 8388616)).setText(UILocale.uiText("OPTION_KEYMAP_REFRESH", "Refresh"));
        ((Control)this.aButton755).setLayoutData((Object)layoutData2);
        this.aButton755.addSelectionListener((SelectionListener)new Class184(this));
        this.method402();
    }
    
    private void method402() {
        this.aCombo699.removeAll();
        this.aCombo699.add("Keyboard");
        for (int method740 = Controllers.method740(), i = 0; i < method740; ++i) {
            this.aCombo699.add(Controllers.method741(i).getName());
        }
        this.aCombo699.setText("Keyboard");
    }
    
    private void method404() {
        Property.aStringArray661[0] = this.aText627.getText().trim();
        Property.aStringArray661[1] = this.aText754.getText().trim();
        Property.aStringArray661[2] = this.aText757.getText().trim();
        Property.aStringArray661[3] = this.aText760.getText().trim();
        Property.aStringArray661[4] = this.aText763.getText().trim();
        Property.aStringArray661[5] = this.aText766.getText().trim();
        Property.aStringArray661[6] = this.aText768.getText().trim();
        Property.aStringArray661[7] = this.aText770.getText().trim();
        Property.aStringArray661[8] = this.aText772.getText().trim();
        Property.aStringArray661[9] = this.aText774.getText().trim();
        Property.aStringArray661[10] = this.aText629.getText().trim();
        Property.aStringArray661[11] = this.aText631.getText().trim();
        Property.aStringArray661[14] = this.aText739.getText().trim();
        Property.aStringArray661[15] = this.aText742.getText().trim();
        Property.aStringArray661[12] = this.aText745.getText().trim();
        Property.aStringArray661[13] = this.aText748.getText().trim();
        Property.aStringArray661[16] = this.aText751.getText().trim();
        Property.aStringArray661[17] = this.aText731.getText().trim();
        Property.aStringArray661[18] = this.aText735.getText().trim();
    }
    
    private void method406() {
        final int selectionIndex;
        if ((selectionIndex = this.aCombo699.getSelectionIndex()) < 0 || this.aText627 == null || ((Widget)this.aText627).isDisposed()) {
            return;
        }
        Text text;
        String method744;
        if (selectionIndex == 0) {
            this.aText627.setText(Property.aStringArray661[0]);
            this.aText754.setText(Property.aStringArray661[1]);
            this.aText757.setText(Property.aStringArray661[2]);
            this.aText760.setText(Property.aStringArray661[3]);
            this.aText763.setText(Property.aStringArray661[4]);
            this.aText766.setText(Property.aStringArray661[5]);
            this.aText768.setText(Property.aStringArray661[6]);
            this.aText770.setText(Property.aStringArray661[7]);
            this.aText772.setText(Property.aStringArray661[8]);
            this.aText774.setText(Property.aStringArray661[9]);
            this.aText629.setText(Property.aStringArray661[10]);
            this.aText631.setText(Property.aStringArray661[11]);
            this.aText739.setText(Property.aStringArray661[14]);
            this.aText742.setText(Property.aStringArray661[15]);
            this.aText745.setText(Property.aStringArray661[12]);
            this.aText748.setText(Property.aStringArray661[13]);
            this.aText751.setText(Property.aStringArray661[16]);
            this.aText731.setText(Property.aStringArray661[17]);
            text = this.aText735;
            method744 = Property.aStringArray661[18];
        }
        else {
            final int n = selectionIndex - 1;
            this.aText627.setText(Controllers.method744(n, 0));
            this.aText754.setText(Controllers.method744(n, 1));
            this.aText757.setText(Controllers.method744(n, 2));
            this.aText760.setText(Controllers.method744(n, 3));
            this.aText763.setText(Controllers.method744(n, 4));
            this.aText766.setText(Controllers.method744(n, 5));
            this.aText768.setText(Controllers.method744(n, 6));
            this.aText770.setText(Controllers.method744(n, 7));
            this.aText772.setText(Controllers.method744(n, 8));
            this.aText774.setText(Controllers.method744(n, 9));
            this.aText629.setText(Controllers.method744(n, 10));
            this.aText631.setText(Controllers.method744(n, 11));
            this.aText739.setText(Controllers.method744(n, 14));
            this.aText742.setText(Controllers.method744(n, 15));
            this.aText745.setText(Controllers.method744(n, 12));
            this.aText748.setText(Controllers.method744(n, 13));
            this.aText751.setText(Controllers.method744(n, 16));
            this.aText731.setText(Controllers.method744(n, 17));
            text = this.aText735;
            method744 = Controllers.method744(n, 18);
        }
        text.setText(method744);
    }
    
    public final boolean updateController() {
        if (this.aCombo699 == null || ((Widget)this.aCombo699).isDisposed()) {
            return false;
        }
        EmulatorImpl.asyncExec(new Class193(this));
        return true;
    }
    
    private void setupSystemComp() {
        (this.systemComp = new Composite((Composite)this.tabFolder, 0)).setLayout((Layout)new GridLayout());
        this.initSystemComp();
    }
    
    private void setupCoreApiComp() {
        (this.coreApiComp = new Composite((Composite)this.tabFolder, 0)).setLayout((Layout)new GridLayout());
        this.method412();
    }
    
    private void method412() {
        final GridData gridData;
        (gridData = new GridData()).grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = 2;
        gridData.horizontalAlignment = 4;
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = 4;
        ((Composite)(this.aGroup678 = new Group(this.coreApiComp, 0))).setLayout((Layout)new GridLayout());
        ((Control)this.aGroup678).setLayoutData((Object)layoutData);
        (this.aButton724 = new Button((Composite)this.aGroup678, 32)).setText(UILocale.uiText("OPTION_COREAPI_VIBRATION", "Enable Vibration APIs."));
        ((Control)this.aButton724).setLayoutData((Object)gridData);
        this.aButton724.setSelection(Settings.enableVibration);
        (this.aButton728 = new Button((Composite)this.aGroup678, 32)).setText(UILocale.uiText("OPTION_COREAPI_KEY_REPEAT", "Enable Canvas.keyRepeated(int)."));
        ((Control)this.aButton728).setLayoutData((Object)gridData);
        this.aButton728.setSelection(Settings.enableKeyRepeat);
        (this.aButton732 = new Button((Composite)this.aGroup678, 32)).setText(UILocale.uiText("OPTION_COREAPI_FULLSCREEN", "Ignore Canvas.setFullScreenMode(boolean)."));
        ((Control)this.aButton732).setLayoutData((Object)gridData);
        this.aButton732.setSelection(Settings.ignoreFullScreen);
        (this.aButton736 = new Button((Composite)this.aGroup678, 32)).setText(UILocale.uiText("OPTION_COREAPI_NO_NETWORK", "Network not available."));
        ((Control)this.aButton736).setLayoutData((Object)gridData);
        this.aButton736.setSelection(Settings.networkNotAvailable);
    }
    
    private void setupMediaComp() {
        (this.mediaComp = new Composite((Composite)this.tabFolder, 0)).setLayout((Layout)new GridLayout());
        this.initMediaComp();
    }
    
    private void initMediaComp() {
        final GridData fill = new GridData();
        fill.horizontalAlignment = 4;
        fill.grabExcessHorizontalSpace = true;
        fill.grabExcessVerticalSpace = true;
        fill.verticalAlignment = 4;
        final GridData fillHor = new GridData();
        fillHor.horizontalAlignment = GridData.FILL;
        fillHor.grabExcessHorizontalSpace = true;
        mediaGroup = new Group(this.mediaComp, 0);
        mediaGroup.setLayout(new GridLayout());
        mediaGroup.setLayoutData(fill);
        new Label(this.mediaGroup, 32).setText(UILocale.uiText("OPTION_MEDIA_VLC_DIR", "VLC Folder " + (Emulator.JAVA_64 ? "(64-bit only)" : " (32-bit only)") + ":"));
        vlcDirText = new Text(mediaGroup, SWT.DEFAULT);
        vlcDirText.setEditable(true);
        vlcDirText.setEnabled(true);
        vlcDirText.setLayoutData(fillHor);
        vlcDirText.setText(Settings.vlcDir);
	}

	private void initSystemComp() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = 4;
        ((Composite)(this.sysChecksGroup = new Group(this.systemComp, 0))).setLayout((Layout)new GridLayout());
        ((Control)this.sysChecksGroup).setLayoutData((Object)layoutData);
        (this.aButton740 = new Button((Composite)this.sysChecksGroup, 32)).setText(UILocale.uiText("OPTION_SYSTEM_ASSOCIATE", "Associate with *.jar files."));
        this.aButton740.setSelection(Settings.associateWithJar);
        (this.aButton743 = new Button((Composite)this.sysChecksGroup, 32)).setText(UILocale.uiText("OPTION_SYSTEM_SHORTCUT", "Add shortcut to right-click menu."));
        this.aButton743.setSelection(Settings.rightClickMenu);
        (this.aButton746 = new Button((Composite)this.sysChecksGroup, 32)).setText(UILocale.uiText("OPTION_SYSTEM_XRAY_BG", "X-Ray View: OverLap images."));
        this.aButton746.setSelection(Settings.xrayOverlapScreen);
        (this.aButton749 = new Button((Composite)this.sysChecksGroup, 32)).setText(UILocale.uiText("OPTION_SYSTEM_XRAY_CLIP", "X-Ray View: Show image clipping region."));
        this.aButton749.setSelection(Settings.xrayShowClipBorder);
        (this.aButton752 = new Button((Composite)this.sysChecksGroup, 32)).setText(UILocale.uiText("OPTION_SYSTEM_INFO_COLOR", "Info View: Show color in (R,G,B)."));
        this.aButton752.setSelection(Settings.infoColorHex);
        (this.aButton703 = new Button((Composite)this.sysChecksGroup, 32)).setText(UILocale.uiText("OPTION_SYSTEM_RELEASED_IMG", "Memory View: Record released images."));
        this.aButton703.setSelection(Settings.recordReleasedImg);
        (this.aButton709 = new Button((Composite)this.sysChecksGroup, 32)).setText(UILocale.uiText("OPTION_SYSTEM_AUTOGEN_JAD", "Auto Generate Jad file with \"KEmu-Platform\"."));
        this.aButton709.setSelection(Settings.autoGenJad);
        (this.aButton714 = new Button((Composite)this.sysChecksGroup, 32)).setText(UILocale.uiText("OPTION_SYSTEM_TRACK_NEW", "Track \"new/new[]...\" operations."));
        this.aButton714.setSelection(Settings.enableNewTrack);
        (this.aButton719 = new Button((Composite)this.sysChecksGroup, 32)).setText(UILocale.uiText("OPTION_SYSTEM_TRACK_METHOD", "Track method calls."));
        this.aButton719.setSelection(Settings.enableMethodTrack);
        //new Label(this.aGroup688, 32).setText(UILocale.uiText("OPTION_SYSTEM_INACTIVITY_TIMER", "Inactivity timer (Set 0 to disable)"));
        //this.inactiveTimerSpinner = new Spinner(this.aGroup688, 32);
        //inactiveTimerSpinner.setValues(0, 0, Integer.MAX_VALUE, 0, 1, 10);
        //inactiveTimerSpinner.setSelection(Emulator.inactivityTimer);
        (this.rpcBtn = new Button((Composite)this.sysChecksGroup, 32)).setText(UILocale.uiText("OPTION_SYSTEM_DISCORD_RICHPRESENCE", "Discord Rich Presence"));
        this.rpcBtn.setSelection(Emulator.rpcEnabled);
        (this.antiAliasBtn = new Button((Composite)this.sysChecksGroup, 32)).setText(UILocale.uiText("OPTION_AWT_ANTIALIASING", "AWT Smooth drawing"));
        this.antiAliasBtn.setSelection(Settings.awtAntiAliasing);
    }
    
    private void setupSysFontComp() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalSpan = 2;
        layoutData.horizontalAlignment = 4;
        layoutData.verticalAlignment = 2;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.grabExcessHorizontalSpace = true;
        final GridData layoutData2;
        (layoutData2 = new GridData()).horizontalAlignment = 4;
        layoutData2.verticalAlignment = 2;
        final GridData layoutData3;
        (layoutData3 = new GridData()).horizontalAlignment = 1;
        layoutData3.verticalAlignment = 2;
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 3;
        (this.sysFontComp = new Composite((Composite)this.tabFolder, 0)).setLayout((Layout)layout);
        (this.aCLabel638 = new CLabel(this.sysFontComp, 0)).setText(UILocale.uiText("OPTION_FONT_DEFAULT_FONT", "Default Font:"));
        ((Control)this.aCLabel638).setLayoutData((Object)layoutData2);
        this.method418();
        (this.aCLabel640 = new CLabel(this.sysFontComp, 0)).setText(UILocale.uiText("OPTION_FONT_LARGE_SIZE", "Large Size:"));
        ((Control)this.aCLabel640).setLayoutData((Object)layoutData3);
        (this.aSpinner670 = new Spinner(this.sysFontComp, 2048)).setMinimum(1);
        this.aSpinner670.setSelection(this.fontLargeSize);
        this.aSpinner670.addModifyListener((ModifyListener)new Class186(this));
        this.method420();
        (this.aCLabel642 = new CLabel(this.sysFontComp, 0)).setText(UILocale.uiText("OPTION_FONT_MIDDLE_SIZE", "Medium Size:"));
        (this.aSpinner679 = new Spinner(this.sysFontComp, 2048)).setMinimum(1);
        this.aSpinner679.setSelection(this.fontMediumSIze);
        this.aSpinner679.addModifyListener((ModifyListener)new Class187(this));
        this.method422();
        (this.aCLabel644 = new CLabel(this.sysFontComp, 0)).setText(UILocale.uiText("OPTION_FONT_SMALL_SIZE", "Small Size:"));
        (this.aSpinner690 = new Spinner(this.sysFontComp, 2048)).setMinimum(1);
        this.aSpinner690.setSelection(this.fontSmallSize);
        this.aSpinner690.addModifyListener((ModifyListener)new Class188(this));
        this.method424();
        (this.aCLabel645 = new CLabel(this.sysFontComp, 0)).setText(UILocale.uiText("OPTION_FONT_TEST_TEXT", "Test Text:"));
        (this.aText633 = new Text(this.sysFontComp, 2048)).setText(UILocale.uiText("OPTION_FONT_TEST_TEXT_TXT", "This is an Example."));
        ((Control)this.aText633).setLayoutData((Object)layoutData);
        this.aText633.addModifyListener((ModifyListener)new Class192(this));
    }
    
    private void method418() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalSpan = 2;
        layoutData.verticalAlignment = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalAlignment = 4;
        ((Control)(this.aCombo689 = new Combo(this.sysFontComp, 8))).setLayoutData((Object)layoutData);
        this.aCombo689.addModifyListener((ModifyListener)new Class191(this));
        final FontData[] fontList = ((Device)Property.aDisplay656).getFontList((String)null, true);
        final ArrayList<Comparable> list = new ArrayList<Comparable>();
        list.add("Nokia");
        for (int i = 0; i < fontList.length; ++i) {
            if (!list.contains(fontList[i].getName()) && !fontList[i].getName().startsWith("@")) {
                list.add(fontList[i].getName());
            }
        }
        final FontData[] fontList2 = ((Device)Property.aDisplay656).getFontList((String)null, false);
        for (int j = 0; j < fontList2.length; ++j) {
            if (!list.contains(fontList2[j].getName()) && !fontList2[j].getName().startsWith("@")) {
                list.add(fontList2[j].getName());
            }
        }
        Collections.sort(list);
        String aString682 = (String) list.get(0);
        for (int k = 0; k < list.size(); ++k) {
            this.aCombo689.add((String)list.get(k));
            if (this.defaultFont.equalsIgnoreCase((String) list.get(k))) {
                aString682 = (String) list.get(k);
            }
        }
        this.defaultFont = aString682;
        this.aCombo689.setText(this.defaultFont);
    }
    
    private void method420() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = 2;
        ((Control)(this.aCanvas663 = new Canvas(this.sysFontComp, 264192))).setLayoutData((Object)layoutData);
        ((Control)this.aCanvas663).addPaintListener((PaintListener)new Class190(this));
    }
    
    private void method422() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = 2;
        ((Control)(this.aCanvas680 = new Canvas(this.sysFontComp, 264192))).setLayoutData((Object)layoutData);
        ((Control)this.aCanvas680).addPaintListener((PaintListener)new Class196(this));
    }
    
    private void method424() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = 2;
        ((Control)(this.aCanvas691 = new Canvas(this.sysFontComp, 264192))).setLayoutData((Object)layoutData);
        ((Control)this.aCanvas691).addPaintListener((PaintListener)new Class195(this));
    }
    
    private void method360(final int n) {
        if (!this.aShell655.isVisible()) {
            return;
        }
        IFont font = null;
        if ((n & 0x1) != 0x0) {
            Label_0080: {
                IFont font2;
                if (Settings.g2d == 0) {
                    font2 = new FontSWT(this.aCombo689.getText(), this.aSpinner670.getSelection(), 0);
                }
                else {
                    if (Settings.g2d != 1) {
                        break Label_0080;
                    }
                    font2 = new emulator.graphics2D.awt.a(this.aCombo689.getText(), this.aSpinner670.getSelection(), 0);
                }
                font = font2;
            }
            final int x = ((Control)this.aCanvas663).getSize().x;
            final int y = ((Control)this.aCanvas663).getSize().y;
            this.anIImage671 = Emulator.getEmulator().newImage(x, y, false, 0);
            final IGraphics2D graphics;
            (graphics = this.anIImage671.createGraphics()).setColor(65280, false);
            graphics.setFont(font);
            graphics.drawString(this.aText633.getText(), 1, y >> 1);
            ((Control)this.aCanvas663).redraw();
        }
        if ((n & 0x2) != 0x0) {
            Label_0242: {
                IFont font3;
                if (Settings.g2d == 0) {
                    font3 = new FontSWT(this.aCombo689.getText(), this.aSpinner679.getSelection(), 0);
                }
                else {
                    if (Settings.g2d != 1) {
                        break Label_0242;
                    }
                    font3 = new emulator.graphics2D.awt.a(this.aCombo689.getText(), this.aSpinner679.getSelection(), 0);
                }
                font = font3;
            }
            final int x2 = ((Control)this.aCanvas680).getSize().x;
            final int y2 = ((Control)this.aCanvas680).getSize().y;
            this.anIImage674 = Emulator.getEmulator().newImage(x2, y2, false, 0);
            final IGraphics2D graphics2;
            (graphics2 = this.anIImage674.createGraphics()).setColor(65280, false);
            graphics2.setFont(font);
            graphics2.drawString(this.aText633.getText(), 1, y2 >> 1);
            ((Control)this.aCanvas680).redraw();
        }
        if ((n & 0x4) != 0x0) {
            Label_0404: {
                IFont font4;
                if (Settings.g2d == 0) {
                    font4 = new FontSWT(this.aCombo689.getText(), this.aSpinner690.getSelection(), 0);
                }
                else {
                    if (Settings.g2d != 1) {
                        break Label_0404;
                    }
                    font4 = new emulator.graphics2D.awt.a(this.aCombo689.getText(), this.aSpinner690.getSelection(), 0);
                }
                font = font4;
            }
            final int x3 = ((Control)this.aCanvas691).getSize().x;
            final int y3 = ((Control)this.aCanvas691).getSize().y;
            this.anIImage693 = Emulator.getEmulator().newImage(x3, y3, false, 0);
            final IGraphics2D graphics3;
            (graphics3 = this.anIImage693.createGraphics()).setColor(65280, false);
            graphics3.setFont(font);
            graphics3.drawString(this.aText633.getText(), 1, y3 >> 1);
            ((Control)this.aCanvas691).redraw();
        }
    }
    
    private void setupRecordsComp() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 2;
        layoutData.verticalAlignment = 2;
        final GridData layoutData2;
        (layoutData2 = new GridData()).horizontalSpan = 3;
        layoutData2.verticalAlignment = 2;
        layoutData2.horizontalAlignment = 4;
        final GridData layoutData3;
        (layoutData3 = new GridData()).horizontalSpan = 3;
        layoutData3.verticalAlignment = 4;
        layoutData3.grabExcessHorizontalSpace = true;
        layoutData3.grabExcessVerticalSpace = false;
        layoutData3.heightHint = 160;
        layoutData3.horizontalAlignment = 4;
        final GridData layoutData4;
        (layoutData4 = new GridData()).horizontalAlignment = 4;
        layoutData4.grabExcessHorizontalSpace = true;
        layoutData4.verticalAlignment = 2;
        final GridData layoutData5;
        (layoutData5 = new GridData()).horizontalAlignment = 4;
        layoutData5.verticalAlignment = 2;
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 3;
        (this.recordsComp = new Composite((Composite)this.tabFolder, 0)).setLayout((Layout)layout);
        (this.aCLabel683 = new CLabel(this.recordsComp, 0)).setText(UILocale.uiText("OPTION_RECORDS_RMS_FOLDER", "RMS Folder:"));
        ((Control)this.aCLabel683).setLayoutData((Object)layoutData5);
        (this.aText662 = new Text(this.recordsComp, 2048)).setEditable(true);
        ((Control)this.aText662).setEnabled(true);
        ((Control)this.aText662).setLayoutData((Object)layoutData4);
        this.aText662.setText(this.rmsFolder);
        (this.aButton666 = new Button(this.recordsComp, 8388616)).setText("...");
        (this.aCLabel647 = new CLabel(this.recordsComp, 0)).setText(UILocale.uiText("OPTION_RECORDS_RMS_TEXT", "All Records in current folder:"));
        ((Control)this.aCLabel647).setLayoutData((Object)layoutData2);
        (this.aTable665 = new Table(this.recordsComp, 2080)).setHeaderVisible(false);
        ((Control)this.aTable665).setLayoutData((Object)layoutData3);
        this.aTable665.setLinesVisible(true);
        (this.aButton761 = new Button(this.recordsComp, 8388608)).setText(UILocale.uiText("OPTION_RECORDS_SELECT_ALL", "Select All"));
        this.aButton761.addSelectionListener((SelectionListener)new Class194(this));
        (this.aButton758 = new Button(this.recordsComp, 8388608)).setText(UILocale.uiText("OPTION_RECORDS_CLEAR_RECORD", "Clear Selected Records"));
        ((Control)this.aButton758).setLayoutData((Object)layoutData);
        this.aButton758.addSelectionListener((SelectionListener)new Class103(this));
        new TableColumn(this.aTable665, 0).setWidth(200);
        this.method428();
        this.aButton666.addSelectionListener((SelectionListener)new Class101(this));
    }
    
    private String method374() {
        String s = null;
        Label_0083: {
            StringBuffer sb;
            String substring;
            if ((s = this.aText662.getText().trim()).startsWith(".")) {
                sb = new StringBuffer().append(Emulator.getAbsolutePath());
                substring = s.substring(1);
            }
            else {
                if (!s.startsWith("/") && !s.startsWith("\\")) {
                    break Label_0083;
                }
                sb = new StringBuffer().append(Emulator.getAbsolutePath());
                substring = s;
            }
            s = sb.append(substring).toString();
        }
        final File file;
        if (!(file = new File(s)).exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        final String string = s + "\\" + this.method355();
        final File file2;
        if (!(file2 = new File(string)).exists() || !file2.isDirectory()) {
            file2.mkdirs();
        }
        return string + "\\";
    }
    
    private void method428() {
        final File file;
        if (!(file = new File(this.method374())).exists() || !file.isDirectory()) {
            return;
        }
        final File[] listFiles;
        if ((listFiles = file.listFiles(new Class105(this))).length > this.aTable665.getItemCount()) {
            for (int i = listFiles.length - this.aTable665.getItemCount(); i > 0; --i) {
                new TableItem(this.aTable665, 0);
            }
        }
        else {
            while (this.aTable665.getItemCount() > listFiles.length) {
                this.aTable665.remove(listFiles.length);
            }
        }
        for (int j = 0; j < listFiles.length; ++j) {
            final TableItem item;
            (item = this.aTable665.getItem(j)).setText(0, listFiles[j].getName().substring(1));
            item.setChecked(false);
        }
    }
    
    private void setupNetworkComp() {
        (this.networkComp = new Composite((Composite)this.tabFolder, 0)).setLayout((Layout)new GridLayout());
        this.method432();
    }
    
    private void method432() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalSpan = 2;
        layoutData.verticalAlignment = 2;
        layoutData.horizontalAlignment = 2;
        final GridData layoutData2;
        (layoutData2 = new GridData()).horizontalAlignment = 4;
        layoutData2.grabExcessHorizontalSpace = true;
        layoutData2.verticalAlignment = 2;
        final GridData layoutData3;
        (layoutData3 = new GridData()).horizontalAlignment = 3;
        layoutData3.verticalAlignment = 2;
        final GridData layoutData4;
        (layoutData4 = new GridData()).horizontalAlignment = 4;
        layoutData4.grabExcessHorizontalSpace = true;
        layoutData4.verticalAlignment = 2;
        final GridData layoutData5;
        (layoutData5 = new GridData()).horizontalAlignment = 3;
        layoutData5.verticalAlignment = 2;
        final GridData layoutData6;
        (layoutData6 = new GridData()).horizontalAlignment = 3;
        layoutData6.verticalAlignment = 2;
        final GridData gridData;
        (gridData = new GridData()).horizontalAlignment = 4;
        gridData.widthHint = 40;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = 2;
        final GridData layoutData7;
        (layoutData7 = new GridData()).horizontalAlignment = 4;
        layoutData7.widthHint = 50;
        layoutData7.grabExcessHorizontalSpace = true;
        layoutData7.verticalAlignment = 2;
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 4;
        final GridData layoutData8;
        (layoutData8 = new GridData()).horizontalAlignment = 4;
        layoutData8.grabExcessHorizontalSpace = true;
        layoutData8.grabExcessVerticalSpace = true;
        layoutData8.verticalAlignment = 1;
        (this.aGroup700 = new Group(this.networkComp, 0)).setText(UILocale.uiText("OPTION_NETWORK_PROXY", "Proxy"));
        ((Composite)this.aGroup700).setLayout((Layout)layout);
        ((Control)this.aGroup700).setLayoutData((Object)layoutData8);
        (this.aCLabel648 = new CLabel((Composite)this.aGroup700, 0)).setText(UILocale.uiText("OPTION_NETWORK_PROXY_TYPE", "ProxyType:"));
        this.method350();
        (this.aCLabel651 = new CLabel((Composite)this.aGroup700, 0)).setText("");
        (this.aCLabel649 = new CLabel((Composite)this.aGroup700, 0)).setText(UILocale.uiText("OPTION_NETWORK_HOST", "Host:"));
        ((Control)this.aCLabel649).setLayoutData((Object)layoutData6);
        ((Control)(this.aText635 = new Text((Composite)this.aGroup700, 2048))).setLayoutData((Object)layoutData7);
        this.aText635.setText(Settings.proxyHost);
        (this.aCLabel650 = new CLabel((Composite)this.aGroup700, 0)).setText(UILocale.uiText("OPTION_NETWORK_PORT", "Port:"));
        (this.aText637 = new Text((Composite)this.aGroup700, 2048)).setText(Settings.proxyPort);
        ((Control)this.aText637).setLayoutData((Object)gridData);
        (this.aCLabel652 = new CLabel((Composite)this.aGroup700, 0)).setText(UILocale.uiText("OPTION_NETWORK_USERNAME", "Username:"));
        ((Control)this.aCLabel652).setLayoutData((Object)layoutData5);
        ((Control)(this.aText639 = new Text((Composite)this.aGroup700, 2048))).setLayoutData((Object)layoutData4);
        this.aText639.setText(Settings.proxyUser);
        (this.aCLabel653 = new CLabel((Composite)this.aGroup700, 0)).setText(UILocale.uiText("OPTION_NETWORK_PASSWORD", "Password:"));
        (this.aText641 = new Text((Composite)this.aGroup700, 4196352)).setText(Settings.proxyPass);
        ((Control)this.aText641).setLayoutData((Object)gridData);
        (this.aCLabel654 = new CLabel((Composite)this.aGroup700, 0)).setText(UILocale.uiText("OPTION_NETWORK_DOMAIN", "Domain:"));
        ((Control)this.aCLabel654).setLayoutData((Object)layoutData3);
        ((Control)(this.aText643 = new Text((Composite)this.aGroup700, 2048))).setLayoutData((Object)layoutData2);
        this.aText643.setText(Settings.proxyDomain);
        (this.aButton764 = new Button((Composite)this.aGroup700, 8388608)).setText(UILocale.uiText("OPTION_NETWORK_CONNECT", "Connect"));
        ((Control)this.aButton764).setLayoutData((Object)layoutData);
        this.aButton764.addSelectionListener((SelectionListener)new Class97(this));
        this.aCombo706.addModifyListener((ModifyListener)new Class65(this));
        this.aCombo706.select(Settings.proxyType);
    }
    
    private void method350() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalSpan = 2;
        layoutData.verticalAlignment = 2;
        ((Control)(this.aCombo706 = new Combo((Composite)this.aGroup700, 8))).setLayoutData((Object)layoutData);
        this.aCombo706.add("None Proxy");
        this.aCombo706.add("HTTP Proxy");
        this.aCombo706.add("Socks5 Proxy");
    }
    
    private static void method351() {
        System.getProperties().remove("http.proxyHost");
        System.getProperties().remove("http.proxyPort");
        System.getProperties().remove("http.auth.ntlm.domain");
        System.getProperties().remove("socksProxyHost");
        System.getProperties().remove("socksProxyPort");
    }
    
    private void method352() {
        Emulator.getEmulator().getScreen();
        EmulatorScreen.method554();
        if (this.aText635.getText().trim().length() < 1) {
            final MessageBox messageBox;
            ((Dialog)(messageBox = new MessageBox(this.aShell655))).setText(UILocale.uiText("OPTION_NETWORK_PROXY_TEST", "Proxy Test"));
            messageBox.setMessage(UILocale.uiText("OPTION_NETWORK_PROXY_EMPTY", "Empty proxy host!"));
            messageBox.open();
        }
        else {
            final MessageBox messageBox2;
            ((Dialog)(messageBox2 = new MessageBox(this.aShell655))).setText(UILocale.uiText("OPTION_NETWORK_PROXY_TEST", "Proxy Test"));
            messageBox2.setMessage(UILocale.uiText("OPTION_NETWORK_PROXY_UNIMP", "Proxy test is underimplemented!"));
            messageBox2.open();
        }
        ((EmulatorScreen)Emulator.getEmulator().getScreen()).method571();
    }
    
    private void method353() {
        method351();
        final int anInt1257;
        if ((anInt1257 = Settings.proxyType) == 0) {
            return;
        }
        if (anInt1257 == 1) {
            System.setProperty("http.proxyHost", Settings.proxyHost);
            System.setProperty("http.proxyPort", Settings.proxyPort);
            if (Settings.proxyDomain.length() > 0) {
                System.setProperty("http.auth.ntlm.domain", Settings.proxyDomain);
            }
        }
        else if (anInt1257 == 2) {
            System.setProperty("socksProxyHost", Settings.proxyHost);
            System.setProperty("socksProxyPort", Settings.proxyPort);
        }
        Authenticator.setDefault(new MyAuthenticator(this, Settings.proxyUser, Settings.proxyPass));
    }
    
    static Combo method361(final Property class38) {
        return class38.aCombo657;
    }
    
    static void method362(final Property class38) {
        class38.method387();
    }
    
    static void method375(final Property class38) {
        class38.method358();
    }
    
    static Shell method364(final Property class38) {
        return class38.aShell655;
    }
    
    static Scale method370(final Property class38) {
        return class38.aScale669;
    }
    
    static CLabel method363(final Property class38) {
        return class38.aCLabel738;
    }
    
    static Combo method376(final Property class38) {
        return class38.aCombo699;
    }
    
    static Text method366(final Property class38) {
        return class38.aText731;
    }
    
    static String[] method365() {
        return Property.aStringArray661;
    }
    
    static Text method377(final Property class38) {
        return class38.aText735;
    }
    
    static Text method380(final Property class38) {
        return class38.aText754;
    }
    
    static Text method385(final Property class38) {
        return class38.aText757;
    }
    
    static Text method388(final Property class38) {
        return class38.aText760;
    }
    
    static Text method391(final Property class38) {
        return class38.aText763;
    }
    
    static Text method394(final Property class38) {
        return class38.aText766;
    }
    
    static Text method397(final Property class38) {
        return class38.aText768;
    }
    
    static Text method399(final Property class38) {
        return class38.aText770;
    }
    
    static Text method401(final Property class38) {
        return class38.aText772;
    }
    
    static Text method403(final Property class38) {
        return class38.aText774;
    }
    
    static Text method405(final Property class38) {
        return class38.aText627;
    }
    
    static Text method407(final Property class38) {
        return class38.aText629;
    }
    
    static Text method409(final Property class38) {
        return class38.aText631;
    }
    
    static Text method411(final Property class38) {
        return class38.aText739;
    }
    
    static Text method413(final Property class38) {
        return class38.aText742;
    }
    
    static Text method415(final Property class38) {
        return class38.aText745;
    }
    
    static Text method417(final Property class38) {
        return class38.aText748;
    }
    
    static Text method419(final Property class38) {
        return class38.aText751;
    }
    
    static void method381(final Property class38) {
        class38.method406();
    }
    
    static void method386(final Property class38) {
        class38.method402();
    }
    
    static void method369(final Property class38, final int n) {
        class38.method360(n);
    }
    
    static IImage method356(final Property class38) {
        return class38.anIImage671;
    }
    
    static IImage method378(final Property class38) {
        return class38.anIImage674;
    }
    
    static IImage method382(final Property class38) {
        return class38.anIImage693;
    }
    
    static Table method367(final Property class38) {
        return class38.aTable665;
    }
    
    static String method368(final Property class38) {
        return class38.method374();
    }
    
    static void method389(final Property class38) {
        class38.method428();
    }
    
    static Text method421(final Property class38) {
        return class38.aText662;
    }
    
    static void method392(final Property class38) {
        class38.method352();
    }
    
    static Combo method383(final Property class38) {
        return class38.aCombo706;
    }
    
    static Text method423(final Property class38) {
        return class38.aText635;
    }
    
    static Text method425(final Property class38) {
        return class38.aText637;
    }
    
    static Text method427(final Property class38) {
        return class38.aText639;
    }
    
    static Text method429(final Property class38) {
        return class38.aText641;
    }
    
    static Text method431(final Property class38) {
        return class38.aText643;
    }
    
    static Button method357(final Property class38) {
        return class38.aButton764;
    }
    
    static void method395(final Property class38) {
        method351();
    }
    
    static {
        Property.aStringArray661 = new String[19];
    }
    
    private final class MyAuthenticator extends Authenticator
    {
        private String aString780;
        private String aString781;
        
        MyAuthenticator(final Property class38, final String aString780, final String aString781) {
            super();
            this.aString780 = aString780;
            this.aString781 = aString781;
        }
        
        public final PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.aString780, this.aString781.toCharArray());
        }
    }
    
    private final class SortProperties extends Properties
    {
        private static final long serialVersionUID = 1L;
        
        private SortProperties(final Property class38) {
            super();
        }
        
        public final Enumeration keys() {
            final List list;
            Collections.sort((List<Comparable>)(list = (List)Collections.list((Enumeration)super.keys())));
            return Collections.enumeration((Collection)list);
        }
        
        SortProperties(final Property class38, final Class117 class39) {
            this(class38);
        }
    }
}
