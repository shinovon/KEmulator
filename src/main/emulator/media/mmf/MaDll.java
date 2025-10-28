package emulator.media.mmf;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class MaDll {

	public static final int MODE_MA3 = 1;
	public static final int MODE_MA5 = 2;

	private interface Ma3 extends Library {

	}

	private interface Ma5 extends Library {
		int MaSound_EmuInitialize(int p1, int p2, int p3);
		int MaSound_Initialize(int p1, int p2, int p3);
		int MaSound_DeviceControl(int p1, int p2, int p3, int p4);
		int MaSound_Terminate();
		int MaSound_EmuTerminate();
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

	private int mode;
	private Library library;

	private int EmuBuf;
	private int EmuP;
	private int instanceId;
	private int currentSound;

	public MaDll(String dllPath, int mode) {
		this.library = Native.load(dllPath, Ma5.class);
		this.mode = mode;
	}

	public void init() {
		if (EmuBuf != 0) {
			Native.free(EmuBuf);
		}
		EmuP = EmuBuf = (int) Native.malloc(1024);
		while ((EmuP & 0xFF) != 0x81) {
			EmuP++;
		}

		int r;
		if ((r = ((Ma5) library).MaSound_EmuInitialize(48000, 2, EmuP)) != 0) {
			throw new RuntimeException("MaSound_EmuInitialize: " + r);
		}
		if ((r = ((Ma5) library).MaSound_Initialize(0, EmuBuf, 0)) != 0) {
			throw new RuntimeException("MaSound_Initialize: " + r);
		}
		if ((r = ((Ma5) library).MaSound_DeviceControl(0x0D, 0, 0, 0)) != 0) {
			throw new RuntimeException("MaSound_DeviceControl: " + r);
		}
		if ((r = ((Ma5) library).MaSound_DeviceControl(0x05, 2, 0, 0)) != 0) {
			throw new RuntimeException("MaSound_DeviceControl: " + r);
		}
		if ((r = ((Ma5) library).MaSound_DeviceControl(0x06, 0, 0, 0)) != 0) {
			throw new RuntimeException("MaSound_DeviceControl: " + r);
		}
		if ((r = ((Ma5) library).MaSound_DeviceControl(0x08, 2, 0, 0)) != 0) {
			throw new RuntimeException("MaSound_DeviceControl: " + r);
		}
		if ((r = ((Ma5) library).MaSound_DeviceControl(0x09, 0, 0, 0)) != 0) {
			throw new RuntimeException("MaSound_DeviceControl: " + r);
		}
		if ((r = ((Ma5) library).MaSound_Create(-1)) < 0) {
			throw new RuntimeException("MaSound_Create: " + r);
		}
		instanceId = r;
	}

	public int load(byte[] data) {
		Memory ptr = new Memory(data.length);
		ptr.write(0, data, 0, data.length);

		int r, sound;
		if ((r = sound = ((Ma5) library).MaSound_Load(instanceId, ptr, data.length, 1, 0, 0)) < 1) {
			throw new RuntimeException("MaSound_Load: " + r);
		}

		if ((r = ((Ma5) library).MaSound_Open(instanceId, sound, 0, 0)) != 0) {
			throw new RuntimeException("MaSound_Open: " + r);
		}
		if ((r = ((Ma5) library).MaSound_Standby(instanceId, sound, 0)) != 0) {
			throw new RuntimeException("MaSound_Standby: " + r);
		}

		setVolume(sound, 127);
		setPitch(sound, 0);
		setPitch(sound, 100);
		seek(sound, 0);

		return sound;
	}

	public void seek(int sound, int pos) {
		int r;
		if ((r = ((Ma5) library).MaSound_Seek(instanceId, sound, 0, pos, 0)) != 0) {
			throw new RuntimeException("MaSound_Seek: " + r);
		}
	}

	// 0-127
	public void setVolume(int sound, int volume) {
		IntBuffer volumeBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		volumeBuf.put(volume);
		int r;
		if ((r = ((Ma5) library).MaSound_Control(instanceId, sound, 0, (int) ((DirectBuffer) volumeBuf).address(), 0)) != 0) {
			throw new RuntimeException("MaSound_Control: " + r);
		}
	}

	public void setPitch(int sound, int pitch) {
		IntBuffer pitchBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		pitchBuf.put(pitch);
		int r;
		if ((r = ((Ma5) library).MaSound_Control(instanceId, sound, 2, (int) ((DirectBuffer) pitchBuf).address(), 0)) != 0) {
			throw new RuntimeException("MaSound_Control: " + r);
		}
	}

	public void setTempo(int sound, int tempo) {
		IntBuffer tempoBuf  = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		tempoBuf.put(tempo);
		int r;
		if ((r = ((Ma5) library).MaSound_Control(instanceId, sound, 1, (int) ((DirectBuffer) tempoBuf).address(), 0)) != 0) {
			throw new RuntimeException("MaSound_Control: " + r);
		}
	}

	public void start(int sound, int loops) {
		int r;
		if ((r = ((Ma5) library).MaSound_Start(instanceId, sound, loops, 0)) != 0) {
			throw new RuntimeException("MaSound_Start: " + r);
		}
	}

	public void stop(int sound) {

	}

	public void pause(int sound) {
	}

	public void resume(int sound) {
	}


	public void destroy() {

	}

}
