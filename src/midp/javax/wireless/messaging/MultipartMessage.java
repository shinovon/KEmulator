package javax.wireless.messaging;

public interface MultipartMessage extends Message
{
    boolean addAddress(final String p0, final String p1);
    
    void addMessagePart(final MessagePart p0) throws SizeExceededException;
    
    String getAddress();
    
    String[] getAddresses(final String p0);
    
    String getHeader(final String p0);
    
    MessagePart getMessagePart(final String p0);
    
    MessagePart[] getMessageParts();
    
    String getStartContentId();
    
    String getSubject();
    
    boolean removeAddress(final String p0, final String p1);
    
    void removeAddresses();
    
    void removeAddresses(final String p0);
    
    boolean removeMessagePart(final MessagePart p0);
    
    boolean removeMessagePartId(final String p0);
    
    boolean removeMessagePartLocation(final String p0);
    
    void setAddress(final String p0);
    
    void setHeader(final String p0, final String p1);
    
    void setStartContentId(final String p0);
    
    void setSubject(final String p0);
}
