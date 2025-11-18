package com.beanshogi.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.beanshogi.gui.listeners.GameStatsListener;
import com.beanshogi.gui.util.FilledRectanglePanel;
import com.beanshogi.util.Sides;

public class StatsPanel extends JPanel implements GameStatsListener {

    private JLabel turnLabel = new JLabel("Turn:");
    private JLabel moveCountLabel = new JLabel("Moves: 0");
    
    public StatsPanel(int x, int y) {
        setLayout(null);
        setBounds(x, y, 120, 80);
        setOpaque(false);

        // Translucent rounded rectangle panel
        FilledRectanglePanel rectPanel = new FilledRectanglePanel(
            120,
            80,
            20,
            new Color(255, 255, 255, 120)
        );
        rectPanel.setLayout(new GridBagLayout());
        rectPanel.setBounds(0, 0, 120, 80);

        turnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        moveCountLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);
        rectPanel.add(turnLabel, gbc);

        gbc.gridy = 1;
        rectPanel.add(moveCountLabel, gbc);

        add(rectPanel);
    }

    @Override
    public void onSideOnTurnChanged(Sides sideOnTurn) {
        SwingUtilities.invokeLater(() -> turnLabel.setText("Turn: " + sideOnTurn.toString()));
    }

    @Override
    public void onMoveCountChanged(int moveCount) {
        SwingUtilities.invokeLater(() -> moveCountLabel.setText("Moves: " + moveCount));
    }
}
