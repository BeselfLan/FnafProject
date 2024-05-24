package main;
//credits: https://stackoverflow.com/questions/38966000/animation-sequence-in-jframe
//credits: https://stackoverflow.com/questions/47610086/how-to-make-3-images-fade-in-and-fade-out-in-jpanel

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * FNAF 6 remake
 * @author Beself Lan
 */

//loading all the componenets in the project
public class GameManager {

    public final int SCREENWIDTH = 1920;
    public final int SCREENHEIGHT = 1080;

    public int night = 1;

    FileManager fm = new FileManager(this);
    ActionHandler aHandler = new ActionHandler(this);
    public SceneAnimator sAnimator = new SceneAnimator(this);

    public UI ui = new UI(this);
    public SceneChanger sChanger = new SceneChanger(this);

    Music music = new Music();
    SE se = new SE();

    // Load audio files
    public URL titleMusic = getClass().getClassLoader().getResource("audio/titleScreen.wav");
    public URL sceneTransition = getClass().getClassLoader().getResource("audio/twinkleTransition.wav");
    public URL fanBlow = getClass().getClassLoader().getResource("audio/fanBlow.wav");
    public URL fanStart = getClass().getClassLoader().getResource("audio/fanStart.wav");
    public URL computerAudio = getClass().getClassLoader().getResource("audio/computerAudio.wav");
    public URL computerBootUp = getClass().getClassLoader().getResource("audio/computerBootUp.wav");
    public URL nightWin = getClass().getClassLoader().getResource("audio/nightWin.wav");
    public URL buttonClick = getClass().getClassLoader().getResource("audio/tabSwitch.wav");
    public URL printPaper = getClass().getClassLoader().getResource("audio/printPaper.wav");
    public URL taskFinish = getClass().getClassLoader().getResource("audio/blip.wav");
    public URL ambience1 = getClass().getClassLoader().getResource("audio/amb_env.wav");
    public URL ambience2 = getClass().getClassLoader().getResource("audio/amb_people.wav");
    public URL fanOffAudio = getClass().getClassLoader().getResource("audio/fanOff.wav");
    public URL computerOffAudio = getClass().getClassLoader().getResource("audio/computerOff.wav");
    //nothing audio is used to stop audio from repeating
    public URL nothing = getClass().getClassLoader().getResource("audio/nothing.wav");
    public URL jumpScare = getClass().getClassLoader().getResource("audio/jumpScare.wav");
    public URL tutorial = getClass().getClassLoader().getResource("audio/tutorial.wav");
    public URL[] ventNoises = new URL[13];

    public BufferedImage buffImage;

    public static void main(String[] args) {

        new GameManager();

    }
    /** Load and create title screen*/
    public GameManager() {

        loadVentSounds();
        sChanger.showScene1();
    }
    
    /** Load buffered images*/
    public void loadBufferedImage(String image) {
        buffImage = null;

        try {
            buffImage = ImageIO.read(getClass().getClassLoader().getResource(image));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Audio
    /** loading vent sounds into array */
    public void loadVentSounds() {
        for (int i = 1; i < ventNoises.length; i++) {
            ventNoises[i] = getClass().getClassLoader().getResource("audio/vent" + i + ".wav");
        }
    }

    /** Used to play sound effects*/
    public void playSE(URL url, float soundChange) {

        se.setFile(url, soundChange);
        se.play(url);
    }

    /** Used to play music audios*/
    public void playMusic (URL url) {

        music.setFile(url);
        music.play(url);
        music.loop(url);
    }

    /** Used to stop music audios*/
    public void stopMusic(URL url) {
        music.stop(url);
    }
}