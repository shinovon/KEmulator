package emulator.sensor;

import javax.microedition.sensor.*;
import java.io.IOException;

public final class Sensor implements SensorInfo, SensorConnection, ChannelDataListener {
    private String description;
    private String contextType;
    private String model;
    private int maxBufferSize;
    private int connType;
    private String quantity;
    private SensorDevice sensorDevice;
    private int channelCount;
    private DataImpl[] retData;
    private DataListener listener;
    private int channelStatus;
    private int channelNumber;
    private int listeningProcessCounter;
    SensorProperties props;
    private volatile int state;
    private volatile boolean ignoreData;
    private ChannelImpl[] channels;
    private boolean isSensorAvailabilityPushSupported;
    private boolean isSensorConditionPushSupported;
    private boolean isNotify;

    Sensor(final int n, final String description, final String quantity, final String contextType, final String model, final int maxBufferSize, final int connType, final SensorProperties props, final boolean isSensorAvailabilityPushSupported, final boolean isSensorConditionPushSupported, final ChannelImpl[] channels, final SensorDevice sensorDevice) {
        super();
        this.sensorDevice = null;
        this.listeningProcessCounter = 0;
        this.description = description;
        this.quantity = quantity;
        this.contextType = contextType;
        this.model = model;
        this.maxBufferSize = maxBufferSize;
        this.connType = connType;
        this.props = props;
        this.channels = channels;
        this.isSensorAvailabilityPushSupported = isSensorAvailabilityPushSupported;
        this.isSensorConditionPushSupported = isSensorConditionPushSupported;
        this.sensorDevice = sensorDevice;
        if (channels != null && channels.length > 0) {
            for (int i = 0; i < channels.length; ++i) {
                channels[i].setSensor(this);
            }
        }
        this.state = 4;
    }

    public final ChannelInfo[] getChannelInfos() {
        final ChannelInfo[] array = new ChannelInfo[this.channels.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.channels[i];
        }
        return array;
    }

    public final int getConnectionType() {
        return this.connType;
    }

    public final String getContextType() {
        return this.contextType;
    }

    public final String getDescription() {
        return this.description;
    }

    public final int getMaxBufferSize() {
        return this.maxBufferSize;
    }

    public final String getModel() {
        return this.model;
    }

    public final String getQuantity() {
        return this.quantity;
    }

    public final String getUrl() {
        return SensorUrl.getUrl(this);
    }

    public final boolean isAvailabilityPushSupported() {
        return this.isSensorAvailabilityPushSupported;
    }

    public final boolean isAvailable() {
        return this.sensorDevice.isAvailable();
    }

    public final boolean isConditionPushSupported() {
        return this.isSensorConditionPushSupported;
    }

    public final Object getProperty(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        final Object property;
        if ((property = this.props.getProperty(s)) == null) {
            throw new IllegalArgumentException();
        }
        return property;
    }

    public final String[] getPropertyNames() {
        return this.props.getPropertyNames();
    }

    public final boolean matches(final String s, final String s2) {
        return (s == null || s.equals(this.quantity)) && (s2 == null || s2.equals(this.contextType));
    }

    public final boolean matches(final SensorUrl d) {
        final String s = (String) this.props.getProperty("location");
        return this.quantity.equals(d.getQuantity()) && (d.getContextType() == null || d.getContextType().equals(this.contextType)) && (d.getLocation() == null || d.getLocation().equals(s)) && (d.getModel() == null || d.getModel().equals(this.model));
    }

    public final synchronized void open() throws IOException {
        this.ignoreData = false;
        if (this.state != 4) {
            throw new IOException("Sensor is already opened");
        }
        if (!(true & this.sensorDevice.start())) {
            throw new IOException("Sensor start fails");
        }
        this.state = 1;
    }

    public final int getState() {
        return this.state;
    }

    public final Channel getChannel(final ChannelInfo channelInfo) {
        if (channelInfo == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < this.channels.length; ++i) {
            if (channelInfo == this.channels[i]) {
                return this.channels[i];
            }
        }
        throw new IllegalArgumentException("This channel is not from this sensor");
    }
    //method240 is unknown | 05.11.2025 and now is known
    protected final void populateChannelObjectValues(final int n, final Object[] array, final int n2) {
        if (n2 == 1) {
            array[0] = new Double(this.sensorDevice.getNormalizedAngle(n));
        } else {
            if (n2 != 2) {
                return;
            }
            // constant from Loco Roco
            array[0] = new Integer((int) (sensorDevice.getNormalizedAngle(n) / (0.7853982F / 1000)));
        }
    }

    public final Data[] getData(final int n) throws IOException {
        return this.getData(n, 0L, false, false, false);
    }

    public final synchronized Data[] getData(int bufferSize, final long bufferingPeriod, final boolean isTimestampIncluded, final boolean isUncertaintyIncluded, final boolean isValidityIncluded) throws IOException {
        if ((bufferSize < 1 && bufferingPeriod < 1L) || bufferSize > this.maxBufferSize) {
            throw new IllegalArgumentException("Wrong buffer size or/and period values");
        }
        if (this.state == 2) {
            throw new IllegalStateException("Wrong state");
        }
        if (this.state == 4) {
            throw new IOException("Wrong state");
        }
        if (bufferSize < 1) {
            bufferSize = this.maxBufferSize;
        }
        this.listener = null;
        this.channelCount = 0;
        this.channelStatus = 0;
        this.retData = new DataImpl[this.channels.length];
        for (int i = 0; i < this.channels.length; ++i) {
            this.channels[i].startDataCollection(this, bufferSize, bufferingPeriod, isTimestampIncluded, isUncertaintyIncluded, isValidityIncluded, false);
        }
        this.isNotify = false;
        while (!this.isNotify) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        if (this.channelStatus != 0) {
            throw new IOException("Read data error with code " + this.channelStatus + " on channel " + this.channelNumber);
        }
        return this.retData;
    }

    public final void channelDataReceived(final int n, final DataImpl l) {
        if (!this.ignoreData) {
            this.retData[n] = l;
            Object listener = this;
            final int channelCount;
            synchronized (this) {
                ++this.channelCount;
                channelCount = this.channelCount;
            }
            if (channelCount == this.channels.length) {
                synchronized (this) {
                    if ((listener = this.listener) == null && this.state == 1) {
                        this.isNotify = true;
                        this.notify();
                    }
                }
                if (listener != null) {
                    final boolean b = this.listeningProcessCounter != 0;
                    ++this.listeningProcessCounter;
                    ((DataListener) listener).dataReceived(this, this.retData, b);
                    --this.listeningProcessCounter;
                    synchronized (this) {
                        this.channelCount = 0;
                    }
                }
            }
        }
    }

    public final void channelErrorReceived(final int number, final int errorCode) {
        synchronized (this) {
            if (this.state != 4) {
                this.channelStatus = errorCode;
                this.channelNumber = number;
                this.isNotify = true;
                this.notify();
            }
        }
    }

    public final SensorInfo getSensorInfo() {
        return this;
    }

    public final void removeDataListener() {
        if (this.state == 4) {
            throw new IllegalStateException("Connection is already closed");
        }
        if (this.state == 2) {
            final boolean aBoolean313 = this.ignoreData;
            this.ignoreData = true;
            for (int i = 0; i < this.channels.length; ++i) {
                this.channels[i].stopGetData();
            }
            synchronized (this) {
                this.listener = null;
            }
            this.ignoreData = aBoolean313;
            this.state = 1;
        }
    }

    public final void setDataListener(final DataListener dataListener, final int n) {
        this.setDataListener(dataListener, n, 0L, false, false, false);
    }

    public final synchronized void setDataListener(final DataListener listener, int bufferSize, final long n, final boolean b, final boolean b2, final boolean b3) {
        if ((bufferSize < 1 && n < 1L) || bufferSize > this.maxBufferSize) {
            throw new IllegalArgumentException("Wrong buffer size or/and period values");
        }
        if (this.state == 4) {
            throw new IllegalStateException("Connection is closed");
        }
        if (listener == null) {
            throw new NullPointerException("Listener is null");
        }
        if (this.state == 2) {
            this.removeDataListener();
        }
        this.state = 2;
        if (bufferSize < 1) {
            bufferSize = this.maxBufferSize;
        }
        this.listener = listener;
        this.channelCount = 0;
        this.channelStatus = 0;
        this.listeningProcessCounter = 0;
        this.retData = new DataImpl[this.channels.length];
        for (int i = 0; i < this.channels.length; ++i) {
            this.channels[i].startDataCollection(this, bufferSize, n, b, b2, b3, true);
        }
    }

    public final void close() throws IOException {
        this.ignoreData = true;
        this.sensorDevice.shutdownSensor();
        if (this.state == 2) {
            this.removeDataListener();
        }
        this.state = 4;
    }

    public final String toString() {
        return super.toString() + "{ quantity=" + this.quantity + " contextType=" + this.contextType + " model=" + this.model + " prop:location=" + this.props.getProperty("location") + "}";
    }
}
