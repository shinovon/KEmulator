/*
Copyright (c) 2025 Fyodor Ryzhov
*/
package emulator.ui.bridge;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

class PermissionWaitData {
	final int requestId;
	final Object lock = new Object();
	private int response = -1;

	private static int counter = 1;

	PermissionWaitData() {
		requestId = counter++;
	}

	/**
	 * Requests permission. Adds request to the queue and blocks the thread until response arrives.
	 */
	boolean request(BridgeFrontend bridge, String message) throws InterruptedException {
		byte[] msgBlob = message.getBytes(StandardCharsets.UTF_8);
		ByteBuffer bb = ByteBuffer.allocate(msgBlob.length + 4);
		bb.putInt(requestId);
		bb.put(msgBlob);
		synchronized (lock) {
			bridge.permissions.add(this);
			bridge.sendToState('P', bb.array());
			lock.wait();
		}
		if (response == 0)
			return false;
		if (response == 1)
			return true;
		throw new IllegalStateException();
	}

	/**
	 * Send response to waiting thread. Does not remove the permission from the queue!
	 */
	void resolve(boolean allow) {
		synchronized (lock) {
			response = allow ? 1 : 0;
			lock.notifyAll();
		}
	}
}
