package javax.microedition.sensor;

import java.util.Hashtable;

public class Unit {
	private static Hashtable units;
	private String symbols;

	private Unit(final String symbols) {
		super();
		this.symbols = symbols;
	}

	public static Unit getUnit(final String s) {
		if (s == null) {
			throw new NullPointerException();
		}
		if (s.length() == 0) {
			throw new IllegalArgumentException();
		}
		Unit unit = null;
		synchronized (Unit.units) {
			Unit unit2;
			if ((unit2 = (Unit) Unit.units.get(s)) == null) {
				unit2 = new Unit(s);
				Unit.units.put(s, unit2);
			}
			unit = unit2;
		}
		return unit;
	}

	public String toString() {
		return this.symbols;
	}

	static {
		Unit.units = new Hashtable();
	}
}
