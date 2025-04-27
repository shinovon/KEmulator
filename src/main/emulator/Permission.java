package emulator;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Permission {

	public static final int ask_always = 0;
	public static final int ask_always_until_yes = 1;
	public static final int allowed = 2;
	public static final int never = 3;
	public static final int ask_always_until_no = 4;
	public static final int ask_once = 5;

	private static final Vector<String> allowPerms = new Vector();
	private static final Vector<String> notAllowPerms = new Vector();
	public static final Map<String, Integer> permissions = new HashMap<String, Integer>();
	private static String imei;
	public static boolean askPermissions = true;
	public static boolean askImei = true;

	public static int getPermissionLevel(String s) {
		if (!Settings.enableSecurity) return 2;
		s = s.toLowerCase();
		if (permissions.containsKey(s))
			return permissions.get(s);
		// default
		if (s.equals("messageconnection.send") ||
				s.equals("messageconnection.receive") ||
				s.equals("connector.open.http") ||
				s.equals("connector.open.file") ||
				s.equals("connector.open.socket") ||
				s.equals("connector.open.serversocket") ||
				s.equals("connector.open.sms") ||
				s.equals("location")) {
			return allowed;
		} else if (s.equals("media.camera") ||
				s.equals("platformrequest")) {
			return ask_always_until_no;
		}
		return ask_always_until_yes;
	}

	public static String getPermissionLevelString(String s) {
		switch (getPermissionLevel(s)) {
			case ask_always:
				return "ask_always";
			case ask_always_until_yes:
				return "ask_always_until_yes";
			case allowed:
				return "allowed";
			case never:
				return "never";
			case ask_always_until_no:
				return "ask_always_until_no";
			case ask_once:
				return "ask_once";
			default:
				return "unknown";
		}
	}

	public static int fromString(String s) {
		if ("ask_always".equals(s)) {
			return ask_always;
		} else if ("ask_always_until_yes".equals(s)) {
			return ask_always_until_yes;
		} else if ("allowed".equals(s)) {
			return allowed;
		} else if ("never".equals(s)) {
			return never;
		} else if ("ask_always_until_no".equals(s)) {
			return ask_always_until_no;
		} else if ("ask_once".equals(s)) {
			return ask_once;
		}
		return ask_always_until_no;
	}

	public synchronized static String askIMEI() {
		if (notAllowPerms.contains("imei"))
			return null;
		if (!askImei) return "0000000000000000";
		if (imei != null) return imei;
		String s = Emulator.getEmulator().getScreen().showIMEIDialog();
		if (s == null) {
			notAllowPerms.add("imei");
		}
		allowPerms.add("imei");
		return imei = s;
	}

	public static boolean showConfirmDialog(final String message) {
		return Emulator.getEmulator().getScreen().showSecurityDialog(message);
	}

	public static void checkPermission(String x) {
		//0: always ask
		//1: ask once if yes, ask next time if no is pressed
		//2: always allowed
		//3: never
		//4: always ask until no is pressed
		//5: ask once
		switch (getPermissionLevel(x)) {
			case ask_always:
				if (!showConfirmDialog(localizePerm(x)))
					throw new SecurityException(x);
				allowPerms.add(x);
				break;
			case ask_always_until_yes:
				if (allowPerms.contains(x))
					return;
				if (!showConfirmDialog(localizePerm(x)))
					throw new SecurityException(x);
				allowPerms.add(x);
			case allowed:
			default:
				break;
			case never:
				throw new SecurityException(x);
			case ask_always_until_no:
				if (notAllowPerms.contains(x))
					return;
				if (!showConfirmDialog(localizePerm(x))) {
					notAllowPerms.add(x);
					throw new SecurityException(x);
				}
				allowPerms.add(x);
				break;
			case ask_once:
				if (notAllowPerms.contains(x))
					throw new SecurityException(x);
				if (allowPerms.contains(x))
					return;
				if (!showConfirmDialog(localizePerm(x))) {
					notAllowPerms.add(x);
					throw new SecurityException(x);
				}
				allowPerms.add(x);
				break;
		}
	}

	private static String localizePerm(String x) {
		if (x.equals("connector.open.http")) {
			return "Allow the application to open HTTP connections?";
		} else if (x.equals("connector.open.file")) {
			return "Allow the application to access the file system?";
		} else if (x.equals("connector.open.socket")) {
			return "Allow the application to open socket connections?";
		} else if (x.equals("connector.open.serversocket")) {
			return "Allow the application to open server socket connections?";
		} else if (x.equals("media.camera")) {
			return UILocale.get("PERMISSION_CAMERA", "Allow the application to use camera?");
		}
		return "Allow the application to use '" + x + "'?";
	}

	public static boolean requestURLAccess(String url) {
		switch (getPermissionLevel("platformrequest")) {
			case never:
				return false;
			case allowed:
				return true;
		}

		String text;
		if (url.length() > 100) {
			url = url.substring(0, 100) + "...";
		}
		if (url.startsWith("vlc:")) {
			text = (UILocale.get("PLATFORMREQUEST_VLC_ALERT", "Application wants to open URL in VLC") +
					": " + url.substring(4));
		} else {
			text = (UILocale.get("PLATFORMREQUEST_ALERT", "Application wants to open URL") +
					": " + url);
		}

		return Emulator.getEmulator().getScreen().showSecurityDialog(text);
	}
}
