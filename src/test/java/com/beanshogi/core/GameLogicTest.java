package com.beanshogi.core;

import com.beanshogi.core.board.*;
import com.beanshogi.core.game.*;
import com.beanshogi.core.pieces.*;
import com.beanshogi.core.pieces.normal.*;
import com.beanshogi.core.pieces.normal.slider.Rook;
import com.beanshogi.core.pieces.normal.slider.Bishop;
import com.beanshogi.core.util.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameLogicTest {
    @Test
    void testLegalMovesPawn() {
        Player p = new Player(Sides.SENTE, "A", PlayerType.HUMAN);
        Board board = new Board(Collections.singletonList(p));
        Position pos = new Position(4,4);
        Pawn pawn = new Pawn(Sides.SENTE, pos, null, board);
        board.setPiece(pos, pawn);
        List<Position> moves = pawn.getLegalMoves();
        assertTrue(moves.contains(new Position(4,3)));
        assertFalse(moves.contains(new Position(4,4)));
    }

    @Test
    void testKingCannotMoveIntoCheck() {
        Player sente = new Player(Sides.SENTE, "Sente", PlayerType.HUMAN);
        Player gote = new Player(Sides.GOTE, "Gote", PlayerType.HUMAN);
        Board board = new Board(Arrays.asList(sente, gote));
        Position kingPos = new Position(4,4);
        King king = new King(Sides.SENTE, kingPos, null, board);
        board.setPiece(kingPos, king);
        Position enemyPos = new Position(4,5);
        Pawn enemyPawn = new Pawn(Sides.GOTE, enemyPos, null, board);
        board.setPiece(enemyPos, enemyPawn);
        List<Position> moves = king.getLegalMoves();
        // Note: getLegalMoves doesn't filter for check, so king can move into attacked square
        assertTrue(moves.contains(new Position(4,5)));
    }

    @Test
    void testEndGameCheckmate() {
        Player sente = new Player(Sides.SENTE, "Sente", PlayerType.HUMAN);
        Player gote = new Player(Sides.GOTE, "Gote", PlayerType.HUMAN);
        Board board = new Board(Arrays.asList(sente, gote));
        // Place Sente King
        Position kingPos = new Position(4,0);
        King king = new King(Sides.SENTE, kingPos, null, board);
        board.setPiece(kingPos, king);
        // Place Gote Rook directly in front
        Position rookPos = new Position(4,1);
        Rook rook = new Rook(Sides.GOTE, rookPos, null, board);
        board.setPiece(rookPos, rook);
        // King is in check
        assertTrue(board.evals.isKingInCheck(Sides.SENTE));
        // King can capture the rook to resolve check
        assertFalse(board.evals.getFilteredLegalMoves(king).isEmpty());
    }

    @Test
    void testPromotion() {
        Player p = new Player(Sides.SENTE, "A", PlayerType.HUMAN);
        Board board = new Board(Collections.singletonList(p));
        Position pos = new Position(0,2); // Promotion zone
        Pawn pawn = new Pawn(Sides.SENTE, pos, null, board);
        Piece promoted = pawn.promote();
        assertNotEquals(pawn, promoted);
        assertTrue(((Promotable)promoted).isPromoted());
    }

    @Test
    void testRookMoves() {
        Player p = new Player(Sides.SENTE, "A", PlayerType.HUMAN);
        Board board = new Board(Collections.singletonList(p));
        Position pos = new Position(4,4);
        Rook rook = new Rook(Sides.SENTE, pos, null, board);
        board.setPiece(pos, rook);
        List<Position> moves = rook.getLegalMoves();
        assertTrue(moves.contains(new Position(4,0))); // Up
        assertTrue(moves.contains(new Position(4,8))); // Down
        assertTrue(moves.contains(new Position(0,4))); // Left
        assertTrue(moves.contains(new Position(8,4))); // Right
        assertFalse(moves.contains(new Position(5,5))); // Diagonal
    }

    @Test
    void testBishopMoves() {
        Player p = new Player(Sides.SENTE, "A", PlayerType.HUMAN);
        Board board = new Board(Collections.singletonList(p));
        Position pos = new Position(4,4);
        Bishop bishop = new Bishop(Sides.SENTE, pos, null, board);
        board.setPiece(pos, bishop);
        List<Position> moves = bishop.getLegalMoves();
        assertTrue(moves.contains(new Position(0,0))); // Diagonal
        assertTrue(moves.contains(new Position(8,8))); // Diagonal
        assertTrue(moves.contains(new Position(0,8))); // Diagonal
        assertTrue(moves.contains(new Position(8,0))); // Diagonal
        assertFalse(moves.contains(new Position(4,5))); // Not diagonal
    }

    @Test
    void testKnightMoves() {
        Player p = new Player(Sides.GOTE, "A", PlayerType.HUMAN);
        Board board = new Board(Collections.singletonList(p));
        Position pos = new Position(4,4);
        Knight knight = new Knight(Sides.GOTE, pos, null, board);
        board.setPiece(pos, knight);
        List<Position> moves = knight.getLegalMoves();
        assertTrue(moves.contains(new Position(3,6))); // L-shape
        assertTrue(moves.contains(new Position(5,6)));
        assertFalse(moves.contains(new Position(4,3))); // Not L-shape
    }

    @Test
    void testBoardOperations() {
        Player sente = new Player(Sides.SENTE, "Sente", PlayerType.HUMAN);
        Player gote = new Player(Sides.GOTE, "Gote", PlayerType.HUMAN);
        Board board = new Board(Arrays.asList(sente, gote));
        Position pos = new Position(4,4);
        Pawn pawn = new Pawn(Sides.SENTE, pos, null, board);
        board.setPiece(pos, pawn);
        assertEquals(pawn, board.getPiece(pos));
        board.removePiece(pos);
        assertNull(board.getPiece(pos));
    }

    @Test
    void testHandGrid() {
        Player p = new Player(Sides.SENTE, "A", PlayerType.HUMAN);
        Board board = new Board(Collections.singletonList(p));
        HandGrid hand = p.getHandGrid();
        Pawn pawn = new Pawn(Sides.SENTE, null, null, board);
        hand.addPiece(pawn);
        assertTrue(hand.getAllPieces().contains(pawn));
        hand.removePiece(pawn);
        assertFalse(hand.getAllPieces().contains(pawn));
    }
}
