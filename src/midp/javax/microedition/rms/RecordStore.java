package javax.microedition.rms;

import emulator.Emulator;
import emulator.ui.IEmulatorFrontend;
import emulator.ui.swt.SWTFrontend;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Vector;

public class RecordStore {
	private static Vector openRecordStores = new Vector();
	static String rmsRootDir = Emulator.getEmulator().getProperty().getRmsFolderPath();
	static String homeRootPath;

	public static final int AUTHMODE_PRIVATE = 0;
	public static final int AUTHMODE_ANY = 1;
	private String name;
	Vector records;
	Vector recordListeners;
	int count;
	private boolean closed;
	private long lastModified;
	private int version;
	private String rootPath;
	private Object sync = new Object();
	private boolean homeSuite;
	private int authmode;
	private boolean writable;


	static {
		try {
			IEmulatorFrontend em = Emulator.getEmulator();
			if (((SWTFrontend) em).midletProps != null) {
				homeRootPath = getRootPath(null, em.getAppProperty("MIDlet-Vendor"), em.getAppProperty("MIDlet-Name"));
				logln("midlet rms path: " + homeRootPath);
			}
		} catch (Exception ignored) {
		}
	}

	private int openCount = 1;

	RecordStore(String aName, String aRootPath, boolean aHomeSuite, boolean existing) throws RecordStoreException, RecordStoreNotFoundException, RecordStoreFullException {
		records = new Vector();
		recordListeners = new Vector(3);
		name = aName;
		rootPath = aRootPath;
		homeSuite = aHomeSuite;
		try {
			if (!existing) {
				count = 1;
				writeIndex();
				openRecordStores.addElement(this);
				return;
			}
			boolean convert = false;
			File file = new File(rootPath + "idx");
			String oldPath = Emulator.getEmulator().getProperty().getOldRmsPath() + "." + aName + "/" + aName;
			if (!file.exists()) {
				if (aHomeSuite) {
					// fallback
					file = new File(oldPath + ".idx");
					logln("Fallback to " + file.getAbsolutePath());
					if (!file.exists()) throw new RecordStoreNotFoundException(name);
					convert = true;
				} else {
					throw new RecordStoreNotFoundException(name);
				}
			}
			if (file.length() == 0) {
				count = 1;
				writeIndex();
				openRecordStores.addElement(this);
				return;
			}
			DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
			count = dataInputStream.readInt();
			for (int j = dataInputStream.readInt(), i = 0; i < j; ++i) {
				records.addElement(new Integer(dataInputStream.readInt()));
			}
			try {
				if (dataInputStream.available() > 0) {
					lastModified = dataInputStream.readLong();
					version = dataInputStream.readInt();
					authmode = dataInputStream.readInt();
					writable = dataInputStream.readBoolean();
				}
			} catch (IOException ignored) {
			}
			dataInputStream.close();
			if (!homeSuite && authmode == AUTHMODE_PRIVATE) {
				throw new SecurityException();
			}
			if (convert) {
				logln("Converting: " + aName);
				writeIndex();
				file.delete();
				try {
					for (int i = 0; i < records.size(); ++i) {
						String n = records.elementAt(i).toString();
						String s1 = oldPath + "_" + n + ".rms";
						String s2 = rootPath + n + ".rms";
						new File(s1).renameTo(new File(s2));
					}
				} catch (Exception e) {
					logln("Conversion failed: " + e.toString());
				}
			}
			openRecordStores.addElement(this);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RecordStoreNotFoundException(name);
		}
	}

	public void setMode(int aAuthmode, boolean aWritable) throws RecordStoreException {
		if (closed) throw new RecordStoreNotOpenException();
		if (!homeSuite) throw new SecurityException("Only read operations allowed");
		if (aAuthmode < 0 || aAuthmode > 1) throw new IllegalArgumentException("Access mode is invalid");
		authmode = aAuthmode;
		writable = aWritable;
		writeIndex();
	}

	public long getLastModified() throws RecordStoreNotOpenException {
		return lastModified;
	}

	public int getVersion() throws RecordStoreNotOpenException {
		return version;
	}

	public String getName() throws RecordStoreNotOpenException {
		return name;
	}

	public int getNextRecordID() throws RecordStoreNotOpenException, RecordStoreException {
		return count;
	}

	public int getRecordSize(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
		if (!recordIdExists(recordId)) {
			throw new InvalidRecordIDException();
		}
		try {
			return (int) new File(rootPath + recordId + ".rms").length();
		} catch (Exception e) {
			throw new RecordStoreException(e.toString());
		}
	}

	public int getSize() throws RecordStoreNotOpenException {
		int n = 0;
		try {
			for (int i = 0; i < records.size(); ++i) {
				n += getRecordSize(((Integer) records.elementAt(i)).intValue());
			}
		} catch (RecordStoreNotOpenException e) {
			throw e;
		} catch (Exception e) {
			throw new RecordStoreNotOpenException(e.toString());
		}
		return n;
	}

	public int getSizeAvailable() throws RecordStoreNotOpenException {
		int r;
		try {
			r = (int) Math.min(32000000L, new File(rootPath).getUsableSpace());
		} catch (Exception e) {
			r = 32000000;
		}
		logln("getSizeAvailable: " + r);
		return r;
	}

	public RecordEnumeration enumerateRecords(RecordFilter recordFilter, RecordComparator recordComparator, boolean keepUpdated) {
		logln("enumerateRecords " + name + (!homeSuite ? " (guest)" : ""));
		return new RecordEnumerationImpl(this, sync, recordFilter, recordComparator, keepUpdated);
	}

	public static RecordStore openRecordStore(String name, boolean createIfNecessary) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
		return openRecordStore(name, createIfNecessary, 0, true);
	}

	public static RecordStore openRecordStore(String name, boolean createIfNecessary, int authmode, boolean writable) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
		if (name.length() > 32 || name.length() < 1) throw new IllegalArgumentException("Record store name is invalid");
		logln("openRecordStore " + name);
		String rootPath = homeRootPath + encodeBase64(name) + "/";
		RecordStore rs = findRecordStore(rootPath);
		if (rs != null) {
			rs.openCount++;
			rs.setMode(authmode, writable);
			return rs;
		}
		File file = new File(rootPath);
		boolean exists = file.exists() && file.isDirectory() && (file = new File(rootPath + "idx")).exists();
		if (!exists) {
			String oldPath = Emulator.getEmulator().getProperty().getOldRmsPath() + "." + name + "/" + name + ".idx";
			file = new File(oldPath);
			exists = file.exists();
		}
		if (exists && !createIfNecessary && file.length() == 0) { // check if index is broken
			throw new RecordStoreNotFoundException(name);
		}
		if (exists || createIfNecessary) {
			rs = new RecordStore(name, rootPath, true, exists);
			rs.setMode(authmode, writable);
			return rs;
		}
		throw new RecordStoreNotFoundException(name);
	}

	public static RecordStore openRecordStore(String name, String vendorName, String suiteName) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
		if (name.length() > 32 || name.length() < 1) throw new IllegalArgumentException("Record store name is invalid");
		logln("openRecordStore " + name + " " + vendorName + " " + suiteName);
		String rootPath = getRootPath(name, vendorName, suiteName) + encodeBase64(name) + "/";
		RecordStore rs = findRecordStore(rootPath);
		if (rs != null) {
			rs.openCount++;
			return rs;
		}
		File file = new File(rootPath);
		if (!file.exists() || !file.isDirectory() || !new File(rootPath + "idx").exists()) {
			throw new RecordStoreNotFoundException(name);
		}
		IEmulatorFrontend e = Emulator.getEmulator();
		return new RecordStore(name, rootPath, vendorName.equals(e.getAppProperty("MIDlet-Vendor")) && suiteName.equals(e.getAppProperty("MIDlet-Name")), true);
	}

	public static void deleteRecordStore(String name) throws RecordStoreException, RecordStoreNotFoundException {
		if (name.length() > 32 || name.length() < 1) throw new IllegalArgumentException("Record store name is invalid");
		logln("deleteRecordStore " + name);
		String rootPath = homeRootPath + encodeBase64(name) + "/";
		if (findRecordStore(rootPath) != null) {
			logln("tried to delete active store");
			throw new RecordStoreException("Cannot delete currently opened record store: " + name);
		}
		File file = new File(rootPath);
		if (!file.exists()) {
			logln("not exist " + rootPath);
			throw new RecordStoreNotFoundException(name);
		}
		try {
			for (File value : file.listFiles()) {
				value.delete();
			}
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RecordStoreException(e.toString());
		}
	}

	public static String[] listRecordStores() {
		String[] list = null;
		try {
			if (homeRootPath == null)
				return null;
			File file = new File(homeRootPath);
			list = file.list();
			ArrayList<String> tmp = new ArrayList<String>();
			if (list != null) {
				for (String s : list) {
					if (!new File(file.getAbsolutePath() + "/" + s + "/idx").exists())
						continue;
					tmp.add(decodeBase64(s));
				}
			}
			list = tmp.toArray(new String[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public void deleteRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
		if (closed) throw new RecordStoreNotOpenException();
		if (!homeSuite && !writable) throw new SecurityException("Only read operations allowed");
		logln("deleteRecord " + name + " " + recordId + (!homeSuite ? " (guest)" : ""));
		synchronized (sync) {
			int i = records.indexOf(new Integer(recordId));
			if (i != -1) {
				records.removeElementAt(i);
				modify();
				try {
					File file = new File(rootPath + recordId + ".rms");
					if (file.exists()) {
						file.delete();
					}
				} catch (Exception e) {
					throw new RecordStoreException(e.toString());
				}
				writeIndex();
				recordDeleted(recordId);
				return;
			}
			throw new InvalidRecordIDException();
		}
	}

	public int getRecord(int recordId, byte[] b, int offset) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
		if (closed) throw new RecordStoreNotOpenException();
		logln("getRecord " + name + " " + recordId + (!homeSuite ? " (guest)" : ""));
		if (!recordIdExists(recordId)) {
			throw new InvalidRecordIDException("recordId=" + recordId);
		}
		try {
			File file = new File(rootPath + recordId + ".rms");
			int length = (int) file.length();
			DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
			dataInputStream.readFully(b, offset, length);
			dataInputStream.close();
			return length;
		} catch (Exception e) {
			throw new RecordStoreException("recordId=" + recordId);
		}
	}

	public byte[] getRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
		if (closed) throw new RecordStoreNotOpenException();
		logln("getRecord " + name + " " + recordId + (!homeSuite ? " (guest)" : ""));
		if (!recordIdExists(recordId)) {
			throw new InvalidRecordIDException("recordId=" + recordId);
		}
		try {
			File file = new File(rootPath + recordId + ".rms");
			long length = file.length();
			if (length > 0) {
				byte[] b = new byte[(int) length];
				DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
				dataInputStream.readFully(b);
				dataInputStream.close();
				return b;
			}
			return null;
		} catch (Exception e) {
			throw new RecordStoreException("recordId=" + recordId);
		}
	}

	public int getNumRecords() throws RecordStoreNotOpenException {
		if (closed) throw new RecordStoreNotOpenException();
		int r = records.size();
		logln("getNumRecords: " + r);
		return r;
	}

	private boolean recordIdExists(int recordId) {
		boolean b = false;
		for (int i = 0; i < records.size(); ++i) {
			if (((Integer) records.elementAt(i)).intValue() == recordId) {
				b = true;
			}
		}
		return b;
	}

	public int addRecord(byte[] data, int offset, int length) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {
		if (closed) throw new RecordStoreNotOpenException();
		if (!homeSuite && !writable) throw new SecurityException("Only read operations allowed");
		logln("addRecord " + name + " " + count + (!homeSuite ? " (guest)" : ""));
		synchronized (sync) {
			records.addElement(new Integer(count));
			modify();
			try {
				File file = new File(rootPath);
				if (!file.exists() && !file.isDirectory()) {
					file.mkdir();
				}
				file = new File(rootPath + count + ".rms");
				if (!file.exists()) file.createNewFile();
				OutputStream fileOutputStream = new FileOutputStream(file);
				if (data != null) {
					fileOutputStream.write(data, offset, length);
				}
				fileOutputStream.close();
				writeIndex();
			} catch (Exception e) {
				throw new RecordStoreException(e.toString());
			}
			recordAdded(count);
			return count++;
		}
	}

	public void setRecord(int recordId, byte[] data, int offset, int length) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {
		if (closed) throw new RecordStoreNotOpenException();
		if (!homeSuite && !writable) throw new SecurityException("Only read operations allowed");
		if (recordId == count) {
			addRecord(data, offset, length);
			return;
		}
		logln("setRecord " + name + " " + recordId + (!homeSuite ? " (guest)" : ""));
		if (!recordIdExists(recordId)) {
			throw new InvalidRecordIDException("recordId=" + recordId);
		}
		synchronized (sync) {
			modify();
			try {
				File file = new File(rootPath);
				if (!file.exists() && !file.isDirectory()) {
					file.mkdir();
				}
				file = new File(rootPath + recordId + ".rms");
				if (!file.exists()) file.createNewFile();
				OutputStream fileOutputStream = new FileOutputStream(file);
				if (data != null) {
					fileOutputStream.write(data, offset, length);
				}
				fileOutputStream.close();
				writeIndex();
			} catch (Exception e) {
				throw new RecordStoreException("recordId=" + recordId);
			}
			recordChanged(recordId);
		}
	}

	public void closeRecordStore() throws RecordStoreNotOpenException, RecordStoreException {
		if (closed) throw new RecordStoreNotOpenException();
		logln("closeRecordStore " + name + (!homeSuite ? " (guest)" : ""));
		if (--openCount < 1) {
			closed = true;
			openRecordStores.removeElement(this);
			if (!recordListeners.isEmpty()) {
				recordListeners.removeAllElements();
			}
			if (homeSuite || writable) writeIndex();
		}
	}

	private void modify() {
		lastModified = System.currentTimeMillis();
		version++;
	}

	private void writeIndex() throws RecordStoreException {
		try {
			File file = new File(rootPath);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(rootPath + "idx");
			if (!file.exists()) file.createNewFile();
			DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
			dataOutputStream.writeInt(count);
			dataOutputStream.writeInt(records.size());
			for (int i = 0; i < records.size(); ++i) {
				dataOutputStream.writeInt(((Integer) records.elementAt(i)).intValue());
			}
			dataOutputStream.writeLong(lastModified);
			dataOutputStream.writeInt(version);
			dataOutputStream.writeInt(authmode);
			dataOutputStream.writeBoolean(writable);
			dataOutputStream.close();
		} catch (Exception e) {
			throw new RecordStoreException(name);
		}
	}

	public void addRecordListener(RecordListener listener) {
		if (!recordListeners.contains(listener)) {
			recordListeners.addElement(listener);
		}
	}

	public void removeRecordListener(RecordListener listener) {
		recordListeners.removeElement(listener);
	}

	private void recordChanged(int n) {
		for (int i = 0; i < recordListeners.size(); ++i) {
			((RecordListener) recordListeners.elementAt(i)).recordChanged(this, n);
		}
	}

	private void recordAdded(int n) {
		for (int i = 0; i < recordListeners.size(); ++i) {
			((RecordListener) recordListeners.elementAt(i)).recordAdded(this, n);
		}
	}

	private void recordDeleted(int n) {
		for (int i = 0; i < recordListeners.size(); ++i) {
			((RecordListener) recordListeners.elementAt(i)).recordDeleted(this, n);
		}
	}

	private static String getRootPath(String name, String vendorName, String suiteName) throws RecordStoreNotFoundException {
		String s = rmsRootDir + encodeBase64(vendorName + "_" + suiteName) + "/";
		File file = new File(s);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdir();
		}
		return s;
	}

	private static String encodeBase64(String name) {
		try {
			return new String(Base64.getEncoder().encode(name.getBytes("UTF-8")), "UTF-8").replace('/', '-');
		} catch (Exception e) {
			return name;
		}
	}

	private static String decodeBase64(String name) {
		try {
			return new String(Base64.getDecoder().decode(name.replace('-', '/').getBytes("UTF-8")), "UTF-8");
		} catch (Exception e) {
			return name;
		}
	}

	private static RecordStore findRecordStore(String rootPath) {
		int num = openRecordStores.size();
		for (int i = 0; i < num; i++) {
			RecordStore rs = (RecordStore) openRecordStores.elementAt(i);
			if (rs.rootPath.equals(rootPath)) {
				return rs;
			}
		}
		return null;
	}

	int[] getRecordIds() throws RecordStoreException {
		if (closed) throw new RecordStoreNotOpenException();
		int[] recordIds = new int[records.size()];
		for (int i = 0; i < recordIds.length; i++) {
			recordIds[i] = ((Integer) records.elementAt(i)).intValue();
		}
		return recordIds;
	}


	private static void logln(String s) {
		Emulator.getEmulator().getLogStream().println("[RMS] " + s);
	}

	private static void log(String s) {
		Emulator.getEmulator().getLogStream().print("[RMS] " + s);
	}

	private static void logendln(String s) {
		Emulator.getEmulator().getLogStream().println(s);
	}
}
