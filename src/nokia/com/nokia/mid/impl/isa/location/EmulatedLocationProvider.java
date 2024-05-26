package com.nokia.mid.impl.isa.location;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

public class EmulatedLocationProvider extends LocationProvider {
	LocationListenerUpdater listenerThread;
	private static Vector locations;
	private static int currentLocation;
	private static EmulatedLocationProvider instance = null;

	public EmulatedLocationProvider() {
		currentLocation = 0;
		locations = new Vector();

		Criteria criteria = new Criteria();
		criteria.getHorizontalAccuracy();
		criteria.getPreferredPowerConsumption();
		criteria.getPreferredResponseTime();
		criteria.getVerticalAccuracy();
	}

	public static LocationProvider getInstance() {
		instance = new EmulatedLocationProvider();

		locations = new Vector();

		locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(1.0D, 1.0D, 3.0F, 10.0F, 20.0F)));

		locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(1.0D, 1.0D, 3.0F, 10.0F, 20.0F)));

		locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(2.0D, 2.0D, 3.0F, 10.0F, 20.0F)));

		locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(3.0D, 4.0D, 3.5F, 10.0F, 20.0F)));

		locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(4.0D, 5.0D, 3.6F, 10.0F, 20.0F)));

		locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(5.0D, 6.0D, 2.0F, 10.0F, 20.0F)));

		return instance;
	}

	public Location getLocation(int timeout) throws LocationException, InterruptedException {
		int newtime = 60;
		if ((timeout == 0) || (timeout < -1)) {
			throw new IllegalArgumentException();
		}
		if (timeout == -1) {
			timeout = newtime;
		} else {
			timeout = newtime;
		}

		locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(1.0D, 1.0D, 3.0F, 10.0F, 20.0F)));

		locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(1.0D, 1.0D, 3.0F, 10.0F, 20.0F)));

		locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(2.0D, 2.0D, 3.0F, 10.0F, 20.0F)));

		locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(3.0D, 4.0D, 3.0F, 10.0F, 20.0F)));

		locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(4.0D, 5.0D, 2.0F, 10.0F, 20.0F)));

		locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(5.0D, 6.0D, 2.0F, 10.0F, 20.0F)));

		return (Location) locations.elementAt(currentLocation++ % (locations.size() - 1));
	}

	public void setLocationListener(LocationListener listener, int interval, int timeout, int maxage) {

		try {
			if (this.listenerThread != null) {
				this.listenerThread.abort();
			}
			this.listenerThread = new LocationListenerUpdater(this, listener, interval, timeout, maxage);

			this.listenerThread.start();
		} catch (IllegalArgumentException ie) {
			throw ie;
		} catch (SecurityException se) {
			throw se;
		} catch (IllegalMonitorStateException ignored) {
		} catch (NullPointerException ignored) {
		} catch (Exception ignored) {
		}
	}

	public int getState() {
		return 1;
	}

	public void reset() {
		setLocationListener(null, -1, -1, -1);
	}

	static boolean parseCoordFile() {
		int counter = 0;
		DataInputStream in = null;
		FileConnection filecon = null;
		try {
			filecon = (FileConnection) Connector.open("file:///root/GPS/coord.txt");
			if (filecon.exists()) {
				in = filecon.openDataInputStream();

				String line = readLine(in);
				while (line != null) {
					MyTokenizer tokenizer = new MyTokenizer(line, ";");
					try {
						double lat = Double.parseDouble(tokenizer.nextToken());
						double lon = Double.parseDouble(tokenizer.nextToken());
						float alt = Float.parseFloat(tokenizer.nextToken());

						float hor = Float.parseFloat(tokenizer.nextToken());
						float ver = Float.parseFloat(tokenizer.nextToken());

						locations
								.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(lat, lon, alt, hor, ver)));

						counter++;
					} catch (Throwable ignored) {
					}
					line = readLine(in);
				}
			}
		} catch (IOException ignored) {
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (filecon != null) {
					filecon.close();
				}
			} catch (IOException ignored) {
			}
		}
		return counter != 0;
	}

	private static String readLine(DataInputStream in) {
		StringBuffer buf = new StringBuffer();
		try {
			int ch;
			while (((ch = in.read()) != 10) && (ch != -1)) {
				if (ch != 13) {
					buf.append((char) ch);
				}
			}
		} catch (IOException ignored) {
		}
		if (buf.length() == 0) {
			return null;
		}
		return buf.toString();
	}

	private static class MyTokenizer {
		private String str;
		private String del;
		private int pos;

		public MyTokenizer(String line, String del) {
			this.pos = 0;
			this.str = line;
			this.del = del;
		}

		public String nextToken() {
			int ind = this.str.indexOf(this.del, this.pos);
			if (ind == -1) {
				if (ind < this.str.length()) {
					String token = this.str.substring(this.pos);
					this.pos = this.str.length();
					return token;
				}
				return null;
			}
			String token = this.str.substring(this.pos, ind);
			this.pos = (ind + 1);
			return token;
		}
	}

	class LocationListenerUpdater extends Thread {
		private LocationProvider provider;
		private final LocationListener listener;
		public Location loc;
		private Vector locations;
		private int interval = 1;
		private int timeout = 1;
		private int maxage = 1;
		private boolean abort;

		LocationListenerUpdater(LocationProvider provider, LocationListener listener, int interval, int timeout,
								int maxage) {
			int intervalcheck = -1;
			int intervalnext = -5;
			int timeoutcheck = -1;
			int maxagecheck = -1;
			int temp_interval = interval;

			this.listener = listener;
			if (listener == null) {
				this.abort = false;
			}
			if (temp_interval == intervalcheck) {
				this.interval = 5;
			} else {
				this.interval = temp_interval;
			}
			if (interval == -5) {
				throw new IllegalArgumentException();
			}
			if (interval < intervalcheck) {
				throw new IllegalArgumentException();
			}
			if ((interval < -1) || ((interval > 0) && ((timeout > interval) || (maxage > interval)
					|| ((timeout < 1) && (timeout != -1)) || ((maxage < 1) && (maxage != -1))))) {
				throw new IllegalArgumentException();
			}
			this.abort = false;
		}

		public void run() {
			try {
				while (!this.abort) {
					try {
						synchronized (this.listener) {
							this.listener.wait(this.interval * 1000);
						}
					} catch (InterruptedException ignored) {
					}
					try {
						this.listener.locationUpdated(this.provider, EmulatedLocationProvider.this
								.getLocation(this.timeout));
					} catch (LocationException ignored) {
					} catch (InterruptedException ignored) {
					} catch (IllegalArgumentException ignored) {
					}
				}
			} catch (NullPointerException ignored) {
			}
		}

		public void abort() {
			try {
				this.abort = true;
			} catch (IllegalMonitorStateException ignored) {
			} catch (Exception ignored) {
			}
		}
	}
}
