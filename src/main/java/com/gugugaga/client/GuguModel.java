package com.gugugaga.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gugugaga.GuguMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gugu Gaga model. The geometry (model.json) and animations (animations.json) are
 * converted from the Bedrock addon and loaded at runtime. Each Bedrock cube has its own
 * per-face UVs, so the texture was repacked into a box-UV atlas (textures/entity/gugugaga.png).
 */
public class GuguModel extends EntityModel<LivingEntityRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(GuguMod.id("gugugaga"), "main");

    private static final JsonObject MODEL = load("model.json");
    private static final Map<String, Anim> ANIMS = loadAnims();

    private record Bone(ModelPart part, String name, float[] off, float[] rot, float[] scale) {}

    private final List<Bone> bones = new ArrayList<>();

    public GuguModel(ModelPart root) {
        super(root);
        for (JsonElement e : MODEL.getAsJsonArray("parts")) collect(root, e.getAsJsonObject());
    }

    private void collect(ModelPart parent, JsonObject o) {
        String n = o.get("n").getAsString();
        ModelPart p = parent.getChild(n);
        bones.add(new Bone(p, n, arr(o, "o"), arr(o, "r"), arr(o, "s")));
        for (JsonElement c : o.getAsJsonArray("c")) collect(p, c.getAsJsonObject());
    }

    // ---------- model building ----------
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        for (JsonElement e : MODEL.getAsJsonArray("parts")) build(root, e.getAsJsonObject());
        return LayerDefinition.create(mesh, MODEL.get("tw").getAsInt(), MODEL.get("th").getAsInt());
    }

    private static void build(PartDefinition parent, JsonObject o) {
        float[] off = arr(o, "o"), r = arr(o, "r");
        CubeListBuilder cb = CubeListBuilder.create();
        if (o.has("box")) {
            float[] b = arr(o, "box"), t = arr(o, "tex");
            cb.texOffs((int) t[0], (int) t[1]).addBox(b[0], b[1], b[2], b[3], b[4], b[5]);
        }
        PartDefinition pd = parent.addOrReplaceChild(o.get("n").getAsString(), cb,
                PartPose.offsetAndRotation(off[0], off[1], off[2], rad(r[0]), rad(r[1]), rad(r[2])));
        for (JsonElement c : o.getAsJsonArray("c")) build(pd, c.getAsJsonObject());
    }

    // ---------- animation ----------
    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks / 20.0f;
        float w = Mth.clamp(state.walkAnimationSpeed * 6.0f, 0.0f, 1.0f); // idle -> walk blend
        Anim idle = ANIMS.get("idle"), walk = ANIMS.get("walk");
        float[] a = new float[3], b = new float[3];
        for (Bone bone : bones) {
            float[] pos = new float[3], rt = new float[3], sc = {1, 1, 1};
            blend(idle, walk, bone.name, 'p', t, w, 0, pos, a, b);
            blend(idle, walk, bone.name, 'r', t, w, 0, rt, a, b);
            blend(idle, walk, bone.name, 's', t, w, 1, sc, a, b);
            ModelPart p = bone.part;
            p.x = bone.off[0] + pos[0];
            p.y = bone.off[1] - pos[1];
            p.z = bone.off[2] + pos[2];
            // Bedrock (x,y,z) rotation -> Java (-x, y, -z)
            p.xRot = rad(bone.rot[0] - rt[0]);
            p.yRot = rad(bone.rot[1] + rt[1]);
            p.zRot = rad(bone.rot[2] - rt[2]);
            p.xScale = bone.scale[0] * sc[0];
            p.yScale = bone.scale[1] * sc[1];
            p.zScale = bone.scale[2] * sc[2];
        }
    }

    private static void blend(Anim idle, Anim walk, String bone, char ch, float t, float w, float def,
                              float[] out, float[] a, float[] b) {
        fill(a, def); fill(b, def);
        if (idle != null) idle.sample(bone, ch, t, a);
        if (walk != null) walk.sample(bone, ch, t, b);
        for (int i = 0; i < 3; i++) out[i] = Mth.lerp(w, a[i], b[i]);
    }

    private static void fill(float[] f, float v) { f[0] = v; f[1] = v; f[2] = v; }

    private static final class Anim {
        final float length;
        final boolean loop;
        final Map<String, float[]> channels = new HashMap<>(); // key: bone + ":" + channel, stride 4 (t,x,y,z)

        Anim(float length, boolean loop) { this.length = length; this.loop = loop; }

        void sample(String bone, char ch, float time, float[] out) {
            float[] k = channels.get(bone + ":" + ch);
            if (k == null) return;
            int n = k.length / 4;
            float t = length > 0 ? (loop ? time % length : Math.min(time, length)) : 0;
            if (t <= k[0] || n == 1) { out[0] = k[1]; out[1] = k[2]; out[2] = k[3]; return; }
            if (t >= k[(n - 1) * 4]) { int i = (n - 1) * 4; out[0] = k[i + 1]; out[1] = k[i + 2]; out[2] = k[i + 3]; return; }
            int lo = 0, hi = n - 1;
            while (hi - lo > 1) {
                int mid = (lo + hi) >>> 1;
                if (k[mid * 4] <= t) lo = mid; else hi = mid;
            }
            float f = (t - k[lo * 4]) / (k[hi * 4] - k[lo * 4]);
            for (int i = 0; i < 3; i++) out[i] = Mth.lerp(f, k[lo * 4 + 1 + i], k[hi * 4 + 1 + i]);
        }
    }

    private static Map<String, Anim> loadAnims() {
        Map<String, Anim> map = new HashMap<>();
        JsonObject root = load("animations.json");
        for (Map.Entry<String, JsonElement> e : root.entrySet()) {
            JsonObject o = e.getValue().getAsJsonObject();
            Anim anim = new Anim(o.get("len").getAsFloat(), o.get("loop").getAsBoolean());
            for (Map.Entry<String, JsonElement> be : o.getAsJsonObject("bones").entrySet()) {
                for (Map.Entry<String, JsonElement> ce : be.getValue().getAsJsonObject().entrySet()) {
                    JsonArray ja = ce.getValue().getAsJsonArray();
                    float[] f = new float[ja.size()];
                    for (int i = 0; i < f.length; i++) f[i] = ja.get(i).getAsFloat();
                    anim.channels.put(be.getKey() + ":" + ce.getKey(), f);
                }
            }
            map.put(e.getKey(), anim);
        }
        return map;
    }

    // ---------- helpers ----------
    private static JsonObject load(String file) {
        try (var in = GuguModel.class.getResourceAsStream("/assets/gugugaga/gugu/" + file)) {
            return JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load " + file, ex);
        }
    }

    private static float[] arr(JsonObject o, String key) {
        JsonArray a = o.getAsJsonArray(key);
        float[] f = new float[a.size()];
        for (int i = 0; i < f.length; i++) f[i] = a.get(i).getAsFloat();
        return f;
    }

    private static float rad(float deg) { return deg * (float) Math.PI / 180.0f; }
}
