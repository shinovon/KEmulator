package com.nttdocomo.ui;

import com.nttdocomo.io.*;

public interface MediaResource {
	void use() throws ConnectionException;

	void unuse();

	void dispose();
}
