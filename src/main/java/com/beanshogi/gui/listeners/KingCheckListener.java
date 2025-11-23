package com.beanshogi.gui.listeners;

import java.util.List;

import com.beanshogi.model.CheckEvent;

/**
 * Listener for king check events.
 */
public interface KingCheckListener {
    void onKingInCheck(List<CheckEvent> checkEvents);
}
