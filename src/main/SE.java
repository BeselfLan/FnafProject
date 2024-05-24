package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

/** Play sound effects*/
public class SE {

    Clip clip;

    /**
     * Load file and adjust the volume
     * @param name is the file name
     * @param volume is the volume change, must be between 0 and 1
     */
    public void setFile(URL name, float volume) {

        try {
            AudioInputStream sound = AudioSystem.getAudioInputStream(name);
            clip = AudioSystem.getClip();
            clip.open(sound);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // calculate decibel from linear amplitude
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
        catch(Exception e) {

        }
    }
    /** Play sound effect
     * @param name determines which sound effect to play
     */
    public void play(URL name) {

        clip.setFramePosition(0);
        clip.start();
    }

    /** stop sound effect
     * @param name determines which sound effect to stop
     */
    public void stop(URL name) {

        clip.stop();
    }
}
