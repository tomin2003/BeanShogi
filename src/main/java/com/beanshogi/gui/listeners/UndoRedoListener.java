package com.beanshogi.gui.listeners;

public interface UndoRedoListener {
    void onUndoStackEmpty(boolean isEmpty);
    void onRedoStackEmpty(boolean isEmpty);
}
