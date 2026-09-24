package com.vodafone.bluetooth;

/**
 * Base service for Vodafone Bluetooth.
 */
public class BaseService {

    protected String serviceID;
    protected String serviceName;

    public BaseService() {
        this.serviceID = "";
        this.serviceName = "";
    }

    public BaseService(String serviceID) throws NullPointerException {
        if (serviceID == null) throw new NullPointerException();
        this.serviceID = serviceID;
        this.serviceName = "";
    }

    public BaseService(String serviceID, String serviceName) throws NullPointerException {
        if (serviceID == null || serviceName == null) throw new NullPointerException();
        this.serviceID = serviceID;
        this.serviceName = serviceName;
    }

    public boolean matches(BaseService other) throws NullPointerException {
        if (other == null) throw new NullPointerException();
        return this.serviceID.equals(other.serviceID);
    }

    public String getServiceID() {
        return serviceID;
    }

    public String getServiceName() {
        return serviceName;
    }
}
