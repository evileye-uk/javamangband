/*
 * InputProcessor.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.ui;

import java.awt.event.KeyEvent;

import uk.co.telperion.mangband.input.InputCallback;
import uk.co.telperion.mangband.input.MangClientImpl;
import uk.co.telperion.mangband.ui.inputstate.InputState;
import uk.co.telperion.mangband.ui.inputstate.InputStateFactory;
import uk.co.telperion.mangband.ui.inputstate.InputStateUser;
import uk.co.telperion.mangband.ui.inputstate.InputTerminal;

public class InputProcessor implements InputStateUser {

  private final InputMode defaultInputMode = InputMode.Normal;
  private final InputTerminal terminal;

  private InputState state;
  private MangClientImpl client;
  
  InputProcessor(InputTerminal terminal) {
    state = InputStateFactory.createState(InputMode.Ignore, null, client, null, terminal, 0, '\0');
    this.terminal = terminal;
  }

  @Override
	public void setState(InputState state) {
  	this.state = state;
  }

  public void setClient(MangClientImpl client) {
    this.client = client;
  }

  /**
   * Handle keypress in non-complete macro
   */
  private void do_macro(KeyEvent e) {
  }

  /**
   * Check for macro key
   *
   * @param e Key event
   */
  private void check_macros(KeyEvent e) {
    char ch = e.getKeyChar();
    int mod = e.getModifiers();
  }
  
  public void processEvent(KeyEvent event) {
  	this.state.handleEvent(event);
  }

	public void setInputState(InputMode mode, InputCallback cb, int maxLen, char passChar) {
		state = InputStateFactory.createState(mode, this, client, cb, terminal, maxLen, passChar);
	}
}
