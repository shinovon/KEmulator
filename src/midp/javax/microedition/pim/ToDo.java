package javax.microedition.pim;

public abstract interface ToDo
        extends PIMItem {
    public static final int CLASS = 100;
    public static final int CLASS_CONFIDENTIAL = 200;
    public static final int CLASS_PRIVATE = 201;
    public static final int CLASS_PUBLIC = 202;
    public static final int COMPLETED = 101;
    public static final int COMPLETION_DATE = 102;
    public static final int DUE = 103;
    public static final int NOTE = 104;
    public static final int PRIORITY = 105;
    public static final int REVISION = 106;
    public static final int SUMMARY = 107;
    public static final int UID = 108;
}
