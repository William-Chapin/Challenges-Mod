package com.bridging.challenges.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ChallengeFileManager {
    private final File challengeFile;
    private final File worldDirectory;
    private final File dataDirectory;

    public ChallengeFileManager(File worldFolder) {
        this.dataDirectory = new File(worldFolder, "challenges_data");
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
        this.challengeFile = new File(dataDirectory, "challenges.txt");
        this.worldDirectory = worldFolder;
    }

    public void saveChallenge(String challenge) {
        try (FileWriter writer = new FileWriter(challengeFile)) {
            writer.write(challenge);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadChallenge() {
        if (challengeFile.exists()) {
            try {
                return new String(Files.readAllBytes(Paths.get(challengeFile.toURI())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "None";
    }

    public Map<String, Double> loadInitialPositions() {
        Map<String, Double> initialPositions = new HashMap<>();
        try {
            for (String dimension : new String[]{"overworld", "nether", "end"}) {
                String value = Files.readString(Paths.get(dataDirectory.getPath(), dimension + "_initialX.txt"));
                initialPositions.put(dimension, Double.parseDouble(value));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return initialPositions;
    }

    public void saveInitialPosition(String dimension, Double initialX) {
        try {
            Files.writeString(Paths.get(dataDirectory.getPath(), dimension + "_initialX.txt"), initialX.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
