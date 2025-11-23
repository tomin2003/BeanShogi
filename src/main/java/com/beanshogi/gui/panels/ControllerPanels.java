package com.beanshogi.gui.panels;

import com.beanshogi.core.game.Sides;
import com.beanshogi.gui.render.HighlightLayerPanel;
import com.beanshogi.gui.render.piece.PieceLayerPanel;

/**
 * Aggregator class for handling panels used in the controller.
 */
public class ControllerPanels {
    private final HighlightLayerPanel boardHighlight;
    private final HighlightLayerPanel boardSelectionHighlight;
    private final HighlightLayerPanel handTopHighlight;
    private final HighlightLayerPanel handBottomHighlight;
    private final PieceLayerPanel boardPanel;
    private final PieceLayerPanel handTopPanel;
    private final PieceLayerPanel handBottomPanel;

    public ControllerPanels(HighlightLayerPanel boardHighlight, HighlightLayerPanel boardSelectionHighlight, HighlightLayerPanel handTopHighlight, HighlightLayerPanel handBottomHighlight,
                                                     PieceLayerPanel boardPanel, PieceLayerPanel handTopPanel, PieceLayerPanel handBottomPanel) {
        this.boardHighlight = boardHighlight;
        this.boardSelectionHighlight = boardSelectionHighlight;
        this.handTopHighlight = handTopHighlight;
        this.handBottomHighlight = handBottomHighlight;
        this.boardPanel = boardPanel;
        this.handTopPanel = handTopPanel;
        this.handBottomPanel = handBottomPanel;
    }

    public HighlightLayerPanel getBoardHighlight() {
        return boardHighlight;
    }

    public HighlightLayerPanel getBoardSelectionHighlight() {
        return boardSelectionHighlight;
    }

    public HighlightLayerPanel getHandTopHighlight() {
        return handTopHighlight;
    }

    public HighlightLayerPanel getHandBottomHighlight() {
        return handBottomHighlight;
    }

    public HighlightLayerPanel getHandHighlight(Sides side) {
        return side == Sides.GOTE ? handTopHighlight : handBottomHighlight;
    }

    public PieceLayerPanel getBoardPanel() {
        return boardPanel;
    }

    public PieceLayerPanel getHandTopPanel() {
        return handTopPanel;
    }

    public PieceLayerPanel getHandBottomPanel() {
        return handBottomPanel;
    }

    public PieceLayerPanel getHandPanel(Sides side) {
        return side == Sides.GOTE ? handTopPanel : handBottomPanel;
    }

    public void clearAllHighlights() {
        boardHighlight.clearAllHighlights();
        boardSelectionHighlight.clearAllHighlights();
        handTopHighlight.clearAllHighlights();
        handBottomHighlight.clearAllHighlights();
    }
}
