package com.travel;

import com.travel.gui.WebDesktopFrame;

import javax.swing.SwingUtilities;

/** Starts the shared HTML/CSS interface embedded in the existing Swing application. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WebDesktopFrame frame = new WebDesktopFrame();
            frame.setVisible(true);
        });
    }
}
