package emulator.bluetooth;

import javax.bluetooth.ServiceRecordImpl;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Registry of local Bluetooth services.
 * Thread-safe.
 */
public class ServiceRegistry {

    private final AtomicInteger handleCounter = new AtomicInteger(0x10000);
    private final Map<Integer, BluetoothService> handleToService = new ConcurrentHashMap<>();
    private final Map<Object, BluetoothService> notifierToService = new ConcurrentHashMap<>();
    private final Map<String, List<BluetoothService>> uuidToServices = new ConcurrentHashMap<>();

    /**
     * Register a new service.
     */
    public synchronized BluetoothService register(BluetoothService service) {
        int handle = handleCounter.getAndIncrement();
        service.getServiceRecord().setHandle(handle);
        handleToService.put(handle, service);
        if (service.getNotifier() != null) {
            notifierToService.put(service.getNotifier(), service);
        }
        String uuid = BluetoothUtils.normalizeServiceIdentifier(service.getUuidOrPsm());
        uuidToServices.computeIfAbsent(uuid, k -> new ArrayList<>()).add(service);
        return service;
    }

    /**
     * Unregister by notifier.
     */
    public synchronized void unregisterByNotifier(Object notifier) {
        BluetoothService svc = notifierToService.remove(notifier);
        if (svc != null) {
            handleToService.remove(svc.getServiceRecord().getHandle());
            String uuid = BluetoothUtils.normalizeServiceIdentifier(svc.getUuidOrPsm());
            List<BluetoothService> list = uuidToServices.get(uuid);
            if (list != null) {
                list.remove(svc);
                if (list.isEmpty()) uuidToServices.remove(uuid);
            }
            svc.close();
        }
    }

    /**
     * Get service by notifier.
     */
    public BluetoothService getByNotifier(Object notifier) {
        return notifierToService.get(notifier);
    }

    /**
     * Get service record by notifier.
     */
    public ServiceRecordImpl getRecordByNotifier(Object notifier) {
        BluetoothService svc = notifierToService.get(notifier);
        return svc != null ? svc.getServiceRecord() : null;
    }

    /**
     * Get all services.
     */
    public Collection<BluetoothService> getAllServices() {
        return new ArrayList<>(handleToService.values());
    }

    /**
     * Find services by UUID (or PSM), accepting equivalent UUID spellings
     * such as dashed and undashed 128-bit forms.
     * If uuid is null, return all.
     */
    public List<BluetoothService> findByUuid(String uuid) {
        if (uuid == null) {
            return new ArrayList<>(handleToService.values());
        }
        List<BluetoothService> list = uuidToServices.get(BluetoothUtils.normalizeServiceIdentifier(uuid));
        return list != null ? new ArrayList<>(list) : Collections.emptyList();
    }

    /**
     * Get service by handle.
     */
    public BluetoothService getByHandle(int handle) {
        return handleToService.get(handle);
    }

    /**
     * Clear all.
     */
    public synchronized void clear() {
        for (BluetoothService svc : handleToService.values()) {
            svc.close();
        }
        handleToService.clear();
        notifierToService.clear();
        uuidToServices.clear();
    }
}
