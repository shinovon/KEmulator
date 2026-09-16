package com.vodafone.bluetooth;

import java.io.IOException;

public class LocalService extends BaseService {

    public LocalService() {
        super();
    }

    public LocalService(String serviceID) throws NullPointerException {
        super(serviceID);
    }

    public LocalService(String serviceID, String serviceName) throws NullPointerException {
        super(serviceID, serviceName);
    }

    public final void setServiceID(String id) throws IllegalArgumentException, NumberFormatException, IOException {
        if (id == null) throw new NullPointerException();
        // Validate as UUID
        if (id.length() < 4) throw new IllegalArgumentException("Invalid service ID");
        this.serviceID = id;
    }

    public final void setServiceName(String name) throws IOException {
        if (name == null) throw new NullPointerException();
        this.serviceName = name;
    }
}
