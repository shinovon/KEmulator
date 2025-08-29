package emulator.ui.swt;

import javax.microedition.lcdui.Image;
import java.util.Comparator;
import java.util.IdentityHashMap;

final class ImagesComparatorByUsage implements Comparator<ImageViewItem> {
	private final MemoryView mv;

	ImagesComparatorByUsage(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public int compare(final ImageViewItem i1, final ImageViewItem i2) {
		Image o1 = i1.drawable;
		Image o2 = i2.drawable;
		int n = o1.getUsedCount() - o2.getUsedCount();
		return mv.getSortByAscending() ? n : -n;
	}
}

final class ImagesComparatorBySize implements Comparator<ImageViewItem> {
	private final MemoryView mv;

	ImagesComparatorBySize(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public int compare(final ImageViewItem i1, final ImageViewItem i2) {
		Image o1 = i1.drawable;
		Image o2 = i2.drawable;
		int size1 = o1.getWidth() * o1.getHeight();
		int size2 = o2.getWidth() * o2.getHeight();
		int n = size1 - size2;
		return mv.getSortByAscending() ? n : -n;
	}
}

final class ImagesComparatorByOrder implements Comparator<ImageViewItem> {
	private final MemoryView mv;
	private final IdentityHashMap<Image, Integer> positions;

	ImagesComparatorByOrder(final MemoryView mv) {
		super();
		this.mv = mv;
		positions = new IdentityHashMap<>(MemoryView.allImages.size());
		for (int i = 0; i < MemoryView.allImages.size(); i++) {
			positions.put(MemoryView.allImages.get(i), i);
		}
	}

	public int compare(final ImageViewItem i1, final ImageViewItem i2) {
		int index1 = positions.get(i1.drawable);
		int index2 = positions.get(i2.drawable);
		int n = index2 - index1;
		return mv.getSortByAscending() ? n : -n;
	}
}
