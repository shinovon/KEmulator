package com.nttdocomo.ui;

public interface MediaPresenter {
	void setData(final MediaData p0);

	MediaResource getMediaResource();

	void play();

	void stop();

	void setAttribute(final int p0, final int p1);

	void setMediaListener(final MediaListener p0);
}
