package com.nttdocomo.io;

import java.io.*;

public interface ServerObexConnection extends ObexConnection {
	void accept() throws IOException;

	void receiveRequest() throws IOException;

	int getOperation();

	void sendResponse(final int p0) throws IOException;
}
