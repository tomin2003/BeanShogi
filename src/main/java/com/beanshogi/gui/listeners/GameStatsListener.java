package com.beanshogi.gui.listeners;

import com.beanshogi.util.Sides;

public interface GameStatsListener {
    void onSideOnTurnChanged(Sides sideOnTurn);
    void onMoveCountChanged(int moveCount);
}
