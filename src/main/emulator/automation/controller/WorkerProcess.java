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
	Path readyPath;
	Path exitPath;
	List<String> command;
	List<String> jvmOptions;
	Path dataDir;
	Path rmsDir;
	Path fileRoot;
	String sessionId;

	Json toJson() {
		return Json.object()
			.set("pid", pid)
			.set("alive", process != null && process.isAlive())
			.set("port", port)
			.set("startedAt", startedAt)
			.set("logPath", logPath == null ? null : logPath.toString())
			.set("readyPath", readyPath == null ? null : readyPath.toString())
			.set("exitPath", exitPath == null ? null : exitPath.toString())
			.set("jvmOptions", jvmOptions)
			.set("dataDir", dataDir == null ? null : dataDir.toString())
			.set("rmsDir", rmsDir == null ? null : rmsDir.toString())
			.set("fileRoot", fileRoot == null ? null : fileRoot.toString())
			.set("sessionId", sessionId)
			.set("emulatedHeap", Json.nil())
			.set("command", command);
	}
}
