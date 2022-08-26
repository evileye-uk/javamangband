/*
 * DirectionState.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.ui.inputstate;

import java.awt.event.KeyEvent;

import uk.co.telperion.mangband.input.DirectionCallback;
import uk.co.telperion.mangband.input.InputCallback;

public class DirectionState implements InputState {

	private final InputCallback inputCallback;

	public DirectionState(InputCallback callback) {
		this.inputCallback = callback;
	}

	@Override
	public void handleEvent(KeyEvent event) {
	  int code = event.getKeyCode();
    DirectionCallback cb = (DirectionCallback) inputCallback;

    if (code == KeyEvent.VK_ESCAPE) {
      cb.cancel();
      return;
    }

    char ch = event.getKeyChar();
    char dir;

    switch (ch) {
      case 'h':
        dir = 4;
        break;
      case 'j':
        dir = 2;
        break;
      case 'k':
        dir = 8;
        break;
      case 'l':
        dir = 6;
        break;
      case 'b':
        dir = 1;
        break;
      case 'n':
        dir = 3;
        break;
      case 'y':
        dir = 7;
        break;
      case 'u':
        dir = 9;
        break;
      case 'p':
        dir = 128;
        break;
      case '*':
      case 't':
        dir = 5;
        break;
      default:
        return;
    }
    cb.update(dir);
	}

}
