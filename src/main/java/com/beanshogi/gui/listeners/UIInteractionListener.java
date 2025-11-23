package com.beanshogi.gui.listeners;

import com.beanshogi.model.Piece;
import com.beanshogi.util.Position;
import com.beanshogi.util.Sides;

import java.util.List;

/**
 * Listener for UI interactions that may require user input or display.
 */
public interface UIInteractionListener {
    
    /**
     * Request user decision on piece promotion.
     * @param piece the piece that can be promoted
     * @param from the starting position
     * @param to the destination position
     * @return true if the user wants to promote, false otherwise
     */
    boolean requestPromotionDecision(Piece piece, Position from, Position to);
    
    /**
     * Request which side should resign when multiple human players exist.
     * @param eligibleSides the sides that can resign
     * @return the side that chose to resign, or null if cancelled
     */
    Sides requestResignationSide(List<Sides> eligibleSides);
    
    /**
     * Display game over message.
     * @param winnerName the name of the winner
     * @param reason the reason for game end
     */
    void showGameOver(String winnerName, String reason);
    
    /**
     * Display game saved confirmation.
     * @param saveLocation the location where the game was saved
     */
    void showGameSaved(String saveLocation);
}
