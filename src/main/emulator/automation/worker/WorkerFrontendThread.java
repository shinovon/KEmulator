package emulator.automation.worker;

import emulator.Emulator;
import emulator.ui.swt.SWTFrontend;
import java.util.concurrent.Callable;

final class WorkerFrontendThread {
	private WorkerFrontendThread() {
	}

	private static RuntimeException propagate(Throwable error) {
		if (error instanceof RuntimeException) {
			return (RuntimeException) error;
		}

		return new RuntimeException(error);
	}

	@SuppressWarnings("unchecked")
	static <T> T call(final Callable<T> callable) {
		if (Emulator.getEmulator() instanceof SWTFrontend) {
			final Object[] result = new Object[1];
			final Throwable[] error = new Throwable[1];
			SWTFrontend.syncExec(new Runnable() {
				public void run() {
					try {
						result[0] = callable.call();
					} catch (Throwable t) {
						error[0] = t;
					}
				}
			});
			if (error[0] != null) {
				throw propagate(error[0]);
			}

			return (T) result[0];
		}

		try {
			return callable.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
