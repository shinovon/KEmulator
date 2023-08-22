package emulator.ui.swt;

import emulator.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.*;

final class Class29 extends DropTargetAdapter
{
    Class29(final EmulatorScreen class93) {
        super();
    }
    
    public final void dragEnter(final DropTargetEvent dropTargetEvent) {
        if (dropTargetEvent.detail == 16) {
            dropTargetEvent.detail = (((dropTargetEvent.operations & 0x1) != 0x0) ? 1 : 0);
        }
    }
    
    public final void dragOver(final DropTargetEvent dropTargetEvent) {
        dropTargetEvent.feedback = 9;
    }
    
    public final void drop(final DropTargetEvent dropTargetEvent) {
        final String[] array;
        if (((ByteArrayTransfer)FileTransfer.getInstance()).isSupportedType(dropTargetEvent.currentDataType) && (array = (String[])((TypedEvent)dropTargetEvent).data).length > 0 && (array[0].endsWith(".jar") || array[0].endsWith(".jad"))) {
            Emulator.loadGame(array[0], Settings.g2d, 1, false);
        }
    }
}
