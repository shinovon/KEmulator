package javax.microedition.media.protocol;

import javax.microedition.media.Control;
import javax.microedition.media.Controllable;
import java.io.IOException;

public abstract class DataSource implements Controllable {
	private String loc;

	public DataSource(String location) {
		super();
		this.loc = location;
	}

	public String getLocator() {
		return this.loc;
	}

	public abstract String getContentType();

	public abstract void connect() throws IOException;

	public abstract void disconnect();

	public abstract void start() throws IOException;

	public abstract void stop() throws IOException;

	public abstract SourceStream[] getStreams();

	public abstract Control getControl(final String p0);

	public abstract Control[] getControls();
}
