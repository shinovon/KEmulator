package emulator.media.vlc;

import javax.imageio.ImageIO;
import javax.microedition.media.MediaException;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class VideoControlImpl implements VideoControl {

    private final VLCPlayerImpl player;

    VideoControlImpl(VLCPlayerImpl player) {
        this.player = player;
    }

    public int getDisplayHeight() {
        return player.width;
    }

    public int getDisplayWidth() {
        return player.height;
    }

    public int getDisplayX() {
        return player.displayX;
    }

    public int getDisplayY() {
        return player.displayY;
    }

    public void setDisplayFullScreen(boolean b) {
        player.fullscreen = b;
    }

    public void setDisplayLocation(int x, int y) {
        player.displayX = x;
        player.displayY = y;
    }

    public void setDisplaySize(int w, int h) {
        if(player.width == w && player.height == h) return;
        player.width = w;
        player.height = h;
        player.notifyListeners(PlayerListener.SIZE_CHANGED, this);
    }

    public void setVisible(boolean b) {
        player.visible = b;
    }

    public int getSourceHeight() {
        if (player.released || player.mediaPlayer == null) {
            throw new IllegalStateException();
        }
        try {
            if (player.mediaPlayer.video().videoDimension() == null) {
                player.mediaPlayer.media().parsing().parse();
                Thread.sleep(100L);
            }
            player.sourceHeight = player.mediaPlayer.video().videoDimension().height;
        } catch (Exception e) {}
        if (player.sourceHeight == 0)
            player.sourceHeight = player.bufferHeight;
        return player.sourceHeight;
    }

    public int getSourceWidth() {
        if (player.released || player.mediaPlayer == null) {
            throw new IllegalStateException();
        }
        try {
            if (player.mediaPlayer.video().videoDimension() == null) {
                player.mediaPlayer.media().parsing().parse();
                Thread.sleep(100L);
            }
            player.sourceWidth = player.mediaPlayer.video().videoDimension().width;
        } catch (Exception e) {}
        if (player.sourceWidth == 0)
            player.sourceWidth = player.bufferWidth;
        return player.sourceWidth;
    }

    public byte[] getSnapshot(String p0) throws MediaException {
        if (player.released || player.mediaPlayer == null)
            throw new IllegalStateException();
        if (player.img != null) {
            return VLCPlayerImpl.imgToBytes(player.img);
        }
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(player.mediaPlayer.snapshots().get(), "JPEG", os);
            byte[] bytes = os.toByteArray();
            os.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            throw new MediaException(e);
        }
    }


    public Object initDisplayMode(int p0, Object p1) {
        if (p0 == 0) {
            player.isItem = true;
            return player.getItem();
        }
        player.canvas = p1;
        return null;
    }
}