package emulator.ui.swt;

import emulator.Emulator;
import emulator.Settings;
import emulator.UILocale;
import emulator.debug.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import javax.microedition.lcdui.Image;
import java.util.*;

public final class MemoryView implements DisposeListener, ControlListener {
	private Shell shell;
	public final Memory memoryMgr = Memory.getInstance();
	private boolean visible;
	private Composite imagesPanel;
	private ImageViewControls imageControls;
	private MemoryViewControls memoryControls;
	private Canvas imagesCanvas;
	private SashForm horizontalSeparator;
	private Composite memoryPanel;
	private Table classTable;
	private Table objectsTable;
	private double imageScaling = 1d;
	private int imagesSortingMethod;
	private boolean sortImagesByAscending;
	private static final Object updateLock = new Object();
	static final ArrayList<Image> allImages = new ArrayList<>();
	static final ArrayList<ImageViewItem> imagesToShow = new ArrayList<>();
	int imagesCanvasScroll;
	private boolean imagesDrawn = true;
	private boolean imagesNeverDrawn = true;
	private boolean showReleasedImages = false;
	private boolean show2dImages = true;
	private boolean show3dImages = true;
	private boolean darkenUnused = false;
	private boolean displayPkgNames = true;

	private boolean drawImagesInfo = false;
	private Object selectedObject;
	private ArrayList<String> classesList = new ArrayList<>();
	private AutoUpdate autoUpdater;
	private int sortColumn = -1;

	public static final String SHELL_TYPE = "MEMORY_VIEW";
	private Group objectPaths;
	private Composite objectPathsContainer;

	public void open() {
		createShell();
		Rectangle clientArea = ((EmulatorScreen) Emulator.getEmulator().getScreen()).getShell().getMonitor().getClientArea();
		shell.setLocation(
				clientArea.x + (int) (clientArea.height * 0.025), //- this.shell.getSize().x >> 3,
				clientArea.y + (int) (clientArea.height * 0.025)// - this.shell.getSize().y >> 2
		);
		shell.open();
		shell.addDisposeListener(this);
		updateEverything();
		visible = true;
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		Settings.showMemViewFrame = false;
		if (autoUpdater != null)
			autoUpdater.stopThread();
		this.memoryMgr.releasedImages.clear();
		this.visible = false;
	}

	public void dispose() {
		if (this.shell != null && !this.shell.isDisposed()) {
			this.shell.dispose();
		}
		this.visible = false;
	}

	public boolean isShown() {
		return this.visible;
	}

	private void createShell() {
		final int shellStyle = SWT.MAX | SWT.FOREGROUND | SWT.TITLE | SWT.MENU | SWT.MIN;
		shell = new Shell(shellStyle);
		shell.setText(UILocale.get("MEMORY_VIEW_TITLE", "MemoryView"));
		this.shell.setImage(new org.eclipse.swt.graphics.Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.shell.setLayout(new GridLayout(1, true));
		shell.setData("TYPE", SHELL_TYPE);
		shell.setMinimumSize(320, 300);

		Rectangle clientArea = ((EmulatorScreen) Emulator.getEmulator().getScreen()).getShell().getMonitor().getClientArea();
		this.shell.setSize(
				(int) (clientArea.width * 0.4),
				(int) (clientArea.height * 0.95)
		);

		final GridData controlsLayout = new GridData();
		controlsLayout.horizontalAlignment = 4;
		controlsLayout.grabExcessHorizontalSpace = true;
		controlsLayout.verticalAlignment = 4;
		memoryControls = new MemoryViewControls(shell, this);
		memoryControls.setLayoutData(controlsLayout);

		this.createSeparator();
	}

	private void createSeparator() {
		final GridData layoutData = new GridData();
		layoutData.horizontalAlignment = 4;
		layoutData.verticalAlignment = 4;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.grabExcessHorizontalSpace = true;

		horizontalSeparator = new SashForm(this.shell, 0);
		horizontalSeparator.setOrientation(SWT.VERTICAL);
		this.horizontalSeparator.setSashWidth(5);
		this.horizontalSeparator.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		this.horizontalSeparator.setLayoutData(layoutData);

		createMemoryView();
		createImagesView();

		horizontalSeparator.setWeights(new int[]{4, 6});
	}

	private void createMemoryView() {
		SashForm memoryPanel = new SashForm(this.horizontalSeparator, 0);

		classTable = new Table(memoryPanel, 67584);
		classTable.setHeaderVisible(true);
		classTable.setLinesVisible(true);
		TableListener tl = new TableListener(this);
		classTable.addSelectionListener(tl);
		classTable.addMouseListener(tl);
		final TableColumn tableColumn;
		(tableColumn = new TableColumn(this.classTable, 0)).setWidth(170);
		tableColumn.setText(UILocale.get("MEMORY_VIEW_CLASS", "Class"));
		tableColumn.addSelectionListener(new Class31(this));
		final TableColumn tableColumn2;
		(tableColumn2 = new TableColumn(this.classTable, 0)).setWidth(70);
		tableColumn2.setText(UILocale.get("MEMORY_VIEW_INSTANCES", "Instances"));
		tableColumn2.addSelectionListener(new Class140(this));
		final TableColumn tableColumn3;
		(tableColumn3 = new TableColumn(this.classTable, 0)).setWidth(100);
		tableColumn3.setText(UILocale.get("MEMORY_VIEW_TOTAL_HEAP_SIZE", "Total Heap Size"));
		tableColumn3.addSelectionListener(new Class17(this));

		SashForm objectsPanel = new SashForm(memoryPanel, 0);
		objectsPanel.setOrientation(SWT.VERTICAL);

		objectsTable = new Table(objectsPanel, 67584);
		objectsTable.setHeaderVisible(true);
		this.objectsTable.setLinesVisible(true);
		ObjectsTableListener otl = new ObjectsTableListener(this);
		this.objectsTable.addSelectionListener(otl);
		this.objectsTable.addMouseListener(otl);
		final TableColumn tableColumn4;
		(tableColumn4 = new TableColumn(this.objectsTable, 0)).setWidth(230);
		tableColumn4.setText(UILocale.get("MEMORY_VIEW_REFERENCE", "Reference"));
		final TableColumn tableColumn5;
		(tableColumn5 = new TableColumn(this.objectsTable, 0)).setWidth(70);
		tableColumn5.setText(UILocale.get("MEMORY_VIEW_INSTANCE", "Instance"));
		final TableColumn tableColumn6;
		(tableColumn6 = new TableColumn(this.objectsTable, 0)).setWidth(100);
		tableColumn6.setText(UILocale.get("MEMORY_VIEW_SIZE", "Size"));

		// borders
		objectPaths = new Group(objectsPanel, SWT.NONE);
		objectPaths.setLayout(new GridLayout(1, false));
		objectPaths.addControlListener(this);
		// inner scroll
		ScrolledComposite sc = new ScrolledComposite(objectPaths, SWT.V_SCROLL);
		final GridData gd = new GridData();
		gd.horizontalAlignment = 4;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = 4;
		sc.setLayoutData(gd);
		// scrolled content
		objectPathsContainer = new Composite(sc, 0);
		objectPathsContainer.setLayout(new GridLayout(1, false));
		sc.setContent(objectPathsContainer);
		clearObjectPaths();

		objectsPanel.setWeights(new int[]{8, 2});
	}

	private void createImagesView() {
		final GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 2;
		layout.marginWidth = 0;

		this.imagesPanel = new Composite(this.horizontalSeparator, 0);
		imagesPanel.setLayout(layout);

		imageControls = new ImageViewControls(imagesPanel, this);
		final GridData controlsLayout = new GridData();
		controlsLayout.horizontalAlignment = 4;
		controlsLayout.grabExcessHorizontalSpace = true;
		controlsLayout.verticalAlignment = 4;
		imageControls.setLayoutData(controlsLayout);

		final GridData canvasLayout = new GridData();
		canvasLayout.horizontalAlignment = 4;
		canvasLayout.grabExcessHorizontalSpace = true;
		canvasLayout.grabExcessVerticalSpace = true;
		canvasLayout.verticalAlignment = 4;

		imagesCanvas = new Canvas(this.imagesPanel, 537135616);
		imagesCanvas.setLayout(null);
		this.imagesCanvas.setLayoutData(canvasLayout);
		ImagesCanvasListener listener = new ImagesCanvasListener(this, imagesCanvas);
		this.imagesCanvas.addPaintListener(listener);
		this.imagesCanvas.addMouseListener(listener);
		this.imagesCanvas.getVerticalBar().addSelectionListener(listener);

		Menu menuSave = new Menu(this.shell, 8);
		final MenuItem menuItem = new MenuItem(menuSave, 8);
		menuItem.setText(UILocale.get("MEMORY_VIEW_SAVE_ONE", "Export selected image"));
		menuItem.addSelectionListener(new SaveImageListener(this));
		final MenuItem menuItem2 = new MenuItem(menuSave, 8);
		menuItem2.setText(UILocale.get("MEMORY_VIEW_SAVE_ALL", "Export all images"));
		menuItem2.addSelectionListener(new SaveAllImagesListener(this));

		imagesCanvas.setMenu(menuSave);
	}

	private void updateModel() {
		synchronized (MemoryView.updateLock) {
			try {
				this.memoryMgr.updateEverything();
				this.updateImagesList();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}

	public void updateView() {
		if (this.shell.isDisposed())
			return;
		try {
			this.imagesCanvas.redraw();
			this.updateClassesView();
		} catch (Exception e) {
			e.printStackTrace();
		}
		memoryControls.refreshStats();
		resortClasses();
	}

	public void updateEverything() {
		this.updateModel();
		this.updateView();
	}

	public void resortImages() {
		synchronized (MemoryView.updateLock) {
			Comparator<ImageViewItem> comp;
			switch (getSortingMethod()) {
				case 0:
					comp = new ImagesComparatorByOrder(this);
					break;
				case 1:
					comp = new ImagesComparatorBySize(this);
					break;
				case 2:
					comp = new ImagesComparatorByUsage(this);
					break;
				default:
					throw new IllegalArgumentException("Unsupported sort method");
			}
			MemoryView.imagesToShow.sort(comp);
		}
	}

	private void updateImagesList() {
		synchronized (MemoryView.updateLock) {
			MemoryView.allImages.clear();
			allImages.ensureCapacity(memoryMgr.images.size());
			MemoryView.imagesToShow.clear();
			synchronized (memoryMgr) {
				for (Image image : memoryMgr.images) {
					allImages.add(image);
					boolean add = (imagesDrawn && image.getUsedCount() > 0) || (imagesNeverDrawn && image.getUsedCount() == 0);
					if (!add)
						continue;
					ImageViewItem i = new ImageViewItem(image, false);
					boolean add2 = (show2dImages && i.type == MemoryViewImageType.LCDUI) || (show3dImages && i.type != MemoryViewImageType.LCDUI);
					if (add2)
						MemoryView.imagesToShow.add(i);

				}
			}
			if (showReleasedImages) {
				for (Image image : this.memoryMgr.releasedImages) {
					allImages.add(image);
					MemoryView.imagesToShow.add(new ImageViewItem(image, true));
				}
			}
		}
		this.resortImages();
	}

	public void selectImageClicked(final int x, final int y) {
		synchronized (MemoryView.updateLock) {
			for (ImageViewItem image : imagesToShow) {
				if (image.drawnRect == null)
					continue;
				if (image.drawnRect.contains(x, y)) {
					selectedObject = image.source;
					TableItem[] array = classTable.getSelection();
					if (array == null || array.length < 1 || !selectedObject.getClass().getName().equals(array[0].getData())) {
						// reselecting proper class
						for (TableItem ti : classTable.getItems()) {
							if (selectedObject.getClass().getName().equals(ti.getData())) {
								classTable.setSelection(ti);
								break;
							}
						}
						onClassTableItemSelection();
					}
					syncObjectSelection();
					imagesCanvas.redraw();
					return;
				}
			}
		}
	}

	/**
	 * Sets selection in table to {@link #selectedObject}.
	 */
	private void syncObjectSelection() {
		for (int i = 0; i < objectsTable.getItemCount(); i++) {
			ObjInstance o = (ObjInstance) objectsTable.getItem(i).getData();
			if (o.value == selectedObject) {
				objectsTable.select(i);
				onObjectTableItemSelection();
				return;
			}
		}
	}

	public void paintImagesCanvas(final GC gc) {
		final int fh = gc.getFontMetrics().getHeight();
		int canvasW = imagesCanvas.getClientArea().width;
		int canvasH = imagesCanvas.getClientArea().height;
		int y = 10 - this.imagesCanvasScroll;
		if (drawImagesInfo)
			y += fh * 4;
		int x = 10;
		int max = 0;
		final Color background = new Color(null, 151, 150, 147);
		final Color regularColor = new Color(null, 0, 0, 0);

		final Color releasedColor = new Color(null, 200, 0, 0);
		final Color selectedColor = new Color(null, 0, 255, 0);
		final Color texColor = new Color(null, 0, 0, 200);

		gc.setBackground(background);
		gc.fillRectangle(0, 0, canvasW, canvasH);
		gc.setInterpolation(SWT.NONE);
		int pixels2d = 0;
		int pixels3d = 0;
		int count2d = 0;
		int count3d = 0;
		synchronized (MemoryView.updateLock) {
			final int size = MemoryView.imagesToShow.size();
			for (int i = 0; i < size; ++i) {
				ImageViewItem item = MemoryView.imagesToShow.get(i);
				Image image = item.drawable;
				final int imgW = (int) (image.getWidth() * this.imageScaling);
				final int imgH = (int) (image.getHeight() * this.imageScaling);
				if (x + imgW + 30 > canvasW) {
					x = 10;
					y += max + 10;
					if (drawImagesInfo)
						y += fh * 4;
					max = 0;
				}
				if (y + imgH > 0 || y > canvasH) {
					try {
						image.getImpl().copyToScreen(gc, 0, 0, image.getWidth(), image.getHeight(), x, y, imgW, imgH);
						if (this.darkenUnused) {
							image.getUsedRegion().copyToScreen(gc, 0, 0, image.getWidth(), image.getHeight(), x, y, imgW, imgH);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (item.released) {
						gc.setForeground(releasedColor);
					} else if (item.source == selectedObject) {
						gc.setForeground(selectedColor);
					} else if (item.type == MemoryViewImageType.LCDUI) {
						gc.setForeground(regularColor);
					} else {
						gc.setForeground(texColor);
					}

					if (drawImagesInfo) {
						gc.drawString(item.getCaption(), x - 1, y - fh * 4, true);
						gc.drawString(item.type2, x - 1, y - fh * 3, true);
						gc.drawString(image.getWidth() + "*" + image.getHeight(), x - 1, y - fh * 2, true);
						if (item.released)
							gc.drawString("Released", x - 1, y - fh, true);
						else
							gc.drawString("x" + image.getUsedCount(), x - 1, y - fh, true);
					}
					gc.drawRectangle(x - 1, y - 1, imgW + 1, imgH + 1);
					item.drawnRect = new Rectangle(x - 1, y - 1, imgW + 1, imgH + 1);
				} else {
					item.drawnRect = null; // skipped, no valid used area.
				}
				if (item.type == MemoryViewImageType.LCDUI) {
					if (!item.released) {
						pixels2d += image.getWidth() * image.getHeight();
						count2d++;
					}
				} else {
					pixels3d += image.getWidth() * image.getHeight();
					count3d++;
				}
				int minImgW = drawImagesInfo ? 50 : 0;
				x += Math.max(imgW, minImgW) + 10;
				max = Math.max(max, imgH);
			}
		}
		background.dispose();
		releasedColor.dispose();
		regularColor.dispose();
		selectedColor.dispose();
		int anInt1144 = y + max + this.imagesCanvasScroll + 10;
		this.imagesCanvas.getVerticalBar().setMaximum(anInt1144);
		this.imagesCanvas.getVerticalBar().setThumb(Math.min(anInt1144, this.imagesCanvas.getClientArea().height));
		this.imagesCanvas.getVerticalBar().setIncrement(10);
		imageControls.updateStats(pixels2d, pixels3d, count2d, count3d);
	}

	void changeClassesSort(final int n) {
		this.classTable.setSortColumn(this.classTable.getColumn(n));
		int x = (this.classTable.getSortDirection() == 128) ? 1024 : 128;
		this.classTable.setSortDirection(x);
		sortColumn = n;
		resortClasses();
	}

	private void resortClasses() {
		if (sortColumn == -1) return;
		classesList.sort(new ClassListComparator(this, sortColumn));
		setClassTableContent();
	}

	private void updateClassesView() {
		this.classesList = new ArrayList<>(memoryMgr.classesTable.keySet());
		for (int i = 0; i < this.classesList.size(); ++i) {
			if (this.memoryMgr.totalObjectsSize(this.classesList.get(i)) == 0) {
				this.classesList.remove(i--);
			}
		}
		Collections.sort(this.classesList);
		if (this.classesList.size() > this.classTable.getItemCount()) {
			for (int j = this.classesList.size() - this.classTable.getItemCount(); j > 0; --j) {
				new TableItem(this.classTable, 0);
			}
		} else {
			while (this.classTable.getItemCount() > this.classesList.size()) {
				this.classTable.remove(this.classesList.size());
			}
		}
		setClassTableContent();
	}

	private void setClassTableContent() {
		for (int i = 0; i < this.classesList.size(); ++i) {
			final String value = this.classesList.get(i);
			final TableItem item = this.classTable.getItem(i);
			if (displayPkgNames)
				item.setText(0, value);
			else {
				String[] split = value.split("\\.");
				item.setText(0, split[split.length - 1]);
			}
			item.setText(1, String.valueOf(this.memoryMgr.instancesCount(value)));
			item.setText(2, String.valueOf(this.memoryMgr.totalObjectsSize(value)));
			item.setData(value);
		}
	}

	void onClassTableItemSelection() {
		TableItem[] array = classTable.getSelection();
		if (array == null || array.length < 1) {
			return;
		}
		objectsTable.removeAll();
		String cls = (String) array[0].getData();
		ArrayList<ObjInstance> objs = this.memoryMgr.objs(cls);
		if (selectedObject != null && !selectedObject.getClass().getName().equals(cls))
			selectedObject = null;
		for (ObjInstance o : objs) {
			TableItem ti = new TableItem(objectsTable, 0);
			if (o.paths.isEmpty())
				ti.setText(0, "Unknown reference");
			else if (o.paths.size() == 1)
				ti.setText(0, o.paths.iterator().next().toString(displayPkgNames));
			else
				ti.setText(0, o.paths.iterator().next().toString(displayPkgNames) + "; " + (o.paths.size() - 1) + " more");
			String s = String.valueOf(o.value);
			//XXX
			if (s.length() > 128) {
				s = s.substring(0, 127) + "...";
			}
			ti.setText(1, s);
			ti.setText(2, String.valueOf(o.size));
			ti.setData(o);
		}
		clearObjectPaths();
	}

	public void widgetDisposed(final DisposeEvent disposeEvent) {
		this.dispose();
	}

	void setAutoUpdate(boolean enabled, int interval) {
		if (autoUpdater != null) {
			autoUpdater.stopThread();
			autoUpdater = null;
		}
		if (enabled) {
			if (interval < 10)
				throw new IllegalArgumentException("Too small update interval");
			autoUpdater = new AutoUpdate(this, interval);
			autoUpdater.start();
		}
	}

	public void setImagesDrawn(boolean b) {
		imagesDrawn = b;
		updateEverything();
	}

	void setImagesNotDrawn(boolean b) {
		imagesNeverDrawn = b;
		updateEverything();
	}

	void setImagesAscend(boolean b) {
		sortImagesByAscending = b;
		resortImages();
		updateView();
	}

	void setImagesSorting(int i) {
		imagesSortingMethod = i;
		resortImages();
		updateView();
	}

	void setShowReleasedImages(boolean b) {
		showReleasedImages = b;
		updateEverything();
	}

	void setShow2dImages(boolean b) {
		show2dImages = b;
		updateEverything();
	}

	void setShow3dImages(boolean b) {
		show3dImages = b;
		updateEverything();
	}

	void setShowImagesInfo(boolean b) {
		drawImagesInfo = b;
		updateView();
	}

	void setDarkenUnused(boolean b) {
		darkenUnused = b;
		updateEverything();
	}

	void setImagesScaling(int i) {
		double s = i;
		if (s == 0) {
			s = 0.5d;
		}
		imageScaling = s;
		updateView();
	}

	void setPkgNamesDisplay(boolean b) {
		displayPkgNames = b;
		updateClassesView();
		onClassTableItemSelection();
		syncObjectSelection();
		onObjectTableItemSelection();
	}

	Shell getShell() {
		return shell;
	}

	Object getSelectedImage() {
		return selectedObject;
	}

	Table getTheTable() {
		return classTable;
	}

	void onObjectTableItemSelection() {
		TableItem[] array = objectsTable.getSelection();
		if (array == null || array.length < 1) {
			clearObjectPaths();
			return;
		}

		ObjInstance inst = (ObjInstance) array[0].getData();
		displayObjectPaths(inst);
		selectedObject = inst.value;
		imagesCanvas.redraw();
	}

	private void clearObjectPaths() {
		for (Control c : objectPathsContainer.getChildren())
			c.dispose();
		new Label(objectPathsContainer, 0).setText("No objects selected. Select an object to inspect it.");
		objectPaths.setText("Paths to the object");
		objectPaths.layout(true, true);
		relayoutObjectSizes();
	}

	private void displayObjectPaths(ObjInstance inst) {
		for (Control c : objectPathsContainer.getChildren())
			c.dispose();
		for (ReferencePath o : inst.paths) {
			new ReferencePathDisplay(objectPathsContainer, o, displayPkgNames);
		}
		objectPaths.setText("Paths to the object (" + inst.paths.size() + ")");
		objectPaths.layout(true, true);
		relayoutObjectSizes();
	}

	private void relayoutObjectSizes() {
		objectPathsContainer.setSize(objectPathsContainer.computeSize(objectPathsContainer.getParent().getSize().x, -1));
	}

	void openWatcherForSelected() {
		TableItem[] array = objectsTable.getSelection();
		if (array == null || array.length < 1) {
			return;
		}
		final Object value = ((ObjInstance) array[0].getData()).value;
		if (value != null && emulator.debug.ClassTypes.isObject(value.getClass())) {
			new Watcher(value).open(shell);
		}
	}

	void openWatcherForSelectedClass() {
		TableItem[] array = classTable.getSelection();
		if (array == null || array.length < 1) {
			return;
		}
		try {
			new Watcher(Memory.cls(array[0].getData().toString())).open(getShell());
		} catch (Throwable ignored) {
			// arrays will throw
		}
	}

	void exportSelectedImage() {
		if (getSelectedImage() != null) {
			for (ImageViewItem item : imagesToShow) {
				if (item.source == getSelectedImage()) {
					final FileDialog fileDialog;
					(fileDialog = new FileDialog(getShell(), 8192)).setText(UILocale.get("MEMORY_VIEW_SAVE_TO_FILE", "Save to file"));
					fileDialog.setFilterExtensions(new String[]{"*.png"});
					final String open;
					if ((open = fileDialog.open()) != null) {
						item.drawable.getImpl().saveToFile(open);
					}
					return;
				}
			}
		}
		Emulator.getEmulator().getScreen().showMessage("No images selected. Click at one to select it.");
	}

	int getSortingMethod() {
		return imagesSortingMethod;
	}

	boolean getSortByAscending() {
		return sortImagesByAscending;
	}

	@Override
	public void controlMoved(ControlEvent controlEvent) {

	}

	@Override
	public void controlResized(ControlEvent controlEvent) {
		relayoutObjectSizes();
	}

	public static final class AutoUpdate extends Thread {
		boolean shouldRun;
		long updateInterval;
		private final MemoryView mv;

		AutoUpdate(final MemoryView mv, final long updateInterval) {
			super();
			this.mv = mv;
			this.updateInterval = updateInterval;
			this.shouldRun = true;
		}

		public void run() {
			while (this.shouldRun && !mv.getShell().isDisposed()) {
				try {
					mv.updateModel();
					mv.getShell().getDisplay().syncExec(mv::updateView);
					Thread.sleep(updateInterval);
				} catch (InterruptedException ignored) {
				}
			}
		}

		public void stopThread() {
			shouldRun = false;
			if (isAlive())
				interrupt();
		}
	}
}
