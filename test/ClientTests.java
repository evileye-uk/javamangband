// Client tests

import org.junit.Test;
import uk.co.telperion.mangband.input.MangClient;
import uk.co.telperion.mangband.network.*;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class ClientTests {
    private IMangbandInputStream istream;
    private IMangbandOutputStream ostream;
    private MangClient mang;
    private Client client;

    public ClientTests() {
        istream = mock(IMangbandInputStream.class);
        ostream = mock(IMangbandOutputStream.class);
        mang = mock(MangClient.class);
        client = new Client(istream, ostream, mang, 1234, this::close);
    }

    void close() {
    }

    @Test
    public void testReceiveMessage() throws UnknownPacketException, IOException {
        String message = "test";
        when(istream.readByte()).thenReturn((byte) ClientPacket.MESSAGE);
        when(istream.readString()).thenReturn(message);

        client.receive();

        verify(mang, times(1)).addMessage(message);
    }

    @Test
    public void testReceiveStudy() throws UnknownPacketException, IOException {
        final byte study = 1;
        when(istream.readByte()).thenReturn((byte) ClientPacket.STUDY).thenReturn(study);

        client.receive();

        verify(mang, times(1)).setStudy(study);
    }

    @Test
    public void testReceiveSpeed() throws UnknownPacketException, IOException {
        when(istream.readByte()).thenReturn((byte) ClientPacket.SPEED);
        when(istream.readShort()).thenReturn((short)3);

        client.receive();

        verify(mang, times(1)).setSpeed((short)3);
    }

    @Test
    public void testReceiveStun() throws UnknownPacketException, IOException {
        when(istream.readByte()).thenReturn((byte) ClientPacket.STUN);
        when(istream.readShort()).thenReturn((short)3);

        client.receive();

        verify(mang, times(1)).setStun((short)3);
    }

    @Test
    public void testReceiveCut() throws UnknownPacketException, IOException {
        when(istream.readByte()).thenReturn((byte) ClientPacket.CUT);
        when(istream.readShort()).thenReturn((short)3);

        client.receive();

        verify(mang, times(1)).setCut((short)3);
    }

    @Test
    public void testReceiveBlind() throws UnknownPacketException, IOException {
        when(istream.readByte()).thenReturn((byte) ClientPacket.BLIND).thenReturn((byte)3);

        client.receive();

        verify(mang, times(1)).setBlind((byte)3);
    }

    @Test
    public void testReceiveConfused() throws UnknownPacketException, IOException {
        when(istream.readByte()).thenReturn((byte) ClientPacket.CONFUSED).thenReturn((byte)3);

        client.receive();

        verify(mang, times(1)).setConfused((byte)3);
    }

    @Test
    public void testReceiveFear() throws UnknownPacketException, IOException {
        when(istream.readByte()).thenReturn((byte) ClientPacket.FEAR).thenReturn((byte)3);

        client.receive();

        verify(mang, times(1)).setFear((byte)3);
    }

    @Test
    public void testReceivePoison() throws UnknownPacketException, IOException {
        when(istream.readByte()).thenReturn((byte) ClientPacket.POISON).thenReturn((byte)3);

        client.receive();

        verify(mang, times(1)).setPoison((byte)3);
    }

    @Test
    public void testReceiveHitPoints() throws UnknownPacketException, IOException {
        final short max = 51;
        final short cur = 33;
        when(istream.readByte()).thenReturn((byte) ClientPacket.HP);
        when(istream.readShort()).thenReturn(max).thenReturn(cur);

        client.receive();

        verify(mang, times(1)).setHP(max, cur);
    }

    @Test
    public void testReceiveMana() throws UnknownPacketException, IOException {
        final short max = 51;
        final short cur = 33;
        when(istream.readByte()).thenReturn((byte) ClientPacket.SP);
        when(istream.readShort()).thenReturn(max).thenReturn(cur);

        client.receive();

        verify(mang, times(1)).setSP(max, cur);
    }

    @Test
    public void testReceiveTitle() throws UnknownPacketException, IOException {
        final String title = "title";
        when(istream.readByte()).thenReturn((byte) ClientPacket.TITLE);
        when(istream.readString()).thenReturn(title);

        client.receive();

        verify(mang, times(1)).setTitle(title);
    }

    @Test
    public void testReceiveGold() throws UnknownPacketException, IOException {
        final int gold = 123;
        when(istream.readByte()).thenReturn((byte) ClientPacket.GOLD);
        when(istream.readInt()).thenReturn(gold);

        client.receive();

        verify(mang, times(1)).setGold(gold);
    }
}
