package javax.microedition.io;

import javax.microedition.io.file.*;
import javax.wireless.messaging.*;

import emulator.sensor.*;
import emulator.*;

import com.sun.cdc.io.*;

import javax.microedition.sensor.*;
import java.io.*;

public class Connector {
	public static final int READ = 1;
	public static final int WRITE = 2;
	public static final int READ_WRITE = 3;

	private Connector() {
		super();
	}

	public static Connection open(final String s) throws IOException {
		return open(s, 3);
	}

	public static Connection open(final String s, final int n) throws IOException {
		return open(s, n, false);
	}

	public static Connection open(final String url, final int n, final boolean b) throws IOException {
		if (url.startsWith("resource:")) {
			return NetworkLog.created(url, new ResourceConnectionImpl(url));
		}
		if(url.startsWith("vserv:")) {
			return NetworkLog.created(url, new VServConnectionWrapper(url));
		}
		if (url.startsWith("file://") && !Settings.protectedPackages.contains("javax.microedition.io.file")) {
			Permission.checkPermission("connector.open.file");
			return NetworkLog.created(url, new FileConnectionImpl(url));
		}
		if (url.startsWith("sms://") && !Settings.protectedPackages.contains("javax.wireless.messaging")) {
			Permission.checkPermission("connector.open.sms");
			return NetworkLog.created(url, new MessageConnectionImpl(url));
		}
		if (url.startsWith("sensor:") && !Settings.protectedPackages.contains("javax.microedition.sensor")) {
			final SensorInfo[] sensors;
			if ((sensors = SensorManager.findSensors(url)).length > 0) {
				((SensorImpl) sensors[0]).method239();
				return (SensorConnection) sensors[0];
			}
			return null;
		} else {
			if (Settings.networkNotAvailable) {
				Emulator.getEmulator().getLogStream().println("MIDlet tried to open: " + url);
				NetworkLog.openFailed(url, "Blocked");
				throw new IOException("Network not available");
			}
			if (url.startsWith("http://")) {
				NetworkLog.checkOpen(url, "connector.open.http");
				return NetworkLog.created(url, checkVserv(url) ? new VServConnectionWrapper(url) : new HttpConnectionImpl(url));
			}
			if (url.startsWith("https://")) {
				NetworkLog.checkOpen(url, "connector.open.http");
				return NetworkLog.created(url, checkVserv(url) ? new VServConnectionWrapper(url) : new HttpConnectionImpl(url));
			}
			if (url.startsWith("socket://:")) {
				NetworkLog.checkOpen(url, "connector.open.serversocket");
				return NetworkLog.created(url, new ServerSocketImpl(url));
			}
			if (url.startsWith("socket://")) {
				NetworkLog.checkOpen(url, "connector.open.socket");
				return NetworkLog.created(url, new SocketConnectionImpl(url));
			}
			Connection openPrim = null;
			String protocol = "";
			if (url.indexOf(':') != -1) {
				protocol = url.substring(0, url.indexOf(':'));
			} else {
				NetworkLog.openFailed(url, "Unknown protocol");
				throw new ConnectionNotFoundException("unknown protocol: " + url);
			}
			try {
				openPrim = ((ConnectionBaseInterface) Class.forName("com.sun.cdc.io.j2me." + protocol + ".Protocol").newInstance()).openPrim(url.substring(url.indexOf(':') + 1), n, b);
			} catch (Exception ex) {
				NetworkLog.openFailed(url, "Unknown protocol");
				throw new ConnectionNotFoundException("unknown protocol: " + url);
			}
			return NetworkLog.created(url, openPrim);
		}
	}

	private static boolean checkVserv(String s) {
		return Settings.bypassVserv && (s.startsWith("http://a.vserv.mobi/") || (s.contains("vserv.mobi/") && s.contains("/adapi")));
	}

	public static DataInputStream openDataInputStream(final String s) throws IOException {
		final InputConnection inputConnection = (InputConnection) open(s, 1);
		return inputConnection.openDataInputStream();
	}

	public static DataOutputStream openDataOutputStream(final String s) throws IOException {
		final OutputConnection outputConnection = (OutputConnection) open(s, 2);
		return outputConnection.openDataOutputStream();
	}

	public static InputStream openInputStream(final String s) throws IOException {
		return openDataInputStream(s);
	}

	public static OutputStream openOutputStream(final String s) throws IOException {
		return openDataOutputStream(s);
	}
}
