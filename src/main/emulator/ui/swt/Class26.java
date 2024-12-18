package emulator.ui.swt;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

final class Class26 implements PaintListener {
	private final MemoryView aClass110_595;

	Class26(final MemoryView aClass110_595) {
		super();
		this.aClass110_595 = aClass110_595;
	}

	public final void paintControl(final PaintEvent paintEvent) {
		MemoryView.method669(this.aClass110_595, MemoryView.method642(this.aClass110_595).getSize().x);
		MemoryView.method675(this.aClass110_595, MemoryView.method642(this.aClass110_595).getSize().y);
		MemoryView.method680(this.aClass110_595, MemoryView.method642(this.aClass110_595).getVerticalBar().getSelection());
		MemoryView.method650(this.aClass110_595, paintEvent.gc);
	}
}
