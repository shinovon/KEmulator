package emulator.ui.swt;

import emulator.Emulator;
import emulator.UILocale;
import emulator.ui.IMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.TextMessage;
import javax.wireless.messaging.TextMessageImpl;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Vector;

public final class MessageConsole implements IMessage, ControlListener, DisposeListener {
	private static final int MESSAGE_TEXT_STYLE = SWT.BORDER | SWT.V_SCROLL | SWT.WRAP;
	private static String lastMessageText;

	private final Vector<String> pendingMessages = new Vector<>();
	private Shell shell;
	private Shell parent;
	private StyledText receivedMessages;
	private boolean blockReceivedMessages;
	private boolean visible;
	private boolean attachedToParent;

	@Override
	public Message receive(String address) throws IOException, InterruptedIOException {
		synchronized (pendingMessages) {
			if (pendingMessages.isEmpty()) {
				return null;
			}
			return new TextMessageImpl(address, pendingMessages.remove(0));
		}
	}

	@Override
	public void send(Message message, String address) throws IOException, InterruptedIOException {
		if (blockReceivedMessages) {
			throw new InterruptedIOException();
		}

		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			lastMessageText = "Message From " + textMessage.getAddress() + "\n" + textMessage.getPayloadText();
		} else if (message instanceof BinaryMessage) {
			lastMessageText = "Message From " + message.getAddress() + "\n [Binary data]";
		}
		Emulator.getEmulator().getLogStream().println(lastMessageText);

		try {
			shell.getDisplay().syncExec(() -> {
				if (receivedMessages != null && !receivedMessages.isDisposed()) {
					receivedMessages.append(lastMessageText);
					receivedMessages.setTopIndex(receivedMessages.getLineCount());
				}
			});
		} catch (Exception ignored) {}
	}

	public boolean isVisible() {
		return visible;
	}

	public Shell getShell() {
		return shell;
	}

	public boolean isAttachedToParent() {
		return attachedToParent;
	}

	public void open(Shell parent) {
		createContents();
		Display display = parent.getDisplay();
		this.parent = parent;
		Point parentSize = parent.getSize();
		shell.setLocation(getDockLocation(parentSize.x));
		shell.setSize(parentSize);
		visible = true;
		attachedToParent = true;
		shell.open();
		shell.addControlListener(this);
		shell.addDisposeListener(this);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		visible = false;
	}

	public void dispose() {
		if (shell != null && !shell.isDisposed()) {
			shell.dispose();
		}
		visible = false;
	}

	private void createContents() {
		shell = new Shell();
		shell.setText(UILocale.get("SMS_CONSOLE_TITLE", "SMS Console"));
		shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/res/icon")));
		shell.setLayout(new GridLayout());
		shell.setSize(new Point(100, 200));
		createSendGroup();
		createReceiveGroup();
	}

	private void createSendGroup() {
		Group sendGroup = new Group(shell, SWT.NONE);
		sendGroup.setLayout(new GridLayout(2, false));
		sendGroup.setText(UILocale.get("SMS_CONSOLE_SEND_TO", "Send to midlet"));
		sendGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		StyledText messageInput = new StyledText(sendGroup, MESSAGE_TEXT_STYLE);
		messageInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		Button clearButton = createSendButton(sendGroup, UILocale.get("SMS_CONSOLE_CLEAR", "Clear"));
		clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				messageInput.setText("");
			}
		});
		Button sendButton = createSendButton(sendGroup, UILocale.get("SMS_CONSOLE_SEND", "Send"));
		sendButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String text = messageInput.getText();
				if (text != null && !text.isEmpty()) {
					pendingMessages.addElement(text);
				}
			}
		});
	}

	private static Button createSendButton(Group group, String label) {
		Button button = new Button(group, SWT.FLAT | SWT.PUSH);
		button.setText(label);
		GridData layout = new GridData(GridData.CENTER, GridData.CENTER, true, false);
		layout.widthHint = 60;
		layout.heightHint = 20;
		button.setLayoutData(layout);
		return button;
	}

	private void createReceiveGroup() {
		Group receiveGroup = new Group(shell, SWT.NONE);
		receiveGroup.setLayout(new GridLayout());
		receiveGroup.setText(UILocale.get("SMS_CONSOLE_RECEIVE", "Receive from midlet"));
		receiveGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Button blockButton = new Button(receiveGroup, SWT.CHECK);
		blockButton.setText(UILocale.get("SMS_CONSOLE_BLOCK", "Block the received message"));
		blockButton.setSelection(blockReceivedMessages);
		blockButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				blockReceivedMessages = blockButton.getSelection();
			}
		});
		receivedMessages = new StyledText(receiveGroup, MESSAGE_TEXT_STYLE);
		receivedMessages.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private Point getDockLocation(int consoleWidth) {
		Point parentLocation = parent.getLocation();
		return new Point(parentLocation.x - consoleWidth, parentLocation.y);
	}

	@Override
	public void controlMoved(ControlEvent event) {
		Point dockLocation = getDockLocation(shell.getSize().x);
		Point consoleLocation = shell.getLocation();
		boolean withinDockRange = Math.abs(dockLocation.x - consoleLocation.x) < 10
				&& Math.abs(dockLocation.y - consoleLocation.y) < 20;
		if (withinDockRange) {
			shell.setLocation(dockLocation);
		}
		attachedToParent = withinDockRange;
	}

	@Override
	public void controlResized(ControlEvent event) {
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {
		dispose();
	}
}
