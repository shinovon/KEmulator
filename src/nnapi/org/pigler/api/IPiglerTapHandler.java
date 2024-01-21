package org.pigler.api;

/**
 * Pigler notification tap handler
 * 
 * @author Shinovon
 */
public interface IPiglerTapHandler {

	/**
	 * Notification tap event
	 * 
	 * @param uid Notification id
	 */
	public void handleNotificationTap(int uid);
	
}
