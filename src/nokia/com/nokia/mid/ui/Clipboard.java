package com.nokia.mid.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public abstract class Clipboard {
    public static void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    public static String copyFromClipboard() {
        String text = null;

        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = clipboard.getContents(null);
        try {
            if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                text = (String) t.getTransferData(DataFlavor.stringFlavor);
                System.out.println("Clipboard contents: " + text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (text == null) {
            text = "";
        }
        return text;
    }
}
