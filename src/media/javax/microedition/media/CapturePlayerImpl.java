package javax.microedition.media;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.microedition.amms.control.camera.CameraControl;
import javax.microedition.amms.control.camera.FlashControl;
import javax.microedition.amms.control.camera.FocusControl;
import javax.microedition.amms.control.imageeffect.ImageTransformControl;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.CameraVideoControlImpl;

import com.github.sarxos.webcam.Webcam;

import emulator.graphics2D.awt.d;

public class CapturePlayerImpl implements Player {
	
	public static CapturePlayerImpl inst;

	private VideoControl videoControl;
	private Webcam webcam;
	public boolean visible;
	public Object canvas;

	public boolean started;

	private int locy;

	private int locx;

	private int scalew;

	private int scaleh;

	private boolean isItem;

	private Item item;

	private FocusControl focusControl;

	private FlashControl flashControl;

	private CameraControl cameraControl;
	
	CapturePlayerImpl() throws MediaException {
		webcam = Webcam.getDefault();
		if(webcam == null) throw new MediaException("No capture devices found!");
		videoControl = new CameraVideoControlImpl(this);
		focusControl = new FocusControlI();
		flashControl = new FlashControlI();
		cameraControl = new CameraControlI();
	}
	
	class FocusControlI implements FocusControl {

		@Override
		public int setFocus(int paramInt) throws MediaException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getFocus() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getMinFocus() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getFocusSteps() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isManualFocusSupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isAutoFocusSupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isMacroSupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setMacro(boolean paramBoolean) throws MediaException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean getMacro() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	class FlashControlI implements FlashControl {

		@Override
		public int[] getSupportedModes() {
			// TODO Auto-generated method stub
			return new int[] { 1 } ;
		}

		@Override
		public void setMode(int paramInt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getMode() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public boolean isFlashReady() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	class CameraControlI implements CameraControl {

		@Override
		public int getCameraRotation() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void enableShutterFeedback(boolean paramBoolean) throws MediaException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isShutterFeedbackEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String[] getSupportedExposureModes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setExposureMode(String paramString) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getExposureMode() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int[] getSupportedVideoResolutions() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int[] getSupportedStillResolutions() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setVideoResolution(int paramInt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setStillResolution(int paramInt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getVideoResolution() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getStillResolution() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}

	@Override
	public Control getControl(String s) {
        if (s.indexOf("VideoControl") != -1 || s.indexOf("GUIControl") != -1) {
            return this.videoControl;
        }
        if(s.indexOf("FocusControl") != -1) {
        	return focusControl;
        }
        if(s.indexOf("FlashControl") != -1) {
        	return flashControl;
        }
        if(s.indexOf("CameraControl") != -1) {
        	return cameraControl;
        }
		return null;
	}

	@Override
	public Control[] getControls() {
		return new Control[] { videoControl, focusControl, flashControl, cameraControl };
	}

	@Override
	public void addPlayerListener(PlayerListener p0) throws IllegalStateException {

	}

	@Override
	public void close() {
		started = false;
		webcam.close();
		inst = null;
	}

	@Override
	public void deallocate() throws IllegalStateException {
		inst = null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public long getDuration() {
		return 0;
	}

	@Override
	public long getMediaTime() {
		return 0;
	}

	@Override
	public int getState() {
		return 0;
	}

	@Override
	public void prefetch() throws IllegalStateException, MediaException {

	}

	@Override
	public void realize() throws IllegalStateException, MediaException {
		inst = this;
	}

	@Override
	public void removePlayerListener(PlayerListener p0) throws IllegalStateException {

	}

	@Override
	public void setLoopCount(int p0) {

	}

	@Override
	public long setMediaTime(long p0) {
		return 0;
	}

	@Override
	public void start() throws IllegalStateException, MediaException {
		webcam.open();
		started = true;
	}

	@Override
	public void stop() throws IllegalStateException, MediaException {
		started = false;
	}

	@Override
	public void setTimeBase(TimeBase p0) throws MediaException {

	}

	@Override
	public TimeBase getTimeBase() {
		return null;
	}

	public void setVisible(boolean p0) {
		visible = p0;
	}

	public void setDisplaySize(int p0, int p1) {
		scalew = p0;
		scaleh = p1;
	}

	public void setDisplayLocation(int p0, int p1) {
		locx = p0;
		locy = p1;
	}

	public Object initDisplayMode(int p0, Object p1) {
		if(p0 == 0) {
			isItem = true;
			return getItem();
		}
		canvas = p1;
		//System.out.println("init " + canvas);
		return null;
	}

	private Item getItem() {
		if(item != null) return item;
		item = new CaptureItem(this);
		return item;
	}

	public byte[] getSnapshot(String p0) throws MediaException {
		if(!visible) throw new MediaException("Not visible");
		if(p0 != null) {
			String[] ar = p0.split("&");
			int w = Integer.parseInt(ar[0].replace("width=", ""));
			int h = Integer.parseInt(ar[1].replace("height=", ""));
		}
		try {
			//webcam.setViewSize(new Dimension(w, h));
			File file = new File("cameracapture.jpg");
			ImageIO.write(webcam.getImage(), "JPG", file);
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			throw new MediaException(e.toString());
		}
	}

	public void setDisplayFullScreen(boolean p0) {
		
	}

	public int getSourceHeight() {
		return webcam.getViewSize().height;
	}

	public int getSourceWidth() {
		return webcam.getViewSize().width;
	}
	

    public static Image resize(Image src_i,
              int size_w, int size_h)
    {
     if(size_h == 0) return src_i;         
        // set source size
        int w = src_i.getWidth();
        int h = src_i.getHeight();
        
    	if(size_w == -1)
    	{
    		size_w = (w/h)*size_h;
    	}
        // no change??
        if(size_w == w && size_h == h) return src_i;
        
        int [] dst = new int[size_w * size_h];
        
        resize_rgb_filtered(src_i, dst, w, h, size_w, size_h);
        
        // not needed anymore
        src_i = null;
                                
        return Image.createRGBImage(dst, size_w, size_h, true);
    }
    

    private static final void resize_rgb_filtered(Image src_i, int [] dst, 
              int w0, int h0, int w1, int h1)
    {
        int [] buffer1 = new int[w0];
        int [] buffer2 = new int[w0];
        
        // UNOPTIMIZED bilinear filtering:               
        //         
        // The pixel position is defined by y_a and y_b,
        // which are 24.8 fixed point numbers
        // 
        // for bilinear interpolation, we use y_a1 <= y_a <= y_b1
        // and x_a1 <= x_a <= x_b1, with y_d and x_d defining how long
        // from x/y_b1 we are.
        //
        // since we are resizing one line at a time, we will at most 
        // need two lines from the source image (y_a1 and y_b1).
        // this will save us some memory but will make the algorithm 
        // noticeably slower
        
        for(int index1 = 0, y = 0; y < h1; y++) {
            
            final int y_a = ((y * h0) << 8) / h1;            
            final int y_a1 = y_a >> 8;            
            int y_d = y_a & 0xFF;
            
            int y_b1 = y_a1 + 1;            
            if(y_b1 >= h0) {
                y_b1 = h0-1;
                y_d = 0;
            }
            
            // get the two affected lines:
            src_i.getRGB(buffer1, 0, w0, 0, y_a1, w0, 1);            
            if(y_d != 0)
                src_i.getRGB(buffer2, 0, w0, 0, y_b1, w0, 1);
            
            for(int x = 0; x < w1; x++) {                 
                // get this and the next point
                int x_a = ((x * w0) << 8) / w1;
                int x_a1 = x_a >> 8;
                int x_d = x_a & 0xFF;
                
                
                int x_b1 = x_a1 + 1;                                
                if(x_b1 >= w0) {
                    x_b1 = w0-1;
                    x_d = 0;
                }
                
                
                // interpolate in x
                int c12, c34;
                int c1 = buffer1[x_a1];
                int c3 = buffer1[x_b1];
                
                // interpolate in y:
                if(y_d == 0) {   
                    c12 = c1;
                    c34 = c3;
                } else {
                    int c2 = buffer2[x_a1];
                    int c4 = buffer2[x_b1];
                    
                    c12 = blend(c2, c1, y_d);
                    c34 = blend(c4, c3, y_d);
                }
                
                // final result
                dst[index1++] = blend(c34, c12, x_d);
            }
        }
        
    }
    

    public static final int blend(final int c1, final int c2, final int value256)
    {
                
        final int v1 = value256 & 0xFF;        
        final int c1_RB = c1 & 0x00FF00FF;
        final int c2_RB = c2 & 0x00FF00FF;
        
        final int c1_AG = (c1 >>> 8) & 0x00FF00FF;
        
        final int c2_AG_org = c2 & 0xFF00FF00;
        final int c2_AG = (c2_AG_org) >>> 8;
        
        // the world-famous tube42 blend with one mult per two components:      
        final int rb = (c2_RB + (((c1_RB - c2_RB) * v1) >> 8)) & 0x00FF00FF;
        final int ag = (c2_AG_org + ((c1_AG - c2_AG) * v1)) & 0xFF00FF00;                
        return ag | rb;
        
    }
    

	public void paint(Graphics g) {
		if(visible && started) {
			try {
				BufferedImage bi = webcam.getImage();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(bi, "PNG", os);
				byte[] bytes = os.toByteArray();
				os.close();
				g.drawImage(resize(new Image(new d(bytes)),-1,scaleh), locx, locy, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void draw(Graphics g, Object obj) {
		if(inst != null) {
			if(inst.canvas == null || obj == inst.canvas) {
				inst.paint(g);
			}
		} else {
			VLCPlayerImpl.draw(g, obj);
		}
		
	}

}
