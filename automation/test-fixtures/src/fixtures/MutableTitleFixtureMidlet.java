package fixtures;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;

public final class MutableTitleFixtureMidlet extends MIDlet {
	private final List menu;
	private final Command openCommand;
	private final Command backCommand;
	private TextBox editor;
	private boolean started;

	public MutableTitleFixtureMidlet() {
		menu = new List("Mutable menu", List.IMPLICIT);
		menu.append("Open editor", null);
		openCommand = new Command("Open editor", Command.ITEM, 1);
		backCommand = new Command("Back", Command.BACK, 1);
		menu.addCommand(openCommand);
		menu.setSelectCommand(openCommand);
		menu.setCommandListener(new CommandListener() {
			public void commandAction(Command command, Displayable displayable) {
				if (command == openCommand || command == List.SELECT_COMMAND) {
					showEditor();
				}
			}
		});
	}

	protected void startApp() {
		Display.getDisplay(this).setCurrent(menu);
		if (started) {
			return;
		}

		started = true;
		Thread transition = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1200L);
				} catch (InterruptedException ignored) {
				}

				Display.getDisplay(MutableTitleFixtureMidlet.this).callSerially(new Runnable() {
					public void run() {
						menu.setTitle("Mutable menu updated");
					}
				});
			}
		});
		transition.start();
	}

	private void showEditor() {
		if (editor == null) {
			editor = new TextBox("Mutable editor", "ready", 32, 0);
			editor.addCommand(backCommand);
			editor.setCommandListener(new CommandListener() {
				public void commandAction(Command command, Displayable displayable) {
					if (command == backCommand) {
						Display.getDisplay(MutableTitleFixtureMidlet.this).setCurrent(menu);
					}
				}
			});
		}

		Display.getDisplay(this).setCurrent(editor);
	}

	protected void pauseApp() {
	}

	protected void destroyApp(boolean unconditional) {
	}
}
