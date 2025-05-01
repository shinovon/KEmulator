package emulator.lcdui;

import javax.microedition.lcdui.Graphics;

public interface IScreenImpl {

	void layout();

	void paint(Graphics graphics);

	boolean isSWT();

	void swtShown();

	void swtHidden();

	void swtUpdateSizes();

	Object getSwtContent();
}
