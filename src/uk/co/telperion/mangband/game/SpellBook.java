/*
 * SpellBook.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.game;

import java.util.Iterator;

/**
 * Storage for a single book of spells.
 *
 * @author evileye
 */
public class SpellBook implements Iterable<String> {

  private final String[] spells;
  private static final int MAX_SPELL = 9;
  private int num_spells;

  public SpellBook() {
    spells = new String[MAX_SPELL];
    num_spells = 0;
  }

  /**
   * Add spell description at a particular line
   *
   * @param line  The spell index to insert
   * @param spell The spell description to be added
   */
  public void addSpell(int line, String spell) {
    spells[line] = spell.trim();
    num_spells = line + 1;
  }

  /**
   * Return the spell at the requested index
   *
   * @param line The spell index to retrieve
   * @return description of the spell
   */
  private String getSpell(int line) {
    return spells[line];
  }

  /**
   * Very basic iterator for spell book names.
   *
   * @return a new spell book iterator instance
   */
  @Override
	public Iterator<String> iterator() {
    class SpellBookIterator implements Iterator<String> {

      private SpellBook spellBook;
      int pos;

      SpellBookIterator(SpellBook book) {
        spellBook = book;
        pos = 0;
      }

      @Override
			public boolean hasNext() {
        return (pos < num_spells && spellBook.spells[pos] != null);
      }

      @Override
			public String next() {
        return spellBook.spells[pos++];
      }

      @Override
			public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
      }
    }
    return new SpellBookIterator(this);
  }
}
