package main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/** Creates an Animatronic*/
public class Animatronic {

    private boolean onLeftSide = false;
    AnimatronicControl aControl;

    private int currentNode;
    private final HashMap<Integer, ArrayList<ArrayList<Integer>>> GRAPH;

    private final int ANIMATRONICNUMBER;
    private int freddyVentTime = 0;
    private long time = 0;
    /** Controls actions when inside a vent next to your room*/
    public Timer freddyVentTimer = new Timer(3000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("in Vent");
            Random r = new Random();
            aControl.ui.gm.playSE(aControl.ui.gm.ventNoises[r.nextInt(12) + 1], 0.4f);
            // if animatronic in vent for too long, you loose and animatronics reset
            if (freddyVentTime == 5) {
                System.out.println("get jumped");

                aControl.ui.disableComputer(true);

                aControl.resetAnimatronics();
                aControl.ui.gm.sAnimator.canAnimate = false;
                aControl.ui.gm.sAnimator.createAnimator("freddyJump", 0, 0, aControl.ui.gm.SCREENWIDTH, aControl.ui.gm.SCREENHEIGHT);
                aControl.ui.gm.sAnimator.startAnimation();
                aControl.ui.gm.playSE(aControl.ui.gm.jumpScare, 1);
                aControl.ui.gm.playSE(aControl.ui.gm.nothing, 0);
            }
            // if you are looking at the vent the animatronic is in, it gets pushed out otherwise it moves closer to you
            if (aControl.ui.gm.sAnimator.lookingAtSide == 0 && onLeftSide) {
                if (freddyVentTime <= 0) {
                    currentNode = 3;
                    moveTimer.start();
                    freddyVentTimer.stop();
                }
                freddyVentTime--;

            }
            else if (aControl.ui.gm.sAnimator.lookingAtSide == 2 && !onLeftSide) {
                if (freddyVentTime <= 0) {
                    currentNode = 10;
                    moveTimer.start();
                    freddyVentTimer.stop();
                }

                freddyVentTime--;
            }
            else {
                freddyVentTime++;
            }
        }
    });

    /** Creates a flicker effect at the location of the animatronic on the motion screen*/
    public Timer flicker = new Timer(50, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (time == 0) {
                updatePos();
                if (aControl.ui.onMotion && aControl.ui.gm.sAnimator.lookingAtSide == 1) {
                    aControl.ui.motionWarn[ANIMATRONICNUMBER].setVisible(true);
                }
            }
            time++;

            if (time == 2) {
                time = 0;
                aControl.ui.motionWarn[ANIMATRONICNUMBER].setVisible(false);
                flicker.stop();
            }
        }
    });

    /** moves the animatronic every 3 seconds*/
    public Timer moveTimer = new Timer(3000, new ActionListener() { //CREATE WORKING MOTION MAP AND AUDIO MAP AND OTHER GRAPHS

        @Override
        public void actionPerformed(ActionEvent e) {

            if (currentNode == 1) {
                onLeftSide = true;
            }
            else if (currentNode == 8) {
                onLeftSide = false;
            }
            if (currentNode == 0) {
            	System.out.println("movement stoped");
            	freddyVentTimer.start();
                moveTimer.stop();
            }
            else {
            	moveAnimatronic();
            }
        }
    });

    /**
     * Creates the animatronic
     * @param animatronicNumber identifies the animatronic from others
     * @param aControl is the AnimatrnoicControl and connects to Animatronic
     * @param currentNode is the given starting node for the animatronic
     */
    public Animatronic(AnimatronicControl aControl, int currentNode, int animatronicNumber) {
        this.ANIMATRONICNUMBER = animatronicNumber;
        this.currentNode = currentNode;
        this.GRAPH = aControl.graph;
        this.aControl = aControl;

        updatePos();
    }

    /** Moves the animatronic using graph theory*/
    public void moveAnimatronic() {
        Random r = new Random();
        int randNum = r.nextInt(100) + 1;
        
        int nearSound = soundCheck();

        //uses default chances in the graph unless there is a room with audio nearby
        if (nearSound == -1 || r.nextInt(100)+1 <= 40) {
        	for (ArrayList<Integer> node : GRAPH.get(currentNode)) {
        		if (randNum <= node.get(1)) {
        			currentNode = node.get(0);
        			if (currentNode != 0) {
                        if(aControl.ui.computerOn) {
                            flicker.start();
                        }
        			}
        			break;
        		}
        	}
        }
        else {
        	currentNode = nearSound;
        }
        System.out.println("Animatronic moved to " + currentNode);

    }

    /** Starts movement of the animatronic*/
    public void movementStart() {
    	
        moveTimer.start();
    }

    /**
     * Checks if the animatronic is near a room with audio playing
     * @return the room location or -1 if room is not nearby
     * */
    private int soundCheck() {
    	
    	for (ArrayList<Integer> node : aControl.connectedNodes) {
    		if (node.get(0) == currentNode) {
    			return node.get(0);
    		}
    	}
    	return -1;
    }

    /** converts the room number into x and y to be used as image coordinates*/
    public void updatePos() {
        int x = aControl.ui.roomLocations.get(currentNode).get(0);
        int y = aControl.ui.roomLocations.get(currentNode).get(1);
        aControl.ui.motionWarn[ANIMATRONICNUMBER].setBounds(x, y, 46, 41);
    }
}