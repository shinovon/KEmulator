package emulator;

import java.text.*;
import java.util.*;
import java.io.*;

public final class KeyRecords
{
    private String aString1156;
    private PrintStream aPrintStream1157;
    private BufferedReader aBufferedReader1158;
    private long aLong1159;
    private String aString1160;
    
    public KeyRecords() {
        super();
        if (Settings.playingRecordedKeys) {
            this.aString1156 = Settings.recordedKeysFile;
            try {
                (this.aBufferedReader1158 = new BufferedReader(new FileReader(this.aString1156))).readLine();
                Settings.recordedRandomSeed = Long.parseLong(this.aBufferedReader1158.readLine());
                this.aLong1159 = -1L;
                return;
            }
            catch (Exception ex) {
                Emulator.getEmulator().getLogStream().println("4 "+ ex.toString());
                Settings.playingRecordedKeys = false;
                return;
            }
        }
        if (Settings.recordKeys) {
            this.aString1156 = method700();
            this.aString1156 = this.aString1156 + new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss", Locale.ENGLISH).format(Calendar.getInstance().getTime()) + ".rec";
            try {
                final File file;
                if (!(file = new File(this.aString1156)).exists()) {
                    file.createNewFile();
                }
                (this.aPrintStream1157 = new PrintStream(new FileOutputStream(file))).println(Emulator.midletJar);
                this.aPrintStream1157.println(Settings.recordedRandomSeed);
            }
            catch (Exception ex2) {
                Emulator.getEmulator().getLogStream().println("5 "+ex2.toString());
            }
        }
    }
    
    public final void print(final String s) {
        if (this.aPrintStream1157 == null) {
            return;
        }
        this.aPrintStream1157.println(s);
    }
    
    public final String method698(final long n) {
        if (this.aBufferedReader1158 == null || !Settings.playingRecordedKeys) {
            return null;
        }
        String s = null;
        if (n > this.aLong1159) {
            try {
                final String line;
                if ((line = this.aBufferedReader1158.readLine()) == null) {
                    Settings.playingRecordedKeys = false;
                }
                else {
                    final String[] split;
                    if ((split = line.split(":")) != null && split.length > 1) {
                        this.aLong1159 = Long.parseLong(split[0]);
                        this.aString1160 = split[1];
                        if (n == this.aLong1159) {
                            s = this.aString1160;
                            this.method699();
                        }
                    }
                }
            }
            catch (IOException ex) {}
        }
        else if (n == this.aLong1159) {
            s = this.aString1160;
            this.method699();
        }
        return s;
    }
    
    private void method699() {
        try {
            final String line;
            if ((line = this.aBufferedReader1158.readLine()) != null) {
                final String[] split;
                if ((split = line.split(":")) != null && split.length > 1) {
                    this.aLong1159 = Long.parseLong(split[0]);
                    this.aString1160 = split[1];
                }
                return;
            }
            Settings.playingRecordedKeys = false;
        }
        catch (IOException ex) {}
    }
    
    private static String method700() {
        final String string = Emulator.getAbsolutePath() + "/records/";
        final File file;
        if (!(file = new File(string)).exists() || !file.isDirectory()) {
            file.mkdir();
        }
        return string;
    }
    
    public static String method701(final String s) {
        if (s == null) {
            return null;
        }
        try {
            return new BufferedReader(new FileReader(s)).readLine();
        }
        catch (Exception ex) {
            return null;
        }
    }
}
