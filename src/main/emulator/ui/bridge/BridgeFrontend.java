package emulator.ui.bridge;

import emulator.Emulator;
import emulator.graphics2D.IFont;
import emulator.graphics2D.IImage;
import emulator.graphics2D.awt.FontAWT;
import emulator.graphics2D.awt.ImageAWT;
import emulator.graphics3D.IGraphics3D;
import emulator.ui.*;
import emulator.ui.swt.Property;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

public class BridgeFrontend implements IEmulatorFrontend {
	public final MappedByteBuffer frameBuffer;
	private final FileChannel bufferChannel;
	public final BufferedReader inputpipe;
	public final FileOutputStream statepipe;
	public final FileOutputStream readypipe;
	public final FileInputStream readpipe;

	final Object stateWriteLock = new Object();

	private final BridgeOutput screen;
	private final Property iproperty = new Property();
	private final BridgeLog log = new BridgeLog();
	private Properties midletProps;

	Thread worker;

	final HashMap<Integer, TargetedCommand> commandsCache = new HashMap<>();
	final Vector<PermissionWaitData> permissions = new Vector<>();

	private static int commandsCounter = 1;

	public BridgeFrontend(String path, int w, int h) {
		if (!new File(path + "inputpipe").exists() || !new File(path + "statepipe").exists() || !new File(path + "readypipe").exists() || !new File(path + "readpipe").exists()) {
			System.out.println("Pipes are missing!");
			System.exit(21);
		}

		MappedByteBuffer _buffer = null;
		FileChannel _bufferChannel = null;
		BufferedReader _inputpipe = null;
		FileOutputStream _statepipe = null;
		FileOutputStream _readypipe = null;
		FileInputStream _readpipe = null;

		try {
			// order is important, check the documentation!
			_bufferChannel = new RandomAccessFile(path + "screen", "rw").getChannel();
			_buffer = _bufferChannel.map(FileChannel.MapMode.READ_WRITE, 0, 4L * w * h);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(24);
		}
		try {
			_inputpipe = new BufferedReader(new FileReader(path + "inputpipe"));
			_statepipe = new FileOutputStream(path + "statepipe");
			_readypipe = new FileOutputStream(path + "readypipe");
			_readpipe = new FileInputStream(path + "readpipe");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(22);
		}

		frameBuffer = _buffer;
		bufferChannel = _bufferChannel;
		inputpipe = _inputpipe;
		statepipe = _statepipe;
		readypipe = _readypipe;
		readpipe = _readpipe;

		screen = new BridgeOutput(this, w, h);
	}

	/**
	 * Sends data to client via "state" FIFO. See documentation for more info.
	 *
	 * @param type Packet type.
	 * @param blob Data to send.
	 */
	public void sendToState(char type, byte[] blob) {
		System.out.println("Sending packet " + type + " to state pipe");
		synchronized (stateWriteLock) {
			byte[] len = ByteBuffer.allocate(4).putInt(blob.length).array();
			try {
				statepipe.write(type);
				statepipe.write(len);
				statepipe.write(blob);
			} catch (IOException e) {
				System.out.println("Broken state pipe");
				System.exit(25);
			}
		}
	}

	public void requestCommandsChoice(Vector<TargetedCommand> list) {
		synchronized (stateWriteLock) {
			try {

				commandsCache.clear();
				if (list == null || list.isEmpty()) {
					sendToState('O', new byte[0]);
					return;
				}
				ByteBuffer bb = ByteBuffer.allocate(4);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bb.putInt(list.size());
				baos.write(bb.array());
				bb.clear();
				for (TargetedCommand command : list) {
					int id = commandsCounter;
					commandsCache.put(commandsCounter, command);
					bb.putInt(id);
					baos.write(bb.array());
					if (command.isChoice())
						baos.write(command.wasSelected ? 2 : 1);
					else
						baos.write(0);
					baos.write(command.text.getBytes(StandardCharsets.UTF_8));
					baos.write((byte) 0);

					bb.clear();
					commandsCounter++;
				}
				sendToState('M', baos.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public IMessage getMessage() {
		return null;
	}

	@Override
	public ILogStream getLogStream() {
		return log;
	}

	@Override
	public IProperty getProperty() {
		return iproperty;
	}

	@Override
	public IScreen getScreen() {
		return screen;
	}

	@Override
	public int getScreenDepth() {
		return 24;
	}

	@Override
	public void pushPlugin(IPlugin p0) {

	}

	@Override
	public void disposeSubWindows() {

	}

	@Override
	public IFont newFont(int size, int style) {
		return new FontAWT(this.iproperty.getDefaultFontName(), size, style, false);
	}

	@Override
	public IFont newCustomFont(int height, int style) {
		return new FontAWT(this.iproperty.getDefaultFontName(), height, style, true);
	}

	@Override
	public IImage newImage(int p0, int p1, boolean transparent) {
		return new ImageAWT(p0, p1, transparent, -1);
	}

	@Override
	public IImage newImage(int p0, int p1, boolean p2, int p3) {
		return new ImageAWT(p0, p1, p2, p3);
	}

	@Override
	public IImage newImage(byte[] p0) throws IOException {
		return new ImageAWT(p0);
	}

	@Override
	public IGraphics3D getGraphics3D() {
		throw new Error("This method seems unused?");
	}

	@Override
	public void syncValues() {

	}

	@Override
	public String getAppProperty(String p0) {
		final String property;
		if (midletProps != null && (property = midletProps.getProperty(p0)) != null) {
			return property.trim();
		}
		return null;
	}

	@Override
	public Properties getAppProperties() {
		return midletProps;
	}

	@Override
	public void setAppProperties(Properties p) {
		midletProps = p;
	}

	@Override
	public void updateLanguage() {

	}

	@Override
	public void dispose() {
		try {
			bufferChannel.close();
			readypipe.close();
			statepipe.close();
			readpipe.close();
			inputpipe.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
