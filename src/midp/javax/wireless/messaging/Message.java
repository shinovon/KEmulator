package javax.wireless.messaging;

import java.util.*;

public interface Message
{
    String getAddress();
    
    void setAddress(final String p0);
    
    Date getTimestamp();
}
