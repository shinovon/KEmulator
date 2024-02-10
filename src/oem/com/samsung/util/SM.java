package com.samsung.util;

public class SM {

    private String dest;
    private String callback;
    private String data;

    public SM() {

    }

    public SM(String dest, String callback, String textMessage) {
        if (textMessage == null || textMessage.length() > 80)
            throw new IllegalArgumentException("textMessage");
        this.dest = dest;
        this.callback = callback;
        this.data = textMessage;
    }

    public void setDestAddress(String address) {
        this.dest = address;
    }

    public void setCallbackAddress(String address) {
        this.callback = address;
    }

    public void setData(String textMessage) {
        if (textMessage == null || textMessage.length() > 80)
            throw new IllegalArgumentException();
        this.data = textMessage;
    }

    public String getDestAddress() {
        return dest;
    }

    public String getCallbackAddress() {
        return callback;
    }

    public String getData() {
        return data;
    }
}
