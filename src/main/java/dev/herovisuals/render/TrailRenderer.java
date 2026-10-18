package dev.herovisuals.render;

import dev.herovisuals.module.ModuleManager;
import dev.herovisuals.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class TrailRenderer {
    private static final int MAX_POINTS = 50;
    private static final List<Vec3d> POINTS = new ArrayList<>();

    public static void clear() { POINTS.clear(); }

    public static void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) { clear(); return; }
        if (!ModuleManager.TRAIL.enabled) return;

        Vec3d pos = mc.player.getPos();
        boolean moved = POINTS.isEmpty() || POINTS.get(POINTS.size() - 1).squaredDistanceTo(pos) > 0.0004;
        if (moved) POINTS.add(pos);
        else if (!POINTS.isEmpty()) POINTS.remove(0); // стоим - след затухает

        while (POINTS.size() > MAX_POINTS) POINTS.remove(0);
    }

    public static void render(MatrixStack ms, VertexConsumerProvider consumers, Vec3d cam) {
        if (!ModuleManager.TRAIL.enabled || POINTS.size() < 2) return;

        VertexConsumer vc = consumers.getBuffer(RenderLayer.getDebugQuads());
        Matrix4f m = ms.peek().getPositionMatrix();
        int mode = ModuleManager.TRAIL.mode;
        int n = POINTS.size();

        for (int i = 1; i < n; i++) {
            Vec3d a = POINTS.get(i - 1);
            Vec3d b = POINTS.get(i);
            double dx = b.x - a.x, dz = b.z - a.z;
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len < 1.0E-4) continue;
            float px = (float) (-dz / len * 0.18);
            float pz = (float) (dx / len * 0.18);

            float ax = (float) (a.x - cam.x), ay = (float) (a.y - cam.y + 0.05), az = (float) (a.z - cam.z);
            float bx = (float) (b.x - cam.x), by = (float) (b.y - cam.y + 0.05), bz = (float) (b.z - cam.z);

            int c0 = ColorUtil.get(mode, (i - 1) / (float) n, (int) (200f * (i - 1) / n));
            int c1 = ColorUtil.get(mode, i / (float) n, (int) (200f * i / n));

            vc.vertex(m, ax + px, ay, az + pz).color(c0);
            vc.vertex(m, ax - px, ay, az - pz).color(c0);
            vc.vertex(m, bx - px, by, bz - pz).color(c1);
            vc.vertex(m, bx + px, by, bz + pz).color(c1);
        }
    }
}
