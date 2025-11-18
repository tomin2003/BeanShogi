package com.beanshogi.gui.panels;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.beanshogi.game.Controller;
import com.beanshogi.gui.listeners.UndoRedoListener;
import com.beanshogi.gui.util.SwingUtils;

public class UndoRedoPanel extends JPanel implements UndoRedoListener {

    private JButton undoButton;
    private JButton redoButton;
    private Controller controller;
    
    public UndoRedoPanel(int x, int y) {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBounds(x, y, 120, 80);
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
