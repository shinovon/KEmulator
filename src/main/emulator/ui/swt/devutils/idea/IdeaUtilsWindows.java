package emulator.ui.swt.devutils.idea;

import org.eclipse.swt.widgets.Shell;

import java.util.Set;

public class IdeaUtilsWindows extends IdeaUtils {
	public IdeaUtilsWindows(Shell parent) {
		super(parent);
	}

	@Override
	protected Set<IdeaInstallation> getIdeaInstallationPath() {
		return null;
	}

	@Override
	protected String getDefaultJdkTablePath() {
		return "";
	}

	//#region Binary pathfinder

	//#endregion
}
