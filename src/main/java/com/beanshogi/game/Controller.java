package com.beanshogi.game;

import com.beanshogi.engine.Evals;
import com.beanshogi.engine.MoveManager;
import com.beanshogi.gui.listeners.BoardMouseListener;
import com.beanshogi.gui.listeners.GameStatsListener;
import com.beanshogi.gui.listeners.KingCheckListener;
import com.beanshogi.gui.listeners.UndoRedoListener;
import com.beanshogi.gui.panels.HighlightLayerPanel;
import com.beanshogi.gui.piece.PieceComponent;
import com.beanshogi.gui.piece.PieceLayerPanel;
import com.beanshogi.gui.piece.PieceSprites;
import com.beanshogi.gui.utils.PromotePopup;
import com.beanshogi.model.*;
import com.beanshogi.util.Position;
import com.beanshogi.util.Sides;

import java.awt.image.BufferedImage;
import java.util.List;

public class Controller {

    private final Game game;
    private final Board board;
    private final MoveManager moveMgr;
    private final Evals evals;

    private final HighlightLayerPanel highlightLayer;
    private final PieceLayerPanel pieceLayer;
    private final PieceSprites sprites = new PieceSprites();

    private final int cellSize;
    private final int gap = 3;

    private GameStatsListener statsListener;
    private UndoRedoListener undoRedoListener;
    private KingCheckListener kingCheckListener;

    private boolean kingInCheck = false;
    private Piece attackingPiece = null;
    private Piece attackedKing = null;

    private Sides sideOnTurn = Sides.SENTE;
    private Piece selectedPiece = null;
    private List<Position> selectedLegalMoves = null;

    public Controller(GameStatsListener gsl, UndoRedoListener url, KingCheckListener kcl, HighlightLayerPanel hl, PieceLayerPanel pl, int cellSize) {
        this.game = new Game();
        this.board = game.getBoard();
        this.moveMgr = new MoveManager(board);
        this.evals = new Evals(board);

        this.highlightLayer = hl;
        this.pieceLayer = pl;

        this.cellSize = cellSize;

        this.statsListener = gsl;
        this.undoRedoListener = url;
        this.kingCheckListener = kcl;
        
        statsListener.onSideOnTurnChanged(sideOnTurn);

        // Attach stateless board mouse listener
        hl.addMouseListener(new BoardMouseListener(this::handleClick, cellSize, gap));
    }

    private void afterMoveUpdate() {
        undoRedoListener.onUndoStackEmpty(moveMgr.isUndoStackEmpty());
        undoRedoListener.onRedoStackEmpty(moveMgr.isRedoStackEmpty());
        statsListener.onMoveCountChanged(moveMgr.getNoOfMoves());
        statsListener.onSideOnTurnChanged(sideOnTurn);
        kingCheckListener.onKingInCheck(kingInCheck, attackedKing, attackingPiece);
        renderBoard();
    }

    public void renderBoard() {
        pieceLayer.removeAll();

        for (Piece piece : board.getAllPieces()) {
            BufferedImage image = sprites.get(piece.getClass());
            if (image == null) continue;

            PieceComponent comp = new PieceComponent(image, piece.getSide(), cellSize);
            pieceLayer.addPiece(comp, piece.getPosition().y, piece.getPosition().x, cellSize);
        }
        pieceLayer.repaint();
    }

    public void undoMove() {
        if (moveMgr.isUndoStackEmpty()) {
            return;
        }
        moveMgr.undoMove(); // actually undo
        sideOnTurn = sideOnTurn.getOpposite();
        afterMoveUpdate();
        renderBoard();
    }

    public void redoMove() {
        if (moveMgr.isRedoStackEmpty()) {
            return;
        }
        moveMgr.redoMove(); // actually redo
        sideOnTurn = sideOnTurn.getOpposite();
        afterMoveUpdate();
        renderBoard();
    }

    private void handleClick(Position clickPosition) {
        Piece piece = board.getPiece(clickPosition);

        // Clicking own piece
        if (piece != null && piece.getSide() == sideOnTurn) {
            selectedPiece = piece;
            highlightLayer.clearAllHighlights();
            selectedLegalMoves = selectedPiece.getLegalMoves();
            highlightLayer.highlightSquares(selectedLegalMoves);
            return;
        }

        // Clicking legal move destination
        if (selectedPiece != null && selectedLegalMoves != null && selectedLegalMoves.contains(clickPosition)) {
            boolean promote = false;
            if (selectedPiece.canPromote() && clickPosition.inPromotionZone(sideOnTurn)) {
                // Simulation: try to move the piece onto the square - if it cannot move further, evoke promotion
                Position original = selectedPiece.getPosition();

                selectedPiece.setPosition(clickPosition);
                if (selectedPiece.getLegalMoves().isEmpty()) {
                    promote = true;
                } else {
                    promote = PromotePopup.ask();
                }
                selectedPiece.setPosition(original);
            }

            moveMgr.applyMove(
                    game.getPlayer(selectedPiece.getSide()),
                    selectedPiece.getPosition(),
                    clickPosition,
                    promote
            );

            kingInCheck = evals.isKingInCheck(sideOnTurn);
            if (kingInCheck) {
                attackingPiece = piece;
                attackedKing = board.getKing(sideOnTurn.getOpposite());
            } else {
                attackingPiece = null;
                attackedKing = null;
            }

            // Reset selection and highlights
            highlightLayer.clearAllHighlights();
            selectedPiece = null;
            selectedLegalMoves.clear();
            sideOnTurn = sideOnTurn.getOpposite();

            afterMoveUpdate();
            renderBoard();
        }
    }
}
