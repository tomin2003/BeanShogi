package com.beanshogi.gui.listeners.board;

import java.util.function.Consumer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

import com.beanshogi.core.util.Position;

/**
 * Converts mouse clicks to board positions and delegates to a handler.
 * Stateless, reusable across boards.
 */
public class BoardMouseListener extends MouseAdapter {

    private final Consumer<Position> clickHandler;
    private final int cellSize;
    private final Position gap;

    public BoardMouseListener(Consumer<Position> clickHandler, int cellSize, Position gap) {
        this.clickHandler = clickHandler;
        this.cellSize = cellSize;
        this.gap = gap;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        int boardX = mouseX / (cellSize + gap.x);
        int boardY = mouseY / (cellSize + gap.y);
        Position clickedPos = new Position(boardX, boardY);

        if (clickedPos.inBounds()) {
            // Ensure EDT-safe call
            // Pass null for the second parameter as board clicks don't need information on Side
            SwingUtilities.invokeLater(() -> clickHandler.accept(clickedPos));
        }
    }
}
