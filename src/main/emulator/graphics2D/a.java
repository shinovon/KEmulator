package emulator.graphics2D;

import java.awt.image.*;
import java.awt.datatransfer.*;
import java.io.*;

final class a implements Transferable {
	private BufferedImage aBufferedImage350;

	public a(final BufferedImage aBufferedImage350) {
		super();
		this.aBufferedImage350 = aBufferedImage350;
	}

	public final DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{DataFlavor.imageFlavor};
	}

	public final boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
		return DataFlavor.imageFlavor.equals(dataFlavor);
	}

	public final Object getTransferData(final DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
		if (!DataFlavor.imageFlavor.equals(dataFlavor)) {
			throw new UnsupportedFlavorException(dataFlavor);
		}
		return this.aBufferedImage350;
	}
}
