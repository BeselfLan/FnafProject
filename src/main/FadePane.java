package main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/** JPanel that creates fade animations*/
public class FadePane extends JPanel {

    private BufferedImage source;
    private Timer timer;

    private float alpha = 0f;

    private int duration = 2000;
    private Long startTime;

    private boolean fadeOut = false;

    private FadeListener fadeListener;

    /**
     * Creates the FadePane
     * @param source is the image to be faded
     * */
    public FadePane(BufferedImage source) {

        this.source = source;

        //timer for repainting the image with less opacity each time creating fade effect
        timer = new Timer(15, e -> {

            if (startTime == null) {
                startTime = System.currentTimeMillis();
                fadeStarted();
            }
            long diff = System.currentTimeMillis() - startTime;
            alpha = (float) diff / (float) duration;
            if (alpha > 1.0) {
                timer.stop();
                alpha = 1.0f;
                fadeCompleted();
                }
            if (fadeOut) {
                alpha = 1.0f - alpha;
            }
            repaint();
        });
    }

    /**
     * creates a FadeListener
     * @param listener is a FadeListener
     * */
    public void setFadeListener(FadeListener listener) {

        fadeListener = listener;
    }

    /** this does something when fade starts*/
    protected void fadeStarted() {

        if (fadeListener != null) {
            fadeListener.fadeStarted(this);
        }
    }

    /** this does something when fade ends*/
    protected void fadeCompleted() {

        if (fadeListener != null) {
            fadeListener.fadeCompleted(this);
        }
    }

    /** Resets the timer and alpha value to default*/
    public void reset() {

        timer.stop();
        alpha = 0;
        startTime = null;
    }

    /** Fades the image in*/
    public void fadeIn() {

        reset();
        fadeOut = false;
        timer.start();
    }

    /** paints an Image changing the opacity*/
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        int x = (getWidth() - source.getWidth()) / 2;
        int y = (getHeight() - source.getHeight()) / 2;
        g2d.drawImage(source, x, y, this);
        g2d.dispose();
    }
}