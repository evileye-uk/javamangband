/*
 * MangbandOutputStream.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.network;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Output stream for network handling
 *
 * @author evileye
 */
class MangbandOutputStream extends BufferedOutputStream implements IMangbandOutputStream {

  private Date lastSent;

  /**
   * Create mangband output stream from the given OutputStream
   *
   * @param ostream output stream to use
   */
  public MangbandOutputStream(OutputStream ostream) {
    super(ostream);
    lastSent = new Date();
  }

  /**
   * Write nul terminated string to the network
   *
   * @param s string value to be written
   * @throws IOException
   */
  public void writeString(String s) throws IOException {
    write(s.getBytes());
    write(0x00);
    lastSent = new Date();
  }

  /**
   * Write 4-byte integer to network
   *
   * @param n integer value to be written
   * @throws IOException
   */
  public void writeInt(int n) throws IOException {
    write(n >> 24);
    write(n >> 16);
    write(n >> 8);
    write(n);
    lastSent = new Date();
  }

  /**
   * Write 2-byte integer to network
   *
   * @param n short value to be written
   * @throws IOException
   */
  public void writeShort(int n) throws IOException {
    write(n >> 8);
    write(n);
    lastSent = new Date();
  }

  /**
   * Write single byte to the network
   *
   * @param n byte to be written
   * @throws IOException
   */
  public void writeByte(byte n) throws IOException {
    write(n);
    lastSent = new Date();
  }

  /**
   * Return the last time any data was sent out on the
   * socket.
   *
   * @return the most recent time data was sent
   */
  public Date getLastSent() {
    return lastSent;
  }

  @Override
  public void writeGraphics(byte[] gfxMap) throws IOException {

  }
}
