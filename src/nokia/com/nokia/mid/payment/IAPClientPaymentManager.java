package com.nokia.mid.payment;

import java.io.InputStream;

import emulator.custom.CustomJarResources;

public final class IAPClientPaymentManager {
	public static final int DEFAULT_AUTHENTICATION = 0;
	public static final int ONLY_IN_SILENT_AUTHENTICATION = 1;
	public static final int NO_FORCED_RESTORATION = 0;
	public static final int FORCED_AUTOMATIC_RESTORATION = 1;
	public static final int SUCCESS = 1;
	public static final int GENERAL_FAIL = -1;
	public static final int PENDING_REQUEST = -2;
	public static final int NULL_INPUT_PARAMETER = -3;
	public static final int KNI_INTERNAL_FAIL = -4;
	public static final int OUT_OF_MEMORY = -5;
	public static final int TEST_SERVER = 1;
	public static final int SIMULATION = 2;
	public static final int PURCHASE = 101;
	public static final int RESTORE = 102;
	public static final int FAIL = 103;
	public static final int NORMAL = 104;
	private static IAPClientPaymentManager inst = new IAPClientPaymentManager();

	public static IAPClientPaymentManager getIAPClientPaymentManager() throws IAPClientPaymentException {
		return inst;
	}

	public static void setIAPClientPaymentListener(IAPClientPaymentListener listener) {
	}

	public int getProductData(String paramString) {
		return FAIL;
	}

	public int getProductData(String[] paramArrayOfString) {
		return FAIL;
	}

	public int purchaseProduct(String paramString, int paramInt) {
		return FAIL;
	}

	public int restoreProduct(String paramString, int paramInt) {
		return FAIL;
	}

	public int getRestorableProducts(int paramInt) {
		return FAIL;
	}

	public int getUserAndDeviceId(int paramInt) {
		return FAIL;
	}

	public InputStream getDRMResourceAsStream(String s) {
		return null;
	}
}
