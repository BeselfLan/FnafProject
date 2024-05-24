package main;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.*;

/** Changes and displays the UI*/

public class UI {

    GameManager gm;

    JFrame window;
    FadePane fPane;

    // maybe could of used array for some varibles, but would have taken too much time to change current code
    public JPanel[] bgPanel = new JPanel[7];
    public JLabel[] bgLabel = new JLabel[7];
    public JLabel computerLight, motionScreen, motionScanning, audioScreen, audioText, audioIcon,
                    ventText, loadingImage, fanButtonBG, computerButtonBG, fanButton, computerButton,
                    leftArrow, rightArrow, monday, tuesday, wednesday, thursday, friday;
    public JLabel[] motionWarn = new JLabel[5];

    public Temperature temp = new Temperature(this);
    public JLabel onesDigit, tensDigit, hundredsDigit;

    public Music computerMusic = new Music(), fanMusic = new Music();
    public SE computerSounds = new SE(), fanSounds = new SE();

    public JButton transitionButton;
    public JButton tasks, motion, audio, vent, logOff;
    public JButton orderSupplies, equipment, maintenance, advertising;
    public JButton orderCups, orderPlates, back;
    public JButton motionButton, ventButton;
    public JButton continueGame, newGame;

    public boolean onTask = false, onMotion = false, onAudio = false, onVent = false, orderPlatesComplete = false, orderCupsComplete = false,
    						allTasksComplete = false, isLoading = false, computerOn = true, fanOn = true;

    public int temperature = 80;
    public int command = 2;
    public String taskName = "";

    public ComputerHandler cHandler = new ComputerHandler(this);
    public HashMap<Integer, ArrayList<Integer>> roomLocations;
    public AnimatronicControl aControl;
    
    private ImageIcon buttonImage;
    
    /** Controls temperature increase*/
    public Timer tempTimer = new Timer(2500, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

            int tempValue = temperature;
            
            // if the fan is on, then temperature doesn't increase
            if (!fanOn) {
                temperature++;
                if (temperature == 120) {
                    tempTimer.stop();
                    // End game, reset to start of the night
                }
            }
            else {
                temperature--;
                if (temperature < 80) {
                    temperature = 80;
                }
            }
            
            // Updates the temperature display
            if (temperature == 100) {
                if (gm.sAnimator.lookingAtSide == 1) {
                    tensDigit.setIcon(temp.setNumber(0));
                    onesDigit.setIcon(temp.setNumber(0));
                }
            }
            else {
                if (temperature > 100) {
                    tempValue -= 100;
                    hundredsDigit.setVisible(true);
                } else {
                    hundredsDigit.setVisible(false);
                }
                if (gm.sAnimator.lookingAtSide == 1) {
                    tensDigit.setIcon(temp.setNumber(Math.floorDiv(tempValue, 10)));
                    onesDigit.setIcon(temp.setNumber(tempValue % 10));
                }
            }
		}
    });
    
    /** Controls how long it takes to complete a task*/
    public long taskTime = 0;
    private long diff;
    public Timer taskTimer = new Timer(20, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
        	
        	// Play task sounds, increase the chance that animatronic moves closer to your room
            if (taskTime == 0) {
            	computerSounds.setFile(gm.printPaper, 1);
                computerSounds.play(gm.printPaper);
                isLoading = true;
                taskTime = System.currentTimeMillis();
                aControl.createTaskGraph();
                disableOrderSupplies(true, "function");
                System.out.println(aControl.graph);
            }
            
            // Once task done, reset the chances that animatronic moves closer to your room, update the task image
            diff = System.currentTimeMillis() - taskTime;
            if (diff >= 70000) {
                isLoading = false;
                resetTaskTimer();
                cHandler.updateComputer("taskComplete");
                switch (taskName) {
                	case "orderPlates": 
                		if (gm.sAnimator.lookingAtSide == 1) {
                			orderPlates.setVisible(false); 
                	        loadingImage.setVisible(false);
                		} 
                		orderPlatesComplete = true; 
                		
                		break;
                	case "orderCups": 
                		if (gm.sAnimator.lookingAtSide == 1) {
                			orderCups.setVisible(false);
                	        loadingImage.setVisible(false);
                		}
                		orderCupsComplete = true; 
                		break;
                }
                gm.playSE(gm.taskFinish, 1);
            }
        }
    });

    /** Once the logOff button is pressed, display the ending animation transition between nights*/
    public long closingTime = 0;
    public Timer closingTimer = new Timer(305, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        	
        	// Load the closing animation
            if (closingTime == 0) {
                gm.sAnimator.createAnimator("closingTime", 760, 500, 400, 80);
                closingTime = System.currentTimeMillis();
            }
            diff = System.currentTimeMillis() - closingTime;
            
            // Play the closing animation
            if (diff > 2000) {
                gm.sAnimator.startAnimation();
            }
            
            // Update the night number, fade out to next night. If finished night 5, display win scene
            if (diff > 10000) {
                command = 3;
                if (gm.night == 6) {
                    gm.night = 5;
                    command = 5;
                }
                closingTime = 0;
                fPane.fadeIn();
                gm.playSE(gm.nothing, 0);
                closingTimer.stop();
            }
        }
    });
    
    /** reset the timers*/
    public void resetTaskTimer() {
        aControl.resetGraph();
        System.out.println(aControl.graph);
        diff = 0;
        taskTime = 0;
        taskTimer.stop();
        disableOrderSupplies(false, "function");
    }
    public void resetTimers() {
        taskTimer.stop();
        resetTaskTimer();
        closingTime = 0;

        tempTimer.stop();
        resetTemp();
    }
    
    /**
     * Main method, creates all the objects
     * @param gm connects GameManager class and UI class
     */
    public UI(GameManager gm) {
        this.gm = gm;

        createMainField();
        generateScene();
        setupAnimation();
        createFadeTransition();


        aControl  = new AnimatronicControl(this);

        window.setVisible(true);

    }
    
    /** Update the tasks so once they are complete, they are not visible or re-clickable*/
    public void updateTasks() {
    	if (gm.ui.orderPlatesComplete) {
        	gm.ui.orderPlates.setVisible(false);
            loadingImage.setVisible(false);
        }
        if (gm.ui.orderCupsComplete) {
        	gm.ui.orderCups.setVisible(false);
            loadingImage.setVisible(false);
        }
    }
    
    /** Create JFrame*/
    public void createMainField() {

        window = new JFrame("FNAF 6");

        Image img = new ImageIcon("imageIcon.png").getImage();
        window.setIconImage(img);

        window.setSize(gm.SCREENWIDTH, gm.SCREENHEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(null);

    }
    
    /**
     * Create background images and load bgPanel array
     * @param bgNum determines which scene number you want to load
     * @param bgFileName determines the background image to be used
     */
    public void createBackground(int bgNum, String bgFileName) {

        bgPanel[bgNum] = new JPanel();
        bgPanel[bgNum].setBounds(0,0,gm.SCREENWIDTH,gm.SCREENHEIGHT);//background size doesn't change so no need for paramters
        bgPanel[bgNum].setLayout(null);
        bgPanel[bgNum].setVisible(false);
        window.add(bgPanel[bgNum]);

        bgLabel[bgNum] = new JLabel();
        bgLabel[bgNum].setBounds(0,0,gm.SCREENWIDTH,gm.SCREENHEIGHT);

        ImageIcon bgIcon = new ImageIcon(getClass().getClassLoader().getResource(bgFileName));
        bgLabel[bgNum].setIcon(bgIcon);
    }
    
    /** create fade animation*/
    public void createFadeTransition() {

        gm.loadBufferedImage("solidBlack.jpg");
        fPane = new FadePane(gm.buffImage);
        fPane.setBounds(0,0,gm.SCREENWIDTH,gm.SCREENHEIGHT);
        fPane.setBackground(new Color(0, 0, 0, 0));

        window.add(fPane);

        fPane.setFadeListener(new FadeListener() {

            @Override
            public void fadeStarted(FadePane pane) {

                transitionButton.setEnabled(false);
                gm.sChanger.canFade = false;
                System.out.println("fading");
            }

            @Override
            public void fadeCompleted(FadePane pane) {

                transitionButton.setEnabled(true);
                gm.aHandler.switchScenes(command);
                gm.sChanger.canFade = true;
                command++;

            }
        });
    }
    
    /** Creates AnimationListener for animation*/
    public void setupAnimation() {

        gm.sAnimator.setAnimationListener(new AnimationListener() {

            @Override
            public void animationStarted(SceneAnimator aPane) {
                gm.sAnimator.canAnimate = false;
            }

            @Override
            public void animationCompleted(SceneAnimator aPane) {
                gm.sAnimator.canAnimate = true;
            }
        });
    }

    /**
     * Creates a button to be used to transition between scenes
     * @param bgNum is used to determine which scene the button is to be added to
     * @param x determines the x coordinate of the button
     * @param y determines the y coordinate of the button
     * @param width determines the width of the button
     * @param height determines the height of the button
     * @param fileName is used to choose the image to be displayed as the button
     * */
    public void createTransitionButton(int bgNum, int x, int y, int width, int height, String fileName) {

        ImageIcon arrowIcon = new ImageIcon(getClass().getClassLoader().getResource(fileName));

        transitionButton = new JButton();
        transitionButton.setBounds(x, y, width, height);
        transitionButton.setBackground(null);
        transitionButton.setContentAreaFilled(false);
        transitionButton.setFocusPainted(false);
        transitionButton.setIcon(arrowIcon);
        transitionButton.addActionListener(gm.aHandler);
        transitionButton.setBorderPainted(false);

        bgPanel[bgNum].add(transitionButton);

    }

    /**
     * Creates a JTextArea to display text
     * @param bgNum determines which scene to add the text to
     * @param x determines the x coordinate of the text
     * @param y determines the y coordinate of the text
     * @param width determines the width of the text
     * @param height determines the height of the text
     * @param text is the text to be displayed
     * @param color is the color of the text
     * @param font is the font of the text
     * @param fontSize is the fontSize of the text
     */
    public void createText(int bgNum, int x, int y, int width, int height, String text, Color color, String font, int fontSize) {

        JTextArea messageText = new JTextArea(text);
        messageText.setBounds(x, y, width, height);
        messageText.setForeground(color);
        messageText.setBackground(new Color(0, 0, 0, 0));
        messageText.setLineWrap(true);
        messageText.setFont(new Font(font, Font.PLAIN, fontSize));
        messageText.setEditable(false);
        bgPanel[bgNum].add(messageText);

    }

    /** Loads bgPanel*/
    public void generateScene() {

        // Load 1st scene
        createBackground(1, "fnafTitle.png");

        continueGame = new JButton();
        continueGame.setBounds(760, 810, 400, 99);
        continueGame.setBackground(null);
        continueGame.setContentAreaFilled(false);
        continueGame.setFocusPainted(false);
        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("continueGame.png"));
        continueGame.setIcon(buttonImage);
        continueGame.addActionListener(gm.aHandler);
        continueGame.setBorderPainted(false);
        bgPanel[1].add(continueGame);
        continueGame.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {

                gm.night = gm.fm.getNight();
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        newGame = new JButton();
        newGame.setBounds(760, 909, 400, 99);
        newGame.setBackground(null);
        newGame.setContentAreaFilled(false);
        newGame.setFocusPainted(false);
        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("newGame.png"));
        newGame.setIcon(buttonImage);
        newGame.addActionListener(gm.aHandler);
        newGame.setBorderPainted(false);
        bgPanel[1].add(newGame);
        newGame.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {

                gm.night = 1;
                gm.fm.modifySaveFile("night=1");

            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        bgPanel[1].add(bgLabel[1]);

        // Load 2nd scene
        createBackground(2, "solidBlack.jpg");
        createText(2, gm.SCREENWIDTH/2-450, gm.SCREENHEIGHT/2-110, 1000, 100, "Please wear headphones for a better experience\n" +
                    "WARNING this game may contain jump scares", Color.white, "Courier New", 30);
        createText(2, gm.SCREENWIDTH/2-240, gm.SCREENHEIGHT/2+100, 400, 100, "Click to begin night", Color.white, "Courier New", 24);
        createTransitionButton(2, 0, 0, gm.SCREENWIDTH, gm.SCREENHEIGHT, "transparentBackground.png");
        bgPanel[2].add(bgLabel[2]);

        // Load 3rd scene
        createBackground(3, "firstNightBG.png");
        loadDates();

        createComputerScreen();
        createTemperature();

        leftArrow = new JLabel();
        leftArrow.setBounds(5, 151, 66, 778);
        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("turnLeftArrow.png"));
        leftArrow.setIcon(buttonImage);
        bgPanel[3].add(leftArrow);

        rightArrow = new JLabel();
        rightArrow.setBounds(1849, 151, 66, 778);
        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("turnRightArrow.png"));
        rightArrow.setIcon(buttonImage);
        bgPanel[3].add(rightArrow);

        computerButton = new JLabel();
        computerButton.setBounds(10, 980, 65, 89);
        computerButton.setBackground(null);
        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("turnOffComputer.png"));
        computerButton.setIcon(buttonImage);
        bgPanel[3].add(computerButton);

        fanButton = new JLabel();
        fanButton.setBounds(85, 978, 65, 90);
        fanButton.setBackground(null);
        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("turnOffFan.png"));
        fanButton.setIcon(buttonImage);
        bgPanel[3].add(fanButton);
        
        computerButtonBG = new JLabel();
        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("onBG.png"));
        computerButtonBG.setIcon(buttonImage);
        computerButtonBG.setBounds(12, 967, 65, 89);
        computerButtonBG.setBackground(null);
        bgPanel[3].add(computerButtonBG);
        
        fanButtonBG = new JLabel();
        fanButtonBG.setIcon(buttonImage);
        fanButtonBG.setBounds(87, 966, 65, 90);
        fanButtonBG.setBackground(null);
        bgPanel[3].add(fanButtonBG);

        // Turn on/off the fan/computer depending on what key is pressed through key binds
        InputMap inputMap = bgPanel[3].getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = bgPanel[3].getActionMap();

        Action toggleComputer = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gm.sAnimator.lookingAtSide == 1) {
                    if (computerOn) {
                        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("offBG.png"));
                        computerButtonBG.setIcon(buttonImage);
                        gm.stopMusic(gm.computerAudio);
                        gm.playSE(gm.computerOffAudio, 1);
                        computerOn = false;

                        taskTimer.stop();

                        disableComputer(false);
                    } else {
                        computerOn = true;
                        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("onBG.png"));
                        gm.playMusic(gm.computerAudio);
                        gm.playSE(gm.computerBootUp, 1);
                        computerButtonBG.setIcon(buttonImage);

                        disableComputer(true);
                    }
                }
            }
        };
        Action toggleFan = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gm.sAnimator.lookingAtSide == 1) {
                    if (fanOn) {
                        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("offBG.png")); //add fan turn off audio
                        fanButtonBG.setIcon(buttonImage);
                        fanMusic.stop(gm.fanBlow);
                        gm.playSE(gm.fanOffAudio, 1);
                        fanOn = false;
                        onTask = false;
                    } else {
                        fanOn = true;
                        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("onBG.png")); // replace image
                        fanButtonBG.setIcon(buttonImage);
                        fanMusic.play(gm.fanBlow);
                        fanMusic.loop(gm.fanBlow);
                        fanSounds.play(gm.fanStart);
                    }
                }
            }
        };

        inputMap.put(KeyStroke.getKeyStroke("Z"), "toggleComputer");
        actionMap.put("toggleComputer", toggleComputer);

        inputMap.put(KeyStroke.getKeyStroke("X"), "toggleFan");
        actionMap.put("toggleFan", toggleFan);

        bgPanel[3].add(bgLabel[3]);

        // Create mouse movement detection to decide when the player turns left/right
        bgPanel[3].addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int mouseX = e.getX();

                if (gm.sAnimator.canAnimate) {
                    if (mouseX < gm.SCREENWIDTH / 5 && !(gm.sAnimator.lookingAtSide == 0)) {
                        if (gm.sAnimator.lookingAtSide == 1) {
                            gm.sAnimator.createAnimator("turnLeft", 0, 0, gm.SCREENWIDTH, gm.SCREENHEIGHT);
                            gm.sAnimator.aPane.setVisible(true);
                        }
                        gm.sAnimator.startAnimation();

                    }
                    else if (mouseX > gm.SCREENWIDTH - gm.SCREENWIDTH / 5 && !(gm.sAnimator.lookingAtSide == 2)) {
                        if (gm.sAnimator.lookingAtSide == 1) {
                            gm.sAnimator.createAnimator("turnRight", 0, 0, gm.SCREENWIDTH, gm.SCREENHEIGHT);
                            gm.sAnimator.aPane.setVisible(true);
                        }
                        gm.sAnimator.startAnimation();
                    }
                }
            }
        });

        // Create Closing Scene
        createBackground(4, "solidBlack.jpg");
        bgPanel[4].add(bgLabel[4]);

        // Create win scene
        createBackground(5, "winEnding.png");
        bgPanel[5].add(bgLabel[5]);
        
    }

    /** Loads all weekday images and adds them to office scene*/
    public void loadDates() {

        monday = new JLabel();
        monday.setBounds(761, 20, 251, 66);
        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("monday.png"));
        monday.setIcon(buttonImage);
        monday.setVisible(false);
        bgPanel[3].add(monday);

        tuesday = new JLabel();
        tuesday.setBounds(761, 20, 251, 66);
        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("tuesday.png"));
        tuesday.setIcon(buttonImage);
        tuesday.setVisible(false);
        bgPanel[3].add(tuesday);

        wednesday = new JLabel();
        wednesday.setBounds(675, 20, 355, 66);
        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("wednesday.png"));
        wednesday.setIcon(buttonImage);
        wednesday.setVisible(false);
        bgPanel[3].add(wednesday);

        thursday = new JLabel();
        thursday.setBounds(744, 20, 283, 66);
        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("thursday.png"));
        thursday.setIcon(buttonImage);
        thursday.setVisible(false);
        bgPanel[3].add(thursday);

        friday = new JLabel();
        friday.setBounds(776, 20, 197, 66);
        buttonImage = new ImageIcon(getClass().getClassLoader().getResource("friday.png"));
        friday.setIcon(buttonImage);
        friday.setVisible(false);
        bgPanel[3].add(friday);
    }

    /** Creates the computer screen during office scene*/
    public void createComputerScreen() {

        computerLight = new JLabel();
        computerLight.setBounds(822, 405, 650, 550);

        ImageIcon computerIcon = new ImageIcon(getClass().getClassLoader().getResource("computerScreen.png"));
        computerLight.setIcon(computerIcon);
        bgPanel[3].add(computerLight);

        // Create task tab
        createComputerButton(827, 415, 150, 50, "tasks.png", "tasks");
        tasks.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

                onMotion = false;
                if (!onTask) {
                    disableTaskTab(false, "opacity");
                }
                disableMotionScreen(true, "opacity");
                disableAudioScreen(true, "opacity");
                disableVentScreen(true, "opacity");
                gm.playSE(gm.buttonClick, 1);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        
        createComputerButton(997, 555, 296, 40, "orderSupplies.png", "orderSupplies");
        orderSupplies.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

                disableTaskTab(true, "opacity");
                disableOrderSupplies(false, "opacity");
                gm.playSE(gm.buttonClick, 1);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
//        createComputerButton(997, 600, 296, 40, "advertising.png", "advertising");
//        createComputerButton(997, 645, 296, 40, "maintenance.png", "maintenance");
//        createComputerButton(997, 690, 296, 40, "equipment.png", "equipment");
        createComputerButton(997, 735, 296, 40, "logOff.png", "logOff");

        // Reset the night, transition, and update night number
        logOff.addMouseListener(new MouseListener() {
        	@Override
            public void mouseClicked(MouseEvent e) {

                resetNight();
                disableComputerButtons(true, "opacity");
                disableComputerButtons(true, "function");
                closingTimer.start();
                gm.sAnimator.createAnimator("closingTime", 960, 540, 400, 800);
                gm.playSE(gm.buttonClick, 1);
                gm.playSE(gm.nothing, 0);
                command = 4;
                gm.night++;
                gm.fm.modifySaveFile("night=" + gm.night);
                System.out.println(gm.night);
                fPane.fadeIn();
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        
        createComputerButton(832, 555, 300, 40, "orderPlates.png", "orderPlates");
        loadingImage = new JLabel();
        loadingImage.setVisible(false);
        bgPanel[3].add(loadingImage);

        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("load1.png"));
        loadingImage.setIcon(imageIcon);

        orderPlates.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                loadingImage.setBounds(1162, 560, 30, 30);
                loadingImage.setVisible(true);
                taskName = "orderPlates";
                taskTimer.start();
                gm.playSE(gm.buttonClick, 1);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        createComputerButton(832, 600, 300, 40, "orderCups.png", "orderCups"); // FINISH WORKING ON TASK BUTTONS
        orderCups.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                loadingImage.setBounds(1162, 600, 30, 30);
                loadingImage.setVisible(true);
                taskName = "orderCups";
                taskTimer.start();
                gm.playSE(gm.buttonClick, 1);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        createComputerButton(832, 895, 296, 40, "back.png", "back");
        back.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

                disableOrderSupplies(true, "opacity");
                disableTaskTab(false, "opacity");
                gm.playSE(gm.buttonClick, 1);

            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        // Create Motion Tab
        createComputerButton(987, 415, 150, 50, "motion.png", "motion");
        motion.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

                onMotion = true;
                onTask = false;
                disableOrderSupplies(true, "opacity");
                disableTaskTab(true, "opacity");
                disableAudioScreen(true, "opacity");
                disableMotionScreen(false, "opacity");
                disableVentScreen(true, "opacity");
                gm.playSE(gm.buttonClick, 1);

            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        motionScreen = new JLabel();
        motionScreen.setBounds(840, 500, 613, 335);

        imageIcon = new ImageIcon(getClass().getClassLoader().getResource("motionScreen.png"));
        motionScreen.setIcon(imageIcon);
        motionScreen.setVisible(false);
        bgPanel[3].add(motionScreen);

        motionScanning = new JLabel();
        motionScanning.setBounds(840, 885, 171, 22);

        createComputerButton(840, 890, 552, 50, "motionScanning.png", "detectMotionButton");

        createRoomLocations();
        motionWarn[1] = new JLabel();
        imageIcon = new ImageIcon(getClass().getClassLoader().getResource("motionWarning.png"));
        motionWarn[1].setIcon(imageIcon);
        motionWarn[1].setVisible(false);
        bgPanel[3].add(motionWarn[1]);

        motionWarn[2] = new JLabel();
        imageIcon = new ImageIcon(getClass().getClassLoader().getResource("motionWarning.png"));
        motionWarn[2].setIcon(imageIcon);
        motionWarn[2].setVisible(false);
        bgPanel[3].add(motionWarn[2]);

        motionWarn[3] = new JLabel();
        imageIcon = new ImageIcon(getClass().getClassLoader().getResource("motionWarning.png"));
        motionWarn[3].setIcon(imageIcon);
        motionWarn[3].setVisible(false);
        bgPanel[3].add(motionWarn[3]);

        motionWarn[4] = new JLabel();
        imageIcon = new ImageIcon(getClass().getClassLoader().getResource("motionWarning.png"));
        motionWarn[4].setIcon(imageIcon);
        motionWarn[4].setVisible(false);
        bgPanel[3].add(motionWarn[4]);

        // Create audio tab
        createComputerButton(1147, 415, 150, 50, "audio.png", "audio");
        audio.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onMotion = false;
                onTask = false;
                disableOrderSupplies(true, "opacity");
                disableTaskTab(true, "opacity");
                disableAudioScreen(false, "opacity");
                disableMotionScreen(true, "opacity");
                disableVentScreen(true, "opacity");
                gm.playSE(gm.buttonClick, 1);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        audioScreen = new JLabel();
        audioScreen.setBounds(840, 500, 613, 335);

        imageIcon = new ImageIcon(getClass().getClassLoader().getResource("audioScreen.png"));
        audioScreen.setIcon(imageIcon); //finsihed audioText and audioButton code
        audioScreen.setVisible(false);
        bgPanel[3].add(audioScreen);

        audioText = new JLabel();
        audioText.setBounds(840, 890, 460, 25);

        imageIcon = new ImageIcon(getClass().getClassLoader().getResource("audioPlaying.png"));
        audioText.setIcon(imageIcon);
        audioText.setVisible(false);
        bgPanel[3].add(audioText);

        audioIcon = new JLabel();
        audioIcon.setBounds(roomLocations.get(7).get(0) + 5, roomLocations.get(7).get(1) + 7, 32, 32);

        imageIcon = new ImageIcon(getClass().getClassLoader().getResource("sound3.png"));
        audioIcon.setIcon(imageIcon);
        audioIcon.setVisible(false);
        bgPanel[3].add(audioIcon);

        // Create ventilation tab
        createComputerButton(1307, 415, 150, 50, "vent.png", "vent");
        vent.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onMotion = false;
                onTask = false;
                disableOrderSupplies(true, "opacity");
                disableTaskTab(true, "opacity");
                disableAudioScreen(true, "opacity");
                disableMotionScreen(true, "opacity");
                disableVentScreen(false, "opacity");
                gm.playSE(gm.buttonClick, 1);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        ventText = new JLabel();
        ventText.setBounds(840, 500, 395, 152);

        imageIcon = new ImageIcon(getClass().getClassLoader().getResource("ventText.png"));
        ventText.setIcon(imageIcon);
        ventText.setVisible(false);
        bgPanel[3].add(ventText);

        createComputerButton(840, 890, 612, 49, "activateVentButton.png", "ventButton");
    }

    /** links room number to image x and y coordinates for easy access*/
    public void createRoomLocations() {
        roomLocations = new HashMap<> ();
        ArrayList<Integer> location = new ArrayList<> (Arrays.asList(982, 783));
        roomLocations.put(1, location);

        location = new ArrayList<> (Arrays.asList(844, 783));
        roomLocations.put(2, location);

        location = new ArrayList<> (Arrays.asList(982, 644));
        roomLocations.put(3, location);

        location = new ArrayList<> (Arrays.asList(844, 644));
        roomLocations.put(4, location);

        location = new ArrayList<> (Arrays.asList(844, 505));
        roomLocations.put(5, location);

        location = new ArrayList<> (Arrays.asList(982, 505));
        roomLocations.put(6, location);

        location = new ArrayList<> (Arrays.asList(1122, 505));
        roomLocations.put(7, location);

        location = new ArrayList<> (Arrays.asList(1265, 783));
        roomLocations.put(8, location);

        location = new ArrayList<> (Arrays.asList(1405, 783));
        roomLocations.put(9, location);

        location = new ArrayList<> (Arrays.asList(1265, 644));
        roomLocations.put(10, location);

        location = new ArrayList<> (Arrays.asList(1405, 644));
        roomLocations.put(11, location);

        location = new ArrayList<> (Arrays.asList(1405, 505));
        roomLocations.put(12, location);

        location = new ArrayList<> (Arrays.asList(1265, 505));
        roomLocations.put(13, location);
    }

    /** Creates a button to be used on the Computer Screen*/
    public void createComputerButton(int x, int y, int width, int height, String bgName, String buttonName) {
        JButton button = new JButton();
        button.setBounds(x, y, width, height); // x changes by length of image(150) + spacing(5)
        button.setBackground(null);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        ImageIcon computerIcon = new ImageIcon(getClass().getClassLoader().getResource(bgName));
        button.setIcon(computerIcon);

        switch(buttonName) {
            case "tasks": tasks = button; bgPanel[3].add(tasks); break;
            case "motion": motion = button; bgPanel[3].add(motion); break;
            case "audio": audio = button; bgPanel[3].add(audio); break;
            case "vent": vent = button; bgPanel[3].add(vent); break;
            case "orderSupplies":
                orderSupplies = button;
                bgPanel[3].add(orderSupplies);
                orderSupplies.setVisible(false);
                break;
            case "orderCups":
                orderCups = button;
                bgPanel[3].add(orderCups);
                orderCups.setVisible(false);
                break;
            case "orderPlates":
                orderPlates = button;
                bgPanel[3].add(orderPlates);
                orderPlates.setVisible(false);
                break;
            case "back":
                back = button;
                bgPanel[3].add(back);
                back.setVisible(false);
                break;
            case "detectMotionButton":
                motionButton = button;
                bgPanel[3].add(motionButton);
                motionButton.setVisible(false);
                break;
            case "ventButton":
                ventButton = button;
                bgPanel[3].add(ventButton);
                ventButton.setVisible(false);
                break;
            case "logOff":
            	logOff = button;
            	bgPanel[3].add(logOff);
            	logOff.setVisible(false);
            	break;
        }
    }
    // Functions to group tab objects together by tab to make switching computer tabs easier
    /**
     * disables/enables every button and JLabel visibility on computer screen
     * @param bool determines if to disable or enable the computer screen
     */
    public void disableComputer(boolean bool) {

        if (!bool) {
            disableComputerButtons(false, "opacity");
            disableComputerButtons(false, "function");

            disableTaskTab(true, "opacity");
            disableTaskTab(true, "function");
            taskTimer.stop();
            taskTime = 0;
            loadingImage.setVisible(false);
            isLoading = false;

            disableOrderSupplies(true, "opacity");
            disableOrderSupplies(true, "function");

            disableVentScreen(true, "opacity");
            disableVentScreen(true, "function");

            disableAudioScreen(true, "opacity");
            disableAudioScreen(true, "function");

            disableMotionScreen(true, "opacity");
            disableMotionScreen(true, "function");

            computerLight.setVisible(false);
            computerSounds.stop(gm.printPaper);
        }
        else {
            disableComputerButtons(true, "opacity");
            disableComputerButtons(true, "function");

            computerLight.setVisible(true);
        }

    }
    /**
     * Disables/Enables only the JButtons on the computer screen
     * @param bool disables/enables visibility or function of buttons
     * @param command change visibility or function of buttons
     */
    public void disableComputerButtons(boolean bool, String command) {
        if (command.equals("opacity")) {
            tasks.setVisible(bool);
            disableTaskTab(bool, "opacity");
            disableOrderSupplies(bool, "opacity");
            motion.setVisible(bool);
            disableMotionScreen(bool, "opacity");
            audio.setVisible(bool);
            vent.setVisible(bool);
            disableVentScreen(bool, "opacity");
            if (allTasksComplete) {
                disableLogOffButton(bool, "opacity");
            }
        }
        else if (command.equals("function")) {
            bool = !bool;
            tasks.setEnabled(bool);
            disableTaskTab(bool, "function");
            disableOrderSupplies(bool, "function");
            motion.setEnabled(bool);
            disableMotionScreen(bool, "function");
            audio.setEnabled(bool);
            vent.setEnabled(bool);
            disableVentScreen(bool, "function");
            if (allTasksComplete){
                disableLogOffButton(bool, "function");
            }
        }
    }

    /**
     * Disables/Enables only the JButtons on the computer screen task tab
     * @param bool disables/enables visibility or function of buttons
     * @param command change visibility or function of buttons
     */
    public void disableTaskTab(boolean bool, String command) {

        if (command.equals("opacity")) {
            bool = !bool;
            orderSupplies.setVisible(bool);
            if (allTasksComplete) {
                disableLogOffButton(bool, "opacity");
            }
        }
        else if (command.equals("function")) {
            orderSupplies.setEnabled(bool);
        }
    }

    /**
     * Disables/Enables only the JButtons on the computer screen orderSupplies tab
     * @param bool disables/enables visibility or function of buttons
     * @param command change visibility or function of buttons
     */
    public void disableOrderSupplies(boolean bool, String command) {

        if (command.equals("opacity")) {
            bool = !bool;
            if (bool) {
                onTask = true;
            }
            if (!orderCupsComplete) {
                orderCups.setVisible(bool);
            }
            if (!orderPlatesComplete) {
                orderPlates.setVisible(bool);
            }
            if (isLoading) {
                loadingImage.setVisible(bool);
            }
            back.setVisible(bool);
        }
        else if (command.equals("function")) {
            orderCups.setEnabled(bool);
            orderPlates.setEnabled(bool);
            back.setEnabled(bool);
        }
    }

    /**
     * Disables/Enables only the JButtons on the computer screen motion tab
     * @param bool disables/enables visibility or function of buttons
     * @param command change visibility or function of buttons
     */
    public void disableMotionScreen(boolean bool, String command) {
    	
        if (command.equals("opacity")) {
            bool = !bool;
            motionScreen.setVisible(bool);
            motionButton.setVisible(bool);
        }
        else if (command.equals("function")) {
            motionButton.setEnabled(bool);
        }
    }

    /**
     * Disables/Enables only the JButtons on the computer screen audio tab
     * @param bool disables/enables visibility or function of buttons
     * @param command change visibility or function of buttons
     */
    public void disableAudioScreen(boolean bool, String command) {
    	
        if (command.equals("opacity")) {
            bool = !bool;
            audioScreen.setVisible(bool);
            audioText.setVisible(bool);
            audioIcon.setVisible(bool);
        }
        else if (command.equals("function")) {

        }
    }

    /**
     * Disables/Enables only the JButtons on the computer ventilation tab
     * @param bool disables/enables visibility or function of buttons
     * @param command change visibility or function of buttons
     */
    public void disableVentScreen(boolean bool, String command) {
    	
        if (command.equals("opacity")) {
            bool = !bool;
            ventText.setVisible(bool);
            ventButton.setVisible(bool);
            System.out.println(bool);

        }
        else if (command.equals("function")) {
            ventButton.setEnabled(bool);
        }
    }

    /**
     * Disables/Enables the logOff button
     * @param bool disables/enables visibility or function of button
     * @param command change visibility or function of button
     */
    public void disableLogOffButton(boolean bool, String command) {
    	
    	if (command.equals("opacity")) {
            logOff.setVisible(bool);

        }
        else if (command.equals("function")) {
            logOff.setEnabled(bool);
        }
    }

    /** loads temperature in the office room*/
    public void createTemperature() {
        onesDigit = new JLabel();
        onesDigit.setBounds(1875, 1000, 38, 81);
        onesDigit.setIcon(temp.setNumber(0));
        bgPanel[3].add(onesDigit);

        tensDigit = new JLabel();
        tensDigit.setBounds(1835, 1000, 38, 81);
        tensDigit.setIcon(temp.setNumber(8));
        bgPanel[3].add(tensDigit);

        hundredsDigit = new JLabel();
        hundredsDigit.setBounds(1800, 1000, 38, 81);
        hundredsDigit.setIcon(temp.setNumber(1));
        hundredsDigit.setVisible(false);
        bgPanel[3].add(hundredsDigit);

    }

    /** Resets the temperature back to default 80 degrees*/
    public void resetTemp() {
        temperature = 80;
        onesDigit.setIcon(temp.setNumber(0));
        tensDigit.setIcon(temp.setNumber(8));
        hundredsDigit.setVisible(false);
    }

    /** Resets the office/night to default values*/
    public void resetNight() {
        resetTimers();
        cHandler.resetTasks();
        aControl.resetAnimatronics();
        aControl.resetGraph();

        onTask = false;
        onMotion = false;
        onAudio = false;
        onVent = false;
        orderPlatesComplete = false;
        orderCupsComplete = false;
        allTasksComplete = false;
        isLoading = false;
        computerOn = true;
        fanOn = true;

        disableLogOffButton(false, "opacity");
        disableLogOffButton(false, "function");
    }
}