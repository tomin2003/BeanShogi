package com.beanshogi.gui.listeners.defaults;

import com.beanshogi.gui.listeners.event.SoundEventListener;
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
