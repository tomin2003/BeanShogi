package com.beanshogi.game;

import com.beanshogi.util.*;

import java.util.List;

import com.beanshogi.model.*;
import com.beanshogi.pieces.normal.GoldGeneral;
import com.beanshogi.pieces.normal.King;
import com.beanshogi.pieces.normal.Knight;
import com.beanshogi.pieces.normal.Pawn;
import com.beanshogi.pieces.normal.SilverGeneral;
import com.beanshogi.pieces.normal.slider.Bishop;
import com.beanshogi.pieces.normal.slider.Lance;
import com.beanshogi.pieces.normal.slider.Rook;

public class Game {
    private Board board;
    private Sides nextTurn = Sides.SENTE;
    
    public Game(List<Player> players) {

        // Clear hand before starting a new game (eg. on a loaded game)
        for (Player player : players) {
            player.getHandGrid().clear();
        }

        this.board = new Board(players);
        this.nextTurn = Sides.SENTE;
        // Initialize board by entire rows - boardPosition value is set in setPiece()
        for (int i = 0; i < 9; i++) {
            if (i == 0 || i == 8) {
                board.setPiece(new Position(i, 0), new Lance(Sides.GOTE, null, null, board));
                board.setPiece(new Position(i, 8), new Lance(Sides.SENTE, null, null, board));
            }
            if (i == 1 || i == 7) {
                board.setPiece(new Position(i, 0), new Knight(Sides.GOTE, null, null, board));
                board.setPiece(new Position(i, 8), new Knight(Sides.SENTE, null, null, board));
                if (i == 1) { 
                    board.setPiece(new Position(i, 1), new Rook(Sides.GOTE, null, null, board));
                    board.setPiece(new Position(i, 7), new Bishop(Sides.SENTE, null, null, board));
                }
                if (i == 7) { 
                    board.setPiece(new Position(i, 1), new Bishop(Sides.GOTE, null, null, board));
                    board.setPiece(new Position(i, 7), new Rook(Sides.SENTE, null, null, board));
                }
            }
            if (i == 2 || i == 6) {
                board.setPiece(new Position(i, 0), new GoldGeneral(Sides.GOTE, null, null, board));
                board.setPiece(new Position(i, 8), new GoldGeneral(Sides.SENTE, null, null, board));
            }
            if (i == 3 || i == 5) {
                board.setPiece(new Position(i, 0), new SilverGeneral(Sides.GOTE, null, null, board));
                board.setPiece(new Position(i, 8), new SilverGeneral(Sides.SENTE, null, null, board));
            }
            if (i == 4) {
                board.setPiece(new Position(i, 0), new King(Sides.GOTE, null, null, board));
                board.setPiece(new Position(i, 8), new King(Sides.SENTE, null, null, board));
            }
            board.setPiece(new Position(i, 2), new Pawn(Sides.GOTE, null, null, board));
            board.setPiece(new Position(i, 6), new Pawn(Sides.SENTE, null, null, board));
        }
    }
    public Board getBoard() {
        return board;
    }

    public Sides getNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(Sides nextTurn) {
        this.nextTurn = nextTurn;
    }

    public static Game getInstance() {
        throw new UnsupportedOperationException("Unimplemented method 'getInstance'");
    }
} 