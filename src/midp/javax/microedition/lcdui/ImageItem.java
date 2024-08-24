package javax.microedition.lcdui;

import emulator.lcdui.*;
import org.eclipse.swt.graphics.Point;

public class ImageItem extends Item {
	public static final int LAYOUT_DEFAULT = 0;
	public static final int LAYOUT_LEFT = 1;
	public static final int LAYOUT_RIGHT = 2;
	public static final int LAYOUT_CENTER = 3;
	public static final int LAYOUT_NEWLINE_BEFORE = 256;
	public static final int LAYOUT_NEWLINE_AFTER = 512;

	/**
	 * If ImageItem is changed, reasons for Re-layouting.
	 */
	static final int UPDATE_ALTTEXT = UPDATE_ITEM_MAX << 1;
	static final int UPDATE_IMAGE = UPDATE_ITEM_MAX << 2;
	private Image image;
	private String altText;
	private int appearanceMode;

	public ImageItem(final String s, final Image image, final int n, final String s2) {
		this(s, image, n, s2, 0);
	}

	public ImageItem(final String s, final Image image, final int layout, final String aString25, final int anInt179) {
		super(s);
		if (anInt179 < 0 || anInt179 > 2) {
			throw new IllegalArgumentException();
		}
		setImage(image);
		setLayout(layout);
		this.altText = aString25;
		this.appearanceMode = anInt179;
	}

	public Image getImage() {
		return this.image;
	}

	public void setImage(Image image)
	{
		if((image == null) && (this.image == null))
		{
			return;
		}
		this.image = image;
		updateParent(UPDATE_IMAGE | UPDATE_SIZE_CHANGED);
	}

	public String getAltText() {
		return this.altText;
	}

	boolean isFocusable()
	{
		return (getNumCommands() > 0);
	}

	/**
	 * Adds command to this ImageItem. If same command is already added to this item,
	 * nothing happens.
	 *
	 * @param command A command to be added.
	 * @throws NullPointerException if cmd is null.
	 */
	public void addCommand(Command command)
	{
		int numCmds = getNumCommands();

		super.addCommand(command);

		if((getNumCommands() != numCmds) && (getNumCommands() == 1))
		{
			updateParent(UPDATE_SIZE_CHANGED);
		}
	}

	/**
	 * Removes command from the ImageItem. If command doesn't exists in this item,
	 * nothing happens.
	 *
	 * @param command The command to be removed.
	 */
	public void removeCommand(Command command)
	{
		int numCmds = getNumCommands();

		super.removeCommand(command);

		if((getNumCommands() != numCmds) && (getNumCommands() == 0))

		{
			updateParent(UPDATE_SIZE_CHANGED);
		}
	}

	public void setAltText(final String aString25) {
		this.altText = aString25;
	}

	public int getLayout() {
		return super.getLayout();
	}

	public void setLayout(final int layout) {
		super.setLayout(layout);
	}

	public int getAppearanceMode() {
		return this.appearanceMode;
	}

	protected void paint(final Graphics graphics) {
	}

	protected void layout() {
	}

	/**
	 * Calculates minimum size of this item.
	 *
	 * @return Minimum size.
	 */
	Point calculateMinimumSize()
	{
		return ImageItemLayouter.calculateMinimumBounds(this);
	}

	/**
	 * Calculates preferred size of this item.
	 *
	 * @return Preferred size.
	 */
	Point calculatePreferredSize()
	{
		return ImageItemLayouter.calculatePreferredBounds(this);
	}
}
