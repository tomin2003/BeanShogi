package com.beanshogi.gui.panels.menu;

// Removed unused Dimension import
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import javax.swing.*;

import com.beanshogi.game.Game;
import com.beanshogi.game.Player;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.util.*;
import com.beanshogi.util.PlayerType;
import com.beanshogi.util.Sides;

public class NewGamePanel extends JPanel {

    public NewGamePanel(ShogiWindow window) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel title = new JLabel("Start New Game", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(18f));
        add(title, gbc);

        // Player name fields
        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("Sente (先手) Name:"), gbc);
        JTextField senteNameField = new JTextField("Player 1", 15);
        gbc.gridx = 1;
        add(senteNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Gote (後手) Name:"), gbc);
        JTextField goteNameField = new JTextField("Player 2", 15);
        gbc.gridx = 1;
        add(goteNameField, gbc);

        // Player type radio groups
        ButtonGroup senteTypeGroup = new ButtonGroup();
        JRadioButton senteHuman = new JRadioButton("Human", true);
        JRadioButton senteAI = new JRadioButton("AI");
        senteTypeGroup.add(senteHuman);
        senteTypeGroup.add(senteAI);

        ButtonGroup goteTypeGroup = new ButtonGroup();
        JRadioButton goteHuman = new JRadioButton("Human", true);
        JRadioButton goteAI = new JRadioButton("AI");
        goteTypeGroup.add(goteHuman);
        goteTypeGroup.add(goteAI);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Sente Type:"), gbc);
        JPanel senteTypePanel = new JPanel();
        senteTypePanel.add(senteHuman);
        senteTypePanel.add(senteAI);
        gbc.gridx = 1;
        add(senteTypePanel, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Gote Type:"), gbc);
        JPanel goteTypePanel = new JPanel();
        goteTypePanel.add(goteHuman);
        goteTypePanel.add(goteAI);
        gbc.gridx = 1;
        add(goteTypePanel, gbc);

        // Play button - construct Game from form
        JButton playButton = SwingUtils.makeButton("Play", e -> {
            String senteName = senteNameField.getText().trim();
            String goteName = goteNameField.getText().trim();
            if (senteName.isEmpty()) senteName = "Player 1";
            if (goteName.isEmpty()) goteName = "Player 2";
            PlayerType senteType = senteHuman.isSelected() ? PlayerType.HUMAN : PlayerType.AI;
            PlayerType goteType = goteAI.isSelected() ? PlayerType.AI : PlayerType.HUMAN;
            Game game = new Game(List.of(
                new Player(Sides.SENTE, senteName, senteType),
                new Player(Sides.GOTE, goteName, goteType)
            ));
            window.showGamePlay(game);
        });

        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        add(playButton, gbc);
        gbc.gridy++;
        add(backButton, gbc);
    }
}
