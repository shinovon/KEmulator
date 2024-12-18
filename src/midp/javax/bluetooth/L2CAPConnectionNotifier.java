package javax.bluetooth;

import javax.microedition.io.Connection;
import java.io.IOException;

public interface L2CAPConnectionNotifier extends Connection {
	L2CAPConnection acceptAndOpen() throws IOException;
}
