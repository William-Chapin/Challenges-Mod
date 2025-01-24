package com.bridging.challenges.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import java.nio.file.Path;

public class EventHandler {
    private final ChallengeManager challengeManager;
    private ChallengeFileManager challengeFileManager;
    private boolean hasOpened = false;
    private GLFWCursorPosCallbackI originalCursorPosCallback;
    private static final KeyMapping openChallengeSelectionKey = new KeyMapping(
            "Open Challenge Selection",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "Challenges"
    );

    public EventHandler(ChallengeManager challengeManager) {
        this.challengeManager = challengeManager;
    }

    public void registerE() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.level instanceof ClientLevel) {
                hasOpened = false;

                // Reinitialize challengeFileManager with the new world path
                MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
                if (server != null) {
                    // world path
                    Path worldPath = server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).getParent();
                    System.out.println("[Challenges] World path: " + worldPath);
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

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && !hasOpened) {
                if ("None".equals(challengeManager.getChallenge())) {
                    Minecraft.getInstance().setScreen(new ChallengeSelection(challengeManager));
                }
                hasOpened = true;
            }

            while (openChallengeSelectionKey.consumeClick()) {
                Minecraft.getInstance().setScreen(new ChallengeSelection(challengeManager));
            }
        });

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof InventoryScreen && challengeManager.isNoInventory()) {
                client.setScreen(null);
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            originalCursorPosCallback = GLFW.glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
                if (challengeManager.isLockedHead() && (Minecraft.getInstance().screen == null)) {
                    return;
                }
                if (originalCursorPosCallback != null) {
                    originalCursorPosCallback.invoke(window, xpos, ypos);
                }
            });

            GLFW.glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
                if (challengeManager.isNoKeyboard() && action != GLFW.GLFW_RELEASE) {
                    if (key == GLFW.GLFW_KEY_ESCAPE && (Minecraft.getInstance().screen instanceof ChallengeSelection)) {
                        // do nothing
                    } else if (openChallengeSelectionKey.matches(key, scancode)) {
                        // do nothing
                    } else {
                        return;
                    }
                }
                Minecraft.getInstance().keyboardHandler.keyPress(window, key, scancode, action, mods);
            });
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (challengeFileManager != null) {
                challengeFileManager.saveChallenge(challengeManager.getChallenge());
            }
        });
    }
}