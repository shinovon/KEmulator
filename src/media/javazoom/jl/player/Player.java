/*
 * 11/19/04		1.0 moved to LGPL.
 * 29/01/00		Initial version. mdm@techie.com
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */

package javazoom.jl.player;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
	
/**
 * The <code>Player</code> class implements a simple player for playback
 * of an MPEG audio stream. 
 * 
 * @author	Mat McGowan
 * @since	0.0.8
 */

// REVIEW: the audio device should not be opened until the
// first MPEG audio frame has been decoded. 
public class Player
{	  	
	/**
	 * The current frame number. 
	 */
	private int frame = 0;
	
	/**
	 * The MPEG audio bitstream. 
	 */
	// javac blank final bug. 
	/*final*/ private Bitstream		bitstream;
	
	/**
	 * The MPEG audio decoder. 
	 */
	/*final*/ private Decoder		decoder; 
	
	/**
	 * The AudioDevice the audio samples are written to. 
	 */
	private AudioDevice	audio;
	
	/**
	 * Has the player been closed?
	 */
	private boolean		closed = false;
	
	/**
	 * Has the player played back all frames from the stream?
	 */
	private boolean		complete = false;

	private int			lastPosition = 0;

	private boolean paused;

	private int bitrate;

	private byte[] aByteArray1339;

	private int positionOffset;

	private int vol;
	
	/**
	 * Creates a new <code>Player</code> instance. 
	 */
	public Player(InputStream stream) throws JavaLayerException
	{
//		vol = 100;
//	    this.lastPosition = 0;
//	    try
//	    {
//	      this.aByteArray1339 = new byte[stream.available()];
//	      new DataInputStream(stream).readFully(this.aByteArray1339);
//	    }
//	    catch (Exception localException) {
//	    	localException.printStackTrace();
//	    }
//	    reset();
		this(stream, null);
	}
	
	public Player(InputStream stream, AudioDevice device) throws JavaLayerException
	{
		vol = 100;
	    this.lastPosition = 0;
		bitstream = new Bitstream(stream);		
		decoder = new Decoder();
		if (device!=null)
		{		
			audio = device;
		}
		else
		{			
			FactoryRegistry r = FactoryRegistry.systemRegistry();
			audio = r.createAudioDevice();
		}
		audio.open(decoder);
	}
	
	public void play() throws JavaLayerException
	{
		play(Integer.MAX_VALUE);
	}
	
	/**
	 * Plays a number of MPEG audio frames. 
	 * 
	 * @param frames	The number of frames to play. 
	 * @return	true if the last frame was played, or false if there are
	 *			more frames. 
	 */
	public boolean play(int frames) throws JavaLayerException
	{
		paused = false;
		boolean ret = true;
			
		while (frames-- > 0 && ret)
		{
			if(paused) {
				return false;
			}
			if(closed) {
				ret = false;
				break;
			}
			//int i = audio.getPosition();
			ret = decodeFrame();	
			//int j = audio.getPosition() - i;
			try {
			//	System.out.println("fps: " + ((1000-j)/j) + " ("+j+"ms)");
			} catch (Exception e) {
				
			}
		}
		if (!ret)
		{
			// last frame, ensure all data flushed to the audio device. 
			AudioDevice out = audio;
			if (out!=null)
			{				
				out.flush();
				synchronized (this)
				{
					complete = (!closed);
					reset();
				}				
			}
		}
		return ret;
	}

	public void reset() {
		positionOffset = 0;
		try {
			if (this.bitstream != null) {
				this.bitstream.close();
				this.bitstream = null;
			}
			this.bitstream = new Bitstream(new ByteArrayInputStream(this.aByteArray1339));
			decoder = new Decoder();
			if (audio != null) {
				audio.close();
				audio = null;
			}
			FactoryRegistry r = FactoryRegistry.systemRegistry();
			audio = r.createAudioDevice();
			audio.open(decoder);
			this.closed = false;
			paused = false;
		} catch (Exception localException) {
			localException.printStackTrace();
		}

	}

	public int getBitrate() {
		if(bitrate == 0)
			try {
				bitstream.header.bitrate();
			} catch (Exception e) {
				
			}
		return bitrate;
	}
		
	/**
	 * Cloases this player. Any audio currently playing is stopped
	 * immediately. 
	 */
	public synchronized void close()
	{		
	    this.closed = true;
	    AudioDevice anAudioDevice1341;
	    if ((anAudioDevice1341 = this.audio) != null)
	    {
	      audio = null;
	      anAudioDevice1341.close();
	      this.lastPosition = anAudioDevice1341.getPosition();
	      try
	      {
	        this.bitstream.close();
	        this.aByteArray1339 = null;
	      }
	      catch (BitstreamException localb) {}
	    }
	    /*
		AudioDevice out = audio;
		if (out!=null)
		{ 
			closed = true;
			audio = null;	
			// this may fail, so ensure object state is set up before
			// calling this method. 
			out.close();
			lastPosition = out.getPosition();
			try
			{
				bitstream.close();
			}
			catch (BitstreamException ex)
			{
			}
		}
		*/
	}
	
	/**
	 * Returns the completed status of this player.
	 * 
	 * @return	true if all available MPEG audio frames have been
	 *			decoded, or false otherwise. 
	 */
	public synchronized boolean isComplete()
	{
		return complete;	
	}
				
	/**
	 * Retrieves the position in milliseconds of the current audio
	 * sample being played. This method delegates to the <code>
	 * AudioDevice</code> that is used by this player to sound
	 * the decoded audio samples. 
	 */
	public int getPosition()
	{
		int position = lastPosition;
		
		AudioDevice out = audio;		
		if (out!=null)
		{
			position = out.getPosition();	
		}
		return position + positionOffset;
	}		
	
	/**
	 * Decodes a single frame.
	 * 
	 * @return true if there are no more frames to decode, false otherwise.
	 */
	protected boolean decodeFrame() throws JavaLayerException
	{		
		try
		{
			AudioDevice out = audio;
			if (out==null)
				return false;

			Header h = bitstream.readFrame();	
			
			if (h==null)
				return false;
			bitrate = h.bitrate();
			//System.out.println(h.toString());
				
			// sample buffer set when decoder constructed
			SampleBuffer output = (SampleBuffer)decoder.decodeFrame(h, bitstream);
																																					
			synchronized (this)
			{
				out = audio;
				if (out!=null)
				{				
					out.setVolume(vol);
					out.write(output.getBuffer(), 0, output.getBufferLength());
				}				
			}
																			
			bitstream.closeFrame();
		}		
		catch (RuntimeException ex)
		{
			throw new JavaLayerException("Exception decoding audio frame", ex);
		}
/*
		catch (IOException ex)
		{
			System.out.println("exception decoding audio frame: "+ex);
			return false;	
		}
		catch (BitstreamException bitex)
		{
			System.out.println("exception decoding audio frame: "+bitex);
			return false;	
		}
		catch (DecoderException decex)
		{
			System.out.println("exception decoding audio frame: "+decex);
			return false;				
		}
*/		
		return true;
	}

	/**
	 * skips over a single frame
	 * @return false	if there are no more frames to decode, true otherwise.
	 */
	protected boolean skipFrame() throws JavaLayerException
	{
		Header h = bitstream.readFrame();
		if (h == null) return false;
		bitstream.closeFrame();
		return true;
	}

	public float framesPerSecond() {
		return framesPerSecond(bitstream.header);
	}
	
	public static float framesPerSecond(Header h) {
		if(h.frequency() == 0) return 0;
		float o = 1;
		switch(h.frequency()) {
		case 44100:
			o = 38f + 1 / 3;
			break;
		case 32000:
			o = 27.6f;
			break;
		case 48000:
			o = 42f;
			break;
		default:
			int i = h.frequency();
			o = 38.5f;
			o = o / 44100;
			o = o * i;
		}
		return o;
	}


	public boolean skip(final int skip, Header h) throws JavaLayerException
	{
		if(h == null) h = bitstream.header;
		bitrate = h.bitrate();
		positionOffset = skip;
		float o = framesPerSecond(h);
		int offset = (int)((skip / 1000D) * o);
		//System.out.println("skip: "+ skip + "ms" + " d: " + o + " off: "+ offset);
		boolean ret = true;
		int initial = offset;
		while (offset-- > 0 && ret) {
			ret = skipFrame();
			try {
				double i = initial;
				double d = (i-offset)/i;
				int l = (int)(d*skip);
				//if(listener != null && offset % 2 == 1) listener.positionChanged(l);
			} catch (ArithmeticException ignored) {}
		}
		return ret;
	}

	public boolean playSkip(final int to, Player old) throws JavaLayerException
	{
		positionOffset = to;
		float o = 1;
		switch(old.bitstream.header.frequency()) {
		case 44100:
			o = 38f + 1 / 3F;
			break;
		case 32000:
			o = 27.6f;
			break;
		case 48000:
			o = 42f;
			break;
		default:
			int i = old.bitstream.header.frequency();
			o = 38.5f;
			o = o / 44100;
			o = o * i;
		}
		int offset = (int)((to / 1000D) * o);
		boolean ret = true;
		int initial = offset;
		while (offset-- > 0 && ret) {
			ret = skipFrame();
			/*
			try {
				double i = (double)initial;
				double d = (i-offset)/i;
				int l = (int)(d*to);
				//if(listener != null && offset % 2 == 1) listener.positionChanged(l);
			} catch (ArithmeticException e) {
			}*/
		}
		return ret && play(Integer.MAX_VALUE);
	}

	public int getFrame() {
		return frame;
	}

	public void setLevel(int n) {
		vol = n;
		if(audio != null) audio.setVolume(n);
	}

	public void stop() {
		paused = true;
	}

	public Bitstream bitstream() {
		return bitstream;
	}

	
}
