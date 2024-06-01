package emulator.ui.swt;

import emulator.graphics2D.awt.FontAWT;
import emulator.graphics2D.awt.ImageAWT;
import org.eclipse.swt.widgets.*;

import java.util.*;

import emulator.*;
import emulator.ui.*;
import emulator.graphics2D.*;
import emulator.graphics2D.swt.FontSWT;
import emulator.graphics2D.swt.ImageSWT;
import emulator.graphics3D.*;

public final class EmulatorImpl implements IEmulator {
	private static Display display;
	private Vector aVector1379;
	private int screenDepth;
	private Methods aClass46_1381;
	private MemoryView aClass110_1382;
	private Watcher aClass5_1377;
	private Watcher aClass5_1391;
	private Property iproperty;
	private EmulatorScreen iscreen;
	private Log ilogstream;
	private KeyPad aClass161_1387;
	private InfosBox aClass108_1390;
	private M3GViewUI aClass90_1384;
	private MessageConsole aClass83_1389;
	public Properties midletProps;
	private static Hashtable<String, FontSWT> swtFontsCache = new Hashtable<String, FontSWT>();

	public EmulatorImpl() {
		super();
		EmulatorImpl.display = new Display();
		this.aVector1379 = new Vector();
		this.screenDepth = EmulatorImpl.display.getDepth();
		this.iproperty = new Property();
		this.ilogstream = new Log();
		this.aClass83_1389 = new MessageConsole();
		this.aClass108_1390 = new InfosBox();
		this.aClass161_1387 = new KeyPad();
		this.aClass5_1377 = new Watcher(0);
		this.aClass5_1391 = new Watcher(1);
		this.aClass110_1382 = new MemoryView();
		this.aClass46_1381 = new Methods();
	}

	public static void dispose() {
		if (!EmulatorImpl.display.isDisposed()) {
			EmulatorImpl.display.dispose();
		}
	}

	public static Display getDisplay() {
		return EmulatorImpl.display;
	}

	public static void syncExec(final Runnable runnable) {
		if (!EmulatorImpl.display.isDisposed()) {
			EmulatorImpl.display.syncExec(runnable);
		}
	}

	public static void asyncExec(final Runnable runnable) {
		if (!EmulatorImpl.display.isDisposed()) {
			EmulatorImpl.display.asyncExec(runnable);
		}
	}

	public final int getScreenDepth() {
		return this.screenDepth;
	}

	public final EmulatorScreen getEmulatorScreen() {
		if (this.iscreen == null) {
			this.iscreen = new EmulatorScreen(Devices.getPropertyInt("SCREEN_WIDTH"), Devices.getPropertyInt("SCREEN_HEIGHT"));
		}
		return this.iscreen;
	}

	public final Watcher method822() {
		return this.aClass5_1377;
	}

	public final Watcher method829() {
		return this.aClass5_1391;
	}

	public final MemoryView method823() {
		return this.aClass110_1382;
	}

	public final Methods method824() {
		return this.aClass46_1381;
	}

	public final InfosBox method825() {
		return this.aClass108_1390;
	}

	public final KeyPad method826() {
		return this.aClass161_1387;
	}

	public final M3GViewUI getM3GView() {
		if (aClass90_1384 == null) {
			this.aClass90_1384 = new M3GViewUI();
		}
		return this.aClass90_1384;
	}

	public final void disposeSubWindows() {
		Settings.showLogFrame = this.ilogstream.isLogOpen();
		Settings.showInfoFrame = this.aClass108_1390.method610();
		this.aClass161_1387.method836();
		this.ilogstream.method330();
		this.aClass83_1389.method482();
		this.aClass108_1390.method608();
		Settings.showMemViewFrame = this.aClass110_1382.method622();
		this.aClass46_1381.method446();
		this.aClass110_1382.method656();
		this.aClass5_1377.method321();
		this.aClass5_1391.method321();
		if (aClass90_1384 != null)
			this.aClass90_1384.close();
		while (Watcher.aVector548.size() > 0) {
			((Watcher) Watcher.aVector548.get(0)).method321();
		}
		for (Object o : this.aVector1379) {
			((IPlugin) o).close();
		}
	}

	public final void pushPlugin(final IPlugin plugin) {
		this.aVector1379.add(plugin);
	}

	public final ILogStream getLogStream() {
		return this.ilogstream;
	}

	public final IProperty getProperty() {
		return this.iproperty;
	}

	public final IScreen getScreen() {
		return this.iscreen;
	}

	public final IMessage getMessage() {
		return this.aClass83_1389;
	}

	public final IFont newFont(final int size, final int style) {
		if (Settings.g2d == 0) {
			String s = this.iproperty.getDefaultFontName() + "." + size + "." + style;
			if (swtFontsCache.containsKey(s)) return swtFontsCache.get(s);
			FontSWT f = new FontSWT(this.iproperty.getDefaultFontName(), size, style);
			swtFontsCache.put(s, f);
			return f;
		}
		if (Settings.g2d == 1) {
			return new FontAWT(this.iproperty.getDefaultFontName(), size, style, false);
		}
		return null;
	}

	public final IFont newCustomFont(final int height, final int style) {
		if (Settings.g2d == 0) {
			String s = this.iproperty.getDefaultFontName() + ".-" + height + "." + style;
			if (swtFontsCache.containsKey(s)) return swtFontsCache.get(s);
			FontSWT f = new FontSWT(this.iproperty.getDefaultFontName(), height, style, true);
			swtFontsCache.put(s, f);
			return f;
		}
		if (Settings.g2d == 1) {
			return new FontAWT(this.iproperty.getDefaultFontName(), height, style, true);
		}
		return null;
	}

	public final IImage newImage(final int n, final int n2, final boolean transparent) {
		if (Settings.g2d == 0) {
			return new ImageSWT(n, n2, transparent, -1);
		}
		if (Settings.g2d == 1) {
			return new ImageAWT(n, n2, transparent, -1);
		}
		return null;
	}

	public final IImage newImage(int n, int n2, boolean b, int n3) {
		return Settings.g2d == 0 ? new ImageSWT(n, n2, b, n3) : (Settings.g2d == 1 ? new ImageAWT(n, n2, b, n3) : null);
	}


	public final IImage newImage(final byte[] array) {
		if (Settings.g2d == 0) {
			return new ImageSWT(array);
		}
		if (Settings.g2d == 1) {
			return new ImageAWT(array);
		}
		return null;
	}

	public final IGraphics3D getGraphics3D() {
		// TODO
		return Emulator.getPlatform().getGraphics3D();
	}

	public final void syncValues() {
		if (Watcher.profiler != null) {
			syncExec(Watcher.profiler);
		}
		for (int i = 0; i < Watcher.aVector548.size(); ++i) {
			asyncExec((Runnable) Watcher.aVector548.get(i));
		}
		if (this.aClass46_1381.method438()) {
			asyncExec(this.aClass46_1381);
		}
	}

	public final String getAppProperty(final String s) {
		final String property;
		if ((property = this.midletProps.getProperty(s)) != null) {
			return property.trim();
		}
		return null;
	}
}
