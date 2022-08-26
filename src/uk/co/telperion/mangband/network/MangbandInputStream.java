/*
 * MangbandInputStream.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream for network handling
 *
 * @author evileye
 */
class MangbandInputStream extends BufferedInputStream implements IMangbandInputStream {

  /**
   * Construct a mangband input stream from the given
   * InputStream
   *
   * @param ostream the input stream to use
   */
  public MangbandInputStream(InputStream ostream) {
    super(ostream);
  }

  /**
   * Read in a nul terminated series of bytes from
   * the socket and return a string.
   *
   * @return the string which has been read in
   * @throws IOException
   */
  public String readString() throws IOException {
    byte[] bytes = new byte[256];

    int ch;
    int i = 0;
    while ((ch = read()) != '\0') {
      bytes[i++] = (byte) (ch & 0xff);
    }

    return (new String(bytes, 0, i));
  }

  /**
   * Read integer data from stream
   * 
   * @param size the size of the integer to read
   * @return
   * @throws IOException
   */
  private int readInteger(int size) throws IOException {
    int n = 0;
    int q;
    
    for(int i=0; i<size; i++)
		{
			n = n << 8;
			q = read();
			if(q < 0) return q;
			n |= q;
		}
    return n;
  }
  
  /**
   * Read in a 4-byte int from the network.
   *
   * @return the integer value which has been read in
   * @throws IOException
   */
  public int readInt() throws IOException {
  	return readInteger(4);
  }
  
  /**
   * Read in a 2-byte int from the network.
   *
   * @return the short value which has been read in
   * @throws IOException
   */
  public short readShort() throws IOException {
  	return (short)readInteger(2);
  }

  /**
   * Read in a single byte from the network.
   *
   * @return the byte value which has been read in
   * @throws IOException
   */
  public byte readByte() throws IOException {
    int n = read();
    return (byte) n;
  }

  @Override
  public byte[] readMotd(int length) throws IOException {
    return new byte[0];
  }

  @Override
  public byte[] readQuitReason() throws IOException {
    return new byte[0];
  }

  /**
   * Returns true if client is ready
   *
   * @return truth value for ready
   */
  public boolean ready() {
    try {
      return (available() > 0);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }
}
