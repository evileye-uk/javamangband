/*
 * CharCallback.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.input;

/**
 * Callback for single character requests
 *
 * @author evileye
 */
public abstract class CharCallback extends InputCallback {

  public CharCallback(CallbackUser client) {
    super(client);
  }

  /**
   * Update function - report back
   *
   * @param ch character to be reported
   */
  public abstract void update(char ch);
}
