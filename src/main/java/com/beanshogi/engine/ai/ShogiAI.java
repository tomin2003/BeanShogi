package com.beanshogi.engine.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.beanshogi.model.*;
import com.beanshogi.util.Position;
import com.beanshogi.util.Sides;

public class ShogiAI {

    private final int MAX_DEPTH = 3;
    private final Board board;

    public ShogiAI(Board board) {
        this.board = board;
    }

    /**
     * Returns the best move (normal or drop) for the given side.
     */
    public Move getBestMove(Sides sideToMove) {
        int bestScore = sideToMove == Sides.SENTE ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Move bestMove = null;

        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Callable<MoveScore>> tasks = new ArrayList<>();

        // Normal moves without drop
        for (Piece piece : board.getPiecesOfSide(sideToMove)) {
            Position from = piece.getBoardPosition();
            for (Position to : piece.getLegalMoves()) {
                final Position fFrom = from;
                final Position fTo = to;

                tasks.add(() -> {
                    Board simBoard = board.copy();
                    Piece simPiece = simBoard.getPiece(fFrom);

                    boolean promotion = false;
                    if (simPiece.canPromote() && fTo.inPromotionZone(sideToMove)) {
                        promotion = true; // can be improved later with conditional promotion
                    }

                    Piece captured = board.getPiece(fTo);
                    Move move = new Move(
                        simBoard.getPlayer(sideToMove), 
                        fFrom, 
                        fTo, 
                        simPiece, 
                        captured, 
                        promotion,
                        false
                    );
                    simBoard.moveManager.applyMove(move);

                    int score = minimax(simBoard, MAX_DEPTH - 1, sideToMove.getOpposite(),
                                        Integer.MIN_VALUE, Integer.MAX_VALUE);
                    return new MoveScore(move, score);
                });
            }
        }

        // Drops from hand
        List<Piece> handPieces = board.getPlayer(sideToMove).getHand();
        for (Piece handPiece : handPieces) {
            List<Position> dropPoints = board.getPieceDropPoints(handPiece.getClass(), sideToMove);
            for (Position dropPos : dropPoints) {
                final Piece fHandPiece = handPiece;
                final Position fDropPos = dropPos;

                tasks.add(() -> {
                    Board simBoard = board.copy();
                    Piece simPiece = simBoard.getPlayer(sideToMove)
                                            .getHandGrid()
                                            .findAndRemoveMatching(fHandPiece);

                    if (simPiece == null) return null; // should not happen

                    simBoard.setPiece(fDropPos, simPiece);

                    Move dropMove = new Move(
                        simBoard.getPlayer(sideToMove),
                        fDropPos, 
                        fDropPos,
                        simPiece, 
                        null, 
                        false,
                        true
                    );
                    simBoard.moveManager.getUndoStack().push(dropMove);
                    simBoard.moveManager.getRedoStack().clear();

                    int score = minimax(simBoard, MAX_DEPTH - 1, sideToMove.getOpposite(),
                                        Integer.MIN_VALUE, Integer.MAX_VALUE);
                    return new MoveScore(dropMove, score);
                });
            }
        }

        try {
            List<Future<MoveScore>> futures = exec.invokeAll(tasks);
            exec.shutdown();
            exec.awaitTermination(10, TimeUnit.SECONDS);

            for (Future<MoveScore> fut : futures) {
                MoveScore ms = fut.get();
                if (ms == null) continue;

                int score = ms.score;

                if (sideToMove == Sides.SENTE && score > bestScore) {
                    bestScore = score;
                    bestMove = ms.move;
                } else if (sideToMove == Sides.GOTE && score < bestScore) {
                    bestScore = score;
                    bestMove = ms.move;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bestMove;
    }

    private static class MoveScore {
        final Move move;
        final int score;
        MoveScore(Move m, int s) { move = m; score = s; }
    }

    /**
     * 
     * @param currentBoard
     * @param depth
     * @param sideToMove
     * @param alpha
     * @param beta
     * @return
     */
    private int minimax(Board currentBoard, int depth, Sides sideToMove, int alpha, int beta) {
        if (depth == 0) return evaluate(currentBoard, sideToMove);

        boolean maximizing = sideToMove == Sides.SENTE;
        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        // 1. Normal moves
        for (Piece piece : currentBoard.getPiecesOfSide(sideToMove)) {
            Position from = piece.getBoardPosition();
            for (Position to : piece.getLegalMoves()) {
                Board simBoard = currentBoard.copy();
                Piece simPiece = simBoard.getPiece(from);

                boolean promotion = false;
                if (simPiece.canPromote() && to.inPromotionZone(sideToMove)) promotion = true;

                Piece captured = board.getPiece(to);
                Move move = new Move(simBoard.getPlayer(sideToMove), from, to, simPiece, captured, promotion, false);
                simBoard.moveManager.applyMove(move);

                int score = minimax(simBoard, depth - 1, sideToMove.getOpposite(), alpha, beta);

                if (maximizing) {
                    best = Math.max(best, score);
                    alpha = Math.max(alpha, best);
                    if (beta <= alpha) break;
                } else {
                    best = Math.min(best, score);
                    beta = Math.min(beta, best);
                    if (beta <= alpha) break;
                }
            }
        }

        // 2. Hand drops
        for (Piece handPiece : currentBoard.getPlayer(sideToMove).getHand()) {
            List<Position> dropPoints = currentBoard.getPieceDropPoints(handPiece.getClass(), sideToMove);
            for (Position dropPos : dropPoints) {
                Board simBoard = currentBoard.copy();
                Piece simPiece = simBoard.getPlayer(sideToMove)
                                        .getHandGrid()
                                        .findAndRemoveMatching(handPiece);
                if (simPiece == null) continue;

                simBoard.setPiece(dropPos, simPiece);

                Move dropMove = new Move(simBoard.getPlayer(sideToMove),
                                         dropPos, 
                                         dropPos, 
                                         simPiece, 
                                         null, 
                                         false,
                                         true);
                simBoard.moveManager.getUndoStack().push(dropMove);
                simBoard.moveManager.getRedoStack().clear();

                int score = minimax(simBoard, depth - 1, sideToMove.getOpposite(), alpha, beta);

                if (maximizing) {
                    best = Math.max(best, score);
                    alpha = Math.max(alpha, best);
                    if (beta <= alpha) break;
                } else {
                    best = Math.min(best, score);
                    beta = Math.min(beta, best);
                    if (beta <= alpha) break;
                }
            }
        }

        return best;
    }

    /**
     * Evaluate score
     * @param board
     * @param maximizingSide
     * @return
     */
    private int evaluate(Board board, Sides maximizingSide) {
        int score = 0;

        if (board.evals.isCheckMate(maximizingSide.getOpposite())) {
            return Integer.MAX_VALUE - 1;
        }
        if (board.evals.isCheckMate(maximizingSide)) {
            return Integer.MIN_VALUE + 1;
        }

        if (board.evals.isKingInCheck(maximizingSide)) {
            score -= 200;
        }
        if (board.evals.isKingInCheck(maximizingSide.getOpposite())) {
            score += 300;
        }

        // Material evaluation
        for (Piece piece : board.getAllPieces()) {
            int pieceScore = piece.value() + piece.getLegalMoves().size() * 3 + evaluatePiecePosition(piece, maximizingSide);
            score += piece.getSide() == maximizingSide ? pieceScore : -pieceScore;
        }

        // Promotion bonuses
        for (Piece piece : board.getPiecesOfSide(maximizingSide)) {
            if (piece.canPromote() && piece.getBoardPosition().inPromotionZone(maximizingSide)) score += 100;
        }
        for (Piece piece : board.getPiecesOfSide(maximizingSide.getOpposite())) {
            if (piece.canPromote() && piece.getBoardPosition().inPromotionZone(maximizingSide.getOpposite())) score -= 100;
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
