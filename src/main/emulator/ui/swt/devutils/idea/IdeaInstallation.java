package emulator.ui.swt.devutils.idea;

import java.util.Objects;

public class IdeaInstallation {
	public final String version;
	public final String path;


	public IdeaInstallation(String version, String path) {
		this.version = version;
		this.path = path;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IdeaInstallation)) return false;
		IdeaInstallation that = (IdeaInstallation) o;
		return Objects.equals(path, that.path);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(path);
	}
}
