package emulator.ui;

import javax.wireless.messaging.Message;
import java.io.IOException;
import java.io.InterruptedIOException;

public interface IMessage {
	Message receive(final String p0) throws IOException, InterruptedIOException;

	void send(final Message p0, final String p1) throws IOException, InterruptedIOException;
}
