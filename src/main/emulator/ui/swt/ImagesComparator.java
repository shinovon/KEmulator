package emulator.ui.swt;

import javax.microedition.lcdui.Image;
import java.util.Comparator;

final class ImagesComparator implements Comparator<ImageViewItem> {
	private final MemoryView mv;

	ImagesComparator(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final int compare(final ImageViewItem i1, final ImageViewItem i2) {
		Image o1 = i1.image;
		Image o2 = i2.image;
		int n = 0;
		Label_0142:
		{
			int usedCount = 0;
			int usedCount2 = 0;
			switch (mv.getSortingMethod()) {
				case 0: {
					for (int i = 0; i < MemoryView.allImages.size(); ++i) {
						if (o1 == MemoryView.allImages.get(i)) {
							n = 1;
							break;
						}
						if (o2 == MemoryView.allImages.get(i)) {
							n = -1;
							break;
						}
					}
					break Label_0142;
				}
				case 1: {
					usedCount = o1.getWidth() * o1.getHeight();
					usedCount2 = o2.getWidth() * o2.getHeight();
					break;
				}
				case 2: {
					usedCount = o1.getUsedCount();
					usedCount2 = o2.getUsedCount();
					break;
				}
			}
			n = usedCount - usedCount2;
		}
		if (mv.getSortByAscending()) {
			return n;
		}
		return -n;
	}
}
