package emulator.automation.shared;

public final class MidletDescriptor {
	public final int index;
	public final String key;
	public final String name;
	public final String icon;
	public final String className;

	public MidletDescriptor(int index, String key, String name, String icon, String className) {
		this.index = index;
		this.key = key;
		this.name = name;
		this.icon = icon;
		this.className = className;
	}
}
