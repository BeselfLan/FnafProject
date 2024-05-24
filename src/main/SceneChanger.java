package main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Changes the Scene*/
public class SceneChanger {

    GameManager gm;

    private boolean soundPlayed = false;

    public boolean canFade = true;
    private int counter;

    /** switches between playing two ambience audios*/
    private Timer ambience = new Timer(160000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (counter == 1) {
                counter = 0;
                gm.playSE(gm.ambience2, 1);
            }
            else {
                gm.playSE(gm.ambience1, 1);
            }
            counter++;
        }
    });

    /**
     * Connects SceneChanger to GameManager
     * @param gm is the GameManager
     */
    public SceneChanger(GameManager gm) {

        this.gm = gm;
    }

    /** Shows title screen and plays title screen music*/
    public void showScene1() {

        gm.playMusic(gm.titleMusic);
        gm.ui.bgPanel[1].setVisible(true);
        gm.ui.bgPanel[3].setVisible(false);
    }

    /** Shows scene 2, sets other scenes to invisible*/
    public void showScene2() {
        gm.ui.bgPanel[1].setVisible(false);
        gm.ui.bgPanel[2].setVisible(true);
        gm.ui.bgPanel[3].setVisible(false);
        gm.stopMusic(gm.titleMusic);
        gm.playSE(gm.sceneTransition, 1);
        gm.playSE(gm.nothing, 0);

        System.out.println("scene transition noise");
    }

    /** Shows and sets up office scene, sets other scenes to invisible*/
    public void showScene3() {
        gm.ui.disableComputerButtons(false, "function");
    	gm.sAnimator.canAnimate = true;
        gm.ui.bgPanel[1].setVisible(false);
        gm.ui.bgPanel[2].setVisible(false);
        gm.ui.bgPanel[4].setVisible(false);
        gm.ui.bgPanel[3].setVisible(true);

        // Play/stop audio
        loadSounds();
        gm.ui.fanSounds.play(gm.fanStart);
        gm.ui.computerSounds.play(gm.computerBootUp);
        gm.ui.computerMusic.play(gm.computerAudio);
        gm.ui.computerMusic.loop(gm.computerAudio);
        gm.ui.fanMusic.play(gm.fanBlow);
        gm.ui.fanMusic.loop(gm.fanBlow);
        gm.playSE(gm.ambience2, 1);
        ambience.start();
        gm.ui.tempTimer.start();

        // Change date depending on night/office level
        gm.ui.monday.setVisible(false);
        gm.ui.tuesday.setVisible(false);
        gm.ui.wednesday.setVisible(false);
        gm.ui.thursday.setVisible(false);
        gm.ui.friday.setVisible(false);

        // update the night/office level
        switch(gm.night) {
            case 1: gm.ui.monday.setVisible(true); break; // gm.playSE(gm.tutorial, 1);
            case 2: gm.ui.tuesday.setVisible(true); break;
            case 3: gm.ui.wednesday.setVisible(true); break;
            case 4: gm.ui.thursday.setVisible(true); break;
            case 5: gm.ui.friday.setVisible(true); break;
        }
    }

    /** Show night pass/closing scene*/
    public void showScene4() {
        gm.ui.bgPanel[3].setVisible(false);
        gm.ui.bgPanel[4].setVisible(true);
        gm.ui.computerMusic.stop(gm.computerAudio);
        gm.ui.fanMusic.stop(gm.fanBlow);
        gm.playSE(gm.nightWin, 1);
    }

    /** Show win scene*/
    public void showWinScene() {
        gm.ui.bgPanel[4].setVisible(false);
        gm.ui.bgPanel[5].setVisible(true);
        gm.playSE(gm.nothing, 0);
    }

    /** Load default sounds for night*/
    public void loadSounds() {
        gm.ui.computerMusic.setFile(gm.computerAudio);
        gm.ui.fanMusic.setFile(gm.fanBlow);
        gm.ui.fanSounds.setFile(gm.fanStart, 1);
        gm.ui.computerSounds.setFile(gm.computerBootUp, 1);
    }

}
