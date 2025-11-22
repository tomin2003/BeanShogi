package com.beanshogi.game;

import com.beanshogi.gui.listeners.*;
import com.beanshogi.gui.panels.*;
import com.beanshogi.gui.panels.overlays.HighlightLayerPanel;
import com.beanshogi.gui.panels.overlays.piece.*;
import com.beanshogi.gui.util.Popups;
import com.beanshogi.gui.util.SoundPlayer;
import com.beanshogi.io.LeaderboardSaveLoad;
import com.beanshogi.leaderboard.Entry;
import com.beanshogi.leaderboard.ResultType;
import com.beanshogi.model.*;
import com.beanshogi.util.*;
import com.beanshogi.engine.ShogiAI;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.SwingUtilities;

public class Controller {
    private final Game game;
    private final Board board;
    private final ShogiAI ai;
    private final ControllerPanels panels;
    private final PieceSprites sprites = new PieceSprites();
    private final Component parentComponent;

    private final ControllerListeners listeners;
    private Runnable onGameEndCallback;

    private Sides sideOnTurn;
    private boolean gameOver = false;

    // Separate selection state
    private Piece selectedBoardPiece = null;
    private Piece selectedHandPiece = null;
    private Sides selectedHandSide = null;
    private List<Position> selectedLegalMoves = null;

    public Controller(Game game, Component parent, ControllerListeners listeners, ControllerPanels panels, Runnable onGameEndCallback) {

        this.game = game;
        this.board = game.getBoard();
        this.ai = new ShogiAI(board);
        this.parentComponent = parent;
        this.panels = panels;
        this.listeners = listeners;
        this.onGameEndCallback = onGameEndCallback;

        Sides savedTurn = game.getNextTurn();
        this.sideOnTurn = savedTurn != null ? savedTurn : Sides.SENTE;

        listeners.notifySideOnTurnChanged(sideOnTurn);
        game.setNextTurn(sideOnTurn);

        // Board click listener
        panels.getBoardHighlight().addMouseListener(new BoardMouseListener(
            (pos) -> handleBoardClick(pos), 
            panels.getBoardPanel().getCellSize(), 
            panels.getBoardPanel().getGap()
        ));

        // Top hand click listener
        panels.getHandTopPanel().addMouseListener(new HandPanelMouseListener(
            this::handleHandClick,panels.getHandTopPanel().getCellSize(),
            panels.getHandTopPanel().getGap(),
            Sides.GOTE
        ));

        // Bottom hand click listener
        panels.getHandBottomPanel().addMouseListener(new HandPanelMouseListener(
            this::handleHandClick,
            panels.getHandBottomPanel().getCellSize(),
            panels.getHandBottomPanel().getGap(),
            Sides.SENTE
        ));
    }

    /**
     * Start the game (for AI vs AI auto-play)
     */
    public void startGame() {
        renderBoard();
        notifyListeners();
        tryAIMove();
    }

    // ==================== TURN MANAGEMENT ====================
    private void advanceTurn() {
        if (gameOver) {
            return;
        }
        sideOnTurn = sideOnTurn.getOpposite();
        listeners.notifySideOnTurnChanged(sideOnTurn);
        game.setNextTurn(sideOnTurn);
    }

    /**
     * Notify listeners about the changed board state
     */
    private void notifyListeners() {
        listeners.updateUndoRedoState(this, board.moveManager);
        listeners.notifyMoveCount(board.moveManager.getNoOfMoves());
        listeners.notifyKingCheck(board.evals.kingChecks());
    }

    // ==================== BOARD RENDERING ====================
    public void renderBoard() {
        PieceLayerPanel boardPanel = panels.getBoardPanel();
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
        PieceLayerPanel topPanel = panels.getHandTopPanel();
        topPanel.removeAll();
        renderHandToPanel(topPanel, board.getPlayer(Sides.GOTE).getHandGrid());
        topPanel.repaint();

        PieceLayerPanel bottomPanel = panels.getHandBottomPanel();
        bottomPanel.removeAll();
        renderHandToPanel(bottomPanel, board.getPlayer(Sides.SENTE).getHandGrid());
        bottomPanel.repaint();
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
     * Call clearAllHighlights() on included highlight panels.
     */
    private void clearAllHighlights() {
        panels.clearAllHighlights();
    }

    /**
     * Make all selection variables empty - represented with null.
     */
    private void nullifySelections() {
        selectedBoardPiece = null;
        selectedHandPiece = null;
        selectedHandSide = null;
        selectedLegalMoves = null;
    }

    /**
     * Clear and reset all transient states of board, notify listeners, render, and handle game over state.
     */
    private void afterMove() {
        if (gameOver) {
            return;
        }
        clearAllHighlights();
        nullifySelections();
        notifyListeners();
        renderBoard();

        if (board.evals.isCheckMate(sideOnTurn)) {
            onGameEnd(sideOnTurn.getOpposite(), "Checkmate", ResultType.CHECKMATE);
        }
    }

    private void onGameEnd(Sides winner, String reason, ResultType resultType) {
        if (gameOver) {
            return;
        }
        gameOver = true;
        String winnerName = board.getPlayer(winner).getName();
        String loserName = board.getPlayer(winner.getOpposite()).getName();

        Entry entry = new Entry(
            winnerName,
            loserName,
            winner.getOpposite(),
            board.moveManager.getNoOfMoves(),
            resultType,
            java.time.Instant.now(),
            board.getPlayer(winner).getType(),
            board.getPlayer(winner.getOpposite()).getType()
        );
        LeaderboardSaveLoad.appendEntry(entry);

        Popups.showGameOver(parentComponent, winnerName, reason);
        if (onGameEndCallback != null) {
            onGameEndCallback.run();
        }
    }

    public void resign(Sides resigningSide) {
        if (resigningSide == null || gameOver) {
            return;
        }
        Sides winner = resigningSide.getOpposite();
        String resigningName = board.getPlayer(resigningSide).getName();
        onGameEnd(winner, resigningName + " resigned.", ResultType.RESIGNATION);
    }

    /**
     * Perform AI move operation
     */
    private void tryAIMove() {
        if (gameOver) {
            return;
        }
        if (!board.getPlayer(sideOnTurn).isAI()) return;

        // Use invokeLater to allow UI to update between moves
        SwingUtilities.invokeLater(() -> {
            if (gameOver) {
                return;
            }
            Move aiMove = ai.getBestMove(sideOnTurn);
            if (aiMove != null) {
                // Reconstruct the move with the real board's player and pieces
                // The AI returns a move with references to simulated board objects
                Piece realMovedPiece = null;
                if (aiMove.isDrop()) {
                    // For drops, find matching piece in real player's hand
                    for (Piece p : board.getPlayer(sideOnTurn).getHandPieces()) {
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
                    throw new Exceptions.PieceNotFoundException("The piece for AI move is not found on board!");
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
                SoundPlayer.playPieceSfx();
            }
        });
    }

    /**
     * Handle making the drop movement from the hand table to the board.
     * @param boardPos The board position of drop target
     */
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

        // Make normal move, with optional promotion
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
        SoundPlayer.playPieceSfx();
    }

    /**
     * Handle making the drop movement from the hand table to the board.
     * @param to The board position of drop target
     */
    private void handleHandDrop(Position to) {
        if (selectedHandPiece == null || selectedHandSide == null) {
            return;
        }
        if (selectedLegalMoves == null || !selectedLegalMoves.contains(to)) {
            return;
        }

        // Use the move manager to apply drop move
        board.moveManager.applyMove(new Move(
            board.getPlayer(selectedHandSide), 
            selectedHandPiece.getHandPosition(), 
            to, 
            selectedHandPiece, 
            null, 
            false, 
            true
        ));

        advanceTurn();
        afterMove();
        tryAIMove();
        SoundPlayer.playPieceSfx();
    }

    public void undoMove() {
        if (board.moveManager.isUndoStackEmpty()) {
            return;
        }

        board.moveManager.undoMove();
        advanceTurn();
        afterMove();

        // Undo once more on AI moves, because the player can't change that
        if (board.getPlayer(sideOnTurn).isAI() && !board.moveManager.isUndoStackEmpty()) {
            board.moveManager.undoMove();
            advanceTurn();
            afterMove();
        }
    }

    public void redoMove() {
        if (board.moveManager.isRedoStackEmpty()) {
            return;
        }

        board.moveManager.redoMove();
        advanceTurn();
        afterMove();

        // Redo once more on AI moves, because the player can't change that
        if (board.getPlayer(sideOnTurn).isAI() && !board.moveManager.isRedoStackEmpty()) {
            board.moveManager.redoMove();
            advanceTurn();
            afterMove();
        }
    }

    private void handleBoardClick(Position clickPosition) {
        if (gameOver) {
            return;
        }
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
            clearAllHighlights();
            panels.getBoardHighlight().highlightSquares(selectedLegalMoves);
            return;
        }

        // Moving selected board piece
        if (selectedBoardPiece != null && selectedLegalMoves != null && selectedLegalMoves.contains(clickPosition)) {
            handleBoardMove(clickPosition);
        }
    }

     /**
     * Handle the mouse click events on specified hand table.
     * @param clickPosition Clicked piece index within table
     * @param handSide The side of click handled hand table.
     */
    public Piece handleHandClick(Position clickPosition, Sides handSide) {
        if (gameOver) {
            return null;
        }
        HandGrid handGrid = board.getPlayer(handSide).getHandGrid();

        for (Piece piece : handGrid.getAllPieces()) {
            if (piece != null && piece.getHandPosition() != null && piece.getHandPosition().equals(clickPosition)) {
                if (piece.getSide() == sideOnTurn) {
                    selectedHandPiece = piece;
                    selectedHandSide = handSide;
                    
                    clearAllHighlights();
                    selectedLegalMoves = board.getPieceDropPoints(piece.getClass(), sideOnTurn);
                    panels.getBoardHighlight().highlightSquares(selectedLegalMoves);
                    
                    // Highlight the selected hand piece
                    HighlightLayerPanel handHighlight = panels.getHandHighlight(handSide);
                    handHighlight.highlightSquare(clickPosition);

                    return piece;
                }
            }
        }
        return null;
    }
}
