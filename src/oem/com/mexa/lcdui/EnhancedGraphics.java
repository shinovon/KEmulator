package com.mexa.lcdui;

import com.nokia.mid.ui.DirectGraphics;
import com.nokia.mid.ui.DirectUtils;
import emulator.Settings;

import javax.microedition.lcdui.Graphics;

public class EnhancedGraphics {
	Graphics graphics;
	DirectGraphics nokiaGraphics;

	public EnhancedGraphics(Graphics paramGraphics) {
		nokiaGraphics = DirectUtils.getDirectGraphics(graphics = paramGraphics);
	}

	public synchronized void drawPolyline(int[] xPoints, int[] yPoints, int count) {
		nokiaGraphics.drawPolygon(xPoints, 0, yPoints, 0, count, graphics.getColor() | 0xFF000000);
	}

	public synchronized void drawPolyline(int[] xPoints, int[] yPoints, int offset, int count) {
		nokiaGraphics.drawPolygon(xPoints, offset, yPoints, offset, count, graphics.getColor() | 0xFF000000);
	}

	public synchronized void fillPolygon(int[] xPoints, int[] yPoints, int count) {
		nokiaGraphics.fillPolygon(xPoints, 0, yPoints, 0, count, graphics.getColor() | 0xFF000000);
	}

	public synchronized void fillPolygon(int[] xPoints, int[] yPoints, int offset, int count) {
		nokiaGraphics.fillPolygon(xPoints, offset, yPoints, offset, count, graphics.getColor() | 0xFF000000);
	}
}
