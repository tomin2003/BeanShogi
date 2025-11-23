package com.beanshogi.game;

import com.beanshogi.gui.panels.ControllerPanels;
import com.beanshogi.gui.panels.overlays.piece.PieceComponent;
import com.beanshogi.gui.panels.overlays.piece.PieceLayerPanel;
import com.beanshogi.gui.panels.overlays.piece.PieceSprites;
import com.beanshogi.model.Board;
import com.beanshogi.model.HandGrid;
import com.beanshogi.model.Piece;
import com.beanshogi.util.Sides;

import java.awt.image.BufferedImage;

/**
 * Handles rendering of the game board and hand panels.
 */
public class BoardRenderer {
    private final Board board;
    private final ControllerPanels panels;
    private final PieceSprites sprites;

    /**
     * Creates a new board renderer.
     * @param board the game board to render
     * @param panels the UI panels to render to
     */
    public BoardRenderer(Board board, ControllerPanels panels) {
        this.board = board;
        this.panels = panels;
        this.sprites = new PieceSprites();
    }

    /**
     * Renders the complete board state including all pieces on the board and in hands.
     */
    public void renderAll() {
        renderBoard();
        renderHands();
    }

    /**
     * Renders all pieces on the main game board.
     */
    public void renderBoard() {
        PieceLayerPanel boardPanel = panels.getBoardPanel();
        boardPanel.removeAll();
        for (Piece piece : board.getAllPieces()) {
            BufferedImage image = sprites.get(piece.getClass());
            PieceComponent comp = new PieceComponent(image, piece.getSide(), boardPanel.getCellSize());
            boardPanel.addPiece(comp, piece.getBoardPosition());
        }
        boardPanel.repaint();
    }

    /**
     * Renders both hand panels (top and bottom).
     */
    public void renderHands() {
        renderHandPanel(panels.getHandTopPanel(), board.getPlayer(Sides.GOTE).getHandGrid());
        renderHandPanel(panels.getHandBottomPanel(), board.getPlayer(Sides.SENTE).getHandGrid());
    }

    /**
     * Renders a specific hand panel with pieces from a hand grid.
     * @param handPanel the panel to render to
     * @param handGrid the hand grid containing pieces to render
     */
    private void renderHandPanel(PieceLayerPanel handPanel, HandGrid handGrid) {
        handPanel.removeAll();
        for (Piece piece : handGrid.getAllPieces()) {
            if (piece != null) {
                BufferedImage image = sprites.get(piece.getClass());
                PieceComponent comp = new PieceComponent(image, piece.getSide(), handPanel.getCellSize());
                handPanel.addPiece(comp, piece.getHandPosition());
            }
        }
        handPanel.repaint();
    }
}
