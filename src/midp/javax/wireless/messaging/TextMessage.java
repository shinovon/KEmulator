package javax.wireless.messaging;

public interface TextMessage extends Message
{
    String getPayloadText();
    
    void setPayloadText(final String p0);
}
