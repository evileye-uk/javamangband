/*
 * BooleanCallback.java
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
public abstract class BooleanCallback extends InputCallback {

  public BooleanCallback(CallbackUser client) {
    super(client);
  }

  /**
   * Update function
   *
   * @param value boolean value to be reported back
   */
  public abstract void update(boolean value);

  /**
   * Cancel function
   */
  public abstract void cancel();
}
