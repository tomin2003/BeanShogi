package com.beanshogi.gui.listeners;

import com.beanshogi.util.Sides;

/**
 * Listener for game statistics updates such as turn changes and move count.
 */
public interface GameStatsListener {
    void onSideOnTurnChanged(Sides sideOnTurn);
    void onMoveCountChanged(int moveCount);
}
