package com.nttdocomo.ui;

public abstract class MApplication extends IApplication {
	public static final int MODE_CHANGED_EVENT = 1;
	public static final int WAKEUP_TIMER_EVENT = 2;
	public static final int CLOCK_TICK_EVENT = 3;
	public static final int FOLD_CHANGED_EVENT = 4;
	public static final int SMS_NOTIFY_EVENT = 5;

	public void processSystemEvent(final int n, final int n2) {
	}

	public final void sleep() {
	}

	public final void setWakeupTimer(final int n) {
	}

	public final int getWakeupTimer() {
		return -1;
	}

	public final void resetWakeupTimer() {
	}

	public final void setClockTick(final boolean b) {
	}

	public final void deactivate() {
	}
}
