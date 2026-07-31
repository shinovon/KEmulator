package fixtures;

import emulator.Permission;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;

public final class CommandFixtureMidlet extends MIDlet implements CommandListener {
	private final Command openCommand = new Command("Open editor", Command.OK, 1);
	private final Command touchCommand = new Command("Open touch", Command.SCREEN, 2);
	private final Command imeiCommand = new Command("Ask IMEI", Command.SCREEN, 3);
	private final Command cameraCommand = new Command("Ask camera", Command.SCREEN, 4);
	private final Command exitCommand = new Command("Exit", Command.EXIT, 5);
	private final Command saveCommand = new Command("Save", Command.OK, 1);
	private final Command backCommand = new Command("Back", Command.BACK, 2);

	private List menu;
	private TextBox editor;
	private TouchCanvas touchCanvas;

	protected void startApp() {
		if (menu == null) {
			menu = new List(initialMenuTitle(), List.IMPLICIT);
			menu.append("Open editor", null);
			menu.append("Open touch", null);
			menu.append("Ask IMEI", null);
			menu.append("Ask camera", null);
			menu.addCommand(openCommand);
			menu.addCommand(touchCommand);
			menu.addCommand(imeiCommand);
			menu.addCommand(cameraCommand);
			menu.addCommand(exitCommand);
			menu.setCommandListener(this);
		}

		Display.getDisplay(this).setCurrent(menu);
	}

	protected void pauseApp() {
	}

	protected void destroyApp(boolean unconditional) {
	}

	public void commandAction(Command command, Displayable displayable) {
		if (displayable == menu) {
			if (command == List.SELECT_COMMAND) {
				int selected = menu.getSelectedIndex();
				if (selected == 0) {
					showEditor();
				} else if (selected == 1) {
					showTouchCanvas();
				} else if (selected == 2) {
					String imei = System.getProperty("com.siemens.IMEI");
					menu.setTitle(imei == null ? "IMEI denied" : "IMEI " + imei);
					Display.getDisplay(this).setCurrent(menu);
				} else if (selected == 3) {
					askCamera();
				}

				return;
			}

			if (command == openCommand) {
				showEditor();

				return;
			}

			if (command == imeiCommand) {
				String imei = System.getProperty("com.siemens.IMEI");
				menu.setTitle(imei == null ? "IMEI denied" : "IMEI " + imei);
				Display.getDisplay(this).setCurrent(menu);

				return;
			}

			if (command == cameraCommand) {
				askCamera();

				return;
			}

			if (command == touchCommand) {
				showTouchCanvas();

				return;
			}

			if (command == exitCommand) {
				notifyDestroyed();
			}

			return;
		}

		if (displayable == editor) {
			if (command == saveCommand) {
				menu.setTitle("Saved");
				Display.getDisplay(this).setCurrent(menu);

				return;
			}

			if (command == backCommand) {
				Display.getDisplay(this).setCurrent(menu);
			}

			return;
		}

		if (displayable == touchCanvas && command == backCommand) {
			Display.getDisplay(this).setCurrent(menu);
		}
	}

	private void showEditor() {
		if (editor == null) {
			editor = new TextBox("Editor", "hello", 64, 0);
			editor.addCommand(saveCommand);
			editor.addCommand(backCommand);
			editor.setCommandListener(this);
		}

		Display.getDisplay(this).setCurrent(editor);
	}

	private void showTouchCanvas() {
		if (touchCanvas == null) {
			touchCanvas = new TouchCanvas();
			touchCanvas.addCommand(backCommand);
			touchCanvas.setCommandListener(this);
		}

		Display.getDisplay(this).setCurrent(touchCanvas);
	}

	private void askCamera() {
		try {
			Permission.checkPermission("media.camera");
			menu.setTitle("Camera allowed");
		} catch (SecurityException denied) {
			menu.setTitle("Camera denied");
		}

		Display.getDisplay(this).setCurrent(menu);
	}

	private String initialMenuTitle() {
		String customTitle = getAppProperty("Fixture-Menu-Title");
		if (customTitle != null) {
			customTitle = customTitle.trim();
			if (customTitle.length() > 0) {
				return customTitle;
			}
		}

		InputStream in = getClass().getResourceAsStream("/fixtures/menu-title.txt");
		if (in != null) {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[128];
				int read;
				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
				}

				String resourceTitle = new String(out.toByteArray(), "UTF-8").trim();
				if (resourceTitle.length() > 0) {
					return resourceTitle;
				}
			} catch (Exception ignored) {
			} finally {
				try {
					in.close();
				} catch (Exception ignored) {
				}
			}
		}

		return "Fixture Menu";
	}

	private static final class TouchCanvas extends Canvas {
		private String status = "Tap or drag";
		private int startX = -1;
		private int startY = -1;
		private boolean dragged;

		private TouchCanvas() {
			setTitle("Touch canvas");
		}

		protected void paint(Graphics graphics) {
			graphics.setColor(0xFFFFFF);
			graphics.fillRect(0, 0, getWidth(), getHeight());
			graphics.setColor(0x204A87);
			graphics.drawRect(12, 12, getWidth() - 24, getHeight() - 24);
			graphics.drawString("Touch fixture", 20, 24, Graphics.LEFT | Graphics.TOP);
			graphics.drawString(status, 20, 52, Graphics.LEFT | Graphics.TOP);
			graphics.drawString("drag between points", 20, 80, Graphics.LEFT | Graphics.TOP);
		}

		protected void pointerPressed(int x, int y) {
			startX = x;
			startY = y;
			dragged = false;
			updateStatus("Tap " + x + "," + y);
		}

		protected void pointerDragged(int x, int y) {
			dragged = true;
			updateStatus("Drag " + startX + "," + startY + " -> " + x + "," + y);
		}

		protected void pointerReleased(int x, int y) {
			if (dragged) {
				updateStatus("Drag " + startX + "," + startY + " -> " + x + "," + y);
			} else {
				updateStatus("Tap " + x + "," + y);
			}
		}

		private void updateStatus(String nextStatus) {
			status = nextStatus;
			setTitle(nextStatus);
			repaint();
		}
	}
}
