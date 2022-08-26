package uk.co.telperion.mangband.ui;

import java.io.Serializable;
import java.util.Arrays;

public class TermModel implements Serializable {
    private final int term_width;
    private final int term_height;
    private byte[] term_chars;
    private byte[] term_attrs;
    private boolean[] term_touch;

    public TermModel(int term_width, int term_height) {
        this.term_width = term_width;
        this.term_height = term_height;
        term_chars = new byte[arraySize()];
        term_attrs = new byte[arraySize()];
        term_touch = new boolean[arraySize()];
    }

    public TermModel copy() {
        TermModel copy = new TermModel(term_width, term_height);
        copy.term_attrs = Arrays.copyOf(term_attrs, arraySize());
        copy.term_chars = Arrays.copyOf(term_chars, arraySize());
        copy.term_touch = Arrays.copyOf(term_touch, arraySize());
        return copy;
    }

    private int arraySize() {
        return term_width * term_height;
    }

    public byte attrs(int x, int y)
    {
        return term_attrs[offset(x, y)];
    }

    private int offset(int x, int y) {
        return x + y * term_width;
    }

    public byte chars(int x, int y)
    {
        return term_chars[offset(x, y)];
    }

    public byte[] chars() {
        return term_chars;
    }

    public boolean touched(int x, int y) {
        return term_touch[offset(x, y)];
    }

    public void clean(int x, int y) {
        term_touch[offset(x, y)] = false;
    }

    public void touch(int x, int y) {
        term_touch[offset(x, y)] = true;
    }

    /**
     * Mark the entire terminal as dirty
     */
    public void touch() {
        for (int i = 0; i < arraySize(); i++) {
            term_touch[i] = true;
        }
    }

    public void setValue(byte x, byte y, byte ch, byte attr) {
        int offset = offset(x, y);
        term_chars[offset] = ch;
        term_attrs[offset] = attr;
        touch(x, y);
    }

    public void drawString(int x, int y, String str, int attr) {
        for (int i = 0; i < str.length() && i < term_width; i++) {
            char ch = str.charAt(i);
            if (ch == 0) {
                ch = ' ';
            }
            int offset = offset(x, y) + i;
            if ((term_chars[offset] != ch) || (term_attrs[offset] != attr)) {
                term_chars[offset] = (byte) ch;
                term_attrs[offset] = (byte) attr;
                term_touch[offset] = true;
            }
        }
    }

    public void clearLine(int x, int y) {
        for (; x < term_width; x++) {
            int offset = offset(x, y);
            term_chars[offset] = 0;
            term_attrs[offset] = 0;
            term_touch[offset] = true;
        }
    }
}