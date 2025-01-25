package com.bridging.challenges.client;

import net.fabricmc.api.ClientModInitializer;


public class ChallengesClient implements ClientModInitializer {

    private ChallengeManager challengeManager = new ChallengeManager();
    private ChallengeFileManager challengeFileManager;

    @Override
    public void onInitializeClient() {
        System.out.print("[Challenges] Mod initialized.");

        EventHandler eventHandler = new EventHandler(challengeManager);
        eventHandler.registerE();
    }
}