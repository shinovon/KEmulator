package com.siemens.s60.lcdui.game;

public class LayerManager extends javax.microedition.lcdui.game.LayerManager {

	public void append(Layer layer) {
		super.append(layer);
	}

	public void insert(Layer layer, int n) {
		super.insert(layer, n);
	}

	public Layer getLayerAt(final int n) {
		return (Layer) super.getLayerAt(n);
	}

	public void remove(final Layer layer) {
		super.remove(layer);
	}
}
