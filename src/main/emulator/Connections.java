/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package emulator;

import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection;

public class Connections {

	public static void open(String url, Connection connection) {
		if (connection != null) {
			Emulator.getEmulator().getLogStream().println("Connection opened: " + url + ", " + connection);
		} else {
			Emulator.getEmulator().getLogStream().println("Connection failed: " + url);
		}
		// TODO
	}

	public static void httpRequest(HttpConnection connection) {
		// TODO
	}

	public static void httpResponse(HttpConnection connection) {
		// TODO
	}

}
