package javax.microedition.midlet;

import emulator.*;
import emulator.custom.CustomMethod;
import org.eclipse.swt.internal.C;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.net.URI;

import javax.microedition.io.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public abstract class MIDlet
{
    private boolean destroyed;
    public MIDlet() {
        super();
        Emulator.setMIDlet(this);
    }
    
    public int checkPermission(final String s) {
        return 1;
    }
    
    public String getAppProperty(final String s) {
        return Emulator.getEmulator().getAppProperty(s);
    }
    
    public void notifyDestroyed() {
        if(destroyed) return;
        destroyed = true;
        Emulator.getEmulator().getLogStream().println("Notify Destroyed");
        Emulator.notifyDestroyed();
        Emulator.getEmulator().getLogStream().println("Exiting Emulator");
    	CustomMethod.close();
        System.exit(0);
    }
    
    public void notifyPaused() {
    }
    
    public boolean platformRequest(String s) throws ConnectionNotFoundException {
        try {
            System.out.println("platformRequest(" + s + ")");
            if (Settings.networkNotAvailable || !Emulator.requestURLAccess(s)) {
                return false;
            }
            if(s.startsWith("file:///root/")) {
            	s = "file:///" + (Emulator.getAbsolutePath().replace("\\", "/") +
                        "/file/root/" + s.substring(13)).replace(" ", "%20");
            } else if(s.startsWith("file:")) {
                throw new SecurityException();
            }
            if(s.startsWith("vlc.exe \"")) {
                s = "vlc:"+s.substring(9, s.length()-1);
            }
            if(s.startsWith("vlc:")) {
                s = s.substring(4);
                if(s.indexOf(':') == -1) {
                    throw new ConnectionNotFoundException("Invalid URL");
                }
                if(s.startsWith("file:///root/")) {
                    s = "file:///" + (Emulator.getAbsolutePath().replace("\\", "/") +
                            "/file" + s.substring(13)).replace(" ", "%20");
                }
            	if(Settings.vlcDir != null && Settings.vlcDir.length() > 2) {
                    Runtime.getRuntime().exec(new File(Settings.vlcDir).getCanonicalPath() + "/vlc \"" + s + "\"");
                	return false;
            	}
                String vlcdir = "C:/Program Files/VideoLAN/VLC/vlc.exe";
                if(!Emulator.isX64() && new File(vlcdir).exists()) {
                    Runtime.getRuntime().exec(vlcdir + " \"" + s + "\"");
                    return false;
                }
                throw new ConnectionNotFoundException("vlc dir not set");
            }
            if(Desktop.getDesktop().isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(s));
            } else {
                if(Emulator.isX64()) {
                    throw new ConnectionNotFoundException("not supported");
                }
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + s);
            }
            return false;
        } catch (ConnectionNotFoundException e) {
            throw e;
        } catch (Exception e) {
        	e.printStackTrace();
            throw new ConnectionNotFoundException(e);
        }
    }
    
    public void resumeRequest() {
    }
    
    protected abstract void destroyApp(final boolean p0) throws MIDletStateChangeException;
    
    protected abstract void startApp() throws MIDletStateChangeException;
    
    protected abstract void pauseApp();
    
    public void invokeDestroyApp(final boolean b) {
        try {
            this.destroyApp(b);
        } catch (Exception ex) {
        	System.out.println("destroyApp exception!");
        	ex.printStackTrace();
        }
        this.notifyDestroyed();
    }
    
    public void invokeStartApp() {
        try {
            this.startApp();
        } catch (MIDletStateChangeException ex) {
        	System.out.println("startApp exception!");
        	ex.printStackTrace();
        } catch (Throwable ex) {
        	System.out.println("startApp exception!");
        	ex.printStackTrace();
        	throw ex;
        }
    }
    
    public void invokePauseApp() {
        this.pauseApp();
    }
}
