package com.nokia.mid.sound;

import emulator.Emulator;
import emulator.media.b;

import java.io.ByteArrayInputStream;
import javax.microedition.media.PlayerImpl;
import javax.microedition.media.control.VolumeControlImpl;

public class Sound
{
  public static final int SOUND_PLAYING = 0;
  public static final int SOUND_STOPPED = 1;
  public static final int SOUND_UNINITIALIZED = 3;
  public static final int FORMAT_TONE = 1;
  public static final int FORMAT_WAV = 5;
  public PlayerImpl m_player;
  private Sound jdField_a_of_type_ComNokiaMidSoundSound;
  private SoundListener jdField_a_of_type_ComNokiaMidSoundSoundListener;
  private int jdField_a_of_type_Int;
  private int type;
  public int dataLen;
  
  public Sound(byte[] paramArrayOfByte, int paramInt)
  {
    this.type = paramInt;
    this.dataLen = paramArrayOfByte.length;
    this.jdField_a_of_type_ComNokiaMidSoundSound = this;
    init(paramArrayOfByte, paramInt);
  }
  
  public String getType()
  {
    if (this.type == 1) {
      return "FORMAT_TONE";
    }
    if (this.type == 5) {
      return "FORMAT_WAV";
    }
    return null;
  }
  
  public Sound(int paramInt, long paramLong)
  {
    init(paramInt, paramLong);
  }
  
  public static int getConcurrentSoundCount(int paramInt)
  {
    return 1;
  }
  
  public int getGain()
  {
    return ((VolumeControlImpl)this.m_player.getControl("VolumeControl")).getLevel();
  }
  
  public int getState()
  {
    return this.jdField_a_of_type_Int;
  }
  
  public static int[] getSupportedFormats()
  {
    int[] arrayOfInt;
    return arrayOfInt = new int[] { 1, 5 };
  }
  
  public void init(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if (paramInt == 1) {
      paramArrayOfByte = new b(paramArrayOfByte).method726();
    }
    try
    {
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      this.m_player = new PlayerImpl(localByteArrayInputStream, type == FORMAT_WAV ? "audio/wav" : null);
      this.m_player.addPlayerListener(new a(this));
      localByteArrayInputStream.close();
    }
    catch (Exception localException) {}
    this.jdField_a_of_type_Int = 3;
  }
  
  public void init(int paramInt, long paramLong)
  {
    Emulator.getEmulator().getLogStream().println("** com.nokia.mid.sound.init(int freq, long duration) not implemented yet **");
  }
  
  public void play(int paramInt)
  {
    this.m_player.setLoopCount(paramInt == 0 ? -1 : paramInt);
    resume();
  }
  
  public void release()
  {
    if (this.jdField_a_of_type_Int == 0) {
      stop();
    }
    if (this.jdField_a_of_type_Int != 3)
    {
      if(m_player != null)this.m_player.deallocate();
      this.jdField_a_of_type_Int = 3;
      if (this.jdField_a_of_type_ComNokiaMidSoundSoundListener != null) {
        this.jdField_a_of_type_ComNokiaMidSoundSoundListener.soundStateChanged(this, 3);
      }
    }
  }
  
  public void resume()
  {
    try
    {
      this.m_player.start();
    }
    catch (Exception localException) {}
    this.jdField_a_of_type_Int = 0;
    if (this.jdField_a_of_type_ComNokiaMidSoundSoundListener != null) {
      this.jdField_a_of_type_ComNokiaMidSoundSoundListener.soundStateChanged(this, 0);
    }
  }
  
  public void setGain(int paramInt)
  {
    this.m_player.setLevel(paramInt);
  }
  
  public void setSoundListener(SoundListener paramSoundListener)
  {
    this.jdField_a_of_type_ComNokiaMidSoundSoundListener = paramSoundListener;
  }
  
  public void stop()
  {
    try
    {
      this.m_player.stop();
    }
    catch (Exception localException) {}
    this.jdField_a_of_type_Int = 1;
    if (this.jdField_a_of_type_ComNokiaMidSoundSoundListener != null) {
      this.jdField_a_of_type_ComNokiaMidSoundSoundListener.soundStateChanged(this, 1);
    }
  }
  
  static int aI(Sound paramSound, int paramInt)
  {
    return paramSound.jdField_a_of_type_Int = paramInt;
  }
  
  static SoundListener aL(Sound paramSound)
  {
    return paramSound.jdField_a_of_type_ComNokiaMidSoundSoundListener;
  }
  
  static Sound aS(Sound paramSound)
  {
    return paramSound.jdField_a_of_type_ComNokiaMidSoundSound;
  }
}
