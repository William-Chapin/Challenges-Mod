package com.bridging.challenges.client;

import java.util.Arrays;
import java.util.List;

public class ChallengeManager {
    public List<String> challengeOptions = Arrays.asList("None", "No Keyboard", "No Inventory", "No Mouse", "Straight Line", "Blindness", "One Slot");
    private int currentIndex = 0;
    private int selectedIndex = 0;

    public boolean isNoKeyboard(){
        return challengeOptions.get(currentIndex).equals("No Keyboard");
    }

    public boolean isNoInventory(){
        return challengeOptions.get(currentIndex).equals("No Inventory");
    }

    public boolean isNoMouse(){
        return challengeOptions.get(currentIndex).equals("No Mouse");
    }

    public boolean isStraightLine(){
        return challengeOptions.get(currentIndex).equals("Straight Line");
    }

    public boolean isBlindness(){
        return challengeOptions.get(currentIndex).equals("Blindness");
    }

    public boolean isOneSlot(){
        return challengeOptions.get(currentIndex).equals("One Slot");
    }

    public void nextChallenge(){
        selectedIndex = (selectedIndex + 1) % challengeOptions.size();
    }

    public void resetChallenge(){
        selectedIndex = 0;
        currentIndex = 0;
    }

    public String getChallenge(){
        return challengeOptions.get(currentIndex);
    }

    public String getSelectedChallenge(){
        return challengeOptions.get(selectedIndex);
    }

    public void applySelectedChallenge(){
        currentIndex = selectedIndex;
    }

    public void setChallenge(String challenge){
        currentIndex = challengeOptions.indexOf(challenge);
        if (currentIndex == -1){
            currentIndex = 0;
        }
    }


}
