package com.bridging.challenges.client;

import java.util.Arrays;
import java.util.List;

public class ChallengeManager {
    public enum Challenge {
        NONE("None"),
        NO_KEYBOARD("No Keyboard"),
        NO_INVENTORY("No Inventory"),
        NO_MOUSE("No Mouse"),
        STRAIGHT_LINE("Straight Line"),
        BLINDNESS("Blindness"),
        HUNGER("Hunger");
        private final String displayName;

        Challenge(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public List<Challenge> challengeOptions = Arrays.asList(Challenge.values());
    private int currentIndex = 0;
    private int selectedIndex = 0;

    public boolean isChallenge(Challenge challenge) {
        return challengeOptions.get(currentIndex) == challenge;
    }

    public void nextChallenge() {
        selectedIndex = (selectedIndex + 1) % challengeOptions.size();
    }

    public void resetChallenge() {
        selectedIndex = 0;
        currentIndex = 0;
    }

    public Challenge getChallenge() {
        return challengeOptions.get(currentIndex);
    }

    public Challenge getSelectedChallenge() {
        return challengeOptions.get(selectedIndex);
    }

    public void applySelectedChallenge() {
        currentIndex = selectedIndex;
    }

    public void setChallenge(Challenge challenge) {
        currentIndex = challengeOptions.indexOf(challenge);
        if (currentIndex == -1) {
            currentIndex = 0;
        }
    }
}
