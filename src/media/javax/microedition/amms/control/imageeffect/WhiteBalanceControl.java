package javax.microedition.amms.control.imageeffect;

import javax.microedition.amms.control.EffectControl;

public abstract interface WhiteBalanceControl extends EffectControl {
	public static final int AUTO = -1000;
	public static final int NEXT = -1001;
	public static final int PREVIOUS = -1002;
	public static final int UNKNOWN = -1004;

	public abstract int setColorTemp(int paramInt);

	public abstract int getColorTemp();

	public abstract int getMinColorTemp();

	public abstract int getMaxColorTemp();

	public abstract int getNumberOfSteps();
}
