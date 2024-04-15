package javax.microedition.m3g;

import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Group;

public class World extends Group {
   private Background aBackground874 = null;
   private Camera aCamera875 = null;

   public void setBackground(Background var1) {
      this.removeReference(this.aBackground874);
      this.aBackground874 = var1;
      this.addReference(this.aBackground874);
   }

   public Background getBackground() {
      return this.aBackground874;
   }

   public void setActiveCamera(Camera var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else {
         this.removeReference(this.aCamera875);
         this.aCamera875 = var1;
         this.addReference(this.aCamera875);
      }
   }

   public Camera getActiveCamera() {
      return this.aCamera875;
   }

   protected void updateAlignReferences() {
      super.updateAlignReferences();
      if(this.aCamera875 != null && this.aCamera875.m_duplicatedNode != null && this.aCamera875.m_duplicatedNode.isDescendantOf(super.m_duplicatedNode)) {
         ((World)super.m_duplicatedNode).aCamera875 = (Camera)this.aCamera875.m_duplicatedNode;
      }

   }
}
