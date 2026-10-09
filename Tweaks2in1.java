package com.tweaks.twoinone;

import java.util.Set;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Tweaks2in1 implements ClientModInitializer {
    public static final String CATEGORY = "key.categories.tweaks2in1";

    private static KeyBinding xrayKey;
    private static KeyBinding freecamKey;

    @Override
    public void onInitializeClient() {
        xrayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tweaks2in1.xray", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY));
        freecamKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tweaks2in1.freecam", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (xrayKey.wasPressed()) {
                XRay.toggle(client);
                client.inGameHud.setOverlayMessage(
                        Text.literal("X-Ray: " + (XRay.enabled ? "ON" : "OFF")), false);
            }
            while (freecamKey.wasPressed()) {
                Freecam.toggle(client);
                client.inGameHud.setOverlayMessage(
                        Text.literal("Freecam: " + (Freecam.active ? "ON" : "OFF")), false);
            }
            Freecam.tick(client);
        });

        // Always start clean in a new world/server.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            Freecam.active = false;
            XRay.enabled = false;
        });
    }

    public static final class XRay {
        public static boolean enabled = false;

        /** Extra blocks to keep visible besides anything tagged as an ore. */
        private static final Set<Block> EXTRA = Set.of(
                Blocks.ANCIENT_DEBRIS, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST,
                Blocks.BARREL, Blocks.SPAWNER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.END_PORTAL_FRAME,
                Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK, Blocks.GOLD_BLOCK, Blocks.IRON_BLOCK
        );

        private XRay() {}

        public static boolean isVisible(BlockState state) {
            return state.isIn(ConventionalBlockTags.ORES) || EXTRA.contains(state.getBlock());
        }

        public static void toggle(MinecraftClient client) {
            enabled = !enabled;
            if (client.worldRenderer != null) client.worldRenderer.reload(); // rebuild chunk meshes
        }
    }


    public static final class Freecam {
        public static boolean active = false;

        public static double x, y, z;
        public static double px, py, pz;      // previous tick (for smooth interpolation)
        public static float yaw, pitch;

        public static double speed = 0.6;     // blocks per tick

        private Freecam() {}

        public static void toggle(MinecraftClient client) {
            if (client.player == null) { active = false; return; }
            active = !active;
            if (active) {
                Vec3d cam = client.gameRenderer.getCamera().getPos();
                x = px = cam.x; y = py = cam.y; z = pz = cam.z;
                yaw = client.player.getYaw();
                pitch = client.player.getPitch();
            }
            if (client.worldRenderer != null) client.worldRenderer.reload();
        }

        public static void tick(MinecraftClient client) {
            if (!active) return;
            if (client.player == null || client.world == null) { active = false; return; }

            px = x; py = y; pz = z;
            GameOptions o = client.options;

            double fwd = (o.forwardKey.isPressed() ? 1 : 0) - (o.backKey.isPressed() ? 1 : 0);
            double side = (o.rightKey.isPressed() ? 1 : 0) - (o.leftKey.isPressed() ? 1 : 0);
            double up = (o.jumpKey.isPressed() ? 1 : 0) - (o.sneakKey.isPressed() ? 1 : 0);

            double yr = Math.toRadians(yaw), pr = Math.toRadians(pitch);
            double fx = -Math.sin(yr) * Math.cos(pr);
            double fy = -Math.sin(pr);
            double fz = Math.cos(yr) * Math.cos(pr);
            double rx = -Math.cos(yr);
            double rz = -Math.sin(yr);

            double mx = fx * fwd + rx * side;
            double my = fy * fwd + up;
            double mz = fz * fwd + rz * side;

            double len = Math.sqrt(mx * mx + my * my + mz * mz);
            if (len > 1.0E-4) {
                double s = speed * (o.sprintKey.isPressed() ? 2.5 : 1.0) / len;
                x += mx * s; y += my * s; z += mz * s;
            }
        }

        public static void look(double dx, double dy) {
            yaw += (float) dx * 0.15f;
            pitch = MathHelper.clamp(pitch + (float) dy * 0.15f, -90f, 90f);
        }
    }

}
