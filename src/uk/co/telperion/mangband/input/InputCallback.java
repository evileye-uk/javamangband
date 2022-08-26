/*
 * InputCallback.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.input;

/**
 * Abstract callback class for all input requests
 *
 * @author evileye
 */
public abstract class InputCallback {

  final CallbackUser client;

  /**
   * Constructor
   *
   * @param client instance of client to use
   */
  public InputCallback(CallbackUser client) {
    this.client = client;
  }
}
