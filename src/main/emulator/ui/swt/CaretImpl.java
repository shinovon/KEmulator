package emulator.ui.swt;

import com.nokia.mid.ui.Clipboard;
import com.nokia.mid.ui.TextEditor;
import emulator.Emulator;
import emulator.ui.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Canvas;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.Item;

import emulator.lcdui.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Transform;

public final class CaretImpl implements ICaret {
	private static final Font font;
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

	public CaretImpl(final Canvas aCanvas838) {
		super();
		this.swtCanvas = aCanvas838;
		(this.swtCaret = new Caret(this.swtCanvas, 0)).setVisible(false);
		this.zoom = 1.0f;
	}

	public final int getCaretPosition() {
		return this.caretPosition;
	}

	public final void a(Transform paramTransform, int paramInt) {
	}

	public final void setCaretLocation(final int x, final int y) {
		this.caretLocX = x;
		this.caretLocY = y;
		int[] i = ((EmulatorScreen) Emulator.getEmulator().getScreen()).transformCaret(x, y);
		swtCaret.setLocation(i[0], i[1]);
	}

	public final void setWindowZoom(final float aFloat840) {
		this.zoom = aFloat840;
		if (this.swtCaret.isVisible()) {
			Font font = item instanceof TextEditor ? ((TextEditor) item).getFont() : CaretImpl.font;
			this.swtCaret.setSize(Math.min(1, (int) aFloat840), (int) (font.getBaselinePosition() * aFloat840));
			this.setCaretLocation(this.caretLocX, this.caretLocY);
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
		EmulatorImpl.syncExec(new Class121(this));
	}

	public final void defocusItem(final Object item) {
		if (this.item == item) {
			Object tmp = this.item;
			this.item = null;
			if (tmp instanceof TextEditor) {
				((TextEditor) tmp).setFocus(false);
			}
			EmulatorImpl.syncExec(new Class115(this));
		}
	}

	public void displayableChanged() {
		if (this.item == null) return;
		Object tmp = this.item;
		item = null;
		if (tmp instanceof TextEditor) {
			((TextEditor) tmp).setFocus(false);
		}
		EmulatorImpl.syncExec(new Class115(this));
	}

	public final void mouseDown(int x, int y) {
		if (item == null || !(item instanceof TextField || item instanceof TextEditor)) return;
		Font font = item instanceof TextEditor ? ((TextEditor) item).getFont() : CaretImpl.font;
		int textHeight = font.getHeight() + 4;
		int line = (y - this.caretY) / textHeight;
		int w = (item instanceof TextEditor ? ((TextEditor) item).getWidth() : ((Item) item).getPreferredWidth()) - 8;
		String[] arr;
		String s = item instanceof TextEditor ? ((TextEditor) item).getContent() : ((TextField) item).getString();
		if (s == null) s = "";
		if ((arr = c.textArr(s, font, w, w)) != null
				&& line >= 0 && line < arr.length) {
			int lineLength = arr[line].length();
			int var9 = 0;
			int var10 = 0;
			this.caretCol = 0;

			int var11;
			for (var11 = x - this.caretX; this.caretCol <= lineLength; ++this.caretCol) {
				var10 = var9;
				if ((var9 = font.substringWidth(arr[line], 0, this.caretCol)) >= var11) {
					break;
				}
			}

			int var12 = (var9 - var10) / 2;
			int var10002;
			if (var11 >= var10 + var12) {
				var10002 = var9;
			} else {
				--this.caretCol;
				var10002 = var10;
			}

			this.setCaretLocation(caretX + var10002, this.caretY + line * textHeight);
			this.caretRow = line;
			this.caretCol = Math.min(Math.max(0, this.caretCol), lineLength);
			int var10001 = this.caretCol;

			while (true) {
				this.caretPosition = var10001;
				if (line <= 0) {
					return;
				}

				--line;
				var10001 = this.caretPosition + arr[line].length();
			}
		}
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
		if ((event.stateMask & SWT.CONTROL) == SWT.CONTROL && event.character == 0x16) {
			String s = Clipboard.copyFromClipboard();
			if (s == null || s.length() == 0) return false;
			char[] c = s.toCharArray();
			for (int i = 0; i < c.length; ++i) {
				type(c[i], 0, i == c.length - 1);
			}
			return true;
		}

		return type(event.character, event.keyCode, true);
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
					((TextEditor) item).setContent(text);
					if (event) ((TextEditor) item)._contentChanged();
				} else {
					((TextField) item).setString(text);
					if (event) ((Item) item).notifyStateChanged();
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
}
