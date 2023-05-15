package javax.microedition.midlet;

import emulator.*;
import emulator.custom.CustomMethod;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
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
    	CustomMethod.close();
        System.exit(0);
    }
    
    public void notifyPaused() {
    }
    
    public boolean platformRequest(String s) throws ConnectionNotFoundException {
        try {
            /*
        	String s2 = s;
        	if(s2.length() > 100) {
        		s2 = s2.substring(0, 90) + "...";
        	}
        	*/
            System.out.println("platformRequest(" + s + ")");

            if (Settings.networkNotAvailable || !Emulator.requestURLAccess(s)/*JOptionPane.showConfirmDialog(new JPanel(), "MIDlet wants to open URL:\n" + s2, "Security", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION*/) {
                return false;
            }
        	//Emulator.checkPermission("midlet.platformrequest");
            if(s.startsWith("file:///root/")) {
            	s = "file:///" + (Emulator.getAbsolutePath() + "/file/root/" + s.substring("file:///root/".length())).replace(" ", "%20");
            }
            // Vika touch support
            if(s.startsWith("vlc.exe")) {
            	if(Settings.vlcDir != null && Settings.vlcDir.length() > 2) {
                	s = new File(Settings.vlcDir).getCanonicalPath() + "/vlc.exe" + s.substring("vlc.exe".length());
                    Runtime.getRuntime().exec(s);
                	return true;
            	}
            	s = "C:/PROGRA~1/VideoLAN/VLC/vlc.exe" + s.substring("vlc.exe".length());
                Runtime.getRuntime().exec(s);
            	return true;
            }
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
            throw new ConnectionNotFoundException(ex);
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
