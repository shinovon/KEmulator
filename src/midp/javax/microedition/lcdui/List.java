package javax.microedition.lcdui;

import emulator.Emulator;
import emulator.Settings;
import emulator.UILocale;
import emulator.graphics2D.swt.FontSWT;
import emulator.ui.swt.EmulatorImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class List extends Screen implements Choice {
	public static final Command SELECT_COMMAND = new Command(UILocale.get("LCDUI_LIST_SELECT_COMMAND", "Select"), 1, 0);
	private int type;
	private Table swtTable;
	private ChoiceImpl choiceImpl;
	private Command selectCommand = List.SELECT_COMMAND;

	private SwtTableSelectionListener swtTableListener =
			new SwtTableSelectionListener();

	public List(String s, int n) {
		this(s, n, new String[0], null);
	}

	public List(String title, int type, String[] text, Image[] img) {
		super(title);
		if (type != 2 && type != 1 && type != 3) {
			throw new IllegalArgumentException();
		}
		this.type = type;
		constructSwt();
		switch(type)
		{
			case Choice.IMPLICIT:
			case Choice.EXCLUSIVE:
				choiceImpl = new ChoiceImpl(false);
				break;
			case Choice.MULTIPLE:
				choiceImpl = new ChoiceImpl(true);
				break;
		}
		choiceImpl.check(text, img);
		for(int i = 0; i < text.length; i++)
		{
			append(text[i], img != null
					? img[i] : null);
		}
	}



	protected Composite _constructSwtContent(int style) {
		Composite c = super._constructSwtContent(style);
		swtTable = new Table(c, getStyle(type));
		return c;
	}

	public void setSelectCommand(Command cmd) {
		if (type == Choice.IMPLICIT) {
			if(cmd == null)
			{
				if(selectCommand != null)
				{
					super.removeCommand(selectCommand);
				}
			}
			else if(cmd != SELECT_COMMAND)
			{
				addCommand(cmd);
			}
			selectCommand = cmd;
		}
	}

	public void removeCommand(Command cmd)
	{
		if(cmd == selectCommand)
		{
			selectCommand = null;
		}
		super.removeCommand(cmd);
	}

	/**
	 * Append item with specified text and image.
	 *
	 * @param text the text
	 * @param img the image
	 * @return index of added item
	 */
	public int append(String text, Image img)
	{
		final int index = choiceImpl.append(text, img);
		syncExec(new Runnable()
		{
			public void run()
			{
				swtInsertItem(index);
				swtUpdateSelection();
			}
		});
		return index;
	}

	/**
	 * Insert item with specified text and image.
	 *
	 * @param position the item index
	 * @param text the text
	 * @param img the image
	 */
	public void insert(int position, String text, Image img)
	{
		choiceImpl.insert(position, text, img);
		final int index = position; // index of added element
		syncExec(new Runnable()
		{
			public void run()
			{
				swtInsertItem(index);
				swtUpdateSelection();
			}
		});
	}

	/**
	 * Set item with specified text and image.
	 *
	 * @param position the item index
	 * @param text the text
	 * @param img the image
	 */
	public void set(int position, String text, Image img)
	{
		choiceImpl.set(position, text, img);
		final int index = position; // index of changed element
		syncExec(new Runnable()
		{
			public void run()
			{
				swtSetItem(index);
				swtUpdateSelection();
			}
		});
	}

	/**
	 * Remove item with at specified position.
	 *
	 * @param position the item index
	 */
	public void delete(int position)
	{
		if (position < 0 || ( position >= size()))
		{
			throw new IndexOutOfBoundsException();
		}
		final int index = position; // index of changed element
		syncExec(new Runnable()
		{
			public void run()
			{
				swtDeleteItem(index);
				swtUpdateSelection();
			}
		});
	}

	/**
	 * Remove all items.
	 */
	public void deleteAll()
	{
		if(type != Choice.IMPLICIT)
		{
			choiceImpl.deleteAll();
		}
		syncExec(new Runnable()
		{
			public void run()
			{
				swtDeleteAllItems();
			}
		});
	}

	/**
	 * Get the fit policy of this list.
	 *
	 * @return the lists fir policy
	 */
	public int getFitPolicy()
	{
		return choiceImpl.getFitPolicy();
	}

	/**
	 * Get the font used in a list item.
	 *
	 * @param position the index of the item
	 * @return the items font
	 */
	public Font getFont(int position)
	{
		return choiceImpl.getFont(position);
	}

	/**
	 * Get the image part of a list item.
	 *
	 * @param position the index of the item
	 * @return the items image part
	 */
	public Image getImage(int position)
	{
		return choiceImpl.getImage(position);
	}

	/**
	 * Get the string part of a list item.
	 *
	 * @param position the index of the item
	 * @return the items string part
	 */
	public String getString(int position)
	{
		return choiceImpl.getString(position);
	}

	/**
	 * Get selected flags.
	 *
	 * @param selectedArray an array with selected items
	 * @return selected flags
	 */
	public int getSelectedFlags(boolean[] selectedArray)
	{
		return choiceImpl.getSelectedFlags(selectedArray);
	}

	/**
	 * Returns the selected index.
	 *
	 * @return the selected index
	 */
	public int getSelectedIndex()
	{
		return choiceImpl.getSelectedIndex();
	}

	/**
	 * Returns if the specified element is selected.
	 *
	 * @param position specified element index
	 * @return true if its selected, false otherwise
	 */
	public boolean isSelected(int position)
	{
		return choiceImpl.isSelected(position);
	}

	public void setFitPolicy(int newFitPolicy)
	{
		choiceImpl.setFitPolicy(newFitPolicy);
//		syncExec(new Runnable()
//		{
//			public void run()
//			{
//				swtTable.setWordWrap(choiceImpl.getFitPolicy()
//						== Choice.TEXT_WRAP_ON);
//			}
//		});
	}

	public void setFont(int n, Font font) {
		choiceImpl.setFont(n, font);
		syncExec(new Runnable()
		{
			public void run()
			{
				swtSetItemFont(n);
			}
		});
	}

	/**
	 * Set selected flags.
	 *
	 * @param selectedArray an array with selected items
	 */
	public void setSelectedFlags(boolean[] selectedArray)
	{
		choiceImpl.setSelectedFlags(selectedArray);
		updateSelection();
	}

	/**
	 * Set selected index.
	 *
	 * @param position the index of the item
	 * @param select selected or not
	 */
	public void setSelectedIndex(int position, boolean select)
	{
		choiceImpl.setSelected(position, select);
		updateSelection();
	}

	/**
	 * Returns the size of the list.
	 *
	 * @return the lists size
	 */
	public int size()
	{
		return choiceImpl.size();
	}

	protected void _drawScrollBar(final Graphics graphics) {
	}

	protected void _paint(Graphics graphics) {
	}

	protected void layout() {
	}

	private int getStyle(int listType)
	{
		int tableStyle = SWT.NONE;
		switch(listType)
		{
			case Choice.IMPLICIT:
				tableStyle |= SWT.SINGLE;
				break;
			case Choice.EXCLUSIVE:
				tableStyle |= SWT.SINGLE | SWT.RADIO;
				break;
			case Choice.MULTIPLE:
				tableStyle |= SWT.MULTI | SWT.CHECK;
				break;
			default:
				break;
		}
		return tableStyle;
	}

	private void updateSelection()
	{
		syncExec(new Runnable()
		{
			public void run()
			{
				swtUpdateSelection();
			}
		});
	}
	/**
	 * Update eSWT Table selection.
	 */
	private void swtUpdateSelection()
	{
		if(type == IMPLICIT || type == EXCLUSIVE)
		{
			int sel = choiceImpl.getSelectedIndex();
			if (sel == -1 && choiceImpl.size() > 0) {
				sel = 0;
			}
			if((sel == 0) || (swtTable.getSelectionIndex() != sel))
			{
				swtTable.setSelection(sel);
			}
			if (isShown()) {
				swtTable.setFocus();
			}
		}
		else
		{
			int size = choiceImpl.size();
			for(int i = 0; i < size; i++)
			{
				if(choiceImpl.isSelected(i))
				{
					swtTable.select(i);
				}
				else
				{
					swtTable.deselect(i);
				}
			}
		}
	}

	private void swtInsertItem(int index)
	{
		TableItem item = new TableItem(swtTable, SWT.NONE, index);
		Image img = choiceImpl.getImage(index);
		item.setImage(0, Image.getSWTImage(img));
		item.setText(0, choiceImpl.getString(index));
	}

	private void swtSetItem(int index)
	{
		TableItem item = swtTable.getItem(index);
		Image img = choiceImpl.getImage(index);
		item.setImage(0, Image.getSWTImage(img));
		item.setText(0, choiceImpl.getString(index));
	}

	private void swtDeleteItem(int index)
	{
		swtTable.getItem(index).dispose();
		choiceImpl.delete(index);
	}

	private void swtSetItemFont(int index)
	{
		Font font = choiceImpl.getFont(index);
		org.eclipse.swt.graphics.Font swtFont = null;
		if (font != null) {
			if (Settings.g2d == 0) {
				swtFont = ((FontSWT) font.getImpl()).getSWTFont();
			} else {
				swtFont = new org.eclipse.swt.graphics.Font(EmulatorImpl.getDisplay(),
						Emulator.getEmulator().getProperty().getDefaultFontName(),
						Math.max(2, (int) (font.getHeight() * 0.7f) - 1),
						font.getStyle() & ~Font.STYLE_UNDERLINED);
			}
		}
		swtTable.getItem(index).setFont(0, swtFont);
	}

	private void swtDeleteAllItems()
	{
		for(int i = swtTable.getItemCount() - 1; i >= 0; i--)
		{
			if(type == Choice.IMPLICIT)
			{
				choiceImpl.delete(i);
			}
			swtTable.getItem(i).dispose();
		}
	}

	class SwtTableSelectionListener implements SelectionListener
	{

		private void update(SelectionEvent se)
		{
			if(se.widget != null && se.item != null)
			{
				int index = ((Table) se.widget).indexOf((TableItem) se.item);
				if(index >= 0)
				{
					choiceImpl.setSelected(index, !isSelected(index));
				}
			}
		}

		public void widgetDefaultSelected(SelectionEvent se)
		{
			if(type == Choice.IMPLICIT)
			{
				if(size() > 0)
				{
					Emulator.getEventQueue().commandAction(selectCommand, List.this);
				}
			}
		}

		public void widgetSelected(SelectionEvent se)
		{
			if(type == Choice.IMPLICIT || type == Choice.EXCLUSIVE)
			{
				update(se);
			}
			else if(type == Choice.MULTIPLE)
			{
				if(se.detail == SWT.CHECK)
				{
					update(se);
				}
			}
		}

	}

	public void _swtShown() {
		super._swtShown();
		swtTable.addSelectionListener(swtTableListener);
		swtTable.addKeyListener(swtKeyListener);
		updateSelection();
	}

	public void _swtHidden() {
		super._swtHidden();
		if (swtTable == null || swtTable.isDisposed()) return;
		swtTable.removeSelectionListener(swtTableListener);
		swtTable.removeKeyListener(swtKeyListener);
	}

	public void _swtResized(int w, int h) {
		super._swtResized(w, h);
		swtTable.setBounds(swtContent.getClientArea());
	}
}