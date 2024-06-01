package emulator.ui.swt;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;

final class Class80 extends SelectionAdapter {
	private final Shell aShell864;
	private final M3GViewCameraSetDialog aClass30_865;

	Class80(final M3GViewCameraSetDialog aClass30_865, final Shell aShell864) {
		super();
		this.aClass30_865 = aClass30_865;
		this.aShell864 = aShell864;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		if (M3GViewCameraSetDialog.method342(this.aClass30_865) == 0) {
			this.aClass30_865.aFloat603 = Float.parseFloat(M3GViewCameraSetDialog.method343(this.aClass30_865).getText());
		} else if (M3GViewCameraSetDialog.method342(this.aClass30_865) == 1) {
			this.aClass30_865.aFloat606 = Float.parseFloat(M3GViewCameraSetDialog.method344(this.aClass30_865).getText());
			this.aClass30_865.aFloat608 = Float.parseFloat(M3GViewCameraSetDialog.method345(this.aClass30_865).getText());
		} else {
			this.aClass30_865.aFloat610 = Float.parseFloat(M3GViewCameraSetDialog.method346(this.aClass30_865).getText());
			this.aClass30_865.aFloat612 = Float.parseFloat(M3GViewCameraSetDialog.method347(this.aClass30_865).getText());
			this.aClass30_865.aFloat614 = Float.parseFloat(M3GViewCameraSetDialog.method348(this.aClass30_865).getText());
		}
		this.aShell864.close();
	}
}
