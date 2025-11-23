package com.beanshogi.gui.listeners.event;

import java.util.List;

import com.beanshogi.core.game.CheckEvent;

/**
 * Listener for king check events.
 */
public interface KingCheckListener {
    void onKingInCheck(List<CheckEvent> checkEvents);
}
