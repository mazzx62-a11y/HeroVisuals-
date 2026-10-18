package dev.herovisuals;

import dev.herovisuals.gui.ClickGuiScreen;
import dev.herovisuals.render.ChinaHatRenderer;
import dev.herovisuals.render.HudRenderer;
import dev.herovisuals.render.TrailRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class HeroVisuals implements ClientModInitializer {
    public static final String NAME = "HeroVisuals";
    private static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.herovisuals.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "HeroVisuals"));

        // название окна игры
        ClientLifecycleEvents.CLIENT_STARTED.register(mc -> mc.getWindow().setTitle(NAME + " 1.0 | Minecraft 1.21.4"));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openKey.wasPressed()) {
                if (mc.currentScreen == null) mc.setScreen(new ClickGuiScreen());
            }
            TrailRenderer.tick(mc);
        });

        HudRenderCallback.EVENT.register((ctx, tickCounter) -> HudRenderer.render(ctx));

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null) return;
            var cam = context.camera().getPos();
            float td = context.tickCounter().getTickDelta(false);

            TrailRenderer.render(context.matrixStack(), consumers, cam);
            ChinaHatRenderer.render(context.matrixStack(), consumers, cam, td);

            if (consumers instanceof VertexConsumerProvider.Immediate imm) {
                imm.draw(RenderLayer.getDebugQuads());
            }
        });
    }
}
