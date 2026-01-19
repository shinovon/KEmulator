/*
Copyright (c) 2025-2026 Arman Jussupgaliyev
*/
package emulator.media.mmf;

import com.sun.jna.*;

import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.*;

@SuppressWarnings("UnusedReturnValue")
public class MaDll {

	public static final int MODE_MA3 = 1;
	public static final int MODE_MA5 = 2;
	public static final int MODE_MA7 = 3;

	// region JNA interfaces

	// common MaSound entries
	private interface MaSound extends Library {
		int MaSound_Initialize();
		int MaSound_DeviceControl(int p1, int p2, int p3, int p4);
		int MaSound_Create(int p1);
		int MaSound_Load(int p1, Pointer p2, int p3, int p4, int p5, int p6);
		int MaSound_Control(int p1, int p2, int p3, Pointer p4, int p5);
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

	public static class PhraseInfo extends Structure {
		public long makerID;
		public int deviceID;
		public int versionID;
		public int maxVoice;
		public int maxChannel;
		public int supportSMAF;
		public long latency;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("makerID", "deviceID", "versionID", "maxVoice", "maxChannel", "supportSMAF", "latency");
		}
	}

	public static class PhraseEvent extends Structure {
		public int ch;
		public int mode;

		public PhraseEvent() {
		}

		public PhraseEvent(Pointer p) {
			super(p);
			read();
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("ch", "mode");
		}
	}

	private static class PhraseEventCallback implements Callback {
		public void callback(Pointer p) {
			PhraseEvent event = new PhraseEvent(p);
			if (phrasePlayer == null) return;
			phrasePlayer.eventCallback(event.ch, event.mode);
		}
	}

	// common Phrase entries for MA3 and MA5
	private interface Phrase extends Library {
		int Phrase_Initialize();
		int Phrase_Terminate();
		int Phrase_GetInfo(PhraseInfo info);
		int Phrase_CheckData(Pointer data, long len);
		int Phrase_SetData(int ch, Pointer data, long len, int check);
		int Phrase_Seek(int ch, long pos);
		int Phrase_Play(int ch, int loop);
		int Phrase_Stop(int ch);
		int Phrase_Pause(int ch);
		int Phrase_Restart(int ch);
		int Phrase_Kill();
		void Phrase_SetVolume(int ch, int vol);
		int Phrase_GetVolume(int ch);
		void Phrase_SetPanpot(int ch, int vol);
		int Phrase_GetPanpot(int ch);
		int Phrase_GetStatus(int ch);
		long Phrase_GetPosition(int ch);
		long Phrase_GetLength(int ch);
		int Phrase_RemoveData(int ch);
		int Phrase_SetLink(int ch, long slave);
		long Phrase_GetLink(int ch);
		int Phrase_SetEvHandler(Callback ev);
	}


	private interface MA3 extends MaSound, Phrase {
	}

	private interface MA5 extends MaSound, Phrase {
		int MaSound_Initialize(int p1, int p2, int p3);
		int MaSound_EmuInitialize(int p1, int p2, int p3);
		int MaSound_Terminate();
		int MaSound_EmuTerminate();
	}

	private interface MA7 extends MaSound {
		int MaSmw_Init();
		int MaSmw_Term();
		int Mapi_EmuGetInfo(int p);
		int Mapi_EmuInitialize(int p1, Pointer p2, Pointer p3, Pointer p4, Pointer p5, int p6);
		int Mapi_Initialize();
		int Mapi_DeviceControlEx(int p1, int p2, Pointer p3);
		int Mapi_GetMode();
		int Mapi_SetMode(int p);
		int Mapi_Terminate();
		int Mapi_EmuTerminate();


		int Mapi_Phrase_GetInfo(PhraseInfo info);
		int Mapi_Phrase_CheckData(Pointer data, int len);
		int Mapi_Phrase_SetData(int ch, Pointer data, int len, int check);
		int Mapi_Phrase_Seek(int ch, int pos);
		int Mapi_Phrase_Play(int ch, int loop);
		int Mapi_Phrase_Stop(int ch);
		int Mapi_Phrase_Pause(int ch);
		int Mapi_Phrase_Restart(int ch);
		int Mapi_Phrase_Kill();
		void Mapi_Phrase_SetVolume(int ch, int vol);
		int Mapi_Phrase_GetVolume(int ch);
		void Mapi_Phrase_SetPanpot(int ch, int vol);
		int Mapi_Phrase_GetPanpot(int ch);
		int Mapi_Phrase_GetStatus(int ch);
		int Mapi_Phrase_GetPosition(int ch);
		int Mapi_Phrase_GetLength(int ch);
		int Mapi_Phrase_RemoveData(int ch);
		int Mapi_Phrase_SetLink(int ch, long slave);
		long Mapi_Phrase_GetLink(int ch);
		int Mapi_Phrase_SetEvHandler(Callback ev);
		// TODO Mapi_Phrase_Audio
	}

	// endregion JNA interfaces

	private final int mode;
	private final Library library;

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

	// region Sound

	private final Map<Integer, Memory> sounds = new HashMap<>();

	private int EmuBuf;
	private int instanceId = -1;

	public static final int STATE_READY = 3;
	public static final int STATE_PLAYING = 4;
	public static final int STATE_PAUSED = 5;

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
			Memory config = new Memory(72);
			config.clear();

			config.setInt(0, 2);
			config.setInt(4, 48000); // sample rate
			config.setByte(8, (byte) 1);
			config.setInt(12, 0);
			config.setInt(16, 15);
			config.setInt(20, 1);
			config.setInt(24, 1);
			config.setShort(28, (short) 120);
			config.setShort(30, (short) 120);
			config.setShort(32, (short) 120);
			config.setShort(34, (short) 120);
			config.setShort(36, (short) 240);
			config.setShort(38, (short) 240);
			config.setShort(40, (short) 240);
			config.setShort(42, (short) 240);
			config.setShort(44, (short) 12);
			config.setShort(46, (short) 12);
			config.setShort(48, (short) 12);
			config.setShort(50, (short) 12);
			config.setShort(52, (short) 16);
			config.setShort(54, (short) 16);
			config.setShort(56, (short) 24);
			config.setShort(58, (short) 24);
			config.setInt(60, 0);
			config.setInt(64, 4096);
			config.setByte(68, (byte) 1);

//			((MA7) library).Mapi_EmuGetInfo(3);

			r = ((MA7) library).Mapi_EmuInitialize(0, config, null, null, null, 0);
			if (r != 0) {
				throw new RuntimeException("Mapi_EmuInitialize: " + r);
			}

			r = ((MA7) library).Mapi_Initialize();
			if (r != 0) {
				throw new RuntimeException("Mapi_Initialize: " + r);
			}

			// unmute?
			r = ((MA7) library).Mapi_DeviceControlEx(0x10000, 0, null);
			if (r != 0) {
				throw new RuntimeException("Mapi_DeviceControlEx: " + r);
			}

//			Memory p = new Memory(2);
//			p.setByte(0, (byte) -10);
//			p.setByte(1, (byte) -10);
//			((MA7) library).Mapi_DeviceControlEx(65552, 0, p);
//			((MA7) library).Mapi_DeviceControlEx(65552, 1, p);
//			((MA7) library).Mapi_DeviceControlEx(65552, 2, p);

			r = ((MA7) library).MaSmw_Init();
			if (r != 0) {
				throw new RuntimeException("MaSmw_Init: " + r);
			}

			// phrases don't work without this
			r = ((MA7) library).Mapi_SetMode(2);
			if (r != 0) {
				throw new RuntimeException("Mapi_SetMode: " + r);
			}

			// regular initialization steps
			r = ((MA7) library).MaSound_Initialize();
			if (r != 0) {
				throw new RuntimeException("MaSound_Initialize: " + r);
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

		sounds.put(sound, ptr);

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
		Memory p = new Memory(4);
		p.setInt(0, volume);
		int r;
		if ((r = ((MaSound) library).MaSound_Control(instanceId, sound, 0, p, 0)) != 0) {
			throw new RuntimeException("MaSound_Control: " + r);
		}
	}

	// -12..+12
	public synchronized void setPitch(int sound, int pitch) {
		Memory p = new Memory(4);
		p.setInt(0, pitch);
		int r;
		if ((r = ((MaSound) library).MaSound_Control(instanceId, sound, 2, p, 0)) != 0) {
			throw new RuntimeException("MaSound_Control: " + r);
		}
	}

	// 70..130
	public synchronized void setTempo(int sound, int tempo) {
		Memory p = new Memory(4);
		p.setInt(0, tempo);
		int r;
		if ((r = ((MaSound) library).MaSound_Control(instanceId, sound, 1, p, 0)) != 0) {
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
		return ((MaSound) library).MaSound_Control(instanceId, sound, 6, Pointer.NULL, 0);
	}

	public synchronized int getPosition(int sound) {
		return ((MaSound) library).MaSound_Control(instanceId, sound, 4, Pointer.NULL, 0);
	}

	public synchronized void close(int sound) {
		((MaSound) library).MaSound_Stop(instanceId, sound, 0);
		((MaSound) library).MaSound_Close(instanceId, sound, 0);
		((MaSound) library).MaSound_Unload(instanceId, sound, 0);
		sounds.remove((Integer) sound);
	}

	public synchronized void destroy() {
		for (Integer sound : sounds.keySet()) {
			((MaSound) library).MaSound_Close(instanceId, sound, 0);
			((MaSound) library).MaSound_Unload(instanceId, sound, 0);
		}
		((MaSound) library).MaSound_Delete(instanceId);

		if (phraseInitialized) {
			phraseKill();
			phraseTerminate();
		}

		if (mode == MODE_MA5) {
			((MA5) library).MaSound_Terminate();
			((MA5) library).MaSound_EmuTerminate();
		} else if (mode == MODE_MA7) {
			((MA7) library).MaSmw_Term();
			((MA7) library).Mapi_Terminate();
			((MA7) library).Mapi_EmuTerminate();
		}

		if (EmuBuf != 0) {
			Native.free(EmuBuf);
			EmuBuf = 0;
		}
	}

	// endregion Sound

	// region Phrase

	private boolean phraseInitialized;
	private static PhrasePlayerImpl phrasePlayer;
	private final Map<Integer, Memory> phraseBuffers = new HashMap<>();
	@SuppressWarnings("FieldCanBeLocal")
	private PhraseEventCallback callback;

	public synchronized void phraseInitialize() {
		if (phraseInitialized) {
			return;
		}
		try {
			if (!sounds.isEmpty()) {
				for (Integer id : sounds.keySet()) {
					if (getStatus(id) == STATE_PLAYING) {
						throw new RuntimeException();
					}
					close(id);
				}
			}
		} catch (Exception ignored) {}

		int r;
		if (mode == MODE_MA7) {
			r = ((MA7) library).Mapi_SetMode(2);
			if (r != 0) {
				throw new RuntimeException("Mapi_SetMode: " + r);
			}
			phraseInitialized = true;
			if ((r = ((MA7) library).Mapi_Phrase_SetEvHandler(callback = new PhraseEventCallback())) != 0) {
				throw new RuntimeException("Phrase_SetEvHandler: " + r);
			}
			return;
		}

		r = ((Phrase) library).Phrase_Initialize();
		if (r != 0) {
			throw new RuntimeException("Phrase_Initialize: " + r);
		}
		phraseInitialized = true;

		if ((r = ((Phrase) library).Phrase_SetEvHandler(callback = new PhraseEventCallback())) != 0) {
			throw new RuntimeException("Phrase_SetEvHandler: " + r);
		}
	}

	public synchronized void phraseSetCallback(PhrasePlayerImpl player) {
		phrasePlayer = player;
	}

	public synchronized void phraseTerminate() {
		if (!phraseInitialized) {
			return;
		}
		if (mode == MODE_MA7) {
			return;
		}
		((Phrase) library).Phrase_Terminate();
		phraseInitialized = false;
	}


	public synchronized void phrasePlay(int ch, int loops) {
		int r;
		if (mode == MODE_MA7) {
			if ((r = ((MA7) library).Mapi_Phrase_Play(ch, loops)) != 0) {
				throw new RuntimeException("Mapi_Phrase_Play: " + r);
			}
			return;
		}
		if ((r = ((Phrase) library).Phrase_Play(ch, loops)) != 0) {
			throw new RuntimeException("Phrase_Play: " + r);
		}
	}

	public synchronized void phrasePause(int ch) {
		int r;
		if (mode == MODE_MA7) {
			if ((r = ((MA7) library).Mapi_Phrase_Pause(ch)) != 0) {
				throw new RuntimeException("Mapi_Phrase_Pause: " + r);
			}
			return;
		}
		if ((r = ((Phrase) library).Phrase_Pause(ch)) != 0) {
			throw new RuntimeException("Phrase_Pause: " + r);
		}
	}

	public synchronized void phraseRestart(int ch) {
		int r;
		if (mode == MODE_MA7) {
			if ((r = ((MA7) library).Mapi_Phrase_Restart(ch)) != 0) {
				throw new RuntimeException("Mapi_Phrase_Restart: " + r);
			}
			return;
		}
		if ((r = ((Phrase) library).Phrase_Restart(ch)) != 0) {
			throw new RuntimeException("Phrase_Restart: " + r);
		}
	}

	public synchronized void phraseStop(int ch) {
		int r;
		if (mode == MODE_MA7) {
			if ((r = ((MA7) library).Mapi_Phrase_Stop(ch)) != 0) {
				throw new RuntimeException("Mapi_Phrase_Stop: " + r);
			}
			return;
		}
		if ((r = ((Phrase) library).Phrase_Stop(ch)) != 0) {
			throw new RuntimeException("Phrase_Stop: " + r);
		}
	}

	public synchronized void phraseSetData(int ch, byte[] data) {
		phraseBuffers.remove(ch);

		int r;
		Memory ptr = new Memory(data.length);
		ptr.write(0, data, 0, data.length);

		// keep buffer from deallocation
		phraseBuffers.put(ch, ptr);

		if (mode == MODE_MA7) {
			if ((r = ((MA7) library).Mapi_Phrase_SetData(ch, ptr, data.length, 1)) != 0) {
				throw new RuntimeException("Phrase_SetData: " + r);
			}
			return;
		}

		if ((r = ((Phrase) library).Phrase_SetData(ch, ptr, data.length, 1)) != 0) {
			throw new RuntimeException("Phrase_SetData: " + r);
		}
	}

	public synchronized void phraseRemoveData(int ch) {
		int r;
		if (mode == MODE_MA7) {
			if ((r = ((MA7) library).Mapi_Phrase_RemoveData(ch)) != 0) {
				throw new RuntimeException("Mapi_Phrase_RemoveData: " + r);
			}
			return;
		};
		if ((r = ((Phrase) library).Phrase_RemoveData(ch)) != 0) {
			throw new RuntimeException("Phrase_RemoveData: " + r);
		}
		phraseBuffers.remove(ch);
	}

	public synchronized void phraseKill() {
		int r;
		if (mode == MODE_MA7) {
			if ((r = ((MA7) library).Mapi_Phrase_Kill()) != 0) {
				throw new RuntimeException("Mapi_Phrase_Kill: " + r);
			}
			return;
		}
		if ((r = ((Phrase) library).Phrase_Kill()) != 0) {
			throw new RuntimeException("Phrase_Kill: " + r);
		}
	}

	public synchronized void phraseSetLink(int ch, long slave) {
		int r;
		if (mode == MODE_MA7) {
			if ((r = ((MA7) library).Mapi_Phrase_SetLink(ch, slave)) != 0) {
				throw new RuntimeException("Mapi_Phrase_SetLink: " + r);
			}
			return;
		}
		if ((r = ((Phrase) library).Phrase_SetLink(ch, slave)) != 0) {
			throw new RuntimeException("Phrase_SetLink: " + r);
		}
	}

	public synchronized void phraseSetVolume(int ch, int volume) {
		if (mode == MODE_MA7) {
			((MA7) library).Mapi_Phrase_SetVolume(ch, volume);
			return;
		}
		((Phrase) library).Phrase_SetVolume(ch, volume);
	}

	public synchronized void phraseSetPanpot(int ch, int panpot) {
		if (mode == MODE_MA7) {
			((MA7) library).Mapi_Phrase_SetPanpot(ch, panpot);
			return;
		}
		((Phrase) library).Phrase_SetPanpot(ch, panpot);
	}

	public synchronized int phraseGetStatus(int ch) {
		if (mode == MODE_MA7) {
			return ((MA7) library).Mapi_Phrase_GetStatus(ch);
		}
		return ((Phrase) library).Phrase_GetStatus(ch);
	}

	public synchronized long phraseGetPosition(int ch) {
		if (mode == MODE_MA7) {
			return ((MA7) library).Mapi_Phrase_GetPosition(ch);
		}
		return ((Phrase) library).Phrase_GetPosition(ch);
	}

	public synchronized long phraseGetLength(int ch) {
		if (mode == MODE_MA7) {
			return ((MA7) library).Mapi_Phrase_GetLength(ch);
		}
		return ((Phrase) library).Phrase_GetLength(ch);
	}

	public synchronized void phraseSeek(int ch, int pos) {
		int r;
		if (mode == MODE_MA7) {
			if ((r = ((MA7) library).Mapi_Phrase_Seek(ch, pos)) != 0) {
				throw new RuntimeException("Mapi_Phrase_Seek: " + r);
			}
			return;
		}
		if ((r = ((Phrase) library).Phrase_Seek(ch, pos)) != 0) {
			throw new RuntimeException("Phrase_Seek: " + r);
		}
	}

	public synchronized int getMaxTracks() {
		// TODO
		return 4;
	}

	// endregion Phrase

}
