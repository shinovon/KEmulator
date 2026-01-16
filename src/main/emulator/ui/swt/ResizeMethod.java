/*
Copyright (c) 2025 Fyodor Ryzhov
*/
package emulator.ui.swt;

/**
 * Specifies how to fit canvas into OS window.
 */
public enum ResizeMethod {
	/**
	 * Canvas will be placed at window center. User will be able to manually zoom it in or out.
	 */
	Manual(0),
	/**
	 * Canvas will be sized to fully cover the window.
	 */
	FollowWindowSize(1),
	/**
	 * Canvas will be zoomed to cover the window, keeping aspect ratio.
	 */
	Fit(2),
	FitInteger(3);

	private final int id;

	ResizeMethod(int i) {
		id = i;
	}

	public static ResizeMethod fromInt(int value) {
		return ResizeMethod.values()[value];
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}
}
