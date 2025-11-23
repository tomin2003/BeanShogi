package com.beanshogi.gui.listeners.event;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.beanshogi.gui.util.SoundPlayer;

/**
 * Handles window closing events to perform cleanup tasks.
 * Stops background music when the window is closing.
 */
public class WindowCloseListener extends WindowAdapter {
    
    @Override
    public void windowClosing(WindowEvent e) {
        SoundPlayer.stopBackgroundMusic();
    }
}
