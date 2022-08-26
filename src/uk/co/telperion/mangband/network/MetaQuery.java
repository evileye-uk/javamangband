/*
 * MetaQuery.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Meta-server query class
 *
 * @author evileye
 */
public class MetaQuery {

	private final ArrayList<GameServer> servers;

	public void parse(BufferedReader reader) {
		try {
			String line;
			String lastLine = null;
			while ((line = reader.readLine()) != null) {
				if (line.length() < 2) {
					continue;
				}

				if (line.charAt(1) == '%' && lastLine != null) {
					int spc = lastLine.indexOf(' ');
					String hostname = lastLine.substring(1, spc);
					String details = lastLine.substring(spc + 1);
					short port = Short.parseShort(line.substring(2));
					servers.add(new GameServer(hostname, port, details));
				}
				lastLine = line;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public MetaQuery() {
		servers = new ArrayList<GameServer>();
	}

	/**
	 * Obtain server iterator
	 *
	 * @return iterator for the list of servers
	 */
	public Iterator<GameServer> getServers() {
		return servers.iterator();
	}

	/**
	 * Returns the game server at a specified index
	 *
	 * @param index
	 *          the requested index
	 * @return the game server at the given index
	 */
	public GameServer getServer(int index) {
		return servers.get(index);
	}
}
