package javax.microedition.amms.control.imageeffect;

import javax.microedition.amms.control.EffectControl;

public abstract interface ChorusControl extends EffectControl {
	public abstract int setWetLevel(int paramInt);

	public abstract int getWetLevel();

	public abstract void setModulationRate(int paramInt);

	public abstract int getModulationRate();

	public abstract int getMinModulationRate();

	public abstract int getMaxModulationRate();

	public abstract void setModulationDepth(int paramInt);

	public abstract int getModulationDepth();

	public abstract int getMaxModulationDepth();

	public abstract void setAverageDelay(int paramInt);

	public abstract int getAverageDelay();

	public abstract int getMaxAverageDelay();
}
