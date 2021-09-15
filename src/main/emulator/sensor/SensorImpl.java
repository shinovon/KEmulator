package emulator.sensor;

import java.io.*;
import javax.microedition.sensor.*;

public final class SensorImpl implements SensorInfo, SensorConnection, ChannelDataListener
{
    private String description;
    private String contextType;
    private String model;
    private int maxBufferSize;
    private int connectionType;
    private String quantity;
    private a ana449;
    private int anInt455;
    private SensorDataImpl[] alArray450;
    private DataListener aDataListener451;
    private int anInt457;
    private int anInt459;
    private int anInt460;
    SensorProperties properties;
    private volatile int state;
    private volatile boolean aBoolean313;
    private k[] channelInfos;
    private boolean availabilityPushSup;
    private boolean conditionPushSup;
    private boolean aBoolean458;
    
    SensorImpl(final int n, final String aString314, final String aString315, final String aString316, final String aString317, final int anInt448, final int anInt449, final SensorProperties aSensorProperties452, final boolean aBoolean440, final boolean aBoolean441, final k[] akArray453, final a ana449) {
        super();
        this.ana449 = null;
        this.anInt460 = 0;
        this.description = aString314;
        this.quantity = aString315;
        this.contextType = aString316;
        this.model = aString317;
        this.maxBufferSize = anInt448;
        this.connectionType = anInt449;
        this.properties = aSensorProperties452;
        this.channelInfos = akArray453;
        this.availabilityPushSup = aBoolean440;
        this.conditionPushSup = aBoolean441;
        this.ana449 = ana449;
        if (akArray453 != null && akArray453.length > 0) {
            for (int i = 0; i < akArray453.length; ++i) {
                akArray453[i].method275(this);
            }
        }
        this.state = 4;
    }
    
    public final ChannelInfo[] getChannelInfos() {
        final ChannelInfo[] array = new ChannelInfo[this.channelInfos.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.channelInfos[i];
        }
        return array;
    }
    
    public final int getConnectionType() {
        return this.connectionType;
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
        return SensorUtil.getUrl(this);
    }
    
    public final boolean isAvailabilityPushSupported() {
        return this.availabilityPushSup;
    }
    
    public final boolean isAvailable() {
        return this.ana449.isAvailable();
    }
    
    public final boolean isConditionPushSupported() {
        return this.conditionPushSup;
    }
    
    public final Object getProperty(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        final Object property;
        if ((property = this.properties.getProperty(s)) == null) {
            throw new IllegalArgumentException();
        }
        return property;
    }
    
    public final String[] getPropertyNames() {
        return this.properties.getPropertyNames();
    }
    
    public final boolean method237(final String s, final String s2) {
        return (s == null || s.equals(this.quantity)) && (s2 == null || s2.equals(this.contextType));
    }
    
    public final boolean method238(final SensorUtil d) {
        final String s = (String)this.properties.getProperty("location");
        return this.quantity.equals(d.method252()) && (d.method254() == null || d.method254().equals(this.contextType)) && (d.method255() == null || d.method255().equals(s)) && (d.method246() == null || d.method246().equals(this.model));
    }
    
    public final synchronized void method239() throws IOException {
        this.aBoolean313 = false;
        if (this.state != 4) {
            throw new IOException("Sensor is already opened");
        }
        if (!(true & this.ana449.start())) {
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
        for (int i = 0; i < this.channelInfos.length; ++i) {
            if (channelInfo == this.channelInfos[i]) {
                return this.channelInfos[i];
            }
        }
        throw new IllegalArgumentException("This channel is not from this sensor");
    }
    
    protected final void method240(final int n, final Object[] array, final int n2) {
        Object[] array2;
        int n3;
        Number n4;
        if (n2 == 1) {
            array2 = array;
            n3 = 0;
            n4 = new Double(this.ana449.method223(n));
        }
        else {
            if (n2 != 2) {
                return;
            }
            array2 = array;
            n3 = 0;
            n4 = new Integer((int)this.ana449.method223(n));
        }
        array2[n3] = n4;
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
        this.aDataListener451 = null;
        this.anInt455 = 0;
        this.anInt457 = 0;
        this.alArray450 = new SensorDataImpl[this.channelInfos.length];
        for (int i = 0; i < this.channelInfos.length; ++i) {
            this.channelInfos[i].method276(this, anInt448, n, b, b2, b3, false);
        }
        this.aBoolean458 = false;
        while (!this.aBoolean458) {
            try {
                this.wait();
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        if (this.anInt457 != 0) {
            throw new IOException("Read data error with code " + this.anInt457 + " on channel " + this.anInt459);
        }
        return this.alArray450;
    }
    
    public final void channelDataReceived(final int n, final SensorDataImpl l) {
        if (!this.aBoolean313) {
            this.alArray450[n] = l;
            Object aDataListener451 = this;
            final int anInt455;
            synchronized (this) {
                ++this.anInt455;
                anInt455 = this.anInt455;
            }
            if (anInt455 == this.channelInfos.length) {
                synchronized (this) {
                    if ((aDataListener451 = this.aDataListener451) == null && this.state == 1) {
                        this.aBoolean458 = true;
                        this.notify();
                    }
                }
                if (aDataListener451 != null) {
                    final boolean b = this.anInt460 != 0;
                    ++this.anInt460;
                    ((DataListener)aDataListener451).dataReceived(this, this.alArray450, b);
                    --this.anInt460;
                    synchronized (this) {
                        this.anInt455 = 0;
                    }
                }
            }
        }
    }
    
    public final void channelErrorReceived(final int anInt459, final int anInt460) {
        synchronized (this) {
            if (this.state != 4) {
                this.anInt457 = anInt460;
                this.anInt459 = anInt459;
                this.aBoolean458 = true;
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
            for (int i = 0; i < this.channelInfos.length; ++i) {
                this.channelInfos[i].method277();
            }
            synchronized (this) {
                this.aDataListener451 = null;
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
        this.aDataListener451 = aDataListener451;
        this.anInt455 = 0;
        this.anInt457 = 0;
        this.anInt460 = 0;
        this.alArray450 = new SensorDataImpl[this.channelInfos.length];
        for (int i = 0; i < this.channelInfos.length; ++i) {
            this.channelInfos[i].method276(this, anInt448, n, b, b2, b3, true);
        }
    }
    
    public final void close() throws IOException {
        this.aBoolean313 = true;
        this.ana449.method225();
        if (this.state == 2) {
            this.removeDataListener();
        }
        this.state = 4;
    }
    
    public final String toString() {
        return super.toString() + "{ quantity=" + this.quantity + " contextType=" + this.contextType + " model=" + this.model + " prop:location=" + this.properties.getProperty("location") + "}";
    }
}
