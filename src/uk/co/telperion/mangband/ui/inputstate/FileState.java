/*
 * FileState.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.ui.inputstate;

import java.awt.event.KeyEvent;

import uk.co.telperion.mangband.input.MangClientImpl;
import uk.co.telperion.mangband.ui.InputMode;

public class FileState implements InputState {

	private final MangClientImpl client;
	private final InputStateUser state_user;

	public FileState(MangClientImpl client, InputStateUser state_user) {
		this.client = client;
		this.state_user = state_user;
	}

	@Override
	public void handleEvent(KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
    	state_user.setState(InputStateFactory.createState(InputMode.Default, state_user, client, null, null, 0, '\0'));
      client.setFileDisp();
      return;
    }

    switch (event.getKeyChar()) {
      case ' ':
        client.nextPage();
        break;
      case 'b':
        client.prevPage();
        break;
      case 'j':
        client.scrollDown();
        break;
      case 'k':
        client.scrollUp();
        break;
    }
	}

}
