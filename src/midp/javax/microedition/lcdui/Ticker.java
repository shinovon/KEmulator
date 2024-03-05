package javax.microedition.lcdui;

public class Ticker {
    private String aString353;

    public Ticker(final String string) {
        super();
        this.setString(string);
    }

    public void setString(final String aString353) {
        if (aString353 == null) {
            throw new NullPointerException();
        }
        this.aString353 = aString353;
    }

    public String getString() {
        return this.aString353;
    }
}
