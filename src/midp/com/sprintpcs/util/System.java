package com.sprintpcs.util;

public class System
{
    public static void setExitURI(final String s) {
        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + s);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void promptMasterVolume() {
    }
}
