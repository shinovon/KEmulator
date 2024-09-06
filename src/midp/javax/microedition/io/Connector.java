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

	public static Connection open(final String s, final int n, final boolean b) throws IOException {
		if (s.startsWith("resource:")) {
			return new ResourceConnectionImpl(s);
		}
		if(s.startsWith("vserv:")) {
			return new VServConnectionWrapper(s);
		}
		if (s.startsWith("file://") && !Settings.protectedPackages.contains("javax.microedition.io.file")) {
			Permission.checkPermission("connector.open.file");
			return new FileConnectionImpl(s);
		}
		if (s.startsWith("sms://") && !Settings.protectedPackages.contains("javax.wireless.messaging")) {
			Permission.checkPermission("connector.open.sms");
			return new MessageConnectionImpl(s);
		}
		if (s.startsWith("sensor:") && !Settings.protectedPackages.contains("javax.microedition.sensor")) {
			final SensorInfo[] sensors;
			if ((sensors = SensorManager.findSensors(s)).length > 0) {
				((SensorImpl) sensors[0]).method239();
				return (SensorConnection) sensors[0];
			}
			return null;
		} else {
			if (Settings.networkNotAvailable) {
				Emulator.getEmulator().getLogStream().println("MIDlet tried to open: " + s);
				throw new IOException("Network not available");
			}
			if (s.startsWith("http://")) {
				Permission.checkPermission("connector.open.http");
				return checkVserv(s) ? new VServConnectionWrapper(s) : new HttpConnectionImpl(s);
			}
			if (s.startsWith("https://")) {
				Permission.checkPermission("connector.open.http");
				return checkVserv(s) ? new VServConnectionWrapper(s) : new HttpConnectionImpl(s);
			}
			if (s.startsWith("socket://:")) {
				Permission.checkPermission("connector.open.serversocket");
				return new ServerSocketImpl(s);
			}
			if (s.startsWith("socket://")) {
				Permission.checkPermission("connector.open.socket");
				return new SocketConnectionImpl(s);
			}
			Connection openPrim = null;
			String protocol = "";
			if (s.indexOf(':') != -1) {
				protocol = s.substring(0, s.indexOf(':'));
			} else {
				throw new ConnectionNotFoundException("unknown protocol: " + s);
			}
			try {
				openPrim = ((ConnectionBaseInterface) Class.forName("com.sun.cdc.io.j2me." + protocol + ".Protocol").newInstance()).openPrim(s.substring(s.indexOf(':') + 1), n, b);
			} catch (Exception ex) {
				throw new ConnectionNotFoundException("unknown protocol: " + s);
			}
			return openPrim;
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
