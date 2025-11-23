package com.beanshogi.gui.listeners;

import com.beanshogi.gui.util.Popups;
import com.beanshogi.model.Piece;
import com.beanshogi.util.Position;
import com.beanshogi.util.Sides;

import java.awt.Component;
import java.util.List;

/**
 * UI interaction listener that returns to main menu after game over.
 */
public class GameOverUIListener implements UIInteractionListener {
    
    private final Component parentComponent;
    private final Runnable onGameOverDismissed;
    
    /**
     * Creates a UI interaction listener with game over callback.
     * @param parentComponent the parent component for dialogs
     * @param onGameOverDismissed callback to run after game over popup is dismissed
     */
    public GameOverUIListener(Component parentComponent, Runnable onGameOverDismissed) {
        this.parentComponent = parentComponent;
        this.onGameOverDismissed = onGameOverDismissed;
    }
    
    @Override
    public boolean requestPromotionDecision(Piece piece, Position from, Position to) {
        return Popups.askPromotion(parentComponent);
    }
    
    @Override
    public Sides requestResignationSide(List<Sides> eligibleSides) {
        return eligibleSides != null && !eligibleSides.isEmpty() ? eligibleSides.get(0) : null;
    }
    
    @Override
    public void showGameOver(String winnerName, String reason) {
        Popups.showGameOver(parentComponent, winnerName, reason);
        // After user dismisses the popup, execute callback
        if (onGameOverDismissed != null) {
            onGameOverDismissed.run();
        }
    }
    
    @Override
    public void showGameSaved(String saveLocation) {
        Popups.showGameSaved(parentComponent, saveLocation);
    }
}
