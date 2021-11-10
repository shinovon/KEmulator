package javax.microedition.media;

import javax.microedition.media.control.*;

public class ToneImpl implements Player
{
    Control toneControl;
    Control volumeControl;
    Control[] controls;
    
    public ToneImpl() {
        super();
        this.toneControl = new ToneControlImpl();
        this.volumeControl = new VolumeControlImpl(this);
        this.controls = new Control[] { this.toneControl, this.volumeControl };
    }
    
    public void addPlayerListener(final PlayerListener playerListener) throws IllegalStateException {
    }
    
    public void close() {
    }
    
    public void deallocate() throws IllegalStateException {
    }
    
    public String getContentType() {
        return null;
    }
    
    public long getDuration() {
        return 0L;
    }
    
    public long getMediaTime() {
        return 0L;
    }
    
    public int getState() {
        return 0;
    }
    
    public void prefetch() throws IllegalStateException, MediaException {
    }
    
    public void realize() throws IllegalStateException, MediaException {
    }
    
    public void removePlayerListener(final PlayerListener playerListener) throws IllegalStateException {
    }
    
    public void setLoopCount(final int n) {
    }
    
    public long setMediaTime(final long n) {
        return 0L;
    }
    
    public void start() throws IllegalStateException, MediaException {
    }
    
    public void stop() throws IllegalStateException, MediaException {
    }
    
    public Control getControl(final String s) {
        if (s == "VolumeControl") {
            return this.volumeControl;
        }
        if (s == "ToneControl") {
            return this.toneControl;
        }
        return null;
    }
    
    public Control[] getControls() {
        return this.controls;
    }
    
    public TimeBase getTimeBase() {
        return null;
    }
    
    public void setTimeBase(final TimeBase timeBase) throws MediaException {
    }
}
