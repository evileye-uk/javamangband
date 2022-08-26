/*
 * ClientPacket.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.network;

/**
 * Packet type definitions
 *
 * @author evileye
 */
public final class ClientPacket {

  /**
   * Undefined packet; unused
   */
  public static final int UNDEFINED = 0;
  public static final int VERIFY = 1;
  public static final int REPLY = 2;
  public static final int PLAY = 3;
  public static final int QUIT = 4;
  public static final int LEAVE = 5;
  public static final int MAGIC = 6;
  public static final int RELIABLE = 7;
  public static final int ACK = 8;
  public static final int TALK = 9;
  public static final int START = 10;
  public static final int END = 11;
  public static final int KEEPALIVE = 12;
  public static final int MAXSTAT = 19;
  public static final int PLUSSES = 20;
  public static final int AC = 21;

  /**
   * Experience points data packet. Sent by the server to indicate a change
   * in the player's experience.
   */
  public static final int EXPERIENCE = 22;
  public static final int GOLD = 23;
  public static final int HP = 24;
  public static final int SP = 25;
  public static final int CHAR_INFO = 26;
  public static final int VARIOUS = 27;
  public static final int STAT = 28;
  public static final int HISTORY = 29;
  public static final int INVEN = 30;
  public static final int EQUIP = 31;
  public static final int TITLE = 32;
  public static final int LEVEL = 33;
  public static final int DEPTH = 34;
  public static final int FOOD = 35;
  public static final int BLIND = 36;
  public static final int CONFUSED = 37;
  public static final int FEAR = 38;
  public static final int POISON = 39;
  public static final int STATE = 40;
  public static final int LINE_INFO = 41;
  public static final int SPEED = 42;
  public static final int STUDY = 43;
  public static final int CUT = 44;
  public static final int STUN = 45;
  public static final int MESSAGE = 46;
  public static final int CHAR = 47;
  public static final int SPELL_INFO = 48;
  public static final int FLOOR = 49;
  public static final int SPECIAL_OTHER = 50;
  public static final int STORE = 51;
  public static final int STORE_INFO = 52;
  public static final int TARGET_INFO = 53;
  public static final int SOUND = 54;
  public static final int MINI_MAP = 55;
  public static final int SKILLS = 57;
  public static final int PAUSE = 58;
  public static final int MONSTER_HEALTH = 59;
  public static final int DIRECTION = 60;
  public static final int ITEM = 61;
  public static final int SELL = 62;
  public static final int PARTY = 63;
  public static final int SPECIAL_LINE = 64;
  public static final int PLAYER_STORE_INFO = 67;
  public static final int WALK = 70;
  public static final int RUN = 71;
  public static final int TUNNEL = 72;
  public static final int AIM_WAND = 73;
  public static final int DROP = 74;
  public static final int FIRE = 75;
  public static final int STAND = 76;
  public static final int DESTROY = 77;
  public static final int LOOK = 78;
  public static final int SPELL = 79;
  public static final int OPEN = 80;
  public static final int PRAY = 81;
  public static final int QUAFF = 82;
  public static final int READ = 83;
  public static final int SEARCH = 84;
  public static final int TAKE_OFF = 85;
  public static final int USE = 86;
  public static final int THROW = 87;
  public static final int WIELD = 88;
  public static final int ZAP = 89;
  public static final int TARGET = 90;
  public static final int INSCRIBE = 91;
  public static final int UNINSCRIBE = 92;
  public static final int ACTIVATE = 93;
  public static final int BASH = 94;
  public static final int DISARM = 95;
  public static final int EAT = 96;
  public static final int FILL = 97;
  public static final int LOCATE = 98;
  public static final int MAP = 99;
  public static final int SEARCH_MODE = 100;
  public static final int CLOSE = 103;
  public static final int GAIN = 104;
  public static final int GO_UP = 105;
  public static final int GO_DOWN = 106;
  public static final int PURCHASE = 107;
  public static final int STORE_LEAVE = 108;
  public static final int STORE_CONFIRM = 109;
  public static final int DROP_GOLD = 110;
  public static final int REST = 112;
  public static final int GHOST = 113;
  public static final int SUICIDE = 114;
  public static final int TARGET_FRIENDLY = 117;
  public static final int MASTER = 118;
  public static final int FAILURE = 121;
  public static final int SUCCESS = 122;
  public static final int CLEAR = 123;
  public static final int FLUSH = 150;
  public static final int CURSOR = 151;
  public static final int OBSERVE = 160;
  public static final int CHANGEPASS = 162;
  public static final int OBJFLAGS = 163;
}
