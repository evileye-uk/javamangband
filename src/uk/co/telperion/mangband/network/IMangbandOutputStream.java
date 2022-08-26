package uk.co.telperion.mangband.network;

import java.io.Flushable;
import java.io.IOException;
import java.util.Date;

public interface IMangbandOutputStream extends Flushable {
    void writeString(String s) throws IOException;

    void writeInt(int n) throws IOException;

    void writeShort(int n) throws IOException;

    void writeByte(byte n) throws IOException;

    Date getLastSent();

    void writeGraphics(byte[] gfxMap) throws IOException;
}
