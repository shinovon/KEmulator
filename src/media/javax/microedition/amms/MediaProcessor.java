package javax.microedition.amms;

import javax.microedition.media.Controllable;
import javax.microedition.media.MediaException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract interface MediaProcessor extends Controllable {
	public static final int REALIZED = 200;
	public static final int STARTED = 400;
	public static final int STOPPED = 300;
	public static final int UNKNOWN = -1;
	public static final int UNREALIZED = 100;

	public abstract void abort();

	public abstract void addMediaProcessorListener(MediaProcessorListener paramMediaProcessorListener);

	public abstract void complete() throws MediaException;

	public abstract int getProgress();

	public abstract int getState();

	public abstract void removeMediaProcessorListener(MediaProcessorListener paramMediaProcessorListener);

	public abstract void setInput(InputStream paramInputStream, int paramInt) throws MediaException;

	public abstract void setInput(Object paramObject) throws MediaException;

	public abstract void setOutput(OutputStream paramOutputStream);

	public abstract void start() throws MediaException;

	public abstract void stop() throws MediaException;
}
