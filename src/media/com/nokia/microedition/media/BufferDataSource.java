package com.nokia.microedition.media;

import java.io.IOException;
import javax.microedition.media.Control;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

public class BufferDataSource extends DataSource {
    protected DataSource iDataSource;
    protected BufferSourceStream iSourceStream;

    public BufferDataSource(DataSource aDataSource) throws IOException {
        super(aDataSource.getLocator());
        this.iDataSource = aDataSource;
        SourceStream ss = aDataSource.getStreams()[0];
        this.iSourceStream = new BufferSourceStream(ss);
    }

    public byte[] getHeader() throws IOException {
        return this.iSourceStream.getHeader();
    }

    public int readAndBuffer(byte[] aBuffer, int aOffset, int aLength) throws IOException {
        return this.iSourceStream.readAndBuffer(aBuffer, aOffset, aLength);
    }

    public String getContentType() {
        return this.iDataSource.getContentType();
    }

    public void connect() throws IOException {
        this.iDataSource.connect();
    }

    public void disconnect() {
        this.iDataSource.disconnect();
    }

    public void start() throws IOException {
        this.iDataSource.start();
    }

    public void stop() throws IOException {
        this.iDataSource.stop();
    }

    public SourceStream[] getStreams() {
        SourceStream[] originalStreams = this.iDataSource.getStreams();
        SourceStream[] streams = new SourceStream[originalStreams.length];
        System.arraycopy(originalStreams, 0, streams, 0, originalStreams.length);

        streams[0] = this.iSourceStream;
        return streams;
    }

    public Control[] getControls() {
        return this.iDataSource.getControls();
    }

    public Control getControl(String aControlType) {
        return this.iDataSource.getControl(aControlType);
    }
}
