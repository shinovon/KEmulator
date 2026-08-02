package emulator.ui.swt;

import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;
import com.sun.jna.ptr.IntByReference;
import emulator.Emulator;
import emulator.Settings;
import emulator.Utils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class FavoritesBrowser {

	static final boolean DEBUG_PERF = true;

	private Shell shell;
	private Shell parentShell;
	private Display display;
	private Runnable onClose;
	private Table listTable;
	private java.util.List<File> favorites = new ArrayList<>();
	private java.util.List<File> unfilteredFavorites = new ArrayList<>();
	private Text searchBox;
	private boolean searchPlaceholder = true;
	private Color searchPlaceholderColor;
	private String favoritesDir;
	private Label statusLabel;
	private String savedDefaultStatusText = "";
	private boolean showingTempStatus;
	private int statusRestoreGen;
	private Font listFont;
	private Font btnFont;
	private Font subFont;
	private Color subColor;
	private Color buildColor;
	private Color selFallbackColor; // brightened system selection color for items with no palette
	private int fontSize = 12;      // +UI/-UI: controls buttons + status labels; saved as FavoritesFontSize
	private int textFontSize = 12;  // T+/T-:   controls list items only;         saved as FavoritesTextFontSize
	private int currentIconSize = 16;
	private boolean fullscreen;
	private boolean darkMode;
	private Color paintBg;
	private Color paintFg;
	private Color paintWidgetBg;
	private Color darkShellBg;
	private Color darkButtonBg;
	private Color darkFg;
	private Color tableBorderBg;
	private Color sepColor;
	private Color hoverFallbackColor;
	private int hoveredRowIdx = -1;

	private Composite topToolbar;
	private Composite bottomBtns;
	private Composite bottomToolbar;
	private Button themeToggleBtn;
	private Composite tableBorderComp;

	// Toolbar buttons for navigation
	private java.util.List<Button> toolbarButtons = new ArrayList<>();
	private int focusTarget = -1; // -1 = list, 0..N = toolbar button index
	private int folderButtonIndex = -1; // index of "Folder..." button, disabled in keyboard mode
	private int firstBottomBtnIndex = -1; // first index of bottom-row buttons
	private int fullscreenBtnIndex = -1;
	private int activeRow = 0; // 0 = top row, 1 = bottom row
	private int selectedRowIdx = -1;
	private int savedFocusRow0 = -1;
	private int savedFocusRow1 = -1;
	private Shell rowOverlayShell;
	private Shell loadingToastShell;
	private Listener navFilter;
	private Listener traverseFilter;
	private final java.util.Map<File, Float> rowHueCache = new java.util.HashMap<>();
	private final java.util.Map<File, Color> animColorCache = new java.util.HashMap<>();
	private final java.util.Map<File, int[]> iconPaletteCache = new java.util.HashMap<>();
	private double huePhaseOffset;
	private Listener keyUpFilter;
	private int heldScrollKey;
	private boolean scrollTimerActive;
	private int scrollTier; // 0=80ms, 1=60ms, 2=40ms, 3=20ms
	private int scrollGen;
	private long tierStartTime;
	private long scrollLingerEnd;
	private long lastScrollTime;
	private boolean keyboardMode;
	private Button folderButton;
	private Button luckyToggleButton;
	private int mouseActivityTimer = -1;
	private Runnable disableButtonTask;
	private Listener mouseMoveFilter;

	private Button heldConfirmBtn;
	private int heldConfirmGen;
	private long heldConfirmStartTime;

	private boolean isSwapped;
	private String originalFavoritesPath;
	private java.util.List<File> cachedJarFiles = new ArrayList<>();
	private int currentJarIndex = -1;
	private Button moveButton;
	private Button prevJarButton;
	private Button currentJarButton;
	private Button nextJarButton;
	private Button swapDirButton;
	private int selectionAnimGen;
	private boolean glowActive;
	private long glowStartTime;
	private String swapDirCache;
	private Button viewToggleBtn;
	private Button detailsInfoBtn;
	private Button namesToggleBtn;
	private Button iconZoomInBtn;
	private Button iconZoomOutBtn;
	private Canvas iconCanvas;
	private int iconScrollOffset;
	private Label infoBarLabel;
	private int heldBtnKey;
	private int btnRepeatGen;
	private StackLayout stackLayout;
	private Composite mainArea;
	private int iconGridCols = 1;
	private Image fallbackIcon;
	private int iconGridIconSize = 64;
	private int selectedGridCol;
	private int lastPaintFirstRow;
	private int lastPaintLastRow;
	private int[] lastPaintRowY;
	private int[] lastPaintRowH;
	private int iconTotalHeight;
	private int iconLoadGeneration;

	private static class MidletInfo {
		String name;
		String vendor;
		String version;
		String iconPath;
		byte[] iconBytes;
		Image iconImage;
		long buildDate;
	}
	private static class CacheEntry implements Serializable {
		private static final long serialVersionUID = 2L;
		boolean valid;
		String name;
		String vendor;
		String version;
		String iconPath;
		long lastModified;
		long buildDate;
	}
	private Map<File, MidletInfo> metadataCache = new HashMap<>();
	private int listPadding;
	private TableColumn spacerCol;
	private TableColumn iconCol;
	private TableColumn textCol;
	private TableColumn rightSpacerCol;

	public FavoritesBrowser(Display display, Runnable onClose) {
		this.display = display;
		this.onClose = onClose;
		this.favoritesDir = getFavoritesDir();
		loadModeSettings(Settings.favoritesViewMode);
		this.darkMode = Settings.favoritesDarkMode;
		disableButtonTask = () -> {
			if (keyboardMode && folderButton != null && !folderButton.isDisposed()) {
				folderButton.setEnabled(false);
			}
		};
	}

	private String getFavoritesDir() {
		int idx = Settings.luckyFolderBrowserIndex;
		if (idx > 0) {
			String[] paths = LuckyFolderManager.getPaths();
			boolean[] modes = LuckyFolderManager.getModes();
			int favIdx = 0;
			for (int i = 0; i < paths.length; i++) {
				if (i < modes.length && modes[i]) {
					favIdx++;
					if (favIdx == idx) return paths[i];
				}
			}
		}
		if (Settings.favoritesPath != null && !Settings.favoritesPath.isEmpty()) {
			return Settings.favoritesPath;
		}
		String defaultPath = Emulator.getUserPath() + File.separator + "favorites";
		Settings.favoritesPath = defaultPath;
		return defaultPath;
	}

	public void open(Shell parent) {
		if (shell != null && !shell.isDisposed()) {
			shell.open();
			Button fullBtn = toolbarButtons.get(fullscreenBtnIndex);
			focusTarget = fullscreenBtnIndex;
			display.asyncExec(() -> {
				if (fullBtn != null && !fullBtn.isDisposed()) {
					fullBtn.setFocus();
					shell.forceActive();
				}
			});
			grabFocus();
			return;
		}
		this.parentShell = parent;

		boolean wasFullscreen = EmulatorScreen.fullscreen; // Performance Scene

		fullscreen = wasFullscreen;
		int style = fullscreen ? SWT.NONE : (SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
		shell = new Shell(parent, style);
		shell.setText("Favorites");
		shell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		shell.setLayout(new GridLayout(1, false));
		if (onClose != null) {
			shell.addDisposeListener(e -> onClose.run());
		}
		if (!fullscreen) {
			shell.setSize(860, 570);
		}

		// Top toolbar — navigation/actions + search
		topToolbar = new Composite(shell, SWT.NONE);
		topToolbar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout topGrid = new GridLayout(1, false);
		topGrid.marginWidth = 4;
		topGrid.marginHeight = 2;
		topGrid.marginLeft = 0;
		topGrid.marginRight = 0;
		topGrid.marginTop = 0;
		topGrid.marginBottom = 0;
		topToolbar.setLayout(topGrid);

		Composite topBtns = new Composite(topToolbar, SWT.NONE);
		topBtns.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		RowLayout topBtnRow = new RowLayout(SWT.HORIZONTAL);
		topBtnRow.center = true;
		topBtnRow.wrap = true;
		topBtnRow.marginLeft = 0;
		topBtnRow.marginRight = 0;
		topBtnRow.marginTop = 0;
		topBtnRow.marginBottom = 0;
		topBtnRow.spacing = 4;
		topBtns.setLayout(topBtnRow);

		toolbarButtons.clear();
		loadJarCache();

		prevJarButton = new Button(topBtns, SWT.FLAT | SWT.PUSH);
		prevJarButton.setText("◀ Prev  ");
		prevJarButton.setToolTipText("Select previous JAR in folder");
		setupLongPressButton(prevJarButton, () -> selectPrevJar(),
			() -> { currentJarIndex--; saveJarCacheIndex(); loadJarDirect(cachedJarFiles.get(currentJarIndex)); });
		toolbarButtons.add(prevJarButton);
		currentJarButton = new Button(topBtns, SWT.FLAT | SWT.PUSH);
		currentJarButton.setText(" ⏎ Current");
		currentJarButton.setToolTipText("Select currently loaded JAR in the list");
		setupLongPressButton(currentJarButton, () -> selectCurrentJar(),
			() -> { saveJarCacheIndex(); loadJarDirect(cachedJarFiles.get(currentJarIndex)); });
		toolbarButtons.add(currentJarButton);
		nextJarButton = new Button(topBtns, SWT.FLAT | SWT.PUSH);
		nextJarButton.setText("Next ▶ ");
		nextJarButton.setToolTipText("Select next JAR in folder");
		setupLongPressButton(nextJarButton, () -> selectNextJar(),
			() -> { currentJarIndex++; saveJarCacheIndex(); loadJarDirect(cachedJarFiles.get(currentJarIndex)); });
		toolbarButtons.add(nextJarButton);
		swapDirButton = createToolButton(topBtns, "Swap directory", "Swap favorites folder with loaded JAR's folder", () -> toggleSwap());
		toolbarButtons.add(swapDirButton);
		moveButton = createToolButton(topBtns, "Move Current Here", "Move current JAR to this folder (after restart)", () -> {
			if (isSwapped) moveSelectedToFavs();
			else scheduleMoveCurrentToFavorites();
		});
		toolbarButtons.add(moveButton);
		Button removeBtn = new Button(topBtns, SWT.FLAT | SWT.PUSH);
		removeBtn.setText("Remove");
		setupLongPressButton(removeBtn,
			() -> removeSelected(),
			() -> { int idx = selectedRowIdx; if (idx >= 0 && idx < favorites.size()) removeJarFile(favorites.get(idx)); });
		toolbarButtons.add(removeBtn);
		toolbarButtons.add(createToolButton(topBtns, "Open", null, () -> openSelected()));
		folderButtonIndex = toolbarButtons.size();
		folderButton = createToolButton(topBtns, "Folder...", null, () -> changeFavoritesPath());
		toolbarButtons.add(folderButton);

		luckyToggleButton = createToolButton(topBtns, getLuckyToggleLabel(), "Toggle between Favorites and Lucky Folders", () -> cycleLuckyFolder());
		toolbarButtons.add(luckyToggleButton);

		updateButtonStates();

		infoBarLabel = new Label(shell, SWT.CENTER);
		GridData infoBarData = new GridData(SWT.FILL, SWT.TOP, true, false);
		infoBarData.exclude = true;
		infoBarLabel.setLayoutData(infoBarData);
		infoBarLabel.setVisible(false);

		// Main content area with stack layout to isolate icon mode from table mode
		stackLayout = new StackLayout();
		mainArea = new Composite(shell, SWT.NONE);
		mainArea.setLayout(stackLayout);
		mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// List view (virtual for fast loading of large directories) — wrapped in border composite
		tableBorderComp = new Composite(mainArea, SWT.NONE);
		GridLayout tableBorderLayout = new GridLayout(1, false);
		tableBorderLayout.marginWidth = 0;
		tableBorderLayout.marginHeight = 0;
		tableBorderComp.setLayout(tableBorderLayout);
		listTable = new Table(tableBorderComp, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.VIRTUAL);
		removeNativeEdge(listTable);
		listTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		listTable.setHeaderVisible(false);
		listTable.setLinesVisible(false);
		listTable.addListener(SWT.Traverse, e -> {
			if (e.detail == SWT.TRAVERSE_ARROW_PREVIOUS || e.detail == SWT.TRAVERSE_ARROW_NEXT
					|| e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS)
				e.doit = false;
		});
		listTable.addListener(SWT.KeyDown, e -> {
			if (e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_UP) {
				e.doit = false;
			}
		});

		spacerCol = new TableColumn(listTable, SWT.LEFT);
		spacerCol.setWidth(listPadding);
		spacerCol.setResizable(false);
		currentIconSize = Math.max(16, fontSize * 2);
		iconCol = new TableColumn(listTable, SWT.LEFT);
		iconCol.setWidth(currentIconSize);
		iconCol.setResizable(false);
		textCol = new TableColumn(listTable, SWT.LEFT);
		rightSpacerCol = new TableColumn(listTable, SWT.LEFT);
		rightSpacerCol.setWidth(listPadding);
		rightSpacerCol.setResizable(false);
		listTable.addListener(SWT.Resize, e -> updateTableCols());

		// Icon canvas view — completely separate from table
		iconCanvas = new Canvas(mainArea, SWT.DOUBLE_BUFFERED | SWT.V_SCROLL);
		iconCanvas.addListener(SWT.Paint, this::onIconCanvasPaint);
		iconCanvas.addListener(SWT.MouseDown, this::onIconCanvasMouseDown);
		iconCanvas.addListener(SWT.MouseDoubleClick, this::onIconCanvasMouseDoubleClick);
		iconCanvas.addListener(SWT.MouseWheel, this::onIconCanvasMouseWheel);
		ScrollBar iconVBar = iconCanvas.getVerticalBar();
		if (iconVBar != null) {
			iconVBar.addListener(SWT.Selection, e -> {
				iconScrollOffset = iconVBar.getSelection();
				loadingIcons = false;
				iconCanvas.redraw();
			});
		}
		iconCanvas.addListener(SWT.MouseMove, e -> {
			int cols = iconGridCols;
			if (cols <= 0) return;
			int totalCellW = listPadding <= 2 ? iconCanvas.getClientArea().width / cols : iconGridIconSize + listPadding;
			if (totalCellW <= 0) return;
			int col = e.x / totalCellW;
			if (col < 0 || col >= cols) { hoveredRowIdx = -1; iconCanvas.redraw(); return; }
			int row = iconCanvasYToRow(e.y);
			int idx = row >= 0 ? row * cols + col : -1;
			if (idx < 0 || idx >= favorites.size()) idx = -1;
			if (idx != hoveredRowIdx) {
				hoveredRowIdx = idx;
				iconCanvas.redraw();
			}
		});
		iconCanvas.addListener(SWT.MouseExit, e -> {
			if (hoveredRowIdx >= 0) {
				hoveredRowIdx = -1;
				iconCanvas.redraw();
			}
		});
		iconCanvas.addListener(SWT.Resize, e -> {
			Rectangle _ca = iconCanvas.getClientArea();
			System.out.println("[DBG] RESIZE: ca=(" + _ca.x + "," + _ca.y + "," + _ca.width + "," + _ca.height + ") favSize=" + favorites.size() + " cols=" + iconGridCols + " offset=" + iconScrollOffset + " iconSize=" + iconGridIconSize);
			if (_ca.width <= 0) return;
			int oldCols = iconGridCols;
			int cellW = iconGridIconSize + listPadding;
			iconGridCols = Math.max(1, _ca.width / cellW);
			if (listPadding <= 2) {
				iconGridCols = Math.max(1, _ca.width / cellW) + 1;
			}
			iconTotalHeight = 0; // invalidate until next paint
			int oldOff = iconScrollOffset;
			int rowH = getIconRowHeight();
			if (rowH > 0) {
				int totalH = ((favorites.size() + iconGridCols - 1) / iconGridCols) * rowH;
				int maxOff = Math.max(0, totalH - _ca.height);
				iconScrollOffset = Math.max(0, Math.min(maxOff, iconScrollOffset));
			}
			System.out.println("[DBG] RESIZE: computed cols " + oldCols + "->" + iconGridCols + " offset " + oldOff + "->" + iconScrollOffset + " calling redraw");
			if (iconCanvas != null && !iconCanvas.isDisposed()) {
				updateIconScrollBar();
				iconCanvas.redraw();
			}
		});
		iconCanvas.addListener(SWT.KeyDown, e -> {
			if (e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_UP) {
				e.doit = false;
			}
		});

		boolean viewIsIcons = "icons".equals(Settings.favoritesViewMode);
		stackLayout.topControl = viewIsIcons ? iconCanvas : tableBorderComp;

		listTable.addListener(SWT.SetData, event -> {
			TableItem item = (TableItem) event.item;
			int idx = favorites.size() > event.index ? event.index : -1;
			if (idx >= 0) {
				File jar = favorites.get(idx);
				MidletInfo info = metadataCache.get(jar);
				item.setText(0, "");
				item.setText(1, "");
				if (info != null) {
					item.setText(2, "");
					Image icon = getMidletIcon(jar, info);
					if (icon != null) {
						Image tableIcon = (icon == fallbackIcon) ? new Image(display, icon.getImageData()) : icon;
						item.setImage(1, tableIcon);
						if (info.iconImage != null && info.iconImage != tableIcon && !info.iconImage.isDisposed()) {
							info.iconImage.dispose();
						}
						info.iconImage = tableIcon;
					}
				} else {
					item.setText(2, "");
				}
				item.setData(jar);
			}
		});

		listTable.addListener(SWT.PaintItem, event -> {
			TableItem item = (TableItem) event.item;
			int idx = listTable.indexOf(item);
			if (idx < 0 || idx >= favorites.size()) return;
			GC gc = event.gc;

			int selIdx = listTable.getSelectionIndex();

			if (glowActive) {
				Rectangle glowRect = null;
				if (idx == selIdx && (event.index == 0 || event.index == 1 || event.index == 2 || event.index == 3)) {
					Rectangle cell = event.index == 0 ? gc.getClipping() : item.getBounds(event.index);
					if (cell != null) glowRect = cell;
				}
				if (glowRect != null) {
					long elapsed = System.currentTimeMillis() - glowStartTime;
					double phase = (elapsed % 2000) / 1000.0 * Math.PI;
					int r, g;
					if (darkMode) {
						r = 200;
						g = 60 + (int)((Math.sin(phase) + 1) / 2 * 120);
					} else {
						r = 255;
						g = 100 + (int)((Math.sin(phase) + 1) / 2 * 155);
					}
					Color glowColor = new Color(display, r, g, 0);
					gc.setBackground(glowColor);
					gc.fillRectangle(glowRect);
					glowColor.dispose();
				}
			}

			if (event.index == 1 && glowActive && idx == selIdx) {
				File jar = favorites.get(idx);
				MidletInfo info = metadataCache.get(jar);
				Image icon = info != null ? info.iconImage : null;
				if (icon != null && !icon.isDisposed()) {
					Rectangle cell = item.getBounds(1);
					int ix = cell.x + (cell.width - currentIconSize) / 2;
					int iy = cell.y + (cell.height - currentIconSize) / 2;
					gc.drawImage(icon, ix, iy);
				}
			}

			if (event.index == 2) {
				File jar = favorites.get(idx);
				MidletInfo info = metadataCache.get(jar);
				event.doit = false;

				boolean compact = "compact".equals(Settings.favoritesViewMode);
				int rightX = event.x + textCol.getWidth() - 4;
				String name = info != null && info.name != null ? info.name : jar.getName().replace(".jar", "");
				int nameH = gc.textExtent(name).y;

				if (compact) {
					int x = event.x + 5;
					int y = event.y + (nameH + 8 - nameH) / 2;
					int mask = Settings.favoritesCompactMask;

					Font boldFont = new Font(display, listFont.getFontData()[0].getName(), listFont.getFontData()[0].getHeight(), SWT.BOLD);
					gc.setFont(boldFont);
				gc.setForeground(fg());
				gc.drawText(name, x, y, true);
				x += gc.textExtent(name).x + 4;

					if (info != null && (mask & 1) != 0 && info.vendor != null && !info.vendor.isEmpty()) {
						gc.drawText(" | ", x, y, true);
						x += gc.textExtent(" | ").x + 4;
						gc.setFont(listFont);
						gc.drawText(info.vendor, x, y, true);
						x += gc.textExtent(info.vendor).x + 4;
						gc.setFont(boldFont);
					}
					if (info != null && (mask & 2) != 0 && info.version != null && !info.version.isEmpty()) {
						gc.drawText(" | ", x, y, true);
						x += gc.textExtent(" | ").x + 4;
						gc.setFont(listFont);
						String vs = "v" + info.version;
						gc.drawText(vs, x, y, true);
						x += gc.textExtent(vs).x + 4;
						gc.setFont(boldFont);
					}
					if ((mask & 4) != 0) {
						boolean isBuildDate = info != null && info.buildDate > 0;
						long dateMs = isBuildDate ? info.buildDate : jar.lastModified();
						String dateStr = dateMs > 0 ? formatDate(dateMs) : "";
						if (!dateStr.isEmpty()) {
							gc.drawText(" | ", x, y, true);
							x += gc.textExtent(" | ").x + 4;
							gc.setFont(listFont);
							gc.drawText(dateStr, x, y, true);
							x += gc.textExtent(dateStr).x + 4;
							gc.setFont(boldFont);
						}
					}
					if ((mask & 8) != 0) {
						long len = jar.length();
						String sizeStr = len > 0 ? formatSize(len) : "";
						if (!sizeStr.isEmpty()) {
							gc.setFont(listFont);
							int sw = gc.textExtent(sizeStr).x;
							gc.drawText(sizeStr, rightX - sw, y, true);
						}
					}
					gc.setFont(listFont);
					boldFont.dispose();
				} else {
					boolean isBuildDate = info != null && info.buildDate > 0;
					long dateMs = isBuildDate ? info.buildDate : jar.lastModified();
					String dateStr = dateMs > 0 ? formatDate(dateMs) : "";
					long len = jar.length();
					String sizeStr = len > 0 ? formatSize(len) : "";

					gc.setFont(listFont);
					gc.setForeground(fg());
					gc.drawText(name, event.x + 5, event.y + 2, true);
					String sub = "";
					if (info != null) {
						if (info.vendor != null && !info.vendor.isEmpty()) sub += info.vendor;
						if (info.version != null && !info.version.isEmpty()) {
							if (!sub.isEmpty()) sub += "  |  ";
							sub += "v" + info.version;
						}
					}
					boolean selected = idx == selectedRowIdx;
					if (!sub.isEmpty()) {
						gc.setFont(subFont);
						gc.setForeground(selected ? fg() : subColor);
						gc.drawText(sub, event.x + 5, event.y + nameH + 4, true);
					}
					if (!dateStr.isEmpty()) {
						gc.setFont(listFont);
						gc.setForeground(isBuildDate ? buildColor : fg());
						int dw = gc.textExtent(dateStr).x;
						gc.drawText(dateStr, rightX - dw, event.y + 2, true);
					}
					if (!sizeStr.isEmpty()) {
						gc.setFont(subFont);
						gc.setForeground(selected ? fg() : subColor);
						int sw = gc.textExtent(sizeStr).x;
						gc.drawText(sizeStr, rightX - sw, event.y + nameH + 4, true);
					}
				}
			}
		});

		listTable.addListener(SWT.EraseItem, event -> {
			event.detail &= ~(SWT.FOCUSED);
			TableItem rowItem = (TableItem) event.item;
			int rowIdx = rowItem != null ? listTable.indexOf(rowItem) : -1;
			if ((event.detail & SWT.SELECTED) != 0) {
				int idx = selectedRowIdx;
				if (idx < 0) idx = listTable.getSelectionIndex();
				if (idx >= 0) {
					Color c = getAnimRowColor(idx);
					if (c == null) c = getSelFallbackColor();
					event.gc.setBackground(c);
					event.gc.fillRectangle(event.x, event.y, event.width, event.height);
					event.detail &= ~SWT.SELECTED;
				}
			} else if (rowIdx >= 0 && rowIdx == hoveredRowIdx && rowIdx != selectedRowIdx) {
				Color c = getHoverFallbackColor();
				event.gc.setBackground(c);
				event.gc.fillRectangle(event.x, event.y, event.width, event.height);
				event.detail &= ~SWT.SELECTED;
			}
		});

		listTable.addListener(SWT.Selection, event -> {
			if ("icons".equals(Settings.favoritesViewMode)) return;
			int tableRow = listTable.getSelectionIndex();
			selectedRowIdx = tableRow;
			if (selectedRowIdx >= 0 && selectedRowIdx < favorites.size()) {
				updateStatusAndInfo(selectedRowIdx);
			}
		});

		applyFontSize();

		listTable.addListener(SWT.MouseMove, event -> {
			TableItem item = listTable.getItem(new Point(event.x, event.y));
			int idx = item != null ? listTable.indexOf(item) : -1;
			if (idx != hoveredRowIdx) {
				int old = hoveredRowIdx;
				hoveredRowIdx = idx;
				if (old >= 0) listTable.redraw();
				if (idx >= 0) listTable.redraw();
			}
		});
		listTable.addListener(SWT.MouseExit, event -> {
			if (hoveredRowIdx >= 0) {
				int old = hoveredRowIdx;
				hoveredRowIdx = -1;
				listTable.redraw();
			}
		});

		listTable.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				TableItem item = listTable.getItem(new Point(e.x, e.y));
				if (item == null) return;
				int flatIdx = listTable.indexOf(item);
				if (flatIdx < 0 || flatIdx >= favorites.size()) return;
				if (e.button == 3) {
					openContainingFolder(favorites.get(flatIdx));
					selectedRowIdx = flatIdx;
					listTable.setSelection(flatIdx);
					startGlow();
				} else if (e.button == 1) {
					selectedRowIdx = flatIdx;
					listTable.setSelection(flatIdx);
					openSelected();
				}
			}
		});

		// Detect mouse move within shell → temporarily enable folder button in keyboard mode
		mouseMoveFilter = event -> {
			if (shell == null || shell.isDisposed() || !shell.isVisible()) return;
			if (!keyboardMode) return;
			Point cursor = display.getCursorLocation();
			Rectangle bounds = shell.getBounds();
			if (bounds.contains(cursor)) {
				if (folderButton != null && !folderButton.isDisposed()) {
					folderButton.setEnabled(true);
				}
				display.timerExec(-1, disableButtonTask);
				display.timerExec(100, disableButtonTask);
			}
		};
		display.addFilter(SWT.MouseMove, mouseMoveFilter);

		// Mouse click exits keyboard mode permanently
		display.addFilter(SWT.MouseDown, event -> {
			if (shell == null || shell.isDisposed() || !shell.isVisible()) return;
			restoreDefaultStatus();
			stopGlow();
			setKeyboardMode(false);
		});

		// Bottom bar — settings buttons centered, status label below
		firstBottomBtnIndex = toolbarButtons.size();

		bottomToolbar = new Composite(shell, SWT.NONE);
		bottomToolbar.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		GridLayout bottomGrid = new GridLayout(1, false);
		bottomGrid.marginWidth = 5;
		bottomGrid.marginHeight = 2;
		bottomToolbar.setLayout(bottomGrid);

		// Search bar — full width, above buttons
		searchBox = new Text(bottomToolbar, SWT.SEARCH | SWT.ICON_CANCEL | SWT.CENTER);
		searchBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		searchBox.setText("Search...");
		searchPlaceholder = true;
		searchBox.addListener(SWT.Modify, e -> {
			if (!searchPlaceholder) applySearchFilter();
		});
		searchBox.addListener(SWT.FocusIn, e -> {
			if (searchPlaceholder) {
				searchPlaceholder = false;
				searchBox.setText("");
				if (darkMode) {
					searchBox.setForeground(paintFg);
				} else {
					searchBox.setForeground(null);
				}
			}
		});
		searchBox.addListener(SWT.FocusOut, e -> {
			if (searchBox.getText().isEmpty()) {
				searchPlaceholder = true;
				searchBox.setText("Search...");
				searchBox.setForeground(searchPlaceholderColor);
			}
		});
		searchBox.addListener(SWT.DefaultSelection, e -> {
			if (!favorites.isEmpty()) {
				selectedRowIdx = 0;
				if ("icons".equals(Settings.favoritesViewMode)) {
					scrollIconToSelected();
					if (iconCanvas != null && !iconCanvas.isDisposed()) iconCanvas.redraw();
				} else if (listTable != null && !listTable.isDisposed()) {
					listTable.setSelection(0);
					listTable.showItem(listTable.getItem(0));
					listTable.forceFocus();
				}
			}
		});

		// Bottom buttons
		bottomBtns = new Composite(bottomToolbar, SWT.NONE);
		bottomBtns.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		RowLayout btnRow = new RowLayout(SWT.HORIZONTAL);
		btnRow.center = true;
		btnRow.spacing = 2;
		btnRow.marginLeft = 0;
		btnRow.marginRight = 0;
		btnRow.marginTop = 0;
		btnRow.marginBottom = 0;
		bottomBtns.setLayout(btnRow);

		boolean compact = "compact".equals(Settings.favoritesViewMode);
		boolean icons = "icons".equals(Settings.favoritesViewMode);
		// +UI/-UI change fontSize only (buttons + status labels); textFontSize is untouched so the list keeps its independent size
		toolbarButtons.add(createToolButton(bottomBtns, "+UI", "Increase UI font size", () -> { fontSize = Math.min(fontSize + 2, 36); applyFontSize(); showTempStatus("User Interface Size: " + fontSize); }));
		toolbarButtons.add(createToolButton(bottomBtns, "-UI", "Decrease UI font size", () -> { fontSize = Math.max(fontSize - 2, 8); applyFontSize(); showTempStatus("User Interface Size: " + fontSize); }));
		// T+/T- change textFontSize only (list items); buttons + status labels are unaffected
		toolbarButtons.add(createToolButton(bottomBtns, "T+", "Increase text font size", () -> { textFontSize = Math.min(textFontSize + 2, 36); applyTextFontSize(); showTempStatus("Text Size: " + textFontSize); }));
		toolbarButtons.add(createToolButton(bottomBtns, "T-", "Decrease text font size", () -> { textFontSize = Math.max(textFontSize - 2, 8); applyTextFontSize(); showTempStatus("Text Size: " + textFontSize); }));
		toolbarButtons.add(createToolButton(bottomBtns, "P+", "Increase padding", () -> { listPadding = Math.min(listPadding + 2, 40); applyListPadding(); showTempStatus("Padding Size: " + listPadding); }));
		toolbarButtons.add(createToolButton(bottomBtns, "P-", "Decrease padding", () -> { listPadding = Math.max(listPadding - 2, 0); applyListPadding(); showTempStatus("Padding Size: " + listPadding); }));
		viewToggleBtn = createToolButton(bottomBtns, icons ? "Icons" : compact ? "Compact" : "Detailed", "Toggle view mode", () -> { toggleViewMode(); String v = Settings.favoritesViewMode; showTempStatus("View: " + v.substring(0, 1).toUpperCase() + v.substring(1)); });
		toolbarButtons.add(viewToggleBtn);
		detailsInfoBtn = createToolButton(bottomBtns, "Detailed Info", "Toggle detail sections in Compact/Icons mode", () -> toggleDetailMask());
		detailsInfoBtn.setEnabled(compact || icons);
		toolbarButtons.add(detailsInfoBtn);
		namesToggleBtn = createToolButton(bottomBtns, "Names", "Show/hide names below icons in Icons mode", () -> { toggleIconsNames(); showTempStatus("Names: " + (Settings.favoritesIconsShowNames ? "ON" : "OFF")); });
		namesToggleBtn.setEnabled(icons);
		toolbarButtons.add(namesToggleBtn);
		iconZoomInBtn = createToolButton(bottomBtns, "+I", "Increase icon size in Icons mode", () -> { if ("icons".equals(Settings.favoritesViewMode) && iconGridIconSize < 196) { iconGridIconSize = Math.min(iconGridIconSize + 8, 196); applyFontSize(); showTempStatus("Icons Size: " + iconGridIconSize + "px"); } });
		toolbarButtons.add(iconZoomInBtn);
		iconZoomOutBtn = createToolButton(bottomBtns, "-I", "Decrease icon size in Icons mode", () -> { if ("icons".equals(Settings.favoritesViewMode) && iconGridIconSize > 32) { iconGridIconSize = Math.max(iconGridIconSize - 8, 32); applyFontSize(); showTempStatus("Icons Size: " + iconGridIconSize + "px"); } });
		toolbarButtons.add(iconZoomOutBtn);
		iconZoomInBtn.setEnabled(icons);
		iconZoomOutBtn.setEnabled(icons);
		Button fsBtn = createToolButton(bottomBtns, "Fullscreen", null, () -> { shell.setFullScreen(!shell.getFullScreen()); });
		fsBtn.setEnabled(!fullscreen); // Performance Scene — gray out if opened from fullscreen
		toolbarButtons.add(fsBtn);
		fullscreenBtnIndex = toolbarButtons.size() - 1;
		themeToggleBtn = createToolButton(bottomBtns, darkMode ? "Light" : "Dark", "Toggle dark/light theme", () -> toggleTheme());
		toolbarButtons.add(themeToggleBtn);
		Button closeBtn = createToolButton(bottomBtns, "Close", null, () -> shell.close());
		toolbarButtons.add(closeBtn);

		statusLabel = new Label(bottomToolbar, SWT.CENTER);
		statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		applyFontSizeAll();
		applyTheme();

		// Ensure favorites dir exists
		new File(favoritesDir).mkdirs();

		executePendingMoves();

		if (!fullscreen) {
			Rectangle parentBounds = parent.getBounds();
			Rectangle shellBounds = shell.getBounds();
			shell.setLocation(
				parentBounds.x + (parentBounds.width - shellBounds.width) / 2,
				parentBounds.y + (parentBounds.height - shellBounds.height) / 2
			);
		}

		final boolean restoreFullscreen = wasFullscreen;
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (listFont != null && !listFont.isDisposed()) {
					listFont.dispose();
				}
				if (btnFont != null && !btnFont.isDisposed()) {
					btnFont.dispose();
				}
				if (subFont != null && !subFont.isDisposed()) {
					subFont.dispose();
				}
			if (subColor != null && !subColor.isDisposed()) {
				subColor.dispose();
			}
			if (buildColor != null && !buildColor.isDisposed()) {
				buildColor.dispose();
			}
			if (selFallbackColor != null && !selFallbackColor.isDisposed()) {
				selFallbackColor.dispose();
			}
			if (paintBg != null && !paintBg.isDisposed()) paintBg.dispose();
			if (paintFg != null && !paintFg.isDisposed()) paintFg.dispose();
			if (paintWidgetBg != null && !paintWidgetBg.isDisposed()) paintWidgetBg.dispose();
			if (darkShellBg != null && !darkShellBg.isDisposed()) darkShellBg.dispose();
			if (darkButtonBg != null && !darkButtonBg.isDisposed()) darkButtonBg.dispose();
			if (darkFg != null && !darkFg.isDisposed()) darkFg.dispose();
			if (tableBorderBg != null && !tableBorderBg.isDisposed()) tableBorderBg.dispose();
			if (searchPlaceholderColor != null && !searchPlaceholderColor.isDisposed()) {
				searchPlaceholderColor.dispose();
			}
				if (navFilter != null) {
					display.removeFilter(SWT.KeyDown, navFilter);
				}
				if (traverseFilter != null) {
					display.removeFilter(SWT.Traverse, traverseFilter);
				}
				if (keyUpFilter != null) {
					display.removeFilter(SWT.KeyUp, keyUpFilter);
				}
			stopScrollRepeat();
			if (mouseMoveFilter != null) {
					display.removeFilter(SWT.MouseMove, mouseMoveFilter);
				}
				if (rowOverlayShell != null && !rowOverlayShell.isDisposed()) {
					rowOverlayShell.close();
					rowOverlayShell = null;
				}
				if (loadingToastShell != null && !loadingToastShell.isDisposed()) {
					loadingToastShell.close();
					loadingToastShell = null;
				}
				display.timerExec(-1, disableButtonTask);
				for (MidletInfo mi : metadataCache.values()) {
					if (mi.iconImage != null && !mi.iconImage.isDisposed()) {
						mi.iconImage.dispose();
					}
				}
				if (fallbackIcon != null && !fallbackIcon.isDisposed()) {
					fallbackIcon.dispose();
					fallbackIcon = null;
				}
				rowHueCache.clear();
				iconPaletteCache.clear();
				for (Color c : animColorCache.values()) {
					if (c != null && !c.isDisposed()) c.dispose();
				}
				animColorCache.clear();
				// Performance Scene — restore fullscreen on parent without destroying/recreating it
			if (restoreFullscreen && parentShell != null && !parentShell.isDisposed()) {
					EmulatorScreen.fullscreen = true;
					parentShell.setFullScreen(true);
				}
			((emulator.ui.swt.Property) Emulator.getEmulator().getProperty()).saveProperties();
			}
		});

		scheduleHueAnim();

		// Dispose palette and anim caches on close
		navFilter = event -> {
			if (shell == null || shell.isDisposed() || !shell.isVisible()) return;
			Control focus = display.getFocusControl();
			if (focus != null) {
				Shell fs = focus.getShell();
				if (fs != shell && fs != rowOverlayShell) return;
			} else {
				Shell active = display.getActiveShell();
				if (active == null || (active != shell && active != rowOverlayShell)) return;
			}
			KeyEvent ke = new KeyEvent(event);
			handleNavKey(ke);
			if (!ke.doit) {
				event.doit = false;
			}
		};
		display.addFilter(SWT.KeyDown, navFilter);

		traverseFilter = event -> {
			if (shell == null || shell.isDisposed() || !shell.isVisible()) return;
			Control focus = display.getFocusControl();
			if (focus != null) {
				Shell fs = focus.getShell();
				if (fs != shell && fs != rowOverlayShell) return;
			} else {
				Shell active = display.getActiveShell();
				if (active == null || (active != shell && active != rowOverlayShell)) return;
			}
			if (event.detail == SWT.TRAVERSE_TAB_NEXT || event.detail == SWT.TRAVERSE_TAB_PREVIOUS
					|| event.detail == SWT.TRAVERSE_ARROW_NEXT || event.detail == SWT.TRAVERSE_ARROW_PREVIOUS) {
				event.doit = false;
			}
		};
		display.addFilter(SWT.Traverse, traverseFilter);

		keyUpFilter = event -> {
			if (shell == null || shell.isDisposed() || !shell.isVisible()) return;
			Control focus = display.getFocusControl();
			if (focus != null) {
				Shell fs = focus.getShell();
				if (fs != shell && fs != rowOverlayShell) return;
			} else {
				Shell active = display.getActiveShell();
				if (active == null || (active != shell && active != rowOverlayShell)) return;
			}
			if (heldScrollKey != 0) {
				KeyEvent ke = new KeyEvent(event);
				if (isDownKey(ke) || isUpKey(ke)) {
					stopScrollRepeat();
				}
			}
			if (heldBtnKey != 0) {
				KeyEvent ke = new KeyEvent(event);
				if (isLeftKey(ke) || isRightKey(ke) || matchNavKey(ke, HotkeyManager.UI_SWITCH_LEFT) || matchNavKey(ke, HotkeyManager.UI_SWITCH_RIGHT)) {
					heldBtnKey = 0;
					btnRepeatGen++;
				}
			}
			if (heldConfirmBtn != null) {
				KeyEvent ke = new KeyEvent(event);
				if (isConfirmKey(ke)) {
					Button btn = heldConfirmBtn;
					heldConfirmBtn = null;
					long[] st = (long[]) btn.getData("holdStartTime");
					boolean[] trig = (boolean[]) btn.getData("holdTriggered");
					if (st != null) st[0] = -1;
					if (!btn.isDisposed()) btn.setForeground(null);
					if (trig == null || !trig[0]) {
						Runnable sc = (Runnable) btn.getData("shortClick");
						if (sc != null) sc.run();
					}
				}
			}
		};
		display.addFilter(SWT.KeyUp, keyUpFilter);

		// Performance Scene — open child as fullscreen overlay, no parent state change
		showLoadingToast();
		refreshList();
		hideLoadingToast();

		shell.open();
		shell.layout(true, true);

		if (fullscreen) { // Performance Scene — open directly maximized
			shell.setFullScreen(true);
			shell.setMaximized(true);
		}

		Button fullBtn = toolbarButtons.get(fullscreenBtnIndex);
		focusTarget = fullscreenBtnIndex;
		display.asyncExec(() -> {
			if (fullBtn != null && !fullBtn.isDisposed()) {
				fullBtn.setFocus();
				shell.forceActive();
			}
		});
		grabFocus();

		if (favorites.size() > 0) {
			if ("icons".equals(Settings.favoritesViewMode)) {
				System.out.println("[DBG] open(): icon mode final redraw+update");
				selectedRowIdx = 0;
				if (iconCanvas != null && !iconCanvas.isDisposed()) {
					iconCanvas.redraw();
					iconCanvas.update();
				}
			} else {
				if (listTable.getSelectionIndex() < 0) {
					listTable.setSelection(0);
					selectedRowIdx = 0;
				} else {
					selectedRowIdx = listTable.getSelectionIndex();
				}
			}
		}
	}

	private void loadJarDirect(File jar) {
		if (jar == null || !jar.isFile()) return;
		String path = jar.getAbsolutePath();
		shell.close();
		final String loadPath = path;
		new Thread("JAR-Direct") {
			public void run() {
				Emulator.loadGame(loadPath, false);
			}
		}.start();
	}

	private void setupLongPressButton(Button btn, Runnable shortClick, Runnable longPress) {
		final long holdMs = 800;
		btn.setData("shortClick", shortClick);
		btn.setData("longPress", longPress);
		final boolean[] triggered = {false};
		final long[] startTime = {0};
		btn.setData("holdStartTime", startTime);
		btn.setData("holdTriggered", triggered);
		final Color defaultFg = btn.getForeground();
		final int dr = defaultFg != null ? defaultFg.getRed() : 200;
		final int dg = defaultFg != null ? defaultFg.getGreen() : 200;
		final int db = defaultFg != null ? defaultFg.getBlue() : 200;
		Runnable animLoop = new Runnable() {
			public void run() {
				long st = startTime[0];
				if (st < 0 || btn.isDisposed() || !btn.isEnabled()) return;
				long elapsed = System.currentTimeMillis() - st;
				if (elapsed >= holdMs) {
					triggered[0] = true;
					if (!btn.isDisposed()) btn.setForeground(null);
					heldConfirmBtn = null;
					Runnable lp = (Runnable) btn.getData("longPress");
					if (lp != null) lp.run();
					return;
				}
				float t = (float)elapsed / holdMs;
				int r = Math.max(0, (int)(dr * (1 - t)));
				int g2 = Math.max(0, Math.min(255, (int)(dg * (1 - t) + 180 * t)));
				int b = Math.max(0, (int)(db * (1 - t)));
				Color c = new Color(display, r, g2, b);
				if (!btn.isDisposed()) btn.setForeground(c);
				c.dispose();
				display.timerExec(30, this);
			}
		};
		btn.setData("holdAnim", animLoop);
		btn.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {}
			public void mouseDown(MouseEvent e) {
				if (!btn.isEnabled()) return;
				triggered[0] = false;
				startTime[0] = System.currentTimeMillis();
				display.timerExec(30, animLoop);
			}
			public void mouseUp(MouseEvent e) {
				startTime[0] = -1;
				if (!btn.isDisposed()) btn.setForeground(null);
			}
		});
		btn.addListener(SWT.Traverse, e -> {
			if (e.detail == SWT.TRAVERSE_ARROW_PREVIOUS || e.detail == SWT.TRAVERSE_ARROW_NEXT
					|| e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS)
				e.doit = false;
		});
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (triggered[0]) {
					triggered[0] = false;
					return;
				}
				shortClick.run();
			}
		});
	}

	private Button createToolButton(Composite parent, String text, String tooltip, Runnable action) {
		Button btn = new Button(parent, SWT.FLAT | SWT.PUSH);
		btn.setText(text);
		if (tooltip != null) btn.setToolTipText(tooltip);
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				action.run();
			}
		});
		btn.addListener(SWT.Traverse, e -> {
			if (e.detail == SWT.TRAVERSE_ARROW_PREVIOUS || e.detail == SWT.TRAVERSE_ARROW_NEXT) {
				e.doit = false;
			}
		});
		return btn;
	}

	private Color getAnimRowColor(int idx) {
		if (idx < 0 || idx >= favorites.size()) return null;
		File jar = favorites.get(idx);
		int[] palette = iconPaletteCache.get(jar);
		if (palette == null) {
			palette = getIconPalette(jar);
		}
		if (palette == null || palette.length < 3) {
				Float baseHue = rowHueCache.get(jar);
				if (baseHue == null) {
					baseHue = cacheBaseHue(jar);
					if (baseHue == null) return null;
				}
				float hue = baseHue + (float)huePhaseOffset;
				hue = hue - (float)Math.floor(hue);
				float s = 0.9f, l = 0.82f;
				float q = l < 0.5f ? l * (1f + s) : l + s - l * s;
				float p = 2f * l - q;
				float r2 = hue + 1f/3f; if (r2 > 1f) r2 -= 1f;
				float g2 = hue;
				float b2 = hue - 1f/3f; if (b2 < 0f) b2 += 1f;
				int r = Math.round(hue2rgb(p, q, r2) * 255f);
				int g = Math.round(hue2rgb(p, q, g2) * 255f);
				int b = Math.round(hue2rgb(p, q, b2) * 255f);
				if (darkMode) {
					r = Math.round(r * 0.6f);
					g = Math.round(g * 0.6f);
					b = Math.round(b * 0.6f);
				}
				return cacheAnimColor(jar, r, g, b);
			}
		int numColors = palette.length / 3;
		float phase = (float)huePhaseOffset * numColors;
		int idx1 = (int)phase % numColors;
		int idx2 = (idx1 + 1) % numColors;
		float frac = phase - (int)phase;
		int r1 = palette[idx1 * 3], g1 = palette[idx1 * 3 + 1], b1 = palette[idx1 * 3 + 2];
		int r2 = palette[idx2 * 3], g2 = palette[idx2 * 3 + 1], b2 = palette[idx2 * 3 + 2];
		float l = 0.50f + (float)Math.sin(huePhaseOffset * Math.PI * 2) * 0.15f;
		r1 = r1 + Math.round((255 - r1) * l);
		g1 = g1 + Math.round((255 - g1) * l);
		b1 = b1 + Math.round((255 - b1) * l);
		r2 = r2 + Math.round((255 - r2) * l);
		g2 = g2 + Math.round((255 - g2) * l);
		b2 = b2 + Math.round((255 - b2) * l);
		int r = Math.round(r1 + (r2 - r1) * frac);
		int g = Math.round(g1 + (g2 - g1) * frac);
		int b = Math.round(b1 + (b2 - b1) * frac);
		if (darkMode) {
			r = Math.round(r * 0.6f);
			g = Math.round(g * 0.6f);
			b = Math.round(b * 0.6f);
		}
		int max = Math.max(r, Math.max(g, b));
		int minBright = darkMode ? 40 : 153;
		if (max < minBright && max > 0) {
			float scale = (float)minBright / max;
			r = Math.min(255, Math.round(r * scale));
			g = Math.min(255, Math.round(g * scale));
			b = Math.min(255, Math.round(b * scale));
		}
		return cacheAnimColor(jar, r, g, b);
	}

	private Color cacheAnimColor(File jar, int r, int g, int b) {
		Color old = animColorCache.get(jar);
		if (old != null && !old.isDisposed()) old.dispose();
		Color c = new Color(display, r, g, b);
		animColorCache.put(jar, c);
		return c;
	}

	// Reads the Windows accent color (via DwmGetColorizationColor from dwmapi.dll)
	// and brightens it for use as the selection bar on items with no icon palette.
	// Falls back to COLOR_LIST_SELECTION if the API is unavailable.
	private Color getSelFallbackColor() {
		if (selFallbackColor == null || selFallbackColor.isDisposed()) {
			int r, g, b;
			try {
				NativeLibrary dwmapi = NativeLibrary.getInstance("dwmapi");
				Function func = dwmapi.getFunction("DwmGetColorizationColor");
				IntByReference colorRef = new IntByReference();
				IntByReference opaqueRef = new IntByReference();
				func.invoke(int.class, new Object[] { colorRef, opaqueRef });
				int argb = colorRef.getValue();
				r = (argb >> 16) & 0xFF;
				g = (argb >> 8) & 0xFF;
				b = argb & 0xFF;
			} catch (Throwable t) {
				Color sys = display.getSystemColor(SWT.COLOR_LIST_SELECTION);
				r = sys.getRed();
				g = sys.getGreen();
				b = sys.getBlue();
			}
			if (darkMode) {
				r = r + (255 - r) / 3;
				g = g + (255 - g) / 3;
				b = b + (255 - b) / 3;
			} else {
				r = r + (255 - r) * 2 / 3;
				g = g + (255 - g) * 2 / 3;
				b = b + (255 - b) * 2 / 3;
			}
			selFallbackColor = new Color(display, r, g, b);
		}
		return selFallbackColor;
	}

	private Color getHoverFallbackColor() {
		if (hoverFallbackColor == null || hoverFallbackColor.isDisposed()) {
			int r, g, b;
			try {
				NativeLibrary dwmapi = NativeLibrary.getInstance("dwmapi");
				Function func = dwmapi.getFunction("DwmGetColorizationColor");
				IntByReference colorRef = new IntByReference();
				IntByReference opaqueRef = new IntByReference();
				func.invoke(int.class, new Object[] { colorRef, opaqueRef });
				int argb = colorRef.getValue();
				r = (argb >> 16) & 0xFF;
				g = (argb >> 8) & 0xFF;
				b = argb & 0xFF;
			} catch (Throwable t) {
				r = 0; g = 100; b = 200;
			}
			if (darkMode) {
				r = 30 + r / 6;
				g = 30 + g / 6;
				b = 30 + b / 6;
			} else {
				r = 220 + (r * 35) / 255;
				g = 220 + (g * 35) / 255;
				b = 220 + (b * 35) / 255;
			}
			hoverFallbackColor = new Color(display,
				Math.min(255, r), Math.min(255, g), Math.min(255, b));
		}
		return hoverFallbackColor;
	}

	private static final int GWL_EXSTYLE = -20;
	private static final int WS_EX_CLIENTEDGE = 0x00000200;
	private static final int WS_EX_STATICEDGE = 0x00020000;

	private void removeNativeEdge(Control c) {
		if (c == null || c.isDisposed()) return;
		try {
			long hwnd = c.handle;
			NativeLibrary user32 = NativeLibrary.getInstance("user32");
			Function getStyle = user32.getFunction("GetWindowLongPtrW");
			Function setStyle = user32.getFunction("SetWindowLongPtrW");

			long exStyle = ((Number) getStyle.invoke(Long.class,
				new Object[]{ hwnd, GWL_EXSTYLE })).longValue();
			exStyle &= ~(WS_EX_CLIENTEDGE | WS_EX_STATICEDGE);
			setStyle.invoke(Long.class, new Object[]{ hwnd, GWL_EXSTYLE, exStyle });

			Function setWindowPos = user32.getFunction("SetWindowPos");
			int SWP_NOMOVE = 0x2, SWP_NOSIZE = 0x1, SWP_NOZORDER = 0x4, SWP_FRAMECHANGED = 0x20;
			setWindowPos.invoke(int.class, new Object[]{
				hwnd, 0L, 0, 0, 0, 0,
				SWP_NOMOVE | SWP_NOSIZE | SWP_NOZORDER | SWP_FRAMECHANGED
			});
		} catch (Throwable t) {
			// Not Win32, or JNA function-mapping failed — leave the OS edge in place
		}
	}

	private void setButtonWinTheme(Button btn, boolean dark) {
		if (btn == null || btn.isDisposed()) return;
		try {
			NativeLibrary uxtheme = NativeLibrary.getInstance("uxtheme");
			Function func = uxtheme.getFunction("SetWindowTheme");
			long hwnd = btn.handle;
			func.invoke(int.class, new Object[]{hwnd, dark ? "" : null, dark ? "" : null});
		} catch (Throwable t) {
			// uxtheme not available
		}
	}

	private int[] getIconPalette(File jar) {
		int[] cached = iconPaletteCache.get(jar);
		if (cached != null) return cached.length > 0 ? cached : null;
		MidletInfo info = metadataCache.get(jar);
		if (info == null || info.iconImage == null || info.iconImage.isDisposed()) {
			iconPaletteCache.put(jar, new int[0]);
			return null;
		}
		ImageData data = info.iconImage.getImageData();
		if (data == null) { iconPaletteCache.put(jar, new int[0]); return null; }
		try {
			boolean direct = data.palette != null && data.palette.isDirect;
			RGB[] colors = data.palette != null ? data.palette.getRGBs() : null;
			java.util.Map<Integer, Integer> freq = new java.util.HashMap<>();
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					int alpha = data.getAlpha(x, y);
					if (alpha > 0) {
						int rr, gg, bb;
						if (direct) {
							RGB rgb = data.palette.getRGB(pixel);
							rr = rgb.red; gg = rgb.green; bb = rgb.blue;
						} else if (colors != null && pixel < colors.length) {
							rr = colors[pixel].red;
							gg = colors[pixel].green;
							bb = colors[pixel].blue;
						} else {
							continue;
						}
						int qr = (rr / 16) * 16, qg = (gg / 16) * 16, qb = (bb / 16) * 16;
						int key = (qr << 16) | (qg << 8) | qb;
						freq.put(key, freq.getOrDefault(key, 0) + 1);
					}
				}
			}
			if (freq.isEmpty()) { iconPaletteCache.put(jar, new int[0]); return null; }
			java.util.List<Integer> sorted = new java.util.ArrayList<>(freq.keySet());
			sorted.sort((a, b2) -> freq.get(b2) - freq.get(a));
			int take = Math.min(12, sorted.size());
			int[] pal = new int[take * 3];
			for (int i = 0; i < take; i++) {
				int key = sorted.get(i);
				pal[i * 3] = (key >> 16) & 0xFF;
				pal[i * 3 + 1] = (key >> 8) & 0xFF;
				pal[i * 3 + 2] = key & 0xFF;
			}
			iconPaletteCache.put(jar, pal);
			return pal;
		} catch (Exception e) {
			iconPaletteCache.put(jar, new int[0]);
			return null;
		}
	}

	private Float cacheBaseHue(File jar) {
		Float cached = rowHueCache.get(jar);
		if (cached != null) return cached;
		MidletInfo info = metadataCache.get(jar);
		if (info == null || info.iconImage == null || info.iconImage.isDisposed()) {
			rowHueCache.put(jar, null);
			return null;
		}
		ImageData data = info.iconImage.getImageData();
		if (data == null) { rowHueCache.put(jar, null); return null; }
		try {
			long r = 0, g = 0, b = 0;
			int count = 0;
			boolean direct = data.palette != null && data.palette.isDirect;
			RGB[] colors = data.palette != null ? data.palette.getRGBs() : null;
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					int alpha = data.getAlpha(x, y);
					if (alpha > 0) {
						int rr, gg, bb;
						if (direct) {
							RGB rgb = data.palette.getRGB(pixel);
							rr = rgb.red;
							gg = rgb.green;
							bb = rgb.blue;
						} else if (colors != null && pixel < colors.length) {
							rr = colors[pixel].red;
							gg = colors[pixel].green;
							bb = colors[pixel].blue;
						} else {
							rr = 128; gg = 128; bb = 128;
						}
						r += rr; g += gg; b += bb;
						count++;
					}
				}
			}
			if (count == 0) { rowHueCache.put(jar, null); return null; }
			int avgR = (int)(r / count), avgG = (int)(g / count), avgB = (int)(b / count);
			float rf = avgR / 255f, gf = avgG / 255f, bf = avgB / 255f;
			float max = Math.max(rf, Math.max(gf, bf));
			float min = Math.min(rf, Math.min(gf, bf));
			float h;
			if (max == min) {
				h = 0;
			} else {
				float d = max - min;
				if (max == rf) h = (gf - bf) / d + (gf < bf ? 6f : 0);
				else if (max == gf) h = (bf - rf) / d + 2f;
				else h = (rf - gf) / d + 4f;
				h /= 6f;
			}
			rowHueCache.put(jar, h);
			return h;
		} catch (Exception e) {
			rowHueCache.put(jar, null);
			return null;
		}
	}

	private static float hue2rgb(float p, float q, float t) {
		if (t < 1f/6f) return p + (q - p) * 6f * t;
		if (t < 1f/2f) return q;
		if (t < 2f/3f) return p + (q - p) * (2f/3f - t) * 6f;
		return p;
	}

	private static boolean isDownKey(KeyEvent e) {
		return matchNavKey(e, HotkeyManager.UI_DOWN) || e.keyCode == SWT.ARROW_DOWN;
	}
	private static boolean isUpKey(KeyEvent e) {
		return matchNavKey(e, HotkeyManager.UI_UP) || e.keyCode == SWT.ARROW_UP;
	}
	private static boolean isLeftKey(KeyEvent e) {
		return matchNavKey(e, HotkeyManager.UI_LEFT) || e.keyCode == SWT.ARROW_LEFT;
	}
	private static boolean isRightKey(KeyEvent e) {
		return matchNavKey(e, HotkeyManager.UI_RIGHT) || e.keyCode == SWT.ARROW_RIGHT;
	}
	static boolean isConfirmKey(KeyEvent e) {
		return matchNavKey(e, HotkeyManager.UI_CONFIRM) || matchNavKey(e, HotkeyManager.UI_CONFIRM_2);
	}

	private void cancelHold() {
		restoreDefaultStatus();
		heldBtnKey = 0;
		btnRepeatGen++;
		if (heldConfirmBtn != null) {
			if (!heldConfirmBtn.isDisposed()) {
				long[] st = (long[]) heldConfirmBtn.getData("holdStartTime");
				if (st != null) st[0] = -1;
				heldConfirmBtn.setForeground(null);
			}
			heldConfirmBtn = null;
		}
	}

	private int restoreOrFirstFocus(int row) {
		int saved = row == 0 ? savedFocusRow0 : savedFocusRow1;
		if (saved >= 0 && saved < toolbarButtons.size() && rowOf(saved) == row && isBtnEnabled(saved)) return saved;
		return firstBtnIndex(row);
	}

	private void handleNavKey(KeyEvent e) {
		stopGlow();
		setKeyboardMode(true);
		if (matchNavKey(e, HotkeyManager.UI_SWITCH_LEFT)) {
			cancelHold();
			if (focusTarget < 0) {
				focusTarget = restoreOrFirstFocus(activeRow);
				focusTarget = prevBtnInRow(focusTarget);
			} else if (rowOf(focusTarget) == activeRow) {
				focusTarget = prevBtnInRow(focusTarget);
			} else {
				focusTarget = restoreOrFirstFocus(activeRow);
			}
			heldBtnKey = -1; btnRepeatGen++; startBtnRepeat(400);
			updateButtonFocus();
			e.doit = false;
		} else if (matchNavKey(e, HotkeyManager.UI_SWITCH_RIGHT)) {
			cancelHold();
			if (focusTarget < 0) {
				focusTarget = restoreOrFirstFocus(activeRow);
				focusTarget = nextBtnInRow(focusTarget);
			} else if (rowOf(focusTarget) == activeRow) {
				focusTarget = nextBtnInRow(focusTarget);
			} else {
				focusTarget = restoreOrFirstFocus(activeRow);
			}
			heldBtnKey = 1; btnRepeatGen++; startBtnRepeat(400);
			updateButtonFocus();
			e.doit = false;
		} else if (isLeftKey(e)) {
			cancelHold();
			if ("icons".equals(Settings.favoritesViewMode)) {
				int cols = iconGridCols;
				if (cols <= 0) cols = 1;
				int curRow = selectedRowIdx / cols;
				int curCol = selectedRowIdx - curRow * cols;
				int newCol = curCol > 0 ? curCol - 1 : cols - 1;
				int target = curRow * cols + newCol;
				if (target >= favorites.size()) target = curRow * cols;
				if (target != selectedRowIdx) {
					selectedRowIdx = target;
					scrollIconToSelected();
					iconCanvas.redraw();
					updateStatusAndInfo(selectedRowIdx);
				}
			} else {
				if (focusTarget < 0) {
					focusTarget = restoreOrFirstFocus(activeRow);
					focusTarget = prevBtnInRow(focusTarget);
				} else if (rowOf(focusTarget) == activeRow) {
					focusTarget = prevBtnInRow(focusTarget);
				} else {
					focusTarget = restoreOrFirstFocus(activeRow);
				}
				heldBtnKey = -1; btnRepeatGen++; startBtnRepeat(400);
				updateButtonFocus();
			}
			e.doit = false;
		} else if (isRightKey(e)) {
			cancelHold();
			if ("icons".equals(Settings.favoritesViewMode)) {
				int cols = iconGridCols;
				if (cols <= 0) cols = 1;
				int curRow = selectedRowIdx / cols;
				int curCol = selectedRowIdx - curRow * cols;
				int newCol = curCol < cols - 1 ? curCol + 1 : 0;
				int target = curRow * cols + newCol;
				if (target >= favorites.size()) target = curRow * cols;
				if (target != selectedRowIdx) {
					selectedRowIdx = target;
					scrollIconToSelected();
					iconCanvas.redraw();
					updateStatusAndInfo(selectedRowIdx);
				}
			} else {
				if (focusTarget < 0) {
					focusTarget = restoreOrFirstFocus(activeRow);
					focusTarget = nextBtnInRow(focusTarget);
				} else if (rowOf(focusTarget) == activeRow) {
					focusTarget = nextBtnInRow(focusTarget);
				} else {
					focusTarget = restoreOrFirstFocus(activeRow);
				}
				heldBtnKey = 1; btnRepeatGen++; startBtnRepeat(400);
				updateButtonFocus();
			}
			e.doit = false;
		} else if (isDownKey(e)) {
			handleScroll(+1);
			e.doit = false;
		} else if (isUpKey(e)) {
			handleScroll(-1);
			e.doit = false;
		} else if (matchNavKey(e, HotkeyManager.UI_CANCEL)) {
			cancelHold();
			// Determine which row the currently focused button is in
			Control fc = display.getFocusControl();
			int idx = -1;
			if (fc instanceof Button) {
				idx = toolbarButtons.indexOf(fc);
			}
			if (idx < 0 && focusTarget >= 0) {
				idx = focusTarget;
			}
			if (idx < 0 || rowOf(idx) != activeRow) {
				focusTarget = restoreOrFirstFocus(activeRow);
				updateButtonFocus();
			} else {
				activeRow = 1 - activeRow;
				focusTarget = firstBtnIndex(activeRow);
				showButtonRowOverlay(activeRow);
				updateButtonFocus();
			}
			e.doit = false;
		} else if (isConfirmKey(e)) {
			if (heldConfirmBtn != null) {
				if (heldConfirmBtn.isDisposed()) heldConfirmBtn = null;
				else { e.doit = false; return; }
			}
			if (focusTarget < 0) {
				openSelected();
			} else if (focusTarget < toolbarButtons.size()) {
				Button btn = toolbarButtons.get(focusTarget);
				if (btn != null && !btn.isDisposed()) {
					Runnable sc = (Runnable) btn.getData("shortClick");
					if (sc != null) {
						heldConfirmBtn = btn;
						heldConfirmStartTime = System.currentTimeMillis();
						long[] st = (long[]) btn.getData("holdStartTime");
						boolean[] trig = (boolean[]) btn.getData("holdTriggered");
						Runnable anim = (Runnable) btn.getData("holdAnim");
						if (st != null && trig != null) {
							trig[0] = false;
							st[0] = System.currentTimeMillis();
						}
						if (anim != null) {
							display.timerExec(30, anim);
						}
					} else {
						btn.notifyListeners(SWT.Selection, new Event());
					}
				}
			}
			e.doit = false;
		}
	}

	private int firstNonFolderButton() {
		for (int i = 0; i < toolbarButtons.size(); i++) {
			if (i != folderButtonIndex) return i;
		}
		return 0;
	}

	private void handleScroll(int dir) {
		restoreDefaultStatus();
		if (focusTarget >= 0) {
			if (rowOf(focusTarget) == 0) savedFocusRow0 = focusTarget;
			else savedFocusRow1 = focusTarget;
		}
		focusTarget = -1;
		updateButtonFocus();
		long now = System.currentTimeMillis();
		if (now - lastScrollTime < 50) return;
		lastScrollTime = now;
		if (heldScrollKey == 0) {
			scrollOne(dir);
			heldScrollKey = dir > 0 ? 1 : -1;
			scrollTier = 0;
			tierStartTime = now;
			scrollLingerEnd = 0;
			scrollGen++;
			scrollTimerActive = true;
			scheduleScrollRepeat(300, scrollGen);
		}
	}

	private void scheduleSelectionAnim(int gen) {
		display.timerExec(33, () -> {
			if (gen != selectionAnimGen) return;
			if (!glowActive) {
				boolean icons = "icons".equals(Settings.favoritesViewMode);
				if (icons && iconCanvas != null && !iconCanvas.isDisposed()) iconCanvas.redraw();
				else if (!icons && listTable != null && !listTable.isDisposed()) listTable.redraw();
				return;
			}
			boolean icons = "icons".equals(Settings.favoritesViewMode);
			if (icons && iconCanvas != null && !iconCanvas.isDisposed()) iconCanvas.redraw();
			else if (!icons && listTable != null && !listTable.isDisposed()) listTable.redraw();
			scheduleSelectionAnim(gen);
		});
	}

	private void scheduleHueAnim() {
		display.timerExec(50, () -> {
			if (shell == null || shell.isDisposed()) return;
			huePhaseOffset += 0.001786;
			if (huePhaseOffset > 1.0) huePhaseOffset -= 1.0;
			boolean icons = "icons".equals(Settings.favoritesViewMode);
			if (icons && iconCanvas != null && !iconCanvas.isDisposed()) iconCanvas.redraw();
			else if (!icons && listTable != null && !listTable.isDisposed()) listTable.redraw();
			scheduleHueAnim();
		});
	}

	private void scrollOne(int dir) {
		if ("icons".equals(Settings.favoritesViewMode)) {
			int count = favorites.size();
			if (count == 0) return;
			int cols = iconGridCols;
			if (cols <= 0) cols = 1;
			int rowH = getIconRowHeight();
			if (rowH <= 0) return;
			int viewH = iconCanvas.getClientArea().height;
			if (viewH <= 0) return;
			int curRow = count > 0 ? selectedRowIdx / cols : 0;
			int curCol = count > 0 ? selectedRowIdx - curRow * cols : 0;
			int lastRow = (count - 1) / cols;
			if (dir > 0) {
				int nextRow = curRow + 1;
				if (nextRow > lastRow) nextRow = 0;
				int target = nextRow * cols + Math.min(curCol, count - nextRow * cols - 1);
				selectedRowIdx = Math.max(0, Math.min(count - 1, target));
			} else {
				int prevRow = curRow - 1;
				if (prevRow < 0) prevRow = lastRow;
				int target = prevRow * cols + Math.min(curCol, count - prevRow * cols - 1);
				selectedRowIdx = Math.max(0, Math.min(count - 1, target));
			}
			scrollIconToSelected();
			loadingIcons = false;
			iconCanvas.redraw();
			updateStatusAndInfo(selectedRowIdx);
		} else {
			int sel = listTable.getSelectionIndex();
			int count = listTable.getItemCount();
			if (dir > 0) {
				sel = sel < count - 1 ? sel + 1 : 0;
				listTable.setSelection(sel);
			} else {
				sel = sel > 0 ? sel - 1 : count - 1;
				listTable.setSelection(sel);
			}
			selectedRowIdx = sel;
			listTable.showSelection();
			listTable.redraw();
			updateStatusAndInfo(selectedRowIdx);
		}
	}

	private void scrollIconToSelected() {
		if (iconCanvas == null || iconCanvas.isDisposed() || iconGridCols <= 0) return;
		int selRow = selectedRowIdx / iconGridCols;
		int viewH = iconCanvas.getClientArea().height;
		if (viewH <= 0) return;
		int rowH = getIconRowHeight();
		if (rowH <= 0) return;

		boolean hasCache = lastPaintRowY != null && lastPaintRowH != null;
		boolean inCache = hasCache && selRow >= lastPaintFirstRow && selRow < lastPaintLastRow;
		int selTop, selBottom;
		if (inCache) {
			int idx = selRow - lastPaintFirstRow;
			selTop = lastPaintRowY[idx] + iconScrollOffset;
			selBottom = lastPaintRowY[idx] + lastPaintRowH[idx] + iconScrollOffset;
		} else if (hasCache && selRow >= lastPaintLastRow && lastPaintRowY.length > 0) {
			int lastIdx = lastPaintRowY.length - 1;
			int cacheBottom = lastPaintRowY[lastIdx] + lastPaintRowH[lastIdx] + iconScrollOffset;
			int rowsAfter = selRow - lastPaintLastRow;
			selTop = cacheBottom + rowsAfter * rowH;
			selBottom = selTop + rowH;
		} else if (hasCache && selRow < lastPaintFirstRow) {
			selTop = selRow * rowH;
			selBottom = selTop + rowH;
		} else {
			selTop = selRow * rowH;
			selBottom = selTop + rowH;
			if (Settings.favoritesIconsShowNames) selBottom += selBottom / 2;
		}

		int totalH = iconTotalHeight > 0 ? iconTotalHeight : ((favorites.size() + iconGridCols - 1) / iconGridCols) * rowH;
		int maxOff = Math.max(0, totalH - viewH);

		if (selTop < iconScrollOffset) {
			iconScrollOffset = selTop;
		} else if (selBottom > iconScrollOffset + viewH) {
			iconScrollOffset = selBottom - viewH;
		}
		iconScrollOffset = Math.max(0, Math.min(maxOff, iconScrollOffset));
		loadingIcons = false;
		updateIconScrollBar();
	}

	private void updateIconScrollBar() {
		if (iconCanvas == null || iconCanvas.isDisposed()) return;
		ScrollBar vBar = iconCanvas.getVerticalBar();
		if (vBar == null) return;
		int rowH = getIconRowHeight();
		if (rowH <= 0) return;
		Rectangle ca = iconCanvas.getClientArea();
		int totalH = iconTotalHeight > 0 ? iconTotalHeight : ((favorites.size() + iconGridCols - 1) / iconGridCols) * rowH;
		int thumb = Math.max(1, ca.height);
		vBar.setMinimum(0);
		vBar.setMaximum(totalH);
		vBar.setThumb(thumb);
		vBar.setSelection(iconScrollOffset);
		vBar.setIncrement(rowH);
		vBar.setPageIncrement(ca.height);
	}

	private int getIconRowHeight() {
		int pad = listPadding;
		int iconSize = iconGridIconSize;
		int rowH = iconSize + pad;
		if (Settings.favoritesIconsShowNames) {
			GC gc = new GC(display);
			gc.setFont(listFont != null && !listFont.isDisposed() ? listFont : display.getSystemFont());
			int lineH = gc.textExtent("Ag").y;
			gc.dispose();
			rowH += lineH + 4;
		}
		return rowH;
	}

	private void startGlow() {
		if (glowActive) return;
		glowActive = true;
		glowStartTime = System.currentTimeMillis();
		selectionAnimGen++;
		scheduleSelectionAnim(selectionAnimGen);
		boolean icons = "icons".equals(Settings.favoritesViewMode);
		if (icons && iconCanvas != null && !iconCanvas.isDisposed()) iconCanvas.redraw();
		else if (!icons && listTable != null && !listTable.isDisposed()) listTable.redraw();
	}

	private void stopGlow() {
		if (!glowActive) return;
		glowActive = false;
		selectionAnimGen++;
		boolean icons = "icons".equals(Settings.favoritesViewMode);
		if (icons && iconCanvas != null && !iconCanvas.isDisposed()) iconCanvas.redraw();
		else if (!icons && listTable != null && !listTable.isDisposed()) listTable.redraw();
	}

	private void toggleViewMode() {
		loadingIcons = false;
		saveModeSettings();
		String cur = Settings.favoritesViewMode;
		String next = "detailed".equals(cur) ? "compact" : "compact".equals(cur) ? "icons" : "detailed";
		boolean wasIcons = "icons".equals(cur);
		boolean nowIcons = "icons".equals(next);
		System.out.println("[DBG] toggleViewMode: " + cur + " -> " + next + " wasIcons=" + wasIcons + " nowIcons=" + nowIcons + " favSize=" + favorites.size());
		Settings.favoritesViewMode = next;
		loadModeSettings(next);
		btnFont = null;
		applyFontSize();
		if (viewToggleBtn != null && !viewToggleBtn.isDisposed()) {
			String label = nowIcons ? "Icons" : "compact".equals(next) ? "Compact" : "Detailed";
			viewToggleBtn.setText(label);
		}
		if (detailsInfoBtn != null && !detailsInfoBtn.isDisposed()) {
			detailsInfoBtn.setEnabled(nowIcons || "compact".equals(next));
		}
		if (namesToggleBtn != null && !namesToggleBtn.isDisposed()) {
			namesToggleBtn.setEnabled(nowIcons);
		}
		if (iconZoomInBtn != null && !iconZoomInBtn.isDisposed()) {
			iconZoomInBtn.setEnabled(nowIcons);
		}
		if (iconZoomOutBtn != null && !iconZoomOutBtn.isDisposed()) {
			iconZoomOutBtn.setEnabled(nowIcons);
		}
		if (nowIcons) {
			stackLayout.topControl = iconCanvas;
			updateIconCanvasLayout();
			updateInfoBarOnModeChange();
		} else {
			stackLayout.topControl = tableBorderComp;
			infoBarLabel.setVisible(false);
			((GridData)infoBarLabel.getLayoutData()).exclude = true;
		}
		listTable.clearAll();
		listTable.redraw();
		shell.layout(true, true);
	}

	private void toggleDetailMask() {
		if ("icons".equals(Settings.favoritesViewMode)) {
			int mask = Settings.favoritesIconsMask;
			mask = (mask == 0) ? 31 : mask - 1;
			Settings.favoritesIconsMask = mask;
			saveModeSettings();
			updateInfoBarOnModeChange();
		} else if ("compact".equals(Settings.favoritesViewMode)) {
			int mask = Settings.favoritesCompactMask;
			mask = (mask == 0) ? 15 : mask - 1;
			Settings.favoritesCompactMask = mask;
			saveModeSettings();
			listTable.clearAll();
			listTable.redraw();
			updateRowHeight();
		}
	}

	private void toggleIconsNames() {
		if (!"icons".equals(Settings.favoritesViewMode)) return;
		Settings.favoritesIconsShowNames = !Settings.favoritesIconsShowNames;
		saveModeSettings();
		updateIconCanvasLayout();
		if (iconCanvas != null && !iconCanvas.isDisposed()) iconCanvas.redraw();
	}

	private void updateInfoBarOnModeChange() {
		if (!"icons".equals(Settings.favoritesViewMode)) return;
		int sel = selectedRowIdx;
		if (sel >= 0 && sel < favorites.size()) {
			File jar = favorites.get(sel);
			MidletInfo info = metadataCache.get(jar);
			updateInfoBar(jar, info);
		} else {
			updateInfoBar(null, null);
		}
	}

	private void updateStatusAndInfo(int sel) {
		if (sel >= 0 && sel < favorites.size()) {
			File jar = favorites.get(sel);
			MidletInfo info = metadataCache.get(jar);
			Float baseHue = rowHueCache.get(jar);
			if (baseHue == null) {
				baseHue = cacheBaseHue(jar);
			}
			String extra = "";
			if (info != null) {
				if (info.vendor != null && !info.vendor.isEmpty()) {
					extra += info.vendor + " ";
				}
				if (info.version != null && !info.version.isEmpty()) {
					extra += "v" + info.version + "  —  ";
				}
			}
			setDefaultStatusText((isSwapped ? "[Swapped] " : "") + extra + jar.getName());
			updateInfoBar(jar, info);
		}
	}

	private void updateInfoBar(File jar, MidletInfo info) {
		if (infoBarLabel == null || infoBarLabel.isDisposed()) return;
		if (!"icons".equals(Settings.favoritesViewMode) || Settings.favoritesIconsMask == 0) {
			infoBarLabel.setVisible(false);
			((GridData)infoBarLabel.getLayoutData()).exclude = true;
			shell.layout(true, true);
			if ("icons".equals(Settings.favoritesViewMode) && iconCanvas != null && !iconCanvas.isDisposed()) {
				scrollIconToSelected();
				loadingIcons = false;
				iconCanvas.redraw();
				iconCanvas.update();
			}
			return;
		}
		int mask = Settings.favoritesIconsMask;
		StringBuilder sb = new StringBuilder();
		String name = info != null && info.name != null ? info.name : (jar != null ? jar.getName().replace(".jar", "") : "");
		if ((mask & 1) != 0) sb.append(name);
		if (info != null && (mask & 2) != 0 && info.vendor != null && !info.vendor.isEmpty()) {
			if (sb.length() > 0) sb.append("  |  ");
			sb.append(info.vendor);
		}
		if (info != null && (mask & 4) != 0 && info.version != null && !info.version.isEmpty()) {
			if (sb.length() > 0) sb.append("  |  ");
			sb.append("v").append(info.version);
		}
		if ((mask & 8) != 0) {
			long dateMs = info != null && info.buildDate > 0 ? info.buildDate : (jar != null ? jar.lastModified() : 0);
			String dateStr = dateMs > 0 ? formatDate(dateMs) : "";
			if (!dateStr.isEmpty()) {
				if (sb.length() > 0) sb.append("  |  ");
				sb.append(dateStr);
			}
		}
		if ((mask & 16) != 0) {
			long len = jar != null ? jar.length() : 0;
			String sizeStr = len > 0 ? formatSize(len) : "";
			if (!sizeStr.isEmpty()) {
				if (sb.length() > 0) sb.append("  |  ");
				sb.append(sizeStr);
			}
		}
		infoBarLabel.setText(sb.toString());
		infoBarLabel.setVisible(true);
		((GridData)infoBarLabel.getLayoutData()).exclude = false;
		shell.layout(true, true);
		if ("icons".equals(Settings.favoritesViewMode) && iconCanvas != null && !iconCanvas.isDisposed()) {
			scrollIconToSelected();
			loadingIcons = false;
			iconCanvas.redraw();
			iconCanvas.update();
		}
	}

	private void setDefaultStatusText(String text) {
		savedDefaultStatusText = text;
		showingTempStatus = false;
		if (statusLabel != null && !statusLabel.isDisposed()) {
			statusLabel.setText(text);
		}
	}

	private void showTempStatus(String text) {
		if (statusLabel == null || statusLabel.isDisposed()) return;
		if (!showingTempStatus) {
			savedDefaultStatusText = statusLabel.getText();
		}
		statusLabel.setText(text);
		showingTempStatus = true;
		statusRestoreGen++;
		int gen = statusRestoreGen;
		display.timerExec(3000, () -> {
			if (gen != statusRestoreGen || statusLabel == null || statusLabel.isDisposed()) return;
			statusLabel.setText(savedDefaultStatusText);
			showingTempStatus = false;
		});
	}

	private void restoreDefaultStatus() {
		statusRestoreGen++;
		showingTempStatus = false;
		if (statusLabel != null && !statusLabel.isDisposed()) {
			statusLabel.setText(savedDefaultStatusText);
		}
	}

	private static int scrollTierRate(int tier) {
		int rate = 60 >> tier;
		return rate < 1 ? 1 : rate;
	}

	private void scheduleScrollRepeat(int delay, int seq) {
		display.timerExec(delay, () -> {
			if (seq != scrollGen) return;
			if (heldScrollKey != 0) {
				scrollOne(heldScrollKey > 0 ? 1 : -1);
				long elapsed = System.currentTimeMillis() - tierStartTime;
				int newTier = (int)(elapsed / 800);
				if (newTier > scrollTier) scrollTier = newTier;
				scheduleScrollRepeat(scrollTierRate(scrollTier), scrollGen);
			} else if (scrollLingerEnd > 0 && System.currentTimeMillis() < scrollLingerEnd) {
				scheduleScrollRepeat(80, scrollGen);
			} else {
				scrollTimerActive = false;
			}
		});
	}

	private void stopScrollRepeat() {
		heldScrollKey = 0;
		scrollTier = 0;
		scrollLingerEnd = System.currentTimeMillis() + 250;
	}

	private void startBtnRepeat(int delay) {
		final int gen = btnRepeatGen;
		display.timerExec(delay, () -> {
			if (gen != btnRepeatGen) return;
			if (heldBtnKey == 0 || focusTarget < 0) return;
			if (focusTarget >= toolbarButtons.size()) return;
			focusTarget = heldBtnKey < 0 ? prevBtnInRow(focusTarget) : nextBtnInRow(focusTarget);
			updateButtonFocus();
			if (heldBtnKey != 0) startBtnRepeat(200);
		});
	}

	private int firstBtnIndex(int row) {
		int start = (row == 0) ? 0 : (firstBottomBtnIndex >= 0 ? firstBottomBtnIndex : 0);
		int end = lastBtnIndex(row);
		for (int i = start; i <= end; i++) {
			if (isBtnEnabled(i)) return i;
		}
		return start;
	}

	private int lastBtnIndex(int row) {
		if (firstBottomBtnIndex < 0) return toolbarButtons.size() - 1;
		if (row == 0) return firstBottomBtnIndex - 1;
		return toolbarButtons.size() - 1;
	}

	private int rowOf(int idx) {
		if (firstBottomBtnIndex < 0) return 0;
		return idx < firstBottomBtnIndex ? 0 : 1;
	}

	private boolean isBtnEnabled(int idx) {
		if (idx < 0 || idx >= toolbarButtons.size()) return false;
		Button btn = toolbarButtons.get(idx);
		return btn != null && !btn.isDisposed() && btn.isEnabled();
	}

	private int nextBtnInRow(int current) {
		int row = rowOf(current);
		int last = lastBtnIndex(row);
		if (current >= last) return firstBtnIndex(row);
		int next = current + 1;
		while (next <= last) {
			if (next != folderButtonIndex && isBtnEnabled(next)) return next;
			next++;
		}
		return firstBtnIndex(row);
	}

	private int prevBtnInRow(int current) {
		int row = rowOf(current);
		int first = firstBtnIndex(row);
		if (current <= first) {
			int last = lastBtnIndex(row);
			if (isBtnEnabled(last)) return last;
			current = last;
		}
		int prev = current - 1;
		while (prev >= first) {
			if (prev != folderButtonIndex && isBtnEnabled(prev)) return prev;
			prev--;
		}
		int last = lastBtnIndex(row);
		if (isBtnEnabled(last)) return last;
		return first;
	}

	private void showButtonRowOverlay(int row) {
		if (rowOverlayShell != null && !rowOverlayShell.isDisposed()) {
			rowOverlayShell.close();
		}
		rowOverlayShell = new Shell(shell, SWT.NO_TRIM | SWT.ON_TOP);
		GridLayout gl = new GridLayout(1, false);
		gl.marginWidth = 20;
		gl.marginHeight = 10;
		rowOverlayShell.setLayout(gl);
		Label label = new Label(rowOverlayShell, SWT.CENTER);
		label.setText(row == 0 ? "BUTTON ROW TOP" : "BUTTON ROW BOTTOM");
		Font overlayFont = new Font(display, "Segoe UI", Math.max(fontSize * 2, 24), SWT.BOLD);
		label.setFont(overlayFont);
		label.addDisposeListener(e -> {
			if (overlayFont != null && !overlayFont.isDisposed()) overlayFont.dispose();
		});
		if (darkMode) {
			rowOverlayShell.setBackground(new Color(display, 45, 45, 45));
			label.setBackground(new Color(display, 45, 45, 45));
			label.setForeground(new Color(display, 224, 224, 224));
		}
		rowOverlayShell.pack();
		Rectangle sb = shell.getBounds();
		rowOverlayShell.setLocation(sb.x + (sb.width - rowOverlayShell.getBounds().width) / 2,
			sb.y + (sb.height - rowOverlayShell.getBounds().height) / 2);
		rowOverlayShell.open();
		display.timerExec(1000, () -> {
			if (rowOverlayShell != null && !rowOverlayShell.isDisposed()) {
				rowOverlayShell.close();
				rowOverlayShell = null;
			}
		});
	}

	private void showLoadingToast() {
		if (loadingToastShell != null && !loadingToastShell.isDisposed()) {
			return;
		}
		loadingToastShell = new Shell(shell, SWT.NO_TRIM | SWT.ON_TOP);
		GridLayout gl = new GridLayout(1, false);
		gl.marginWidth = 25;
		gl.marginHeight = 12;
		loadingToastShell.setLayout(gl);
		Label label = new Label(loadingToastShell, SWT.CENTER);
		label.setText("LOADING...");
		Font toastFont = new Font(display, "Segoe UI", Math.max(fontSize * 2, 22), SWT.BOLD);
		label.setFont(toastFont);
		label.addDisposeListener(e -> {
			if (toastFont != null && !toastFont.isDisposed()) toastFont.dispose();
		});
		if (darkMode) {
			loadingToastShell.setBackground(new Color(display, 45, 45, 45));
			label.setBackground(new Color(display, 45, 45, 45));
			label.setForeground(new Color(display, 224, 224, 224));
		}
		loadingToastShell.pack();
		// Performance Scene — center on the monitor where KEmulator is running
		Rectangle mb = parentShell != null && !parentShell.isDisposed()
			? parentShell.getMonitor().getClientArea()
			: display.getPrimaryMonitor().getClientArea();
		loadingToastShell.setLocation(mb.x + (mb.width - loadingToastShell.getBounds().width) / 2,
			mb.y + (mb.height - loadingToastShell.getBounds().height) / 2);
		loadingToastShell.open();
		loadingToastShell.update();
	}

	private void hideLoadingToast() {
		if (loadingToastShell != null && !loadingToastShell.isDisposed()) {
			loadingToastShell.close();
			loadingToastShell = null;
		}
	}

	private void setKeyboardMode(boolean kb) {
		if (keyboardMode == kb) return;
		keyboardMode = kb;
		if (folderButton == null || folderButton.isDisposed()) return;
		if (!kb) {
			// Mouse mode: enable folder button permanently
			display.timerExec(-1, disableButtonTask);
			folderButton.setEnabled(true);
		} else {
			// Keyboard mode: disable folder button
			folderButton.setEnabled(false);
		}
	}

	/** Show a KEYPAD-aware modal dialog. Returns SWT.OK, SWT.YES, SWT.NO, or SWT.CANCEL. */
	private int showDialog(Shell parent, String title, String message, int buttons) {
		Shell dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout(1, false));
		dialog.setText(title);

		Label label = new Label(dialog, SWT.WRAP);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		label.setText(message);

		Composite buttonBar = new Composite(dialog, SWT.NONE);
		buttonBar.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, false));
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.center = true;
		rowLayout.spacing = 8;
		rowLayout.marginLeft = 0;
		rowLayout.marginRight = 0;
		rowLayout.marginTop = 0;
		rowLayout.marginBottom = 0;
		buttonBar.setLayout(rowLayout);

		final int[] result = new int[]{SWT.CANCEL};

		if ((buttons & SWT.YES) != 0) {
			Button yesBtn = new Button(buttonBar, SWT.FLAT | SWT.PUSH);
			yesBtn.setText("Yes");
			yesBtn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					result[0] = SWT.YES;
					dialog.close();
				}
			});
		}
		if ((buttons & SWT.NO) != 0) {
			Button noBtn = new Button(buttonBar, SWT.FLAT | SWT.PUSH);
			noBtn.setText("No");
			noBtn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					result[0] = SWT.NO;
					dialog.close();
				}
			});
		}
		if ((buttons & SWT.OK) != 0 || buttons == SWT.NONE) {
			Button okBtn = new Button(buttonBar, SWT.FLAT | SWT.PUSH);
			okBtn.setText("OK");
			okBtn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					result[0] = SWT.OK;
					dialog.close();
				}
			});
		}

		// Apply scaling from Favorites Browser
		label.setFont(listFont);
		for (Control c : buttonBar.getChildren()) {
			if (c instanceof Button && btnFont != null && !btnFont.isDisposed()) {
				c.setFont(btnFont);
			}
		}

		dialog.pack();
		dialog.setLocation(
			parent.getBounds().x + (parent.getBounds().width - dialog.getBounds().width) / 2,
			parent.getBounds().y + (parent.getBounds().height - dialog.getBounds().height) / 2
		);

		Listener dialogFilter = event -> {
			if (dialog == null || dialog.isDisposed() || !dialog.isVisible()) return;
			Control focus = display.getFocusControl();
			if (focus == null || focus.getShell() != dialog) return;
			KeyEvent ke = new KeyEvent(event);
			if (isConfirmKey(ke)) {
				if (focus instanceof Button) {
					((Button) focus).notifyListeners(SWT.Selection, new Event());
				}
				event.doit = false;
			} else if (matchNavKey(ke, HotkeyManager.UI_CANCEL)) {
				dialog.close();
				event.doit = false;
			} else if (matchNavKey(ke, HotkeyManager.UI_LEFT) || matchNavKey(ke, HotkeyManager.UI_RIGHT)) {
				Control[] children = buttonBar.getChildren();
				for (int i = 0; i < children.length; i++) {
					if (children[i] == focus) {
						int next = matchNavKey(ke, HotkeyManager.UI_RIGHT) ?
							(i + 1) % children.length :
							(i - 1 + children.length) % children.length;
						children[next].setFocus();
						break;
					}
				}
				event.doit = false;
			}
		};
		display.addFilter(SWT.KeyDown, dialogFilter);

		Control[] children = buttonBar.getChildren();
		if (children.length > 0) children[0].setFocus();

		if (darkMode) {
			dialog.setBackground(new Color(display, 45, 45, 45));
			label.setBackground(new Color(display, 45, 45, 45));
			label.setForeground(new Color(display, 224, 224, 224));
			buttonBar.setBackground(new Color(display, 45, 45, 45));
			for (Control c2 : buttonBar.getChildren()) {
				if (c2 instanceof Button) {
					c2.setBackground(new Color(display, 60, 60, 60));
					c2.setForeground(new Color(display, 224, 224, 224));
				}
			}
		}

		dialog.open();

		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.removeFilter(SWT.KeyDown, dialogFilter);

		return result[0];
	}

	static boolean matchNavKey(KeyEvent e, HotkeyManager.HotkeyAction a) {
		if (a.keyCode == 0) return false;
		int mods = e.stateMask & (SWT.CTRL | SWT.SHIFT | SWT.ALT);
		if (mods != a.stateMask) return false;
		if (e.keyCode == a.keyCode) return true;
		// NumLock ON: numpad digit keys send ASCII codes
		if (a.keyCode >= SWT.KEYPAD_0 && a.keyCode <= SWT.KEYPAD_9) {
			int ascii = '0' + (a.keyCode - SWT.KEYPAD_0);
			if (e.keyCode == ascii) return true;
		}
		return Character.toLowerCase(e.keyCode) == Character.toLowerCase(a.keyCode);
	}

	private void updateButtonFocus() {
		for (int i = 0; i < toolbarButtons.size(); i++) {
			Button btn = toolbarButtons.get(i);
			if (btn != null && !btn.isDisposed()) {
				if (i == focusTarget) {
					btn.setFocus();
					display.asyncExec(() -> { if (!btn.isDisposed()) btn.setFocus(); });
					if (rowOf(i) == 0) savedFocusRow0 = i;
					else savedFocusRow1 = i;
				}
			}
		}
	}

	private void grabFocus() {
		// Immediate attempt
		if (shell != null && !shell.isDisposed()) {
			shell.forceActive();
			if (focusTarget < 0) {
				if ("icons".equals(Settings.favoritesViewMode) && iconCanvas != null && !iconCanvas.isDisposed()) {
					iconCanvas.setFocus();
				} else {
					listTable.setFocus();
				}
			} else {
				updateButtonFocus();
			}
		}
		// Retry after event queue drains
		display.asyncExec(() -> {
			if (shell != null && !shell.isDisposed()) {
				shell.forceActive();
				if (focusTarget < 0) {
					if ("icons".equals(Settings.favoritesViewMode) && iconCanvas != null && !iconCanvas.isDisposed()) {
						iconCanvas.setFocus();
					} else {
						listTable.setFocus();
					}
				} else {
					updateButtonFocus();
				}
			}
		});
		// Retry after a small delay for stubborn window managers
		display.timerExec(150, () -> {
			if (shell != null && !shell.isDisposed()) {
				shell.forceActive();
				if (focusTarget < 0) {
					if ("icons".equals(Settings.favoritesViewMode) && iconCanvas != null && !iconCanvas.isDisposed()) {
						iconCanvas.setFocus();
					} else {
						listTable.setFocus();
					}
				} else {
					updateButtonFocus();
				}
			}
		});
	}

	// Called by +UI/-UI, init, toggleViewMode, +I/-I.
	// Updates list display using textFontSize (list items use the independent text size),
	// then updates buttons + status labels via applyFontSizeAll() which uses fontSize.
	// Only saves favoritesFontSize — textFontSize is untouched here.
	private void applyFontSize() {
		rebuildListFont();
		int[] selInfo = new int[2];
		if (!"icons".equals(Settings.favoritesViewMode)) {
			selInfo[0] = listTable.getTopIndex();
			selInfo[1] = listTable.getSelectionIndex();
		}
		listTable.removeAll();
		for (MidletInfo mi : metadataCache.values()) {
			if (mi.iconImage != null && !mi.iconImage.isDisposed()) {
				mi.iconImage.dispose();
			}
			mi.iconImage = null;
		}
		if ("icons".equals(Settings.favoritesViewMode)) {
			updateIconCanvasLayout();
		} else {
			updateRowHeight();
			listTable.setFont(listFont);
			listTable.setItemCount(favorites.size());
			if (selInfo[1] >= 0 && selInfo[1] < favorites.size()) {
				listTable.setSelection(selInfo[1]);
				selectedRowIdx = selInfo[1];
			} else {
				selectedRowIdx = -1;
			}
			listTable.setTopIndex(selInfo[0]);
		}
		applyFontSizeAll();
		saveModeSettings();
	}

	// Called by T+/T- only.
	// Updates list display using textFontSize — all other UI elements (buttons, status labels) are left alone.
	// Only saves favoritesTextFontSize — fontSize is untouched here.
	private void applyTextFontSize() {
		rebuildListFont();
		int[] selInfo = new int[2];
		if ("icons".equals(Settings.favoritesViewMode)) {
			updateIconCanvasLayout();
		} else {
			selInfo[0] = listTable.getTopIndex();
			selInfo[1] = listTable.getSelectionIndex();
			listTable.removeAll();
			for (MidletInfo mi : metadataCache.values()) {
				if (mi.iconImage != null && !mi.iconImage.isDisposed()) {
					mi.iconImage.dispose();
				}
				mi.iconImage = null;
			}
			updateRowHeight();
			listTable.setFont(listFont);
			listTable.setItemCount(favorites.size());
			if (selInfo[1] >= 0 && selInfo[1] < favorites.size()) {
				listTable.setSelection(selInfo[1]);
				selectedRowIdx = selInfo[1];
			}
			listTable.setTopIndex(selInfo[0]);
		}
		if (iconCanvas != null && !iconCanvas.isDisposed()) iconCanvas.redraw();
		saveModeSettings();
	}

	private void rebuildListFont() {
		if (listFont != null && !listFont.isDisposed()) {
			listFont.dispose();
		}
		listFont = new Font(display, "Segoe UI", textFontSize, SWT.NORMAL);
		if (subFont != null && !subFont.isDisposed()) {
			subFont.dispose();
		}
		int subSize = Math.max(textFontSize - 2, 8);
		subFont = new Font(display, "Segoe UI", subSize, SWT.NORMAL);
		if (subColor == null || subColor.isDisposed()) {
			subColor = new Color(display, darkMode ? 180 : 140, darkMode ? 180 : 140, darkMode ? 180 : 140);
		}
		if (buildColor == null || buildColor.isDisposed()) {
			buildColor = new Color(display, darkMode ? 100 : 70, darkMode ? 180 : 130, darkMode ? 230 : 180);
		}
	}

	private void updateRowHeight() {
		if (listTable == null || listTable.isDisposed()) return;
		try {
			GC gc = new GC(listTable);
			gc.setFont(listFont);
			int nameH = gc.textExtent("Ag").y;
			gc.setFont(subFont);
			int subH = gc.textExtent("Ag").y;
			gc.dispose();
			boolean compact = "compact".equals(Settings.favoritesViewMode);
			int rowHeight = compact ? nameH + 8 : nameH + subH + 10;
			currentIconSize = Math.max(16, rowHeight - 4);
			listTable.setItemCount(favorites.size());
			java.lang.reflect.Method m = Table.class.getDeclaredMethod("setItemHeight", Integer.TYPE);
			m.setAccessible(true);
			m.invoke(listTable, rowHeight);
		} catch (Exception ignored) {
			currentIconSize = Math.max(16, fontSize * 2);
		}
		updateTableCols();
	}

	private void updateTableCols() {
		if (iconCol != null && !iconCol.isDisposed()) iconCol.setWidth(currentIconSize);
		if (spacerCol != null && !spacerCol.isDisposed()) spacerCol.setWidth(listPadding);
		if (rightSpacerCol != null && !rightSpacerCol.isDisposed()) rightSpacerCol.setWidth(listPadding);
		if (textCol != null && !textCol.isDisposed()) {
			Rectangle r = listTable.getClientArea();
			textCol.setWidth(Math.max(100, r.width - currentIconSize - listPadding * 2));
		}
	}

	private void applyListPadding() {
		saveModeSettings();
		if ("icons".equals(Settings.favoritesViewMode)) {
			updateIconCanvasLayout();
		} else {
			updateTableCols();
			listTable.clearAll();
			listTable.redraw();
		}
	}

	// Updates all UI chrome: buttons and status labels.
	// Always uses fontSize — never textFontSize — so +UI/-UI control the chrome
	// and T+/T- only affect the list items.
	private void applyFontSizeAll() {
		if (btnFont != null && !btnFont.isDisposed()) {
			btnFont.dispose();
		}
		int btnFontSize = Math.max(fontSize - 2, 8);
		btnFont = new Font(display, "Segoe UI", btnFontSize, SWT.NORMAL);
		GC gc = new GC(display);
		gc.setFont(btnFont);
		int btnTextH = gc.textExtent("Ag").y + 6;
		gc.dispose();
		for (Button btn : toolbarButtons) {
			if (btn != null && !btn.isDisposed()) {
				btn.setFont(btnFont);
				btn.setLayoutData(new RowData(SWT.DEFAULT, btnTextH));
			}
		}
		if (statusLabel != null && !statusLabel.isDisposed()) {
			statusLabel.setFont(btnFont);
		}
		if (infoBarLabel != null && !infoBarLabel.isDisposed()) {
			infoBarLabel.setFont(btnFont);
		}
		if (searchBox != null && !searchBox.isDisposed()) {
			searchBox.setFont(btnFont);
		}
		shell.layout(true, true);
	}

	private Color bg() {
		return paintBg != null ? paintBg : display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	}

	private Color fg() {
		return paintFg != null ? paintFg : display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	}

	private Color widgetBg() {
		return paintWidgetBg != null ? paintWidgetBg : display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	}

	private void disposeDarkColors() {
		if (paintBg != null && !paintBg.isDisposed()) paintBg.dispose();
		if (paintFg != null && !paintFg.isDisposed()) paintFg.dispose();
		if (paintWidgetBg != null && !paintWidgetBg.isDisposed()) paintWidgetBg.dispose();
		if (darkShellBg != null && !darkShellBg.isDisposed()) darkShellBg.dispose();
		if (darkButtonBg != null && !darkButtonBg.isDisposed()) darkButtonBg.dispose();
		if (darkFg != null && !darkFg.isDisposed()) darkFg.dispose();
		if (tableBorderBg != null && !tableBorderBg.isDisposed()) tableBorderBg.dispose();
		if (sepColor != null && !sepColor.isDisposed()) sepColor.dispose();
		if (subColor != null && !subColor.isDisposed()) subColor.dispose();
		if (buildColor != null && !buildColor.isDisposed()) buildColor.dispose();
		if (selFallbackColor != null && !selFallbackColor.isDisposed()) {
			selFallbackColor.dispose();
			selFallbackColor = null;
		}
		if (hoverFallbackColor != null && !hoverFallbackColor.isDisposed()) {
			hoverFallbackColor.dispose();
			hoverFallbackColor = null;
		}
		if (searchPlaceholderColor != null && !searchPlaceholderColor.isDisposed()) {
			searchPlaceholderColor.dispose();
			searchPlaceholderColor = null;
		}
		subColor = null;
		buildColor = null;
		paintBg = null;
		paintFg = null;
		paintWidgetBg = null;
		darkShellBg = null;
		darkButtonBg = null;
		darkFg = null;
		tableBorderBg = null;
		sepColor = null;
	}

	private void applyThemeToChildren(Composite parent) {
		for (Control c : parent.getChildren()) {
			if (c == null || c.isDisposed()) continue;
			if (darkMode) {
				if (c instanceof Button) {
					c.setBackground(darkButtonBg);
					c.setForeground(darkFg);
				} else if (c instanceof Label) {
					c.setBackground(darkShellBg);
					c.setForeground(darkFg);
				} else if (c instanceof Table) {
					c.setBackground(paintBg);
					c.setForeground(paintFg);
	} else if (c instanceof Canvas) {
		c.setBackground(paintBg);
	} else if (c instanceof Text) {
		c.setBackground(paintWidgetBg);
	} else if (c instanceof Composite) {
		c.setBackground(darkShellBg);
		applyThemeToChildren((Composite) c);
	}
} else {
	c.setBackground(null);
	if (c instanceof Label || c instanceof Button || c instanceof Table || c instanceof Text) {
		c.setForeground(null);
	}
	if (c instanceof Composite) {
		applyThemeToChildren((Composite) c);
	}
}
		}
	}

	private void applyTheme() {
		disposeDarkColors();
		if (darkMode) {
			paintBg = new Color(display, 18, 18, 18);
			paintFg = new Color(display, 224, 224, 224);
			paintWidgetBg = new Color(display, 50, 50, 50);
			darkShellBg = new Color(display, 45, 45, 45);
			darkButtonBg = new Color(display, 60, 60, 60);
			darkFg = new Color(display, 224, 224, 224);
			tableBorderBg = new Color(display, 55, 55, 55);
			sepColor = new Color(display, 240, 240, 240);
			searchPlaceholderColor = new Color(display, 160, 160, 160);
		} else {
			searchPlaceholderColor = null;
		}
		rebuildListFont();
		if (!"icons".equals(Settings.favoritesViewMode) && listTable != null && !listTable.isDisposed()) {
			listTable.setFont(listFont);
			updateRowHeight();
		}
		shell.setBackground(darkMode ? darkShellBg : null);
		applyThemeToChildren(shell);
		if (tableBorderComp != null && !tableBorderComp.isDisposed()) {
			tableBorderComp.setBackground(darkMode ? tableBorderBg : null);
			GridLayout bl = (GridLayout) tableBorderComp.getLayout();
			bl.marginWidth = darkMode ? 1 : 0;
			bl.marginHeight = darkMode ? 1 : 0;
			tableBorderComp.layout();
		}
		if (topToolbar != null && !topToolbar.isDisposed()) {
			topToolbar.setBackground(darkMode ? darkShellBg : null);
		}
		if (bottomBtns != null && !bottomBtns.isDisposed()) {
			bottomBtns.setBackground(darkMode ? darkShellBg : null);
		}
		Color sysBtn = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		Color sysFg = display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
		for (Button btn : toolbarButtons) {
			if (btn != null && !btn.isDisposed()) {
				setButtonWinTheme(btn, darkMode);
				btn.setBackground(sysBtn);
				btn.setForeground(sysFg);
			}
		}
		if (searchBox != null && !searchBox.isDisposed()) {
			if (searchPlaceholder) {
				searchBox.setForeground(searchPlaceholderColor);
			} else if (darkMode) {
				searchBox.setForeground(paintFg);
			} else {
				searchBox.setForeground(null);
			}
		}
		shell.layout(true, true);
		shell.redraw();
	}

	private void loadModeSettings(String mode) {
		switch (mode) {
			case "icons":
				fontSize = Settings.favoritesUISize_icons > 0 ? Settings.favoritesUISize_icons : 12;
				textFontSize = Settings.favoritesTextSize_icons > 0 ? Settings.favoritesTextSize_icons : fontSize;
				listPadding = Settings.favoritesPadding_icons;
				iconGridIconSize = Settings.favoritesIconSize_icons;
				Settings.favoritesIconsMask = Settings.favoritesMask_icons;
				Settings.favoritesIconsShowNames = Settings.favoritesNames_icons;
				break;
			case "compact":
				fontSize = Settings.favoritesUISize_compact > 0 ? Settings.favoritesUISize_compact : 12;
				textFontSize = Settings.favoritesTextSize_compact > 0 ? Settings.favoritesTextSize_compact : fontSize;
				listPadding = Settings.favoritesPadding_compact;
				Settings.favoritesCompactMask = Settings.favoritesMask_compact;
				break;
			case "detailed":
				fontSize = Settings.favoritesUISize_detailed > 0 ? Settings.favoritesUISize_detailed : 12;
				textFontSize = Settings.favoritesTextSize_detailed > 0 ? Settings.favoritesTextSize_detailed : fontSize;
				listPadding = Settings.favoritesPadding_detailed;
				break;
		}
	}

	private void saveModeSettings() {
		String mode = Settings.favoritesViewMode;
		switch (mode) {
			case "icons":
				Settings.favoritesUISize_icons = fontSize;
				Settings.favoritesTextSize_icons = textFontSize;
				Settings.favoritesPadding_icons = listPadding;
				Settings.favoritesIconSize_icons = iconGridIconSize;
				Settings.favoritesMask_icons = Settings.favoritesIconsMask;
				Settings.favoritesNames_icons = Settings.favoritesIconsShowNames;
				break;
			case "compact":
				Settings.favoritesUISize_compact = fontSize;
				Settings.favoritesTextSize_compact = textFontSize;
				Settings.favoritesPadding_compact = listPadding;
				Settings.favoritesMask_compact = Settings.favoritesCompactMask;
				break;
			case "detailed":
				Settings.favoritesUISize_detailed = fontSize;
				Settings.favoritesTextSize_detailed = textFontSize;
				Settings.favoritesPadding_detailed = listPadding;
				break;
		}
	}

	private void toggleTheme() {
		darkMode = !darkMode;
		Settings.favoritesDarkMode = darkMode;
		if (themeToggleBtn != null && !themeToggleBtn.isDisposed()) {
			themeToggleBtn.setText(darkMode ? "Light" : "Dark");
		}
		showLoadingToast();
		applyTheme();
		refreshList();
		hideLoadingToast();
		EmulatorScreen.notifyThemeChanged();
	}

	private void applySearchFilter() {
		String filter = searchBox != null && !searchBox.isDisposed() ? searchBox.getText().trim().toLowerCase() : "";
		if (filter.isEmpty()) {
			favorites = unfilteredFavorites;
		} else {
			ArrayList<File> filtered = new ArrayList<>();
			for (File jar : unfilteredFavorites) {
				MidletInfo mi = metadataCache.get(jar);
				String name = mi != null && mi.name != null ? mi.name.toLowerCase() : "";
				String vendor = mi != null && mi.vendor != null ? mi.vendor.toLowerCase() : "";
				String fileName = jar.getName().toLowerCase();
				String fileNameClean = fileName.replace(".jar", "").replace('_', ' ').trim();
				if (name.contains(filter) || vendor.contains(filter) || fileName.contains(filter) || fileNameClean.contains(filter)) filtered.add(jar);
			}
			favorites = filtered;
		}
		selectedRowIdx = favorites.isEmpty() ? -1 : 0;
		if ("icons".equals(Settings.favoritesViewMode)) {
			iconScrollOffset = 0;
			loadingIcons = false;
			updateIconScrollBar();
			if (iconCanvas != null && !iconCanvas.isDisposed()) iconCanvas.redraw();
		} else if (listTable != null && !listTable.isDisposed()) {
			listTable.setItemCount(favorites.size());
			listTable.clearAll();
		}
		setDefaultStatusText((isSwapped ? "[Swapped] " : "") + favorites.size() + " favorite" + (favorites.size() != 1 ? "s" : "") + " — " + favoritesDir);
	}

	private void refreshList() {
		stopGlow();
		loadingIcons = false;
		selectedRowIdx = -1;
		iconScrollOffset = 0;
		favorites.clear();
		listTable.removeAll();
		// Dispose old cached icon images
		for (MidletInfo mi : metadataCache.values()) {
			if (mi.iconImage != null && !mi.iconImage.isDisposed()) {
				mi.iconImage.dispose();
			}
		}
		metadataCache.clear();

		File dir = new File(favoritesDir);
		if (!dir.exists() || !dir.isDirectory()) {
			setDefaultStatusText("Folder not found: " + favoritesDir);
			return;
		}

		File[] jars = dir.listFiles(new FilenameFilter() {
			public boolean accept(File d, String name) {
				return name.toLowerCase().endsWith(".jar");
			}
		});

		if (jars == null) jars = new File[0];

		Arrays.sort(jars, new Comparator<File>() {
			public int compare(File a, File b) {
				return a.getName().compareToIgnoreCase(b.getName());
			}
		});

		// Try disk cache
		Map<String, CacheEntry> diskCache = readCache(dir);
		Map<String, CacheEntry> newCache = new LinkedHashMap<>();

		for (File jar : jars) {
			favorites.add(jar);
			String fileName = jar.getName();
			CacheEntry ce = diskCache != null ? diskCache.get(fileName) : null;
			if (ce != null && ce.lastModified == jar.lastModified()) {
				// Trust cached entry — valid or invalid, no re-read
				if (ce.valid) {
					MidletInfo info = new MidletInfo();
					info.name = ce.name;
					info.vendor = ce.vendor;
					info.version = ce.version;
					info.iconPath = ce.iconPath;
					info.buildDate = ce.buildDate;
					metadataCache.put(jar, info);
				}
				newCache.put(fileName, ce);
			} else {
				MidletInfo info = loadMidletManifest(jar);
				if (info != null) {
					metadataCache.put(jar, info);
				}
				CacheEntry entry = new CacheEntry();
				entry.valid = info != null;
				entry.lastModified = jar.lastModified();
				if (info != null) {
					entry.name = info.name;
					entry.vendor = info.vendor;
					entry.version = info.version;
					entry.iconPath = info.iconPath;
					entry.buildDate = info.buildDate;
				}
				newCache.put(fileName, entry);
			}
		}

		// Write updated cache in background
		writeCacheAsync(dir, newCache);

		if (!"icons".equals(Settings.favoritesViewMode)) {
			listTable.setItemCount(favorites.size());
			listTable.clearAll();
			updateRowHeight();
		}

		unfilteredFavorites = new ArrayList<>(favorites);
		if (searchBox != null && !searchBox.isDisposed() && !searchPlaceholder && searchBox.getText().length() > 0) {
			searchBox.setText("");
		}
		setDefaultStatusText((isSwapped ? "[Swapped] " : "") + favorites.size() + " favorite" + (favorites.size() != 1 ? "s" : "") + " — " + favoritesDir);
		boolean iconsMode = "icons".equals(Settings.favoritesViewMode);
		System.out.println("[DBG] refreshList: jarsFound=" + favorites.size() + " iconsMode=" + iconsMode + " topControl=" + (stackLayout != null ? (stackLayout.topControl == iconCanvas ? "iconCanvas" : stackLayout.topControl == listTable ? "listTable" : "other") : "null") + " iconCanvas.visible=" + (iconCanvas != null && !iconCanvas.isDisposed() ? iconCanvas.isVisible() : "N/A") + " iconCanvas.enabled=" + (iconCanvas != null && !iconCanvas.isDisposed() ? iconCanvas.isEnabled() : "N/A"));
		if (!iconsMode) {
			updateIconCanvasLayout();
		} else if (iconCanvas != null && !iconCanvas.isDisposed()) {
			updateIconScrollBar();
		}
	}

	private void changeFavoritesPath() {
		DirectoryDialog dlg = new DirectoryDialog(shell, SWT.OPEN);
		dlg.setText("Select Favorites Folder");
		dlg.setFilterPath(favoritesDir);
		String path = dlg.open();
		if (path != null) {
			favoritesDir = path;
			Settings.favoritesPath = path;
			EmulatorScreen screen = (EmulatorScreen) Emulator.getEmulator().getScreen();
			if (screen != null) screen.reloadBgIcons();
			if (isSwapped) {
				isSwapped = false;
				originalFavoritesPath = null;
			}
			new File(favoritesDir).mkdirs();
			stopGlow();
			showLoadingToast();
			refreshList();
			hideLoadingToast();
			updateButtonStates();
			if (shell != null && !shell.isDisposed()) {
				shell.layout(true, true);
			}
			if ("icons".equals(Settings.favoritesViewMode) && favorites.size() > 0 && iconCanvas != null && !iconCanvas.isDisposed()) {
				selectedRowIdx = 0;
				iconCanvas.redraw();
				iconCanvas.update();
			}
		}
	}

	private String getLuckyToggleLabel() {
		int idx = Settings.luckyFolderBrowserIndex;
		if (idx == 0) return "Favorites";
		String[] paths = LuckyFolderManager.getPaths();
		boolean[] modes = LuckyFolderManager.getModes();
		int favIdx = 0;
		for (int i = 0; i < paths.length; i++) {
			if (i < modes.length && modes[i]) {
				favIdx++;
				if (favIdx == idx) {
					String label = paths[i];
					int lastSep = Math.max(label.lastIndexOf('/'), label.lastIndexOf('\\'));
					if (lastSep >= 0) label = label.substring(lastSep + 1);
					if (label.length() > 20) label = label.substring(0, 17) + "...";
					return label;
				}
			}
		}
		return "Favorites";
	}

	private void cycleLuckyFolder() {
		String[] paths = LuckyFolderManager.getPaths();
		boolean[] modes = LuckyFolderManager.getModes();
		int count = 0;
		for (int i = 0; i < paths.length; i++) {
			if (i < modes.length && modes[i]) count++;
		}
		int next = Settings.luckyFolderBrowserIndex + 1;
		if (next > count) next = 0;
		Settings.luckyFolderBrowserIndex = next;

		String dir;
		if (next == 0) {
			String orig = Settings.favoritesPath;
			if (orig == null || orig.isEmpty()) orig = Emulator.getUserPath() + File.separator + "favorites";
			dir = orig;
		} else {
			int favIdx = 0;
			dir = null;
			for (int i = 0; i < paths.length; i++) {
				if (i < modes.length && modes[i]) {
					favIdx++;
					if (favIdx == next) { dir = paths[i]; break; }
				}
			}
			if (dir == null) return;
		}

		favoritesDir = dir;
		if (next == 0) {
			Settings.favoritesPath = dir;
			isSwapped = false;
			originalFavoritesPath = null;
		} else {
			if (!isSwapped) originalFavoritesPath = Settings.favoritesPath;
			isSwapped = true;
		}
		EmulatorScreen screen = (EmulatorScreen) Emulator.getEmulator().getScreen();
		if (screen != null) screen.reloadBgIcons();
		new File(favoritesDir).mkdirs();
		stopGlow();
		showLoadingToast();
		refreshList();
		hideLoadingToast();
		updateButtonStates();
		if (luckyToggleButton != null && !luckyToggleButton.isDisposed()) {
			luckyToggleButton.setText(getLuckyToggleLabel());
		}
		if (shell != null && !shell.isDisposed()) {
			shell.layout(true, true);
		}
		if ("icons".equals(Settings.favoritesViewMode) && favorites.size() > 0 && iconCanvas != null && !iconCanvas.isDisposed()) {
			selectedRowIdx = 0;
			iconCanvas.redraw();
			iconCanvas.update();
		}
		((Property) Emulator.getEmulator().getProperty()).saveProperties();
	}

	private void scheduleMoveCurrentToFavorites() {
		if (Emulator.midletJarPath == null) {
			showDialog(shell, "No JAR Loaded", "No JAR is currently loaded.", SWT.OK);
			return;
		}

		File srcJar = new File(Emulator.midletJarPath);
		if (!srcJar.exists()) {
			showDialog(shell, "File Not Found", "JAR file not found: " + Emulator.midletJarPath, SWT.OK);
			return;
		}

		File destJar = new File(favoritesDir, srcJar.getName());
		if (destJar.getAbsolutePath().equalsIgnoreCase(srcJar.getAbsolutePath())) {
			showDialog(shell, "Already in Folder", "JAR is already in this folder.", SWT.OK);
			return;
		}

		String entry = srcJar.getAbsolutePath() + "||" + destJar.getAbsolutePath();
		if (Settings.pendingFavoriteMoves == null || Settings.pendingFavoriteMoves.isEmpty()) {
			Settings.pendingFavoriteMoves = entry;
		} else {
			Settings.pendingFavoriteMoves = Settings.pendingFavoriteMoves + ";;" + entry;
		}
		((emulator.ui.swt.Property) Emulator.getEmulator().getProperty()).saveProperties();

		File parentDir = srcJar.getParentFile();
		if (parentDir != null && parentDir.isDirectory()) {
			File[] allJars = parentDir.listFiles(new java.io.FileFilter() {
				public boolean accept(File f) {
					return f.isFile() && f.getName().toLowerCase().endsWith(".jar");
				}
			});
			if (allJars != null && allJars.length > 1) {
				Arrays.sort(allJars, new Comparator<File>() {
					public int compare(File a, File b) {
						return a.getName().compareToIgnoreCase(b.getName());
					}
				});
				int currentIdx = 0;
				for (int i = 0; i < allJars.length; i++) {
					if (allJars[i].getAbsolutePath().equalsIgnoreCase(srcJar.getAbsolutePath())) {
						currentIdx = i;
						break;
					}
				}
				int nextIdx = (currentIdx + 1) % allJars.length;
				String nextPath = allJars[nextIdx].getAbsolutePath();
				shell.close();
				final String loadPath = nextPath;
				new Thread("JAR-Switch") {
					public void run() {
						Emulator.loadGame(loadPath, false);
					}
				}.start();
				return;
			}
		}

		shell.close();
		showDialog(parentShell, "Scheduled", srcJar.getName() + " will be moved to favorites on next launch.", SWT.OK);
	}

	private void loadJarCache() {
		cachedJarFiles.clear();
		currentJarIndex = -1;
		String jarPath = Emulator.midletJarPath;
		// Fall back to last known directory if no JAR is currently loaded
		if (jarPath == null) jarPath = Settings.lastJarDir;
		if (jarPath == null || jarPath.isEmpty()) return;
		File srcJar = new File(jarPath);
		File parentDir = srcJar.isDirectory() ? srcJar : srcJar.getParentFile();
		if (parentDir == null || !parentDir.isDirectory()) return;
		File[] jars = parentDir.listFiles((d, name) -> name.toLowerCase().endsWith(".jar"));
		if (jars == null || jars.length == 0) return;
		Arrays.sort(jars, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
		for (File f : jars) cachedJarFiles.add(f);
		if (Emulator.midletJarPath != null) {
			for (int i = 0; i < cachedJarFiles.size(); i++) {
				if (cachedJarFiles.get(i).getAbsolutePath().equalsIgnoreCase(srcJar.getAbsolutePath())) {
					currentJarIndex = i;
					break;
				}
			}
		} else {
			currentJarIndex = Math.min(Settings.lastJarIndex, cachedJarFiles.size() - 1);
		}
		Settings.lastJarDir = parentDir.getAbsolutePath();
		saveJarCacheIndex();
	}

	private void selectPrevJar() {
		if (currentJarIndex <= 0 || cachedJarFiles.isEmpty()) return;
		selectJarInList(cachedJarFiles.get(currentJarIndex - 1));
	}

	private void selectNextJar() {
		if (currentJarIndex < 0 || currentJarIndex >= cachedJarFiles.size() - 1) return;
		selectJarInList(cachedJarFiles.get(currentJarIndex + 1));
	}

	private void selectCurrentJar() {
		if (currentJarIndex < 0 || cachedJarFiles.isEmpty()) return;
		selectJarInList(cachedJarFiles.get(currentJarIndex));
	}

	private void saveJarCacheIndex() {
		Settings.lastJarIndex = currentJarIndex;
		if (Emulator.getEmulator() != null) {
			((emulator.ui.swt.Property) Emulator.getEmulator().getProperty()).saveProperties();
		}
	}

	private void selectJarInList(File target) {
		for (int i = 0; i < favorites.size(); i++) {
			if (favorites.get(i).getAbsolutePath().equalsIgnoreCase(target.getAbsolutePath())) {
				listTable.setSelection(i);
				selectedRowIdx = i;
				listTable.showSelection();
				listTable.redraw();
				if ("icons".equals(Settings.favoritesViewMode) && iconCanvas != null && !iconCanvas.isDisposed()) {
					iconCanvas.redraw();
				}
				return;
			}
		}
		String parentDir = target.getParent();
		if (parentDir != null && !parentDir.equalsIgnoreCase(favoritesDir)) {
			swapDirCache = parentDir;
			if (!isSwapped) originalFavoritesPath = favoritesDir;
			favoritesDir = parentDir;
			isSwapped = true;
		showLoadingToast();
		refreshList();
		hideLoadingToast();
		if (shell != null && !shell.isDisposed()) {
			shell.layout(true, true);
		}
			for (int i = 0; i < favorites.size(); i++) {
				if (favorites.get(i).getAbsolutePath().equalsIgnoreCase(target.getAbsolutePath())) {
					listTable.setSelection(i);
					selectedRowIdx = i;
					listTable.showSelection();
					listTable.redraw();
					if ("icons".equals(Settings.favoritesViewMode) && iconCanvas != null && !iconCanvas.isDisposed()) {
						iconCanvas.redraw();
						iconCanvas.update();
					}
					return;
				}
			}
		}
	}

	private void toggleSwap() {
		System.out.println("[DBG] toggleSwap: isSwapped=" + isSwapped + " curDir=" + favoritesDir);
		if (isSwapped) {
			favoritesDir = originalFavoritesPath;
			isSwapped = false;
		} else {
			String swapDir;
			if (swapDirCache != null) {
				swapDir = swapDirCache;
			} else if (Emulator.midletJarPath != null) {
				File jarParent = new File(Emulator.midletJarPath).getParentFile();
				if (jarParent == null || !jarParent.isDirectory()) return;
				swapDir = jarParent.getAbsolutePath();
			} else if (!cachedJarFiles.isEmpty()) {
				swapDir = cachedJarFiles.get(0).getParentFile().getAbsolutePath();
			} else {
				showDialog(shell, "No Cache", "Load a JAR first to swap to its folder.", SWT.OK);
				return;
			}
			swapDirCache = swapDir;
			originalFavoritesPath = favoritesDir;
			favoritesDir = swapDir;
			isSwapped = true;
		}
		new File(favoritesDir).mkdirs();
		EmulatorScreen screen = (EmulatorScreen) Emulator.getEmulator().getScreen();
		if (screen != null) screen.reloadBgIcons(favoritesDir);
		System.out.println("[DBG] toggleSwap: newDir=" + favoritesDir);
		System.out.println("[DBG] toggleSwap: BEFORE refreshList dir=" + favoritesDir);
		showLoadingToast();
		refreshList();
		hideLoadingToast();
		updateButtonStates();
		if (shell != null && !shell.isDisposed()) {
			shell.layout(true, true);
		}
		if (favorites.size() > 0) {
			if ("icons".equals(Settings.favoritesViewMode)) {
				System.out.println("[DBG] toggleSwap: icon mode, redraw+update after layout");
				selectedRowIdx = 0;
				if (iconCanvas != null && !iconCanvas.isDisposed()) {
					iconCanvas.redraw();
					iconCanvas.update();
				}
			} else {
				System.out.println("[DBG] toggleSwap: table mode");
				listTable.setSelection(0);
				selectedRowIdx = 0;
				listTable.redraw();
			}
		} else {
			System.out.println("[DBG] toggleSwap: favorites is EMPTY");
		}
	}

	private File getCacheFile(File dir) {
		return new File(dir, ".midlet_cache");
	}

	@SuppressWarnings("unchecked")
	private Map<String, CacheEntry> readCache(File dir) {
		File f = getCacheFile(dir);
		if (!f.exists()) return null;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
			return (Map<String, CacheEntry>) ois.readObject();
		} catch (Exception e) {
			return null;
		}
	}

	private void writeCacheAsync(File dir, final Map<String, CacheEntry> cache) {
		new Thread("Cache-Write") {
			public void run() {
				File f = getCacheFile(dir);
				try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
					oos.writeObject(cache);
				} catch (Exception ignored) {}
			}
		}.start();
	}

	private MidletInfo loadMidletManifest(File jar) {
		// Use tolerant custom parser that handles BOM, malformed lines, unusual line lengths
		java.util.Map<String, String> attrs = null;
		long buildDate = 0;
		ZipFile zf = null;
		try {
			zf = new ZipFile(jar);
			org.apache.tools.zip.ZipEntry me = zf.getEntry("META-INF/MANIFEST.MF");
			if (me != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try (InputStream is = zf.getInputStream(me)) {
					byte[] buf = new byte[4096]; int n;
					while ((n = is.read(buf)) >= 0) bos.write(buf, 0, n);
				}
				attrs = parseManifestRaw(stripBom(bos.toByteArray()));
				if (me.getTime() > 0) buildDate = me.getTime();
			}
			if (zf != null) try { zf.close(); } catch (Exception ignored) {}
		} catch (Exception e) {
			if (zf != null) try { zf.close(); } catch (Exception ignored) {}
			writeDebugLog("[WARN] loadMidletManifest ZipFile failed for " + jar.getName() + ": " + e);
		}
		if (attrs == null) {
			// Fallback: ZipInputStream
			try (FileInputStream fis = new FileInputStream(jar);
				 java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(fis)) {
				java.util.zip.ZipEntry ze;
				while ((ze = zis.getNextEntry()) != null) {
					if (ze.getName().equalsIgnoreCase("META-INF/MANIFEST.MF")) {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						byte[] buf = new byte[4096]; int n;
						while ((n = zis.read(buf)) >= 0) bos.write(buf, 0, n);
						attrs = parseManifestRaw(stripBom(bos.toByteArray()));
						if (ze.getTime() > 0) buildDate = ze.getTime();
						break;
					}
				}
			} catch (Exception e2) {
				writeDebugLog("[WARN] loadMidletManifest ZipInputStream fallback failed for " + jar.getName() + ": " + e2);
			}
		}
		if (attrs == null || attrs.isEmpty()) return null;
		String name = attrs.get("MIDlet-Name");
		String midlet1 = attrs.get("MIDlet-1");
		if (name == null && midlet1 != null) {
			String[] parts = midlet1.split(",");
			if (parts.length > 0 && !parts[0].trim().isEmpty()) name = parts[0].trim();
		}
		String iconPath = attrs.get("MIDlet-Icon");
		if (iconPath == null && midlet1 != null) {
			String[] parts = midlet1.split(",");
			if (parts.length > 1) { String p = parts[1].trim(); if (!p.isEmpty()) iconPath = p; }
		}
		MidletInfo info = new MidletInfo();
		info.name = name != null ? name : jar.getName().replace(".jar", "").replace('_', ' ');
		info.vendor = attrs.get("MIDlet-Vendor");
		info.version = attrs.get("MIDlet-Version");
		info.iconPath = iconPath;
		info.buildDate = buildDate > 0 ? buildDate : jar.lastModified();
		return info;
	}

	private java.util.Map<String, String> parseManifestRaw(byte[] data) {
		java.util.Map<String, String> map = new java.util.LinkedHashMap<>();
		if (data == null || data.length == 0) return map;
		String text;
		try { text = new String(data, "UTF-8"); } catch (Exception e) { text = new String(data); }
		String[] lines = text.split("\\r?\\n");
		StringBuilder currentKey = new StringBuilder();
		StringBuilder currentVal = new StringBuilder();
		boolean inHeader = false;
		for (String rawLine : lines) {
			if (rawLine.isEmpty() || rawLine.charAt(0) == '#') continue;
			if (rawLine.length() > 0 && (rawLine.charAt(0) == ' ' || rawLine.charAt(0) == '\t')) {
				// Continuation line
				if (inHeader) currentVal.append(rawLine.substring(1));
				continue;
			}
			if (inHeader) {
				map.put(currentKey.toString().trim(), currentVal.toString().trim());
				currentKey.setLength(0); currentVal.setLength(0);
			}
			int colon = rawLine.indexOf(':');
			if (colon <= 0) continue; // skip malformed lines
			currentKey.append(rawLine.substring(0, colon));
			currentVal.append(colon + 1 < rawLine.length() ? rawLine.substring(colon + 1) : "");
			inHeader = true;
		}
		if (inHeader) map.put(currentKey.toString().trim(), currentVal.toString().trim());
		return map;
	}

	private String formatDate(long ms) {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTimeInMillis(ms);
		String d = Integer.toString(cal.get(java.util.Calendar.DAY_OF_MONTH));
		String m = Integer.toString(cal.get(java.util.Calendar.MONTH) + 1);
		String y = Integer.toString(cal.get(java.util.Calendar.YEAR));
		if (d.length() == 1) d = "0" + d;
		if (m.length() == 1) m = "0" + m;
		return d + "." + m + "." + y;
	}

	private String formatSize(long bytes) {
		if (bytes < 1024 * 1024) {
			return Math.round(bytes / 1024.0) + " KB";
		}
		return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
	}

	private int textExtentCallCount;

	private java.util.List<String> wrapText(GC gc, String text, int maxWidth) {
		java.util.List<String> lines = new ArrayList<>();
		if (text == null || text.isEmpty()) { lines.add(""); return lines; }
		String[] words = text.split(" ");
		StringBuilder cur = new StringBuilder();
		for (String w : words) {
			String test = cur.length() == 0 ? w : cur + " " + w;
			textExtentCallCount++;
			if (gc.textExtent(test).x > maxWidth) {
				if (cur.length() > 0) {
					lines.add(cur.toString());
					cur = new StringBuilder();
				}
				textExtentCallCount++;
				if (gc.textExtent(w).x > maxWidth) {
					String remaining = w;
					while (!remaining.isEmpty()) {
						int breakAt = 0;
						for (int i = 1; i <= remaining.length(); i++) {
							textExtentCallCount++;
							if (gc.textExtent(remaining.substring(0, i)).x > maxWidth) {
								breakAt = i - 1;
								break;
							}
							breakAt = i;
						}
						if (breakAt <= 0) breakAt = 1;
						lines.add(remaining.substring(0, breakAt));
						remaining = remaining.substring(breakAt);
					}
				} else {
					cur = new StringBuilder(w);
				}
			} else {
				cur = new StringBuilder(test);
			}
		}
		if (cur.length() > 0) lines.add(cur.toString());
		return lines;
	}

	private Image getMidletIcon(File jar, MidletInfo info) {
		int sz = Math.max(4, currentIconSize);
		writeDebugLog("[getMidletIcon] jar=" + (jar != null ? jar.getName() : "null") + " iconPath=" + info.iconPath + " iconBytes=" + (info.iconBytes != null ? info.iconBytes.length + "b" : "null"));
		if (info.iconBytes == null && info.iconPath != null && jar != null) {
			info.iconBytes = loadIconBytes(jar, info.iconPath);
			writeDebugLog("[getMidletIcon] after loadIconBytes: iconBytes=" + (info.iconBytes != null ? info.iconBytes.length + "b" : "null"));
		}
		if (info.iconBytes != null) {
			try {
				ImageData data = new ImageData(new ByteArrayInputStream(info.iconBytes));
				data = data.scaledTo(sz, sz);
				writeDebugLog("[getMidletIcon] ImageData created OK, size=" + data.width + "x" + data.height);
				return new Image(display, toDirectColor(data));
			} catch (Exception e) {
				writeDebugLog("[getMidletIcon] ImageData failed: " + e);
			}
		} else {
			writeDebugLog("[getMidletIcon] iconBytes is null after load attempt, using fallback");
		}
		return loadFallbackIcon();
	}

	private ImageData toDirectColor(ImageData data) {
		if (data.palette == null || data.palette.isDirect) return data;
		RGB[] colors = data.palette.getRGBs();
		if (colors == null) return data;
		int w = data.width, h = data.height;
		PaletteData directPal = new PaletteData(0xFF0000, 0xFF00, 0xFF);
		ImageData out = new ImageData(w, h, 24, directPal);
		int tp = data.transparentPixel;
		int[] rowBuf = new int[w];
		byte[] alphaLine = new byte[w];
		for (int y = 0; y < h; y++) {
			data.getPixels(0, y, w, rowBuf, 0);
			data.getAlphas(0, y, w, alphaLine, 0);
			for (int x = 0; x < w; x++) {
				int p = rowBuf[x];
				if (tp >= 0 && p == tp) {
					alphaLine[x] = 0;
					rowBuf[x] = 0;
				} else if (p >= 0 && p < colors.length) {
					RGB rgb = colors[p];
					rowBuf[x] = directPal.getPixel(rgb);
				} else {
					rowBuf[x] = 0;
				}
			}
			out.setPixels(0, y, w, rowBuf, 0);
			out.setAlphas(0, y, w, alphaLine, 0);
		}
		return out;
	}

	private Image loadFallbackIcon() {
		int sz = Math.max(4, currentIconSize);
		if (fallbackIcon != null && !fallbackIcon.isDisposed()) {
			Rectangle b = fallbackIcon.getBounds();
			if (b.width == sz && b.height == sz) return fallbackIcon;
			fallbackIcon.dispose();
			fallbackIcon = null;
		}
		try {
			InputStream is = getClass().getResourceAsStream("/unknown.png");
			if (is != null) {
				ImageData data = new ImageData(is);
				is.close();
				data = data.scaledTo(sz, sz);
				fallbackIcon = new Image(display, toDirectColor(data));
				return fallbackIcon;
			}
		} catch (Exception e) {
			// fall through to programmatic fallback
		}
		// Programmatic fallback: gray diagonal stripe pattern
		PaletteData pal = new PaletteData(0xFF0000, 0xFF00, 0xFF);
		ImageData data = new ImageData(sz, sz, 24, pal);
		int[] row = new int[sz];
		for (int y = 0; y < sz; y++) {
			for (int x = 0; x < sz; x++)
				row[x] = (x / 4 + y / 4) % 2 == 0 ? 0xAAAAAA : 0x666666;
			data.setPixels(0, y, sz, row, 0);
		}
		fallbackIcon = new Image(display, data);
		return fallbackIcon;
	}

	private byte[] loadIconBytes(File jar, String iconPath) {
		if (iconPath == null || iconPath.isEmpty()) return null;
		String name = iconPath.replace('\\', '/');
		while (name.startsWith("./") || name.startsWith(".\\")) name = name.substring(2);
		while (name.length() > 0 && name.charAt(0) == '/') name = name.substring(1);
		// Primary: org.apache.tools.zip.ZipFile (same as Application Settings runtime)
		ZipFile zf = null;
		try {
			zf = new ZipFile(jar);
			byte[] result = readIconBytesFromZipFile(zf, name);
			if (result != null) { zf.close(); return result; }
			writeDebugLog("[WARN] loadIconBytes ZipFile returned null for " + jar.getName() + " name=" + name);
			zf.close(); zf = null;
		} catch (Exception e) {
			if (zf != null) try { zf.close(); } catch (Exception ignored) {}
			writeDebugLog("[WARN] loadIconBytes ZipFile failed for " + jar.getName() + ": " + e);
		}
		// Fallback: ZipInputStream for severely corrupted ZIPs
		try {
			byte[] result = readIconBytesFromZipStream(jar, name);
			if (result != null) return result;
			int slash = name.lastIndexOf('/');
			String fname = slash >= 0 ? name.substring(slash + 1) : name;
			result = readIconBytesFromZipStreamByFilename(jar, fname);
			if (result != null) return result;
		} catch (Exception e2) {
			writeDebugLog("[WARN] loadIconBytes ZipInputStream fallback failed for " + jar.getName() + ": " + e2);
		}
		writeDebugLog("[WARN] loadIconBytes all attempts failed for " + jar.getName() + " name=" + name);
		return null;
	}

	private byte[] readIconBytesFromZipFile(ZipFile zf, String name) throws Exception {
		org.apache.tools.zip.ZipEntry entry = zf.getEntry(name);
		if (entry == null) {
			// Some JARs use leading / in entry names
			String alt = name;
			while (alt.length() > 0 && alt.charAt(0) == '/') alt = alt.substring(1);
			if (!alt.equals(name)) entry = zf.getEntry(alt);
			else entry = zf.getEntry("/" + name);
		}
		if (entry == null) {
			Enumeration<org.apache.tools.zip.ZipEntry> entries = zf.getEntries();
			while (entries.hasMoreElements()) {
				org.apache.tools.zip.ZipEntry ze = entries.nextElement();
				String en = ze.getName();
				while (en.length() > 0 && en.charAt(0) == '/') en = en.substring(1);
				if (en.equalsIgnoreCase(name)) { entry = ze; break; }
			}
		}
		if (entry == null) {
			int slash = name.lastIndexOf('/');
			String fname = slash >= 0 ? name.substring(slash + 1) : name;
			Enumeration<org.apache.tools.zip.ZipEntry> entries = zf.getEntries();
			while (entries.hasMoreElements()) {
				org.apache.tools.zip.ZipEntry ze = entries.nextElement();
				String en = ze.getName();
				while (en.length() > 0 && en.charAt(0) == '/') en = en.substring(1);
				int es = en.lastIndexOf('/');
				String ef = es >= 0 ? en.substring(es + 1) : en;
				if (ef.equalsIgnoreCase(fname)) { entry = ze; break; }
			}
		}
		if (entry == null) return null;
		try (InputStream is = zf.getInputStream(entry)) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];
			int n;
			while ((n = is.read(buf)) >= 0) bos.write(buf, 0, n);
			return bos.toByteArray();
		}
	}

	private byte[] readIconBytesFromZipStream(File jar, String name) throws Exception {
		try (FileInputStream fis = new FileInputStream(jar);
			 java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(fis)) {
			java.util.zip.ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) {
				String en = ze.getName();
				while (en.length() > 0 && en.charAt(0) == '/') en = en.substring(1);
				if (en.equalsIgnoreCase(name)) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] buf = new byte[4096];
					int n;
					while ((n = zis.read(buf)) >= 0) bos.write(buf, 0, n);
					return bos.toByteArray();
				}
			}
		}
		return null;
	}

	private byte[] readIconBytesFromZipStreamByFilename(File jar, String fname) throws Exception {
		try (FileInputStream fis = new FileInputStream(jar);
			 java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(fis)) {
			java.util.zip.ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) {
				String en = ze.getName();
				while (en.length() > 0 && en.charAt(0) == '/') en = en.substring(1);
				int es = en.lastIndexOf('/');
				String ef = es >= 0 ? en.substring(es + 1) : en;
				if (ef.equalsIgnoreCase(fname)) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] buf = new byte[4096];
					int n;
					while ((n = zis.read(buf)) >= 0) bos.write(buf, 0, n);
					return bos.toByteArray();
				}
			}
		}
		return null;
	}

	/** Rebuild cache from a specific directory. */
	private void loadJarCacheDir(String dir) {
		cachedJarFiles.clear();
		currentJarIndex = -1;
		if (dir == null || dir.isEmpty()) return;
		File parentDir = new File(dir);
		if (!parentDir.isDirectory()) return;
		File[] jars = parentDir.listFiles((d, name) -> name.toLowerCase().endsWith(".jar"));
		if (jars == null || jars.length == 0) return;
		Arrays.sort(jars, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
		for (File f : jars) cachedJarFiles.add(f);
		currentJarIndex = 0;
	}

	private void moveSelectedToFavs() {
		int idx = selectedRowIdx;
		if (idx < 0 || idx >= favorites.size()) {
			showDialog(shell, "No Selection", "Select a JAR to copy to favorites.", SWT.OK);
			return;
		}
		File src = favorites.get(idx);
		File destDir = new File(originalFavoritesPath != null ? originalFavoritesPath : Settings.favoritesPath);
		destDir.mkdirs();
		File dest = new File(destDir, src.getName());
		if (dest.exists()) {
			if (showDialog(shell, "Already Exists", src.getName() + " already exists in favorites. Overwrite?", SWT.YES | SWT.NO) != SWT.YES) {
				return;
			}
		}
		try {
			java.nio.file.Files.copy(src.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			refreshList();
			if (shell != null && !shell.isDisposed()) {
				shell.layout(true, true);
			}
			if ("icons".equals(Settings.favoritesViewMode) && favorites.size() > 0 && iconCanvas != null && !iconCanvas.isDisposed()) {
				selectedRowIdx = 0;
				iconCanvas.redraw();
				iconCanvas.update();
			}
		} catch (IOException e) {
			showDialog(shell, "Error", "Failed to copy: " + e.getMessage(), SWT.OK);
		}
	}

	private void updateButtonStates() {
		boolean jarLoaded = Emulator.midletJarPath != null;
		boolean hasCache = !cachedJarFiles.isEmpty();
		prevJarButton.setEnabled(hasCache && currentJarIndex > 0);
		currentJarButton.setEnabled(hasCache && currentJarIndex >= 0);
		nextJarButton.setEnabled(hasCache && currentJarIndex >= 0 && currentJarIndex < cachedJarFiles.size() - 1);
		swapDirButton.setEnabled(hasCache);
		if (isSwapped) {
			moveButton.setText("Move Selected to Fav's");
			moveButton.setToolTipText("Copy selected JAR back to the original favorites folder");
		} else {
			moveButton.setText("Move Current Here");
			moveButton.setToolTipText("Move current JAR to this folder (after restart)");
		}
		moveButton.getParent().layout();
		moveButton.setEnabled(isSwapped || jarLoaded);
		for (Button btn : toolbarButtons) {
			if (btn != null && !btn.isDisposed() && "Remove".equals(btn.getText())) {
				btn.setEnabled(!isSwapped);
				break;
			}
		}
	}

	public static void executePendingMoves() {
		String pending = Settings.pendingFavoriteMoves;
		if (pending == null || pending.isEmpty()) return;

		String[] entries = pending.split(";;");
		StringBuilder remaining = new StringBuilder();

		for (String entry : entries) {
			String[] parts = entry.split("\\|\\|", 2);
			if (parts.length != 2) continue;
			File src = new File(parts[0]);
			File dest = new File(parts[1]);

			synchronized (Emulator.jarFileLock) {
				if (Emulator.midletJar != null && Emulator.midletJarPath != null) {
					File cur = new File(Emulator.midletJarPath);
					if (cur.getAbsolutePath().equalsIgnoreCase(src.getAbsolutePath())) {
						try { Emulator.midletJar.close(); } catch (IOException ignored) {}
						Emulator.midletJar = null;
					}
				}
			}

			if (src.exists() && !dest.exists()) {
				boolean ok = false;
				try {
					java.nio.file.Files.move(src.toPath(), dest.toPath(),
						java.nio.file.StandardCopyOption.REPLACE_EXISTING);
					ok = true;
				} catch (IOException ignored) {}
				if (!ok) {
					ok = src.renameTo(dest);
				}
				if (ok || !src.exists()) continue;
			} else if (src.exists() && dest.exists()) {
				if (src.delete()) continue;
			}

			if (src.exists()) {
				if (remaining.length() > 0) remaining.append(";;");
				remaining.append(entry);
			}
		}

		Settings.pendingFavoriteMoves = remaining.length() > 0 ? remaining.toString() : null;
	}

	private void removeSelected() {
		int idx = selectedRowIdx;
		if (idx < 0 || idx >= favorites.size()) {
			showDialog(shell, "No Selection", "Select a favorite to remove.", SWT.OK);
			return;
		}
		File jar = favorites.get(idx);
		if (showDialog(shell, "Remove Favorite", "Remove " + jar.getName() + " from favorites?", SWT.YES | SWT.NO) != SWT.YES) {
			return;
		}
		removeJarFile(jar);
	}

	private void removeJarFile(File jar) {
		synchronized (Emulator.jarFileLock) {
			if (Emulator.midletJar != null && Emulator.midletJarPath != null) {
				File cur = new File(Emulator.midletJarPath);
				if (cur.getAbsolutePath().equalsIgnoreCase(jar.getAbsolutePath())) {
					try { Emulator.midletJar.close(); } catch (IOException ignored) {}
					Emulator.midletJar = null;
				}
			}
		}
		if (jar.delete()) {
			stopGlow();
			refreshList();
			if (shell != null && !shell.isDisposed()) {
				shell.layout(true, true);
			}
			if ("icons".equals(Settings.favoritesViewMode) && iconCanvas != null && !iconCanvas.isDisposed()) {
				if (favorites.size() > 0) {
					selectedRowIdx = 0;
				}
				iconCanvas.redraw();
				iconCanvas.update();
			}
		}
	}

	private void openSelected() {
		stopGlow();
		int idx = selectedRowIdx;
		if (idx < 0 || idx >= favorites.size()) {
			return;
		}
		openJar(favorites.get(idx));
	}

	private void openJar(final File jar) {
		String path = jar.getAbsolutePath();
		Thread t = new Thread("JAR-Open") {
			public void run() {
				Emulator.loadGame(path, false);
			}
		};
		t.setDaemon(true);
		t.start();
	}

	private void openContainingFolder(File file) {
		if (file == null || !file.exists()) return;
		try {
			if (Utils.win) {
				Runtime.getRuntime().exec(new String[] {
					"cmd.exe", "/c", "explorer.exe /select,\"" + file.getAbsolutePath() + "\""
				});
			} else {
				String parent = file.getParent();
				if (parent != null) {
					Runtime.getRuntime().exec(new String[] {
						"xdg-open", parent
					});
				}
			}
		} catch (Exception ex) {
			System.err.println("openContainingFolder: " + ex.getMessage());
		}
	}

	// ── Icon Canvas event handlers ──────────────────────────────

	private void onIconCanvasPaint(Event e) {
		textExtentCallCount = 0;
		long t0 = System.nanoTime();
		GC gc = e.gc;
		Rectangle ca = iconCanvas.getClientArea();
		gc.setBackground(bg());
		gc.fillRectangle(ca);
		Rectangle clip = gc.getClipping();
		int cols = iconGridCols;
		if (cols <= 0) return;
		int pad = listPadding;
		int iconSize = iconGridIconSize;
		int totalCellW = pad <= 2 ? ca.width / cols : iconSize + pad;
		if (totalCellW <= 0) { System.out.println("[DBG] ICON PAINT: totalCellW<=0, return"); return; }
		gc.setFont(listFont != null && !listFont.isDisposed() ? listFont : display.getSystemFont());
		int lineH = gc.textExtent("Ag").y;
		int rowH = iconSize + pad;
		if (Settings.favoritesIconsShowNames) rowH += lineH + 4;
		if (rowH <= 0) rowH = 1;
		long t1 = System.nanoTime();

		int totalRows = (favorites.size() + cols - 1) / cols;
		int firstRow = Math.max(0, iconScrollOffset / rowH);
		int cellTextW = Math.min(iconSize, totalCellW) - 6;
		if (cellTextW < 10) cellTextW = 10;

		// Pass 1: compute row heights and cache wrapText results
		int visRows = clip.height / Math.max(rowH, 1) + 3;
		int maxRowCount = Math.min(totalRows - firstRow, visRows);
		int[] rowY = new int[maxRowCount];
		int[] rowHTable = new int[maxRowCount];
		java.util.List<String>[] wrapCache = new java.util.List[maxRowCount * cols];
		int rowCount = 0;
		int curY = firstRow * rowH - iconScrollOffset;
		{
			for (int i = 0; i < maxRowCount; i++) {
				int r = firstRow + i;
				int startIdx = r * cols;
				int maxLines = 1;
				for (int c2 = 0; c2 < cols; c2++) {
					int fidx = startIdx + c2;
					int wi = i * cols + c2;
					if (fidx >= favorites.size()) { wrapCache[wi] = null; continue; }
					File fj = favorites.get(fidx);
					MidletInfo mi = metadataCache.get(fj);
					String fn = mi != null && mi.name != null ? mi.name : fj.getName().replace(".jar", "");
					wrapCache[wi] = wrapText(gc, fn, cellTextW);
					int lns = wrapCache[wi].size();
					if (lns > maxLines) maxLines = lns;
				}
				int rh = iconSize + pad;
				if (Settings.favoritesIconsShowNames) rh += maxLines * lineH + 4;
				rowY[i] = curY;
				rowHTable[i] = rh;
				curY += rh;
				rowCount++;
				if (curY > clip.height) break;
			}
		}
		int lastRow = Math.min(totalRows, firstRow + rowCount);
		lastPaintFirstRow = firstRow;
		lastPaintLastRow = lastRow;
		lastPaintRowY = rowY;
		lastPaintRowH = rowHTable;
		if (rowCount > 0) {
			int bottomScroll = curY + iconScrollOffset;
			iconTotalHeight = bottomScroll + (totalRows - lastRow) * rowH;
		} else {
			iconTotalHeight = totalRows * rowH;
		}

		long t2 = System.nanoTime();
		boolean needAsyncLoad = false;

		long drawWrapTime = 0;
		long drawTextTime = 0;
		int drawItemCount = 0;
		for (int ri = 0; ri < rowCount; ri++) {
			int r = firstRow + ri;
			int y0 = rowY[ri];
			int actualRowH = rowHTable[ri];
			int startIdx = r * cols;

			for (int c = 0; c < cols; c++) {
				int flatIdx = startIdx + c;
				int cx = c * totalCellW;

				if (flatIdx >= favorites.size()) {
					gc.setBackground(bg());
					gc.fillRectangle(cx, y0, totalCellW, actualRowH);
					continue;
				}

				gc.setBackground(bg());
				gc.fillRectangle(cx, y0, totalCellW, actualRowH);

				if (flatIdx != selectedRowIdx && flatIdx == hoveredRowIdx) {
					gc.setBackground(getHoverFallbackColor());
					gc.fillRectangle(cx, y0, totalCellW, actualRowH);
				}

				// When names are shown, draw selection as solid fill BEHIND icon/text
				// to avoid GDI alpha corruption from gc.setAlpha() that breaks text rendering on Windows
				if (flatIdx == selectedRowIdx && Settings.favoritesIconsShowNames) {
					if (glowActive) {
						long elapsed = System.currentTimeMillis() - glowStartTime;
						double phase = (elapsed % 2000) / 1000.0 * Math.PI;
						int glowR = darkMode ? 200 : 255;
						int glowG = darkMode ? (60 + (int)((Math.sin(phase) + 1) / 2 * 120)) : (100 + (int)((Math.sin(phase) + 1) / 2 * 155));
						Color glowColor = new Color(display, glowR, glowG, 0);
						gc.setBackground(glowColor);
						gc.fillRectangle(cx, y0, totalCellW, actualRowH);
						glowColor.dispose();
					} else {
						Color anim = getAnimRowColor(flatIdx);
						if (anim == null) anim = getSelFallbackColor();
						gc.setBackground(anim);
						gc.fillRectangle(cx, y0, totalCellW, actualRowH);
					}
				}

				File fj = favorites.get(flatIdx);
				MidletInfo mi = metadataCache.get(fj);

				int drawSize = iconSize;
				int ix = cx + (totalCellW - drawSize) / 2;
				int iy = y0 + (pad / 2);

				Image icon = mi != null ? mi.iconImage : null;
				if (icon != null && !icon.isDisposed()) {
					gc.drawImage(icon, ix, iy);
				} else {
					if (mi != null) {
						needAsyncLoad = true;
					}
					gc.setBackground(widgetBg());
					gc.fillRectangle(ix, iy, drawSize, drawSize);
				}

				if (Settings.favoritesIconsShowNames) {
					String fname = mi != null && mi.name != null ? mi.name : fj.getName().replace(".jar", "");
					gc.setBackground(bg());
					gc.setFont(listFont);
					gc.setForeground(fg());
					int ty = iy + drawSize + 2;
					Rectangle saveClip = gc.getClipping();
					gc.setClipping(cx, y0, totalCellW, actualRowH);
					java.util.List<String> lines = wrapCache[ri * cols + c];
					for (int li = 0; li < lines.size(); li++) {
						String line = lines.get(li);
						int lw = gc.textExtent(line).x;
						gc.drawText(line, cx + (totalCellW - lw) / 2, ty + li * lineH, true);
					}
					gc.setClipping(saveClip);
					drawItemCount++;
				} else if (flatIdx == selectedRowIdx) {
					if (glowActive) {
						// Names off: glow as 50% alpha overlay on top of icon (no text to corrupt)
						long elapsed = System.currentTimeMillis() - glowStartTime;
						double phase = (elapsed % 2000) / 1000.0 * Math.PI;
						int glowR = darkMode ? 200 : 255;
						int glowG = darkMode ? (60 + (int)((Math.sin(phase) + 1) / 2 * 120)) : (100 + (int)((Math.sin(phase) + 1) / 2 * 155));
						Color glowColor = new Color(display, glowR, glowG, 0);
						gc.setBackground(glowColor);
						gc.setAlpha(127);
						gc.fillRectangle(cx, y0, totalCellW, actualRowH);
						gc.setAlpha(255);
						glowColor.dispose();
					} else {
						// No names: draw selection as alpha overlay on top of icon (no text to corrupt)
						Color anim = getAnimRowColor(flatIdx);
						if (anim == null) anim = getSelFallbackColor();
						gc.setBackground(anim);
						gc.setAlpha(191);
						gc.fillRectangle(cx, y0, totalCellW, actualRowH);
						gc.setAlpha(255);
					}
				}
			}
		}
		long t3 = System.nanoTime();

		if (needAsyncLoad && !loadingIcons) {
			iconLoadGeneration++;
			// Flush icons not in current visible range so stale icons don't linger
			int flushFirst = Math.max(0, firstRow - (visRows + 1));
			int flushLast = Math.min(totalRows, lastRow + (visRows + 1));
			for (int fi = 0; fi < favorites.size(); fi++) {
				int r = fi / cols;
				if (r >= flushFirst && r < flushLast) continue;
				File fj2 = favorites.get(fi);
				MidletInfo mi2 = metadataCache.get(fj2);
				if (mi2 != null && mi2.iconImage != null && !mi2.iconImage.isDisposed()) {
					mi2.iconImage.dispose();
					mi2.iconImage = null;
				}
			}
			loadingIcons = true;
			display.timerExec(10, new IconLoadBatch(firstRow, iconLoadGeneration));
		}

		if (DEBUG_PERF) {
			System.out.println("[PERF] onIconCanvasPaint total_us=" + ((t3-t0)/1000) + " setup_us=" + ((t1-t0)/1000) + " rowHeights_us=" + ((t2-t1)/1000) + " draw_us=" + ((t3-t2)/1000) + " drawItems=" + drawItemCount + " wrapCacheHits=" + drawItemCount + " drawTextExtent_us=" + (drawTextTime/1000) + " textExtentCalls=" + textExtentCallCount + " firstRow=" + firstRow + " lastRow=" + lastRow + " cols=" + cols);
		}
	}

	private boolean loadingIcons;

	private class IconLoadBatch implements Runnable {
		final int gen;
		int cursor;
		IconLoadBatch(int startRow, int gen) {
			this.gen = gen;
			this.cursor = startRow * iconGridCols;
		}
		public void run() {
			try {
				if (gen != iconLoadGeneration) return;
				if (!loadingIcons || iconCanvas == null || iconCanvas.isDisposed() || iconGridCols <= 0 || !"icons".equals(Settings.favoritesViewMode)) {
					if (gen == iconLoadGeneration) loadingIcons = false;
					return;
				}
				int cols = iconGridCols;
				int totalItems = favorites.size();

				int rowH = getIconRowHeight();
				if (rowH <= 0) { if (gen == iconLoadGeneration) loadingIcons = false; return; }
				Rectangle ca = iconCanvas.getClientArea();
				int curFirstRow = Math.max(0, iconScrollOffset / rowH);
				int curLastRow = Math.min((totalItems + cols - 1) / cols, curFirstRow + ca.height / rowH + 2);

				int viewEndFlat = Math.min(totalItems, curLastRow * cols);
				int viewStartFlat = curFirstRow * cols;

				// Evict icons more than 3 viewports away from current scroll
				int viewRows = ca.height / rowH + 1;
				int evictBefore = Math.max(0, curFirstRow - viewRows * 3);
				int evictAfter = Math.min((totalItems + cols - 1) / cols, curLastRow + viewRows * 3);
				for (int i = 0; i < totalItems; i++) {
					File j = favorites.get(i);
					MidletInfo mi = metadataCache.get(j);
					if (mi == null || mi.iconImage == null || mi.iconImage.isDisposed()) continue;
					int row = i / cols;
					if (row < evictBefore || row > evictAfter) {
						mi.iconImage.dispose();
						mi.iconImage = null;
					}
				}

				// Reset cursor if viewport scrolled past it
				if (cursor >= viewEndFlat) {
					cursor = viewStartFlat;
				}

				boolean loaded = false;
				int limit = Math.min(20, viewEndFlat - cursor);
				for (int i = 0; i < limit && cursor < viewEndFlat && gen == iconLoadGeneration; i++) {
					int idx = cursor++;
					if (idx >= totalItems) break;
					File j = favorites.get(idx);
					MidletInfo mi = metadataCache.get(j);
					if (mi != null && (mi.iconImage == null || mi.iconImage.isDisposed())) {
						int oldSz = currentIconSize;
						currentIconSize = iconGridIconSize;
						Image img = null;
						try {
							img = getMidletIcon(j, mi);
						} catch (Exception e) {
							// ignore, img stays null
						} finally {
							currentIconSize = oldSz;
						}
						if (img != null) {
							if (mi.iconImage != null && mi.iconImage != img && !mi.iconImage.isDisposed())
								mi.iconImage.dispose();
							mi.iconImage = img;
							loaded = true;
						}
					}
				}
				if (gen != iconLoadGeneration) return;
				if (iconCanvas != null && !iconCanvas.isDisposed()) {
					if (loaded) iconCanvas.redraw();
					if (cursor < viewEndFlat && gen == iconLoadGeneration) {
						display.timerExec(10, this);
						return;
					}
				}
			} catch (Exception e) {
				// ignore
			}
			if (gen == iconLoadGeneration) {
				loadingIcons = false;
			}
			if (iconCanvas != null && !iconCanvas.isDisposed()) {
				iconCanvas.redraw();
			}
		}
	}

	private void onIconCanvasMouseDown(Event e) {
		restoreDefaultStatus();
		if (iconCanvas == null || iconCanvas.isDisposed()) return;
		int cols = iconGridCols;
		if (cols <= 0) return;
		Rectangle ca = iconCanvas.getClientArea();
		int totalCellW = listPadding <= 2 ? ca.width / cols : iconGridIconSize + listPadding;
		if (totalCellW <= 0) return;
		int col = e.x / totalCellW;
		if (col < 0 || col >= cols) return;
		int row = iconCanvasYToRow(e.y);
		if (row < 0) return;

		int flatIdx = row * cols + col;
		if (flatIdx < 0 || flatIdx >= favorites.size()) return;
		if (e.button == 3) {
			selectedRowIdx = flatIdx;
			selectedGridCol = col;
			updateStatusAndInfo(flatIdx);
			iconCanvas.redraw();
			return;
		}
		if (e.button != 1) return;
		selectedRowIdx = flatIdx;
		selectedGridCol = col;
		updateStatusAndInfo(flatIdx);
		iconCanvas.redraw();
	}

	private void onIconCanvasMouseDoubleClick(Event e) {
		restoreDefaultStatus();
		if (iconCanvas == null || iconCanvas.isDisposed()) return;
		int cols = iconGridCols;
		if (cols <= 0) return;
		Rectangle ca = iconCanvas.getClientArea();
		int totalCellW = listPadding <= 2 ? ca.width / cols : iconGridIconSize + listPadding;
		if (totalCellW <= 0) return;
		int col = e.x / totalCellW;
		if (col < 0 || col >= cols) return;
		int row = iconCanvasYToRow(e.y);
		if (row < 0) return;
		int flatIdx = row * cols + col;
		if (flatIdx < 0 || flatIdx >= favorites.size()) return;
		if (e.button == 3) {
			selectedRowIdx = flatIdx;
			selectedGridCol = col;
			openContainingFolder(favorites.get(flatIdx));
			startGlow();
			return;
		}
		if (e.button != 1) return;
		selectedRowIdx = flatIdx;
		selectedGridCol = col;
		openSelected();
	}

	private boolean isIconCoordOnItem(int mx, int my) {
		if (iconCanvas == null || iconCanvas.isDisposed()) return false;
		int cols = iconGridCols;
		if (cols <= 0) return false;
		Rectangle ca = iconCanvas.getClientArea();
		int totalCellW = listPadding <= 2 ? ca.width / cols : iconGridIconSize + listPadding;
		if (totalCellW <= 0) return false;
		int col = mx / totalCellW;
		if (col < 0 || col >= cols) return false;
		int row = iconCanvasYToRow(my);
		if (row < 0) return false;
		int flatIdx = row * cols + col;
		return flatIdx < favorites.size();
	}

	private int iconCanvasYToRow(int canvasY) {
		if (lastPaintRowY != null && lastPaintRowH != null) {
			for (int i = 0; i < lastPaintRowY.length; i++) {
				if (canvasY >= lastPaintRowY[i] && canvasY < lastPaintRowY[i] + lastPaintRowH[i]) {
					return lastPaintFirstRow + i;
				}
			}
		}
		int rowH = getIconRowHeight();
		if (rowH <= 0) return -1;
		return (canvasY + iconScrollOffset) / rowH;
	}

	private void onIconCanvasMouseWheel(Event e) {
		restoreDefaultStatus();
		if (iconCanvas == null || iconCanvas.isDisposed()) return;
		e.doit = false;
		int rowH = getIconRowHeight();
		if (rowH <= 0) return;
		int totalH = iconTotalHeight > 0 ? iconTotalHeight : ((favorites.size() + iconGridCols - 1) / iconGridCols) * rowH;
		int viewH = iconCanvas.getClientArea().height;
		int maxOffset = Math.max(0, totalH - viewH);
		int lines = e.count > 0 ? 1 : (e.count < 0 ? -1 : 0);
		iconScrollOffset = Math.max(0, Math.min(maxOffset, iconScrollOffset - lines * rowH));
		loadingIcons = false;
		updateIconScrollBar();
		iconCanvas.redraw();
	}

	private void updateIconCanvasLayout() {
		if (iconCanvas == null || iconCanvas.isDisposed()) return;
		Rectangle ca = iconCanvas.getClientArea();
		System.out.println("[DBG] updateIconCanvasLayout: ENTER ca=(" + ca.width + "," + ca.height + ") favSize=" + favorites.size() + " cols=" + iconGridCols + " iconGridIconSize=" + iconGridIconSize + " listPadding=" + listPadding + " iconScrollOffset=" + iconScrollOffset + " caller=" + Thread.currentThread().getStackTrace()[2].getMethodName());
		if (ca.width <= 0) {
			System.out.println("[DBG] updateIconCanvasLayout: EARLY RETURN (ca.width<=0)");
			return;
		}

		int oldCols = iconGridCols;
		iconGridIconSize = Math.max(32, Math.min(196, iconGridIconSize));
		int cellW = iconGridIconSize + listPadding;
		iconGridCols = Math.max(1, ca.width / cellW);
		if (listPadding <= 2) {
			iconGridCols = Math.max(1, ca.width / cellW) + 1;
		}
		System.out.println("[DBG] updateIconCanvasLayout: computed iconGridCols=" + iconGridCols + " (was " + oldCols + ")");

		iconTotalHeight = 0; // invalidate until next paint
		int oldOffset = iconScrollOffset;
		int rowH = getIconRowHeight();
		if (rowH > 0) {
			int totalH = ((favorites.size() + iconGridCols - 1) / iconGridCols) * rowH;
			int maxOff = Math.max(0, totalH - ca.height);
			iconScrollOffset = Math.max(0, Math.min(maxOff, iconScrollOffset));
		}
		System.out.println("[DBG] updateIconCanvasLayout: scrollOffset " + oldOffset + " -> " + iconScrollOffset + " rowH=" + rowH + " totalH=" + (((favorites.size() + iconGridCols - 1) / iconGridCols) * (rowH > 0 ? rowH : 1)));

		loadingIcons = false; // layout change may alter visible range, cancel any in-flight batch

		updateIconScrollBar();

		if (iconCanvas != null && !iconCanvas.isDisposed()) {
			System.out.println("[DBG] updateIconCanvasLayout: calling redraw + update");
			iconCanvas.redraw();
			iconCanvas.update();
		}
		System.out.println("[DBG] updateIconCanvasLayout: EXIT");
	}

	private void writeDebugLog(String msg) {
		try {
			java.nio.file.Path logPath = java.nio.file.Paths.get(System.getProperty("java.io.tmpdir"), "favorites_debug.log");
			java.nio.file.Files.write(logPath,
				(msg + System.lineSeparator()).getBytes(),
				java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
		} catch (Exception ignored) {}
	}

	private static byte[] stripBom(byte[] data) {
		if (data != null && data.length >= 3 && (data[0] & 0xFF) == 0xEF && (data[1] & 0xFF) == 0xBB && (data[2] & 0xFF) == 0xBF) {
			byte[] stripped = new byte[data.length - 3];
			System.arraycopy(data, 3, stripped, 0, stripped.length);
			return stripped;
		}
		return data;
	}

}
