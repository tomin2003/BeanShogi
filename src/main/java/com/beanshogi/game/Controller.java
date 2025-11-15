package com.beanshogi.game;

import com.beanshogi.gui.listeners.*;
import com.beanshogi.gui.panels.*;
import com.beanshogi.gui.piece.*;
import com.beanshogi.gui.utils.*;
import com.beanshogi.model.*;
import com.beanshogi.util.*;

import java.awt.image.BufferedImage;
import java.util.List;

public class Controller {

    private final Game game;
    private final Board board;

    private final HighlightLayerPanel highlightLayer;
    private final PieceLayerPanel pieceLayer;
    private final PieceSprites sprites = new PieceSprites();

    private final int cellSize;
    private final int gap = 2;

    private GameStatsListener statsListener;
    private UndoRedoListener undoRedoListener;
    private KingCheckListener kingCheckListener;

    private Sides sideOnTurn = Sides.SENTE;
    private Piece selectedPiece = null;
    private List<Position> selectedLegalMoves = null;

    public Controller(GameStatsListener gsl, UndoRedoListener url, KingCheckListener kcl, HighlightLayerPanel hl, PieceLayerPanel pl, int cellSize) {
        this.game = new Game();
        this.board = game.getBoard();

        this.highlightLayer = hl;
        this.pieceLayer = pl;

        this.cellSize = cellSize;

        this.statsListener = gsl;
        this.undoRedoListener = url;
        this.kingCheckListener = kcl;
        
        // Initialize the statsListener with the starting default side (Sente always starts first)
        statsListener.onSideOnTurnChanged(sideOnTurn);

        // Attach stateless board mouse listener
        hl.addMouseListener(new BoardMouseListener(this::handleClick, cellSize, gap));
    }

    /**
     * Provide listeners with changed states
     */
    private void notifyListeners() {
        undoRedoListener.gainController(this);
        undoRedoListener.onUndoStackEmpty(board.moveManager.isUndoStackEmpty());
        undoRedoListener.onRedoStackEmpty(board.moveManager.isRedoStackEmpty());

        statsListener.onMoveCountChanged(board.moveManager.getNoOfMoves());
        statsListener.onSideOnTurnChanged(sideOnTurn);

        kingCheckListener.onKingInCheck(board.evals.kingChecks());
    }

    /**
     * Render actual board state
     */
    public void renderBoard() {
        pieceLayer.removeAll();
        // Draw out all the present pieces on the board
        for (Piece piece : board.getAllPieces()) {
            BufferedImage image = sprites.get(piece.getClass());
            PieceComponent comp = new PieceComponent(image, piece.getSide(), cellSize);
            pieceLayer.addPiece(comp, piece.getPosition().y, piece.getPosition().x, cellSize);
        }
        pieceLayer.repaint();
    }
    
    /**
     * Handle GUI and controller "defaulting" tasks after each move is made
     */
    private void afterEach() {
        highlightLayer.clearAllHighlights();
        if (!selectedLegalMoves.isEmpty()) {
            selectedLegalMoves.clear();
        }
        selectedPiece = null;
        sideOnTurn = sideOnTurn.getOpposite();
        notifyListeners();
        renderBoard();
    }

    public void undoMove() {
        if (board.moveManager.isUndoStackEmpty()) {
            return;
        }
        board.moveManager.undoMove();
        afterEach();
    }

    public void redoMove() {
        if (board.moveManager.isRedoStackEmpty()) {
            return;
        }
        board.moveManager.redoMove();
        afterEach();
    }

    private void handleClick(Position clickPosition) {
        Piece piece = board.getPiece(clickPosition);

        // Initial selection of piece
        if (piece != null && piece.getSide() == sideOnTurn) {
            selectedPiece = piece;
            highlightLayer.clearAllHighlights();
            selectedLegalMoves = selectedPiece.getLegalMoves();
            highlightLayer.highlightSquares(selectedLegalMoves);
            return;
        }

        // Clicking legal move destination
        if (selectedPiece != null && selectedLegalMoves != null && selectedLegalMoves.contains(clickPosition)) {
            // Promotion handling logic
            boolean promote = false;
            if (selectedPiece.canPromote() && clickPosition.inPromotionZone(sideOnTurn)) {
                // Try to move the piece onto clicked square - if it cannot move further, evoke promotion, otherwise ask
                if (selectedPiece.shouldPromote(selectedPiece.getPosition(), clickPosition)) {
                    promote = true;
                } else {
                    promote = PromotePopup.ask();
                }
            }

            // Apply move if all conditions are met
            board.moveManager.applyMove(
                    board.getPlayer(selectedPiece.getSide()),
                    selectedPiece.getPosition(),
                    clickPosition,
                    promote
            );

            // CHECKMATE WORKS!! YIPEE
            System.out.println(board.evals.isCheckMate(sideOnTurn));

            // Refresh data after each move
            afterEach();
        }
    }
}
