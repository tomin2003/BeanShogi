package com.beanshogi.model;

import java.util.*;
import java.util.stream.Collectors;

import com.beanshogi.engine.Evals;
import com.beanshogi.engine.MoveManager;
import com.beanshogi.game.Player;
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
    private final List<Player> players;
    public final MoveManager moveManager;
    public final Evals evals;

    public Board(List<Player> players) {
        this.moveManager = new MoveManager(this);
        this.evals = new Evals(this);
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getPlayer(Sides side) {
        for (Player player : players) {
            if (player.getSide() == side) {
                return player;
            }
        }
        throw new IllegalArgumentException("No player with side: " + side);
    }

    public Piece getPiece(Position pos) {
        return board[pos.x][pos.y];
    }

    public King getKing(Sides side) {
        return kings.get(side);
    }

    public void setPiece(Position pos, Piece piece) {
        board[pos.x][pos.y] = piece;
        if (piece != null) {
            piece.setBoardPosition(pos);
        }
        if (piece instanceof King) {
            kings.put(piece.getSide(), (King)piece);
        }
    }

    public void removePiece(Position pos) {
        int x = pos.x;
        int y = pos.y;
        Piece removed = board[x][y];
        board[x][y] = null;
        if (removed instanceof King) {
            kings.remove(removed.getSide());
        }
    }
    
    public boolean isEmptyAt(Position pos) {
        return board[pos.x][pos.y] == null;
    }
    
    public void clear() {
        for (int i = 0; i < 9; i++) {
            Arrays.fill(board[i], null);
        }
        kings.clear();
    }

    // Get all pieces
    public List<Piece> getAllPieces() {
        List<Piece> pieces = new ArrayList<>();
        for (Piece[] row : board) {
            for (Piece piece : row) {
                if (piece == null) {
                    continue;
                }
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

    // TODO: Add uchifuzume, possibly refactor
    public <T extends Piece> List<Position> getPieceDropPoints(Class<T> pieceClass, Sides pieceside) {
        Set<Position> dropPoints = new HashSet<>();

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
            Position pos = new Position(x,y);
                if (!isEmptyAt(pos)) {
                    continue;
                }
                if (pieceClass == Pawn.class) {
                    // Pawn specific rule (nifu): no two pawns in the same column
                    boolean nifu = false;
                    for (int i = 0; i < 9; i++) {
                        Piece p = board[x][i];
                        if (p instanceof Pawn && p.getSide() == pieceside) {
                            nifu = true;
                            break;
                        }
                    }
                    // Pawns are on the same column, cannot move here
                    if (nifu) {
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
        return new ArrayList<>(dropPoints);
    }

    /**
     * Get a deep copy of a board
     * @return copy of current board
     */
    public Board copy() {
        // Create new player list and board so players/hands are not shared between copies
        List<Player> newPlayers = new ArrayList<>();
        Board newBoard = new Board(newPlayers);

        // Create empty player instances corresponding to original players
        for (Player p : this.players) {
            Player np = new Player(p.getSide(), p.getName(), p.getType(), p.getDifficulty());
            newPlayers.add(np);
        }

        // Copy board pieces into newBoard
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = this.board[x][y];
                if (p != null) {
                    Piece clone = p.cloneForBoard(newBoard);
                    clone.setBoardPosition(new Position(x, y)); // make sure position is correct
                    newBoard.setPiece(clone.getBoardPosition(), clone);
                    if (clone instanceof King) {
                        newBoard.kings.put(clone.getSide(), (King) clone);
                    }
                }
            }
        }

        // Copy captured pieces (player hands) into their corresponding new players
        for (Player orig : this.players) {
            Player target = newBoard.getPlayer(orig.getSide());
            HandGrid origGrid = orig.getHandGrid();
            HandGrid targetGrid = target.getHandGrid();
            
            // Deep copy the hand grid
            HandGrid copiedGrid = origGrid.copy(newBoard);
            // Replace the target's hand grid
            for (Piece piece : copiedGrid.getAllPieces()) {
                targetGrid.addPiece(piece);
            }
        }

        return newBoard;
    }
}