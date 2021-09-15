package com.nokia.mid.ui;

public abstract class CanvasGraphicsItemPainterInvoker {
	private static CanvasGraphicsItemPainterInvoker sInvoker;

	public static void setInvoker(CanvasGraphicsItemPainterInvoker invoker) {
		sInvoker = invoker;
	}

	protected static CanvasGraphicsItemPainter createCanvasGraphicsItemPainter(CanvasGraphicsItem aItem,
			Object aToolkit, int aWidth, int aHeight) {
		throw new RuntimeException("Not implemented yet.");
		// return sInvoker.doCreateCanvasGraphicsItemPainter(aItem, aToolkit, aWidth,
		// aHeight);
	}

	protected abstract CanvasGraphicsItemPainter doCreateCanvasGraphicsItemPainter(
			CanvasGraphicsItem paramCanvasGraphicsItem, Object paramObject, int paramInt1, int paramInt2);
}
