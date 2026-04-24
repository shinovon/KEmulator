package javax.microedition.lcdui;

import emulator.ui.TargetedCommand;
import java.util.Vector;

public final class AutomationStateExtractor {
	private AutomationStateExtractor() {
	}

	public static String getDisplayableKind(Displayable displayable) {
		if (displayable == null) {
			return "none";
		}

		if (displayable instanceof TextBox) {
			return "text_box";
		}

		if (displayable instanceof List) {
			return "list";
		}

		if (displayable instanceof Canvas) {
			return "canvas";
		}

		if (displayable instanceof Alert) {
			return "alert";
		}

		if (displayable instanceof Screen) {
			return "screen";
		}

		return displayable.getClass().getName();
	}

	public static String getLeftSoftLabel(Displayable displayable) {
		if (displayable == null) {
			return "";
		}

		Command command = displayable.getLeftSoftCommand();

		return command == null ? "" : command.getLabel();
	}

	public static String getRightSoftLabel(Displayable displayable) {
		if (displayable == null) {
			return "";
		}

		Command command = displayable.getRightSoftCommand();

		return command == null ? "" : command.getLabel();
	}

	public static Vector<TargetedCommand> buildCommands(Displayable displayable) {
		return displayable == null ? new Vector<TargetedCommand>() : displayable.buildAllCommands();
	}
}
