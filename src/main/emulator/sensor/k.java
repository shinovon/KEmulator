package emulator.sensor;

import javax.microedition.sensor.*;
import java.util.Calendar;
import java.util.Vector;

public final class k implements Channel, ChannelInfo, Runnable {
	private MeasurementRange[] aMeasurementRangeArray489;
	private String aString490;
	private int anInt488;
	private int anInt499;
	private int anInt501;
	private Unit anUnit491;
	private float aFloat492;
	private Vector aVector493;
	private SensorImpl aj494;
	private Thread aThread495;
	private ChannelDataListener aChannelDataListener496;
	private int anInt503;
	private boolean aBoolean497;
	private boolean aBoolean500;
	private boolean aBoolean502;
	private volatile boolean aBoolean504;
	private boolean aBoolean505;
	private Object anObject498;

	public k(final int n, final int anInt488, final String aString490, final int anInt489, final float aFloat492, final int anInt490, final Unit anUnit491, final MeasurementRange[] aMeasurementRangeArray489) {
		super();
		this.aVector493 = new Vector();
		this.anObject498 = null;
		this.aString490 = aString490;
		this.anInt488 = anInt488;
		this.anInt499 = anInt489;
		this.aFloat492 = aFloat492;
		this.anInt501 = anInt490;
		this.anUnit491 = anUnit491;
		this.aMeasurementRangeArray489 = aMeasurementRangeArray489;
	}

	final synchronized Condition[] method274() {
		Condition[] array = null;
		if (this.aVector493.size() > 0) {
			array = new Condition[this.aVector493.size()];
			for (int i = 0; i < array.length; ++i) {
				array[i] = ((e) this.aVector493.elementAt(i)).method265();
			}
		}
		return array;
	}

	public final float getAccuracy() {
		return this.aFloat492;
	}

	public final int getDataType() {
		return this.anInt499;
	}

	public final MeasurementRange[] getMeasurementRanges() {
		MeasurementRange[] array = new MeasurementRange[0];
		if (this.anInt499 != 4 && this.aMeasurementRangeArray489 != null && this.aMeasurementRangeArray489.length > 0) {
			array = new MeasurementRange[this.aMeasurementRangeArray489.length];
			for (int i = 0; i < this.aMeasurementRangeArray489.length; ++i) {
				final MeasurementRange measurementRange = this.aMeasurementRangeArray489[i];
				array[i] = new MeasurementRange(measurementRange.getSmallestValue(), measurementRange.getLargestValue(), measurementRange.getResolution());
			}
		}
		return array;
	}

	public final String getName() {
		return this.aString490;
	}

	public final int getScale() {
		return this.anInt501;
	}

	public final Unit getUnit() {
		return this.anUnit491;
	}

	public final synchronized void addCondition(final ConditionListener conditionListener, final Condition condition) {
		if (conditionListener == null) {
			throw new NullPointerException();
		}
		if (this.aj494.getState() == 4) {
			throw new IllegalStateException();
		}
		if (condition == null) {
			throw new NullPointerException();
		}
		final boolean b;
		if (((b = (condition instanceof ObjectCondition)) && this.anInt499 != 4) || (!b && this.anInt499 == 4)) {
			throw new IllegalArgumentException();
		}
		final int size = this.aVector493.size();
		final e e = new e(conditionListener, condition);
		for (int i = 0; i < size; ++i) {
			if (((e) this.aVector493.elementAt(i)).method266(e)) {
				return;
			}
		}
		this.aVector493.addElement(e);
	}

	public final ChannelInfo getChannelInfo() {
		return this;
	}

	public final synchronized Condition[] getConditions(final ConditionListener conditionListener) {
		if (conditionListener == null) {
			throw new NullPointerException();
		}
		final int size = this.aVector493.size();
		final Vector vector = new Vector<Condition>(size);
		for (int i = 0; i < size; ++i) {
			final e e;
			if ((e = (emulator.sensor.e) this.aVector493.elementAt(i)).method267(conditionListener)) {
				vector.addElement(e.method265());
			}
		}
		final Condition[] array = new Condition[vector.size()];
		for (int j = 0; j < array.length; ++j) {
			array[j] = (Condition) vector.elementAt(j);
		}
		return array;
	}

	public final String getChannelUrl() {
		return b.method222(this);
	}

	public final synchronized void removeAllConditions() {
		if (this.aj494.getState() == 4) {
			throw new IllegalStateException();
		}
		this.aVector493.removeAllElements();
	}

	public final synchronized void removeCondition(final ConditionListener conditionListener, final Condition condition) {
		if (conditionListener == null || condition == null) {
			throw new NullPointerException();
		}
		if (this.aj494.getState() == 4) {
			throw new IllegalStateException();
		}
		for (int size = this.aVector493.size(), i = 0; i < size; ++i) {
			if (((e) this.aVector493.elementAt(i)).method268(conditionListener, condition)) {
				this.aVector493.removeElementAt(i);
				return;
			}
		}
	}

	public final synchronized void removeConditionListener(final ConditionListener conditionListener) {
		if (conditionListener == null) {
			throw new NullPointerException();
		}
		if (this.aj494.getState() == 4) {
			throw new IllegalStateException();
		}
		for (int i = 0; i < this.aVector493.size(); ++i) {
			if (((e) this.aVector493.elementAt(i)).method267(conditionListener)) {
				this.aVector493.removeElementAt(i);
				--i;
			}
		}
	}

	final synchronized void method275(final SensorImpl aj494) {
		this.aj494 = aj494;
	}

	final void method276(final ChannelDataListener aChannelDataListener496, final int anInt503, final long n, final boolean aBoolean497, final boolean aBoolean498, final boolean aBoolean499, final boolean aBoolean500) {
		this.method278();
		this.aBoolean504 = false;
		this.aChannelDataListener496 = aChannelDataListener496;
		this.anInt503 = anInt503;
		this.aBoolean497 = aBoolean497;
		this.aBoolean500 = aBoolean498;
		this.aBoolean502 = aBoolean499;
		this.aBoolean505 = aBoolean500;
		(this.aThread495 = new Thread(this)).start();
	}

	public final void run() {
		do {
			final SensorDataImpl l = new SensorDataImpl(this, this.anInt503, this.getDataType(), this.aBoolean497, this.aBoolean500, this.aBoolean502);
			int n = 0;
			while (!this.aBoolean504 && n < this.anInt503) {
				final Object[] array = {null};
				this.aj494.method240(this.anInt488, array, this.getDataType());
				if (this.anObject498 != null && this.anObject498.equals(array[0])) {
					continue;
				}
				this.anObject498 = array[0];
				final int n2 = n;
				if ((n += array.length) > this.anInt503) {
					l.method269(n);
				}
				for (int i = 0; i < array.length; ++i) {
					l.method270(n2 + i, array[i]);
					if (this.aBoolean497) {
						l.method271(n2 + i, Calendar.getInstance().getTime().getTime());
					}
					if (this.aBoolean500) {
						l.method272(n2 + i, 0.0f);
					}
					if (this.aBoolean502) {
						l.method273(n2 + i, true);
					}
				}
			}
			if (this.aBoolean504) {
				return;
			}
			if (n < 0) {
				n = 0;
			}
			if (n < this.anInt503) {
				l.method269(n);
			}
			this.aChannelDataListener496.channelDataReceived(this.anInt488, l);
		} while (this.aBoolean505 && !this.aBoolean504);
	}

	private void method278() {
		this.aBoolean504 = true;
		try {
			if (this.aThread495.isAlive()) {
				this.aThread495.join();
			}
		} catch (Exception ex) {
			Thread.currentThread().interrupt();
		}
	}

	final void method277() {
		this.method278();
	}
}
