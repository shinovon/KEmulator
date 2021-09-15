package javax.microedition.midlet;

import emulator.*;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.net.URI;

import javax.microedition.io.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public abstract class MIDlet
{
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
        Emulator.getEmulator().getLogStream().println("Notify Destroyed");
        Emulator.notifyDestroyed();
        Emulator.getEmulator().getLogStream().println("Exiting Emulator");
        System.exit(0);
    }
    
    public void notifyPaused() {
    }
    
    public boolean platformRequest(String s) throws ConnectionNotFoundException {
        try {
            if (Settings.networkNotAvailable || JOptionPane.showConfirmDialog(new JPanel(), "MIDlet wants to open URL: " + s, "Security", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            	System.out.println("MIDlet tried to request: " + s);
                return false;
            }
        	//Emulator.checkPermission("midlet.platformrequest");
            if(s.startsWith("file:///root/")) {
            	s = "file:///" + (Emulator.getAbsolutePath() + "/file/root/" + s.substring("file:///root/".length())).replace(" ", "%20");
            }
            if(s.startsWith("vlc.exe")) {
            	s = "C:/PROGRA~1/VideoLAN/VLC/vlc.exe" + s.substring("vlc.exe".length());
                Runtime.getRuntime().exec(s);
            	return true;
            }
            System.out.println("platformRequest " + s);
            try {
	            if(Desktop.getDesktop().isDesktopSupported()) {
	            	Desktop.getDesktop().browse(new URI(s));
	            }
            } catch (Exception e) {
            	System.out.println(e);
            	Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + s);
            }
            return false;
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            throw new ConnectionNotFoundException(ex.toString());
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
