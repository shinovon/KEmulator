package javax.microedition.lcdui;

import emulator.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class TextBox extends Screen {
//	private TextField textField;

	private SwtModifyListener swtModifyListener = new SwtModifyListener();
	private Text swtText;

	private String text;
	private int size;
	private int maxSize;

	public TextBox(String title, String text, int maxSize, int constraints) {
		super(title);
		constructSwt();
		setString(text);
		setMaxSize(maxSize);
		setConstraints(constraints);
	}

	protected Composite constructSwtContent(int style) {
		Composite c = super.constructSwtContent(style);
		swtText = new Text(c, SWT.V_SCROLL);
		return c;
	}

	public void swtShown() {
		super.swtShown();
		swtText.addModifyListener(swtModifyListener);
	}

	public void swtHidden() {
		super.swtHidden();
		swtText.removeModifyListener(swtModifyListener);
	}

	public void swtResized(int w, int h) {
		super.swtResized(w, h);
		swtText.setBounds(swtContent.getClientArea());
	}

	protected void focusCaret() {
	}

	private void swtGetText() {
		text = swtText.getText();
	}

	private void swtSetText(String text) {
		swtText.setText(text);
	}

	public String getString() {
		safeSyncExec(new Runnable() {
			public void run() {
				swtGetText();
			}
		});
		return text;
	}

	public void setString(String string) {
		if (string == null) {
			string = "";
		}
		text = string;
		size = string.length();
		safeSyncExec(new Runnable() {
			public void run() {
				swtSetText(text);
			}
		});
	}

	public int getChars(final char[] array) {
		// TODO
		return 0;
	}

	public void setChars(final char[] array, final int n, final int n2) {
		// TODO
	}

	public void insert(final String s, final int n) {
		// TODO
	}

	public void insert(final char[] array, final int n, final int n2, final int n3) {
		// TODO
	}

	public void delete(final int n, final int n2) {
		// TODO
	}

	public int getMaxSize() {
		safeSyncExec(new Runnable() {
			public void run() {
				maxSize = swtText.getTextLimit();
			}
		});
		return maxSize;
	}

	public int setMaxSize(final int maxSize) {
		safeSyncExec(new Runnable() {
			public void run() {
				swtText.setTextLimit(maxSize);
			}
		});
		return getMaxSize();
	}

	public int size() {
		safeSyncExec(new Runnable() {
			public void run() {
				size = swtText.getText().length();
			}
		});
		return size;
	}

	public int getCaretPosition() {
		// TODO
		return 0;
	}

	public void setConstraints(final int constraints) {
		// TODO
	}

	public int getConstraints() {
		// TODO
		return 0;
	}

	public void setInitialInputMode(final String initialInputMode) {
		// TODO
	}

	public void setTitle(final String title) {
		super.setTitle(title);
	}

	public void setTicker(final Ticker ticker) {
		super.setTicker(ticker);
	}

	protected void paint(final Graphics graphics) {
	}

	protected void layout() {
	}

	protected void defocus() {
	}

	class SwtModifyListener implements ModifyListener
	{

		public void modifyText(ModifyEvent me)
		{
		}

	}

}
