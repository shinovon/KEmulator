package javax.microedition.lcdui;

import emulator.lcdui.BoundsUtils;
import emulator.lcdui.c;

import java.util.Vector;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.a;

public class ChoiceGroup
		extends Item
		implements Choice {
	boolean aBoolean541;
	int choiceType;
	private int fitPolicy;
	private Vector items;
	boolean aBoolean542;
	int anInt28;
	int anInt29;
	Command aCommand540;
	private int currentSelect;

	public ChoiceGroup(String label, int choiceType) {
		this(label, choiceType, new String[0], null);
	}

	public ChoiceGroup(String label, int choiceType, String[] stringElements, Image[] imageElements) {
		this(label, choiceType, stringElements, imageElements, false);
	}

	ChoiceGroup(String label, int choiceType, String[] strs, Image[] imgs, boolean b) {
		super(label);
		if (!(choiceType == MULTIPLE || choiceType == EXCLUSIVE || choiceType == IMPLICIT && b || choiceType == POPUP)) {
			throw new IllegalArgumentException();
		}
		int i = 0;
		while (i < strs.length) {
			if (strs[i] == null) {
				throw new NullPointerException();
			}
			++i;
		}
		if (imgs != null && strs.length != imgs.length) {
			throw new IllegalArgumentException();
		}
		this.choiceType = choiceType;
		this.fitPolicy = 0;
		this.items = new Vector();
		int j = 0;
		while (j < strs.length) {
			this.items.add(new a(strs[j], imgs == null ? null : imgs[j], null, this));
			++j;
		}
		if (this.items.size() > 0) {
			((a) this.items.get(0)).sel = true;
		}
	}

	public synchronized int append(String s, Image image) {
		if (s == null) {
			throw new NullPointerException();
		}
		this.items.add(new a(s, image, null, this));
		return this.items.size() - 1;
	}

	public synchronized void delete(int n) {
		if (n < 0 || n >= this.items.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.items.remove(n);
	}

	public synchronized void deleteAll() {
		this.items.removeAllElements();
	}

	public void setFitPolicy(int anInt30) {
		this.fitPolicy = anInt30;
	}

	public int getFitPolicy() {
		return this.fitPolicy;
	}

	public void setFont(int n, Font aFont420) {
		if (n < 0 || n >= this.items.size()) {
			throw new IndexOutOfBoundsException();
		}
		((a) this.items.get(n)).font = aFont420;
	}

	public Font getFont(int n) {
		return ((a) this.items.get(n)).font;
	}

	public Image getImage(int n) {
		return ((a) this.items.get(n)).image;
	}

	public String getString(int n) {
		return ((a) this.items.get(n)).string;
	}

	public synchronized void insert(int n, String s, Image image) {
		if (s == null) {
			throw new NullPointerException();
		}
		this.items.insertElementAt(new a(s, image, null, this), n);
	}

	public synchronized void set(int n, String s, Image image) {
		if (s == null) {
			throw new NullPointerException();
		}
		this.items.set(n, new a(s, image, null, this));
	}

	public synchronized void setSelectedFlags(boolean[] array) {
		if (array == null) {
			throw new NullPointerException();
		}
		if (array.length < this.items.size()) {
			throw new IllegalArgumentException();
		}
		if (this.choiceType != MULTIPLE) {
			int n = 0;
			int i = 0;
			while (i < this.items.size()) {
				if (n == 0 && array[i]) {
					((a) this.items.get(i)).sel = true;
					n = 1;
				} else {
					((a) this.items.get(i)).sel = false;
				}
				++i;
			}
			if (n == 0 && this.items.size() > 0) {
				((a) this.items.get(n)).sel = true;
			}
			return;
		}
		int j = 0;
		while (j < this.items.size()) {
			((a) this.items.get(j)).sel = array[j];
			++j;
		}
	}

	public int getSelectedFlags(boolean[] array) {
		if (array == null) {
			throw new NullPointerException();
		}
		if (array.length < this.items.size()) {
			throw new IllegalArgumentException();
		}
		int n = 0;
		int i = 0;
		while (i < this.items.size()) {
			array[i] = ((a) this.items.get(i)).sel;
			if (array[i]) {
				++n;
			}
			++i;
		}
		return n;
	}

	public int getSelectedIndex() {
		if (this.choiceType != MULTIPLE) {
			int i = 0;
			while (i < this.items.size()) {
				if (((a) this.items.get(i)).sel) {
					return i;
				}
				++i;
			}
		}
		return 0;
	}

	public boolean isSelected(int n) {
		return ((a) this.items.get(n)).sel;
	}

	public synchronized void setSelectedIndex(int n, boolean flag) {
		if (n < 0 || n >= this.items.size()) {
			throw new IndexOutOfBoundsException();
		}
		if (this.choiceType != MULTIPLE) {
			if (flag) {
				currentSelect = n;
				int i = 0;
				while (i < this.items.size()) {
					((a) this.items.get(i)).sel = i == n;
					++i;
				}
			}
			return;
		}
		((a) this.items.get(n)).sel = flag;
	}

	public int size() {
		return this.items.size();
	}

	protected void itemApplyCommand() {
		super.itemApplyCommand();
		if (this.aBoolean541 && this.aCommand540 != null) {
			if (screen.cmdListener != null)
				this.screen.cmdListener.commandAction(this.aCommand540, this.screen);
		}
		if (this.choiceType == EXCLUSIVE) {
			notifyStateChanged();
			this.setSelectedIndex(this.currentPos, true);
			return;
		}
		if (this.choiceType == MULTIPLE) {
			notifyStateChanged();
			this.setSelectedIndex(this.currentPos, !this.isSelected(this.currentPos));
			return;
		}
		if (this.choiceType == POPUP) {
			if (this.aBoolean542) {
				notifyStateChanged();
				this.setSelectedIndex(this.currentPos, true);
			}
			this.aBoolean542 = !this.aBoolean542;
		}
	}

	protected void paint(Graphics g, int x, int y, int w, int h) {
		if (!this.aBoolean541) {
			super.paint(g, x, y, w, h);
		} else {
			g.setColor(-16777216);
		}
		int n = y;
		if (this.labelArr != null && this.labelArr.length > 0) {
			g.setFont(Item.font);
			int i = 0;
			while (i < this.labelArr.length) {
				g.drawString(this.labelArr[i], x + 4, n + 2, 0);
				n += Item.font.getHeight() + 4;
				++i;
			}
		}
		if (this.items.size() > 0) {
			switch (this.choiceType) {
				case EXCLUSIVE:
				case MULTIPLE:
				case IMPLICIT: {
					int j = 0;
					while (j < this.items.size()) {
						a a2 = (a) this.items.get(j);
						if (a2.aBoolean424) {
							a2.method211(g, this.inFocus && j == this.currentPos, y);
						}
						++j;
					}
				}
				case POPUP: {
					if (this.aBoolean542 && this.anIntArray179 != null) {
						this.anInt28 = Math.max(y - this.anInt29 / 2 - 4, 0);
						a a2 = (a) this.items.get(0);
						emulator.lcdui.a.method178(g, x, this.anInt28 - 2, a2.bounds[2], this.anInt29 + 2);
						int k = 0;
						while (k < this.items.size()) {
							a a3 = (a) this.items.get(k);
							if (a3.aBoolean424) {
								a3.method211(g, k == this.currentPos, y);
							}
							++k;
						}
						return;
					}
					try {
						((a) this.items.get(this.currentSelect)).method211(g, this.inFocus, y);
					} catch (Exception ignored) {}
				}
			}
		}
	}

	protected synchronized void layout(Row row) {
		super.layout(row);
		int n = 0;
		if (this.label != null) {
			int n2 = this.getPreferredWidth() - 8;
			this.labelArr = c.textArr(this.label, Item.font, n2, n2);
			n = (Item.font.getHeight() + 4) * this.labelArr.length;
		} else {
			this.labelArr = null;
		}
		switch (this.choiceType) {
			case EXCLUSIVE:
			case MULTIPLE:
			case IMPLICIT: {
				this.anIntArray179 = new int[this.items.size()];
				int i = 0;
				while (i < this.items.size()) {
					a a2 = (a) this.items.get(i);
					a2.method212();
					a2.bounds[Y] = n;
					n += a2.bounds[H];
					this.anIntArray179[i] = i;
					if (this.choiceType == IMPLICIT && i == this.currentPos) {
						this.setSelectedIndex(i, true);
					}
					++i;
				}
				break;
			}
			case POPUP: {
				a a2 = (a) this.items.get(this.getSelectedIndex());
				a2.method212();
				n += a2.bounds[H];
				if (this.aBoolean542) {
					this.anIntArray179 = new int[this.items.size()];
					this.anInt29 = 0;
					int j = 0;
					while (j < this.items.size()) {
						this.anInt29 += ((a) this.items.get(j)).bounds[H];
						++j;
					}
					int n3 = 0;
					int k = 0;
					while (k < this.items.size()) {
						a a3 = (a) this.items.get(k);
						a3.method212();
						a3.bounds[Y] = n3;
						n3 += a3.bounds[H];
						a3.bounds[X] = this.screen.bounds[W] / 4;
						a3.bounds[W] = this.screen.bounds[W] / 2;
						this.anIntArray179[k] = k++;
					}
					break;
				}
				this.anIntArray179 = null;
				this.anInt29 = -1;
				this.anInt28 = n - a2.bounds[H];
			}
		}
		this.bounds[H] = Math.min(n, this.screen.bounds[H]);
	}

	protected boolean scrollUp() {
		if (this.choiceType == 4 && this.aBoolean542) {
			if (super.scrollUp()) {
				this.currentPos = this.anIntArray179.length - 1;
			}
			return false;
		}

		return super.scrollUp();
	}

	protected boolean scrollDown() {
		if (this.choiceType == 4 && this.aBoolean542) {
			if (super.scrollDown()) {
				this.currentPos = 0;
			}
			return false;
		}
		return super.scrollDown();
	}

	protected void pointerPressed(int x, int y) {
		int[] array = new int[4];
		int i = 0;
		while (i < this.items.size()) {
			a a2 = (a) this.items.get(i);
			System.arraycopy(a2.bounds, 0, array, 0, 4);
			boolean n3 = true;
			array[1] = array[1] + (this.aBoolean542 ? this.anInt28 : this.bounds[1]);
			if (a2.aBoolean424 && BoundsUtils.collides(array, x, y)) {
				this.currentPos = i;
			}
			++i;
		}
	}

	public int getPreferredWidth() {
		return -1;
	}

	public int getMinimumHeight() {
		final Font font = Item.font;
		return (font.getHeight() + 4) * (hasLabel() ? 2 : 1);
	}
}
