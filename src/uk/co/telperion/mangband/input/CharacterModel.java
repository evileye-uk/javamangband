/*
 * CharacterModel.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.input;

import java.util.ArrayList;
import java.util.function.Predicate;

import uk.co.telperion.mangband.game.InventoryItem;

public class CharacterModel {
  public final InventoryItem[] inventory;
  public final InventoryItem[] equip;

  public int gold;
  public boolean ghost;
	public String nick;

  public class Stat {
    public short current;
    public short maximum;
    public short limit;
    int order;
  }

  public final Stat[] stats;

  public short baseAC, plusAC;
  public short curHP, maxHP;
  public short curSP, maxSP;
  public int race;
  public int char_class;
  public int sex;
  public short level;
  public int maxExp;
  public int curExp;
  public int advExp;
  public String title;
  public short toHit;
  public short toDam;
	public short food;

  public short height;
  public short weight;
  public short age;
  public short social_class;

  /* skills */
  public short skill_fighting;
  public short skill_missile;
  public short skill_saving;
  public short skill_stealth;
  public short skill_perception;
  public short skill_searching;
  public short skill_disarming;
  public short skill_magic_dev;
  public short num_blows;
  public short num_shots;
  public short see_infra;

  public short speed;
  public byte study;
  public short paralysed;
  public short searching;
  public short resting;
  public byte confused;
  public byte poison;
  public byte fear;
  public short cut;
  public short stun;
  public byte blind;
  public String party;
  public short depth;
	public ArrayList<String> charHistory;

	public static final int MAX_STAT = 6;
  private static final int MAX_INVEN = 24;
  private static final int MAX_EQUIP = 12;

  public CharacterModel()
  {
		stats = new Stat[MAX_STAT];

		for (int i = 0; i < stats.length; i++) {
			stats[i] = new CharacterModel.Stat();
		}

    inventory = new InventoryItem[MAX_INVEN];
    equip = new InventoryItem[MAX_EQUIP];
  }

	public void setFighting(short value) {
		skill_fighting = value;
	}

	public void setMissile(short value) {
		skill_missile = value;
	}

	public void setSaving(short value) {
		skill_saving = value;
	}

	public void setStealth(short value) {
		skill_stealth = value;
	}

	public void setPerception(short value) {
		skill_perception = value;
	}

	public void setSearching(short value) {
		skill_searching = value;
	}

	/**
	 * Set disarming skill value.
	 * 
	 * @param value
	 *          the new disarming value to set
	 */
	public void setDisarming(short value) {
		skill_disarming = value;
	}

	/**
	 * Set the player's magic device level.
	 * 
	 * @param value
	 *          the new value to be set
	 */
	public void setMagicDevice(short value) {
		skill_magic_dev = value;
	}

	public void setBlows(short value) {
		num_blows = value;
	}

	public void setShots(short value) {
		num_shots = value;
	}

	public void setInfravision(short value) {
		see_infra = value;
	}

  /**
   * Return the inventory item referenced by index. This should be a positive
   * value less than MAX_INVEN or null will be returned.
   *
   * @param index
   *          index of the inventory item
   * @return the inventory item referenced by index
   */
  public InventoryItem inventoryItem(int index) {
    if (index < 0 || index >= MAX_INVEN)
      return null;
    return inventory[index];
  }

  /**
   * Return the equipment item referenced by index. This should be a positive
   * value less than MAX_EQUIP or null will be returned.
   *
   * @param index
   *          index of the equipment item
   * @return equipment item referenced by index
   */
  public InventoryItem equipmentItem(int index) {
    if (index < 0 || index >= MAX_EQUIP)
      return null;
    return equip[index];
  }

  /**
   * Count and return the number of inventory items matching a particular predicate.
   * 
   * @param predicate
   *          predicate function 
   * @return number of items matching the predicate
   */
  public int countInventory(Predicate<InventoryItem> predicate) {
    int count = 0;
    for (int i = 0; i < MAX_INVEN; i++) {
      if (predicate.test(inventory[i])) {
        count++;
      }
    }
    return count;
  }
}
