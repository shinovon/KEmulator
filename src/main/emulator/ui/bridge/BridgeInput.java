package emulator.ui.bridge;

import emulator.Emulator;
import emulator.Settings;
import emulator.ui.TargetedCommand;

import java.io.IOException;

class BridgeInput implements Runnable {

	private final BridgeFrontend bridge;

	BridgeInput(BridgeFrontend bridge) {
		this.bridge = bridge;
	}

	@Override
	public void run() {
		while (true) {
			String line = null;
			try {
				line = bridge.inputpipe.readLine();
			} catch (IOException e) {
				System.exit(29);
			}

			if (line == null) {
				// client disconnected
				bridge.worker.interrupt();
				return;
			}

			try {
				String[] data = line.split(" ");
				handleEvent(data);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleEvent(String[] data) {
		switch (data[0]) {
			case "K": {
				int code = Integer.parseInt(data[2]);
				switch (data[1]) {
					case "D":
						Emulator.getEventQueue().keyPress(code);
						break;
					case "R":
						Emulator.getEventQueue().keyRepeat(code);
						break;
					case "U":
						Emulator.getEventQueue().keyRelease(code);
						break;
					default:
						throw new IllegalArgumentException();
				}
				break;
			}
			case "T": {
				int n = Integer.parseInt(data[1]);
				int x = Integer.parseInt(data[3]);
				int y = Integer.parseInt(data[4]);
				switch (data[2]) {
					case "D":
						Emulator.getEventQueue().mouseDown(x, y, n);
						break;
					case "M":
						Emulator.getEventQueue().mouseDrag(x, y, n);
						break;
					case "U":
						Emulator.getEventQueue().mouseUp(x, y, n);
						break;
					default:
						throw new IllegalArgumentException();
				}
				break;
			}
			case "C": {
				int x = Integer.parseInt(data[1]);
				TargetedCommand c = bridge.commandsCache.get(x);
				if (c == null)
					throw new IllegalArgumentException();
				c.invoke();
				break;
			}
			case "P": {
				int x = Integer.parseInt(data[1]);
				boolean a = parseBool(data[2]);
				l1:
				{
					for (PermissionWaitData pwd : bridge.permissions) {
						if (pwd.requestId == x) {
							bridge.permissions.remove(pwd);
							pwd.resolve(a);
							break l1;
						}
					}
					throw new IllegalArgumentException();
				}
				break;
			}
			case "I": {
				throw new RuntimeException("Not implemented");
			}
			case "E": {
				throw new RuntimeException("Not implemented");
			}
			case "X": {
				Settings.xrayView = parseBool(data[1]);
				break;
			}
			default:
				throw new IllegalArgumentException("Unknown event type: " + data[0]);
		}
	}

	private static boolean parseBool(String s) {
		if (s.equals("0"))
			return false;
		if (s.equals("1"))
			return true;
		throw new IllegalArgumentException();
	}
}
