/*
 * BooleanState.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.ui.inputstate;

import java.awt.event.KeyEvent;

import uk.co.telperion.mangband.input.BooleanCallback;
import uk.co.telperion.mangband.input.InputCallback;

public class BooleanState implements InputState {

	private final InputCallback inputCallback;

	public BooleanState(InputCallback callback) {
		this.inputCallback = callback;
	}

	@Override
	public void handleEvent(KeyEvent event) {
    BooleanCallback cb = (BooleanCallback) inputCallback;

    if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
      cb.cancel();
      return;
    }
    switch (event.getKeyChar()) {
      case 'y':
      case 'Y':
        cb.update(true);
        break;
      case 'n':
      case 'N':
        cb.update(false);
        break;
    }
	}

}
