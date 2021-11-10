package javax.microedition.amms.control.tuner;

import java.util.Date;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public abstract interface RDSControl extends Control {
	public static final String RDS_NEW_DATA = "RDS_NEW_DATA";
	public static final String RDS_NEW_ALARM = "RDS_ALARM";
	public static final String RADIO_CHANGED = "radio_changed";

	public abstract boolean isRDSSignal();

	public abstract String getPS();

	public abstract String getRT();

	public abstract short getPTY();

	public abstract String getPTYString(boolean paramBoolean);

	public abstract short getPI();

	public abstract int[] getFreqsByPTY(short paramShort);

	public abstract int[][] getFreqsByTA(boolean paramBoolean);

	public abstract String[] getPSByPTY(short paramShort);

	public abstract String[] getPSByTA(boolean paramBoolean);

	public abstract Date getCT();

	public abstract boolean getTA();

	public abstract boolean getTP();

	public abstract void setAutomaticSwitching(boolean paramBoolean) throws MediaException;

	public abstract boolean getAutomaticSwitching();

	public abstract void setAutomaticTA(boolean paramBoolean) throws MediaException;

	public abstract boolean getAutomaticTA();
}
