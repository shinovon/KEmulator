/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package emulator;

import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection;
import java.util.List;
import java.util.Map;

public class Connections {

	// TODO ui

	public static void open(String url, Connection connection) {
		if (connection != null) {
			Emulator.getEmulator().getLogStream().println("Connection opened: " + url);
		} else {
			Emulator.getEmulator().getLogStream().println("Connection failed: " + url);
		}
	}

	public static synchronized void httpRequest(HttpConnection connection, Map<String, List<String>> headers) {
		if (Settings.traceHttp) {
			Emulator.getEmulator().getLogStream().println("HTTP request: " + connection.getURL());
			if (headers.get(null) != null) {
				for (String value : headers.get(null)) {
					Emulator.getEmulator().getLogStream().println(value);
				}
			}

			for (String key : headers.keySet()) {
				if (key == null) {
					continue;
				}
				for (String value : headers.get(key)) {
					Emulator.getEmulator().getLogStream().println(key + ": " + value);
				}
			}
			Emulator.getEmulator().getLogStream().println();
		}
	}

	public static synchronized void httpResponse(HttpConnection connection, Map<String, List<String>> headers) {
		if (Settings.traceHttp) {
			Emulator.getEmulator().getLogStream().println("HTTP response: " + connection.getURL());
			if (headers.get(null) != null) {
				for (String value : headers.get(null)) {
					Emulator.getEmulator().getLogStream().println(value);
				}
			}

			for (String key : headers.keySet()) {
				if (key == null) {
					continue;
				}
				for (String value : headers.get(key)) {
					Emulator.getEmulator().getLogStream().println(key + ": " + value);
				}
			}
			Emulator.getEmulator().getLogStream().println();
		}
	}

}
