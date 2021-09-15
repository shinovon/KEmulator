package network;

import emulator.Emulator;
import emulator.EventQueue;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public final class b
  implements Runnable
{
  protected DatagramSocket jdField_a_of_type_JavaNetDatagramSocket;
  protected int a;
  protected int b;
  private boolean jdField_a_of_type_Boolean;
  private boolean jdField_b_of_type_Boolean;
  private boolean c = false;
  
  public b()
  {
    this.jdField_a_of_type_Boolean = false;
    this.jdField_b_of_type_Boolean = false;
    this.b = 0;
  }
  
  public final int aInt()
  {
    return this.b;
  }
  
  public final void a(boolean paramBoolean)
  {
    this.b = (paramBoolean ? Math.min(this.b + 1, 100) : Math.max(this.b - 1, 0));
  }
  
  public final boolean aBoolean()
  {
    try
    {
      InetAddress.getLocalHost().getHostName();
    }
    catch (IOException localIOException)
    {
      return false;
    }
    boolean bool = false;
    int i = 0;
    try
    {
      this.jdField_a_of_type_JavaNetDatagramSocket = new DatagramSocket(2110);
      this.a = 2110;
      this.jdField_a_of_type_Boolean = true;
      bool = true;
    }
    catch (SocketException localSocketException1)
    {
      this.jdField_a_of_type_Boolean = false;
      i = 10000;
    }
    while (i < 10010) {
      try
      {
        this.jdField_a_of_type_JavaNetDatagramSocket = new DatagramSocket(i);
        this.a = i;
        bool = true;
      }
      catch (SocketException localSocketException2)
      {
        i++;
      }
    }
    if (bool)
    {
      this.jdField_b_of_type_Boolean = true;
      new Thread(this).start();
    }
    return bool;
  }
  
  public final boolean b()
  {
    return this.jdField_b_of_type_Boolean;
  }
  
  public final void a()
  {
    if (!this.jdField_b_of_type_Boolean) {
      return;
    }
    this.jdField_b_of_type_Boolean = false;
    this.jdField_a_of_type_JavaNetDatagramSocket.close();
    try
    {
      if (this.jdField_a_of_type_Boolean)
      {
        this.jdField_a_of_type_JavaNetDatagramSocket = new DatagramSocket(2111);
        byte[] arrayOfByte = a(1, 0);
        b(arrayOfByte);
        this.jdField_a_of_type_JavaNetDatagramSocket.close();
      }
      return;
    }
    catch (IOException localIOException) {}
  }
  
  public static void a(int paramInt)
  {
    Emulator.getEventQueue().queue(paramInt);
  }
  
  public final void b(int paramInt)
  {
    if (!this.jdField_b_of_type_Boolean) {
      return;
    }
    try
    {
      byte[] arrayOfByte = a(16, paramInt);
      a(arrayOfByte);
      if (this.jdField_a_of_type_Boolean) {
        b(arrayOfByte);
      }
      return;
    }
    catch (IOException localIOException) {}
  }
  
  protected static int a(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0xFF) << 24 | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  private static void a(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte[paramInt1] = ((byte)(paramInt2 >> 24 & 0xFF));
    paramArrayOfByte[(paramInt1 + 1)] = ((byte)(paramInt2 >> 16 & 0xFF));
    paramArrayOfByte[(paramInt1 + 2)] = ((byte)(paramInt2 >> 8 & 0xFF));
    paramArrayOfByte[(paramInt1 + 3)] = ((byte)(paramInt2 & 0xFF));
  }
  
  protected static int b(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[paramInt] & 0xFF;
  }
  
  private static void b(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte[paramInt1] = ((byte)(paramInt2 & 0xFF));
  }
  
  private byte[] a(int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte;
    b(arrayOfByte = new byte[20], 0, 1);
    b(arrayOfByte, 1, this.b);
    a(arrayOfByte, 2, this.a);
    a(arrayOfByte, 6, paramInt1);
    a(arrayOfByte, 10, paramInt2);
    return arrayOfByte;
  }
  
  private void a(byte[] paramArrayOfByte)
    throws IOException
  {
    DatagramPacket localDatagramPacket = new DatagramPacket(paramArrayOfByte, paramArrayOfByte.length, InetAddress.getByName("255.255.255.255"), 2110);
    this.jdField_a_of_type_JavaNetDatagramSocket.send(localDatagramPacket);
  }
  
  private void b(byte[] paramArrayOfByte)
    throws IOException
  {
    for (int i = 10000; i < 10010; i++)
    {
      DatagramPacket localDatagramPacket = new DatagramPacket(paramArrayOfByte, paramArrayOfByte.length, InetAddress.getLocalHost(), i);
      this.jdField_a_of_type_JavaNetDatagramSocket.send(localDatagramPacket);
    }
  }
  
  public final void run()
  {
    try
    {
      this.c = true;
      while (this.jdField_b_of_type_Boolean)
      {
        byte[] arrayOfByte = new byte[20];
        DatagramPacket localDatagramPacket = new DatagramPacket(arrayOfByte, arrayOfByte.length);
        this.jdField_a_of_type_JavaNetDatagramSocket.receive(localDatagramPacket);
        new a(this, localDatagramPacket).start();
      }
    }
    catch (Exception localException) {}
    this.c = false;
  }
  
  static boolean a(b paramb)
  {
    return paramb.jdField_a_of_type_Boolean;
  }
  
  static void a(b paramb, byte[] paramArrayOfByte)
    throws IOException
  {
    paramb.b(paramArrayOfByte);
  }
}
