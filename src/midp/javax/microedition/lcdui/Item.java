package javax.microedition.lcdui;

import emulator.Emulator;
import emulator.lcdui.a;
import java.util.Vector;

public abstract class Item {
   public static final int LAYOUT_DEFAULT = 0;
   public static final int LAYOUT_LEFT = 1;
   public static final int LAYOUT_RIGHT = 2;
   public static final int LAYOUT_CENTER = 3;
   public static final int LAYOUT_TOP = 16;
   public static final int LAYOUT_BOTTOM = 32;
   public static final int LAYOUT_VCENTER = 48;
   public static final int LAYOUT_NEWLINE_BEFORE = 256;
   public static final int LAYOUT_NEWLINE_AFTER = 512;
   public static final int LAYOUT_SHRINK = 1024;
   public static final int LAYOUT_EXPAND = 2048;
   public static final int LAYOUT_VSHRINK = 4096;
   public static final int LAYOUT_VEXPAND = 8192;
   public static final int LAYOUT_2 = 16384;
   public static final int PLAIN = 0;
   public static final int HYPERLINK = 1;
   public static final int BUTTON = 2;
   static final int anInt24 = 32563;
   static final Font aFont173 = Font.getFont(0, 1, 8);
   int[] anIntArray21;
   boolean aBoolean18;
   boolean aBoolean177;
   Command aCommand174;
   public ItemCommandListener itemCommandListener;
   public Vector itemCommands;
   String aString172;
   String[] aStringArray175;
   Screen aScreen176;
   int anInt178;
   int anInt180 = -1;
   int anInt181 = -1;
   int[] anIntArray179;
   int anInt182;

   Item(String aString172) {
      this.aString172 = aString172;
      this.aScreen176 = null;
      this.itemCommands = new Vector();
      this.anIntArray21 = new int[4];
   }

   public void setLabel(String aString172) {
      this.aString172 = aString172;
   }

   public String getLabel() {
      return this.aString172;
   }

   public int getLayout() {
      return this.anInt178;
   }

   public void setLayout(int anInt178) {
      if((anInt178 & ~anInt24) != 0) {
         throw new IllegalArgumentException();
      } else {
         this.anInt178 = anInt178;
      }
   }

   public void addCommand(Command command) {
      if(this.aScreen176 instanceof Alert) {
         throw new IllegalStateException();
      } else if(command == null) {
         throw new NullPointerException();
      } else if(!this.itemCommands.contains(command)) {
         int i;
         for(i = 0; i < this.itemCommands.size(); ++i) {
            Command command2 = (Command)this.itemCommands.get(i);
            if(command.getCommandType() > command2.getCommandType() || command.getCommandType() == command2.getCommandType() && command.getPriority() <= command2.getPriority()) {
               break;
            }
         }

         this.itemCommands.add(i, command);
         if(this.aScreen176 != null && Emulator.getCurrentDisplay().getCurrent() == this.aScreen176) {
            this.aScreen176.updateCommands();
         }

      }
   }

   public void removeCommand(Command command) {
      if(this.itemCommands.contains(command)) {
         if(command == this.aCommand174) {
            this.aCommand174 = null;
         }

         this.itemCommands.remove(command);
         if(this.aScreen176 != null && Emulator.getCurrentDisplay().getCurrent() == this.aScreen176) {
            this.aScreen176.updateCommands();
         }
      }

   }

   public void setItemCommandListener(ItemCommandListener itemCommandListener) {
      if(this.aScreen176 instanceof Alert) {
         throw new IllegalStateException();
      } else {
         this.itemCommandListener = itemCommandListener;
      }
   }

   protected void itemApplyCommand() {
      if(this.itemCommandListener != null && this.aCommand174 != null) {
         this.itemCommandListener.commandAction(this.aCommand174, this);
      }

   }

   public int getPreferredWidth() {
      return this.anInt180 != -1?this.anInt180:this.anIntArray21[2];
   }

   public int getPreferredHeight() {
      return this.anInt181 != -1?this.anInt181:this.anIntArray21[3];
   }

   public void setPreferredSize(int anInt180, int anInt181) {
      if(this.aScreen176 instanceof Alert) {
         throw new IllegalStateException();
      } else {
         this.anInt180 = anInt180;
         this.anInt181 = anInt181;
      }
   }

   public int getMinimumWidth() {
      return 0;
   }

   public int getMinimumHeight() {
      return 0;
   }

   public void setDefaultCommand(Command aCommand174) {
      if(this.aScreen176 instanceof Alert) {
         throw new IllegalStateException();
      } else {
         if((this.aCommand174 = aCommand174) != null) {
            this.addCommand(aCommand174);
         }

      }
   }

   public void notifyStateChanged() {
      if(!(this.aScreen176 instanceof Form)) {
         throw new IllegalStateException();
      } else {
         ((Form)this.aScreen176).anItemStateListener858.itemStateChanged(this);
      }
   }

   protected void focus() {
      this.aBoolean18 = true;
      if(this.aScreen176 != null) {
         this.aScreen176.setItemCommands(this);
      }

   }

   protected void defocus() {
      this.aBoolean18 = false;
      if(this.aScreen176 != null) {
         this.aScreen176.removeItemCommands(this);
      }

   }

   protected void paint(Graphics graphics) {
      graphics.setColor(-16777216);
      if(this.aBoolean18) {
         a.method178(graphics, this.anIntArray21[0], this.anIntArray21[1], this.anIntArray21[2], this.anIntArray21[3]);
      }

   }

   protected void layout() {
      this.anIntArray21[0] = 0;
      this.anIntArray21[1] = 0;
      this.anIntArray21[2] = this.aScreen176.anIntArray21[2];
      this.anIntArray21[3] = Screen.anInt181;
   }

   protected int getcurPage() {
      return this.anIntArray179 != null && this.anIntArray179.length > 0?this.anIntArray179[this.anInt182]:0;
   }

   protected boolean scrollUp() {
      if(this.anIntArray179 != null && this.anIntArray179.length > 0 && this.anInt182 > 0) {
         --this.anInt182;
         return false;
      } else {
         return true;
      }
   }

   protected boolean scrollDown() {
      if(this.anIntArray179 != null && this.anIntArray179.length > 0 && this.anInt182 < this.anIntArray179.length - 1) {
         ++this.anInt182;
         return false;
      } else {
         return true;
      }
   }

   protected void pointerPressed(int n, int n2) {
   }
}
