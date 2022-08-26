/*
 * NumericState.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.ui.inputstate;

import java.awt.event.KeyEvent;

import uk.co.telperion.mangband.input.InputCallback;
import uk.co.telperion.mangband.input.NumberCallback;

public class NumericState implements InputState {

	private final InputCallback inputCallback;
	private int inputPos;
    private final byte[] inputBuffer;
    private final int INPUT_BUFFER_SIZE = 1024;
    private final int maxLength;
	private final InputTerminal terminal;

	public NumericState(InputCallback callback, InputTerminal terminal, int maxLen) {
		this.inputCallback = callback;
		this.terminal = terminal;
		this.maxLength = maxLen;
        inputBuffer = new byte[INPUT_BUFFER_SIZE];
	}

	@Override
	public void handleEvent(KeyEvent event) {
    int code = event.getKeyCode();
    NumberCallback cb = (NumberCallback) inputCallback;

    if (code == KeyEvent.VK_ESCAPE) {
      cb.cancel();
      inputPos = 0;
      terminal.leaveInput(true);
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
        int num;
        if (inputPos > 0) {
          num = Integer.parseInt(new String(inputBuffer, 0, inputPos));
        } else {
          num = 0;
        }
        cb.update(num);
        inputPos = 0;
        terminal.leaveInput(true);
        break;
      default:
        if (ch >= '0' && ch <= '9') {
          if (maxLength > inputPos) {
            inputBuffer[inputPos++] = (byte) ch;
            terminal.addInput(ch);
          }
        }
    }
	}

}
