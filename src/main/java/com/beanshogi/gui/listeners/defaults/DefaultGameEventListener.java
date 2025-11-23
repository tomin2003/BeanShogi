package com.beanshogi.gui.listeners.defaults;

import com.beanshogi.core.game.Sides;
import com.beanshogi.gui.listeners.event.GameEventListener;
import com.beanshogi.io.LeaderboardSaveLoad;
import com.beanshogi.leaderboard.Entry;
import com.beanshogi.leaderboard.ResultType;

/**
 * Default implementation of GameEventListener that saves to leaderboard.
 */
public class DefaultGameEventListener implements GameEventListener {
    
    @Override
    public void onGameEnd(Sides winner, String reason, ResultType resultType, Entry entry) {
        LeaderboardSaveLoad.appendEntry(entry);
    }
}
