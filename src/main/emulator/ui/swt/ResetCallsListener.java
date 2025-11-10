package emulator.ui.swt;

import emulator.custom.h;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import java.util.Enumeration;

final class ResetCallsListener extends SelectionAdapter {
	ResetCallsListener(final Methods class46) {
		super();
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final Enumeration<h.MethodInfo> elements = h.methodProfiles.elements();
		while (elements.hasMoreElements()) {
			final h.MethodInfo methodInfo;
			(methodInfo = elements.nextElement()).callCount = 0;
			methodInfo.totalExecutionTime = 0L;
			methodInfo.averageExecutionTime = 0.0f;
			methodInfo.timePercentage = 0.0f;
		}
	}
}
