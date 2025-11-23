package com.beanshogi.core.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.beanshogi.core.board.Board;
import com.beanshogi.core.board.Move;
import com.beanshogi.core.game.Player;
import com.beanshogi.core.game.Sides;
import com.beanshogi.core.pieces.Piece;
import com.beanshogi.core.util.Position;


public class ShogiAI {

    private static class MoveScore {
        final com.beanshogi.core.board.Move move;
        final int orderingScore;
        MoveScore(Move m, int ordering) {
            move = m;
            orderingScore = ordering;
        }
    }

    private final Board board;
    private final Random random = new Random();

    public ShogiAI(Board board) {
        this.board = board;
    }

    /**
     * Returns the best move (normal or drop) for the given side.
     */
    public Move getBestMove(Sides sideToMove, AIDifficulty difficulty) {
        AIDifficulty activeDifficulty = difficulty != null ? difficulty : AIDifficulty.NORMAL;
        int maxDepth = Math.max(1, activeDifficulty.getSearchDepth());
        
        // Work on an isolated copy so search can apply/undo moves without touching the live board
        Board searchBoard = board.copy();
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        // Generate all possible moves from the copied board so we can apply/undo safely
        List<MoveScore> candidateMoves = generateAllMoves(searchBoard, sideToMove, activeDifficulty, true);

        // Evaluate moves sequentially with alpha-beta
        for (MoveScore ms : candidateMoves) {

            searchBoard.moveManager.applyMove(ms.move);
            if (searchBoard.evals.isKingInCheck(sideToMove)) {
                // Illegal move (king left in check)
                searchBoard.moveManager.undoMove();
                searchBoard.moveManager.getRedoStack().clear();
                continue;
            }

            int score = minimax(searchBoard, maxDepth - 1, sideToMove.getOpposite(),
                                sideToMove, Integer.MIN_VALUE, Integer.MAX_VALUE, activeDifficulty);

            if (bestMove == null || score > bestScore) {
                bestScore = score;
                bestMove = ms.move;
            }

            searchBoard.moveManager.undoMove();
            searchBoard.moveManager.getRedoStack().clear();
        }
        return bestMove;
    }
    
    /**
     * Generate pseudo-legal moves for the given board/side, ordered by simple heuristics.
     */
    private List<MoveScore> generateAllMoves(Board targetBoard, Sides sideToMove, AIDifficulty difficulty, boolean applyNoise) {
        List<MoveScore> moves = new ArrayList<>();
        Player player = targetBoard.getPlayer(sideToMove);

        for (Piece piece : targetBoard.getPiecesOfSide(sideToMove)) {
            Position from = piece.getBoardPosition();
            if (from == null) {
                continue;
            }

            // Generate both promotion and non-promotion variants up-front so move ordering can pick the best quickly
            for (Position to : piece.getLegalMoves()) {
                Piece captured = targetBoard.getPiece(to);
                boolean canPromote = piece.canPromote() &&
                    (from.inPromotionZone(sideToMove) || to.inPromotionZone(sideToMove));
                boolean mustPromote = canPromote && piece.shouldPromote(from, to);

                int priority = (captured != null ? Math.abs(captured.value()) + 100 : 0);

                if (mustPromote) {
                    moves.add(new MoveScore(new Move(player, from, to, piece, captured, true, false),
                                             addOrderingNoise(priority + 50, difficulty, applyNoise)));
                } else if (canPromote) {
                    moves.add(new MoveScore(new Move(player, from, to, piece, captured, true, false),
                                             addOrderingNoise(priority + 50, difficulty, applyNoise)));
                    moves.add(new MoveScore(new Move(player, from, to, piece, captured, false, false),
                                             addOrderingNoise(priority, difficulty, applyNoise)));
                } else {
                    moves.add(new MoveScore(new Move(player, from, to, piece, captured, false, false),
                                             addOrderingNoise(priority, difficulty, applyNoise)));
                }
            }
        }

        for (Piece handPiece : player.getHandPieces()) {
            List<Position> dropPoints = targetBoard.getPieceDropPoints(handPiece.getClass(), sideToMove);
            for (Position dropPos : dropPoints) {
                Move dropMove = new Move(
                    player,
                    handPiece.getHandPosition(),
                    dropPos,
                    handPiece,
                    null,
                    false,
                    true
                );
                moves.add(new MoveScore(dropMove, addOrderingNoise(10, difficulty, applyNoise)));
            }
        }

        moves.sort((a, b) -> Integer.compare(b.orderingScore, a.orderingScore));
        return moves;
    }

    private int addOrderingNoise(int baseScore, AIDifficulty difficulty, boolean applyNoise) {
        if (!applyNoise) {
            return Math.max(0, baseScore);
        }
        int noiseRange = difficulty.getOrderingNoise();
        if (noiseRange <= 0) {
            return Math.max(0, baseScore);
        }
        int jitter = random.nextInt(noiseRange * 2 + 1) - noiseRange;
        return Math.max(0, baseScore + jitter);
    }

    /**
     * Minimax with alpha-beta pruning
     * @param currentBoard
     * @param depth
     * @param sideToMove
     * @param alpha
     * @param beta
     * @return evaluation score
     */
    private int minimax(Board currentBoard, int depth, Sides sideToMove, Sides maximizingSide,
                        int alpha, int beta, AIDifficulty difficulty) {

        if (depth == 0) {
            return evaluate(currentBoard, maximizingSide);
        }

        List<MoveScore> moves = generateAllMoves(currentBoard, sideToMove, difficulty, false);
        boolean maximizing = sideToMove == maximizingSide;
        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        boolean exploredLegalMove = false;

        if (moves.isEmpty()) {
            return currentBoard.evals.isKingInCheck(sideToMove) ? (maximizing ? Integer.MIN_VALUE + depth : Integer.MAX_VALUE - depth) : 0;
        }

        for (MoveScore ms : moves) {
            // Apply the move directly on the shared board, evaluate, then undo to reuse state
            currentBoard.moveManager.applyMove(ms.move);
            if (currentBoard.evals.isKingInCheck(sideToMove)) {
                currentBoard.moveManager.undoMove();
                currentBoard.moveManager.getRedoStack().clear();
                continue;
            }

            exploredLegalMove = true;
            int score = minimax(currentBoard, depth - 1, sideToMove.getOpposite(), maximizingSide, alpha, beta, difficulty);

            currentBoard.moveManager.undoMove();
            currentBoard.moveManager.getRedoStack().clear();

            if (maximizing) {
                best = Math.max(best, score);
                alpha = Math.max(alpha, best);
            } else {
                best = Math.min(best, score);
                beta = Math.min(beta, best);
            }

            if (beta <= alpha) {
                break;
            }
        }

        if (!exploredLegalMove) {
            return currentBoard.evals.isKingInCheck(sideToMove)
                    ? (maximizing ? Integer.MIN_VALUE + depth : Integer.MAX_VALUE - depth)
                    : 0;
        }

        return best;
    }

    /**
     * Evaluate score
     * @param board
     * @param maximizingSide
     * @return evaluation score
     */
    private int evaluate(Board board, Sides maximizingSide) {
        int score = 0;

        // Only run expensive mate checks when a king is already in check, otherwise rely on material heuristics
        boolean maxInCheck = board.evals.isKingInCheck(maximizingSide);
        boolean oppInCheck = board.evals.isKingInCheck(maximizingSide.getOpposite());

        if (oppInCheck && board.evals.isCheckMate(maximizingSide.getOpposite())) {
            return Integer.MAX_VALUE - 1;
        }
        if (maxInCheck && board.evals.isCheckMate(maximizingSide)) {
            return Integer.MIN_VALUE + 1;
        }

        // Apply check bonuses/penalties
        if (maxInCheck) {
            score -= 200;
        }
        if (oppInCheck) {
            score += 300;
        }

        // Material evaluation - optimized to iterate once
        for (Piece piece : board.getAllPieces()) {
            int mobilityBonus = piece.getLegalMoves().size() * 3;
            int positionBonus = evaluatePiecePosition(piece, maximizingSide);
            int promotionBonus = 0;
            
            // Check promotion potential
            if (piece.canPromote() && piece.getBoardPosition().inPromotionZone(piece.getSide())) {
                promotionBonus = 100;
            }
            
            int pieceScore = piece.value() + mobilityBonus + positionBonus + promotionBonus;
            score += piece.getSide() == maximizingSide ? pieceScore : -pieceScore;
        }

        return score;
    }

    private int evaluatePiecePosition(Piece piece, Sides maximizingSide) {
        int score = 0;
        int x = piece.getBoardPosition().x;
        int y = piece.getBoardPosition().y;

        if (x >= 3 && x <= 5 && y >= 3 && y <= 5) {
            score += 20;
        }
        if (piece.canPromote() && piece.getBoardPosition().inPromotionZone(maximizingSide)) {
            score += 30;
        }

        return score;
    }
}
