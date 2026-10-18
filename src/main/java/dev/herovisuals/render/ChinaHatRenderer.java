package dev.herovisuals.render;

import dev.herovisuals.module.ModuleManager;
import dev.herovisuals.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class ChinaHatRenderer {
    private static final int SEGMENTS = 48;
    private static final float RADIUS = 0.62f;
    private static final float HEIGHT = 0.30f;

    public static void render(MatrixStack ms, VertexConsumerProvider consumers, Vec3d cam, float td) {
        if (!ModuleManager.CHINA_HAT.enabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.options.getPerspective().isFirstPerson()) return;

        double x = MathHelper.lerp(td, p.prevX, p.getX()) - cam.x;
        double y = MathHelper.lerp(td, p.prevY, p.getY()) - cam.y + p.getHeight() - 0.02;
        double z = MathHelper.lerp(td, p.prevZ, p.getZ()) - cam.z;

        ms.push();
        ms.translate(x, y, z);
        Matrix4f m = ms.peek().getPositionMatrix();
        VertexConsumer vc = consumers.getBuffer(RenderLayer.getDebugQuads());
        int mode = ModuleManager.CHINA_HAT.mode;

        for (int i = 0; i < SEGMENTS; i++) {
            float a0 = (float) (i * Math.PI * 2 / SEGMENTS);
            float a1 = (float) ((i + 1) * Math.PI * 2 / SEGMENTS);
            float x0 = MathHelper.cos(a0) * RADIUS, z0 = MathHelper.sin(a0) * RADIUS;
            float x1 = MathHelper.cos(a1) * RADIUS, z1 = MathHelper.sin(a1) * RADIUS;

            int cA = ColorUtil.get(mode, i / (float) SEGMENTS, 120);
            int cB = ColorUtil.get(mode, (i + 1) / (float) SEGMENTS, 120);
            int apex = ColorUtil.get(mode, i / (float) SEGMENTS, 200);

            // конус (треугольник как вырожденный quad)
            vc.vertex(m, 0, HEIGHT, 0).color(apex);
            vc.vertex(m, 0, HEIGHT, 0).color(apex);
            vc.vertex(m, x1, 0, z1).color(cB);
            vc.vertex(m, x0, 0, z0).color(cA);

            // яркий ободок по краю
            float ix0 = x0 * 0.94f, iz0 = z0 * 0.94f, ix1 = x1 * 0.94f, iz1 = z1 * 0.94f;
            int e0 = ColorUtil.get(mode, i / (float) SEGMENTS, 255);
            int e1 = ColorUtil.get(mode, (i + 1) / (float) SEGMENTS, 255);
            vc.vertex(m, x0, 0, z0).color(e0);
            vc.vertex(m, x1, 0, z1).color(e1);
            vc.vertex(m, ix1, 0.012f, iz1).color(e1);
            vc.vertex(m, ix0, 0.012f, iz0).color(e0);
        }
        ms.pop();
    }
}
