/*
 * MIT License
 * Copyright (c) 2026 Yury Kharchenko
 */

package com.mascotcapsule.micro3d.v3;

class Loader {
	final byte[] data;
	int pos;
	int cached;
	int cache;

	Loader(byte[] bytes) { data = bytes; }
	
	final void skip(int bytes) {
		pos += bytes;
	}

	final int readUByte() {
		return data[pos++] & 0xff;
	}

	final byte readByte() {
		return data[pos++];
	}

	final short readShort() {
		return (short) ((data[pos++] & 0xff) | (data[pos++] << 8));
	}

	final int readUShort() {
		return (data[pos++] & 0xff) | ((data[pos++] & 0xff) << 8);
	}

	final int readInt() { 
		return readUShort() | (readUShort() << 16); 
	}

	final int readUBits(int size) {
		if (size > 25) {
			System.out.println("readUBits(size=" + size + ')');
			throw new IllegalArgumentException("Invalid bit size=" + size);
		}
		
		while (size > cached) {
			cache |= readUByte() << cached;
			cached += 8;
		}
		
		int mask = ~(0xffffffff << size);
		int result = cache & mask;
		cached -= size;
		cache >>>= size;
		return result;
	}

	final int readBits(int size) {
		int lzb = 32 - size;
		return (readUBits(size) << lzb) >> lzb;
	}

	final void clearCache() {
		cache = 0;
		cached = 0;
	}
}
