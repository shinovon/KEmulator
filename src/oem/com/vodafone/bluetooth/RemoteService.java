package com.vodafone.bluetooth;

import javax.bluetooth.ServiceRecord;

public class RemoteService extends BaseService {

    private final Device device;
    private final ServiceRecord serviceRecord;

    public RemoteService(Device device, ServiceRecord record) {
        this.device = device;
        this.serviceRecord = record;
        if (record != null) {
            try {
                this.serviceID = record.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                // Try to get service name from attributes
                javax.bluetooth.DataElement nameElem = record.getAttributeValue(0x0100);
                if (nameElem != null) {
                    this.serviceName = nameElem.getValue().toString();
                }
            } catch (Exception ignored) {
                this.serviceID = record.toString();
            }
        }
    }

    private RemoteService(Device device, ServiceRecord record, String id) throws IllegalArgumentException {
        if (device == null || record == null) throw new IllegalArgumentException();
        this.device = device;
        this.serviceRecord = record;
        this.serviceID = id;
    }

    public final Device getDevice() {
        return device;
    }

    public ServiceRecord getServiceRecord() {
        return serviceRecord;
    }
}
