package javax.microedition.amms;

import javax.microedition.media.Control;
import javax.microedition.media.Controllable;

public class Spectator implements Controllable {
    private Controllable specImpl;

    Spectator(Controllable impl) {
        this.specImpl = impl;
    }

    public Control getControl(String controlType) {
        return this.specImpl.getControl(controlType);
    }

    public Control[] getControls() {
        return this.specImpl.getControls();
    }
}
