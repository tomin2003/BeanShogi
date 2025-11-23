package com.beanshogi.gui.listeners.event;

import com.beanshogi.core.game.Sides;

/**
 * Listener for game statistics updates such as turn changes and move count.
 */
public interface GameStatsListener {
    void onSideOnTurnChanged(Sides sideOnTurn);
    void onMoveCountChanged(int moveCount);
}
