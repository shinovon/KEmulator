package emulator.ui.swt;

import emulator.UILocale;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public final class M3GViewCameraSetDialog extends Dialog {
	public enum Mode {
		FIELD_OF_VIEW, CLIPPING_PLANES, POSITION
	}

	private final Mode mode;
	float fieldOfView = 50.0f;
	float nearPlane = 1.0f;
	float farPlane = 10000.0f;
	float cameraX;
	float cameraY;
	float cameraZ;

	public M3GViewCameraSetDialog(Shell parent, Mode mode) {
		super(parent, SWT.APPLICATION_MODAL | SWT.BORDER | SWT.TITLE | SWT.CLOSE);
		this.mode = mode;
	}

	public void setFieldOfView(float fieldOfView) {
		this.fieldOfView = fieldOfView;
	}

	public void setClippingPlanes(float nearPlane, float farPlane) {
		this.nearPlane = nearPlane;
		this.farPlane = farPlane;
	}

	public void setCameraPosition(float x, float y, float z) {
		cameraX = x;
		cameraY = y;
		cameraZ = z;
	}

	public void open() {
		Shell parent = getParent();
		Shell shell = new Shell(parent, getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();

		Rectangle parentBounds = parent.getBounds();
		Point dialogSize = shell.getSize();
		shell.setLocation(parentBounds.x + (parentBounds.width - dialogSize.x) / 2,
				parentBounds.y + (parentBounds.height - dialogSize.y) / 2);
		shell.open();

		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(2, true));
		final Runnable applyValues;
		switch (mode) {
			case FIELD_OF_VIEW: {
				Text field = createField(shell, UILocale.get("M3G_VIEW_DIALOG_FOVY", "Fovy"), fieldOfView, 2);
				applyValues = () -> fieldOfView = parseValue(field);
				break;
			}
			case CLIPPING_PLANES: {
				Text nearField = createField(shell, UILocale.get("M3G_VIEW_DIALOG_NEAR", "Near"), nearPlane, 1);
				Text farField = createField(shell, UILocale.get("M3G_VIEW_DIALOG_FAR", "Far"), farPlane, 1);
				applyValues = () -> {
					nearPlane = parseValue(nearField);
					farPlane = parseValue(farField);
				};
				break;
			}
			case POSITION: {
				Text xField = createField(shell, "X", cameraX, 1);
				Text yField = createField(shell, "Y", cameraY, 1);
				Text zField = createField(shell, "Z", cameraZ, 2);
				applyValues = () -> {
					cameraX = parseValue(xField);
					cameraY = parseValue(yField);
					cameraZ = parseValue(zField);
				};
				break;
			}
			default:
				throw new AssertionError(mode);
		}

		Button okButton = createButton(shell, UILocale.get("DIALOG_OK", "OK"));
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				applyValues.run();
				shell.close();
			}
		});
		Button cancelButton = createButton(shell, UILocale.get("DIALOG_CANCEL", "Cancel"));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
	}

	private static Text createField(Shell shell, String label, float value, int columnSpan) {
		Group group = new Group(shell, SWT.NONE);
		group.setText(label);
		GridData groupLayout = new GridData(GridData.FILL_HORIZONTAL);
		groupLayout.horizontalSpan = columnSpan;
		group.setLayoutData(groupLayout);
		group.setLayout(new GridLayout(1, true));

		Text field = new Text(group, SWT.BORDER | SWT.RIGHT);
		field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		field.setText(String.valueOf(value));
		return field;
	}

	private static Button createButton(Shell shell, String label) {
		Button button = new Button(shell, SWT.PUSH | SWT.FLAT);
		button.setText(label);
		GridData buttonLayout = new GridData(GridData.FILL_HORIZONTAL);
		buttonLayout.widthHint = 80;
		button.setLayoutData(buttonLayout);
		return button;
	}

	private static float parseValue(Text field) {
		return Float.parseFloat(field.getText());
	}
}
