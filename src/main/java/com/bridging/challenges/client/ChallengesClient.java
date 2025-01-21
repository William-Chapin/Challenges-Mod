package com.bridging.challenges.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import org.lwjgl.glfw.GLFW;
import java.nio.file.Path;


public class ChallengesClient implements ClientModInitializer {

    private ChallengeManager challengeManager = new ChallengeManager();
    private static final KeyMapping openChallengeSelectionKey = new KeyMapping(
            "Open Challenge Selection",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "Challenges"
    );
    private ChallengeFileManager challengeFileManager;

    @Override
    public void onInitializeClient() {
        System.out.print("[Challenges] Mod initialized.");

        KeyBindingHelper.registerKeyBinding(openChallengeSelectionKey);

        EventHandler eventHandler = new EventHandler(challengeManager);
        eventHandler.registerE();
    }
}