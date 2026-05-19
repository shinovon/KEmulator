package emulator.automation.controller;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import mjson.Json;

final class WorkerProcess {
	AppTarget entry;
	String pid;
	Process process;
	int port;
	final ReentrantLock protocolLock = new ReentrantLock();
	long nextRequestId = 1L;
	long startedAt;
	Path logPath;
	List<String> command;

	Json toJson() {
		return Json.object()
			.set("pid", pid)
			.set("alive", process != null && process.isAlive())
			.set("port", port)
			.set("startedAt", startedAt)
			.set("logPath", logPath == null ? null : logPath.toString())
			.set("command", command);
	}
}
