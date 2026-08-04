package com.j_phone.io;

import com.jblend.media.MediaData;
import com.jblend.media.MediaFactory;
import com.jblend.media.MediaPlayer;
import com.jblend.media.smaf.SmafData;
import emulator.custom.ResourceManager;

import java.io.IOException;

public final class FileUtility {
	public static final int WRITABLE = 0;
	public static final int EXISTS = 1;
	public static final int INSUFFICIENT = 2;
	public static final int COUNT_LIMIT = 3;
	public static final int FILETYPE_DIFFERENT = 4;
	public static final int WRITE_PROTECT = 5;
	public static final int OTHER_ERROR = -1;

	public static FileUtility getInstance() {
		return new FileUtility();
	}

	public void play(String paramString)
			throws IOException {
		System.out.println("FileUtility.play " + paramString);
		play(ResourceManager.getBytes(paramString), 1);
	}

//    public void play(MailData paramMailData, int paramInt)
//            throws IOException {
//    }

	public void play(byte[] paramArrayOfByte, int paramInt)
			throws IOException {
		System.out.println("FileUtility.play bytes " + paramInt);
	}

    public MediaPlayer getMediaPlayer(String paramString)
            throws IOException {
        return MediaFactory.getMediaPlayer(paramString);
    }

    public MediaPlayer getMediaPlayer(String paramString, int paramInt)
            throws IOException {
		return MediaFactory.getMediaPlayer(paramString, paramInt);
    }

    public MediaData getMediaData(String paramString)
            throws IOException {
        return new SmafData(paramString);
    }

    public MediaData getMediaData(String paramString, int paramInt)
            throws IOException {
		return new SmafData(paramString);
    }

	public int getFreeSpace(String paramString)
			throws IOException {
		System.out.println("FileUtility.getFreeSpace " + paramString);
		return 0;
	}

	public int precheckStorable(String paramString, int paramInt) {
		System.out.println("FileUtility.precheckStorable " + paramString + " " + paramInt);
		return 0;
	}
}
