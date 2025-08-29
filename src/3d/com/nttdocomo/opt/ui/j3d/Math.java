package com.nttdocomo.opt.ui.j3d;

import ru.woesss.j2me.micro3d.MathUtil;

public class Math {

	public static int sqrt(int p) {
		return MathUtil.iSqrt(p);
	}

	public static int sin(int p) {
		return MathUtil.iSin(p);
	}

	public static int cos(int p) {
		return MathUtil.iCos(p);
	}

	public static int atan2(int paramInt1, int paramInt2) {
		return MathUtil.iAtan2(paramInt1, paramInt2);
	}
}