package com.beanshogi.core.game;

import com.beanshogi.gui.listeners.*;
import com.beanshogi.gui.listeners.board.BoardMouseListener;
import com.beanshogi.gui.listeners.hand.HandPanelMouseListener;
import com.beanshogi.gui.panels.*;
import com.beanshogi.gui.render.BoardRenderer;
import com.beanshogi.gui.render.HighlightLayerPanel;
import com.beanshogi.gui.util.SelectionState;
import com.beanshogi.gui.util.SoundPlayer;
import com.beanshogi.leaderboard.Entry;
import com.beanshogi.leaderboard.ResultType;
import com.beanshogi.core.ai.AIDifficulty;
import com.beanshogi.core.ai.ShogiAI;
import com.beanshogi.core.board.Board;
import com.beanshogi.core.board.HandGrid;
import com.beanshogi.core.board.Move;
import com.beanshogi.core.pieces.Piece;
import com.beanshogi.core.util.*;

import java.awt.Component;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;

public class Controller {
    private final Game game;
    private final Board board;
    private final ShogiAI ai;
    private final ControllerPanels panels;
    private final BoardRenderer renderer;
    private final SelectionState selection;

    private final ControllerListeners listeners;

    private Sides sideOnTurn;
    private boolean gameOver = false;
    private final ExecutorService aiExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "beanshogi-ai");
        t.setDaemon(true);
        return t;
    });
    private volatile boolean aiTurnInProgress = false;

    /**
     * Constructs a new game controller.
     * @param game the game instance to control
     * @param parent the parent component for dialogs
     * @param listeners the controller listeners for events
     * @param panels the UI panels to manage
     */
    public Controller(Game game, Component parent, ControllerListeners listeners, ControllerPanels panels) {

        this.game = game;
        this.board = game.getBoard();
        this.ai = new ShogiAI(board);
        this.panels = panels;
        this.renderer = new BoardRenderer(board, panels);
        this.selection = new SelectionState();
        this.listeners = listeners;

        Sides savedTurn = game.getNextTurn();
        this.sideOnTurn = savedTurn != null ? savedTurn : Sides.SENTE;

        listeners.notifySideOnTurnChanged(sideOnTurn);
        game.setNextTurn(sideOnTurn);

        // Board click listener
        panels.getBoardHighlight().addMouseListener(new BoardMouseListener(
            this::handleBoardClick, 
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
     * Cleanly shuts down the AI executor service.
     * Should be called when the controller is no longer needed.
     */
    public void shutdown() {
        aiExecutor.shutdownNow();
    }

    /**
     * Start the game (for AI vs AI auto-play)
     */
    public void startGame() {
        SoundPlayer.playBackgroundMusic();
        renderer.renderAll();
        notifyListeners();
        listeners.notifyGameStart();
        tryAIMove();
    }

    /* Turn management */

    /**
     * Advances the turn to the opposite side.
     */
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

    /* Board rendering */

    /**
     * Renders the current board state including all pieces on the board and in hands.
     */
    public void renderBoard() {
        renderer.renderAll();
    }

    /**
     * Call clearAllHighlights() on included highlight panels.
     */
    private void clearAllHighlights() {
        panels.clearAllHighlights();
    }

    /**
     * Clears all selection state variables.
     */
    private void nullifySelections() {
        selection.clear();
    }

    /* Handling after move */

    /**
     * Performs post-move cleanup: clears highlights and selections, notifies listeners,
     * re-renders the board, and checks for checkmate.
     */
    private void afterMove() {
        if (gameOver) {
            return;
        }
        clearAllHighlights();
        nullifySelections();
        notifyListeners();
        renderBoard();

        // Check for checkmate
        if (board.evals.isCheckMate(sideOnTurn)) {
            onGameEnd(sideOnTurn.getOpposite(), "Checkmate", ResultType.CHECKMATE);
            return;
        }
        
        // Check for Sennichite (fourfold repetition)
        if (board.isSennichite()) {
            onGameEnd(null, "Draw by Sennichite (repetition)", ResultType.DRAW);
        }
    }

    /* End game handling */

    /**
     * Handles game end logic including saving results and showing game over dialog.
     * @param winner the side that won
     * @param reason the reason for game end
     * @param resultType the type of result (checkmate, resignation, etc.)
     */
    private void onGameEnd(Sides winner, String reason, ResultType resultType) {
        if (gameOver) {
            return;
        }
        gameOver = true;
        aiTurnInProgress = false;
        SoundPlayer.stopBackgroundMusic();
        
        Entry entry;
        if (winner == null) {
            // Draw - no winner
            Player sente = board.getPlayer(Sides.SENTE);
            Player gote = board.getPlayer(Sides.GOTE);
            entry = new Entry(
                "Draw",
                "Draw",
                null,
                board.moveManager.getNoOfMoves(),
                resultType,
                java.time.Instant.now(),
                sente.getType(),
                gote.getType()
            );
        } else {
            String winnerName = board.getPlayer(winner).getName();
            String loserName = board.getPlayer(winner.getOpposite()).getName();
            entry = new Entry(
                winnerName,
                loserName,
                winner.getOpposite(),
                board.moveManager.getNoOfMoves(),
                resultType,
                java.time.Instant.now(),
                board.getPlayer(winner).getType(),
                board.getPlayer(winner.getOpposite()).getType()
            );
        }
        
        listeners.notifyGameEnd(winner, reason, resultType, entry);
    }

    /**
     * Handles a player resignation.
     * @param resigningSide the side that is resigning
     */
    public void resign(Sides resigningSide) {
        if (resigningSide == null || gameOver) {
            return;
        }
        listeners.notifyResignation(resigningSide);
        Sides winner = resigningSide.getOpposite();
        String resigningName = board.getPlayer(resigningSide).getName();
        onGameEnd(winner, resigningName + " resigned.", ResultType.RESIGNATION);
    }

    /* Move handling */

    /**
     * Initiates an AI move if it's an AI player's turn.
     * Executes on a background thread to keep UI responsive.
     */
    private void tryAIMove() {
        if (gameOver || aiTurnInProgress) {
            return;
        }

        Player currentPlayer = board.getPlayer(sideOnTurn);
        if (!currentPlayer.isAI()) {
            return;
        }

        aiTurnInProgress = true;
        final Sides turnSnapshot = sideOnTurn;
        final AIDifficulty difficulty = currentPlayer.getDifficulty();
        
        aiExecutor.execute(() -> {
            try {
                Move aiMove = ai.getBestMove(turnSnapshot, difficulty);
                SwingUtilities.invokeLater(() -> handleAiMove(turnSnapshot, currentPlayer, aiMove));
            } catch (Exception e) {
                System.err.println("AI move calculation failed: " + e.getMessage());
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> aiTurnInProgress = false);
            }
        });
    }

    /**
     * Handles applying an AI move to the board.
     * @param expectedSide the side that was expected to move
     * @param aiPlayer the AI player making the move
     * @param aiMove the computed move
     */
    private void handleAiMove(Sides expectedSide, Player aiPlayer, Move aiMove) {
        try {
            if (gameOver || sideOnTurn != expectedSide) {
                return;
            }
            if (aiMove == null) {
                return;
            }

            Piece realMovedPiece = mapAiMovePiece(aiPlayer, aiMove, expectedSide);
            Move realMove = new Move(
                aiPlayer,
                aiMove.getFrom(),
                aiMove.getTo(),
                realMovedPiece,
                null,
                aiMove.isPromotion(),
                aiMove.isDrop()
            );

            board.moveManager.applyMove(realMove);
            advanceTurn();
            afterMove();
            listeners.notifyMoveMade(realMove, expectedSide);
        } finally {
            aiTurnInProgress = false;
            tryAIMove(); // Chain to next AI move if needed
        }
    }

    /**
     * Maps an AI move's piece reference to the actual piece on the board.
     * @param aiPlayer the AI player
     * @param aiMove the AI's calculated move
     * @param expectedSide the expected side of the piece
     * @return the actual piece instance on the board
     * @throws Exceptions.PieceNotFoundException if the piece cannot be found
     */
    private Piece mapAiMovePiece(Player aiPlayer, Move aiMove, Sides expectedSide) {
        Piece realMovedPiece;
        if (aiMove.isDrop()) {
            realMovedPiece = aiPlayer.getHandPieces().stream()
                .filter(p -> p.getClass() == aiMove.getMovedPiece().getClass() && p.getSide() == expectedSide)
                .findFirst()
                .orElse(null);
        } else {
            realMovedPiece = board.getPiece(aiMove.getFrom());
        }

        if (realMovedPiece == null) {
            throw new Exceptions.PieceNotFoundException("The piece for AI move is not found on board!");
        }
        return realMovedPiece;
    }

    /**
     * Handles moving a selected board piece to a destination square.
     * @param to the destination position on the board
     */
    private void handleBoardMove(Position to) {
        Piece piece = selection.getSelectedBoardPiece();
        Position from = piece.getBoardPosition();

        // Determine if promotion is required or should be offered
        boolean promotion = false;
        if (piece.canPromote() && to.inPromotionZone(sideOnTurn)) {
            if (piece.shouldPromote(from, to)) {
                promotion = true;
            } else {
                promotion = listeners.requestPromotionDecision(piece, from, to);
            }
        }

        // Make normal move, with optional promotion
        board.moveManager.applyMove(new Move(
            board.getPlayer(sideOnTurn), 
            from, 
            to, 
            piece, 
            null, 
            promotion, 
            false
        ));

        advanceTurn();
        afterMove();
        tryAIMove();
        listeners.notifyMoveMade(null, sideOnTurn.getOpposite());
    }

    /**
     * Handles dropping a selected hand piece onto the board.
     * @param to the board position where the piece should be dropped
     */
    private void handleHandDrop(Position to) {
        if (!selection.hasHandPieceSelected() || 
            selection.getSelectedLegalMoves() == null || 
            !selection.getSelectedLegalMoves().contains(to)) {
            return;
        }

        Piece piece = selection.getSelectedHandPiece();
        Sides side = selection.getSelectedHandSide();

        // Use the move manager to apply drop move
        board.moveManager.applyMove(new Move(
            board.getPlayer(side), 
            piece.getHandPosition(), 
            to, 
            piece, 
            null, 
            false, 
            true
        ));

        advanceTurn();
        afterMove();
        tryAIMove();
        listeners.notifyMoveMade(null, side);
    }

    /**
     * Undoes the last move(s). If the current player is AI, undoes two moves
     * to return to the human player's turn.
     */
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

    /**
     * Redoes the previously undone move(s). If the current player is AI, redoes two moves
     * to advance to the human player's turn.
     */
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

    /* Click event handling */

    /**
     * Handles mouse clicks on the board.
     * @param clickPosition the position on the board that was clicked
     */
    private void handleBoardClick(Position clickPosition) {
        if (gameOver) {
            return;
        }
        // Handle hand piece drop first
        if (selection.hasHandPieceSelected()) {
            List<Position> legalMoves = selection.getSelectedLegalMoves();
            if (legalMoves != null && legalMoves.contains(clickPosition)) {
                handleHandDrop(clickPosition);
            } else {
                afterMove(); // deselect
            }
            return;
        }

        Piece piece = board.getPiece(clickPosition);

        // Selecting a board piece
        if (piece != null && piece.getSide() == sideOnTurn) {
            List<Position> legalMoves = board.evals.getFilteredLegalMoves(piece);
            selection.selectBoardPiece(piece, legalMoves);
            clearAllHighlights();
            panels.getBoardHighlight().highlightSquares(legalMoves);
            panels.getBoardSelectionHighlight().highlightSquare(piece.getBoardPosition());
            return;
        }

        // Moving selected board piece
        if (selection.hasBoardPieceSelected()) {
            List<Position> legalMoves = selection.getSelectedLegalMoves();
            if (legalMoves != null && legalMoves.contains(clickPosition)) {
                handleBoardMove(clickPosition);
            }
        }
    }

    /**
     * Handles mouse click events on a hand panel.
     * @param clickPosition the clicked piece position within the hand grid
     * @param handSide the side of the hand panel that was clicked
     * @return the selected piece, or null if no valid piece was selected
     */
    public Piece handleHandClick(Position clickPosition, Sides handSide) {
        if (gameOver) {
            return null;
        }
        HandGrid handGrid = board.getPlayer(handSide).getHandGrid();

        for (Piece piece : handGrid.getAllPieces()) {
            if (piece != null && piece.getHandPosition() != null && piece.getHandPosition().equals(clickPosition)) {
                if (piece.getSide() == sideOnTurn) {
                    List<Position> legalDrops = board.getPieceDropPoints(piece.getClass(), sideOnTurn);
                    selection.selectHandPiece(piece, handSide, legalDrops);
                    
                    clearAllHighlights();
                    panels.getBoardHighlight().highlightSquares(legalDrops);
                    
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
