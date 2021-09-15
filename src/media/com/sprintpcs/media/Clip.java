package com.sprintpcs.media;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerImpl;

public class Clip {
   protected int priority;
   protected int vibration;
   private String aString205 = null;
   private String aString207 = null;
   private byte[] aByteArray206 = null;

   public Clip(String var1, String var2, int var3, int var4) throws IOException {
      this.aString205 = var1;
      this.aString207 = var2;
      this.priority = var3;
      this.vibration = var4;
   }

   public Clip(byte[] var1, String var2, int var3, int var4) throws IOException {
      this.aByteArray206 = var1;
      this.aString207 = var2;
      this.priority = var3;
      this.vibration = var4;
   }

   public String getContentType() {
      return this.aString207;
   }

   final Player method112() {
      ByteArrayInputStream var1 = null;
      Player var2 = null;

      try {
         label33: {
            Player var5;
            label32: {
               Object var10000;
               if(this.aString205 != null) {
                  if(this.aString205.indexOf(58) == -1) {
                     var5 = Manager.createPlayer(this.aString205);
                     break label32;
                  }

                  var10000 = ((InputConnection)Connector.open(this.aString205)).openInputStream();
               } else {
                  if(this.aByteArray206 == null) {
                     break label33;
                  }

                  var10000 = var1 = new ByteArrayInputStream(this.aByteArray206);
               }

               var5 = Manager.createPlayer((InputStream)var10000, this.aString207);
            }

            var2 = var5;
         }

         if(var2 != null) {
            var2.prefetch();
         }

         if(var1 != null) {
            var1.close();
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return var2;
   }

   public String getStateString() {
      String var1;
      Player var2;
      label20: {
         var1 = "Inactive";
         var2 = null;
         Player var10000;
         if(com.sprintpcs.media.Player.aClip909 == this) {
            var1 = "Active Clip: ";
            var10000 = com.sprintpcs.media.Player.aPlayer907;
         } else {
            if(com.sprintpcs.media.Player.aClip914 != this) {
               break label20;
            }

            var1 = "Background Clip: ";
            var10000 = com.sprintpcs.media.Player.aPlayer913;
         }

         var2 = var10000;
      }

      if(var2 != null && var2 instanceof PlayerImpl) {
         var1 = var1 + ((PlayerImpl)var2).getState();
      }

      return var1;
   }
}
