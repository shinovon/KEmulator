/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies).
* All rights reserved.
* This component and the accompanying materials are made available
* under the terms of "Eclipse Public License v1.0"
* which accompanies this distribution, and is available
* at the URL "http://www.eclipse.org/legal/epl-v10.html".
*
* Initial Contributors:
* Nokia Corporation - initial contribution.
*
* Contributors:
*
* Description:
*
*/
package javax.microedition.lcdui;

import java.util.Vector;

/**
 * ChoiceImpl class implements the basic functionality of the Choice interface.
 * <br> List and ChoiceGroup class use this class as their member to implement
 * their own functionality.
 */
final class ChoiceImpl
{

	/**
	 * Internal class for storing Choice data.
	 */
	private class ChoiceData
	{
		String text;
		Image img;
		Font font;
		boolean sel;

		ChoiceData(String text, Image img, Font font, boolean sel)
		{
			this.text = text;
			this.img = img;
			this.font = font;
			this.sel = sel;
		}
	}

	/**
	 * Array of ChoiceData items.
	 */
	private Vector items = new Vector();

	/**
	 * Fit policy flag.
	 */
	private int fitPolicy;

	/**
	 * Stores if the choice allows single or multiple selections.
	 */
	private boolean multiSel;

	/**
	 * Constructor.
	 *
	 * @param multiSel multi or single selection
	 */
	ChoiceImpl(boolean multiSel)
	{
		this.multiSel = multiSel;
	}

	/**
	 * Check if the arrays are valid.
	 *
	 * @param textElements string array
	 * @param imgElements  image array
	 */
	void check(String[] textElements, Image[] imgElements)
	{
		// check textElements
		if(textElements == null)
		{
			throw new NullPointerException();
		}
		// check every text element
		for(int i = 0; i < textElements.length; i++)
		{
			if(textElements[i] == null)
			{
				throw new NullPointerException();
			}
		}
		// check imgElements array length
		if(imgElements != null && imgElements.length != textElements.length)
		{
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Append item with specified text and image.
	 */
	synchronized int append(String text, Image image)
	{
		validateString(text);
		ChoiceData data = new ChoiceData(text, image, Font.getDefaultFont(),
										 (!multiSel && size() == 0));
		items.addElement(data);
		// return index of added element
		return size() - 1;
	}

	/**
	 * Insert item with specified text and image.
	 */
	synchronized void insert(int elementNum, String text, Image image)
	{
		validateString(text);
		validatePosition(elementNum, size() + 1);
		ChoiceData data = new ChoiceData(text, image, Font.getDefaultFont(),
										 (!multiSel && size() == 0));
		items.insertElementAt(data, elementNum);
	}

	/**
	 * Set item with specified text and image.
	 */
	synchronized void set(int elementNum, String text, Image image)
	{
		validateString(text);
		validatePosition(elementNum, size());
		ChoiceData data = (ChoiceData) items.elementAt(elementNum);
		data.text = text;
		data.img = image;
	}

	/**
	 * Set item's font.
	 */
	synchronized void setFont(int elementNum, Font font)
	{
		validatePosition(elementNum, size());
		Font checkedFont = (font == null ? Font.getDefaultFont() : font);
		((ChoiceData) items.elementAt(elementNum)).font = checkedFont;
	}

	/**
	 * Delete item from specified position.
	 */
	synchronized void delete(int elementNum)
	{
		validatePosition(elementNum, size());
		if(!multiSel && isSelected(elementNum) && size() > 1)
		{
			// we are removing the selected item and there are more
			if(elementNum == size() - 1)
			{
				// select previous item
				setSelected(elementNum - 1, true);
			}
			else
			{
				// select next item
				setSelected(elementNum + 1, true);
			}
		}
		items.removeElementAt(elementNum);
	}

	/**
	 * Delete all items.
	 */
	synchronized void deleteAll()
	{
		items.removeAllElements();
	}

	/**
	 * Get fit policy.
	 */
	int getFitPolicy()
	{
		return fitPolicy;
	}

	/**
	 * Get the Font of a given item.
	 */
	synchronized Font getFont(int elementNum)
	{
		validatePosition(elementNum, size());
		return ((ChoiceData) items.elementAt(elementNum)).font;
	}

	/**
	 * Get the image of a given item.
	 */
	synchronized Image getImage(int elementNum)
	{
		validatePosition(elementNum, size());
		return ((ChoiceData) items.elementAt(elementNum)).img;
	}

	/**
	 * Get the text of a given item.
	 */
	synchronized String getString(int elementNum)
	{
		validatePosition(elementNum, size());
		return ((ChoiceData) items.elementAt(elementNum)).text;
	}

	/**
	 * Set fit policy.
	 */
	void setFitPolicy(int newFitPolicy)
	{
		switch(newFitPolicy)
		{
		case Choice.TEXT_WRAP_DEFAULT:
		case Choice.TEXT_WRAP_OFF:
		case Choice.TEXT_WRAP_ON:
			fitPolicy = newFitPolicy;
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Get the selection index. Returns the first selection index if multiple
	 * selection is allowed.
	 */
	synchronized int getSelectedIndex()
	{
		final int size = size();
		if(!multiSel)
		{
			for(int i = 0; i < size; i++)
			{
				if(isSelected(i))
				{
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Checks if an item is selected.
	 */
	synchronized boolean isSelected(int elementNum)
	{
		validatePosition(elementNum, size());
		return ((ChoiceData) items.elementAt(elementNum)).sel;
	}

	/**
	 * Sets an item's selection.
	 */
	synchronized void setSelected(int elementNum, boolean select)
	{
		validatePosition(elementNum, size());
		if(multiSel)
		{
			((ChoiceData) items.elementAt(elementNum)).sel = select;
		}
		else
		{
			if(select)
			{
				// clear all
				final int size = size();
				for(int i = 0; i < size; i++)
				{
					((ChoiceData) items.elementAt(i)).sel = false;
				}
				// set just one
				((ChoiceData) items.elementAt(elementNum)).sel = true;
			}
		}
	}

	/**
	 * Get selection flags.
	 */
	synchronized int getSelectedFlags(boolean[] selectedArray)
	{
		validateSelectedArray(selectedArray);
		final int size = size();
		int numSelected = 0;
		for(int i = 0; i < selectedArray.length; i++)
		{
			if( (i < size) && (((ChoiceData) items.elementAt(i)).sel))
			{
				selectedArray[i] = true;
				numSelected++;
			}
			else
			{
				selectedArray[i] = false;
			}
		}	  

		return numSelected;
	}

	/**
	 * Set selection flags.
	 */
	synchronized void setSelectedFlags(boolean[] selectedArray)
	{
		validateSelectedArray(selectedArray);
		final int size = size();
		if(size > 0)
		{
			if(multiSel)
			{
				for(int i = 0; i < size; i++)
				{
					((ChoiceData) items.elementAt(i)).sel = selectedArray[i];
				}
			}
			else
			{
				int firstSelected = 0;
				for(int i = 0; i < size; i++)
				{
					if(selectedArray[i])
					{
						firstSelected = i;
						break;
					}
				}
				setSelected(firstSelected, true);
			}
		}
	}

	/**
	 * Returns the size of the list.
	 *
	 * @return the lists size
	 */
	synchronized int size()
	{
		return items.size();
	}

	/**
	 * Validates position.
	 *
	 * @param position an index of the text array
	 * @param upperBoundary upper boundary for position
	 */
	private synchronized void validatePosition(int position, int upperBoundary)
	{
		if(position < 0 || position >= upperBoundary)
		{
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Validates a string.
	 *
	 * @param str string
	 */
	private void validateString(String str)
	{
		if(str == null)
		{
			throw new NullPointerException();
		}
	}

	/**
	 * Validates an array containing selections.
	 *
	 * @param selectedArray selected array
	 */
	private void validateSelectedArray(boolean[] selectedArray)
	{
		if(selectedArray == null)
		{
			throw new NullPointerException();
		}
		if(selectedArray.length < size())
		{
			throw new IllegalArgumentException();
		}
	}

}