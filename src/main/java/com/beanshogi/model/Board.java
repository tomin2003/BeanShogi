package com.beanshogi.model;

import java.util.*;
import java.util.stream.Collectors;

import com.beanshogi.pieces.normal.King;
import com.beanshogi.pieces.normal.Knight;
import com.beanshogi.pieces.normal.Pawn;
import com.beanshogi.pieces.normal.slider.Lance;
import com.beanshogi.util.*;

/**
 * Class representing a game board.
 * @param board 9x9 2D array for a shogi board.
 * @param kings a HashMap for the 2 kings represented on the board, so that they don't have to be searched for on the board
 */
public class Board {
    private final Piece[][] board = new Piece[9][9];
    private final Map<Colors, King> kings = new HashMap<>();

    // --Methods-- //
    public Piece getPiece(Position pos) {
        return board[pos.getX()][pos.getY()];
    }

    public King getKing(Colors color) {
        return kings.get(color);
    }

    public void setPiece(Position pos, Piece piece) {
        board[pos.getX()][pos.getY()] = piece;
        if (piece != null) {
            piece.setPosition(pos);
        }
        if (piece instanceof King) {
            kings.put(piece.getColor(), (King)piece);
        }
    }

    public void removePiece(Position pos) {
        int x = pos.getX();
        int y = pos.getY();
        Piece removed = board[x][y];
        board[x][y] = null;
        if (removed instanceof King) {
            kings.remove(removed.getColor());
        }
    }
    
    public boolean isEmptyAt(Position pos) {
        return board[pos.getX()][pos.getY()] == null;
    }
    
    public void clear() {
        for (int i = 0; i < 9; i++) {
            Arrays.fill(board[i], null);
        }
        kings.clear();
    }

    // Get pieces via stream
    public List<Piece> getAllPieces() {
        List<Piece> pieces = new ArrayList<>();
        for (Piece[] row : board) {
            for (Piece piece : row) {
                pieces.add(piece);
            }
        }
        return pieces;
    }

    // Get pieces via stream and also filter for color
    public Collection<Piece> getPiecesByColor(Colors color) {
        return getAllPieces().stream()
                    .filter(p -> p.getColor() == color)
                    .collect(Collectors.toList());
    }

    // Generic search for piece type with stream
    public <T extends Piece> T findPiece(Class<T> pieceClass, Colors pieceColor) {
        return getPiecesByColor(pieceColor).stream()
                        .filter(pieceClass::isInstance)
                        .map(pieceClass::cast)
                        .findFirst()
                        .orElse(null);
    }

    // Get a hashset of drop points
    public <T extends Piece> Set<Position> getPieceDropPoints(Class<T> pieceClass, Colors pieceColor) {
        Set<Position> dropPoints = new HashSet<>();

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
            Position pos = new Position(x,y);
                if (!isEmptyAt(pos)) {
                    continue;
                }
                // Pawn specific rule: no two pawns in the same column
                if (pieceClass == Pawn.class) {
                    boolean sameColumnPawns = false;
                    for (int i = 0; i < 9; i++) {
                        Piece p = board[x][i];
                        if (p instanceof Pawn && p.getColor() == pieceColor) {
                            sameColumnPawns = true;
                            break;
                        }
                    }
                    // Pawns are on the same column, cannot move here
                    if (sameColumnPawns) {
                        continue;
                    }
                    // Piece cannot be dropped past the drop zone
                    if ((pieceColor == Colors.BLACK && y == 0) || (pieceColor == Colors.WHITE && y == 8)) {
                        continue;
                    }
                }   
                if (pieceClass == Lance.class) {
                    if ((pieceColor == Colors.BLACK && y == 0) || (pieceColor == Colors.WHITE && y == 8)) {
                        continue;
                    }
                }
                if (pieceClass == Knight.class) {
                    if ((pieceColor == Colors.BLACK && y <= 1) || (pieceColor == Colors.WHITE && y >= 7)) {
                        continue;
                    }
                }
                dropPoints.add(pos);
            }
        }
        return dropPoints;
    }
}