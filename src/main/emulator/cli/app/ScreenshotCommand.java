package emulator.cli.app;

import emulator.cli.controller.*;
import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import emulator.cli.output.CliTextRenderer;
import emulator.cli.parse.CliParsing;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import mjson.Json;

public final class ScreenshotCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("screenshot");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		Path out = null;
		boolean sawOut = false;
		for (int i = 1; i < invocation.tokens().size(); i++) {
			String token = invocation.tokens().get(i);
			if ("--out".equals(token)) {
				if (sawOut) {
					throw new KemuCliException(
						"USAGE_ERROR",
						"Duplicate option: --out.",
						CliExitCodes.USAGE,
						"screenshot",
						invocation.json());
				}

				if (i + 1 >= invocation.tokens().size()) {
					throw new KemuCliException(
						"USAGE_ERROR",
						CliTextRenderer.usageText("screenshot"),
						CliExitCodes.USAGE,
						"screenshot",
						invocation.json());
				}

				sawOut = true;
				out = CliParsing.resolveUserPath(invocation.tokens().get(++i));
			} else {
				throw new KemuCliException(
					"USAGE_ERROR",
					CliTextRenderer.usageText("screenshot"),
					CliExitCodes.USAGE,
					"screenshot",
					invocation.json());
			}
		}

		if (out == null) {
			throw new KemuCliException(
				"USAGE_ERROR",
				CliTextRenderer.usageText("screenshot"),
				CliExitCodes.USAGE,
				"screenshot",
				invocation.json());
		}

		if (!out.toString().toLowerCase(java.util.Locale.ROOT).endsWith(".png")) {
			throw new KemuCliException(
				"USAGE_ERROR",
				"Screenshot output must use .png extension: " + out,
				CliExitCodes.USAGE,
				"screenshot",
				invocation.json());
		}

		ControllerStatus status = ControllerLifecycle.requireRunningController("screenshot", invocation.json());
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			"app.screenshot",
			Json.object(),
			"screenshot",
			invocation.json()));
		String imageBase64 = payload.at("imageBase64", Json.nil()).isNull()
			? null
			: payload.at("imageBase64").asString();
		if (imageBase64 == null || imageBase64.length() == 0) {
			throw new KemuCliException(
				"SCREENSHOT_FAILED",
				"Controller did not return image data.",
				CliExitCodes.RUNTIME,
				"screenshot",
				invocation.json());
		}

		byte[] imageBytes;
		try {
			imageBytes = Base64.getDecoder().decode(imageBase64);
		} catch (IllegalArgumentException e) {
			throw new KemuCliException(
				"SCREENSHOT_FAILED",
				"Controller returned invalid image data.",
				CliExitCodes.RUNTIME,
				"screenshot",
				invocation.json());
		}

		Path parent = out.getParent();
		try {
			if (parent != null) {
				Files.createDirectories(parent);
			}

			Files.write(out, imageBytes);
		} catch (IOException e) {
			throw new KemuCliException(
				"SCREENSHOT_WRITE_FAILED",
				"Could not write screenshot to " + out + ": " + e.getMessage(),
				CliExitCodes.RUNTIME,
				"screenshot",
				invocation.json(),
				Json.object().set("path", out.toString()));
		}

		payload.delAt("imageBase64");
		payload.set("saved", true);
		payload.set("path", out.toString());

		return new CommandResult("screenshot", out.toString(), payload, invocation.json());
	}
}
