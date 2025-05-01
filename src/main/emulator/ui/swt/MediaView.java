package emulator.ui.swt;

import com.nokia.mid.sound.Sound;
import com.samsung.util.AudioClip;
import emulator.Emulator;
import emulator.Settings;
import emulator.UILocale;
import emulator.debug.Memory;
import emulator.debug.PlayerActionType;
import emulator.media.capture.CapturePlayerImpl;
import emulator.media.tone.MIDITonePlayer;
import emulator.media.vlc.VLCPlayerImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import javax.microedition.media.MIDIImpl;
import javax.microedition.media.PlayerImpl;
import javax.microedition.media.ToneImpl;
import java.util.Arrays;
import java.util.Vector;

/**
 * Tool window that allows user to inspect, modify and export active media players.
 */
public class MediaView extends SelectionAdapter implements DisposeListener, SelectionListener, Runnable {

	private Shell shell;
	private final Display display = SWTFrontend.getDisplay();
	private final Memory memoryMgr = Memory.getInstance();
	private Button resumeBtn;
	private Button pauseBtn;
	private Button stopBtn;
	private Button exportBtn;
	private CLabel volumeValueLabel;
	private Scale volumeScale;
	private CLabel timeLabel;
	private ProgressBar timeBar;
	private Table table;
	private boolean visible;

	public void open() {
		createShell();
		shell.open();
		shell.addDisposeListener(this);
		updateAll();
		new Thread(this).start();
		this.visible = true;
		while (!this.shell.isDisposed()) {
			if (!this.display.readAndDispatch()) {
				this.display.sleep();
			}
		}
		this.visible = false;
	}

	private void createShell() {
		shell = new Shell(SWT.MAX | SWT.FOREGROUND | SWT.TITLE | SWT.MENU | SWT.MIN);
		shell.setText(UILocale.get("MEDIA_VIEW_TITLE", "Media view"));
		this.shell.setImage(new org.eclipse.swt.graphics.Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.shell.setLayout(new GridLayout(9, false));

		resumeBtn = new Button(shell, SWT.FLAT);
		resumeBtn.setText(UILocale.get("MEMORY_VIEW_SOUND_START", "Resume"));
		resumeBtn.addSelectionListener(this);

		pauseBtn = new Button(shell, SWT.FLAT);
		pauseBtn.setText(UILocale.get("MEMORY_VIEW_SOUND_PAUSE", "Pause"));
		pauseBtn.addSelectionListener(this);

		stopBtn = new Button(shell, SWT.FLAT);
		stopBtn.setText(UILocale.get("MEMORY_VIEW_SOUND_STOP", "Stop"));
		stopBtn.addSelectionListener(this);

		exportBtn = new Button(shell, SWT.FLAT);
		exportBtn.setText(UILocale.get("MEMORY_VIEW_SOUND_EXPORT", "Export"));
		exportBtn.addSelectionListener(this);

		CLabel volumeLabel = new CLabel(shell, SWT.NONE);
		volumeLabel.setText(UILocale.get("MEMORY_VIEW_VOLUME_LABEL", "Volume"));

		volumeValueLabel = new CLabel(shell, SWT.NONE);
		volumeValueLabel.setText("000%");

		volumeScale = new Scale(shell, SWT.HORIZONTAL);
		volumeScale.setMaximum(100);
		volumeScale.addSelectionListener(this);

		timeLabel = new CLabel(shell, SWT.NONE);
		timeLabel.setText("0000:00/0000:00");

		GridData gd1 = new GridData();
		gd1.grabExcessHorizontalSpace = true;
		gd1.verticalAlignment = GridData.CENTER;
		gd1.horizontalAlignment = GridData.FILL;
		timeBar = new ProgressBar(shell, SWT.SMOOTH);
		timeBar.setLayoutData(gd1);

		GridData gd2 = new GridData();
		gd2.grabExcessHorizontalSpace = true;
		gd2.grabExcessVerticalSpace = true;
		gd2.verticalAlignment = GridData.FILL;
		gd2.horizontalAlignment = GridData.FILL;
		gd2.horizontalSpan = 9;
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(this);
		table.setLayoutData(gd2);

		addTableColumn("Instance", 200);
		addTableColumn("Content type", 100);
		addTableColumn("State", 89);
		addTableColumn("Duration", 80);
		addTableColumn("Loops", 50);
		addTableColumn("Data size", 80);
		addTableColumn("Implementation", 100);

		Rectangle clientArea = ((EmulatorScreen) Emulator.getEmulator().getScreen()).getShell().getMonitor().getClientArea();
		shell.setSize(
				(int) (clientArea.width * 0.4),
				(int) (clientArea.height * 0.6)
		);
		shell.setLocation(
				clientArea.x + (int) (clientArea.height * 0.025),
				clientArea.y + (int) (clientArea.height * 0.025)
		);
	}

	private void updateAll() {
		if (shell.isDisposed())
			return;
		memoryMgr.updateEverything();
		Vector players = new Vector(memoryMgr.players);
		Vector<TableItem> lines = new Vector<>(Arrays.asList(table.getItems()));

		while (!lines.isEmpty()) {
			TableItem line = lines.lastElement();
			if (players.contains(line.getData())) {
				// player is still alive
				updateTableLine(line);
				players.remove(line.getData());
				lines.remove(lines.size() - 1);
				continue;
			}
			// player is dead
			table.remove(lines.size() - 1);
			lines.remove(lines.size() - 1);
		}
		for (Object player : players) {
			TableItem line = new TableItem(this.table, 0);
			line.setData(player);
			updateTableLine(line);
		}

		updateControls();
	}

	private void updateTableLine(TableItem item) {
		Object value = item.getData();
		int msLen = Memory.getPlayerDurationMs(value);
		String lengthText = msLen < 0 ? "Unknown" : (formatTime(msLen) + String.format(".%1$03d", msLen % 1000));
		int loopCount = Memory.getPlayerLoopCount(value);
		item.setText(0, value.toString());
		item.setText(1, Memory.getPlayerType(value));
		item.setText(2, Memory.playerStateStr(value));
		item.setText(3, lengthText);
		item.setText(4, loopCount < 0 ? "∞" : String.valueOf(loopCount));
		item.setText(5, String.valueOf(Memory.getPlayerDataLength(value)));
		item.setText(6, getImplementation(value));
	}

	private void updateControls() {
		final Object player = getSelectedPlayer();
		if (player == null) {
			resumeBtn.setEnabled(false);
			pauseBtn.setEnabled(false);
			stopBtn.setEnabled(false);
			exportBtn.setEnabled(false);
			volumeValueLabel.setText("???%");
			volumeScale.setSelection(0);
			volumeScale.setEnabled(false);
			timeLabel.setText("??:??/??:??");
			timeBar.setSelection(0);
			return;
		}

		resumeBtn.setEnabled(true);
		pauseBtn.setEnabled(true);
		stopBtn.setEnabled(true);
		exportBtn.setEnabled(Settings.enableMediaDump);
		volumeValueLabel.setText(Memory.getPlayerVolume(player) + "%");
		volumeScale.setEnabled(true);
		volumeScale.setSelection(Memory.getPlayerVolume(player));
		try {
			int total = Memory.getPlayerDurationMs(player);
			int now = Memory.getPlayerCurrentMs(player);
			timeLabel.setText(formatTime(now) + "/" + formatTime(total));
			if (total > 0 && now >= 0)
				timeBar.setSelection(now * 100 / total);
			else
				timeBar.setSelection(0);
		} catch (Exception e) {
			timeLabel.setText("Error");
		}
	}

	private static String formatTime(int ms) {
		if (ms < 0)
			return "??:??";
		return String.format("%1$02d:%2$02d", ms / 60000, ms / 1000 % 60);
	}

	private static String getImplementation(Object player) {
		if (player instanceof AudioClip) {
			AudioClip ac = ((AudioClip) player);
			if (ac.type == AudioClip.TYPE_MMF)
				return "ma3smwemu";
			return getImplementation(ac.m_player);
		}
		if (player instanceof VLCPlayerImpl)
			return "VLC";
		if (player instanceof Sound)
			return getImplementation(((Sound) player).m_player);
		if (player instanceof ToneImpl || player instanceof MIDITonePlayer || player instanceof MIDIImpl)
			return "JVM MIDI";
		if (player instanceof CapturePlayerImpl)
			return "Capture";
		if (player instanceof PlayerImpl)
			return ((PlayerImpl) player).getReadableImplementationType();
		return "Unknown";
	}

	private Object getSelectedPlayer() {
		TableItem[] sel = table.getSelection();
		if (sel != null && sel.length >= 1)
			return sel[0].getData();
		return null;
	}

	@Override
	public final void widgetSelected(final SelectionEvent e) {
		if (e.widget == table) {
			updateControls();
			return;
		}
		if (e.widget == resumeBtn) {
			Memory.modifyPlayer(getSelectedPlayer(), PlayerActionType.resume);
			updateControls();
			return;
		}
		if (e.widget == pauseBtn) {
			Memory.modifyPlayer(getSelectedPlayer(), PlayerActionType.pause);
			updateControls();
			return;
		}
		if (e.widget == stopBtn) {
			Memory.modifyPlayer(getSelectedPlayer(), PlayerActionType.stop);
			updateControls();
			return;
		}
		if (e.widget == exportBtn) {
			Memory.modifyPlayer(getSelectedPlayer(), PlayerActionType.export);
			updateControls();
			return;
		}
		if (e.widget == volumeScale) {
			Memory.setPlayerVolume(getSelectedPlayer(), volumeScale.getSelection());
			updateControls();
			return;
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent disposeEvent) {
		this.dispose();
	}

	public final boolean isShown() {
		return this.visible;
	}

	public final void dispose() {
		if (this.shell != null && !this.shell.isDisposed()) {
			this.shell.dispose();
		}
		this.visible = false;
	}

	public void run() {
		try {
			Thread.sleep(1000);
			do {
				display.syncExec(this::updateAll);
				Thread.sleep(1000);
			} while (visible && !this.shell.isDisposed());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void addTableColumn(String name, int width) {
		TableColumn tc = new TableColumn(table, SWT.NONE);
		tc.setText(name);
		tc.setWidth(width);
	}
}
