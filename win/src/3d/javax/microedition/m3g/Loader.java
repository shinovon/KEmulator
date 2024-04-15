package javax.microedition.m3g;

import emulator.graphics3D.m3g.j;
import java.io.IOException;
import javax.microedition.m3g.Object3D;

public class Loader {
   public static Object3D[] load(String var0) throws IOException {
      return j.method697(var0);
   }

   public static Object3D[] load(byte[] var0, int var1) throws IOException {
      return j.method698(var0, var1);
   }
}
