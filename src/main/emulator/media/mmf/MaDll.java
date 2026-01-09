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

public class MaDll {

	public static final int MODE_MA3 = 1;
	public static final int MODE_MA5 = 2;
	public static final int MODE_MA7 = 3;

	private interface MA extends Library {
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

	private interface MA3 extends MA {
		int MaSound_Initialize();
	}

	private interface MA5 extends MA {
		int MaSound_EmuInitialize(int p1, int p2, int p3);
		int MaSound_Terminate();
		int MaSound_EmuTerminate();
	}

	private interface MA7 extends MA {
		int MaSmw_Init();
		int MaSmw_Term();
	}

	private int mode;
	private Library library;

	private int EmuBuf;
	private int EmuP;
	private int instanceId = -1;

	private List<Integer> sounds = new ArrayList<Integer>();

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

	public void init() {
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
			EmuP = EmuBuf = (int) Native.malloc(1024);
			while ((EmuP & 0xFF) != 0x81) {
				EmuP++;
			}
			if ((r = ((MA5) library).MaSound_EmuInitialize(48000, 2, EmuP)) != 0) {
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
		if ((r = ((MA) library).MaSound_Create(1)) < 0) {
			throw new RuntimeException("MaSound_Create: " + r);
		}
		instanceId = r;
	}

	public int load(byte[] data) {
		Memory ptr = new Memory(data.length);
		ptr.write(0, data, 0, data.length);

		int r;
		if ((r = ((MA) library).MaSound_Load(instanceId, ptr, data.length, 1, 0, 0)) < 1) {
			throw new RuntimeException("MaSound_Load: " + r);
		}
		int sound = r;

		if ((r = ((MA) library).MaSound_Open(instanceId, sound, 0, 0)) != 0) {
			throw new RuntimeException("MaSound_Open: " + r);
		}
		if ((r = ((MA) library).MaSound_Standby(instanceId, sound, 0)) != 0) {
			throw new RuntimeException("MaSound_Standby: " + r);
		}

		setVolume(sound, 127);
		setPitch(sound, 0);
		setTempo(sound, 100);
		seek(sound, 0);

		sounds.add(sound);

		return sound;
	}

	public void seek(int sound, int pos) {
		int r;
		if ((r = ((MA) library).MaSound_Seek(instanceId, sound, 0, pos, 0)) != 0) {
			throw new RuntimeException("MaSound_Seek: " + r);
		}
	}

	// 0-127
	public void setVolume(int sound, int volume) {
		IntBuffer volumeBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		volumeBuf.put(volume);
		int r;
		if ((r = ((MA) library).MaSound_Control(instanceId, sound, 0, (int) getAddress(volumeBuf), 0)) != 0) {
			throw new RuntimeException("MaSound_Control: " + r);
		}
	}

	public void setPitch(int sound, int pitch) {
		IntBuffer pitchBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		pitchBuf.put(pitch);
		int r;
		if ((r = ((MA) library).MaSound_Control(instanceId, sound, 2, (int) getAddress(pitchBuf), 0)) != 0) {
			throw new RuntimeException("MaSound_Control: " + r);
		}
	}

	public void setTempo(int sound, int tempo) {
		IntBuffer tempoBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		tempoBuf.put(tempo);
		int r;
		if ((r = ((MA) library).MaSound_Control(instanceId, sound, 1, (int) getAddress(tempoBuf), 0)) != 0) {
			throw new RuntimeException("MaSound_Control: " + r);
		}
	}

	public void start(int sound, int loops) {
		int r;
		if ((r = ((MA) library).MaSound_Start(instanceId, sound, loops, 0)) != 0) {
			throw new RuntimeException("MaSound_Start: " + r);
		}
	}

	public void stop(int sound) {
		int r;
		if ((r = ((MA) library).MaSound_Stop(instanceId, sound, 0)) != 0) {
			throw new RuntimeException("MaSound_Stop: " + r);
		}
	}

	public void pause(int sound) {
		int r;
		if ((r = ((MA) library).MaSound_Pause(instanceId, sound, 0)) != 0) {
			throw new RuntimeException("MaSound_Pause: " + r);
		}
	}

	public void resume(int sound) {
		int r;
		if ((r = ((MA) library).MaSound_Restart(instanceId, sound, 0)) != 0) {
			throw new RuntimeException("MaSound_Pause: " + r);
		}
	}

	public void close(int sound) {
		((MA) library).MaSound_Stop(instanceId, sound, 0);
		((MA) library).MaSound_Close(instanceId, sound, 0);
		((MA) library).MaSound_Unload(instanceId, sound, 0);
		sounds.remove((Integer) sound);
	}

	public void destroy() {
		for (Integer sound : sounds) {
			((MA) library).MaSound_Close(instanceId, sound, 0);
			((MA) library).MaSound_Unload(instanceId, sound, 0);
		}
		((MA) library).MaSound_Delete(instanceId);

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
