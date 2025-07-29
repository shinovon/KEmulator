package com.nttdocomo.ui;

public class VisualPresenter extends Component implements MediaPresenter {
	public static final int IMAGE_XPOS = 1;
	public static final int IMAGE_YPOS = 2;
	public static final int VISUAL_PLAYING = 1;
	public static final int VISUAL_STOPPED = 2;
	public static final int VISUAL_COMPLETE = 3;

	public void setImage(final MediaImage mediaImage) {
	}

	public void setData(final MediaData mediaData) {
	}

	public MediaResource getMediaResource() {
		return null;
	}

	public void play() {
	}

	public void stop() {
	}

	public void setAttribute(final int n, final int n2) {
	}

	public void setMediaListener(final MediaListener mediaListener) {
	}
}
