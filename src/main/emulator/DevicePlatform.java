package emulator;

import org.w3c.dom.*;

import java.util.*;

public final class DevicePlatform {
    String name;
    Properties properties;
    DevicePlatform parent;

    public DevicePlatform(final Element element) {
        super();
        this.properties = new Properties();
        this.name = element.getAttribute("name");
        this.parse(element);
    }

    private void parse(final Element element) {
        final NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node item;
            if ((item = childNodes.item(i)).getNodeType() == 1) {
                final Element element2;
                if ((element2 = (Element) item).getNodeName().toLowerCase() == "define") {
                    this.put(element2);
                }
            }
        }
        this.parent = Devices.getPlatform(element.getAttribute("parent").toLowerCase());
        DevicePlatform e = this;
        DevicePlatform ane1203;
        while ((ane1203 = e.parent) != null) {
            final Enumeration<Object> keys = ane1203.properties.keys();
            while (keys.hasMoreElements()) {
                final String s = (String) keys.nextElement();
                final String s2 = (String) ane1203.properties.get(s);
                if (!this.properties.containsKey(s)) {
                    this.properties.put(s, s2);
                }
            }
            e = ane1203;
        }
    }

    private void put(final Element element) {
        final String attribute = element.getAttribute("name");
        final String attribute2 = element.getAttribute("value");
        if (!this.properties.containsKey(attribute)) {
            this.properties.put(attribute, attribute2);
        }
    }

    public final int getInt(final String s) {
        int int1 = 0;
        try {
            int1 = Integer.parseInt(this.getString(s));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return int1;
    }

    public final String getString(final String s) {
        String s2;
        if ((s2 = (String) this.properties.get(s)) == null) {
            s2 = "";
        }
        return s2;
    }

    public final boolean exists(final String s) {
        if (this.properties.get(s) == null) {
            return false;
        }
        return true;
    }

    public final boolean hasNokiaUI() {
        return this.getInt("NOKIA_UI") == 1;
    }
}
