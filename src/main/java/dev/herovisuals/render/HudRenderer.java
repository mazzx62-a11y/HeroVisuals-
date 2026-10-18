package dev.herovisuals.render;

import dev.herovisuals.module.Module;
import dev.herovisuals.module.ModuleManager;
import dev.herovisuals.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HudRenderer {
    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;
        TextRenderer tr = mc.textRenderer;

        // Watermark
        if (ModuleManager.WATERMARK.enabled) {
            String name = "HeroVisuals";
            String rest = " | " + mc.getCurrentFps() + " fps";
            int w = tr.getWidth(name) + tr.getWidth(rest) + 12;
            ctx.fill(4, 4, 4 + w, 20, 0xA0101018);
            ctx.fill(4, 4, 4 + w, 5, ColorUtil.ACCENT);
            ctx.drawTextWithShadow(tr, name, 10, 9, ColorUtil.ACCENT);
            ctx.drawTextWithShadow(tr, rest, 10 + tr.getWidth(name), 9, 0xFFFFFFFF);
        }

        // Module list (справа сверху)
        if (ModuleManager.MODULE_LIST.enabled) {
            List<Module> active = new ArrayList<>();
            for (Module m : ModuleManager.ALL) if (m.enabled && m != ModuleManager.MODULE_LIST) active.add(m);
            active.sort(Comparator.comparingInt((Module m) -> tr.getWidth(m.name)).reversed());
            int sw = ctx.getScaledWindowWidth();
            int y = 4;
            for (int i = 0; i < active.size(); i++) {
                Module m = active.get(i);
                int w = tr.getWidth(m.name);
                int col = ColorUtil.get(ModuleManager.MODULE_LIST.mode, i * 0.08f, 255);
                ctx.fill(sw - w - 10, y, sw - 4, y + 11, 0x90101018);
                ctx.fill(sw - 4, y, sw - 3, y + 11, col);
                ctx.drawTextWithShadow(tr, m.name, sw - w - 7, y + 2, col);
                y += 11;
            }
        }

        // Info (слева снизу)
        if (ModuleManager.INFO.enabled) {
            double dx = mc.player.getX() - mc.player.prevX;
            double dz = mc.player.getZ() - mc.player.prevZ;
            double bps = Math.sqrt(dx * dx + dz * dz) * 20.0;
            String xyz = String.format("XYZ: %.1f %.1f %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            String sp = String.format("Speed: %.2f b/s", bps);
            int sh = ctx.getScaledWindowHeight();
            ctx.drawTextWithShadow(tr, xyz, 6, sh - 22, 0xFFFFFFFF);
            ctx.drawTextWithShadow(tr, sp, 6, sh - 12, ColorUtil.ACCENT);
        }
    }
}
