package javax.microedition.rms;

import java.util.*;
import emulator.*;
import java.io.*;

public class RecordStore
{
    public static final int AUTHMODE_PRIVATE = 0;
    public static final int AUTHMODE_ANY = 1;
    static String rmsDir;
    String name;
    Vector records;
    Vector aVector372;
    int count;
	private boolean closed;
    
    RecordStore(final String aString371, final boolean b) throws RecordStoreException, RecordStoreNotFoundException, RecordStoreFullException {
        super();
        this.records = new Vector();
        this.aVector372 = new Vector(3);
        this.name = aString371;
        try {
            final File file;
            if (!(file = new File(RecordStore.rmsDir + this.fileName())).exists() || !file.isDirectory()) {
                file.mkdir();
            }
            if (!b) {
                this.count = 1;
                return;
            }
            final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(RecordStore.rmsDir + this.fileName() + this.name + ".idx"));
            this.count = dataInputStream.readInt();
            for (int int1 = dataInputStream.readInt(), i = 0; i < int1; ++i) {
                this.records.add(new Integer(dataInputStream.readInt()));
            }
            dataInputStream.close();
        }
        catch (Exception ex) {
            throw new RecordStoreNotFoundException(this.name);
        }
    }
    
    public void setMode(final int n, final boolean b) throws RecordStoreException {
    }
    
    public long getLastModified() throws RecordStoreNotOpenException {
        return 0L;
    }
    
    public int getVersion() throws RecordStoreNotOpenException {
        return 0;
    }
    
    public String getName() throws RecordStoreNotOpenException {
        return this.name;
    }
    
    private String fileName() {
        return "." + this.name + "/";
    }
    
    public int getNextRecordID() throws RecordStoreNotOpenException, RecordStoreException {
        return this.count;
    }
    
    public int getRecordSize(final int n) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        if (!this.method184(n)) {
            throw new InvalidRecordIDException();
        }
        try {
            return (int)new File(RecordStore.rmsDir + this.fileName() + this.name + "_" + n + ".rms").length();
        }
        catch (Exception ex) {
            throw new RecordStoreException();
        }
    }
    
    public int getSize() throws RecordStoreNotOpenException {
        int n = 0;
        try {
            for (int i = 0; i < this.records.size(); ++i) {
                n += this.getRecordSize((int)this.records.get(i));
            }
        }
        catch (Exception ex) {
            throw new RecordStoreNotOpenException();
        }
        return n;
    }
    
    public int getSizeAvailable() throws RecordStoreNotOpenException {
        return 10000000;
    }
    
    public RecordEnumeration enumerateRecords(final RecordFilter recordFilter, final RecordComparator recordComparator, final boolean b) {
        logln("enumerateRecords " + this.name);
        return new RecordEnumerationImpl(this, b);
    }
    
    public static RecordStore openRecordStore(final String s, final boolean b) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
        return openRecordStore(s, b, 0, true);
    }
    
    public static RecordStore openRecordStore(String substring, final boolean b, final int n, final boolean b2) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
        if (substring.startsWith("/")) {
            substring = substring.substring(1);
        }
        logln("openRecordStore " + substring);
        boolean exists = false;
        final File file;
        if ((file = new File(RecordStore.rmsDir + "." + substring)).exists() && file.isDirectory()) {
            exists = new File(RecordStore.rmsDir + "." + substring + "/" + substring + ".idx").exists();
        }
        if (exists || b) {
            return new RecordStore(substring, exists);
        }
        throw new RecordStoreNotFoundException(substring);
    }

	public static RecordStore openRecordStore(final String s, final String s2, final String s3) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
        return openRecordStore(s, false);
    }
    
    public static void deleteRecordStore(String substring) throws RecordStoreException, RecordStoreNotFoundException {
        if (substring.startsWith("/")) {
            substring = substring.substring(1);
        }
        new RecordStore(substring, true).method183();
    }
    
    private void method183() throws RecordStoreException, RecordStoreNotFoundException {
        logln("deleteRecordStore " + this.name);
        for (int i = this.records.size() - 1; i >= 0; --i) {
            this.deleteRecord((int)this.records.get(i));
        }
        try {
            if (new File(RecordStore.rmsDir + this.fileName()).exists()) {
                new File(RecordStore.rmsDir + this.fileName() + this.name + ".idx").delete();
            }
            new File(RecordStore.rmsDir + this.fileName()).delete();
        }
        catch (Exception ex) {
            throw new RecordStoreNotFoundException(this.name);
        }
    }
    
    public static String[] listRecordStores() {
        final String[] list;
        if ((list = new File(RecordStore.rmsDir).list(new RMSListFileNameFilter())) != null) {
            for (int i = list.length - 1; i >= 0; --i) {
                list[i] = list[i].substring(1);
            }
        }
        logln("listRecordStores: " + list.length);
        return list;
    }
    
    public void deleteRecord(final int n) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        log("deleteRecord " + this.name + "_" + n + ": ");
        int i = this.records.size() - 1;
        try {
            while (i >= 0) {
                if ((int)this.records.get(i) == n) {
                    this.records.remove(i);
                    if (new File(RecordStore.rmsDir + this.fileName()).exists()) {
                        new File(RecordStore.rmsDir + this.fileName() + this.name + "_" + n + ".rms").delete();
                    }
                    this.method186();
                    logendln("OK");
                    this.method188(n);
                    return;
                }
                --i;
            }
        }
        catch (Exception ex) {
            throw new RecordStoreException("recordId=" + n);
        }
        logendln("FAIL");
    }
    
    public int getRecord(final int n, final byte[] array, final int n2) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        logln("getRecord " + this.name + "_" + n);
        if (!this.method184(n)) {
            throw new InvalidRecordIDException("recordId=" + n);
        }
        try {
            final File file = new File(RecordStore.rmsDir + this.fileName() + this.name + "_" + n + ".rms");
            final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            final int n3 = (int)file.length();
            dataInputStream.readFully(array, n2, n3);
            return n3;
        }
        catch (Exception ex) {
            throw new RecordStoreException("recordId=" + n);
        }
    }
    
    public byte[] getRecord(final int n) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        logln("getRecord " + this.name + "_" + n);
        if (!this.method184(n)) {
            throw new InvalidRecordIDException("recordId=" + n);
        }
        try {
            final File file;
            if ((file = new File(RecordStore.rmsDir + this.fileName() + this.name + "_" + n + ".rms")).length() > 0L) {
                final byte[] array = new byte[(int)file.length()];
                new DataInputStream(new FileInputStream(file)).readFully(array);
                return array;
            }
            return null;
        }
        catch (Exception ex) {
            throw new RecordStoreException("recordId=" + n);
        }
    }
    
    public int getNumRecords() throws RecordStoreNotOpenException {
    	logln("getNumRecords " + this.name + ": " + records.size());
        return this.records.size();
    }
    
    private boolean method184(final int n) {
        boolean b = false;
        for (int i = 0; i < this.records.size(); ++i) {
            if ((int)this.records.get(i) == n) {
                b = true;
            }
        }
        return b;
    }
    
    public int addRecord(final byte[] array, final int n, final int n2) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {
        logln("addRecord " + this.name + "_" + this.count);
        this.records.add(new Integer(this.count));
        try {
            final File file;
            if (!(file = new File(RecordStore.rmsDir + this.fileName())).exists() && !file.isDirectory()) {
                file.mkdir();
            }
            final FileOutputStream fileOutputStream = new FileOutputStream(RecordStore.rmsDir + this.fileName() + this.name + "_" + this.count + ".rms");
            if (array != null) {
                fileOutputStream.write(array, n, n2);
            }
            fileOutputStream.close();
            this.method186();
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            throw new RecordStoreException();
        }
        this.method187(this.count);
        return this.count++;
    }
    
    public void setRecord(final int n, final byte[] array, final int n2, final int n3) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {
    	logln("setRecord " + this.name + " " + n);
        if(n == count) {
            addRecord(array, n2, n3);
            return;
        }
    	if (!this.method184(n)) {
            throw new InvalidRecordIDException("recordId=" + n);
        }
        try {
            final File file;
            if (!(file = new File(RecordStore.rmsDir + this.fileName())).exists() && !file.isDirectory()) {
                file.mkdir();
            }
            final FileOutputStream fileOutputStream = new FileOutputStream(RecordStore.rmsDir + this.fileName() + this.name + "_" + n + ".rms");
            if (array != null) {
                fileOutputStream.write(array, n2, n3);
            }
            fileOutputStream.close();
            this.method186();
        }
        catch (Exception ex) {
            throw new RecordStoreException("recordId=" + n);
        }
        this.method185(n);
    }
    
    public void closeRecordStore() throws RecordStoreNotOpenException, RecordStoreException {
    	if(closed) throw new RecordStoreNotOpenException();
    	closed = true;
    	logln("closeRecordStore " + this.name);
        if (!this.aVector372.isEmpty()) {
            this.aVector372.removeAllElements();
        }
        this.method186();
    }
    
    private void method186() throws RecordStoreException {
        try {
            final File file;
            if (!(file = new File(RecordStore.rmsDir + this.fileName())).exists() && !file.isDirectory()) {
                file.mkdir();
            }
            final DataOutputStream dataOutputStream;
            (dataOutputStream = new DataOutputStream(new FileOutputStream(RecordStore.rmsDir + this.fileName() + this.name + ".idx"))).writeInt(this.count);
            dataOutputStream.writeInt(this.records.size());
            for (int i = 0; i < this.records.size(); ++i) {
                dataOutputStream.writeInt((int)this.records.get(i));
            }
            dataOutputStream.close();
        }
        catch (Exception ex) {
            throw new RecordStoreException(this.name);
        }
    }
    
    public void addRecordListener(final RecordListener recordListener) {
        if (!this.aVector372.contains(recordListener)) {
            this.aVector372.addElement(recordListener);
        }
    }
    
    public void removeRecordListener(final RecordListener recordListener) {
        this.aVector372.removeElement(recordListener);
    }
    
    private void method185(final int n) {
        for (int i = 0; i < this.aVector372.size(); ++i) {
            ((RecordListener)this.aVector372.elementAt(i)).recordChanged(this, n);
        }
    }
    
    private void method187(final int n) {
        for (int i = 0; i < this.aVector372.size(); ++i) {
            ((RecordListener)this.aVector372.elementAt(i)).recordAdded(this, n);
        }
    }
    
    private void method188(final int n) {
        for (int i = 0; i < this.aVector372.size(); ++i) {
            ((RecordListener)this.aVector372.elementAt(i)).recordDeleted(this, n);
        }
    }
    
    private static void logendln(String s) {
        Emulator.getEmulator().getLogStream().println(s);
	}
    
    private static void logln(String s) {
        Emulator.getEmulator().getLogStream().println("[RMS] " + s);
	}
    
    private static void log(String s) {
        Emulator.getEmulator().getLogStream().print("[RMS] " + s);
	}
    
    static {
        RecordStore.rmsDir = Emulator.getEmulator().getProperty().getRmsFolderPath();
    }
}
