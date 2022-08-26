package uk.co.telperion.mangband.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MetaQueryClient {
	private final short METASERVER_PORT = 8802;
	private final String METASERVER_HOSTNAME = "mangband.org";

	private final MetaQuery result;
	
	public MetaQueryClient() {
		result = new MetaQuery();

		try {
			Socket socket = new Socket(METASERVER_HOSTNAME, METASERVER_PORT);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			result.parse(reader);
			
			socket.close();
		} catch (IOException e) {
		}
	}
	
	public MetaQuery result()
	{
		return result;
	}
}
