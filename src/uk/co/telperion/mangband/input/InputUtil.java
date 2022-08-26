/*
 * InputUtil.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.input;

import uk.co.telperion.mangband.ui.MangbandTerm;

/**
 * Input utility
 *
 * @author evileye
 */
public class InputUtil {

  private String rvalString;
  private int rvalInt;
  private final CallbackUser client;
  private final MangbandTerm term;

  public InputUtil(CallbackUser client, MangbandTerm term) {
    this.client = client;
    this.term = term;
  }

  /**
   * Read a string of up to (maxLen) characters
   *
   * @param x      horizontal position of cursor
   * @param y      vertical position of cursor
   * @param maxLen maximum number of characters
   * @param escape
   */
  public String getString(int x, int y, int maxLen, boolean escape) {

    class InStringCallback extends StringCallback {

      boolean escape;
      int maxLen;

      public InStringCallback(CallbackUser client, int maxLen, boolean escape) {
        super(client);
        this.escape = escape;
        this.maxLen = maxLen;
      }

      @Override
			public void update(String input) {
        rvalString = input;
        term.cancelInputMode();
        rvalInt = 1;
      }

      @Override
			public void cancel() {
        term.cancelInputMode();
        rvalInt = 0;
      }
    }

    rvalInt = -1;
    rvalString = null;

    InStringCallback callback = new InStringCallback(client, maxLen, escape);

    term.inputString(x, y, maxLen, callback);
    while (rvalInt == -1) {
      try {
        term.paintBuffer();
        Thread.sleep(50);
      } catch (InterruptedException e) {
      }
    }

    return rvalString;
  }
}
