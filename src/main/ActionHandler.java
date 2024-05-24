package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** This class deals with transitions*/
public class ActionHandler  implements ActionListener {

    GameManager gm;

    /** Connects ActionHandler to GameManager
     * @param gm is the GameManager
     */
    public ActionHandler(GameManager gm) {

        this.gm = gm;

    }

    /** Run a fade animation if fade animation not already running*/
    @Override
    public void actionPerformed(ActionEvent e) {

        if (gm.sChanger.canFade) {
            gm.ui.fPane.fadeIn();
        }
    }

    /**
     * Switch scenes depending on option
     * @param option is the scene to switch to
     */
    public void switchScenes(int option) {

        switch (option) {
            case 2 -> {
                gm.sChanger.showScene2();
                System.out.println("TitleScreen clicked");
            }
            case 3 -> {
                gm.sChanger.showScene3();
                System.out.println("Scene2 clicked");
                gm.ui.aControl.startAnimatronics();
            }
            case 4 -> {
                gm.sChanger.showScene4();
                System.out.println("Night1 clicked");
            }
            case 5 -> {
                gm.sChanger.showWinScene();
                System.out.println("win");
            }
        }
    }
}
