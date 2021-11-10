package com.nokia.microedition.media;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamSeekControl
  implements SeekControl
{
  private InputStream iInputStream;
  
  public InputStreamSeekControl()
  {
    this.iInputStream = null;
  }
  
  public InputStreamSeekControl(InputStream aInputStream)
  {
    this.iInputStream = aInputStream;
    if (this.iInputStream.markSupported() == true) {
      this.iInputStream.mark(0);
    }
  }
  
  public void seek(int aWhere)
    throws IOException
  {
    if (this.iInputStream.markSupported() == true) {
      this.iInputStream.reset();
    }
  }
  
  public void close() {}
}
