/*
 * ShoppingState.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.ui.inputstate;

import java.awt.event.KeyEvent;

import uk.co.telperion.mangband.input.MangClientImpl;
import uk.co.telperion.mangband.ui.InputMode;

public class ShoppingState implements InputState {

	private final MangClientImpl client;
	private final InputStateUser state_user;

	public ShoppingState(MangClientImpl client, InputStateUser state_user) {
		this.client = client;
		this.state_user = state_user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.co.telperion.mangband.ui.InputState#handleEvent(java.awt.event.KeyEvent)
	 */
	@Override
	public void handleEvent(KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.state_user.setState(InputStateFactory.createState(InputMode.Default, state_user, client, null, null, 0, '\0'));
			
			client.endShopping();
			return;
		}

		switch (event.getKeyChar()) {
		case 'p':
			client.purchase();
			break;
		case 's':
			client.init_sell();
			break;
		case ' ':
			client.nextShopPage();
			break;
		}
	}
}
