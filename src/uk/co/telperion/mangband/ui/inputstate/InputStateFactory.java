/*
 * InputStateFactory.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.ui.inputstate;

import uk.co.telperion.mangband.input.InputCallback;
import uk.co.telperion.mangband.input.MangClientImpl;
import uk.co.telperion.mangband.ui.InputMode;

public class InputStateFactory {

	public static InputState createState(InputMode mode, InputStateUser state_user, MangClientImpl client,
			InputCallback callback, InputTerminal terminal, int maxLen, char passChar) {

		switch (mode) {

		case NonRogue:
			return new NonRogueState(client, state_user);

		case Ignore:
			return new IgnoreState(state_user);

		case Shopping:
			return new ShoppingState(client, state_user);

		case Direction:
			return new DirectionState(callback);

		case String:
			return new StringState(callback, state_user, client, terminal, maxLen, passChar);
			
		case Character:
			return new CharacterState(callback);
			
		case Numeric:
			return new NumericState(callback, terminal, maxLen);
			
		case Boolean:
			return new BooleanState(callback);
			
		case File:
			return new FileState(client, state_user);
			
		case Macro:
			return new MacroState();

		case Default:
		case Normal:
		default:
			return new NormalState(client, state_user);

		}
	}
}
