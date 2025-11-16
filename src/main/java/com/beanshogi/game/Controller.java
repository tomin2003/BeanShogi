package com.beanshogi.game;

import com.beanshogi.gui.listeners.*;
import com.beanshogi.gui.panels.*;
import com.beanshogi.gui.piece.*;
import com.beanshogi.gui.utils.PromotePopup;
import com.beanshogi.model.*;
import com.beanshogi.util.*;
import com.beanshogi.engine.ai.*;

import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.Timer;

public class Controller {

    private final Game game;
    private final Board board;
    private final ShogiAI ai;

    private final HighlightLayerPanel highlightLayer;
    private final PieceLayerPanel pieceLayer;
    private final PieceLayerPanel handTopPanel;
    private final PieceLayerPanel handBottomPanel;
    private final PieceSprites sprites = new PieceSprites();

    private final int cellSize;
    private final int gap = 2;

    private GameStatsListener statsListener;
    private UndoRedoListener undoRedoListener;
    private KingCheckListener kingCheckListener;

    private Sides sideOnTurn = Sides.SENTE;

    // Separate selection state
    private Piece selectedBoardPiece = null;
    private Piece selectedHandPiece = null;
    private Sides selectedHandSide = null;
    private List<Position> selectedLegalMoves = null;

    public Controller(GameStatsListener gsl, UndoRedoListener url, KingCheckListener kcl,
                      HighlightLayerPanel hl, PieceLayerPanel pl, PieceLayerPanel handTop,
                      PieceLayerPanel handBottom, int cellSize) {

        this.game = new Game();
        this.board = game.getBoard();
        this.ai = new ShogiAI(board);

        this.highlightLayer = hl;
        this.pieceLayer = pl;
        this.handTopPanel = handTop;
        this.handBottomPanel = handBottom;
        this.cellSize = cellSize;

        this.statsListener = gsl;
        this.undoRedoListener = url;
        this.kingCheckListener = kcl;

        statsListener.onSideOnTurnChanged(sideOnTurn);

        // Board click listener
        hl.addMouseListener(new BoardMouseListener(this::handleBoardClick, cellSize, gap));

        // Hand click listeners
        handTop.addMouseListener(new HandPanelMouseListener(this::handleHandClickWrapper, cellSize, gap, Sides.GOTE));
        handBottom.addMouseListener(new HandPanelMouseListener(this::handleHandClickWrapper, cellSize, gap, Sides.SENTE));
    }

    // ==================== TURN MANAGEMENT ====================
    private void advanceTurn() {
        sideOnTurn = sideOnTurn.getOpposite();
        statsListener.onSideOnTurnChanged(sideOnTurn);
    }

    // ==================== LISTENER NOTIFICATION ====================
    private void notifyListeners() {
        undoRedoListener.gainController(this);
        undoRedoListener.onUndoStackEmpty(board.moveManager.isUndoStackEmpty());
        undoRedoListener.onRedoStackEmpty(board.moveManager.isRedoStackEmpty());

        statsListener.onMoveCountChanged(board.moveManager.getNoOfMoves());
        kingCheckListener.onKingInCheck(board.evals.kingChecks());
    }

    // ==================== BOARD RENDERING ====================
    public void renderBoard() {
        pieceLayer.removeAll();
        for (Piece piece : board.getAllPieces()) {
            BufferedImage image = sprites.get(piece.getClass());
            PieceComponent comp = new PieceComponent(image, piece.getSide(), cellSize);
            pieceLayer.addPiece(comp, piece.getPosition().y, piece.getPosition().x, cellSize);
        }
        pieceLayer.repaint();
        renderHands();
    }

    private void renderHands() {
        handTopPanel.removeAll();
        renderHandToPanel(handTopPanel, board.getPlayer(Sides.GOTE).getHandGrid());
        handTopPanel.repaint();

        handBottomPanel.removeAll();
        renderHandToPanel(handBottomPanel, board.getPlayer(Sides.SENTE).getHandGrid());
        handBottomPanel.repaint();
    }

    private void renderHandToPanel(PieceLayerPanel panel, HandGrid handGrid) {
        int[] dims = handGrid.getDimensions();
        for (int row = 0; row < dims[0]; row++) {
            for (int col = 0; col < dims[1]; col++) {
                Piece piece = handGrid.getPieceAt(row, col);
                if (piece != null) {
                    BufferedImage image = sprites.get(piece.getClass());
                    PieceComponent comp = new PieceComponent(image, piece.getSide(), cellSize);
                    panel.addPiece(comp, row, col, cellSize);
                }
            }
        }
    }

    /**
     * Perform these operations after move
     */
    private void afterMove() {
        highlightLayer.clearAllHighlights();
        selectedBoardPiece = null;
        selectedHandPiece = null;
        selectedHandSide = null;
        selectedLegalMoves = null;

        notifyListeners();
        renderBoard();

        if (board.evals.isCheckMate(sideOnTurn)) {
            onGameEnd(sideOnTurn.getOpposite());
        }
    }

    private void onGameEnd(Sides winner) {
        System.out.println(sideOnTurn + " wins");
        // TODO: Proper game end screen/popup
    }

    /**
     * Perform AI move operation
     */
    private void tryAIMove() {
        if (!board.getPlayer(sideOnTurn).isAI()) return;

        Timer aiTimer = new Timer(500, e -> {
            Move aiMove = ai.getBestMove(sideOnTurn);
            if (aiMove != null) {
                board.moveManager.applyMove(aiMove);
                advanceTurn();
                afterMove();
                tryAIMove(); // support AI vs AI
            }
        });
        aiTimer.setRepeats(false);
        aiTimer.start();
    }

    // ==================== BOARD CLICK HANDLING ====================
    private void handleBoardClick(Position clickPosition) {
        // Handle hand piece drop first
        if (selectedHandPiece != null && selectedHandSide != null) {
            if (selectedLegalMoves != null && selectedLegalMoves.contains(clickPosition)) {
                handleHandDrop(clickPosition);
            } else {
                afterMove(); // deselect
            }
            return;
        }

        Piece piece = board.getPiece(clickPosition);

        // Selecting a board piece
        if (piece != null && piece.getSide() == sideOnTurn) {
            selectedBoardPiece = piece;
            selectedLegalMoves = piece.getLegalMoves();
            highlightLayer.clearAllHighlights();
            highlightLayer.highlightSquares(selectedLegalMoves);
            return;
        }

        // Moving selected board piece
        if (selectedBoardPiece != null && selectedLegalMoves != null && selectedLegalMoves.contains(clickPosition)) {
            handleBoardMove(clickPosition);
        }
    }

    private void handleBoardMove(Position to) {
        boolean promote = false;
        if (selectedBoardPiece.canPromote() && to.inPromotionZone(sideOnTurn)) {
            promote = PromotePopup.ask(); // GUI-driven callback
        }

        board.moveManager.applyMove(
                board.getPlayer(sideOnTurn),
                selectedBoardPiece.getPosition(),
                to,
                promote
        );

        advanceTurn();
        afterMove();
        tryAIMove();
    }

    // ==================== HAND CLICK HANDLING ====================
    public void handleHandClickWrapper(Position clickPosition, Sides handSide) {
        handleHandClick(clickPosition, handSide);
    }

    public Piece handleHandClick(Position clickPosition, Sides handSide) {
        HandGrid hand = board.getPlayer(handSide).getHandGrid();
        int[] dims = hand.getDimensions();

        int row = clickPosition.x;
        int col = clickPosition.y;
        if (row < 0 || row >= dims[0] || col < 0 || col >= dims[1]) return null;

        Piece piece = hand.getPieceAt(row, col);
        if (piece != null && piece.getSide() == sideOnTurn) {
            selectedHandPiece = piece;
            selectedHandSide = handSide;

            highlightLayer.clearAllHighlights();
            selectedLegalMoves = board.getPieceDropPoints(piece.getClass(), sideOnTurn);
            highlightLayer.highlightSquares(selectedLegalMoves);

            return piece;
        }
        return null;
    }

    private boolean handleHandDrop(Position boardPos) {
        if (selectedHandPiece == null || selectedHandSide == null) {
            return false;
        }
        if (selectedLegalMoves == null || !selectedLegalMoves.contains(boardPos)) {
            return false;
        }

        // Use the proper API for applying drops
        board.moveManager.applyDrop(
                board.getPlayer(selectedHandSide),
                selectedHandPiece,
                boardPos
        );

        advanceTurn();
        afterMove();
        tryAIMove();

        return true;
    }

    // ==================== UNDO / REDO ====================
    public void undoMove() {
        if (board.moveManager.isUndoStackEmpty()) return;

        board.moveManager.undoMove();
        sideOnTurn = sideOnTurn.getOpposite();
        afterMove();

        if (board.getPlayer(sideOnTurn).isAI() && !board.moveManager.isUndoStackEmpty()) {
            board.moveManager.undoMove();
            sideOnTurn = sideOnTurn.getOpposite();
            afterMove();
        }
    }

    public void redoMove() {
        if (board.moveManager.isRedoStackEmpty()) return;

        board.moveManager.redoMove();
        sideOnTurn = sideOnTurn.getOpposite();
        afterMove();

        if (board.getPlayer(sideOnTurn).isAI() && !board.moveManager.isRedoStackEmpty()) {
            board.moveManager.redoMove();
            sideOnTurn = sideOnTurn.getOpposite();
            afterMove();
        }
    }
}
