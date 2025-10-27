package emulator.sensor;

import emulator.Emulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
//https://github.com/ezyang/SensorSimulator/blob/master/SensorSimulatorSettings/src/org/openintents/sensorsimulator/hardware/SensorSimulatorClient.java
public final class SensorSimulatorClient {
	protected boolean connected;
	Socket mSocket;
	PrintWriter mOut;
	BufferedReader mIn;

	public SensorSimulatorClient() {
		super();
		this.connected = false;
	}

	public final void connect() {
		if (this.connected) {
			return;
		}
		this.mSocket = null;
		this.mOut = null;
		this.mIn = null;
		Emulator.getEmulator().getLogStream().println("Sensor Simulator Starting connection...");
		final String s = "localhost";
		final String s2 = "8010";
		Emulator.getEmulator().getLogStream().println("Connecting to " + s + " : " + s2);
		try {
			this.mSocket = new Socket(s, Integer.parseInt(s2));
			this.mOut = new PrintWriter(this.mSocket.getOutputStream(), true);
			this.mIn = new BufferedReader(new InputStreamReader(this.mSocket.getInputStream()));
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
			line = this.mIn.readLine();
		} catch (IOException ex4) {
			Emulator.getEmulator().getLogStream().println("Couldn't get I/O for the connection to: x.x.x.x.");
			return;
		}
		Emulator.getEmulator().getLogStream().println("Received: " + line);
		if (line.equals("SensorSimulator")) {
			this.connected = true;
			Emulator.getEmulator().getLogStream().println("Connected");
			return;
		}
		Emulator.getEmulator().getLogStream().println("Problem connecting: Wrong string sent.");
		this.disconnect();
	}

	public final void disconnect() {
		if (this.connected) {
			Emulator.getEmulator().getLogStream().println("Disconnect()");
			try {
				this.mOut.close();
				this.mIn.close();
				this.mSocket.close();
			} catch (IOException ex) {
				Emulator.getEmulator().getLogStream().println("Couldn't get I/O for the connection to: x.x.x.x.");
			}
			this.connected = false;
		}
	}

	public final void disableSensor(final String s) {
		if (!this.connected) {
			return;
		}
		this.mOut.println("disableSensor()");
		this.mOut.println(s);
		try {
			if (this.mIn.readLine().compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException("Sensor '" + s + "' is not supported.");
			}
		} catch (IOException ex) {
			Emulator.getEmulator().getLogStream().println("Couldn't get I/O for the connection to: x.x.x.x.");
		}
	}

	public final void enableSensor(final String s) {
		if (!this.connected) {
			return;
		}
		this.mOut.println("enableSensor()");
		this.mOut.println(s);
		try {
			if (this.mIn.readLine().compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException("Sensor '" + s + "' is not supported.");
			}
		} catch (IOException ex) {
			Emulator.getEmulator().getLogStream().println("Couldn't get I/O for the connection to: x.x.x.x.");
		}
	}

	public final void readSensor(final String s, final float[] array) {
		if (!this.connected) {
			return;
		}
		if (array == null) {
			throw new NullPointerException("readSensor for '" + s + "' called with sensorValues == null.");
		}
		this.mOut.println("readSensor()\n" + s);
		try {
			final String line;
			if ((line = this.mIn.readLine()).compareTo("throw IllegalArgumentException") == 0) {
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
				array[i] = Float.parseFloat(this.mIn.readLine());
			}
		} catch (IOException ex) {
			Emulator.getEmulator().getLogStream().println("Couldn't get I/O for the connection to: x.x.x.x.");
		}
	}
}
