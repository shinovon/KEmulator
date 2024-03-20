package emulator.media.capture;

import javax.microedition.amms.control.camera.CameraControl;
import javax.microedition.media.MediaException;

class CameraControlImpl implements CameraControl {
    public int getCameraRotation() {
        return 0;
    }

    public void enableShutterFeedback(boolean paramBoolean) throws MediaException {
    }

    public boolean isShutterFeedbackEnabled() {
        return false;
    }

    public String[] getSupportedExposureModes() {
        return null;
    }

    public void setExposureMode(String paramString) {
    }

    public String getExposureMode() {
        return null;
    }

    public int[] getSupportedVideoResolutions() {
        return null;
    }

    public int[] getSupportedStillResolutions() {
        return null;
    }

    public void setVideoResolution(int paramInt) {
    }

    public void setStillResolution(int paramInt) {
    }

    public int getVideoResolution() {
        return 0;
    }

    public int getStillResolution() {
        return 0;
    }
}