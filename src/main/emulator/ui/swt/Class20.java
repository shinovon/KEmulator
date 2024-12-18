package emulator.ui.swt;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

final class Class20 extends KeyAdapter {
	private final TreeItem aTreeItem587;
	private final Text aText588;
	private final Watcher aClass5_589;

	Class20(final Watcher aClass5_589, final TreeItem aTreeItem587, final Text aText588) {
		super();
		this.aClass5_589 = aClass5_589;
		this.aTreeItem587 = aTreeItem587;
		this.aText588 = aText588;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		switch (keyEvent.keyCode) {
			case 13: {
				this.aTreeItem587.setText(1, this.aText588.getText());
				Watcher.method315(this.aClass5_589, this.aTreeItem587, this.aText588.getText());
			}
			case 27: {
				this.aText588.dispose();
				break;
			}
		}
	}
}
