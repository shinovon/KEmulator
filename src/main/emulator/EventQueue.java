package emulator;

import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.lcdui.*;
import net.rim.device.api.system.*;
import emulator.graphics2D.*;

public final class EventQueue implements Runnable {
	private ArrayList<Integer> events;
	private ArrayList<Integer> mouseEvents;
	//private int anInt1215;
	//private int anInt1220;
	private int anInt1222;
	private boolean running;
	private ThreadCallSerially threadCallSerially = new ThreadCallSerially(this);
	private Thread eventThread;
	private boolean canvasHidden;
	private boolean repainted2;
	private boolean repainted;
	private Thread mouseThread;
	private int dx;
	private int dy;
	private boolean changed;
	protected long last;
	public static Runnable aRunnable1219;

	public EventQueue() {
		super();
		this.events = new ArrayList<Integer>(50);
		this.mouseEvents = new ArrayList<Integer>(50);
		//this.anInt1215 = 0;
		//this.anInt1220 = 0;
		this.anInt1222 = 0;
		this.canvasHidden = false;
		this.running = true;
		this.repainted2 = true;
		this.repainted = true;
		(this.eventThread = new Thread(this)).setPriority(1);
		this.eventThread.start();
		
		(this.mouseThread = new Thread() {
			public void run() {
				while(running) {
					if (Emulator.getMIDlet() != null && !canvasHidden) {
						int i = nextMouseEvent();
						if(i != 0) {
							if ((i & 0x10000000) != 0x0) {
								if (Emulator.getCurrentDisplay().getCurrent() == null) {
									continue;
								}
								final int x = method719(i, i, false);
								final int y = method719(i, i >> 12, false);
								if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
									Emulator.getCanvas().invokePointerPressed(x, y);
									continue;
								}
								Emulator.getScreen().invokePointerPressed(x, y);
								continue;
							} else if ((i & 0x20000000) != 0x0) {
								if (Emulator.getCurrentDisplay().getCurrent() == null) {
									continue;
								}
								final int x = method719(i, i, false);
								final int y = method719(i, i >> 12, false);
								if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
									Emulator.getCanvas().invokePointerReleased(x, y);
									continue;
								}
								Emulator.getScreen().invokePointerReleased(x, y);
								continue;
							} else {
								if ((i & 0x40000000) == 0x0) {
									continue;
								}
								if (Emulator.getCurrentDisplay().getCurrent() == null) {
									continue;
								}
								final int x = method719(i, i, false);
								final int y = method719(i, i >> 12, false);
								if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
									Emulator.getCanvas().invokePointerDragged(x, y);
									continue;
								}
								Emulator.getScreen().invokePointerDragged(x, y);
								continue;
							}
						}
					}

					try {
						Thread.sleep(1L);
						Thread.yield();
					} catch (Exception ex) {
					}
				}
			}
		}).setPriority(1);
		this.mouseThread.start();
		
	}

	public final void stop() {
		this.running = false;
	}

	public final void method717(final int n, final int n2) {
		final int n3 = n | method718(n2);
		this.queue(n3);
		Emulator.getNetMonitor().b(n3);
	}

	private static int method718(final int n) {
		if (n < 0) {
			return (-n & 0xFFF) | 0x8000;
		}
		return n & 0xFFF;
	}

	private int method719(int i, final int n, final boolean b) {
		final int n2 = n & 0xFFF;
		if (!b) {
			return n2;
		}
		if ((i & 0x8000) != 0x0) {
			return -n2;
		}
		return n2;
	}

	public final void mouseDown(final int x, final int y) {
		//try {
		//	if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
		//		Emulator.getCanvas().invokePointerPressed(x, y);
		//	} else Emulator.getScreen().invokePointerPressed(x, y);
		//} catch (Throwable e) {
		//}
		mouse(268435456, x, y);
	}
	
	public final void mouseUp(final int x, final int y) {
		//try {
		//	if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
		//		Emulator.getCanvas().invokePointerReleased(x, y);
		//	} else Emulator.getScreen().invokePointerReleased(x, y);
		//} catch (Throwable e) {
		//}
		mouse(536870912, x, y);
	}
	
	public final void mouseDrag(final int x, final int y) {
		//try {
		//	if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
		//		Emulator.getCanvas().invokePointerDragged(x, y);
		//	} else Emulator.getScreen().invokePointerDragged(x, y);
		//} catch (Throwable e) {
		//}
		//dx = x;
		//dy = y;
		//changed = true;
		//resetTimer();
		mouse(1073741824, x, y);
	}

	public void queue(int n, int x, int y) {
		final int n4 = n | method718(x) | method718(y) << 12;
		this.queue(n4);
	}

	//private void resetTimer() {
	//	last = System.currentTimeMillis();
	//}

	public synchronized final void mouse(final int n, final int x, final int y) {
		final int n4 = n | method718(x) | method718(y) << 12;
		//this.queue(n4);
		mouseEvents.add(n4);
		//Emulator.getNetMonitor().b(n4);
	}
	
	private synchronized int nextMouseEvent() {
		//synchronized (events) {
		if(mouseEvents.size() == 0) return 0;
		final int n = mouseEvents.get(0);
		mouseEvents.remove(0);
		return n;
		//}
	}

	/**
	 * queue event
	 */
	public synchronized final void queue(final int n) {
		synchronized (events) {
		if (n == 1) {
			this.repainted2 = false;
		}
		if (n == 3) {
			this.repainted = false;
		}
		if (n == 15) {
			this.canvasHidden = false;
		}
		events.add(n);
		}
	}

	private synchronized int nextEvent() {
		synchronized (events) {
		if(events.size() == 0) return 0;
		final int n = events.get(0);
		events.remove(0);
		return n;
		}
	}

	public final void waitRepainted2() {
		if (Thread.currentThread() == this.eventThread) {
			return;
		}
		while (!this.repainted2 && this.running) {
			try {
				Thread.sleep(1L);
			} catch (Exception ex) {
			}
		}
	}

	public final void waitRepainted() {
		if (Thread.currentThread() == this.eventThread) {
			return;
		}
		while (!this.repainted && this.running) {
			try {
				Thread.sleep(1L);
			} catch (Exception ex) {
			}
		}
	}

	public final void run() {
		new Thread(this.threadCallSerially).start();
			while (this.running) {
				try {
				if (Emulator.getMIDlet() != null) {
					if (!this.canvasHidden) {
						switch (this.anInt1222 = this.nextEvent()) {
						case 1: {
							if (Emulator.getCanvas() == null
									|| Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
								this.repainted2 = true;
								break;
							}
							final IImage backBufferImage = Emulator.getEmulator().getScreen().getBackBufferImage();
							final IImage xRayScreenImage = Emulator.getEmulator().getScreen().getXRayScreenImage();
							try {
								Emulator.getCanvas().invokePaint(new Graphics(backBufferImage, xRayScreenImage));
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							(Settings.xrayView ? xRayScreenImage : backBufferImage)
									.cloneImage(Emulator.getEmulator().getScreen().getScreenImage());
							Emulator.getEmulator().getScreen().repaint();
							this.repainted2 = true;
							break;
						}
						case 2: {
							this.threadCallSerially.method590(EventQueue.aRunnable1219);
							break;
						}
						case 3: {
							if (Emulator.getCanvas() == null
									|| Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
								this.repainted = true;
								break;
							}
							final IImage screenImage = Emulator.getEmulator().getScreen().getScreenImage();
							final IImage backBufferImage2 = Emulator.getEmulator().getScreen().getBackBufferImage();
							final IImage xRayScreenImage2 = Emulator.getEmulator().getScreen().getXRayScreenImage();
							(Settings.xrayView ? xRayScreenImage2 : backBufferImage2).cloneImage(screenImage);
							Emulator.getEmulator().getScreen().repaint();
							this.repainted = true;
							break;
						}
						case 10: {
							if (Emulator.getMIDlet() != null) {
								new Thread(new InvokeStartAppRunnable(this)).start();
								break;
							}
							break;
						}
						case 11: {
							Emulator.getNetMonitor().a();
							this.stop();
							if (Emulator.getMIDlet() != null) {
								this.repainted2 = true;
								this.repainted = true;
								new Thread(new InvokeDestroyAppRunnable(this)).start();
								break;
							}
							break;
						}
						case 4: {
							if (Emulator.getScreen() == null) {
								break;
							}
							if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getScreen()) {
								break;
							}
							final IImage backBufferImage3 = Emulator.getEmulator().getScreen().getBackBufferImage();
							final IImage xRayScreenImage3 = Emulator.getEmulator().getScreen().getXRayScreenImage();
							Emulator.getScreen().invokePaint(new Graphics(backBufferImage3, xRayScreenImage3));
							(Settings.xrayView ? xRayScreenImage3 : backBufferImage3)
									.cloneImage(Emulator.getEmulator().getScreen().getScreenImage());
							Emulator.getEmulator().getScreen().repaint();
							try {
								Thread.sleep(100L);
							} catch (Exception ex2) {
							}
							this.queue(4);
							break;
						}
						case 15: {
							if (Emulator.getCanvas() == null) {
								break;
							}
							if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
								break;
							}
							Emulator.getCanvas().invokeShowNotify();
							break;
						}
						case 16: {
							if (Emulator.getCanvas() == null) {
								break;
							}
							if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
								break;
							}
							Emulator.getCanvas().invokeHideNotify();
							this.canvasHidden = true;
							break;
						}
						case 0: {
							break;
						}
						default: {
							if ((this.anInt1222 & 0x4000000) != 0x0) {
								if (Emulator.getCurrentDisplay().getCurrent() == null) {
									break;
								}
								final int internalKeyPress = Application
										.internalKeyPress(this.method719(this.anInt1222, this.anInt1222, true));
								if (!Emulator.getCurrentDisplay().getCurrent()
										.handleSoftKeyAction(internalKeyPress, true)) {
									if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
										Emulator.getCanvas().invokeKeyPressed(internalKeyPress);
									} else {
										Emulator.getScreen().invokeKeyPressed(internalKeyPress);
									}
								}
								break;
							} else if ((this.anInt1222 & 0x8000000) != 0x0) {
								if (Emulator.getCurrentDisplay().getCurrent() == null) {
									break;
								}
								final int internalKeyRelease = Application
										.internalKeyRelease(this.method719(this.anInt1222, this.anInt1222, true));
								if (!Emulator.getCurrentDisplay().getCurrent()
										.handleSoftKeyAction(internalKeyRelease, false)) {
									if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
										Emulator.getCanvas().invokeKeyReleased(internalKeyRelease);
									} else {
										Emulator.getScreen().invokeKeyReleased(internalKeyRelease);
									}
								}
								break;
							} else if ((this.anInt1222 & 0x2000000) != 0x0) {
								if (Emulator.getCanvas() == null) {
									break;
								}
								if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
									break;
								}
								Emulator.getCanvas().invokeKeyRepeated(this.method719(this.anInt1222, this.anInt1222, true));
								break;
							} else if ((this.anInt1222 & Integer.MIN_VALUE) != 0x0) {
								if (Emulator.getCanvas() == null) {
									break;
								}
								if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
									break;
								}
								Emulator.getCanvas().invokeSizeChanged(this.method719(this.anInt1222, this.anInt1222, false), this
										.method719(this.anInt1222, this.anInt1222 >> 12, false));
								break;
							} else if ((this.anInt1222 & 0x10000000) != 0x0) {
								if (Emulator.getCurrentDisplay().getCurrent() == null) {
									break;
								}
								final int method719 = this.method719(this.anInt1222, this.anInt1222, false);
								final int method720 = this.method719(this.anInt1222, this.anInt1222 >> 12, false);
								if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
									Emulator.getCanvas().invokePointerPressed(method719, method720);
									break;
								}
								Emulator.getScreen().invokePointerPressed(method719, method720);
								break;
							} else if ((this.anInt1222 & 0x20000000) != 0x0) {
								if (Emulator.getCurrentDisplay().getCurrent() == null) {
									break;
								}
								final int method721 = this.method719(this.anInt1222, this.anInt1222, false);
								final int method722 = this.method719(this.anInt1222, this.anInt1222 >> 12, false);
								if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
									Emulator.getCanvas().invokePointerReleased(method721, method722);
									break;
								}
								Emulator.getScreen().invokePointerReleased(method721, method722);
								break;
							} else {
								if ((this.anInt1222 & 0x40000000) == 0x0) {
									break;
								}
								if (Emulator.getCurrentDisplay().getCurrent() == null) {
									break;
								}
								final int method723 = this.method719(this.anInt1222, this.anInt1222, false);
								final int method724 = this.method719(this.anInt1222, this.anInt1222 >> 12, false);
								if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
									Emulator.getCanvas().invokePointerDragged(method723, method724);
									break;
								}
								Emulator.getScreen().invokePointerDragged(method723, method724);
								break;
							}
						}
						}
						this.anInt1222 = 0;
						Controllers.method751();
						try {
							Thread.sleep(1L);
						} catch (Exception ex3) {
						}
						continue;
					}
				}
				try {
					Thread.sleep(1L);
				} catch (Exception ex4) {
				}
			} catch (Exception e) {
				System.err.println("Exception in Event Thread!");
				e.printStackTrace();
			}
		}
	}

	static boolean method723(final EventQueue j) {
		return j.running;
	}

	private final class ThreadCallSerially implements Runnable {
		private boolean aBoolean1048;
		private Runnable aRunnable1049;
		private final EventQueue aj1050;

		private ThreadCallSerially(final EventQueue aj1050) {
			super();
			this.aj1050 = aj1050;
		}

		public final void method590(final Runnable aRunnable1049) {
			if (aRunnable1049 != null) {
				this.aRunnable1049 = aRunnable1049;
				this.aBoolean1048 = true;
			}
		}

		public final void run() {
			while (EventQueue.method723(this.aj1050)) {
				if (this.aBoolean1048) {
					this.aBoolean1048 = false;

					// new Thread(this.aj1050).start();
					this.aRunnable1049.run();
				}
				try {
					Thread.sleep(1L);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		ThreadCallSerially(final EventQueue j, final InvokeStartAppRunnable c) {
			this(j);
		}
	}
}
