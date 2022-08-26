/*
 * UnknownPacketException.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.network;

/**
 * Simple exception class for unknown packets
 *
 * @author evileye
 */
public class UnknownPacketException extends Exception {

  private static final long serialVersionUID = 1;
  private final int packet_type;

  /**
   * Construct unknown packet exception with the given type
   *
   * @param pack_type packet type which caused the exception
   */
  public UnknownPacketException(int packet_type) {
    this.packet_type = packet_type;
  }

  /**
   * Return exception message.
   *
   * @return the exception message string
   */
  @Override
  public String getMessage() {
    return ("Unknown packet type: " + packet_type);
  }
}
