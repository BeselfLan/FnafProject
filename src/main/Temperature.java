package main;

import javax.swing.*;
import java.util.HashMap;

/** Converts int temperature to images*/
public class Temperature {

    UI ui;
    private HashMap<Integer, ImageIcon> digits;

    /**
     * Main method of Temperature, loads resources
     * @param ui is the UI
     */
    public Temperature(UI ui) {

        this.ui = ui;

        loadNumbers();

    }
    /** Loads number images*/
    private void loadNumbers() {
        digits = new HashMap<> ();
        ImageIcon number;

        for (int i = 0; i < 10; i++) {
            number = new ImageIcon(getClass().getClassLoader().getResource(i + ".png"));
            digits.put(i, number);
        }
    }

    /**
     * converts Int to Images
     * @param number is the integer digit to be converted
     * @return the ImageIcon of the digit
     */
    public ImageIcon setNumber(int number) {
        return digits.get(number);
    }
}
