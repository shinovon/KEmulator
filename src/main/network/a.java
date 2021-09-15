package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

final class a
  extends Thread
{
  private DatagramPacket datagramPacket;
  private final b bb;
  
  public a(b paramb, DatagramPacket paramDatagramPacket)
  {
    this.bb = paramb;
    this.datagramPacket = paramDatagramPacket;
  }
  
  public final void run()
  {
    try
    {
      byte[] arrayOfByte = this.datagramPacket.getData();
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      i = b.b(arrayOfByte, 1);
      j = b.a(arrayOfByte, 2);
      k = b.a(arrayOfByte, 6) & 0xFF;
      m = b.a(arrayOfByte, 10);
      if ((InetAddress.getLocalHost().getHostAddress().equals(this.datagramPacket.getAddress().getHostAddress())) && (j == this.bb.a)) {
        return;
      }
      switch (k)
      {
      case 16: 
        if (this.bb.b == i) {
          b.a(m);
        }
        if (b.a(this.bb)) {
          b.a(this.bb, arrayOfByte);
        }
        break;
      case 1: 
        if (!b.a(this.bb))
        {
          this.bb.a();
          this.bb.a();
        }
        break;
      }
      return;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      return;
    }
    finally {}
  }
}
