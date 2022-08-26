/*
 * ClientFactory.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.input;

import uk.co.telperion.mangband.ui.MangbandTerm;

public class ClientFactory {
	public static MangClient create(MangbandTerm mainTerm, String hostname)
	{
		 return new MangClientImpl(mainTerm, hostname);
	}
}
