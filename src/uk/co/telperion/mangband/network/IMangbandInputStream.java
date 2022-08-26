package uk.co.telperion.mangband.network;

import java.io.IOException;

public interface IMangbandInputStream {
    String readString() throws IOException;

    int readInt() throws IOException;

    short readShort() throws IOException;

    byte readByte() throws IOException;

    byte[] readMotd(int length) throws IOException;

    byte[] readQuitReason() throws IOException;

    boolean ready();
}
