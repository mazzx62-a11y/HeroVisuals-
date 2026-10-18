package dev.herovisuals.gui;

import dev.herovisuals.module.Category;
import dev.herovisuals.module.Module;
import dev.herovisuals.module.ModuleManager;
import dev.herovisuals.util.ColorUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClickGuiScreen extends Screen {
    private static final int W = 440, H = 270, HEADER = 30, TAB_W = 110, ROW_H = 26;
    private static Category selected = Category.RENDER;

    private static class Dot { float x, y, vx, vy; int size; float phase; }
    private final List<Dot> dots = new ArrayList<>();
    private final Random rnd = new Random();
    private long lastNanos = System.nanoTime();
    private float openAnim = 0f;

    public ClickGuiScreen() {
        super(Text.literal("HeroVisuals"));
    }

    @Override
    protected void init() {
        dots.clear();
        int count = 140;
        for (int i = 0; i < count; i++) {
            Dot d = new Dot();
            d.x = rnd.nextFloat() * width;
            d.y = rnd.nextFloat() * height;
            d.vy = 14f + rnd.nextFloat() * 40f;     // пикселей в секунду вниз
            d.vx = (rnd.nextFloat() - 0.5f) * 6f;
            d.size = rnd.nextFloat() < 0.8f ? 1 : 2;
            d.phase = rnd.nextFloat() * 6.28f;
            dots.add(d);
        }
    }

    @Override public boolean shouldPause() { return false; }

    private int wx() { return (width - W) / 2; }
    private int wy() { return (height - H) / 2; }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        long now = System.nanoTime();
        float dt = Math.min((now - lastNanos) / 1.0E9f, 0.1f);
        lastNanos = now;
        openAnim = Math.min(1f, openAnim + dt * 4f);

        // фон: тёмный градиент (не просто чёрный)
        ctx.fillGradient(0, 0, width, height, 0xE0050510, 0xF01A1233);

        // падающие белые точки
        long t = System.currentTimeMillis();
        for (Dot d : dots) {
            d.y += d.vy * dt;
            d.x += d.vx * dt;
            if (d.y > height) { d.y = -2; d.x = rnd.nextFloat() * width; }
            if (d.x < 0) d.x = width; else if (d.x > width) d.x = 0;
            int a = (int) (110 + 100 * Math.sin(t / 700.0 + d.phase));
            ctx.fill((int) d.x, (int) d.y, (int) d.x + d.size, (int) d.y + d.size, (a << 24) | 0xFFFFFF);
        }

        int x = wx(), y = wy();
        int slide = (int) ((1f - openAnim) * 12);
        y += slide;

        // окно
        ctx.fill(x - 1, y - 1, x + W + 1, y + H + 1, ColorUtil.withAlpha(ColorUtil.ACCENT, 110));
        ctx.fill(x, y, x + W, y + H, 0xE6101018);
        // шапка
        ctx.fillGradient(x, y, x + W, y + HEADER, 0xFF1B1B2E, 0xFF12121C);
        ctx.fill(x, y + HEADER - 1, x + W, y + HEADER, ColorUtil.ACCENT);

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x + 12, y + 8, 0);
        ctx.getMatrices().scale(1.5f, 1.5f, 1f);
        ctx.drawTextWithShadow(textRenderer, "HeroVisuals", 0, 0, ColorUtil.get(1, 0f, 255));
        ctx.getMatrices().pop();
        ctx.drawTextWithShadow(textRenderer, "v1.0", x + W - 28, y + 11, 0xFF8888AA);

        // вкладки слева
        ctx.fill(x, y + HEADER, x + TAB_W, y + H, 0xFF0C0C14);
        int ty = y + HEADER + 8;
        for (Category c : Category.values()) {
            boolean sel = c == selected;
            boolean hover = mx >= x && mx <= x + TAB_W && my >= ty && my <= ty + 24;
            if (sel) {
                ctx.fill(x + 6, ty, x + TAB_W - 6, ty + 24, ColorUtil.withAlpha(ColorUtil.ACCENT, 70));
                ctx.fill(x + 6, ty, x + 8, ty + 24, ColorUtil.ACCENT);
            } else if (hover) {
                ctx.fill(x + 6, ty, x + TAB_W - 6, ty + 24, 0x30FFFFFF);
            }
            ctx.drawTextWithShadow(textRenderer, c.label, x + 18, ty + 8, sel ? 0xFFFFFFFF : 0xFF9A9AB8);
            ty += 28;
        }

        // список модулей
        int rx = x + TAB_W + 12, rw = W - TAB_W - 24;
        int ry = y + HEADER + 10;
        for (Module m : ModuleManager.byCategory(selected)) {
            m.anim += ((m.enabled ? 1f : 0f) - m.anim) * Math.min(1f, dt * 12f);
            boolean hover = mx >= rx && mx <= rx + rw && my >= ry && my <= ry + ROW_H - 3;

            ctx.fill(rx, ry, rx + rw, ry + ROW_H - 3, hover ? 0xFF1E1E30 : 0xFF161624);
            ctx.fill(rx, ry, rx + 2, ry + ROW_H - 3, ColorUtil.lerp(0x3A3A52, ColorUtil.ACCENT & 0xFFFFFF, m.anim) | 0xFF000000);

            ctx.drawTextWithShadow(textRenderer, m.name, rx + 10, ry + 4, 0xFFFFFFFF);
            ctx.drawTextWithShadow(textRenderer, m.description, rx + 10, ry + 14, 0xFF77778F);

            if (m.modes.length > 0) {
                String label = m.modeName();
                int lw = textRenderer.getWidth(label);
                ctx.drawTextWithShadow(textRenderer, label, rx + rw - 54 - lw, ry + 8, ColorUtil.get(m.mode == 2 ? 2 : 1, 0f, 255));
            }

            // переключатель
            int sx = rx + rw - 40, sy = ry + 6;
            ctx.fill(sx, sy, sx + 30, sy + 12, ColorUtil.lerp(0x2A2A3E, ColorUtil.ACCENT & 0xFFFFFF, m.anim) | 0xFF000000);
            int knob = sx + 2 + (int) (m.anim * 18);
            ctx.fill(knob, sy + 2, knob + 8, sy + 10, 0xFFFFFFFF);

            ry += ROW_H + 2;
        }

        ctx.drawTextWithShadow(textRenderer, "ЛКМ - вкл/выкл  |  ПКМ - режим цвета  |  RShift/ESC - закрыть",
                x + TAB_W + 12, y + H - 14, 0xFF666680);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int x = wx(), y = wy();

        // вкладки
        int ty = y + HEADER + 8;
        for (Category c : Category.values()) {
            if (mx >= x && mx <= x + TAB_W && my >= ty && my <= ty + 24) { selected = c; return true; }
            ty += 28;
        }

        // модули
        int rx = x + TAB_W + 12, rw = W - TAB_W - 24;
        int ry = y + HEADER + 10;
        for (Module m : ModuleManager.byCategory(selected)) {
            if (mx >= rx && mx <= rx + rw && my >= ry && my <= ry + ROW_H - 3) {
                if (button == 0) m.toggle();
                else if (button == 1) m.cycleMode();
                return true;
            }
            ry += ROW_H + 2;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
