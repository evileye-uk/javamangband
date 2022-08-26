/*
 * CallbackUser.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.input;

public interface CallbackUser {
  void display_pack(boolean equipment, boolean awaitEscape);
  void clearMessage();
  void send_target(int value);
  void resetTarget();
  void show_book(int index);
}