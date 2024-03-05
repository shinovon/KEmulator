package ru.woesss.micro3d;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;

import emulator.custom.CustomJarResources;

public class PlatformHelper {

    public static byte[] getResourceBytes(String s) {
        try {
            return CustomJarResources.getBytes(s);
        } catch (IOException e) {
            return null;
        }
    }

    public static int[] getBuffer(Graphics g) {
        return g.getImage().getData();
    }

    public static int getWidth(Graphics g) {
        return g.getImage().getWidth();
    }

    public static int getHeight(Graphics g) {
        return g.getImage().getHeight();
    }

    public static void onReleased(Graphics g) {
    }
}
