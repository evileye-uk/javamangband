/*
 * MangClientImpl.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.input;

import uk.co.telperion.mangband.TermColours;
import uk.co.telperion.mangband.game.*;
import uk.co.telperion.mangband.network.*;
import uk.co.telperion.mangband.ui.ClientView;
import uk.co.telperion.mangband.ui.MangbandTerm;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

/**
 * Mangband client main class
 *
 * @author evileye
 */
public class MangClientImpl implements Runnable, CallbackUser, MangClient, Shop {

	private final MangbandTerm term;
	private Client netClient;
	private NetClient netSocket;
	private final ClientView clientView;
	private final CharacterModel model;

	private boolean redrawStat;
	private int redraw_time = 20;
	private byte floor_tval;

	private final LinkedList<String> history;
	private final ArrayList<SpellBook> spellBooks;
	private final ArrayList<ShopItem> shopItems;
	private int shopSize;

	private static final int WARRIOR = 0;
	private static final int MAGE = 1;
	private static final int PRIEST = 2;
	private static final int ROGUE = 3;
	private static final int RANGER = 4;
	private static final int PALADIN = 5;

	private boolean shopping = false;
	private int storePage;
	private String shopOwner;
	private boolean login = false;
	private String passwd;
	private boolean fileDisp = false;
	private int fileLine;
	private int lastLine;
	private int specialFile;
	private int maxFileLine;
	private String serverName;
	private boolean motd = false;

	private final InputUtil inputUtil;
	private short serverPort = 18346;
	private int target_x;
	private int target_y;

	private String username;
	private String localhostname;

	private short index;

	MangClientImpl(MangbandTerm mainTerm, String hostname) {
		term = mainTerm;
		mainTerm.setClient(this);

		model = new CharacterModel();
		clientView = new ClientView(mainTerm, model);

		inputUtil = new InputUtil(this, mainTerm);

		history = new LinkedList<String>();
		spellBooks = new ArrayList<SpellBook>();
		spellBooks.ensureCapacity(8);
		shopItems = new ArrayList<ShopItem>(8);
		shopItems.ensureCapacity(10);
		model.charHistory = new ArrayList<String>();
		serverName = hostname;

		try {
			localhostname = System.getenv("MANGBAND_HOST");
			username = System.getenv("MANGBAND_USER");
		} catch (Exception e) {
		}

		if (localhostname == null)
			localhostname = "default";
		if (username == null)
			username = "javaclient";
	}

	private FilteredItemCallback<ObjIntConsumer<Client>> createEquipItemCallback(Predicate<InventoryItem> predicate, ObjIntConsumer<Client> action) {
		return new FilteredItemCallback<ObjIntConsumer<Client>>(this, term, predicate, action, true) {

			@Override
			public void update_item(int index) {
				if(!predicate.test(model.equipmentItem(index)))
					return;
				
				action.accept(netClient, index + 24);

				if (inven_shown) {
					clientView.restoreTerm();
				}
				clientView.cancelInputMode();
			}
		};
	}
	
	private void initClient() {
		if (!netClient.login_reply()) {
			System.out.println("Login reply fail");
		} else {

			if (netClient.login_verify(username, model.nick, passwd, model.sex, model.race, model.char_class)) {
				if (netClient.read_magic()) {
					netClient.read_motd();
				}
			}
		}
	}

	/**
	 * Thread main loop. Run the actual client
	 */
	@Override
	public void run() {
		while (!login) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.println("Interrupted:" + e);
			}
			draw();
		}

		initClient();

		while (motd) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.println("Interrupted:" + e);
			}
			draw();
		}

		if (netClient.send_play()) {
			long last = 0;

			while (netSocket.isConnected()) {
				try {
					if (!netClient.ready()) {
						Thread.sleep(redraw_time);
					} else {
						netClient.receive();
					}

					long now = new Date().getTime();
					if (now - last > 10) {
						last = now;
						draw();
					}
					if (netSocket.isConnected()) {
						netClient.maintain();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (UnknownPacketException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	@Override
	public void display_pack(boolean equipment, boolean awaitEscape) {
		clientView.saveTerm();

		InventoryItem[] items = equipment ? model.equip : model.inventory;

		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				if (equipment)
					clientView.clearPackLine(i + 1);
				if (items[i].isValid()) {
					char index1 = (char) ('a' + i);
					clientView.clearPackLine(i + 1);

					// Chop inventory length
					int length = items[i].getName().length();
					if (length > 60) {
						length = 60;
					}

					clientView.drawString(index1 + ") " + items[i].getName().substring(0, length), items[i].getAttr(), 15, i + 1,
							false);
				}
			}
		}

		if (awaitEscape) {
			clientView.waitEscape();
		}
	}

	public void do_move(int direction, boolean run) {
		clientView.clearMsg();
		netClient.send_move(direction, run);
	}

	public void do_map() {
		netClient.send_map();
		clientView.show_map(this);
	}

	public void pickup() {
		netClient.send_stay();
	}

	public void go_down() {
		netClient.send_go_down();
	}

	public void go_up() {
		netClient.send_go_up();
	}

	private void send_chat(String talkBuffer) {
		netClient.send_chat(talkBuffer);
	}

	private void draw() {
		if (redrawStat) {
			clientView.drawAC();
			clientView.drawGold();
			clientView.drawHP();
			clientView.drawChar();
			clientView.drawTitle();
			clientView.drawExperience();
			clientView.drawSP();
			clientView.drawDepth();
			clientView.drawSpeed();
			clientView.drawStudy();
			clientView.drawParalysis();
			clientView.drawSearching();
			clientView.drawResting();
			clientView.drawConfusion();
			clientView.drawPoison();
			clientView.drawFear();
			clientView.drawCut();
			clientView.drawStun();
			clientView.drawBlind();
			clientView.drawDepth();
			clientView.drawFood();

			for (int i = 0; i < CharacterModel.MAX_STAT; i++) {
				clientView.drawStat(i);
			}
			redrawStat = false;
		}

		clientView.paintBuffer();
	}

	/**
	 * Set current and maximum hitpoints. Updates the display to reflect this.
	 *
	 * @param max
	 *          maximum hitpoints available value
	 * @param cur
	 *          current hitpoints value
	 */
	public void setHP(short max, short cur) {
		model.maxHP = max;
		model.curHP = cur;

		clientView.drawHP();
	}

	/**
	 * Set base and modifier armour class. Updates the display to reflect this.
	 *
	 * @param base
	 *          base armour class
	 * @param plus
	 *          armour class modifiers
	 */
	public void setAC(short base, short plus) {
		model.baseAC = base;
		model.plusAC = plus;

		clientView.drawAC();
	}

	/**
	 * Draw the character on the terminal
	 *
	 * @param c
	 *          character to draw
	 * @param a
	 *          attribute colour
	 * @param x
	 *          x position
	 * @param y
	 *          y position
	 */
	public void setChar(byte c, byte a, byte x, byte y) {
		clientView.drawChar(c, a, x, y, true);
	}

	/**
	 * Add a persistent message to the terminal.
	 */
	public void addMessage(String message) {
		clientView.putMessage(message);

		history.addFirst(message);
		/*
		 * while (history.size() > 20) { history.removeLast(); }
		 */

		showHistory(3, true);
		showHistory(3, false);
	}

	private void showHistory(int maxLines, boolean base) {
		int i = 0;

		Iterator<String> it;
		it = history.iterator();

		while (it.hasNext() && i < maxLines) {
			clientView.clearLine(28 - i, base);
			clientView.drawString(it.next(), 1, 0, 28 - i, base);
			i++;
		}
	}

	public void displayHistory() {
		clientView.saveTerm();
		clientView.clear(false);
		showHistory(27, false);
		clientView.inputFile();
		fileDisp = true;
	}

	/**
	 * Set the current gold amount. Updates display.
	 *
	 * @param gold
	 *          amount of gold being held
	 */
	public void setGold(int gold) {
		model.gold = gold;
		clientView.drawGold();
	}

	/**
	 * Set a single player character statistic.
	 *
	 * @param statIndex
	 *          index of the statistic
	 * @param maximum
	 *          the maximum value for the statistic
	 * @param current
	 *          the current value for the statistic
	 */
	public void setStat(byte statIndex, short maximum, short current) {
		model.stats[statIndex].current = current;
		model.stats[statIndex].maximum = maximum;

		clientView.drawStat(statIndex);
	}

	/**
	 * Initial line setter for map
	 *
	 * @param y
	 *          line y value
	 * @param attrs
	 *          array of attributes for the line
	 * @param chars
	 *          array of characters for the line
	 * @param base
	 *          display to base/current view
	 */
	public void setLine(short y, byte[] attrs, byte[] chars, boolean base) {
		for (int i = 0; i < 80; i++) {
			clientView.drawChar(chars[i], attrs[i], (byte) i, (byte) y, base);
		}
		this.redrawStat = true;
	}

	/**
	 * Set character information
	 *
	 * @param race
	 *          race type
	 * @param char_class
	 *          character class
	 * @param sex
	 *          character sex
	 */
	public void setCharInfo(short race, short char_class, short sex) {
		model.race = race;
		model.char_class = char_class;
		model.sex = sex;
		clientView.drawChar();
	}

	/**
	 * Set max stats for character
	 *
	 * @param stat
	 *          index of stat being set
	 * @param max
	 *          maximum value for stat
	 */
	public void setMaxStat(byte stat, short max) {
		model.stats[stat].limit = max;
	}

	/**
	 * Set character's title. Updates view
	 *
	 * @param title
	 *          title string to be set
	 */
	public void setTitle(String title) {
		model.title = title;
		model.ghost = title.equals("Ghost");
		clientView.drawTitle();
	}

	/**
	 * Set experience levels
	 *
	 * @param level
	 *          new experience level
	 * @param max
	 *          new max experience
	 * @param cur
	 *          new current experience
	 * @param adv
	 *          experience to advance
	 */
	public void setExperience(short level, int max, int cur, int adv) {
		model.level = level;
		model.maxExp = max;
		model.curExp = cur;
		model.advExp = adv;

		clientView.drawExperience();
	}

	/**
	 * Store an inventory item in array
	 *
	 * @param pos
	 *          alphabetic index of inventory item
	 * @param attr
	 *          colour attribute of item
	 * @param weight
	 *          weight of item
	 * @param amount
	 *          number of items being held
	 * @param tval
	 *          type value of item
	 * @param name
	 *          descriptive string
	 */
	public void setInventory(byte pos, byte attr, short weight, short amount, byte tval, String name) {
		InventoryItem item;

		item = new InventoryItem(attr, weight, amount, tval, name);

		model.inventory[pos - 97] = item;
	}

	/**
	 * Store an equipment item in array
	 *
	 * @param pos
	 *          alphabetic index of inventory item
	 * @param attr
	 *          colour attribute of item
	 * @param weight
	 *          weight of item
	 * @param tval
	 *          type value of item
	 * @param name
	 *          descriptive string
	 */
	public void setEquip(byte pos, byte attr, short weight, byte tval, String name) {
		InventoryItem item;

		item = new InventoryItem(attr, weight, 1, tval, name);

		model.equip[pos - 97] = item;
	}

	/**
	 * Wear an item
	 */
	public void init_wield() {
		if (model.countInventory(InventoryItem::isWieldable) == 0) {
			addMessage("You have nothing to wield.");
			return;
		}

		clientView.chooseItem(InventoryItem::isWieldable, "Wield what?", this, (index) -> netClient.send_wield((short)index));
	}

	/**
	 * Set current and maximum spell points. Updates display.
	 *
	 * @param max
	 *          maximum spell points available
	 * @param cur
	 *          current spell points
	 */
	public void setSP(short max, short cur) {
		model.maxSP = max;
		model.curSP = cur;

		clientView.drawSP();
	}

	public void do_message() {
		StringCallback callback;

		callback = new StringCallback(this) {

			@Override
			public void update(String str) {
				send_chat(str);
				clientView.clearLine(25, false);
			}

			@Override
			public void cancel() {
				clientView.cancelInputMode();
			}
		};

		clientView.inputString(25, callback);
	}

	/**
	 * Set current food value
	 *
	 * @param food
	 *          new food value
	 */
	public void setFood(short food) {
		model.food = food;
		clientView.drawFood();
	}

	public void escape() {
		if (motd) {
			motd = false;
		}
		clientView.restoreTerm();
	}

	/**
	 * Start to remove a wielded/worn item.
	 */
	public void init_remove() {	
		FilteredItemCallback<ObjIntConsumer<Client>> itemCallback = createEquipItemCallback(InventoryItem::isValid, (a, b) -> a.send_remove((char)b));
		clientView.requestItem("Remove what?", itemCallback);
	}

	public void init_read() {
		if (model.countInventory(InventoryItem::isScroll) == 0) {
			addMessage("You have no scrolls.");
			return;
		}

		clientView.chooseItem(InventoryItem::isScroll, "Read what?", this, (index) -> netClient.send_read((char)index));
	}

	public void init_ghost() {
		if (!model.ghost) {
			clientView.putMessage("You have no ghostly powers");
			return;
		}

		use_magic(index, MagicType.GHOST);
	}

	public void init_cast(final MagicType magicType) {
		if (model.ghost) {
			clientView.putMessage("You are a ghost");
			return;
		}

		get_book(magicType);
	}

	private void get_book(MagicType magicType) {
		ItemCallback bookCallback;

		bookCallback = new ItemCallback(this, term) {
			@Override
			public void update_item(int index) {
				if (!model.inventoryItem(index).isCastable(magicType))
					return;
				if (inven_shown) {
					clientView.restoreTerm();
				}
				use_magic(index, magicType);
			}
		};

		clientView.requestItem(magicType.bookSelectionString(), bookCallback);
	}

	private void use_magic(int index, final MagicType magicType) {

		SpellCallback spellCallback = new SpellCallback(this, term, index) {
			@Override
			public void updateSpell(char ch) {
				switch (magicType) {
				case SPELL:
					netClient.send_cast(index, ch - 'a');
					break;
				case PRAYER:
					netClient.send_pray(index, ch - 'a');
					break;
				case GHOST:
					netClient.send_ghost(ch - 'a');
					break;
				}
			}
		};

		clientView.putMessage(magicType.selectionString());
		clientView.inputChar(spellCallback);
	}

	public void init_quaff() {
		if (model.countInventory(InventoryItem::isQuaffable) == 0) {
			addMessage("You have nothing to drink.");
			return;
		}

		clientView.chooseItem(InventoryItem::isQuaffable, "Quaff what?", this, (index) -> netClient.send_quaff((char)index));
	}

	public void init_eat() {
		if (model.countInventory(InventoryItem::isEatable) == 0) {
			addMessage("You have nothing to eat.");
			return;
		}

		clientView.chooseItem(InventoryItem::isEatable, "Eat what?", this, (index) -> netClient.send_eat((char)index));
	}

	public void init_fill() {
		clientView.chooseItem(InventoryItem::isFuelable, "Fill what?", this, (index) -> netClient.send_fill((char)index));
	}

	public void init_drop() {
		ItemCallback callback;
		CallbackUser callback_client = this;

		callback = new ItemCallback(this, term) {

			int dropIndex;
			final NumberCallback numberCallback = new NumberCallback(callback_client) {

				@Override
				public void update(int num) {
					send_drop(dropIndex, num);
					cancel();
				}

				@Override
				public void cancel() {
					clientView.resetInputMode();
				}
			};

			@Override
			public void update_item(int index) {
				dropIndex = index;
				if (inven_shown) {
					clientView.restoreTerm();
				}

				if (!model.inventoryItem(index).isValid())
					return;

				if (model.inventoryItem(dropIndex).getAmount() > 1) {
					clientView.putMessage("Amount:");
					clientView.inputNum(8, 6, numberCallback);
				} else {
					send_drop(dropIndex, 1);
					clientView.cancelInputMode();
				}
			}
		};

		clientView.requestItem("Drop what?", callback);
	}

	private void send_drop(int index, int amount) {
		netClient.send_drop(index, amount);
	}

	public void init_destroy() {
		ItemCallback callback;
		CallbackUser callback_client = this;

		callback = new ItemCallback(this, term) {

			int itemIndex;

			final NumberCallback numberCallback = new NumberCallback(callback_client) {

				@Override
				public void update(int num) {
					send_destroy(itemIndex, num);
					cancel();
				}

				@Override
				public void cancel() {
					clientView.cancelInputMode();
				}
			};

			@Override
			public void update_item(int index) {
				itemIndex = index;

				if (inven_shown) {
					clientView.restoreTerm();
				}

				if (!model.inventoryItem(index).isValid())
					return;

				if (model.inventoryItem(index).getAmount() > 1) {
					clientView.putMessage("Amount:");
					clientView.inputNum(8, 6, numberCallback);
				} else {
					send_destroy(index, 1);
					clientView.cancelInputMode();
				}
			}
		};

		clientView.requestItem("Destroy what?", callback);
	}

	private void send_destroy(int index, int amount) {
		netClient.send_destroy(index, amount);
	}

	public void ask_item() {
		clientView.chooseItem(InventoryItem::isValid, "Which Item?", this, (index) -> netClient.send_item((char)index));
	}

	/**
	 * Add spell to spellbooks
	 *
	 * @param bookIndex
	 * @param line
	 * @param spell
	 */
	public void addSpell(int bookIndex, int line, String spell) {
		SpellBook book;

		try {
			book = this.spellBooks.get(bookIndex);
		} catch (IndexOutOfBoundsException e) {
			book = new SpellBook();
			this.spellBooks.add(bookIndex, book);
		}
		book.addSpell(line, spell);
	}

	public void peruse() {
		if (model.ghost) {
			show_book(0);
			clientView.waitEscape();
			clientView.clearMsg();
			return;
		}

		MagicType magicType;

		switch (model.char_class) {
		case PRIEST:
		case PALADIN:
			magicType = MagicType.PRAYER;
			break;
		case MAGE:
		case ROGUE:
		case RANGER:
			magicType = MagicType.SPELL;
			break;
		default:
			addMessage("You cannot read books.");
			return;
		}

		int num_books = model.countInventory(p -> p.isCastable(magicType));

		if (num_books == 0) {
			addMessage("You have no books from which to read.");
			return;
		}

		ItemCallback callback;

		callback = new ItemCallback(this, term) {

			@Override
			public void update_item(int index) {
				if (!model.inventoryItem(index).isCastable(magicType))
					return;

				if (inven_shown) {
					clientView.restoreTerm();
					inven_shown = false;
				}
				clientView.cancelInputMode();
				show_book(index);
				clientView.waitEscape();
				clientView.clearMsg();
			}
		};

		clientView.requestItem("Peruse which book?", callback);
	}

	public void show_book(int index) {
		int book_id = 0;

		if (!isClassBook(model.inventoryItem(index)))
			return;

		for (int i = index; i > 0; i--) {
			if (isClassBook(model.inventoryItem(i))) {
				book_id++;
			}
		}

		// Index should be good at this point.
		SpellBook book = spellBooks.get(book_id);

		clientView.show_book(book, this);
	}

	/**
	 * Activate an item
	 */
	public void activate() {
		clientView.chooseItem(InventoryItem::isValid, "Activate which item?", this, (index) -> netClient.send_activate((char)index));
	}

	/**
	 * Zap a rod.
	 */
	public void zap() {
		if (model.countInventory(InventoryItem::isRod) == 0) {
			addMessage("You have no rods.");
			return;
		}

		clientView.chooseItem(InventoryItem::isRod, "Zap which rod?", this, (index) -> netClient.send_zap((char)index));
	}

	/**
	 * Use a staff
	 */
	public void use() {
		if (model.countInventory(InventoryItem::isStaff) == 0) {
			addMessage("You have no staves.");
			return;
		}

		clientView.chooseItem(InventoryItem::isStaff, "Use which staff?", this, (index) -> netClient.send_use((char)index));
	}


	/**
	 * Gain a new spell/prayer
	 */
	public void init_gain() {
		ItemCallback bookCallback;

		if (model.ghost) {
			clientView.putMessage("You are a ghost");
			return;
		}

		bookCallback = new ItemCallback(this, term) {

			@Override
			public void update_item(int index) {
				if (!isClassBook(model.inventoryItem(index)))
					return;
				if (inven_shown) {
					clientView.restoreTerm();
					inven_shown = false;
				}
				gain(index);
			}

		};

		clientView.requestItem("Gain from which book?", bookCallback);
	}

	/**
	 * Function to test whether item is usable book
	 *
	 * @param item
	 *          item from inventory
	 * @return true if item is a usable book
	 */
	private boolean isClassBook(InventoryItem item) {
		switch (model.char_class) {
		case PRIEST:
		case PALADIN:
			return item.isCastable(MagicType.PRAYER);
		case MAGE:
		case ROGUE:
		case RANGER:
			return item.isCastable(MagicType.SPELL);
		case WARRIOR:
		}
		return false;
	}

	private void gain(int index) {
		if (model.inventoryItem(index).isCastable(MagicType.PRAYER)) {
			netClient.send_gain(index, 0);
			clientView.cancelInputMode();
		} else {
			SpellCallback spellCallback = new SpellCallback(this, term, index) {
				@Override
				public void updateSpell(char ch) {
					netClient.send_gain(index, ch - 'a');
				}
			};
			clientView.putMessage("Gain which spell?");
			clientView.inputChar(spellCallback);
		}
	}

	/**
	 * Disarm a trap
	 */
	public void disarm() {
		DirectionCallback callback;

		callback = new DirectionCallback(this, hasTarget()) {

			@Override
			public void update_dir(char direction) {
				netClient.send_disarm(direction);
				cancel();
			}

			@Override
			public void cancel() {
				clientView.clearMsg();
				clientView.cancelInputMode();
			}
		};

		clientView.askDirection("Disarm which direction?", callback);
	}

	/**
	 * Aim a wand
	 */
	public void aim() {
		if (model.countInventory(InventoryItem::isWand) == 0) {
			addMessage("You have no wands.");
			return;
		}

		FilteredItemCallback<BiConsumer<Client, DirectionObj>> callback;

		callback = createDirectionalItemCallback(this,
				InventoryItem::isWand,
				(a, b) -> a.send_aim(b.getIndex(), b.getDirection()),
				"Direction?");

		clientView.requestItem("Aim which wand?", callback);
	}

	/**
	 * Obtain direction from user and send back to the server.
	 */
	public void getDirection() {
		DirectionCallback callback;

		callback = new DirectionCallback(this, hasTarget()) {

			@Override
			public void update_dir(char direction) {
				send_direction(direction);
				cancel();
			}

			@Override
			public void cancel() {
				clientView.cancelInputMode();
			}
		};

		clientView.askDirection("Direction?", callback);
	}

	private void send_direction(char ch) {
		netClient.send_direction(ch);
	}

	/**
	 * Calculate and set the redraw time, in milliseconds
	 *
	 * @param fps
	 *          frames per second value
	 */
	public void setFps(int fps) {
		if (fps != 0) {
			this.redraw_time = 1000 / fps;
		}
	}

	/**
	 * Set floor item type
	 *
	 * @param floor_tval
	 *          floor item type
	 */
	public void setFloor(byte floor_tval) {
		this.floor_tval = floor_tval;
	}

	/**
	 * Set a pause on the terminal
	 */
	public void setPause() {
		clientView.setPause(new Date());
	}

	/**
	 * Insert a shop inventory item at the given position.
	 *
	 * @param pos
	 *          location of item to be added
	 * @param shopItem
	 *          instance of item being added to shop
	 */
	public void addShop(int pos, ShopItem shopItem) {
		if (shopItems.size() <= pos) {
			shopItems.add(pos, shopItem);
		} else {
			shopItems.set(pos, shopItem);
		}
	}

	/**
	 * Set the shop inventory size and enable shopping mode.
	 *
	 * @param size
	 * @param store
	 * @param owner
	 */
	public void setShop(int size, int store, int owner) {
		shopSize = size;
		shopOwner = StoreOwner.names[store][owner];

		if (!shopping) {
			shopping = true;
			clientView.saveTerm();
		}
		clientView.inputShopping();
		displayStore(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.co.telperion.mangband.input.Shop#nextShopPage()
	 */
	@Override
	public void nextShopPage() {
		displayStore(storePage + 1);
	}

	private void displayStore(int page) {
		int pageSize = clientView.getTermHeight() - 10;

		if (pageSize * page >= shopSize) {
			page = 0;
		}

		int pageLength = ((page + 1) * pageSize > shopSize) ? shopSize % pageSize : pageSize;

		clientView.clearPanel();

		storePage = page;

		clientView.drawString(shopOwner, TermColours.LIGHT_GREEN, 1, 0);

		DecimalFormat weightFormat, priceFormat;
		weightFormat = new DecimalFormat("####0.0");
		priceFormat = new DecimalFormat("######");

		try {
			for (int i = 0; i < pageLength; i++) {
				ShopItem item = shopItems.get(page * pageSize + i);

				clientView.drawShopItemString(i, item, weightFormat.format(item.getWeight()), priceFormat.format(item.getPrice()));
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.co.telperion.mangband.input.Shop#endShopping()
	 */
	@Override
	public void endShopping() {
		clientView.restoreTerm();
		shopping = false;
		netClient.send_store_leave();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.co.telperion.mangband.input.Shop#purchase()
	 */
	@Override
	public void purchase() {
		CharCallback callback;
		CallbackUser callback_client = this;

		final int pageSize = clientView.getTermHeight() - 10;

		clientView.saveTerm();
		clientView.drawOverlayString("Purchase which item?", 0, 0, 0, TermColours.YELLOW);

		callback = new CharCallback(callback_client) {

			int index;
			final NumberCallback numberCallback = new NumberCallback(callback_client) {

				@Override
				public void update(int amount) {
					if (amount > 0) {
						netClient.send_purchase(index, amount);
					}
					cancel();
				}

				@Override
				public void cancel() {
					clientView.inputShopping();
					clientView.restoreTerm();
				}
			};

			@Override
			public void update(char ch) {

				if (ch >= 'a' && ch <= 'w') {
					index = (ch - 'a') + storePage * pageSize;
					if (index >= shopItems.size()) {
						return;
					}

					if (shopItems.get(index).getAmount() > 1) {
						clientView.putMessage("Amount:");
						clientView.inputNum(8, 6, numberCallback);
					} else {
						netClient.send_purchase(index, 1);
						endPurchase();
					}
				} else if (ch == MangbandTerm.ESCAPE) {
					endPurchase();
				}
			}

			private void endPurchase() {
				clientView.inputShopping();
				clientView.restoreTerm();
			}
		};

		clientView.inputChar(callback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.co.telperion.mangband.input.Shop#init_sell()
	 */
	@Override
	public void init_sell() {
		ItemCallback callback;
		CallbackUser callback_client = this;

		clientView.drawOverlayString("Sell which item?", 0, 0, 0, TermColours.YELLOW);

		callback = new ItemCallback(this, term) {

			int sellIndex;

			final NumberCallback numberCallback = new NumberCallback(callback_client) {

				@Override
				public void update(int amount) {
					netClient.send_sell(sellIndex, amount);
					clientView.inputShopping();
				}

				@Override
				public void cancel() {
					clientView.inputShopping();
				}
			};

			@Override
			public void update_item(int index) {
				if (!model.inventoryItem(index).isValid())
					return;

				sellIndex = index;
				if (inven_shown) {
					clientView.restoreTerm();
					inven_shown = false;
				}
				if (model.inventoryItem(index).getAmount() > 1) {
					clientView.putMessage("Amount:");
					clientView.inputNum(8, 6, numberCallback);
				} else {
					netClient.send_sell(index, 1);
					clientView.inputShopping();
				}
			}

			@Override
			public void cancel() {
				if (inven_shown) {
					clientView.restoreTerm();
					inven_shown = false;
				}
				clientView.inputShopping();
				clientView.clearMsg();
			}

		};

		clientView.inputChar(callback);
	}

	public void confirm_sell(int price) {
		BooleanCallback callback;

		clientView.clearMsg();
		clientView.drawString("Sell for " + price + " gold?", TermColours.YELLOW, 0, 0);

		callback = new BooleanCallback(this) {

			@Override
			public void update(boolean val) {
				if (val) {
					netClient.send_confirm_sell();
				}
				clientView.inputShopping();
				clientView.clearMsg();
			}

			@Override
			public void cancel() {
				clientView.inputShopping();
				clientView.clearMsg();
			}
		};
		clientView.inputBool(callback);
	}

	public void search() {
		netClient.send_search();
	}

	public void setSearch() {
		netClient.send_setsearch();
	}

	/**
	 * Perform a metaserver request and present the results to the user. Connect
	 * to the chosen server.
	 */
	private void doMetaQuery() {
		MetaQueryClient meta_client = new MetaQueryClient();
		MetaQuery meta = meta_client.result();

		int i = 0;
		int j = 0;

		Iterator<GameServer> it = meta.getServers();

		while (it.hasNext() && i < 12) {
			GameServer server = it.next();

			clientView.drawString((char) ('a' + j) + ") " + server.getHostname(), TermColours.LIGHT_BLUE, 5, 1 + i);

			i++;
			j++;

			String details = server.getDetails();
			int pos = 0;

			while (details.length() > clientView.getTermWidth()) {
				String split = details.substring(pos, clientView.getTermWidth());
				int newpos = split.lastIndexOf(' ') + 1;
				split = details.substring(pos, newpos);
				details = details.substring(newpos);
				clientView.drawString(split, TermColours.LIGHT_GREEN, 5, 1 + i);

				pos = newpos;
				i++;
			}

			clientView.drawString(details, TermColours.LIGHT_GREEN, 5, 1 + i);
			i++;
		}
		int index = this.getIndex(i);
		GameServer server = meta.getServer(index);
		this.serverName = server.getHostname();
		this.serverPort = server.getPort();
	}

	/**
	 * Interact with user and connect to a server
	 */
	@Override
	public boolean login() {

		if (serverName.length() == 0) {
			doMetaQuery();
		}

		clientView.clear(false);

		clientView.drawString("Name:", TermColours.LIGHT_GREEN, 5, 5);
		clientView.drawString("Password:", TermColours.LIGHT_GREEN, 5, 6);

		setNick(inputUtil.getString(11, 5, 20, false));
		clientView.setPassChar('*');
		setPass(inputUtil.getString(15, 6, 20, false));

		clientView.clear(true);

		netSocket = new NetClient(serverName, serverPort);
		netClient = netSocket.createClient(this);

		if (netSocket.isConnected()) {
			netClient.init_login(username, model.nick, localhostname);
			clientView.setPassChar('\0');
			login = true;
			return true;
		}

		return false;
	}

	private void setNick(String input) {
		model.nick = input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	/**
	 * Request more information from the user
	 */
	public void req_info() {
		clientView.drawString("Enter character details", TermColours.LIGHT_BLUE, 5, 4);

		for (int i = 0; i < clientView.sexTitle.length; i++) {
			clientView.drawString((char) ('a' + i) + ") " + clientView.sexTitle[i], TermColours.LIGHT_BLUE, 5, 5 + i);
		}

		model.sex = getIndex(2);

		for (int i = 0; i < clientView.classTitle.length; i++) {
			clientView.drawString((char) ('a' + i) + ") " + clientView.classTitle[i], TermColours.LIGHT_BLUE, 5, 8 + i);
		}

		model.char_class = getIndex(clientView.classTitle.length);

		for (int i = 0; i < clientView.raceTitle.length; i++) {
			clientView.drawString((char) ('a' + i) + ") " + clientView.raceTitle[i], TermColours.LIGHT_BLUE, 25, 8 + i);
		}

		model.race = getIndex(clientView.raceTitle.length);

		readStatOrder();

		if (netClient.login_verify(username, model.nick, passwd, model.sex, model.race, model.char_class)) {
			if (netClient.read_magic()) {
				netClient.read_motd();
			}
		} else {
			System.out.println("Login failed");
		}
	}

	/**
	 * Request an index selection from the user.
	 *
	 * @param max
	 *          maximum index selection
	 * @return index selected
	 */
	private int getIndex(int max) {
		class IndexCallback extends CharCallback {

			int max;
			int rvalInt = -1;

			public IndexCallback(MangClientImpl client, int max) {
				super(client);
				this.max = max;
			}

			@Override
			public void update(char ch) {
				if (ch >= 'a' && ch <= ('a' + max)) {
					rvalInt = (ch - 'a');
					clientView.cancelInputMode();
				}
			}

			public int status() {
				return rvalInt;
			}
		}

		IndexCallback callback = new IndexCallback(this, max);

		clientView.inputChar(callback);
		while (callback.status() == -1) {
			try {
				this.draw();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				System.out.println("Interrupted:" + e);
			}
		}

		return callback.status();
	}

	/**
	 * Read in the stat order
	 */
	private void readStatOrder() {
		final String[] names = { "a) Strength", "b) Intelligence", "c) Wisdom", "d) Dexterity", "e) Constitution",
				"f) Charisma" };
		boolean[] chosen = new boolean[6];
		int nStats = 0;
		clientView.drawStats(chosen, names);

		while (nStats < 6) {
			int ind = getIndex(6);
			if (!chosen[ind]) {
				model.stats[nStats++].order = ind;
				chosen[ind] = true;
				clientView.drawStat(chosen[ind], names[ind], ind);
			}
		}
	}

	public void displayFile(int file, int line) {
		fileLine = line;
		specialFile = file;
		netClient.send_special_line(file, line);
	}

	private void setPass(String input) {
		passwd = input;
	}

	/**
	 * Set file display mode.
	 *
	 */
	public void setFileDisp() {
		// Ignore non-change setting
		if (!fileDisp) {
			return;
		}

		fileDisp = false;
		clientView.restoreTerm();
	}

	/**
	 * Display text data from a file
	 *
	 * @param line
	 *          screen line on which to draw string
	 * @param max
	 *          maximum line
	 * @param string
	 *          string to be drawn
	 * @param attr
	 *          colour in which to draw
	 */
	public void display_file(int line, int max, String string, byte attr) {
		if (!fileDisp) {
			clientView.saveTerm();
			clientView.clear(false);
			clientView.inputFile();
			fileDisp = true;
			maxFileLine = max;
		}
		if (attr == -1) {
			attr = TermColours.LIGHT_WHITE;
		}
		clientView.drawString(string, attr, 0, line);
		lastLine = line + fileLine;
	}

	private void showBackground() {
		clientView.drawString("Background", TermColours.WHITE, 25, 15);

		Iterator<String> it = model.charHistory.iterator();
		int i = 16;
		while (it.hasNext()) {
			clientView.drawString(it.next(), TermColours.WHITE, 10, i++);
		}
	}

	/**
	 * Set to hit and to damage modifiers.
	 *
	 * @param mod_hit
	 *          value to set toHit
	 * @param mod_dam
	 *          value to set toDam
	 */
	public void setMods(short mod_hit, short mod_dam) {
		model.toHit = mod_hit;
		model.toDam = mod_dam;
	}

	/**
	 * Move forward one page
	 */
	public void nextPage() {
		if (fileLine + 20 < maxFileLine) {
			clientView.clear(false);
			this.displayFile(specialFile, lastLine);
		}
	}

	/**
	 * Move back one page
	 */
	public void prevPage() {
		clientView.clear(false);
		int newLine = (fileLine >= 19 ? fileLine - 19 : 0);
		this.displayFile(specialFile, newLine);
	}

	public void scrollDown() {
		if (fileLine + 20 < maxFileLine) {
			clientView.clear(false);
			this.displayFile(specialFile, fileLine + 1);
		}
	}

	public void scrollUp() {
		clientView.clear(false);
		int newLine = (fileLine >= 1 ? fileLine - 1 : 0);
		this.displayFile(specialFile, newLine);
	}

	/**
	 * Display the message of the day.
	 *
	 * @param line
	 *          index of the line to be displayed
	 * @param string
	 *          string to be displayed on the line
	 */
	public void display_motd(int line, String string) {
		if (!motd) {
			motd = true;
			clientView.saveTerm();
		}
		clientView.drawString(string, TermColours.LIGHT_GREEN, 0, line);
		clientView.waitEscape();
	}

	public void rest() {
		netClient.send_rest();
	}

	public void uninscribe() {
		clientView.chooseItem(InventoryItem::isValid, "Uninscribe which item?", this, (index) -> netClient.send_uninscribe((short)index));
	}

	public void inscribe() {
		ItemCallback itemCallback;
		CallbackUser callback_client = this;

		itemCallback = new ItemCallback(this, term) {

			int itemIndex;

			@Override
			public void update_item(int index) {
				if (!model.inventoryItem(index).isValid())
					return;

				itemIndex = index;
				if (inven_shown) {
					clientView.restoreTerm();
					inven_shown = false;
				}

				StringCallback callback;

				callback = new StringCallback(callback_client) {

					@Override
					public void update(String str) {
						netClient.send_inscribe(itemIndex, str);
						clientView.clearMsg();
					}

					@Override
					public void cancel() {
						clientView.cancelInputMode();
					}
				};

				clientView.inputString(0, callback);
			}
		};

		clientView.requestItem("Inscribe which item?", itemCallback);
	}

	/**
	 * Set character basic stats
	 *
	 * @param height
	 *          height of the character
	 * @param weight
	 *          weight of the character
	 * @param age
	 *          character age
	 * @param social_class
	 *          social class
	 */
	public void setCharStats(short height, short weight, short age, short social_class) {
		model.height = height;
		model.weight = weight;
		model.age = age;
		model.social_class = social_class;
	}

	/**
	 * Tunnel through walls/trees.
	 *
	 * @param direction
	 *          direction in which to dig
	 */
	public void do_tunnel(int direction) {
		netClient.send_tunnel(direction);
	}

	/**
	 * Send quit and close the connection
	 */
	public void quit() {
		netClient.send_quit();
		netSocket.close();
	}

	/**
	 * Set player shop info and set shopping mode
	 *
	 * @param size
	 * @param store
	 * @param owner
	 */
	public void setPlayerShop(short size, short store, String owner) {
		shopSize = size;
		shopOwner = owner;

		if (!shopping) {
			shopping = true;
			clientView.saveTerm();
		}
		clientView.clearPanel();
		clientView.inputShopping();
		displayStore(0);
	}

	@Override
	public void clearMessage() {
		clientView.clearMsg();
	}

	/**
	 * Drop gold
	 */
	public void drop_gold() {
		NumberCallback numberCallback = new NumberCallback(this) {

			@Override
			public void update(int amount) {
				netClient.send_dropgold(amount);
				cancel();
			}

			@Override
			public void cancel() {
				clientView.cancelInputMode();
			}
		};

		clientView.putMessage("Drop amount: ");
		clientView.inputNum(13, 8, numberCallback);
	}

	/**
	 * Close a door. Interact to determine direction.
	 */
	public void close_door() {
		DirectionCallback callback;

		callback = new DirectionCallback(this, hasTarget()) {

			@Override
			public void update_dir(char direction) {
				netClient.send_close(direction);
				cancel();
			}

			@Override
			public void cancel() {
				clientView.resetInputMode();
			}
		};

		clientView.askDirection("Close which direction?", callback);
	}

	/**
	 * Open a door. Interact with user to determine direction.
	 */
	public void open_door() {
		DirectionCallback callback;

		callback = new DirectionCallback(this, hasTarget()) {

			@Override
			public void update_dir(char direction) {
				netClient.send_open(direction);
				cancel();
			}

			@Override
			public void cancel() {
				clientView.resetInputMode();
			}
		};

		clientView.askDirection("Open which direction?", callback);
	}

	/**
	 * Suicide - character has died and is too angry for a rescue. Interact to
	 * determine whether or not to proceed.
	 */
	public void suicide() {
		BooleanCallback callback;

		clientView.drawString("Really commit suicide?", TermColours.YELLOW, 0, 0);

		callback = new BooleanCallback(this) {

			@Override
			public void update(boolean val) {
				if (val) {
					netClient.send_suicide();
				}
				cancel();
			}

			@Override
			public void cancel() {
				clientView.resetInputMode();
			}
		};
		clientView.inputBool(callback);
	}

	/**
	 * Return stat position based on index
	 *
	 * @param index
	 *          index of stat
	 */
	public int getStatOrder(int index) {
		return model.stats[index].order;
	}

	class DirectionObj {
		public DirectionObj(int index, char direction) {
			this.index = index;
			this.direction = direction;
		}

		public int getIndex() {
			return index;
		}

		public char getDirection() {
			return direction;
		}

		private final int index;
		private final char direction;
	}

	/**
	 * Throw item; interact with user for direction
	 */
	public void throwItem() {
		FilteredItemCallback<BiConsumer<Client, DirectionObj>> throwCallback;

		throwCallback = createDirectionalItemCallback(this,
				InventoryItem::isValid,
				(a, b) -> a.send_throw(b.getIndex(), b.getDirection()),
				"Throw which direction?");

		clientView.requestItem("Throw which item?", throwCallback);
	}

	private FilteredItemCallback<BiConsumer<Client, DirectionObj>> createDirectionalItemCallback(CallbackUser callback_client,
			Predicate<InventoryItem> predicate,
			BiConsumer<Client, DirectionObj> action, String directionPrompt) {
		return new FilteredItemCallback<BiConsumer<Client, DirectionObj>>(this, term, predicate, action) {

			int throwIndex;
			
			@Override
			public void update_item(int index) {
				if(!predicate.test(model.inventoryItem(index)))
					return;

				throwIndex = index;

				if (inven_shown) {
					clientView.restoreTerm();
					inven_shown = false;
				}

				DirectionCallback callback = new DirectionCallback(callback_client, hasTarget()) {

					@Override
					public void update_dir(char direction) {
						action.accept(netClient, new DirectionObj(throwIndex, direction));
						cancel();
					}

					@Override
					public void cancel() {
						clientView.resetInputMode();
					}
				};
				clientView.askDirection(directionPrompt, callback);
			}
		};
	}

	/**
	 * Fire an item; interact with user for direction/target
	 */
	public void fireItem() {
		FilteredItemCallback<BiConsumer<Client, DirectionObj>> callback = this.createDirectionalItemCallback(this,
				InventoryItem::isValid,
				(a,  b) -> a.send_fire(b.getIndex(), b.getDirection()),
				"Fire which direction?");
		clientView.requestItem("Fire which item?", callback);
	}

	public void playSound(byte soundId) {
	}

	/**
	 * Set speed for indicator
	 *
	 * @param speed
	 *          New current speed
	 */
	public void setSpeed(short speed) {
		model.speed = speed;
		clientView.drawSpeed();
	}

	/**
	 * Set study
	 *
	 * @param study
	 *          if player can study, this is non-zero
	 */
	public void setStudy(byte study) {
		model.study = study;
		clientView.drawStudy();
	}

	public void setState(short paralysed, short searching, short resting) {
		model.paralysed = paralysed;
		model.searching = searching;
		model.resting = resting;
		clientView.drawParalysis();
		clientView.drawSearching();
		clientView.drawResting();
	}

	public void setConfused(byte confused) {
		model.confused = confused;
		clientView.drawConfusion();
	}

	public void setBlind(byte blind) {
		model.blind = blind;
		clientView.drawBlind();
	}

	public void setStun(short stun) {
		model.stun = stun;
		clientView.drawStun();
	}

	public void setCut(short cut) {
		model.cut = cut;
		clientView.drawCut();
	}

	public void setFear(byte fear) {
		model.fear = fear;
		clientView.drawFear();
	}

	public void setPoison(byte poison) {
		model.poison = poison;
		clientView.drawPoison();
	}

	public void setParty(String party) {
		model.party = party;
	}

	public void setDepth(short depth) {
		model.depth = depth;
		clientView.drawDepth();
	}

	public void partyCommand() {
		StringCallback callback;

		callback = new StringCallback(this) {

			@Override
			public void update(String str) {
				netClient.send_create_party(str);
				clientView.clearMsg();
			}

			@Override
			public void cancel() {
				clientView.cancelInputMode();
			}
		};

		clientView.clearMsg();
		clientView.inputString(0, callback);
	}

	public void locate() {
		DirectionCallback callback;

		callback = new DirectionCallback(this, hasTarget(), false) {

			@Override
			public void update_dir(char direction) {
				netClient.send_locate(direction);
			}

			@Override
			public void cancel() {
				clientView.resetInputMode();
				netClient.send_locate((char) 0);
			}
		};

		clientView.clearMsg();
		netClient.send_locate((char) 5);
		clientView.inputDirection(callback);
	}

	public void look() {
		DirectionCallback callback;

		callback = new DirectionCallback(this, hasTarget()) {

			@Override
			public void update(char direction) {
				update_dir(direction);
			}

			@Override
			public void update_dir(char direction) {
				netClient.send_look(direction);
			}

			@Override
			public void cancel() {
				clientView.clearMsg();
				netClient.send_look((char) 5);
				clientView.setCursorVisible(false);
				clientView.cancelInputMode();
			}
		};

		clientView.clearMsg();
		clientView.setCursorVisible(true);
		netClient.send_look((char) 0);
		clientView.inputDirection(callback);
	}

	public void setTarget(String buf, int x, int y) {
		clientView.putMessage(buf);
		target_x = x;
		target_y = y;
		clientView.setCursor(x, y);
	}

	public void setHistory(short line, String text) {
		if (line == 0)
			model.charHistory.clear();
		model.charHistory.add(text);
	}

	/**
	 * Return true if a current target exists
	 *
	 * @return true if target is set
	 */
	private boolean hasTarget() {
		return (target_x != 0 && target_y != 0);
	}

	/**
	 * Reset target variables
	 */
	@Override
	public void resetTarget() {
		target_x = 0;
		target_y = 0;
	}

	/**
	 * Send target request to net client
	 */
	@Override
	public void send_target(int value) {
		netClient.send_target(value);
	}

	public void setCursor(byte x, byte y, boolean visible) {
		clientView.setCursor(x, y, visible);
	}

	public void display_character_sheet() {
		clientView.display_character_sheet();
	}

	@Override
	public CharacterModel getModel() {
		return model;
	}
}
