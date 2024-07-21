package ru.nnproject.kemulator.windowapi;

import emulator.graphics2D.awt.ImageAWT;

import javax.microedition.lcdui.Canvas;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public final class SecondaryWindow {

    JFrame frame;
    CanvasImpl awtCanvas;
    Canvas listener;
    private ImageAWT screenBuffer;

    private SecondaryWindow(String title, int w, int h) {
        frame = new JFrame();
        frame.setTitle(title);
        awtCanvas = new CanvasImpl();
        awtCanvas.addKeyListener(awtCanvas);
        awtCanvas.addMouseListener(awtCanvas);
        awtCanvas.addComponentListener(awtCanvas);
        awtCanvas.addMouseMotionListener(awtCanvas);
        awtCanvas.setDoubleBuffered(true);
        frame.add(awtCanvas);
        frame.addWindowListener(awtCanvas);
        frame.setBounds(100, 100, w, h);
    }

    // hidden, resizable by default
    public static SecondaryWindow create(String title, int w, int h) {
        return new SecondaryWindow(title, w, h);
    }

    public int getWidth() {
        return awtCanvas.getWidth();
    }

    public int getHeight() {
        return awtCanvas.getHeight();
    }

    public void setBounds(int x, int y, int w, int h) {
        frame.setBounds(x, y, w, h);
    }

    public void setResizable(boolean b) {
        frame.setResizable(b);
    }

    public void setAlwaysOnTop(boolean b) {
        frame.setAlwaysOnTop(b);
    }

    public void setTitle(String s) {
        frame.setTitle(s);
    }

    public void setVisible(boolean b) {
        frame.setVisible(b);
        if (b) frame.repaint();
    }

    public void repaint() {
        awtCanvas.repaint();
    }

    // paint(graphics), key events, pointer events, sizeChanged
    public void setListener(Canvas c) {
        this.listener = c;
        if (c != null) {
            c.setFullScreenMode(true);
            awtCanvas.repaint();
        }
    }

    private class CanvasImpl extends JPanel implements KeyListener, MouseListener, MouseMotionListener, ComponentListener, WindowListener {

        public void paintComponent(java.awt.Graphics g) {
            if (screenBuffer == null ||
                    screenBuffer.getWidth() != getWidth() ||
                    screenBuffer.getHeight() != getHeight())
                screenBuffer = new ImageAWT(new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB));
            if (listener != null)
                listener.invokePaint(screenBuffer,null);
            g.drawImage(screenBuffer.getBufferedImage(), 0, 0, null);
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if (listener != null) listener.invokeKeyPressed(e.getKeyCode());
        }

        public void keyReleased(KeyEvent e) {
            if (listener != null) listener.invokeKeyReleased(e.getKeyCode());
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            if (listener != null) listener.invokePointerPressed(e.getX(), e.getY());
        }

        public void mouseReleased(MouseEvent e) {
            if (listener != null) listener.invokePointerReleased(e.getX(), e.getY());
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void componentResized(ComponentEvent e) {
            if (listener != null) listener.invokeSizeChanged(getWidth(), getHeight());
        }

        public void componentMoved(ComponentEvent e) {
            if (listener != null) listener.invokeSizeChanged(getWidth(), getHeight());
        }

        public void componentShown(ComponentEvent e) {
        }

        public void componentHidden(ComponentEvent e) {
        }

        public void mouseDragged(MouseEvent e) {

        }

        public void mouseMoved(MouseEvent e) {
            if (listener != null) listener.invokePointerDragged(e.getX(), e.getY());
        }

        public void windowOpened(WindowEvent e) {
            if (listener != null) listener.invokeShowNotify();
        }

        public void windowClosing(WindowEvent e) {
            if (listener != null) listener.invokeHideNotify();
        }

        public void windowClosed(WindowEvent e) {
        }

        public void windowIconified(WindowEvent e) {

        }

        public void windowDeiconified(WindowEvent e) {

        }

        public void windowActivated(WindowEvent e) {

        }

        public void windowDeactivated(WindowEvent e) {

        }
    }

}
