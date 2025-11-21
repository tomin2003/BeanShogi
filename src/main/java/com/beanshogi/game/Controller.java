package com.beanshogi.game;

import com.beanshogi.gui.listeners.*;
import com.beanshogi.gui.panels.*;
import com.beanshogi.gui.piece.*;
import com.beanshogi.gui.util.Popups;
import com.beanshogi.gui.util.SoundPlayer;
import com.beanshogi.model.*;
import com.beanshogi.util.*;
import com.beanshogi.engine.ShogiAI;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.SwingUtilities;

public class Controller {
    private final Board board;
    private final ShogiAI ai;

    private final HighlightLayerPanel highlightLayer;
    private final HighlightLayerPanel handTopHighlight;
    private final HighlightLayerPanel handBottomHighlight;
    private final PieceLayerPanel boardPanel;
    private final PieceLayerPanel handTopPanel;
    private final PieceLayerPanel handBottomPanel;
    private final PieceSprites sprites = new PieceSprites();
    private final Component parentComponent;

    private GameStatsListener statsListener;
    private UndoRedoListener undoRedoListener;
    private KingCheckListener kingCheckListener;
    private Runnable onGameEndCallback;

    private Sides sideOnTurn = Sides.SENTE;
    private boolean gameActive = true;

    // Separate selection state
    private Piece selectedBoardPiece = null;
    private Piece selectedHandPiece = null;
    private Sides selectedHandSide = null;
    private List<Position> selectedLegalMoves = null;

    public Controller(Game game, Component parent, GameStatsListener gsl, UndoRedoListener url, KingCheckListener kcl,
                      HighlightLayerPanel hl, HighlightLayerPanel handTopHL, HighlightLayerPanel handBottomHL,
                      PieceLayerPanel bp, PieceLayerPanel ht, PieceLayerPanel hb, Runnable onGameEndCallback) {

        this.board = game.getBoard();
        this.ai = new ShogiAI(board);
        this.parentComponent = parent;

        this.highlightLayer = hl;
        this.handTopHighlight = handTopHL;
        this.handBottomHighlight = handBottomHL;
        this.boardPanel = bp;
        this.handTopPanel = ht;
        this.handBottomPanel = hb;

        this.statsListener = gsl;
        this.undoRedoListener = url;
        this.kingCheckListener = kcl;
        this.onGameEndCallback = onGameEndCallback;

        statsListener.onSideOnTurnChanged(sideOnTurn);

        // Board click listener - now uses BiConsumer for consistency with HandPanelMouseListener
        hl.addMouseListener(new BoardMouseListener((pos) -> handleBoardClick(pos), bp.getCellSize(), bp.getGap()));

        // Hand click listeners
        ht.addMouseListener(new HandPanelMouseListener(this::handleHandClick, ht.getCellSize(), ht.getGap(), Sides.GOTE));
        hb.addMouseListener(new HandPanelMouseListener(this::handleHandClick, hb.getCellSize(), hb.getGap(), Sides.SENTE));
    }

    /**
     * Start the game (for AI vs AI auto-play)
     */
    public void startGame() {
        renderBoard();
        notifyListeners();
        tryAIMove();
    }

    /**
     * Stop the game and cleanup resources
     */
    public void stopGame() {
        gameActive = false;
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
        boardPanel.removeAll();
        for (Piece piece : board.getAllPieces()) {
            BufferedImage image = sprites.get(piece.getClass());
            PieceComponent comp = new PieceComponent(image, piece.getSide(), boardPanel.getCellSize());
            boardPanel.addPiece(comp, piece.getBoardPosition());
        }
        boardPanel.repaint();
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

    /**
     * Render the current contents of a hand panel.
     * @param panel
     * @param handGrid
     */
    private void renderHandToPanel(PieceLayerPanel handPanel, HandGrid handGrid) {
        for (Piece piece : handGrid.getAllPieces()) {
            if (piece != null) {
                BufferedImage image = sprites.get(piece.getClass());
                PieceComponent comp = new PieceComponent(image, piece.getSide(), handPanel.getCellSize());
                handPanel.addPiece(comp, piece.getHandPosition());
            }
        }
    }

    /**
     * Perform these operations after move
     */
    private void afterMove() {
        SoundPlayer.playPieceSfx();
        highlightLayer.clearAllHighlights();
        handTopHighlight.clearAllHighlights();
        handBottomHighlight.clearAllHighlights();
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
        stopGame();
        String winnerName = board.getPlayer(winner).getName();
        Popups.showGameOver(parentComponent, winnerName);
        if (onGameEndCallback != null) {
            onGameEndCallback.run();
        }
    }

    /**
     * Perform AI move operation
     */
    private void tryAIMove() {
        if (!gameActive || !board.getPlayer(sideOnTurn).isAI()) return;

        // Use invokeLater to allow UI to update between moves
        SwingUtilities.invokeLater(() -> {
            if (!gameActive) return;
            
            Move aiMove = ai.getBestMove(sideOnTurn);
            if (aiMove != null) {
                // Reconstruct the move with the real board's player and pieces
                // The AI returns a move with references to simulated board objects
                Piece realMovedPiece = null;
                if (aiMove.isDrop()) {
                    // For drops, find matching piece in real player's hand (don't remove yet, applyMove will do it)
                    for (Piece p : board.getPlayer(sideOnTurn).getHand()) {
                        if (p.getClass() == aiMove.getMovedPiece().getClass() && 
                            p.getSide() == sideOnTurn) {
                            realMovedPiece = p;
                            break;
                        }
                    }
                } else {
                    // For normal moves, get piece from real board
                    realMovedPiece = board.getPiece(aiMove.getFrom());
                }
                
                if (realMovedPiece == null) {
                    System.err.println("ERROR: Could not find real piece for AI move!");
                    return;
                }
                
                Move realMove = new Move(
                    board.getPlayer(sideOnTurn),  // Real player
                    aiMove.getFrom(),
                    aiMove.getTo(),
                    realMovedPiece,  // Real piece
                    null,  // Will be determined by applyMove
                    aiMove.isPromotion(),
                    aiMove.isDrop()
                );
                
                board.moveManager.applyMove(realMove);
                advanceTurn();
                afterMove();
                tryAIMove(); // support AI vs AI
            }
        });
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
            handTopHighlight.clearAllHighlights();
            handBottomHighlight.clearAllHighlights();
            highlightLayer.highlightSquares(selectedLegalMoves);
            return;
        }

        // Moving selected board piece
        if (selectedBoardPiece != null && selectedLegalMoves != null && selectedLegalMoves.contains(clickPosition)) {
            handleBoardMove(clickPosition);
        }
    }

    private void handleBoardMove(Position to) {
        Position from = selectedBoardPiece.getBoardPosition();

        // Check if forcing promotion is necessary, otherwise, ask
        boolean promotion = false;
        if (selectedBoardPiece.canPromote() && to.inPromotionZone(sideOnTurn)) {
            if (selectedBoardPiece.shouldPromote(from, to)) {
                promotion = true;
            } else {
                promotion = Popups.askPromotion(parentComponent);
            }
        }

        board.moveManager.applyMove(new Move(
            board.getPlayer(sideOnTurn), 
            from, 
            to, 
            selectedBoardPiece, 
            null, 
            promotion, 
            false
        ));
        advanceTurn();
        afterMove();
        tryAIMove();
    }

     /**
     * Handle the mouse click events on specified hand table.
     * @param clickPosition Clicked piece index within table
     * @param handSide The side of click handled hand table.
     */
    public Piece handleHandClick(Position clickPosition, Sides handSide) {
        HandGrid handGrid = board.getPlayer(handSide).getHandGrid();

        for (Piece piece : handGrid.getAllPieces()) {
            if (piece != null && piece.getHandPosition() != null && piece.getHandPosition().equals(clickPosition)) {
                if (piece.getSide() == sideOnTurn) {
                    selectedHandPiece = piece;
                    selectedHandSide = handSide;

                    highlightLayer.clearAllHighlights();
                    handTopHighlight.clearAllHighlights();
                    handBottomHighlight.clearAllHighlights();
                    
                    selectedLegalMoves = board.getPieceDropPoints(piece.getClass(), sideOnTurn);
                    highlightLayer.highlightSquares(selectedLegalMoves);
                    
                    // Highlight the selected hand piece
                    HighlightLayerPanel handHighlight = (handSide == Sides.GOTE) ? handTopHighlight : handBottomHighlight;
                    handHighlight.highlightSquare(clickPosition);

                    return piece;
                }
            }
        }
        return null;
    }

    // TODO: review drop legality logic
    private void handleHandDrop(Position boardPos) {
        if (selectedHandPiece == null || selectedHandSide == null) {
            return;
        }
        if (selectedLegalMoves == null || !selectedLegalMoves.contains(boardPos)) {
            return;
        }

        // Use the proper API for applying drops
        board.moveManager.applyMove(new Move(
            board.getPlayer(selectedHandSide), 
            selectedHandPiece.getHandPosition(), 
            boardPos, 
            selectedHandPiece, 
            null, 
            false, 
            true
        ));

        advanceTurn();
        afterMove();
        tryAIMove();
    }

    // ==================== UNDO / REDO ====================
    public void undoMove() {
        if (board.moveManager.isUndoStackEmpty()) return;

        board.moveManager.undoMove();
        sideOnTurn = sideOnTurn.getOpposite();
        statsListener.onSideOnTurnChanged(sideOnTurn);
        afterMove();

        if (board.getPlayer(sideOnTurn).isAI() && !board.moveManager.isUndoStackEmpty()) {
            board.moveManager.undoMove();
            sideOnTurn = sideOnTurn.getOpposite();
            statsListener.onSideOnTurnChanged(sideOnTurn);
            afterMove();
        }
    }

    public void redoMove() {
        if (board.moveManager.isRedoStackEmpty()) return;

        board.moveManager.redoMove();
        sideOnTurn = sideOnTurn.getOpposite();
        statsListener.onSideOnTurnChanged(sideOnTurn);
        afterMove();

        if (board.getPlayer(sideOnTurn).isAI() && !board.moveManager.isRedoStackEmpty()) {
            board.moveManager.redoMove();
            sideOnTurn = sideOnTurn.getOpposite();
            statsListener.onSideOnTurnChanged(sideOnTurn);
            afterMove();
        }
    }
}
