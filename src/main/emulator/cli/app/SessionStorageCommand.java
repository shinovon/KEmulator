package emulator.cli.app;

import emulator.cli.controller.ControllerCalls;
import emulator.cli.controller.ControllerStatus;
import emulator.cli.controller.ControllerStatusService;
import emulator.cli.core.CliCommand;
import emulator.cli.core.CliExitCodes;
import emulator.cli.core.CliInvocation;
import emulator.cli.core.CommandPath;
import emulator.cli.core.CommandResult;
import emulator.cli.core.KemuCliException;
import emulator.cli.support.KemuPaths;
import emulator.cli.support.SessionStorageArchives;
import emulator.cli.support.SessionStoragePaths;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import mjson.Json;

public final class SessionStorageCommand implements CliCommand {
	private final String scope;
	private final String action;

	public SessionStorageCommand(String scope, String action) {
		this.scope = scope;
		this.action = action;
	}

	public CommandPath path() {
		return CommandPath.of(scope, action);
	}

	private String commandName() {
		return scope + " " + action;
	}

	private void requireInactive(CliInvocation invocation) throws Exception {
		ControllerStatus status = ControllerStatusService.readControllerStatus();
		if (!status.running) {
			return;
		}
		Json current = ControllerCalls.currentAppResult(
			ControllerStatusService.controllerClient(status),
			commandName(),
			invocation.json());
		if (current.at("active", false).asBoolean()) {
			throw new KemuCliException(
				"APP_ALREADY_OPEN",
				"Close the active app before changing or archiving session storage.",
				CliExitCodes.RUNTIME,
				commandName(),
				invocation.json(),
				current);
		}
	}

	private KemuCliException storageFailure(CliInvocation invocation, IOException error) {
		return new KemuCliException(
			"STORAGE_ERROR",
			error.getMessage(),
			CliExitCodes.RUNTIME,
			commandName(),
			invocation.json());
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		int expectedTokens = "reset".equals(action) ? 2 : 3;
		if (invocation.tokens().size() != expectedTokens) {
			throw new KemuCliException(
				"USAGE_ERROR",
				"Usage: kemu " + commandName() + ("reset".equals(action) ? "" : " FILE"),
				CliExitCodes.USAGE,
				commandName(),
				invocation.json());
		}
		requireInactive(invocation);
		SessionStoragePaths paths = SessionStoragePaths.read();
		Map<String, Path> roots = "rms".equals(scope)
			? SessionStorageArchives.rmsRoots(paths)
			: SessionStorageArchives.stateRoots(paths);
		Path archive = expectedTokens == 3
			? Paths.get(invocation.tokens().get(2)).toAbsolutePath().normalize()
			: null;
		try {
			if ("reset".equals(action)) {
				SessionStorageArchives.reset(paths.rmsDir);
			} else if ("export".equals(action) || "snapshot".equals(action)) {
				SessionStorageArchives.exportArchive(archive, roots);
			} else {
				SessionStorageArchives.importArchive(archive, roots);
			}
		} catch (IOException error) {
			throw storageFailure(invocation, error);
		}
		Json payload = Json.object()
			.set("sessionId", KemuPaths.sessionId())
			.set("scope", scope)
			.set("action", action)
			.set("dataDir", paths.dataDir.toString())
			.set("rmsDir", paths.rmsDir.toString())
			.set("fileRoot", paths.fileRoot.toString());
		if (archive != null) {
			payload.set("archive", archive.toString());
		}
		String text = commandName() + " completed for session " + KemuPaths.sessionId()
			+ (archive == null ? "" : ": " + archive);
		return new CommandResult(commandName(), text, payload, invocation.json());
	}
}
