package emulator.graphics3D.m3g;

import java.io.IOException;
import java.io.InputStream;

public class b extends InputStream {
   private InputStream anInputStream1154;
   private int anInt1093;

   public int read() throws IOException {
      ++this.anInt1093;
      return this.anInputStream1154.read();
   }

   public final void method805() {
      this.anInt1093 = 0;
   }

   public final int method806() {
      return this.anInt1093;
   }

   public int available() throws IOException {
      return this.anInputStream1154.available();
   }

   public void close() throws IOException {
      this.anInputStream1154.close();
   }

   public b(InputStream var1) {
      this.anInputStream1154 = var1;
      this.method805();
   }
}
