package javax.microedition.media;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.control.FramePositioningControl;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.media.protocol.DataSource;

import emulator.Emulator;
import emulator.custom.CustomJarResources;
import emulator.graphics2D.awt.d;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.media.TrackType;
import uk.co.caprica.vlcj.media.callback.CallbackMedia;
import uk.co.caprica.vlcj.media.callback.seekable.RandomAccessFileMedia;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters;

public class VLCPlayerImpl implements Player, MediaPlayerEventListener {

    private Control[] controls;
    private String contentType;
    private int state;
    private FramePositioningControl frameControl;
    public int dataLen;
    private int loopCount;
    private Vector listeners;
    private TimeBase timeBase;
    private Thread playerThread;
    private VideoControl videoControl;
    private VolumeControl volumeControl;
    private String url;
    private String mediaUrl;
    private InputStream inputStream;
    private boolean playing;
    private Object canvas;
    public boolean isItem;
    public int locx;
    public int locy;
    public int w;
    public int h;
    public boolean visible = true;
    public int srcw;
    public int srch;
    public boolean fullscreen;
    private boolean prepared;
    public int bw;
    public int bh;
    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;
    public BufferedImage img;
    public ByteBuffer bb;
    private boolean released;
    private DataSource dataSource;
    private boolean lengthNotified;
    private int vol = -1;
    private CallbackMedia mediaCallback;
    private File tempFile;
    private static VLCPlayerImpl inst;

    static {
    }

    private boolean started;

    public static void draw(Graphics g, Object obj) {
        if (inst != null) {
            if (obj == inst.canvas) {
                inst.paint(g);
            }
        }
    }

    private void paint(Graphics g) {
        if (visible && img != null && playing) {
            if (w == 0 || h == 0) {
                w = Emulator.getEmulator().getScreen().getWidth();
                h = Emulator.getEmulator().getScreen().getHeight();
                fullscreen = true;
            }
            try {
                if (fullscreen) {
                    if (w != Emulator.getEmulator().getScreen().getWidth()
                            || h != Emulator.getEmulator().getScreen().getHeight()) {
                        w = Emulator.getEmulator().getScreen().getWidth();
                        h = Emulator.getEmulator().getScreen().getHeight();
                    }
                    BufferedImage bi = resizeProportional(img, w, h);
                    Image draw = awtImgToLcdui(bi);
                    int x = 0;
                    int y = 0;
                    if (draw.getWidth() < w)
                        x = (w - draw.getWidth()) / 2;
                    if (draw.getHeight() < h)
                        y = (h - draw.getHeight()) / 2;
                    g.drawImage(draw, x, y, 0);
                } else {
                    g.setColor(0);
                    g.fillRect(locx, locy, w, h);
                    BufferedImage bi;
                    bi = resize(img, w, h);
                    //bi = resizeProportional(img, w, h);
                    Image draw = awtImgToLcdui(bi);
                    int x = locx;
                    int y = locy;
                    //if (draw.getWidth() < w)
                    //	x = (w - draw.getWidth()) / 2;
                    //if (draw.getHeight() < h)
                    //	y = (h - draw.getHeight()) / 2;
                    g.drawImage(draw, x, y, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Image awtImgToLcdui(BufferedImage img) {
        return new Image(new d(img));
        //return new Image(new d(imgToBytes(img)));
    }

    private static byte[] imgToBytes(BufferedImage img) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = iter.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(0.5f);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(img, null, null), iwp);
            writer.dispose();
            byte[] bytes = os.toByteArray();
            os.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public VLCPlayerImpl(String url) throws IOException {
        this();
        if (url.startsWith("file:///root/")) {
            url = "file:///" + (Emulator.getAbsolutePath() + "/file/root/" + url.substring("file:///root/".length())).replace(" ", "%20");
        }
        this.url = url;
        this.mediaUrl = url;
        this.state = 100;
    }

    public VLCPlayerImpl(InputStream inputStream, String type) throws IOException {
        this();
        this.contentType = type;
        this.inputStream = inputStream;
        this.state = 100;
    }

    private VLCPlayerImpl() throws IOException {
        this.loopCount = 1;
        this.listeners = new Vector();
        frameControl = new FramePositioningControlI();
        videoControl = new VideoControlI();
        volumeControl = new VolumeControlI();
        controls = new Control[]{frameControl, videoControl, volumeControl};
        this.timeBase = Manager.getSystemTimeBase();
    }

    public VLCPlayerImpl(String contentType, DataSource src) throws IOException {
        this();
        this.contentType = contentType;
        this.dataSource = src;
        this.state = 100;
        //throw new MediaException("Video from DataStream not supported!");
    }

    public VLCPlayerImpl(String locator, String contentType, DataSource src) throws IOException {
        this();
        if (locator.startsWith("file:///root/")) {
            locator = "file:///" + (Emulator.getAbsolutePath() + "/file/root/" + url.substring("file:///root/".length())).replace(" ", "%20");
        }
        this.url = locator;
        this.mediaUrl = locator;
        this.state = 100;
        this.dataSource = src;
    }

    public VLCPlayerImpl(String url, String contentType) throws IOException {
        this();
        if (url.startsWith("file:///root/")) {
            url = "file:///" + (Emulator.getAbsolutePath() + "/file/root/" + url.substring("file:///root/".length())).replace(" ", "%20");
        }
        this.url = url;
        this.mediaUrl = url;
        this.contentType = contentType;
        this.state = 100;
    }

    public Control getControl(String s) {
        if (s.contains("FramePositioningControl")) {
            return frameControl;
        }
        if (s.contains("GUIControl") || s.contains("VideoControl")) {
            return videoControl;
        }
        if (s.contains("VolumeControl")) {
            return volumeControl;
        }
        return null;
    }

    public Control[] getControls() {
        return controls;
    }

    public void addPlayerListener(PlayerListener p0) throws IllegalStateException {
        if (this.state == 0) {
            throw new IllegalStateException();
        }
        if (p0 != null)
            listeners.add(p0);
    }

    private void load() throws MediaException {
        if (mediaUrl == null) {
            if (this.dataSource != null) {
                try {
                    this.dataSource.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new MediaException(e);
                }
            }
            if (inputStream != null) {
                if (inputStream instanceof FileInputStream) {
                    Field field;
                    try {
                        field = inputStream.getClass().getDeclaredField("path");
                        field.setAccessible(true);
                        String path = (String) field.get(inputStream);
                        File f = new File(path);
                        mediaUrl = path;
                        dataLen = (int) f.length();
                        prepared = true;
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                boolean bufferToFile = inputStream instanceof ByteArrayInputStream && false;
                if (!bufferToFile) {
                    mediaCallback = new VLCCallbackStream(inputStream, dataLen);
                } else {
                    try {
                        Manager.log("buffering to file");
                        File d = new File(System.getProperty("java.io.tmpdir"));
                        tempFile = new File(d.getPath() + File.separator + "kemtempmedia");
                        tempFile.deleteOnExit();
                        if (tempFile.exists())
                            tempFile.delete();
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        CustomJarResources.write(inputStream, fos);
                        fos.close();
                        //System.gc();
                        dataLen = (int) tempFile.length();
                        this.mediaUrl = tempFile.toString();
                        mediaCallback = new RandomAccessFileMedia(tempFile);
                        Manager.log("buffered " + mediaUrl);
						/*
						byte[] b = CustomJarResources.getBytes(inputStream);
						File d = new File(System.getProperty("java.io.tmpdir"));
						File f = new File(d.getPath() + File.separator + "kemtempvideo");
						if (f.exists())
							f.delete();
						FileOutputStream fos = new FileOutputStream(f);
						fos.write(b);
						fos.close();
						dataLen = (int) f.length();
						this.mediaUrl = f.toString();
						b = null;
						Manager.log("buffered");
						System.gc();
						*/
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new MediaException("failed to write temp file");
                    }
                }
            } else if (dataSource != null) {
                mediaCallback = new VLCCallbackSourceStream(dataSource);
            }
        }
        prepared = true;
    }

    public void prefetch() throws IllegalStateException, MediaException {
        //System.out.println("prefetch");
        if (this.state == 0) {
            throw new IllegalStateException("closed");
        }
        if (this.state == 100) {
            this.realize();
        } else if (this.state != 200) {
            return;
        }
        if (!prepared) {
            load();
            // startPaused() вместо prepare()
            try {
                if (mediaCallback != null) {
                    if (!mediaPlayer.media().startPaused(mediaCallback)) {
                        Manager.log("Failed to prepare");
                        throw new MediaException("failed to prepare");
                    }
                } else {
                    if (!mediaPlayer.media().startPaused(mediaUrl)) {
                        Manager.log("Failed to prepare");
                        throw new MediaException("failed to prepare");
                    }
                }
            } catch (MediaException e) {
                throw e;
            } catch (Exception e) {
                throw new MediaException(e);
            }
            //System.out.println("prepared");
            prepared = true;
        }
        this.state = 300;
    }

    public void realize() throws IllegalStateException, MediaException {
        if (this.state == 0) {
            throw new IllegalStateException("closed");
        }
        if (this.state == 200) {
            return;
        }
        inst = this;
        try {
            factory = new MediaPlayerFactory();
            mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();

            mediaPlayer.events().addMediaPlayerEventListener(this);

            mediaPlayer.videoSurface().set(new MyVideoSurface());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MediaException(e);
        }
        if (this.state == 100) {
            this.state = 200;
        }
    }

    public void close() {
        if (this.state == 0) {
            return;
        }
        inst = null;
        if (playing) {
            try {
                this.stop();
            } catch (Exception ex) {
            }
        }
        try {
            if (mediaPlayer == null) {
                released = true;
            } else {
                if (!released)
                    mediaPlayer.release();
                released = true;
            }
        } catch (Error e) {
            e.printStackTrace();
        }
        if (dataSource != null) {
            dataSource.disconnect();
        }
        this.state = 0;
        this.notifyListeners("closed", null);
    }

    public void deallocate() throws IllegalStateException {
        if (this.state == 0) {
            throw new IllegalStateException("closed");
        }
        if (this.state == 400) {
            try {
                this.stop();
                return;
            } catch (MediaException ex) {
                return;
            }
        }
        if (!released)
            mediaPlayer.release();
        released = true;
        if (this.state == 300) {
            state = 200;
        } else {
            if (this.state != 200) {
                return;
            }
            state = 100;
        }
        dataSource = null;
    }

    public void start() throws IllegalStateException, MediaException {
        if (this.state == 0) {
            throw new IllegalStateException("closed");
        }

        if (this.state == 100) {
            realize();
        }
        if (released)
            throw new IllegalStateException("mediaPlayer released");
        if (this.state != 400) {
            if (!prepared) {
                prefetch();
            } else {
                mediaPlayer.controls().play();
            }
            if (vol == -1) {
                notifyListeners("volumeChanged", vol = 50);
                mediaPlayer.audio().setVolume(vol);
            }
            playing = true;
            this.state = 400;
        }
    }

    private void update() {
        Emulator.getEventQueue().queueRepaint();
    }

    public void stop() throws IllegalStateException, MediaException {
        if (this.state == 0) {
            throw new IllegalStateException("closed");
        }
        if (playing) {
            mediaPlayer.controls().pause();
            playing = false;
        }
    }

    public String getContentType() {
        return contentType;
    }

    public long getDuration() {
        if (mediaPlayer == null) return 0;
        if (mediaPlayer.status() == null) return 0;
        return mediaPlayer.status().length() * 1000L;
    }

    public long getMediaTime() {
        if (mediaPlayer == null) return 0;
        if (mediaPlayer.status() == null) return 0;
        return mediaPlayer.status().time() * 1000L;
    }

    public int getState() {
        return state;
    }

    public void removePlayerListener(PlayerListener p0) throws IllegalStateException {
        if (this.state == 0) {
            throw new IllegalStateException();
        }
        if (p0 != null)
            listeners.remove(p0);
    }

    public long setMediaTime(long p0) throws MediaException {
        mediaPlayer.controls().setTime(p0 / 1000L);
        return mediaPlayer.status().time();
    }

    public Object getItem() {
        return null;
    }

    protected void notifyListeners(final String s, final Object o) {
        if ("started".equals(s)) {
            if (started) return;
            started = true;
        }
        if ("stopped".equals(s) || "endOfMedia".equals(s)) {
            started = false;
        }
        //System.out.println("notifyListeners " + s + " " + o);
        if (listeners == null)
            return;
        try {
            final Enumeration<PlayerListener> elements = this.listeners.elements();
            while (elements.hasMoreElements()) {
                elements.nextElement().playerUpdate(this, s.intern(), o);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setLoopCount(int p0) {
        if (p0 == -1) {
            mediaPlayer.controls().setRepeat(true);
        }
    }

    public void setTimeBase(TimeBase tb) throws MediaException {
        if (this.state == 100 || this.state == 400 || this.state == 0) {
            throw new IllegalStateException();
        }
    }

    public TimeBase getTimeBase() {
        if (this.state == 100 || this.state == 0) {
            throw new IllegalStateException();
        }
        return this.timeBase;
    }

    /**
     * @deprecated
     */
    public void pause() {
        if (this.state == 0) {
            throw new IllegalStateException("closed");
        }
        mediaPlayer.controls().pause();
    }

    class FramePositioningControlI implements FramePositioningControl {

        @Override
        public long mapFrameToTime(int frameBumber) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int mapTimeToFrame(long mediaTime) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int seek(int frameNumber) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int skip(int framesToSkip) {
            // TODO Auto-generated method stub
            return 0;
        }

    }

    class VolumeControlI implements VolumeControl {

        public int getLevel() {
            if (released || mediaPlayer == null)
                throw new IllegalStateException();
            if (isMuted()) return 0;
            return vol = mediaPlayer.audio().volume();
        }

        public boolean isMuted() {
            if (released || mediaPlayer == null)
                throw new IllegalStateException();
            return mediaPlayer.audio().isMute();
        }

        public int setLevel(int p0) {
            if (released || mediaPlayer == null)
                throw new IllegalStateException();
            if (isMuted()) return 0;
            mediaPlayer.audio().setVolume(p0);
            notifyListeners("volumeChanged", mediaPlayer.audio().volume());
            return vol = mediaPlayer.audio().volume();
        }

        public void setMute(boolean p0) {
            if (released || mediaPlayer == null)
                throw new IllegalStateException();
            mediaPlayer.audio().setMute(p0);
        }

    }

    class VideoControlI implements VideoControl {

        @Override
        public int getDisplayHeight() {
            return w;
        }

        @Override
        public int getDisplayWidth() {
            return h;
        }

        @Override
        public int getDisplayX() {
            return locx;
        }

        @Override
        public int getDisplayY() {
            return locy;
        }

        @Override
        public void setDisplayFullScreen(boolean p0) {
            fullscreen = p0;
        }

        @Override
        public void setDisplayLocation(int p0, int p1) {
            locx = p0;
            locy = p1;
        }

        @Override
        public void setDisplaySize(int p0, int p1) {
            w = p0;
            h = p1;
        }

        @Override
        public void setVisible(boolean p0) {
            visible = p0;
        }

        @Override
        public int getSourceHeight() {
            if (released || mediaPlayer == null) {
                throw new IllegalStateException();
            }
            if (mediaPlayer.video().videoDimension() == null) {
                mediaPlayer.media().parsing().parse();
                try {
                    Thread.sleep(100L);
                } catch (Exception e) {
                }
            }
            srch = mediaPlayer.video().videoDimension().height;
            if (srch == 0)
                srch = bh;
            return srch;
        }

        @Override
        public int getSourceWidth() {
            if (released || mediaPlayer == null) {
                throw new IllegalStateException();
            }
            if (mediaPlayer.video().videoDimension() == null) {
                mediaPlayer.media().parsing().parse();
                try {
                    Thread.sleep(100L);
                } catch (Exception e) {
                }
            }
            srcw = mediaPlayer.video().videoDimension().width;
            if (srcw == 0)
                srcw = bw;
            return srcw;
        }

        @Override
        public byte[] getSnapshot(String p0) throws MediaException {
            if (released || mediaPlayer == null)
                throw new IllegalStateException();
            if (img != null) {
                return imgToBytes(img);
            }
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(mediaPlayer.snapshots().get(), "JPEG", os);
                byte[] bytes = os.toByteArray();
                os.close();
                return bytes;
            } catch (IOException e) {
                e.printStackTrace();
                throw new MediaException(e);
            }
        }

        @Override
        public Object initDisplayMode(int p0, Object p1) {
            //System.out.println("initDisplayMode " + p0 + " " + p1);
            if (p0 == 0) {
                isItem = true;
                return getItem();
            }
            canvas = p1;
            return null;
        }

    }

    private class MyVideoSurface extends CallbackVideoSurface {

        MyVideoSurface() {
            super(new MyBufferFormatCallback(), new MyRenderCallback(), true, VideoSurfaceAdapters
                    .getVideoSurfaceAdapter());
        }

    }

    private class MyBufferFormatCallback implements BufferFormatCallback {
        @Override
        public BufferFormat getBufferFormat(int w, int h) {
            bw = w;
            bh = h;
            return new RV32BufferFormat(w, h);
        }

        @Override
        public void allocatedBuffers(ByteBuffer[] buffers) {
            bb = buffers[0];
            img = new BufferedImage(bw, bh, BufferedImage.TYPE_3BYTE_BGR);
        }

    }

    private class MyRenderCallback implements RenderCallback {
        @Override
        public void display(MediaPlayer mediaPlayer, ByteBuffer[] buffers, BufferFormat bufferFormat) {
            bb.rewind();
            byte[] raw = new byte[bb.capacity()];
            bb.get(raw);
            DataBuffer buffer = new DataBufferByte(raw, raw.length);
            SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, bw, bh, 4, bw
                    * 4, new int[]{2, 1, 0});
            Raster r = Raster.createRaster(sampleModel, buffer, null);
            if (img == null || img.getWidth() != bw || img.getHeight() != bh) {
                img = new BufferedImage(bw, bh, BufferedImage.TYPE_3BYTE_BGR);
            }
            img.setData(r);
            update();
        }
    }

    private static BufferedImage resizeProportional(BufferedImage img, int sw, int sh) {
        int iw = img.getWidth();
        int ih = img.getHeight();
        if (sw == iw && sh == ih)
            return img;
        double widthRatio = (double) sw / (double) iw;
        double heightRatio = (double) sh / (double) ih;
        double ratio = Math.min(widthRatio, heightRatio);
        int tw = (int) (iw * ratio);
        int th = (int) (ih * ratio);
        return resize(img, tw, th);
    }

    private static BufferedImage resize(BufferedImage original, int w, int h) {
        try {
            BufferedImage resized = new BufferedImage(w, h, original.getType());
            Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(original, 0, 0, w, h, 0, 0, original.getWidth(),
                    original.getHeight(), null);
            g.dispose();
            return resized;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void audioDeviceChanged(MediaPlayer arg0, String arg1) {
        notifyListeners("vlc.audioDeviceChanged", arg1);

    }

    @Override
    public void backward(MediaPlayer arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void buffering(MediaPlayer arg0, float arg1) {
        notifyListeners("vlc.buffering", new Double(arg1));

    }

    @Override
    public void chapterChanged(MediaPlayer arg0, int arg1) {
        notifyListeners("vlc.chapterChanged", arg1);

    }

    @Override
    public void corked(MediaPlayer arg0, boolean arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void elementaryStreamAdded(MediaPlayer arg0, TrackType arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void elementaryStreamDeleted(MediaPlayer arg0, TrackType arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void elementaryStreamSelected(MediaPlayer arg0, TrackType arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void forward(MediaPlayer arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void lengthChanged(MediaPlayer arg0, long arg1) {
        if (!lengthNotified) {
            lengthNotified = true;
            return;
        }
        // TODO Auto-generated method stub
        notifyListeners("durationChanged", arg1);
    }

    @Override
    public void mediaChanged(MediaPlayer arg0, MediaRef arg1) {
        // TODO Auto-generated method stub
        notifyListeners("vlc.mediaChanged", arg1.toString());

    }

    @Override
    public void mediaPlayerReady(MediaPlayer arg0) {
        // TODO Auto-generated method stub
        notifyListeners("vlc.mediaPlayerReady", null);
    }

    @Override
    public void muted(MediaPlayer arg0, boolean arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void opening(MediaPlayer arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void pausableChanged(MediaPlayer arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void paused(MediaPlayer arg0) {
        this.state = 300;
        notifyListeners("stopped", getMediaTime());
    }

    @Override
    public void positionChanged(MediaPlayer arg0, float arg1) {
        // TODO Auto-generated method stub
        //notifyListeners("positionChanged", new Double(arg1));
    }

    @Override
    public void scrambledChanged(MediaPlayer arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void seekableChanged(MediaPlayer arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void snapshotTaken(MediaPlayer arg0, String arg1) {
        notifyListeners("snapshotTaken", arg1);

    }

    @Override
    public void stopped(MediaPlayer arg0) {
        this.state = 300;
        notifyListeners("stopped", getMediaTime());
    }

    @Override
    public void timeChanged(MediaPlayer arg0, long arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void titleChanged(MediaPlayer arg0, int arg1) {
        notifyListeners("titleChanged", arg1);

    }

    @Override
    public void videoOutput(MediaPlayer arg0, int arg1) {
        srch = mediaPlayer.video().videoDimension().height;
        srcw = mediaPlayer.video().videoDimension().width;
    }

    @Override
    public void volumeChanged(MediaPlayer arg0, float arg1) {
        notifyListeners("com.nokia.external.volume.event", (int) (arg1 * 100F));

    }

    public void finished(MediaPlayer mediaPlayer) {
        this.state = 300;
        notifyListeners("endOfMedia", getMediaTime());
    }

    public void error(MediaPlayer mediaPlayer) {
        Manager.log("vlcplayer error");
        notifyListeners("error", null);
    }

    public void playing(MediaPlayer mediaPlayer) {
        Manager.log("vlcplayer started");
        notifyListeners("started", getMediaTime());
    }

}
