/*
 * MacroCommand.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.input;

/**
 * Abstract macro command processor
 *
 * @author evileye
 */
public abstract class MacroCommand {

  private String name;

  public String getName() {
    return name;
  }
}
