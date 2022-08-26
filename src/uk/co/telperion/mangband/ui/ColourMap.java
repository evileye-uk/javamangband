package uk.co.telperion.mangband.ui;

import uk.co.telperion.mangband.TermColours;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;

public class ColourMap implements Serializable {
    private Color[] attrColours;

    /**
     * Obtain actual colour from attribute index.
     *
     * @param attr index of colour attribute
     * @return colour instance
     */
    Color getTermColour(byte attr) {
        if (attr >= attrColours.length) {
            System.out.println("Bad attribute: " + attr);
            new Exception().printStackTrace();
            return null;
        }

        return attrColours[attr];
    }

    /**
     * Set up terminal colours and map to attribute values.
     */
    void init_colours() {
        attrColours = new Color[16];

        class ColourAdder {
            void add(int colourId, int red, int green, int blue, char code) {
                attrColours[colourId] = new Color(red, green, blue);
            }
        }

        ColourAdder adder = new ColourAdder();
        adder.add(TermColours.DARK, 0, 0, 0, 'd');
        adder.add(TermColours.WHITE, 255, 255, 255, 'w');
        adder.add(TermColours.SLATE, 127, 127, 127, 's');
        adder.add(TermColours.ORANGE, 255, 127, 0, 'o');
        adder.add(TermColours.RED, 192, 0, 0, 'r');
        adder.add(TermColours.GREEN, 0, 127, 64, 'g');
        adder.add(TermColours.BLUE, 0, 0, 255, 'b');
        adder.add(TermColours.UMBER, 127, 64, 0, 'u');
        adder.add(TermColours.LIGHT_DARK, 64, 64, 64, 'D');
        adder.add(TermColours.LIGHT_WHITE, 192, 192, 192, 'W');
        adder.add(TermColours.VIOLET, 255, 0, 255, 'v');
        adder.add(TermColours.YELLOW, 255, 255, 0, 'y');
        adder.add(TermColours.LIGHT_RED, 255, 0, 0, 'R');
        adder.add(TermColours.LIGHT_GREEN, 0, 255, 0, 'G');
        adder.add(TermColours.LIGHT_BLUE, 0, 255, 255, 'B');
        adder.add(TermColours.LIGHT_UMBER, 192, 127, 64, 'U');
    }
}