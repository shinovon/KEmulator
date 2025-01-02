package com.j_phone.system;

import com.j_phone.phonedata.MailData;

import java.io.IOException;

public class MailAgent {
	public static MailAgent getInstance() {
		return null;
	}

	public void setMailTransportListener(MailTransportListener paramMailTransportListener) {
	}

	public void send(MailData paramMailData)
			throws IOException {
	}

	public void receiveRemainder(MailData paramMailData)
			throws IOException {
	}

	public int checkMailSize(MailData paramMailData)
			throws IOException {
		return 0;
	}
}
