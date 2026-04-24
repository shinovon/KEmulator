package emulator.cli;

import emulator.cli.app.*;
import emulator.cli.controller.*;
import emulator.cli.core.*;
import emulator.cli.library.*;
import emulator.cli.output.CliResponses;
import mjson.Json;

public final class KemuMain {
	private static final CliApp CLI_APP = createCliApp();

	private KemuMain() {
	}

	public static void main(String[] args) {
		boolean json = containsJsonFlag(args);
		try {
			CommandResult result = CLI_APP.run(args);
			if (result.json) {
				writeJson(CliResponses.successEnvelope(result.commandName, result.payload));
			} else if (result.text != null && result.text.length() > 0) {
				System.out.println(result.text);
			}

			System.exit(CliExitCodes.OK);
		} catch (KemuCliException e) {
			if (json || e.json) {
				writeJson(CliResponses.errorEnvelope(e.commandName, e.code, e.getMessage(), e.payload));
			} else {
				System.err.println("Error: " + e.getMessage());
			}

			System.exit(e.exitCode);
		} catch (Exception e) {
			if (json) {
				writeJson(CliResponses.errorEnvelope(null, "INTERNAL_ERROR", e.toString(), null));
			} else {
				e.printStackTrace(System.err);
			}

			System.exit(CliExitCodes.RUNTIME);
		}
	}

	private static CliApp createCliApp() {
		CommandRegistry registry = new CommandRegistry();
		HelpCommand helpCommand = new HelpCommand(registry);
		registry.add(helpCommand);
		registry.add(new StatusCommand());
		registry.add(new StartCommand());
		registry.add(new StopCommand());
		registry.add(new LogsCommand());
		registry.add(new InspectCommand());
		registry.add(new OpenCommand());
		registry.add(new CloseCommand());
		registry.add(new StateCommand());
		registry.add(new ObserveCommand());
		registry.add(new ScreenshotCommand());
		registry.add(new WaitCommand());
		registry.add(new KeyCommand());
		registry.add(new TapCommand());
		registry.add(new DragCommand());
		registry.add(new CommandRootCommand());
		registry.add(new RunUiCommand());
		registry.add(new PermissionCommand());

		return new CliApp(registry, helpCommand);
	}

	private static void writeJson(Json json) {
		System.out.println(json.toString());
	}

	private static boolean containsJsonFlag(String[] args) {
		for (String arg : args) {
			if ("--json".equals(arg)) {
				return true;
			}
		}

		return false;
	}
}
