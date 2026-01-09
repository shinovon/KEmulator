package emulator.ui.swt;

import emulator.Emulator;
import emulator.UILocale;
import emulator.ui.IMessage;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
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
	private Shell shell;
	private boolean aBoolean869;
	private Vector aVector870;
	static String aString871;
	private boolean visible;
	private Group aGroup872;
	private Group aGroup876;
	private Button aButton873;
	private StyledText aStyledText874;
	private StyledText aStyledText877;
	private Button aButton878;
	private Button aButton880;
	private Shell parent;
	private boolean aBoolean881;

	public MessageConsole() {
		super();
		this.shell = null;
		this.aGroup872 = null;
		this.aGroup876 = null;
		this.aButton873 = null;
		this.aStyledText874 = null;
		this.aStyledText877 = null;
		this.aButton878 = null;
		this.aButton880 = null;
		this.aBoolean869 = false;
		this.aVector870 = new Vector();
	}

	public final Message receive(final String s) throws IOException, InterruptedIOException {
		if (this.aVector870.size() > 0) {
			final String s2 = (String) this.aVector870.firstElement();
			this.aVector870.removeElementAt(0);
			return new TextMessageImpl(s, s2);
		}
		return null;
	}

	public final void send(final Message message, final String s) throws IOException, InterruptedIOException {
		if (this.aBoolean869) {
			throw new InterruptedIOException();
		}
		Label_0106:
		{
			StringBuffer sb;
			String payloadText;
			if (message instanceof TextMessage) {
				final TextMessage textMessage = (TextMessage) message;
				sb = new StringBuffer().append("Message From ").append(textMessage.getAddress()).append("\n");
				payloadText = textMessage.getPayloadText();
			} else {
				if (!(message instanceof BinaryMessage)) {
					break Label_0106;
				}
				sb = new StringBuffer().append("Message From ").append(message.getAddress());
				payloadText = "\n [Binary data]";
			}
			MessageConsole.aString871 = sb.append(payloadText).toString();
		}
		Emulator.getEmulator().getLogStream().println(MessageConsole.aString871);
		try {
			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					if (aStyledText877 == null || aStyledText877.isDisposed()) {
						return;
					}
					aStyledText877.append(MessageConsole.aString871);
					aStyledText877.setTopIndex(aStyledText877.getLineCount());
				}
			});
		} catch (Exception ignored) {}
	}

	public final boolean method479() {
		return this.visible;
	}

	public final Shell method480() {
		return this.shell;
	}

	public final void open(final Shell parent) {
		this.method487();
		final Display current = parent.getDisplay();
		this.parent = parent;
		this.shell.setLocation(parent.getLocation().x - parent.getSize().x, parent.getLocation().y);
		this.shell.setSize(parent.getSize());
		this.visible = true;
		this.aBoolean881 = true;
		this.shell.open();
		this.shell.addControlListener(this);
		this.shell.addDisposeListener(this);
		while (!this.shell.isDisposed()) {
			if (!current.readAndDispatch()) {
				current.sleep();
			}
		}
		this.visible = false;
	}

	public final void dispose() {
		if (this.shell != null && !this.shell.isDisposed()) {
			this.shell.dispose();
		}
		this.visible = false;
	}

	private void method487() {
		(this.shell = new Shell()).setText(UILocale.get("SMS_CONSOLE_TITLE", "SMS Console"));
		this.shell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.shell.setLayout(new GridLayout());
		this.shell.setSize(new Point(100, 200));
		this.method490();
		this.method491();
	}

	private void method490() {
		final GridData gridData;
		(gridData = new GridData()).horizontalAlignment = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
		gridData.heightHint = 20;
		gridData.widthHint = 60;
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = 4;
		final GridData layoutData2;
		(layoutData2 = new GridData()).horizontalAlignment = 4;
		layoutData2.grabExcessHorizontalSpace = true;
		layoutData2.grabExcessVerticalSpace = true;
		layoutData2.verticalAlignment = 4;
		layoutData2.horizontalSpan = 2;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 2;
		(this.aGroup872 = new Group(this.shell, 0)).setLayout(layout);
		this.aGroup872.setText(UILocale.get("SMS_CONSOLE_SEND_TO", "Send to midlet"));
		this.aGroup872.setLayoutData(layoutData);
		(this.aStyledText874 = new StyledText(this.aGroup872, 2624)).setLayoutData(layoutData2);
		(this.aButton878 = new Button(this.aGroup872, 8388616)).setText(UILocale.get("SMS_CONSOLE_CLEAR", "Clear"));
		this.aButton878.setLayoutData(gridData);
		this.aButton878.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				aStyledText874.setText("");
			}
		});
		(this.aButton880 = new Button(this.aGroup872, 8388616)).setText(UILocale.get("SMS_CONSOLE_SEND", "Send"));
		this.aButton880.setLayoutData(gridData);
		this.aButton880.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				final String text;
				if ((text = aStyledText874.getText()) != null && text.length() > 0) {
					aVector870.addElement(text);
				}
			}
		});
	}

	private void method491() {
		final GridData gridData;
		(gridData = new GridData()).horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = 4;
		(this.aGroup876 = new Group(this.shell, 0)).setLayout(new GridLayout());
		this.aGroup876.setText(UILocale.get("SMS_CONSOLE_RECEIVE", "Receive from midlet"));
		this.aGroup876.setLayoutData(gridData);
		(this.aButton873 = new Button(this.aGroup876, 32)).setText(UILocale.get("SMS_CONSOLE_BLOCK", "Block the received message"));
		this.aButton873.setSelection(this.aBoolean869);
		this.aButton873.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				aBoolean869 = aButton873.getSelection();
			}
		});
		(this.aStyledText877 = new StyledText(this.aGroup876, 2624)).setLayoutData(gridData);
	}

	public final boolean method488() {
		return this.aBoolean881;
	}

	public final void controlMoved(final ControlEvent controlEvent) {
		MessageConsole class83;
		boolean aBoolean881;
		if (Math.abs(this.parent.getLocation().x - this.shell.getSize().x - this.shell.getLocation().x) < 10 && Math.abs(this.parent.getLocation().y - this.shell.getLocation().y) < 20) {
			this.shell.setLocation(this.parent.getLocation().x - this.shell.getSize().x, this.parent.getLocation().y);
			class83 = this;
			aBoolean881 = true;
		} else {
			class83 = this;
			aBoolean881 = false;
		}
		class83.aBoolean881 = aBoolean881;
	}

	public final void controlResized(final ControlEvent controlEvent) {
	}

	public final void widgetDisposed(final DisposeEvent disposeEvent) {
		this.dispose();
	}
}
