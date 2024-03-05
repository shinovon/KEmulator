package javax.microedition.lcdui;

public class AlertType {
    public static final AlertType INFO;
    public static final AlertType WARNING;
    public static final AlertType ERROR;
    public static final AlertType ALARM;
    public static final AlertType CONFIRMATION;

    protected AlertType() {
        super();
    }

    AlertType(final int n) {
        super();
    }

    public boolean playSound(final Display display) {
        return false;
    }

    static {
        INFO = new AlertType(1);
        WARNING = new AlertType(2);
        ERROR = new AlertType(3);
        ALARM = new AlertType(4);
        CONFIRMATION = new AlertType(5);
    }
}
