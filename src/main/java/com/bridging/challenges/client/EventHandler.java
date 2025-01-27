package com.bridging.challenges.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.LevelResource;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EventHandler {
    private final ChallengeManager challengeManager;
    private ChallengeFileManager challengeFileManager;
    private GLFWCursorPosCallbackI originalCursorPosCallback;
    private final Map<String, Double> initialPositions = new HashMap<>();
    private int randomItemTimer;

    public EventHandler(ChallengeManager challengeManager) {
        this.challengeManager = challengeManager;
    }

    public void registerEvents() {
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
                        challengeManager.setChallenge(ChallengeManager.Challenge.valueOf(savedChallenge));
                        initialPositions.putAll(challengeFileManager.loadInitialPositions());
                    }
                } else {
                    System.err.println("[Challenges] Failed to set challenge.");
                }
            }
        });

        // No Inventory Challenge
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (Minecraft.getInstance().getSingleplayerServer() != null && screen instanceof InventoryScreen && challengeManager.isChallenge(ChallengeManager.Challenge.NO_INVENTORY)) {
                client.setScreen(null);
            }
        });

        // No Mouse Challenge & No Keyboard Challenge
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            if (Minecraft.getInstance().getSingleplayerServer() != null) {
                long windowHandle = Minecraft.getInstance().getWindow().getWindow();

                // Manage No Mouse Challenge
                originalCursorPosCallback = GLFW.glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
                    if (challengeManager.isChallenge(ChallengeManager.Challenge.NO_MOUSE) && (Minecraft.getInstance().screen == null)) {
                        return;
                    }
                    if (originalCursorPosCallback != null) {
                        originalCursorPosCallback.invoke(window, xpos, ypos);
                    }
                });

                // Manage No Keyboard Challenge
                GLFW.glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
                    if (challengeManager.isChallenge(ChallengeManager.Challenge.NO_KEYBOARD) && action != GLFW.GLFW_RELEASE) {
                        if (key == GLFW.GLFW_KEY_ESCAPE && (Minecraft.getInstance().screen instanceof ChallengeSelection)) {
                            // do nothing
                        } else {
                            return;
                        }
                    }
                    Minecraft.getInstance().keyboardHandler.keyPress(window, key, scancode, action, mods);
                });
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (Minecraft.getInstance().getSingleplayerServer() != null) {
                // Straight Line Challenge
                if (challengeManager.isChallenge(ChallengeManager.Challenge.STRAIGHT_LINE) && client.player != null) {
                    String dimension = client.level.dimension().location().getPath();
                    double playerX = client.player.getX();
                    double playerZ = client.player.getZ();

                    if (!initialPositions.containsKey(dimension)) {
                        double initialX = Math.floor(playerX) + 0.5;
                        initialPositions.put(dimension, initialX);
                        challengeFileManager.saveInitialPosition(dimension, initialX);
                        System.out.println("Initial X for " + dimension + ": " + initialX);
                    }

                    double initialX = initialPositions.get(dimension);
                    if (Math.abs(playerX - initialX) > 0.4) {
                        client.player.setPos(initialX + (playerX > initialX ? 0.4 : -0.4), client.player.getY(), playerZ);
                    }
                }

                // Blindness Challenge
                if (challengeManager.isChallenge(ChallengeManager.Challenge.BLINDNESS) && client.player != null) {
                    client.player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 1, false, false));
                }
                if (!challengeManager.isChallenge(ChallengeManager.Challenge.BLINDNESS) && client.player != null) {
                    client.player.removeEffect(MobEffects.BLINDNESS);
                }

                // Hunger Challenge
                if (challengeManager.isChallenge(ChallengeManager.Challenge.HUNGER) && client.player != null) {
                    client.player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 25, false, false));
                }
                if (!challengeManager.isChallenge(ChallengeManager.Challenge.HUNGER) && client.player != null) {
                    client.player.removeEffect(MobEffects.HUNGER);
                }
            }
        });

        // Save Challenge on Disconnect
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (Minecraft.getInstance().getSingleplayerServer() != null && challengeFileManager != null) {
                challengeFileManager.saveChallenge(challengeManager.getChallenge().name());
            }

            // reset selected challenge
            challengeManager.resetChallenge();
        });
    }
}