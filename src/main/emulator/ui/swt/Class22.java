package emulator.ui.swt;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

final class Class22 implements ModifyListener {
	private final MemoryView aClass110_591;

	Class22(final MemoryView aClass110_591) {
		super();
		this.aClass110_591 = aClass110_591;
	}

	public final void modifyText(final ModifyEvent modifyEvent) {
		MemoryView.method662(this.aClass110_591, MemoryView.method654(this.aClass110_591).getSelectionIndex());
		this.aClass110_591.resortImages();
		MemoryView.method668(this.aClass110_591);
	}
}
