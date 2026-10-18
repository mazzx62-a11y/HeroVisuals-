package dev.herovisuals.module;

import dev.herovisuals.render.TrailRenderer;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final String[] COLORS = {"Rainbow", "Hero", "White"};

    public static final Module CHINA_HAT = new Module("China Hat", "Китайская шляпа над головой", Category.RENDER, true, COLORS);
    public static final Module TRAIL = new Module("Trail", "След за игроком", Category.RENDER, true, COLORS) {
        @Override public void onDisable() { TrailRenderer.clear(); }
    };

    public static final Module WATERMARK = new Module("Watermark", "Название клиента, FPS", Category.HUD, true);
    public static final Module MODULE_LIST = new Module("Module List", "Список включённых модулей", Category.HUD, true, COLORS);
    public static final Module INFO = new Module("Info", "Координаты и скорость", Category.HUD, true);

    public static final List<Module> ALL = new ArrayList<>(List.of(CHINA_HAT, TRAIL, WATERMARK, MODULE_LIST, INFO));

    public static List<Module> byCategory(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : ALL) if (m.category == c) out.add(m);
        return out;
    }
}
