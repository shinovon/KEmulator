package emulator.debug;

import com.nokia.mid.sound.Sound;
import com.samsung.util.AudioClip;
import emulator.Emulator;
import emulator.Settings;
import emulator.graphics2D.IImage;
import emulator.media.vlc.VLCPlayerImpl;
import emulator.ui.swt.EmulatorScreen;
import org.apache.tools.zip.ZipFile;

import javax.microedition.lcdui.Image;
import javax.microedition.m3g.*;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerImpl;
import javax.microedition.media.control.VolumeControlImpl;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class Memory {

	public Hashtable classesTable;
	public Vector instances;
	public Vector images = new Vector();
	public Vector releasedImages = new Vector();
	public Vector players = new Vector();
	public Vector m3gObjects = new Vector();
	private final Vector checkClasses = new Vector();
	static Class _J;
	static Class _I;
	static Class _S;
	static Class _B;
	static Class _Z;
	static Class _F;
	static Class _D;
	static Class _C;

	public static final Object m3gLock = new Object();
	public static boolean debuggingM3G;

	private static Memory inst;

	public static Memory getInstance() {
		if (inst == null) {
			inst = new Memory();
		}
		return inst;
	}

	private Memory() {
		super();
		this.classesTable = new Hashtable();
		this.instances = new Vector() {
			public synchronized int indexOf(Object o, int index) {
				if (o == null) {
					for (int i = index; i < elementCount; i++)
						if (elementData[i] == null)
							return i;
				} else {
					for (int i = index; i < elementCount; i++)
						if (o == elementData[i])
							return i;
				}
				return -1;
			}
		};
		checkClasses.add("javax.microedition.lcdui.ImageItem");
		checkClasses.add("javax.microedition.lcdui.CustomItem");
		checkClasses.add("javax.microedition.lcdui.List");
		checkClasses.add("javax.microedition.lcdui.ChoiceGroup");
		checkClasses.add("javax.microedition.lcdui.Display");
		checkClasses.add("javax.microedition.lcdui.Form");
		checkClasses.add("javax.microedition.lcdui.Graphics");
	}

	public void updateEverything() {
		if (Settings.recordReleasedImg) {
			for (int i = 0; i < this.images.size(); ++i) {
				if (!this.releasedImages.contains(this.images.get(i))) {
					this.releasedImages.addElement(this.images.get(i));
				}
			}
		}
		this.classesTable.clear();
		this.instances.clear();
		this.images.clear();
		this.players.clear();
		this.m3gObjects.clear();
		try {
			this.players.addAll(PlayerImpl.players);
		} catch (Exception ignored) {}
		for (int j = 0; j < Emulator.jarClasses.size(); ++j) {
			final String s = (String) Emulator.jarClasses.get(j);
			Class cls = null;
			Object o;
			if (Emulator.getMIDlet().getClass().getName().equals(s)) {
				cls = cls(s);
				o = Emulator.getMIDlet();
			} else if (Emulator.getCurrentDisplay() != null && Emulator.getCurrentDisplay().getCurrent() != null && Emulator.getCurrentDisplay().getCurrent().getClass().getName().equals(s)) {
				cls = cls(s);
				o = Emulator.getCurrentDisplay().getCurrent();
			} else {
				try {
					cls = cls(s);
				} catch (Throwable ignored) {}
				o = null;
			}
			if (cls != null)
				method847(cls, o, s, false);
		}
		for (int j = 0; j < checkClasses.size(); ++j) {
			Class cls = null;
			final String s = (String) checkClasses.get(j);
			try {
				cls = cls(s);
			} catch (Throwable ignored) {}
			if (cls != null)
				method847(cls, null, s, false);
		}

		if (m3gObjects.size() == 0) return;

		debuggingM3G = true;

		if (Settings.g3d == 1) {
			// lwjgl engine
			for (int i = 0; i < m3gObjects.size(); i++) {
				m3gReadTextures(m3gObjects.elementAt(i));
			}
			return;
		}
		try {
			synchronized (m3gLock) {
				// delays to make sure jsr184client.dll did all the job
				Thread.sleep(5);
				for (int i = 0; i < m3gObjects.size(); i++) {
					m3gReadTextures(m3gObjects.elementAt(i));
					Thread.yield();
				}
				Thread.sleep(5);
			}
		} catch (Exception ignored) {}
	}

	private void method847(final Class clazz, final Object o, final String s, boolean vector) {
		String clazzName = clazz.getName();
		if (clazz.isArray()) {
			clazzName = ClassTypes.method869(clazz);
		}
		ClassInfo classInfo = (ClassInfo) this.classesTable.get(clazzName);
		if (clazz.isInterface()) {
			return;
		}
		if (classInfo == null) {
			classInfo = new ClassInfo(this, clazz.getName());
			this.classesTable.put(clazzName, classInfo);
		} else if (o == null) {
			return;
		}
		if (o != null) {
			try {
				if (this.instances.contains(o)) {
					return;
				}
			} catch (Exception ignored) {
			}
			++classInfo.instancesCount;
			classInfo.objs.add(new ObjInstance(this, s, o));
			this.instances.add(o);
			try {
				if (o instanceof Image) {
					this.images.add(o);
					if (Settings.recordReleasedImg && this.releasedImages.contains(o)) {
						this.releasedImages.removeElement(o);
					}
				} else if (o instanceof Sound || o instanceof AudioClip || o instanceof Player) {
					if (!PlayerImpl.players.contains(o))
						this.players.add(o);
				} else if (o instanceof Node) {
					this.m3gObjects.add(o);
				} else if (o instanceof Image2D) {
					IImage img = MemoryViewImage.createFromM3GImage((Image2D) o);
					if (img != null)
						this.images.add(new MemoryViewImage(img));
				} else if (o.getClass().getName().equals("com.mascotcapsule.micro3d.v3.Texture") && Emulator.getPlatform().supportsMascotCapsule()) {
					IImage img = MemoryViewImage.createFromMicro3DTexture(o);
					if (img != null)
						this.images.add(new MemoryViewImage(img));
				}
			} catch (NoClassDefFoundError ignored) {}
		}
		if (o != null && clazz.isArray()) {
			Class clazz2 = clazz;
			Class componentType;
			while ((componentType = clazz2.getComponentType()).getComponentType() != null) {
				clazz2 = componentType;
			}
			if (!ClassTypes.method871(componentType) && componentType != String.class) {
				return;
			}
			for (int i = 0; i < Array.getLength(o); ++i) {
				final Object value;
				if ((value = Array.get(o, i)) != null) {
					this.method847(value.getClass(), value, s + '[' + i + ']', true);
				}
			}
		} else {
			if (o instanceof Vector) {
				final Enumeration<Object> elements = (Enumeration<Object>) ((Vector) o).elements();
				while (elements.hasMoreElements()) {
					final Object nextElement;
					if ((nextElement = elements.nextElement()) != null) {
						this.method847(nextElement.getClass(), nextElement, s + "(VectorElement)", true);
					}
				}
				return;
			}
			if (o instanceof Hashtable) {
				final Enumeration<Object> keys = (Enumeration<Object>) ((Hashtable) o).keys();
				while (keys.hasMoreElements()) {
					final Object nextElement2 = keys.nextElement();
					final Object value2;
					if ((value2 = ((Hashtable) o).get(nextElement2)) != null) {
						this.method847(value2.getClass(), value2, s + "(HashtableKey=" + nextElement2 + ")", true);
					}
				}
				return;
			}
			try {
				if (o instanceof Object3D) {
					final Field[] method845 = fields(clazz);
					for (int j = 0; j < method845.length; ++j) {
						final String name = method845[j].getName();
						method845[j].setAccessible(true);
						final Object method846 = ClassTypes.getFieldValue(o, method845[j]);
						final String string = s + '.' + name;
						if (!method845[j].getType().isPrimitive() && method846 != null) {
							this.method847(method846.getClass(), method846, string, false);
						}
					}
					return;
				}
			} catch (NoClassDefFoundError ignored) {}
			if (Emulator.jarClasses.contains(clazz.getName()) || vector || checkClasses.contains(clazz.getName()) || InputStream.class.isAssignableFrom(clazz)) {
				final Field[] f = fields(clazz);
				for (int k = 0; k < f.length; ++k) {
					final String name2 = f[k].getName();
					//if ((o instanceof Item || o instanceof Screen) && f[k].getType() == int[].class) continue;
					if (!Modifier.isFinal(f[k].getModifiers()) || !f[k].getType().isPrimitive()) {
						f[k].setAccessible(true);
						final Object method848 = ClassTypes.getFieldValue(o, f[k]);
						final String string2 = s + '.' + name2;
						if (!f[k].getType().isPrimitive() && method848 != null) {
							this.method847(method848.getClass(), method848, string2, false);
						}
					}
				}
			}
		}
	}

	private synchronized void m3gReadTextures(Object obj) {
		if (obj == null) return;
		if (obj instanceof Group) {
			Group g = (Group) obj;

			for (int i = 0; i < g.getChildCount(); i++) {
				Node child = g.getChild(i);
				m3gReadTextures(child);
				Thread.yield();
			}
		} else if (obj instanceof Mesh) {
			Mesh mesh = (Mesh) obj;

			for (int i = 0; i < mesh.getSubmeshCount(); i++) {
				m3gReadTextures(mesh.getAppearance(i));
				Thread.yield();
			}

			if (obj instanceof SkinnedMesh) {
				m3gReadTextures(((SkinnedMesh) mesh).getSkeleton());
				Thread.yield();
			}
		} else if (obj instanceof Appearance) {
			Appearance ap = (Appearance) obj;

			for (int i = 0; ; i++) {
				Texture2D tex2d;
				try {
					tex2d = ap.getTexture(i);
					if (tex2d == null) break;
				} catch (IndexOutOfBoundsException e) {
					break;
				}
				m3gReadTextures(tex2d);
				Thread.yield();
			}
		} else if (obj instanceof Sprite3D) {
			m3gReadTextures(((Sprite3D) obj).getImage());
		} else if (obj instanceof Texture2D) {
			m3gReadTextures(((Texture2D) obj).getImage());
		} else if (obj instanceof Image2D) {
			Image2D img2d = (Image2D) obj;
			//use only after all objects are added to instances list!
			if (this.instances.contains(img2d)) return;
			this.instances.add(img2d);

			IImage img = MemoryViewImage.createFromM3GImage(img2d);
			if (img != null)
				this.images.add(new MemoryViewImage(img));
		}
	}

	private static Field[] fields(final Class clazz) {
		final Vector vector = new Vector<Field>();
		method849(clazz, vector);
		final Field[] array = new Field[vector.size()];
		for (int i = 0; i < array.length; ++i) {
			array[i] = (Field) vector.get(i);
		}
		return array;
	}

	private static void method849(final Class clazz, final Vector vector) {
		try {
			if (clazz.getSuperclass() != null) {
				method849(clazz.getSuperclass(), vector);
			}
			final Field[] declaredFields = clazz.getDeclaredFields();
			for (int i = 0; i < declaredFields.length; ++i) {
				vector.add(declaredFields[i]);
			}
		} catch (Error ignored) {
		}
	}

	public static int bytecodeSize() {
		int n = 0;
		try {
			if (Emulator.midletJar != null) {
				final ZipFile zipFile = new ZipFile(Emulator.midletJar);
				final Enumeration<String> elements = Emulator.jarClasses.elements();
				while (elements.hasMoreElements()) {
					final String s;
					try {
						if (!cls(s = elements.nextElement()).isInterface()) {
							n += (int) zipFile.getEntry(s.replace('.', '/') + ".class").getSize();
						}
					} catch (Throwable ignored) {
					}
				}
			} else {
				final Enumeration<String> elements2 = Emulator.jarClasses.elements();
				while (elements2.hasMoreElements()) {
					final String s2;
					if (!cls(s2 = elements2.nextElement()).isInterface()) {
						n += (int) Emulator.getFileFromClassPath(s2.replace('.', '/') + ".class").length();
					}
				}
			}
		} catch (Exception ignored) {
		}
		return n;
	}

	public final int objectsSize() {
		int n = 0;
		final Enumeration<ClassInfo> elements = this.classesTable.elements();
		while (elements.hasMoreElements()) {
			n += elements.nextElement().size();
		}
		return n;
	}

	public static String playerType(final Object o) {
		if (o instanceof Sound) {
			return ((Sound) o).getType();
		}
		if (o instanceof AudioClip) {
			return "MMF";
		}
		return ((Player) o).getContentType();
	}

	public static String playerStateStr(final Object o) {
		if (o instanceof Sound) {
			switch (((Sound) o).getState()) {
				case 0: {
					return "SOUND_PLAYING";
				}
				case 1: {
					return "SOUND_STOPPED";
				}
				case 3: {
					return "SOUND_UNINITIALIZED";
				}
				default: {
					return "INVALID STATE";
				}
			}
		} else if (o instanceof AudioClip) {
			switch (((AudioClip) o).getStatus()) {
				case 1: {
					return "SOUND_PLAY";
				}
				case 2: {
					return "SOUND_PAUSE";
				}
				case 0: {
					return "SOUND_STOP";
				}
				default: {
					return "INVALID STATE";
				}
			}
		} else {
			switch (((Player) o).getState()) {
				case 0: {
					return "CLOSED";
				}
				case 300: {
					return "PREFETCHED";
				}
				case 200: {
					return "REALIZED";
				}
				case 400: {
					return "STARTED";
				}
				case 100: {
					return "UNREALIZED";
				}
				default: {
					return "INVALID STATE";
				}
			}
		}
	}

	public static int loopCount(final Object o) {
		if (o instanceof Sound && ((Sound) o).m_player instanceof PlayerImpl) {
			return ((PlayerImpl) ((Sound) o).m_player).loopCount;
		}
		if (o instanceof AudioClip) {
			return ((AudioClip) o).loopCount;
		}
		if (!(o instanceof PlayerImpl)) {
			return 0;
		}
		return ((PlayerImpl) o).loopCount;
	}

	public static int durationMs(final Object o) {
		if (o instanceof PlayerImpl) {
			long dur = ((PlayerImpl) o).getDuration();
			if (dur < 0) return -1;
			return (int) (dur / 1000);
		}
		return -1; //TODO
	}

	public static int dataLen(final Object o) {
		if (o instanceof Sound) {
			return ((Sound) o).dataLen;
		}
		if (o instanceof AudioClip) {
			return ((AudioClip) o).dataLen;
		}
		if (o instanceof VLCPlayerImpl) {
			return ((VLCPlayerImpl) o).dataLen;
		}
		if (!(o instanceof PlayerImpl)) {
			return 0;
		}
		return ((PlayerImpl) o).dataLen;
	}

	public static int progress(final Object o) {
		try {
			if (o instanceof Sound) {
				final Sound sound = (Sound) o;
				return (int) (sound.m_player.getMediaTime() * 100L / (sound.m_player.getDuration() / 1000L));
			}
			if (o instanceof AudioClip) {
				return 0;
			}

			if (o instanceof VLCPlayerImpl) {
				final VLCPlayerImpl v = (VLCPlayerImpl) o;
				return (int) (((double) v.getMediaTime() / (double) v.getDuration()) * 100D);
			}
			if (!(o instanceof PlayerImpl)) {
				return 0;
			}
			final PlayerImpl playerImpl = (PlayerImpl) o;
			return (int) (((double) playerImpl.getMediaTime() / (double) playerImpl.getDuration()) * 100D);
		} catch (Exception ex) {
			return 0;
		}
	}

	public static int volume(final Object o) {
		try {
			if (o instanceof Sound) {
				return ((Sound) o).getGain();
			}
			if (o instanceof AudioClip) {
				return ((AudioClip) o).volume * 20;
			}

			if (o instanceof VLCPlayerImpl) {
				return ((VolumeControlImpl) ((VLCPlayerImpl) o).getControl("VolumeControl")).getLevel();
			}
			if (!(o instanceof PlayerImpl)) {
				return 0;
			}
			return ((VolumeControlImpl) ((PlayerImpl) o).getControl("VolumeControl")).getLevel();
		} catch (Exception ex) {
			return 0;
		}
	}

	public static void setVolume(final Object o, final int n) {
		try {
			if (o instanceof Sound) {
				((Sound) o).setGain(n);
			} else if (!(o instanceof AudioClip)) {

				if (o instanceof VLCPlayerImpl) {
					((VolumeControlImpl) ((VLCPlayerImpl) o).getControl("VolumeControl")).setLevel(n);
					return;
				}
				if (!(o instanceof PlayerImpl)) {
					return;
				}
				((VolumeControlImpl) ((PlayerImpl) o).getControl("VolumeControl")).setLevel(n);
			}
		} catch (Exception ignored) {
		}
	}

	public static void playerAct(final Object o, final PlayerActionType n) {
		if (o instanceof Sound) {
			final Sound sound = (Sound) o;
			try {
				switch (n) {
					case resume: {
						sound.resume();
						break;
					}
					case pause: {
						final long mediaTime = sound.m_player.getMediaTime();
						sound.stop();
						sound.m_player.setMediaTime(mediaTime);
						break;
					}
					case stop: {
						sound.stop();
						break;
					}
					case export: {
						try {
							byte[] b = sound.getData();
							String s = sound.getExportName();
							if (b != null) {
								exportAudio(b, sound.getExportName());
								((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Saved: " + s);
							} else {
								((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export failed: unsupported stream type");
							}
						} catch (Exception e) {
							((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export failed: " + e.toString());
						}
						break;
					}
				}
			} catch (Exception ignored) {
			}
			return;
		}
		if (o instanceof AudioClip) {
			final AudioClip audioClip = (AudioClip) o;
			switch (n) {
				case resume: {
					audioClip.play(audioClip.loopCount, audioClip.volume);
					break;
				}
				case pause: {
					audioClip.pause();
					break;
				}
				case stop: {
					audioClip.stop();
					break;
				}
				case export: {
					try {
						byte[] b = audioClip.getData();
						String s = audioClip.getExportName();
						if (b != null) {
							exportAudio(b, s);
							((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Saved: " + s);
						} else {
							((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export failed: unsupported stream type");
						}
					} catch (Exception e) {
						((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export failed: " + e.toString());
					}
					break;
				}
			}
			return;
		}
		if (o instanceof VLCPlayerImpl) {
			final VLCPlayerImpl v = (VLCPlayerImpl) o;
			try {
				switch (n) {
					case resume: {
						v.start();
						break;
					}
					case pause: {
						final long mediaTime2 = v.getMediaTime();
						v.stop();
						v.setMediaTime(mediaTime2);
					}
					case stop: {
						v.stop();
						break;
					}
					case export: {
						((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export not supported!");
						break;
					}
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			return;
		}
		if (!(o instanceof PlayerImpl)) {
			return;
		}
		final PlayerImpl playerImpl = (PlayerImpl) o;
		try {
			switch (n) {
				case resume: {
					playerImpl.start();
					break;
				}
				case pause: {
					final long mediaTime2 = playerImpl.getMediaTime();
					playerImpl.stop();
					playerImpl.setMediaTime(mediaTime2);
					break;
				}
				case stop: {
					playerImpl.stop();
					break;
				}
				case export: {
					try {
						byte[] b = playerImpl.getData();
						String s = "audio" + playerImpl.getExportName();
						if (b != null) {
							exportAudio(b, s);
							((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Saved: " + s);
						} else {
							((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export failed: unsupported stream type");
						}
					} catch (Exception e) {
						((EmulatorScreen) Emulator.getEmulator().getScreen()).showMessage("Export failed: " + e.toString());
					}
				}
			}
		} catch (Exception ignored) {
		}
	}

	public static void exportAudio(byte[] b, String name) throws IOException {
		File f = new File(Emulator.getUserPath() + "/" + name);
		if (f.exists()) return;
		f.createNewFile();
		DataOutputStream o = new DataOutputStream(new FileOutputStream(f));
		o.write(b);
		o.close();
	}

	public final int method866(final Object o) {
		try {
			return ((ClassInfo) this.classesTable.get(o)).instancesCount;
		} catch (NullPointerException ignored) {
		}
		return 0;
	}

	public final int method867(final Object o) {
		try {
			return ((ClassInfo) this.classesTable.get(o)).size();
		} catch (NullPointerException ignored) {
		}
		return 0;
	}

	public final Vector objs(final Object o) {
		return ((ClassInfo) this.classesTable.get(o)).objs;
	}

	public static String refs(final Object o) {
		return ((ObjInstance) o).ref;
	}

	public static Object val(final Object o) {
		return ((ObjInstance) o).val;
	}

	public static int size(final Object o) {
		return ((ObjInstance) o).size;
	}

	public final int size(Class c, Object o, String s) {
		return size(c, o);
	}

	public final int size(final Class cls, final Object o) {
		final Field[] fields = fields(cls);
		int res = 0;
		for (int i = 0; i < fields.length; ++i) {
			final Field field;
			final Class<?> type = (field = fields[i]).getType();
			if ((!Modifier.isFinal(field.getModifiers()) || !type.isPrimitive()) && (!Modifier.isStatic(field.getModifiers()) || o == null)) {
				if (Modifier.isStatic(field.getModifiers()) || o != null) {
					if (type == Long.TYPE || type == Double.TYPE) {
						res += 24;
					} else {
						res += 16;
					}
				}
			}
		}
		if (o != null) {
			if (cls.isArray()) {
				res += this.arraySize(cls, o);
			} else {
				res += 12;
				if (cls == String.class) {
					res += 2 + ((String) o).length();
				} else {
					if (cls == Image.class) {
						final Image image = (Image) o;
						res += image.size();
					} else {
						try {
							if (cls == Image2D.class) {
								final Image2D image2D = (Image2D) o;
								res += image2D.size();
							}
						} catch (NoClassDefFoundError ignored) {}
                        /*if (!(cls == Vector.class || cls == Hashtable.class
                                || cls == StringItem.class || cls == Command.class
                                || cls == cls("javax.microedition.lcdui.a")
                                ))
                            return res + o.toString().length();
                        else */
						return res;
					}
				}
			}
		}
		return res;
	}

	private int arraySize(final Class clazz, final Object o) {
		int n = 0;
		n += 16;
		if (clazz == ((Memory._J != null) ? Memory._J : (Memory._J = cls("[J")))) {
			n = 16 + 8 * Array.getLength(o);
		} else if (clazz == ((Memory._I != null) ? Memory._I : (Memory._I = cls("[I")))) {
			n = 16 + 4 * Array.getLength(o);
		} else if (clazz == ((Memory._S != null) ? Memory._S : (Memory._S = cls("[S")))) {
			n = 16 + 2 * Array.getLength(o);
		} else if (clazz == ((Memory._B != null) ? Memory._B : (Memory._B = cls("[B")))) {
			n = 16 + 1 * Array.getLength(o);
		} else if (clazz == ((Memory._Z != null) ? Memory._Z : (Memory._Z = cls("[Z")))) {
			n = 16 + 4 * Array.getLength(o);
		} else if (clazz == ((Memory._D != null) ? Memory._D : (Memory._D = cls("[D")))) {
			n = 16 + 8 * Array.getLength(o);
		} else if (clazz == ((Memory._F != null) ? Memory._F : (Memory._F = cls("[F")))) {
			n = 16 + 4 * Array.getLength(o);
		} else if (clazz == ((Memory._C != null) ? Memory._C : (Memory._C = cls("[C")))) {
			n = 16 + 1 * Array.getLength(o);
		} else {
			for (int i = Array.getLength(o) - 1; i >= 0; --i) {
				final Object value;
				if ((value = Array.get(o, i)) != null && !ClassTypes.method871(clazz.getComponentType())) {
					n += this.size(value.getClass(), value);
				} else if (value != null && value.getClass().isArray()) {
					n += 16;
				} else {
					n += 4;
				}
			}
		}
		return n;
	}

	static Class cls(final String s) {
		Class<?> forName;
		try {
			forName = Class.forName(s, false, Emulator.getCustomClassLoader());
		} catch (ClassNotFoundException ex2) {
			throw new NoClassDefFoundError(ex2.getMessage());
		}
		return forName;
	}

	private final class ObjInstance {
		String ref;
		Object val;
		int size;

		ObjInstance(final Memory a, final String s, final Object o) {
			super();
			this.ref = s;
			this.val = o;
			this.size = a.size(o.getClass(), o, s);
		}
	}

	private final class ClassInfo implements Comparable {
		String s;
		int instancesCount;
		int anInt1487;
		Vector objs;

		public final int size() {
			int anInt1487 = this.anInt1487;
			for (int i = this.objs.size() - 1; i >= 0; --i) {
				anInt1487 += ((ObjInstance) this.objs.get(i)).size;
			}
			return anInt1487;
		}

		public final int compareTo(final Object o) {
			return this.s.compareTo(((ClassInfo) o).s);
		}

		ClassInfo(final Memory m, final String aString1484) {
			super();
			this.objs = new Vector();
			this.instancesCount = 0;
			this.anInt1487 = m.size(cls(aString1484), null);
			this.s = aString1484;
		}
	}
}
