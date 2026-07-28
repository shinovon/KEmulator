package emulator.ui.swt;

import javax.imageio.ImageIO;
import javax.microedition.m3g.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * Minimal glTF 2.0 (.glb) scene exporter for the M3G scene viewer.
 * No external glTF libraries used - JSON and binary buffer are built by hand.
 *
 * Limitations (by design):
 *  - No skinning / bones (SkinnedMesh is exported as a static bind-pose Mesh)
 *  - No animations (KeyframeSequence / AnimationController are ignored)
 *  - Sprite3D is exported as a flat quad using its current node transform
 *    (billboard behaviour is NOT preserved, since glTF has no runtime billboarding)
 *  - Ambient lights are NOT exported (KHR_lights_punctual has no ambient light type)
 *  - Light attenuation coefficients (constant/linear/quadratic) are NOT mapped;
 *    exported lights use glTF's default infinite-range inverse-square falloff
 */
public final class GltfExporter {

    public static void export(Node root, File outFile) throws IOException {
        new GltfExporter().doExport(root, outFile);
    }

    // ---------- export state ----------
    private final ByteArrayOutputStream bin = new ByteArrayOutputStream();
    private final List<Object> gNodes = new ArrayList<>();
    private final List<Object> gMeshes = new ArrayList<>();
    private final List<Object> gAccessors = new ArrayList<>();
    private final List<Object> gBufferViews = new ArrayList<>();
    private final List<Object> gMaterials = new ArrayList<>();
    private final List<Object> gTextures = new ArrayList<>();
    private final List<Object> gImages = new ArrayList<>();
    private final List<Object> gSamplers = new ArrayList<>(); // NEW: texture wrap modes
    private final List<Object> gLights = new ArrayList<>();   // NEW: KHR_lights_punctual lights

    private final IdentityHashMap<Appearance, Integer> materialCache = new IdentityHashMap<>();
    private final IdentityHashMap<Image2D, Integer> imageCache = new IdentityHashMap<>();
    private final Map<Long, Integer> samplerCache = new HashMap<>();     // NEW: (wrapS,wrapT) -> sampler index
    private final IdentityHashMap<Light, Integer> lightCache = new IdentityHashMap<>(); // NEW

    // ---------- entry point ----------
    private void doExport(Node root, File outFile) throws IOException {
        int rootIdx = exportNode(root, null);
        writeGlb(outFile, rootIdx);
    }

    // ---------- scene graph traversal ----------
    private int exportNode(Node node, Node parent) {
        Map<String, Object> gnode = new LinkedHashMap<>();

        Transform t = new Transform();
        if (parent != null) {
            node.getTransformTo(parent, t);
        } else {
            t.setIdentity();
        }
        gnode.put("matrix", floatList(m3gToGltfMatrix(t)));
        gnode.put("name", node.getClass().getSimpleName() + "_" + node.getUserID());

        if (node instanceof Mesh) {
            gnode.put("mesh", exportMesh((Mesh) node));
        } else if (node instanceof Sprite3D) {
            gnode.put("mesh", exportSpriteAsQuad((Sprite3D) node));
        } else if (node instanceof Light) {
            // NEW: export light source via KHR_lights_punctual, attached to this node.
            // The node itself (with its transform) is still added to the scene graph
            // so the light's position/orientation is preserved.
            int lightIdx = exportLight((Light) node);
            if (lightIdx >= 0) {
                Map<String, Object> ext = new LinkedHashMap<>();
                Map<String, Object> khr = new LinkedHashMap<>();
                khr.put("light", lightIdx);
                ext.put("KHR_lights_punctual", khr);
                gnode.put("extensions", ext);
            }
        }

        if (node instanceof Group) {
            Group g = (Group) node;
            List<Integer> children = new ArrayList<>();
            int cc = g.getChildCount();
            for (int i = 0; i < cc; i++) {
                Node child = g.getChild(i);
                if (child == null) continue;
                if (child instanceof Camera) continue; // CHANGED: only cameras are skipped now, lights are exported
                children.add(exportNode(child, node));
            }
            if (!children.isEmpty()) gnode.put("children", children);
        }

        gNodes.add(gnode);
        return gNodes.size() - 1;
    }

    // ---------- light export (NEW) ----------
    private int exportLight(Light light) {
        Integer cached = lightCache.get(light);
        if (cached != null) return cached;

        String type;
        switch (light.getMode()) {
            case Light.DIRECTIONAL: type = "directional"; break;
            case Light.OMNI:        type = "point"; break;
            case Light.SPOT:        type = "spot"; break;
            case Light.AMBIENT:
            default:
                // KHR_lights_punctual has no ambient light type - skip.
                return -1;
        }

        float[] col = new float[3];
        int c = light.getColor(); // packed 0x00RRGGBB, no alpha channel used
        col[0] = ((c >> 16) & 0xFF) / 255f;
        col[1] = ((c >> 8) & 0xFF) / 255f;
        col[2] = (c & 0xFF) / 255f;

        Map<String, Object> gl = new LinkedHashMap<>();
        gl.put("type", type);
        gl.put("color", floatList(col));
        // NOTE: M3G intensity is a unitless multiplier; glTF expects candela (point/spot)
        // or lux (directional). Passed through as best-effort approximation - re-tune
        // in your DCC tool if lights look too dim/bright after import.
        gl.put("intensity", (double) light.getIntensity());

        if (light.getMode() == Light.SPOT) {
            Map<String, Object> spot = new LinkedHashMap<>();
            float outer = (float) Math.toRadians(light.getSpotAngle());
            // M3G has no separate inner cone angle, only a single cutoff angle plus
            // an exponent controlling falloff sharpness. Approximate inner cone from
            // spotExponent: higher exponent => tighter hotspot => inner closer to outer.
            float innerFactor = 1.0f - Math.min(light.getSpotExponent() / 128f, 0.9f);
            spot.put("innerConeAngle", outer * innerFactor);
            spot.put("outerConeAngle", outer);
            gl.put("spot", spot);
        }

        gLights.add(gl);
        int idx = gLights.size() - 1;
        lightCache.put(light, idx);
        return idx;
    }

    // ---------- matrix conversion ----------
    private static float[] m3gToGltfMatrix(Transform t) {
        float[] src = new float[16];
        t.get(src);
        float[] dst = new float[16];
        for (int row = 0; row < 4; row++)
            for (int col = 0; col < 4; col++)
                dst[col * 4 + row] = src[row * 4 + col];
        return dst;
    }

    // ---------- mesh export ----------
    private int exportMesh(Mesh mesh) {
        VertexBuffer vb = mesh.getVertexBuffer();

        float[] posSB = new float[4];
        VertexArray posArr = vb.getPositions(posSB);
        int vertexCount = posArr.getVertexCount();
        float[] positions = readVecN(posArr, posSB, vertexCount, 3);

        float[] normals = null;
        VertexArray normArr = vb.getNormals();
        if (normArr != null) {
            float[] noSB = {1f, 0f, 0f, 0f};
            normals = readVecN(normArr, noSB, vertexCount, 3);
            normalize3(normals);
        }

        // CHANGED: keep raw UVs (without texture's own transform) - the per-texture
        // transform is now applied per-submesh, since different submeshes of the
        // same mesh may use different Texture2D transforms (tiling scale etc).
        float[] baseUvs = null;
        float[] uvSB = new float[4];
        VertexArray uvArr = vb.getTexCoords(0, uvSB);
        if (uvArr != null) baseUvs = readVecN(uvArr, uvSB, vertexCount, 2);

        float[] colors = null;
        VertexArray colArr = vb.getColors();
        if (colArr != null) colors = readColorsRGBA(colArr, vertexCount);

        int posAcc = writeAccessor(positions, 3, true);
        int normAcc = normals != null ? writeAccessor(normals, 3, false) : -1;
        int colAcc = colors != null ? writeAccessor(colors, 4, false) : -1;
        int identityUvAcc = -1; // lazily created accessor for submeshes without a texture

        List<Object> primitives = new ArrayList<>();
        int submeshCount = mesh.getSubmeshCount();
        for (int i = 0; i < submeshCount; i++) {
            IndexBuffer ib = mesh.getIndexBuffer(i);
            Appearance ap = mesh.getAppearance(i);
            if (ib == null) continue;

            int[] triIndices = toTriangleList(ib);

            Map<String, Object> attrs = new LinkedHashMap<>();
            attrs.put("POSITION", posAcc);
            if (normAcc >= 0) attrs.put("NORMAL", normAcc);
            if (colAcc >= 0) attrs.put("COLOR_0", colAcc);

            // CHANGED: bake this submesh's Texture2D transform (tiling scale/offset/
            // rotation) into a dedicated UV accessor, instead of reusing raw UVs.
            if (baseUvs != null) {
                Texture2D tex0 = ap != null ? ap.getTexture(0) : null;
                if (tex0 != null) {
                    float[] transformedUvs = baseUvs.clone();
                    applyTextureTransform(transformedUvs, tex0);
                    attrs.put("TEXCOORD_0", writeAccessor(transformedUvs, 2, false));
                } else {
                    if (identityUvAcc < 0) identityUvAcc = writeAccessor(baseUvs, 2, false);
                    attrs.put("TEXCOORD_0", identityUvAcc);
                }
            }

            Map<String, Object> prim = new LinkedHashMap<>();
            prim.put("attributes", attrs);
            prim.put("indices", writeIndexAccessor(triIndices));
            prim.put("mode", 4); // TRIANGLES
            int matIdx = exportMaterial(ap);
            if (matIdx >= 0) prim.put("material", matIdx);
            primitives.add(prim);
        }

        Map<String, Object> gmesh = new LinkedHashMap<>();
        gmesh.put("primitives", primitives);
        gMeshes.add(gmesh);
        return gMeshes.size() - 1;
    }

    // ---------- texture transform baking (NEW) ----------
    // Mirrors Emulator3D.draw()'s GL_TEXTURE matrix setup:
    //   glLoadMatrixf(texture2D.getCompositeTransform())
    //   glTranslatef(scaleBias[1], scaleBias[2], scaleBias[3])
    //   glScalef(scaleBias[0], scaleBias[0], scaleBias[0])
    // The scale/translate part (VertexArray fixed-point decode) is already applied
    // by readVecN() above; here we apply the remaining compositeTransform on top,
    // which is where M3G authors typically encode texture tiling (Texture2D extends
    // Transformable and can have its own scale/rotate/translate).
    private void applyTextureTransform(float[] uvs, Texture2D tex) {
        Transform t = new Transform();
        tex.getCompositeTransform(t);
        float[] m = new float[16];
        t.get(m); // row-major, translation at indices {3,7,11} (same convention as m3gToGltfMatrix)

        for (int i = 0; i < uvs.length; i += 2) {
            float u = uvs[i], v = uvs[i + 1];
            float nu = m[0] * u + m[1] * v + m[3];
            float nv = m[4] * u + m[5] * v + m[7];
            float nw = m[12] * u + m[13] * v + m[15];
            if (nw != 0f && nw != 1f) { nu /= nw; nv /= nw; }
            uvs[i] = nu;
            uvs[i + 1] = nv;
        }
    }

    // ---------- Sprite3D exported as a flat quad (billboarding is NOT preserved) ----------
    private int exportSpriteAsQuad(Sprite3D sprite) {
        float[] positions = {
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f,  0.5f, 0f,
                -0.5f,  0.5f, 0f
        };
        float[] uvs = {0, 1, 1, 1, 1, 0, 0, 0};
        int[] indices = {0, 1, 2, 0, 2, 3};

        int posAcc = writeAccessor(positions, 3, true);
        int uvAcc = writeAccessor(uvs, 2, false);

        Map<String, Object> attrs = new LinkedHashMap<>();
        attrs.put("POSITION", posAcc);
        attrs.put("TEXCOORD_0", uvAcc);

        Map<String, Object> prim = new LinkedHashMap<>();
        prim.put("attributes", attrs);
        prim.put("indices", writeIndexAccessor(indices));
        prim.put("mode", 4);

        Appearance ap = sprite.getAppearance();
        int matIdx = exportMaterial(ap);
        if (matIdx >= 0) prim.put("material", matIdx);

        Map<String, Object> gmesh = new LinkedHashMap<>();
        gmesh.put("primitives", Collections.singletonList(prim));
        gMeshes.add(gmesh);
        return gMeshes.size() - 1;
    }

    // ---------- VertexArray reading ----------
    private float[] readVecN(VertexArray arr, float[] scaleBias, int vertexCount, int comps) {
        float scale = scaleBias[0] == 0 ? 1f : scaleBias[0];
        float biasX = scaleBias.length > 1 ? scaleBias[1] : 0f;
        float biasY = scaleBias.length > 2 ? scaleBias[2] : 0f;
        float biasZ = scaleBias.length > 3 ? scaleBias[3] : 0f;
        int arrComps = arr.getComponentCount();
        float[] out = new float[vertexCount * comps];

        if (arr.getComponentType() == 1) {
            byte[] raw = arr.getByteValues();
            fillVec(out, raw, scale, biasX, biasY, biasZ, vertexCount, arrComps, comps);
        } else {
            short[] raw = arr.getShortValues();
            fillVec(out, raw, scale, biasX, biasY, biasZ, vertexCount, arrComps, comps);
        }
        return out;
    }

    private void fillVec(float[] out, short[] raw, float scale, float bx, float by, float bz,
                         int vc, int arrComps, int comps) {
        float[] bias = {bx, by, bz};
        for (int v = 0; v < vc; v++)
            for (int c = 0; c < comps; c++)
                out[v * comps + c] = raw[v * arrComps + c] * scale + (c < 3 ? bias[c] : 0f);
    }

    private void fillVec(float[] out, byte[] raw, float scale, float bx, float by, float bz,
                         int vc, int arrComps, int comps) {
        float[] bias = {bx, by, bz};
        for (int v = 0; v < vc; v++)
            for (int c = 0; c < comps; c++)
                out[v * comps + c] = raw[v * arrComps + c] * scale + (c < 3 ? bias[c] : 0f);
    }

    private void normalize3(float[] data) {
        for (int i = 0; i < data.length; i += 3) {
            float x = data[i], y = data[i + 1], z = data[i + 2];
            float len = (float) Math.sqrt(x * x + y * y + z * z);
            if (len > 1e-6f) { data[i] /= len; data[i + 1] /= len; data[i + 2] /= len; }
        }
    }

    private float[] readColorsRGBA(VertexArray arr, int vertexCount) {
        int comps = arr.getComponentCount();
        byte[] raw = arr.getByteValues();
        float[] out = new float[vertexCount * 4];
        for (int v = 0; v < vertexCount; v++) {
            out[v * 4]     = (raw[v * comps]     & 0xFF) / 255f;
            out[v * 4 + 1] = (raw[v * comps + 1] & 0xFF) / 255f;
            out[v * 4 + 2] = (raw[v * comps + 2] & 0xFF) / 255f;
            out[v * 4 + 3] = comps == 4 ? (raw[v * comps + 3] & 0xFF) / 255f : 1f;
        }
        return out;
    }

    // ---------- triangle strip -> triangle list ----------
    private int[] toTriangleList(IndexBuffer ib) {
        if (!(ib instanceof TriangleStripArray)) {
            throw new UnsupportedOperationException("Unsupported IndexBuffer type: " + ib.getClass());
        }
        TriangleStripArray tsa = (TriangleStripArray) ib;
        int[] out = new int[tsa.getIndexCount()];
        tsa.getIndices(out);
        return out;
    }

    // ---------- materials / textures ----------
    private int exportMaterial(Appearance ap) {
        if (ap == null) return -1;
        Integer cached = materialCache.get(ap);
        if (cached != null) return cached;

        Map<String, Object> mat = new LinkedHashMap<>();
        Map<String, Object> pbr = new LinkedHashMap<>();

        float[] baseColor = {1f, 1f, 1f, 1f};
        Material m = ap.getMaterial();
        if (m != null) {
            int diffuse = m.getColor(Material.DIFFUSE);
            baseColor[0] = ((diffuse >> 16) & 0xFF) / 255f;
            baseColor[1] = ((diffuse >> 8) & 0xFF) / 255f;
            baseColor[2] = (diffuse & 0xFF) / 255f;
            baseColor[3] = 1f;
        }
        pbr.put("baseColorFactor", floatList(baseColor));
        pbr.put("metallicFactor", 0.0);
        pbr.put("roughnessFactor", 1.0);

        Texture2D tex = ap.getTexture(0);
        if (tex != null) {
            Image2D img = tex.getImage();
            Map<String, Object> texRef = new LinkedHashMap<>();
            texRef.put("index", exportTexture(img, tex));
            pbr.put("baseColorTexture", texRef);
        }

        mat.put("pbrMetallicRoughness", pbr);

        // NEW: emissive color
        if (m != null) {
            int emissive = m.getColor(Material.EMISSIVE);
            float er = ((emissive >> 16) & 0xFF) / 255f;
            float eg = ((emissive >> 8) & 0xFF) / 255f;
            float eb = (emissive & 0xFF) / 255f;
            if (er > 0f || eg > 0f || eb > 0f) {
                mat.put("emissiveFactor", floatList(new float[]{er, eg, eb}));
            }
        }

        // NEW: alpha mode from CompositingMode
        CompositingMode cm = ap.getCompositingMode();
        if (cm != null) {
            if (cm.getBlending() != CompositingMode.REPLACE) {
                mat.put("alphaMode", "BLEND");
            } else if (cm.getAlphaThreshold() > 0.0f) {
                mat.put("alphaMode", "MASK");
                mat.put("alphaCutoff", (double) cm.getAlphaThreshold());
            }
            // else: default OPAQUE, no need to write explicitly
        }

        mat.put("doubleSided", true); // TODO: read PolygonMode.getCulling() for accuracy
        gMaterials.add(mat);
        int idx = gMaterials.size() - 1;
        materialCache.put(ap, idx);
        return idx;
    }

    private int exportTexture(Image2D img, Texture2D tex) {
        Integer cachedImg = imageCache.get(img);
        int imageIndex;
        if (cachedImg != null) {
            imageIndex = cachedImg;
        } else {
            BufferedImage bi = toBufferedImage(img);
            byte[] png;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bi, "png", baos);
                png = baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int bv = addBufferView(png, 0);
            Map<String, Object> imgObj = new LinkedHashMap<>();
            imgObj.put("bufferView", bv);
            imgObj.put("mimeType", "image/png");
            gImages.add(imgObj);
            imageIndex = gImages.size() - 1;
            imageCache.put(img, imageIndex);
        }

        Map<String, Object> texObj = new LinkedHashMap<>();
        texObj.put("source", imageIndex);
        texObj.put("sampler", getSampler(tex)); // NEW: correct wrap mode (repeat/clamp)
        gTextures.add(texObj);
        return gTextures.size() - 1;
    }

    // ---------- sampler (wrap mode) export (NEW) ----------
    private static final int GLTF_REPEAT = 10497;
    private static final int GLTF_CLAMP_TO_EDGE = 33071;

    private int getSampler(Texture2D tex) {
        boolean clampS = tex.getWrappingS() == Texture2D.WRAP_CLAMP;
        boolean clampT = tex.getWrappingT() == Texture2D.WRAP_CLAMP;
        long key = (clampS ? 1 : 0) | ((clampT ? 1 : 0) << 1);

        Integer cached = samplerCache.get(key);
        if (cached != null) return cached;

        Map<String, Object> sampler = new LinkedHashMap<>();
        sampler.put("wrapS", clampS ? GLTF_CLAMP_TO_EDGE : GLTF_REPEAT);
        sampler.put("wrapT", clampT ? GLTF_CLAMP_TO_EDGE : GLTF_REPEAT);
        gSamplers.add(sampler);
        int idx = gSamplers.size() - 1;
        samplerCache.put(key, idx);
        return idx;
    }

    private BufferedImage toBufferedImage(Image2D img) {
        int w = img.getWidth();
        int h = img.getHeight();
        byte[] data = img.getImageData();
        int format = img.getFormat();
        int bpp = img.getBitsPerColor();

        int[] argb = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int idx = (y * w + x) * bpp;
                int a = 255, r = 0, g = 0, b = 0;
                switch (format) {
                    case Image2D.ALPHA:
                        a = data[idx] & 0xFF;
                        r = g = b = 255;
                        break;
                    case Image2D.LUMINANCE: {
                        int l = data[idx] & 0xFF;
                        r = g = b = l;
                        break;
                    }
                    case Image2D.LUMINANCE_ALPHA: {
                        int l = data[idx] & 0xFF;
                        a = data[idx + 1] & 0xFF;
                        r = g = b = l;
                        break;
                    }
                    case Image2D.RGB:
                        r = data[idx] & 0xFF;
                        g = data[idx + 1] & 0xFF;
                        b = data[idx + 2] & 0xFF;
                        break;
                    case Image2D.RGBA:
                        r = data[idx] & 0xFF;
                        g = data[idx + 1] & 0xFF;
                        b = data[idx + 2] & 0xFF;
                        a = data[idx + 3] & 0xFF;
                        break;
                    default:
                        break;
                }
                argb[y * w + x] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        bi.setRGB(0, 0, w, h, argb, 0, w);
        return bi;
    }

    // ---------- accessors / bufferViews / binary buffer ----------
    private void align4() {
        int pad = (4 - (bin.size() % 4)) % 4;
        for (int i = 0; i < pad; i++) bin.write(0);
    }

    private int addBufferView(byte[] data, int target) {
        align4();
        int offset = bin.size();
        bin.write(data, 0, data.length);
        Map<String, Object> bv = new LinkedHashMap<>();
        bv.put("buffer", 0);
        bv.put("byteOffset", offset);
        bv.put("byteLength", data.length);
        if (target != 0) bv.put("target", target);
        gBufferViews.add(bv);
        return gBufferViews.size() - 1;
    }

    private int writeAccessor(float[] data, int comps, boolean withBounds) {
        ByteBuffer bb = ByteBuffer.allocate(data.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        for (float f : data) bb.putFloat(f);
        int bv = addBufferView(bb.array(), 34962); // ARRAY_BUFFER

        Map<String, Object> acc = new LinkedHashMap<>();
        acc.put("bufferView", bv);
        acc.put("componentType", 5126); // FLOAT
        acc.put("count", data.length / comps);
        acc.put("type", comps == 2 ? "VEC2" : comps == 3 ? "VEC3" : "VEC4");

        if (withBounds) {
            float[] min = new float[comps], max = new float[comps];
            Arrays.fill(min, Float.MAX_VALUE);
            Arrays.fill(max, -Float.MAX_VALUE);
            for (int i = 0; i < data.length; i += comps)
                for (int c = 0; c < comps; c++) {
                    min[c] = Math.min(min[c], data[i + c]);
                    max[c] = Math.max(max[c], data[i + c]);
                }
            acc.put("min", floatList(min));
            acc.put("max", floatList(max));
        }
        gAccessors.add(acc);
        return gAccessors.size() - 1;
    }

    private int writeIndexAccessor(int[] indices) {
        int max = 0;
        for (int i : indices) max = Math.max(max, i);
        boolean useShort = max < 65536;
        ByteBuffer bb;
        int componentType;
        if (useShort) {
            bb = ByteBuffer.allocate(indices.length * 2).order(ByteOrder.LITTLE_ENDIAN);
            for (int i : indices) bb.putShort((short) i);
            componentType = 5123; // UNSIGNED_SHORT
        } else {
            bb = ByteBuffer.allocate(indices.length * 4).order(ByteOrder.LITTLE_ENDIAN);
            for (int i : indices) bb.putInt(i);
            componentType = 5125; // UNSIGNED_INT
        }
        int bv = addBufferView(bb.array(), 34963); // ELEMENT_ARRAY_BUFFER
        Map<String, Object> acc = new LinkedHashMap<>();
        acc.put("bufferView", bv);
        acc.put("componentType", componentType);
        acc.put("count", indices.length);
        acc.put("type", "SCALAR");
        gAccessors.add(acc);
        return gAccessors.size() - 1;
    }

    private static List<Float> floatList(float[] arr) {
        List<Float> list = new ArrayList<>(arr.length);
        for (float f : arr) list.add(f);
        return list;
    }

    // ---------- .glb writing ----------
    private void writeGlb(File outFile, int rootNodeIdx) throws IOException {
        Map<String, Object> asset = new LinkedHashMap<>();
        asset.put("version", "2.0");
        asset.put("generator", "M3GViewUI Exporter");

        Map<String, Object> buffer = new LinkedHashMap<>();
        buffer.put("byteLength", bin.size());

        Map<String, Object> scene = new LinkedHashMap<>();
        scene.put("nodes", Collections.singletonList(rootNodeIdx));

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("asset", asset);
        root.put("scene", 0);
        root.put("scenes", Collections.singletonList(scene));
        root.put("nodes", gNodes);
        if (!gMeshes.isEmpty()) root.put("meshes", gMeshes);
        if (!gMaterials.isEmpty()) root.put("materials", gMaterials);
        if (!gTextures.isEmpty()) root.put("textures", gTextures);
        if (!gImages.isEmpty()) root.put("images", gImages);
        if (!gSamplers.isEmpty()) root.put("samplers", gSamplers); // NEW
        root.put("accessors", gAccessors);
        root.put("bufferViews", gBufferViews);
        root.put("buffers", Collections.singletonList(buffer));

        // NEW: KHR_lights_punctual extension block
        if (!gLights.isEmpty()) {
            root.put("extensionsUsed", Collections.singletonList("KHR_lights_punctual"));
            Map<String, Object> khrLights = new LinkedHashMap<>();
            khrLights.put("lights", gLights);
            Map<String, Object> extensions = new LinkedHashMap<>();
            extensions.put("KHR_lights_punctual", khrLights);
            root.put("extensions", extensions);
        }

        byte[] jsonBytes = toJson(root).getBytes("UTF-8");
        int jsonPad = (4 - (jsonBytes.length % 4)) % 4;
        int jsonChunkLen = jsonBytes.length + jsonPad;

        byte[] binBytes = bin.toByteArray();
        int binPad = (4 - (binBytes.length % 4)) % 4;
        int binChunkLen = binBytes.length + binPad;

        int totalLen = 12 + 8 + jsonChunkLen + 8 + binChunkLen;

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)))) {
            writeLE(out, 0x46546C67); // "glTF"
            writeLE(out, 2);
            writeLE(out, totalLen);

            writeLE(out, jsonChunkLen);
            writeLE(out, 0x4E4F534A); // "JSON"
            out.write(jsonBytes);
            for (int i = 0; i < jsonPad; i++) out.write(0x20);

            writeLE(out, binChunkLen);
            writeLE(out, 0x004E4942); // "BIN\0"
            out.write(binBytes);
            for (int i = 0; i < binPad; i++) out.write(0);
        }
    }

    private void writeLE(DataOutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }

    // ---------- minimal JSON writer (no external dependency) ----------
    private static String toJson(Object o) {
        StringBuilder sb = new StringBuilder();
        writeJson(o, sb);
        return sb.toString();
    }

    private static void writeJson(Object o, StringBuilder sb) {
        if (o == null) { sb.append("null"); return; }
        if (o instanceof Map) {
            sb.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                if (!first) sb.append(',');
                first = false;
                sb.append('"').append(escape(e.getKey().toString())).append("\":");
                writeJson(e.getValue(), sb);
            }
            sb.append('}');
        } else if (o instanceof List) {
            sb.append('[');
            boolean first = true;
            for (Object v : (List<?>) o) {
                if (!first) sb.append(',');
                first = false;
                writeJson(v, sb);
            }
            sb.append(']');
        } else if (o instanceof String) {
            sb.append('"').append(escape((String) o)).append('"');
        } else if (o instanceof Number || o instanceof Boolean) {
            sb.append(o.toString());
        } else {
            sb.append('"').append(escape(o.toString())).append('"');
        }
    }

    private static String escape(String s) {
        StringBuilder r = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': r.append("\\\""); break;
                case '\\': r.append("\\\\"); break;
                case '\n': r.append("\\n"); break;
                default: r.append(c);
            }
        }
        return r.toString();
    }
}