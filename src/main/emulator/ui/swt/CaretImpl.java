package emulator.ui.swt;

import com.nokia.mid.ui.TextEditor;
import emulator.Emulator;
import emulator.Settings;
import emulator.ui.ICaret;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Text;

import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.TextField;

public final class CaretImpl implements ICaret, ModifyListener, TraverseListener, FocusListener, KeyListener {
	private static Font font;
	private Text swtText;
	private Canvas swtCanvas;
	private Caret swtCaret;
	private float zoom;
	private int caretLocX;
	private int caretLocY;
	private int caretX;
	private int caretY;
	private Object currentItem;
	private int caretPosition;
	private int caretCol;
	private int caretRow;
	private org.eclipse.swt.graphics.Font swtFont;

	public CaretImpl(final Canvas aCanvas838) {
		super();
		this.swtCanvas = aCanvas838;
		(this.swtCaret = new Caret(this.swtCanvas, 0)).setVisible(false);

		this.zoom = 1.0f;
	}

	public final int getCaretPosition() {
		if (swtText == null || currentItem == null) return 0;
		EmulatorImpl.syncExec( new Runnable() {
			public void run() {
				try {
					caretPosition = swtText.getCaretPosition();
				} catch (Exception ignored) {}
			}
		});
		return caretPosition;
	}

	public final String getSelection() {
		if (swtText == null || currentItem == null) return null;
		return swtText.getSelectionText();
	}

	public void setCaret(final int index) {
		if (swtText == null || currentItem == null) return;
		EmulatorImpl.syncExec( new Runnable() {
			public void run() {
				try {
					swtText.setSelection(index);
				} catch (Exception ignored) {}
			}
		});
	}

	public void setSelection(final int index, final int length) {
		if (swtText == null || currentItem == null) return;
		EmulatorImpl.syncExec( new Runnable() {
			public void run() {
				try {
					swtText.setSelection(index, length);
				} catch (Exception ignored) {}
			}
		});
	}

	public final void a(Transform paramTransform, int paramInt) {
	}

	public final void setCaretLocation(final int x, final int y) {
		this.caretLocX = x;
		this.caretLocY = y;
		int[] i = ((EmulatorScreen) Emulator.getEmulator().getScreen()).transformCaret(x, y);
		swtCaret.setLocation(i[0], i[1]);
		if (swtText != null) swtText.setLocation(i[0], i[1]);
	}

	public final void setWindowZoom(final float aFloat840) {
		this.zoom = aFloat840;
		if (currentItem != null) {
			if (swtFont != null && !swtFont.isDisposed() && swtText != null) {
				swtText.setFont(null);
				swtFont.dispose();
			}
			if (currentItem instanceof DateField) {
				this.swtCaret.setSize(Math.min(1, (int) aFloat840), (int) (font.getBaselinePosition() * aFloat840));
				this.setCaretLocation(this.caretLocX, this.caretLocY);
//				swtCaret.setVisible(true);
			} else {
				int w, h;
				if (currentItem instanceof TextEditor) {
					w = ((TextEditor) currentItem).getWidth();
					h = ((TextEditor) currentItem).getHeight();
				} else {
					w = ((TextField) currentItem)._getTextAreaWidth();
					h = ((TextField) currentItem)._getTextAreaHeight();
				}
				swtFont = new org.eclipse.swt.graphics.Font(swtCanvas.getDisplay(),
						Emulator.getEmulator().getProperty().getDefaultFontName(),
						Math.max(2, (int) (font.getHeight() * zoom * 0.7f) - 1), 0);
				swtText.setFont(swtFont);
				int[] i = ((EmulatorScreen) Emulator.getEmulator().getScreen()).transformCaretSize(w, h);
				this.swtText.setSize(Math.max(1, i[0]), Math.max(1, i[1]));
				this.setCaretLocation(this.caretLocX, this.caretLocY);
				swtText.setVisible(true);
			}
		}
	}

	public final void focusItem(final Object item, final int x, final int y) {
		Object tmp;
		synchronized (this) {
			tmp = this.currentItem;
			this.currentItem = item;
		}
		if (tmp != item) {
			if (tmp instanceof TextEditor) {
				((TextEditor) tmp).setFocus(false);
			} else if (tmp instanceof TextField) {
				((TextField) tmp)._swtFocusLost();
			}
		}

		this.caretX = x;
		this.caretY = y;
		this.caretPosition = 0;
		this.caretCol = 0;
		this.caretRow = 0;

		if (item instanceof TextEditor) {
			font = ((TextEditor) item).getFont();
		} else {
			font = Font.getDefaultFont();
		}

		final Object lastItem = tmp;
		EmulatorImpl.syncExec(new Runnable() {
			public final void run() {
				if (lastItem != item && swtText != null) {
					if (!swtText.isDisposed()) swtText.dispose();
					swtText = null;
				}

				if (item instanceof DateField) {
					swtCaret.setVisible(true);
				} else {
					swtCaret.setVisible(false);

					Text text = swtText;
					if (text == null) {
						text = new Text(swtCanvas,
								item instanceof TextField || !((TextEditor) item).isMultiline()
										? SWT.MULTI : SWT.MULTI | SWT.WRAP);
						text.setText(item instanceof TextField ?
								((TextField) item).getString() : ((TextEditor) item).getContent());
						text.setData(item);
						text.addModifyListener(CaretImpl.this);
						text.addTraverseListener(CaretImpl.this);
						text.addFocusListener(CaretImpl.this);
						text.addKeyListener(CaretImpl.this);
						swtText = text;
					}

					text.setEditable(!(item instanceof TextField) || !((TextField) item)._isUneditable());
					text.setVisible(true);
					text.setFocus();
				}
				setCaretLocation(caretX, caretY);
				setWindowZoom(zoom);
				((EmulatorScreen) Emulator.getEmulator().getScreen()).toggleMenuAccelerators(false);
			}
		});
	}

	public final void defocusItem(final Object item) {
		Object tmp;
		synchronized (this) {
			tmp = this.currentItem;
			if (tmp != item) return;
			this.currentItem = null;
		}
		if (tmp instanceof TextEditor) {
			((TextEditor) tmp).setFocus(false);
		} else if (tmp instanceof TextField) {
			((TextField) tmp)._swtFocusLost();
		}
		EmulatorImpl.syncExec(new Runnable() {
			public final void run() {
				if (swtText != null && !swtText.isDisposed()) {
					swtText.dispose();
					swtText = null;
				}
				swtCaret.setVisible(false);
				if (!Settings.canvasKeyboard)
					((EmulatorScreen) Emulator.getEmulator().getScreen()).toggleMenuAccelerators(true);
			}
		});
	}

	public void updateText(Object item, final String text) {
		if (item != this.currentItem || swtText == null) return;
		EmulatorImpl.syncExec(new Runnable() {
			public final void run() {
				try {
					swtText.setText(text);
				} catch (Exception ignored) {}
			}
		});
	}

	public void displayableChanged() {
		if (this.currentItem == null) return;
		defocusItem(currentItem);
	}

	public final void mouseDown(int x, int y) {
	}

	public final boolean _keyPressed(KeyEvent event) {
		if (this.currentItem == null || !(currentItem instanceof DateField))
			return false;
		char c = event.character;
		if (c == '\n' || c == '\r') c = 0;
		if (c >= '0' && c <= '9' || c == 0) {
			((DateField) currentItem)._input(c);
		}
		return true;
	}

	public void keyPressed(KeyEvent keyEvent) {
		if (keyEvent.keyCode == SWT.F1 || keyEvent.keyCode == SWT.F2 || keyEvent.keyCode == SWT.F3) {
			((EmulatorScreen) Emulator.getEmulator().getScreen()).keyPressed(keyEvent);
		}
	}

	public void keyReleased(KeyEvent keyEvent) {
		if (keyEvent.keyCode == SWT.F1 || keyEvent.keyCode == SWT.F2 || keyEvent.keyCode == SWT.F3) {
			((EmulatorScreen) Emulator.getEmulator().getScreen()).keyReleased(keyEvent);
		}
	}


	static int caretX(final CaretImpl class67) {
		return class67.caretX;
	}

	static int caretY(final CaretImpl class67) {
		return class67.caretY;
	}

	static Caret caret(final CaretImpl class67) {
		return class67.swtCaret;
	}

	static float caretFloat(final CaretImpl class67) {
		return class67.zoom;
	}

	static {
		font = Font.getDefaultFont();
	}



	public void keyTraversed(TraverseEvent e) {
		String text = swtText.getText();
		int length = text.length();
		int pos = swtText.getCaretPosition(),
			line = swtText.getCaretLineNumber(),
			key = e.keyCode;
		Object item = e.widget.getData();
		switch (e.detail) {
			case SWT.TRAVERSE_ESCAPE:
			case SWT.TRAVERSE_TAB_PREVIOUS:
			case SWT.TRAVERSE_TAB_NEXT:
				defocusItem(currentItem);
				break;
			case SWT.TRAVERSE_ARROW_PREVIOUS:
			case SWT.TRAVERSE_ARROW_NEXT:
				if (item instanceof TextEditor) break;
				if (length == 0) {
					defocusItem(item);
					break;
				}
				boolean b = false;
				switch (key) {
					case SWT.ARROW_UP:
						b = line == 0;
						break;
					case SWT.ARROW_DOWN:
						b = !text.contains("\n");
						break;
					case SWT.ARROW_LEFT:
						b = pos == 0;
						break;
					case SWT.ARROW_RIGHT:
						b = pos == length;
						break;
				}
				if (b) defocusItem(item);
				break;
		}
	}

	public void modifyText(ModifyEvent modifyEvent) {
		String text = ((Text) modifyEvent.widget).getText();
		Object item = modifyEvent.widget.getData();
		if (item instanceof TextEditor) {
			if (text.equals(((TextEditor) item).getContent())) return;
			((TextEditor) item)._contentChanged(text);
		} else {
			if (text.equals(((TextField) item).getString())) return;
			((TextField) item)._setString(text);
		}
	}

	public void focusGained(FocusEvent e) {
		if (e.widget != swtText) {
			swtCanvas.setFocus();
		}
	}

	public void focusLost(FocusEvent e) {
	}
}
