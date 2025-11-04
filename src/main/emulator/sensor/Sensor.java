package emulator.sensor;

import javax.microedition.sensor.*;
import java.io.IOException;
//https://github.com/hbao/phonemefeaturedevices/blob/master/VirtualMachine/phoneme_feature/jsr256/src/share/classes/com/sun/javame/sensor/Sensor.java
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
    private volatile boolean aBoolean313; // unknown
    private ChannelImpl[] channels;
    private boolean isSensorAvailabilityPushSupported;
    private boolean isSensorConditionPushSupported;
    private boolean isNotify;

    Sensor(final int n, final String aString314, final String aString315, final String aString316, final String aString317, final int anInt448, final int anInt449, final SensorProperties aSensorProperties452, final boolean aBoolean440, final boolean aBoolean441, final ChannelImpl[] akArray453, final SensorDevice sensorDevice) {
        super();
        this.sensorDevice = null;
        this.listeningProcessCounter = 0;
        this.description = aString314;
        this.quantity = aString315;
        this.contextType = aString316;
        this.model = aString317;
        this.maxBufferSize = anInt448;
        this.connType = anInt449;
        this.props = aSensorProperties452;
        this.channels = akArray453;
        this.isSensorAvailabilityPushSupported = aBoolean440;
        this.isSensorConditionPushSupported = aBoolean441;
        this.sensorDevice = sensorDevice;
        if (akArray453 != null && akArray453.length > 0) {
            for (int i = 0; i < akArray453.length; ++i) {
                akArray453[i].setSensor(this);
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
        this.aBoolean313 = false;
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
    //method240 is unknown
    protected final void method240(final int n, final Object[] array, final int n2) {
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

    public final synchronized Data[] getData(int anInt448, final long n, final boolean b, final boolean b2, final boolean b3) throws IOException {
        if ((anInt448 < 1 && n < 1L) || anInt448 > this.maxBufferSize) {
            throw new IllegalArgumentException("Wrong buffer size or/and period values");
        }
        if (this.state == 2) {
            throw new IllegalStateException("Wrong state");
        }
        if (this.state == 4) {
            throw new IOException("Wrong state");
        }
        if (anInt448 < 1) {
            anInt448 = this.maxBufferSize;
        }
        this.listener = null;
        this.channelCount = 0;
        this.channelStatus = 0;
        this.retData = new DataImpl[this.channels.length];
        for (int i = 0; i < this.channels.length; ++i) {
            this.channels[i].startDataCollection(this, anInt448, n, b, b2, b3, false);
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
        if (!this.aBoolean313) {
            this.retData[n] = l;
            Object aDataListener451 = this;
            final int anInt455;
            synchronized (this) {
                ++this.channelCount;
                anInt455 = this.channelCount;
            }
            if (anInt455 == this.channels.length) {
                synchronized (this) {
                    if ((aDataListener451 = this.listener) == null && this.state == 1) {
                        this.isNotify = true;
                        this.notify();
                    }
                }
                if (aDataListener451 != null) {
                    final boolean b = this.listeningProcessCounter != 0;
                    ++this.listeningProcessCounter;
                    ((DataListener) aDataListener451).dataReceived(this, this.retData, b);
                    --this.listeningProcessCounter;
                    synchronized (this) {
                        this.channelCount = 0;
                    }
                }
            }
        }
    }

    public final void channelErrorReceived(final int anInt459, final int anInt460) {
        synchronized (this) {
            if (this.state != 4) {
                this.channelStatus = anInt460;
                this.channelNumber = anInt459;
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
            final boolean aBoolean313 = this.aBoolean313;
            this.aBoolean313 = true;
            for (int i = 0; i < this.channels.length; ++i) {
                this.channels[i].stopGetData();
            }
            synchronized (this) {
                this.listener = null;
            }
            this.aBoolean313 = aBoolean313;
            this.state = 1;
        }
    }

    public final void setDataListener(final DataListener dataListener, final int n) {
        this.setDataListener(dataListener, n, 0L, false, false, false);
    }

    public final synchronized void setDataListener(final DataListener aDataListener451, int anInt448, final long n, final boolean b, final boolean b2, final boolean b3) {
        if ((anInt448 < 1 && n < 1L) || anInt448 > this.maxBufferSize) {
            throw new IllegalArgumentException("Wrong buffer size or/and period values");
        }
        if (this.state == 4) {
            throw new IllegalStateException("Connection is closed");
        }
        if (aDataListener451 == null) {
            throw new NullPointerException("Listener is null");
        }
        if (this.state == 2) {
            this.removeDataListener();
        }
        this.state = 2;
        if (anInt448 < 1) {
            anInt448 = this.maxBufferSize;
        }
        this.listener = aDataListener451;
        this.channelCount = 0;
        this.channelStatus = 0;
        this.listeningProcessCounter = 0;
        this.retData = new DataImpl[this.channels.length];
        for (int i = 0; i < this.channels.length; ++i) {
            this.channels[i].startDataCollection(this, anInt448, n, b, b2, b3, true);
        }
    }

    public final void close() throws IOException {
        this.aBoolean313 = true;
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
