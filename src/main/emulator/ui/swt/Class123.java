package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import javax.microedition.lcdui.Image;

final class Class123 extends SelectionAdapter {
	private final MemoryView aClass110_1206;

	Class123(final MemoryView aClass110_1206) {
		super();
		this.aClass110_1206 = aClass110_1206;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		for (int i = 0; i < MemoryView.method629(this.aClass110_1206).images.size(); ++i) {
			((Image) MemoryView.method629(this.aClass110_1206).images.get(i)).resetUsedRegion();
		}
		this.aClass110_1206.updateEverything();
	}
}
