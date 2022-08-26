package uk.co.telperion.mangband.input;

import java.util.function.Predicate;

import uk.co.telperion.mangband.game.InventoryItem;
import uk.co.telperion.mangband.ui.MangbandTerm;

public abstract class FilteredItemCallback<T> extends ItemCallback {

  protected final Predicate<InventoryItem> predicate;
  protected final T action;

  public FilteredItemCallback(CallbackUser client, MangbandTerm mainTerm, Predicate<InventoryItem> predicate, T action) {
    this(client, mainTerm, predicate, action, false);
  }

  public FilteredItemCallback(CallbackUser client, MangbandTerm mainTerm, Predicate<InventoryItem> predicate, T action, boolean equip) {
    super(client, mainTerm, equip);
    this.predicate = predicate;
    this.action = action;
  }

  /**
   * Item update function
   *
   * @param index index of a valid selected item
   */
  abstract public void update_item(int index);
}
