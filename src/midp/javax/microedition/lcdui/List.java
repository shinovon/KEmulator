package javax.microedition.lcdui;

public class List extends Screen implements Choice {
   public static final Command SELECT_COMMAND = new Command("Select", 1, 0);
   private ChoiceGroup choiceGroup;

   public List(String s, int n) {
      this(s, n, new String[0], new Image[0]);
   }

   public List(String s, int n, String[] array, Image[] array2) {
      super(s);
      if(n != 2 && n != 1 && n != 3) {
         throw new IllegalArgumentException();
      } else {
         this.choiceGroup = new ChoiceGroup((String)null, n, array, array2, true);
         super.items.add(this.choiceGroup);
         this.choiceGroup.screen = this;
         this.choiceGroup.aBoolean541 = true;
         this.choiceGroup.aBoolean177 = true;
         this.choiceGroup.focus();
         this.choiceGroup.aCommand540 = SELECT_COMMAND;
         super.addCommand(SELECT_COMMAND);
      }
   }

   public void setSelectCommand(Command aCommand540) {
      if(this.choiceGroup.choiceType == 3) {
         super.removeCommand(this.choiceGroup.aCommand540);
         super.addCommand(this.choiceGroup.aCommand540 = aCommand540);
      }
   }

   public int append(String s, Image image) {
      return this.choiceGroup.append(s, image);
   }

   public void delete(int n) {
      this.choiceGroup.delete(n);
   }

   public void deleteAll() {
      this.choiceGroup.deleteAll();
   }

   public int getFitPolicy() {
      return this.choiceGroup.getFitPolicy();
   }

   public Font getFont(int n) {
      return this.choiceGroup.getFont(n);
   }

   public Image getImage(int n) {
      return this.choiceGroup.getImage(n);
   }

   public int getSelectedFlags(boolean[] array) {
      return this.choiceGroup.getSelectedFlags(array);
   }

   public int getSelectedIndex() {
      return this.choiceGroup.getSelectedIndex();
   }

   public String getString(int n) {
      return this.choiceGroup.getString(n);
   }

   public void insert(int n, String s, Image image) {
      this.choiceGroup.insert(n, s, image);
   }

   public boolean isSelected(int n) {
      return this.choiceGroup.isSelected(n);
   }

   public void set(int n, String s, Image image) {
      this.choiceGroup.set(n, s, image);
   }

   public void setFitPolicy(int fitPolicy) {
      this.choiceGroup.setFitPolicy(fitPolicy);
   }

   public void setFont(int n, Font font) {
      this.choiceGroup.setFont(n, font);
   }

   public void setSelectedFlags(boolean[] selectedFlags) {
      this.choiceGroup.setSelectedFlags(selectedFlags);
   }

   public void setSelectedIndex(int n, boolean b) {
      this.choiceGroup.setSelectedIndex(n, b);
   }

   public int size() {
      return this.choiceGroup.size();
   }

   protected void paint(Graphics graphics) {
      this.layout();
      this.choiceGroup.paint(graphics);
   }

   protected void layout() {
      this.choiceGroup.layout();
      this.choiceGroup.anIntArray21[1] = Screen.fontHeight4;
   }
}
