package emulator.sensor;

import java.util.Enumeration;
import java.util.Hashtable;
//class need to be renamed into SensorPropertiesImpl
public final class h implements SensorProperties {
	private Hashtable properties;

	public h() {
		super();
		this.properties = new Hashtable(6);
	}

	public final void setProperty(final String s, final Object o) {
		this.properties.put(s, o);
	}

	public final Object getProperty(final String s) {
		return this.properties.get(s);
	}

	public final String[] getPropertyNames() {
		final String[] array = new String[this.properties.size()];
		int n = 0;
		final Enumeration<String> keys = this.properties.keys();
		while (keys.hasMoreElements()) {
			array[n++] = keys.nextElement();
		}
		return array;
	}
}
