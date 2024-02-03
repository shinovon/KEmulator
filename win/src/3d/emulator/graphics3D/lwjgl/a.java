package emulator.graphics3D.lwjgl;

import java.nio.*;
import org.lwjgl.*;

public final class a
{
    private static ByteBuffer aByteBuffer405;
    
    public a() {
        super();
    }
    
    public static ByteBuffer method198(final byte[] array) {
        if (a.aByteBuffer405 == null || a.aByteBuffer405.capacity() < array.length) {
            a.aByteBuffer405 = BufferUtils.createByteBuffer(array.length);
        }
        a.aByteBuffer405.position(a.aByteBuffer405.capacity() - array.length);
        a.aByteBuffer405.put(array);
        a.aByteBuffer405.position(a.aByteBuffer405.capacity() - array.length);
        return a.aByteBuffer405;
    }
}
