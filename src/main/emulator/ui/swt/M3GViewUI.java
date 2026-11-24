package emulator.ui.swt;

import emulator.Emulator;
import emulator.UILocale;
import emulator.Utils;
import emulator.debug.Memory;
import emulator.graphics3D.Quaternion;
import emulator.graphics3D.Transform3D;
import emulator.graphics3D.Vector4f;
import emulator.graphics3D.lwjgl.GLCanvasUtil;
import emulator.graphics3D.view.M3GView3D;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import javax.microedition.m3g.Group;
import javax.microedition.m3g.*;

public final class M3GViewUI implements MouseMoveListener, DisposeListener, KeyListener, MouseWheelListener {
	private Shell shell;
	private SashForm mainSashForm;
	private Composite treePanel;
	private Menu menu;
	private Composite canvasPanel;
	private Tree objectTree;
	Win32KeyboardPoller poller;
	Canvas canvas;
	private Memory memory;
	private M3GView3D m3gview;
	private Camera camera;
	private Transform cameraTransform;
	private Menu displayMenu;
	private Menu cameraMenu;
	private Menu lightMenu;
	private boolean glInitialized;
	boolean renderPending;
	private enum CameraMode { ORBIT, PAN, DOLLY, ZOOM }
	private int lightingMode;

	private CameraMode cameraMode = CameraMode.ORBIT;
	private int parallelProjEnabled;
	private float nearPlane;
	private float farPlane;
	private float fov;
	private float cameraX;
	private float cameraY;
	private float cameraZ;
	private Quaternion quaternion;
	private Background currentBackground;
	private Node currentNode;
	private Rectangle canvasClientArea;
	private MenuItem updateWorldMenuItem;
    private MenuItem exportGltfItem;
	private MenuItem clipPlanesMenuItem;
	private MenuItem fovMenuItem;
	private MenuItem cameraPositionMenuItem;
	private MenuItem resetCameraMenuItem;
	private MenuItem sceneLightItem;
	private MenuItem viewLightItem;
	private int lastMouseX;
	private int lastMouseY;
	private float rotationX, rotationY;
	protected float moveSpeed = 1F;
	private boolean moveForward, moveBackward, moveRight, moveLeft;
	private boolean shift, control;
	private int rotateX, rotateY;
	private long lastUpdate = 1;

	private MenuItem renderInvisibleItem;

	private MenuItem axisItem;
	private static boolean showAxis;
	private MenuItem gridItem;
	private static boolean showGrid;
	private MenuItem xrayItem;

	public M3GViewUI() {
		super();
		this.shell = null;
		this.mainSashForm = null;
		this.treePanel = null;
		this.menu = null;
		this.canvasPanel = null;
		this.objectTree = null;
		this.canvas = null;
		this.displayMenu = null;
		this.cameraMenu = null;
		this.lightMenu = null;
		this.currentBackground = null;
		this.currentNode = null;
		this.camera = new Camera();
		this.cameraTransform = new Transform();
		this.quaternion = new Quaternion();
		this.memory = Memory.getInstance();
		this.m3gview = M3GView3D.getViewInstance();
	}

	private void setDefaultPreferences() {
		showAxis = false;
		axisItem.setSelection(showAxis);
		showGrid = false;
		gridItem.setSelection(showGrid);

		renderInvisibleItem.setSelection(m3gview.isRenderInvisibleNodes());
		sceneLightItem.setSelection(true);

		cameraMode = CameraMode.ORBIT;

		parallelProjEnabled = 0;

		resetCameraToDefaults();
	}

	private void resetCameraToDefaults() {
		this.nearPlane = 1.0f;
		this.farPlane = 100000.0f;
		this.fov = 70.0f;
		this.setupCamera();
		this.cameraX = 0.0f;
		this.cameraY = 0.0f;
		this.cameraZ = 20.0f;
		this.quaternion.setAngleAxis(-10.0f, 1.0f, 0.0f, 0.0f);
		this.cameraTransform.setIdentity();
		this.cameraTransform.postRotateQuat(this.quaternion.x, this.quaternion.y, this.quaternion.z, this.quaternion.w);
		this.cameraTransform.postTranslate(this.cameraX, this.cameraY, this.cameraZ);
	}


	private void setupCamera() {
		final Rectangle renderArea = canvas.getClientArea();
		if (renderArea.width == 0 || renderArea.height == 0) return;

		if (parallelProjEnabled == 0) {
			if (fov < 0.0f) fov = 0.0f;
			if (fov >= 180.0f) fov = 179.99f;

			camera.setPerspective(fov, (float) renderArea.width / renderArea.height, nearPlane, farPlane);
		} else {
			camera.setParallel(fov, (float) renderArea.width / renderArea.height, nearPlane, farPlane);
		}
	}

	public final void openWindow() {
		this.createShellAndMenus();
		Display display = Display.getCurrent();
		Rectangle clientArea = shell.getMonitor().getClientArea();
		Point size = shell.getSize();
		this.shell.setLocation(clientArea.x + (clientArea.width - size.x) / 2, clientArea.y + (clientArea.height - size.y) / 2);
		this.shell.open();
		this.shell.addDisposeListener(this);
		this.setDefaultPreferences();
		this.addM3GObjects();
		new Thread(new Refresher(this)).start();
		new Thread(new Flusher()).start();
		while (!this.shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public final void close() {
		if (canvas != null && !canvas.isDisposed()) {
			canvas.dispose();
		}
		canvas = null;
		if (this.shell != null && !this.shell.isDisposed()) {
			this.shell.dispose();
		}
	}

	public final boolean isShellOpen() {
		return this.shell != null && !this.shell.isDisposed();
	}

	private void addM3GObjects() {
		this.memory.updateEverything();
		this.objectTree.removeAll();

		for (int i = 0; i < this.memory.m3gObjects.size(); ++i) {
			final Node node = (Node) this.memory.m3gObjects.get(i);
			final String name = node.getClass().getName();
			final TreeItem widget = new TreeItem(this.objectTree, SWT.NONE);

			widget.setText(name.substring(name.lastIndexOf(".") + 1) + "_" + node.getUserID());
			if (!node.isRenderingEnabled()) widget.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

			widget.setData(node);
			if (node instanceof Group) {
				new TreeItem((TreeItem) widget, SWT.NONE);
			}
		}
	}

	private void applyNodeSelection() {
		currentBackground = null;
		currentNode = null;
		if (objectTree.getSelectionCount() > 0) {
			Node selectedNode = (Node) objectTree.getSelection()[0].getData();
			if (selectedNode instanceof Sprite3D || selectedNode instanceof Mesh || selectedNode instanceof Group) {
				currentNode = selectedNode;
			}
		} else if (!memory.m3gObjects.isEmpty()) {
			currentNode = (Node) memory.m3gObjects.get(0);
		}

		if (currentNode != null) {
			if (lightingMode == 0 && currentNode instanceof World) {
				World world = (World) currentNode;
				currentBackground = world.getBackground();
				m3gview.collectWorldLights(world);
			} else {
				Light viewerLight = new Light();
				viewerLight.setMode(Light.AMBIENT);
				M3GView3D.resetLights();
				M3GView3D.addLight(viewerLight, null);
			}
		}
		this.canvasClientArea = this.canvas.getClientArea();
	}

	private void update() {
		float deltaTime = (float) ((System.nanoTime() - lastUpdate) * 60.0 / 10000000000.0);
		lastUpdate = System.nanoTime();

		float tmpSpeed = moveSpeed;
		if (shift) tmpSpeed *= 5;
		if (control) tmpSpeed /= 5;

		float forward = ((moveForward ? 1 : 0) - (moveBackward ? 1 : 0)) * tmpSpeed * deltaTime;
		float strafe = ((moveRight ? 1 : 0) - (moveLeft ? 1 : 0)) * tmpSpeed * deltaTime;

		if (forward != 0 && strafe != 0) {
			forward /= Math.sqrt(2);
			strafe /= Math.sqrt(2);
		}

		if (forward != 0 || strafe != 0) {
			Transform t = new Transform();
			float[] m = new float[16];
			t.postTranslate(cameraX, cameraY, cameraZ);
			t.postRotateQuat(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
			t.get(m);

			if (forward != 0) {
				cameraX += -m[2] * forward;
				cameraY += -m[6] * forward;
				cameraZ += -m[10] * forward;
			}
			if (strafe != 0) {
				cameraX += m[0] * strafe;
				cameraY += m[4] * strafe;
				cameraZ += m[8] * strafe;
			}
		}
		// TODO camera rotation

		try {
			m3gview.setCurrent(this.canvasClientArea.width, this.canvasClientArea.height);
			this.cameraTransform.setIdentity();
			this.cameraTransform.postTranslate(this.cameraX, this.cameraY, this.cameraZ);
			this.cameraTransform.postRotateQuat(this.quaternion.x, this.quaternion.y, this.quaternion.z, this.quaternion.w);

			M3GView3D.setCamera(this.camera, this.cameraTransform);
			m3gview.clearBackground(this.currentBackground);
			if (showGrid) {
				m3gview.drawGrid(1.0F);
			}
			if (this.currentNode != null) {
				try {
					m3gview.render(this.currentNode, null);
				} catch (Exception localException) {
					//                localException.printStackTrace();
				}
			}
			if (showAxis) {
				m3gview.drawAxis();
			}
			m3gview.setViewport(this.canvasClientArea.width, this.canvasClientArea.height);
			m3gview.swapBuffers();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void setXray(boolean b) {
		this.m3gview.setXray(b);
	}

    private void exportSceneAsGltf() {
        if (currentNode == null) {
            MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
            mb.setMessage(UILocale.get("M3G_VIEW_EXPORT_NO_SCENE", "No scene loaded"));
            mb.open();
            return;
        }

        FileDialog dialog = new FileDialog(shell, SWT.SAVE);
        dialog.setFilterExtensions(new String[]{"*.glb"});
        dialog.setFilterNames(new String[]{"glTF Binary (*.glb)"});
        dialog.setFileName("scene.glb");
        String path = dialog.open();
        if (path == null) return;

        if (!path.toLowerCase().endsWith(".glb")) {
            path = path + ".glb";
        }

        try {
            GltfExporter.export(currentNode, new java.io.File(path));

            MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
            mb.setMessage(UILocale.get("M3G_VIEW_EXPORT_DONE", "Export finished") + ":\n" + path);
            mb.open();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            mb.setMessage(UILocale.get("M3G_VIEW_EXPORT_ERROR", "Export failed") + ":\n" + ex.getMessage());
            mb.open();
        }
    }

	//TODO: Use anonymous classes
	private void createShellAndMenus() {
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 1;
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		(this.shell = new Shell()).setText(UILocale.get("M3G_VIEW_TITLE", "M3G View"));
		this.shell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.createMainLayout();
		this.shell.setLayout(layout);
		this.shell.setSize(new Point(600, 400));
		this.menu = new Menu(this.shell, SWT.BAR);
		final MenuItem menuItem = new MenuItem(this.menu, SWT.CASCADE);
		final MenuItem menuItem2;
		(menuItem2 = new MenuItem(this.menu, SWT.CASCADE)).setText(UILocale.get("M3G_VIEW_CAMERA", "Camera"));
		final MenuItem menuItem3;
		(menuItem3 = new MenuItem(this.menu, SWT.CASCADE)).setText(UILocale.get("M3G_VIEW_LIGHT", "Light"));
		this.lightMenu = new Menu(menuItem3);
		(this.sceneLightItem = new MenuItem(this.lightMenu, SWT.RADIO)).setText(UILocale.get("M3G_VIEW_LIGHT_SCENE", "Scene Graphics"));
		sceneLightItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				lightingMode = 0;
			}
		});
		(this.viewLightItem = new MenuItem(this.lightMenu, SWT.RADIO)).setText(UILocale.get("M3G_VIEW_LIGHT_VIEW", "Viewer Light"));
		viewLightItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				lightingMode = 1;
			}
		});
		menuItem3.setMenu(this.lightMenu);
		this.cameraMenu = new Menu(menuItem2);


		(this.clipPlanesMenuItem = new MenuItem(this.cameraMenu, SWT.PUSH)).setText(UILocale.get("M3G_VIEW_CAMERA_CLIP_PLANES", "Clipping Planes") + "\tC");
		this.clipPlanesMenuItem.setAccelerator('C');
		this.clipPlanesMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				M3GViewCameraSetDialog dialog = new M3GViewCameraSetDialog(shell, M3GViewCameraSetDialog.Mode.CLIPPING_PLANES);
				dialog.setText(UILocale.get("M3G_VIEW_CAMERA_CLIP_PLANES", "Clipping Planes"));
				dialog.setClippingPlanes(nearPlane, farPlane);
				dialog.open();
				if (parallelProjEnabled == 0) {
					if (dialog.nearPlane > 0.0f) {
						nearPlane = dialog.nearPlane;
					}
					if (dialog.farPlane > 0.0f) {
						farPlane = dialog.farPlane;
					}
				} else {
					nearPlane = dialog.nearPlane;
					farPlane = dialog.farPlane;
				}
				setupCamera();
			}
		});
		(this.fovMenuItem = new MenuItem(this.cameraMenu, SWT.PUSH)).setText(UILocale.get("M3G_VIEW_CAMEAR_FIELD_OF_VIEW", "Field of View") + "\tF");
		this.fovMenuItem.setAccelerator('F');
		this.fovMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				M3GViewCameraSetDialog dialog = new M3GViewCameraSetDialog(shell, M3GViewCameraSetDialog.Mode.FIELD_OF_VIEW);
				dialog.setText(UILocale.get("M3G_VIEW_CAMEAR_FIELD_OF_VIEW", "Field of View"));
				dialog.setFieldOfView(fov);
				dialog.open();
				if (dialog.fieldOfView > 0.0f) {
					if (parallelProjEnabled == 0) {
						if (dialog.fieldOfView < 180.0f) {
							fov = dialog.fieldOfView;
						}
					} else {
						fov = dialog.fieldOfView;
					}
					setupCamera();
				}
			}
		});
		(this.cameraPositionMenuItem = new MenuItem(this.cameraMenu, SWT.PUSH)).setText(UILocale.get("M3G_VIEW_CAMEAR_POSITION", "Camera Position") + "\tP");
		this.cameraPositionMenuItem.setAccelerator('P');
		this.cameraPositionMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				M3GViewCameraSetDialog dialog = new M3GViewCameraSetDialog(shell, M3GViewCameraSetDialog.Mode.POSITION);
				dialog.setText(UILocale.get("M3G_VIEW_CAMEAR_POSITION", "Camera Position"));
				dialog.setCameraPosition(cameraX, cameraY, cameraZ);
				dialog.open();
				cameraX = dialog.cameraX;
				cameraY = dialog.cameraY;
				cameraZ = dialog.cameraZ;
			}
		});
		new MenuItem(this.cameraMenu, SWT.SEPARATOR);
		(this.resetCameraMenuItem = new MenuItem(this.cameraMenu, SWT.PUSH)).setText(UILocale.get("M3G_VIEW_CAMEAR_RESET", "Reset Camera") + "\tR");
		this.resetCameraMenuItem.setAccelerator('R');
		this.resetCameraMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				resetCameraToDefaults();
			}
		});
		menuItem2.setMenu(this.cameraMenu);
		this.displayMenu = new Menu(menuItem);
		(this.axisItem = new MenuItem(this.displayMenu, SWT.CHECK)).setText(UILocale.get("M3G_VIEW_DISPLAY_COORDINATE", "Coordinate Axis"));
		this.axisItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				showAxis = axisItem.getSelection();
			}
		});
		(this.gridItem = new MenuItem(this.displayMenu, SWT.CHECK)).setText(UILocale.get("M3G_VIEW_DISPLAY_SHOW_GRID", "Show Grid"));
		this.gridItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				showGrid = gridItem.getSelection();
			}
		});
		(this.xrayItem = new MenuItem(this.displayMenu, SWT.CHECK)).setText(UILocale.get("M3G_VIEW_DISPLAY_SHOW_XRAY", "Show Xray") + "\tX");
		this.xrayItem.setAccelerator('X');
		this.xrayItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				setXray(xrayItem.getSelection());
			}
		});

		renderInvisibleItem = new MenuItem(this.displayMenu, SWT.CHECK);
		renderInvisibleItem.setText(UILocale.get("M3G_VIEW_DISPLAY_RENDER_INVISIBLE", "Render invisible nodes") + "\tV");
		renderInvisibleItem.setAccelerator('V');
		renderInvisibleItem.addSelectionListener(new SelectionAdapter() {
			public final void widgetSelected(final SelectionEvent selectionEvent) {
				m3gview.setRenderInvisibleNodes(((MenuItem) selectionEvent.widget).getSelection());
			}
		});

		new MenuItem(this.displayMenu, SWT.SEPARATOR);
		(this.updateWorldMenuItem = new MenuItem(this.displayMenu, SWT.PUSH)).setText(UILocale.get("M3G_VIEW_DISPLAY_UPDATE_WORLD", "Update World") + "\tF5");
		this.updateWorldMenuItem.setAccelerator(SWT.F5);
		this.updateWorldMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				addM3GObjects();
			}
		});
        this.updateWorldMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                addM3GObjects();
            }
        });

        new MenuItem(this.displayMenu, SWT.SEPARATOR); // separator
        (this.exportGltfItem = new MenuItem(this.displayMenu, SWT.PUSH)).setText(UILocale.get("M3G_VIEW_DISPLAY_EXPORT_GLTF", "Export Scene as glTF...") + "\tE");
        this.exportGltfItem.setAccelerator('E');
        this.exportGltfItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                exportSceneAsGltf();
            }
        });
		menuItem.setMenu(this.displayMenu);
		menuItem.setText(UILocale.get("M3G_VIEW_DISPLAY", "Display"));
		this.shell.setMenuBar(this.menu);
		this.shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent shellEvent) {
				shellEvent.doit = false;
				close();
			}
		});
	}

	private void createMainLayout() {
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		(this.mainSashForm = new SashForm(this.shell, SWT.NONE)).setLayoutData(layoutData);
		this.createTreePanel();
		this.createCanvasPanel();
		this.mainSashForm.setWeights(new int[]{3, 7});
	}

	private void createTreePanel() {
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		(this.treePanel = new Composite(this.mainSashForm, SWT.NONE)).setLayout(layout);
		(this.objectTree = new Tree(this.treePanel, SWT.BORDER)).setHeaderVisible(false);
		this.objectTree.setLayoutData(layoutData);
		this.objectTree.setLinesVisible(false);
		this.objectTree.addListener(SWT.Expand, new Listener() {
			public void handleEvent(Event event) {
				final TreeItem groupWidget = (TreeItem) event.item;
				final TreeItem[] items = groupWidget.getItems();

				if (items.length != 1) return;
				if (items[0].getData() != null) return;

				items[0].dispose();

				if (groupWidget.getData() instanceof Group) {
					final Group group = (Group) groupWidget.getData();

					for (int i = 0; i < group.getChildCount(); ++i) {
						final Node child = group.getChild(i);
						final String name = child.getClass().getName();
						final TreeItem childWidget = new TreeItem(groupWidget, SWT.NONE);

						childWidget.setText(name.substring(name.lastIndexOf(".") + 1) + "_" + child.getUserID());
						if (!child.isRenderingEnabled())
							childWidget.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

						childWidget.setData(child);
						if (child instanceof Group) {
							new TreeItem((TreeItem) childWidget, SWT.NONE);
						}
					}
				}
			}
		});
		this.objectTree.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent mouseEvent) {
				if (mouseEvent.button == 3) {
					try {
						final Node node;
						if (objectTree.getSelection() != null && (node = (Node) objectTree.getSelection()[0].getData()) != null) {
							new Watcher(node).open(shell);
						}
					} catch (Exception ignored) {}
				}
			}
		});
	}

	private void createCanvasPanel() {
		final GridLayout layout;
		(layout = new GridLayout()).marginWidth = 0;
		layout.marginHeight = 0;
		this.canvasPanel = new Composite(this.mainSashForm, SWT.NONE);
		this.initCanvas();
		this.canvasPanel.setLayout(layout);
	}

	private void initCanvas() {
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		try {
			canvas = GLCanvasUtil.initGLCanvas(canvasPanel, SWT.BORDER | SWT.NO_BACKGROUND, 1);
		} catch (Throwable e) {
			e.printStackTrace();
			if (canvas != null) canvas.dispose();
			canvas = new Canvas(this.canvasPanel, SWT.BORDER | SWT.NO_BACKGROUND);
		}
		if (Utils.win) {
			poller = new Win32KeyboardPoller((EmulatorScreen) Emulator.getEmulator().getScreen());
		}

		canvas.setLayoutData(layoutData);
		this.canvas.addMouseMoveListener(this);
		this.canvas.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					lastMouseX = e.x;
					lastMouseY = e.y;
				}
				canvas.forceFocus();
			}
		});
		canvas.addKeyListener(this);
		this.canvas.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				if (shell.getVisible()) {
					setupCamera();
				}
			}
		});
		canvas.addListener(SWT.Dispose, event -> {
			glInitialized = false;
			M3GView3D.releaseContext();
		});
		canvas.addMouseWheelListener(this);
	}

	public final void mouseMove(final MouseEvent mouseEvent) {
		if (!this.glInitialized) {
			return;
		}
		if ((mouseEvent.stateMask & SWT.BUTTON1) != 0) {
			this.handleMouseDrag(this.lastMouseX - mouseEvent.x, this.lastMouseY - mouseEvent.y);
			this.lastMouseX = mouseEvent.x;
			this.lastMouseY = mouseEvent.y;
		}
	}

	public void keyPressed(KeyEvent keyEvent) {
		int key = keyEvent.keyCode;
		if (keyEvent.keyCode >= SWT.ARROW_UP) {
			float x = 0;
			float y = 0;
			switch (key) {
				case SWT.ARROW_UP:
					y = 1;
					break;
				case SWT.ARROW_DOWN:
					y = -1;
					break;
				case SWT.ARROW_LEFT:
					x = 1;
					break;
				case SWT.ARROW_RIGHT:
					x = -1;
					break;
			}

			rotationX += x * 5F;
			rotationY += y * 5F;

			quaternion.setAngleAxis(0, 0, 0, 0);
			Quaternion rotation = new Quaternion();
			rotation.setAngleAxis(rotationX, 0, 1, 0);
			rotation.mul(quaternion);
			quaternion.set(rotation);

			rotation = new Quaternion();
			rotation.setAngleAxis(rotationY, 1, 0, 0);
			quaternion.mul(rotation);
			return;
		}
		switch (key) {
			case 'w':
				moveForward = true;
				break;
			case 'a':
				moveLeft = true;
				break;
			case 's':
				moveBackward = true;
				break;
			case 'd':
				moveRight = true;
				break;
			case SWT.SHIFT:
				shift = true;
				break;
			case SWT.CONTROL:
				control = true;
				break;
			default:
				break;
		}
	}

	public void keyReleased(KeyEvent keyEvent) {
		keyReleased(keyEvent.keyCode);
	}

	public void keyReleased(int key) {
		switch (key) {
			case SWT.ARROW_UP:
			case SWT.ARROW_DOWN:
				rotateY = 0;
				break;
			case SWT.ARROW_LEFT:
			case SWT.ARROW_RIGHT:
				rotateX = 0;
				break;
			case 'w':
				moveForward = false;
				break;
			case 'a':
				moveLeft = false;
				break;
			case 's':
				moveBackward = false;
				break;
			case 'd':
				moveRight = false;
				break;
			case SWT.SHIFT:
				shift = false;
				break;
			case SWT.CONTROL:
				control = false;
				break;
			default:
				break;
		}
	}

	private void handleMouseDrag(final int x, final int y) {
		switch (this.cameraMode) {
			case ORBIT: {
				rotationX += x / 2F;
				rotationY += y / 2F;

				quaternion.setAngleAxis(0, 0, 0, 0);
				Quaternion rotation = new Quaternion();
				rotation.setAngleAxis(rotationX, 0, 1, 0);
				rotation.mul(quaternion);
				quaternion.set(rotation);

				rotation = new Quaternion();
				rotation.setAngleAxis(rotationY, 1, 0, 0);
				quaternion.mul(rotation);
				break;
			}
			case PAN: {
				this.cameraX += (float) x / 10.0f;
				this.cameraY -= (float) y / 10.0f;
				// Fall through to the dolly adjustment.
			}
			case DOLLY: {
				if (this.parallelProjEnabled == 0) {
					this.cameraZ -= (float) x / 10.0f;
					return;
				}
				break;
			}
			case ZOOM: {
				this.fov -= (float) x / 10.0f;
				if (!(fov > 0.0f && (fov < 180.0f || parallelProjEnabled != 0))) {
					fov += x / 10.0f;
				}
				this.setupCamera();
				break;
			}
		}
	}

	private Vector4f computeCameraRightVector(final Quaternion q) { //not used actually
		final Vector4f a = new Vector4f(this.cameraX, this.cameraY, this.cameraZ, 1.0f);
		final Vector4f b = new Vector4f();
		final Transform transform;
		(transform = new Transform()).postRotateQuat(q.x, q.y, q.z, q.w);
		transform.postTranslate(this.cameraX, this.cameraY, this.cameraZ);
		((Transform3D) transform.getImpl()).transform(a);
		b.cross(a, Vector4f.Y_AXIS);
		return b;
	}

	public final void widgetDisposed(final DisposeEvent disposeEvent) {
		this.close();
	}

	public void mouseScrolled(MouseEvent mouseEvent) {
		if (mouseEvent.count == 0) return;
		moveSpeed *= Math.pow(1.15F, mouseEvent.count > 0 ? 1 : -1);
		moveSpeed = Math.max(0.01F, Math.min(1000F, moveSpeed)); // limit

	}

	private final class Flusher implements Runnable {
		public final void run() {
			glInitialized = m3gview.init(canvas);
			while (canvas != null) {
				if (canvas.isDisposed()) {
					return;
				}
				if (glInitialized && renderPending) {
					try {
						update();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					renderPending = false;
				}
				try {
					Thread.sleep(10L);
				} catch (Exception ignored) {
				}
			}
		}
	}

	final static class Refresher implements Runnable {
		final M3GViewUI ui;
		private Runnable r = new Runnable() {
			public final void run() {
				try {
					if (ui.poller != null && ui.canvas != null) {
						ui.poller.pollKeyboard(ui.canvas);
					}
					if (ui.glInitialized) {
						if (!ui.renderPending) {
							ui.applyNodeSelection();
							ui.renderPending = true;
							return;
						}
					} else {
						final Rectangle clientArea = ui.canvas.getClientArea();
						final GC gc;
						(gc = new GC(ui.canvas)).setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
						gc.fillRectangle(clientArea);
						gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
						gc.drawString("M3GView init .....", clientArea.width >> 2, clientArea.height >> 2, true);
						gc.dispose();
					}

				} catch (Exception ignored) {}

			}
		};

		private Refresher(final M3GViewUI ui) {
			super();
			this.ui = ui;
		}

		public final void run() {
			while (ui.canvas != null) {
				if (ui.canvas.isDisposed()) {
					return;
				}
				ui.canvas.getDisplay().syncExec(r);
				try {
					Thread.sleep(10L);
				} catch (Exception ignored) {
				}
			}
		}

	}
}
