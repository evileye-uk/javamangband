/*
 * MangClient.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.input;

import uk.co.telperion.mangband.game.ShopItem;

public interface MangClient extends Runnable {

	boolean login();

	void req_info();

	int getStatOrder(int i);

	void display_motd(int i, String string);

	void addShop(int pos, ShopItem shopItem);
	void addSpell(int book, int line, String spell);

	void playSound(byte soundId);

	void setEquip(byte pos, byte attr, short weight, byte tval, String name);
	void setInventory(byte pos, byte attr, short weight, short amount, byte tval, String name);
	void setStudy(byte study);
	void setSpeed(short speed);
	void setState(short paralysed, short searching, short resting);
	void setPoison(byte poison);
	void setFear(byte fear);
	void setConfused(byte confused);
	void setBlind(byte blind);
	void setStun(short stun);
	void setCut(short cut);
	void getDirection();
	void setCursor(byte x, byte y, boolean b);
	void setPause();
	void setFloor(byte floor_tval);
	void setSP(short max, short cur);
	void setHP(short max, short cur);
	void setStat(byte stat, short max, short cur);
	void setGold(int gold);
	void setExperience(short level, int max, int cur, int adv);
	void setTitle(String title);
	void setCharInfo(short race, short char_class, short sex);
	void setLine(short y, byte[] attrs, byte[] chars, boolean map);
	void setMaxStat(byte stat, short max);
	void setAC(short base, short plus);
	void setCharStats(short height, short weight, short age, short social_class);
	void setMods(short mod_hit, short mod_dam);
	void setChar(byte c, byte a, byte x, byte y);
	void setParty(String string);
	void setTarget(String buf, int x, int y);
	void setDepth(short depth);
	void setFood(short food);
	void setFps(int fps);
	void setShop(int num_items, int store_num, int owner_num);
	void setPlayerShop(short num_items, short store_num, String player_name);

	void confirm_sell(int price);

	void display_file(int line, int max, String string, byte attr);

	void ask_item();

	void addMessage(String message);

	void setHistory(short line, String hist_line);
	
	CharacterModel getModel();
}