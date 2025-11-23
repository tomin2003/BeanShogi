package com.beanshogi.gui.listeners.event;

import com.beanshogi.core.game.Controller;

/**
 * Listener for history stack state and invoking undo and redo operations through UI.
 */
public interface UndoRedoListener {
    void gainController(Controller controller);
    void onUndoStackEmpty(boolean isEmpty);
    void onRedoStackEmpty(boolean isEmpty);
}
