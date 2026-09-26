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
	private static final Boolean NOT_REFERENCED = new Boolean(false);
	private static final Boolean REFERENCED = new Boolean(true);
	private Vector loadedObjects = new Vector();
	private Vector loadedObjectsRef = new Vector();
	private Vector fileHistory = new Vector();
	private Vector animTracks = null;
	private String resourceName;
	private String parentResourceName;
	private int currentSection;
	private boolean containedExternalLinks;
	private boolean externalLinks;

	public static Object3D[] load(String name) throws IOException {
		if (name == null) {
			throw new NullPointerException();
		} else {
			try {
				return (new M3GLoader()).loadFromString(name);
			} catch (SecurityException e) {
				throw e;
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

	public static Object3D[] load(byte[] data, int offset) throws IOException {
		if (data == null) {
			throw new NullPointerException();
		} else {
			try {
				return (new M3GLoader()).loadFromByteArray(data, offset);
			} catch (SecurityException e) {
				throw e;
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

	private M3GLoader() {
	}

	private M3GLoader(Vector fileHistory, String parentResourceName) {
		this.parentResourceName = parentResourceName;
		this.fileHistory = fileHistory;
	}

	private Object3D[] loadFromString(String name) throws IOException {
		if (name == null) {
			throw new NullPointerException();
		} else if (this.inFileHistory(name)) {
			throw new IOException("Reference loop detected.");
		} else {
			this.resourceName = name;
			this.fileHistory.addElement(name);
			PeekInputStream stream;
			int fileType = getFileType(stream = new PeekInputStream(this.getInputStream(name), 12));
			stream.rewind();
			Object3D[] objects = this.loadStream(stream, fileType);
			this.fileHistory.removeElement(name);
			return objects;
		}
	}

	private Object3D[] loadFromByteArray(byte[] data, int offset) throws IOException {
		if (data == null) {
			throw new NullPointerException("Resource byte array is null.");
		} else {
			int fileType = getInnerFileType(data, offset);
			ByteArrayInputStream stream = new ByteArrayInputStream(data, offset, data.length - offset);
			return this.loadStream(stream, fileType);
		}
	}

	private Object3D[] loadStream(InputStream in, int fileType) throws IOException {
		if (fileType == 1) {
			return this.loadM3G(in);
		} else if (fileType == 2) {
			return loadPNG(in);
		} else {
			throw new IOException("File not recognized.");
		}
	}

	private static Object3D[] loadPNG(InputStream in) throws IOException {
		return new Object3D[]{new Image2D(100, Image.createImage(in))};
	}

	private Object3D[] loadM3G(InputStream in)
			throws IOException {
		in.skip(M3G_MAGIC_NUMBER.length);
		while (loadSection(in)) {
			this.currentSection += 1;
		}
		return getUnreferencedObjects();
	}

	private boolean loadSection(InputStream in) throws IOException {
		if (this.currentSection > 1 && this.externalLinks && !this.containedExternalLinks) {
			throw new IOException("No external sections (" + this.resourceName + ").");
		} else {
			AdlerInputStream ais;
			int compression;
			if ((compression = readByte(ais = new AdlerInputStream(in))) == -1) {
				return false;
			} else if (this.currentSection == 0 && compression != 0) {
				throw new IOException("Compressed header (" + this.resourceName + ").");
			} else {
				long sectionLength = readUInt32LE(ais);
				long uncompressedLength = readUInt32LE(ais);
				Object sectionStream = null;
				if (compression == 0) {
					sectionStream = ais;
					if (uncompressedLength != sectionLength - 13L) {
						Emulator.getEmulator().getLogStream().println("M3GLoader: Section length mismatch!!!");
						return false;
					}
				} else {
					if (compression != 1) {
						Emulator.getEmulator().getLogStream().println("M3GLoader: Unrecognized compression scheme(" + compression + ")!!!");
						return false;
					}

					byte[] compressed = new byte[(int) sectionLength - 13];
					ais.read(compressed);
					byte[] uncompressed = new byte[(int) uncompressedLength];
					inflate(compressed, uncompressed);
					sectionStream = new CountedInputStream(new ByteArrayInputStream(uncompressed));
				}

				((CountedInputStream) sectionStream).resetCounter();

				while ((long) ((CountedInputStream) sectionStream).getCounter() < uncompressedLength) {
					this.addLoaded(this.loadObject((CountedInputStream) sectionStream));
				}

				if ((long) ((CountedInputStream) sectionStream).getCounter() != uncompressedLength) {
					Emulator.getEmulator().getLogStream().println("M3GLoader: Section length mismatch!!!");
					return false;
				} else {
					long checksum = ais.getChecksum();
					long expectedChecksum = readUInt32LE(ais);
					if (checksum != expectedChecksum) {
						throw new IOException("Checksum is wrong (" + this.resourceName + ").");
					} else {
						return true;
					}
				}
			}
		}
	}

	private Object3D loadObject(CountedInputStream in) throws IOException {
		int objectType = readByte(in);
		long length = readUInt32LE(in);
		long endPosition = (long) in.getCounter() + length;
		Object object = null;
		switch (objectType) {
			case 0:
				if (this.currentSection != 0) {
					throw new IOException("Header in wrong section (" + this.resourceName + ").");
				}

				this.readHeader(in);
				break;
			case 1:
				object = this.readAnimationController(in);
				break;
			case 2:
				object = this.readAnimationTrack(in);
				break;
			case 3:
				object = this.readAppearance(in);
				break;
			case 4:
				object = this.readBackground(in);
				break;
			case 5:
				object = this.readCamera(in);
				break;
			case 6:
				object = this.readCompositingMode(in);
				break;
			case 7:
				object = this.readFog(in);
				break;
			case 8:
				object = this.readPolygonMode(in);
				break;
			case 9:
				object = this.readGroup(in);
				break;
			case 10:
				object = this.readImage2D(in);
				break;
			case 11:
				object = this.readTriangleStripArray(in);
				break;
			case 12:
				object = this.readLight(in);
				break;
			case 13:
				object = this.readMaterial(in);
				break;
			case 14:
				object = this.readMesh(in);
				break;
			case 15:
				object = this.readMorphingMesh(in);
				break;
			case 16:
				object = this.readSkinnedMesh(in);
				break;
			case 17:
				object = this.readTexture2D(in);
				break;
			case 18:
				object = this.readSprite(in);
				break;
			case 19:
				object = this.readKeyframeSequence(in);
				break;
			case 20:
				object = this.readVertexArray(in);
				break;
			case 21:
				object = this.readVertexBuffer(in);
				break;
			case 22:
				object = this.readWorld(in);
				break;
			case 255:
				if (this.currentSection != 1) {
					throw new IOException("External reference in wrong section (" + this.resourceName + ").");
				}

				if (!this.externalLinks) {
					throw new IOException("External links in self contained file (" + this.resourceName + ").");
				}

				String uri = readString(in);
				this.containedExternalLinks = true;
				object = (new M3GLoader(this.fileHistory, this.resourceName)).loadFromString(uri)[0];
				break;
			default:
				throw new IOException("Unrecognized object type " + objectType + " (" + this.resourceName + ").");
		}

		if (endPosition != (long) in.getCounter()) {
			throw new IOException("Object length mismatch (" + this.resourceName + ").");
		} else {
			this.addAnimTracks((Object3D) object);
			return (Object3D) object;
		}
	}

	private void addLoaded(Object3D object) {
		if (object != null) {
			this.loadedObjects.addElement(object);
			this.loadedObjectsRef.addElement(NOT_REFERENCED);
		}

	}

	private Object3D getLoaded(int index) {
		if (index == 0) {
			return null;
		} else if (index >= 2 && index - 2 < this.loadedObjects.size()) {
			this.loadedObjectsRef.setElementAt(REFERENCED, index - 2);
			return (Object3D) this.loadedObjects.elementAt(index - 2);
		} else {
			throw new IllegalArgumentException("Invalid reference index (" + this.resourceName + ").");
		}
	}

	private Object3D[] getUnreferencedObjects() {
		Vector unreferenced = new Vector();

		for (int i = 0; i < this.loadedObjects.size(); ++i) {
			if (this.loadedObjectsRef.elementAt(i) == NOT_REFERENCED) {
				unreferenced.addElement(this.loadedObjects.elementAt(i));
			}
		}

		Object3D[] result = new Object3D[unreferenced.size()];

		for (int i = 0; i < unreferenced.size(); ++i) {
			result[i] = (Object3D) unreferenced.elementAt(i);
		}

		return result;
	}

	private void readHeader(InputStream in)
			throws IOException {
		byte[] version = new byte[2];
		in.read(version);
		this.externalLinks = readBit(in);
		readUInt32LE(in);
		readUInt32LE(in);
		if ((version[0] != 1) || (version[1] != 0)) {
			throw new IOException("Invalid file version (" + this.resourceName + ").");
		}
		readString(in);
	}

	private void readObject3DData(Object3D object, InputStream in) throws IOException {
		object.setUserID( (int)readUInt32LE(in) );
		long trackCount = readUInt32LE(in);
		this.animTracks = new Vector();

		while (trackCount-- > 0L) {
			AnimationTrack track;
			if ((track = (AnimationTrack) this.readReference(in)) == null) {
				throw new NullPointerException();
			}

			this.animTracks.addElement(track);
		}

		long userParameterCount = readUInt32LE(in);

		if (userParameterCount != 0) {
			Hashtable userParameters = new Hashtable();
			while (userParameterCount-- > 0L) {
				int parameterId = (int) readUInt32LE(in);
				byte[] parameterValue = new byte[(int) readUInt32LE(in)];
				in.read(parameterValue);
				userParameters.put(new Integer(parameterId), parameterValue);
			}

			object.setUserObject(userParameters);
		}
	}

	private void readTransformableData(Transformable transformable, InputStream in) throws IOException {
		this.readObject3DData(transformable, in);
		if (readBit(in)) {
			transformable.setTranslation(readFloat32LE(in), readFloat32LE(in), readFloat32LE(in));
			transformable.setScale(readFloat32LE(in), readFloat32LE(in), readFloat32LE(in));
			transformable.setOrientation(readFloat32LE(in), readFloat32LE(in), readFloat32LE(in), readFloat32LE(in));
		}

		if (readBit(in)) {
			transformable.setTransform(readTransform(in));
		}

	}

	private void readNodeData(Node node, InputStream in) throws IOException {
		this.readTransformableData(node, in);
		node.setRenderingEnable(readBit(in));
		node.setPickingEnable(readBit(in));
		node.setAlphaFactor((float) readByte(in) / 255.0F);
		node.setScope((int) readUInt32LE(in));
		if (readBit(in)) {
			int zTarget = readByte(in);
			int yTarget = readByte(in);
			int zReference = (int) readUInt32LE(in);
			int yReference = (int) readUInt32LE(in);
			node.setAlignment((Node) this.getLoaded(zReference), zTarget, (Node) this.getLoaded(yReference), yTarget);
		}

	}

	private void readGroupData(Group group, InputStream in) throws IOException {
		this.readNodeData(group, in);
		int childCount = (int) readUInt32LE(in);

		while (childCount-- > 0) {
			group.addChild((Node) this.readReference(in));
		}

	}

	private void addAnimTracks(Object3D object) {
		if (this.animTracks != null && object != null) {
			for (int i = 0; i < this.animTracks.size(); ++i) {
				object.addAnimationTrack((AnimationTrack) this.animTracks.elementAt(i));
			}

			this.animTracks = null;
		}

	}

	private AnimationController readAnimationController(InputStream in) throws IOException {
		AnimationController controller = new AnimationController();
		this.readObject3DData(controller, in);
		float speed = readFloat32LE(in);
		float weight = readFloat32LE(in);
		controller.setActiveInterval(readInt32LE(in), readInt32LE(in));
		float sequenceTime = readFloat32LE(in);
		int referenceWorldTime = readInt32LE(in);
		controller.setPosition(sequenceTime, referenceWorldTime);
		controller.setSpeed(speed, referenceWorldTime);
		controller.setWeight(weight);
		return controller;
	}

	private AnimationTrack readAnimationTrack(InputStream in) throws IOException {
		AnimationController objectData = new AnimationController();
		this.readObject3DData(objectData, in);
		KeyframeSequence sequence = (KeyframeSequence) this.readReference(in);
		AnimationController controller = (AnimationController) this.readReference(in);
		int property = (int) readUInt32LE(in);
		AnimationTrack track = new AnimationTrack(sequence, property);
		copyObject3D(objectData, track);
		track.setController(controller);
		return track;
	}

	private Appearance readAppearance(InputStream in) throws IOException {
		Appearance appearance = new Appearance();
		this.readObject3DData(appearance, in);
		appearance.setLayer(readByte(in));
		appearance.setCompositingMode((CompositingMode) this.readReference(in));
		appearance.setFog((Fog) this.readReference(in));
		appearance.setPolygonMode((PolygonMode) this.readReference(in));
		appearance.setMaterial((Material) this.readReference(in));
		int textureCount = (int) readUInt32LE(in);

		for (int i = 0; i < textureCount; ++i) {
			Texture2D texture;
			if ((texture = (Texture2D) this.readReference(in)) == null) {
				throw new IOException("Null texture reference");
			}

			appearance.setTexture(i, texture);
		}

		return appearance;
	}

	private Background readBackground(InputStream in) throws IOException {
		Background background = new Background();
		this.readObject3DData(background, in);
		background.setColor(readRGBA(in));
		Image2D image = (Image2D) this.readReference(in);
		background.setImage(image);
		background.setImageMode(readByte(in), readByte(in));
		background.setCrop(readInt32LE(in), readInt32LE(in), readInt32LE(in), readInt32LE(in));
		background.setDepthClearEnable(readBit(in));
		background.setColorClearEnable(readBit(in));
		return background;
	}

	private Camera readCamera(InputStream in) throws IOException {
		Camera camera = new Camera();
		this.readNodeData(camera, in);
		int projectionType;
		if ((projectionType = readByte(in)) == 48) {
			camera.setGeneric(readTransform(in));
		} else if (projectionType == 50) {
			camera.setPerspective(readFloat32LE(in), readFloat32LE(in), readFloat32LE(in), readFloat32LE(in));
		} else {
			if (projectionType != 49) {
				throw new IOException("Projection type not recognized: " + projectionType + "(" + this.resourceName + ").");
			}

			camera.setParallel(readFloat32LE(in), readFloat32LE(in), readFloat32LE(in), readFloat32LE(in));
		}

		return camera;
	}

	private CompositingMode readCompositingMode(InputStream in) throws IOException {
		CompositingMode compositingMode = new CompositingMode();
		this.readObject3DData(compositingMode, in);
		compositingMode.setDepthTestEnable(readBit(in));
		compositingMode.setDepthWriteEnable(readBit(in));
		compositingMode.setColorWriteEnable(readBit(in));
		compositingMode.setAlphaWriteEnable(readBit(in));
		compositingMode.setBlending(readByte(in));
		compositingMode.setAlphaThreshold((float) readByte(in) / 255.0F);
		compositingMode.setDepthOffset(readFloat32LE(in), readFloat32LE(in));
		return compositingMode;
	}

	private Fog readFog(InputStream in) throws IOException {
		Fog fog = new Fog();
		this.readObject3DData(fog, in);
		fog.setColor(readRGB(in));
		fog.setMode(readByte(in));
		if (fog.getMode() == 80) {
			fog.setDensity(readFloat32LE(in));
		} else if (fog.getMode() == 81) {
			fog.setLinear(readFloat32LE(in), readFloat32LE(in));
		}

		return fog;
	}

	private Group readGroup(InputStream in) throws IOException {
		Group group = new Group();
		this.readGroupData(group, in);
		return group;
	}

	private Image2D readImage2D(InputStream in) throws IOException {
		AnimationController objectData = new AnimationController();
		this.readObject3DData(objectData, in);
		int format = readByte(in);
		boolean isMutable = readBit(in);
		int width = (int) readUInt32LE(in);
		int height = (int) readUInt32LE(in);
		Image2D image = null;
		Image2D createdImage;
		if (isMutable) {
			createdImage = new Image2D(format, width, height);
		} else {
			byte[] palette = new byte[(int) readUInt32LE(in)];
			if (palette.length > 0) {
				in.read(palette);
			}

			byte[] pixels = new byte[(int) readUInt32LE(in)];
			in.read(pixels);
			createdImage = palette.length != 0 ? new Image2D(format, width, height, pixels, palette) : new Image2D(format, width, height, pixels);
		}

		image = createdImage;
		copyObject3D(objectData, image);
		return image;
	}

	private KeyframeSequence readKeyframeSequence(InputStream in) throws IOException {
		AnimationController objectData = new AnimationController();
		this.readObject3DData(objectData, in);
		int interpolation = readByte(in);
		int repeatMode = readByte(in);
		int encoding = readByte(in);
		int duration = (int) readUInt32LE(in);
		int validRangeFirst = (int) readUInt32LE(in);
		int validRangeLast = (int) readUInt32LE(in);
		int componentCount = (int) readUInt32LE(in);
		int keyframeCount = (int) readUInt32LE(in);
		KeyframeSequence sequence = new KeyframeSequence(keyframeCount, componentCount, interpolation);
		copyObject3D(objectData, sequence);
		sequence.setRepeatMode(repeatMode);
		sequence.setDuration(duration);
		sequence.setValidRange(validRangeFirst, validRangeLast);
		float[] keyframeValue = new float[componentCount];
		int index;
		if (encoding == 0) {
			for (int keyframe = 0; keyframe < keyframeCount; ++keyframe) {
				int time = readInt32LE(in);

				for (index = 0; index < componentCount; ++index) {
					keyframeValue[index] = readFloat32LE(in);
				}

				sequence.setKeyframe(keyframe, time, keyframeValue);
			}
		} else {
			if (encoding != 1 && encoding != 2) {
				throw new IOException("Encoding not recognized: " + encoding + "(" + this.resourceName + ").");
			}

			float[] vectorBias = new float[componentCount];
			float[] vectorScale = new float[componentCount];

			for (index = 0; index < componentCount; ++index) {
				vectorBias[index] = readFloat32LE(in);
			}

			for (index = 0; index < componentCount; ++index) {
				vectorScale[index] = readFloat32LE(in);
			}

			for (index = 0; index < keyframeCount; ++index) {
				int time = readInt32LE(in);
				int component;
				int encodedValue;
				if (encoding == 1) {
					for (component = 0; component < componentCount; ++component) {
						encodedValue = readByte(in);
						keyframeValue[component] = vectorBias[component] + vectorScale[component] * (float) encodedValue / 255.0F;
					}
				} else {
					for (component = 0; component < componentCount; ++component) {
						encodedValue = readInt16LE(in);
						keyframeValue[component] = vectorBias[component] + vectorScale[component] * (float) encodedValue / 65535.0F;
					}
				}

				sequence.setKeyframe(index, time, keyframeValue);
			}
		}

		return sequence;
	}

	private Light readLight(InputStream in) throws IOException {
		Light light = new Light();
		this.readNodeData(light, in);
		light.setAttenuation(readFloat32LE(in), readFloat32LE(in), readFloat32LE(in));
		light.setColor(readRGB(in));
		light.setMode(readByte(in));
		light.setIntensity(readFloat32LE(in));
		light.setSpotAngle(readFloat32LE(in));
		light.setSpotExponent(readFloat32LE(in));
		return light;
	}

	private Material readMaterial(InputStream in) throws IOException {
		Material material = new Material();
		this.readObject3DData(material, in);
		material.setColor(1024, readRGB(in)); //AMBIENT
		material.setColor(2048, readRGBA(in)); //DIFFUSE
		material.setColor(4096, readRGB(in)); //EMISSIVE
		material.setColor(8192, readRGB(in)); //SPECULAR
		material.setShininess(readFloat32LE(in));
		material.setVertexColorTrackingEnable(readBit(in));
		return material;
	}

	private Mesh readMesh(InputStream in) throws IOException {
		Group nodeData = new Group();
		this.readNodeData(nodeData, in);
		VertexBuffer vertexBuffer = (VertexBuffer) this.readReference(in);
		int submeshCount;
		IndexBuffer[] submeshes = new IndexBuffer[submeshCount = (int) readUInt32LE(in)];
		Appearance[] appearances = new Appearance[submeshCount];

		for (int i = 0; i < submeshCount; ++i) {
			submeshes[i] = (IndexBuffer) this.readReference(in);
			appearances[i] = (Appearance) this.readReference(in);
		}

		Mesh mesh = new Mesh(vertexBuffer, submeshes, appearances);
		copyNode(nodeData, mesh);
		return mesh;
	}

	private MorphingMesh readMorphingMesh(InputStream in) throws IOException {
		Mesh mesh = this.readMesh(in);
		int targetCount;
		VertexBuffer[] targets = new VertexBuffer[targetCount = (int) readUInt32LE(in)];
		float[] weights = new float[targetCount];

		for (int i = 0; i < targetCount; ++i) {
			targets[i] = (VertexBuffer) this.readReference(in);
			weights[i] = readFloat32LE(in);
		}

		int submeshCount;
		IndexBuffer[] submeshes = new IndexBuffer[submeshCount = mesh.getSubmeshCount()];
		Appearance[] appearances = new Appearance[submeshCount];

		for (int i = 0; i < submeshCount; ++i) {
			submeshes[i] = mesh.getIndexBuffer(i);
			appearances[i] = mesh.getAppearance(i);
		}

		MorphingMesh morphingMesh = new MorphingMesh(mesh.getVertexBuffer(), targets, submeshes, appearances);
		copyMesh(mesh, morphingMesh);
		morphingMesh.setWeights(weights);
		return morphingMesh;
	}

	private PolygonMode readPolygonMode(InputStream in) throws IOException {
		PolygonMode polygonMode = new PolygonMode();
		this.readObject3DData(polygonMode, in);
		polygonMode.setCulling(readByte(in));
		polygonMode.setShading(readByte(in));
		polygonMode.setWinding(readByte(in));
		polygonMode.setTwoSidedLightingEnable(readBit(in));
		polygonMode.setLocalCameraLightingEnable(readBit(in));
		polygonMode.setPerspectiveCorrectionEnable(readBit(in));
		return polygonMode;
	}

	private SkinnedMesh readSkinnedMesh(InputStream in) throws IOException {
		Mesh mesh = this.readMesh(in);
		Group skeleton = (Group) this.readReference(in);
		int submeshCount;
		IndexBuffer[] submeshes = new IndexBuffer[submeshCount = mesh.getSubmeshCount()];
		Appearance[] appearances = new Appearance[submeshCount];

		for (int i = 0; i < submeshCount; ++i) {
			submeshes[i] = mesh.getIndexBuffer(i);
			appearances[i] = mesh.getAppearance(i);
		}

		SkinnedMesh skinnedMesh = new SkinnedMesh(mesh.getVertexBuffer(), submeshes, appearances, skeleton);
		copyMesh(mesh, skinnedMesh);
		int boneCount = (int) readUInt32LE(in);

		while (boneCount-- > 0) {
			Node bone = (Node) this.readReference(in);
			int firstVertex = (int) readUInt32LE(in);
			int vertexCount = (int) readUInt32LE(in);
			int weight = readInt32LE(in);
			skinnedMesh.addTransform(bone, weight, firstVertex, vertexCount);
		}

		return skinnedMesh;
	}

	private Sprite3D readSprite(InputStream in) throws IOException {
		Group nodeData = new Group();
		this.readNodeData(nodeData, in);
		Image2D image = (Image2D) this.readReference(in);
		Appearance appearance = (Appearance) this.readReference(in);
		boolean scaled = readBit(in);
		Sprite3D sprite = new Sprite3D(scaled, image, appearance);
		copyNode(nodeData, sprite);
		sprite.setCrop(readInt32LE(in), readInt32LE(in), readInt32LE(in), readInt32LE(in));
		return sprite;
	}

	private Texture2D readTexture2D(InputStream in) throws IOException {
		Group transformableData = new Group();
		this.readTransformableData(transformableData, in);
		Texture2D texture = new Texture2D((Image2D) this.readReference(in));
		copyTransformable(transformableData, texture);
		texture.setBlendColor(readRGB(in));
		texture.setBlending(readByte(in));
		texture.setWrapping(readByte(in), readByte(in));
		texture.setFiltering(readByte(in), readByte(in));
		return texture;
	}

	private TriangleStripArray readTriangleStripArray(InputStream in) throws IOException {
		AnimationController objectData;
		int encoding;
		int firstIndex;
		int[] indices;
		objectData = new AnimationController();
		this.readObject3DData(objectData, in);
		encoding = readByte(in);
		firstIndex = 0;
		indices = null;
		int index;
		label54:
		switch (encoding) {
			case 0:
				firstIndex = (int) readUInt32LE(in);
				break;
			case 1:
				firstIndex = readByte(in);
				break;
			case 2:
				firstIndex = readInt16LE(in);
				break;
			case 128:
				indices = new int[(int) readUInt32LE(in)];
				index = 0;

				while (true) {
					if (index >= indices.length) {
						break label54;
					}

					indices[index] = (int) readUInt32LE(in);
					++index;
				}
			case 129:
				indices = new int[(int) readUInt32LE(in)];
				index = 0;

				while (true) {
					if (index >= indices.length) {
						break label54;
					}

					indices[index] = readByte(in);
					++index;
				}
			case 130:
				indices = new int[(int) readUInt32LE(in)];
				index = 0;

				while (true) {
					if (index >= indices.length) {
						break label54;
					}

					indices[index] = readInt16LE(in);
					++index;
				}
			default:
				throw new IllegalArgumentException("Invalid TriangleStripArray encoding (" + this.resourceName + ").");
		}

		int[] stripLengths = new int[(int) readUInt32LE(in)];

		for (int i = 0; i < stripLengths.length; ++i) {
			stripLengths[i] = (int) readUInt32LE(in);
		}

		TriangleStripArray triangleStripArray = null;
		triangleStripArray = encoding != 0 && encoding != 1 && encoding != 2 ? new TriangleStripArray(indices, stripLengths) : new TriangleStripArray(firstIndex, stripLengths);
		copyObject3D(objectData, triangleStripArray);
		return triangleStripArray;
	}

	private VertexArray readVertexArray(InputStream in) throws IOException {
		AnimationController objectData = new AnimationController();
		this.readObject3DData(objectData, in);
		int componentSize = readByte(in);
		int componentCount = readByte(in);
		int encoding = readByte(in);
		int vertexCount = readInt16LE(in);
		if (encoding != 0 && encoding != 1) {
			throw new IllegalArgumentException("Invalid VertexArray encoding (" + this.resourceName + ").");
		} else {
			VertexArray vertexArray = new VertexArray(vertexCount, componentCount, componentSize);
			int[] accumulator = new int[componentCount];
			int vertex;
			int component;
			if (componentSize == 1) {
				byte[] byteValues = new byte[componentCount];
				if (encoding == 0) {
					for (vertex = 0; vertex < vertexCount; ++vertex) {
						for (component = 0; component < componentCount; ++component) {
							byteValues[component] = (byte) readByte(in);
						}

						vertexArray.set(vertex, 1, byteValues);
					}
				} else {
					for (vertex = 0; vertex < vertexCount; ++vertex) {
						for (component = 0; component < componentCount; ++component) {
							accumulator[component] += (byte) readByte(in);
							byteValues[component] = (byte) accumulator[component];
						}

						vertexArray.set(vertex, 1, byteValues);
					}
				}
			} else {
				short[] shortValues = new short[componentCount];
				if (encoding == 0) {
					for (vertex = 0; vertex < vertexCount; ++vertex) {
						for (component = 0; component < componentCount; ++component) {
							shortValues[component] = (short) readInt16LE(in);
						}

						vertexArray.set(vertex, 1, shortValues);
					}
				} else {
					for (vertex = 0; vertex < vertexCount; ++vertex) {
						for (component = 0; component < componentCount; ++component) {
							accumulator[component] += (short) readInt16LE(in);
							shortValues[component] = (short) accumulator[component];
						}

						vertexArray.set(vertex, 1, shortValues);
					}
				}
			}

			copyObject3D(objectData, vertexArray);
			return vertexArray;
		}
	}

	private VertexBuffer readVertexBuffer(InputStream in) throws IOException {
		VertexBuffer vertexBuffer = new VertexBuffer();
		this.readObject3DData(vertexBuffer, in);
		vertexBuffer.setDefaultColor(readRGBA(in));
		VertexArray positions = (VertexArray) this.readReference(in);
		float[] bias = new float[3];

		for (int i = 0; i < 3; ++i) {
			bias[i] = readFloat32LE(in);
		}

		float positionScale = readFloat32LE(in);
		if (positions != null) {
			vertexBuffer.setPositions(positions, positionScale, bias);
		}

		VertexArray normals;
		if ((normals = (VertexArray) this.readReference(in)) != null) {
			vertexBuffer.setNormals(normals);
		}

		VertexArray colors;
		if ((colors = (VertexArray) this.readReference(in)) != null) {
			vertexBuffer.setColors(colors);
		}

		int texCoordArrayCount = (int) readUInt32LE(in);

		for (int unit = 0; unit < texCoordArrayCount; ++unit) {
			VertexArray texCoords;
			if ((texCoords = (VertexArray) this.readReference(in)) == null) {
				throw new IOException("Null texture vertex array");
			}

			for (int i = 0; i < 3; ++i) {
				bias[i] = readFloat32LE(in);
			}

			float texCoordScale = readFloat32LE(in);
			vertexBuffer.setTexCoords(unit, texCoords, texCoordScale, bias);
		}

		return vertexBuffer;
	}

	private World readWorld(InputStream in) throws IOException {
		World world = new World();
		this.readGroupData(world, in);
		Camera camera;
		if ((camera = (Camera) this.readReference(in)) != null) {
			world.setActiveCamera(camera);
		}

		world.setBackground((Background) this.readReference(in));
		return world;
	}

	private Object3D readReference(InputStream in) throws IOException {
		return this.getLoaded((int) readUInt32LE(in));
	}

	private static final Transform readTransform(InputStream in) throws IOException {
		Transform transform = new Transform();
		float[] matrix = new float[16];

		for (int i = 0; i < 16; ++i) {
			matrix[i] = readFloat32LE(in);
		}

		transform.set(matrix);
		return transform;
	}

	private static final int readByte(InputStream in) throws IOException {
		return in.read();
	}

	private static boolean readBit(InputStream in) throws IOException {
		int value;
		if ((value = in.read()) == 0) {
			return false;
		} else if (value != 1) {
			throw new IOException("Malformed boolean.");
		} else {
			return true;
		}
	}

	private static int readInt16LE(InputStream in) throws IOException {
		return in.read() + (in.read() << 8);
	}

	private static final int readInt32LE(InputStream in) throws IOException {
		return in.read() + (in.read() << 8) + (in.read() << 16) + (in.read() << 24);
	}

	private static final long readUInt32LE(InputStream in) throws IOException {
		return (long) in.read() + ((long) in.read() << 8) + ((long) in.read() << 16) + ((long) in.read() << 24);
	}

	private static final float readFloat32LE(InputStream in) throws IOException {
		int bits;
		if (((bits = readInt32LE(in)) & 0x7f800000) != 0x7f800000 && bits != Integer.MIN_VALUE && ((bits & 0x7fffff) == 0 || (bits & 0x7f800000) != 0)) {
			return Float.intBitsToFloat(bits);
		} else {
			throw new IOException("Malformed float.");
		}
	}

	private static int readRGBA(InputStream in) throws IOException {
		return (in.read() << 16) + (in.read() << 8) + in.read() + (in.read() << 24);
	}

	private static int readRGB(InputStream in) throws IOException {
		return (in.read() << 16) + (in.read() << 8) + in.read();
	}

	private static String readString(InputStream in) throws IOException {
		StringBuffer buffer = new StringBuffer();

		int firstByte;
		for (InputStream stream = in; (firstByte = stream.read()) != 0; stream = in) {
			if ((firstByte & 128) == 0) {
				buffer.append((char) (firstByte & 255));
			} else {
				int secondByte;
				if ((firstByte & 224) == 192) {
					if (((secondByte = in.read()) & 192) != 128) {
						throw new IOException("Invalid UTF-8 string.");
					}

					buffer.append((char) ((firstByte & 31) << 6 | secondByte & 63));
				} else {
					if ((firstByte & 240) != 224) {
						throw new IOException("Invalid UTF-8 string.");
					}

					secondByte = in.read();
					int thirdByte = in.read();
					if ((secondByte & 192) != 128 || (thirdByte & 192) != 128) {
						throw new IOException("Invalid UTF-8 string.");
					}

					buffer.append((char) ((firstByte & 15) << 12 | (secondByte & 63) << 6 | thirdByte & 63));
				}
			}
		}

		return buffer.toString();
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

	private static int getFileType(InputStream in) throws IOException {
		byte[] header = new byte[12];
		in.read(header);
		return getInnerFileType(header, 0);
	}

	private boolean inFileHistory(String name) {
		for (int i = 0; i < this.fileHistory.size(); ++i) {
			if (((String) this.fileHistory.elementAt(i)).equals(name)) {
				return true;
			}
		}

		return false;
	}

	private static InputStream getHttpInputStream(String url) throws IOException {
		InputConnection connection;
		HttpConnection httpConnection;
		String contentType;
		if ((connection = (InputConnection) Connector.open(url)) instanceof HttpConnection && (contentType = (httpConnection = (HttpConnection) connection).getHeaderField("Content-Type")) != null && !contentType.equals("application/m3g") && !contentType.equals("image/png")) {
			throw new IOException("Wrong MIME type: " + contentType);
		} else {
			return connection.openInputStream();
		}
	}

	private InputStream getInputStream(String name) throws IOException {
		if (name.indexOf(58) != -1) {
			return getHttpInputStream(name);
		} else if (name.charAt(0) == 47) {
			return ResourceManager.getResourceAsStream(name);
		} else if (this.parentResourceName == null) {
			throw new IOException("Relative URI.");
		} else {
			String absoluteName;
			return (absoluteName = this.parentResourceName.substring(0, this.parentResourceName.lastIndexOf(47) + 1) + name).charAt(0) == 47 ? ResourceManager.getResourceAsStream(absoluteName) : getHttpInputStream(absoluteName);
		}
	}

	private static void copyObject3D(Object3D source, Object3D target) {
		target.setUserObject(source.getUserObject());
		target.setUserID(source.getUserID());
	}

	private static void copyNode(Node source, Node target) {
		copyTransformable(source, target);
		target.setAlphaFactor(source.getAlphaFactor());
		target.setScope(source.getScope());
		target.setPickingEnable(source.isPickingEnabled());
		target.setRenderingEnable(source.isRenderingEnabled());
	}

	private static void copyTransformable(Transformable source, Transformable target) {
		copyObject3D(source, target);
		float[] values = new float[4];
		Transform transform = new Transform();
		source.getTranslation(values);
		target.setTranslation(values[0], values[1], values[2]);
		source.getScale(values);
		target.setScale(values[0], values[1], values[2]);
		source.getOrientation(values);
		target.setOrientation(values[0], values[1], values[2], values[3]);
		source.getTransform(transform);
		target.setTransform(transform);
	}

	private static void copyMesh(Mesh source, Mesh target) {
		copyNode(source, target);
	}

	private static void inflate(byte[] compressed, byte[] uncompressed) {
		try {
			Inflater inflater = new Inflater(false);
			inflater.setInput(compressed);
			inflater.inflate(uncompressed);
			inflater.end();
		} catch (Exception e) {
			Emulator.getEmulator().getLogStream().println("m3g unzip error");
		}
	}
}
