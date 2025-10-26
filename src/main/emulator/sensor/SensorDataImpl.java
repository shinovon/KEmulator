package emulator.sensor;

import javax.microedition.sensor.ChannelInfo;
import javax.microedition.sensor.Data;

public final class SensorDataImpl implements Data {
	private ChannelInfo channelInfo;
	private boolean isTimestampIncluded;
	private long[] timeStamps;
	private boolean isUncertaintyIncluded;
	private float[] uncertainties;
	private boolean isValiditesIncluded;
	private boolean[] validities;
	private int dataType;
	private int bufferSize;
	private Object[] objectValues;

	SensorDataImpl(final ChannelInfo channelInfo, final int bufferSize, final int dataType, final boolean isTimestampIncluded, final boolean isUncertaintyIncluded, final boolean isValidityIncluded) {
		super();
		this.channelInfo = channelInfo;
		this.isTimestampIncluded = isTimestampIncluded;
		if (isTimestampIncluded) {
			this.timeStamps = new long[bufferSize];
		}
		this.isUncertaintyIncluded = isUncertaintyIncluded;
		if (isUncertaintyIncluded) {
			this.uncertainties = new float[bufferSize];
		}
		this.isValiditesIncluded = isValidityIncluded;
		if (isValidityIncluded) {
			this.validities = new boolean[bufferSize];
		}
		this.dataType = dataType;
		this.objectValues = new Object[bufferSize];
		this.bufferSize = bufferSize;
	}

	public final ChannelInfo getChannelInfo() {
		return this.channelInfo;
	}

	public final double[] getDoubleValues() {
		if (this.dataType != 1) {
			throw new IllegalStateException("Data type is not double");
		}
		double[] retValues = new double[0];
		if (this.objectValues != null && this.bufferSize > 0) {
			retValues = new double[this.bufferSize];
			for (int i = 0; i < this.bufferSize; ++i) {
				final Object tmp;
				if ((tmp = this.objectValues[i]) instanceof Double) {
					retValues[i] = (Double) tmp;
				} else {
					retValues[i] = 0.0;
					if (this.isValiditesIncluded && this.validities[i]) {
						this.validities[i] = false;
					}
				}
			}
		}
		return retValues;
	}

	public final int[] getIntValues() {
		if (this.dataType != 2) {
			throw new IllegalStateException("Data type is not int");
		}
		int[] retValues = new int[0];
		if (this.objectValues != null && this.bufferSize > 0) {
			retValues = new int[this.bufferSize];
			for (int i = 0; i < this.bufferSize; ++i) {
				final Object tmp;
				if ((tmp = this.objectValues[i]) instanceof Integer) {
					retValues[i] = (Integer) tmp;
				} else {
					retValues[i] = 0;
					if (this.isValiditesIncluded && this.validities[i]) {
						this.validities[i] = false;
					}
				}
			}
		}
		return retValues;
	}

	public final Object[] getObjectValues() {
		if (this.dataType != 4) {
			throw new IllegalStateException("Data type is not Object");
		}
		Object[] retValues = new Object[0];
		if (this.objectValues != null) {
			if (this.bufferSize == this.objectValues.length) {
				retValues = this.objectValues;
			} else if (this.bufferSize > 0) {
				retValues = new Object[this.bufferSize];
				System.arraycopy(this.objectValues, 0, retValues, 0, this.bufferSize);
			}
		}
		return retValues;
	}

	public final long getTimestamp(final int index) {
		if (!this.isTimestampIncluded) {
			throw new IllegalStateException("Timestamp wasn't requested");
		}
		return this.timeStamps[index];
	}

	public final float getUncertainty(final int index) {
		if (!this.isUncertaintyIncluded) {
			throw new IllegalStateException("Uncertainty wasn't requested");
		}
		return this.uncertainties[index];
	}

	public final boolean isValid(final int index) {
		if (!this.isValiditesIncluded) {
			throw new IllegalStateException("Validity wasn't requested");
		}
		return this.validities[index];
	}

	final void setBufferSize(final int bufferSize) {
		if (this.bufferSize < bufferSize) {
			long[] tmpTimeStamps = null;
			float[] tmpUncertainties = null;
			boolean[] tmpValidities = null;
			if (this.timeStamps != null) {
				tmpTimeStamps = new long[bufferSize];
				System.arraycopy(this.timeStamps, 0, tmpTimeStamps, 0, this.timeStamps.length);
			}
			if (this.uncertainties != null) {
				tmpUncertainties = new float[bufferSize];
				System.arraycopy(this.uncertainties, 0, tmpUncertainties, 0, this.uncertainties.length);
			}
			if (this.validities != null) {
				tmpValidities = new boolean[bufferSize];
				System.arraycopy(this.validities, 0, tmpValidities, 0, this.validities.length);
			}
			final Object[] tmpObjectValues = new Object[bufferSize];
			System.arraycopy(this.objectValues, 0, tmpObjectValues, 0, this.objectValues.length);
			this.timeStamps = tmpTimeStamps;
			this.uncertainties = tmpUncertainties;
			this.validities = tmpValidities;
			this.objectValues = tmpObjectValues;
		}
		this.bufferSize = bufferSize;
	}

	final void setData(final int index, final Object item) {
		this.objectValues[index] = item;
	}

	final void setTimestamp(final int index, final long timestamp) {
		if (this.isTimestampIncluded) {
			this.timeStamps[index] = timestamp;
		}
	}

	final void setUncertainty(final int index, final float uncertainty) {
		if (this.isUncertaintyIncluded) {
			this.uncertainties[index] = uncertainty;
		}
	}

	final void setValidities(final int index, final boolean validity) {
		if (this.isValiditesIncluded) {
			this.validities[index] = validity;
		}
	}
}
