package com.beanshogi;

import java.util.List;

import com.beanshogi.game.Game;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.model.Piece;
import com.beanshogi.util.Position;

public class Main {
    public static void main(String[] args) {
        // Separate GUI thread
        javax.swing.SwingUtilities.invokeLater(ShogiWindow::new);
    }
}