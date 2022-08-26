/*
 * CharacterState.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */package uk.co.telperion.mangband.ui.inputstate;

import java.awt.event.KeyEvent;

import uk.co.telperion.mangband.input.CharCallback;
import uk.co.telperion.mangband.input.InputCallback;

public class CharacterState implements InputState {

	private final InputCallback inputCallback;

	public CharacterState(InputCallback callback) {
		this.inputCallback = callback;
	}

	@Override
	public void handleEvent(KeyEvent event) {
    CharCallback cb = (CharCallback) inputCallback;
    cb.update(event.getKeyChar());
	}
}
