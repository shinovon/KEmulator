package javax.microedition.amms.control;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public abstract interface FormatControl extends Control {
	public static final int METADATA_NOT_SUPPORTED = 0;
	public static final int METADATA_SUPPORTED_FIXED_KEYS = 1;
	public static final int METADATA_SUPPORTED_FREE_KEYS = 2;
	public static final String PARAM_BITRATE = "bitrate";
	public static final String PARAM_BITRATE_TYPE = "bitrate type";
	public static final String PARAM_SAMPLERATE = "sample rate";
	public static final String PARAM_FRAMERATE = "frame rate";
	public static final String PARAM_QUALITY = "quality";
	public static final String PARAM_VERSION_TYPE = "version type";

	public abstract String[] getSupportedFormats();

	public abstract String[] getSupportedStrParameters();

	public abstract String[] getSupportedIntParameters();

	public abstract String[] getSupportedStrParameterValues(String paramString);

	public abstract int[] getSupportedIntParameterRange(String paramString);

	public abstract void setFormat(String paramString);

	public abstract String getFormat();

	public abstract int setParameter(String paramString, int paramInt);

	public abstract void setParameter(String paramString1, String paramString2);

	public abstract String getStrParameterValue(String paramString);

	public abstract int getIntParameterValue(String paramString);

	public abstract int getEstimatedBitRate() throws MediaException;

	public abstract void setMetadata(String paramString1, String paramString2) throws MediaException;

	public abstract String[] getSupportedMetadataKeys();

	public abstract int getMetadataSupportMode();

	public abstract void setMetadataOverride(boolean paramBoolean);

	public abstract boolean getMetadataOverride();
}
