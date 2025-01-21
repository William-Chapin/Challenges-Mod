package com.bridging.challenges.client;

import java.util.Arrays;
import java.util.List;

public class ChallengeManager {
    public List<String> challengeOptions = Arrays.asList("None", "No Keyboard", "No Inventory", "No Mouse", "Straight Line");
    private int currentIndex = 0;

    public boolean isNoKeyboard(){
        return challengeOptions.get(currentIndex).equals("No Keyboard");
    }

    public boolean isNoInventory(){
        return challengeOptions.get(currentIndex).equals("No Inventory");
    }

    public boolean isLockedHead(){
        return challengeOptions.get(currentIndex).equals("No Mouse");
    }

    public boolean isStraightLine(){
        return challengeOptions.get(currentIndex).equals("Straight Line");
    }

    public void nextChallenge(){
        currentIndex = (currentIndex + 1) % challengeOptions.size();
    }

    public String getChallenge(){
        return challengeOptions.get(currentIndex);
    }

    public void setChallenge(String challenge){
        currentIndex = challengeOptions.indexOf(challenge);
        if (currentIndex == -1){
            currentIndex = 0;
        }
    }


}
