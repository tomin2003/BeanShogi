package com.beanshogi.io;

import java.io.File;
import com.beanshogi.game.Game;
import com.beanshogi.game.Player;
import com.beanshogi.model.Board;
import com.beanshogi.model.Piece;
import com.beanshogi.util.Sides;

public class SaveLoad extends Serialize {

    /**
     * Saves the game automatically into format <YYYY-MM-DD-UID> 
     * where <UID> = unique identifier of saves with matching dates
     * @param game the game to be saved into file
     */
    public static void save(Game game) {
        // Get today's date
        String dateToday = java.time.LocalDate.now().toString();
        
        // Get the contents of savegame directory and find next UID
        File wd = new File("savegame/");
        int UID = 1;
        if (wd.exists() && wd.listFiles() != null) {
            for (File file : wd.listFiles()) {
                String fileName = file.getName();
                if (fileName.startsWith(dateToday)) {
                    UID++;
                }
            }
        }
        // Save with the new filename
        String filename = String.format("savegame/%s_%d.json", dateToday, UID);
        Serialize.save(game, filename);
    }

    public static Game load() {
        return load("savegame/game.json");
    }
    
    public static Game load(String filepath) {
        Game game = Serialize.load(filepath, Game.class);
        
        if (game != null && game.getBoard() != null) {
            restoreTransientReferences(game.getBoard());
        }
        
        return game;
    }
    
    /**
     * Restore all transient references after deserialization.
     */
    private static void restoreTransientReferences(Board board) {
        // Restore board reference in MoveManager
        if (board.moveManager != null) {
            setPrivateField(board.moveManager, "board", board);
        }
        
        // Restore board reference in Evals
        if (board.evals != null) {
            setPrivateField(board.evals, "board", board);
        }
        
        // Restore board reference for all pieces on the board
        for (Piece piece : board.getAllPieces()) {
            if (piece != null) {
                piece.setBoard(board);
            }
        }
        
        // Restore board reference for pieces in hands
        for (Sides side : Sides.values()) {
            try {
                Player player = board.getPlayer(side);
                if (player != null && player.getHandGrid() != null) {
                    for (Piece piece : player.getHandGrid().getAllPieces()) {
                        if (piece != null) {
                            piece.setBoard(board);
                        }
                    }
                }
            } catch (Exception e) {
                // Player might not exist for this side
            }
        }
    }
    
    /**
     * Helper to set a private field using reflection.
     */
    private static void setPrivateField(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            System.err.println("Failed to set field " + fieldName + ": " + e.getMessage());
        }
    }
}
