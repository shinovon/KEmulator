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
         choiceGroup = new ChoiceGroup((String)null, n, array, array2, true);
         super.items.add(choiceGroup);
         choiceGroup.screen = this;
         choiceGroup.aBoolean541 = true;
         choiceGroup.aBoolean177 = true;
         choiceGroup.focus();
         choiceGroup.aCommand540 = SELECT_COMMAND;
         super.addCommand(SELECT_COMMAND);
      }
   }

   public void setSelectCommand(Command aCommand540) {
      if(choiceGroup.choiceType == 3) {
         super.removeCommand(choiceGroup.aCommand540);
         super.addCommand(choiceGroup.aCommand540 = aCommand540);
      }
   }

   public int append(String s, Image image) {
      return choiceGroup.append(s, image);
   }

   public void delete(int n) {
      choiceGroup.delete(n);
   }

   public void deleteAll() {
      choiceGroup.deleteAll();
   }

   public int getFitPolicy() {
      return choiceGroup.getFitPolicy();
   }

   public Font getFont(int n) {
      return choiceGroup.getFont(n);
   }

   public Image getImage(int n) {
      return choiceGroup.getImage(n);
   }

   public int getSelectedFlags(boolean[] array) {
      return choiceGroup.getSelectedFlags(array);
   }

   public int getSelectedIndex() {
      return choiceGroup.getSelectedIndex();
   }

   public String getString(int n) {
      return choiceGroup.getString(n);
   }

   public void insert(int n, String s, Image image) {
      choiceGroup.insert(n, s, image);
   }

   public boolean isSelected(int n) {
      return choiceGroup.isSelected(n);
   }

   public void set(int n, String s, Image image) {
      choiceGroup.set(n, s, image);
   }

   public void setFitPolicy(int fitPolicy) {
      choiceGroup.setFitPolicy(fitPolicy);
   }

   public void setFont(int n, Font font) {
      choiceGroup.setFont(n, font);
   }

   public void setSelectedFlags(boolean[] selectedFlags) {
      choiceGroup.setSelectedFlags(selectedFlags);
   }

   public void setSelectedIndex(int n, boolean b) {
      choiceGroup.setSelectedIndex(n, b);
   }

   public int size() {
      return choiceGroup.size();
   }

   @Override
   protected void drawScrollBar(final Graphics graphics) {
       emulator.lcdui.a.method179(graphics, bounds[W] + 1, Screen.fontHeight4 - 1, 2, bounds[H] - 2, choiceGroup.size(), (choiceGroup.getSelectedIndex() != 0) ? choiceGroup.getSelectedIndex() : -1);
   }

   protected void paint(Graphics graphics) {
      this.layout();
      choiceGroup.paint(graphics);
   }

   protected void layout() {
      choiceGroup.layout();
      choiceGroup.bounds[1] = Screen.fontHeight4;
   }
}
