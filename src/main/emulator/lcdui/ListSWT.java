package emulator.lcdui;

import emulator.Emulator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import javax.microedition.lcdui.*;


public class ListSWT extends SWTScreen implements IListImpl {

	private final List list;
	private int type;
	private Table swtTable;
	private ChoiceImpl choiceImpl;

	private SwtTableSelectionListener swtTableListener =
			new SwtTableSelectionListener();

	public ListSWT(List list, String title, int type, String[] text, Image[] img) {
		if (type != 2 && type != 1 && type != 3) {
			throw new IllegalArgumentException();
		}
		this.list = list;
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

	protected Composite _constructSwtContent(int style) {
		Composite c = super._constructSwtContent(style);
		swtTable = new Table(c, getStyle(type));
		_setSwtStyle(swtTable);

		swtTable.addListener(SWT.MeasureItem, event -> {
			TableItem item = (TableItem)event.item;
			String text = item.getText(event.index);
			event.gc.setFont(item.getFont());
			int i = SWT.DRAW_TRANSPARENT;
			if (choiceImpl.getFitPolicy() == Choice.TEXT_WRAP_ON) {
				i |= SWT.DRAW_DELIMITER | SWT.DRAW_TAB;
			}
			Point size = event.gc.textExtent(text, i);
			org.eclipse.swt.graphics.Image img = (org.eclipse.swt.graphics.Image) item.getData(); //item.getImage();
//			if (choiceImpl.getFitPolicy() == Choice.TEXT_WRAP_ON && text.indexOf('\n') != -1) {
//				event.height = event.gc.textExtent("A\nA").y;
//			} else {
			event.height = size.y;
//			}
			int imgWidth = 0;
			if (img != null) {
				try {
					ImageData data = img.getImageData();
					imgWidth = data.width;
					int imgHeight = data.height;
					if (imgWidth != 0 && imgHeight != 0) {
						size.x += (int) ((float) (event.height * imgWidth) / imgHeight);
					}
				} catch (Exception ignored) {
					// FIXME
				}
			}
			event.width = size.x + 2;
		});
		swtTable.addListener(SWT.EraseItem, event -> event.detail &= ~SWT.FOREGROUND);
		swtTable.addListener(SWT.PaintItem, event -> {
			TableItem item = (TableItem)event.item;
			org.eclipse.swt.graphics.Image img = (org.eclipse.swt.graphics.Image) item.getData(); //item.getImage();
			String text = item.getText(event.index);
			event.gc.setFont(item.getFont());
			int imgWidth = 0;
			if (img != null) {
				try {
					ImageData data = img.getImageData();
					imgWidth = data.width;
					int imgHeight = data.height;
					if (imgWidth != 0 && imgHeight != 0) {
						int dstWidth = (int) ((float) (event.height * imgWidth) / imgHeight);
						event.gc.drawImage(img, 0, 0, imgWidth, imgHeight,
								event.x, event.y,
								dstWidth, event.height);
						imgWidth = dstWidth;
					} else {
						imgWidth = 0;
					}
				} catch (Exception ignored) {
					// FIXME
				}
			}
			int yOffset = 0;
			if (event.index == 1) {
				Point size = event.gc.textExtent(text);
				yOffset = Math.max(0, (event.height - size.y) / 2);
			}
			int i = SWT.DRAW_TRANSPARENT;
			if (choiceImpl.getFitPolicy() == Choice.TEXT_WRAP_ON) {
				i |= SWT.DRAW_DELIMITER | SWT.DRAW_TAB;
			}
			event.gc.drawText(text, event.x + imgWidth, event.y + yOffset, i);
		});

		return c;
	}

	private int getStyle(int listType)
	{
		int tableStyle = SWT.V_SCROLL;
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
		if(type == List.IMPLICIT || type == List.EXCLUSIVE)
		{
			int sel = choiceImpl.getSelectedIndex();
			if (sel == -1 && choiceImpl.size() > 0) {
				sel = 0;
			}
			if((sel == 0) || (swtTable.getSelectionIndex() != sel))
			{
				swtTable.setSelection(sel);
			}
			if (list.isShown()) {
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
					swtTable.getItem(i).setChecked(true);
				}
				else
				{
					swtTable.deselect(i);
					swtTable.getItem(i).setChecked(false);
				}
			}
		}
	}

	private void swtInsertItem(int index)
	{
		TableItem item = new TableItem(swtTable, SWT.NONE, index);
		Image img = choiceImpl.getImage(index);
//		item.setImage(0, Image.getSWTImage(img));
		item.setData(SWTScreen.getSWTImage(img));
		item.setText(0, choiceImpl.getString(index));
	}

	private void swtSetItem(int index)
	{
		TableItem item = swtTable.getItem(index);
		Image img = choiceImpl.getImage(index);
//		item.setImage(0, Image.getSWTImage(img));
		item.setData(SWTScreen.getSWTImage(img));
		item.setText(0, choiceImpl.getString(index));
		swtTable.redraw();
	}

	private void swtDeleteItem(int index)
	{
		swtTable.getItem(index).dispose();
		choiceImpl.delete(index);
	}

	private void swtSetItemFont(int index)
	{
		Font font = choiceImpl.getFont(index);
		swtTable.getItem(index).setFont(0, font == null ? null : SWTScreen.getSWTFont(font, false));
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

	public boolean isSWT() {
		return true;
	}

	public void drawScrollBar(Graphics graphics) {

	}

	public void paint(Graphics graphics) {

	}

	public void layout() {

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
					if (type == Choice.MULTIPLE) {
						choiceImpl.setSelected(index, ((TableItem) se.item).getChecked());
					} else {
						choiceImpl.setSelected(index, !isSelected(index));
					}
				}
			}
		}

		public void widgetDefaultSelected(SelectionEvent se)
		{
			if(type == Choice.IMPLICIT)
			{
				if(size() > 0)
				{
					Emulator.getEventQueue().commandAction(list._getSelectCommand(), list);
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

	public void swtShown() {
		super.swtShown();
		swtTable.addSelectionListener(swtTableListener);
		swtTable.addKeyListener(swtKeyListener);
		updateSelection();
	}

	public void swtHidden() {
		super.swtHidden();
		if (swtTable == null || swtTable.isDisposed()) return;
		swtTable.removeSelectionListener(swtTableListener);
		swtTable.removeKeyListener(swtKeyListener);
	}

	public void swtResized(int w, int h) {
		super.swtResized(w, h);
//		swtTable.setFont(Font.getDefaultSWTFont(false));
		swtTable.pack();
		swtTable.setBounds(swtContent.getClientArea());
	}

}
