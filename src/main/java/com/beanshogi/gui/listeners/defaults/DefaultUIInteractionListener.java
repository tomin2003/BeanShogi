package com.beanshogi.gui.listeners.defaults;

import com.beanshogi.core.game.Sides;
import com.beanshogi.core.pieces.Piece;
import com.beanshogi.core.util.Position;
import com.beanshogi.gui.listeners.ui.UIInteractionListener;
import com.beanshogi.gui.util.Popups;

import java.awt.Component;
import java.util.List;

/**
 * Default implementation of UIInteractionListener that uses Popups utility.
 */
public class DefaultUIInteractionListener implements UIInteractionListener {
    
    private final Component parentComponent;
    
    public DefaultUIInteractionListener(Component parentComponent) {
        this.parentComponent = parentComponent;
    }
    
    @Override
    public boolean requestPromotionDecision(Piece piece, Position from, Position to) {
        return Popups.askPromotion(parentComponent);
    }
    
    @Override
    public Sides requestResignationSide(List<Sides> eligibleSides) {
        // For default implementation, just return the first eligible side if only one
        // For proper implementation, this should show a dialog
        return eligibleSides != null && !eligibleSides.isEmpty() ? eligibleSides.get(0) : null;
    }
    
    @Override
    public void showGameOver(String winnerName, String reason) {
        Popups.showGameOver(parentComponent, winnerName, reason);
    }
    
    @Override
    public void showGameSaved(String saveLocation) {
        Popups.showGameSaved(parentComponent, saveLocation);
    }
}
