/*
 * Client.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.network;

import java.io.IOException;
import java.util.Date;

import uk.co.telperion.mangband.PreferenceLoader;
import uk.co.telperion.mangband.game.ShopItem;
import uk.co.telperion.mangband.input.MangClient;

/**
 * Network client for Mangband
 *
 * @author evileye
 */
public class Client {

	private static final int MAX_UNKNOWN = 100;
	private static final int MAX_FEATURE = 128;
	private static final int MAX_OBJECT = 544;
	private static final int MAX_MONSTER = 620;
	private static final int STAT_SUCCESS = 0;
	private static final int STAT_VERSION = 1;
	private static final int STAT_GAME_FULL = 2;
	private static final int STAT_NEED_INFO = 3;
	private static final int STAT_TWO_PLAYERS = 4;
	private static final int STAT_IN_USE = 8;
	private static final int STAT_SOCKET = 9;
	private static final int STAT_INVAL = 10;
	private static final int magic = 12345;
	private static final short version = 4384;
	private final IMangbandOutputStream ostream;
	private final IMangbandInputStream istream;
	private final MangClient controller;
	private int seq;
	private final int port;
	private final Runnable close;
	private static final int KEEPALIVE_TIME = 3000;
	private static final boolean use_graphics = false;

	public Client(IMangbandInputStream istream, IMangbandOutputStream ostream, MangClient mang, int port,
			Runnable close) {
		this.istream = istream;
		this.ostream = ostream;
		this.controller = mang;
		this.port = port;
		this.close = close;
	}

	/**
	 * Returns true if client is ready
	 *
	 * @return truth value for ready
	 */
	public boolean ready() {
		return (istream.ready());
	}

	/**
	 * Initialise client for login
	 *
	 * @param name
	 *            display unix login name inside client
	 * @param nick
	 *            character name
	 * @param hostname
	 *            display hostname inside client
	 */
	public void init_login(String name, String nick, String hostname) {
		try {
			ostream.writeInt(magic);

			ostream.writeString(name);
			ostream.writeShort(port);
			ostream.writeByte((byte)0xff);

			ostream.writeString(nick);
			ostream.writeString(hostname);
			ostream.writeShort(version);

			ostream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handle the login packet reply
	 *
	 * @return true on successful login
	 */
	public boolean login_reply() {
		try {
			int reply, status, port;
			reply = istream.readByte();
			status = istream.readByte();
			port = istream.readInt();

			if (status == STAT_SUCCESS) {
				if (reply == 254) {
					System.out.println("Lag meter enabled");
				}
				return true;
			} else {
				switch (status) {
				case STAT_VERSION:
					System.out.println("Incorrect version");
					break;
				case STAT_TWO_PLAYERS:
					System.out.println("Two players");
					break;
				case STAT_IN_USE:
					System.out.println("In Use");
					break;
				case STAT_SOCKET:
					System.out.println("Socket");
					break;
				case STAT_NEED_INFO:
					controller.req_info();
					break;
				case STAT_INVAL:
					System.out.println("Invalid input");
					break;
				default:
					System.out.println("Something else");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Verify login
	 *
	 * @param real
	 *            user's real name
	 * @param nick
	 *            character name
	 * @param pass
	 *            password
	 * @param sex
	 *            sex of character
	 * @param race
	 *            race of character
	 * @param characterClass
	 *            character class
	 * @return success
	 */
	public boolean login_verify(String real, String nick, String pass, int sex, int race, int characterClass) {
		try {
			ostream.writeByte((byte)ClientPacket.VERIFY);
			ostream.writeString(real);
			ostream.writeString(nick);
			ostream.writeString(pass);
			ostream.writeShort(sex);
			ostream.writeShort(race);
			ostream.writeShort(characterClass);

			ostream.writeByte((byte)'X');
			ostream.writeByte((byte)'X');

			ostream.writeShort(12 + 64 + (MAX_UNKNOWN + MAX_FEATURE + MAX_OBJECT + MAX_MONSTER + 4) * 2);

			send_statorder();
			send_options();
			send_unknown_opts();
			send_feature_opts(use_graphics);
			send_object_opts(use_graphics);
			send_monster_opts(use_graphics);

			ostream.flush();

			int reply = istream.readByte();

			if (reply == ClientPacket.REPLY) {
				if (check_reply(ClientPacket.VERIFY) == ClientPacket.SUCCESS) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private int check_reply(int packetType) {
		try {
			int type;
			int result;
			type = istream.readByte();
			result = istream.readByte();

			if (type == packetType) {
				return result;
			}
			System.out.println("Incorrect reply packet: " + type);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ClientPacket.FAILURE;
	}

	private void send_statorder() {
		try {
			for (int i = 0; i < 6; i++) {
				ostream.writeShort(controller.getStatOrder(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send_options() {
		Options opts = new Options();
		try {
			for (int i = 0; i < 64; i++) {
				ostream.writeByte((byte)(opts.defaults[i] ? 1 : 0));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send_unknown_opts() {
		try {
			ostream.writeShort(MAX_UNKNOWN);
			for (int i = 0; i < MAX_UNKNOWN; i++) {
				ostream.writeShort(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send_opts(boolean use_graphics, int size, char map_char) {
		try {
			ostream.writeShort(size);
			
			PreferenceLoader loader = new PreferenceLoader();

			if (use_graphics) {
				byte[] gfxMap = loader.readGraphicsMap(map_char, size);

				ostream.writeGraphics(gfxMap);
			} else {
				for (int i = 0; i < size; i++) {
					ostream.writeShort(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send feature graphics remappings to server
	 */
	private void send_feature_opts(boolean use_graphics) {
		send_opts(use_graphics, MAX_FEATURE, 'F');
	}

	/**
	 * Send object graphics remappings to server
	 */
	private void send_object_opts(boolean use_graphics) {
		send_opts(use_graphics, MAX_OBJECT, 'K');
	}

	/**
	 * Send monster graphics remappings to server
	 */
	private void send_monster_opts(boolean use_graphics) {
		send_opts(use_graphics, MAX_MONSTER, 'R');
	}

	public boolean read_magic() {
		try {
			byte type;
			int magic_n;
			type = (byte) istream.readByte();
			magic_n = istream.readInt();
			if (type == ClientPacket.MAGIC) {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void read_motd() {
		try {
			int motd_len;
			int fps;

			motd_len = istream.readInt();
			fps = istream.readShort();

			byte motd[] = istream.readMotd(motd_len);

			for (int i = 0; i < motd_len / 80; i++) {
				controller.display_motd(i, new String(motd, i * 80, 80));
			}

			controller.setFps(fps);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean send_play() {
		try {
			ostream.writeByte((byte)ClientPacket.PLAY);
			ostream.flush();

			byte reply;
			reply = (byte) istream.readByte();

			switch (reply) {
			case ClientPacket.QUIT:
				byte[] reason = istream.readQuitReason();
				System.out.println(new String(reason, 0, reason.length - 1));
				break;

			case ClientPacket.REPLY:
				return (check_reply(ClientPacket.PLAY) == ClientPacket.SUCCESS);

			default:
				System.out.println("Invalid packet received after play.");
				System.out.print(reply);
				dump_netbytes();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void receive() throws UnknownPacketException {
		byte pkt;

		try {
			pkt = istream.readByte();
			short packet = (short) (pkt & 0xff);

			switch (packet) {
			case ClientPacket.PARTY:
				receive_party();
				break;
			case ClientPacket.END:
				break;
			case ClientPacket.MESSAGE:
				receive_message();
				break;
			case ClientPacket.CHAR:
				receive_char();
				break;
			case ClientPacket.MINI_MAP:
				receive_line_info(false);
				break;
			case ClientPacket.LINE_INFO:
				receive_line_info(true);
				break;
			case ClientPacket.CHAR_INFO:
				receive_char_info();
				break;
			case ClientPacket.TITLE:
				receive_title();
				break;
			case ClientPacket.EXPERIENCE:
				receive_experience();
				break;
			case ClientPacket.GOLD:
				receive_gold();
				break;
			case ClientPacket.STAT:
				receive_stat();
				break;
			case ClientPacket.MAXSTAT:
				receive_maxstat();
				break;
			case ClientPacket.AC:
				receive_ac();
				break;
			case ClientPacket.HP:
				receive_hp();
				break;
			case ClientPacket.SP:
				receive_sp();
				break;
			case ClientPacket.DEPTH:
				receive_depth();
				break;
			case ClientPacket.FOOD:
				receive_food();
				break;
			case ClientPacket.MONSTER_HEALTH:
				receive_monster_health();
				break;
			case ClientPacket.HISTORY:
				receive_history();
				break;
			case ClientPacket.OBJFLAGS:
				receive_objflags();
				break;
			case ClientPacket.VARIOUS:
				receive_various();
				break;
			case ClientPacket.PLUSSES:
				receive_plusses();
				break;
			case ClientPacket.SKILLS:
				receive_skills();
				break;
			case ClientPacket.CUT:
				receive_cut();
				break;
			case ClientPacket.STUN:
				receive_stun();
				break;
			case ClientPacket.STATE:
				receive_state();
				break;
			case ClientPacket.BLIND:
				receive_blind();
				break;
			case ClientPacket.CONFUSED:
				receive_confused();
				break;
			case ClientPacket.FEAR:
				receive_fear();
				break;
			case ClientPacket.POISON:
				receive_poison();
				break;
			case ClientPacket.SPEED:
				receive_speed();
				break;
			case ClientPacket.STUDY:
				receive_study();
				break;
			case ClientPacket.INVEN:
				receive_inven();
				break;
			case ClientPacket.EQUIP:
				receive_equip();
				break;
			case ClientPacket.QUIT:
				receive_quit();
				break;
			case ClientPacket.KEEPALIVE:
				receive_keepalive();
				break;
			case ClientPacket.SOUND:
				receive_sound();
				break;
			case ClientPacket.SPELL_INFO:
				receive_spell_info();
				break;
			case ClientPacket.FLOOR:
				receive_floor();
				break;
			case ClientPacket.STORE:
				receive_store();
				break;
			case ClientPacket.STORE_INFO:
				receive_store_info();
				break;
			case ClientPacket.PLAYER_STORE_INFO:
				receive_player_store_info();
				break;
			case ClientPacket.FLUSH:
				receive_flush();
				break;
			case ClientPacket.ITEM:
				receive_item();
				break;
			case ClientPacket.SPECIAL_OTHER:
				receive_special_other();
				break;
			case ClientPacket.CURSOR:
				receive_cursor();
				break;
			case ClientPacket.DIRECTION:
				receive_direction();
				break;
			case ClientPacket.PAUSE:
				receive_pause();
				break;
			case ClientPacket.SELL:
				receive_sell();
				break;
			case ClientPacket.SPECIAL_LINE:
				receive_special_line();
				break;
			case ClientPacket.TARGET_INFO:
				receive_target_info();
				break;
			default:
				System.out.println("Unknown packet: " + packet);
				dump_netbytes();
				throw new UnknownPacketException(packet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Debug method to output some packet trace
	 */
	private void dump_netbytes() {
		try {
			for (int i = 0; i < 16; i++) {
				byte pkt = istream.readByte();
				short packet = (short) (pkt & 0xff);
				System.out.print(packet + " ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
	}

	private void receive_level() throws IOException {
		System.out.println("fail");
	}

	private void receive_play() throws IOException {
	}

	private void receive_store() throws IOException {
		byte pos;
		byte attr;
		short weight;
		short num;
		int price;
		String name;

		pos = istream.readByte();
		attr = istream.readByte();
		weight = istream.readShort();
		num = istream.readShort();
		price = istream.readInt();
		name = istream.readString();

		controller.addShop(pos, new ShopItem(attr, weight, num, price, name));
	}

	private void receive_store_info() throws IOException {
		short store_num;
		short owner_num;
		short num_items;

		store_num = istream.readShort();
		owner_num = istream.readShort();
		num_items = istream.readShort();
		controller.setShop(num_items, store_num, owner_num);
	}

	private void receive_player_store_info() throws IOException {
		short store_num;
		String player_name;
		short num_items;

		store_num = istream.readShort();
		player_name = istream.readString();
		num_items = istream.readShort();
		controller.setPlayerShop(num_items, store_num, player_name);
	}

	private void receive_spell_info() throws IOException {
		short book;
		short line;
		String spell;

		book = istream.readShort();
		line = istream.readShort();
		spell = istream.readString();

		controller.addSpell(book, line, spell);
	}

	private void receive_sound() throws IOException {
		byte soundId;

		soundId = istream.readByte();
		controller.playSound(soundId);
	}

	private void receive_quit() throws IOException {
		String message;

		message = istream.readString();
		System.out.println("Quit: " + message);
		close.run();
	}

	private void receive_equip() throws IOException {
		byte attr;
		byte pos;
		short weight;
		byte tval;
		String name;

		pos = istream.readByte();
		attr = istream.readByte();
		weight = istream.readShort();
		tval = istream.readByte();
		name = istream.readString().trim();

		controller.setEquip(pos, attr, weight, tval, name);
	}

	private void receive_inven() throws IOException {
		byte attr;
		byte pos;
		short weight;
		short amount;
		byte tval;
		String name;

		pos = istream.readByte();
		attr = istream.readByte();
		weight = istream.readShort();
		amount = istream.readShort();
		tval = istream.readByte();
		name = istream.readString().trim();

		controller.setInventory(pos, attr, weight, amount, tval, name);
	}

	private void receive_study() throws IOException {
		byte study;

		study = istream.readByte();
		controller.setStudy(study);
	}

	private void receive_speed() throws IOException {
		short speed;

		speed = istream.readShort();
		controller.setSpeed(speed);
	}

	private void receive_state() throws IOException {
		short paralysed;
		short searching;
		short resting;

		paralysed = istream.readShort();
		searching = istream.readShort();
		resting = istream.readShort();

		controller.setState(paralysed, searching, resting);
	}

	private void receive_poison() throws IOException {
		byte poison;

		poison = istream.readByte();
		controller.setPoison(poison);
	}

	private void receive_fear() throws IOException {
		byte fear;

		fear = istream.readByte();
		controller.setFear(fear);
	}

	private void receive_confused() throws IOException {
		byte confused;

		confused = istream.readByte();
		controller.setConfused(confused);
	}

	private void receive_blind() throws IOException {
		byte blind;

		blind = istream.readByte();
		controller.setBlind(blind);
	}

	private void receive_stun() throws IOException {
		short stun;

		stun = istream.readShort();
		controller.setStun(stun);
	}

	private void receive_cut() throws IOException {
		short cut;

		cut = istream.readShort();
		controller.setCut(cut);
	}

	private void receive_skills() throws IOException {
		controller.getModel().setFighting(istream.readShort());
		controller.getModel().setMissile(istream.readShort());
		controller.getModel().setSaving(istream.readShort());
		controller.getModel().setStealth(istream.readShort());
		controller.getModel().setPerception(istream.readShort());
		controller.getModel().setSearching(istream.readShort());
		controller.getModel().setDisarming(istream.readShort());
		controller.getModel().setMagicDevice(istream.readShort());
		controller.getModel().setBlows(istream.readShort());
		controller.getModel().setShots(istream.readShort());
		controller.getModel().setInfravision(istream.readShort());
	}

	private void receive_plusses() throws IOException {
		short mod_dam;
		short mod_hit;

		mod_hit = istream.readShort();
		mod_dam = istream.readShort();
		controller.setMods(mod_hit, mod_dam);
	}

	private void receive_various() throws IOException {
		short height, weight;
		short age;
		short social_class;

		height = istream.readShort();
		weight = istream.readShort();
		age = istream.readShort();
		social_class = istream.readShort();

		controller.setCharStats(height, weight, age, social_class);
	}

	private void receive_objflags() throws IOException {
		short y;

		y = istream.readShort();

		for (int x = 0; x < 13; x++) {
			byte ch, attr;
			short rep;
			ch = istream.readByte();
			attr = istream.readByte();
			rep = 1;
			if ((attr & 0x40) != 0) {
				attr &= ~(0x40);
				rep = istream.readByte();
			}
			x += rep - 1;
		}
	}

	private void receive_history() throws IOException {
		short line;
		String hist_line;

		line = istream.readShort();
		hist_line = istream.readString();

		controller.setHistory(line, hist_line);
	}

	private void receive_monster_health() throws IOException {
		byte num, attr;

		num = istream.readByte();
		attr = istream.readByte();
	}

	private void receive_depth() throws IOException {
		short depth;
		depth = istream.readShort();
		controller.setDepth(depth);
	}

	private void receive_food() throws IOException {
		short food;
		food = istream.readShort();
		controller.setFood(food);
	}

	private void receive_party() throws IOException {
		byte party_bytes[] = new byte[160];

		int i = 0;
		do {
			party_bytes[i] = istream.readByte();
		} while (party_bytes[i++] != '\0');

		controller.setParty(new String(party_bytes, 0, i));
	}

	private void receive_message() throws IOException {
		String message = istream.readString();

		controller.addMessage(message);
	}

	private void receive_char() throws IOException {
		byte x, y, a, c;

		x = istream.readByte();
		y = istream.readByte();
		a = istream.readByte();
		c = istream.readByte();

		controller.setChar(c, a, x, y);
	}

	private void receive_line_info(boolean map) throws IOException {
		short y;
		byte ch, attr;
		byte rep;
		byte[] attrs, chars;

		attrs = new byte[80];
		chars = new byte[80];

		y = istream.readShort();

		for (int x = 0; x < 80; x++) {
			ch = istream.readByte();
			attr = istream.readByte();

			if ((attr & 0x40) != 0) {
				attr &= ~(0x40);
				rep = istream.readByte();
			} else {
				rep = 1;
			}

			if (ch != 0) {
				for (int i = 0; i < rep; i++) {
					attrs[x + i] = attr;
					chars[x + i] = ch;
				}
			}

			x += rep - 1;
		}
		controller.setLine(y, attrs, chars, map);
	}

	private void receive_char_info() throws IOException {
		short race, char_class, sex;

		race = istream.readShort();
		char_class = istream.readShort();
		sex = istream.readShort();

		controller.setCharInfo(race, char_class, sex);
	}

	private void receive_title() throws IOException {
		String title;

		title = istream.readString().trim();
		controller.setTitle(title);
	}

	private void receive_experience() throws IOException {
		short level;
		int max, cur, adv;

		level = istream.readShort();
		max = istream.readInt();
		cur = istream.readInt();
		adv = istream.readInt();

		controller.setExperience(level, max, cur, adv);
	}

	private void receive_gold() throws IOException {
		int gold;
		gold = istream.readInt();
		controller.setGold(gold);
	}

	private void receive_stat() throws IOException {
		byte stat;
		short max, cur;

		stat = istream.readByte();
		max = istream.readShort();
		cur = istream.readShort();

		controller.setStat(stat, max, cur);
	}

	private void receive_maxstat() throws IOException {
		byte stat;
		short max;

		stat = istream.readByte();
		max = istream.readShort();
		controller.setMaxStat(stat, max);
	}

	private void receive_ac() throws IOException {
		short base, plus;

		base = istream.readShort();
		plus = istream.readShort();
		controller.setAC(base, plus);
	}

	private void receive_hp() throws IOException {
		short max, cur;

		max = istream.readShort();
		cur = istream.readShort();
		controller.setHP(max, cur);
	}

	private void receive_sp() throws IOException {
		short max, cur;

		max = istream.readShort();
		cur = istream.readShort();
		controller.setSP(max, cur);
	}

	/**
	 * Maintain client connection. Send keepalive packets if time interval
	 * expires.
	 */
	public void maintain() {
		Date now = new Date();
		int interval;

		interval = (int) (now.getTime() - ostream.getLastSent().getTime());

		try {
			if (interval > KEEPALIVE_TIME) {
				send_keepalive();
			}
			ostream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			close.run();
		}
	}

	/**
	 * Send keepalive to the server.
	 */
	private void send_keepalive() throws IOException {
		byte pkt = ClientPacket.KEEPALIVE;
		ostream.writeByte(pkt);
		ostream.writeInt(++seq);
	}

	/**
	 * Handle a keepalive packet
	 */
	private void receive_keepalive() throws IOException {
		int time;
		int num;

		num = istream.readInt();

		if (num != seq) {
			System.out.println("Got bad keepalive reply: " + num + " expected " + seq);
		}
	}

	/**
	 * Move in the specified direction.
	 *
	 * @param direction
	 *            direction in which to move
	 * @param run
	 *            whether or not to run
	 */
	public void send_move(int direction, boolean run) {
		sendDirectionalCommand(direction, run ? ClientPacket.RUN : ClientPacket.WALK);
	}

	/**
	 * Send map request to the server.
	 */
	public void send_map() {
		try {
			ostream.writeByte((byte) ClientPacket.MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendDirectionalCommand(int direction, int command) {
		try {
			ostream.writeByte((byte) command);
			ostream.writeByte((byte) direction);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Locate user on the main map and permit paging.
	 *
	 * @param direction
	 *            direction to locate
	 */
	public void send_locate(char direction) {
		sendDirectionalCommand(direction, ClientPacket.LOCATE);
	}

	/**
	 * Look at interesting spots on the map.
	 *
	 * @param direction
	 *            direction in which to look
	 */
	public void send_look(char direction) {
		sendDirectionalCommand(direction, ClientPacket.LOOK);
	}

	public void send_stay() {
		try {
			ostream.writeByte((byte) ClientPacket.STAND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_go_down() {
		try {
			ostream.writeByte((byte) ClientPacket.GO_DOWN);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_go_up() {
		try {
			ostream.writeByte((byte) ClientPacket.GO_UP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_chat(String message) {
		try {
			ostream.writeByte((byte) ClientPacket.MESSAGE);
			ostream.writeString(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Receives info about items on which the player stands.
	 *
	 * @throws IOException
	 */
	private void receive_floor() throws IOException {
		byte floor_tval;
		floor_tval = istream.readByte();
		controller.setFloor(floor_tval);
	}

	private void receive_flush() {
	}

	/**
	 * Send request to wield the indexed item.
	 *
	 * @param index
	 *            index of inventory item
	 */
	public void send_wield(short index) {
		try {
			ostream.writeByte((byte) ClientPacket.WIELD);
			ostream.writeShort(index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_read(char ch) {
		try {
			ostream.writeByte((byte) ClientPacket.READ);
			ostream.writeShort((short) ch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_quaff(char ch) {
		try {
			ostream.writeByte((byte) ClientPacket.QUAFF);
			ostream.writeShort((short) ch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_cast(int index, int spell) {
		try {
			ostream.writeByte((byte) ClientPacket.SPELL);
			ostream.writeShort(index);
			ostream.writeShort(spell);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_pray(int index, int prayer) {
		try {
			ostream.writeByte((byte) ClientPacket.PRAY);
			ostream.writeShort(index);
			ostream.writeShort(prayer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_ghost(int power) {
		try {
			ostream.writeByte((byte) ClientPacket.GHOST);
			ostream.writeShort(power);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_remove(char ch) {
		try {
			ostream.writeByte((byte) ClientPacket.TAKE_OFF);
			ostream.writeShort((short) ch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_destroy(int index, int amount) {
		try {
			ostream.writeByte((byte) ClientPacket.DESTROY);
			ostream.writeShort((short) index);
			ostream.writeShort((short) amount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_eat(char ch) {
		try {
			ostream.writeByte((byte) ClientPacket.EAT);
			ostream.writeShort((short) ch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_fill(char ch) {
		try {
			ostream.writeByte((byte) ClientPacket.FILL);
			ostream.writeShort((short) ch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_drop(int index, int amount) {
		try {
			ostream.writeByte((byte) ClientPacket.DROP);
			ostream.writeShort((short) index);
			ostream.writeShort((short) amount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receive_item() {
		controller.ask_item();
	}

	public void send_item(char ch) {
		try {
			ostream.writeByte((byte) ClientPacket.ITEM);
			ostream.writeShort((short) ch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receive_special_other() throws IOException {
		String line;

		line = istream.readString();
		System.out.println(line);
	}

	private void receive_cursor() throws IOException {
		byte vis;
		byte x, y;

		vis = istream.readByte();
		x = istream.readByte();
		y = istream.readByte();

		controller.setCursor(x, y, (vis != 0));
	}

	private void receive_direction() {
		controller.getDirection();
	}

	public void send_direction(char ch) {
		try {
			ostream.writeByte((byte) ClientPacket.DIRECTION);
			ostream.writeByte((byte) ch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receive_pause() {
		controller.setPause();
	}

	public void send_store_leave() {
		try {
			ostream.writeByte((byte) ClientPacket.STORE_LEAVE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Purchase an amount of the specified item
	 *
	 * @param item
	 * @param amount
	 */
	public void send_purchase(int item, int amount) {
		try {
			ostream.writeByte((byte) ClientPacket.PURCHASE);
			ostream.writeShort(item);
			ostream.writeShort(amount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sell an amount of the specified item
	 *
	 * @param item
	 * @param amount
	 */
	public void send_sell(int item, int amount) {
		try {
			ostream.writeByte((byte) ClientPacket.SELL);
			ostream.writeShort(item);
			ostream.writeShort(amount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receive_sell() throws IOException {
		int price = istream.readInt();
		controller.confirm_sell(price);
	}

	public void send_confirm_sell() {
		try {
			ostream.writeByte((byte) ClientPacket.STORE_CONFIRM);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Disarm a trap
	 *
	 * @param direction
	 *            direction to disarm
	 */
	public void send_disarm(char direction) {
		sendDirectionalCommand(direction, ClientPacket.DISARM);
	}

	public void send_search() {
		try {
			ostream.writeByte((byte) ClientPacket.SEARCH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_setsearch() {
		try {
			ostream.writeByte((byte) ClientPacket.SEARCH_MODE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_special_line(int fileType, int line) {
		try {
			ostream.writeByte((byte) ClientPacket.SPECIAL_LINE);
			ostream.writeByte((byte) fileType);
			ostream.writeShort(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receive_special_line() throws IOException {
		short max;
		short line;
		byte attr;
		String string;

		max = istream.readShort();
		line = istream.readShort();
		attr = istream.readByte();
		string = istream.readString();

		controller.display_file(line, max, string, attr);
	}

	public void send_rest() {
		try {
			ostream.writeByte((byte) ClientPacket.REST);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_tunnel(int direction) {
		sendDirectionalCommand(direction, ClientPacket.TUNNEL);
	}

	public void send_quit() {
		try {
			ostream.writeByte((byte) ClientPacket.QUIT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_gain(int index, int spell) {
		try {
			ostream.writeByte((byte) ClientPacket.GAIN);
			ostream.writeShort(index);
			ostream.writeShort(spell);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_dropgold(int amount) {
		try {
			ostream.writeByte((byte) ClientPacket.DROP_GOLD);
			ostream.writeInt(amount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_close(char direction) {
		sendDirectionalCommand(direction, ClientPacket.CLOSE);
	}

	public void send_open(char direction) {
		sendDirectionalCommand(direction, ClientPacket.OPEN);
	}

	public void send_zap(int index) {
		try {
			ostream.writeByte((byte) ClientPacket.ZAP);
			ostream.writeShort(index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_use(int index) {
		try {
			ostream.writeByte((byte) ClientPacket.USE);
			ostream.writeShort(index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_suicide() {
		try {
			ostream.writeByte((byte) ClientPacket.SUICIDE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send_shoot_command(int index, byte direction, byte command) {
		try {
			ostream.writeByte(command);
			ostream.writeByte(direction);
			ostream.writeShort(index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_aim(int index, int direction) {
		try {
			ostream.writeByte((byte)ClientPacket.AIM_WAND);
			ostream.writeShort(index);
			ostream.writeByte((byte)direction);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_throw(int index, int direction) {
		send_shoot_command(index, (byte)direction, (byte)ClientPacket.THROW);
	}

	public void send_fire(int index, int direction) {
		send_shoot_command(index, (byte)direction, (byte)ClientPacket.FIRE);
	}

	public void send_inscribe(int index, String buf) {
		send_string_command(index, buf, (byte) ClientPacket.INSCRIBE);
	}

	private void send_string_command(int index, String buf, byte inscribe) {
		try {
			ostream.writeByte(inscribe);
			ostream.writeShort(index);
			ostream.writeString(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_uninscribe(int index) {
		try {
			ostream.writeByte((byte) ClientPacket.UNINSCRIBE);
			ostream.writeShort(index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send_create_party(String name) {
		send_party(PartyPacket.CREATE, name);
	}

	private void send_leave_party(String name) {
		send_party(PartyPacket.LEAVE, "");
	}

	private void send_party(int command, String buf) {
		send_string_command(command, buf, (byte) ClientPacket.PARTY);
	}

	public void send_target(int direction) {
		sendDirectionalCommand(direction, ClientPacket.TARGET);
	}

	private void receive_target_info() throws IOException {
		byte x, y;
		String buf;

		x = istream.readByte();
		y = istream.readByte();
		buf = istream.readString();

		controller.setTarget(buf, x, y);
	}

	/**
	 * Activate an item
	 *
	 * @param index
	 *            index of item
	 */
	public void send_activate(int index) {
		try {
			ostream.writeByte((byte) ClientPacket.ACTIVATE);
			ostream.writeShort((short) index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Bash a door
	 *
	 * @param direction
	 *            direction to bash
	 */
	private void send_bash(char direction) {
		sendDirectionalCommand(direction, ClientPacket.BASH);
	}
}
