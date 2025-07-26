package emulator.graphics3D.m3g;

import emulator.Emulator;
import emulator.custom.ResourceManager;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.Inflater;

public final class M3GLoader {
	private final static int TEXTURE = 2;
	private final static int M3G = 1;
	// «JSR184»
	static final byte[] M3G_MAGIC_NUMBER = new byte[]{-85, 74, 83, 82, 49, 56, 52, -69, 13, 10, 26, 10};
	static final byte[] PNG_MAGIC_NUMBER = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
	private static final Boolean aBoolean1081 = new Boolean(false);
	private static final Boolean aBoolean1087 = new Boolean(true);
	private Vector aVector1082 = new Vector();
	private Vector aVector1088 = new Vector();
	private Vector aVector1091 = new Vector();
	private Vector aVector1092 = null;
	private String filePath;
	private String aString1089;
	private int currentSection;
	private boolean aBoolean1085;
	private boolean aBoolean1090;

	public static Object3D[] load(String var0) throws IOException {
		if (var0 == null) {
			throw new NullPointerException();
		} else {
			try {
				return (new M3GLoader()).method746(var0);
			} catch (SecurityException var2) {
				throw var2;
			} catch (IOException var3) {
				throw var3;
			} catch (Exception var4) {
				throw new IOException(var4);
			}
		}
	}

	public static Object3D[] load(byte[] var0, int var1) throws IOException {
		if (var0 == null) {
			throw new NullPointerException();
		} else {
			try {
				return (new M3GLoader()).method747(var0, var1);
			} catch (SecurityException var3) {
				throw var3;
			} catch (IOException var4) {
				throw var4;
			} catch (Exception var5) {
				throw new IOException(var5);
			}
		}
	}

	private M3GLoader() {
	}

	private M3GLoader(Vector var1, String var2) {
		this.aString1089 = var2;
		this.aVector1091 = var1;
	}

	private Object3D[] method746(String var1) throws IOException {
		if (var1 == null) {
			throw new NullPointerException();
		} else if (this.method715(var1)) {
			throw new IOException("Reference loop detected.");
		} else {
			this.filePath = var1;
			this.aVector1091.addElement(var1);
			PeekInputStream var2;
			int var3 = getFileType(var2 = new PeekInputStream(this.method752(var1), 12));
			var2.rewind();
			Object3D[] var4 = this.method696(var2, var3);
			this.aVector1091.removeElement(var1);
			return var4;
		}
	}

	private Object3D[] method747(byte[] var1, int var2) throws IOException {
		if (var1 == null) {
			throw new NullPointerException("Resource byte array is null.");
		} else {
			int var3 = getInnerFileType(var1, var2);
			ByteArrayInputStream var4 = new ByteArrayInputStream(var1, var2, var1.length - var2);
			return this.method696(var4, var3);
		}
	}

	private Object3D[] method696(InputStream var1, int var2) throws IOException {
		if (var2 == 1) {
			return this.method748(var1);
		} else if (var2 == 2) {
			return method707(var1);
		} else {
			throw new IOException("File not recognized.");
		}
	}

	private static Object3D[] method707(InputStream var0) throws IOException {
		return new Object3D[]{new Image2D(100, Image.createImage(var0))};
	}

	private Object3D[] method748(InputStream paramInputStream)
			throws IOException {
		paramInputStream.skip(M3G_MAGIC_NUMBER.length);
		while (method718(paramInputStream)) {
			this.currentSection += 1;
		}
		return method743();
	}

	private boolean method718(InputStream var1) throws IOException {
		if (this.currentSection > 1 && this.aBoolean1090 && !this.aBoolean1085) {
			throw new IOException("No external sections (" + this.filePath + ").");
		} else {
			AdlerInputStream ais;
			int compression;
			if ((compression = readByte(ais = new AdlerInputStream(var1))) == -1) {
				return false;
			} else if (this.currentSection == 0 && compression != 0) {
				throw new IOException("Compressed header (" + this.filePath + ").");
			} else {
				long var5 = readUInt32LE(ais);
				long var7 = readUInt32LE(ais);
				Object var9 = null;
				if (compression == 0) {
					var9 = ais;
					if (var7 != var5 - 13L) {
						Emulator.getEmulator().getLogStream().println("M3GLoader: Section length mismatch!!!");
						return false;
					}
				} else {
					if (compression != 1) {
						Emulator.getEmulator().getLogStream().println("M3GLoader: Unrecognized compression scheme(" + compression + ")!!!");
						return false;
					}

					byte[] var10 = new byte[(int) var5 - 13];
					ais.read(var10);
					byte[] var11 = new byte[(int) var7];
					inflate(var10, var11);
					var9 = new CountedInputStream(new ByteArrayInputStream(var11));
				}

				((CountedInputStream) var9).resetCounter();

				while ((long) ((CountedInputStream) var9).getCounter() < var7) {
					this.method722(this.method719((CountedInputStream) var9));
				}

				if ((long) ((CountedInputStream) var9).getCounter() != var7) {
					Emulator.getEmulator().getLogStream().println("M3GLoader: Section length mismatch!!!");
					return false;
				} else {
					long var12 = ais.getChecksum();
					long var14 = readUInt32LE(ais);
					if (var12 != var14) {
						throw new IOException("Checksum is wrong (" + this.filePath + ").");
					} else {
						return true;
					}
				}
			}
		}
	}

	private Object3D method719(CountedInputStream var1) throws IOException {
		int var2 = readByte(var1);
		long var3 = readUInt32LE(var1);
		long var5 = (long) var1.getCounter() + var3;
		Object object = null;
		switch (var2) {
			case 0:
				if (this.currentSection != 0) {
					throw new IOException("Header in wrong section (" + this.filePath + ").");
				}

				this.method744(var1);
				break;
			case 1:
				object = this.readAnimationController(var1);
				break;
			case 2:
				object = this.readAnimationTrack(var1);
				break;
			case 3:
				object = this.readAppearance(var1);
				break;
			case 4:
				object = this.readBackground(var1);
				break;
			case 5:
				object = this.readCamera(var1);
				break;
			case 6:
				object = this.readCompositingMode(var1);
				break;
			case 7:
				object = this.readFog(var1);
				break;
			case 8:
				object = this.readPolygonMode(var1);
				break;
			case 9:
				object = this.readGroup(var1);
				break;
			case 10:
				object = this.readImage2D(var1);
				break;
			case 11:
				object = this.readTriangleStripArray(var1);
				break;
			case 12:
				object = this.readLight(var1);
				break;
			case 13:
				object = this.readMaterial(var1);
				break;
			case 14:
				object = this.readMesh(var1);
				break;
			case 15:
				object = this.readMorphingMesh(var1);
				break;
			case 16:
				object = this.readSkinnedMesh(var1);
				break;
			case 17:
				object = this.readTexture2D(var1);
				break;
			case 18:
				object = this.readSprite(var1);
				break;
			case 19:
				object = this.readKeyframeSequence(var1);
				break;
			case 20:
				object = this.readVertexArray(var1);
				break;
			case 21:
				object = this.readVertexBuffer(var1);
				break;
			case 22:
				object = this.readWorld(var1);
				break;
			case 255:
				if (this.currentSection != 1) {
					throw new IOException("External reference in wrong section (" + this.filePath + ").");
				}

				if (!this.aBoolean1090) {
					throw new IOException("External links in self contained file (" + this.filePath + ").");
				}

				String var8 = method710(var1);
				this.aBoolean1085 = true;
				object = (new M3GLoader(this.aVector1091, this.filePath)).method746(var8)[0];
				break;
			default:
				throw new IOException("Unrecognized object type " + var2 + " (" + this.filePath + ").");
		}

		if (var5 != (long) var1.getCounter()) {
			throw new IOException("Object length mismatch (" + this.filePath + ").");
		} else {
			this.method749((Object3D) object);
			return (Object3D) object;
		}
	}

	private void method722(Object3D var1) {
		if (var1 != null) {
			this.aVector1082.addElement(var1);
			this.aVector1088.addElement(aBoolean1081);
		}

	}

	private Object3D method726(int var1) {
		if (var1 == 0) {
			return null;
		} else if (var1 >= 2 && var1 - 2 < this.aVector1082.size()) {
			this.aVector1088.setElementAt(aBoolean1087, var1 - 2);
			return (Object3D) this.aVector1082.elementAt(var1 - 2);
		} else {
			throw new IllegalArgumentException("Invalid reference index (" + this.filePath + ").");
		}
	}

	private Object3D[] method743() {
		Vector var1 = new Vector();

		for (int var2 = 0; var2 < this.aVector1082.size(); ++var2) {
			if (this.aVector1088.elementAt(var2) == aBoolean1081) {
				var1.addElement(this.aVector1082.elementAt(var2));
			}
		}

		Object3D[] var4 = new Object3D[var1.size()];

		for (int var3 = 0; var3 < var1.size(); ++var3) {
			var4[var3] = (Object3D) var1.elementAt(var3);
		}

		return var4;
	}

	private void method744(InputStream paramInputStream)
			throws IOException {
		byte[] arrayOfByte = new byte[2];
		paramInputStream.read(arrayOfByte);
		this.aBoolean1090 = readBit(paramInputStream);
		readUInt32LE(paramInputStream);
		readUInt32LE(paramInputStream);
		if ((arrayOfByte[0] != 1) || (arrayOfByte[1] != 0)) {
			throw new IOException("Invalid file version (" + this.filePath + ").");
		}
		method710(paramInputStream);
	}

	private void method736(Object3D var1, InputStream var2) throws IOException {
		var1.setUserID( (int)readUInt32LE(var2) );
		long var3 = readUInt32LE(var2);
		this.aVector1092 = new Vector();

		while (var3-- > 0L) {
			AnimationTrack var5;
			if ((var5 = (AnimationTrack) this.method728(var2)) == null) {
				throw new NullPointerException();
			}

			this.aVector1092.addElement(var5);
		}

		long var6 = readUInt32LE(var2);

		if (var6 != 0) {
			Hashtable var10 = new Hashtable();
			while (var6-- > 0L) {
				int var8 = (int) readUInt32LE(var2);
				byte[] var9 = new byte[(int) readUInt32LE(var2)];
				var2.read(var9);
				var10.put(new Integer(var8), var9);
			}

			var1.setUserObject(var10);
		}
	}

	private void method737(Transformable var1, InputStream var2) throws IOException {
		this.method736(var1, var2);
		if (readBit(var2)) {
			var1.setTranslation(readFloat32LE(var2), readFloat32LE(var2), readFloat32LE(var2));
			var1.setScale(readFloat32LE(var2), readFloat32LE(var2), readFloat32LE(var2));
			var1.setOrientation(readFloat32LE(var2), readFloat32LE(var2), readFloat32LE(var2), readFloat32LE(var2));
		}

		if (readBit(var2)) {
			var1.setTransform(method732(var2));
		}

	}

	private void method745(Node var1, InputStream var2) throws IOException {
		this.method737(var1, var2);
		var1.setRenderingEnable(readBit(var2));
		var1.setPickingEnable(readBit(var2));
		var1.setAlphaFactor((float) readByte(var2) / 255.0F);
		var1.setScope((int) readUInt32LE(var2));
		if (readBit(var2)) {
			int var3 = readByte(var2);
			int var4 = readByte(var2);
			int var5 = (int) readUInt32LE(var2);
			int var6 = (int) readUInt32LE(var2);
			var1.setAlignment((Node) this.method726(var5), var3, (Node) this.method726(var6), var4);
		}

	}

	private void method716(Group var1, InputStream var2) throws IOException {
		this.method745(var1, var2);
		int var3 = (int) readUInt32LE(var2);

		while (var3-- > 0) {
			var1.addChild((Node) this.method728(var2));
		}

	}

	private void method749(Object3D var1) {
		if (this.aVector1092 != null && var1 != null) {
			for (int var2 = 0; var2 < this.aVector1092.size(); ++var2) {
				var1.addAnimationTrack((AnimationTrack) this.aVector1092.elementAt(var2));
			}

			this.aVector1092 = null;
		}

	}

	private AnimationController readAnimationController(InputStream var1) throws IOException {
		AnimationController var2 = new AnimationController();
		this.method736(var2, var1);
		float var3 = readFloat32LE(var1);
		float var4 = readFloat32LE(var1);
		var2.setActiveInterval(readInt32LE(var1), readInt32LE(var1));
		float var5 = readFloat32LE(var1);
		int var6 = readInt32LE(var1);
		var2.setPosition(var5, var6);
		var2.setSpeed(var3, var6);
		var2.setWeight(var4);
		return var2;
	}

	private AnimationTrack readAnimationTrack(InputStream var1) throws IOException {
		AnimationController var2 = new AnimationController();
		this.method736(var2, var1);
		KeyframeSequence var3 = (KeyframeSequence) this.method728(var1);
		AnimationController var4 = (AnimationController) this.method728(var1);
		int var5 = (int) readUInt32LE(var1);
		AnimationTrack var6 = new AnimationTrack(var3, var5);
		copyObject3D(var2, var6);
		var6.setController(var4);
		return var6;
	}

	private Appearance readAppearance(InputStream var1) throws IOException {
		Appearance var2 = new Appearance();
		this.method736(var2, var1);
		var2.setLayer(readByte(var1));
		var2.setCompositingMode((CompositingMode) this.method728(var1));
		var2.setFog((Fog) this.method728(var1));
		var2.setPolygonMode((PolygonMode) this.method728(var1));
		var2.setMaterial((Material) this.method728(var1));
		int var3 = (int) readUInt32LE(var1);

		for (int var4 = 0; var4 < var3; ++var4) {
			Texture2D var5;
			if ((var5 = (Texture2D) this.method728(var1)) == null) {
				throw new IOException("Null texture reference");
			}

			var2.setTexture(var4, var5);
		}

		return var2;
	}

	private Background readBackground(InputStream var1) throws IOException {
		Background var2 = new Background();
		this.method736(var2, var1);
		var2.setColor(readARGB(var1));
		Image2D var3 = (Image2D) this.method728(var1);
		var2.setImage(var3);
		var2.setImageMode(readByte(var1), readByte(var1));
		var2.setCrop(readInt32LE(var1), readInt32LE(var1), readInt32LE(var1), readInt32LE(var1));
		var2.setDepthClearEnable(readBit(var1));
		var2.setColorClearEnable(readBit(var1));
		return var2;
	}

	private Camera readCamera(InputStream var1) throws IOException {
		Camera var2 = new Camera();
		this.method745(var2, var1);
		int var3;
		if ((var3 = readByte(var1)) == 48) {
			var2.setGeneric(method732(var1));
		} else if (var3 == 50) {
			var2.setPerspective(readFloat32LE(var1), readFloat32LE(var1), readFloat32LE(var1), readFloat32LE(var1));
		} else {
			if (var3 != 49) {
				throw new IOException("Projection type not recognized: " + var3 + "(" + this.filePath + ").");
			}

			var2.setParallel(readFloat32LE(var1), readFloat32LE(var1), readFloat32LE(var1), readFloat32LE(var1));
		}

		return var2;
	}

	private CompositingMode readCompositingMode(InputStream var1) throws IOException {
		CompositingMode var2 = new CompositingMode();
		this.method736(var2, var1);
		var2.setDepthTestEnable(readBit(var1));
		var2.setDepthWriteEnable(readBit(var1));
		var2.setColorWriteEnable(readBit(var1));
		var2.setAlphaWriteEnable(readBit(var1));
		var2.setBlending(readByte(var1));
		var2.setAlphaThreshold((float) readByte(var1) / 255.0F);
		var2.setDepthOffset(readFloat32LE(var1), readFloat32LE(var1));
		return var2;
	}

	private Fog readFog(InputStream var1) throws IOException {
		Fog var2 = new Fog();
		this.method736(var2, var1);
		var2.setColor(readRGB(var1));
		var2.setMode(readByte(var1));
		if (var2.getMode() == 80) {
			var2.setDensity(readFloat32LE(var1));
		} else if (var2.getMode() == 81) {
			var2.setLinear(readFloat32LE(var1), readFloat32LE(var1));
		}

		return var2;
	}

	private Group readGroup(InputStream var1) throws IOException {
		Group var2 = new Group();
		this.method716(var2, var1);
		return var2;
	}

	private Image2D readImage2D(InputStream var1) throws IOException {
		AnimationController var2 = new AnimationController();
		this.method736(var2, var1);
		int var3 = readByte(var1);
		boolean var4 = readBit(var1);
		int var5 = (int) readUInt32LE(var1);
		int var6 = (int) readUInt32LE(var1);
		Image2D var7 = null;
		Image2D var10000;
		if (var4) {
			var10000 = new Image2D(var3, var5, var6);
		} else {
			byte[] var8 = new byte[(int) readUInt32LE(var1)];
			if (var8.length > 0) {
				var1.read(var8);
			}

			byte[] var9 = new byte[(int) readUInt32LE(var1)];
			var1.read(var9);
			var10000 = var8.length != 0 ? new Image2D(var3, var5, var6, var9, var8) : new Image2D(var3, var5, var6, var9);
		}

		var7 = var10000;
		copyObject3D(var2, var7);
		return var7;
	}

	private KeyframeSequence readKeyframeSequence(InputStream var1) throws IOException {
		AnimationController var2 = new AnimationController();
		this.method736(var2, var1);
		int var3 = readByte(var1);
		int var4 = readByte(var1);
		int var5 = readByte(var1);
		int var6 = (int) readUInt32LE(var1);
		int var7 = (int) readUInt32LE(var1);
		int var8 = (int) readUInt32LE(var1);
		int var9 = (int) readUInt32LE(var1);
		int var10 = (int) readUInt32LE(var1);
		KeyframeSequence var11 = new KeyframeSequence(var10, var9, var3);
		copyObject3D(var2, var11);
		var11.setRepeatMode(var4);
		var11.setDuration(var6);
		var11.setValidRange(var7, var8);
		float[] var12 = new float[var9];
		int var15;
		if (var5 == 0) {
			for (int var13 = 0; var13 < var10; ++var13) {
				int var14 = readInt32LE(var1);

				for (var15 = 0; var15 < var9; ++var15) {
					var12[var15] = readFloat32LE(var1);
				}

				var11.setKeyframe(var13, var14, var12);
			}
		} else {
			if (var5 != 1 && var5 != 2) {
				throw new IOException("Encoding not recognized: " + var5 + "(" + this.filePath + ").");
			}

			float[] var19 = new float[var9];
			float[] var20 = new float[var9];

			for (var15 = 0; var15 < var9; ++var15) {
				var19[var15] = readFloat32LE(var1);
			}

			for (var15 = 0; var15 < var9; ++var15) {
				var20[var15] = readFloat32LE(var1);
			}

			for (var15 = 0; var15 < var10; ++var15) {
				int var16 = readInt32LE(var1);
				int var17;
				int var18;
				if (var5 == 1) {
					for (var17 = 0; var17 < var9; ++var17) {
						var18 = readByte(var1);
						var12[var17] = var19[var17] + var20[var17] * (float) var18 / 255.0F;
					}
				} else {
					for (var17 = 0; var17 < var9; ++var17) {
						var18 = readInt16LE(var1);
						var12[var17] = var19[var17] + var20[var17] * (float) var18 / 65535.0F;
					}
				}

				var11.setKeyframe(var15, var16, var12);
			}
		}

		return var11;
	}

	private Light readLight(InputStream var1) throws IOException {
		Light var2 = new Light();
		this.method745(var2, var1);
		var2.setAttenuation(readFloat32LE(var1), readFloat32LE(var1), readFloat32LE(var1));
		var2.setColor(readRGB(var1));
		var2.setMode(readByte(var1));
		var2.setIntensity(readFloat32LE(var1));
		var2.setSpotAngle(readFloat32LE(var1));
		var2.setSpotExponent(readFloat32LE(var1));
		return var2;
	}

	private Material readMaterial(InputStream var1) throws IOException {
		Material var2 = new Material();
		this.method736(var2, var1);
		var2.setColor(1024, readRGB(var1)); //AMBIENT
		var2.setColor(2048, readARGB(var1)); //DIFFUSE
		var2.setColor(4096, readRGB(var1)); //EMISSIVE
		var2.setColor(8192, readRGB(var1)); //SPECULAR
		var2.setShininess(readFloat32LE(var1));
		var2.setVertexColorTrackingEnable(readBit(var1));
		return var2;
	}

	private Mesh readMesh(InputStream var1) throws IOException {
		Group var2 = new Group();
		this.method745(var2, var1);
		VertexBuffer var3 = (VertexBuffer) this.method728(var1);
		int var4;
		IndexBuffer[] var5 = new IndexBuffer[var4 = (int) readUInt32LE(var1)];
		Appearance[] var6 = new Appearance[var4];

		for (int var7 = 0; var7 < var4; ++var7) {
			var5[var7] = (IndexBuffer) this.method728(var1);
			var6[var7] = (Appearance) this.method728(var1);
		}

		Mesh var8 = new Mesh(var3, var5, var6);
		copyNode(var2, var8);
		return var8;
	}

	private MorphingMesh readMorphingMesh(InputStream var1) throws IOException {
		Mesh var2 = this.readMesh(var1);
		int var3;
		VertexBuffer[] var4 = new VertexBuffer[var3 = (int) readUInt32LE(var1)];
		float[] var5 = new float[var3];

		int var6;
		for (var6 = 0; var6 < var3; ++var6) {
			var4[var6] = (VertexBuffer) this.method728(var1);
			var5[var6] = readFloat32LE(var1);
		}

		IndexBuffer[] var7 = new IndexBuffer[var6 = var2.getSubmeshCount()];
		Appearance[] var8 = new Appearance[var6];

		for (int var9 = 0; var9 < var6; ++var9) {
			var7[var9] = var2.getIndexBuffer(var9);
			var8[var9] = var2.getAppearance(var9);
		}

		MorphingMesh var10 = new MorphingMesh(var2.getVertexBuffer(), var4, var7, var8);
		copyMesh(var2, var10);
		var10.setWeights(var5);
		return var10;
	}

	private PolygonMode readPolygonMode(InputStream var1) throws IOException {
		PolygonMode var2 = new PolygonMode();
		this.method736(var2, var1);
		var2.setCulling(readByte(var1));
		var2.setShading(readByte(var1));
		var2.setWinding(readByte(var1));
		var2.setTwoSidedLightingEnable(readBit(var1));
		var2.setLocalCameraLightingEnable(readBit(var1));
		var2.setPerspectiveCorrectionEnable(readBit(var1));
		return var2;
	}

	private SkinnedMesh readSkinnedMesh(InputStream var1) throws IOException {
		Mesh var2 = this.readMesh(var1);
		Group var3 = (Group) this.method728(var1);
		int var4;
		IndexBuffer[] var5 = new IndexBuffer[var4 = var2.getSubmeshCount()];
		Appearance[] var6 = new Appearance[var4];

		for (int var7 = 0; var7 < var4; ++var7) {
			var5[var7] = var2.getIndexBuffer(var7);
			var6[var7] = var2.getAppearance(var7);
		}

		SkinnedMesh var13 = new SkinnedMesh(var2.getVertexBuffer(), var5, var6, var3);
		copyMesh(var2, var13);
		int var8 = (int) readUInt32LE(var1);

		while (var8-- > 0) {
			Node var9 = (Node) this.method728(var1);
			int var10 = (int) readUInt32LE(var1);
			int var11 = (int) readUInt32LE(var1);
			int var12 = readInt32LE(var1);
			var13.addTransform(var9, var12, var10, var11);
		}

		return var13;
	}

	private Sprite3D readSprite(InputStream var1) throws IOException {
		Group var2 = new Group();
		this.method745(var2, var1);
		Image2D var3 = (Image2D) this.method728(var1);
		Appearance var4 = (Appearance) this.method728(var1);
		boolean var5 = readBit(var1);
		Sprite3D var6 = new Sprite3D(var5, var3, var4);
		copyNode(var2, var6);
		var6.setCrop(readInt32LE(var1), readInt32LE(var1), readInt32LE(var1), readInt32LE(var1));
		return var6;
	}

	private Texture2D readTexture2D(InputStream var1) throws IOException {
		Group var2 = new Group();
		this.method737(var2, var1);
		Texture2D var3 = new Texture2D((Image2D) this.method728(var1));
		copyTransformable(var2, var3);
		var3.setBlendColor(readRGB(var1));
		var3.setBlending(readByte(var1));
		var3.setWrapping(readByte(var1), readByte(var1));
		var3.setFiltering(readByte(var1), readByte(var1));
		return var3;
	}

	private TriangleStripArray readTriangleStripArray(InputStream var1) throws IOException {
		AnimationController var2;
		int var3;
		int var4;
		int[] var5;
		var2 = new AnimationController();
		this.method736(var2, var1);
		var3 = readByte(var1);
		var4 = 0;
		var5 = null;
		int var6;
		label54:
		switch (var3) {
			case 0:
				var4 = (int) readUInt32LE(var1);
				break;
			case 1:
				var4 = readByte(var1);
				break;
			case 2:
				var4 = readInt16LE(var1);
				break;
			case 128:
				var5 = new int[(int) readUInt32LE(var1)];
				var6 = 0;

				while (true) {
					if (var6 >= var5.length) {
						break label54;
					}

					var5[var6] = (int) readUInt32LE(var1);
					++var6;
				}
			case 129:
				var5 = new int[(int) readUInt32LE(var1)];
				var6 = 0;

				while (true) {
					if (var6 >= var5.length) {
						break label54;
					}

					var5[var6] = readByte(var1);
					++var6;
				}
			case 130:
				var5 = new int[(int) readUInt32LE(var1)];
				var6 = 0;

				while (true) {
					if (var6 >= var5.length) {
						break label54;
					}

					var5[var6] = readInt16LE(var1);
					++var6;
				}
			default:
				throw new IllegalArgumentException("Invalid TriangleStripArray encoding (" + this.filePath + ").");
		}

		int[] var9 = new int[(int) readUInt32LE(var1)];

		for (int var7 = 0; var7 < var9.length; ++var7) {
			var9[var7] = (int) readUInt32LE(var1);
		}

		TriangleStripArray var8 = null;
		var8 = var3 != 0 && var3 != 1 && var3 != 2 ? new TriangleStripArray(var5, var9) : new TriangleStripArray(var4, var9);
		copyObject3D(var2, var8);
		return var8;
	}

	private VertexArray readVertexArray(InputStream var1) throws IOException {
		AnimationController var2 = new AnimationController();
		this.method736(var2, var1);
		int var3 = readByte(var1);
		int var4 = readByte(var1);
		int var5 = readByte(var1);
		int var6 = readInt16LE(var1);
		if (var5 != 0 && var5 != 1) {
			throw new IllegalArgumentException("Invalid VertexArray encoding (" + this.filePath + ").");
		} else {
			VertexArray var7 = new VertexArray(var6, var4, var3);
			int[] var8 = new int[var4];
			int var10;
			int var11;
			if (var3 == 1) {
				byte[] var9 = new byte[var4];
				if (var5 == 0) {
					for (var10 = 0; var10 < var6; ++var10) {
						for (var11 = 0; var11 < var4; ++var11) {
							var9[var11] = (byte) readByte(var1);
						}

						var7.set(var10, 1, var9);
					}
				} else {
					for (var10 = 0; var10 < var6; ++var10) {
						for (var11 = 0; var11 < var4; ++var11) {
							var8[var11] += (byte) readByte(var1);
							var9[var11] = (byte) var8[var11];
						}

						var7.set(var10, 1, var9);
					}
				}
			} else {
				short[] var12 = new short[var4];
				if (var5 == 0) {
					for (var10 = 0; var10 < var6; ++var10) {
						for (var11 = 0; var11 < var4; ++var11) {
							var12[var11] = (short) readInt16LE(var1);
						}

						var7.set(var10, 1, var12);
					}
				} else {
					for (var10 = 0; var10 < var6; ++var10) {
						for (var11 = 0; var11 < var4; ++var11) {
							var8[var11] += (short) readInt16LE(var1);
							var12[var11] = (short) var8[var11];
						}

						var7.set(var10, 1, var12);
					}
				}
			}

			copyObject3D(var2, var7);
			return var7;
		}
	}

	private VertexBuffer readVertexBuffer(InputStream var1) throws IOException {
		VertexBuffer var2 = new VertexBuffer();
		this.method736(var2, var1);
		var2.setDefaultColor(readARGB(var1));
		VertexArray var3 = (VertexArray) this.method728(var1);
		float[] var4 = new float[3];

		for (int var5 = 0; var5 < 3; ++var5) {
			var4[var5] = readFloat32LE(var1);
		}

		float var13 = readFloat32LE(var1);
		if (var3 != null) {
			var2.setPositions(var3, var13, var4);
		}

		VertexArray var6;
		if ((var6 = (VertexArray) this.method728(var1)) != null) {
			var2.setNormals(var6);
		}

		VertexArray var7;
		if ((var7 = (VertexArray) this.method728(var1)) != null) {
			var2.setColors(var7);
		}

		int var8 = (int) readUInt32LE(var1);

		for (int var9 = 0; var9 < var8; ++var9) {
			VertexArray var10;
			if ((var10 = (VertexArray) this.method728(var1)) == null) {
				throw new IOException("Null texture vertex array");
			}

			for (int var11 = 0; var11 < 3; ++var11) {
				var4[var11] = readFloat32LE(var1);
			}

			float var12 = readFloat32LE(var1);
			var2.setTexCoords(var9, var10, var12, var4);
		}

		return var2;
	}

	private World readWorld(InputStream var1) throws IOException {
		World var2 = new World();
		this.method716(var2, var1);
		Camera var3;
		if ((var3 = (Camera) this.method728(var1)) != null) {
			var2.setActiveCamera(var3);
		}

		var2.setBackground((Background) this.method728(var1));
		return var2;
	}

	private Object3D method728(InputStream var1) throws IOException {
		return this.method726((int) readUInt32LE(var1));
	}

	private static final Transform method732(InputStream var0) throws IOException {
		Transform var1 = new Transform();
		float[] var2 = new float[16];

		for (int var3 = 0; var3 < 16; ++var3) {
			var2[var3] = readFloat32LE(var0);
		}

		var1.set(var2);
		return var1;
	}

	private static final int readByte(InputStream var0) throws IOException {
		return var0.read();
	}

	private static boolean readBit(InputStream var0) throws IOException {
		int var1;
		if ((var1 = var0.read()) == 0) {
			return false;
		} else if (var1 != 1) {
			throw new IOException("Malformed boolean.");
		} else {
			return true;
		}
	}

	private static int readInt16LE(InputStream var0) throws IOException {
		return var0.read() + (var0.read() << 8);
	}

	private static final int readInt32LE(InputStream var0) throws IOException {
		return var0.read() + (var0.read() << 8) + (var0.read() << 16) + (var0.read() << 24);
	}

	private static final long readUInt32LE(InputStream var0) throws IOException {
		return (long) var0.read() + ((long) var0.read() << 8) + ((long) var0.read() << 16) + ((long) var0.read() << 24);
	}

	private static final float readFloat32LE(InputStream var0) throws IOException {
		int var1;
		if (((var1 = readInt32LE(var0)) & 0x7f800000) != 0x7f800000 && var1 != Integer.MIN_VALUE && ((var1 & 0x7fffff) == 0 || (var1 & 0x7f800000) != 0)) {
			return Float.intBitsToFloat(var1);
		} else {
			throw new IOException("Malformed float.");
		}
	}

	private static int readARGB(InputStream var0) throws IOException {
		return (var0.read() << 16) + (var0.read() << 8) + var0.read() + (var0.read() << 24);
	}

	private static int readRGB(InputStream var0) throws IOException {
		return (var0.read() << 16) + (var0.read() << 8) + var0.read();
	}

	private static String method710(InputStream var0) throws IOException {
		StringBuffer var1 = new StringBuffer();

		int var2;
		for (InputStream var10000 = var0; (var2 = var10000.read()) != 0; var10000 = var0) {
			if ((var2 & 128) == 0) {
				var1.append((char) (var2 & 255));
			} else {
				int var3;
				if ((var2 & 224) == 192) {
					if (((var3 = var0.read()) & 192) != 128) {
						throw new IOException("Invalid UTF-8 string.");
					}

					var1.append((char) ((var2 & 31) << 6 | var3 & 63));
				} else {
					if ((var2 & 240) != 224) {
						throw new IOException("Invalid UTF-8 string.");
					}

					var3 = var0.read();
					int var4 = var0.read();
					if ((var3 & 192) != 128 || (var4 & 192) != 128) {
						throw new IOException("Invalid UTF-8 string.");
					}

					var1.append((char) ((var2 & 15) << 12 | (var3 & 63) << 6 | var4 & 63));
				}
			}
		}

		return var1.toString();
	}

	private static int getInnerFileType(byte[] file, int offset) {
        int i, missmatchedBytes;
		for (missmatchedBytes = i = 0; i < PNG_MAGIC_NUMBER.length; ++i) {
			if (file[i + offset] != PNG_MAGIC_NUMBER[i]) {
				++missmatchedBytes;
			}
		}

		if (missmatchedBytes == 0) {
			return 2;
		} else {
            i = 0;
			for (missmatchedBytes = 0; i < M3G_MAGIC_NUMBER.length; ++i) {
				if (file[i + offset] != M3G_MAGIC_NUMBER[i]) {
					++missmatchedBytes;
				}
			}

			if (missmatchedBytes == 0) {
				return 1;
			} else {
				Emulator.getEmulator().getLogStream().println("M3GLoader:Invalid file type, use png instead");
				return 2;
			}
		}
	}

	private static int getFileType(InputStream var0) throws IOException {
		byte[] var1 = new byte[12];
		var0.read(var1);
		return getInnerFileType(var1, 0);
	}

	private boolean method715(String var1) {
		for (int var2 = 0; var2 < this.aVector1091.size(); ++var2) {
			if (((String) this.aVector1091.elementAt(var2)).equals(var1)) {
				return true;
			}
		}

		return false;
	}

	private static InputStream method709(String var0) throws IOException {
		InputConnection var1;
		HttpConnection var2;
		String var3;
		if ((var1 = (InputConnection) Connector.open(var0)) instanceof HttpConnection && (var3 = (var2 = (HttpConnection) var1).getHeaderField("Content-Type")) != null && !var3.equals("application/m3g") && !var3.equals("image/png")) {
			throw new IOException("Wrong MIME type: " + var3);
		} else {
			return var1.openInputStream();
		}
	}

	private InputStream method752(String var1) throws IOException {
		if (var1.indexOf(58) != -1) {
			return method709(var1);
		} else if (var1.charAt(0) == 47) {
			return ResourceManager.getResourceAsStream(var1);
		} else if (this.aString1089 == null) {
			throw new IOException("Relative URI.");
		} else {
			String var2;
			return (var2 = this.aString1089.substring(0, this.aString1089.lastIndexOf(47) + 1) + var1).charAt(0) == 47 ? ResourceManager.getResourceAsStream(var2) : method709(var2);
		}
	}

	private static void copyObject3D(Object3D var0, Object3D var1) {
		var1.setUserObject(var0.getUserObject());
		var1.setUserID(var0.getUserID());
	}

	private static void copyNode(Node var0, Node var1) {
		copyTransformable(var0, var1);
		var1.setAlphaFactor(var0.getAlphaFactor());
		var1.setScope(var0.getScope());
		var1.setPickingEnable(var0.isPickingEnabled());
		var1.setRenderingEnable(var0.isRenderingEnabled());
	}

	private static void copyTransformable(Transformable var0, Transformable var1) {
		copyObject3D(var0, var1);
		float[] var2 = new float[4];
		Transform var3 = new Transform();
		var0.getTranslation(var2);
		var1.setTranslation(var2[0], var2[1], var2[2]);
		var0.getScale(var2);
		var1.setScale(var2[0], var2[1], var2[2]);
		var0.getOrientation(var2);
		var1.setOrientation(var2[0], var2[1], var2[2], var2[3]);
		var0.getTransform(var3);
		var1.setTransform(var3);
	}

	private static void copyMesh(Mesh var0, Mesh var1) {
		copyNode(var0, var1);
	}

	private static void inflate(byte[] var0, byte[] var1) {
		try {
			Inflater var2 = new Inflater(false);
			var2.setInput(var0);
			var2.inflate(var1);
			var2.end();
		} catch (Exception e) {
			Emulator.getEmulator().getLogStream().println("m3g unzip error");
		}
	}
}
