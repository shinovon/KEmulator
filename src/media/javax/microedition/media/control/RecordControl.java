package javax.microedition.media.control;

import javax.microedition.media.Control;
import java.io.OutputStream;

public interface RecordControl extends Control {
	void commit();

	String getContentType();

	void reset();

	void setRecordLocation(final String p0);

	int setRecordSizeLimit(final int p0);

	void setRecordStream(final OutputStream p0);

	void startRecord();

	void stopRecord();
}
