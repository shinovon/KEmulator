package com.siemens.mp.media.protocol;

public class ContentDescriptor {
	private String contentType;

	public String getContentType() {
		return this.contentType;
	}

	public ContentDescriptor(String s) {
		this.contentType = s;
	}
}
