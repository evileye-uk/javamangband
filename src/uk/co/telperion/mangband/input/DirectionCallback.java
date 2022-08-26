/*
 * DirectionCallback.java
 *
 * (c) Copyright 2011-2022 by Phoebe Smith, All Rights Reserved
 *
 */

package uk.co.telperion.mangband.input;

/**
 * Callback for direction requests
 *
 * @author evileye
 */
public abstract class DirectionCallback extends InputCallback {

  private final boolean has_target;
	private final boolean one_shot;
	
	public DirectionCallback(CallbackUser client, boolean has_target)
	{
		this(client, has_target, true);
	}
	
  public DirectionCallback(CallbackUser client, boolean has_target, boolean one_shot) {
    super(client);
    this.has_target = has_target;
    this.one_shot = one_shot;
  }

  /**
   * Update function to receive user direction input
   *
   * @param direction character containing direction info
   */
  public void update(char direction) {
    if (direction != 5 || has_target) {
      if (direction == 5) {
        client.send_target(5);
      }
      update_dir(direction);
      client.resetTarget();
      if(one_shot)
      {
      	cancel();
      }
    } else {
      client.send_target(0);
    }
  }

  /**
   * Update function to pass back direction details
   *
   * @param direction character containing direction info
   */
  public abstract void update_dir(char direction);

  /**
   * Cancel function
   */
  public abstract void cancel();
}
