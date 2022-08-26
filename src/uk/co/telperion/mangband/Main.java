/*
 * Main.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband;

import uk.co.telperion.mangband.input.ClientFactory;
import uk.co.telperion.mangband.input.MangClient;
import uk.co.telperion.mangband.ui.MangbandTerm;

/**
 * Java client for Mangband
 *
 * @author evileye
 */
public class Main {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    String hostname = "";
    if (args.length >= 1) {
      hostname = args[0];
    }

    MangbandTerm term = new MangbandTerm();

    MangClient mangClient = ClientFactory.create(term, hostname);
    term.setVisible(true);
    term.init();

    if (mangClient.login()) {
      Thread mainThread = new Thread(mangClient);
      boolean quit = false;

      mainThread.start();

      while (!quit) {
        try {
          mainThread.join();
          quit = true;
        } catch (InterruptedException e) {
        }
      }
    }
    System.exit(0);
  }
}
