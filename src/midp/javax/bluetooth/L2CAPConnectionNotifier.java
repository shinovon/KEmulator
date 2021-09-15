package javax.bluetooth;

import javax.microedition.io.*;
import java.io.*;

public interface L2CAPConnectionNotifier extends Connection
{
    L2CAPConnection acceptAndOpen() throws IOException;
}
