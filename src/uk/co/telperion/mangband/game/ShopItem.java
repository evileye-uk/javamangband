/*
 * ShopItem.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.game;

/**
 * Simple class to describe an item as listed in shop inventory.
 *
 * @author evileye
 */
public class ShopItem extends Item {

  private final int price;

	/**
   * Constructor: ShopItem; once constructed this
   * object is immutable
   *
   * @param attr   Colour attribute for item
   * @param weight Weight of object
   * @param amount Number of available objects
   * @param price  Price per item (Au)
   * @param name   Descriptive name
   */
  public ShopItem(byte attr, short weight, int amount, int price, String name) {
    super(name, attr, amount, weight);
    this.price = price;
  }

  /**
   * @return offer price of item
   */
  public int getPrice() {
    return price;
  }
}
