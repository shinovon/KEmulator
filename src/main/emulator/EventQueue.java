package emulator;

import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.lcdui.*;
import net.rim.device.api.system.*;
import emulator.graphics2D.*;

public final class EventQueue implements Runnable {
	private int[] events;
	//private ArrayList<Integer> mouseEvents;
	private int readed;
	private int ind;
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
	private InputThread input = new InputThread();
	private Thread inputThread;

	public EventQueue() {
		super();
		this.events = new int[128];
		//this.events = new ArrayList<Integer>(50);
		//this.mouseEvents = new ArrayList<Integer>(50);
		this.ind = 0;
		this.readed = 0;
		this.anInt1222 = 0;
		this.canvasHidden = false;
		this.running = true;
		this.repainted2 = true;
		this.repainted = true;
		(this.eventThread = new Thread(this)).setPriority(2);
		this.eventThread.start();
		(this.inputThread = new Thread(input)).setPriority(3);
		this.inputThread.start();
		/*
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
		*/
		//mouseThread.start();
		
	}

	public final void stop() {
		this.running = false;
	}
	
	public final void keyRepeat(int n) {
		input.queue(2, n, 0, true);
		//method717(33554432, n);
	}
	public final void keyPress(int n) {
		input.queue(0, n, 0, true);
		//method717(67108864, n);
	}
	public final void keyRelease(int n) {
		input.queue(1, n, 0, true);
		//method717(134217728, n);
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
		input.queue(0, x, y, false);
		//mouse(268435456, x, y);
	}
	
	public final void mouseUp(final int x, final int y) {
		input.queue(1, x, y, false);
		//mouse(536870912, x, y);
	}
	
	public final void mouseDrag(final int x, final int y) {
		input.queue(2, x, y, false);
		//dx = x;
		//dy = y;
		//changed = true;
		//resetTimer();
		//mouse(1073741824, x, y);
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
		this.queue(n4);
		//mouseEvents.add(n4);
		//Emulator.getNetMonitor().b(n4);
	}
	/*
	private synchronized int nextMouseEvent() {
		//synchronized (events) {
		if(mouseEvents.size() == 0) return 0;
		final int n = mouseEvents.get(0);
		mouseEvents.remove(0);
		return n;
		//}
	}
*/
	/**
	 * queue event
	 */
	public synchronized final void queue(final int n) {
		//synchronized (events) {
		if (n == 1) {
			this.repainted2 = false;
		}
		if (n == 3) {
			this.repainted = false;
		}
		if (n == 15) {
			this.canvasHidden = false;
		}
		//events.add(n);
		this.events[this.ind++] = n;
		if (this.ind >= events.length) {
			this.ind = 0;
		}
		//}
	}

	private synchronized int nextEvent() {
		if (this.readed == this.ind) {
			return 0;
		}
		int n = this.events[this.readed];
		this.events[this.readed++] = 0;
		if (this.readed >= events.length) {
			this.readed = 0;
		}
		return n;
		/*
		//synchronized (events) {
		if(events.size() == 0) return 0;
		final int n = events.get(0);
		events.remove(0);
		return n;
		//}
		 */
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
									if(Emulator.getScreen() != null) Emulator.getScreen().invokeSizeChanged(this.method719(this.anInt1222, this.anInt1222, false), this
											.method719(this.anInt1222, this.anInt1222 >> 12, false));
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
			} catch (Throwable e) {
				System.err.println("Exception in Event Thread!");
				e.printStackTrace();
			}
		}
	}

	static boolean method723(final EventQueue j) {
		return j.running;
	}
	
	private class InputThread implements Runnable {
		private int readIdx;
		private int writeIdx;
		private Object[] inputTasks;
		
		private Object inputLock = new Object();
		//private Object inputWriteLock = new Object();
		
		private InputThread() {
			inputTasks = new Object[128];
		}
		
		public void queue(int state, int arg1, int arg2, boolean key) {
			append(new Object[] { state, arg1, arg2, key });
		}
		
		private void append(Object o) {
			inputTasks[writeIdx++] = o;
			if(writeIdx >= inputTasks.length) writeIdx = 0;
			synchronized(inputLock) {
				inputLock.notify();
			}
		}

		public void run() {
			try {
				while(running) {
					synchronized(inputLock) {
						inputLock.wait();
					}
					Object[] o;
					while(readIdx != writeIdx && (o = (Object[]) inputTasks[readIdx++]) != null) {
						if(readIdx >= inputTasks.length) readIdx = 0;

						Displayable d = Emulator.getCurrentDisplay().getCurrent();
						boolean canv = d == Emulator.getCanvas();
						if((boolean)o[3]) {
							// keyboard
							int n = (int) o[1];
							switch((int) o[0]) {
							case 0: {
								int k = Application.internalKeyPress(n);
								if(!d.handleSoftKeyAction(k, true)) {
									if (canv) ((Canvas)d).invokeKeyPressed(k);
									else ((Screen)d).invokeKeyPressed(k);
								}
								break;
							}
							case 1: {
								int k = Application.internalKeyRelease(n);
								if(!d.handleSoftKeyAction(k, false)) {
									if (canv) ((Canvas)d).invokeKeyReleased(k);
									else ((Screen)d).invokeKeyReleased(k);
								}
								break;
							}
							case 2:
								if(!canv) return;
								((Canvas)d).invokeKeyRepeated(n);
								break;
							}
						} else {
							// mouse
							int x = (int) o[1];
							int y = (int) o[2];
							switch((int) o[0]) {
							case 0:
								if(canv) ((Canvas)d).invokePointerPressed(x, y);
								else ((Screen)d).invokePointerPressed(x, y);
								break;
							case 1:
								if(canv) ((Canvas)d).invokePointerReleased(x, y);
								else ((Screen)d).invokePointerReleased(x, y);
								break;
							case 2:
								if(canv) ((Canvas)d).invokePointerDragged(x, y);
								else ((Screen)d).invokePointerDragged(x, y);
								break;
							}
						}
					}
				}
			} catch (Throwable e) {
				System.out.println("Exception in Input Thread!");
				e.printStackTrace();
			}
		}
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
