package emulator.ui.swt;

import emulator.Emulator;
import emulator.Settings;
import emulator.UILocale;
import emulator.debug.Memory;
import emulator.debug.MemoryViewImageType;
import emulator.debug.ObjInstance;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import javax.microedition.lcdui.Image;
import java.util.*;

public final class MemoryView implements DisposeListener {
	private Shell shell;
	private Button autoUpdateBtn;
	private Text autoUpdateIntervalText;
	private CLabel bytecodeSizeLbl;
	private CLabel objectsSizeLbl;
	private CLabel totalmemLbl;
	private CLabel maxmemLbl;
	private final Display display = SWTFrontend.getDisplay();
	public final Memory memoryMgr = Memory.getInstance();
	private boolean visible;
	private Composite aComposite1098;
	private ImageViewControls imageControls;
	public Canvas imagesCanvas;
	private SashForm horizontalSeparator;
	private Composite aComposite1116;
	private Table table;
	private Table classTable;
	private double imageScaling = 1d;
	private int imagesSortingMethod;
	private boolean sortImagesByAscending;
	Menu menuSaveOne;
	Menu menuSaveAll;
	private int bytecodeSize;
	private int objectsSize;
	private int maxObjectsSize;
	private static final Object updateLock = new Object();
	static final Vector<Image> allImages = new Vector();
	static final ArrayList<ImageViewItem> imagesToShow = new ArrayList<>();
	private boolean updateInProgress;
	int imagesCanvasScroll;
	private boolean imagesDrawn = true;
	private boolean imagesNeverDrawn = true;
	private boolean showReleasedImages;
	private boolean darkenUnused;
	private boolean imgClassSelected;
	private Image selectedImage;
	private ArrayList<String> classesList = new ArrayList<>();
	private AutoUpdate autoUpdater;
	private Thread autoUpdateThread;
	private CLabel jvmmemLabel;
	private int sortColumn = -1;

	public static final String SHELL_TYPE = "MEMORY_VIEW";

	public void open() {
		this.createShell();
		Rectangle clientArea = ((EmulatorScreen) Emulator.getEmulator().getScreen()).getShell().getMonitor().getClientArea();
		this.shell.setLocation(
				clientArea.x + (int) (clientArea.height * 0.025), //- this.shell.getSize().x >> 3,
				clientArea.y + (int) (clientArea.height * 0.025)// - this.shell.getSize().y >> 2
		);
		this.shell.open();
		this.shell.addDisposeListener(this);
		updateEverything();
		this.visible = true;
		while (!this.shell.isDisposed()) {
			if (!this.display.readAndDispatch()) {
				this.display.sleep();
			}
		}
		Settings.showMemViewFrame = false;
		if (this.autoUpdateThread != null && this.autoUpdateThread.isAlive()) {
			this.autoUpdateThread.interrupt();
		}
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
		final GridData gridData;
		(gridData = new GridData()).horizontalAlignment = 4;
		gridData.verticalAlignment = 2;
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.verticalAlignment = 2;
		final GridData layoutData2;
		(layoutData2 = new GridData()).horizontalAlignment = 4;
		layoutData2.verticalAlignment = 2;
		final GridData layoutData3;
		(layoutData3 = new GridData()).horizontalAlignment = 4;
		layoutData3.verticalAlignment = 2;
		final GridData layoutData4;
		(layoutData4 = new GridData()).horizontalSpan = 2;
		layoutData4.verticalAlignment = 2;
		layoutData4.grabExcessHorizontalSpace = false;
		layoutData4.horizontalAlignment = 4;
		final GridData gridData2;
		(gridData2 = new GridData()).horizontalAlignment = 4;
		gridData2.grabExcessHorizontalSpace = false;
		gridData2.verticalAlignment = 2;
		final GridData gridData3;
		(gridData3 = new GridData()).horizontalAlignment = 4;
		gridData3.grabExcessHorizontalSpace = false;
		gridData3.verticalAlignment = 2;
		final GridData gridData4;
		(gridData4 = new GridData()).horizontalAlignment = 4;
		gridData4.grabExcessHorizontalSpace = false;
		gridData4.horizontalSpan = 3;
		gridData4.verticalAlignment = 2;
		final GridData layoutData5;
		(layoutData5 = new GridData()).horizontalAlignment = 4;
		layoutData5.grabExcessHorizontalSpace = false;
		layoutData5.horizontalIndent = 0;
		layoutData5.verticalAlignment = 2;
		final GridData layoutData6;
		(layoutData6 = new GridData()).verticalAlignment = 2;
		layoutData6.verticalSpan = 1;
		layoutData6.grabExcessHorizontalSpace = false;
		layoutData6.horizontalAlignment = 4;
		final GridData gridData7;
		(gridData7 = new GridData()).horizontalAlignment = 4;
		gridData7.grabExcessHorizontalSpace = false;
		gridData7.verticalAlignment = 2;
		final GridData gridData8;
		(gridData8 = new GridData()).horizontalAlignment = 4;
		gridData8.grabExcessHorizontalSpace = false;
		gridData8.verticalAlignment = 2;
		final GridData gridData9;
		(gridData9 = new GridData()).verticalAlignment = 2;
		gridData9.grabExcessHorizontalSpace = false;
		gridData9.horizontalAlignment = 4;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 7;
		final int shellStyle = SWT.MAX | SWT.FOREGROUND | SWT.TITLE | SWT.MENU | SWT.MIN;
		(this.shell = new Shell(shellStyle)).setText(UILocale.get("MEMORY_VIEW_TITLE", "MemoryView"));
		this.shell.setImage(new org.eclipse.swt.graphics.Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.shell.setLayout(layout);
		shell.setData("TYPE", SHELL_TYPE);

		Rectangle clientArea = ((EmulatorScreen) Emulator.getEmulator().getScreen()).getShell().getMonitor().getClientArea();
		this.shell.setSize(
				(int) (clientArea.width * 0.4),
				(int) (clientArea.height * 0.95)
		);

		Button aButton1081;
		(aButton1081 = new Button(this.shell, 8388608)).setText(" " + UILocale.get("MEMORY_VIEW_UPDATE", "Update") + " ");
		(aButton1081).setLayoutData(layoutData6);
		aButton1081.addSelectionListener(new MemoryViewUpdateListener(this));
		(this.autoUpdateBtn = new Button(this.shell, 32)).setText(UILocale.get("MEMORY_VIEW_AUTO_UPDATE", "AutoUpdate"));
		(this.autoUpdateBtn).setLayoutData(layoutData4);
		this.autoUpdateBtn.addSelectionListener(new MemoryViewAutoUpdateListener(this));
		CLabel aCLabel1105;
		(aCLabel1105 = new CLabel(this.shell, 0)).setText(UILocale.get("MEMORY_VIEW_BYTECODE_SIZE", "ByteCode Size:"));
		(aCLabel1105).setLayoutData(layoutData);
		(this.bytecodeSizeLbl = new CLabel(this.shell, 0)).setText("0              bytes");
		(this.bytecodeSizeLbl).setLayoutData(gridData3);
		CLabel aCLabel1128;
		(aCLabel1128 = new CLabel(this.shell, 0)).setText(UILocale.get("MEMORY_VIEW_TOTALMEM_SIZE", "Total Memory Used:"));
		(aCLabel1128).setLayoutData(gridData);
		(this.totalmemLbl = new CLabel(this.shell, 0)).setText("0");
		(this.totalmemLbl).setLayoutData(gridData2);


		Button gcButton = new Button(shell, SWT.PUSH);
		gcButton.setText("GC");
		gcButton.setLayoutData(gridData9);
		gcButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				System.gc();
			}
		});
		CLabel aCLabel1082;
		(aCLabel1082 = new CLabel(this.shell, 0)).setText(UILocale.get("MEMORY_VIEW_INTERVAL", "Interval(milli sec):"));
		(aCLabel1082).setLayoutData(layoutData3);
		(this.autoUpdateIntervalText = new Text(this.shell, 2048)).setText("1000");
		(this.autoUpdateIntervalText).setLayoutData(layoutData5);
		CLabel aCLabel1114;
		(aCLabel1114 = new CLabel(this.shell, 0)).setText(UILocale.get("MEMORY_VIEW_OBJECT_SIZE", "Objects Size:"));
		aCLabel1114.setLayoutData(layoutData2);
		(this.objectsSizeLbl = new CLabel(this.shell, 0)).setText("0");
		this.objectsSizeLbl.setLayoutData(gridData7);
		CLabel aCLabel1136;
		(aCLabel1136 = new CLabel(this.shell, 0)).setText(UILocale.get("MEMORY_VIEW_MAX_OBJECT_SIZE", "Max Objects Size:"));
		aCLabel1136.setLayoutData(gridData);
		(this.maxmemLbl = new CLabel(this.shell, 0)).setText("0");
		this.maxmemLbl.setLayoutData(gridData2);
		new CLabel(this.shell, 0).setText(UILocale.get("MEMORY_VIEW_JVM_MEMORY", "Real usage:"));
		(this.jvmmemLabel = new CLabel(this.shell, 0)).setText("0");
		this.jvmmemLabel.setLayoutData(gridData8);
		this.createSeparator();
	}

	private void createControlsForImagesView() {
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 1;
		layout.marginHeight = 2;
		layout.marginWidth = 0;
		(this.aComposite1098 = new Composite(this.horizontalSeparator, 0)).setLayout(layout);
		imageControls = new ImageViewControls(aComposite1098, this);
		this.createImagesCanvas();
	}

	private void createImagesCanvas() {
		final GridData layoutData = new GridData();
		layoutData.horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = 4;
		((this.imagesCanvas = new Canvas(this.aComposite1098, 537135616))).setLayout(null);
		this.imagesCanvas.setLayoutData(layoutData);
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

	private void createSeparator() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalSpan = 7;
		layoutData.horizontalAlignment = 4;
		layoutData.verticalAlignment = 4;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.grabExcessHorizontalSpace = true;

		(this.horizontalSeparator = new SashForm(this.shell, 0)).setOrientation(SWT.VERTICAL);
		this.horizontalSeparator.setSashWidth(5);
		this.horizontalSeparator.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		this.horizontalSeparator.setLayoutData(layoutData);
		this.method688();
		this.createControlsForImagesView();

		this.horizontalSeparator.setWeights(new int[]{4, 6});

	}

	private void method688() {
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 1;
		this.aComposite1116 = new Composite(this.horizontalSeparator, 0);
		this.method689();
		this.aComposite1116.setLayout(layout);
	}

	private void method689() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = 4;
		SashForm aSashForm1103;
		(aSashForm1103 = new SashForm(this.aComposite1116, 0)).setLayoutData(layoutData);
		(this.table = new Table(aSashForm1103, 67584)).setHeaderVisible(true);
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
		(this.classTable = new Table(aSashForm1103, 67584)).setHeaderVisible(true);
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

	private void updateModel() {
		if (this.updateInProgress) return;
		this.updateInProgress = true;
		try {
			this.memoryMgr.updateEverything();
			this.updateImagesList();
			this.bytecodeSize = Memory.getBytecodeSize();
			this.objectsSize = this.memoryMgr.objectsSize();
			this.maxObjectsSize = Math.max(this.maxObjectsSize, this.objectsSize);
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
		} catch (Exception ignored) {
		}
		this.bytecodeSizeLbl.setText(this.bytecodeSize + " bytes");
		this.objectsSizeLbl.setText(this.objectsSize + " bytes");
		this.totalmemLbl.setText(this.bytecodeSize + this.objectsSize + " bytes");
		this.maxmemLbl.setText(this.maxObjectsSize + " bytes");
		long t = Runtime.getRuntime().totalMemory();
		long f = Runtime.getRuntime().freeMemory();
		this.jvmmemLabel.setText(((t - f) / 1024L) + "/" + (t / 1024L) + " kb");
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
				if (add)
					MemoryView.imagesToShow.add(new ImageViewItem(image, false));

			}
			if (showReleasedImages) {
				for (Image image : this.memoryMgr.releasedImages) {
					allImages.add(image);
					MemoryView.imagesToShow.add(new ImageViewItem(image, false));
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
					return true;
				}
			}
			return false;
		}
	}

	public void paintImagesCanvas(final GC gc) {
		int canvasW = imagesCanvas.getClientArea().width;
		int canvasH = imagesCanvas.getClientArea().height;
		int y = 12 - this.imagesCanvasScroll;
		int x = 10;
		int max = 0;
		final Color background = new Color(null, 151, 150, 147);
		final Color releasedColor = new Color(null, 255, 0, 0);
		final Color regularColor = new Color(null, 0, 0, 0);
		final Color selectedColor = new Color(null, 0, 255, 0);
		final Color texColor = new Color(null, 0, 0, 255);
		gc.setBackground(background);
		gc.fillRectangle(0, 0, canvasW, canvasH);
		int pixels2d = 0;
		int pixels3d = 0;
		synchronized (MemoryView.updateLock) {
			final int size = MemoryView.imagesToShow.size();
			for (int i = 0; i < size; ++i) {
				ImageViewItem item = MemoryView.imagesToShow.get(i);
				Image image = item.image;
				final int imgW = (int) (image.getWidth() * this.imageScaling);
				final int imgH = (int) (image.getHeight() * this.imageScaling);
				if (x + imgW + 30 > canvasW) {
					x = 10;
					y += max + 12;
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
					if (item.type == MemoryViewImageType.Lcdui) {
						if (this.imgClassSelected && selectedImage == image) {
							gc.setForeground(selectedColor);
						} else {
							gc.setForeground(item.released ? releasedColor : regularColor);
						}
					} else {
						gc.setForeground(texColor);
						gc.drawString(item.type.toString(), x - 1, y + 1 - gc.getFontMetrics().getHeight(), true);
					}
					gc.drawRectangle(x - 1, y - 1, imgW + 1, imgH + 1);
					item.drawnRect = new Rectangle(x - 1, y - 1, imgW + 1, imgH + 1);
				} else {
					item.drawnRect = null; // skipped, no valid used area.
				}
				if (item.type == MemoryViewImageType.Lcdui) {
					if (!item.released)
						pixels2d += image.getWidth() * image.getHeight();
				} else {
					pixels3d += image.getWidth() * image.getHeight();
				}
				x += imgW + 10;
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
		imageControls.updateStats(pixels2d + pixels3d);
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

	void setAutoUpdate() {
		if (autoUpdateBtn.getSelection()) {
			final long updateInterval;
			if ((updateInterval = Long.parseLong(autoUpdateIntervalText.getText().trim())) > 0L) {
				if (autoUpdater != null) {
					autoUpdater.shouldRun = false;
				}
				autoUpdater = new MemoryView.AutoUpdate(this, updateInterval);
				autoUpdateThread = new Thread(autoUpdater);
				autoUpdateThread.start();
			}
			return;
		}
		if (autoUpdater != null) {
			autoUpdater.shouldRun = false;
			if (autoUpdateThread.isAlive()) {
				autoUpdateThread.interrupt();
			}
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

	public static final class AutoUpdate implements Runnable {
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
	}
}
