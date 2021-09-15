package javax.microedition.lcdui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import emulator.*;

public class TextBox2 extends Screen
{
	private JFrame frame;
	private JTextArea textArea;
	private int maxSize;
	private int constraints;
	
    public TextBox2(final String s, final String s2, final int n, final int n2) {
        super(s);
        frame = new JFrame(s);
        frame.setAlwaysOnTop(true);
        JScrollPane scrollPane = new JScrollPane();
        frame.getContentPane().add(scrollPane);
        
        textArea = new JTextArea();
        scrollPane.setViewportView(textArea);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        /*
          if(textArea.getText().length() > n) {
					textArea.setText(textArea.getText().substring(0, n));
				}
				char c = textArea.getText().toCharArray()[textArea.getText().length()-1];
				
				if(n2 == TextField.PHONENUMBER) {
					if(!(c >= '0' && c <= '9') && !(c == '+')) {
						textArea.setText(textArea.getText().substring(0, textArea.getText().length()-1));
					}
				}
         */
		if(n2 == TextField.UNEDITABLE) {
			textArea.setEditable(true);
		}
       textArea.addKeyListener(new KeyListener() {

		@Override
		public void keyPressed(KeyEvent arg0) {
			if(n2 == TextField.PHONENUMBER || n2 == TextField.NUMERIC) {
				if(!(arg0.getKeyChar() >= '0' && arg0.getKeyChar() <= '9') && !(arg0.getKeyChar() == '+') && !(arg0.getKeyChar() == '-')) {
					arg0.setKeyChar((char) 0);
				}
			}
			
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			if(n2 == TextField.PHONENUMBER || n2 == TextField.NUMERIC) {
				if(!(arg0.getKeyChar() >= '0' && arg0.getKeyChar() <= '9') && !(arg0.getKeyChar() == '+') && !(arg0.getKeyChar() == '-')) {
					arg0.setKeyChar((char) 0);
				}
			}
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			if(n2 == TextField.PHONENUMBER || n2 == TextField.NUMERIC) {
				if(!(arg0.getKeyChar() >= '0' && arg0.getKeyChar() <= '9') && !(arg0.getKeyChar() == '+') && !(arg0.getKeyChar() == '-')) {
					arg0.setKeyChar((char) 0);
				}
			}
			
		}
    	   
       });
    }

	@Override
	protected void paint(Graphics p0) {
		//frame.paint(frame.getGraphics());
	}

	@Override
    protected void defocus() {
		frame.setVisible(false);
    }

    protected void focusCaret() {
		frame.setVisible(true);
    }
    
    public String getString() {
        return textArea.getText();
    }
    
    public void setString(final String string) {
        textArea.setText(string);
    }
    
    public int getChars(final char[] array) {
        if (this.aString25 == null) {
            return 0;
        }
        final char[] charArray;
        System.arraycopy(charArray = textArea.getText().toCharArray(), 0, array, 0, charArray.length);
        return charArray.length;
    }
    
    public void setChars(final char[] array, final int n, final int n2) {
        final char[] array2 = new char[n2];
        System.arraycopy(array, n, array2, 0, n2);
        this.setString(new String(array2));
    }
    
    public void insert(final String s, final int n) {
        final String aString25 = this.aString25;
        this.setString(aString25.substring(0, n) + s + aString25.substring(n));
    }
    
    public void insert(final char[] array, final int n, final int n2, final int n3) {
        final char[] array2 = new char[n2];
        System.arraycopy(array, n, array2, 0, n2);
        this.insert(new String(array2), n3);
    }
    
    public void delete(final int n, final int n2) {
        final String aString25 = this.aString25;
        this.setString(aString25.substring(0, n) + aString25.substring(n + n2));
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    public int setMaxSize(final int maxSize) {
        return this.maxSize = maxSize;
    }
    
    public int size() {
        return this.textArea.getText().length();
    }
    
    public int getCaretPosition() {
    //    return this.aTextField1305.getCaretPosition();
    	return 0;
    }
    
    public void setConstraints(final int constraints) {
    	this.constraints = constraints;
    }
    
    public int getConstraints() {
    	return this.constraints;
      //  return this.aTextField1305.getConstraints();
    }
    
    public void setInitialInputMode(final String initialInputMode) {
       // this.aTextField1305.setInitialInputMode(initialInputMode);
    }
    
    public void setTitle(final String title) {
        super.setTitle(title);
    }
    
    public void setTicker(final Ticker ticker) {
        super.setTicker(ticker);
    }
    
	/*
    private TextField aTextField1305;
    
    public TextBox(final String s, final String s2, final int n, final int n2) {
        super(s);
        this.aTextField1305 = new TextField(null, s2, n, n2);
        super.aVector443.add(this.aTextField1305);
        this.aTextField1305.aScreen176 = this;
        this.aTextField1305.isTextBox = true;
        this.aTextField1305.aBoolean177 = true;
        this.aTextField1305.aBoolean18 = true;
        this.aTextField1305.aScreen176.setItemCommands(this.aTextField1305);
    }
    
    protected void foucsCaret() {
        this.aTextField1305.aScreen176.setItemCommands(this.aTextField1305);
        Emulator.getEmulator().getScreen().getCaret().foucsItem(this.aTextField1305, super.anIntArray21[0] + 4, Screen.anInt181 + 4);
    }
    
    public String getString() {
        return this.aTextField1305.getString();
    }
    
    public void setString(final String string) {
        this.aTextField1305.setString(string);
    }
    
    public int getChars(final char[] array) {
        return this.aTextField1305.getChars(array);
    }
    
    public void setChars(final char[] array, final int n, final int n2) {
        this.aTextField1305.setChars(array, n, n2);
    }
    
    public void insert(final String s, final int n) {
        this.aTextField1305.insert(s, n);
    }
    
    public void insert(final char[] array, final int n, final int n2, final int n3) {
        this.aTextField1305.insert(array, n, n2, n3);
    }
    
    public void delete(final int n, final int n2) {
        this.aTextField1305.delete(n, n2);
    }
    
    public int getMaxSize() {
        return this.aTextField1305.getMaxSize();
    }
    
    public int setMaxSize(final int maxSize) {
        return this.aTextField1305.setMaxSize(maxSize);
    }
    
    public int size() {
        return this.aTextField1305.size();
    }
    
    public int getCaretPosition() {
        return this.aTextField1305.getCaretPosition();
    }
    
    public void setConstraints(final int constraints) {
        this.aTextField1305.setConstraints(constraints);
    }
    
    public int getConstraints() {
        return this.aTextField1305.getConstraints();
    }
    
    public void setInitialInputMode(final String initialInputMode) {
        this.aTextField1305.setInitialInputMode(initialInputMode);
    }
    
    public void setTitle(final String title) {
        super.setTitle(title);
    }
    
    public void setTicker(final Ticker ticker) {
        super.setTicker(ticker);
    }
    
    protected void paint(final Graphics graphics) {
        this.layout();
        this.aTextField1305.paint(graphics);
    }
    
    protected void layout() {
        this.aTextField1305.layout();
        this.aTextField1305.anIntArray21[1] = Screen.anInt181;
    }
    */
}
