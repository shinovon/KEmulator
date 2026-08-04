package emulator.ui.swt;

import emulator.graphics3D.m3g.BoneTransform;
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
 * Supported:
 *  - Full scene graph (Group/Mesh/Sprite3D/Light/Camera)
 *  - Materials (baseColor incl. alpha, emissive, alphaMode, doubleSided, texture + tiling)
 *  - Lights via KHR_lights_punctual (directional/point/spot; ambient is skipped, no glTF analog)
 *  - Camera (perspective/orthographic; GENERIC projection type is skipped, no analog)
 *  - MorphingMesh -> glTF morph targets (delta-encoded, see derivation below)
 *  - SkinnedMesh -> glTF skin/joints (bind-pose only, no animation - see caveats below)
 *
 * Limitations (by design):
 *  - No animations (KeyframeSequence / AnimationController / AnimationTrack are ignored) -
 *    exported skins/morphs only reproduce the CURRENT bind-pose/weights, not playback
 *  - Sprite3D is exported as a flat quad using its current node transform
 *    (billboard behaviour is NOT preserved, since glTF has no runtime billboarding)
 *  - Ambient lights are NOT exported (KHR_lights_punctual has no ambient light type)
 *  - Light attenuation coefficients (constant/linear/quadratic) are NOT mapped;
 *    exported lights use glTF's default infinite-range inverse-square falloff
 *  - Fog is NOT exported (no equivalent concept in core glTF 2.0)
 *  - Vertex colors (COLOR_0) are always multiplicative with baseColorFactor per glTF spec,
 *    which only approximates M3G's GL_COLOR_MATERIAL vertex-color-tracking behaviour
 *  - Color bytes are copied as-is without sRGB/linear conversion (visually close, not exact)
 */
public final class GltfExporter {

    public static void export(Node root, File outFile) throws IOException {
        new GltfExporter().doExport(root, outFile);
    }

    private static final float[] IDENTITY_MATRIX = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    // ---------- export state ----------
    private final ByteArrayOutputStream bin = new ByteArrayOutputStream();
    private final List<Object> gNodes = new ArrayList<>();
    private final List<Object> gMeshes = new ArrayList<>();
    private final List<Object> gAccessors = new ArrayList<>();
    private final List<Object> gBufferViews = new ArrayList<>();
    private final List<Object> gMaterials = new ArrayList<>();
    private final List<Object> gTextures = new ArrayList<>();
    private final List<Object> gImages = new ArrayList<>();
    private final List<Object> gSamplers = new ArrayList<>();
    private final List<Object> gLights = new ArrayList<>();
    private final List<Object> gCameras = new ArrayList<>();  // NEW
    private final List<Object> gSkins = new ArrayList<>();    // NEW

    private final IdentityHashMap<Appearance, Integer> materialCache = new IdentityHashMap<>();
    private final IdentityHashMap<Image2D, Integer> imageCache = new IdentityHashMap<>();
    private final Map<Long, Integer> samplerCache = new HashMap<>();
    private final IdentityHashMap<Light, Integer> lightCache = new IdentityHashMap<>();
    private final IdentityHashMap<Camera, Integer> cameraCache = new IdentityHashMap<>(); // NEW
    private final IdentityHashMap<Node, Integer> nodeIndexMap = new IdentityHashMap<>();  // NEW: M3G Node -> gltf node index

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

        // CHANGED: SkinnedMesh must be checked before the generic Mesh branch (inheritance)
        if (node instanceof SkinnedMesh) {
            SkinnedMesh sm = (SkinnedMesh) node;
            gnode.put("mesh", exportMesh(sm));

            // NEW: export skeleton subtree as children of this node. SkinnedMesh sets
            // skeleton.parent = this internally, so computing bone transforms relative
            // to this node (via getTransformTo) is consistent with that convention.
            //
            // IMPORTANT: per glTF convention, a node with "skin" should have an identity
            // local transform, since most runtimes apply the node's own TRS on top of the
            // skin result, which would double-transform the mesh otherwise. The skeleton's
            // own bind-pose transforms (computed relative to this now-identity node) fully
            // reproduce the correct static appearance.
            gnode.put("matrix", floatList(IDENTITY_MATRIX));

            List<Integer> children = new ArrayList<>();
            children.add(exportNode(sm.getSkeleton(), sm));
            gnode.put("children", children);

            // NEW: skin/joints are best-effort. If a referenced bone wasn't found in the
            // exported skeleton subtree, exportSkin() returns -1 and we simply keep the
            // static (bind-pose) geometry, which is always correct regardless.
            int skinIdx = exportSkin(sm);
            if (skinIdx >= 0) gnode.put("skin", skinIdx);

        } else if (node instanceof Mesh) {
            gnode.put("mesh", exportMesh((Mesh) node));
        } else if (node instanceof Sprite3D) {
            gnode.put("mesh", exportSpriteAsQuad((Sprite3D) node));
        } else if (node instanceof Light) {
            int lightIdx = exportLight((Light) node);
            if (lightIdx >= 0) {
                Map<String, Object> ext = new LinkedHashMap<>();
                Map<String, Object> khr = new LinkedHashMap<>();
                khr.put("light", lightIdx);
                ext.put("KHR_lights_punctual", khr);
                gnode.put("extensions", ext);
            }
        } else if (node instanceof Camera) { // NEW
            int camIdx = exportCamera((Camera) node);
            if (camIdx >= 0) gnode.put("camera", camIdx);
        }

        if (node instanceof Group) {
            Group g = (Group) node;
            List<Integer> children = new ArrayList<>();
            int cc = g.getChildCount();
            for (int i = 0; i < cc; i++) {
                Node child = g.getChild(i);
                if (child == null) continue;
                // CHANGED: cameras and lights are no longer skipped - they're now exported
                children.add(exportNode(child, node));
            }
            if (!children.isEmpty()) {
                // NOTE: SkinnedMesh above already may have set "children" (the skeleton) -
                // Group and SkinnedMesh are mutually exclusive in M3G, so no conflict here.
                gnode.put("children", children);
            }
        }

        gNodes.add(gnode);
        int idx = gNodes.size() - 1;
        nodeIndexMap.put(node, idx); // NEW: needed for skin joints lookup
        return idx;
    }

    // ---------- camera export (NEW) ----------
    private int exportCamera(Camera cam) {
        Integer cached = cameraCache.get(cam);
        if (cached != null) return cached;

        float[] proj = new float[4];
        int type = cam.getProjection(proj);
        if (type == Camera.GENERIC) {
            // Arbitrary projection matrix - has no equivalent in glTF's
            // perspective/orthographic camera model. Skipped.
            return -1;
        }

        Map<String, Object> gcam = new LinkedHashMap<>();
        if (type == Camera.PERSPECTIVE) {
            Map<String, Object> persp = new LinkedHashMap<>();
            persp.put("yfov", Math.toRadians(proj[0]));
            persp.put("aspectRatio", (double) proj[1]);
            persp.put("znear", (double) proj[2]);
            persp.put("zfar", (double) proj[3]);
            gcam.put("type", "perspective");
            gcam.put("perspective", persp);
        } else { // PARALLEL
            float height = proj[0];
            float aspect = proj[1];
            Map<String, Object> ortho = new LinkedHashMap<>();
            ortho.put("xmag", (double) (aspect * height / 2f));
            ortho.put("ymag", (double) (height / 2f));
            ortho.put("znear", (double) proj[2]);
            ortho.put("zfar", (double) proj[3]);
            gcam.put("type", "orthographic");
            gcam.put("orthographic", ortho);
        }

        gCameras.add(gcam);
        int idx = gCameras.size() - 1;
        cameraCache.put(cam, idx);
        return idx;
    }

    // ---------- light export ----------
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
                return -1; // no ambient equivalent in KHR_lights_punctual
        }

        float[] col = new float[3];
        int c = light.getColor();
        col[0] = ((c >> 16) & 0xFF) / 255f;
        col[1] = ((c >> 8) & 0xFF) / 255f;
        col[2] = (c & 0xFF) / 255f;

        Map<String, Object> gl = new LinkedHashMap<>();
        gl.put("type", type);
        gl.put("color", floatList(col));
        gl.put("intensity", (double) light.getIntensity());

        if (light.getMode() == Light.SPOT) {
            Map<String, Object> spot = new LinkedHashMap<>();
            float outer = (float) Math.toRadians(light.getSpotAngle());
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
        float[] basePositions = readVecN(posArr, posSB, vertexCount, 3);

        float[] baseNormals = null;
        VertexArray normArr = vb.getNormals();
        if (normArr != null) {
            float[] noSB = {1f, 0f, 0f, 0f};
            baseNormals = readVecN(normArr, noSB, vertexCount, 3);
            normalize3(baseNormals);
        }

        float[] baseUvs = null;
        float[] uvSB = new float[4];
        VertexArray uvArr = vb.getTexCoords(0, uvSB);
        if (uvArr != null) baseUvs = readVecN(uvArr, uvSB, vertexCount, 2);

        float[] colors = null;
        VertexArray colArr = vb.getColors();
        if (colArr != null) colors = readColorsRGBA(colArr, vertexCount);

        int posAcc = writeAccessor(basePositions, 3, true);
        int normAcc = baseNormals != null ? writeAccessor(baseNormals, 3, false) : -1;
        int colAcc = colors != null ? writeAccessor(colors, 4, false) : -1;

        // NEW: skinning accessors, computed once for the whole mesh (shared across submeshes)
        int jointsAcc = -1, weightsAcc = -1;
        if (mesh instanceof SkinnedMesh) {
            int[] skinAcc = writeSkinningAccessors((SkinnedMesh) mesh, vertexCount);
            jointsAcc = skinAcc[0];
            weightsAcc = skinAcc[1];
        }

        // NEW: morph targets, delta-encoded.
        // M3G's MorphingMesh formula: final = baseWeight*base + sum(weight_i * target_i),
        // where baseWeight = 1 - sum(weight_i). This is algebraically identical to glTF's
        // final = base + sum(weight_i * (target_i - base)), so we simply export per-target
        // position/normal DELTAS and pass through the current weights unchanged.
        List<Object> morphTargets = null;
        List<Float> currentWeights = null;
        if (mesh instanceof MorphingMesh) {
            MorphingMesh mm = (MorphingMesh) mesh;
            int targetCount = mm.getMorphTargetCount();
            morphTargets = new ArrayList<>();
            float[] weightsArr = new float[targetCount];
            mm.getWeights(weightsArr);
            currentWeights = new ArrayList<>();
            for (float w : weightsArr) currentWeights.add(w);

            for (int ti = 0; ti < targetCount; ti++) {
                VertexBuffer targetVb = mm.getMorphTarget(ti);

                float[] tPosSB = new float[4];
                VertexArray tPosArr = targetVb.getPositions(tPosSB);
                float[] targetPositions = readVecN(tPosArr, tPosSB, vertexCount, 3);
                float[] posDelta = new float[targetPositions.length];
                for (int i = 0; i < posDelta.length; i++) posDelta[i] = targetPositions[i] - basePositions[i];

                Map<String, Object> target = new LinkedHashMap<>();
                target.put("POSITION", writeAccessor(posDelta, 3, false));

                VertexArray tNormArr = targetVb.getNormals();
                if (tNormArr != null && baseNormals != null) {
                    float[] tNoSB = {1f, 0f, 0f, 0f};
                    float[] targetNormals = readVecN(tNormArr, tNoSB, vertexCount, 3);
                    normalize3(targetNormals);
                    float[] normDelta = new float[targetNormals.length];
                    for (int i = 0; i < normDelta.length; i++) normDelta[i] = targetNormals[i] - baseNormals[i];
                    target.put("NORMAL", writeAccessor(normDelta, 3, false));
                }
                morphTargets.add(target);
            }
        }

        List<Object> primitives = new ArrayList<>();
        int submeshCount = mesh.getSubmeshCount();
        for (int i = 0; i < submeshCount; i++) {
            IndexBuffer ib = mesh.getIndexBuffer(i);
            Appearance ap = mesh.getAppearance(i);
            if (ib == null) continue;

            int[] triIndices = toTriangleList(ib);

            // NEW: fix triangle winding order to match glTF's CCW front-face convention
            PolygonMode pm = ap != null ? ap.getPolygonMode() : null;
            if (pm != null && pm.getWinding() == PolygonMode.WINDING_CW) {
                for (int k = 0; k + 2 < triIndices.length; k += 3) {
                    int tmp = triIndices[k + 1];
                    triIndices[k + 1] = triIndices[k + 2];
                    triIndices[k + 2] = tmp;
                }
            }

            Map<String, Object> attrs = new LinkedHashMap<>();
            attrs.put("POSITION", posAcc);
            if (normAcc >= 0) attrs.put("NORMAL", normAcc);
            if (colAcc >= 0) attrs.put("COLOR_0", colAcc);

            if (baseUvs != null) {
                Texture2D tex0 = ap != null ? ap.getTexture(0) : null;
                if (tex0 != null) {
                    float[] transformedUvs = baseUvs.clone();
                    applyTextureTransform(transformedUvs, tex0);
                    attrs.put("TEXCOORD_0", writeAccessor(transformedUvs, 2, false));
                } else {
                    attrs.put("TEXCOORD_0", writeAccessor(baseUvs, 2, false));
                }
            }

            if (jointsAcc >= 0) {
                attrs.put("JOINTS_0", jointsAcc);
                attrs.put("WEIGHTS_0", weightsAcc);
            }

            Map<String, Object> prim = new LinkedHashMap<>();
            prim.put("attributes", attrs);
            prim.put("indices", writeIndexAccessor(triIndices));
            prim.put("mode", 4); // TRIANGLES
            int matIdx = exportMaterial(ap);
            if (matIdx >= 0) prim.put("material", matIdx);
            if (morphTargets != null) prim.put("targets", morphTargets);
            primitives.add(prim);
        }

        Map<String, Object> gmesh = new LinkedHashMap<>();
        gmesh.put("primitives", primitives);
        if (currentWeights != null) gmesh.put("weights", currentWeights);
        gMeshes.add(gmesh);
        return gMeshes.size() - 1;
    }

    // ---------- skinning (NEW) ----------
    private int[] writeSkinningAccessors(SkinnedMesh mesh, int vertexCount) {
        int[] vtxBones = mesh.getVerticesBones();
        int[] vtxWeights = mesh.getVerticesWeights();
        final int slots = 4; // Emulator3D.MaxTransformsPerVertex

        int[] joints = new int[vertexCount * slots];
        float[] weights = new float[vertexCount * slots];

        for (int v = 0; v < vertexCount; v++) {
            int sum = 0;
            for (int s = 0; s < slots; s++) sum += vtxWeights[v * slots + s];
            for (int s = 0; s < slots; s++) {
                int boneId = vtxBones[v * slots + s]; // 0 = no bone, else boneTransList index + 1
                int w = vtxWeights[v * slots + s];
                joints[v * slots + s] = boneId > 0 ? boneId - 1 : 0;
                weights[v * slots + s] = (boneId > 0 && sum > 0) ? (float) w / sum : 0f;
            }
        }

        ByteBuffer jb = ByteBuffer.allocate(joints.length * 2).order(ByteOrder.LITTLE_ENDIAN);
        for (int j : joints) jb.putShort((short) j);
        int jointsBv = addBufferView(jb.array(), 34962); // ARRAY_BUFFER

        Map<String, Object> jointsAccObj = new LinkedHashMap<>();
        jointsAccObj.put("bufferView", jointsBv);
        jointsAccObj.put("componentType", 5123); // UNSIGNED_SHORT
        jointsAccObj.put("count", vertexCount);
        jointsAccObj.put("type", "VEC4");
        gAccessors.add(jointsAccObj);
        int jointsAccIdx = gAccessors.size() - 1;

        int weightsAccIdx = writeAccessor(weights, 4, false);

        return new int[]{jointsAccIdx, weightsAccIdx};
    }

    private int exportSkin(SkinnedMesh mesh) {
        Vector boneTransList = mesh.getTransforms();
        if (boneTransList.isEmpty()) return -1;

        List<Integer> joints = new ArrayList<>();
        List<Float> ibmFlat = new ArrayList<>();

        for (int i = 0; i < boneTransList.size(); i++) {
            BoneTransform bt = (BoneTransform) boneTransList.elementAt(i);
            Integer boneNodeIdx = nodeIndexMap.get(bt.bone);
            if (boneNodeIdx == null) {
                // Referenced bone wasn't found among exported nodes - abort skin export,
                // the mesh keeps its correct static bind-pose geometry regardless.
                return -1;
            }
            joints.add(boneNodeIdx);

            float[] m = m3gToGltfMatrix(bt.toBoneTrans);
            for (float f : m) ibmFlat.add(f);
        }

        ByteBuffer bb = ByteBuffer.allocate(ibmFlat.size() * 4).order(ByteOrder.LITTLE_ENDIAN);
        for (float f : ibmFlat) bb.putFloat(f);
        int bv = addBufferView(bb.array(), 0);

        Map<String, Object> acc = new LinkedHashMap<>();
        acc.put("bufferView", bv);
        acc.put("componentType", 5126); // FLOAT
        acc.put("count", joints.size());
        acc.put("type", "MAT4");
        gAccessors.add(acc);
        int ibmAccIdx = gAccessors.size() - 1;

        Map<String, Object> skin = new LinkedHashMap<>();
        skin.put("joints", joints);
        skin.put("inverseBindMatrices", ibmAccIdx);

        gSkins.add(skin);
        return gSkins.size() - 1;
    }

    // ---------- texture transform baking ----------
    private void applyTextureTransform(float[] uvs, Texture2D tex) {
        Transform t = new Transform();
        tex.getCompositeTransform(t);
        float[] m = new float[16];
        t.get(m);

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

    // ---------- Sprite3D exported as a flat quad ----------
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
            // CHANGED: the top byte of diffuseColor IS the material's alpha channel
            // (confirmed by Material's default value 0xFFCCCCCC and by
            // Material.updateProperty's ALPHA case masking exactly 0xFF000000).
            // Using >>> since diffuse may be a negative int (top bit set).
            baseColor[0] = ((diffuse >> 16) & 0xFF) / 255f;
            baseColor[1] = ((diffuse >> 8) & 0xFF) / 255f;
            baseColor[2] = (diffuse & 0xFF) / 255f;
            baseColor[3] = ((diffuse >>> 24) & 0xFF) / 255f;
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

        // NEW: alphaMode from CompositingMode
        CompositingMode cm = ap.getCompositingMode();
        if (cm != null) {
            if (cm.getBlending() != CompositingMode.REPLACE) {
                mat.put("alphaMode", "BLEND");
            } else if (cm.getAlphaThreshold() > 0.0f) {
                mat.put("alphaMode", "MASK");
                mat.put("alphaCutoff", (double) cm.getAlphaThreshold());
            }
        }

        // CHANGED: doubleSided now reflects actual PolygonMode culling
        // (default PolygonMode culling is CULL_BACK, i.e. single-sided!)
        PolygonMode pm = ap.getPolygonMode();
        boolean doubleSided = pm != null && pm.getCulling() == PolygonMode.CULL_NONE;
        mat.put("doubleSided", doubleSided);

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
        texObj.put("sampler", getSampler(tex));
        gTextures.add(texObj);
        return gTextures.size() - 1;
    }

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
        int bv = addBufferView(bb.array(), 34962);

        Map<String, Object> acc = new LinkedHashMap<>();
        acc.put("bufferView", bv);
        acc.put("componentType", 5126);
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
            componentType = 5123;
        } else {
            bb = ByteBuffer.allocate(indices.length * 4).order(ByteOrder.LITTLE_ENDIAN);
            for (int i : indices) bb.putInt(i);
            componentType = 5125;
        }
        int bv = addBufferView(bb.array(), 34963);
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
        if (!gSamplers.isEmpty()) root.put("samplers", gSamplers);
        if (!gCameras.isEmpty()) root.put("cameras", gCameras); // NEW
        if (!gSkins.isEmpty()) root.put("skins", gSkins);       // NEW
        root.put("accessors", gAccessors);
        root.put("bufferViews", gBufferViews);
        root.put("buffers", Collections.singletonList(buffer));

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
            writeLE(out, 0x46546C67);
            writeLE(out, 2);
            writeLE(out, totalLen);

            writeLE(out, jsonChunkLen);
            writeLE(out, 0x4E4F534A);
            out.write(jsonBytes);
            for (int i = 0; i < jsonPad; i++) out.write(0x20);

            writeLE(out, binChunkLen);
            writeLE(out, 0x004E4942);
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

    // ---------- minimal JSON writer ----------
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