package com.j_phone.ui;

import emulator.Emulator;
import emulator.ui.swt.EmulatorImpl;
import emulator.ui.swt.EmulatorScreen;
import emulator.ui.swt.InputDialog;

public final class FEPControl {
	private InputDialog inputDialog;

	public static final FEPControl getDefaultFEPControl() {
		return new FEPControl();
	}

	public String getInputText(String paramString, int paramInt1, int paramInt2, boolean paramBoolean) {
		EmulatorImpl.getDisplay().syncExec(new Runnable() {
			public void run() {
				inputDialog = new InputDialog(((EmulatorScreen) Emulator.getEmulator().getScreen()).getShell());
				inputDialog.setInput(paramString);
				inputDialog.setText("FEPControl");
				inputDialog.open();
			}
		});
		return inputDialog.getInput();
	}
}
