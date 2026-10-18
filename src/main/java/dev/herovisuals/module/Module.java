package dev.herovisuals.module;

public class Module {
    public final String name;
    public final String description;
    public final Category category;
    public final String[] modes;
    public boolean enabled;
    public int mode;
    public float anim; // для плавного переключателя в GUI

    public Module(String name, String description, Category category, boolean enabled, String... modes) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = enabled;
        this.modes = modes;
    }

    public void toggle() {
        enabled = !enabled;
        if (!enabled) onDisable();
    }

    public void cycleMode() {
        if (modes.length > 0) mode = (mode + 1) % modes.length;
    }

    public String modeName() {
        return modes.length == 0 ? "" : modes[mode];
    }

    public void onDisable() {}
}
