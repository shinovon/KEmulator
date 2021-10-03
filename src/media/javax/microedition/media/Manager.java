package javax.microedition.media;

import emulator.Emulator;
import emulator.custom.*;
import java.io.*;
import java.util.ArrayList;

import javax.microedition.io.*;
import javax.microedition.media.protocol.DataSource;

public class Manager
{
    public static final String TONE_DEVICE_LOCATOR = "device://tone";
    public static final String CONTENT_TYPE_UNKNOWN = "unknown";
    public static final String CONTENT_TYPE_MIDI = "audio/midi";
    public static final String CONTENT_TYPE_XMIDI = "audio/x-midi";
    public static final String CONTENT_TYPE_XWAVE = "audio/x-wav";
    public static final String CONTENT_TYPE_WAVE = "audio/wav";
    public static final String CONTENT_TYPE_TONE = "audio/x-tone-seq";
    public static final String CONTENT_TYPE_AMR = "audio/amr";
    public static final String CONTENT_TYPE_MPEG = "audio/mpeg";
    public static final String PROTOCOL_FILE = "file";
    public static final String PROTOCOL_HTTP = "http";
    static TimeBase systemTimeBase;
    
    public Manager() {
        super();
    }
    
    public static Player createPlayer(final InputStream inputStream, final String s) throws IOException, MediaException {
    	Emulator.getEmulator().getLogStream().println("createPlayer " + inputStream + " " + s);
        if (inputStream == null) {
            throw new IllegalArgumentException();
        }
        if(s != null && s.startsWith("video/")) {
        	return new VideoPlayerImpl(inputStream, s);
        }
        // buffer
        boolean buf = true;
        if(buf)
        	return new PlayerImpl(new ByteArrayInputStream(CustomJarResources.getBytes(inputStream)), s);
        else
        	return new PlayerImpl(inputStream, s);
    }
    
    public static Player createPlayer(final String s) throws IOException, MediaException {
    	Emulator.getEmulator().getLogStream().println("createPlayer " + s);
    	if(s.startsWith("rtps://")) {
    		return new VideoPlayerImpl(s);
    	}
        if (s.startsWith("device://tone")) {
        	return new ToneImpl();
        }
        if (s.startsWith("capture://image") || s.startsWith("capture://video")) {
        	return new CapturePlayerImpl();
        }
        boolean video = s.endsWith(".mpg") || s.endsWith(".mp4") || s.endsWith(".avi");
		String type = "";
		if(s.endsWith(".mpg")) {
			type = "video/mpeg";
		} else if(s.endsWith(".avi")) {
			type = "video/avi";
		} else if(s.endsWith(".mp4")) {
			type = "video/mp4";
		} else if(s.endsWith(".mp3")) {
			type = "video/mpeg";
		}
        if (s.indexOf(58) != -1) {
        	if(video) {
            	return new VideoPlayerImpl(((InputConnection)Connector.open(s)).openInputStream(), type);
        	}
            return createPlayer(((InputConnection)Connector.open(s)).openInputStream(), s.endsWith(".mp3") ? "audio/mpeg" : "unknown");
        }

    	if(video) {
            return new VideoPlayerImpl(CustomJarResources.getResourceStream(s), type);
    	}
        return createPlayer(CustomJarResources.getResourceStream(s), s.endsWith(".mp3") ? "audio/mpeg" : "unknown");
    }
    
    public static Player createPlayer(final DataSource src) throws IOException, MediaException {
    	Emulator.getEmulator().getLogStream().println("createPlayer DataSource " + src);
    	//System.out.println("*** Manager.createPlayer(DataSource) is not implemented yet! ***");
    	//throw new MediaException("Not supported");
    	return new PlayerImpl(src);
    }
    
    public static String[] getSupportedContentTypes(final String protocol) {
        String[] arr = new String[] { "audio/midi", "audio/x-midi", "audio/x-wav", "audio/wav", "audio/x-tone-seq", "audio/amr", "audio/mpeg", "audio/mp3", 
        		"video/" };
   
        if(protocol == null || protocol.length() <= 1)
        	return arr;
        else if(protocol.equalsIgnoreCase("http")) {
        	return new String[] { "audio/mpeg", "audio/mp3" };
        } else {
        	ArrayList<String> list = new ArrayList<String>();
        	for(String t: arr) {
        		if(t.startsWith(protocol.toLowerCase()))
        			list.add(t);
        	}
        	return list.toArray(new String[0]);
        }
    }
    
    public static String[] getSupportedProtocols(final String type) {
        return new String[] { "device://tone", "capture://image", "http", "file", "https" };
    }
    
    public static void playTone(final int n, final int n2, final int n3) throws MediaException {
    }
    
    public static TimeBase getSystemTimeBase() {
        if (Manager.systemTimeBase == null) {
            Manager.systemTimeBase = new SystemTimeBaseImpl();
        }
        return Manager.systemTimeBase;
    }
}
