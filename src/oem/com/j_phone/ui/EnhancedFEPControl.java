package com.j_phone.ui;

import emulator.Emulator;
import emulator.ui.swt.EmulatorImpl;
import emulator.ui.swt.EmulatorScreen;
import emulator.ui.swt.InputDialog;

public final class EnhancedFEPControl {
	private InputDialog inputDialog;

	public static final EnhancedFEPControl getDefaultEnhancedFEPControl() {
		return new EnhancedFEPControl();
	}

	public String getInputText(String paramString, int paramInt1, int paramInt2, boolean paramBoolean) {
		EmulatorImpl.getDisplay().syncExec(new Runnable() {
			public void run() {
				inputDialog = new InputDialog(((EmulatorScreen) Emulator.getEmulator().getScreen()).getShell());
				inputDialog.setInput(paramString);
				inputDialog.setText("EnhancedFEPControl");
				inputDialog.open();
			}
		});
		return inputDialog.getInput();
	}
}
