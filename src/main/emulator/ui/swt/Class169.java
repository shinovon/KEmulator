package emulator.ui.swt;

import java.util.*;

import emulator.custom.*;

final class Class169 implements Comparator {
	private final int anInt1438;
	private final Methods aClass46_1439;

	Class169(final Methods aClass46_1439, final int anInt1438) {
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
					n2 = methodInfo.aString1172.compareTo(methodInfo2.aString1172);
					break;
				}
				case 1: {
					n2 = methodInfo.aString1177.compareTo(methodInfo2.aString1177);
					break;
				}
				case 2: {
					n2 = methodInfo.aString1181.compareTo(methodInfo2.aString1181);
					break;
				}
				case 3: {
					n2 = methodInfo.anInt1173 - methodInfo2.anInt1173;
					break;
				}
				case 4: {
					n2 = methodInfo.refCount - methodInfo2.refCount;
					break;
				}
				case 5: {
					n2 = methodInfo.anInt1182 - methodInfo2.anInt1182;
					break;
				}
				case 6: {
					n = ((methodInfo.aLong1179 - methodInfo2.aLong1179 == 0L) ? 0 : ((methodInfo.aLong1179 - methodInfo2.aLong1179 > 0L) ? 1 : -1));
					break Label_0290;
				}
				case 7: {
					n = ((methodInfo.aFloat1175 - methodInfo2.aFloat1175 == 0.0f) ? 0 : ((methodInfo.aFloat1175 - methodInfo2.aFloat1175 > 0.0f) ? 1 : -1));
					break Label_0290;
				}
				case 8: {
					n2 = ((methodInfo.aFloat1180 - methodInfo2.aFloat1180 == 0.0f) ? 0 : ((methodInfo.aFloat1180 - methodInfo2.aFloat1180 > 0.0f) ? 1 : -1));
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
