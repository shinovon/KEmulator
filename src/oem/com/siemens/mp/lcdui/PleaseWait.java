package com.siemens.mp.lcdui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Gauge;

public class PleaseWait extends Alert {
	public PleaseWait(String s, String s2) {
		super(s, s2, null, null);
		setTimeout(-2);
		setIndicator(new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING));
	}
}
