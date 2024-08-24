package javax.microedition.lcdui;

import emulator.lcdui.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class Form extends Screen {
	private FormLayouter formLayouter;
	ItemStateListener itemStateListener;
	private ScrolledComposite formComposite;
	private FormScrollBarListener fsbl = new FormScrollBarListener();

	private Timer layoutTimer = new Timer();

	private int startIndex;

	private FormTimerTask formTimerTask;

	public Form(final String s) {
		super(s);

		constructSwt();
		formLayouter = new FormLayouter(this);
	}

	public Form(String title, Item[] formItems)
	{
		this(title);
		if(formItems != null)
		{
			for(int i = 0; i < formItems.length; i++)
			{
				this.append(formItems[i]);
			}
		}
	}

	protected Composite constructSwtContent(int style) {
		Composite comp = super.constructSwtContent(SWT.VERTICAL);
		ScrollBar vBar = comp.getVerticalBar();
		if(vBar != null)
		{
			vBar.setVisible(true);
			vBar.setEnabled(true);
		}
		formComposite = new ScrolledComposite(comp, SWT.NONE);
		return comp;
	}

	/* (non-Javadoc)
	 * @see Displayable#eswtHandleShowCurrentEvent()
	 */
	public void swtShown()
	{
		super.swtShown();
		ScrollBar vBar = getContentComp().getVerticalBar();
		if(vBar != null)
		{
			vBar.addSelectionListener(fsbl);
		}
		formLayouter.handleShowCurrentEvent();
	}

	/* (non-Javadoc)
	 * @see Displayable#eswtHandleHideCurrentEvent()
	 */

	public void swtHidden() {
		super.swtHidden();

		ScrollBar vBar = getContentComp().getVerticalBar();
		if(vBar != null)
		{
			vBar.removeSelectionListener(fsbl);
		}
		formLayouter.handleHideCurrentEvent();
	}

	/* (non-Javadoc)
	 * @see Displayable#eswtHandleResizeEvent(int, int)
	 */
	public void swtResized(int width, int height)
	{
		super.swtResized(width, height);
		if(formComposite != null)
		{
			formComposite.setRedraw(false);
			formComposite.setOrigin(0, 0, true);
			formComposite.setSize(width, height);
			formComposite.setRedraw(true);
			formLayouter.handleResizeEvent(width, height);
			resetLayoutTimer(0);
		}
	}

	public int append(final Item item) {
		synchronized(items) {
			if (item == null) {
				throw new NullPointerException();
			}
			if (item.screen != null) {
				throw new IllegalStateException();
			}
			super.items.add(item);
			item.screen = this;
			return super.items.size() - 1;
		}
	}

	public int append(final String s) {
		synchronized(items) {
			final StringItem stringItem = new StringItem(null, s);
			super.items.add(stringItem);
			stringItem.screen = this;
			return super.items.size() - 1;
		}
	}

	public int append(final Image image) {
		synchronized(items) {
			final ImageItem imageItem = new ImageItem(null, image, 0, null);
			super.items.add(imageItem);
			imageItem.screen = this;
			return super.items.size() - 1;
		}
	}

	public void insert(final int n, final Item item) {
		if (item == null) {
			throw new NullPointerException();
		}
		if (item.screen != null) {
			throw new IllegalStateException();
		}
		synchronized(items) {
			super.items.insertElementAt(item, n);
			item.screen = this;
		}
	}

	public void delete(final int n) {
		if (n < 0 || n >= super.items.size()) {
			throw new IndexOutOfBoundsException();
		}
		synchronized(items) {
			((Item) super.items.get(n)).screen = null;
			super.items.remove(n);
		}
	}

	public void deleteAll() {
		synchronized(items) {
			for (Object item : super.items) {
				((Item) item).screen = null;
			}
			super.items.removeAllElements();
		}
	}

	public void set(final int n, final Item item) {
		if (n < 0 || n >= super.items.size()) {
			throw new IndexOutOfBoundsException();
		}
		if (item == null) {
			throw new NullPointerException();
		}
		if (item.screen != null) {
			throw new IllegalStateException();
		}
		synchronized(items) {
			((Item) super.items.get(n)).screen = null;
			super.items.set(n, item);
			item.screen = this;
		}
	}

	public Item get(final int n) {
		if (n < 0 || n >= super.items.size()) {
			throw new IndexOutOfBoundsException();
		}
		return ((Item) super.items.get(n));
	}

	public void setItemStateListener(final ItemStateListener anItemStateListener858) {
		this.itemStateListener = anItemStateListener858;
	}

	public int size() {
		return super.items.size();
	}

	public int getWidth() {
		return super.getWidth();
	}

	public int getHeight() {
		return super.getHeight();
	}

	protected void paint(final Graphics g) {
	}

	protected void layout() {
	}

	protected void sizeChanged(final int w, final int h) {
		this.w = w;
		this.h = h;
		this.bounds = new int[]{0, Screen.fontHeight4, this.w - 4, this.h - Screen.fontHeight4};
		layout();
	}

	public Vector getItems() {
		return items;
	}

	public ScrolledComposite getFormComposite() {
		return formComposite;
	}

	/**
	 * Returns form Form Layouter.
	 * @return Reference to layout policy.
	 *
	 */
	FormLayouter getFormLayouter()
	{
		return formLayouter;
	}
	

	/**
	 * Update item state in form.
	 *
	 * @param item
	 * @param updateReason
	 * @param param additional parameter
	 */
	void updateItemState(Item item, int updateReason, Object param)
	{
		if(item != null && item.getParent() == this)
		{
			if(layoutTimer != null)
			{
				formLayouter.updateItemState(item, updateReason, param);
			}

			if((updateReason & Item.UPDATE_SIZE_CHANGED) != 0)
			{
				synchronized(formLayouter)
				{
					resetLayoutTimer(items.indexOf(item));
				}
			}
		}
	}

	/**
	 * Reset timer for do layout with a given start index.
	 */
	private void resetLayoutTimer(int newStartIndex)
	{
		if(layoutTimer != null)
		{
			if(formTimerTask != null)
			{
				formTimerTask.cancel();
				formTimerTask = null;
			}
			// schedule new timer
			startIndex = Math.max(0, Math.min(newStartIndex, startIndex));
			// formLayouter.layoutForm(startIndex);
			formTimerTask = new FormTimerTask(startIndex);
			layoutTimer.schedule(formTimerTask, Config.FORM_LAYOUT_TIMER_DELAY);
		}
	}

	/**
	 * Cancel Layout Timer.
	 */
	private void cancelLayoutTimer()
	{
		if(layoutTimer != null)
		{
			if(formTimerTask != null)
			{
				formTimerTask.cancel();
				formTimerTask = null;
			}
			layoutTimer.cancel();
			layoutTimer = null;
		}
	}

	/**
	 * Form Timer task. Triggers the formComposite to Layout.
	 */
	class FormTimerTask extends TimerTask
	{

		private int index;

		FormTimerTask(int newIndex)
		{
			index = newIndex;
		}

		public void run()
		{
			Logger.method(Form.this, "layout");
			formLayouter.layoutForm(index);
			startIndex = items.size();
		}

	}
	class FormScrollBarListener implements SelectionListener
	{

		public void widgetDefaultSelected(SelectionEvent se)
		{
		}

		public void widgetSelected(SelectionEvent se)
		{
			ScrollBar sb = (ScrollBar) se.widget;
			formLayouter.updateScrolling(sb.getSelection(), false);
		}
	}
}
