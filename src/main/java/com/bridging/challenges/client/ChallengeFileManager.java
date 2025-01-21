package com.bridging.challenges.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChallengeFileManager {
    private final File challengeFile;

    public ChallengeFileManager(File worldFolder){
        this.challengeFile = new File(worldFolder, "challenges.txt");
    }

    public void saveChallenge(String challenge){
        try (FileWriter writer = new FileWriter(challengeFile)){
            writer.write(challenge);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String loadChallenge(){
        if (challengeFile.exists()){
            try{
                return new String(Files.readAllBytes(Paths.get(challengeFile.toURI())));
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return "None";
    }
}
