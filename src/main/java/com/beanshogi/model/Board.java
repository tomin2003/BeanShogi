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
    private final Map<Sides, King> kings = new HashMap<>();

    // --Methods-- //
    public Piece getPiece(Position pos) {
        return board[pos.getX()][pos.getY()];
    }

    public King getKing(Sides side) {
        return kings.get(side);
    }

    public void setPiece(Position pos, Piece piece) {
        board[pos.getX()][pos.getY()] = piece;
        if (piece != null) {
            piece.setPosition(pos);
        }
        if (piece instanceof King) {
            kings.put(piece.getSide(), (King)piece);
        }
    }

    public void removePiece(Position pos) {
        int x = pos.getX();
        int y = pos.getY();
        Piece removed = board[x][y];
        board[x][y] = null;
        if (removed instanceof King) {
            kings.remove(removed.getSide());
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

    public boolean isPromotionZone(Position to, Sides sideToCheck) {
        if (sideToCheck == Sides.SENTE) {
            return to.getY() >= 6;
        } else {
            return to.getY() <= 2;
        }
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

    // Get pieces via stream and also filter for side
    public Collection<Piece> getPiecesOfSide(Sides side) {
        return getAllPieces().stream()
                    .filter(p -> p.getSide() == side)
                    .collect(Collectors.toList());
    }

    // Generic search for piece type with stream
    public <T extends Piece> T findPiece(Class<T> pieceClass, Sides pieceside) {
        return getPiecesOfSide(pieceside).stream()
                        .filter(pieceClass::isInstance)
                        .map(pieceClass::cast)
                        .findFirst()
                        .orElse(null);
    }

    // Get a hashset of drop points
    public <T extends Piece> Set<Position> getPieceDropPoints(Class<T> pieceClass, Sides pieceside) {
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
                        if (p instanceof Pawn && p.getSide() == pieceside) {
                            sameColumnPawns = true;
                            break;
                        }
                    }
                    // Pawns are on the same column, cannot move here
                    if (sameColumnPawns) {
                        continue;
                    }
                    // Piece cannot be dropped past the drop zone
                    if ((pieceside == Sides.SENTE && y == 0) || (pieceside == Sides.GOTE && y == 8)) {
                        continue;
                    }
                }   
                if (pieceClass == Lance.class) {
                    if ((pieceside == Sides.SENTE && y == 0) || (pieceside == Sides.GOTE && y == 8)) {
                        continue;
                    }
                }
                if (pieceClass == Knight.class) {
                    if ((pieceside == Sides.SENTE && y <= 1) || (pieceside == Sides.GOTE && y >= 7)) {
                        continue;
                    }
                }
                dropPoints.add(pos);
            }
        }
        return dropPoints;
    }

    /**
     * Get a deep copy of a board
     * @return copy of current board
     */
    public Board copy() {   
        Board newBoard = new Board();
        // Copy the pieces on the board as well, fill the board copy with said copied pieces
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = board[x][y];
                if (p != null) {
                    Piece clone = p.cloneForBoard(newBoard);
                    newBoard.setPiece(new Position(x, y), clone);
                }
            }
        }
        return newBoard;
    }
}