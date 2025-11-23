package com.beanshogi.gui.listeners.event;

/**
 * Listener for sound events during gameplay.
 */
public interface SoundEventListener {
    
    /**
     * Called when a piece is moved or dropped.
     */
    void onPieceMove();
    
    /**
     * Called when a piece is captured.
     */
    default void onPieceCapture() {
        onPieceMove(); // Default to same sound
    }
    
    /**
     * Called when a check occurs.
     */
    default void onCheck() {}
    
    /**
     * Called when checkmate occurs.
     */
    default void onCheckmate() {}
}
