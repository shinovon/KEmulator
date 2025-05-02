package emulator.ui.swt;

import emulator.Emulator;
import emulator.Settings;
import emulator.UILocale;
import emulator.debug.Memory;
import emulator.debug.MemoryViewImageType;
import emulator.debug.ObjInstance;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
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
	public Canvas imagesCanvas;
	private SashForm horizontalSeparator;
	private Composite memoryPanel;
	private Table table;
	private Table classTable;
	private double imageScaling = 1d;
	private int imagesSortingMethod;
	private boolean sortImagesByAscending;
	Menu menuSaveOne;
	Menu menuSaveAll;
	private static final Object updateLock = new Object();
	static final Vector<Image> allImages = new Vector();
	static final ArrayList<ImageViewItem> imagesToShow = new ArrayList<>();
	private boolean updateInProgress;
	int imagesCanvasScroll;
	private boolean imagesDrawn = true;
	private boolean imagesNeverDrawn = true;
	private boolean showReleasedImages = false;
	private boolean show2dImages = true;
	private boolean show3dImages = true;
	private boolean darkenUnused = false;
	private boolean imgClassSelected;
	private boolean drawImagesInfo = false;
	private Image selectedImage;
	private ArrayList<String> classesList = new ArrayList<>();
	private AutoUpdate autoUpdater;
	private int sortColumn = -1;

	public static final String SHELL_TYPE = "MEMORY_VIEW";

	public void open() {
		createShell();
		Rectangle clientArea = ((EmulatorScreen) Emulator.getEmulator().getScreen()).getShell().getMonitor().getClientArea();
		shell.setLocation(
				clientArea.x + (int) (clientArea.height * 0.025), //- this.shell.getSize().x >> 3,
				clientArea.y + (int) (clientArea.height * 0.025)// - this.shell.getSize().y >> 2
		);
		shell.open();
		shell.addDisposeListener(this);
		shell.addControlListener(this);
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

		table = new Table(memoryPanel, 67584);
		table.setHeaderVisible(true);
		this.table.setLinesVisible(true);
		this.table.addSelectionListener(new TableListener(this));
		final TableColumn tableColumn;
		(tableColumn = new TableColumn(this.table, 0)).setWidth(170);
		tableColumn.setText(UILocale.get("MEMORY_VIEW_CLASS", "Class"));
		tableColumn.addSelectionListener(new Class31(this));
		final TableColumn tableColumn2;
		(tableColumn2 = new TableColumn(this.table, 0)).setWidth(70);
		tableColumn2.setText(UILocale.get("MEMORY_VIEW_INSTANCES", "Instances"));
		tableColumn2.addSelectionListener(new Class140(this));
		final TableColumn tableColumn3;
		(tableColumn3 = new TableColumn(this.table, 0)).setWidth(100);
		tableColumn3.setText(UILocale.get("MEMORY_VIEW_TOTAL_HEAP_SIZE", "Total Heap Size"));
		tableColumn3.addSelectionListener(new Class17(this));

		classTable = new Table(memoryPanel, 67584);
		classTable.setHeaderVisible(true);
		this.classTable.setLinesVisible(true);
		this.classTable.addSelectionListener(new Class19(this));
		this.classTable.addMouseListener(new ClassTableListener(this));
		final TableColumn tableColumn4;
		(tableColumn4 = new TableColumn(this.classTable, 0)).setWidth(230);
		tableColumn4.setText(UILocale.get("MEMORY_VIEW_REFERENCE", "Reference"));
		final TableColumn tableColumn5;
		(tableColumn5 = new TableColumn(this.classTable, 0)).setWidth(70);
		tableColumn5.setText(UILocale.get("MEMORY_VIEW_INSTANCE", "Instance"));
		final TableColumn tableColumn6;
		(tableColumn6 = new TableColumn(this.classTable, 0)).setWidth(100);
		tableColumn6.setText(UILocale.get("MEMORY_VIEW_SIZE", "Size"));
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
		this.imagesCanvas.addPaintListener(new ImagesCanvasRepainter(this));
		this.imagesCanvas.addMouseListener(new ImagesCanvasListener(this));
		this.imagesCanvas.getVerticalBar().addSelectionListener(new Class23(this));

		this.menuSaveOne = new Menu(this.shell, 8);
		final MenuItem menuItem;
		(menuItem = new MenuItem(this.menuSaveOne, 8)).setText(UILocale.get("MEMORY_VIEW_SAVE_AS", "Save As..."));
		menuItem.addSelectionListener(new SaveImageListener(this));
		this.menuSaveAll = new Menu(this.shell, 8);
		final MenuItem menuItem2;
		(menuItem2 = new MenuItem(this.menuSaveAll, 8)).setText(UILocale.get("MEMORY_VIEW_SAVE_ALL", "Save All Images..."));
		menuItem2.addSelectionListener(new SaveAllImagesListener(this));
	}

	private void updateModel() {
		if (this.updateInProgress) return;
		this.updateInProgress = true;
		try {
			this.memoryMgr.updateEverything();
			this.updateImagesList();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		this.updateInProgress = false;
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
			MemoryView.imagesToShow.sort(new ImagesComparator(this));
		}
	}

	private void updateImagesList() {
		synchronized (MemoryView.updateLock) {
			MemoryView.allImages.clear();
			MemoryView.imagesToShow.clear();
			for (Image image : this.memoryMgr.images) {
				allImages.add(image);
				boolean add = (imagesDrawn && image.getUsedCount() > 0) || (imagesNeverDrawn && image.getUsedCount() == 0);
				if (!add)
					continue;
				ImageViewItem i = new ImageViewItem(image, false);
				boolean add2 = (show2dImages && i.type == MemoryViewImageType.LCDUI) || (show3dImages && i.type != MemoryViewImageType.LCDUI);
				if (add2)
					MemoryView.imagesToShow.add(i);

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

	public boolean selectImageClicked(final int x, final int y) {
		synchronized (MemoryView.updateLock) {
			for (ImageViewItem image : imagesToShow) {
				if (image.drawnRect == null)
					continue;
				if (image.drawnRect.contains(x, y)) {
					selectedImage = image.image;
					if (!imgClassSelected)
						return true;
					for (int i = 0; i < classTable.getItemCount(); i++) {
						ObjInstance o = (ObjInstance) classTable.getItem(i).getData();
						if (o.value == selectedImage) {
							classTable.select(i);
							imagesCanvas.redraw();
							return true;
						}
					}
					imagesCanvas.redraw();
					return true;
				}
			}
			return false;
		}
	}

	public void paintImagesCanvas(final GC gc) {
		final int fh = gc.getFontMetrics().getHeight();
		int canvasW = imagesCanvas.getClientArea().width;
		int canvasH = imagesCanvas.getClientArea().height;
		int y = 10 - this.imagesCanvasScroll;
		if (drawImagesInfo)
			y += fh * 3;
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
				Image image = item.image;
				final int imgW = (int) (image.getWidth() * this.imageScaling);
				final int imgH = (int) (image.getHeight() * this.imageScaling);
				if (x + imgW + 30 > canvasW) {
					x = 10;
					y += max + 10;
					if (drawImagesInfo)
						y += fh * 3;
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
					} else if (item.type == MemoryViewImageType.LCDUI) {
						if (this.imgClassSelected && selectedImage == image) {
							gc.setForeground(selectedColor);
						} else {
							gc.setForeground(regularColor);
						}
					} else {
						gc.setForeground(texColor);
					}

					if (drawImagesInfo) {
						gc.drawString(item.getCaption(), x - 1, y - fh * 3, true);
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
				int minImgW = drawImagesInfo ? 30 : 0;
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
		this.table.setSortColumn(this.table.getColumn(n));
		int x = (this.table.getSortDirection() == 128) ? 1024 : 128;
		this.table.setSortDirection(x);
		sortColumn = n;
		resortClasses();
	}

	private void resortClasses() {
		if (sortColumn == -1) return;
		classesList.sort(new ClassListComparator(this, sortColumn));
		for (int i = 0; i < this.classesList.size(); ++i) {
			final String value = this.classesList.get(i);
			final TableItem item;
			(item = this.table.getItem(i)).setText(0, value);
			item.setText(1, String.valueOf(this.memoryMgr.instancesCount(value)));
			item.setText(2, String.valueOf(this.memoryMgr.totalObjectsSize(value)));
		}
	}

	private void updateClassesView() {
		this.classesList = Collections.list(this.memoryMgr.classesTable.keys());
		for (int i = 0; i < this.classesList.size(); ++i) {
			if (this.memoryMgr.totalObjectsSize(this.classesList.get(i)) == 0) {
				this.classesList.remove(i--);
			}
		}
		Collections.sort(this.classesList);
		if (this.classesList.size() > this.table.getItemCount()) {
			for (int j = this.classesList.size() - this.table.getItemCount(); j > 0; --j) {
				new TableItem(this.table, 0);
			}
		} else {
			while (this.table.getItemCount() > this.classesList.size()) {
				this.table.remove(this.classesList.size());
			}
		}
		for (int k = 0; k < this.classesList.size(); ++k) {
			final String value = this.classesList.get(k);
			final TableItem item;
			(item = this.table.getItem(k)).setText(0, value);
			item.setText(1, String.valueOf(this.memoryMgr.instancesCount(value)));
			item.setText(2, String.valueOf(this.memoryMgr.totalObjectsSize(value)));
		}
		//this.aTable1096.setSortColumn(this.aTable1096.getColumn(0));
		//this.aTable1096.setSortDirection(128);
	}

	void onTableItemSelection() {
		TableItem[] array = table.getSelection();
		if (array == null || array.length < 1) {
			return;
		}
		classTable.removeAll();
		String text = array[0].getText(0);
		Vector<ObjInstance> objs = this.memoryMgr.objs(text);
		imgClassSelected = text.equalsIgnoreCase("javax.microedition.lcdui.Image");
		selectedImage = null;
		for (ObjInstance o : objs) {
			TableItem ti = new TableItem(classTable, 0);
			if (o.paths.isEmpty())
				ti.setText(0, "Unknown reference");
			else if (o.paths.size() == 1)
				ti.setText(0, o.paths.get(0));
			else
				ti.setText(0, o.paths.get(0) + "; " + (o.paths.size() - 1) + " more");
			String s = String.valueOf(o.value);
			//XXX
			if (s.length() > 128) {
				s = s.substring(0, 127) + "...";
			}
			ti.setText(1, s);
			ti.setText(2, String.valueOf(o.size));
			ti.setData(o);
		}
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

	Shell getShell() {
		return shell;
	}

	Image getSelectedImage() {
		return selectedImage;
	}

	Table getTheTable() {
		return table;
	}

	void trySetImageSelectedIndexForClasses() {
		if (!imgClassSelected)
			return;
		TableItem[] array = classTable.getSelection();
		if (array == null || array.length < 1)
			return;
		final Object value = ((ObjInstance) array[0].getData()).value;
		if (value instanceof Image)
			selectedImage = (Image) value;
		imagesCanvas.redraw();
	}

	void openWatcherForSelected() {
		TableItem[] array = classTable.getSelection();
		if (array == null || array.length < 1) {
			return;
		}
		final Object value = ((ObjInstance) array[0].getData()).value;
		if (value != null && emulator.debug.ClassTypes.method871(value.getClass())) {
			new Watcher(value).open(shell);
		}
	}

	int getSortingMethod() {
		return imagesSortingMethod;
	}

	boolean getSortByAscending() {
		return sortImagesByAscending;
	}

	public boolean isUpdating() {
		return updateInProgress;
	}

	@Override
	public void controlMoved(ControlEvent controlEvent) {

	}

	@Override
	public void controlResized(ControlEvent controlEvent) {
		imageControls.layout(true, true);
		imagesPanel.layout(true, true);
	}

	public static final class AutoUpdate extends Thread {
		boolean shouldRun;
		long currentMillis;
		long updateInterval;
		private final MemoryView mv;

		AutoUpdate(final MemoryView mv, final long updateInterval) {
			super();
			this.mv = mv;
			this.updateInterval = updateInterval;
			this.shouldRun = true;
			mv.autoUpdater = this;
		}

		public void run() {
			this.currentMillis = System.currentTimeMillis();
			while (this.shouldRun && !mv.getShell().isDisposed()) {
				try {
					if (System.currentTimeMillis() - this.currentMillis > this.updateInterval && !mv.isUpdating()) {
						mv.updateModel();
						mv.getShell().getDisplay().syncExec(mv::updateView);
						currentMillis = System.currentTimeMillis();
					}
					Thread.sleep(1L);
				} catch (InterruptedException ignored) {
				}
			}
			this.mv.autoUpdater = this;
		}

		public void stopThread() {
			shouldRun = false;
			if (isAlive())
				interrupt();
		}
	}
}
