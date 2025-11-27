package com.beanshogi.gui.panels.overlays;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.beanshogi.core.game.Controller;
import com.beanshogi.core.game.Player;
import com.beanshogi.core.util.Position;
import com.beanshogi.gui.listeners.event.UndoRedoListener;
import com.beanshogi.gui.util.SwingUtils;

/**
 * Panel providing grouped undo and redo buttons for move management.
 * Listens for undo and redo stack changes to enable/disable buttons accordingly.
 */
public class UndoRedoPanel extends JPanel implements UndoRedoListener {

    private JButton undoButton;
    private JButton redoButton;
    private Controller controller;

    // Track state
    private boolean isAITurn = false;
    private boolean undoStackEmpty = true;
    private boolean redoStackEmpty = true;

    public UndoRedoPanel(Position pos) {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBounds(pos.x, pos.y, 120, 80);
        setOpaque(false);

        undoButton = SwingUtils.makeButton("Undo", e -> {
            if (controller != null) controller.undoMove();
        });

        redoButton = SwingUtils.makeButton("Redo", e -> {
            if (controller != null) controller.redoMove();
        });

        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
        add(undoButton);
        add(redoButton);
    }

    @Override
    public void gainController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void onUndoStackEmpty(boolean isEmpty) {
        this.undoStackEmpty = isEmpty;
        updateButtonStates();
    }

    @Override
    public void onRedoStackEmpty(boolean isEmpty) {
        this.redoStackEmpty = isEmpty;
        updateButtonStates();
    }

    @Override
    public void onTurnAdvance(Player playerOnTurn) {
        this.isAITurn = (playerOnTurn.isAI());
        updateButtonStates();
    }

    private void updateButtonStates() {
        SwingUtilities.invokeLater(() -> {
            undoButton.setEnabled(!isAITurn && !undoStackEmpty);
            redoButton.setEnabled(!isAITurn && !redoStackEmpty);
        });
    }

}
