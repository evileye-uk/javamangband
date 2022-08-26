/*
 * InputTerminal.java
 *
 * (c) Copyright 2015-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband;

public interface InputTerminal {

  void leaveInput(boolean clear);

  void backSpace();

  void addInput(char ch);
}
