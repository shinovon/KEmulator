package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class117 implements ModifyListener
{
    private final Property aClass38_1183;
    
    Class117(final Property aClass38_1183) {
        super();
        this.aClass38_1183 = aClass38_1183;
    }
    
    public final void modifyText(final ModifyEvent modifyEvent) {
        final DevicePlatform ane1073 = Devices.curPlatform;
        Devices.curPlatform = Devices.getPlatform(Property.method361(this.aClass38_1183).getText().trim());
        this.aClass38_1183.updateCustomProperties();
        Property.method362(this.aClass38_1183);
        Devices.curPlatform = ane1073;
        this.aClass38_1183.updateCustomProperties();
    }
}
