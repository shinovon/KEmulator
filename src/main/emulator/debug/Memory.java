package emulator.debug;

import com.nokia.mid.sound.Sound;
import com.samsung.util.AudioClip;
import emulator.Emulator;
import emulator.Settings;
import emulator.graphics2D.IImage;
import org.apache.tools.zip.ZipFile;

import javax.microedition.lcdui.Image;
import javax.microedition.m3g.*;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerImpl;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class Memory {

	public Hashtable<String, ClassInfo> classesTable = new Hashtable<>();
	public Vector instances;
	public Vector<Image> images = new Vector<>();
	public Vector<Image> releasedImages = new Vector<>();
	public Vector players = new Vector();
	public Vector m3gObjects = new Vector();
	private final Vector<String> checkClasses = new Vector<>();
	static Class _J;
	static Class _I;
	static Class _S;
	static Class _B;
	static Class _Z;
	static Class _F;
	static Class _D;
	static Class _C;

	public static final Object m3gLock = new Object();

	private static Memory inst;

	private static int bytecodeSize = -1;

	public static Memory getInstance() {
		if (inst == null) {
			inst = new Memory();
		}
		return inst;
	}

	private Memory() {
		super();
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

	public synchronized void updateEverything() {
		// copy still alive images
		if (Settings.recordReleasedImg) {
			for (Image image : images) {
				if (!releasedImages.contains(image)) {
					releasedImages.addElement(image);
				}
			}
		}

		// clears
		classesTable.clear();
		instances.clear();
		images.clear();
		m3gObjects.clear();

		// players
		try {
			players.clear(); // here go mmapi ones. Others will be collected from heap later.
			players.addAll(PlayerImpl.players);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// iterate global roots
		for (final String s : Emulator.jarClasses) {
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
				} catch (Throwable e) {
					e.printStackTrace();
				}
				o = null;
			}
			if (cls != null)
				collectObjects(cls, o, new ReferencePath(s, o != null), false);
		}

		// iterate via lcdui static roots
		for (String checkClass : checkClasses) {
			Class cls = null;
			try {
				cls = cls(checkClass);
			} catch (Throwable ignored) {
			}
			if (cls != null)
				collectObjects(cls, null, new ReferencePath(checkClass, true), false);
		}

		if (m3gObjects.size() == 0) return;

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
		} catch (Exception ignored) {
		}
	}

	private void collectObjects(final Class clazz, final Object o, final ReferencePath path, boolean vector) {
		if (clazz.isInterface())
			return;

		String clazzName = clazz.getName();
		if (clazz.isArray()) {
			clazzName = ClassTypes.getReadableClassName(clazz);
		}


		ClassInfo classInfo = this.classesTable.get(clazzName);
		if (classInfo == null) {
			classInfo = new ClassInfo(this, clazz);
			this.classesTable.put(clazzName, classInfo);
		} else if (o == null) {
			// In the end of this method there is a pass over static fields.
			// We want it to fire only once. This return will trigger on 2nd+ pass over null object.
			return;
		}

		if (o != null) {

			if (this.instances.contains(o)) {
				for (ObjInstance obj : classInfo.objs) {
					if (obj.value == o) {
						if (!obj.paths.contains(path))
							obj.paths.add(path);
					}
				}
				return;
			}

			++classInfo.instancesCount;
			classInfo.objs.add(new ObjInstance(this, path, o));
			instances.add(o);
			try {
				if (o instanceof Image) {
					this.images.add((Image) o);
					if (Settings.recordReleasedImg && this.releasedImages.contains(o)) {
						this.releasedImages.removeElement(o); // this image is still alive
					}
				} else if (o instanceof Sound || o instanceof AudioClip || o instanceof Player) {
					if (!players.contains(o))
						players.add(o);
				} else if (o instanceof Node) {
					this.m3gObjects.add(o);
				} else if (o instanceof Image2D) {
					IImage img = MemoryViewImage.createFromM3GImage((Image2D) o);
					if (img != null) {
						MemoryViewImage mvi = new MemoryViewImage(img, MemoryViewImageType.M3G, o);
						this.images.add(mvi);
						int i = releasedImages.indexOf(mvi);
						if (i >= 0) {
							this.releasedImages.remove(i); // this image is still alive
						}
					}
				} else if (o.getClass().getName().equals("com.mascotcapsule.micro3d.v3.Texture") && Emulator.getPlatform().supportsMascotCapsule()) {
					IImage img = MemoryViewImage.createFromMicro3DTexture(o);
					if (img != null) {
						MemoryViewImage mvi = new MemoryViewImage(img, MemoryViewImageType.Micro3D, o);
						this.images.add(mvi);
						int i = releasedImages.indexOf(mvi);
						if (i >= 0) {
							this.releasedImages.remove(i); // this image is still alive
						}
					}
				}
			} catch (NoClassDefFoundError e) {
				e.printStackTrace();
			}

			if (clazz.isArray()) {
				Class clazz2 = clazz;
				Class componentType;
				while ((componentType = clazz2.getComponentType()).getComponentType() != null) {
					clazz2 = componentType;
				}
				if (!ClassTypes.isObject(componentType) && componentType != String.class) {
					return;
				}
				for (int i = 0; i < Array.getLength(o); ++i) {
					final Object value;
					if ((value = Array.get(o, i)) != null) {
						this.collectObjects(value.getClass(), value, path.append(value, i), true);
					}
				}
			} else if (o instanceof Vector) {
				final Enumeration<Object> elements = (Enumeration<Object>) ((Vector) o).elements();
				int index = 0;
				while (elements.hasMoreElements()) {
					final Object nextElement = elements.nextElement();
					if (nextElement != null) {
						this.collectObjects(nextElement.getClass(), nextElement, path.append(nextElement, index), true);
					}
					index++;
				}
				return;
			}
			if (o instanceof Hashtable) {
				Hashtable h = (Hashtable) o;
				final Enumeration<Object> keys = h.keys();
				while (keys.hasMoreElements()) {
					final Object key = keys.nextElement();
					final Object val = h.get(key);
					if (val != null) {
						this.collectObjects(val.getClass(), val, path.append(val, key.toString(), true), true);
					}
				}
				return;
			}
			try {
				if (o instanceof Object3D) {
					iterateFields(clazz, o, path);
					return;
				}
			} catch (NoClassDefFoundError e) {
				e.printStackTrace();
			}
		}

		if (Emulator.jarClasses.contains(clazz.getName()) || vector || checkClasses.contains(clazz.getName()) || InputStream.class.isAssignableFrom(clazz)) {
			iterateFields(clazz, o, path);
		}
	}

	private void iterateFields(Class clazz, Object o, ReferencePath path) {
		final Field[] fields = fields(clazz);
		for (Field f : fields) {
			if (Modifier.isFinal(f.getModifiers()) && f.getType().isPrimitive())
				continue; // const field

			final String fieldName = f.getName();
			f.setAccessible(true);

			final Object value = ClassTypes.getFieldValue(o, f);
			final ReferencePath newPath;
			if (Modifier.isStatic(f.getModifiers()))
				newPath = new ReferencePath(clazz.getName(), true).append(value, fieldName, false);
			else
				newPath = path.append(value, fieldName, false);
			if (!f.getType().isPrimitive() && value != null) {
				this.collectObjects(value.getClass(), value, newPath, false);
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
			if (img != null) {
				MemoryViewImage mvi = new MemoryViewImage(img, MemoryViewImageType.M3G, img2d);
				this.images.add(mvi);
				int i = releasedImages.indexOf(mvi);
				if (i >= 0) {
					this.releasedImages.remove(i); // this image is still alive
				}
			}
		}
	}

	private static Field[] fields(final Class clazz) {
		final Vector<Field> vector = new Vector<>();
		addFieldsWithSupers(clazz, vector);
		final Field[] array = new Field[vector.size()];
		return vector.toArray(array);
	}

	private static void addFieldsWithSupers(final Class clazz, final Vector<Field> vector) {
		try {
			if (clazz.getSuperclass() != null) {
				addFieldsWithSupers(clazz.getSuperclass(), vector);
			}
			Collections.addAll(vector, clazz.getDeclaredFields());
		} catch (Error ignored) {
		}
	}

	public static int getBytecodeSize() {
		if (bytecodeSize >= 0)
			return bytecodeSize;
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
		bytecodeSize = n;
		return n;
	}

	public int objectsSize() {
		int n = 0;
		final Enumeration<ClassInfo> elements = this.classesTable.elements();
		while (elements.hasMoreElements()) {
			n += elements.nextElement().size();
		}
		return n;
	}

	public final int instancesCount(final String o) {
		try {
			return this.classesTable.get(o).instancesCount;
		} catch (NullPointerException ignored) {
		}
		return 0;
	}

	public final int totalObjectsSize(final String o) {
		try {
			return this.classesTable.get(o).size();
		} catch (NullPointerException ignored) {
		}
		return 0;
	}

	public final Vector<ObjInstance> objs(final String o) {
		return this.classesTable.get(o).objs;
	}


	public final int size(final Class cls, final Object o) {
		final Field[] fields = fields(cls);
		int res = 0;

		// fields
		for (Field field : fields) {
			final Class type = field.getType();
			if ((Modifier.isFinal(field.getModifiers()) && type.isPrimitive()))
				continue; // constant primitive field

			if (!Modifier.isStatic(field.getModifiers()) || o == null) {
				if (Modifier.isStatic(field.getModifiers()) || o != null) {
					if (type == Long.TYPE || type == Double.TYPE) {
						res += 24;
					} else {
						res += 16;
					}
				}
			}
		}

		if (o == null)
			return res;

		if (cls.isArray()) {
			return res + this.arraySize(cls, o);
		}

		res += 12;

		if (cls == String.class) {
			res += 2 + ((String) o).length();
			return res;
		}

		if (cls == Image.class) {
			final Image image = (Image) o;
			res += image.size();
		} else {
			try {
				if (cls == Image2D.class) {
					final Image2D image2D = (Image2D) o;
					res += image2D.size();
				}
			} catch (NoClassDefFoundError ignored) {
			}
			return res;
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
				if ((value = Array.get(o, i)) != null && !ClassTypes.isObject(clazz.getComponentType())) {
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

	/**
	 * Gets {@link Class} by name.
	 *
	 * @param s Class' name.
	 * @return Class object.
	 */
	public static Class cls(final String s) {
		Class<?> forName;
		try {
			forName = Class.forName(s, false, Emulator.getCustomClassLoader());
		} catch (ClassNotFoundException ex2) {
			throw new NoClassDefFoundError(ex2.getMessage());
		}
		return forName;
	}
}