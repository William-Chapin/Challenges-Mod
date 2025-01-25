package com.bridging.challenges.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.LevelResource;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import java.nio.file.Path;

public class EventHandler {
    private final ChallengeManager challengeManager;
    private ChallengeFileManager challengeFileManager;
    private GLFWCursorPosCallbackI originalCursorPosCallback;
    private double initialX; // store initial coordinates for straight line challenge
    private boolean initialXSet = false;

    public EventHandler(ChallengeManager challengeManager) {
        this.challengeManager = challengeManager;
    }

    public void registerE() {
        // Initialize ChallengeManager and ChallengeFileManager on world join
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.level instanceof ClientLevel) {
                MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
                if (server != null) {
                    Path worldPath = server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).getParent();
                    challengeFileManager = new ChallengeFileManager(worldPath.toFile());

                    String savedChallenge = challengeFileManager.loadChallenge();
                    if ("None".equals(savedChallenge)) {
                        Minecraft.getInstance().setScreen(new ChallengeSelection(challengeManager));
                    } else {
                        challengeManager.setChallenge(savedChallenge);
                    }
                } else {
                    System.err.println("[Challenges] Failed to obtain MinecraftServer instance.");
                }
            }
        });

        // No Inventory Challenge
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof InventoryScreen && challengeManager.isNoInventory()) {
                client.setScreen(null);
            }
        });

        // No Mouse Challenge & No Keyboard Challenge
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();

            // Manage No Mouse Challenge
            originalCursorPosCallback = GLFW.glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
                if (challengeManager.isNoMouse() && (Minecraft.getInstance().screen == null)) {
                    return;
                }
                if (originalCursorPosCallback != null) {
                    originalCursorPosCallback.invoke(window, xpos, ypos);
                }
            });

            // Manage No Keyboard Challenge
            GLFW.glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
                if (challengeManager.isNoKeyboard() && action != GLFW.GLFW_RELEASE) {
                    if (key == GLFW.GLFW_KEY_ESCAPE && (Minecraft.getInstance().screen instanceof ChallengeSelection)) {
                        // do nothing
                    } else {
                        return;
                    }
                }
                Minecraft.getInstance().keyboardHandler.keyPress(window, key, scancode, action, mods);
            });
        });

        // Straight Line Challenge
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (challengeManager.isStraightLine() && client.player != null) {
                double playerX = client.player.getX();
                double playerZ = client.player.getZ();

                if (!initialXSet) {
                    initialX = Math.floor(playerX) + 0.5;
                    initialXSet = true;
                    System.out.println("Initial X: " + initialX);
                }

                // prevent player movement past 0.2 from initialX
                if (Math.abs(playerX - initialX) > 0.4) {
                    client.player.setPos(initialX + (playerX > initialX ? 0.4 : -0.4), client.player.getY(), playerZ);
                }
            }

            // Blindness Challenge
            if (challengeManager.isBlindness() && client.player != null){
                client.player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 1));
            }

            if (!challengeManager.isBlindness() && client.player != null){
                client.player.removeEffect(MobEffects.BLINDNESS);
            }

            // One Slot Challenge
            if (challengeManager.isOneSlot() && client.player != null) {
                for (int x = 1; x < 36; x++) {
                    if (!client.player.getInventory().getItem(x).is(Items.BARRIER)) {
                        client.player.getInventory().setItem(x, Items.BARRIER.getDefaultInstance());
                    }
                }
            }
        });

        // Save Challenge on Disconnect
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (challengeFileManager != null) {
                challengeFileManager.saveChallenge(challengeManager.getChallenge());
            }

            // reset selected challenge
            challengeManager.resetChallenge();
        });

    }
}