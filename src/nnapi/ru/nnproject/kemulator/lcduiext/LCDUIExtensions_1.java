package ru.nnproject.kemulator.lcduiext;

import emulator.lcdui.LCDUIUtils;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

public class LCDUIExtensions_1 {

	public static boolean isSupported() {
		return true;
	}

	public static int getVersion() {
		return 1;
	}

	// type:
	// 0 - foreground
	// 1 - background
	// 2 - highlight foreground
	// 3 - highlight background
	// 4 - button border
	public static void setGlobalColor(int type, int color) {
		switch (type) {
			case 0:
				LCDUIUtils.foregroundColor = color;
				break;
			case 1:
				LCDUIUtils.backgroundColor = color;
				break;
			case 2:
				LCDUIUtils.highlightForegroundColor = color;
				break;
			case 3:
				LCDUIUtils.highlightBackgroundColor = color;
				break;
			case 4:
				LCDUIUtils.buttonBorderColor = color;
				break;
		}
	}

	// type:
	// 0 - foreground
	// 1 - background
	// 2 - highlight background
	public static void setItemColor(StringItem item, int type, int color) {
		item._setColor(type, color);
	}

	public static void setLabelColor(Item item, int color) {
		item._setLabelColor(color);
	}

	public static void setLabelFont(Item item, Font font) {
		item._setLabelFont(font);
	}
}
