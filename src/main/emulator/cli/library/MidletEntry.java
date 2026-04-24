package emulator.cli.library;

import mjson.Json;

public final class MidletEntry {
	public final int index;
	public final String key;
	public final String name;
	public final String icon;
	public final String className;

	public MidletEntry(int index, String key, String name, String icon, String className) {
		this.index = index;
		this.key = key;
		this.name = name;
		this.icon = icon;
		this.className = className;
	}

	public Json toJson() {
		return Json.object()
			.set("index", index)
			.set("key", key)
			.set("name", name)
			.set("icon", icon)
			.set("className", className);
	}
}
