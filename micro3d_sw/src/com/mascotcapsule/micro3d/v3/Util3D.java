/*
 * MIT License
 * Copyright (c) 2026 Yury Kharchenko
 * Copyright (c) 2026 Roman Lahin
 */

package com.mascotcapsule.micro3d.v3;

public class Util3D {
	
	private static final short[] sinTable = new short[4096];
	
	static {
		//Mascot Capsule docs state that result should be multiplied by 1024
		//But reference implementation uses 4096 scale factor
		
		for(int i = 0; i < 4096; i++) {
			sinTable[i] = (short) Math.floor(Math.sin(i * Math.PI / 2048.0) * 4096.0 + 0.5);
		}
	}
	
	private Util3D() {}

	public static final int sin(int p) {
		return sinTable[p & 4095];
	}

	public static final int cos(int p) {
		return sinTable[(p + 1024) & 4095];
	}
	
	public static final int sqrt(int p) {
		if (p == 0) return 0;
		
		double a;
		if (p < 0) {
			//Fix for numbers with sign bit used
			if (p > 0xfffd0002) return 0xffff;
			a = p & 0xffffffffL;
		} else {
			a = p;
		}
		
		return (int) (Math.sqrt(a) + 0.5);
	}
}
