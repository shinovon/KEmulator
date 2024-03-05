package javax.microedition.amms;

import javax.microedition.media.Controllable;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public abstract interface Module extends Controllable {
    public abstract void addMIDIChannel(Player paramPlayer, int paramInt) throws MediaException;

    public abstract void addPlayer(Player paramPlayer) throws MediaException;

    public abstract void removeMIDIChannel(Player paramPlayer, int paramInt);

    public abstract void removePlayer(Player paramPlayer);
}
