package dev.herovisuals.util;

import java.awt.Color;

public class ColorUtil {
    public static final int ACCENT = 0xFF6C7BFF;

    /** mode: 0 Rainbow, 1 Hero (сине-белый градиент), 2 White. Возвращает ARGB. */
    public static int get(int mode, float offset, int alpha) {
        offset = ((offset % 1f) + 1f) % 1f;
        int rgb;
        switch (mode) {
            case 0 -> rgb = Color.HSBtoRGB(((System.currentTimeMillis() % 4000L) / 4000f + offset) % 1f, 0.55f, 1f);
            case 1 -> {
                float t = (float) (Math.sin(System.currentTimeMillis() / 600.0 + offset * Math.PI * 2) * 0.5 + 0.5);
                rgb = lerp(0x6C7BFF, 0xFFFFFF, t);
            }
            default -> rgb = 0xFFFFFF;
        }
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }

    public static int lerp(int a, int b, float t) {
        int r = (int) (((a >> 16) & 255) + (((b >> 16) & 255) - ((a >> 16) & 255)) * t);
        int g = (int) (((a >> 8) & 255) + (((b >> 8) & 255) - ((a >> 8) & 255)) * t);
        int bl = (int) ((a & 255) + ((b & 255) - (a & 255)) * t);
        return (r << 16) | (g << 8) | bl;
    }

    public static int withAlpha(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }
}
