package javax.microedition.lcdui;

import emulator.Emulator;
import emulator.lcdui.a;
import org.eclipse.swt.graphics.Point;

import java.util.Vector;

public abstract class Item {
	public static final int X = 0;
	public static final int Y = 1;
	public static final int W = 2;
	public static final int H = 3;
	public static final int LAYOUT_DEFAULT = 0;
	public static final int LAYOUT_LEFT = 1;
	public static final int LAYOUT_RIGHT = 2;
	public static final int LAYOUT_CENTER = 3;
	public static final int LAYOUT_TOP = 16;
	public static final int LAYOUT_BOTTOM = 32;
	public static final int LAYOUT_VCENTER = 48;
	public static final int LAYOUT_NEWLINE_BEFORE = 256;
	public static final int LAYOUT_NEWLINE_AFTER = 512;
	public static final int LAYOUT_SHRINK = 1024;
	public static final int LAYOUT_EXPAND = 2048;
	public static final int LAYOUT_VSHRINK = 4096;
	public static final int LAYOUT_VEXPAND = 8192;
	public static final int LAYOUT_2 = 16384;
	public static final int PLAIN = 0;
	public static final int HYPERLINK = 1;
	public static final int BUTTON = 2;
	static final int anInt24 = 32563;

	/**
	 * Combination of all possible layout directives.
	 */
	private static final int LAYOUT_BITMASK =
			LAYOUT_DEFAULT
					| LAYOUT_LEFT
					| LAYOUT_RIGHT
					| LAYOUT_CENTER
					| LAYOUT_TOP
					| LAYOUT_BOTTOM
					| LAYOUT_VCENTER
					| LAYOUT_NEWLINE_BEFORE
					| LAYOUT_NEWLINE_AFTER
					| LAYOUT_SHRINK
					| LAYOUT_EXPAND
					| LAYOUT_VSHRINK
					| LAYOUT_VEXPAND
					| LAYOUT_2;

	static final int LAYOUT_HORIZONTAL_MASK = LAYOUT_CENTER; // 15;

	static final int LAYOUT_VERTICAL_MASK = LAYOUT_VCENTER; // 48;

	/**
	 * If Item is changed, reasons for Re-layouting.
	 */
	static final int UPDATE_NONE = 0;

	static final int UPDATE_ADDCOMMAND = 1;
	static final int UPDATE_REMOVECOMMAND = 1 << 1;
	static final int UPDATE_DEFAULTCOMMAND = 1 << 2;
	static final int UPDATE_ITEMCOMMANDLISTENER = 1 << 3;
	static final int UPDATE_LABEL = 1 << 4;
	static final int UPDATE_LAYOUT = 1 << 5;
	static final int UPDATE_PREFERREDSIZE = 1 << 6;

	static final int UPDATE_HEIGHT_CHANGED = 1 << 7;
	static final int UPDATE_WIDTH_CHANGED = 1 << 8;
	static final int UPDATE_SIZE_CHANGED =
			UPDATE_HEIGHT_CHANGED | UPDATE_WIDTH_CHANGED;
	static final int UPDATE_SIZE_MASK = ~UPDATE_SIZE_CHANGED;

	static final int UPDATE_ITEM_MAX = 1 << 15;

	static final Font font = Font.getFont(0, 1, 8);
	int[] bounds;
	boolean inFocus;
	boolean shownOnForm;
	Command defaultCommand;
	public ItemCommandListener itemCommandListener;
	public Vector itemCommands;
	String label;
	String[] labelArr;
	Screen screen;
	int layout;
	int preferredW = -1;
	int preferredH = -1;
	int[] anIntArray179;
	int currentPos;

	private boolean focused;
	private boolean visible;
	private int lockedPrefWidth = -1;
	private int lockedPrefHeight = -1;
	private Point calculatedPrefSize;
	private Point calculatedMinSize;
	private ItemControlStateChangeListener controlListener;

	Item(String label) {
		this.screen = null;
		this.itemCommands = new Vector();
		bounds = new int[4];
		setLabel(label);
	}

	void clearParent()
	{
		if(screen != null)
		{
			screen = null;
		}
	}

	public void setLabel(String newLabel) {
		if(isContainedInAlert())
		{
			throw new IllegalStateException();
		}
		if((newLabel == null) && (label == null))
		{
			return;
		}
		label = newLabel;
		updateParent(UPDATE_LABEL | UPDATE_SIZE_CHANGED);
	}

	public String getLabel() {
		return this.label;
	}

	public int getLayout() {
		return this.layout;
	}

	public void setLayout(int newLayout) {
		if(isContainedInAlert())
		{
			throw new IllegalStateException();
		}

		if(!isValidLayout(newLayout))
		{
			throw new IllegalArgumentException();
		}
		layout = newLayout;
		Logger.method(this, "setLayout", String.valueOf(layout));
		updateParent(UPDATE_LAYOUT | UPDATE_SIZE_CHANGED);
	}

	protected boolean isLayoutDefault() {
		return layout == 0;
	}

	protected boolean isLayoutAlignDefault() {
		return !isLayoutLeft() && !isLayoutCenter() && !isLayoutRight();
	}

	protected boolean isLayoutLeft() {
		return (layout & LAYOUT_LEFT) == LAYOUT_LEFT;
	}

	protected boolean isLayoutCenter() {
		return (layout & LAYOUT_CENTER) == LAYOUT_CENTER;
	}

	protected boolean isLayoutRight() {
		return (layout & LAYOUT_RIGHT) == LAYOUT_RIGHT;
	}

	public void addCommand(Command command) {
		if (this.screen instanceof Alert) {
			throw new IllegalStateException();
		} else if (command == null) {
			throw new NullPointerException();
		} else if (!this.itemCommands.contains(command)) {
			int i;
			for (i = 0; i < this.itemCommands.size(); ++i) {
				Command command2 = (Command) this.itemCommands.get(i);
				if (command.getCommandType() > command2.getCommandType()
						|| command.getCommandType() == command2.getCommandType()
						&& command.getPriority() <= command2.getPriority()) {
					break;
				}
			}

			this.itemCommands.add(i, command);

			updateParent(UPDATE_ADDCOMMAND, command);
//			if (this.screen != null && Emulator.getCurrentDisplay().getCurrent() == this.screen) {
//				this.screen.updateCommands();
//			}

		}
	}

	public void removeCommand(Command command) {
		if (this.itemCommands.contains(command)) {
			if (command == this.defaultCommand) {
				this.defaultCommand = null;
			}

			this.itemCommands.remove(command);
			if(defaultCommand == command)
			{
				defaultCommand = null;
			}
			updateParent(UPDATE_REMOVECOMMAND, command);
//			if (this.screen != null && Emulator.getCurrentDisplay().getCurrent() == this.screen) {
//				this.screen.updateCommands();
//			}
		}

	}

	public void setItemCommandListener(ItemCommandListener itemCommandListener) {
		if (this.screen instanceof Alert) {
			throw new IllegalStateException();
		} else {
			this.itemCommandListener = itemCommandListener;
		}
	}

	protected void itemApplyCommand() {
		if (this.itemCommandListener != null && this.defaultCommand != null) {
			this.itemCommandListener.commandAction(this.defaultCommand, this);
		}

	}

	public void setDefaultCommand(Command aCommand174) {
		if (this.screen instanceof Alert) {
			throw new IllegalStateException();
		} else {
			if ((this.defaultCommand = aCommand174) != null) {
				this.addCommand(aCommand174);
			}
			updateParent(UPDATE_SIZE_CHANGED);

		}
	}

	public void notifyStateChanged() {
		if (screen == null) return;
		if (!(this.screen instanceof Form)) {
			//throw new IllegalStateException();
		} else {
			if (((Form) this.screen).itemStateListener == null) return;
			((Form) this.screen).itemStateListener.itemStateChanged(this);
		}
	}

	protected void focus() {
		this.inFocus = true;
		if (this.screen != null) {
			this.screen.setItemCommands(this);
		}

	}

	protected void defocus() {
		this.inFocus = false;
		if (this.screen != null) {
			this.screen.removeItemCommands(this);
		}

	}

	protected void paint(Graphics graphics) {

	}

	protected void layout() {
		bounds[X] = 0;
		bounds[Y] = 0;
		bounds[W] = this.screen.bounds[W];
		bounds[H] = Screen.fontHeight4;
	}

	protected int getcurPage() {
		return this.anIntArray179 != null && this.anIntArray179.length > 0 ? this.anIntArray179[this.currentPos] : 0;
	}

	protected boolean scrollUp() {
		if (this.anIntArray179 != null && this.anIntArray179.length > 0 && this.currentPos > 0) {
			--this.currentPos;
			return false;
		} else {
			return true;
		}
	}

	protected boolean scrollDown() {
		if (this.anIntArray179 != null && this.anIntArray179.length > 0
				&& this.currentPos < this.anIntArray179.length - 1) {
			++this.currentPos;
			return false;
		} else {
			return true;
		}
	}

	protected void pointerPressed(int n, int n2) {
	}

	void updateHidden() {

	}

    boolean hasLayout(int aLayout) {
		return (getLayout() & aLayout) != 0;
    }

	boolean isFocusable() {
		return false;
	}

	boolean hasLabel() {
		return label != null && label.length() > 0;
	}

	int internalGetLayout() {
		return getLayout();
	}

	int getNumCommands() {
		return itemCommands.size();
	}

	Vector getCommands() {
		return itemCommands;
	}

	/**
	 * Check that new layout is bitwise OR combination of layout
	 * directives:
	 *
	 * @param layout Layout combination to be check.
	 * @return true If provided layout is valid.
	 */
	static boolean isValidLayout(int layout)
	{
		if((layout & (0xffffffff ^ LAYOUT_BITMASK)) != 0)
		{
			return false;
		}
		return true;
	}

	/**
	 * Get Item horizontal layout directive: LAYOUT_LEFT, LAYOUT_RIGHT,
	 * LAYOUT_CENTER, DEFAULT_LAYOUT .
	 *
	 * @param layout item Layout.
	 * @return horizontal layout of an Item.
	 */
	static int getHorizontalLayout(int layout)
	{
		return layout & Item.LAYOUT_HORIZONTAL_MASK;
	}

	/**
	 * Get Item vertical Layout directive: LAYOUT_TOP, LAYOUT_BOTTOM,
	 * LAYOUT_VCENTER.
	 *
	 * @param layout item Layout.
	 * @return vertical layout of an Item.
	 */
	static int getVerticalLayout(int layout)
	{
		return layout & Item.LAYOUT_VERTICAL_MASK;
	}
	
	/**
	 * Sets current item as focused
	 */
	void internalSetFocused(boolean isFocused)
	{
		if(isFocusable())
		{
			focused = isFocused;
		}
	}

	/**
	 * Check that item has current focus on it.
	 *
	 * @return true if item has focus.
	 */
	boolean isFocused()
	{
		return focused;
	}
	
	/**
	 * Sets current item visibility
	 */
	void internalSetVisible(boolean isVisible)
	{
		this.visible = isVisible;
	}

	/**
	 * Check that item is visible.
	 *
	 * @return true if item is visible
	 */
	boolean isVisible()
	{
		return visible;
	}
	/**
	 * Gets LAYOUT_2 width of this item.
	 */
	int getLayoutWidth()
	{
		if(hasLayout(LAYOUT_SHRINK))
		{
			return getMinimumWidth();
		}
		return getPreferredWidth();
	}

	/**
	 * Gets LAYOUT_2 height of this item.
	 */
	int getLayoutHeight()
	{
		if(hasLayout(LAYOUT_VSHRINK))
		{
			return getMinimumHeight();
		}
		return getPreferredHeight();
	}
	/**
	 * Is this item's size locked.
	 */
	boolean isSizeLocked()
	{
		return (lockedPrefWidth >= 0) || (lockedPrefHeight >= 0);
	}

	/**
	 * Gets locked preferred width of this item.
	 *
	 * @return Locked preferred width. If width is not locked, returns -1.
	 */
	int getLockedPreferredWidth()
	{
		return lockedPrefWidth;
	}

	/**
	 * Gets locked preferred height of this item.
	 *
	 * @return Locked preferred height. If height is not locked, returns -1.
	 */
	int getLockedPreferredHeight()
	{
		return lockedPrefHeight;
	}

	public void callCommandAction(Command command) {
		if (itemCommandListener != null && command != null) {
			// TODO queue
			itemCommandListener.commandAction(command, this);
		}
	}

	public Command getMSKCommand() {
		Command ret = null;
		if(defaultCommand != null)
		{
			ret = defaultCommand;
		}
		else if(getNumCommands() == 1)
		{
			ret = (Command) itemCommands.elementAt(0);
		}
		return ret;
	}
	/**
	 * Gets the minimum width of this item.
	 *
	 * @return Minimum width.
	 */
	public int getMinimumWidth()
	{
		return getCalculatedMinimumSize().x;
	}

	/**
	 * Gets the minimum height of this item.
	 *
	 * @return Minimum height.
	 */
	public int getMinimumHeight()
	{
		return getCalculatedMinimumSize().y;
	}

	private Point getCalculatedMinimumSize()
	{
		if(calculatedMinSize == null)
		{
			calculatedMinSize = calculateMinimumSize();
			System.out.println(this + " calculated min size " + calculatedMinSize);
			// Logger.method(this, "calculateMinimumSize", calculatedMinSize);
		}
		return calculatedMinSize;
	}

	/**
	 * Calculates minimum size of this item.
	 *
	 * @return Minimum size.
	 */
	abstract Point calculateMinimumSize();

	/**
	 * Gets the preferred width of this item.
	 *
	 * @return Preferred width.
	 */
	public int getPreferredWidth()
	{
		if(lockedPrefWidth >= 0)
		{
			if(calculatedMinSize == null)
			{
				checkLockedSizes();
			}
			return lockedPrefWidth;
		}
		else
		{
			return getCalculatedPreferredSize().x;
		}
	}

	/**
	 * Gets the preferred height of this item.
	 *
	 * @return Preferred height.
	 */
	public int getPreferredHeight()
	{
		if(lockedPrefHeight >= 0)
		{
			if(calculatedMinSize == null)
			{
				checkLockedSizes();
			}
			return lockedPrefHeight;
		}
		else
		{
			return getCalculatedPreferredSize().y;
		}
	}

	private Point getCalculatedPreferredSize()
	{
		if(calculatedPrefSize == null)
		{
			calculatedPrefSize = calculatePreferredSize();
			System.out.println(this + " calculated pref size " + calculatedPrefSize);
			// Logger.method(this, "calculatePreferredSize", calculatedPrefSize);
		}
		return calculatedPrefSize;
	}

	void checkLockedSizes()
	{
		if(lockedPrefWidth >= 0)
		{
			lockedPrefWidth = Math.min(
					Math.max(lockedPrefWidth, getMinimumWidth()),
					ItemLayouter.getMaximumItemWidth(null));
		}
		if(lockedPrefHeight >= 0)
		{
			lockedPrefHeight = Math.max(lockedPrefHeight, getMinimumHeight());
		}
	}

	/**
	 * Invalidates this Item's calculated preferred size. Forces the
	 * implementation to calculate it again.
	 */
	void invalidateCachedSizes()
	{
		calculatedMinSize = null;
		calculatedPrefSize = null;
	}

	/**
	 * Calculates preferred size of this item.
	 *
	 * @return Preferred size.
	 */
	abstract Point calculatePreferredSize();

	/**
	 * Sets the preferred size of this item. If new value is larger than -1 and
	 * less than minimum value, the minimum value is used instead. If value is
	 * larger than minimum value, the dimension is locked to provided value. It
	 * is possible to unlock dimension by setting value to -1.
	 *
	 * @param w New preferred width.
	 * @param h New preferred height.
	 * @throws IllegalArgumentException if w or h is less than -1.
	 * @throws IllegalStateException If this Item is contained within an Alert.
	 */
	public synchronized void setPreferredSize(int w, int h)
	{
		if(isContainedInAlert())
		{
			throw new IllegalStateException();
		}
		if((w < -1) || (h < -1))
		{
			throw new IllegalArgumentException();
		}

		lockedPrefWidth = w;
		lockedPrefHeight = h;
		checkLockedSizes();
		updateParent(UPDATE_PREFERREDSIZE | UPDATE_SIZE_CHANGED);
	}

	/**
	 * If the item is owned by an Alert.
	 */
	boolean isContainedInAlert()
	{
		return ((screen != null) && (screen instanceof Alert));
	}

	/**
	 * If the item is owned by an Form.
	 */
	boolean isContainedInForm()
	{
		return ((screen != null) && (screen instanceof Form));
	}

	/**
	 * Updates the parent if it's a Form.
	 */
	void updateParent(int updateReason)
	{
		updateParent(updateReason, null);
	}

	/**
	 * Updates the parent if it's a Form.
	 *
	 * @param param additional parameter
	 */
	void updateParent(int updateReason, Object param)
	{
		if((updateReason & UPDATE_SIZE_CHANGED) != 0)
		{
			invalidateCachedSizes();
		}
		if(isContainedInForm())
		{
			((Form) screen).updateItemState(this, updateReason, param);
		}
	}

	Displayable getParent() {
		return screen;
	}

	Command getDefaultCommand()
	{
		return defaultCommand;
	}

	void setItemControlStateChangeListener(ItemControlStateChangeListener listener)
	{
		controlListener = listener;
	}

	ItemControlStateChangeListener getItemControlStateChangeListener()
	{
		return controlListener;
	}
}
