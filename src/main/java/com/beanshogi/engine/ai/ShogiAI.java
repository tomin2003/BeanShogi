package com.beanshogi.engine.ai;

import com.beanshogi.engine.Evals;
import com.beanshogi.engine.MoveManager;
import com.beanshogi.game.Player;
import com.beanshogi.model.*;
import com.beanshogi.util.Position;
import com.beanshogi.util.Sides;

/**
 * Class handling AI logic
 * Implements minimax with alpha-beta pruning
 */
public class ShogiAI {

    private final int MAX_DEPTH = 3;
    private Board board;

    public ShogiAI(Board board) {
        this.board = board;
    }

    /**
     * Public entry point to get best move for a side
     */
    public Move getBestMove(Sides sideToMove, Player player) {
        int bestScore = sideToMove == Sides.SENTE ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Move bestMove = null;

        for (Piece piece : board.getPiecesOfSide(sideToMove)) {
            Position from = piece.getPosition();
            for (Position to : piece.getLegalMoves()) {
                Board simBoard = board.copy();
                MoveManager simMoveManager = new MoveManager(simBoard);

                boolean promotion = piece.canPromote();
                Move move = new Move(player, from, to, piece, simBoard.getPiece(to), promotion);
                simMoveManager.applyMove(move);

                int score = minimax(simBoard, MAX_DEPTH - 1, sideToMove.getOpposite(), Integer.MIN_VALUE, Integer.MAX_VALUE);

                if (sideToMove == Sides.SENTE && score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                } else if (sideToMove == Sides.GOTE && score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        }

        return bestMove;
    }

    /**
     * Minimax with alpha-beta pruning
     */
    private int minimax(Board currentBoard, int depth, Sides sideToMove, int alpha, int beta) {
        if (depth == 0) {
            return evaluate(currentBoard, sideToMove);
        }

        boolean maximizingSide = sideToMove == Sides.SENTE;
        int best = maximizingSide ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Piece piece : currentBoard.getPiecesOfSide(sideToMove)) {
            Position from = piece.getPosition();
            for (Position to : piece.getLegalMoves()) {
                Board simBoard = currentBoard.copy();
                MoveManager simMoveManager = new MoveManager(simBoard);

                boolean promotion = piece.canPromote();
                Move move = new Move(null, from, to, piece, simBoard.getPiece(to), promotion);
                simMoveManager.applyMove(move);

                int score = minimax(simBoard, depth - 1, sideToMove.getOpposite(), alpha, beta);

                if (maximizingSide) {
                    best = Math.max(best, score);
                    alpha = Math.max(alpha, best);
                    if (beta <= alpha) break; // prune
                } else {
                    best = Math.min(best, score);
                    beta = Math.min(beta, best);
                    if (beta <= alpha) break; // prune
                }
            }
        }

        return best;
    }

    /**
     * Evaluate board state for the given side
     */
    private int evaluate(Board board, Sides sideToMove) {
        Evals evals = new Evals(board);

        if (evals.isCheckMate()) {
            return sideToMove == Sides.SENTE ? Integer.MIN_VALUE + 1 : Integer.MAX_VALUE - 1;
        }

        if (evals.isKingInCheck()) {
            return sideToMove == Sides.SENTE ? -3000 : 3000;
        }

        return evaluateRegularPlay(board);
    }

    /**
     * Simple material-based evaluation
     */
    private int evaluateRegularPlay(Board board) {
        int score = 0;
        for (Piece piece : board.getAllPieces()) {
            int value = piece.value();
            score += piece.getSide() == Sides.SENTE ? value : -value;
        }
        return score;
    }
}
