package com.beanshogi.game;

import com.beanshogi.util.*;
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
    private Player player1;
    private Player player2;

    public Game() {
        this.board = new Board();
        // Initialize board by entire rows
        for (int i = 0; i < 9; i++) {
            if (i == 0 || i == 8) {
                board.setPiece(new Position(i, 0), new Lance(Sides.GOTE, null, board));
                board.setPiece(new Position(i, 8), new Lance(Sides.SENTE, null, board));
            }
            if (i == 1 || i == 7) {
                board.setPiece(new Position(i, 0), new Knight(Sides.GOTE, null, board));
                board.setPiece(new Position(i, 8), new Knight(Sides.SENTE, null, board));
                if (i == 1) { 
                    board.setPiece(new Position(i, 1), new Rook(Sides.GOTE, null, board));
                    board.setPiece(new Position(i, 7), new Bishop(Sides.SENTE, null, board));
                }
                if (i == 7) { 
                    board.setPiece(new Position(i, 1), new Bishop(Sides.GOTE, null, board));
                    board.setPiece(new Position(i, 7), new Rook(Sides.SENTE, null, board));
                }
            }
            if (i == 2 || i == 6) {
                board.setPiece(new Position(i, 0), new GoldGeneral(Sides.GOTE, null, board));
                board.setPiece(new Position(i, 8), new GoldGeneral(Sides.SENTE, null, board));
            }
            if (i == 3 || i == 5) {
                board.setPiece(new Position(i, 0), new SilverGeneral(Sides.GOTE, null, board));
                board.setPiece(new Position(i, 8), new SilverGeneral(Sides.SENTE, null, board));
            }
            if (i == 4) {
                board.setPiece(new Position(i, 0), new King(Sides.GOTE, null, board));
                board.setPiece(new Position(i, 8), new King(Sides.SENTE, null, board));
            }
            board.setPiece(new Position(i, 2), new Pawn(Sides.GOTE, null, board));
            board.setPiece(new Position(i, 6), new Pawn(Sides.SENTE, null, board));
        }
        player1 = new Player(Sides.GOTE, PlayerType.HUMAN);
        player2 = new Player(Sides.SENTE, PlayerType.HUMAN);
    }

    public Player getPlayer(Sides side) {
        return player1.getSide() == side ? player1 : player2;
    }

    public Board getBoard() {
        return board;
    }
} 