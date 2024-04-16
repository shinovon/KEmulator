package javax.microedition.m3g;

import emulator.graphics3D.m3g.M3GLoader;
import java.io.IOException;

public class Loader {
   public static Object3D[] load(String var0) throws IOException {
      return M3GLoader.loadObject3D(var0);
   }

   public static Object3D[] load(byte[] var0, int var1) throws IOException {
      return M3GLoader.loadObject3D(var0, var1);
   }
}
