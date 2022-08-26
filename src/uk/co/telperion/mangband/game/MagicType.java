/*
 * MagicType.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.game;

public enum MagicType {
  SPELL {
    @Override
		public byte bookType() {
      return ItemType.MAGIC_BOOK;
    }

    @Override
    public String verb() {
      return "Cast";
    }

    @Override
    public String noun() {
      return "spell";
    }
  },
  PRAYER {
    @Override
		public byte bookType() {
      return ItemType.PRAYER_BOOK;
    }

    @Override
    public String verb() {
      return "Pray";
    }

    @Override
    public String noun() {
      return "prayer";
    }
  },
  GHOST {
    @Override
    public String verb() {
      return "Use";
    }

    @Override
    public String noun() {
      return "power";
    }
    
    public String bookSelectionString() {
    	return "";
    }
  };

  public byte bookType() {
    return 0;
  }

  protected abstract String verb();

  protected abstract String noun();

  public String selectionString() {
    return verb() + " which " + noun() + '?';
  }

  public String bookSelectionString() {
    return verb() + " from which book?";
  }
}
