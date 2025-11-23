package com.beanshogi.gui.listeners.hand;

import java.util.function.BiConsumer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

import com.beanshogi.core.game.Sides;
import com.beanshogi.core.util.Position;


/**
 * Converts mouse clicks on hand panels to grid positions and delegates to a handler.
 * Handles coordinate conversion from panel-relative to grid coordinates.
 */
public class HandPanelMouseListener extends MouseAdapter {

    private final BiConsumer<Position, Sides> clickHandler;
    private final int cellSize;
    private final Position gap;
    private final Sides handSide;

    public HandPanelMouseListener(BiConsumer<Position, Sides> clickHandler, int cellSize, Position gap, Sides handSide) {
        this.clickHandler = clickHandler;
        this.cellSize = cellSize;
        this.gap = gap;
        this.handSide = handSide;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        // Convert pixel coordinates to grid coordinates
        int gridX = mouseX / (cellSize + gap.x);
        int gridY = mouseY / (cellSize + gap.y);

        Position clickedPos = new Position(gridX, gridY);

        // Ensure EDT-safe call
        SwingUtilities.invokeLater(() -> clickHandler.accept(clickedPos, handSide));
    }
}
