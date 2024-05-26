package emulator.sensor;

import emulator.*;

import java.net.*;
import java.io.*;

public final class f {
	protected boolean aBoolean472;
	Socket aSocket473;
	PrintWriter aPrintWriter474;
	BufferedReader aBufferedReader475;

	public f() {
		super();
		this.aBoolean472 = false;
	}

	public final void method260() {
		if (this.aBoolean472) {
			return;
		}
		this.aSocket473 = null;
		this.aPrintWriter474 = null;
		this.aBufferedReader475 = null;
		Emulator.getEmulator().getLogStream().println("Sensor Simulator Starting connection...");
		final String s = "localhost";
		final String s2 = "8010";
		Emulator.getEmulator().getLogStream().println("Connecting to " + s + " : " + s2);
		try {
			this.aSocket473 = new Socket(s, Integer.parseInt(s2));
			this.aPrintWriter474 = new PrintWriter(this.aSocket473.getOutputStream(), true);
			this.aBufferedReader475 = new BufferedReader(new InputStreamReader(this.aSocket473.getInputStream()));
		} catch (UnknownHostException ex) {
			Emulator.getEmulator().getLogStream().println("Don't know about host: " + s + " : " + s2);
			return;
		} catch (SocketTimeoutException ex2) {
			Emulator.getEmulator().getLogStream().println("Connection time out: " + s + " : " + s2);
			return;
		} catch (IOException ex3) {
			Emulator.getEmulator().getLogStream().println("Couldn't get I/O for the connection to: " + s + " : " + s2);
			return;
		}
		Emulator.getEmulator().getLogStream().println("Read line...");
		String line;
		try {
			line = this.aBufferedReader475.readLine();
		} catch (IOException ex4) {
			Emulator.getEmulator().getLogStream().println("Couldn't get I/O for the connection to: x.x.x.x.");
			return;
		}
		Emulator.getEmulator().getLogStream().println("Received: " + line);
		if (line.equals("SensorSimulator")) {
			this.aBoolean472 = true;
			Emulator.getEmulator().getLogStream().println("Connected");
			return;
		}
		Emulator.getEmulator().getLogStream().println("Problem connecting: Wrong string sent.");
		this.method263();
	}

	public final void method263() {
		if (this.aBoolean472) {
			Emulator.getEmulator().getLogStream().println("Disconnect()");
			try {
				this.aPrintWriter474.close();
				this.aBufferedReader475.close();
				this.aSocket473.close();
			} catch (IOException ex) {
				Emulator.getEmulator().getLogStream().println("Couldn't get I/O for the connection to: x.x.x.x.");
			}
			this.aBoolean472 = false;
		}
	}

	public final void method261(final String s) {
		if (!this.aBoolean472) {
			return;
		}
		this.aPrintWriter474.println("disableSensor()");
		this.aPrintWriter474.println(s);
		try {
			if (this.aBufferedReader475.readLine().compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException("Sensor '" + s + "' is not supported.");
			}
		} catch (IOException ex) {
			Emulator.getEmulator().getLogStream().println("Couldn't get I/O for the connection to: x.x.x.x.");
		}
	}

	public final void method264(final String s) {
		if (!this.aBoolean472) {
			return;
		}
		this.aPrintWriter474.println("enableSensor()");
		this.aPrintWriter474.println(s);
		try {
			if (this.aBufferedReader475.readLine().compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException("Sensor '" + s + "' is not supported.");
			}
		} catch (IOException ex) {
			Emulator.getEmulator().getLogStream().println("Couldn't get I/O for the connection to: x.x.x.x.");
		}
	}

	public final void method262(final String s, final float[] array) {
		if (!this.aBoolean472) {
			return;
		}
		if (array == null) {
			throw new NullPointerException("readSensor for '" + s + "' called with sensorValues == null.");
		}
		this.aPrintWriter474.println("readSensor()\n" + s);
		try {
			final String line;
			if ((line = this.aBufferedReader475.readLine()).compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException("Sensor '" + s + "' is not supported.");
			}
			if (line.compareTo("throw IllegalStateException") == 0) {
				throw new IllegalStateException("Sensor '" + s + "' is currently not enabled.");
			}
			final int int1 = Integer.parseInt(line);
			if (array.length < int1) {
				throw new ArrayIndexOutOfBoundsException("readSensor for '" + s + "' called with sensorValues having too few elements (" + array.length + ") to hold the sensor values (" + int1 + ").");
			}
			for (int i = 0; i < int1; ++i) {
				array[i] = Float.parseFloat(this.aBufferedReader475.readLine());
			}
		} catch (IOException ex) {
			Emulator.getEmulator().getLogStream().println("Couldn't get I/O for the connection to: x.x.x.x.");
		}
	}
}
