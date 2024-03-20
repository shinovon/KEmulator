package emulator.media.capture;

import javax.microedition.amms.control.camera.FocusControl;
import javax.microedition.media.MediaException;

class FocusControlImpl implements FocusControl {

    public int setFocus(int paramInt) throws MediaException {
        return 0;
    }

    public int getFocus() {
        return 0;
    }

    public int getMinFocus() {
        return 0;
    }

    public int getFocusSteps() {
        return 0;
    }

    public boolean isManualFocusSupported() {
        return false;
    }

    public boolean isAutoFocusSupported() {
        return false;
    }

    public boolean isMacroSupported() {
        return false;
    }

    public void setMacro(boolean paramBoolean) throws MediaException {
    }

    public boolean getMacro() {
        return false;
    }
}