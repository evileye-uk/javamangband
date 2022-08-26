/*
 * PreferenceLoader.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Preference loader class
 *
 * @author evileye
 */
public class PreferenceLoader {

	public byte[] readGraphicsMap(char type, int size) {
		byte[] gfxMap = new byte[size * 2];

		try {
			String home = System.getProperty("user.home");
			String fileName = home + "/mangband/graf.prf";
			BufferedReader reader = new BufferedReader(new FileReader(fileName));

			String line;
			int num_gfx = 0;
			do {
				line = reader.readLine();
				if (line != null) {
					parse(line, type, num_gfx, gfxMap);
				}
			} while (line != null);

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return gfxMap;
	}

	private boolean parse(String line, char type, int num_gfx, byte[] gfxMap) {
		String parse = line.trim();

		if (parse.length() == 0 || parse.charAt(0) == '#') {
			return false;
		}

		if (parse.charAt(0) == type) {
			int n;
			byte x = 0;
			byte y = 0;

			int hashIdx = parse.indexOf('#', 0);
			if (hashIdx != -1) {
				parse = parse.substring(0, hashIdx).trim();
			}

			String[] args = parse.split(":", 9);

			if (args.length != 3) {
				return false;
			}

			n = Integer.parseInt(args[1]);

			if ((n * 2) >= gfxMap.length) {
				return false;
			}

			String[] xyPair = args[2].split("/", 3);
			if (xyPair.length != 2) {
				return false;
			}

			x = parseNumber(xyPair[0]);
			y = parseNumber(xyPair[1]);
			if (x == 0 || y == 0) {
				return false;
			}

			gfxMap[n * 2] = x;
			gfxMap[n * 2 + 1] = y;

			return true;
		}
		return false;
	}

	private byte parseNumber(String num) throws NumberFormatException {
		if (num.charAt(0) == '0' && num.charAt(1) == 'x') {
			return (byte) Integer.parseInt(num.substring(2), 16);
		}
		return (byte) Integer.parseInt(num);
	}
}
