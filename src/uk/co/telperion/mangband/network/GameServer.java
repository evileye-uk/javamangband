/*
 * GameServer.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.network;

/**
 * Store information about a running server
 *
 * @author evileye
 */
public class GameServer {

  private final String hostname;
  private final short port;
  private final String details;

  /**
   * Constructor
   *
   * @param hostname host name of the server
   * @param port     port number of the server
   * @param details  details about version, etc.
   */
  public GameServer(String hostname, short port, String details) {
    this.hostname = hostname;
    this.port = port;
    this.details = details;
  }

  /**
   * Returns port number
   *
   * @return port number of server
   */
  public short getPort() {
    return port;
  }

  /**
   * Returns server details
   *
   * @return a string containing server details
   */
  public String getDetails() {
    return details;
  }

  /**
   * Returns host name
   *
   * @return host name of server
   */
  public String getHostname() {
    return hostname;
  }
}
