package com.sprintpcs.media;

import com.sprintpcs.media.Clip;
import com.sprintpcs.media.DualTone;
import com.sprintpcs.media.Player;
import com.sprintpcs.media.PlayerListener;
import java.io.PrintStream;
import javax.microedition.media.MediaException;

final class Class55
implements javax.microedition.media.PlayerListener {
    private void method365(javax.microedition.media.Player player) {
        try {
            player.addPlayerListener((javax.microedition.media.PlayerListener)this);
            return;
        }
        catch (Exception exception) {
            System.out.println("Exception in addPlayerListener");
            return;
        }
    }

    public final void playerUpdate(javax.microedition.media.Player player, String string, Object object) {
        block33 : {
            int n;
            PlayerListener playerListener;
            block35 : {
                block34 : {
                    block32 : {
                        if (Player.method313() == null && string != "closed") {
                            System.out.println("ERROR: currentPlayed is null!");
                        }
                        if (string != "started") break block32;
                        long l = Long.parseLong((String)object.toString());
                        if (l > 0L) {
                            if (Player.method314() != null) {
                                Player.method314().playerUpdate(6, Player.method313());
                            }
                        } else if (l == 0L) {
                            if (Player.method314() != null) {
                                Player.method314().playerUpdate(3, Player.method313());
                            }
                        } else {
                            System.out.println("com.sprintpcs.media.Player: media time is not supported");
                        }
                        break block33;
                    }
                    if (string != "stopped") break block33;
                    if (Player.method315() != 2 && Player.method315() != 5) break block34;
                    if (Player.method314() == null) break block33;
                    playerListener = Player.method314();
                    n = 5;
                    break block35;
                }
                if (Player.method314() == null) break block33;
                playerListener = Player.method314();
                n = 4;
            }
            playerListener.playerUpdate(n, Player.method313());
        }
        if (string != "endOfMedia") {
            if (string == "deviceUnavailable") {
                if (Player.method314() != null) {
                    Player.method314().playerUpdate(0, Player.method313());
                    return;
                }
            } else if (string == "error" && Player.method314() != null) {
                Player.method314().playerUpdate(2, Player.method313());
                return;
            }
        } else {
            if (Player.method314() != null) {
                Player.method314().playerUpdate(1, Player.method313());
            }
            if (player == Player.aPlayer907 ? Player.method319() > 0 || Player.method319() == -1 : (player == Player.aPlayer916 ? Player.method321() > 0 || Player.method321() == -1 : player == Player.aPlayer913)) {
                return;
            }
            switch (Player.method315()) {
                case 1: {
                    Player.aPlayer907.close();
                    Player.aPlayer907 = null;
                    Player.aClip909 = null;
                    Player.method316((Object)null);
                    Player.method317((int)0);
                    Player.method320((int)0);
                    if (Player.aPlayer913 == null) break;
                    Player.method320((int)0);
                    Player.method317((int)4);
                    Player.method316((Object)Player.aClip914);
                    try {
                        Player.aPlayer913.start();
                        return;
                    }
                    catch (IllegalStateException | MediaException mediaException) {
                        mediaException.printStackTrace();
                        return;
                    }
                }
                case 4: {
                    return;
                }
                case 6: {
                    Exception exception;
                    try {
                        Player.aPlayer916.close();
                        Player.aPlayer916 = null;
                        Player.aDualTone910 = null;
                        Player.method316((Object)null);
                        Player.method317((int)0);
                        Player.method320((int)0);
                        if (Player.aPlayer913 != null) {
                            Player.method320((int)0);
                            Player.method317((int)4);
                            Player.method316((Object)Player.aClip914);
                            Player.aPlayer913.start();
                            return;
                        }
                        return;
                    }
                    catch (MediaException mediaException) {
                        if (Player.method314() != null) {
                            Player.method314().playerUpdate(2, Player.method313());
                            System.out.println(new StringBuffer().append("play background clip encountered a MediaException: ").append(mediaException.getMessage()).toString());
                        }
                        exception = mediaException;
                    }
                    catch (Exception exception2) {
                        Exception exception3;
                        exception = exception3 = exception2;
                    }
                    exception.printStackTrace();
                }
            }
        }
    }

    Class55(PlayerListener playerListener) {
        Player.method318((PlayerListener)playerListener);
    }

    static void method366(Class55 class55, javax.microedition.media.Player player) {
        class55.method365(player);
    }
}
