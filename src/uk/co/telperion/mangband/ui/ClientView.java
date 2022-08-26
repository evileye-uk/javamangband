/*
 * ClientView.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.ui;

import uk.co.telperion.mangband.TermColours;
import uk.co.telperion.mangband.game.InventoryItem;
import uk.co.telperion.mangband.game.ShopItem;
import uk.co.telperion.mangband.game.SpellBook;
import uk.co.telperion.mangband.input.*;

import java.util.Date;
import java.util.function.IntConsumer;
import java.util.function.Predicate;

public class ClientView {

	private final MangbandTerm term;
	private final CharacterModel model;

	public final String[] raceTitle = { "Human", "Half-elf", "Elf", "Hobbit", "Gnome", "Dwarf", "Half-Orc", "Half-Troll",
			"Dunadan", "High-elf" };

	public final String[] classTitle = { "Warrior", "Mage", "Priest", "Rogue", "Ranger", "Paladin" };

	public final String[] sexTitle = { "Female", "Male" };

	private final String[] rating = { "Very Bad", "Bad", "Poor", "Fair", "Good", "Very Good", "Excellent", "Superb", "Heroic",
			"Legendary" };

	public ClientView(MangbandTerm term, CharacterModel model) {
		this.term = term;
		this.model = model;
	}

	private FilteredItemCallback<IntConsumer> createItemIndexCallback(Predicate<InventoryItem> predicate, IntConsumer action, CallbackUser user) {
		return new FilteredItemCallback<IntConsumer>(user, term, predicate, action, false) {
			@Override
			public void update_item(int index) {
				if(!predicate.test(model.inventoryItem(index)))
					return;

				action.accept(index);

				if (inven_shown) {
					restoreTerm();
				}
				cancelInputMode();
			}
		};
	}

	public void chooseItem(Predicate<InventoryItem> predicate, String prompt, CallbackUser user, IntConsumer action)
	{
		FilteredItemCallback<IntConsumer> callback = createItemIndexCallback(predicate, action, user);
		requestItem(prompt, callback);
	}

	/**
	 * Draw details of armour class on the terminal
	 */
	public void drawAC() {
		term.drawString(String.format("AC: %7d", (model.baseAC + model.plusAC)), TermColours.GREEN, 0, 15, true);
	}

	/**
	 * Draw gold amount to the terminal
	 */
	public void drawGold() {
		term.drawString(String.format("AU: %7d", model.gold), TermColours.YELLOW, 0, 6, true);
	}

	public void drawStat(int stat) {
		final String statName[] = { "Str:", "Int:", "Wis:", "Dex:", "Con:", "Chr:" };

		int statVal = model.stats[stat].current;
		String statOut;
		if (statVal > 18) {
			statOut = "18/" + (statVal - 18);
		} else {
			statOut = Integer.toString(statVal);
		}

		int colour;
		if (model.stats[stat].current != model.stats[stat].maximum) {
			colour = TermColours.YELLOW;
		} else if (model.stats[stat].limit == 18 + 100) {
			colour = TermColours.LIGHT_UMBER;
		} else {
			colour = TermColours.GREEN;
		}

		term.drawString(String.format("%s %6s", statName[stat], statOut), colour, 0, 8 + stat, true);
	}

	/**
	 * Draw character race and class
	 */
	public void drawChar() {
		term.drawString(String.format("%-10s", raceTitle[model.race]), TermColours.LIGHT_BLUE, 0, 1, true);
		term.drawString(String.format("%-10s", classTitle[model.char_class]), TermColours.LIGHT_BLUE, 0, 2, true);
	}

	/**
	 * Draw current title to terminal
	 */
	public void drawTitle() {
		if (model.title != null) {
			term.drawString(String.format("%-10s", model.title), TermColours.LIGHT_BLUE, 0, 3, true);
		}
	}

	/**
	 * Draw current level and experience to terminal
	 */
	public void drawExperience() {
		term.drawString(String.format("Level: %4d", model.level), TermColours.LIGHT_GREEN, 0, 4, true);
		term.drawString(String.format("Exp: %6d", model.curExp), TermColours.LIGHT_GREEN, 0, 5, true);
	}

	public void drawSP() {
		term.drawString(String.format("MSP: %6d", model.maxSP), TermColours.GREEN, 0, 18, true);
		term.drawString(String.format("CSP: %6d", model.curSP),
				(model.curSP == model.maxSP) ? TermColours.GREEN : TermColours.YELLOW, 0, 19, true);
	}

	public void drawHP() {
		term.drawString(String.format("Max HP:%4d", model.maxHP), TermColours.GREEN, 0, 16, true);
		term.drawString(String.format("Cur HP:%4d", model.curHP),
				(model.curHP == model.maxHP) ? TermColours.GREEN : TermColours.YELLOW, 0, 17, true);
	}

	public void drawDepth() {
		String depthString = model.depth == 0 ? "Town" : model.depth * 50 + "ft   ";

		term.drawString(depthString, TermColours.WHITE, 70, 23, true);
	}

	public void drawSpeed() {
		if (model.speed != 0) {
			term.drawString(String.format("Speed %+2d", model.speed), TermColours.YELLOW, 40, 24, true);
		} else {
			term.drawString("        ", TermColours.YELLOW, 40, 24, true);
		}
	}

	private void drawStatus(boolean value, int colour, int y, int x, String string) {

		if (!value) {
			string = new String(new char[string.length()]).replace('\0', ' ');
		}
		term.drawString(string, colour, x, y, true);
	}

	public void drawStudy() {
		drawStatus(model.study != 0, TermColours.WHITE, 24, 52, "Study");
	}

	public void drawParalysis() {
		drawStatus(model.paralysed != 0, TermColours.RED, 23, 52, "Paralysed");
	}

	public void drawSearching() {
		drawStatus(model.searching != 0, TermColours.RED, 23, 42, "Searching");
	}

	public void drawResting() {
		drawStatus(model.resting != 0, TermColours.WHITE, 23, 32, "Resting");
	}

	public void drawConfusion() {
		drawStatus(model.confused != 0, TermColours.RED, 23, 22, "Confused");
	}

	public void drawPoison() {
		drawStatus(model.poison != 0, TermColours.RED, 23, 12, "Poisoned");
	}

	public void drawFear() {
		drawStatus(model.fear != 0, TermColours.RED, 23, 52, "Afraid");
	}

	public void drawCut() {
		final String[] text = { "Mortal wound", "Deep gash   ", "Severe cut  ", "Nasty cut   ", "Bad cut     ",
				"Light cut   ", "Graze       ", "            " };

		final int[] values = { 1000, 200, 100, 50, 25, 10, 0, -1 };

		final int[] colours = { TermColours.LIGHT_RED, TermColours.RED, TermColours.RED, TermColours.ORANGE,
				TermColours.ORANGE, TermColours.YELLOW, TermColours.YELLOW, TermColours.YELLOW };

		drawMultiStat(model.cut, 52, 23, text, values, colours);
	}

	public void drawStun() {
		final String[] text = { "Knocked out", "Heavy stun ", "Stun       ", "           " };

		final int[] values = { 100, 50, 0, -1 };

		final int[] colours = { TermColours.RED, TermColours.ORANGE, TermColours.YELLOW, TermColours.YELLOW };

		drawMultiStat(model.stun, 0, 23, text, values, colours);
	}

	public void drawBlind() {
		drawStatus(model.blind != 0, TermColours.SLATE, 24, 7, "Blind");
	}

	public void drawFood() {
		final String[] text = { "Gorged", "Full  ", "      ", "Hungry", "Weak  ", "Weak  ", "Weak  " };

		final int[] values = { 15000, 10000, 2000, 1000, 500, 100, 0 };

		final int[] colours = { TermColours.LIGHT_GREEN, TermColours.GREEN, TermColours.YELLOW, TermColours.YELLOW,
				TermColours.ORANGE, TermColours.RED, TermColours.RED };

		drawMultiStat(model.food, 0, 24, text, values, colours);
	}

	private void drawMultiStat(int stat, int x, int y, String[] text, int[] values, int[] colours) {
		for (int i = 0; i < values.length; i++) {
			if (stat > values[i]) {
				term.drawString(text[i], colours[i], x, y, true);
				break;
			}
		}
	}

	/**
	 * Display character sheet
	 */
	public void display_character_sheet() {
		term.saveTerm();
		term.clear(false);

		term.drawString("Name: " + model.nick, TermColours.LIGHT_BLUE, 1, 2);
		term.drawString("Sex: " + sexTitle[model.sex], TermColours.LIGHT_BLUE, 1, 3);
		term.drawString("Race: " + raceTitle[model.race], TermColours.LIGHT_BLUE, 1, 4);
		term.drawString("Class: " + classTitle[model.char_class], TermColours.LIGHT_BLUE, 1, 5);

		term.drawString("+ To Hit " + model.toHit, TermColours.LIGHT_BLUE, 1, 9);
		term.drawString("+ To Dam " + model.toDam, TermColours.LIGHT_BLUE, 1, 10);
		term.drawString("+ To AC " + model.plusAC, TermColours.LIGHT_BLUE, 1, 11);
		term.drawString("+ Base AC " + model.baseAC, TermColours.LIGHT_BLUE, 1, 12);

		term.drawString("Level " + model.level, TermColours.LIGHT_GREEN, 28, 9);
		term.drawString("Experience " + model.curExp,
				model.curExp == model.maxExp ? TermColours.LIGHT_GREEN : TermColours.YELLOW, 28, 10);
		term.drawString("Max Exp " + model.maxExp, TermColours.LIGHT_GREEN, 28, 11);
		term.drawString("Exp to Adv." + model.advExp, TermColours.LIGHT_GREEN, 28, 12);

		term.drawString("Max Hit Points " + model.maxHP, TermColours.LIGHT_GREEN, 52, 9);
		term.drawString("Cur Hit Points " + model.curHP, (model.curHP == model.maxHP ? TermColours.LIGHT_GREEN
				: (model.curHP > model.maxHP / 10 ? TermColours.YELLOW : TermColours.RED)), 52, 10);
		term.drawString("Max SP (Mana) " + model.maxSP, TermColours.LIGHT_GREEN, 52, 11);
		term.drawString("Cur SP (Mana) " + model.curSP, (model.curSP == model.maxSP ? TermColours.LIGHT_GREEN
				: (model.curSP > model.maxSP / 10 ? TermColours.YELLOW : TermColours.RED)), 52, 12);

		term.drawString("Age: " + model.age, TermColours.WHITE, 32, 2);
		term.drawString("Height: " + model.height, TermColours.WHITE, 32, 3);
		term.drawString("Weight: " + model.weight, TermColours.WHITE, 32, 4);
		term.drawString("Social Class: " + model.social_class, TermColours.WHITE, 32, 5);

		term.drawString("Fighting: " + getRating(model.skill_fighting), TermColours.WHITE, 1, 16);
		term.drawString("Bows/Throw: " + getRating(model.skill_missile), TermColours.WHITE, 1, 17);
		term.drawString("Saving Throw: " + getRating(model.skill_saving), TermColours.WHITE, 1, 18);
		term.drawString("Stealth: " + getRating(model.skill_stealth), TermColours.WHITE, 1, 19);

		term.drawString("Perception: " + getRating(model.skill_perception), TermColours.WHITE, 28, 16);
		term.drawString("Searching: " + getRating(model.skill_searching), TermColours.WHITE, 28, 17);
		term.drawString("Disarming: " + getRating(model.skill_disarming), TermColours.WHITE, 28, 18);
		term.drawString("Magic Device: " + getRating(model.skill_magic_dev), TermColours.WHITE, 28, 19);

		term.drawString("Blows/Round: " + model.num_blows, TermColours.WHITE, 55, 16);
		term.drawString("Shots/Round: " + model.num_shots, TermColours.WHITE, 55, 17);
		term.drawString("Infra-Vision: " + model.see_infra, TermColours.WHITE, 55, 19);

		term.waitEscape();
	}

	private String getRating(int x) {
		if (x < 0)
			return rating[0];

		x /= 12;
		if (x <= 1)
			return rating[1];
		if (x <= 2)
			return rating[2];
		if (x <= 4)
			return rating[3];
		if (x <= 5)
			return rating[4];
		if (x <= 6)
			return rating[5];
		if (x <= 8)
			return rating[6];
		if (x <= 13)
			return rating[7];
		if (x <= 17)
			return rating[8];
		return rating[9];
	}

	/**
	 * Draw a message to the status line.
	 */
	public void putMessage(String message) {
		term.clearLine(0, false);
		term.drawString(message, TermColours.WHITE, 0, 0, false);
	}

	public void resetInputMode() {
		term.clearLine(0, false);
		term.cancelInputMode();
	}

	public void drawShopItemString(int i, ShopItem item, String weightString, String priceString) {
		term.drawString((char) ('a' + i) + ") ", TermColours.WHITE, 2, i + 2);
		term.drawString(item.getName(), item.getAttr(), 5, i + 2);
		term.drawString(weightString, TermColours.WHITE, 60 + 8 - weightString.length(), i + 2);
		term.drawString(priceString, TermColours.WHITE, 70 + 8 - priceString.length(), i + 2);
	}

	public void drawOverlayString(String string, int x, int y, int string_x, int colour) {
		term.clearLine(x, y, false);
		term.drawString(string, colour, string_x, y, false);
	}

	public void saveTerm() {
		term.saveTerm();
	}

	public void restoreTerm() {
		term.restoreTerm();
	}

	public void clearMsg() {
		term.clearLine(0, false);
	}

	public void clearPackLine(int y) {
		term.clearLine(14, y, false);
	}

	public void clearLine(int y, boolean base) {
		term.clearLine(y, base);
	}

	public void paintBuffer() {
		term.paintBuffer();
	}

	public void clear(boolean base) {
		term.clear(base);
	}

	public void inputFile() {
		term.inputFile();
	}

	public void inputChar(CharCallback callback) {
		term.inputChar(callback);
	}

	public void drawString(String string, int attr, int x, int y) {
		term.drawString(string, attr, x, y);
	}

	public void drawString(String string, int attr, int x, int y, boolean base) {
		term.drawString(string, attr, x, y, base);
	}

	public void cancelInputMode() {
		term.cancelInputMode();
	}

	public void inputString(int y, StringCallback callback) {
		term.inputString(0, y, term.getTermWidth(), callback);
	}

	public void drawChar(byte ch, byte attr, byte x, byte y, boolean base) {
		term.drawChar(ch, attr, x, y, base);
	}

	public void waitEscape() {
		term.waitEscape();
	}

	public void requestItem(String prompt, CharCallback itemCallback) {
		putMessage(prompt);
		inputChar(itemCallback);
	}

	public void inputDirection(DirectionCallback callback) {
		term.inputDirection(callback);
	}

	public void inputNum(int x, int maxLen, NumberCallback numberCallback) {
		term.inputNum(x, 0, maxLen, numberCallback);
	}

	public void setPause(Date date) {
		term.setPause(date);
	}

	public void inputShopping() {
		term.inputShopping();
	}

	public void clearPanel() {
		term.clearPanel(false);
	}

	public int getTermHeight() {
		return term.getTermHeight();
	}

	public void inputBool(BooleanCallback callback) {
		term.inputBool(callback);
	}

	public int getTermWidth() {
		return term.getTermWidth();
	}

	public void setPassChar(char ch) {
		term.setPassChar(ch);
	}

	public void setCursorVisible(boolean visible) {
		term.setCursorVisible(visible);
	}

	public void setCursor(int x, int y) {
		term.setCursor(x, y);
	}

	public void setCursor(byte x, byte y, boolean visible) {
		term.setCursorVisible(visible);
		term.setCursor(x, y);
	}

	public void askDirection(String query_string, DirectionCallback callback) {
		putMessage(query_string);
		term.inputDirection(callback);
	}

	public void show_map(CallbackUser user) {
		saveTerm();

		CharCallback callback;

		callback = new CharCallback(user) {
			@Override
			public void update(char ch) {
				if (ch == MangbandTerm.ESCAPE) {
					restoreTerm();
					cancelInputMode();
				}
			}
		};

		inputChar(callback);
	}

	public void show_book(SpellBook book, MangClientImpl mangClient) {
		saveTerm();

		int i = 0;
		for (String spell : book) {
			drawOverlayString(spell, 15, i + 1, 16, TermColours.YELLOW);
			i++;
		}
	}

	public void drawStat(boolean chosen, final String name, int i) {
		int attr = TermColours.LIGHT_WHITE;
		if (chosen) {
			attr = TermColours.SLATE;
		}
		term.drawString(name, attr, 45, 8 + i);
	}

	/**
	 * Draw stats on selection screen
	 * @param chosen
	 * @param names
	 */
	public void drawStats(boolean[] chosen, String[] names) {
		for (int i = 0; i < names.length; i++) {
			drawStat(chosen[i], names[i], i);
		}
	}
}
