/*
 * MangbandTerm.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.ui;

import javax.swing.*;

import uk.co.telperion.mangband.TermColours;
import uk.co.telperion.mangband.input.BooleanCallback;
import uk.co.telperion.mangband.input.CharCallback;
import uk.co.telperion.mangband.input.DirectionCallback;
import uk.co.telperion.mangband.input.MangClientImpl;
import uk.co.telperion.mangband.input.NumberCallback;
import uk.co.telperion.mangband.input.StringCallback;
import uk.co.telperion.mangband.ui.inputstate.InputTerminal;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.Date;
import java.util.Stack;

/**
 * Main terminal window for the game client. This class is responsible for the
 * screen and keyboard interactions.
 *
 * @author evileye
 */
public class MangbandTerm extends JFrame implements KeyListener, InputTerminal {

	private static final long serialVersionUID = 1;
	public static final int ESCAPE = 27;
	private final int TERM_WIDTH = 80;
	private final int TERM_HEIGHT = 32;
	private final ColourMap colourMap = new ColourMap();
	private TermModel currentTerm;
	private final TermModel baseTerm;
	private int charWidth, charHeight, ascent;
	private MangClientImpl client;
	private BufferStrategy dblBuffer;
	private int curs_x, curs_y;
	private boolean curs_visible;
	private boolean awaitEscape;
	private final Stack<TermModel> storedTerms;
	private long pauseTime;
	private Image gfx;
	private boolean imageLoaded;
	private final boolean useGraphics = false;
	private final InputProcessor inputProcessor;
	private char passChar;

	public MangbandTerm() {
		super();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		setBackground(Color.black);
		setForeground(Color.white);

		baseTerm = new TermModel(TERM_WIDTH, TERM_HEIGHT);
		currentTerm = baseTerm;

		colourMap.init_colours();
		addKeyListener(this);

		inputProcessor = new InputProcessor(this);

		curs_visible = false;
		storedTerms = new Stack<TermModel>();

		if (useGraphics) {
			System.out.println("Loading graphics");
			String home = System.getProperty("user.home");
			String fileName = home + "/mangband/32x32.png";
			gfx = Toolkit.getDefaultToolkit().getImage(fileName);

			gfx.getHeight(this);

			while (!imageLoaded) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	@Override
	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
		if (flags != ALLBITS) {
			if ((flags & (ERROR | ABORT)) != 0) {
				imageLoaded = true;
				return false;
			}
			return true;
		} else {
			imageLoaded = true;
			return false;
		}
	}

	public void init() {
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

		setFont(font);
		Graphics g = this.getGraphics();
		FontMetrics fm = g.getFontMetrics();
		charWidth = fm.charWidth('M');
		charHeight = fm.getHeight();
		ascent = fm.getMaxAscent();
		this.setSize(TERM_WIDTH * charWidth, TERM_HEIGHT * charHeight);

		this.createBufferStrategy(2);
		dblBuffer = this.getBufferStrategy();
	}

	/**
	 * Perform all drawing actions on buffer
	 */
	public void paintBuffer() {
		int x, y;

		long drawTime = new Date().getTime();
		if (drawTime - pauseTime < 1000) {
			return;
		}

		Graphics g = dblBuffer.getDrawGraphics();

		byte c_attr = -1;

		for (y = 0; y < TERM_HEIGHT; y++) {
			for (x = 0; x < TERM_WIDTH; x++) {
				if (currentTerm.touched(x, y)) {

					g.clearRect(x * charWidth, (y + 2) * charHeight - ascent, charWidth, charHeight);

					if (c_attr >= 0) {
						g.setColor(colourMap.getTermColour(c_attr));
					}

					if (useGraphics && (currentTerm.attrs(x, y) & 0x80) != 0) {
						int gx = currentTerm.chars(x, y) & 0x7f;
						int gy = currentTerm.attrs(x, y) & 0x7f;

						g.drawImage(gfx, x * charWidth, (y + 1) * charHeight + 5, (x + 1) * charWidth, (y + 2) * charHeight + 5,
								gx * 32, gy * 32, (gx + 1) * 32, (gy + 1) * 32, this);

					} else {
						if (c_attr != currentTerm.attrs(x, y)) {
							c_attr = currentTerm.attrs(x, y);
							g.setColor(colourMap.getTermColour(c_attr));
						}
						g.drawBytes(currentTerm.chars(), x + y * TERM_WIDTH, 1, (x) * charWidth, (y + 2) * charHeight);
					}
					currentTerm.clean(x, y);
				}
			}
		}

		if (curs_visible) {
			g.fillRect(curs_x * charWidth, (curs_y + 1) * charHeight + 5, charWidth, charHeight);
		}

		dblBuffer.show();
		Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * Set a single character on the terminal
	 *
	 * @param ch
	 *          character to be placed
	 * @param attr
	 *          attribute value of the character
	 * @param x
	 *          x position
	 * @param y
	 *          y position
	 * @param base
	 *          if true, paint on the main terminal
	 */
	public void drawChar(byte ch, byte attr, byte x, byte y, boolean base) {
		TermModel term = base ? baseTerm : currentTerm;

		if ((term.chars(x, y) != ch) || (term.attrs(x, y) != attr)) {
			term.setValue(x, y, ch, attr);
		}
	}

	/**
	 * Set a string on the terminal
	 *
	 * @param str
	 *          string to be placed
	 * @param attr
	 *          attribute value of the character
	 * @param x
	 *          x position
	 * @param y
	 *          y position
	 * @param base
	 *          if true, paint on the main terminal
	 */
	public void drawString(String str, int attr, int x, int y, boolean base) {
		TermModel term = base ? baseTerm : currentTerm;

		term.drawString(x, y, str, attr);
	}

	/**
	 * Draw string to main terminal. Calls main drawString function with base
	 * value set false.
	 *
	 * @param str
	 *          string to be drawn
	 * @param attr
	 *          colour of the string
	 * @param x
	 *          x position of the string
	 * @param y
	 *          y position of the string
	 */
	public void drawString(String str, int attr, int x, int y) {
		drawString(str, attr, x, y, false);
	}

	/**
	 * Key typed handler; unused
	 *
	 * @param event
	 *          A KeyEvent
	 */
	@Override
	public void keyTyped(KeyEvent event) {
	}

	/**
	 * Key released handler; unused
	 *
	 * @param event
	 *          A KeyEvent
	 */
	@Override
	public void keyReleased(KeyEvent event) {
	}

	/**
	 * Keypress handler
	 *
	 * @param event
	 *          A KeyEvent
	 */
	@Override
	public void keyPressed(KeyEvent event) {
		this.pauseTime = 0;

		if (awaitEscape) {
			if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				client.escape();
				awaitEscape = false;
			}
			return;
		}

		inputProcessor.processEvent(event);
	}

	/**
	 * Associate terminal with a client instance
	 *
	 * @param client
	 *          The client instance to attach
	 */
	public void setClient(MangClientImpl client) {
		this.client = client;
		inputProcessor.setClient(client);
	}

	/**
	 * Set cursor position
	 *
	 * @param x
	 *          New horizontal position of the cursor
	 * @param y
	 *          New vertical position of the cursor
	 */
	public void setCursor(int x, int y) {
		currentTerm.touch(curs_x, curs_y);
		curs_x = x;
		curs_y = y;
	}

	/**
	 * Set cursor visibility
	 *
	 * @param visible
	 *          Flag to determine cursor visibility
	 */
	public void setCursorVisible(boolean visible) {
		currentTerm.touch(curs_x, curs_y);
		curs_visible = visible;
	}

	/**
	 * Set escape waiting mode. Do not process keypresses until the escape key has
	 * been pressed.
	 */
	public void waitEscape() {
		awaitEscape = true;
	}

	/**
	 * Clear all characters from position to end of line
	 *
	 * @param x
	 *          Horizontal starting position
	 * @param y
	 *          Line to be cleared
	 * @param base
	 *          If set, use the main terminal rather than a copy
	 */
	public void clearLine(int x, int y, boolean base) {
		TermModel term = base ? baseTerm : currentTerm;

		term.clearLine(x, y);
	}

	/**
	 * Clear an entire line
	 *
	 * @param y
	 *          Line to be cleared
	 * @param base
	 *          If set, use the main terminal rather than a copy
	 */
	public void clearLine(int y, boolean base) {
		clearLine(0, y, base);
	}

	public void inputString(int x, int y, int maxLen, StringCallback cb) {
		setCursor(x, y);
		setCursorVisible(true);

		inputProcessor.setInputState(InputMode.String, cb, maxLen, passChar);
	}

	public void inputChar(CharCallback cb) {
		inputProcessor.setInputState(InputMode.Character, cb, 0, '\0');
	}

	public void inputShopping() {
		inputProcessor.setInputState(InputMode.Shopping, null, 0, '\0');
	}

	/**
	 * Get the terminal width
	 *
	 * @return the terminal height
	 */
	public int getTermWidth() {
		return TERM_WIDTH;
	}

	/**
	 * Get the terminal height
	 *
	 * @return the terminal height
	 */
	public int getTermHeight() {
		return TERM_HEIGHT;
	}

	/**
	 * Push a copy of the current terminal state on to a stack
	 */
	public void saveTerm() {
		storedTerms.push(currentTerm);
		currentTerm = currentTerm.copy();
	}

	/**
	 * Restore old terminal state from the stack
	 */
	public void restoreTerm() {
		if (!storedTerms.empty()) {
			currentTerm = storedTerms.pop();
			currentTerm.touch();
		} else {
			System.out.println("Attempt to pop extra term");
			new Exception().printStackTrace();
		}
	}

	public void inputNum(int x, int y, int maxLen, NumberCallback cb) {
		setCursor(x, y);
		setCursorVisible(true);
		inputProcessor.setInputState(InputMode.Numeric, cb, maxLen, '\0');
	}

	public void inputDirection(DirectionCallback cb) {
		inputProcessor.setInputState(InputMode.Direction, cb, 0, '\0');
	}

	public void setPause(Date date) {
		paintBuffer();
		pauseTime = date.getTime();
	}

	public void clear(boolean base) {
		for (int i = 0; i < TERM_HEIGHT; i++) {
			clearLine(i, base);
		}
	}

	public void clearPanel(boolean base) {
		for (int i = 0; i < TERM_HEIGHT - 7; i++) {
			clearLine(i, base);
		}
	}

	public void inputBool(BooleanCallback cb) {
		inputProcessor.setInputState(InputMode.Boolean, cb, 0, '\0');
	}

	public void setPassChar(char ch) {
		passChar = ch;
	}

	public void inputFile() {
		inputProcessor.setInputState(InputMode.File, null, 0, '\0');
	}

	@Override
	public void leaveInput(boolean clear) {
		if (clear) {
			this.clearLine(this.curs_y, false);
		}
		this.setCursorVisible(false);
	}

	@Override
	public void backSpace() {
		this.drawChar((byte) ' ', (byte) TermColours.DARK, (byte) (curs_x - 1), (byte) this.curs_y, false);
		setCursor(curs_x - 1, this.curs_y);
	}

	@Override
	public void addInput(char ch) {
		this.drawChar((byte) ch, (byte) TermColours.LIGHT_BLUE, (byte) curs_x, (byte) curs_y, false);
		setCursor(curs_x + 1, curs_y);
	}

	public void cancelInputMode() {
		inputProcessor.setInputState(InputMode.Default, null, 0, '\0');
	}
}
