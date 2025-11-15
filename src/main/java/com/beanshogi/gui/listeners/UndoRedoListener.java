package com.beanshogi.gui.listeners;

import com.beanshogi.game.Controller;

public interface UndoRedoListener {
    void gainController(Controller controller);
    void onUndoStackEmpty(boolean isEmpty);
    void onRedoStackEmpty(boolean isEmpty);
}
