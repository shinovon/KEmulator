package javax.microedition.io;

import com.nttdocomo.io.maker.ScratchPadConnection;
import com.sun.cdc.io.ConnectionBaseInterface;
import emulator.Connections;
import emulator.Emulator;
import emulator.Permission;
import emulator.Settings;
import emulator.bluetooth.BluetoothBackend;
import emulator.bluetooth.BluetoothBackendProvider;
import emulator.sensor.Sensor;

import javax.bluetooth.BluetoothStateException;
import javax.microedition.io.file.FileConnectionImpl;
import javax.microedition.sensor.SensorConnection;
import javax.microedition.sensor.SensorInfo;
import javax.microedition.sensor.SensorManager;
import javax.wireless.messaging.MessageConnectionImpl;
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

	public static Connection open(String s, final int n, final boolean b) throws IOException {
		String origUrl = s;
		{
			// nokia
			int i = s.indexOf(";nokia_netid=");
			if (i != -1) {
				s = s.substring(0, i);
			}
			i = s.indexOf(";nokia_apnid=");
			if (i != -1) {
				s = s.substring(0, i);
			}
			i = s.indexOf(";nokia_timeout=");
			if (i != -1) {
				s = s.substring(0, i);
			}
			// blackberry
			i = s.indexOf(";interface=");
			if (i != -1) {
				s = s.substring(0, i);
			}
			i = s.indexOf(";deviceside=");
			if (i != -1) {
				s = s.substring(0, i);
			}
			//
			i = s.indexOf(";nokia_socket");
			if (i != -1) {
				s = s.substring(0, i);
			}
			i = s.indexOf(";j9proxy=");
			if (i != -1) {
				s = s.substring(0, i);
			}
		}
		if (s.startsWith("vserv:")) {
			return new VServConnectionWrapper(s);
		}
		Connection res = null;
		res: try {
			if (s.startsWith("resource:")) {
				res = new ResourceConnectionImpl(s);
			}
			if (s.startsWith("scratchpad:")) {
				res = ScratchPadConnection.open(s);
			}
			if (s.startsWith("file://") && !Settings.protectedPackages.contains("javax.microedition.io.file")) {
				Permission.checkPermission("connector.open.file");
				res = new FileConnectionImpl(s);
			}
			if (s.startsWith("sms://") && !Settings.protectedPackages.contains("javax.wireless.messaging")) {
				Permission.checkPermission("connector.open.sms");
				res = new MessageConnectionImpl(s);
			}
			if (s.startsWith("sensor:") && !Settings.protectedPackages.contains("javax.microedition.sensor")) {
				final SensorInfo[] sensors;
				if ((sensors = SensorManager.findSensors(s)).length > 0) {
					((Sensor) sensors[0]).open();
					res = (SensorConnection) sensors[0];
					break res;
				}
				res = null;
				break res;
			}
			if (Settings.networkNotAvailable) {
				throw new ConnectionNotFoundException("Network not available");
			}
			if (s.startsWith("http://")) {
				Permission.checkPermission("connector.open.http");
				if (Emulator.doja) {
					res = new com.nttdocomo.io.maker.HttpConnectionImpl(s, n);
					break res;
				}
				res = checkVserv(s) ? new VServConnectionWrapper(s) : new HttpConnectionImpl(s);
				break res;
			}
			if (s.startsWith("https://")) {
				Permission.checkPermission("connector.open.http");
				res = checkVserv(s) ? new VServConnectionWrapper(s) : new HttpConnectionImpl(s);
				break res;
			}
			if (s.startsWith("socket://:")) {
				Permission.checkPermission("connector.open.serversocket");
				res = new ServerSocketImpl(s);
				break res;
			}
			if (s.startsWith("socket://")) {
				Permission.checkPermission("connector.open.socket");
				res = new SocketConnectionImpl(s);
				break res;
			}
			if (s.startsWith("datagram://:")) {
				Permission.checkPermission("connector.open.datagramreceive");
				res = new UDPDatagramConnectionImpl(s);
				break res;
			}
			if (s.startsWith("datagram://")) {
				Permission.checkPermission("connector.open.datagram");
				res = new UDPDatagramConnectionImpl(s);
				break res;
			}
			if (s.startsWith("ssl://")) {
				Permission.checkPermission("connector.open.ssl");
				res = new SecureConnectionImpl(s);
				break res;
			}
			if (!Settings.protectedPackages.contains("javax.bluetooth")) {
				if (s.startsWith("btspp://") || s.startsWith("btl2cap://") || s.startsWith("btgoep://")) {
					Permission.checkPermission("connector.open.bt");
					try {
						BluetoothBackend backend =
								BluetoothBackendProvider.getInstance();
						res = s.contains("://localhost:") ? backend.openServerNotifier(s) : backend.openClientConnection(s);
						break res;
					} catch (BluetoothStateException bse) {
						throw new IOException("Bluetooth not available: " + bse);
					}
				}
			}
			String protocol;
			if (s.indexOf(':') != -1) {
				protocol = s.substring(0, s.indexOf(':'));
			} else {
				throw new ConnectionNotFoundException("unknown protocol: " + s);
			}
			try {
				res = ((ConnectionBaseInterface) Class.forName("com.sun.cdc.io.j2me." + protocol + ".Protocol").newInstance()).openPrim(s.substring(s.indexOf(':') + 1), n, b);
			} catch (Exception ex) {
				if (ex instanceof IOException) {
					throw (IOException) ex;
				}
				throw new ConnectionNotFoundException("unknown protocol: " + s, ex);
			}
		} finally {
			Connections.open(origUrl, res);
		}
		return res;
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
