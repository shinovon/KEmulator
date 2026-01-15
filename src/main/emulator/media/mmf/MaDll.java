package emulator.media.mmf;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public class MaDll {
	public static final int STATE_READY = 3;
	public static final int STATE_PLAYING = 4;
	public static final int STATE_PAUSED = 5;

	public static final int MODE_MA3 = 1;
	public static final int MODE_MA5 = 2;
	public static final int MODE_MA7 = 3;

	// common MaSound entries
	private interface MaSound extends Library {
		int MaSound_Initialize(int p1, int p2, int p3);
		int MaSound_DeviceControl(int p1, int p2, int p3, int p4);
		int MaSound_Create(int p1);
		int MaSound_Load(int p1, Pointer p2, int p3, int p4, int p5, int p6);
		int MaSound_Control(int p1, int p2, int p3, int p4, int p5);
		int MaSound_Open(int p1, int p2, int p3, int p4);
		int MaSound_Standby(int p1, int p2, int p3);
		int MaSound_Start(int p1, int p2, int p3, int p4);
		int MaSound_GetEmuInfo(int p1);
		int MaSound_Stop(int p1, int p2, int p3);
		int MaSound_Pause(int p1, int p2, int p3);
		int MaSound_Restart(int p1, int p2, int p3);
		int MaSound_Seek(int p1, int p2, int p3, int p4, int p5);
		int MaSound_Close(int p1, int p2, int p3);
		int MaSound_Unload(int p1, int p2, int p3);
		int MaSound_Delete(int p1);
	}

	// common Phrase entries for MA3 and MA5
	private interface Phrase extends Library {
		int Phrase_Initialize();
		int Phrase_Terminate();
		int Phrase_SetData(int ch, Pointer data, long len, int check);
		int Phrase_Seek(int ch, long pos);
		int Phrase_Play(int ch, int loop);
		int Phrase_Stop(int ch);
		int Phrase_Pause(int ch);
		int Phrase_Restart(int ch);
		int Phrase_Kill();
		int Phrase_SetVolume(int ch, int vol);
		int Phrase_GetVolume(int ch);
		int Phrase_SetPanpot(int ch, int vol);
		int Phrase_GetPanpot(int ch);
		int Phrase_GetStatus(int ch);
		long Phrase_GetPosition(int ch);
		long Phrase_GetLength(int ch);
		int Phrase_RemoveData(int ch);
	}

	private interface MA3 extends MaSound, Phrase {
		int MaSound_Initialize();
	}

	private interface MA5 extends MaSound, Phrase {
		int MaSound_EmuInitialize(int p1, int p2, int p3);
		int MaSound_Terminate();
		int MaSound_EmuTerminate();
	}

	// MA7 has its own Phrase entries TODO
	private interface MA7 extends MaSound {
		int MaSmw_Init();
		int MaSmw_Term();
	}

	private final int mode;
	private final Library library;

	private int EmuBuf;
	private int instanceId = -1;

	private final List<Integer> sounds = new ArrayList<Integer>();

	public MaDll(String dllPath, int mode) {
		if (mode == MODE_MA3) {
			this.library = Native.load(dllPath, MA3.class);
		} else if (mode == MODE_MA5) {
			this.library = Native.load(dllPath, MA5.class);
		} else if (mode == MODE_MA7) {
			this.library = Native.load(dllPath, MA7.class);
		} else {
			throw new IllegalArgumentException();
		}
		this.mode = mode;
	}

	// masound

	public synchronized void init() {
		if (EmuBuf != 0) {
			Native.free(EmuBuf);
			EmuBuf = 0;
		}

		int r;
		if (mode == MODE_MA3) {
			if ((r = ((MA3) library).MaSound_Initialize()) != 0) {
				throw new RuntimeException("MaSound_Initialize: " + r);
			}
		} else if (mode == MODE_MA5) {
			int emuP = EmuBuf = (int) Native.malloc(1024);
			while ((emuP & 0xFF) != 0x81) {
				emuP++;
			}
			if ((r = ((MA5) library).MaSound_EmuInitialize(48000, 2, emuP)) != 0) {
				throw new RuntimeException("MaSound_EmuInitialize: " + r);
			}
			if ((r = ((MA5) library).MaSound_Initialize(0, EmuBuf, 0)) != 0) {
				throw new RuntimeException("MaSound_Initialize: " + r);
			}
			if ((r = ((MA5) library).MaSound_DeviceControl(0x0D, 0, 0, 0)) != 0) {
				throw new RuntimeException("MaSound_DeviceControl: " + r);
			}
			if ((r = ((MA5) library).MaSound_DeviceControl(0x05, 2, 0, 0)) != 0) {
				throw new RuntimeException("MaSound_DeviceControl: " + r);
			}
			if ((r = ((MA5) library).MaSound_DeviceControl(0x06, 0, 0, 0)) != 0) {
				throw new RuntimeException("MaSound_DeviceControl: " + r);
			}
			if ((r = ((MA5) library).MaSound_DeviceControl(0x08, 2, 0, 0)) != 0) {
				throw new RuntimeException("MaSound_DeviceControl: " + r);
			}
			if ((r = ((MA5) library).MaSound_DeviceControl(0x09, 0, 0, 0)) != 0) {
				throw new RuntimeException("MaSound_DeviceControl: " + r);
			}
		} else if (mode == MODE_MA7) {
			if ((r = ((MA7) library).MaSmw_Init()) != 0) {
				throw new RuntimeException("MaSmw_Init: " + r);
			}
		}
		if ((r = ((MaSound) library).MaSound_Create(1)) < 0) {
			throw new RuntimeException("MaSound_Create: " + r);
		}
		instanceId = r;
	}

	public synchronized int load(byte[] data) {
		Memory ptr = new Memory(data.length);
		ptr.write(0, data, 0, data.length);

		int r;
		if ((r = ((MaSound) library).MaSound_Load(instanceId, ptr, data.length, 1, 0, 0)) < 1) {
			throw new RuntimeException("MaSound_Load: " + r);
		}
		int sound = r;

		if ((r = ((MaSound) library).MaSound_Open(instanceId, sound, 0, 0)) != 0) {
			throw new RuntimeException("MaSound_Open: " + r);
		}
		if ((r = ((MaSound) library).MaSound_Standby(instanceId, sound, 0)) != 0) {
			throw new RuntimeException("MaSound_Standby: " + r);
		}

		setVolume(sound, 127);
		setPitch(sound, 0);
		setTempo(sound, 100);
		seek(sound, 0);

		sounds.add(sound);

		return sound;
	}

	public synchronized void seek(int sound, int pos) {
		int r;
		if ((r = ((MaSound) library).MaSound_Seek(instanceId, sound, pos, 0, 0)) != 0) {
			throw new RuntimeException("MaSound_Seek: " + r);
		}
	}

	// 0..127
	public synchronized void setVolume(int sound, int volume) {
		IntBuffer volumeBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		volumeBuf.put(volume);
		int r;
		if ((r = ((MaSound) library).MaSound_Control(instanceId, sound, 0, (int) getAddress(volumeBuf), 0)) != 0) {
			throw new RuntimeException("MaSound_Control: " + r);
		}
	}

	// -12..+12
	public synchronized void setPitch(int sound, int pitch) {
		IntBuffer pitchBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		pitchBuf.put(pitch);
		int r;
		if ((r = ((MaSound) library).MaSound_Control(instanceId, sound, 2, (int) getAddress(pitchBuf), 0)) != 0) {
			throw new RuntimeException("MaSound_Control: " + r);
		}
	}

	// 70..130
	public synchronized void setTempo(int sound, int tempo) {
		IntBuffer tempoBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		tempoBuf.put(tempo);
		int r;
		if ((r = ((MaSound) library).MaSound_Control(instanceId, sound, 1, (int) getAddress(tempoBuf), 0)) != 0) {
			throw new RuntimeException("MaSound_Control: " + r);
		}
	}

	public synchronized void start(int sound, int loops) {
		int r;
		if ((r = ((MaSound) library).MaSound_Start(instanceId, sound, loops, 0)) != 0) {
			throw new RuntimeException("MaSound_Start: " + r);
		}
	}

	public synchronized void stop(int sound) {
		int r;
		if ((r = ((MaSound) library).MaSound_Stop(instanceId, sound, 0)) != 0) {
			throw new RuntimeException("MaSound_Stop: " + r);
		}
	}

	public synchronized void pause(int sound) {
		int r;
		if ((r = ((MaSound) library).MaSound_Pause(instanceId, sound, 0)) != 0) {
			throw new RuntimeException("MaSound_Pause: " + r);
		}
	}

	public synchronized void resume(int sound) {
		int r;
		if ((r = ((MaSound) library).MaSound_Restart(instanceId, sound, 0)) != 0) {
			throw new RuntimeException("MaSound_Restart: " + r);
		}
	}

	public synchronized int getStatus(int sound) {
		return ((MaSound) library).MaSound_Control(instanceId, sound, 6, 0, 0);
	}

	public synchronized int getPosition(int sound) {
		return ((MaSound) library).MaSound_Control(instanceId, sound, 4, 0, 0);
	}

	public synchronized void close(int sound) {
		((MaSound) library).MaSound_Stop(instanceId, sound, 0);
		((MaSound) library).MaSound_Close(instanceId, sound, 0);
		((MaSound) library).MaSound_Unload(instanceId, sound, 0);
		sounds.remove((Integer) sound);
	}

	public synchronized void destroy() {
		for (Integer sound : sounds) {
			((MaSound) library).MaSound_Close(instanceId, sound, 0);
			((MaSound) library).MaSound_Unload(instanceId, sound, 0);
		}
		((MaSound) library).MaSound_Delete(instanceId);

		if (mode == MODE_MA5) {
			((MA5) library).MaSound_Terminate();
			((MA5) library).MaSound_EmuTerminate();
		} else if (mode == MODE_MA7) {
			((MA7) library).MaSmw_Term();
		}

		if (EmuBuf != 0) {
			Native.free(EmuBuf);
			EmuBuf = 0;
		}
	}

	// phrase TODO



	// utils
	
	private static long getAddress(Buffer buf) {
		try {
			Class cls = Class.forName("sun.nio.ch.DirectBuffer");
			Method m = cls.getMethod("address");
			return (Long) m.invoke(buf);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
