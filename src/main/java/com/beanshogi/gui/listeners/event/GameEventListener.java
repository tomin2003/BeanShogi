package com.beanshogi.gui.listeners.event;

import com.beanshogi.core.board.Move;
import com.beanshogi.core.game.Sides;
import com.beanshogi.leaderboard.Entry;
import com.beanshogi.leaderboard.ResultType;

/**
 * Listener for game events such as moves, game end, and state changes.
 */
public interface GameEventListener {
    
    /**
     * Called when a move has been made.
     * @param move the move that was made
     * @param side the side that made the move
     */
    default void onMoveMade(Move move, Sides side) {}
    
    /**
     * Called when the game ends.
     * @param winner the winning side
     * @param reason the reason for game end
     * @param resultType the type of result
     * @param entry the leaderboard entry for this game
     */
    default void onGameEnd(Sides winner, String reason, ResultType resultType, Entry entry) {}
    
    /**
     * Called when a player resigns.
     * @param resigningSide the side that resigned
     */
    default void onResignation(Sides resigningSide) {}
    
    /**
     * Called when the game is started.
     */
    default void onGameStart() {}
}
