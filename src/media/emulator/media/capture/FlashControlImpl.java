package emulator.media.capture;

import javax.microedition.amms.control.camera.FlashControl;

class FlashControlImpl implements FlashControl {
	public int[] getSupportedModes() {
		return new int[]{1};
	}

	public void setMode(int paramInt) {
	}

	public int getMode() {
		return 1;
	}

	public boolean isFlashReady() {
		return false;
	}
}