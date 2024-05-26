package emulator.ui.swt;

import emulator.ui.*;

import java.util.*;

import org.eclipse.swt.custom.*;

import java.io.*;
import javax.wireless.messaging.*;

import emulator.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

public final class Class83 implements IMessage, ControlListener, DisposeListener {
	private Shell aShell868;
	private boolean aBoolean869;
	private Vector aVector870;
	static String aString871;
	private boolean aBoolean875;
	private Group aGroup872;
	private Group aGroup876;
	private Button aButton873;
	private StyledText aStyledText874;
	private StyledText aStyledText877;
	private Button aButton878;
	private Button aButton880;
	private Shell aShell879;
	private boolean aBoolean881;

	public Class83() {
		super();
		this.aShell868 = null;
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
			Class83.aString871 = sb.append(payloadText).toString();
		}
		Emulator.getEmulator().getLogStream().println(Class83.aString871);
		EmulatorImpl.syncExec(new Textout(this));
	}

	public final boolean method479() {
		return this.aBoolean875;
	}

	public final Shell method480() {
		return this.aShell868;
	}

	public final void method481(final Shell aShell879) {
		this.method487();
		final Display current = Display.getCurrent();
		this.aShell879 = aShell879;
		this.aShell868.setLocation(aShell879.getLocation().x - aShell879.getSize().x, aShell879.getLocation().y);
		this.aShell868.setSize(aShell879.getSize());
		this.aBoolean875 = true;
		this.aBoolean881 = true;
		this.aShell868.open();
		this.aShell868.addControlListener(this);
		this.aShell868.addDisposeListener(this);
		while (!this.aShell868.isDisposed()) {
			if (!current.readAndDispatch()) {
				current.sleep();
			}
		}
		this.aBoolean875 = false;
	}

	public final void method482() {
		if (this.aShell868 != null && !this.aShell868.isDisposed()) {
			this.aShell868.dispose();
		}
		this.aBoolean875 = false;
	}

	private void method487() {
		(this.aShell868 = new Shell()).setText(UILocale.get("SMS_CONSOLE_TITLE", "SMS Console"));
		this.aShell868.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.aShell868.setLayout(new GridLayout());
		this.aShell868.setSize(new Point(100, 200));
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
		(this.aGroup872 = new Group(this.aShell868, 0)).setLayout(layout);
		this.aGroup872.setText(UILocale.get("SMS_CONSOLE_SEND_TO", "Send to midlet"));
		this.aGroup872.setLayoutData(layoutData);
		(this.aStyledText874 = new StyledText(this.aGroup872, 2624)).setLayoutData(layoutData2);
		(this.aButton878 = new Button(this.aGroup872, 8388616)).setText(UILocale.get("SMS_CONSOLE_CLEAR", "Clear"));
		this.aButton878.setLayoutData(gridData);
		this.aButton878.addSelectionListener(new Class84(this));
		(this.aButton880 = new Button(this.aGroup872, 8388616)).setText(UILocale.get("SMS_CONSOLE_SEND", "Send"));
		this.aButton880.setLayoutData(gridData);
		this.aButton880.addSelectionListener(new Class87(this));
	}

	private void method491() {
		final GridData gridData;
		(gridData = new GridData()).horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = 4;
		(this.aGroup876 = new Group(this.aShell868, 0)).setLayout(new GridLayout());
		this.aGroup876.setText(UILocale.get("SMS_CONSOLE_RECEIVE", "Receive from midlet"));
		this.aGroup876.setLayoutData(gridData);
		(this.aButton873 = new Button(this.aGroup876, 32)).setText(UILocale.get("SMS_CONSOLE_BLOCK", "Block the received message"));
		this.aButton873.setSelection(this.aBoolean869);
		this.aButton873.addSelectionListener(new Class86(this));
		(this.aStyledText877 = new StyledText(this.aGroup876, 2624)).setLayoutData(gridData);
	}

	public final boolean method488() {
		return this.aBoolean881;
	}

	public final void controlMoved(final ControlEvent controlEvent) {
		Class83 class83;
		boolean aBoolean881;
		if (Math.abs(this.aShell879.getLocation().x - this.aShell868.getSize().x - this.aShell868.getLocation().x) < 10 && Math.abs(this.aShell879.getLocation().y - this.aShell868.getLocation().y) < 20) {
			this.aShell868.setLocation(this.aShell879.getLocation().x - this.aShell868.getSize().x, this.aShell879.getLocation().y);
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
		this.method482();
	}

	static StyledText method483(final Class83 class83) {
		return class83.aStyledText877;
	}

	static StyledText method489(final Class83 class83) {
		return class83.aStyledText874;
	}

	static Vector method484(final Class83 class83) {
		return class83.aVector870;
	}

	static boolean method485(final Class83 class83, final boolean aBoolean869) {
		return class83.aBoolean869 = aBoolean869;
	}

	static Button method486(final Class83 class83) {
		return class83.aButton873;
	}

	private final class Textout implements Runnable {
		private final Class83 aClass83_867;

		private Textout(final Class83 aClass83_867) {
			super();
			this.aClass83_867 = aClass83_867;
		}

		public final void run() {
			if (Class83.method483(this.aClass83_867) == null || Class83.method483(this.aClass83_867).isDisposed()) {
				return;
			}
			Class83.method483(this.aClass83_867).append(Class83.aString871);
			Class83.method483(this.aClass83_867).setTopIndex(Class83.method483(this.aClass83_867).getLineCount());
		}

		Textout(final Class83 class83, final Class84 class84) {
			this(class83);
		}
	}
}
