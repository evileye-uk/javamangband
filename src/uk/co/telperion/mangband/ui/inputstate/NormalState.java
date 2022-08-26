/*
 * NormalState.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.ui.inputstate;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import uk.co.telperion.mangband.SpecialFile;
import uk.co.telperion.mangband.game.MagicType;
import uk.co.telperion.mangband.input.MangClientImpl;

public class NormalState implements InputState {

	private final MangClientImpl client;

	public NormalState(MangClientImpl client, InputStateUser state_user) {
		this.client = client;
	}

	@Override
	public void handleEvent(KeyEvent event) {
		char ch = event.getKeyChar();
		int mod = event.getModifiers();

		if (ch == KeyEvent.CHAR_UNDEFINED) {
			return;
		}

		if ((mod & InputEvent.CTRL_MASK) != 0) {
			ch += '@';
		}

		switch (ch) {
		case 'h':
		case 'H':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.do_tunnel(4);
			} else {
				client.do_move(4, (ch & 0x20) == 0);
			}
			break;
		case 'j':
		case 'J':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.do_tunnel(2);
			} else {
				client.do_move(2, (ch & 0x20) == 0);
			}

			break;
		case 'k':
		case 'K':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.do_tunnel(8);
			} else {
				client.do_move(8, (ch & 0x20) == 0);
			}

			break;
		case 'l':
		case 'L':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.do_tunnel(6);
			} else {
				client.do_move(6, (ch & 0x20) == 0);
			}

			break;
		case 'y':
		case 'Y':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.do_tunnel(7);
			} else {
				client.do_move(7, (ch & 0x20) == 0);
			}

			break;
		case 'b':
		case 'B':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.do_tunnel(1);
			} else {
				client.do_move(1, (ch & 0x20) == 0);
			}

			break;
		case 'n':
		case 'N':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.do_tunnel(3);
			} else {
				client.do_move(3, (ch & 0x20) == 0);
			}
			break;
		case 'u':
		case 'U':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.do_tunnel(9);
			} else {
				client.do_move(9, (ch & 0x20) == 0);
			}
			break;
		case 'M':
			client.do_map();
			break;
		case 'g':
			client.pickup();
			break;
		case '>':
			client.go_down();
			break;
		case '<':
			client.go_up();
			break;
		case ':':
			client.do_message();
			break;
		case 'i':
			client.display_pack(false, true);
			break;
		case 'e':
			client.display_pack(true, true);
			break;
		case 'w':
			client.init_wield();
			break;
		case 'T':
			client.init_remove();
			break;
		case 'r':
			client.init_read();
			break;
		case 'm':
			client.init_cast(MagicType.SPELL);
			break;
		case 'q':
			client.init_quaff();
			break;
		case 'E':
			client.init_eat();
			break;
		case 'F':
			client.init_fill();
			break;
		case 'd':
			client.init_drop();
			break;
		case 'P':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.displayHistory();
			} else {
				client.peruse();
			}
			break;
		case 'G':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.init_ghost();
			} else {
				client.init_gain();
			}
			break;
		case 'p':
			client.init_cast(MagicType.PRAYER);
			break;
		case 'z':
			client.aim();
			break;
		case 'Z':
			client.use();
			break;
		case 'D':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.init_destroy();
			} else {
				client.disarm();
			}
			break;
		case 'a':
			client.zap();
			break;
		case 'A':
			client.activate();
			break;
		case 's':
			client.search();
			break;
		case 'S':
			client.setSearch();
			break;
		case '@':
			client.displayFile(SpecialFile.PLAYER, 0);
			break;
		case '?':
			client.displayFile(SpecialFile.HELP, 0);
			break;
		case '~':
			client.displayFile(SpecialFile.ARTIFACT, 0);
			break;
		case '#':
			client.displayFile(SpecialFile.SCORES, 0);
			break;
		case '|':
			client.displayFile(SpecialFile.UNIQUE, 0);
			break;
		case 'C':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.suicide();
			} else {
				client.display_character_sheet();
			}
			break;
		case 'c':
			client.close_door();
			break;
		case 'o':
			client.open_door();
			break;
		case 'R':
			if ((mod & InputEvent.CTRL_MASK) == 0) {
				client.rest();
			}

			break;
		case '}':
			client.uninscribe();
			break;
		case '{':
			client.inscribe();
			break;
		case 'X':
			if ((mod & InputEvent.CTRL_MASK) != 0) {
				client.quit();
			}
			break;
		case '$':
			client.drop_gold();
			break;
		case 'v':
			client.throwItem();
			break;
		case 't':
			client.fireItem();
			break;
		case '+':
			client.partyCommand();
			break;
		case 'W':
			client.locate();
			break;
		case 'x':
			client.look();
			break;
		}
	}

}
