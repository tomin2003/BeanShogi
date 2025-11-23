package com.beanshogi.gui.listeners;

import com.beanshogi.io.LeaderboardSaveLoad;
import com.beanshogi.leaderboard.Entry;
import com.beanshogi.leaderboard.ResultType;
import com.beanshogi.util.Sides;

/**
 * Default implementation of GameEventListener that saves to leaderboard.
 */
public class DefaultGameEventListener implements GameEventListener {
    
    @Override
    public void onGameEnd(Sides winner, String reason, ResultType resultType, Entry entry) {
        LeaderboardSaveLoad.appendEntry(entry);
    }
}
