package emulator.ui.swt;

import emulator.Settings;
import emulator.UILocale;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ImageViewControls extends Composite implements SelectionListener, ModifyListener {

	private final Combo scale;
	private final Combo sortingMethod;
	private final Button drawDrawn;
	private final Button drawNonDrawn;
	private final Button darkenUnused;
	private final Button reverseSort;
	private final Button drawReleased;
	private final Button clearReleasedBtn;

	private final CLabel totalPixelsLabel;
	private final MemoryView mv;

	public ImageViewControls(Composite parent, MemoryView mv) {
		super(parent, 0);
		this.mv = mv;
		GridLayout layout = new GridLayout(7, false);
		layout.marginHeight = 2;
		setLayout(layout);

		new CLabel(this, 0).setText(UILocale.get("MEMORY_VIEW_ZOOM", "Zoom:"));
		scale = new Combo(this, 8);
		initZoomCombo();
		scale.addModifyListener(this);

		new CLabel(this, 0).setText(UILocale.get("MEMORY_VIEW_SORT", "Sort:"));
		sortingMethod = new Combo(this, 8);
		initSortByCombo();
		sortingMethod.addModifyListener(this);

		drawDrawn = new Button(this, 32);
		drawDrawn.setText(UILocale.get("MEMORY_VIEW_IMAGES_DRAWN", "Images Drawn"));
		drawDrawn.setSelection(true);
		drawDrawn.addSelectionListener(this);

		darkenUnused = new Button(this, 32);
		darkenUnused.setText(UILocale.get("MEMORY_VIEW_UNUSED_REGION", "Darken Unused Regions"));
		darkenUnused.setSelection(false);
		darkenUnused.addSelectionListener(this);

		Button clearUsageBtn = new Button(this, 8388608);
		clearUsageBtn.setText(UILocale.get("MEMORY_VIEW_RESET_IMAGE", "Reset Image Usage"));
		clearUsageBtn.addSelectionListener(new ResetUsageListener(mv));

		new CLabel(this, 0).setText(UILocale.get("MEMORY_VIEW_SIZE", "Size"));

		totalPixelsLabel = new CLabel(this, 0);
		totalPixelsLabel.setLayoutData(new GridData(4, 2, false, false, 2, 1));

		reverseSort = new Button(this, 32);
		reverseSort.setText(UILocale.get("MEMORY_VIEW_ASCEND", "Ascending"));
		reverseSort.setSelection(true);
		reverseSort.addSelectionListener(this);

		drawNonDrawn = new Button(this, 32);
		drawNonDrawn.setText(UILocale.get("MEMORY_VIEW_IMAGE_NEVER_DRAW", "Images Never Drawn"));
		drawNonDrawn.setSelection(true);
		drawNonDrawn.addSelectionListener(this);

		drawReleased = new Button(this, 32);
		drawReleased.setText(UILocale.get("MEMORY_VIEW_RELEASED_IMAGES", "Released Images"));
		drawReleased.setEnabled(Settings.recordReleasedImg);
		drawReleased.setSelection(false);
		drawReleased.addSelectionListener(this);

		clearReleasedBtn = new Button(this, 8388608);
		clearReleasedBtn.setText(UILocale.get("MEMORY_VIEW_CLEAR_RELEASED_IMAGES", "Clear Released Images"));
		clearReleasedBtn.setEnabled(Settings.recordReleasedImg);
		clearReleasedBtn.addSelectionListener(this);
	}

	private void initZoomCombo() {
		this.scale.add("50%");
		this.scale.add("100%");
		this.scale.add("200%");
		this.scale.add("300%");
		this.scale.add("400%");
		this.scale.setText("100%");
	}

	private void initSortByCombo() {
		final GridData layoutData = new GridData();
		layoutData.horizontalAlignment = 1;
		layoutData.grabExcessHorizontalSpace = false;
		layoutData.verticalAlignment = 2;
		sortingMethod.setEnabled(true);
		sortingMethod.setLayoutData(layoutData);
		sortingMethod.add(UILocale.get("MEMORY_VIEW_SORT_REFERENCE", "Reference"));
		sortingMethod.add(UILocale.get("MEMORY_VIEW_SORT_SIZE", "Size"));
		sortingMethod.add(UILocale.get("MEMORY_VIEW_SORT_DRAW_COUNT", "Draw Count"));
		sortingMethod.setText(UILocale.get("MEMORY_VIEW_SORT_REFERENCE", "Reference"));
	}

	public void updateStats(int pixels) {
		totalPixelsLabel.setText(pixels + " pixels");
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
