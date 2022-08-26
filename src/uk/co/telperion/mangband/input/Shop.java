/*
 * Shop.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */
package uk.co.telperion.mangband.input;

public interface Shop {

	void nextShopPage();

	/**
	 * End shopping. Restore terminal and notify server
	 */
	void endShopping();

	void purchase();

	void init_sell();

}