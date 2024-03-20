package emulator.media.vlc;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

import uk.co.caprica.vlcj.media.callback.DefaultCallbackMedia;
import uk.co.caprica.vlcj.media.callback.nonseekable.NonSeekableInputStreamMedia;

public class VLCCallbackSourceStream extends DefaultCallbackMedia {

    private DataSource src;
    private SourceStream srcStream;

    public VLCCallbackSourceStream(DataSource src) {
        super((src.getStreams()[0]).getSeekType() == 2);
        this.src = src;
        this.srcStream = src.getStreams()[0];
    }

    @Override
    protected int onRead(byte[] buffer, int bufferSize) throws IOException {
        return srcStream.read(buffer, 0, bufferSize);
    }

    @Override
    protected long onGetSize() {
        return srcStream.getContentLength();
    }

    @Override
    protected boolean onOpen() {
        try {
            src.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected boolean onSeek(long offset) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void onClose() {
        try {
            src.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
