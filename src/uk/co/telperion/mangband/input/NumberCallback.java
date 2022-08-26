/*
 * NumberCallback.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.input;

/**
 * Provide a callback for numeric requests. This class awaits a numerical
 * user input and returns that value through the update function.
 *
 * @author evileye
 */
public abstract class NumberCallback extends InputCallback {

  public NumberCallback(CallbackUser client) {
    super(client);
  }

  /**
   * Update function; pass in a number
   *
   * @param number value
   */
  public abstract void update(int number);

  /**
   * Cancel function
   */
  public abstract void cancel();
}

