package com.beanshogi.core;

import com.beanshogi.core.board.*;
import com.beanshogi.core.game.*;
import com.beanshogi.core.pieces.*;
import com.beanshogi.core.pieces.normal.*;
import com.beanshogi.core.pieces.normal.slider.Rook;
import com.beanshogi.core.pieces.promoted.slider.PromotedRook;
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

    @Test
    void testKingInCheck() {
        Player sente = new Player(Sides.SENTE, "Sente", PlayerType.HUMAN);
        Player gote = new Player(Sides.GOTE, "Gote", PlayerType.HUMAN);
        Board board = new Board(Arrays.asList(sente, gote));
        // Place Sente King
        Position kingPos = new Position(4, 4);
        King king = new King(Sides.SENTE, kingPos, null, board);
        board.setPiece(kingPos, king);
        // Place Gote Rook attacking the king
        Position rookPos = new Position(4, 0);
        Rook rook = new Rook(Sides.GOTE, rookPos, null, board);
        board.setPiece(rookPos, rook);
        // King should be in check
        assertTrue(board.evals.isKingInCheck(Sides.SENTE));
    }

    @Test
    void testKingCannotMoveIntoCheck() {
        Player sente = new Player(Sides.SENTE, "Sente", PlayerType.HUMAN);
        Player gote = new Player(Sides.GOTE, "Gote", PlayerType.HUMAN);
        Board board = new Board(Arrays.asList(sente, gote));
        // Place Sente King
        Position kingPos = new Position(4, 4);
        King king = new King(Sides.SENTE, kingPos, null, board);
        board.setPiece(kingPos, king);
        // Place Gote Rook attacking the king
        Position rookPos = new Position(3, 0);
        Rook rook = new Rook(Sides.GOTE, rookPos, null, board);
        board.setPiece(rookPos, rook);
        // King should not yet be in check
        assertFalse(board.evals.isKingInCheck(Sides.SENTE));
        assertFalse(board.evals.getFilteredLegalMoves(king).contains(new Position (3,4))); // Moving into check not allowed
    }

    @Test
    void testCheckAvoid() {
        Player sente = new Player(Sides.SENTE, "Sente", PlayerType.HUMAN);
        Player gote = new Player(Sides.GOTE, "Gote", PlayerType.HUMAN);
        Board board = new Board(Arrays.asList(sente, gote));
        // Place gote King
        Position kingPos = new Position(4, 0);
        King king = new King(Sides.GOTE, kingPos, null, board);
        board.setPiece(kingPos, king);

        // Place sente Rook attacking the king head on
        Position headOn = new Position(4, 4);
        Rook headOnRook = new Rook(Sides.SENTE, headOn, null, board);
        board.setPiece(headOn, headOnRook);

        // Place sente Rook diagonal to the square in front of king
        Position diagonal = new Position(0, 1);
        Rook diagonalRook = new Rook(Sides.SENTE, diagonal, null, board);
        board.setPiece(diagonal, diagonalRook);

        // King can avoid check by moving
        assertTrue(board.evals.isKingInCheck(Sides.GOTE));
        assertTrue(board.evals.getFilteredLegalMoves(king).contains(new Position (3,0))); // Avoid left
        assertTrue(board.evals.getFilteredLegalMoves(king).contains(new Position (5,0))); // Avoid right
        assertTrue(board.evals.getFilteredLegalMoves(king).size() == 2); // Only two moves available
    }

    @Test
    void testCheckBlock() {
        Player sente = new Player(Sides.SENTE, "Sente", PlayerType.HUMAN);
        Player gote = new Player(Sides.GOTE, "Gote", PlayerType.HUMAN);
        Board board = new Board(Arrays.asList(sente, gote));
        // Place gote King
        Position kingPos = new Position(4, 0);
        King king = new King(Sides.GOTE, kingPos, null, board);
        board.setPiece(kingPos, king);

        // Place sente Rook attacking the king head on
        Position headOn = new Position(4, 4);
        Rook headOnRook = new Rook(Sides.SENTE, headOn, null, board);
        board.setPiece(headOn, headOnRook);

        assertTrue(board.evals.isKingInCheck(Sides.GOTE));

        // Place gote rook to block the check
        Position blockRookPos = new Position(1, 1);
        Rook blockRook = new Rook(Sides.GOTE, blockRookPos, null, board);
        board.setPiece(blockRookPos, blockRook);
        
        assertTrue(board.evals.getFilteredLegalMoves(blockRook).contains(new Position(4,1))); // Blocking the check
        assertFalse(board.evals.getFilteredLegalMoves(blockRook).contains(new Position(0,1))); // Cannot move to other positions now
        board.moveManager.applyMove(new Move(gote, blockRookPos, new Position(4,1), blockRook, null, false, false));

        // King should no longer be in check
        assertFalse(board.evals.isKingInCheck(Sides.GOTE));

        board.moveManager.undoMove();

        // King should be back in check
        assertTrue(board.evals.isKingInCheck(Sides.GOTE));

        // Try to block check with drop
        Piece pawn = new Pawn(Sides.GOTE, null, null, board);
        sente.addToHand(pawn);
        Position dropPos = new Position(4,2);
        assertFalse(board.getPieceDropPoints(Pawn.class, Sides.GOTE).contains(new Position(3,2))); // Cannot drop elsewhere (other column)
        board.moveManager.applyMove(new Move(sente, pawn.getHandPosition(), dropPos, pawn, null, false, true));

        // King should no longer be in check
        assertFalse(board.evals.isKingInCheck(Sides.GOTE));
    }

    @Test
    void testCheckmate() {
        Player sente = new Player(Sides.SENTE, "Sente", PlayerType.HUMAN);
        Player gote = new Player(Sides.GOTE, "Gote", PlayerType.HUMAN);
        Board board = new Board(Arrays.asList(sente, gote));
        // Place Sente King in the corner
        Position kingPos = new Position(4, 0);
        King king = new King(Sides.GOTE, kingPos, null, board);
        board.setPiece(kingPos, king);
        // Place two sente rooks and misc. non pawn piece to deliver checkmate
        Position promotedRookPos1 = new Position(2, 1);
        PromotedRook promotedRook1 = new PromotedRook(Sides.SENTE, promotedRookPos1, null, board);
        board.setPiece(promotedRookPos1, promotedRook1);

        Position promotedRookPos2 = new Position(5, 2);
        PromotedRook promotedRook2 = new PromotedRook(Sides.SENTE, promotedRookPos2, null, board);
        board.setPiece(promotedRookPos2, promotedRook2);

        Position silverPos = new Position(4, 1);
        SilverGeneral silver = new SilverGeneral(Sides.SENTE, silverPos, null, board);
        board.setPiece(silverPos, silver);

        assertTrue(board.evals.isCheckMate(Sides.GOTE));
    }

    @Test
    void testPieceCapture() {
        Player sente = new Player(Sides.SENTE, "Sente", PlayerType.HUMAN);
        Player gote = new Player(Sides.GOTE, "Gote", PlayerType.HUMAN);
        Board board = new Board(Arrays.asList(sente, gote));
        Position gotePos = new Position(4,4);
        Pawn gotePawn = new Pawn(Sides.GOTE, null, null, board);
        board.setPiece(gotePos, gotePawn);
        Position sentePos = new Position(4,5);
        Pawn sentePawn = new Pawn(Sides.SENTE, null, null, board);
        board.setPiece(sentePos, sentePawn);

        board.moveManager.applyMove(new Move(sente, sentePos, gotePos, sentePawn, null, false, false));
        // Capture
        assertTrue(sente.getHandPieces().contains(gotePawn));
        assertNull((gotePawn).getBoardPosition());
        assertEquals(Sides.SENTE, gotePawn.getSide());
    }

    @Test
    void testUchifuzume() { // No pawn drop mate
        Player sente = new Player(Sides.SENTE, "Sente", PlayerType.HUMAN);
        Player gote = new Player(Sides.GOTE, "Gote", PlayerType.HUMAN);
        Board board = new Board(Arrays.asList(sente, gote));

        // Place gote King in the default position
        Position kingPos = new Position(4, 0);
        King king = new King(Sides.GOTE, kingPos, null, board);
        board.setPiece(kingPos, king);

        // Place two sente rooks to almost deliver checkmate
        Position promotedRookPos1 = new Position(2, 1);
        PromotedRook promotedRook1 = new PromotedRook(Sides.SENTE, promotedRookPos1, null, board);
        board.setPiece(promotedRookPos1, promotedRook1);

        Position promotedRookPos2 = new Position(5, 2);
        PromotedRook promotedRook2 = new PromotedRook(Sides.SENTE, promotedRookPos2, null, board);
        board.setPiece(promotedRookPos2, promotedRook2);

        // Create a new capturable gote pawn
        Pawn pawn = new Pawn(Sides.GOTE, null, null, board);
        // Add to sente's hand (changes side to SENTE)
        sente.addToHand(pawn);
        // Try dropping pawn to deliver checkmate in front of king
        Position pawnDropTarget = new Position(4, 1);

        // The uchifuzume violation should be detected
        assertTrue(board.evals.violatesUchifuzume(Sides.SENTE, pawnDropTarget));
        // Legal drop positions should not include the illegal pawn drop
        assertFalse(board.getPieceDropPoints(Pawn.class, Sides.SENTE).contains(pawnDropTarget));
    }
}