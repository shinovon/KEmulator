package javax.microedition.media;

import javax.microedition.media.control.*;

public class ToneImpl implements Player
{
    Control aControl306;
    Control aControl310;
    Control[] aControlArray307;
    
    public ToneImpl() {
        super();
        this.aControl306 = new ToneControlImpl();
        this.aControl310 = new VolumeControlImpl(this);
        this.aControlArray307 = new Control[] { this.aControl306, this.aControl310 };
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
    	System.out.print('\7');
    }
    
    public void stop() throws IllegalStateException, MediaException {
    }
    
    public Control getControl(final String s) {
        if (s == "VolumeControl") {
            return this.aControl310;
        }
        if (s == "ToneControl") {
            return this.aControl306;
        }
        return null;
    }
    
    public Control[] getControls() {
        return this.aControlArray307;
    }
    
    public TimeBase getTimeBase() {
        return null;
    }
    
    public void setTimeBase(final TimeBase timeBase) throws MediaException {
    }
}
