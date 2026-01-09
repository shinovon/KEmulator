package emulator.ui.bridge;

import emulator.Emulator;
import emulator.custom.ResourceManager;
import emulator.graphics2D.IImage;
import emulator.graphics2D.awt.ImageAWT;
import emulator.ui.CommandsMenuPosition;
import emulator.ui.ICaret;
import emulator.ui.IScreen;
import emulator.ui.TargetedCommand;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

final class BridgeOutput implements IScreen {

	private final BridgeFrontend bridge;
	private final ImageAWT screenImageAwt;
	private final ImageAWT backBufferImageAwt;
	private final ImageAWT xrayScreenImageAwt;

	private boolean firstFrame = true;

	private final Object lock = new Object();

	private String leftSoftLabel;
	private String rightSoftLabel;

	BridgeOutput(BridgeFrontend bridge, int w, int h) {
		this.bridge = bridge;
		this.screenImageAwt = new ImageAWT(w, h, false, -1);
		this.backBufferImageAwt = new ImageAWT(w, h, false, -1);
		this.xrayScreenImageAwt = new ImageAWT(w, h, true, -1);
	}

	@Override
	public void initScreen(int w, int h) {

	}

	@Override
	public IImage getScreenImg() {
		synchronized (lock) {
			return screenImageAwt;
		}
	}

	@Override
	public IImage getBackBufferImage() {
		synchronized (lock) {
			return backBufferImageAwt;
		}
	}

	@Override
	public IImage getXRayScreenImage() {
		synchronized (lock) {
			return xrayScreenImageAwt;
		}
	}

	@Override
	public void repaint() {
		synchronized (lock) {
			if (!firstFrame) {
				try {
					int read = bridge.readpipe.read();
					if (read == -1)
						System.exit(27);
					if (read != 10) {
						System.out.println("Not line feed read from read event pipe!");
					}
				} catch (IOException e) {
					System.exit(27);
				}
			}
			firstFrame = false;

			try {
				sendBuffer();
			} catch (Throwable e) {
				e.printStackTrace();
				System.exit(28);
			}
			try {
				bridge.readypipe.write('\n');
			} catch (IOException e) {
				System.exit(26);
			}
		}
	}

	private void sendBuffer() {
		//int[] data = (Settings.xrayView ? screenImageAwt : xrayScreenImageAwt).getData();
		int[] data = screenImageAwt.getData();

		bridge.frameBuffer.position(0);
		for (int pixel : data) {
			bridge.frameBuffer.putInt(pixel);
		}
	}

	@Override
	public int getWidth() {
		return getScreenImg().getWidth();
	}

	@Override
	public int getHeight() {
		return getScreenImg().getHeight();
	}

	@Override
	public void setLeftSoftLabel(String label) {
		leftSoftLabel = label;
		sendLabels();
	}

	@Override
	public void setRightSoftLabel(String label) {
		rightSoftLabel = label;
		sendLabels();
	}

	private void sendLabels() {
		byte[] l = leftSoftLabel == null ? (new byte[0]) : leftSoftLabel.getBytes(StandardCharsets.UTF_8);
		byte[] r = rightSoftLabel == null ? (new byte[0]) : rightSoftLabel.getBytes(StandardCharsets.UTF_8);
		ByteBuffer bb = ByteBuffer.allocate(l.length + 1 + r.length);
		bb.put(l);
		bb.put((byte) 0);
		bb.put(r);
		bridge.sendToState('C', bb.array());
	}

	@Override
	public void showCommandsList(Vector<TargetedCommand> cmds, CommandsMenuPosition target, int tx, int ty) {
		bridge.requestCommandsChoice(cmds); //TODO add support for targets
	}

	@Override
	public void forceCloseCommandsList() {
		bridge.requestCommandsChoice(null);
	}

	@Override
	public void startVibra(long p0) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(p0);
		bridge.sendToState('V', bb.array());
	}

	@Override
	public void stopVibra() {
		bridge.sendToState('V', new byte[8]);
	}

	@Override
	public ICaret getCaret() {
		return new DummyCaret();
	}

	@Override
	public void setSize(int w, int h) {

	}

	@Override
	public void setWindowIcon(InputStream inputStream) {
		try {
			ImageAWT img = new ImageAWT(ResourceManager.getBytes(inputStream));
			int[] rgba = img.getData();
			ByteBuffer bb = ByteBuffer.allocate(4 + rgba.length * 4);
			bb.putShort((short) img.getWidth());
			bb.putShort((short) img.getHeight());
			for (int i = 0; i < rgba.length; i++) {
				bb.putInt(rgba[i]);
			}
			bridge.sendToState('I', bb.array());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showMessage(String message) {
		bridge.sendToState('L', message.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public void showMessage(String title, String detail) {
		showMessage(title + "\n\n" + detail);
	}

	@Override
	public void showMessageThreadSafe(String title, String detail) {
		showMessage(title, detail);
	}

	@Override
	public int showMidletChoice(Vector<String> midletKeys) {
		return 0;
	}

	@Override
	public int showUpdateDialog(int type) {
		return 0;
	}

	@Override
	public boolean showSecurityDialog(String message) {
		try {
			return new PermissionWaitData().request(bridge, message);
		} catch (InterruptedException e) {
			//TODO what to do here?
			return false;
		}
	}

	@Override
	public String showIMEIDialog() {
		return "";
	}

	@Override
	public void setCurrent(Displayable d) {
		if (d instanceof TextBox) {
			throw new IllegalArgumentException("Not implemented");
		} else if (d instanceof List) {
			throw new IllegalArgumentException("Not implemented");
		} else {
			bridge.sendToState('D', new byte[]{(byte) 'B'});
		}
		updateTitle();
	}

	@Override
	public void updateTitle() {
		String title = Emulator.getCurrentDisplay().getCurrent().getTitle();
		if (title == null)
			bridge.sendToState('T', new byte[0]);
		else
			bridge.sendToState('T', title.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public void runWithMidlet() {
		bridge.worker = Thread.currentThread();
		while (true) {
			try {
				Thread.sleep(3600);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	@Override
	public void runEmpty() {
		System.exit(30);
	}

	public void appStarted(boolean first) {
		if (first)
			new Thread(new BridgeInput(bridge)).start();
	}

	public boolean isShown() {
		return true;
	}
}
