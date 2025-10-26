package emulator.sensor;

import javax.microedition.sensor.*;
import java.util.Calendar;
import java.util.Vector;
//k class must be some reimplementation of ChannelImpl.java
//https://github.com/hbao/phonemefeaturedevices/blob/master/VirtualMachine/phoneme_feature/jsr256/src/share/classes/com/sun/javame/sensor/ChannelImpl.java#L84
public final class k implements Channel, ChannelInfo, Runnable {
	private MeasurementRange[] ranges;
	private String name;
	private int channelNumber;
	private int dataType;
	private int scale;
	private Unit unit;
	private float accuracy;
	private Vector conditions;
	private SensorImpl sensor;
	private Thread dataThread;
	private ChannelDataListener dataListener;
	private int bufferSize;
	private boolean isTimestampIncluded;
	private boolean isUncertaintyIncluded;
	private boolean isValidityIncluded;
	private volatile boolean isStopped;
	private boolean isRepeat;
	private Object lastValue;

	public k(final int n, final int channelNumber, final String name, final int dataType, final float accuracy, final int scale, final Unit unit, final MeasurementRange[] ranges) {
		super();
		this.conditions = new Vector();
		this.lastValue = null;
		this.name = name;
		this.channelNumber = channelNumber;
		this.dataType = dataType;
		this.accuracy = accuracy;
		this.scale = scale;
		this.unit = unit;
		this.ranges = ranges;
	}

	final synchronized Condition[] getAllConditions() {
		Condition[] array = null;
		if (this.conditions.size() > 0) {
			array = new Condition[this.conditions.size()];
			for (int i = 0; i < array.length; ++i) {
				array[i] = ((e) this.conditions.elementAt(i)).method265();
			}
		}
		return array;
	}

	public final float getAccuracy() {
		return this.accuracy;
	}

	public final int getDataType() {
		return this.dataType;
	}

	public final MeasurementRange[] getMeasurementRanges() {
		MeasurementRange[] array = new MeasurementRange[0];
		if (this.dataType != 4 && this.ranges != null && this.ranges.length > 0) {
			array = new MeasurementRange[this.ranges.length];
			for (int i = 0; i < this.ranges.length; ++i) {
				final MeasurementRange measurementRange = this.ranges[i];
				array[i] = new MeasurementRange(measurementRange.getSmallestValue(), measurementRange.getLargestValue(), measurementRange.getResolution());
			}
		}
		return array;
	}

	public final String getName() {
		return this.name;
	}

	public final int getScale() {
		return this.scale;
	}

	public final Unit getUnit() {
		return this.unit;
	}

	public final synchronized void addCondition(final ConditionListener conditionListener, final Condition condition) {
		if (conditionListener == null) {
			throw new NullPointerException();
		}
		if (this.sensor.getState() == 4) {
			throw new IllegalStateException();
		}
		if (condition == null) {
			throw new NullPointerException();
		}
		final boolean b;
		if (((b = (condition instanceof ObjectCondition)) && this.dataType != 4) || (!b && this.dataType == 4)) {
			throw new IllegalArgumentException();
		}
		final int size = this.conditions.size();
		final e e = new e(conditionListener, condition);
		for (int i = 0; i < size; ++i) {
			if (((e) this.conditions.elementAt(i)).method266(e)) {
				return;
			}
		}
		this.conditions.addElement(e);
	}

	public final ChannelInfo getChannelInfo() {
		return this;
	}

	public final synchronized Condition[] getConditions(final ConditionListener conditionListener) {
		if (conditionListener == null) {
			throw new NullPointerException();
		}
		final int size = this.conditions.size();
		final Vector vector = new Vector<Condition>(size);
		for (int i = 0; i < size; ++i) {
			final e e;
			if ((e = (emulator.sensor.e) this.conditions.elementAt(i)).method267(conditionListener)) {
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
		if (this.sensor.getState() == 4) {
			throw new IllegalStateException();
		}
		this.conditions.removeAllElements();
	}

	public final synchronized void removeCondition(final ConditionListener conditionListener, final Condition condition) {
		if (conditionListener == null || condition == null) {
			throw new NullPointerException();
		}
		if (this.sensor.getState() == 4) {
			throw new IllegalStateException();
		}
		for (int size = this.conditions.size(), i = 0; i < size; ++i) {
			if (((e) this.conditions.elementAt(i)).method268(conditionListener, condition)) {
				this.conditions.removeElementAt(i);
				return;
			}
		}
	}

	public final synchronized void removeConditionListener(final ConditionListener conditionListener) {
		if (conditionListener == null) {
			throw new NullPointerException();
		}
		if (this.sensor.getState() == 4) {
			throw new IllegalStateException();
		}
		for (int i = 0; i < this.conditions.size(); ++i) {
			if (((e) this.conditions.elementAt(i)).method267(conditionListener)) {
				this.conditions.removeElementAt(i);
				--i;
			}
		}
	}

	final synchronized void setSensor(final SensorImpl aj494) {
		this.sensor = aj494;
	}

	final void startDataCollection(final ChannelDataListener aChannelDataListener496, final int anInt503, final long n, final boolean aBoolean497, final boolean aBoolean498, final boolean aBoolean499, final boolean aBoolean500) {
		this.stopThread();
		this.isStopped = false;
		this.dataListener = aChannelDataListener496;
		this.bufferSize = anInt503;
		this.isTimestampIncluded = aBoolean497;
		this.isUncertaintyIncluded = aBoolean498;
		this.isValidityIncluded = aBoolean499;
		this.isRepeat = aBoolean500;
		(this.dataThread = new Thread(this)).start();
	}

	public final void run() {
		do {
			final SensorDataImpl l = new SensorDataImpl(this, this.bufferSize, this.getDataType(), this.isTimestampIncluded, this.isUncertaintyIncluded, this.isValidityIncluded);
			int n = 0;
			while (!this.isStopped && n < this.bufferSize) {
				final Object[] array = {null};
				this.sensor.method240(this.channelNumber, array, this.getDataType());
				if (this.lastValue != null && this.lastValue.equals(array[0])) {
					continue;
				}
				this.lastValue = array[0];
				final int n2 = n;
				if ((n += array.length) > this.bufferSize) {
					l.setBufferSize(n);
				}
				for (int i = 0; i < array.length; ++i) {
					l.setData(n2 + i, array[i]);
					if (this.isTimestampIncluded) {
						l.setTimestamp(n2 + i, Calendar.getInstance().getTime().getTime());
					}
					if (this.isUncertaintyIncluded) {
						l.setUncertainty(n2 + i, 0.0f);
					}
					if (this.isValidityIncluded) {
						l.setValidities(n2 + i, true);
					}
				}
			}
			if (this.isStopped) {
				return;
			}
			if (n < 0) {
				n = 0;
			}
			if (n < this.bufferSize) {
				l.setBufferSize(n);
			}
			this.dataListener.channelDataReceived(this.channelNumber, l);
		} while (this.isRepeat && !this.isStopped);
	}

	private void stopThread() {
		this.isStopped = true;
		try {
			if (this.dataThread.isAlive()) {
				this.dataThread.join();
			}
		} catch (Exception ex) {
			Thread.currentThread().interrupt();
		}
	}

	final void method277() {
		this.stopThread();
	}
}
