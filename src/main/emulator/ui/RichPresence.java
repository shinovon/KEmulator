package emulator.ui;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import emulator.Emulator;
import emulator.Settings;

public class RichPresence {

	protected static Object rpc;
	private static Thread rpcCallbackThread;
	public static long rpcStartTimestamp;
	public static long rpcEndTimestamp;
	public static String rpcState;
	public static String rpcDetails;
	public static int rpcPartySize;
	public static int rpcPartyMax;

	public static void initRichPresence() {
		if (!Settings.rpc)
			return;
		final DiscordRPC rpc = (DiscordRPC) (RichPresence.rpc = DiscordRPC.INSTANCE);
		DiscordEventHandlers handlers = new DiscordEventHandlers();
//        handlers.ready = new DiscordEventHandlers.OnReady() {
//            public void accept(DiscordUser user) {}
//        };
		rpc.Discord_Initialize("823522436444192818", handlers, true, "");
		DiscordRichPresence presence = new DiscordRichPresence();
		presence.startTimestamp = rpcStartTimestamp = System.currentTimeMillis() / 1000;
		presence.state = "No MIDlet loaded";
		rpc.Discord_UpdatePresence(presence);
		rpcCallbackThread = new Thread("KEmulator RPC-Callback-Handler") {
			public void run() {
				while (true) {
					rpc.Discord_RunCallbacks();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		rpcCallbackThread.start();
	}

	public static void updatePresence() {
		if (rpc == null)
			return;
		DiscordRPC rpc = (DiscordRPC) RichPresence.rpc;
		DiscordRichPresence presence = new DiscordRichPresence();
		presence.state = rpcState;
		presence.details = rpcDetails;
		presence.startTimestamp = rpcStartTimestamp;
		presence.endTimestamp = rpcEndTimestamp;
		presence.partySize = rpcPartySize;
		presence.partyMax = rpcPartyMax;
		rpc.Discord_UpdatePresence(presence);
	}

	public static void close() {
		if (rpcCallbackThread != null)
			rpcCallbackThread.interrupt();
	}
}
