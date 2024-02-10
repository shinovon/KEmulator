package com.motorola.phone;

import java.io.IOException;

public class Dialer {
    public void endCall()
            throws IOException {
    }

    public static Dialer getDefaultDialer() {
        return null;
    }

    public void sendExtNo(String extNumber)
            throws IOException {
    }

    public void startCall(String teleNumber)
            throws IOException {
    }

    public void startCall(String teleNumber, String extNo)
            throws IOException {
    }

    public void setDialerListener(DialerListener listener) {
    }

    public void notifyDialerListener(byte event) {
    }
}
