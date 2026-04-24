package emulator.automation.shared;

import java.io.IOException;
import mjson.Json;

public class AutomationRemoteException extends IOException {
	public final String code;
	public final Json details;

	public AutomationRemoteException(String code, String message, Json details) {
		super(message);
		this.code = code;
		this.details = details;
	}
}
