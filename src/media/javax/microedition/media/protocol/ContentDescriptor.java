package javax.microedition.media.protocol;

public class ContentDescriptor
{
    private String contentType;
    
    public ContentDescriptor(final String type) {
        super();
        this.contentType = type;
    }
    
    public String getContentType() {
        return this.contentType;
    }
}
