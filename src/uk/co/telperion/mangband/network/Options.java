/*
 * Options.java 
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.network;

/**
 * Options class
 *
 * @author evileye
 */
public class Options {
  final String[] description =
    {
      "Rogue like commands",
      "Activate quick messages",
      "Prompt before picking things up",
      "Use old target by default",
      "Pick things up by default",
      "Show dungeon level in feet",
      "Merge inscriptions when stacking",
      "Merge discounts when stacking",
      "Show weights in object listings",
      "Audible bell (on errors, etc.)",
      "Use colour if possible",
      "Run past stairs",
      "Run through open doors",
      "Run past known corners",
      "Run into potential corners",
      "Disturb whenever any monster moves",
      "Disturb whenever viewable monster moves",
      "Disturb whenever map panel changes",
      "Disturb whenever player state changes",
      "Disturb whenever boring things happen",
      "Disturb whenever various things happen",
      "Alert user to critical hitpoints",
      "Alert user to various failures",
      null, null, null, null,

      "Death is permanent",
      "Auto-scum for good levels",
      "Allow weapons and armour to stack",
      "Allow wands/staffs/rods to stack",
      "Expand the poser of the look command"
    };

  final boolean[] defaults =
    {
    /*  user interface */
      false, false, false, false, false, false, false, false,
      false, false, false, false, false, false, false, false,

    /* disturbance */
      false, false, false, false, false, false, false, false,
      false, false, false, false,

    /* game play options */
      false, true, false, false,
      false, false, false, false, false, false, false, false,
      false, false, false, false, false, false, false, false,

      false, false, false, false, false, false, false, true,
      true, true, true, true, true, true, true, true
    };

}

