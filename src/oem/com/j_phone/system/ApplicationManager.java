package com.j_phone.system;

public class ApplicationManager {

	private static ApplicationManager instance;

	public static ApplicationManager getInstance() {
		if (instance == null) {
			return instance = new ApplicationManager();
		}
		return instance;
	}

	public void setPausedTransitMenu(int n) {

	}

}
