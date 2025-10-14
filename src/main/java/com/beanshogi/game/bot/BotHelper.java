package com.beanshogi.game.bot;

import java.util.*;

import com.beanshogi.model.Board;
import com.beanshogi.model.Piece;
import com.beanshogi.util.*;

public class BotHelper {
    private Board board;

    public BotHelper(Board board) {
        this.board = board;
    }
    
    public HashMap<Piece, Collection<Position>> getAllLegalMoves() {
        // Hashmap for storing Piece-Position mappings
        HashMap<Piece, Collection<Position>> legalMoves = new HashMap<>();
        // Place the legal moves of all white pieces into the HashMap
        for (Piece piece : board.getPiecesByColor(Colors.WHITE)) {
            legalMoves.put(piece, piece.getLegalMoves());
        }
        return legalMoves;
    }

    int minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        // TODO: if (depth == 0 || isGameOver) return eval(board);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            // TODO: recursive logic /w unto/redo stacks
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            // TODO: recursive logic /w undo/redo stacks
            return minEval;
        }
    }

}
 