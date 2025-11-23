package com.beanshogi.gui.panels.overlays;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.beanshogi.core.game.Controller;
import com.beanshogi.core.util.Position;
import com.beanshogi.gui.listeners.event.UndoRedoListener;
import com.beanshogi.gui.util.SwingUtils;

public class UndoRedoPanel extends JPanel implements UndoRedoListener {

    private JButton undoButton;
    private JButton redoButton;
    private Controller controller;
    
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
        SwingUtilities.invokeLater(() -> undoButton.setEnabled(!isEmpty));
    }

    @Override
    public void onRedoStackEmpty(boolean isEmpty) {
        SwingUtilities.invokeLater(() -> redoButton.setEnabled(!isEmpty));
    }
}
