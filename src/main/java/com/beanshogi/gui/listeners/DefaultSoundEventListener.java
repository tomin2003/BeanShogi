package com.beanshogi.gui.listeners;

import com.beanshogi.gui.util.SoundPlayer;

/**
 * Default implementation of SoundEventListener that uses SoundPlayer utility.
 */
public class DefaultSoundEventListener implements SoundEventListener {
    
    @Override
    public void onPieceMove() {
        SoundPlayer.playPieceSfx();
    }
}
