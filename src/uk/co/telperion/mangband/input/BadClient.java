package uk.co.telperion.mangband.input;

import uk.co.telperion.mangband.game.ShopItem;

public interface BadClient extends Runnable {
	boolean login();

	int getStatOrder(int i);

	void display_motd(int i, String string);

	void addShop(int pos, ShopItem shopItem);
	void addSpell(int book, int line, String spell);

	void setFood(short food);
	void setFps(int fps);
	void setShop(int num_items, int store_num, int owner_num);
	void setPlayerShop(short num_items, short store_num, String player_name);


	void ask_item();

	void addMessage(String message);

	void setHistory(short line, String hist_line);
    void getDirection();
}
