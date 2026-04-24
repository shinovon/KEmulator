package emulator.automation.controller;

import mjson.Json;

interface ControllerOperation {
	String op();

	DispatchMode dispatchMode();

	Json execute(Json args) throws Exception;
}
