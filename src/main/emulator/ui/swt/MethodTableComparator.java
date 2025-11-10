package emulator.ui.swt;

import emulator.custom.h;

import java.util.Comparator;

final class MethodTableComparator implements Comparator {
	private final int anInt1438;
	private final Methods aClass46_1439;

	MethodTableComparator(final Methods aClass46_1439, final int anInt1438) {
		super();
		this.aClass46_1439 = aClass46_1439;
		this.anInt1438 = anInt1438;
	}

	public final int compare(final Object o, final Object o2) {
		int n = 0;
		final h.MethodInfo methodInfo = (h.MethodInfo) o;
		final h.MethodInfo methodInfo2 = (h.MethodInfo) o2;
		Label_0290:
		{
			int n2 = 0;
			switch (this.anInt1438) {
				case 0: {
					n2 = methodInfo.className.compareTo(methodInfo2.className);
					break;
				}
				case 1: {
					n2 = methodInfo.methodName.compareTo(methodInfo2.methodName);
					break;
				}
				case 2: {
					n2 = methodInfo.methodSignature.compareTo(methodInfo2.methodSignature);
					break;
				}
				case 3: {
					n2 = methodInfo.codeSize - methodInfo2.codeSize;
					break;
				}
				case 4: {
					n2 = methodInfo.refCount - methodInfo2.refCount;
					break;
				}
				case 5: {
					n2 = methodInfo.callCount - methodInfo2.callCount;
					break;
				}
				case 6: {
					n = ((methodInfo.totalExecutionTime - methodInfo2.totalExecutionTime == 0L) ? 0 : ((methodInfo.totalExecutionTime - methodInfo2.totalExecutionTime > 0L) ? 1 : -1));
					break Label_0290;
				}
				case 7: {
					n = ((methodInfo.averageExecutionTime - methodInfo2.averageExecutionTime == 0.0f) ? 0 : ((methodInfo.averageExecutionTime - methodInfo2.averageExecutionTime > 0.0f) ? 1 : -1));
					break Label_0290;
				}
				case 8: {
					n2 = ((methodInfo.timePercentage - methodInfo2.timePercentage == 0.0f) ? 0 : ((methodInfo.timePercentage - methodInfo2.timePercentage > 0.0f) ? 1 : -1));
					break;
				}
			}
			n = n2;
		}
		if (Methods.method441(this.aClass46_1439).getSortDirection() == 128) {
			return n;
		}
		return -n;
	}
}
