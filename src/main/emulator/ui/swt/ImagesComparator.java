package emulator.ui.swt;

import javax.microedition.lcdui.Image;
import java.util.Comparator;

final class ImagesComparator implements Comparator {
	private final MemoryView mv;

	ImagesComparator(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final int compare(final Object o, final Object o2) {
		int n = 0;
		final Image image = (Image) o;
		final Image image2 = (Image) o2;
		Label_0142:
		{
			int usedCount = 0;
			int usedCount2 = 0;
			switch (mv.getSortingMethod()) {
				case 0: {
					for (int i = 0; i < MemoryView.allImages.size(); ++i) {
						if (o == MemoryView.allImages.get(i)) {
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
					usedCount = image.getWidth() * image.getHeight();
					usedCount2 = image2.getWidth() * image2.getHeight();
					break;
				}
				case 2: {
					usedCount = image.getUsedCount();
					usedCount2 = image2.getUsedCount();
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
