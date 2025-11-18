package com.beanshogi.gui.listeners;

import com.beanshogi.util.Position;

import java.util.function.BiConsumer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**
 * Converts mouse clicks to board positions and delegates to a handler.
 * Stateless, reusable across boards.
 * Uses BiConsumer interface for consistency with HandPanelMouseListener.
 */
public class BoardMouseListener extends MouseAdapter {

    private final BiConsumer<Position, Void> clickHandler;
    private final int cellSize;
    private final Position gap;

    public BoardMouseListener(BiConsumer<Position, Void> clickHandler, int cellSize, Position gap) {
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
            SwingUtilities.invokeLater(() -> clickHandler.accept(clickedPos, null));
        }
    }
}
