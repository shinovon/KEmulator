package emulator.cli.core;

public interface CliCommand {
	CommandPath path();

	CommandResult run(CliInvocation invocation) throws Exception;
}
