/*
 * StringState.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.ui.inputstate;

import java.awt.event.KeyEvent;

import uk.co.telperion.mangband.input.InputCallback;
import uk.co.telperion.mangband.input.MangClientImpl;
import uk.co.telperion.mangband.input.StringCallback;
import uk.co.telperion.mangband.ui.InputMode;

public class StringState implements InputState {

	private int inputPos;
    private final byte[] inputBuffer;
    private final int INPUT_BUFFER_SIZE = 1024;
    private final int maxLength;
    private final char passChar;

	private final InputCallback inputCallback;
	private final InputStateUser state_user;
	private final MangClientImpl client;
	private final InputTerminal terminal;

	public StringState(InputCallback callback, InputStateUser state_user, MangClientImpl client, InputTerminal terminal, int maxLen, char passChar) {
		this.inputCallback = callback;
		this.state_user = state_user;
		this.client = client;
		this.terminal = terminal;
		this.maxLength = maxLen;
		this.passChar = passChar;
        inputBuffer = new byte[INPUT_BUFFER_SIZE];
	}

	@Override
	public void handleEvent(KeyEvent event) {
	  int code = event.getKeyCode();
    StringCallback cb = (StringCallback) inputCallback;

    if (code == KeyEvent.VK_ESCAPE) {
      inputPos = 0;
      terminal.leaveInput(true);
      cb.cancel();
      return;
    }

    if (code == KeyEvent.VK_BACK_SPACE) {
      if (inputPos > 0) {
        inputPos--;
        terminal.backSpace();
      }
      return;
    }

    char ch = event.getKeyChar();

    if (ch == 0xffff) {
      return;
    }

    switch (ch) {
      case '\n':
        state_user.setState(InputStateFactory.createState(InputMode.Default, state_user, client, null, null, 0, '\0'));
        int length = inputPos;
        inputPos = 0;
        terminal.leaveInput(false);
        cb.update(new String(inputBuffer, 0, length));
        break;
      default:
        if (maxLength > inputPos) {
          inputBuffer[inputPos++] = (byte) ch;
          if (this.passChar != '\0') {
            ch = this.passChar;
          }
          terminal.addInput(ch);
        }
    }
	}
}
