package com.sprintpcs.media;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.ToneControl;

public class Player {
    private static int anInt905 = 0;
    private static int anInt912 = 0;
    private static Object anObject906 = null;
    private static int anInt915 = -2;
    private static int anInt917 = -2;
    private static int anInt918 = -2;
    static javax.microedition.media.Player aPlayer907 = null;
    static javax.microedition.media.Player aPlayer913 = null;
    static javax.microedition.media.Player aPlayer916 = null;
    private static ToneControl aToneControl908 = null;
    static Clip aClip909 = null;
    static DualTone aDualTone910 = null;
    static Clip aClip914 = null;
    private static PlayerListener aPlayerListener911 = null;

    public Player() {
    }

    public static void addPlayerListener(PlayerListener playerListener) {
        new Class55(playerListener);
    }

    public static void play(Clip clip, int n) throws IllegalArgumentException {
        if (n < -1) {
            throw new IllegalArgumentException("Repeat must be -1 or greater");
        }
        if (clip == null || clip.priority < anInt912) {
            return;
        }
        try {
            switch (anInt905) {
                case 0: {
                    break;
                }
                case 1: {
                    if (aPlayer907 != null) {
                        aPlayer907.close();
                        aPlayer907 = null;
                    }
                    if (aPlayerListener911 == null) break;
                    aPlayerListener911.playerUpdate(7, anObject906);
                    break;
                }
                case 6: {
                    if (aPlayer916 != null) {
                        aPlayer916.close();
                        aPlayer916 = null;
                    }
                    if (aPlayerListener911 == null) break;
                    aPlayerListener911.playerUpdate(7, anObject906);
                    break;
                }
                case 4: {
                    if (aPlayer913 != null) {
                        aPlayer913.stop();
                    }
                    if (aPlayerListener911 == null) break;
                    aPlayerListener911.playerUpdate(7, anObject906);
                    break;
                }
                default: {
                    return;
                }
            }
            aPlayer907 = clip.method112();
            anInt915 = n != -1 ? n + 1 : -1;
            aPlayer907.setLoopCount(anInt915);
            Class55 class55 = new Class55(aPlayerListener911);
            Class55.method366(class55, aPlayer907);
            anInt912 = clip.priority;
            aClip909 = clip;
            anInt905 = 1;
            anObject906 = clip;
            Vibrator.vibrate((int) clip.vibration);
            aPlayer907.start();
            return;
        } catch (MediaException mediaException) {
            if (aPlayerListener911 != null) {
                aPlayerListener911.playerUpdate(2, anObject906);
            }
            System.out.println("Player.play() clip encountered a MediaException: " + mediaException.getMessage());
            mediaException.printStackTrace();
            anInt905 = 0;
            anObject906 = null;
            aClip909 = null;
            anInt912 = 0;
            return;
        } catch (Exception exception) {
            anInt905 = 0;
            anObject906 = null;
            aClip909 = null;
            anInt912 = 0;
            exception.printStackTrace();
            return;
        }
    }

    public static void play(DualTone dualTone, int n) throws IllegalArgumentException {
        if (n < -1) {
            throw new IllegalArgumentException("Repeat must be -1 or greater");
        }
        if (dualTone.anInt514 < anInt912) {
            return;
        }
        try {
            switch (anInt905) {
                case 1: {
                    if (aPlayer907 != null) {
                        aPlayer907.close();
                        aPlayer907 = null;
                    }
                    if (aPlayerListener911 == null) break;
                    aPlayerListener911.playerUpdate(7, anObject906);
                    break;
                }
                case 4: {
                    if (aPlayer913 != null) {
                        aPlayer913.stop();
                    }
                    if (aPlayerListener911 == null) break;
                    aPlayerListener911.playerUpdate(7, anObject906);
                    break;
                }
                case 6: {
                    if (aPlayer916 != null) {
                        aPlayer916.close();
                        aPlayer916 = null;
                    }
                    if (aPlayerListener911 == null) break;
                    aPlayerListener911.playerUpdate(7, anObject906);
                    break;
                }
                default: {
                    return;
                }
                case 0:
            }
            if (aPlayer916 != null) {
                aPlayer916.close();
                aPlayer916 = null;
            }
            aPlayer916 = Manager.createPlayer("device://tone");
            aPlayer916.realize();
            aToneControl908 = (ToneControl) aPlayer916.getControl("ToneControl");
            anInt917 = n != -1 ? n + 1 : -1;
            aPlayer916.setLoopCount(anInt917);
            aToneControl908.setSequence(dualTone.aByteArray513);
            Class55 class55 = new Class55(aPlayerListener911);
            Class55.method366(class55, aPlayer916);
            anInt912 = dualTone.anInt514;
            anObject906 = dualTone;
            aDualTone910 = dualTone;
            anInt905 = 6;
            aPlayer916.start();
            return;
        } catch (MediaException mediaException) {
            if (aPlayerListener911 != null) {
                aPlayerListener911.playerUpdate(2, anObject906);
            }
            mediaException.printStackTrace();
            System.out.println("Player.play(DualTone) encountered an exception: " + mediaException.getMessage());
            return;
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    public static void playBackground(Clip clip, int n) throws IllegalArgumentException {
        if (n < -1) {
            throw new IllegalArgumentException("Repeat must be -1 or greater");
        }
        if (clip == null) {
            return;
        }
        switch (anInt905) {
            case 0: {
                anInt905 = 4;
                break;
            }
            default: {
                return;
            }
            case 1:
            case 4:
            case 6:
        }
        try {
            if (aPlayer913 != null) {
                aPlayer913.close();
            }
            aPlayer913 = clip.method112();
            anInt918 = n != -1 ? n + 1 : -1;
            aPlayer913.setLoopCount(anInt918);
            Class55 class55 = new Class55(aPlayerListener911);
            Class55.method366(class55, aPlayer913);
            aClip914 = clip;
            if (anInt905 != 1 && anInt905 != 6) {
                anObject906 = aClip914;
                aPlayer913.start();
            }
            return;
        } catch (MediaException mediaException) {
            if (aPlayerListener911 != null) {
                aPlayerListener911.playerUpdate(2, anObject906);
            }
            mediaException.printStackTrace();
            System.out.println("playBackground() encountered a MediaException: " + mediaException.getMessage());
            anInt905 = 0;
            anObject906 = null;
            aClip914 = null;
            return;
        } catch (Exception exception) {
            if (aPlayerListener911 != null) {
                aPlayerListener911.playerUpdate(2, anObject906);
            }
            System.out.println("playBackground() encountered an IOException: " + exception.getMessage());
            anInt905 = 0;
            anObject906 = null;
            aClip914 = null;
            exception.printStackTrace();
            return;
        }
    }

    public static void pause() {
        block8:
        {
            try {
                switch (anInt905) {
                    case 1: {
                        if (aPlayer907.getState() != 400) break;
                        anInt905 = 2;
                        aPlayer907.stop();
                        break block8;
                    }
                    case 4: {
                        if (aPlayer913.getState() != 400) break;
                        anInt905 = 5;
                        aPlayer913.stop();
                    }
                }
                return;
            } catch (MediaException mediaException) {
                System.out.println("Player.pause encountered an exception: " + mediaException.getMessage());
                mediaException.printStackTrace();
                anInt905 = 0;
                anObject906 = null;
                aClip914 = null;
                if (aPlayerListener911 != null) {
                    aPlayerListener911.playerUpdate(2, anObject906);
                }
                return;
            } catch (Exception exception) {
                anInt905 = 0;
                anObject906 = null;
                aClip914 = null;
                exception.printStackTrace();
            }
        }
    }

    public static void resume() {
        try {
            switch (anInt905) {
                case 2: {
                    anInt905 = 1;
                    aPlayer907.start();
                    break;
                }
                case 5: {
                    anInt905 = 4;
                    aPlayer913.start();
                    break;
                }
                default: {
                    return;
                }
            }
        } catch (Exception exception) {
            if (aPlayerListener911 != null) {
                aPlayerListener911.playerUpdate(2, anObject906);
            }
            anInt905 = 0;
            anObject906 = null;
            aClip914 = null;
            exception.printStackTrace();
        }
    }

    public static void stop() {
        switch (anInt905) {
            case 1: {
                if (aPlayer907 == null) break;
                aPlayer907.close();
                aPlayer907 = null;
                break;
            }
            case 2: {
                if (aPlayer907 == null) break;
                aPlayer907.close();
                aPlayer907 = null;
                break;
            }
            case 4: {
                if (aPlayer913 == null) break;
                aPlayer913.close();
                aPlayer913 = null;
                break;
            }
            case 5: {
                if (aPlayer913 == null) break;
                aPlayer913.close();
                aPlayer913 = null;
                break;
            }
            case 6: {
                if (aPlayer916 == null) break;
                aPlayer916.close();
                System.out.println("entering here");
                aPlayer916 = null;
                break;
            }
            default: {
                return;
            }
        }
        try {
            anInt905 = 0;
            anObject906 = null;
            anInt912 = 0;
            return;
        } catch (Exception exception) {
            if (aPlayerListener911 != null) {
                aPlayerListener911.playerUpdate(2, anObject906);
            }
            anInt905 = 0;
            anObject906 = null;
            anInt912 = 0;
            exception.printStackTrace();
            return;
        }
    }

    static Object method313() {
        return anObject906;
    }

    static PlayerListener method314() {
        return aPlayerListener911;
    }

    static int method315() {
        return anInt905;
    }

    static int method319() {
        return anInt915;
    }

    static int method321() {
        return anInt917;
    }

    static Object method316(Object object) {
        anObject906 = object;
        return anObject906;
    }

    static int method317(int n) {
        anInt905 = n;
        return anInt905;
    }

    static int method320(int n) {
        anInt912 = n;
        return anInt912;
    }

    static PlayerListener method318(PlayerListener playerListener) {
        aPlayerListener911 = playerListener;
        return aPlayerListener911;
    }

    static {
    }
}
