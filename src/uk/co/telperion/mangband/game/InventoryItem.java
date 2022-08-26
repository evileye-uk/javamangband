/*
 * InventoryItem.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.game;

/**
 * Simple class to contain inventory items.
 *
 * @author evileye
 */
public class InventoryItem extends Item {

  private final byte tval;

  public InventoryItem(byte attr, short weight, int amount, byte tval, String
    name) {
    super(name, attr, amount, weight);
    this.tval = tval;
  }

  public boolean isValid() {
	  return tval != ItemType.INVALID;
  }
  
  /**
   * @return true if indexed item is wieldable
   */
  public boolean isWieldable() {
    return tval >= ItemType.BOW && tval <= ItemType.RING;
  }

  /**
   * @return true if item is a wand
   */
  public boolean isWand() {
    return (tval == ItemType.WAND);
  }

  /**
   * @param magicType
   * @return true if indexed item is castable
   */
  public boolean isCastable(MagicType magicType) {
    return (tval == magicType.bookType());
  }

  /**
   * @return true if item is a staff
   */
  public boolean isStaff() {
    return (tval == ItemType.STAFF);
  }

  /**
   * @return true if item is a rod
   */
  public boolean isRod() {
    return (tval == ItemType.ROD);
  }

  /**
   * @return true if item is a potion
   */
  public boolean isQuaffable() {
    return (tval == ItemType.POTION);
  }

  /**
   * @return true if item is an item of food
   */
  public boolean isEatable() {
    return (tval == ItemType.FOOD);
  }

  /**
   * @return true if item can be used as fuel
   */
  public boolean isFuelable() {
    return (tval == ItemType.LITE || tval == ItemType.FLASK);
  }

  /**
   * @return true if item is a scroll
   */
  public boolean isScroll() {
    return (tval == ItemType.SCROLL);
  }
}
