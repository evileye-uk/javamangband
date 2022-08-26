/*
 * NonRogueState.java
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

public class NonRogueState implements InputState {

	private final MangClientImpl client;
  private boolean runModifier = false;
  private boolean tunnelModifier = false;

	public NonRogueState(MangClientImpl client, InputStateUser state_user) {
		this.client = client;
	}

	@Override
	public void handleEvent(KeyEvent event) {
		// TODO Auto-generated method stub
	   char ch = event.getKeyChar();
	    int mod = event.getModifiers();

	    if (ch == KeyEvent.CHAR_UNDEFINED) {
	      return;
	    }

	    if ((mod & InputEvent.CTRL_MASK) != 0) {
	      ch += '@';
	    }

	    if (ch == '.') {
	      runModifier = true;
	      return;
	    }

	    if (ch == 'T') {
	      tunnelModifier = true;
	      return;
	    }

	    switch (ch) {
	      case '4':
	        if (tunnelModifier) {
	          client.do_tunnel(4);
	        } else {
	          client.do_move(4, runModifier);
	        }
	        break;
	      case '2':
	        if (tunnelModifier) {
	          client.do_tunnel(4);
	        } else {
	          client.do_move(2, runModifier);
	        }
	        break;
	      case '8':
	        if (tunnelModifier) {
	          client.do_tunnel(4);
	        } else {
	          client.do_move(8, runModifier);
	        }
	        break;
	      case '6':
	        if (tunnelModifier) {
	          client.do_tunnel(4);
	        } else {
	          client.do_move(6, runModifier);
	        }
	        break;
	      case '7':
	        if (tunnelModifier) {
	          client.do_tunnel(4);
	        } else {
	          client.do_move(7, runModifier);
	        }
	        break;
	      case '1':
	        if (tunnelModifier) {
	          client.do_tunnel(4);
	        } else {
	          client.do_move(1, runModifier);
	        }
	        break;
	      case '3':
	        if (tunnelModifier) {
	          client.do_tunnel(4);
	        } else {
	          client.do_move(3, runModifier);
	        }
	        break;
	      case '9':
	        if (tunnelModifier) {
	          client.do_tunnel(4);
	        } else {
	          client.do_move(9, runModifier);
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
	      case 't':
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
	        }
	        break;
	      case 'b':
	        client.peruse();
	        break;
	      case 'G':
	        client.init_gain();
	        break;
	      case 'p':
	        client.init_cast(MagicType.PRAYER);
	        break;
	      case 'u':
	        client.use();
	        break;
	      case 'D':
	        client.disarm();
	        break;
	      case 'k':
	        client.init_destroy();
	        break;
	      case 'a':
	        client.aim();
	        break;
	      case 'z':
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
	        client.display_character_sheet();
	        break;
	      case 'Q':
	        client.suicide();
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
	      case '$':
	        client.drop_gold();
	        break;
	      case 'v':
	        client.throwItem();
	        break;
	      case 'f':
	        client.fireItem();
	        break;
	      case '+':
	        client.partyCommand();
	        break;
	      case 'L':
	        client.locate();
	        break;
	      case 'l':
	        client.look();
	        break;
	      case 'X':
	        if ((mod & InputEvent.CTRL_MASK) != 0) {
	          client.quit();
	        }
	        break;
	    }
	    runModifier = false;
	    tunnelModifier = false;
	}

}
