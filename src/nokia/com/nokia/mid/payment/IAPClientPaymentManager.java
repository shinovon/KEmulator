package com.nokia.mid.payment;

import java.io.InputStream;
import java.util.Properties;

import emulator.Emulator;
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
    private static IAPClientPaymentListener listener;

    public static IAPClientPaymentManager getIAPClientPaymentManager() throws IAPClientPaymentException {
        System.out.println("getIAPClientPaymentManager");
        Thread.dumpStack();
        IAPClientPaymentManager localIAPClientPaymentManager = new IAPClientPaymentManager();
        return localIAPClientPaymentManager;
    }

    public static void setIAPClientPaymentListener(IAPClientPaymentListener listener) {
        System.out.println("setIAPClientPaymentListener " + listener);
        Thread.dumpStack();
    }

    public int getProductData(String paramString) {
        System.out.println("getProductData1");
        Thread.dumpStack();
        int i = 1;
        return i;
    }

    public int getProductData(String[] paramArrayOfString) {
        System.out.println("getProductData2");
        Thread.dumpStack();
        int i = 1;
        return i;
    }

    public int purchaseProduct(String paramString, int paramInt) {
        System.out.println("purchaseProduct");
        Thread.dumpStack();
        int i = 1;
        return i;
    }

    public int restoreProduct(String paramString, int paramInt) {
        System.out.println("restoreProduct");
        Thread.dumpStack();
        listener.restorationCompleted(1, "1212");
        int i = 1;
        return i;
    }

    public int getRestorableProducts(int paramInt) {
        System.out.println("getRestorableProducts");
        Thread.dumpStack();
        int i = 1;
        return i;
    }

    public int getUserAndDeviceId(int paramInt) {
        System.out.println("getUserAndDeviceId");
        Thread.dumpStack();
        int i = 1;
        return i;
    }

    public InputStream getDRMResourceAsStream(String s) {
        System.out.println("getDRMResourceAsStream " + s);
        Thread.dumpStack();
        InputStream localInputStream = CustomJarResources.getResourceStream(s);
        return localInputStream;
    }
}
