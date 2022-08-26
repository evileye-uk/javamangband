/*
 * ItemCallback.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.input;

import uk.co.telperion.mangband.ui.MangbandTerm;

/**
 * Callback for item action requests
 *
 * @author evileye
 */
public abstract class ItemCallback extends CharCallback {

  private final MangbandTerm term;
  private boolean equip = false;

  protected boolean inven_shown = false;

  public ItemCallback(CallbackUser client, MangbandTerm mainTerm) {
    this(client, mainTerm, false);
  }

  protected ItemCallback(CallbackUser client, MangbandTerm mainTerm, boolean equip) {
    super(client);
    term = mainTerm;
    this.equip = equip;
  }

  /**
   * Update function
   *
   * @param ch character passed in from user input
   */
  @Override
	public void update(char ch) {
    if (ch >= 'a' && ch <= 'w') {
      int index = ch - 'a';

      update_item(index);
    }
    if (ch == '*' && !inven_shown) {
      inven_shown = true;
      client.display_pack(equip, false);
    } else if (ch == MangbandTerm.ESCAPE) {
      cancel();
    }
  }

  /**
   * Item update function
   *
   * @param index index of a valid selected item
   */
  abstract public void update_item(int index);

  public void cancel() {
    term.cancelInputMode();
    if (inven_shown) {
      term.restoreTerm();
    }
    client.clearMessage();
  }
}
