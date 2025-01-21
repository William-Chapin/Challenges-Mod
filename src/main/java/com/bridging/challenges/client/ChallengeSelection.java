package com.bridging.challenges.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ChallengeSelection extends Screen {
    private Button button;
    private Component titleText;
    private final ChallengeManager challengeManager;

    public ChallengeSelection(ChallengeManager challengeManager){
        super(Component.literal("Challenge Selection"));
        this.titleText = Component.literal("Challenge Selection");
        this.challengeManager = challengeManager;
    }

    @Override
    protected void init(){
        int buttonW = 200;
        int buttonH = 20;
        int x = (this.width - buttonW) / 2;
        int y = (this.height - buttonH) / 2;

        String currentChallenge = challengeManager.getChallenge();
        this.button = Button.builder(Component.literal(currentChallenge), button ->{
            challengeManager.nextChallenge();
            String next = challengeManager.getChallenge();
            this.button.setMessage(Component.literal(next));
        }).bounds(x,y,buttonW,buttonH).build();

        this.addRenderableWidget(this.button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta){
        this.renderBackground(guiGraphics, mouseX, mouseY, delta);;
        guiGraphics.drawCenteredString(this.font, this.titleText.getString(), this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen(){
        return false;
    }
}
