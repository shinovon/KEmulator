package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Menu;

final class Class25 extends MouseAdapter {
	private final MemoryView aClass110_594;

	Class25(final MemoryView aClass110_594) {
		super();
		this.aClass110_594 = aClass110_594;
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		MemoryView.method642(this.aClass110_594).forceFocus();
		if (mouseEvent.button == 3) {
			Canvas canvas;
			Menu menu;
			if (MemoryView.method625(this.aClass110_594, mouseEvent.x, mouseEvent.y)) {
				canvas = MemoryView.method642(this.aClass110_594);
				menu = MemoryView.method643(this.aClass110_594);
			} else {
				canvas = MemoryView.method642(this.aClass110_594);
				menu = MemoryView.method663(this.aClass110_594);
			}
			canvas.setMenu(menu);
		}
	}
}
