    package com.beanshogi.gui.panels.menucards;

    import java.awt.GridBagLayout;
    import java.awt.GridBagConstraints;
    import java.awt.Insets;
    import java.util.List;

    import javax.swing.*;

    import com.beanshogi.game.Game;
    import com.beanshogi.game.Player;
    import com.beanshogi.gui.ShogiWindow;
    import com.beanshogi.gui.util.*;
    import com.beanshogi.util.AIDifficulty;
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
        JTextField senteNameField = new JTextField("Player 1", 15);
        addRow(gbc, new JLabel("Sente (先手) Name:"), senteNameField);

        JTextField goteNameField = new JTextField("Player 2", 15);
        addRow(gbc, new JLabel("Gote (後手) Name:"), goteNameField);

        // Player type radio button groups
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

        JComboBox<AIDifficulty> senteDifficultyCombo = new JComboBox<>(AIDifficulty.values());
        senteDifficultyCombo.setSelectedItem(AIDifficulty.NORMAL);
        senteDifficultyCombo.setEnabled(false);

        JComboBox<AIDifficulty> goteDifficultyCombo = new JComboBox<>(AIDifficulty.values());
        goteDifficultyCombo.setSelectedItem(AIDifficulty.NORMAL);
        goteDifficultyCombo.setEnabled(false);

        senteHuman.addActionListener(e -> senteDifficultyCombo.setEnabled(false));
        senteAI.addActionListener(e -> senteDifficultyCombo.setEnabled(true));
        goteHuman.addActionListener(e -> goteDifficultyCombo.setEnabled(false));
        goteAI.addActionListener(e -> goteDifficultyCombo.setEnabled(true));

        JPanel senteTypePanel = new JPanel();
        senteTypePanel.add(senteHuman);
        senteTypePanel.add(senteAI);
        addRow(gbc, new JLabel("Sente Type:"), senteTypePanel);

        JPanel goteTypePanel = new JPanel();
        goteTypePanel.add(goteHuman);
        goteTypePanel.add(goteAI);
        addRow(gbc, new JLabel("Gote Type:"), goteTypePanel);

        addRow(gbc, new JLabel("Sente Difficulty:"), senteDifficultyCombo);
        addRow(gbc, new JLabel("Gote Difficulty:"), goteDifficultyCombo);

        // Play button - construct Game from form
        JButton playButton = SwingUtils.makeButton("Play", e -> {
            String senteName = senteNameField.getText().trim();
            String goteName = goteNameField.getText().trim();
            if (senteName.isEmpty()) senteName = "Player 1";
            if (goteName.isEmpty()) goteName = "Player 2";
            PlayerType senteType = senteHuman.isSelected() ? PlayerType.HUMAN : PlayerType.AI;
            PlayerType goteType = goteAI.isSelected() ? PlayerType.AI : PlayerType.HUMAN;
            AIDifficulty senteDifficulty = senteType == PlayerType.AI
                ? (AIDifficulty) senteDifficultyCombo.getSelectedItem()
                : AIDifficulty.NORMAL;
            AIDifficulty goteDifficulty = goteType == PlayerType.AI
                ? (AIDifficulty) goteDifficultyCombo.getSelectedItem()
                : AIDifficulty.NORMAL;
            Game game = new Game(List.of(
                new Player(Sides.SENTE, senteName, senteType, senteDifficulty),
                new Player(Sides.GOTE, goteName, goteType, goteDifficulty)
            ));
            window.showGamePlay(game);
        });

        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(playButton, gbc);
        gbc.gridy++;
        add(backButton, gbc);
    }

    private void addRow(GridBagConstraints gbc, JLabel label, JComponent comp) {
        add(label, gbc);
        gbc.gridx = 1;
        add(comp, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }
}
