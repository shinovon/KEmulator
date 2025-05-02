package emulator.ui.swt;

import emulator.Settings;
import emulator.UILocale;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class ImageViewControls extends Composite implements SelectionListener, ModifyListener {

	private final Combo scale;
	private final Combo sortingMethod;
	private final Button drawDrawn;
	private final Button drawNonDrawn;
	private final Button darkenUnused;
	private final Button infos;
	private final Button reverseSort;
	private final Button drawReleased;
	private final Button draw2d;
	private final Button draw3d;
	private final Button clearReleasedBtn;

	private final CLabel totalPixelsLabel;
	private final MemoryView mv;

	public ImageViewControls(Composite parent, MemoryView mv) {
		super(parent, 0);
		this.mv = mv;
		RowLayout rl = new RowLayout();
		rl.wrap = true;
		rl.center = true;
		rl.type = SWT.HORIZONTAL;
		rl.spacing = 2;
		setLayout(rl);

		scale = new Combo(this, 8);
		initZoomCombo();

		new CLabel(this, 0).setText(UILocale.get("MEMORY_VIEW_SORT", "Sort:"));
		sortingMethod = new Combo(this, 8);
		initSortByCombo();

		reverseSort = new Button(this, 32);
		reverseSort.setText(UILocale.get("MEMORY_VIEW_ASCEND", "Ascending"));
		reverseSort.setSelection(true);
		reverseSort.addSelectionListener(this);
		reverseSort.setLayoutData(new RowData(90, SWT.DEFAULT));

		Group displays = new Group(this, 0);
		displays.setText("Display");
		RowLayout rl2 = new RowLayout();
		rl2.center = true;
		rl2.wrap = false;
		rl2.spacing = 5;
		displays.setLayout(rl2);
		{
			infos = new Button(displays, 32);
			infos.setText("Infos");
			infos.setSelection(false);
			infos.addSelectionListener(this);

			darkenUnused = new Button(displays, 32);
			darkenUnused.setText(UILocale.get("MEMORY_VIEW_UNUSED_REGION", "Used regions"));
			darkenUnused.setSelection(false);
			darkenUnused.addSelectionListener(this);

			Button clearUsageBtn = new Button(displays, 8388608);
			clearUsageBtn.setText(UILocale.get("MEMORY_VIEW_RESET_IMAGE", "Reset usage"));
			clearUsageBtn.addSelectionListener(new ResetUsageListener(mv));
			clearUsageBtn.setLayoutData(new RowData(SWT.DEFAULT, 20));
		}

		Group filters = new Group(this, 0);
		filters.setText("Filters");
		filters.setLayout(new GridLayout(5, false));
		{
			drawDrawn = new Button(filters, 32);
			drawDrawn.setText(UILocale.get("MEMORY_VIEW_IMAGES_DRAWN", "Used"));
			drawDrawn.setToolTipText("Show images those were drawn at least once");
			drawDrawn.setSelection(true);
			drawDrawn.addSelectionListener(this);

			drawNonDrawn = new Button(filters, 32);
			drawNonDrawn.setText(UILocale.get("MEMORY_VIEW_IMAGE_NEVER_DRAW", "Unused"));
			drawNonDrawn.setToolTipText("Show images those were not drawn yet");
			drawNonDrawn.setSelection(true);
			drawNonDrawn.addSelectionListener(this);

			draw2d = new Button(filters, 32);
			draw2d.setText("LCDUI");
			draw2d.setToolTipText("Show LCDUI images");
			draw2d.setSelection(true);
			draw2d.addSelectionListener(this);

			draw3d = new Button(filters, 32);
			draw3d.setText("3D");
			draw3d.setToolTipText("Show M3G and Micro3D textures");
			draw3d.setSelection(true);
			draw3d.addSelectionListener(this);

			drawReleased = new Button(filters, 32);
			drawReleased.setText(UILocale.get("MEMORY_VIEW_RELEASED_IMAGES", "Released"));
			drawReleased.setEnabled(Settings.recordReleasedImg);
			drawReleased.setSelection(false);
			drawReleased.addSelectionListener(this);
		}

		Group stats = new Group(this, 0);
		stats.setText("Statistics");
		stats.setLayout(new RowLayout());
		totalPixelsLabel = new CLabel(stats, 0);
		totalPixelsLabel.setText("? pixels");
		totalPixelsLabel.setLayoutData(new RowData(200, 20));

		clearReleasedBtn = new Button(this, 8388608);
		clearReleasedBtn.setText(UILocale.get("MEMORY_VIEW_CLEAR_RELEASED_IMAGES", "Clear Released Images"));
		clearReleasedBtn.setEnabled(Settings.recordReleasedImg);
		clearReleasedBtn.addSelectionListener(this);
	}

	private void initZoomCombo() {
		scale.add("50%");
		scale.add("100%");
		scale.add("200%");
		scale.add("300%");
		scale.add("400%");
		scale.setText("100%");
		scale.setToolTipText("Images zoom");
		scale.addModifyListener(this);
	}

	private void initSortByCombo() {
		sortingMethod.setEnabled(true);
		sortingMethod.add(UILocale.get("MEMORY_VIEW_SORT_REFERENCE", "Reference"));
		sortingMethod.add(UILocale.get("MEMORY_VIEW_SORT_SIZE", "Size"));
		sortingMethod.add(UILocale.get("MEMORY_VIEW_SORT_DRAW_COUNT", "Draw Count"));
		sortingMethod.setText(UILocale.get("MEMORY_VIEW_SORT_REFERENCE", "Reference"));
		sortingMethod.addModifyListener(this);
	}

	public void updateStats(int pixels2d, int pixels3d, int count2d, int count3d) {
		String text = "Total (LCDUI+3D): " + (pixels2d + pixels3d) + "px";
		totalPixelsLabel.setText(text);
		String t2d = "LCDUI: " + count2d + " images, " + pixels2d + "px (" + (pixels2d / 1024) + "kpx) in total";
		String t3d = "M3G+Micro3D: " + count3d + " images, " + pixels3d + "px (" + (pixels3d / 1024) + "kpx) in total";
		String tip = "To estimate memory usage, multiply numbers by 4.";
		String tip2 = "Real memory usage will differ per device due to different formats.";
		totalPixelsLabel.setToolTipText(t2d + "\n" + t3d + "\n\n" + tip + "\n" + tip2);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.widget == drawDrawn) {
			mv.setImagesDrawn(drawDrawn.getSelection());
		} else if (e.widget == drawNonDrawn) {
			mv.setImagesNotDrawn(drawNonDrawn.getSelection());
		} else if (e.widget == reverseSort) {
			mv.setImagesAscend(reverseSort.getSelection());
		} else if (e.widget == drawReleased) {
			mv.setShowReleasedImages(drawReleased.getSelection());
		} else if (e.widget == darkenUnused) {
			mv.setDarkenUnused(darkenUnused.getSelection());
		} else if (e.widget == clearReleasedBtn) {
			mv.memoryMgr.releasedImages.clear();
			mv.updateEverything();
		} else if (e.widget == infos) {
			mv.setShowImagesInfo(infos.getSelection());
		} else if (e.widget == draw2d) {
			mv.setShow2dImages(draw2d.getSelection());
		} else if (e.widget == draw3d) {
			mv.setShow3dImages(draw3d.getSelection());
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	@Override
	public void modifyText(ModifyEvent e) {
		if (e.widget == sortingMethod) {
			mv.setImagesSorting(sortingMethod.getSelectionIndex());
		} else if (e.widget == scale) {
			mv.setImagesScaling(scale.getSelectionIndex());
		}
	}
}
