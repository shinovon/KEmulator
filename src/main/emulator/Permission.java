package emulator;

import emulator.ui.swt.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import java.util.Vector;

public class Permission {

    public static final int ask_always = 0;
    public static final int ask_always_until_yes = 1;
    public static final int allowed = 2;
    public static final int never = 3;
    public static final int ask_always_until_no = 4;
    public static final int ask_once = 5;

    static InputDialog imeiDialog;
    private static int dialogResult;
    private static Vector allowPerms = new Vector();
    private static Vector notAllowPerms = new Vector();
    private static String imei;
    public static boolean askPermissions = true;
    public static boolean askImei = true;

    private static int getAppPermissionLevel(String x) {
        if(!askPermissions) return 2;
        x = x.toLowerCase();
        switch(x) {
            case "messageconnection.send":
            case "messageconnection.receive":
            case "connector.open.http":
            case "connector.open.file":
            case "connector.open.socket":
            case "connector.open.serversocket":
            case "connector.open.sms":
                return allowed; // return 5;
            case "camera":
                return ask_always_until_no;
            default:
                return ask_always_until_yes;
        }
    }

    public synchronized static String askIMEI() {
        if(notAllowPerms.contains("imei"))
            return null;
        if(!askImei) return "0000000000000000";
        if(imei != null) return imei;
        Emulator.emulatorimpl.getEmulatorScreen().getShell().getDisplay().syncExec(() -> {
            imeiDialog = new InputDialog(Emulator.emulatorimpl.getEmulatorScreen().getShell());
            imeiDialog.setMessage("Application asks for IMEI");
            imeiDialog.setInput("0000000000000000");
            imeiDialog.open();
        });
        String s = imeiDialog.getInput();
        if(s == null) {
            notAllowPerms.add("imei");
        }
        allowPerms.add("imei");
        return imei = s;
    }

    public static boolean showConfirmDialog(String message, String title) {
        Emulator.emulatorimpl.getEmulatorScreen().getShell().getDisplay().syncExec(() -> {
            MessageBox messageBox = new MessageBox(Emulator.emulatorimpl.getEmulatorScreen().getShell(), SWT.YES | SWT.NO);
            messageBox.setMessage(message);
            messageBox.setText(title);
            dialogResult = messageBox.open();
        });
        return dialogResult == SWT.YES;
    }

    public static void checkPermission(String x) {
        //0: always ask
        //1: ask once if yes, ask next time if no is pressed
        //2: always allowed
        //3: never
        //4: always ask until no is pressed
        //5: ask once
        switch(getAppPermissionLevel(x)) {
            case ask_always:
                if(!showConfirmDialog(localizePerm(x), "Security"))
                    throw new SecurityException(x);
                allowPerms.add(x);
                break;
            case ask_always_until_yes:
                if(allowPerms.contains(x))
                    return;
                if(!showConfirmDialog(localizePerm(x), "Security"))
                    throw new SecurityException(x);
                allowPerms.add(x);
            case allowed:
            default:
                break;
            case never:
                throw new SecurityException(x);
            case ask_always_until_no:
                if(notAllowPerms.contains(x))
                    return;
                if(!showConfirmDialog(localizePerm(x), "Security")) {
                    notAllowPerms.add(x);
                    throw new SecurityException(x);
                }
                allowPerms.add(x);
                break;
            case ask_once:
                if(notAllowPerms.contains(x))
                    throw new SecurityException(x);
                if(allowPerms.contains(x))
                    return;
                if(!showConfirmDialog(localizePerm(x), "Security")) {
                    notAllowPerms.add(x);
                    throw new SecurityException(x);
                }
                allowPerms.add(x);
                break;
        }
    }

    private static String localizePerm(String x) {
        switch(x) {
            case "connector.open.http":
                return "Allow the application to open HTTP connections?";
            case "connector.open.file":
                return "Allow the application to access the file system?";
            case "connector.open.socket":
                return "Allow the application to open socket connections?";
            case "connector.open.serversocket":
                return "Allow the application to open server socket connections?";
            case "camera":
                return "Allow the application to use camera?";
            default:
                return "Allow the application to use \'" + x + "\'?";
        }
    }

    public static boolean requestURLAccess(final String url) {
        Emulator.emulatorimpl.getEmulatorScreen().getShell().getDisplay().syncExec(() -> {
            MessageBox messageBox = new MessageBox(Emulator.emulatorimpl.getEmulatorScreen().getShell(), SWT.YES | SWT.NO);
            String s = url;
            if (s.length() > 100) {
                s = s.substring(0, 100) + "...";
            }
            if(s.startsWith("vlc:")) {
                s = s.substring(4);
                messageBox.setMessage("MIDlet wants to open URL in VLC: " + s);
            } else {
                messageBox.setMessage("MIDlet wants to open URL: " + s);
            }
            messageBox.setText("Security");
            dialogResult = messageBox.open();
        });
        return dialogResult == SWT.YES;
    }
}
