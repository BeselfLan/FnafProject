package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/** Read and writes to a save file*/
public class FileManager {

    GameManager gm;

    public int night;

    /**
     * Links FileManager to GameManager and loads night from saveFile
     * @param gm is the GameManager
     */
    public FileManager(GameManager gm) {

        this.gm = gm;
        getNight();

    }

    /**
     * Loads night from saveData
     * @return the saved night level
     * */
    public int getNight() {
        try {
            Scanner sc = new Scanner(new File("src/main/saveData"));

            String nightLine = sc.nextLine();
            //get the last index of the string since it is the night level
            nightLine = nightLine.substring(6, nightLine.length());

            //turn the last index into an integer and return it
            night = Integer.parseInt(nightLine);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return night;
    }

    /**
     * Changes the saveFile to given string
     * @param night is the line to change the saveFile to
     * */
    public void modifySaveFile(String night) {

        String filePath = "src/main/saveData";

        File file = new File(filePath);

        try {
            //update the save file
            FileWriter writer = new FileWriter(file);

            writer.write(night);

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while trying to modify the text file.");
            e.printStackTrace();
        }
    }
}
