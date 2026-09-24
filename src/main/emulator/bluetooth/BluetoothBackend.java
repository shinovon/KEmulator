package emulator.bluetooth;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.ServiceRegistrationException;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import java.io.IOException;

/**
 * Backend contract used by the public JSR-82 facades.
 *
 * <p>{@link BluetoothStack} is the built-in LAN implementation. Keeping the
 * facades dependent on this narrow contract lets a future native Bluetooth
 * implementation be selected without changing {@code javax.bluetooth},
 * Connector, or OEM API classes.</p>
 */
public interface BluetoothBackend {

    String getLocalAddress();

    String getFriendlyName();

    void setFriendlyName(String name);

    int getDiscoverable();

    boolean setDiscoverable(int mode) throws BluetoothStateException;

    int getDeviceClass();

    boolean isPowerOn();

    boolean startInquiry(int accessCode, DiscoveryListener listener) throws BluetoothStateException;

    boolean cancelInquiry(DiscoveryListener listener);

    RemoteDevice[] retrieveDevices(int option);

    int searchServices(int[] attrSet, UUID[] uuidSet, RemoteDevice device,
                       DiscoveryListener listener) throws BluetoothStateException;

    boolean cancelServiceSearch(int transId);

    String selectService(UUID uuid, int security, boolean master) throws BluetoothStateException;

    ServiceRecord getRecord(Connection notifier);

    void updateRecord(ServiceRecord record) throws ServiceRegistrationException;

    void unregisterService(Object notifier);

    Connection openClientConnection(String url) throws IOException;

    Connection openServerNotifier(String url) throws IOException;

    String getProperty(String property);

    /** Releases discovery, SDP, and registered-service resources. */
    void shutdown();
}
