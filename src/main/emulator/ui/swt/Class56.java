package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class56 extends MouseAdapter {
	private final M3GViewUI aClass90_822;

	Class56(final M3GViewUI aClass90_822) {
		super();
		this.aClass90_822 = aClass90_822;
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		Label_0205:
		{
			if (mouseEvent.button == 3) {
                /*M3GViewUI.nextCameraMode(this.aClass90_822);
                MenuItem menuItem = null;
                Label_0166:
                {
                    switch (M3GViewUI.getCameraMode(this.aClass90_822)) {
                        case 0: {
                            M3GViewUI.method534(this.aClass90_822).setSelection(false);
                            menuItem = M3GViewUI.method539(this.aClass90_822);
                            break Label_0166;
                        }
                        case 1: {
                            M3GViewUI.method539(this.aClass90_822).setSelection(false);
                            menuItem = M3GViewUI.method541(this.aClass90_822);
                            break Label_0166;
                        }
                        case 2: {
                            M3GViewUI.method541(this.aClass90_822).setSelection(false);
                            if (M3GViewUI.method500(this.aClass90_822) == 0) {
                                menuItem = M3GViewUI.method505(this.aClass90_822);
                                break Label_0166;
                            }
                            M3GViewUI.nextCameraMode(this.aClass90_822);
                            break;
                        }
                        case 3: {
                            M3GViewUI.method505(this.aClass90_822).setSelection(false);
                            break;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    menuItem = M3GViewUI.method534(this.aClass90_822);
                }
                menuItem.setSelection(true);*/
			} else if (mouseEvent.button == 1) {
				M3GViewUI.method530(this.aClass90_822, mouseEvent.x);
				M3GViewUI.method535(this.aClass90_822, mouseEvent.y);
			}
		}
		aClass90_822.canvas.forceFocus();
	}
}
