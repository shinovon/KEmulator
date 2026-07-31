package fixtures;

import emulator.Permission;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;

public final class MegaCliFixtureMidlet extends MIDlet implements CommandListener {
	private final Command openEditorCommand = new Command("Open editor", Command.OK, 1);
	private final Command openCanvasCommand = new Command("Open canvas", Command.SCREEN, 2);
	private final Command askImeiCommand = new Command("Ask IMEI", Command.SCREEN, 3);
	private final Command askCameraCommand = new Command("Ask camera", Command.SCREEN, 4);
	private final Command askRaceCommand = new Command("Ask permission race", Command.SCREEN, 5);
	private final Command autoMutateCommand = new Command("Auto mutate", Command.SCREEN, 6);
	private final Command crashCommand = new Command("Crash command", Command.SCREEN, 7);
	private final Command hangCommand = new Command("Hang command", Command.SCREEN, 8);
	private final Command exitCommand = new Command("Exit", Command.EXIT, 9);
	private final Command saveCommand = new Command("Save", Command.OK, 1);
	private final Command backCommand = new Command("Back", Command.BACK, 2);
	private final Command lateCommand = new Command("Late command", Command.SCREEN, 9);

	private List menu;
	private TextBox editor;
	private MegaCanvas canvas;
	private boolean lateCommandAdded;
	private int raceCompleted;
	private StringBuffer raceResult = new StringBuffer();

	protected void startApp() {
		if (menu == null) {
			menu = new List("Mega menu", List.IMPLICIT);
			menu.append("Open editor", null);
			menu.append("Open canvas", null);
			menu.append("Ask IMEI", null);
			menu.append("Ask camera", null);
			menu.append("Ask permission race", null);
			menu.append("Auto mutate", null);
			menu.append("Crash command", null);
			menu.append("Hang command", null);
			menu.addCommand(openEditorCommand);
			menu.addCommand(openCanvasCommand);
			menu.addCommand(askImeiCommand);
			menu.addCommand(askCameraCommand);
			menu.addCommand(askRaceCommand);
			menu.addCommand(autoMutateCommand);
			menu.addCommand(crashCommand);
			menu.addCommand(hangCommand);
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
				runMenuIndex(menu.getSelectedIndex());

				return;
			}

			if (command == openEditorCommand) {
				showEditor();

				return;
			}

			if (command == openCanvasCommand) {
				showCanvas();

				return;
			}

			if (command == askImeiCommand) {
				askImei();

				return;
			}

			if (command == askCameraCommand) {
				askCamera();

				return;
			}

			if (command == askRaceCommand) {
				askPermissionRace();

				return;
			}

			if (command == autoMutateCommand) {
				autoMutate();

				return;
			}

			if (command == lateCommand) {
				menu.setTitle("Late command selected");
				Display.getDisplay(this).setCurrent(menu);

				return;
			}

			if (command == crashCommand) {
				throw new RuntimeException("Mega fixture crash command");
			}

			if (command == hangCommand) {
				hangForever();

				return;
			}

			if (command == exitCommand) {
				notifyDestroyed();
			}

			return;
		}

		if (displayable == editor) {
			if (command == saveCommand) {
				menu.setTitle("Saved " + editor.getString());
				Display.getDisplay(this).setCurrent(menu);

				return;
			}

			if (command == backCommand) {
				Display.getDisplay(this).setCurrent(menu);
			}

			return;
		}

		if (displayable == canvas && command == backCommand) {
			Display.getDisplay(this).setCurrent(menu);
		}
	}

	private void runMenuIndex(int index) {
		if (index == 0) {
			showEditor();
		} else if (index == 1) {
			showCanvas();
		} else if (index == 2) {
			askImei();
		} else if (index == 3) {
			askCamera();
		} else if (index == 4) {
			askPermissionRace();
		} else if (index == 5) {
			autoMutate();
		} else if (index == 6) {
			throw new RuntimeException("Mega fixture crash command");
		} else if (index == 7) {
			hangForever();
		}
	}

	private void showEditor() {
		if (editor == null) {
			editor = new TextBox("Mega editor", "alpha", 128, 0);
			editor.addCommand(saveCommand);
			editor.addCommand(backCommand);
			editor.setCommandListener(this);
		}

		Display.getDisplay(this).setCurrent(editor);
	}

	private void showCanvas() {
		if (canvas == null) {
			canvas = new MegaCanvas();
			canvas.addCommand(backCommand);
			canvas.setCommandListener(this);
		}

		Display.getDisplay(this).setCurrent(canvas);
	}

	private void askImei() {
		String imei = System.getProperty("com.siemens.IMEI");
		menu.setTitle(imei == null ? "IMEI denied" : "IMEI allowed " + imei);
		Display.getDisplay(this).setCurrent(menu);
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

	private void askPermissionRace() {
		synchronized (this) {
			raceCompleted = 0;
			raceResult = new StringBuffer();
		}

		menu.setTitle("Permission race pending");
		Display.getDisplay(this).setCurrent(menu);
		startPermissionThread("mega.race.one", "one");
		startPermissionThread("media.camera", "camera");
	}

	private void startPermissionThread(final String permission, final String label) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				boolean allowed = false;
				try {
					Permission.checkPermission(permission);
					allowed = true;
				} catch (SecurityException denied) {
					allowed = false;
				}

				synchronized (MegaCliFixtureMidlet.this) {
					if (raceResult.length() > 0) {
						raceResult.append(",");
					}

					raceResult.append(label).append(allowed ? ":allow" : ":deny");
					raceCompleted++;
					if (raceCompleted == 2) {
						Display.getDisplay(MegaCliFixtureMidlet.this).callSerially(new Runnable() {
							public void run() {
								menu.setTitle("Permission race " + raceResult.toString());
								Display.getDisplay(MegaCliFixtureMidlet.this).setCurrent(menu);
							}
						});
					}
				}
			}
		});
		thread.start();
	}

	private void autoMutate() {
		menu.setTitle("Auto mutate pending");
		Display.getDisplay(this).setCurrent(menu);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(700L);
				} catch (InterruptedException ignored) {
				}

				Display.getDisplay(MegaCliFixtureMidlet.this).callSerially(new Runnable() {
					public void run() {
						if (!lateCommandAdded) {
							lateCommandAdded = true;
							menu.addCommand(lateCommand);
						}

						menu.setTitle("Auto mutate done");
						Display.getDisplay(MegaCliFixtureMidlet.this).setCurrent(menu);
					}
				});
			}
		});
		thread.start();
	}

	private void hangForever() {
		while (true) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException ignored) {
			}
		}
	}

	private static final class MegaCanvas extends Canvas {
		private String status = "Canvas ready";
		private int startX = -1;
		private int startY = -1;
		private boolean dragged;

		private MegaCanvas() {
			setTitle(status);
		}

		protected void paint(Graphics graphics) {
			graphics.setColor(0xFFFFFF);
			graphics.fillRect(0, 0, getWidth(), getHeight());
			graphics.setColor(0x003366);
			graphics.drawRect(8, 8, getWidth() - 16, getHeight() - 16);
			graphics.drawString("Mega CLI fixture", 16, 24, Graphics.LEFT | Graphics.TOP);
			graphics.drawString(status, 16, 52, Graphics.LEFT | Graphics.TOP);
		}

		protected void keyPressed(int keyCode) {
			updateStatus("Key " + keyCode);
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
