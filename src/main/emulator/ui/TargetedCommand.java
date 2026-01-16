/*
Copyright (c) 2025 Fyodor Ryzhov
*/
package emulator.ui;

import emulator.Emulator;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;

public class TargetedCommand {
	public final Command command;
	public final Displayable screen;
	public final Item item;
	public final int selectionIndex;
	/**
	 * True if this command is an element of POPUP choice, and it was selected at moment of menu creation.
	 */
	public final boolean wasSelected;
	public final String text;

	/**
	 * Command, hosted on displayable.
	 */
	public TargetedCommand(Command command, Displayable screen) {
		this.command = command;
		this.screen = screen;
		this.item = null;
		selectionIndex = -1;
		wasSelected = false;
		text = command.getLongLabel();
	}

	/**
	 * Command, hosted on item.
	 */
	public TargetedCommand(Command command, Item item) {
		this.command = command;
		this.screen = null;
		this.item = item;
		selectionIndex = -1;
		wasSelected = false;
		text = command.getLongLabel();
	}

	public TargetedCommand(ChoiceGroup group, int index, boolean selected) {
		this.command = null;
		this.screen = null;
		this.item = group;
		this.selectionIndex = index;
		wasSelected = selected;
		text = group.getString(index);
	}

	public boolean isChoice() {
		return (item instanceof ChoiceGroup) && command == null;
	}

	public void invoke() {
		if (command == null && item != null) {
			((ChoiceGroup) item).setSelectedIndex(selectionIndex, true);
			return;
		}
		if (command != null && screen != null) {
			Emulator.getEventQueue().commandAction(command, screen);
			return;
		}
		if (command != null && item != null) {
			Emulator.getEventQueue().commandAction(command, item);
			return;
		}
		throw new IllegalStateException();

	}
}
