package com.nttdocomo.io;

import java.io.*;

public interface ClientObexConnection extends ObexConnection
{
    void connect() throws IOException;
    
    void setOperation(final int p0);
    
    void sendRequest() throws IOException;

    int getResponseCode();
}
