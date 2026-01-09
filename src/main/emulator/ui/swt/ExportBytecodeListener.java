package emulator.ui.swt;

import emulator.Emulator;
import emulator.UILocale;
import emulator.custom.h;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

final class ExportBytecodeListener extends SelectionAdapter {
	private final Methods aClass46_1432;

	ExportBytecodeListener(final Methods aClass46_1432) {
		super();
		this.aClass46_1432 = aClass46_1432;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {

	}
}
