package emulator.lcdui;

import javax.microedition.lcdui.Graphics;

public final class LCDUIUtils {
	public static int backgroundColor = 0xFFEFF0F0; // 0xFFEFF0F0
	public static int foregroundColor = 0xFF000000; // 0xFF000000
	public static int highlightedForegroundColor = 0xFF556D95;
	public static int highlightedBackgroundColor = 0xFFE6E6E6;
	public static int borderColor = 0xFFABABAB;
	public static int highlightedBorderColor =  0xFF556D95;
	public static int gaugeColor = 0xFF0000FF;

	public static void drawDisplayableBackground(Graphics var0, int var1, int var2, int var3, int var4, boolean var5) {
		var0.setColor(backgroundColor);
		var0.fillRect(var1, var2, var3, var4);
		if (var5) {
			var0.setColor(-8355712);
			var0.drawRect(var1, var2, var3, var4);
		}

	}

	public static void drawSelectedItemBackground(Graphics g, int x, int y, int w, int h) {
		g.setColor(highlightedBackgroundColor);
		g.fillRect(x + 1, y + 1, w - 2, h - 2);
		g.setColor(highlightedBorderColor);
		g.drawRect(x, y, w, h);
	}

	public static void drawTickerBackground(Graphics var0, int var1, int var2, int var3, int var4) {
		var0.setColor(backgroundColor);
		var0.fillRect(var1, var2, var3, var4);
		int var5 = var0.getStrokeStyle();
		var0.setColor(8874950);
		var0.setStrokeStyle(1);
		var0.drawLine(var1, var2, var1 + var3, var2);
		var0.drawLine(var1, var2 + var4, var1 + var3, var2 + var4);
		var0.setStrokeStyle(var5);
		var0.setColor(foregroundColor);
	}

	public static void drawScrollbar(Graphics var0, int var1, int var2, int var3, int var4, int var5, int var6) {
		var0.setColor(12697332);
		var0.fillRect(var1, var2, var3, var4);
		if (var5 > 0 && var6 >= 0 && var6 < var5) {
			int var7 = Math.max(var4 / var5, 1);
			int var8 = var2 + var4 * var6 / var5;
			var0.setColor(8617456);
			var0.fillRect(var1, var8, var3, var7);
		}

	}

	public static void drawChoiceItem(Graphics var0, int var1, int var2, boolean var3, int var4) {
		Graphics var10000;
		int var10001;
		int var10002;
		int var10003;
		int var10004;
		switch (var4) {
			case 1:
				var0.drawRect(var1, var2, 10, 10);
				if (var3) {
					var0.fillRect(var1 + 3, var2 + 3, 5, 5);
					return;
				}

				return;
			case 2:
				var0.drawRect(var1, var2, 10, 10);
				if (var3) {
					var0.drawLine(var1 + 1, var2 + 5, var1 + 4, var2 + 8);
					var10000 = var0;
					var10001 = var1 + 5;
					var10002 = var2 + 8;
					var10003 = var1 + 10;
					var10004 = var2;
					break;
				}

				return;
			case 3:
			default:
				return;
			case 4:
				var1 += 4;
				var0.fillRect(var1, var2, 4, 8);
				var0.fillRect(var1 + 1, var2 + 8, 2, 2);
				var0.drawLine(var1 - 1, var2 + 6, var1 + 5, var2 + 6);
				var0.drawLine(var1 + 5, var2 + 6, var1 + 2, var2 + 10);
				var10000 = var0;
				var10001 = var1 - 2;
				var10002 = var2 + 6;
				var10003 = var1 + 1;
				var10004 = var2 + 10;
		}

		var10000.drawLine(var10001, var10002, var10003, var10004);
	}
}
