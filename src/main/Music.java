package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

/** Plays background/looping audio*/
public class Music {

    Clip clip;

    /**
     * Loads File
     * @param name is the file name
     */
    public void setFile(URL name) {

        try {
            AudioInputStream sound = AudioSystem.getAudioInputStream(name);
            clip = AudioSystem.getClip();
            clip.open(sound);
        }
        catch(Exception e) {

        }
    }

    /** Play music
     * @param name determines what music to play
     */
    public void play(URL name) {

        clip.setFramePosition(0);
        clip.start();
    }

    /** Loop music
     * @param name determines what music to loop
     */
    public void loop(URL name) {

        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /** stop music
     * @param name determines what music to stop playing
     */
    public void stop(URL name) {

        clip.stop();
    }
}

