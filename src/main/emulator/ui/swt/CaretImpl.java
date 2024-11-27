package emulator.ui.swt;

import com.nokia.mid.ui.Clipboard;
import com.nokia.mid.ui.TextEditor;
import emulator.Emulator;
import emulator.Settings;
import emulator.ui.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Canvas;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.Item;

import emulator.lcdui.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Transform;

public final class CaretImpl implements ICaret, ModifyListener, TraverseListener, FocusListener {
	private static final Font font;
	private final Text swtText;
	private Canvas swtCanvas;
	private Caret swtCaret;
	private float zoom;
	private int caretLocX;
	private int caretLocY;
	private int caretX;
	private int caretY;
	private Object item;
	private int caretPosition;
	private int caretCol;
	private int caretRow;
	private org.eclipse.swt.graphics.Font swtFont;

	public CaretImpl(final Canvas aCanvas838) {
		super();
		this.swtCanvas = aCanvas838;
		(this.swtCaret = new Caret(this.swtCanvas, 0)).setVisible(false);

		swtText = new Text(swtCanvas, SWT.MULTI | SWT.WRAP);

		swtText.setVisible(false);
		swtText.addModifyListener(this);
		swtText.addTraverseListener(this);
		swtText.addFocusListener(this);

		this.zoom = 1.0f;
	}

	public final int getCaretPosition() {
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
		return swtText.getSelectionText();
	}

	public void setCaret(final int index) {
		EmulatorImpl.syncExec( new Runnable() {
			public void run() {
				try {
					swtText.setSelection(index);
				} catch (Exception ignored) {}
			}
		});
	}

	public void setSelection(final int index, final int length) {
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
		swtText.setLocation(i[0], i[1]);
	}

	public final void setWindowZoom(final float aFloat840) {
		this.zoom = aFloat840;
		if (item != null) {
			if (swtFont != null && !swtFont.isDisposed()) {
				swtText.setFont(null);
				swtFont.dispose();
			}
			if (item instanceof DateField) {
				this.swtCaret.setSize(Math.min(1, (int) aFloat840), (int) (font.getBaselinePosition() * aFloat840));
				this.setCaretLocation(this.caretLocX, this.caretLocY);
//				swtCaret.setVisible(true);
			} else {
				int w, h;
				if (item instanceof TextEditor) {
					w = ((TextEditor) item).getWidth();
					h = ((TextEditor) item).getHeight();
				} else {
					w = ((TextField) item)._getTextAreaWidth();
					h = ((TextField) item)._getTextAreaHeight();
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
		Object tmp = this.item;
		this.item = item;
		if (tmp != item && tmp instanceof TextEditor) {
			((TextEditor) tmp).setFocus(false);
		}
		this.caretX = x;
		this.caretY = y;
		this.caretPosition = 0;
		this.caretCol = 0;
		this.caretRow = 0;
		EmulatorImpl.syncExec(new Runnable() {
			public final void run() {
				setCaretLocation(caretX, caretY);
				if (CaretImpl.this.item instanceof DateField) {
					swtText.setVisible(false);
					swtCaret.setVisible(true);
				} else {
					swtCaret.setVisible(false);
					swtText.setVisible(true);
					swtText.setEditable(!(item instanceof TextField) || !((TextField) item)._isUneditable());
					swtText.setText(item instanceof TextField ? ((TextField) item).getString() : ((TextEditor) item).getContent());
					swtText.setFocus();
				}
				setWindowZoom(zoom);
				((EmulatorScreen) Emulator.getEmulator().getScreen()).toggleMenuAccelerators(false);
			}
		});
	}

	public final void defocusItem(final Object item) {
		if (this.item == item) {
			Object tmp = this.item;
			this.item = null;
			if (tmp instanceof TextEditor) {
				((TextEditor) tmp).setFocus(false);
			}
			EmulatorImpl.syncExec(new Runnable() {
				public final void run() {
					swtText.setVisible(false);
					swtCaret.setVisible(false);
					swtCanvas.setFocus();
					if (!Settings.canvasKeyboard)
						((EmulatorScreen) Emulator.getEmulator().getScreen()).toggleMenuAccelerators(true);
				}
			});
		}
	}

	public void updateText(Object item, final String text) {
		if (item != this.item) return;
		EmulatorImpl.syncExec(new Runnable() {
			public final void run() {
				swtText.setText(text);
			}
		});
	}

	public void displayableChanged() {
		if (this.item == null) return;
		defocusItem(item);
	}

	public final void mouseDown(int x, int y) {
//		if (item == null || !(item instanceof TextField || item instanceof TextEditor)) return;
//		Font font = item instanceof TextEditor ? ((TextEditor) item).getFont() : CaretImpl.font;
//		int textHeight = font.getHeight() + 4;
//		int line = (y - this.caretY) / textHeight;
//		int w = (item instanceof TextEditor ? ((TextEditor) item).getWidth() : ((Item) item).getPreferredWidth()) - 8;
//		String[] arr;
//		String s = item instanceof TextEditor ? ((TextEditor) item).getContent() : ((TextField) item).getString();
//		if (s == null) s = "";
//		if ((arr = c.textArr(s, font, w, w)) != null
//				&& line >= 0 && line < arr.length) {
//			int lineLength = arr[line].length();
//			int var9 = 0;
//			int var10 = 0;
//			this.caretCol = 0;
//
//			int var11;
//			for (var11 = x - this.caretX; this.caretCol <= lineLength; ++this.caretCol) {
//				var10 = var9;
//				if ((var9 = font.substringWidth(arr[line], 0, this.caretCol)) >= var11) {
//					break;
//				}
//			}
//
//			int var12 = (var9 - var10) / 2;
//			int var10002;
//			if (var11 >= var10 + var12) {
//				var10002 = var9;
//			} else {
//				--this.caretCol;
//				var10002 = var10;
//			}
//
//			this.setCaretLocation(caretX + var10002, this.caretY + line * textHeight);
//			this.caretRow = line;
//			this.caretCol = Math.min(Math.max(0, this.caretCol), lineLength);
//			int var10001 = this.caretCol;
//
//			while (true) {
//				this.caretPosition = var10001;
//				if (line <= 0) {
//					return;
//				}
//
//				--line;
//				var10001 = this.caretPosition + arr[line].length();
//			}
//		}
	}

	public final boolean keyPressed(KeyEvent event) {
		if (this.item == null) return false;
		if (!(item instanceof TextField || item instanceof TextEditor)) {
			if (item instanceof DateField) {
				char c = event.character;
				if (c == '\n' || c == '\r') c = 0;
				if (c >= '0' && c <= '9' || c == 0) {
					((DateField) item)._input(c);
				}
			}
			return true;
		}

		// handle ctrl+v
//		if ((event.stateMask & SWT.CONTROL) == SWT.CONTROL && event.character == 0x16) {
//			String s = Clipboard.copyFromClipboard();
//			if (s == null || s.length() == 0) return false;
//			char[] c = s.toCharArray();
//			for (int i = 0; i < c.length; ++i) {
//				type(c[i], 0, i == c.length - 1);
//			}
//			return true;
//		}
//
//		return type(event.character, event.keyCode, true);
		return true;
	}

	private boolean type(char character, int keyCode, boolean event) {
		int w = (item instanceof TextEditor ? ((TextEditor) item).getWidth() : ((Item) item).getPreferredWidth()) - 8;
		String text = item instanceof TextEditor ? ((TextEditor) item).getContent() : ((TextField) item).getString();
		Font font = item instanceof TextEditor ? ((TextEditor) item).getFont() : CaretImpl.font;
		if (text == null) text = "";
		if (caretPosition > text.length()) caretPosition = text.length();
		String[] var4;
		if ((var4 = c.textArr(text, font, w, w)) != null && this.caretRow >= 0 && this.caretRow < var4.length) {
			int var5 = font.getHeight() + 4;
			String var6;
			int var7 = (var6 = var4[this.caretRow]).length();
			if (character == 0) {
				label131:
				{
					CaretImpl var10000;
					int var10001;
					label118:
					{
						switch (keyCode) {
							case 16777219:
								var10000 = this;
								var10001 = this.caretCol - 1;
								break;
							case 16777220:
								var10000 = this;
								var10001 = this.caretCol + 1;
								break;
							case 16777221:
							case 16777222:
							default:
								break label118;
							case 16777223:
								var10000 = this;
								var10001 = 0;
								break;
							case 16777224:
								var10000 = this;
								var10001 = var7;
						}

						var10000.caretCol = var10001;
					}

					label112:
					{
						if (this.caretCol < 0) {
							if (this.caretRow <= 0) {
								var10000 = this;
								var10001 = 0;
								break label112;
							}

							--this.caretRow;
							var7 = (var6 = var4[this.caretRow]).length();
						} else {
							if (this.caretCol <= var7) {
								break label131;
							}

							if (this.caretRow < var4.length - 1) {
								++this.caretRow;
								var7 = (var6 = var4[this.caretRow]).length();
								var10000 = this;
								var10001 = 0;
								break label112;
							}
						}

						var10000 = this;
						var10001 = var7;
					}

					var10000.caretCol = var10001;
				}
			}

			String var8 = var6.substring(0, this.caretCol);
			var6.substring(this.caretCol, var7);
			String var9 = null;
			switch (character) {
				case '\b':
					if (this.caretCol == 0) {
						if (this.caretRow > 0) {
							--this.caretRow;
							var7 = (var6 = var4[this.caretRow]).length();
							this.caretCol = var7;
							var8 = var6.substring(0, this.caretCol);
							var6.substring(this.caretCol, var7);
						}
					} else {
						var8 = var6.substring(0, --this.caretCol);
						if (this.caretCol == 0 && this.caretRow > 0) {
							--this.caretRow;
							var7 = (var6 = var4[this.caretRow]).length();
							this.caretCol = var7;
							var8 = var6.substring(0, this.caretCol);
							var6.substring(this.caretCol, var7);
						}

						text = text.substring(0, this.caretPosition - 1) + text.substring(this.caretPosition);
					}
				case '\t':
				case '\n':
				case '\r':
					break;
				case '\u007f':
					if (this.caretCol == var7) {
						if (this.caretRow < var4.length - 1) {
							++this.caretRow;
							var7 = (var6 = var4[this.caretRow]).length();
							this.caretCol = 0;
							var8 = var6.substring(0, this.caretCol);
							var6.substring(this.caretCol, var7);
						}
					} else {
						if (var6.substring(this.caretCol + 1, var7).length() == 0 && this.caretCol == 0 && this.caretRow > 0) {
							--this.caretRow;
							var7 = (var6 = var4[this.caretRow]).length();
							this.caretCol = var7;
							var8 = var6.substring(0, this.caretCol);
							var6.substring(this.caretCol, var7);
						}

						text = text.substring(0, this.caretPosition) + text.substring(this.caretPosition + 1);
					}
					break;
				default:
					int max = item instanceof TextEditor ? ((TextEditor) item).getMaxSize() : ((TextField) item).getMaxSize();
					if (character >= 32 && text.length() < max) {
						try {
							text = text.substring(0, this.caretPosition) + character + text.substring(this.caretPosition);
							if (this.caretCol == var7 && this.caretRow < var4.length - 1) {
								++this.caretRow;
								var7 = (var6 = var4[this.caretRow]).length();
								this.caretCol = 0;
								var8 = var6.substring(0, this.caretCol);
								var6.substring(this.caretCol, var7);
							}

							var8 = var8 + character;
							++this.caretCol;
							if (font.stringWidth(var8) > w) {
								var9 = "";
								var8 = var9 + character;
								this.caretCol = 1;
								++this.caretRow;
							}
						} catch (Exception ignored) {}
					}
			}

			int var10 = font.stringWidth(var8);
			this.setCaretLocation(this.caretX + var10, this.caretY + this.caretRow * var5);
			if (character != 0) {
				if (item instanceof TextEditor) {
					((TextEditor) item)._contentChanged(text);
				} else {
					((TextField) item)._setString(text);
				}
			}

			this.caretPosition = this.caretCol;

			for (int var11 = 0; var11 < this.caretRow; ++var11) {
				this.caretPosition += var4[var11].length();
			}
		}
		return true;
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

	public void modifyText(ModifyEvent modifyEvent) {
		String text = swtText.getText();
		if (item == null) return;
		if (item instanceof TextEditor) {
			if (text.equals(((TextEditor) item).getContent())) return;
			((TextEditor) item)._contentChanged(text);
		} else {
			if (text.equals(((TextField) item).getString())) return;
			((TextField) item)._setString(text);
		}
	}

	public void keyTraversed(TraverseEvent e) {
		int l = swtText.getText().length();
		int i = swtText.getCaretPosition(), y = swtText.getCaretLineNumber(), h = swtText.getTopIndex();
		int k = e.keyCode;
		switch (e.detail) {
			case SWT.TRAVERSE_ESCAPE:
			case SWT.TRAVERSE_TAB_PREVIOUS:
			case SWT.TRAVERSE_TAB_NEXT:
				defocusItem(item);
				break;
//			case SWT.TRAVERSE_ARROW_PREVIOUS:
//				if (i == 0) defocusItem(item);
//				break;
//
//			case SWT.TRAVERSE_ARROW_NEXT:
//				if (i == l) defocusItem(item);
//				break;
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
