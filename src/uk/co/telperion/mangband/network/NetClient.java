package uk.co.telperion.mangband.network;

import java.io.IOException;
import java.net.Socket;

import uk.co.telperion.mangband.input.MangClientImpl;

public class NetClient {
	private Socket clientSocket;

	private MangbandOutputStream ostream;
	private MangbandInputStream istream;
	private boolean connected = false;
	
	public NetClient(String hostname, short port) {
		connected = connect(hostname, port);		
	}

	public Client createClient(MangClientImpl mang)
	{
		return new Client(istream, ostream, mang, clientSocket.getPort(), this::close);
	}
	
	/**
	 * Returns true if client is connected
	 *
	 * @return truth value for connection
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * Connect function
	 *
	 * @param hostname
	 *          hostname to resolve and connect
	 * @param port
	 *          port on which to connect
	 * @return true on success
	 */
	private boolean connect(String hostname, short port) {
		try {
			clientSocket = new Socket(hostname, port);
			ostream = new MangbandOutputStream(clientSocket.getOutputStream());
			istream = new MangbandInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	/**
	 * Close the connection
	 */
	public void close() {
		try {
			clientSocket.close();
			connected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}