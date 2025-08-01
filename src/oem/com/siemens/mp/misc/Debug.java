package com.siemens.mp.misc;

public class Debug {

	public static void breakpoint() {}
	public static void off() {}
	public static void on() {}
	public static void out(String s) {
		System.out.print(s);
	}
	public static void out(int i, String s) {
		System.out.print(s);
	}
	public static void outln(String s) {
		System.out.println(s);
	}
	public static void outln(int i, String s) {
		System.out.println(s);
	}
	public static void set(int i) {}
}
