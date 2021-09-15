package javax.microedition.lcdui;

public class List extends Screen implements Choice {
   public static final Command SELECT_COMMAND = new Command("Select", 1, 0);
   private ChoiceGroup aChoiceGroup1304;

   public List(String s, int n) {
      this(s, n, new String[0], new Image[0]);
   }

   public List(String s, int n, String[] array, Image[] array2) {
      super(s);
      if(n != 2 && n != 1 && n != 3) {
         throw new IllegalArgumentException();
      } else {
         this.aChoiceGroup1304 = new ChoiceGroup((String)null, n, array, array2, true);
         super.aVector443.add(this.aChoiceGroup1304);
         this.aChoiceGroup1304.aScreen176 = this;
         this.aChoiceGroup1304.aBoolean541 = true;
         this.aChoiceGroup1304.aBoolean177 = true;
         this.aChoiceGroup1304.focus();
         this.aChoiceGroup1304.aCommand540 = SELECT_COMMAND;
         super.addCommand(SELECT_COMMAND);
      }
   }

   public void setSelectCommand(Command aCommand540) {
      if(this.aChoiceGroup1304.anInt349 == 3) {
         super.removeCommand(this.aChoiceGroup1304.aCommand540);
         super.addCommand(this.aChoiceGroup1304.aCommand540 = aCommand540);
      }
   }

   public int append(String s, Image image) {
      return this.aChoiceGroup1304.append(s, image);
   }

   public void delete(int n) {
      this.aChoiceGroup1304.delete(n);
   }

   public void deleteAll() {
      this.aChoiceGroup1304.deleteAll();
   }

   public int getFitPolicy() {
      return this.aChoiceGroup1304.getFitPolicy();
   }

   public Font getFont(int n) {
      return this.aChoiceGroup1304.getFont(n);
   }

   public Image getImage(int n) {
      return this.aChoiceGroup1304.getImage(n);
   }

   public int getSelectedFlags(boolean[] array) {
      return this.aChoiceGroup1304.getSelectedFlags(array);
   }

   public int getSelectedIndex() {
      return this.aChoiceGroup1304.getSelectedIndex();
   }

   public String getString(int n) {
      return this.aChoiceGroup1304.getString(n);
   }

   public void insert(int n, String s, Image image) {
      this.aChoiceGroup1304.insert(n, s, image);
   }

   public boolean isSelected(int n) {
      return this.aChoiceGroup1304.isSelected(n);
   }

   public void set(int n, String s, Image image) {
      this.aChoiceGroup1304.set(n, s, image);
   }

   public void setFitPolicy(int fitPolicy) {
      this.aChoiceGroup1304.setFitPolicy(fitPolicy);
   }

   public void setFont(int n, Font font) {
      this.aChoiceGroup1304.setFont(n, font);
   }

   public void setSelectedFlags(boolean[] selectedFlags) {
      this.aChoiceGroup1304.setSelectedFlags(selectedFlags);
   }

   public void setSelectedIndex(int n, boolean b) {
      this.aChoiceGroup1304.setSelectedIndex(n, b);
   }

   public int size() {
      return this.aChoiceGroup1304.size();
   }

   protected void paint(Graphics graphics) {
      this.layout();
      this.aChoiceGroup1304.paint(graphics);
   }

   protected void layout() {
      this.aChoiceGroup1304.layout();
      this.aChoiceGroup1304.anIntArray21[1] = Screen.anInt181;
   }
}
