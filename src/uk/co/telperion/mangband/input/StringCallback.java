/*
 * StringCallback.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.input;

/**
 * Callback for string requests
 *
 * @author evileye
 */
public abstract class StringCallback extends InputCallback {

  public StringCallback(CallbackUser client) {
    super(client);
  }

  public abstract void update(String str);

  public abstract void cancel();
}

