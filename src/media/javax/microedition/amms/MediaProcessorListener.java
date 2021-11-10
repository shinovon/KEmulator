package javax.microedition.amms;

public abstract interface MediaProcessorListener {
	public static final String PROCESSING_ABORTED = "processingAborted";
	public static final String PROCESSING_COMPLETED = "processingCompleted";
	public static final String PROCESSING_ERROR = "processingError";
	public static final String PROCESSING_STARTED = "processingStarted";
	public static final String PROCESSING_STOPPED = "processingStopped";
	public static final String PROCESSOR_REALIZED = "processRealized";

	public abstract void mediaProcessorUpdate(MediaProcessor paramMediaProcessor, String paramString,
			Object paramObject);
}
