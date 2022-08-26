/*
 * Item.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.game;

/**
 * Item base class
 *
 * @author evileye
 */
public abstract class Item {
  private final byte attr;
  private final short weight;
  private final int amount;
  private final String name;

  public Item(String name, byte attr, int amount, short weight) {
    this.name = name;
    this.attr = attr;
    this.amount = amount;
    this.weight = weight;
  }

  /**
   * @return attribute of the item
   */
  public byte getAttr() {
    return attr;
  }

  /**
   * @return weight of the combined items
   */
  public short getWeight() {
    return weight;
  }

  /**
   * @return total number of stacked items
   */
  public int getAmount() {
    return amount;
  }

  /**
   * @return item description
   */
  public String getName() {
    return name;
  }
}
