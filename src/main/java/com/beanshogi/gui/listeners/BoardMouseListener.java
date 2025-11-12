package com.beanshogi.gui.listeners;

import com.beanshogi.util.Position;

import java.util.function.Consumer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**
 * Converts mouse clicks to board positions and delegates to a handler.
 * Stateless, reusable across boards.
 */
public class BoardMouseListener extends MouseAdapter {

    private final Consumer<Position> clickHandler;
    private final int cellSize;
    private final int gap;

    public BoardMouseListener(Consumer<Position> clickHandler, int cellSize, int gap) {
        this.clickHandler = clickHandler;
        this.cellSize = cellSize;
        this.gap = gap;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        int boardX = mouseX / (cellSize + gap);
        int boardY = mouseY / (cellSize + gap);
        Position clickedPos = new Position(boardX, boardY);

        if (clickedPos.inBounds()) {
            // Ensure EDT-safe call
            SwingUtilities.invokeLater(() -> clickHandler.accept(clickedPos));
        }
    }
}
