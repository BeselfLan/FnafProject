package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * create and play animations
 */
public class SceneAnimator {

    GameManager gm;

    // lookintAtSide = 0 means left vent, 2 means right vent, 1 means looking middle, at the computer

    public JPanel aPane;
    private Timer timer;
    public int nextImage = 0;
    private BufferedImage[] turnLeftImages = new BufferedImage[16];
    private BufferedImage[] turnRightImages = new BufferedImage[16];
    private BufferedImage[] closingImages = new BufferedImage[16];
    private BufferedImage[] freddyJumpImages = new BufferedImage[14];
    private BufferedImage[] loadingImages = new BufferedImage[10];
    private Image img = null;

    private AnimationListener animationListener;

    public boolean turningBack = false;
    public boolean canAnimate = true;
    public String animationName = "turn";
    public int lookingAtSide = 1;
    public String action;

    private int counter = 0;
    private int width, height;

    /**
     * connects SceneAnimator with GameManager, loads animation resources
     * @param gm is the GameManager
     */
    public SceneAnimator(GameManager gm) {

        this.gm = gm;
        loadImages();
    }

    /** Loads all the animation images into arrays*/
    public void loadImages() {

        // Load turn left frames into an array
        for (int i = 1; i < turnLeftImages.length + 1; i++) {
            gm.loadBufferedImage("turnLeft" + i + ".png");
            turnLeftImages[i-1] = gm.buffImage;
        }

        // Load turn right frames into an array
        for (int i = 1; i < turnRightImages.length + 1; i++) {
            gm.loadBufferedImage("turnRight" + i + ".png");
            turnRightImages[i-1] = gm.buffImage;
        }

        // Load closing animation
        for (int i = 1; i < closingImages.length + 1; i++) {
            gm.loadBufferedImage("closingTime" + i + ".png");
            closingImages[i-1] = gm.buffImage;
        }
        
        // Load jump scare
        for (int i = 1; i < freddyJumpImages.length + 1; i++) {
            gm.loadBufferedImage("freddyJump" + i + ".png");
            freddyJumpImages[i-1] = gm.buffImage;
        }
    }

    /**
     * Loads an animation of choice and location
     * @param action used to pick the animation you want
     * @param x used to get x value of animation
     * @param y used to get y value of animation*
     * @param width used to get width value of animation
     * @param height used to get height value of animation*
     * */
    public void createAnimator(String action, int x, int y, int width, int height) {
        timer = new Timer(20, listener); //make sure animation not called during animation, figure how to turn back from vent
        this.action = action;
        this.width = width;
        this.height = height;

        counter = 0;
        nextImage = 0;

        // JPanel used for displaying animating frames
        aPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                switch(action) {
                    case "turnLeft": img = turnLeftImages[nextImage]; break;
                    case "turnRight": img = turnRightImages[nextImage]; break;
                    case "closingTime": img = closingImages[nextImage]; break;
                    case "freddyJump": img = freddyJumpImages[nextImage]; break; 
                }

                g.drawImage(img , x, y, width, height, this);
            }

            @Override
            public Dimension getPreferredSize() {

                return new Dimension(width, height);
            }
        };
        aPane.setBounds(0,0,gm.SCREENWIDTH,gm.SCREENHEIGHT);
        aPane.setBackground(new Color(0, 0, 0, 0));

        gm.ui.window.add(aPane);
    }

    /** Listener to help avoid animating at the same time*/
    public void setAnimationListener(AnimationListener listener) {

        animationListener = listener;
    }

    /** Detect when animation starts*/
    protected void animationStarted() {

        if (animationListener != null) {
            animationListener.animationStarted(this);
        }
    }

    /** Detect when animation is complete*/
    protected void animationCompleted() {
        if (animationListener != null) {
            animationListener.animationCompleted(this);
        }
    }

    /** Start the timer that starts animation sequence of the animation loaded*/
    public void startAnimation() {
        animationStarted();
        timer.start();
    }

    /** ActionListener to control animations*/
    ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (action.equals("turnLeft") || action.equals("turnRight")) {
                animateTurn();
            }
            else if (action.equals("closingTime")) {
                animateClosing();
            }
            else if (action.equals("freddyJump")) {
            	animateJumpScare();
            }
            else {
                System.out.println("Animation broken (probably misspelled a string");
            }
            aPane.repaint();
        }
    };

    /** Animate the closing shiny effect after each night*/
    public void animateClosing() {
        nextImage ++;
        if (nextImage >= closingImages.length) {
            nextImage = 0;
            timer.stop();
        }
//        System.out.println("nextImage = " + nextImage + " for closing");
    }

    /** Animate turning left and right between vents*/
    public void animateTurn() {

        // If currently looking one vent, then when looking back, repaint animation in reverse
    	if (animationName.equals("turn")) {
            if (turningBack) {
                nextImage--;
            } else {
                nextImage++;
            }

            // If currently animating, then disable the computer buttons so they can't be used when not looking at computer
            if (!turningBack && nextImage == 1) {
                gm.ui.disableComputerButtons(true, "function");
            } else if (turningBack && nextImage == 1) {
                gm.ui.disableComputerButtons(false, "function");
            }

            // Detect if looking at vent or computer, stop animating if so
            if (nextImage >= turnLeftImages.length) {
                nextImage = turnLeftImages.length - 1;
                turningBack = true;

                timer.stop();
                // Update which direction the player is looking at
                if (action == "turnLeft") {
                    lookingAtSide = 0;
                } else if (action == "turnRight") {
                    lookingAtSide = 2;
                }
                animationCompleted();
            } else if (nextImage <= 0) {
                nextImage = 0;
                turningBack = false;

                timer.stop();
                lookingAtSide = 1;
                aPane.setVisible(false);
                animationCompleted();
            }

            //change visibility of task options only when looking at computer to avoid visual glitching
            if (lookingAtSide == 1) {
            	gm.ui.updateTasks();
            }
        }
    }
    /** Jump scare animation*/
    public void animateJumpScare() {

        //increment the frame number
        if (nextImage < freddyJumpImages.length) {
        	nextImage++;
        }
        // If on the last frame of the jumpScare, then start jumpScareTimer and stop animating
        if (nextImage >= freddyJumpImages.length) {
    		nextImage = freddyJumpImages.length - 1;
            timer.stop();
            jumpScareTimer.start();

    	}
    }
    /** Wait timer after jump scare for better effect*/
    public Timer jumpScareTimer = new Timer(500, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {

            // counter used as delay
            counter++;

            //reset the night after waiting 2000 miliseconds after the jump scare
            if (counter >= 5) {
            	gm.ui.computerSounds.stop(gm.printPaper);
                gm.ui.loadingImage.setVisible(false);
                gm.ui.resetNight();
                gm.ui.resetTaskTimer();
                gm.ui.aControl.resetAnimatronics();
                gm.ui.aControl.createAnimatronics();
                gm.ui.computerMusic.stop(gm.computerAudio);
                gm.ui.fanMusic.stop(gm.fanBlow);
                gm.se.stop(gm.ambience1);
                gm.se.stop(gm.ambience2);
                gm.ui.command = 2;
                gm.ui.fPane.fadeIn();


                gm.ui.disableComputer(false);

                jumpScareTimer.stop();
            }
        }
    });
}